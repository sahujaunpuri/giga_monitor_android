//package br.inatel.icc.gigasecurity.gigamonitor.model;
//
//import android.content.Context;
//import android.os.Environment;
//import android.os.Message;
//import android.util.Log;
//import android.widget.Toast;
//
//import com.basic.G;
//import com.lib.EUIMSG;
//import com.lib.FunSDK;
//import com.lib.IFunSDKResult;
//import com.lib.MsgContent;
//import com.lib.SDKCONST;
//import com.lib.sdk.struct.H264_DVR_FILE_DATA;
//
//import java.io.File;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Locale;
//
//import br.inatel.icc.gigasecurity.gigamonitor.core.DeviceManager;
//import br.inatel.icc.gigasecurity.gigamonitor.listeners.PlaybackListener;
//import br.inatel.icc.gigasecurity.gigamonitor.ui.SurfaceViewComponent;
//import br.inatel.icc.gigasecurity.gigamonitor.util.Utils;
//
///**
// * Created by filipecampos on 03/12/2015.
// */
//public class SurfaceViewManager implements IFunSDKResult{
//
//    //IDs / Handlers
//    String TAG = "SurfaceViewComp";
//    String TAG2 = "svcTOUCH";
//
//    private int mUserID = -1;
//    public PlaybackListener currentPlaybackListener;
//    public int recHandler;
//    public ArrayList<SurfaceViewComponent> channels = new ArrayList<SurfaceViewComponent>();
//
//    private Context mContext;
//    private DeviceManager mDeviceManager;
//
//    public SurfaceViewManager(Context context){
//        if(mUserID == -1){
//            mUserID = FunSDK.RegUser(this);
//        }
//        mDeviceManager = DeviceManager.getInstance();
//        mContext = context;
////        surfaceViewLayout = new SurfaceViewLayout(mContext, this);
//    }
//
//
//    /*public void onPlayLive(SurfaceViewComponent svc) {
//        if(svc.isConnected())
//            onResume(svc);
//        else
//            mDeviceManager.addToStart(svc);
//    }*/
//
//    public void onStartVideo(SurfaceViewComponent svc){
//        svc.setConnected(true);
//        svc.mPlayerHandler = FunSDK.MediaRealPlay(mUserID, svc.deviceSn, svc.mySurfaceViewChannelId, svc.streamType, svc.mySurfaceView, svc.mySurfaceViewOrderId);
//    }
//
//    public void onPause(SurfaceViewComponent svc){
//        if (svc.isPlaying) {
//            FunSDK.MediaPause(svc.mPlayerHandler, 1, svc.mySurfaceViewOrderId);
//        }
//    }
//
//    public void onResume(SurfaceViewComponent svc){
//        FunSDK.MediaPause(svc.mPlayerHandler, 0, svc.mySurfaceViewOrderId);
//    }
//
//    public void onStop(SurfaceViewComponent svc){
//        if ( svc.mPlayerHandler != 0 ) {
//            if(svc.isREC)
//                stopRecord(svc);
//            FunSDK.MediaStop(svc.mPlayerHandler);
//            svc.mPlayerHandler = 0;
//            if(svc.playType == 1)
//                svc.setConnected(false);
//
//        }
//        svc.isPlaying = false;
//    }
//
//    public void restartVideo(SurfaceViewComponent svc){
//        if(svc.isConnected()){
//            onStop(svc);
//            onStartVideo(svc);
////            mPlayerHandler = FunSDK.MediaRealPlay(mUserID, deviceSn, mySurfaceViewChannelId, streamType, surfaceViewComponent.mySurfaceView, mySurfaceViewOrderId);
//        }
//    }
//
//    public void takeSnapshot(SurfaceViewComponent svc){
//        if(svc.mPlayerHandler != 0){
//            String path = Environment.getExternalStorageDirectory().getPath() + "/Pictures/Giga Monitor/" + Utils.currentDateTime();
//            int result = FunSDK.MediaSnapImage(svc.mPlayerHandler, path, 0);
//            if(result == 0){
//                Utils.savePictureFile(path);
//            }
//        }
//    }
//
//    public void startRecord(SurfaceViewComponent svc){
//        if(svc.mPlayerHandler != 0) {
//            String path = Environment.getExternalStorageDirectory().getPath() + "/Movies/Giga Monitor/" + Utils.currentDateTime() + ".mp4";
//            FunSDK.MediaStartRecord(svc.mPlayerHandler, path, 0);
//            recHandler = svc.mPlayerHandler;
//            svc.isREC = true;
//        }
//    }
//
//    public void stopRecord(SurfaceViewComponent svc){
//        if(svc.mPlayerHandler != 0) {
//            FunSDK.MediaStopRecord(recHandler, 0);
//        }
//    }
//
//    public void onPlayPlayback(H264_DVR_FILE_DATA file, SurfaceViewComponent svc){
//        svc.mPlayerHandler = FunSDK.MediaNetRecordPlay(mUserID, svc.deviceSn, G.ObjToBytes(file), svc.mySurfaceView, 0);
//    }
//
//    public void seekByTime(int absTime, SurfaceViewComponent svc) {
//        if (svc.mPlayerHandler != 0) {
//            FunSDK.MediaSeekToTime(svc.mPlayerHandler, 0, absTime, 0);
//        }
//    }
//
//    public void seekByPos(int percentage, SurfaceViewComponent svc){
//        if (svc.mPlayerHandler != 0) {
//            svc.seekPercentage = percentage;
//            FunSDK.MediaSeekToPos(svc.mPlayerHandler, percentage, 0);
//        }
//    }
//
//    public void setCurrentPlaybackListener(PlaybackListener listener){
//        this.currentPlaybackListener = listener;
//    }
//
//    private int parsePlayPosition(String str) {
//        try {
//            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
//            return (int)(sdf.parse(str).getTime()/1000);
//        } catch (Exception e) {
//            Log.d(TAG, "parsePlayPosition: " + e.toString());
//        }
//        return 0;
//    }
//
//    public void ptzControl(int command, SurfaceViewComponent svc){
//        //EPTZCMD
//        FunSDK.DevPTZControl(svc.mPlayerHandler, svc.deviceSn, svc.mySurfaceViewChannelId, command, 0, 4, svc.mySurfaceViewChannelId);
//    }
//
//    public SurfaceViewComponent findSurfaceByHandler(int handler){
//        if(handler>0){
//            for(SurfaceViewComponent svc : channels){
//                if(svc.mPlayerHandler == handler)
//                    return svc;
//            }
//        }
//        Log.d(TAG, "findSurfaceByHandler: Surface not Found");
//        return null;
//    }
//
//    @Override
//    public int OnFunSDKResult(Message msg, MsgContent msgContent) {
//        Log.d(TAG, "msg.what : " + msg.what);
//        Log.d(TAG, "msg.arg1 : " + msg.arg1);
//        Log.d(TAG, "msg.arg2 : " + msg.arg2);
//        if (null != msgContent) {
//            Log.d(TAG, "msgContent.sender : " + msgContent.sender);
//            Log.d(TAG, "msgContent.seq : " + msgContent.seq);
//            Log.d(TAG, "msgContent.str : " + msgContent.str);
//            Log.d(TAG, "msgContent.arg3 : " + msgContent.arg3);
//            Log.d(TAG, "msgContent.pData : " + msgContent.pData);
//        }
//        switch (msg.what) {
//            case EUIMSG.START_PLAY: {
//                Log.i(TAG, "EUIMSG.START_PLAY");
//                SurfaceViewComponent svc = findSurfaceByHandler(msgContent.sender);
//                    if (msg.arg1 == 0) {
//                        svc.setConnected(true);
//                        /*if (mDeviceManager.isOnStartQueue(this)) {
//                            mDeviceManager.removeFromStartQueue(this);
//                            mDeviceManager.requestStart();
//                        }*/
//                        Log.i(TAG, "START SUCCESS");
//                    } else {
//                        //mDeviceManager.requestStart();
//                        onStartVideo(svc);
//                        Log.i(TAG, "START FAILED");
//                    }
//            }
//            break;
//            case EUIMSG.STOP_PLAY: {
//                Log.i(TAG, "EUIMSG.STOP_PLAY");
//                if (msg.arg1 == 0) {
//                    Log.i(TAG, "STOP SUCCESS");
//                    findSurfaceByHandler(msgContent.sender).setConnected(false);
//                } else {
//                    Log.i(TAG, "STOP FAILED");
//                }
//            }
//            break;
//            case EUIMSG.PAUSE_PLAY: {
//                Log.i(TAG, "EUIMSG.PAUSE_PLAY");
//                SurfaceViewComponent svc = findSurfaceByHandler(msgContent.sender);
//                if (msg.arg1 == 1) {
//                    svc.isPlaying = true;
//                    Log.i(TAG, "PLAY/PAUSE: playing");
//                } else if(msg.arg1 == 2) {
//                    svc.isPlaying = false;
//                    Log.i(TAG, "PLAY/PAUSE: paused");
//                }
//            }
//            break;
//            case EUIMSG.ON_PLAY_BUFFER_BEGIN: {
//                Log.i(TAG, "EUIMSG.ON_PLAY_BUFFER_BEGIN");
//                if (msg.arg1 == 0) {
//                    Log.i(TAG, "PLAY BUFFER BEGIN");
//                }
//            }
//            break;
//            case EUIMSG.ON_PLAY_BUFFER_END: {
//                Log.i(TAG, "EUIMSG.ON_PLAY_BUFFER_END");
//                if (msg.arg1 == 0) {
//                    SurfaceViewComponent svc = findSurfaceByHandler(msgContent.sender);
//                    svc.isPlaying = true;
//                    if(svc.playType == 0) {
//                        svc.isLoading(false);
////                        menu.updateIcons();
//                    }else if(svc.playType == 1){
//                        currentPlaybackListener.onPlayState(2);
//                    }
//                    Log.i(TAG, "PLAY BUFFER END");
//                }
//            }
//            break;
//            case EUIMSG.SAVE_IMAGE_FILE: {
//                Log.i(TAG, "EUIMSG.SAVE_IMAGE_FILE");
//                if (msg.arg1 == 0) {
//                        Toast.makeText(mContext, "Imagem salva com sucesso", Toast.LENGTH_SHORT).show();
//                    Log.i(TAG, "PLAY BUFFER END");
//                } else{
//                    Toast.makeText(mContext, "Falha na captura da imagem", Toast.LENGTH_SHORT).show();
//                }
//            }
//            break;
//            case EUIMSG.START_SAVE_MEDIA_FILE: {
//                if(msg.arg1 == 0){
//                    Toast.makeText(mContext, "Gravação iniciada", Toast.LENGTH_SHORT).show();
//                } else{
//                    Toast.makeText(mContext, "Falha na gravação", Toast.LENGTH_SHORT).show();
//                }
//            }
//            break;
//            case EUIMSG.STOP_SAVE_MEDIA_FILE: {
//                if(msg.arg1 == 0){
//                    File file = new File(msgContent.str);
//                    findSurfaceByHandler(msgContent.sender).isREC = false;
//                    if(file.length() > 1000)
//                        Toast.makeText(mContext, "Gravação finalizada", Toast.LENGTH_SHORT).show();
//                    else{
//                        file.delete();
//                        Toast.makeText(mContext, "Falha na captura da imagem", Toast.LENGTH_SHORT).show();
//                    }
//                } else{
//                    Toast.makeText(mContext, "Falha na gravação", Toast.LENGTH_SHORT).show();
//                }
//            }
//            break;
//            case EUIMSG.ON_PLAY_INFO: {
////                Log.d(TAG, "OnFunSDKResult: ON_PLAY_INFO");
//                SurfaceViewComponent svc = findSurfaceByHandler(msgContent.sender);
//                if (svc.playType == 1) {
//                    int progress = msg.arg2 - msg.arg1;
////                    Log.d(TAG, "OnFunSDKResult: PROGRESS " + progress);
//                    if(!svc.isSeeking && progress > 0)
//                        currentPlaybackListener.onChangeProgress(progress);
//                    else if(svc.isSeeking){
//                        currentPlaybackListener.onChangeProgress(progress);
//                    }
//                }
//            }
//            break;
//            case EUIMSG.ON_PLAY_END: {
//                SurfaceViewComponent svc = findSurfaceByHandler(msgContent.sender);
//                if(svc.playType == 1){
//                    currentPlaybackListener.onComplete();
//                }
//            }
//            case EUIMSG.SEEK_TO_POS: {
//                SurfaceViewComponent svc = findSurfaceByHandler(msgContent.sender);
//                if(msg.arg1 == 0){
//                    svc.seekPercentage = 0;
////                    if(isSeeking && !isPlaying)
////                        onResume();
//                } else{
//                    seekByPos(svc.seekPercentage, svc);
//                }
//            }
//            break;
//        }
//        return 0;
//    }
//
//}
//
