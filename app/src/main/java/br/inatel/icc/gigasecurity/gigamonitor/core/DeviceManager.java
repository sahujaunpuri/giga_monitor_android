package br.inatel.icc.gigasecurity.gigamonitor.core;

import android.content.Context;
import android.os.Environment;
import android.os.Message;
import android.util.Log;

import com.basic.G;
import com.google.gson.annotations.Expose;
import com.lib.EDEV_OPTERATE;
import com.lib.EFUN_ATTR;
import com.lib.EUIMSG;
import com.lib.FunSDK;
import com.lib.IFunSDKResult;
import com.lib.MsgContent;
import com.lib.sdk.struct.H264_DVR_FILE_DATA;
import com.lib.sdk.struct.H264_DVR_FINDINFO;
import com.lib.sdk.struct.SDK_CONFIG_NET_COMMON_V2;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import br.inatel.icc.gigasecurity.gigamonitor.R;
import br.inatel.icc.gigasecurity.gigamonitor.activities.DeviceListActivity;
import br.inatel.icc.gigasecurity.gigamonitor.adapters.DeviceExpandableListAdapter;
import br.inatel.icc.gigasecurity.gigamonitor.listeners.ConfigListener;
import br.inatel.icc.gigasecurity.gigamonitor.listeners.LoginDeviceListener;
import br.inatel.icc.gigasecurity.gigamonitor.listeners.PlaybackSearchListener;
import br.inatel.icc.gigasecurity.gigamonitor.model.Device;
import br.inatel.icc.gigasecurity.gigamonitor.model.ListComponent;
import br.inatel.icc.gigasecurity.gigamonitor.model.SurfaceViewComponent;
import br.inatel.icc.gigasecurity.gigamonitor.util.ComplexPreferences;
import br.inatel.icc.gigasecurity.gigamonitor.util.Utils;

/**
 * Created by rinaldo.bueno on 29/08/2014.
 */
public class DeviceManager implements IFunSDKResult{
    private static final String APP_UUID = "e29c9d4ac9fa41fab19413885818ca54";
    private static final String APP_KEY = "d55b6614829f4d1c84d3ab2a9193234b";
    private static final String APP_SECRET = "7a58fdbc242b4f6ba95652b7a3502b91";
    private static final int APP_MOVECARD = 8;
    private static final String SERVER_IP = "200.98.128.50";
    private static final int SERVER_PORT = 8000;

    private static String TAG = DeviceManager.class.getSimpleName();
    private static DeviceManager mInstance = null;
    private int mFunUserHandler;

    public HashMap<String, ArrayList<Integer>> favoritesMap;
    private ArrayList<Device> mDevices = new ArrayList<Device>();
    private ArrayList<Device> mLanDevices = new ArrayList<Device>();
    private final HashMap<Integer, Device> mLoggedDevices = new HashMap<Integer, Device>();
    private LinkedList<SurfaceViewComponent> startList = new LinkedList<SurfaceViewComponent>();


    private LoginDeviceListener currentLoginListener;
    private PlaybackSearchListener currentPlaybackSearchListener;
    private ConfigListener currentConfigListener;
    private JSONObject currentConfig, currentConfigA, currentConfigB;
    private JSONArray currentConfigArray;

    private DeviceExpandableListAdapter expandableListAdapter;
    private ArrayList<ListComponent> listComponents;

    private boolean startPlay = false;
    public boolean channelOnRec;

    private DeviceManager() {
    }

    public static synchronized DeviceManager getInstance() {
        if (mInstance == null) {
            mInstance = new DeviceManager();
        }
        return mInstance;
    }

    public int getHandler(){
        return mFunUserHandler;
    }

    public void init(Context context){
        int result;
        Log.d(TAG, "DeviceManager: INIT");


        InitParam initparam = new InitParam();

//        FunSDK.InitEx(0, G.ObjToBytes(initparam), "GIGA_", "", 0);

        FunSDK.Init(0, G.ObjToBytes(initparam));

        FunSDK.MyInitNetSDK();
        String defaultPath = getMediaPath(context) + File.separator + context.getPackageName() + File.separator;
        FunSDK.SetFunStrAttr(EFUN_ATTR.APP_PATH, defaultPath);

        FunSDK.SysInitAsAPModle("ConfigPath/ap.txt");

        FunSDK.SysInitNet(SERVER_IP, SERVER_PORT);

        FunSDK.SysInitLocal(defaultPath + "DBFile.db");

        FunSDK.XMCloundPlatformInit(APP_UUID, APP_KEY, APP_SECRET, APP_MOVECARD);
        mFunUserHandler = FunSDK.RegUser(this);
        FunSDK.SetFunIntAttr(EFUN_ATTR.FUN_MSG_HANDLE, mFunUserHandler);

        loadSavedData(context);

    }

    @Override
    public int OnFunSDKResult(Message msg, MsgContent msgContent){
        Log.d(TAG, "msg.what : " + msg.what);
        Log.d(TAG, "msg.arg1 : " + msg.arg1);
        Log.d(TAG, "msg.arg2 : " + msg.arg2);
        if (null != msgContent) {
            Log.d(TAG, "msgContent.sender : " + msgContent.sender);
            Log.d(TAG, "msgContent.seq : " + msgContent.seq);
            Log.d(TAG, "msgContent.str : " + msgContent.str);
            Log.d(TAG, "msgContent.arg3 : " + msgContent.arg3);
            Log.d(TAG, "msgContent.pData : " + msgContent.pData);
        }
        switch (msg.what) {
            case EUIMSG.DEV_SEARCH_DEVICES:
            {
                int length = msg.arg2;
                if (length > 0) {
                    SDK_CONFIG_NET_COMMON_V2[] searchResult = new SDK_CONFIG_NET_COMMON_V2[length];
                    for (int i = 0; i < searchResult.length; i++) {
                        searchResult[i] = new SDK_CONFIG_NET_COMMON_V2();
                    }
                    G.BytesToObj(searchResult, msgContent.pData);
                    for (int i = 0; i < searchResult.length; i++) {
                        Log.d(TAG, "DATA: " + searchResult[i].toString());
                    }

                    updateLanDeviceList(searchResult);
                } else {
                    updateLanDeviceList(null);
                }
            }
            break;
            case EUIMSG.DEV_LOGIN:
            {
                Device device = null;
                if(msgContent.seq != 0) {
                    device = findDeviceById(msgContent.seq);
                }
                if(msg.arg1 == 0) {
                    Log.d(TAG, "OnFunSDKResult: Login SUCCESS");
                    device.isLogged = true;
                    putLoggedDevice(device);

                    if(device.getChannelNumber()>0)
                        currentLoginListener.onLoginSuccess();
                    else {
                        FunSDK.DevGetConfigByJson(getHandler(), device.getSerialNumber(), "SystemInfo", 4096, -1, 10000, device.getId());
                    }
                } else if(msg.arg1 == -11301) {
                    if (device != null)
                        currentLoginListener.onLoginError(msg.arg1, device);
                } else if(msg.arg1 == -11307){
                    if(device != null)
                        currentLoginListener.onLoginError(msg.arg1, device);
                } else {
                    FunSDK.DevLogin(getHandler(), device.getSerialNumber(), device.getUsername(), device.getPassword(), device.getId());
                    Log.d(TAG, "OnFunSDKResult: Login ERROR");
                }
            }
            break;
            case EUIMSG.SYS_GET_DEV_STATE:
            {
                Device device = null;
                if(!msgContent.str.equals("0")) {
                    device = findDeviceBySN(msgContent.str);
                }
                if(msg.arg1>0){
                    if(device != null)
                        FunSDK.DevLogin(getHandler(), device.getSerialNumber(), device.getUsername(), device.getPassword(), device.getId());
                    Log.d(TAG, "OnFunSDKResult: Device ONLINE");
                } else {
                    Log.i(TAG, "OnFunSDKResult: Device OFFLINE");
                    currentLoginListener.onLoginError(-1, device);

                }
            }
            break;
            case EUIMSG.DEV_GET_JSON:
            {
                if(msg.arg1 >= 0){
                    Log.d(TAG, "OnFunSDKResult: GETCONFIGJSON SUCCESS");
                    Device device = findDeviceById(msgContent.seq);
                    String jsonText = G.ToString(msgContent.pData);
                    Log.d(TAG, "EUIMSG.DEV_GET_JSON --> json: " + jsonText);
                    JSONObject json = null;
                    try {
                        json = new JSONObject(jsonText);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    switch(msgContent.str){
                        case "SystemInfo":{
                            setDeviceInfo(json, device);
                            currentLoginListener.onLoginSuccess();
                        }
                        break;
                        case "NetWork.NetCommon":{
                            handleEthernetConfig(json, device);
                            currentConfigListener.onReceivedConfig();
                        }
                        break;
                        case "NetWork.Nat" :{
                            handleDNSConfig(json, device);
                            currentConfigListener.onReceivedConfig();
                        }
                        break;
                        case "NetWork.NetDDNS":{
                            handleDDNSConfig(json, device);
                            currentConfigListener.onReceivedConfig();
                        }
                        break;
                        case "NetWork.Upnp":{
                            handleUPnPConfig(json, device);
                            currentConfigListener.onReceivedConfig();
                        }
                        break;
                    }
//                     salvar json como txt
                    File file = new File("/storage/emulated/0/", "abc.txt");
                    try {
                        FileWriter writer = new FileWriter(file);
                        writer.append(json.toString());
                        writer.flush();
                        writer.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else{
                    Device device = findDeviceById(msgContent.seq);
                    FunSDK.DevGetConfigByJson(getHandler(), device.getSerialNumber(), msgContent.str, 4096, -1, 10000, device.getId());
                    Log.d(TAG, "OnFunSDKResult: GETCONFIGJSON ERROR");
                }
            }
            break;
            case EUIMSG.DEV_SET_JSON:
            {
                if(msg.arg1 >= 0){
                    Log.d(TAG, "OnFunSDKResult: CONFIG SET SUCCESS");
                    currentConfigListener.onSetConfig();
                } else{
                    Log.d(TAG, "OnFunSDKResult: CONFIG SET ERROR");
                    currentConfigListener.onError();
                }
            }
            break;
            case EUIMSG.DEV_GET_CONFIG:
            {
                if(msg.arg1 >= 0){
                    Log.d(TAG, "OnFunSDKResult: GETCONFIG SUCCESS");
                    String data = G.ToString(msgContent.pData);
                    Log.d(TAG, "--> DATA: " + data);
                } else{
                    Log.d(TAG, "OnFunSDKResult: GETCONFIG ERROR");
                }
            }
            break;
            case EUIMSG.DEV_GET_CHN_NAME:
            {
                Log.d(TAG, "OnFunSDKResult: DEV_GET_CHN_NAME");
            }
            break;
            case EUIMSG.DEV_FIND_FILE:
            {
                Log.d(TAG, "OnFunSDKResult: DEV_FIND_FILE");
                Device device = findDeviceById(msgContent.seq);
                int fileNum = msg.arg1;
                if (fileNum < 0) {
                    Log.d(TAG, "OnFunSDKResult: NENHUM ARQUIVO ENCONTRADO");
                    currentPlaybackSearchListener.onEmptyListFound();
                } else {
                    H264_DVR_FILE_DATA files[] = new H264_DVR_FILE_DATA[msg.arg1];
                    for (int i = 0; i < files.length; i++) {
                        files[i] = new H264_DVR_FILE_DATA();
                    }
                    G.BytesToObj(files, msgContent.pData);
                    currentPlaybackSearchListener.onFindList(files);

                }
            }
            break;

        }
        return 0;
    }

    public void loadSavedData(Context context){
        mDevices = loadDevices(context);
        favoritesMap = new HashMap<String, ArrayList<Integer>>();
        favoritesMap = loadFavorites(context);
    }

    public void addDevice(Context context, Device device) {
        mDevices.add(device);
        saveData(context);
    }

    public void saveData(Context context) {
        ComplexPreferences cp = new ComplexPreferences(context, context.getString(R.string.sheredpreference_list), Context.MODE_PRIVATE);
        cp.putObject("DeviceList", new DeviceList(mDevices));
        cp.putObject("FavoritesMap", favoritesMap);

        cp.apply();
    }

    private ArrayList<Device> loadDevices(Context context) {
        ComplexPreferences cp = new ComplexPreferences(context, context.getString(R.string.sheredpreference_list), Context.MODE_PRIVATE);
        DeviceList deviceList = cp.getObject("DeviceList", DeviceList.class);
        if (deviceList == null || deviceList.getList() == null) {
            return new ArrayList<Device>();
        }
        return deviceList.getList();
    }

    private HashMap<String, ArrayList<Integer>> loadFavorites(Context context) {
        ComplexPreferences cp = new ComplexPreferences(context, context.getString(R.string.sheredpreference_list), Context.MODE_PRIVATE);
        HashMap<String, ArrayList<Double>> hashMap = cp.getObject("FavoritesMap", HashMap.class);
        if(hashMap != null)
            for (HashMap.Entry<String, ArrayList<Double>> entry : hashMap.entrySet()){
                ArrayList<Integer> arrayList = new ArrayList<Integer>();
                for(Double i : entry.getValue()){
                    arrayList.add(i.intValue());
                }
                favoritesMap.remove(entry.getKey());
                favoritesMap.put(entry.getKey(), arrayList);
            }

        return favoritesMap;
    }

    public ArrayList<Device> getDevices(){
        return mDevices;
    }

    public void updateDevices(Context context, ArrayList<Device> devices){
        mDevices = devices;
        saveData(context);
    }

    private void setDeviceInfo(JSONObject json, Device device) {
        try {
            JSONObject systemInfo;
            if(json.has("SystemInfo"))
                systemInfo = json.getJSONObject("SystemInfo");
            else
                return;

            if(systemInfo.has("AudioInChannel"))
                device.audioInChannel = systemInfo.getInt("AudioInChannel");
            if(systemInfo.has("SerialNo"))
                device.setSerialNumber(systemInfo.getString("SerialNo"));
            if(systemInfo.has("TalkInChannel"))
                device.talkInChannel = systemInfo.getInt("TalkInChannel");
            if(systemInfo.has("TalkOutChannel"))
                device.talkOutChannel = systemInfo.getInt("TalkOutChannel");
            if(systemInfo.has("VideoInChannel"))
                device.setChannelNumber(systemInfo.getInt("VideoInChannel"));
            saveData(DeviceListActivity.mContext);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getJsonConfig(Device device, String configString, ConfigListener configListener){
        currentConfigListener = configListener;
        FunSDK.DevGetConfigByJson(getHandler(), device.getSerialNumber(), configString, 4096, -1, 10000, device.getId());
    }

    private void handleEthernetConfig(JSONObject jsonObject, Device device){
        try {
            JSONObject json = new JSONObject();
            if(jsonObject.has("NetWork.NetCommon"))
                json = jsonObject.getJSONObject("NetWork.NetCommon");

            if(json.has("HostName")){
                device.setHostname(json.getString("HostName"));
            }
            if(json.has("GateWay")){
                device.setGateway(Utils.hexStringToIP(json.getString("GateWay")));
            }
            if(json.has("HostIP")){
                device.setIpAddress(Utils.hexStringToIP(json.getString("HostIP")));
            }
            if(json.has("Submask")){
                device.setSubmask(Utils.hexStringToIP(json.getString("Submask")));
            }
            if(json.has("HttpPort")){
                device.setHttpPort(json.getInt("HttpPort"));
            }
            if(json.has("MAC")){
                device.setMacAddress(json.getString("MAC"));
            }
            if(json.has("MaxBps")){
                device.setMaxBPS(json.getInt("MaxBps"));
            }
            if(json.has("MonMode")){
                device.setMonMode(json.getString("MonMode"));
            }
            if(json.has("SSLPort")){
                device.setSslPort(json.getInt("SSLPort"));
            }
            if(json.has("TCPMaxConn")){
                device.setTcpMaxConn(json.getInt("TCPMaxConn"));
            }
            if(json.has("TCPPort")){
                device.setTCPPort(json.getInt(("TCPPort")));
            }
            if(json.has("TransferPlan")){
                device.setTransferPlan(json.optInt("TransferPlan"));
            }
            if(json.has("UDPPort")){
                device.setUdpPort(json.getInt("UDPPort"));
            }
//            if(json.has("UseHSDownLoad")){
//            }
            currentConfig = json;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        expandableListAdapter.notifyDataSetChanged();
    }

    private void handleDNSConfig(JSONObject jsonObject, Device device){
        try {
            JSONObject json = new JSONObject();
            if (jsonObject.has("NetWork.Nat"))
                json = jsonObject.getJSONObject("NetWork.Nat");

            if(json.has("DnsServer1"))
                device.setPrimaryDNS(Utils.hexStringToIP(json.getString("DnsServer1")));
            if(json.has("DnsServer2"))
                device.setSecondaryDNS(Utils.hexStringToIP(json.getString("DnsServer2")));

            currentConfig = json;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        expandableListAdapter.notifyDataSetChanged();
    }

    private void handleDDNSConfig(JSONObject jsonObject, Device device){
        try {
            currentConfigArray = new JSONArray();
            JSONObject json;
            if (jsonObject.has("NetWork.NetDDNS")) {
                currentConfigArray.put(1, jsonObject.getJSONArray("NetWork.NetDDNS").getJSONObject(1));
                currentConfigArray.put(2, jsonObject.getJSONArray("NetWork.NetDDNS").getJSONObject(2));
                currentConfigArray.put(3, jsonObject.getJSONArray("NetWork.NetDDNS").getJSONObject(3));
                currentConfigArray.put(4, jsonObject.getJSONArray("NetWork.NetDDNS").getJSONObject(4));
            }
            json = jsonObject.getJSONArray("NetWork.NetDDNS").getJSONObject(0);
            Log.d(TAG, "HANDLECONFIG: " + json.toString());


            currentConfig = json;

            if(json.has("Enable"))
                device.setDdnsEnable(json.getBoolean("Enable"));
            if(json.has("HostName"))
                device.setDdnsDomain(json.getString("HostName"));
            if(json.has("Server"))
                json = json.getJSONObject("Server");
            if(json.has("UserName"))
                device.setDdnsUserName(json.getString("UserName"));
            currentConfigB = json;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        expandableListAdapter.notifyDataSetChanged();
    }

    private void handleUPnPConfig(JSONObject jsonObject, Device device){
        try {
            JSONObject json = new JSONObject();
            if (jsonObject.has("NetWork.Upnp"))
                json = jsonObject.getJSONObject("NetWork.Upnp");

            if(json.has("Enable"))
                device.setUpnpEnable(json.getBoolean("Enable"));
            if(json.has("HTTPPort"))
                device.setHTTPPort(json.getInt("HTTPPort"));
            if(json.has("MediaPort"))
                device.setMediaPort(json.getInt("MediaPort"));
            if(json.has("MobilePort"))
                device.setMobilePort(json.getInt("MobilePort"));


            currentConfig = json;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        expandableListAdapter.notifyDataSetChanged();
    }

    public void setEthernetConfig(Device device){
        try {
            currentConfig.put("HostName", device.getHostname());
            currentConfig.put("GateWay", Utils.stringIpToHexString(device.getGateway()));
            currentConfig.put("HostIP", Utils.stringIpToHexString(device.getIpAddress()));
            currentConfig.put("Submask", Utils.stringIpToHexString(device.getSubmask()));
            currentConfig.put("HttpPort", device.getHttpPort());
            currentConfig.put("MaxBps", device.getMaxBPS());
            currentConfig.put("MonMode", device.getMonMode());
            currentConfig.put("SSLPort", device.getSslPort());
            currentConfig.put("TCPMaxConn", device.getTcpMaxConn());
            currentConfig.put("TCPPort", device.getTCPPort());
            currentConfig.put("TransferPlan", device.getTransferPlan());
            currentConfig.put("UDPPort", device.getUdpPort());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d(TAG, "setCurrentConfig: " + currentConfig.toString());
        FunSDK.DevSetConfigByJson(getHandler(), device.getSerialNumber(), "NetWork.NetCommon", currentConfig.toString(), -1, 15000, device.getId());
        currentConfig = null;
    }

    public void setDNSConfig(Device device){
        try{
            currentConfig.put("DnsServer1", Utils.stringIpToHexString(device.getPrimaryDNS()));
            currentConfig.put("DnsServer2", Utils.stringIpToHexString(device.getSecondaryDNS()));
        }catch (JSONException e){
            e.printStackTrace();
        }
        Log.d(TAG, "setCurrentConfig: " + currentConfig.toString());
        FunSDK.DevSetConfigByJson(getHandler(), device.getSerialNumber(), "NetWork.Nat", currentConfig.toString(), -1, 15000, device.getId());
        currentConfig = null;
    }

    public void setDDNSConfig(Device device){
        try{
            currentConfig.put("DDNSKey", "Giga DDNS");
            currentConfig.put("Enable", device.isDdnsEnable());
            currentConfig.put("HostName", device.getDdnsDomain());
            currentConfigB.put("UserName", device.getDdnsUserName());
            currentConfigB.put("Name", "gigaddns.com.br");
            currentConfigB.put("Address", "0x0A060001"/*Utils.stringIpToHexString("10.6.0.1")*/);
            currentConfig.put("Server", currentConfigB);
            currentConfigArray.put(0, currentConfig);

        }catch (JSONException e){
            e.printStackTrace();
        }
        Log.d(TAG, "setCurrentConfig: " + currentConfig.toString());
        FunSDK.DevSetConfigByJson(getHandler(), device.getSerialNumber(), "NetWork.NetDDNS", currentConfigArray.toString(), -1, 15000, device.getId());
        currentConfig = null;
    }

    public void setUpnpConfig(Device device){
        try{
            currentConfig.put("Enable", device.isUpnpEnable());
            currentConfig.put("HTTPPort", device.getHTTPPort());
            currentConfig.put("MediaPort", device.getMediaPort());
            currentConfig.put("MobilePort", device.getMobilePort());
        }catch (JSONException e){
            e.printStackTrace();
        }
        Log.d(TAG, "setCurrentConfig: " + currentConfig.toString());
        FunSDK.DevSetConfigByJson(getHandler(), device.getSerialNumber(), "NetWork.Upnp", currentConfig.toString(), -1, 15000, device.getId());
        currentConfig = null;
    }

    public void rebootDevice(Device device){
        JSONObject reboot = new JSONObject();
        JSONObject OPMachine = new JSONObject();
        try {
            reboot.put("Name", "OPMachine");
            OPMachine.put("Action", "Reboot");
            OPMachine.put("SessionID", "0x3");
            reboot.put("OPMachine", OPMachine);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        FunSDK.DevCmdGeneral(getHandler(), device.getSerialNumber(), 1450, "OPMachine", 2048, 10000, reboot.toString().getBytes(), -1, device.getId());
        logoutDevice(mDevices.get(DeviceListActivity.previousGroup));
        DeviceListActivity.collapseGroup(DeviceListActivity.previousGroup);
    }

    public DeviceExpandableListAdapter getExpandableListAdapter(Context context){
        if(expandableListAdapter == null){
            expandableListAdapter = new DeviceExpandableListAdapter(context, mDevices);
        }
        return expandableListAdapter;
    }

    public ArrayList<ListComponent> getListComponents(){
        if(listComponents == null){
            listComponents = new ArrayList<ListComponent>();
            for(int i=0; i < mDevices.size(); i++) {
                ListComponent listComponent = new ListComponent(mDevices.get(i));
                listComponents.add(listComponent);
            }
        }
        return listComponents;
    }

    public void updateListComponents(){
        listComponents.clear();
        for(int i=0; i < mDevices.size(); i++) {
            ListComponent listComponent = new ListComponent(mDevices.get(i));
            listComponents.add(listComponent);

        }
    }

    public void removeFromExpandableList(ArrayList<Integer> itens){
        for(Integer i : itens) {
            listComponents.get(i).stopChannels(0);
            expandableListAdapter.removeGroup(i);
        }
        updateListComponents();
    }

    public void loginDevice(final Device device, final LoginDeviceListener loginDeviceListener) {
        currentLoginListener = loginDeviceListener;
        FunSDK.SysGetDevState(getHandler(), device.getSerialNumber(), device.getId());
    }

    private void putLoggedDevice(Device device) {
        synchronized (mLoggedDevices) {
            mLoggedDevices.put(device.getId(), device);
        }
    }

    private void removeLoggedDevice(Device device) {
        synchronized (mLoggedDevices) {
            mLoggedDevices.remove(device.getId());
            device.isLogged = false;
        }
    }

    public boolean logoutDevice(Device device){
        removeLoggedDevice(device);
        device.isLogged = false;
        return (FunSDK.DevLogout(getHandler(), device.getSerialNumber(), device.getId()) == 0);
    }

    public void changePassword(Device device, String oldPassword, String newPassword, ConfigListener listener) {
        currentConfigListener = listener;
        device.setPassword(newPassword);

        JSONObject jsonPassword = new JSONObject();
        try {
            String new_pwd = FunSDK.DevMD5Encrypt(newPassword);
            String old_pwd = FunSDK.DevMD5Encrypt(oldPassword);
            jsonPassword.put("EncryptType", "MD5");
            jsonPassword.put("NewPassWord", new_pwd);
            jsonPassword.put("PassWord", old_pwd);
            jsonPassword.put("UserName", "admin");
            jsonPassword.put("SessionID", "0x6E472E78");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        FunSDK.DevSetConfigByJson(getHandler(), device.getSerialNumber(), "ModifyPassword", jsonPassword.toString(), -1, 15000, device.getId());
    }

    public boolean searchDevices() {
        int result = FunSDK.DevSearchDevice(getHandler(), 10000, 0);
        return (result == 0);
    }

    private void updateLanDeviceList(SDK_CONFIG_NET_COMMON_V2[] searchResult) {
        mLanDevices.clear();

        if (searchResult != null) {
            for (SDK_CONFIG_NET_COMMON_V2 com : searchResult) {
                addLanDevice(com);
            }
        }
    }

    private Device addLanDevice(SDK_CONFIG_NET_COMMON_V2 comm) {
        Device device = null;
        synchronized (mLanDevices) {
            String devSn = G.ToString(comm.st_14_sSn);

            if (null != devSn) {
                if (findLanDevice(devSn) == null) {
                    device = new Device(comm);
                    mLanDevices.add(device);
                }
            }
        }
        return device;
    }

    public Device findLanDevice(String devSn) {
        for (Device device : mLanDevices) {
            if (devSn.equals(device.getSerialNumber())) {
                return device;
            }
        }
        return null;
    }

    public Device findDeviceBySN(String devSn){
        //devSn = devSn.substring(8);
        Log.d(TAG, "findDeviceBySN: procurando " + devSn);
        for(Device device : mDevices){
            if(devSn.equals(device.getSerialNumber()))
                return device;
        }
        Log.d(TAG, "findDeviceBySN: DEVICE NOT FOUND");
        return null;
    }

    public Device findDeviceById(int devId){
        Log.d(TAG, "findDeviceById: procurando " + devId);
        for(Device device : mDevices){
            if(device.getId() == devId)
                return device;
        }
        Log.d(TAG, "findDeviceById: DEVICE NOT FOUND");
        return null;
    }

    public ArrayList<Device> getLanDevices(){
        return mLanDevices;
    }

    public static String getMediaPath(Context context) {
        String path = "";
        File dirFile = null;
        String exStorageState = Environment.getExternalStorageState();
        if (exStorageState == null || exStorageState.equals(Environment.MEDIA_MOUNTED)
                || exStorageState.equals(Environment.MEDIA_MOUNTED_READ_ONLY)) {
            dirFile = Environment.getExternalStorageDirectory();
        } else {
            dirFile = context.getExternalFilesDir(null);
        }

        if (dirFile == null) {
            dirFile = context.getFilesDir();
        } else {
            path = dirFile.getAbsolutePath();
        }

        return path == null ? "" : path;
    }

    public void addToStart(SurfaceViewComponent surfaceViewComponent) {
        synchronized (startList) {
            startList.add(surfaceViewComponent);
            if (!startPlay)
                requestStart();
        }
    }

    public void requestStart(){
        if(!startList.isEmpty()){
            startPlay = true;
            startList.getFirst().onStartVideo();
        } else{
            startPlay = false;
        }
    }

    public void removeFromStartQueue(SurfaceViewComponent surfaceViewComponent){
        synchronized (startList){
            startList.remove(surfaceViewComponent);
        }
    }

    public boolean isOnStartQueue(SurfaceViewComponent surfaceViewComponent){
        synchronized (startList) {
            return startList.contains(surfaceViewComponent);
        }
    }

    public void findPlaybackList(Device device, H264_DVR_FINDINFO info, PlaybackSearchListener listener){
        currentPlaybackSearchListener = listener;
        FunSDK.DevFindFile(getHandler(), device.getSerialNumber(), G.ObjToBytes(info), 64, 20000, device.getId());
    }

    public void remoteControl(Device device, int command){
        FunSDK.DevOption(getHandler(), device.getSerialNumber(), EDEV_OPTERATE.EDOPT_DEV_CONTROL, null, 0, command, 0, 0, "", device.getId());
    }

    public boolean isFavorite(SurfaceViewComponent channel){
        return favoritesMap.containsKey(channel.deviceSn) && favoritesMap.get(channel.deviceSn).contains(channel.mySurfaceViewChannelId);
    }

    public void addFavorite(Context context, SurfaceViewComponent channel){
        Log.d(TAG, "addFavorite: channel " + channel.mySurfaceViewChannelId);
        ArrayList<Integer> arrayList = new ArrayList<Integer>();
        if(favoritesMap.containsKey(channel.deviceSn)){
            arrayList = favoritesMap.get(channel.deviceSn);
            favoritesMap.remove(channel.deviceSn);
        }
        arrayList.add(channel.mySurfaceViewChannelId);
        favoritesMap.put(channel.deviceSn, arrayList);
        channel.isFavorite = true;
        saveData(context);
    }

    public void removeFavorite(Context context, SurfaceViewComponent channel){
        Log.d(TAG, "removeFavorite: channel " + channel.mySurfaceViewChannelId);
        if(isFavorite(channel)){
            ArrayList<Integer> arrayList = favoritesMap.get(channel.deviceSn);
            arrayList.remove(Integer.valueOf(channel.mySurfaceViewChannelId));
            favoritesMap.remove(channel.deviceSn);
            if(arrayList.size() > 0)
                favoritesMap.put(channel.deviceSn, arrayList);
        }
        channel.isFavorite = false;
        saveData(context);
    }



    // TODO
    /*
    public boolean startDeviceIntercom(Context context, Device device) {
        boolean started = false;
        if (mVoiceSessions.containsKey(device.getVoiceHandle())) {
            return true;
        }

        VoiceIntercom voiceIntercom = new VoiceIntercom(mVoiceHandler,
                device.getAudioParam(), context);

        Log.d(TAG, "Starting bidirectional audio streaming. Device IP: " + device.getIpAddress());
        long voiceHandle = mNetSdk.StartVoiceComMR(device.getLoginID(), 0L);
        device.setVoiceHandle(voiceHandle);
        if (voiceIntercom.prepare()) {
            started = voiceIntercom.start(voiceHandle);
            if (started) {
                mVoiceSessions.put(voiceHandle, voiceIntercom);
            }
        }

        return started;
    }

    public boolean stopDeviceIntercom(Device device) {
        boolean stopped = false;

        final long voiceHandle = device.getVoiceHandle();

        if (mVoiceSessions.containsKey(voiceHandle)) {
            VoiceIntercom vi = mVoiceSessions.get(voiceHandle);
            if (mNetSdk.StopVoiceCom(voiceHandle) && vi.stop()) {
                vi.release();
                mVoiceSessions.remove(voiceHandle);
                stopped = true;
            }
        }

        return stopped;
    }*/

    /*public void setDeviceAudioEnabled(MySurfaceView sv, boolean enabled) {
        if (enabled) {
            sv.setAudioCtrl(MyConfig.AudioState.OPENED);
        } else {
            sv.setAudioCtrl(MyConfig.AudioState.CLOSED);
        }
    }

    public boolean initAlarm(NetSdk.OnAlarmListener l) {
        mNetSdk.setOnAlarmListener(l);

        return mNetSdk.SetAlarmMessageCallBack();
    }

    public boolean setupDeviceAlarm(long loginID) {
        boolean set = mNetSdk.SetupAlarmChan(loginID);

        Log.d(TAG, String.format(set ? "Alarm set on device with loginID %d" :
                "Error setting alarm on device with login %d", loginID));

        return set;
    }

    public boolean closeDeviceAlarm(long loginID) {
        boolean set = mNetSdk.CloseAlarmChan(loginID);

        Log.d(TAG, String.format(set ? "Alarm closed on device with loginID %d" :
                "Error closing alarm on device with login %d", loginID));

        return set;
    }

    public SDK_AllAlarmIn getAlarmInConfig(Device device) {
        SDK_AllAlarmIn alarmInConfig = new SDK_AllAlarmIn();

        boolean get = mNetSdk.H264DVRGetDevConfig(device.getLoginID(), MyConfig.SdkConfigType.E_SDK_CONFIG_ALARM_IN, -1,
                alarmInConfig, 5000);

        return get? alarmInConfig : null;
    }

    public boolean setAllAlarmInConfig(Device device, SDK_AllAlarmIn alarmInConfig) {
        return mNetSdk.H264DVRSetDevConfig(device.getLoginID(), MyConfig.SdkConfigType.E_SDK_CONFIG_ALARM_IN, 0,
                alarmInConfig, 5000);
    }

    public SDK_AllAlarmOut getAlarmOutConfig(Device device) {
        SDK_AllAlarmOut alarmOutConfig = new SDK_AllAlarmOut();

        boolean get = mNetSdk.H264DVRGetDevConfig(device.getLoginID(), MyConfig.SdkConfigType.E_SDK_CONFIG_ALARM_OUT, -1,
                alarmOutConfig, 5000);

        return get? alarmOutConfig : null;
    }

    public boolean setAllAlarmOutConfig(Device device, SDK_AllAlarmOut alarmOutConfig) {
        return mNetSdk.H264DVRSetDevConfig(device.getLoginID(), MyConfig.SdkConfigType.E_SDK_CONFIG_ALARM_OUT, -1,
                alarmOutConfig, 5000);
    }

     public SDK_LogList getEventList(Device device, SDK_LogSearchCondition searchCondition) {
        SDK_LogList logList = new SDK_LogList();

        byte[] loglist = new byte[com.basic.G.Sizeof(logList)];

        boolean bret = mNetSdk.FindDVRLog(device.getLoginID(), com.basic.G.ObjToBytes(searchCondition), loglist,2000);

        Log.d(TAG, "error:"+mNetSdk.GetLastError());

        if(bret) {
            com.basic.G.BytesToObj(logList, loglist);
        }

        return logList;
    }*/


    //Class used to Persistence the list of Devices
    public class DeviceList {
        @Expose
        public ArrayList<Device> list = new ArrayList<Device>();

        public DeviceList(ArrayList<Device> deviceList) {
            this.list = deviceList;
        }

        public ArrayList<Device> getList() {
            return list;
        }

        public void setList(ArrayList<Device> list) {
            this.list = list;
        }
    }

}