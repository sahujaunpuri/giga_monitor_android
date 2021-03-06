package br.inatel.icc.gigasecurity.gigamonitor.core;

import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
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
import com.lib.sdk.struct.SDK_SYSTEM_TIME;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;

import br.inatel.icc.gigasecurity.gigamonitor.R;
import br.inatel.icc.gigasecurity.gigamonitor.activities.DevicePlaybackActivity;
import br.inatel.icc.gigasecurity.gigamonitor.adapters.DeviceExpandableListAdapter;
import br.inatel.icc.gigasecurity.gigamonitor.listeners.ConfigListener;
import br.inatel.icc.gigasecurity.gigamonitor.listeners.DownloadPlaybackListener;
import br.inatel.icc.gigasecurity.gigamonitor.listeners.LoginDeviceListener;
import br.inatel.icc.gigasecurity.gigamonitor.listeners.PlaybackSearchListener;
import br.inatel.icc.gigasecurity.gigamonitor.model.ChannelsManager;
import br.inatel.icc.gigasecurity.gigamonitor.model.Device;
import br.inatel.icc.gigasecurity.gigamonitor.model.DeviceChannelsManager;
import br.inatel.icc.gigasecurity.gigamonitor.model.FavoritePair;
import br.inatel.icc.gigasecurity.gigamonitor.model.FavoritesChannelsManager;
import br.inatel.icc.gigasecurity.gigamonitor.model.FileData;
import br.inatel.icc.gigasecurity.gigamonitor.model.StatePreferences;
import br.inatel.icc.gigasecurity.gigamonitor.ui.SurfaceViewComponent;
import br.inatel.icc.gigasecurity.gigamonitor.util.ComplexPreferences;
import br.inatel.icc.gigasecurity.gigamonitor.util.Utils;

import static android.content.Context.ACTIVITY_SERVICE;
import static android.content.Context.MODE_PRIVATE;
import static br.inatel.icc.gigasecurity.gigamonitor.activities.DeviceListActivity.mContext;
import static br.inatel.icc.gigasecurity.gigamonitor.activities.DeviceListActivity.previousGroup;

/**
 * Created by rinaldo.bueno on 29/08/2014.
 */
public class DeviceManager implements IFunSDKResult {

    private static final String APP_UUID = "GIGA";
    private static final String APP_KEY = "743a9ea517924b2dbfa2cfc007c0a147";
    private static final String APP_SECRET = "62d3c9a2a51245b4a42e2d952b8dcbbc";
    private static final int APP_MOVECARD = 4;
    private static final String SERVER_IP_OLD = "cloudgiga.com.br";
    private static final String SERVER_IP_NEW = "200.219.196.58";
    private static String SERVER_IP = SERVER_IP_NEW;
    private static final int SERVER_PORT = 8000;
    private static final String MEDIA_DISK_NAME = "MEDIA_DISK";

    private static String TAG = DeviceManager.class.getSimpleName();
    private static DeviceManager mInstance = null;
    public int mFunUserHandler = -1;

    public ArrayList<FavoritePair> favoritesList;
    private ArrayList<Device> mDevices = new ArrayList<Device>();
    public ArrayList<Device> devicesWithJsonError = new ArrayList<Device>();
    private ArrayList<Device> mLanDevices = new ArrayList<Device>();
    public ArrayList<Device> mFavoriteDevices = new ArrayList<Device>();
    public LinkedList<SurfaceViewComponent> startList = new LinkedList<SurfaceViewComponent>();
    private HashMap<Integer, LoginDeviceListener> loginList = new HashMap<Integer, LoginDeviceListener>();
    private SharedPreferences mPreferences;

    private PlaybackSearchListener currentPlaybackSearchListener;
    private ConfigListener currentConfigListener;
    private JSONObject currentConfig, currentConfigB;
    private JSONArray currentConfigArray;
    public Context currentContext, tempContext;
    private DownloadPlaybackListener downloadPlaybackListener;
    private int downloadHandler;

    private DeviceExpandableListAdapter expandableListAdapter;
    private ArrayList<ChannelsManager> deviceChannelsManagers;
    public Device favoriteDevice;
    public FavoritesChannelsManager favoriteManager;
    public int favoriteChannels = 0;

    private boolean startPlay = false;
    public boolean loadedState = false;
    public boolean channelOnRec;
    public int collapse = -1;

    public int screenWidth;
    public int screenHeight;
    public int networkType; // -1:offline, 0:3g/4g, 1: wifi
    public String networkIp;
    public int networkMask;
    private String networkName = null;

    private ArrayList<File> mVideoFiles = new ArrayList<>();
    private ArrayList<Drawable> savedMediaVideos = new ArrayList<>();
    private ArrayList<Uri> videoUris = new ArrayList<>();
    private ArrayList<Boolean> savedMediaVideosPositionOk = new ArrayList<>();

    public boolean mediaViewDidSelectMovies = false;
    public boolean loggedByConnectionReceiver = false;

    private DeviceManager() {
    }

    public static synchronized DeviceManager getInstance() {
        if (mInstance == null) {
            mInstance = new DeviceManager();
        }
        return mInstance;
    }

    public int getHandler() {
        return mFunUserHandler;
    }

    /**
     * SDK INITIALIZATION
     */

    public void init(Context context) {
        currentContext = context;
        Log.d(TAG, "DeviceManager: INIT");

        InitParam initparam = new InitParam();

//        FunSDK.InitEx(0, G.ObjToBytes(initparam), "GIGA_", "", 0);
        FunSDK.InitExV2(0, G.ObjToBytes(initparam), 4, "GIGA_", SERVER_IP, 8765);
        FunSDK.SetFunIntAttr(EFUN_ATTR.DSS_STREAM_ENC_SYN_DEV, 1);
//        FunSDK.SysSetServerIPPort("MI_SERVER", SERVER_IP, 80);

//        FunSDK.Init(0, G.ObjToBytes(initparam));

        FunSDK.MyInitNetSDK();
        String defaultPath = getMediaPath(context) + File.separator + context.getPackageName() + File.separator;
        FunSDK.SetFunStrAttr(EFUN_ATTR.APP_PATH, defaultPath);

//        FunSDK.SysInitAsAPModle("ConfigPath/ap.txt");

        FunSDK.SysInitNet("223.4.33.127;54.84.132.236;112.124.0.188", 15010);

        FunSDK.XMCloundPlatformInit(APP_UUID, APP_KEY, APP_SECRET, APP_MOVECARD);
        mFunUserHandler = FunSDK.RegUser(this);
        FunSDK.SetFunIntAttr(EFUN_ATTR.FUN_MSG_HANDLE, mFunUserHandler);

        FunSDK.SysInitLocal(defaultPath + "DBFile.db");

        loadSavedData(context);
        getScreenSize();
        networkType = getConnectionMethod();

        if (networkType == 1) {
            //Verify Connection Name if Wi-Fi
            ConnectivityManager cm = (ConnectivityManager) currentContext.getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            String stringNetworkName = activeNetwork.toString();
            int index = stringNetworkName.indexOf("extra");
            int quotationMarksIndex = stringNetworkName.indexOf(",", index + 8);
            networkName = stringNetworkName.substring(index + 8, quotationMarksIndex - 1);
        }

//        AsyncTask.execute(new Runnable() {
//            @Override
//            public void run() {
//                loadSavedMediaVideos();
//            }
//        });

        Log.d(TAG, "init: ");
    }

    public void getScreenSize() {
        screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
        screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;
    }

    public void loadSavedData(Context context) {
        mDevices = loadDevices(context);
        favoritesList = loadFavorites(context);
        createFavoriteDevice();
//        for(Device device : mDevices)
//            device.checkConnectionMethod();
        getDeviceChannelsManagers();
//        loginAllDevices();
    }

    public void createFavoriteDevice() {
        favoriteDevice = findDeviceBySN("Favoritos");
        if (favoriteDevice == null) {
            favoriteDevice = new Device("Favoritos");
            favoriteDevice.setSerialNumber("Favoritos");
            favoriteDevice.setChannelNumber(favoriteChannels);
            favoriteDevice.isFavorite = true;
//            favoriteDevice.checkConnectionMethod();
            mDevices.add(0, favoriteDevice);
            saveData();
        }

        favoriteDevice.isLogged = true;
//            addSurfaceViewManager(favoritos);
    }

    public void addDevice(Device device) {
//        device.checkConnectionMethod();
        mDevices.add(device);
        if (expandableListAdapter != null)
            expandableListAdapter.setDevices(mDevices);
        saveData();
    }

    public void addDevice(Device device, int position) {
//        device.checkConnectionMethod();
        mDevices.add(position, device);
//        expandableListAdapter.mDevices = mDevices;
//        expandableListAdapter.groupViewHolder.get(position).mDevice = device;
//        expandableListAdapter.groupViewHolder.get(position).tvDeviceName.setText(device.deviceName);
        expandableListAdapter.notifyDataSetChanged();
        deviceChannelsManagers.get(position).mDevice = mDevices.get(position);
        saveData();
    }

    public void saveData() {
        ComplexPreferences cp = new ComplexPreferences(currentContext, "SHARED_PREFERENCE_LIST", MODE_PRIVATE);
        cp.putObject("DeviceList", new DeviceList(mDevices));
        cp.putObject("FavoritesList", favoritesList);

        cp.apply();
    }

    public void setSharedPreferences(SharedPreferences sharedPreferences) {
        mPreferences = sharedPreferences;
    }

    public StatePreferences loadState() {
        StatePreferences state = new StatePreferences();
        try {
            state.previousGroup = mPreferences.getInt("previousGroup", -1);
            if (state.previousGroup > -1) {
                ChannelsManager channelsManager = getDeviceChannelsManagers().get(state.previousGroup);
                state.previousChannel = mPreferences.getInt("previousChannel", -1);
                state.previousGrid = mPreferences.getInt("previousGrid", -1);
                state.previousLastGrid = mPreferences.getInt("previousLastGrid", -1);
                state.previousHD = mPreferences.getInt("previousHD", -1);
                state.previousExpand = mPreferences.getInt("previousExpand", -1);
                channelsManager.lastFirstVisibleItem = state.previousChannel;
                channelsManager.numQuad = state.previousGrid;
                channelsManager.lastNumQuad = state.previousLastGrid;
                channelsManager.reOrderSurfaceViewComponents();
                channelsManager.lastFirstItemBeforeSelectChannel = mPreferences.getInt("previousLastVisibleChannel", -1);
                channelsManager.lastExpand = state.previousExpand;
                channelsManager.changeSurfaceViewSize(channelsManager.lastExpand);
                if (state.previousHD > -1 && channelsManager.surfaceViewComponents.size() > state.previousHD) {
                    if(!mDevices.get(state.previousGroup).getSerialNumber().equals("Favoritos"))
                        state.previousHD = findChannelManagerByDevice(mDevices.get(state.previousGroup)).getChannelSelected(state.previousHD);
                }
            }
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
        return state;
    }

    public void saveState(StatePreferences state) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putInt("previousGroup", state.previousGroup);
        if (state.previousGroup != -1) {
            ChannelsManager channelsManager = getDeviceChannelsManagers().get(state.previousGroup);
            state.previousChannel = channelsManager.lastFirstVisibleItem;
            state.previousGrid = channelsManager.numQuad;
            state.previousLastGrid = channelsManager.lastNumQuad;
            state.previousExpand = channelsManager.lastExpand;
            editor.putInt("previousChannel", state.previousChannel);
            editor.putInt("previousGrid", state.previousGrid);
            editor.putInt("previousLastGrid", state.previousLastGrid);
            editor.putInt("previousHD", channelsManager.hdChannel);
            editor.putInt("previousLastVisibleChannel", channelsManager.lastFirstItemBeforeSelectChannel);
            editor.putInt("previousExpand", channelsManager.lastExpand);
        }
        editor.apply();
    }

    private ArrayList<Device> loadDevices(Context context) {
        ComplexPreferences cp = new ComplexPreferences(context, context.getString(R.string.sheredpreference_list), MODE_PRIVATE);
        DeviceList deviceList = cp.getObject("DeviceList", DeviceList.class);
        if (deviceList == null || deviceList.getList() == null) {
            return new ArrayList<Device>();
        }
//        for(Device device : deviceList.getList())
//            device.checkConnectionMethod();
        return deviceList.getList();
    }

    private ArrayList<FavoritePair> loadFavorites(Context context) {
        ComplexPreferences cp = new ComplexPreferences(context, context.getString(R.string.sheredpreference_list), MODE_PRIVATE);
        ArrayList<FavoritePair> list = cp.getObject("FavoritesList", ArrayList.class);
        if (list != null) {
            for (FavoritePair pair : list) {
                Log.d(TAG, "loadFavorites: " + pair.toString());
//                Device device = findDeviceById(pair.deviceId);
                favoriteChannels++;
            }
            return list;
        } else
            return new ArrayList<FavoritePair>();

    }

    public ArrayList<Device> getDevices() {
        return mDevices;
    }

    public void updateDevices(ArrayList<Device> devices) {
        mDevices = null;
        mDevices = devices;
        saveData();
    }

    private void setDeviceInfo(JSONObject json, Device device) {
        try {
            JSONObject systemInfo;
            if (json != null) {
                if (json.has("SystemInfo"))
                    systemInfo = json.getJSONObject("SystemInfo");
                else {
                    return;
                }

                if (systemInfo.has("AudioInChannel"))
                    device.audioInChannel = systemInfo.getInt("AudioInChannel");
                if (systemInfo.has("SerialNo"))
                    device.setSerialNumber(systemInfo.getString("SerialNo"));
                if (systemInfo.has("TalkInChannel"))
                    device.talkInChannel = systemInfo.getInt("TalkInChannel");
                if (systemInfo.has("TalkOutChannel"))
                    device.talkOutChannel = systemInfo.getInt("TalkOutChannel");
                if (systemInfo.has("HardWare"))
                    device.setHardwareVersion(systemInfo.getString("HardWare"));
                if (systemInfo.has("SoftWareVersion"))
                    device.setSoftwareVersion(systemInfo.getString("SoftWareVersion"));
                if (systemInfo.has("BuildTime"))
                    device.setBuildTime(systemInfo.getString("BuildTime"));
                saveData();
                if (device.getIpAddress() == null || device.getIpAddress().isEmpty())
                    getJsonConfig(device, "NetWork.NetCommon", null);
                if (device.getDomain() == null || device.getDomain().isEmpty())
                    getJsonConfig(device, "NetWork.NetDDNS", null);
            } else {
                return;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void downloadFile(Device device, byte[] fileData, String path, DownloadPlaybackListener downloadListener) {
        downloadPlaybackListener = downloadListener;
        int hand = getHandler();
        String connection = device.connectionString;
        int id = device.getId();
        downloadHandler = FunSDK.DevDowonLoadByFile(hand, connection, fileData, path, id);
    }

    public void cancelDownload() {
        FunSDK.DevStopDownLoad(downloadHandler);
    }

    public void getJsonConfig(Device device, String configString, ConfigListener configListener) {
        currentConfigListener = configListener;
        FunSDK.DevGetConfigByJson(getHandler(), device.connectionString, configString, 4096, -1, 10000, device.getId());
    }

    public void setJsonConfig(Device device, String configString, JSONObject jsonObject, ConfigListener configListener) {
        if (jsonObject != null) {
            currentConfigListener = configListener;
            FunSDK.DevSetConfigByJson(getHandler(), device.connectionString, configString, jsonObject.toString(), -1, 15000, device.getId());
        }
    }

    public void getTimeConfig(Device device, ConfigListener configListener){
        currentConfigListener = configListener;
        FunSDK.DevCmdGeneral(getHandler(), device.connectionString, 1452, "OPTimeQuery", 4096, 10000, null, -1, device.getId());
    }

    private void handleEthernetConfig(JSONObject jsonObject, Device device) {
        int position = mDevices.indexOf(device);
        try {
            JSONObject json = new JSONObject();
            if (jsonObject.has("NetWork.NetCommon"))
                json = jsonObject.getJSONObject("NetWork.NetCommon");

            if (json.has("HostName")) {
                device.setHostname(json.getString("HostName"));
            }
            if (json.has("GateWay")) {
                device.setGateway(Utils.hexStringToIP(json.getString("GateWay")));
            }
            if (json.has("HostIP")) {
                if (device.getIpAddress().isEmpty())
                    collapse = position;
                device.setIpAddress(Utils.hexStringToIP(json.getString("HostIP")));
            }
            if (json.has("Submask")) {
                device.setSubmask(Utils.hexStringToIP(json.getString("Submask")));
            }
            if (json.has("HttpPort")) {
                device.setHttpPort(json.getInt("HttpPort"));
            }
            if (json.has("MAC")) {
                device.setMacAddress(json.getString("MAC"));
            }
            if (json.has("MaxBps")) {
                device.setMaxBPS(json.getInt("MaxBps"));
            }
            if (json.has("MonMode")) {
                device.setMonMode(json.getString("MonMode"));
            }
            if (json.has("SSLPort")) {
                device.setSslPort(json.getInt("SSLPort"));
            }
            if (json.has("TCPMaxConn")) {
                device.setTcpMaxConn(json.getInt("TCPMaxConn"));
            }
            if (json.has("TCPPort")) {
                if (device.getTCPPort() == 0)
                    device.setTCPPort(json.getInt(("TCPPort")));
            }
            if (json.has("TransferPlan")) {
                device.setTransferPlan(json.optInt("TransferPlan"));
            }
            if (json.has("UDPPort")) {
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

    private void handleDNSConfig(JSONObject jsonObject, Device device) {
        try {
            JSONObject json = new JSONObject();
            if (jsonObject.has("NetWork.NetDNS"))
                json = jsonObject.getJSONObject("NetWork.NetDNS");

            if (json.has("Address"))
                device.setPrimaryDNS(Utils.hexStringToIP(json.getString("Address")));
            if (json.has("SpareAddress"))
                device.setSecondaryDNS(Utils.hexStringToIP(json.getString("SpareAddress")));

            currentConfig = json;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        expandableListAdapter.notifyDataSetChanged();
    }

    private void handleDDNSConfig(JSONObject jsonObject, Device device) {
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

            if (json.has("Enable"))
                device.setDdnsEnable(json.getBoolean("Enable"));
            if (json.has("HostName")) {
                device.setDdnsDomain(json.getString("HostName"));
                if (device.isDdnsEnable())
                    device.setDomain(json.getString("HostName") + ".gigaddns.com.br");
            }
            if (json.has("Server"))
                json = json.getJSONObject("Server");
            if (json.has("UserName"))
                device.setDdnsUserName(json.getString("UserName"));
            currentConfigB = json;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        expandableListAdapter.notifyDataSetChanged();
    }

    private void handleUPnPConfig(JSONObject jsonObject, Device device) {
        try {
            JSONObject json = new JSONObject();
            if (jsonObject.has("NetWork.Upnp"))
                json = jsonObject.getJSONObject("NetWork.Upnp");

            if (json.has("Enable"))
                device.setUpnpEnable(json.getBoolean("Enable"));
            if (json.has("HTTPPort"))
                device.setHttpPort(json.getInt("HTTPPort"));
            if (json.has("MediaPort"))
                device.setMediaPort(json.getInt("MediaPort"));
            if (json.has("MobilePort"))
                device.setMobilePort(json.getInt("MobilePort"));


            currentConfig = json;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        expandableListAdapter.notifyDataSetChanged();
    }

    private void handleEncodeConfig(JSONObject jsonObject, Device device){
        try{
            device.initEncodeArrays();
            device.setSimplifyEncodeJson(jsonObject);
            JSONArray json = jsonObject.getJSONArray("Simplify.Encode");
            for(int i=0; i<device.getChannelNumber(); i++){
                JSONObject streamJson = json.getJSONObject(i);
                device.parsePrimaryConfigs(i, streamJson.getJSONObject("MainFormat"));
                device.parseSecondaryConfigs(i, streamJson.getJSONObject("ExtraFormat"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private JSONObject setStreamEncode(JSONObject jsonObject, Device device) {
        JSONObject jsonObjectReturn = jsonObject;

        try {
            JSONArray json = jsonObject.getJSONArray("Simplify.Encode");
            for (int channel = 0; channel < json.length(); channel++) {
                JSONObject streamJson = json.getJSONObject(channel);
                JSONObject extracFormatJsonObject = streamJson.getJSONObject("ExtraFormat");
                JSONObject video =  extracFormatJsonObject.getJSONObject("Video");
                video.put("Resolution", "CIF");
                video.put("FPS", 3);
                video.put("Quality", 3);
            }
           jsonObjectReturn = jsonObject.put("Simplify.Encode", json);
        } catch (Exception error) {
            error.printStackTrace();
        }

        device.setSimplifyEncodeJson(jsonObjectReturn);
        setJsonConfig(device, "Simplify.Encode", device.getSimplifyEncodeJson(), currentConfigListener);

        return jsonObjectReturn;
    }

    public void handleEncodeCapability(JSONObject jsonObject, Device device){
        try {
            JSONObject json = jsonObject.getJSONObject("EncodeCapability");
            device.setImageSizePerChannel(json.getJSONArray("ImageSizePerChannel"));
            device.setExImageSizePerChannel(json.getJSONArray("ExImageSizePerChannel"));
            device.setMaxEncodePowerPerChannel(json.getJSONArray("MaxEncodePowerPerChannel"));
            device.setEncodeMasks(json.getJSONArray("EncodeInfo"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "handleEncodeCapability: ");
    }

    public void handleNatInfo(JSONObject jsonObject, Device device){
        try{
            if(jsonObject != null && device != null) {
                JSONObject json = null;
                if (jsonObject.has("Status.NatInfo"))
                    json = jsonObject.getJSONObject("Status.NatInfo");
                if (json.has("NaInfoCode"))
                    device.setNatCode(json.getString("NaInfoCode"));
                if (json.has("NatStatus"))
                    device.setNatStatus(json.getString("NatStatus"));
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    public void handleTimeConfig(JSONObject jsonObject, Device device){
        try{
            device.setTimeString(jsonObject.getString("OPTimeQuery"));
        }catch(JSONException e){
            e.printStackTrace();
        }
    }

    public void setEthernetConfig(Device device, ConfigListener configListener) {
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

    public void setEthernetConfigOffline(Device device) {
        JSONObject json = new JSONObject();
        try {
            json.put("GateWay", Utils.stringIpToHexString(device.getGateway()));
            json.put("HostIP", Utils.stringIpToHexString(device.getIpAddress()));
            json.put("HostName", device.getHostname());
            json.put("HttpPort", device.getHttpPort());
            json.put("MAC", device.getMacAddress());
            json.put("MaxBps", device.getMaxBPS());
            json.put("MonMode", device.getMonMode());
            json.put("SSLPort", device.getSslPort());
            json.put("Submask", Utils.stringIpToHexString(device.getSubmask()));
            json.put("TCPMaxConn", device.getTcpMaxConn());
            json.put("TCPPort", device.getTCPPort());
            json.put("TransferPlan", device.getTransferPlan());
            json.put("UDPPort", device.getUdpPort());
            json.put("UseHSDownLoad", false);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d(TAG, "setCurrentConfigOffline: " + json.toString());
        FunSDK.DevSetConfigByJson(getHandler(), device.connectionString, "NetWork.NetCommon", json.toString(), -1, 15000, 123);
    }

    public void setDNSConfig(Device device) {
        try {
            currentConfig.put("Address", Utils.stringIpToHexString(device.getPrimaryDNS()));
            currentConfig.put("SpareAddress", Utils.stringIpToHexString(device.getSecondaryDNS()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "setCurrentConfig: " + currentConfig.toString());
        FunSDK.DevSetConfigByJson(getHandler(), device.connectionString, "NetWork.NetDNS", currentConfig.toString(), -1, 15000, device.getId());
        currentConfig = null;
    }

    public void setDDNSConfig(Device device) {
        try {
            if (currentConfig == null){
                currentConfigListener.onError();
            } else {
                currentConfig.put("DDNSKey", "Giga DDNS");
                currentConfig.put("Enable", device.isDdnsEnable());
                if (!device.isDdnsEnable())
                    device.setDomain("");
                currentConfig.put("HostName", device.getDdnsDomain());
                currentConfig.put("Online", "true");
                currentConfigB.put("UserName", device.getDdnsUserName());
                currentConfigB.put("Name", "gigaddns.com.br");
                currentConfigB.put("Address", "0x0A060001"/*Utils.stringIpToHexString("10.6.0.1")*/);
                currentConfig.put("Server", currentConfigB);
                currentConfigArray.put(0, currentConfig);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "setCurrentConfig: " + currentConfig.toString());
        FunSDK.DevSetConfigByJson(getHandler(), device.connectionString, "NetWork.NetDDNS", currentConfigArray.toString(), -1, 15000, device.getId());
        currentConfig = null;
    }

    public void setUpnpConfig(Device device) {
        try {
            currentConfig.put("Enable", device.isUpnpEnable());
            currentConfig.put("HTTPPort", device.getHttpPort());
            currentConfig.put("MediaPort", device.getMediaPort());
            currentConfig.put("MobilePort", device.getMobilePort());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "setCurrentConfig: " + currentConfig.toString());
        FunSDK.DevSetConfigByJson(getHandler(), device.connectionString, "NetWork.Upnp", currentConfig.toString(), -1, 15000, device.getId());
        currentConfig = null;
    }

    public void setTimeConfig(Device device){
        String config = "{\"Name\":\"OPTimeSetting\",\"SessionID\":\"0x00001234\",\"OPTimeSetting\":\"" + device.getTimeString() +"\"}";
        FunSDK.DevSetConfigByJson(getHandler(), device.connectionString, "OPTimeSetting", config, 0, 20000, device.getId());
    }

    public void rebootDevice(Device device) {
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

    public void sendAudio(Device device, byte[] data, int size) {
        FunSDK.DevSendTalkData(device.connectionString, data, size);
    }

    public void remoteControl(Device device, String command, String click) {
        JSONObject jsonObj = new JSONObject();
        JSONObject json = new JSONObject();
        try {
            jsonObj.put("Value", command);
            jsonObj.put("Status", click);
            json.put("Name", "OPNetKeyboard");
            json.put("OPNetKeyboard", jsonObj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "remoteControl: " + jsonObj.toString());
        Log.d(TAG, "remoteControl: " + Arrays.toString(jsonObj.toString().getBytes()));
        FunSDK.DevCmdGeneral(getHandler(), device.connectionString, 1550, "OPNetKeyboard", 0, 10000, json.toString().getBytes(), -1, 0);
    }

    public DeviceExpandableListAdapter getExpandableListAdapter(Context context, ExpandableListView mExpandableListView) {
        if (expandableListAdapter == null) {
            expandableListAdapter = new DeviceExpandableListAdapter(context, mDevices, mExpandableListView);
        }
        return expandableListAdapter;
    }

    public DeviceExpandableListAdapter getExpandableListAdapter() {
        return expandableListAdapter;
    }

    public void invalidateExpandableList() {
        expandableListAdapter = null;
    }

    public ArrayList<ChannelsManager> getDeviceChannelsManagers() {
        if (deviceChannelsManagers == null) {
            deviceChannelsManagers = new ArrayList<ChannelsManager>();
            for (Device device : mDevices) {
                ChannelsManager deviceChannelsManager;
                if (device.getSerialNumber().equals("Favoritos")) {
                    favoriteManager = new FavoritesChannelsManager(device);
                    deviceChannelsManager = favoriteManager;
                    favoriteDevice.channelsManager = favoriteManager;
                } else {
                    deviceChannelsManager = new DeviceChannelsManager(device);
                    device.setChannelsManager(deviceChannelsManager);
                }
                deviceChannelsManagers.add(deviceChannelsManager);
            }
            favoriteManager.createComponents();
        }
//        return deviceChannelsManagers;
        return enabledDeviceChannelsManagers();
    }

    //fix temporário, otimizar. Encontrar uma forma de atualizar a lista de channelsmanager quando o usuário modificar algum device
    // pra não precisar ficar passando pelo laço toda vez que der o get
    private ArrayList<ChannelsManager> enabledDeviceChannelsManagers(){
        ArrayList<ChannelsManager> enabledDeviceChannelsManagers = new ArrayList<>();
        for(ChannelsManager channelsManager : deviceChannelsManagers){
            if(channelsManager.mDevice.isEnable())
                enabledDeviceChannelsManagers.add(channelsManager);
        }
        return enabledDeviceChannelsManagers;
    }

    public ChannelsManager findChannelManagerByDevice(Device device) {
        try {
            for (ChannelsManager svm : deviceChannelsManagers) {
                if (device.getId() == svm.mDevice.getId())
                    return svm;
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ChannelsManager findChannelManagerByDevice(int deviceId){
        try {
            for (ChannelsManager svm : deviceChannelsManagers) {
                if (deviceId == svm.mDevice.getId())
                    return svm;
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        Log.e(TAG, "findChannelManagerByDevice: DeviceChannelsManager Not Found");
        return null;
    }

    public void addSurfaceViewManager(Device device) {
        if (deviceChannelsManagers == null) {
            getDeviceChannelsManagers();
        }
        DeviceChannelsManager deviceChannelsManager = new DeviceChannelsManager(device);
        deviceChannelsManagers.add(deviceChannelsManager);
        device.channelsManager = deviceChannelsManager;
    }

    public void updateDevicesManagers() {
        if (deviceChannelsManagers == null) {
            return;
        }
        int i = 0;
        for (ChannelsManager svm : deviceChannelsManagers) {
            svm.mDevice = mDevices.get(i);
            i++;
        }
    }

    public void updateSurfaceViewManagers() {
        if (deviceChannelsManagers == null)
            return;
//        deviceChannelsManagers.clear();
        deviceChannelsManagers = null;
        getDeviceChannelsManagers();
        /*for(int i=0; i < mDevices.size(); i++) {
            DeviceChannelsManager deviceChannelsManager = new DeviceChannelsManager(mDevices.get(i));
            deviceChannelsManagers.add(deviceChannelsManager);
        }
        loginList.clear();*/
    }

    public void updateSurfaceViewManager(int i) {
//        deviceChannelsManagers.remove(i);
//        deviceChannelsManagers.add(i, new DeviceChannelsManager(mDevices.get(i)));
        deviceChannelsManagers.get(i).mDevice = mDevices.get(i);
        expandableListAdapter.notifyDataSetChanged();
    }

    public int getDevicePosition(Device device) {
        return mDevices.indexOf(device);
    }

    public void removeFromExpandableList(ArrayList<Integer> itens) {
        for (Integer i : itens) {
            deviceChannelsManagers.get(i).stopChannels(0);
            expandableListAdapter.removeGroup(i);
        }
        updateSurfaceViewManagers();
    }

    public void loginAllDevices() {
        final ArrayList<Device> devicesToLogin = new ArrayList<Device>();

        for (final Device device : mDevices) {
            if (!device.isLogged) {
                devicesToLogin.add(device);
            }
        }

        if (devicesToLogin.size() > 0) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    for (Device deviceToLogin : devicesToLogin) {
                        loginDevice(deviceToLogin, null);
                    }
                }
            }).start();
        }

    }

    public void setDevicesLogout(final boolean networkFail) {
        final ArrayList<Device> devicesToLogout = new ArrayList<Device>();

        if (networkFail) {
            Log.e("DeviceManager", "setDevicesLogout");
            try {
                Toast.makeText(currentContext, "Sem conexão com a internet", Toast.LENGTH_SHORT).show();
            } catch (Exception error) {
                error.printStackTrace();
            }
        }

        try {
            for (int index = 0; index < mDevices.size(); index ++) {
                previousGroup = -1;
                expandableListAdapter.collapseGroup(index);
                ChannelsManager deviceChannelsManager = deviceChannelsManagers.get(index);
                for (SurfaceViewComponent channel : deviceChannelsManager.surfaceViewComponents){
                    if (deviceChannelsManager.surfaceViewComponents.size() > 0) {
                        deviceChannelsManager.mediaStop(channel);
                    }
                }
            }
        } catch(Exception error) {
            error.printStackTrace();
        }

        for (final Device device : mDevices) {
            if (device.isLogged) {
                if (!device.isFavorite)
                    devicesToLogout.add(device);
            }
        }

        if (devicesToLogout.size() > 0) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    for (Device deviceToLogout : devicesToLogout) {
                        logoutDevice(deviceToLogout);
                    }
                }
            }).start();
        }
    }

    public void loginDevice(final Device device, final LoginDeviceListener loginDeviceListener) {
        if (!loginList.containsKey(device.getId())) {
            loginAttempt(device);
        }
        loginList.put(device.getId(), loginDeviceListener);
    }

    public void loginAttempt(final Device device) throws NullPointerException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (device == null)
                    return;
                if (device.loginAttempt > 3) {
                    device.loginAttempt = 0;
                    device.allAttempstFail = true;
                    expandableListAdapter.setMessage(mDevices.indexOf(device), "Falha na conexão");
                    return;
                }
                int nextConnectionType = -1;
                boolean tryIpConnection = true;
                if (device.getConnectionNetworkName() != null) {
                    tryIpConnection = device.getConnectionNetworkName().equals(networkName);
                }
                if (device.getConnectionMethod() == -1 || device.getConnectionMethod() == 2) {
                    if (networkType == 1 && tryIpConnection && device.isIpPriorityConnection()) {   //wifi connection
                        nextConnectionType = 0;
                    } else if (device.isDomainPriorityConnection()) {
                        nextConnectionType = 1;
                    } else if (device.isCloudPriorityConnection()) {
                        nextConnectionType = 2;
                    }
                } else if (device.getConnectionMethod() == 0 && device.isDomainPriorityConnection()) {
                    nextConnectionType = 1;
                } else if (device.getConnectionMethod() == 1 && device.isCloudPriorityConnection()) {
                    nextConnectionType = 2;
                }
                if (device.setConnectionString(nextConnectionType) < 0) {
                    loginAttempt(device);
                } else {
                    Log.d(TAG, "loginAttempt: " + device.loginAttempt + " " + device.connectionString);
                    device.loginAttempt++;
                    if (expandableListAdapter != null)
                        expandableListAdapter.setMessage(mDevices.indexOf(device), device.message);
                    FunSDK.DevLogin(getHandler(), device.connectionString, device.getUsername(), device.getPassword(), device.getId());
                }
            }
        }).start();

    }

    public void removeLoginListener(int id) {
        if (loginList.containsKey(id))
            loginList.put(id, null);
    }

    public void loginError(int error, Device device) {
        loginList.get(device.getId()).onLoginError(error, device);
    }

    public boolean logoutDevice(Device device) {
        device.isLogged = false;
        if (loginList.containsKey(device.getId()))
            loginList.remove(device.getId());
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
            try {
                String devSn = G.ToString(comm.st_14_sSn);
                if (null != devSn) {
                    if (findLanDevice(devSn) == null) {
                        device = new Device(comm);
                        mLanDevices.add(device);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
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

    public Device findDeviceBySN(String devSn) {
        //devSn = devSn.substring(8);
        Log.d(TAG, "findDeviceBySN: procurando " + devSn);
        for (Device device : mDevices) {
            if (devSn.equals(device.getSerialNumber()))
                return device;
        }
        Log.d(TAG, "findDeviceBySN: DEVICE NOT FOUND");
        return null;
    }

    public Device findDeviceById(int devId) {
        Log.d(TAG, "findDeviceById: procurando " + devId);
        for (Device device : mDevices) {
            if (device.getId() == devId)
                return device;
        }
        Log.d(TAG, "findDeviceById: DEVICE NOT FOUND");
        return null;
    }

    public boolean verifyIfDeviceExists(Device device) {
        boolean exist = false;
        for (Device dev : mDevices) {
            if (dev.getSerialNumber() != null && device.getSerialNumber() != null && dev.getSerialNumber().equals(device.getSerialNumber())) {
                exist = true;
                break;
            } else if (dev.getDomain() != null && device.getDomain() != null && dev.getDomain().equals(device.getDomain()) && dev.getExternalPort() == device.getExternalPort()) {
                exist = true;
                break;
            }
        }
        return exist;
    }

    public ArrayList<Device> getLanDevices() {
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
        if (!file.exists()) {
            file.mkdirs();
        }

        return path == null ? "" : path;
    }

    public void clearStart() {
        startList.clear();
        startPlay = false;
    }


    public void requestStart() {
        if (!startList.isEmpty()) {
            startPlay = true;
            SurfaceViewComponent svc = startList.poll();
//            if(!svc.stoping)
            svc.mChannelsManager.onStartVideo(svc);
//            else
//                addToStart(svc);
        } else {
            startPlay = false;
        }
    }

    public void removeFromStartQueue(SurfaceViewComponent svc) {
        synchronized (startList) {
            startList.remove(svc);
        }
    }

    public boolean isOnStartQueue(SurfaceViewComponent svc) {
        synchronized (startList) {
            return startList.contains(svc);
        }
    }

    public void findPlaybackList(Device device, Context context, H264_DVR_FINDINFO info, PlaybackSearchListener listener) {
        tempContext = context;
        currentPlaybackSearchListener = listener;
        FunSDK.DevFindFile(getHandler(), device.connectionString, G.ObjToBytes(info), 10000, 20000, device.getId());
    }

    public void findThumbnailList(Device device, FileData info, H264_DVR_FINDINFO findInfo, PlaybackSearchListener listener) {
//        currentPlaybackSearchListener = listener;
//        H264_DVR_FILE_DATA info2 = info.getFileData();
//        String path = Environment.getExternalStorageDirectory().getPath() + "/Pictures/Giga Monitor/";
//        String thumb_path = path + File.separator + getDownloadFileNameByData(info2, 1, true);
//        int result = FunSDK.DownloadRecordBImages();
//        String result1 = String.valueOf(result);
//        Log.e("PICSEARCH", result1);
    }

    public static String getDownloadFileNameByData(H264_DVR_FILE_DATA data, int type, boolean bThumbnail) {
        StringBuffer sb = new StringBuffer();
        if (null != data) {
            try {
                sb.append(getTime(data.st_3_beginTime, 0));
                sb.append("_");
                sb.append(getTime(data.st_4_endTime, 0));
                if (type == 1) {
                    sb.append("_");
                    int orderNum = Utils.getOrderNum(G.ToString(data.st_2_fileName), 1);
                    sb.append(orderNum);
                    if (bThumbnail) {
                        sb.append("_thumb");
                    }
                    sb.append(".jpg");
                } else if (type == 0) {
                    sb.append("_");
                    sb.append(data.st_6_StreamType);
                    if (bThumbnail)
                        sb.append("_thumb");
                    sb.append(".mp4");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    public static String getTime(SDK_SYSTEM_TIME tm, int type) {
        if (type == 0) {
            return String.format("%04d%02d%02d%02d%02d%02d", tm.st_0_year,
                    tm.st_1_month, tm.st_2_day, tm.st_4_hour, tm.st_5_minute, tm.st_6_second);
        } else if (type == 1) {
            return String.format("%04d-%02d-%02d %02d:%02d:%02d", tm.st_0_year,
                    tm.st_1_month, tm.st_2_day, tm.st_4_hour, tm.st_5_minute, tm.st_6_second);
        } else {
            return "";
        }
    }

    public boolean isFavorite(int deviceId, int channelNumber) {
        /*for(FavoritePair pair : favoritesList){
            if(pair.deviceId == deviceId)
                if(pair.channelNumber == channelNumber)
                    return true;
        }*/
        return favoritesList.contains(new FavoritePair(deviceId, channelNumber));
    }

    public void addFavorite(SurfaceViewComponent channel) {
        Log.d(TAG, "addFavorite: channel " + channel.mySurfaceViewNewChannelId);
        favoritesList.add(new FavoritePair(channel.deviceId, channel.mySurfaceViewNewChannelId));
        channel.setFavorite(true);
//        if(favoriteChannels == 0)
//            expandableListAdapter.notifyDataSetChanged();
        favoriteChannels++;
        favoriteDevice.setChannelNumber(favoriteChannels);
//        favoriteManager.createComponents();
        expandableListAdapter.updateGrid(deviceChannelsManagers.indexOf(favoriteManager), favoriteManager);

        saveData();
    }

    public void removeFavorite(SurfaceViewComponent channel) {
        Log.d(TAG, "removeFavorite: channel " + channel.mySurfaceViewNewChannelId);
        FavoritePair favorite = new FavoritePair(channel.deviceId, channel.mySurfaceViewNewChannelId);
        favoritesList.remove(favorite);
        channel.setFavorite(false);
        int channelPosition = findChannelManagerByDevice(channel.deviceId).getChannelSelected(channel.mySurfaceViewNewChannelId);
        findChannelManagerByDevice(channel.deviceId).surfaceViewComponents.get(channelPosition).setFavorite(false);
        favoriteChannels--;
        favoriteDevice.setChannelNumber(favoriteChannels);
//        favoriteManager.createComponents();
        favoriteManager.clearSurfaceViewComponents();
        expandableListAdapter.updateGrid(deviceChannelsManagers.indexOf(favoriteManager), favoriteManager);
//        if(favoriteChannels == 0)
//            expandableListAdapter.notifyDataSetChanged();
        saveData();
    }

    public void removeDeviceFromFavorite(int deviceId) {
        ArrayList<Integer> toRemove = new ArrayList<>();
        int i = 0;
        for (FavoritePair pair : favoritesList) {
            if (pair.deviceId == deviceId)
                toRemove.add(i);
            i++;
        }
        for (int j = toRemove.size() - 1; j >= 0; j--) {
            favoritesList.remove((int) toRemove.get(j));
            favoriteChannels--;
        }
        if (!toRemove.isEmpty()) {
            favoriteDevice.setChannelNumber(favoriteChannels);
//            expandableListAdapter.updateGrid(deviceChannelsManagers.indexOf(favoriteManager), favoriteManager);
        }
        Log.d(TAG, "removeDeviceFromFavorite: ");
    }

    public void cleanFavorites() {
        for (FavoritePair pair : favoritesList) {
            for (int i = 0; i < findChannelManagerByDevice(pair.deviceId).surfaceViewComponents.size(); i++) {
                findChannelManagerByDevice(pair.deviceId).surfaceViewComponents.get(i).setFavorite(false);
            }
        }
        favoritesList = new ArrayList<FavoritePair>();
        favoriteChannels = 0;
        favoriteDevice.setChannelNumber(favoriteChannels);
        expandableListAdapter.updateGrid(deviceChannelsManagers.indexOf(favoriteManager), favoriteManager);
        saveData();
    }

    public boolean someDeviceIsRecording() {
        boolean someRecord = false;
        for (Device device: mDevices) {
            if (device.channelsManager != null) {
                someRecord = device.channelsManager.verifyIfSomeChannelIsRecording();
            }
        }
        return someRecord;
    }

    public void removeDevice(int id){
        removeDeviceFromFavorite(id);
        deviceChannelsManagers.remove(findChannelManagerByDevice(id));
        mDevices.remove(findDeviceById(id));
        saveData();
    }

    private int getConnectionMethod() {
        ConnectivityManager cm = (ConnectivityManager) currentContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork == null)
            return -1;
        else
            return activeNetwork.getType() == ConnectivityManager.TYPE_WIFI ? 1 : 0;
    }

    private void getNetworkIp(Context context) {
        InetAddress ipInet = null;
        short netPrefix = 0;
        WifiManager wifiMan = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        DhcpInfo dhcpInfo = wifiMan.getDhcpInfo();
        String ip = String.valueOf(Utils.intToIp(dhcpInfo.ipAddress));

        try {
            ipInet = InetAddress.getByName(ip);
            NetworkInterface networkInterface = NetworkInterface.getByInetAddress(ipInet);
            for (InterfaceAddress address : networkInterface.getInterfaceAddresses()) {
                netPrefix = address.getNetworkPrefixLength();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.d(TAG, "init: IP Adress " + ip);
        Log.d(TAG, "init: IP Adress Mask " + netPrefix);

        networkMask = netPrefix;
    }

    public String getNetworkName() {
        return networkName;
    }

    public void mediaPlayerMessageAlreadySeen() {
        ComplexPreferences cp = new ComplexPreferences(currentContext, MEDIA_DISK_NAME, MODE_PRIVATE);
        cp.putObject("mediaMessage", false);

        cp.apply();
    }

    public boolean showMediaCheckbox() {
        ComplexPreferences cp = new ComplexPreferences(mContext, MEDIA_DISK_NAME, MODE_PRIVATE);
        if (cp.getObject("mediaMessage", Boolean.class) != null) {
            return cp.getObject("mediaMessage", Boolean.class);
        } else {
            return true;
        }
    }

    public void saveImage(File file) {
        try {

            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(file.getPath());
            Bitmap bitmap = retriever.getFrameAtTime(1000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
            int position = 0;
            mVideoFiles.add(position, file);
            videoUris.add(position, Uri.fromFile(file));
            if (bitmap != null) {
                savedMediaVideos.add(position, new BitmapDrawable(mContext.getResources(), bitmap));
                savedMediaVideosPositionOk.add(position, true);
            } else {
                savedMediaVideos.add(position, null);
                savedMediaVideosPositionOk.add(position, true);
            }
        } catch(RuntimeException e) {
            e.printStackTrace();
        }
    }

    public void deleteImage(final int position) {
        savedMediaVideos.remove(position);
        savedMediaVideosPositionOk.remove(position);
        videoUris.remove(position);
        mVideoFiles.remove(position);
    }

    public ArrayList getImages() {
        return savedMediaVideos;
    }

    public ArrayList getImagesBoolean() {
        return savedMediaVideosPositionOk;
    }

    public ArrayList<File> getmVideoFiles() {
        return mVideoFiles;
    }

    public ArrayList<Uri> getVideoUris() {
        return videoUris;
    }

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

    @Override
    public int OnFunSDKResult(Message msg, MsgContent msgContent){
        Log.d(TAG, "msg.what : " + msg.what);
        Log.d(TAG, "msg.arg1 : " + msg.arg1);
        Log.d(TAG, "msg.arg2 : " + msg.arg2);
        if (null != msgContent) {
            Log.d(TAG, " .sender : " + msgContent.sender);
            Log.d(TAG, "msgContent.seq : " + msgContent.seq);
            Log.d(TAG, "msgContent.str : " + msgContent.str);
            Log.d(TAG, "msgContent.arg3 : " + msgContent.arg3);
            Log.d(TAG, "msgContent.pData : " + msgContent.pData);
        }

        checkMemory(mContext);
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
                    Log.d(TAG, "***************** LOGIN *****************");
                    Log.d(TAG, "OnFunSDKResult: Login SUCCESS " + device.deviceName);
                    Log.d(TAG, "Device: " + device.deviceName);
                    Log.d(TAG, "Connection String: " + device.connectionString);
                    Log.d(TAG, "*****************************************");

                    device.isLogged = true;
                    device.loginAttempt = 1;
                    device.setConnectionMethod(-1);

                    favoriteManager.refreshFromDevice(device.getId());
                    FunSDK.SysGetDevState(getHandler(), device.connectionString, device.getId());
                    FunSDK.DevGetConfigByJson(getHandler(), device.connectionString, "SystemInfo", 4096, -1, 10000, device.getId());
                    FunSDK.DevGetConfigByJson(getHandler(), device.connectionString, "Status.NatInfo", 4096, -1, 10000, device.getId());
                } else if(msg.arg1 == -11301){ //wrong password or login
                    if(device != null) {
                        if (loginList.get(device.getId()) != null)
                            loginList.get(device.getId()).onLoginError(msg.arg1, device);
                        loginList.remove(device.getId());
                        device.setConnectionMethod(-1);
                    }
                } else if(msg.arg1 == -11302){ //wrong password or login
                    if(device != null) {
                        if (loginList.get(device.getId()) != null)
                            loginList.get(device.getId()).onLoginError(msg.arg1, device);
                        loginList.remove(device.getId());
                        device.setConnectionMethod(-1);
                    }
                } else if (device != null) {
                    if (device.allAttempstFail) {
                        if (loginList.get(device.getId()) != null)
                            loginList.get(device.getId()).onLoginError(msg.arg1, device);
                        loginList.remove(device.getId());
                    } else {
                        try {
                            loginAttempt(device);
                            loginList.get(device.getId()).onLoginError(msg.arg1, device);
                        } catch (Exception error) {
                            error.printStackTrace();
                        }
                    }
                }
            }
            break;
            case EUIMSG.SYS_GET_DEV_STATE:{
                if(msg.arg1 == 40 || msg.arg1 == 8)
                    findDeviceById(msgContent.seq).dss = true;
                }
            break;
            case EUIMSG.DEV_GET_JSON:
            {
                if(msg.arg1 >= 0 && msgContent.pData != null){
                    JSONObject json = null;
                    Log.d(TAG, "OnFunSDKResult: GETCONFIGJSON SUCCESS");
                    Device device = findDeviceById(msgContent.seq);
                    try {
                        String jsonText = G.ToStringJson(msgContent.pData);
                        json = new JSONObject(jsonText);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    switch(msgContent.str){
                        case "SystemInfo":{
                            if (json != null && device != null) {
                                setDeviceInfo(json, device);
                                device.setConnectionMethodString(connectionMethod(msg.arg2));
                                FunSDK.DevGetChnName(getHandler(), device.connectionString, device.getUsername(), device.getPassword(), device.getId());
                            }
                        }
                        break;
                        case "NetWork.NetCommon":{
                            handleEthernetConfig(json, device);
                            if(currentConfigListener != null)
                                currentConfigListener.onReceivedConfig();
                        }
                        break;
                        case "NetWork.NetDNS" :{
                            handleDNSConfig(json, device);
                            currentConfigListener.onReceivedConfig();
                        }
                        break;
                        case "NetWork.NetDDNS":{
                            handleDDNSConfig(json, device);
                            if(currentConfigListener != null)
                                currentConfigListener.onReceivedConfig();
                        }
                        break;
                        case "NetWork.Upnp":{
                            handleUPnPConfig(json, device);
                            currentConfigListener.onReceivedConfig();
                        }
                        break;
                        case "Simplify.Encode":{
                            setStreamEncode(json, device);
                            if(currentConfigListener != null)
                                currentConfigListener.onReceivedConfig();
                        }
                        break;
                        case "EncodeCapability":{
                            handleEncodeCapability(json, device);
                            currentConfigListener.onReceivedConfig();
                        }
                        break;
                        case "Status.NatInfo":{
                            handleNatInfo(json, device);
                        }
                        break;
                        /*case "Uart.PTZ":{
                            Log.d(TAG, "OnFunSDKResult: Possui ptz" + device.connectionString);
                        }
                        break;*/
                    }

                } else{
                    try {
                        Device device = findDeviceById(msgContent.seq);
//                        FunSDK.DevGetConfigByJson(getHandler(), device.connectionString, msgContent.str, 4096, -1, 10000, device.getId());
                        Log.d(TAG, "OnFunSDKResult: GETCONFIGJSON ERROR");
                        if (!devicesWithJsonError.contains(device)) {
                            devicesWithJsonError.add(device);
                        }
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
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
                } else {
                    if (currentConfigListener != null) {
                        Log.d(TAG, "OnFunSDKResult: CONFIG SET ERROR");
                        currentConfigListener.onError();
                    }
                }
            }
            break;
            case EUIMSG.DEV_GET_CONFIG:
            {
                if(msg.arg1 >= 0){
                    try {
                        Log.d(TAG, "OnFunSDKResult: GETCONFIG SUCCESS");
                        String data = G.ToString(msgContent.pData);
                        Log.d(TAG, "--> DATA: " + data);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else{
                    Log.d(TAG, "OnFunSDKResult: GETCONFIG ERROR");
                }
            }
            break;
            case EUIMSG.DEV_GET_CHN_NAME:
            {
                Device device = findDeviceById(msgContent.seq);
                device.setChannelNumber(msg.arg1);
                if(loginList.get(device.getId()) != null) {
                    loginList.get(device.getId()).onLoginSuccess(device);
                }
                loginList.remove(device.getId());
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
                    final H264_DVR_FILE_DATA files[] = new H264_DVR_FILE_DATA[msg.arg1];
                    final byte[] data = msgContent.pData;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            for (int i = 0; i < files.length; i++) {
                                files[i] = new H264_DVR_FILE_DATA();
                            }
                            G.BytesToObj(files, data);

                            ((DevicePlaybackActivity)tempContext).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    currentPlaybackSearchListener.onFindList(files);
                                }
                            });
                        }
                    }).start();
                }
            }
            break;
            case EUIMSG.DEV_ON_DISCONNECT:
            {
                /*if(msgContent.str != null){
                    String mensagem = "Dispositivo desconectado";
                    Device device = findDeviceById(msgContent.str.hashCode());
                    if(device != null) {
                        mensagem = mensagem.concat(" (" + device.connectionString + ")");
                        device.isLogged = false;
//                        FunSDK.SysGetDevState(getHandler(), device.connectionString, device.getId());
                    }
                    Toast.makeText(currentContext, mensagem, Toast.LENGTH_LONG).show();
                }*/
            }
            break;
            case EUIMSG.DEV_CMD_EN:
            {
                if(msg.arg1 >= 0 && msgContent != null){
                    try {
                        JSONObject json = null;
                        String data = G.ToString(msgContent.pData);
                        Log.d(TAG, "--> DATA: " + data);
                        Device device = findDeviceById(msgContent.seq);
                        if(msgContent.str.equals("OPTimeQuery")){
                            json = new JSONObject(data);
                            handleTimeConfig(json, device);
                            if(currentConfigListener != null)
                                currentConfigListener.onReceivedConfig();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if(msg.arg1 < 0){
                    if(msgContent.str.equals("OPTimeQuery") && currentConfigListener != null)
                        currentConfigListener.onReceivedConfig();
                }
            }
            break;
            case EUIMSG.DEV_SEARCH_PIC:
            {
                if (msg.arg1 == 0) {
                    Log.e("SEARCHPIC", "OK");
                }
            }
            break;
            case EUIMSG.ON_FILE_DOWNLOAD:
            {
                if (msg.arg1 >= 0) {
                    String object = msgContent.str;
                    int lengthIndex = object.indexOf("length");
                    int finalLength = object.indexOf(";", lengthIndex);
                    int fileSize = Integer.valueOf(object.substring(lengthIndex + 7, finalLength));
                    downloadPlaybackListener.onStartDownload(fileSize);
                } else {
                    downloadPlaybackListener.onErrorDownload();
                }
            }
            break;
            case EUIMSG.ON_FILE_DLD_COMPLETE:
            {
                if (msg.arg1 >= 0) {
                    downloadPlaybackListener.onFinishDownload();
                }
            }
            break;
            case EUIMSG.ON_FILE_DLD_POS:
            {
                if (msg.arg1 >= 0) {
                    int currentProgress = msg.arg2;
                    int totalProgress = msg.arg1;
                    downloadPlaybackListener.onProgressDownload(currentProgress, totalProgress);
                }
            }
            break;
            case EUIMSG.EMSG_Stop_DownLoad:
            {
                if (msg.arg1 == 0) {
                    downloadPlaybackListener.onCancelDownload();
                }
            }
            break;
        }
        return 0;
    }

    public String connectionMethod(int methodNumber){
        String connectionString = "";
        switch (methodNumber){
            case 0:
                connectionString = "P2P";
                break;
            case 1:
                connectionString = "Transmit Mode / Proxy";
                break;
            case 2:
                connectionString = "IP";
                break;
            case 5:
                connectionString = "RPS";
                break;
        }
        return connectionString;
    }

    public static String getAppUuid() {
        return APP_UUID;
    }

    public static String getServerIpOld() {
        return SERVER_IP_OLD;
    }

    public static String getServerIp() {
        return SERVER_IP;
    }

    public static void setServerIp(String serverIp) {
        SERVER_IP = serverIp;
    }

    public static String getServerIpNew() {
        return SERVER_IP_NEW;
    }

    public double checkMemory(Context context) {
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        ActivityManager activityManager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        activityManager.getMemoryInfo(mi);
        double availableMegs = mi.availMem / 0x100000L;

        // Total memory
        double totalMem = mi.totalMem / 0x100000L;

        //Percentage can be calculated for API 16+
        double percentAvail = mi.availMem / (double)mi.totalMem * 100.0;

//        Log.d("Check Memory", "********************* MEMORY *********************");
//        Log.d("totalMem", String.valueOf(totalMem));
//        Log.d("availableMem", String.valueOf(availableMegs));
//        Log.d("percentAvail", String.valueOf(percentAvail));

        return availableMegs;
    }
}