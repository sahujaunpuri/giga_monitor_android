package br.inatel.icc.gigasecurity.gigamonitor.core;

import android.content.Context;
import android.content.res.Resources;
import android.os.Environment;
import android.os.Message;
import android.util.Log;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.basic.G;
import com.google.gson.annotations.Expose;
import com.lib.EFUN_ATTR;
import com.lib.EUIMSG;
import com.lib.FunSDK;
import com.lib.IFunSDKResult;
import com.lib.MsgContent;
import com.lib.sdk.struct.H264_DVR_FILE_DATA;
import com.lib.sdk.struct.H264_DVR_FINDINFO;
import com.lib.sdk.struct.SDK_CONFIG_NET_COMMON_V2;


import org.apache.commons.lang3.tuple.Pair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import br.inatel.icc.gigasecurity.gigamonitor.R;
import br.inatel.icc.gigasecurity.gigamonitor.adapters.DeviceExpandableListAdapter;
import br.inatel.icc.gigasecurity.gigamonitor.listeners.ConfigListener;
import br.inatel.icc.gigasecurity.gigamonitor.listeners.LoginDeviceListener;
import br.inatel.icc.gigasecurity.gigamonitor.listeners.PlaybackSearchListener;
import br.inatel.icc.gigasecurity.gigamonitor.model.ChannelsManager;
import br.inatel.icc.gigasecurity.gigamonitor.model.Device;
import br.inatel.icc.gigasecurity.gigamonitor.model.DeviceChannelsManager;
import br.inatel.icc.gigasecurity.gigamonitor.model.FavoritesChannelsManager;
import br.inatel.icc.gigasecurity.gigamonitor.model.FavoritePair;
import br.inatel.icc.gigasecurity.gigamonitor.ui.SurfaceViewComponent;
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

    public ArrayList<FavoritePair> favoritesList;
    private ArrayList<Device> mDevices = new ArrayList<Device>();
    private ArrayList<Device> mLanDevices = new ArrayList<Device>();
    public ArrayList<Device> mFavoriteDevices = new ArrayList<Device>();
    public LinkedList<SurfaceViewComponent> startList = new LinkedList<SurfaceViewComponent>();
    private HashMap<String, LoginDeviceListener> loginList = new HashMap<String, LoginDeviceListener>();


    private PlaybackSearchListener currentPlaybackSearchListener;
    private ConfigListener currentConfigListener;
    private JSONObject currentConfig, currentConfigB;
    private JSONArray currentConfigArray;
    public Context currentContext;

    private DeviceExpandableListAdapter expandableListAdapter;
    private ArrayList<ChannelsManager> deviceChannelsManagers;
    public Device favoriteDevice;
    public FavoritesChannelsManager favoriteManager;
    public int favoriteChannels = 0;

    private boolean startPlay = false;
    public boolean channelOnRec;
    private int stateCheckTry = 0;
    public int collapse = -1;

    public int screenWidth;
    public int screenHeight;

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
        currentContext = context;
        Log.d(TAG, "DeviceManager: INIT");


        InitParam initparam = new InitParam();

//        FunSDK.InitEx(0, G.ObjToBytes(initparam), "GIGA_", "", 0);
        FunSDK.InitExV2(0, G.ObjToBytes(initparam), 4, "GIGA_", "cloudgiga.com.br", 8765);
        FunSDK.SysSetServerIPPort("MI_SERVER", "cloudgiga.com.br", 80);



//        FunSDK.Init(0, G.ObjToBytes(initparam));

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

        getScreenSize();
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
                if(msg.arg1 == 0 && device != null) {
                    Log.d(TAG, "OnFunSDKResult: Login SUCCESS");
                    device.isLogged = true;
//                    putLoggedDevice(device);

                    favoriteManager.refreshFromDevice(device.getId());
                    if(device.getChannelNumber()>0){
                        if(loginList.get(device.connectionString) != null)
                            loginList.get(device.connectionString).onLoginSuccess(device);
                        loginList.remove(device.connectionString);
                    }else {
                        FunSDK.DevGetConfigByJson(getHandler(), device.connectionString, "SystemInfo", 4096, -1, 10000, device.getId());
                    }
               } else if(msg.arg1 == -11301 ) {
                    if (device != null && loginList.get(device.connectionString) != null) {
                        loginList.get(device.connectionString).onLoginError(msg.arg1, device);
                        loginList.remove(device.connectionString);
                    }
                } else if(msg.arg1 == -11307){
                    if(device != null && loginList.get(device.connectionString) != null) {
                        loginList.get(device.connectionString).onLoginError(msg.arg1, device);
                        loginList.remove(device.connectionString);
                    }
                } else if(device != null){
                    FunSDK.DevLogin(getHandler(), device.connectionString, device.getUsername(), device.getPassword(), device.getId());
                    Log.d(TAG, "OnFunSDKResult: Login ERROR");
                }
            }
            break;
            case EUIMSG.SYS_GET_DEV_STATE:
            {
                Device device = null;
                if(!msgContent.str.equals("0")) {
//                    device = findDeviceBySN(msgContent.str);
                    device = findDeviceById((msgContent.str).hashCode());
                }
                if(device != null) {
                    if (msg.arg1 > 0) {
                        Log.d(TAG, "OnFunSDKResult: Device ONLINE");
                        device.isOnline = true;
                        FunSDK.DevLogin(getHandler(), device.connectionString, device.getUsername(), device.getPassword(), device.getId());
                    } else {
                        Log.i(TAG, "OnFunSDKResult: Device OFFLINE");

                        if(stateCheckTry>3 ) {
                            if(loginList.get(device.connectionString) != null) {
                                loginList.get(device.connectionString).onLoginError(-1, device);
                                loginList.remove(device.connectionString);
                            }
                            device.isOnline = false;
                            stateCheckTry = 0;
                        } else{
                            FunSDK.SysGetDevState(getHandler(), device.connectionString, device.getId());
                            stateCheckTry++;
                        }
                    }
                }
            }
            break;
            case EUIMSG.DEV_GET_JSON:
            {
                if(msg.arg1 >= 0){
                    Log.d(TAG, "OnFunSDKResult: GETCONFIGJSON SUCCESS");
                    Device device = findDeviceById(msgContent.seq);
                    String jsonText = G.ToStringJson(msgContent.pData);
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
                            if(loginList.get(device.connectionString) != null) {
                                loginList.get(device.connectionString).onLoginSuccess(device);
                            }
                            loginList.remove(device.connectionString);

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
                    File file = new File("/storage/emulated/0/", msgContent.str + ".txt");
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
                    FunSDK.DevGetConfigByJson(getHandler(), device.connectionString, msgContent.str, 4096, -1, 10000, device.getId());
                    Log.d(TAG, "OnFunSDKResult: GETCONFIGJSON ERROR");
                }
            }
            break;
            case EUIMSG.DEV_SET_JSON:
            {
                if(msg.arg1 >= 0){
                    Log.d(TAG, "OnFunSDKResult: CONFIG SET SUCCESS");
                    if(currentConfigListener != null) {
                        currentConfigListener.onSetConfig();
                        currentConfigListener = null;
                    }
                    expandableListAdapter.notifyDataSetChanged();
                    updateDevicesManagers();
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
            case EUIMSG.DEV_ON_DISCONNECT:
            {
                if(msgContent.str != null){
                    String mensagem = "Dispositivo desconectado";
                    Device device = findDeviceById(msgContent.str.hashCode());
                    if(device != null) {
                        mensagem = mensagem.concat(" (" + device.connectionString + ")");
                        device.isLogged = false;
//                        FunSDK.SysGetDevState(getHandler(), device.connectionString, device.getId());
                    }
                    Toast.makeText(currentContext, mensagem, Toast.LENGTH_LONG).show();
                }
            }
            break;
            case EUIMSG.DEV_CMD_EN:
            {
                if(msgContent != null){
                    String data = G.ToString(msgContent.pData);
                    Log.d(TAG, "--> DATA: " + data);
                }
            }
            break;

        }
        return 0;
    }

    public void getScreenSize(){
        screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
        screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;
    }

    public void loadSavedData(Context context){
        mDevices = loadDevices(context);
        favoritesList = loadFavorites(context);
        createFavoriteDevice();
        for(Device device : mDevices)
            device.checkConnectionMethod();
        loginAllDevices();
    }

    public void createFavoriteDevice(){
        favoriteDevice = findDeviceBySN("Favoritos");
        if(favoriteDevice == null) {
            favoriteDevice = new Device("Favoritos");
            favoriteDevice.setSerialNumber("Favoritos");
            favoriteDevice.setChannelNumber(favoriteChannels);
            favoriteDevice.checkConnectionMethod();
            mDevices.add(0, favoriteDevice);
            saveData();
        }

        favoriteDevice.isLogged = true;
//            addSurfaceViewManager(favoritos);
    }

    public void addDevice(Device device) {
        device.checkConnectionMethod();
        mDevices.add(device);
        saveData();
    }

    public void addDevice(Device device, int position) {
        device.checkConnectionMethod();
        mDevices.add(position, device);
//        expandableListAdapter.mDevices = mDevices;
        expandableListAdapter.groupViewHolder.get(position).mDevice = device;
        expandableListAdapter.groupViewHolder.get(position).tvDeviceName.setText(device.deviceName);
        expandableListAdapter.notifyDataSetChanged();
        deviceChannelsManagers.get(position).mDevice = mDevices.get(position);
        saveData();
    }

    public void saveData() {
        ComplexPreferences cp = new ComplexPreferences(currentContext, "SHARED_PREFERENCE_LIST", Context.MODE_PRIVATE);
        cp.putObject("DeviceList", new DeviceList(mDevices));
        cp.putObject("FavoritesList", favoritesList);

        cp.apply();
    }

    private ArrayList<Device> loadDevices(Context context) {
        ComplexPreferences cp = new ComplexPreferences(context, context.getString(R.string.sheredpreference_list), Context.MODE_PRIVATE);
        DeviceList deviceList = cp.getObject("DeviceList", DeviceList.class);
        if (deviceList == null || deviceList.getList() == null) {
            return new ArrayList<Device>();
        }
        for(Device device : deviceList.getList())
            device.checkConnectionMethod();
        return deviceList.getList();
    }

    private ArrayList<FavoritePair> loadFavorites(Context context) {
        ComplexPreferences cp = new ComplexPreferences(context, context.getString(R.string.sheredpreference_list), Context.MODE_PRIVATE);
        ArrayList<FavoritePair> list = cp.getObject("FavoritesList", ArrayList.class);
        if(list != null){
            for(FavoritePair pair : list){
                Log.d(TAG, "loadFavorites: " + pair.toString());
                Device device = findDeviceById(pair.deviceId);
                if(!device.isFavorite)
                    device.isFavorite = true;
                favoriteChannels++;
            }
            return list;
        } else
            return new ArrayList<FavoritePair>();

    }

    public ArrayList<Device> getDevices(){
        return mDevices;
    }

    public void updateDevices(Context context, ArrayList<Device> devices){
        mDevices = devices;
        saveData();
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
            saveData();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getJsonConfig(Device device, String configString, ConfigListener configListener){
        currentConfigListener = configListener;
        FunSDK.DevGetConfigByJson(getHandler(), device.connectionString, configString, 4096, -1, 10000, device.getId());
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
                device.setHttpPort(json.getInt("HTTPPort"));
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

    public void setEthernetConfig(Device device, ConfigListener configListener){
        currentConfigListener = configListener;
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
        FunSDK.DevSetConfigByJson(getHandler(), device.connectionString, "NetWork.NetCommon", currentConfig.toString(), -1, 15000, device.getId());
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
        FunSDK.DevSetConfigByJson(getHandler(), device.connectionString, "NetWork.Nat", currentConfig.toString(), -1, 15000, device.getId());
        currentConfig = null;
    }

    public void setDDNSConfig(Device device){
        try{
            currentConfig.put("DDNSKey", "Giga DDNS");
            currentConfig.put("Enable", device.isDdnsEnable());
            currentConfig.put("HostName", device.getDdnsDomain());
            currentConfig.put("Online", "true");
            currentConfigB.put("UserName", device.getDdnsUserName());
            currentConfigB.put("Name", "gigaddns.com.br");
            currentConfigB.put("Address", "0x0A060001"/*Utils.stringIpToHexString("10.6.0.1")*/);
            currentConfig.put("Server", currentConfigB);
            currentConfigArray.put(0, currentConfig);
        }catch (JSONException e){
            e.printStackTrace();
        }
        Log.d(TAG, "setCurrentConfig: " + currentConfig.toString());
        FunSDK.DevSetConfigByJson(getHandler(), device.connectionString, "NetWork.NetDDNS", currentConfigArray.toString(), -1, 15000, device.getId());
        currentConfig = null;
    }

    public void setUpnpConfig(Device device){
        try{
            currentConfig.put("Enable", device.isUpnpEnable());
            currentConfig.put("HTTPPort", device.getHttpPort());
            currentConfig.put("MediaPort", device.getMediaPort());
            currentConfig.put("MobilePort", device.getMobilePort());
        }catch (JSONException e){
            e.printStackTrace();
        }
        Log.d(TAG, "setCurrentConfig: " + currentConfig.toString());
        FunSDK.DevSetConfigByJson(getHandler(), device.connectionString, "NetWork.Upnp", currentConfig.toString(), -1, 15000, device.getId());
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
        collapse = mDevices.indexOf(device);
        logoutDevice(device);
        FunSDK.DevCmdGeneral(getHandler(), device.connectionString, 1450, "OPMachine", 2048, 10000, reboot.toString().getBytes(), -1, device.getId());
    }

    public void generalCommand(JSONObject json, Device device, int commandId) throws JSONException {
        FunSDK.DevCmdGeneral(getHandler(), device.connectionString,
                commandId, json.getString("Name"), 0, 10000,
                json.toString().getBytes(), -1, device.getId());
    }

    public void sendAudio(Device device, byte[] data, int size){
        FunSDK.DevSendTalkData(device.connectionString, data, size);
    }

    public void remoteControl(Device device, int command){
        JSONObject j = new JSONObject();
        JSONObject jsonObj = new JSONObject();
        try {
            j.put("Name", "OPNetKeyboard");
            jsonObj.put("Value", command);
            jsonObj.put("Status", 0);
//            j.put("SessionID", "0x3");
            j.put("OPNetKeyboard", jsonObj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "remoteControl: " + jsonObj.toString());
//        Log.d(TAG, "remoteControl: " + jsonObj.toString().getBytes());
        FunSDK.DevCmdGeneral(getHandler(), device.connectionString, 1550, "OPNetKeyboard", 0, 10000, j.toString().getBytes(), -1, device.getId());
    }

    public DeviceExpandableListAdapter getExpandableListAdapter(Context context, ExpandableListView mExpandableListView){
        if(expandableListAdapter == null){
            expandableListAdapter = new DeviceExpandableListAdapter(context, mDevices, mExpandableListView);
        }
        return expandableListAdapter;
    }

    public void invalidateExpandableList(){
        expandableListAdapter = null;
    }

    public ArrayList<ChannelsManager> getDeviceChannelsManagers(){
        if(deviceChannelsManagers == null){
            deviceChannelsManagers = new ArrayList<ChannelsManager>();
            for(Device device : mDevices) {
                ChannelsManager deviceChannelsManager;
                if(device.getSerialNumber().equals("Favoritos")) {
                    favoriteManager = new FavoritesChannelsManager(device);
                    deviceChannelsManager = favoriteManager;
                }else
                    deviceChannelsManager = new DeviceChannelsManager(device);
                deviceChannelsManagers.add(deviceChannelsManager);
            }
            favoriteManager.createComponents();
        }
        return deviceChannelsManagers;
    }

    public ChannelsManager findSurfaceViewManagerByDevice(Device device){
        for(ChannelsManager svm : deviceChannelsManagers){
            if(device.getId() == svm.mDevice.getId())
                return svm;
        }
        Log.e(TAG, "findSurfaceViewManagerByDevice: DeviceChannelsManager Not Found");
        return null;
    }

    public ChannelsManager findSurfaceViewManagerByDevice(int deviceId){
        for(ChannelsManager svm : deviceChannelsManagers){
            if(deviceId == svm.mDevice.getId())
                return svm;
        }
        Log.e(TAG, "findSurfaceViewManagerByDevice: DeviceChannelsManager Not Found");
        return null;
    }

    public void addSurfaceViewManager(Device device){
        if(deviceChannelsManagers == null)
            getDeviceChannelsManagers();
        DeviceChannelsManager deviceChannelsManager = new DeviceChannelsManager(device);
        deviceChannelsManagers.add(deviceChannelsManager);
    }

    public void updateDevicesManagers(){
        if(deviceChannelsManagers == null)
            return;
        int i = 0;
        for(ChannelsManager svm : deviceChannelsManagers){
            svm.mDevice = mDevices.get(i);
            i++;
        }
    }

    public void updateSurfaceViewManagers(){
        if(deviceChannelsManagers == null)
            return;
        deviceChannelsManagers.clear();
        deviceChannelsManagers = null;
        getDeviceChannelsManagers();
        /*for(int i=0; i < mDevices.size(); i++) {
            DeviceChannelsManager deviceChannelsManager = new DeviceChannelsManager(mDevices.get(i));
            deviceChannelsManagers.add(deviceChannelsManager);
        }
        loginList.clear();*/
    }

    public void updateSurfaceViewManager(int i){
        deviceChannelsManagers.remove(i);
        deviceChannelsManagers.add(i, new DeviceChannelsManager(mDevices.get(i)));
        expandableListAdapter.notifyDataSetChanged();
    }

    public int getDevicePosition(Device device){
        return mDevices.indexOf(device);
    }

    public void removeFromExpandableList(ArrayList<Integer> itens){
        for(Integer i : itens) {
            deviceChannelsManagers.get(i).stopChannels(0);
            expandableListAdapter.removeGroup(i);
        }
        updateSurfaceViewManagers();
    }

    public void loginAllDevices(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                for(Device device : mDevices){
                    if(!device.isLogged)
                        loginDevice(device, null);
                }
            }
        }).start();
    }

    public void loginDevice(final Device device, final LoginDeviceListener loginDeviceListener) {
        if(!loginList.containsKey(device.connectionString) || loginList.get(device.connectionString) != null)
            FunSDK.SysGetDevState(getHandler(), device.connectionString, device.getId());
        loginList.put(device.connectionString, loginDeviceListener);
    }

    public void removeLoginListener(String connectionString){
        if(loginList.containsKey(connectionString))
            loginList.put(connectionString, null);
    }

    public void loginError(int error, Device device){
        loginList.get(device.connectionString).onLoginError(error, device);
    }

    public boolean logoutDevice(Device device){
        device.isLogged = false;
        return (FunSDK.DevLogout(getHandler(), device.connectionString, device.getId()) == 0);
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

        FunSDK.DevSetConfigByJson(getHandler(), device.connectionString, "ModifyPassword", jsonPassword.toString(), -1, 15000, device.getId());
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

        File file = new File(dirFile.getPath() + "/Pictures/Giga Monitor/");
        if(!file.exists()){
            file.mkdirs();
        }

        return path == null ? "" : path;
    }

    public void clearStart(){
        startList.clear();
        startPlay = false;
    }

    public void addToStart(SurfaceViewComponent svc) {
        synchronized (startList) {
            if(!isOnStartQueue(svc) && !svc.isConnected()) {
                svc.isLoading(true);
                startList.add(svc);
            }
            if (!startPlay)
                requestStart();
        }
    }

    public void requestStart(){
        if(!startList.isEmpty()){
            startPlay = true;
            SurfaceViewComponent svc = startList.poll();
//            if(!svc.stoping)
                svc.mChannelsManager.onStartVideo(svc);
//            else
//                addToStart(svc);
        } else{
            startPlay = false;
        }
    }

    public void removeFromStartQueue(SurfaceViewComponent svc){
        synchronized (startList){
            startList.remove(svc);
        }
    }

    public boolean isOnStartQueue(SurfaceViewComponent svc){
        synchronized (startList) {
            return startList.contains(svc);
        }
    }

    public void findPlaybackList(Device device, H264_DVR_FINDINFO info, PlaybackSearchListener listener){
        currentPlaybackSearchListener = listener;
        FunSDK.DevFindFile(getHandler(), device.connectionString, G.ObjToBytes(info), 64, 20000, device.getId());
    }

    public boolean isFavorite(int deviceId, int channelNumber){
        /*for(FavoritePair pair : favoritesList){
            if(pair.deviceId == deviceId)
                if(pair.channelNumber == channelNumber)
                    return true;
        }*/
        return favoritesList.contains(new FavoritePair(deviceId, channelNumber));
    }

    public void addFavorite(SurfaceViewComponent channel){
        Log.d(TAG, "addFavorite: channel " + channel.mySurfaceViewChannelId);
        favoritesList.add(new FavoritePair(channel.deviceId, channel.mySurfaceViewChannelId));
        channel.setFavorite(true);
//        if(favoriteChannels == 0)
//            expandableListAdapter.notifyDataSetChanged();
        favoriteChannels++;
        favoriteDevice.setChannelNumber(favoriteChannels);
//        favoriteManager.createComponents();
        expandableListAdapter.updateGrid(deviceChannelsManagers.indexOf(favoriteManager), favoriteManager);

        saveData();
    }

    public void removeFavorite(SurfaceViewComponent channel){
        Log.d(TAG, "removeFavorite: channel " + channel.mySurfaceViewChannelId);
        FavoritePair favorite = new FavoritePair(channel.deviceId, channel.mySurfaceViewChannelId);
        favoritesList.remove(favorite);
        channel.setFavorite(false);
        int channelPosition = findSurfaceViewManagerByDevice(channel.deviceId).getChannelSelected(channel.mySurfaceViewChannelId);
        findSurfaceViewManagerByDevice(channel.deviceId).surfaceViewComponents.get(channelPosition).setFavorite(false);
        favoriteChannels--;
        favoriteDevice.setChannelNumber(favoriteChannels);
//        favoriteManager.createComponents();
        expandableListAdapter.updateGrid(deviceChannelsManagers.indexOf(favoriteManager), favoriteManager);
//        if(favoriteChannels == 0)
//            expandableListAdapter.notifyDataSetChanged();
        saveData();
    }

    public void removeDeviceFromFavorite(int deviceId){
        ArrayList<Integer> toRemove = new ArrayList<>();
        int i = 0;
        for(FavoritePair pair : favoritesList) {
            if (pair.deviceId == deviceId)
                toRemove.add(i);
            i++;
        }
        for(int j=toRemove.size()-1 ; j>=0; j--) {
            favoritesList.remove((int) toRemove.get(j));
            favoriteChannels--;
        }
        favoriteDevice.setChannelNumber(favoriteChannels);
        expandableListAdapter.updateGrid(deviceChannelsManagers.indexOf(favoriteManager), favoriteManager);
        Log.d(TAG, "removeDeviceFromFavorite: ");
    }

    public void cleanFavorites(){
        for(FavoritePair pair : favoritesList){
            findSurfaceViewManagerByDevice(pair.deviceId).surfaceViewComponents.get(pair.channelNumber).setFavorite(false);
        }
        favoritesList = new ArrayList<FavoritePair>();
        favoriteChannels = 0;
        favoriteDevice.setChannelNumber(favoriteChannels);
        expandableListAdapter.updateGrid(deviceChannelsManagers.indexOf(favoriteManager), favoriteManager);
        saveData();
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