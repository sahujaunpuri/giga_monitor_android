package br.inatel.icc.gigasecurity.gigamonitor.core;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;

import com.basic.G;
import com.google.gson.annotations.Expose;
import com.xm.ChnInfo;
import com.xm.DevInfo;
import com.xm.FindInfo;
import com.xm.MyConfig;
import com.xm.NetSdk;
import com.xm.SDK_AllAlarmIn;
import com.xm.SDK_AllAlarmOut;
import com.xm.SearchDeviceInfo;
import com.xm.audio.VoiceIntercom;
import com.xm.javaclass.SDK_CONFIG_NET_COMMON_V3;
import com.xm.javaclass.SDK_LogList;
import com.xm.javaclass.SDK_LogSearchCondition;
import com.xm.video.MySurfaceView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import br.inatel.icc.gigasecurity.gigamonitor.R;
import br.inatel.icc.gigasecurity.gigamonitor.model.Device;
import br.inatel.icc.gigasecurity.gigamonitor.model.FileDataGiga;
import br.inatel.icc.gigasecurity.gigamonitor.model.LoginMethod;
import br.inatel.icc.gigasecurity.gigamonitor.task.LoginDeviceAsyncTask;
import br.inatel.icc.gigasecurity.gigamonitor.util.BitmapUtil;
import br.inatel.icc.gigasecurity.gigamonitor.util.ComplexPreferences;
import br.inatel.icc.gigasecurity.gigamonitor.util.Utils;

/**
 * Created by rinaldo.bueno on 29/08/2014.
 */
public class DeviceManager {

    private static String TAG = DeviceManager.class.getSimpleName();
    private NetSdk mNetSdk = null;

    private final HashMap<Long, Device> mLoggedDevices = new HashMap<Long, Device>();

    private int channelOnRec;


    private DeviceManager() {

        NetSdk.H264DVRUserData(1000,"200.98.128.50");
        NetSdk.DevInit();

        mNetSdk = NetSdk.getInstance(1000, "200.98.128.50:8000"); //Parameters are meant to login with "GIGA_" prefix

        mNetSdk.setOnDisConnectListener(new NetSdk.OnDisConnectListener() {
            @Override
            public void onDisConnect(int i, long loginId, byte[] bytes, long l2) {
                Log.d(TAG, String.format("Device loginId: %s Disconnected.", loginId));

                synchronized (mLoggedDevices) {
                    mLoggedDevices.remove(loginId);
                }

                Log.d(TAG, String.valueOf(mLoggedDevices.size()));
            }
        });

        mVoiceSessions = new HashMap<Long, VoiceIntercom>();

        channelOnRec = -1;  //No channel on rec
    }

    public void loginDevice(final Device device, final LoginMethod lm, final LoginDeviceInterface loginDeviceInterface) {

        new Thread(new Runnable() {
            @Override
            public void run() {

                long frt;

                switch (lm) {
                    case TRY_ALL: default:
                        frt = tryLoginAll(device, loginDeviceInterface);
                        break;
                    case LAN:
                        loginDeviceInterface.onLoginLAN();
                        frt = loginLAN(device);
                        break;
                    case CLOUD:
                        loginDeviceInterface.onLoginCloud();
                        frt = loginCloud(device);
                        break;
                }

                if(frt == 0) {
                    loginDeviceInterface.onLoginError(frt);
                } else {
                    loginDeviceInterface.onLoginSuccess(frt);
                }

            }
        }).start();

    }

    public interface LoginDeviceInterface {
        void onLoginSuccess(long loginID);
        void onLoginError(long loginID);

        void onLoginCloud();
        void onLoginLAN();
        void onLoginDDNS();
    }

    public long loginOnDevice(final Device device, LoginMethod lm) {
        long frt;

        switch (lm) {
            case TRY_ALL: default:
                //frt = tryLoginAll(device);
                frt = 0;
                break;
            case LAN:
                frt = loginLAN(device);
                break;
            case CLOUD:
                frt = loginCloud(device);
                break;
        }

        return frt;
    }

    private long tryLoginAll(Device device, final LoginDeviceInterface loginDeviceInterface) {
        long loginRet;

        if (!mLoggedDevices.containsValue(device)) {

            if(device.getIpAddress() != "") {
                loginDeviceInterface.onLoginLAN();
                loginRet = loginLAN(device);
            } else {
                loginRet = 0;
            }

            if (loginRet == 0 && device.getDdnsDomain() != "") {
                loginDeviceInterface.onLoginDDNS();
                loginRet = loginDDNSnUNPnP(device);
            }

            if (loginRet == 0) {
                loginDeviceInterface.onLoginCloud();
                loginRet = loginCloud(device);
            }

            if (loginRet != 0) {
                putLoggedDevice(device);
            }
        } else {
            loginRet = device.getLoginID();
        }

        return loginRet;
    }

    private void putLoggedDevice(Device device) {
        long login = device.getLoginID();

        synchronized (mLoggedDevices) {
            mLoggedDevices.put(login, device);
        }
    }

    private void removeLoggedDevice(Device device) {
        long login = device.getLoginID();

        synchronized (mLoggedDevices) {
            mLoggedDevices.remove(login);
        }
    }

    public void testPTZ(Device device) throws Exception {
        mNetSdk.PTZControl(device.getLoginID(), 0, MyConfig.PTZ_ControlType.ZOOM_OUT, false, 4L, 0L, 0L);
    }

    public void rebootDevice(Device device) {
        mNetSdk.H264DVRControlDVR(device.getLoginID(), 0, 2000);
    }

    private long loginLAN(Device device) {
        DevInfo deviceInfo = device.getDeviceInfo();

        Log.d("Login", "Attempting to login via LAN ");

        deviceInfo.Socketstyle = MyConfig.SocketStyle.TCPSOCKET;

        int [] error = new int[1];

        long loginid = mNetSdk.onLoginDev(0, deviceInfo, error, MyConfig.SocketStyle.TCPSOCKET);

        if (loginid == 0) {
            Log.d("Login", "FAILED to login via LAN. LoginID: " + loginid + " - Error: " + error);

            if (error[0] != -11301) {
                return 0;
            } else {
                return Long.valueOf(error[0]);
            }


        }

        device.addDeviceInfo(deviceInfo);
        device.setLoginID(loginid);

        Log.d("Login", "SUCCESS to login via LAN.");

        return loginid;
    }

    private long loginDDNSnUNPnP(Device device) {
        Log.d("Login", "Attempting to login via loginDDNS and UNPnP.");

        if(LoginDeviceAsyncTask.mProgressDialog != null) {
            String text = "Connecting via DDNS.";

            LoginDeviceAsyncTask.changeProgressDialogMsg(text);
        }

        DevInfo deviceInfo = device.getDeviceInfo();

        String ip = deviceInfo.Ip;

        deviceInfo.Ip = device.getDomain();
        deviceInfo.Socketstyle = MyConfig.SocketStyle.TCPSOCKET;

        int [] error = new int[1];

        long loginid = mNetSdk.onLoginDev(0, deviceInfo, error, MyConfig.SocketStyle.TCPSOCKET);

        deviceInfo.Ip = ip;

        if (loginid == 0) {
            Log.d("Login", "FAILED to login via loginDDNS and UNPnP. LoginID: " + loginid);

            if (error[0] != -11301) {
                return 0;
            } else {
                return Long.valueOf(error[0]);
            }
        }

        device.addDeviceInfo(deviceInfo);
        device.setLoginID(loginid);

        Log.d("Login", "SUCCESS to login via loginDDNS and UNPnP.");

        return loginid;
    }

    private long loginCloud(Device device) {
        if(device.getSerialNumber().equals("")) {
            return 0;
        }

        DevInfo deviceInfo = device.getDeviceInfo();

        Log.d("Login", "Attempting to login via Cloud ");

        String ip = deviceInfo.Ip;

        deviceInfo.Ip = deviceInfo.SerialNumber;
        deviceInfo.Socketstyle = MyConfig.SocketStyle.NATSOCKET;

        int [] error = new int[1];

        long loginid = mNetSdk.onLoginDev(0, deviceInfo, error, MyConfig.SocketStyle.NATSOCKET);

        deviceInfo.Ip = ip;

        if (loginid == 0) {
            Log.d("Login", "FAILED to login via Cloud. LoginID: " + loginid);

            device.setLoginID(loginid);

            if (error[0] != -11301) {
                return 0;
            } else {
                return Long.valueOf(error[0]);
            }
        }

        device.addDeviceInfo(deviceInfo);
        device.setLoginID(loginid);

        Log.d("Login", "SUCCESS to login via Cloud.");
        return loginid;
    }

    public void logout(final Device device, final LogoutInterface logoutInterface) throws Exception {

        new Thread(new Runnable() {
            @Override
            public void run() {
                final long loginId = device.getLoginID();
                final long success = mNetSdk.onDevLogout(loginId);

                removeLoggedDevice(device);

                device.setLoginID(0);

                logoutInterface.onFinishLogout(success);
            }
        }).start();

    }

    public interface LogoutInterface {
        void onFinishLogout(long logoutHandle);
    }
//    public String getAccessInfo(Device device){
//
//    }

    public ArrayList<Device> searchDevices() {
        SearchDeviceInfo[] searchDeviceInfos = new SearchDeviceInfo[100];

        final int result = mNetSdk.SearchDevice(searchDeviceInfos, 100, 8000, false);

        ArrayList<Device> list = new ArrayList<Device>();

        for (int i = 0; i < result; i++) {
            list.add(new Device(searchDeviceInfos[i]));
        }

        return list;
    }

    public <T> T getConfig(Long loginId, long key, T config) throws Exception {
        boolean b = mNetSdk.H264DVRGetDevConfig(loginId, key, -1, config, 1000);
        if (!b) {

            Log.d(TAG, String.format("Unable to get configuration. loginId: %d key: %d object: %s", loginId, key, config.getClass().getSimpleName()));
            return null;
        }
        return config;
    }

    public long getLastError() {
        return mNetSdk.GetLastError();
    }

    public <T> boolean setConfig(Long loginId, long key, T config) throws Exception {
        boolean b = NetSdk.getInstance().H264DVRSetDevConfig(loginId, key, -1, config, 1000);

        if (!b) {
            Log.d(TAG, String.format("Unable to set configuration. loginId: %s key: %s object: %s", loginId, key, config.getClass().getSimpleName()));
            Log.w("ERRO:", "ERRO: " + getLastError());
        }

        return b;
    }



/*
    public Object getConfig2(long loginID, int key, Object object) {
        boolean bret;
        int bufsize = com.basic.G.Sizeof(object);
        if(bufsize <= 0)
            return null;
        byte[] buf = new byte[bufsize];
        bret = mNetSdk.H264DVRGetDevConfig2(loginID, key, -1,buf,5000);
        if(bret)
            return com.basic.G.BytesToObj(object, buf);
        else {
            Log.d(TAG, "error:" + mNetSdk.GetLastError());
            return null;
        }
    }*/

    public <T> boolean getConfig2(long loginId, long key, T config) {
        boolean bret = false;

        int buffSize = G.Sizeof(config);

        if (buffSize <= 0)
            return false;

        byte[] buf = new byte[buffSize];

        bret = mNetSdk.H264DVRGetDevConfig2(loginId, key, -1, buf, 5000, 5000);

        if (bret) {
            G.BytesToObj(config, buf);
            return true;
        } else {
            Log.d(TAG, "error: " + mNetSdk.GetLastError());
            return false;
        }
    }

    public <T> boolean setConfig2(long loginId, int key, T config) {
        boolean bret;

        bret = mNetSdk.H264DVRSetDevConfig2(loginId, key, -1, G.ObjToBytes(config), 5000, 5000);

        if (!bret) {
            Log.d(TAG, "error:" + mNetSdk.GetLastError());
        }

        return bret;
    }

    public int changePassword(long loginId, String oldPassword,
                              String newPassword) {

        byte[] oldBuff = new byte[32];

       /*if (!mNetSdk.getDevPwd(loginId, MyConfig.SdkConfigType.E_SDK_CONFIG_USER, -1, oldBuff, 1000, 1000)) {
            return MyConfig.ModifyPwd.USER_NOT_EXIST;
        }*/

        final long param2 = 8L;
        final int channelId = -1;
        final int timeout = 5000;

        return mNetSdk.setDevPwd(loginId, param2, channelId, newPassword.getBytes(), oldPassword.getBytes(),
                MyConfig.SdkConfigType.E_SDK_CONFIG_USER, timeout);

        /*return mNetSdk.setDevPwd(loginId, MyConfig.SdkConfigType.E_SDK_CONFIG_USER, channelId, newPassword.getBytes(), oldPassword.getBytes(),
                timeout, timeout);*/
    }

    public Bitmap takeSnapshot(Device device, int channelNumber) {
        Bitmap bm = null;

        byte[] buffer = new byte[1024 * 250];
        int[] pPicLen = new int[1];
        boolean bret = mNetSdk.H264DVRCatchPicInBuffer(device.getLoginID(), channelNumber,
                buffer, 0, pPicLen);
        if (bret) {
            bm = BitmapFactory.decodeByteArray(buffer, 0, pPicLen[0]);
        }

        return bm;
    }

    public File takeSnapshot(Context context, MySurfaceView surfaceView) {
        final String path = BitmapUtil.getAlbumStorageDir(context).getPath();
        final File pictureFile = surfaceView.OnCapture(context, path);

        if (pictureFile == null) {
            return null;
        }

        Bitmap image = BitmapFactory.decodeFile(pictureFile.getPath());

        try {

            FileOutputStream fos = new FileOutputStream(pictureFile);

            image.compress(Bitmap.CompressFormat.PNG, 90, fos);

            fos.flush();
            fos.close();

        } catch (FileNotFoundException e) {
            Log.w("Error take SNAPSHOT:", "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d("Error take SNAPSHOT:", "Error accessing file: " + e.getMessage());
        }

        return pictureFile;
    }

    public File startSnapvideo(MySurfaceView surfaceView,int channel) {
        if (channelOnRec != -1) { //Cannot record more than one video at the same time
            return null;

        } else {
            channelOnRec = channel;

            File mediaStorageDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                    + "/Movies/Giga Monitor/");

            //Create the storage directory if it does not exist.
            if (!mediaStorageDir.exists()) {
                if (!mediaStorageDir.mkdirs()) {
                    return null;
                }
            }

            surfaceView.initRecord(mediaStorageDir.getAbsolutePath());

            mediaStorageDir = surfaceView.onStartRecord();

            return mediaStorageDir;
        }
    }

    public File stopSnapvideo(final MySurfaceView surfaceView, Context context) {

        channelOnRec = -1;

        File file = surfaceView.onStopRecord(true);

        Log.v("Rocali"," Total Spacee "+file.getTotalSpace() + "  Free Space "+file.getFreeSpace() + " Usable Space "+file.getUsableSpace() +" Lengt "+ file.length());

        if (file.length() > 10000) {  //Save just if the video has more than 10kb
            Log.v("Rocali","Save "+file.length());

            ContentValues values = new ContentValues();

            values.put(MediaStore.Video.Media.TITLE, file.getName());
            values.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4");
            values.put(MediaStore.Video.Media.DATA, file.getAbsolutePath());

            context.getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);

            return file;
        } else {
            return null;
        }
    }

    public boolean cleanDevices(Context context) {
        return saveDevices(context, new ArrayList<Device>());
    }

    public boolean saveDevices(Context context, ArrayList<Device> devices) {
        ComplexPreferences cp = new ComplexPreferences(context, context.getString(R.string.sheredpreference_list), Context.MODE_PRIVATE);
        cp.putObject("DeviceList", new DeviceList(devices));
        return cp.commit();
    }

    public ArrayList<Device> loadDevices(Context context) {
        ComplexPreferences cp = new ComplexPreferences(context, context.getString(R.string.sheredpreference_list), Context.MODE_PRIVATE);
        DeviceList deviceList = cp.getObject("DeviceList", DeviceList.class);
        if (deviceList == null || deviceList.getList() == null) {
            return new ArrayList<Device>();
        }
        return deviceList.getList();
    }

    public ArrayList<FileDataGiga> findPlaybacks(long loginID, FindInfo findInfo) {
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
    }

    public void playbackPlay(final Device device, final FileDataGiga fileDataGiga,
                             final MySurfaceView sv, final int svID) {

        final long playHandle = mNetSdk.PlayBackByName(svID,
                device.getLoginID(), fileDataGiga.getSdkFileData(),device.getChannelNumber()-1);

        Log.d(TAG, playHandle != 0 ? "Playback started!" : "Playback NOT started!");

        device.setPlaybackHandle(playHandle);

        sv.initData();
        sv.onPlay();
    }

    public void playbackStop(final long playbackHandle, final MySurfaceView sv) {
        if (mNetSdk.StopPlayBack(playbackHandle)) sv.onStop();
    }

    public void playbackResume(final long playbackHandle, final MySurfaceView sv) {
        sv.onPlay();
        mNetSdk.PlayBackControl(playbackHandle,
                MyConfig.PlayBackAction.SDK_PLAY_BACK_CONTINUE, 0L);
    }

    public void playbackPause(final long playbackHandle, final MySurfaceView sv) {
        sv.onPause();
        mNetSdk.PlayBackControl(playbackHandle,
                MyConfig.PlayBackAction.SDK_PLAY_BACK_PAUSE, 0L);
    }

    public boolean playbackFaster(long playbackHandle, MySurfaceView sv) {

        if(sv.mplaystatus != MyConfig.PlayState.MPS_FAST) {
            sv.onPause();
            sv.onPlayBackFast();

            return mNetSdk.PlayBackControl(playbackHandle, MyConfig.PlayBackAction.SDK_PLAY_BACK_FAST, 3);
        }

        return false;
    }

    public boolean playbackSlow(long playbackHandle, MySurfaceView sv) {

        if(sv.mplaystatus != MyConfig.PlayState.MPS_SLOW) {
            sv.onPause();
            sv.onPlayBackSlow();

            return mNetSdk.PlayBackControl(playbackHandle, MyConfig.PlayBackAction.SDK_PLAY_BACK_SLOW, 3);
        }

        return false;
    }

    public boolean setPlaybackProgress(final long playbackHandle, final int progress) {
        return mNetSdk.PlayBackControl(playbackHandle, MyConfig.PlayBackAction.SDK_PLAY_BACK_SEEK_PERCENT,
                progress);
    }

    public void setOnPlaybackCompleteListener(NetSdk.OnRPlayBackCompletedListener l) {
        mNetSdk.setOnRPlayBackCompletedListener(l);
    }

    // Key => Device.voiceHandle
    private HashMap<Long, VoiceIntercom> mVoiceSessions;

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
    }

    public void startDeviceVideo(final long loginID, final MySurfaceView sv,
                                 final int surfaceViewID, final ChnInfo ci) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final long handleID = mNetSdk.onRealPlay(surfaceViewID, loginID, ci);

                if (mNetSdk.setDataCallback(handleID) != 0) {
                    // TODO Should we call success listener?
                    sv.initData();
                } else {
                    // TODO Call error listener?
                }
            }
        }).start();
    }

    public void startDeviceVideo2(final long loginID, final int surfaceViewID,
                                     final ChnInfo ci, final StartDeviceVideoListener startDeviceVideoListener) {

        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                final long handleID = mNetSdk.onRealPlay(surfaceViewID, loginID, ci);

                if (mNetSdk.setDataCallback(handleID) != 0) {
                    startDeviceVideoListener.onSuccessStartDevice(handleID);
                } else {
                    startDeviceVideoListener.onErrorStartDevice();
                }

            }
        }).start();
    }

    public interface StartDeviceVideoListener {
        void onSuccessStartDevice(long handleID);

        void onErrorStartDevice();
    }

    public void changeChannel(final long handleID, final long loginID, final int surfaceViewID,
                                  final ChnInfo ci, final MySurfaceView sv, final changeChannelInterface mChangeChannelInterface) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                sv.onStop();

                stopDeviceVideo(handleID, sv);

                mNetSdk.onStopRealPlay(handleID);

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                long newHandleID = mNetSdk.onRealPlay(surfaceViewID, loginID, ci);

                if (mNetSdk.setDataCallback(newHandleID) != 0) {
                    mChangeChannelInterface.onSuccessChangeChannel(newHandleID);
                } else {
                    mChangeChannelInterface.onErrorChangeChannel();
                }

            }
        }).start();
    }

    public interface changeChannelInterface {
        void onSuccessChangeChannel(long handleID);

        void onErrorChangeChannel();
    }

    public void stopDeviceVideo(final long handleID, final MySurfaceView sv) {
        mNetSdk.onStopRealPlay(handleID);
        sv.onStop();
    }

    public void stopDeviceVideo2(final long handleID, final MySurfaceView sv, final StopDeviceVideoListener stopDeviceVideoListener) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    mNetSdk.onStopRealPlay(handleID);
                    sv.onStop();

                    Thread.sleep(1000);

                    stopDeviceVideoListener.onSuccessStopDevice();
                }catch (Exception e) {
                    stopDeviceVideoListener.onErrorStopDevice();
                }

            }
        }).start();
    }

    public interface StopDeviceVideoListener {
        void onSuccessStopDevice();

        void onErrorStopDevice();
    }

    public void pauseDeviceVideo(final MySurfaceView sv) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                sv.onPause();
            }
        }).start();
    }

    public void playDeviceVideo(final MySurfaceView sv) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                sv.onPlay();
            }
        }).start();
    }

    public void setDeviceAudioEnabled(MySurfaceView sv, boolean enabled) {
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
    }


    public void setConfigOverNet(final Device device, final String username, final String password, final String ipAddress,
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
    }

    public boolean remoteControl(long loginID, int keyboardValue) {
        return mNetSdk.H264DVRClickKey(loginID, keyboardValue, MyConfig.NetKeyBoardState.SDK_NET_KEYBOARD_KEYDOWN);
    }


    private static class LazySingleton {
        private static final DeviceManager DEVICE_HANDLER_INSTANCE = new DeviceManager();
    }

    public static DeviceManager getInstance() {
        return LazySingleton.DEVICE_HANDLER_INSTANCE;
    }

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

    public int getChannelOnRec() {
        return channelOnRec+1;
    }


}