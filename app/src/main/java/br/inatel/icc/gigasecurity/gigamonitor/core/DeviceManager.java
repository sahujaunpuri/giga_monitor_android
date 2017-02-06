package br.inatel.icc.gigasecurity.gigamonitor.core;

import android.content.ContentValues;
import android.content.Context;
import android.os.Environment;
import android.os.Message;
import android.util.Log;
import android.provider.MediaStore;
import android.view.Surface;

import com.basic.G;
import com.google.gson.annotations.Expose;
import com.lib.EFUN_ATTR;
import com.lib.IFunSDKResult;
import com.lib.MsgContent;
import com.lib.sdk.struct.SDBDeviceInfo;

import br.inatel.icc.gigasecurity.gigamonitor.activities.DeviceListActivity;
import br.inatel.icc.gigasecurity.gigamonitor.listeners.*;
import br.inatel.icc.gigasecurity.gigamonitor.R;

import com.lib.FunSDK;
import com.lib.EUIMSG;
import com.lib.sdk.struct.SDK_CONFIG_NET_COMMON_V2;
import com.lib.sdk.struct.SDK_ChannelNameConfigAll;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import br.inatel.icc.gigasecurity.gigamonitor.model.Device;
//import br.inatel.icc.gigasecurity.gigamonitor.model.FileDataGiga;
import br.inatel.icc.gigasecurity.gigamonitor.model.LoginMethod;
import br.inatel.icc.gigasecurity.gigamonitor.model.SurfaceViewComponent;
import br.inatel.icc.gigasecurity.gigamonitor.util.BitmapUtil;
import br.inatel.icc.gigasecurity.gigamonitor.util.ComplexPreferences;
//import br.inatel.icc.gigasecurity.gigamonitor.task.LoginDeviceAsyncTask;

/**
 * Created by rinaldo.bueno on 29/08/2014.
 */
public class DeviceManager implements IFunSDKResult{
    private static final String APP_UUID = "e29c9d4ac9fa41fab19413885818ca54";
    private static final String APP_KEY = "d55b6614829f4d1c84d3ab2a9193234b";
    private static final String APP_SECRET = "7a58fdbc242b4f6ba95652b7a3502b91";
    private static final int APP_MOVECARD = 8;
//    private static final String SERVER_IP = "200.98.128.50";
//    private static final int SERVER_PORT = 8000;
    private static final String SERVER_IP = "223.4.33.127;54.84.132.236;112.124.0.188";
    private static final int SERVER_PORT = 15010; // 更新版本的服务器端口


    private static String TAG = DeviceManager.class.getSimpleName();
    private static DeviceManager mInstance = null;
    private int mFunUserHandler;
    public int nSeq = 0;

    private final HashMap<Integer, Device> mLoggedDevices = new HashMap<Integer, Device>();
    public LinkedList<SurfaceViewComponent> startList = new LinkedList<SurfaceViewComponent>();
    public boolean startPlay = false;


    public boolean channelOnRec;
    private ArrayList<Device> mDevices = new ArrayList<Device>();
    private ArrayList<Device> mLanDevices = new ArrayList<Device>();
    private LoginDeviceInterface currentInterface;


    private DeviceManager() {
    }


    public static synchronized DeviceManager getInstance() {
        if (mInstance == null) {
            mInstance = new DeviceManager();
        }
        return mInstance;
    }

    public void init(Context context){
        int result;
        Log.d(TAG, "DeviceManager: INIT");


        InitParam initparam = new InitParam();

//        FunSDK.InitEx(0, G.ObjToBytes(initparam), /*CUSTOM PASSWORD*/, /*CUSTOM SERVER ADDRESS*/, /*CUSTOM PORT*/);

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

        teste();
    }

    private void teste(){
//        Device device = new Device();
//        device.setHostname("teste");
//        device.setSerialNumber("07c1454a72ef1c5b");
//        device.setIpAddress("07c1454a72ef1c5b");
//        device.setUsername("admin");
//        device.setPassword("");
//        device.setMacAddress("07c1454a72ef1c5b");
        //device.setChannelNumber(4);

//        device.setSerialNumber("2ced13a121185528");
//        device.setIpAddress("2ced13a121185528");
//        device.setUsername("admin");
//        device.setPassword("");
//        device.setMacAddress("2ced13a121185528");
//        device.setChannelNumber(1);

//        mDevices.add(device);
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
                        currentInterface.onLoginSuccess();
                    else
                        FunSDK.DevGetConfigByJson(getHandler(), device.getSerialNumber(), "SystemInfo", 4096, -1, 10000, device.getId());
                }
                else {
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
                    Log.d(TAG, "OnFunSDKResult: Device OFFLINE");
                }
            }
            break;
            case EUIMSG.DEV_GET_JSON:
            {
                if(msg.arg1 >= 0){
                    Device device = findDeviceById(msgContent.seq);
                    FunSDK.DevGetChnName(getHandler(), device.getSerialNumber(), device.getUsername(), device.getPassword(), nSeq);
                    Log.d(TAG, "OnFunSDKResult: GETCONFIGJSON SUCCESS");
                } else{
                    Device device = findDeviceById(msgContent.seq);
                    FunSDK.DevGetConfigByJson(getHandler(), device.getSerialNumber(), "SystemInfo", 4096, -1, 15000, device.getId());
                    Log.d(TAG, "OnFunSDKResult: GETCONFIGJSON ERROR");
                }
            }
            break;
            case EUIMSG.DEV_GET_CHN_NAME:
            {
                Log.d(TAG, "OnFunSDKResult: ongetchnname");
                if (msg.arg1 >= 0) {
                    if (msgContent.pData != null && msgContent.pData.length > 0) {
                        SDK_ChannelNameConfigAll channel = new SDK_ChannelNameConfigAll();
                        G.BytesToObj(channel, msgContent.pData);
                        channel.nChnCount = msg.arg1;
                        mDevices.get(msgContent.seq).setChannel(channel);
                        mDevices.get(msgContent.seq).setChannelNumber(msg.arg1);
                        saveDevices(DeviceListActivity.mContext);
                        currentInterface.onLoginSuccess();
                    }
                }
            }
            break;

        }
        return 0;
    }

    private void loadSavedData(Context context){
        mDevices = loadDevices(context);
    }

    public void addDevice(Context context, Device device) {
        mDevices.add(device);
        saveDevices(context);
    }

    private boolean saveDevices(Context context) {
        ComplexPreferences cp = new ComplexPreferences(context, context.getString(R.string.sheredpreference_list), Context.MODE_PRIVATE);
        cp.putObject("DeviceList", new DeviceList(mDevices));
        return cp.commit();
    }

    private ArrayList<Device> loadDevices(Context context) {
        ComplexPreferences cp = new ComplexPreferences(context, context.getString(R.string.sheredpreference_list), Context.MODE_PRIVATE);
        DeviceList deviceList = cp.getObject("DeviceList", DeviceList.class);
        if (deviceList == null || deviceList.getList() == null) {
            return new ArrayList<Device>();
        }
        return deviceList.getList();
    }

    public ArrayList<Device> getDevices(){
        return mDevices;
    }

    public void updateDevices(Context context, ArrayList<Device> devices){
        mDevices = devices;
        saveDevices(context);
    }

    public int getHandler(){
        return mFunUserHandler;
    }

    public void loginDevice(final Device device, final LoginDeviceInterface loginDeviceInterface) {
        currentInterface = loginDeviceInterface;
        FunSDK.SysGetDevState(getHandler(), device.getSerialNumber(), device.getId());
//        FunSDK.DevLogin(getHandler(), device.getSerialNumber(), device.getUsername(), device.getPassword(), device.getId());
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
        return (FunSDK.DevLogout(getHandler(), device.getSerialNumber(), device.getId()) == 0);
    }

    public int changePassword(Device device, String newPassword) {
        return FunSDK.DevSetLocalPwd(device.getSerialNumber(), device.getUsername(), newPassword);
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
        startList.add(surfaceViewComponent);
        if(!startPlay)
            requestStart();
    }

    public void requestStart(){
        if(!startList.isEmpty()){
            startPlay = true;
            startList.getFirst().onStartVideo();
        } else{
            startPlay = false;
        }
    }

    // TODO
    /*public void testPTZ(Device device) throws Exception {
        mNetSdk.PTZControl(device.getLoginID(), 0, MyConfig.PTZ_ControlType.ZOOM_OUT, false, 4L, 0L, 0L);
    }*/

    /*public void rebootDevice(Device device) {
        mNetSdk.H264DVRControlDVR(device.getLoginID(), 0, 2000);
    }*/

    /*public ArrayList<FileDataGiga> findPlaybacks(long loginID, FindInfo findInfo) {
        ArrayList<FileDataGiga> playbacks = new ArrayList<FileDataGiga>();

        int filesFoundCount;

        do {
            com.xm.FileData[] fileData = new com.xm.FileData[64];

            // Initiating each FileData
            for (int i = 0; i < 64; i++) {
                fileData[i] = new com.xm.FileData();
            }

            filesFoundCount = mNetSdk.FindFile(loginID, findInfo, fileData, 256, 5000);

            for (int i = 0; i < filesFoundCount; i++) {
                playbacks.add(new FileDataGiga(fileData[i]));
            }

            if (filesFoundCount == 64) {
                findInfo.startTime.year = fileData[63].stEndTime.year;
                findInfo.startTime.month = fileData[63].stEndTime.month;
                findInfo.startTime.day = fileData[63].stEndTime.day;
                findInfo.startTime.hour = fileData[63].stEndTime.hour;
                findInfo.startTime.minute = fileData[63].stEndTime.minute;
                findInfo.startTime.second = fileData[63].stEndTime.second;
            }

        } while (filesFoundCount == 64);


        return playbacks;
    }*/

    /*public void playbackPlay(final Device device, final FileDataGiga fileDataGiga,
                             final MySurfaceView sv, final int svID) {

        final long playHandle = mNetSdk.PlayBackByName(svID,
                device.getLoginID(), fileDataGiga.getSdkFileData(),device.getChannelNumber()-1);

        Log.d(TAG, playHandle != 0 ? "Playback started!" : "Playback NOT started!");

        device.setPlaybackHandle(playHandle);

        sv.initData();
        sv.onPlay();
    }*/

    /*public void playbackStop(final long playbackHandle, final MySurfaceView sv) {
        if (mNetSdk.StopPlayBack(playbackHandle)) sv.onStop();
    }*/

    /*public void playbackResume(final long playbackHandle, final MySurfaceView sv) {
        sv.onPlay();
        mNetSdk.PlayBackControl(playbackHandle,
                MyConfig.PlayBackAction.SDK_PLAY_BACK_CONTINUE, 0L);
    }*/

    /*public void playbackPause(final long playbackHandle, final MySurfaceView sv) {
        sv.onPause();
        mNetSdk.PlayBackControl(playbackHandle,
                MyConfig.PlayBackAction.SDK_PLAY_BACK_PAUSE, 0L);
    }*/

    /*public boolean playbackFaster(long playbackHandle, MySurfaceView sv) {

        if(sv.mplaystatus != MyConfig.PlayState.MPS_FAST) {
            sv.onPause();
            sv.onPlayBackFast();

            return mNetSdk.PlayBackControl(playbackHandle, MyConfig.PlayBackAction.SDK_PLAY_BACK_FAST, 3);
        }

        return false;
    }*/

    /*public boolean playbackSlow(long playbackHandle, MySurfaceView sv) {

        if(sv.mplaystatus != MyConfig.PlayState.MPS_SLOW) {
            sv.onPause();
            sv.onPlayBackSlow();

            return mNetSdk.PlayBackControl(playbackHandle, MyConfig.PlayBackAction.SDK_PLAY_BACK_SLOW, 3);
        }

        return false;
    }*/

    /*public boolean setPlaybackProgress(final long playbackHandle, final int progress) {
        return mNetSdk.PlayBackControl(playbackHandle, MyConfig.PlayBackAction.SDK_PLAY_BACK_SEEK_PERCENT,
                progress);
    }*/

    /*public void setOnPlaybackCompleteListener(NetSdk.OnRPlayBackCompletedListener l) {
        mNetSdk.setOnRPlayBackCompletedListener(l);
    }*/

    // Key => Device.voiceHandle
    /*private HashMap<Long, VoiceIntercom> mVoiceSessions;

    private Handler mVoiceHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Log.d(TAG, msg.toString());
            super.handleMessage(msg);
        }
    };

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


    /*public void setConfigOverNet(final Device device, final String username, final String password, final String ipAddress,
                                 final String gateway, final String mask, final String mac, final setConfigOverNetInterface configOverNetInterface){

        new Thread(new Runnable() {
            @Override
            public void run() {

                SDK_CONFIG_NET_COMMON_V3 config = new SDK_CONFIG_NET_COMMON_V3();

                G.SetValue(config.st_14_sMac, device.getMacAddress());
                G.SetValue(config.st_15_UserName, username);
                G.SetValue(config.st_16_Password, password);
                G.SetValue(config.st_17_LocalMac, mac);
                G.SetValue(config.st_00_HostName, device.getHostname());
                G.SetValue(config.st_17_Zarg0, "Giga");
                config.st_06_SSLPort = device.getSslPort();
                config.st_05_TCPPort = device.getTCPPort();
                config.st_07_UDPPort = device.getUdpPort();
                config.st_04_HttpPort = device.getHttpPort();

                final int[] ipAddressArray = Utils.parseIp(ipAddress);
                final int[] gatewayArray = Utils.parseIp(gateway);
                final int[] maskArray = Utils.parseIp(mask);

                config.st_18_nPasswordType = 1;
                config.st_01_HostIP.st_0_ip[0] = (byte)ipAddressArray[0];
                config.st_01_HostIP.st_0_ip[1] = (byte)ipAddressArray[1];
                config.st_01_HostIP.st_0_ip[2] = (byte)ipAddressArray[2];
                config.st_01_HostIP.st_0_ip[3] = (byte)ipAddressArray[3];

                config.st_03_Gateway.st_0_ip[0] = (byte)gatewayArray[0];
                config.st_03_Gateway.st_0_ip[1] = (byte)gatewayArray[1];
                config.st_03_Gateway.st_0_ip[2] = (byte)gatewayArray[2];
                config.st_03_Gateway.st_0_ip[3] = (byte)gatewayArray[3];

                config.st_02_Submask.st_0_ip[0] = (byte)maskArray[0];
                config.st_02_Submask.st_0_ip[1] = (byte)maskArray[1];
                config.st_02_Submask.st_0_ip[2] = (byte)maskArray[2];
                config.st_02_Submask.st_0_ip[3] = (byte)maskArray[3];

                boolean success = mNetSdk.SetConfigOverNet(MyConfig.SdkConfigType.E_SDK_CONFIG_SYSNET, -1, G.ObjToBytes(config), 1000);

                if (success) {
                    configOverNetInterface.onSetConfigOverNetSuccess();
                } else {
                    configOverNetInterface.onSetConfigOverNetError();
                }
            }
        }).start();

    }

    public interface setConfigOverNetInterface {
        void onSetConfigOverNetSuccess();

        void onSetConfigOverNetError();
    }*/

    /*public boolean remoteControl(long loginID, int keyboardValue) {
        return mNetSdk.H264DVRClickKey(loginID, keyboardValue, MyConfig.NetKeyBoardState.SDK_NET_KEYBOARD_KEYDOWN);
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