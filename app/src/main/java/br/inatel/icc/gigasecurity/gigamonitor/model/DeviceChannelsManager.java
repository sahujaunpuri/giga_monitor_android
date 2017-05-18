package br.inatel.icc.gigasecurity.gigamonitor.model;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Environment;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.basic.G;
import com.lib.EUIMSG;
import com.lib.FunSDK;
import com.lib.IFunSDKResult;
import com.lib.MsgContent;
import com.lib.sdk.struct.H264_DVR_FILE_DATA;
import com.video.opengl.GLSurfaceView20;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import br.inatel.icc.gigasecurity.gigamonitor.adapters.ChannelRecyclerViewAdapter;
import br.inatel.icc.gigasecurity.gigamonitor.core.DeviceManager;
import br.inatel.icc.gigasecurity.gigamonitor.listeners.PlaybackListener;
import br.inatel.icc.gigasecurity.gigamonitor.task.AudioRecordThread;
import br.inatel.icc.gigasecurity.gigamonitor.ui.SurfaceViewComponent;
import br.inatel.icc.gigasecurity.gigamonitor.util.Utils;

/**
 * Created by filipecampos on 02/05/2016.
 */
public class DeviceChannelsManager extends ChannelsManager implements IFunSDKResult {
    String TAG = "DeviceChannelsManager";

    public ArrayList<SurfaceViewComponent> surfaceViewComponents;
    public ArrayList<GLSurfaceView20> mySurfaceViews;
    public ArrayList<Integer> handlers;
    public Device mDevice;
    public DeviceManager mDeviceManager;
    public ChannelRecyclerViewAdapter mRecyclerAdapter;
    public Context mContext;
    public int numQuad, lastNumQuad;
    public int lastFirstVisibleItem;
    public int lastLastVisibleItem;
    public int lastFirstItemBeforeSelectChannel;

    public FrameLayout.LayoutParams pbParam;
    public FrameLayout.LayoutParams surfaceViewLayout;

    private int mUserID = -1;
    public PlaybackListener currentPlaybackListener;
    public AudioRecordThread mRecordThread;
    public int recCounter = 0;
    private int talkHandler;
    public int playType = 0; //0 - live, 1 - playback live
    private H264_DVR_FILE_DATA fileToStart;

    private int[][] inverseMatrix = new int[][]{
            {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15},
            {0, 2, 1, 3, 4, 6, 5, 7, 8, 10, 9, 11, 12, 14, 13, 15},
            {0, 3, 6, 1, 4, 7, 2, 5, 8, 9, 12, 14, 10, 13, 15, 11},
            {0, 4, 8, 12, 1, 5, 9, 13, 2, 6, 10, 14, 3, 7, 11, 15}
    };

    public DeviceChannelsManager(Device mDevice) {
        if(mUserID == -1){
            mUserID = FunSDK.RegUser(this);
        }
        this.mDevice = mDevice;
        this.surfaceViewComponents = new ArrayList<>();
        this.mySurfaceViews = new ArrayList<GLSurfaceView20>();
        this.handlers = new ArrayList<>();
        this.numQuad = 1;
        this.lastNumQuad = 1;
        this.mDeviceManager = DeviceManager.getInstance();
        this.mContext = mDeviceManager.currentContext;

        this.lastFirstVisibleItem = 0;
        this.lastLastVisibleItem = 0;


        surfaceViewLayout = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        pbParam = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
    }

    public void createComponents() {
        if(surfaceViewComponents.isEmpty()) {
            for (int i = 0; i < mDevice.getChannelNumber(); i++) {

                this.createComponent(i);

            }
            this.reOrderSurfaceViewComponents();
            changeSurfaceViewSize();
        }
    }


    public void createComponent(int position) {

        int i = position;
        GLSurfaceView20 mySurfaceView;
        mySurfaceView = new GLSurfaceView20(mContext);
        mySurfaceView.setLayoutParams(surfaceViewLayout);
        mySurfaceViews.add(i, mySurfaceView);


        SurfaceViewComponent surfaceViewComponent = new SurfaceViewComponent(mContext, this, i);

        surfaceViewComponent.mySurfaceViewChannelId = i;
        surfaceViewComponent.mySurfaceViewOrderId = i;
        surfaceViewComponent.deviceConnection = mDevice.connectionString;
        surfaceViewComponent.deviceId = mDevice.getId();

        if(mDeviceManager.isFavorite(mDevice.getId(), i))
            surfaceViewComponent.setFavorite(true);

//            surfaceViewComponent.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        surfaceViewComponent.setLayoutParams(surfaceViewLayout);

        surfaceViewComponents.add(i,surfaceViewComponent);
    }


    public void addSurfaceViewComponent(SurfaceViewComponent svc){
        surfaceViewComponents.add(svc);
    }

    public void changeSurfaceViewSize() {
        mDeviceManager.getScreenSize();
        int surfaceViewWidth = (int) Math.ceil((mDeviceManager.screenWidth / numQuad));// + numQuad;
        int surfaceViewHeight = ((mDeviceManager.screenHeight / 3) + 10) / numQuad;

        if (mContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            surfaceViewHeight = (mDeviceManager.screenHeight) / numQuad;
        }
//        surfaceViewComponent.setViewSize(surfaceViewWidth, surfaceViewHeight);
        surfaceViewLayout.width = surfaceViewWidth;
        surfaceViewLayout.height = surfaceViewHeight;

        pbParam.width = surfaceViewWidth/4;
        pbParam.height = surfaceViewHeight/4;

        /*for(SurfaceViewComponent svc : surfaceViewComponents){
            svc.requestLayout();
        }*/
    }

    public GLSurfaceView20 getMySurfaceView(int channelId){
        return mySurfaceViews.get(channelId);
    }


    /** Grid Functions **/
    public void reOrderSurfaceViewComponents() {
        for (SurfaceViewComponent svc : surfaceViewComponents) {
            svc.mySurfaceViewOrderId = inverseMatrix[numQuad - 1][svc.mySurfaceViewChannelId];
        }

        Collections.sort(surfaceViewComponents, new Comparator<SurfaceViewComponent>() {
            public int compare(SurfaceViewComponent svc1, SurfaceViewComponent svc2) {
                return svc1.mySurfaceViewOrderId - svc2.mySurfaceViewOrderId;
            }
        });
    }

    public int getChannelSelected(int gridPositionSelected) {
        return inverseMatrix[numQuad - 1][gridPositionSelected];
    }

    public int scrollToItem(int currentFirstVisibleItem, int currentLastVisibleItem) {
        int itemToScroll = 0;
        int totalQuads = numQuad*numQuad;
        if (totalQuads > 1) {
            if (currentLastVisibleItem % totalQuads == totalQuads - 1) {
                itemToScroll = currentLastVisibleItem;
            } else if (currentFirstVisibleItem % totalQuads == 0) {
                itemToScroll = currentFirstVisibleItem;
            } else if (currentLastVisibleItem == mDevice.getChannelNumber() - 1) {
                itemToScroll = currentLastVisibleItem;
            } else {
                itemToScroll = currentFirstVisibleItem;
            }
        } else if (totalQuads == 1) {
            if (lastFirstVisibleItem != currentFirstVisibleItem) {
                itemToScroll = currentFirstVisibleItem;
            } else if (lastLastVisibleItem != currentLastVisibleItem) {
                itemToScroll = currentLastVisibleItem;
            }
        }
        this.lastFirstVisibleItem = currentFirstVisibleItem;
        this.lastLastVisibleItem = currentLastVisibleItem;

//        this.handleVisibleChannels();
        return itemToScroll;
    }

    public void resetScale(){
        ((Activity) mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
//                reOrderSurfaceViewComponents();
                for(SurfaceViewComponent svc : surfaceViewComponents){
                    if(svc.mScaleFactor > 1.F)
                        svc.mySurfaceView.resetScaleInfo();
                }
            }
        });
    }


    /** Media Control Functions **/
    public void onPlayLive(SurfaceViewComponent svc) {
        mDeviceManager.addToStart(svc);
    }

    public void onStartVideo(final SurfaceViewComponent svc){
        new Thread(new Runnable() {
            @Override
            public void run() {
                svc.mPlayerHandler = FunSDK.MediaRealPlay(mUserID, mDevice.connectionString, svc.mySurfaceViewChannelId, svc.streamType, mySurfaceViews.get(svc.mySurfaceViewChannelId), svc.mySurfaceViewOrderId);
            }
        }).start();

    }

    // NOT TESTED
    public void onRefreshVideo(final SurfaceViewComponent svc){
//        svc.mPlayerHandler = FunSDK.MediaPlay(svc.mPlayerHandler, mUserID);
        svc.mPlayerHandler = FunSDK.MediaRefresh(svc.mPlayerHandler, mUserID);
    }

    public void setPlayView(final SurfaceViewComponent svc){
        FunSDK.MediaSetPlayView(svc.mPlayerHandler, mySurfaceViews.get(svc.mySurfaceViewChannelId), mUserID);
    }

    public void onPause(SurfaceViewComponent svc){
        if (svc.isPlaying) {
            FunSDK.MediaPause(svc.mPlayerHandler, 1, svc.mySurfaceViewOrderId);
        }
    }

    public void onResume(SurfaceViewComponent svc){
        FunSDK.MediaPause(svc.mPlayerHandler, 0, svc.mySurfaceViewOrderId);
    }

    public void onStop(SurfaceViewComponent svc){
        if ( svc.isConnected() ) {
            if(svc.isREC) {
                svc.stoppingRec = true;
                stopRecord(svc);
            } else
                FunSDK.MediaStop(svc.mPlayerHandler);
            svc.setConnected(false);

        }
        svc.isPlaying = false;
    }

    public void restartVideo(SurfaceViewComponent svc){
        if(svc.isConnected()){
            onStop(svc);
            onStartVideo(svc);
        }
    }

    public void toggleReceiveAudio(SurfaceViewComponent svc){
        int volume;
        if(svc.isReceiveAudioEnabled)
            volume = 100;
        else
            volume = -1;
        FunSDK.MediaSetSound(svc.mPlayerHandler, volume, svc.mySurfaceViewOrderId);
    }

    public void enableSendAudio(SurfaceViewComponent svc){
        talkHandler = FunSDK.DevStarTalk(mUserID, svc.deviceConnection, mUserID);
        mRecordThread = new AudioRecordThread(mDeviceManager.findDeviceById(svc.deviceId));
    }

    public void disableSendAudio(){
        mRecordThread.Stop();
        mRecordThread.Pause(true);
        mRecordThread = null;
//        FunSDK.MediaSetSound(talkHandler, 50, 0);
        FunSDK.DevStopTalk(talkHandler);
    }

    public void takeSnapshot(SurfaceViewComponent svc){
        if(svc.isConnected()){
            String path = Environment.getExternalStorageDirectory().getPath() + "/Pictures/Giga Monitor/" + Utils.currentDateTime() + ".jpg";
            int result = FunSDK.MediaSnapImage(svc.mPlayerHandler, path, 0);
            if(result == 0){
                Utils.savePictureFile(path);
            }
        }
    }

    public void startRecord(SurfaceViewComponent svc){
        if(svc.isConnected()) {
            recCounter++;
            svc.recordFileName = Environment.getExternalStorageDirectory().getPath() + "/Movies/Giga Monitor/" + Utils.currentDateTime() + ".mp4";
            FunSDK.MediaStartRecord(svc.mPlayerHandler, svc.recordFileName, 0);
//            recHandler = svc.mPlayerHandler;
            svc.isREC = true;
        }
    }

    public void stopRecord(SurfaceViewComponent svc){
        if(svc.isConnected()) {
            FunSDK.MediaStopRecord(svc.mPlayerHandler, 0);
        }
    }

    public void onPlayPlayback(H264_DVR_FILE_DATA file, SurfaceViewComponent svc){
        Log.d(TAG, "onPlayPlayback FILE: " + file.toString());
        fileToStart = file;
        svc.mPlayerHandler = FunSDK.MediaNetRecordPlay(mUserID, svc.deviceConnection, G.ObjToBytes(file), svc.mySurfaceView, 555);

    }

    public void onPlayPlaybackByTime(H264_DVR_FILE_DATA file, SurfaceViewComponent svc){
        svc.mPlayerHandler = FunSDK.MediaNetRecordPlayByTime(mUserID, svc.deviceConnection, G.ObjToBytes(file), svc.mySurfaceView, 555);

    }

    public void seekByTime(int absTime, SurfaceViewComponent svc) {
        if (svc.isConnected()) {
            FunSDK.MediaSeekToTime(svc.mPlayerHandler, 0, absTime, 555);
        }
    }

    public void seekByPos(int percentage, SurfaceViewComponent svc){
        if (svc.isConnected()) {
            svc.seekPercentage = percentage;
            FunSDK.MediaSeekToPos(svc.mPlayerHandler, percentage, 555);
        }
    }

    public void setCurrentPlaybackListener(PlaybackListener listener){
        this.currentPlaybackListener = listener;
    }

    private int parsePlayPosition(String str) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
            return (int)(sdf.parse(str).getTime()/1000);
        } catch (Exception e) {
            Log.d(TAG, "parsePlayPosition: " + e.toString());
        }
        return 0;
    }

    public void ptzControl(int command, SurfaceViewComponent svc){
        //EPTZCMD
        FunSDK.DevPTZControl(svc.mPlayerHandler, svc.deviceConnection, svc.mySurfaceViewChannelId, command, 0, 4, svc.mySurfaceViewChannelId);
    }

    public void handleVisibleChannels() {
        if (this.lastLastVisibleItem - this.lastFirstVisibleItem == this.numQuad * this.numQuad - 1 || this.lastLastVisibleItem == this.surfaceViewComponents.size() - 1) {
            for (final SurfaceViewComponent svc : this.surfaceViewComponents) {
                if (svc.mySurfaceViewChannelId >= this.lastFirstVisibleItem && svc.mySurfaceViewChannelId <= lastLastVisibleItem) {
                    if (!svc.isPlaying /*&& svc.isConnected*/) {
                        svc.isLoading(true);
                        onStartVideo(svc);
                    }
                } else {
                    if (svc.isPlaying && svc.isConnected()) {
                        onStop(svc);
                    }
                }
            }
        }
    }

    public void stopChannels(final int start){
        new Thread(new Runnable() {
            @Override
            public void run() {
                for(int i = start; i < surfaceViewComponents.size(); i++){
                    if(surfaceViewComponents.get(i).isConnected())
                        onStop(surfaceViewComponents.get(i));
                }
            }
        }).start();

    }


    /***************/
    public SurfaceViewComponent findSurfaceByHandler(int handler){
        SurfaceViewComponent found = null;
//        Log.d(TAG, "findSurfaceByHandler: --------------------------------------- searching for: " + handler);
        if(handler>0){
            for(SurfaceViewComponent svc : surfaceViewComponents){
//                Log.d(TAG, "findSurfaceByHandler: " + svc.mPlayerHandler + " searching for: " + handler);
                if(svc.mPlayerHandler == handler) {
                    found = svc;
                    break;
                }
            }
        }
        if(found == null)
            Log.d(TAG, "findSurfaceByHandler: Surface not Found");
        return found;
    }


    /** Async return from SDK**/
    @Override
    public int OnFunSDKResult(Message msg, MsgContent msgContent) {
        if(!(msg.what == EUIMSG.ON_PLAY_INFO && playType == 0)) {
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
        }
        switch (msg.what) {
            case EUIMSG.START_PLAY: {
                Log.i(TAG, "EUIMSG.START_PLAY");
                SurfaceViewComponent svc = findSurfaceByHandler(msgContent.sender);
                if(svc!=null) {
                    if (msg.arg1 == 0) {
                        svc.setConnected(true);
                        if (svc.playType == 0) {
                            mDeviceManager.requestStart();
                        }
                        Log.i(TAG, "START SUCCESS " + (svc.getMySurfaceViewChannelId()+1));
                    } else {
                        if(svc.playType == 0) {
                            svc.setConnected(false);
                            mDeviceManager.addToStart(svc);
                        } else{
                            onPlayPlayback(fileToStart, svc);
                        }

                        Log.i(TAG, "START FAILED");
                    }
                }
            }
            break;
            case EUIMSG.STOP_PLAY: {
                Log.i(TAG, "EUIMSG.STOP_PLAY");
                if (msg.arg1 == 0) {
                    Log.i(TAG, "STOP SUCCESS");
                    SurfaceViewComponent svc = findSurfaceByHandler(msgContent.sender);
                    svc.setConnected(false);
                } else {
                    Log.i(TAG, "STOP FAILED");
                }
            }
            break;
            case EUIMSG.PAUSE_PLAY: {
                Log.i(TAG, "EUIMSG.PAUSE_PLAY");
                SurfaceViewComponent svc = findSurfaceByHandler(msgContent.sender);
                if (msg.arg1 == 1) {
                    svc.isPlaying = true;
                    Log.i(TAG, "PLAY/PAUSE: playing");
                } else if(msg.arg1 == 2) {
                    svc.isPlaying = false;
                    Log.i(TAG, "PLAY/PAUSE: paused");
                }
            }
            break;
            case EUIMSG.ON_PLAY_BUFFER_BEGIN: {
                Log.i(TAG, "EUIMSG.ON_PLAY_BUFFER_BEGIN");
                if (msg.arg1 == 0) {
                    Log.i(TAG, "PLAY BUFFER BEGIN");
                }
            }
            break;
            case EUIMSG.ON_PLAY_BUFFER_END: {
                Log.i(TAG, "EUIMSG.ON_PLAY_BUFFER_END");
                if (msg.arg1 == 0) {
                    SurfaceViewComponent svc = findSurfaceByHandler(msgContent.sender);
                    if(svc!=null) {
                        svc.isPlaying = true;
                        if (svc.playType == 0) {
                            svc.isLoading(false);
                            /*if(!svc.isVisible){
                                onStop(svc);
                            }*/
//                        menu.updateIcons();
                        } else if (svc.playType == 1) {
                            svc.isSeeking = false;
                            currentPlaybackListener.onPlayState(2);
                        }
                    }
                    Log.i(TAG, "PLAY BUFFER END");
                }
            }
            break;
            case EUIMSG.SAVE_IMAGE_FILE: {
                Log.i(TAG, "EUIMSG.SAVE_IMAGE_FILE");
                if (msg.arg1 == 0) {
                    Toast.makeText(mContext, "Imagem salva com sucesso", Toast.LENGTH_SHORT).show();
                    Log.i(TAG, "PLAY BUFFER END");
                } else{
                    Toast.makeText(mContext, "Falha na captura da imagem", Toast.LENGTH_SHORT).show();
                }
            }
            break;
            case EUIMSG.START_SAVE_MEDIA_FILE: {
                if(msg.arg1 == 0){
                    Toast.makeText(mContext, "Gravação iniciada", Toast.LENGTH_SHORT).show();
                } else{
                    Toast.makeText(mContext, "Falha na gravação", Toast.LENGTH_SHORT).show();
                }
            }
            break;
            case EUIMSG.STOP_SAVE_MEDIA_FILE: {
                if(msg.arg1 == 0){
                    SurfaceViewComponent svc = findSurfaceByHandler(msgContent.sender);
                    File file = new File(svc.recordFileName);
                    svc.isREC = false;
                    if(file.length() > 1000) {
                        String mensagem = "Gravação do canal " + (svc.mySurfaceViewChannelId + 1) + " finalizada";
                        Toast.makeText(mContext, mensagem, Toast.LENGTH_SHORT).show();
                        if(svc.stoppingRec) {
                            onStop(svc);
                            svc.stoppingRec = false;
                        }
                    }else{
                        file.delete();
                        Toast.makeText(mContext, "Falha na captura da imagem", Toast.LENGTH_SHORT).show();
                    }
                } else{
                    Toast.makeText(mContext, "Falha na gravação", Toast.LENGTH_SHORT).show();
                }
                recCounter--;
            }
            break;
            case EUIMSG.ON_PLAY_INFO: {
//                Log.d(TAG, "OnFunSDKResult: ON_PLAY_INFO");
                if (playType == 1) {
                    SurfaceViewComponent svc = findSurfaceByHandler(msgContent.sender);
                    if(svc != null) {
                        String[] info = msgContent.str.split(";");
                        if(info.length > 1){
                            Date date = Utils.parseStringToDate(info[0]);
                            if(date!=null) {
                                Calendar calendar = GregorianCalendar.getInstance();
                                calendar.setTime(date);
                                int sec = calendar.get(Calendar.SECOND);
                                int min = calendar.get(Calendar.MINUTE);
                                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                                int currentSec = calendar.get(Calendar.SECOND) + calendar.get(Calendar.MINUTE)*60 + calendar.get(Calendar.HOUR_OF_DAY)*3600;
                                Log.d(TAG, "OnFunSDKResult TIME: " + hour + "h" + min + "min " + sec + "s. " + currentSec);
                                currentPlaybackListener.onChangeProgress(currentSec);
                            }
//                            currentPlaybackListener.onChangeProgress(Utils.parseStringToBits(info[1]));
                        }
                        /*int progress = msg.arg2 - msg.arg1;
                        Log.d(TAG, "OnFunSDKResult: PROGRESS " + progress);
                        if (!svc.isSeeking && progress > 0)
                            currentPlaybackListener.onChangeProgress(progress);
                        else if (svc.isSeeking) {
                            currentPlaybackListener.onChangeProgress(progress);
                        }*/
                    }
                }
            }
            break;
            case EUIMSG.ON_PLAY_END: {
                SurfaceViewComponent svc = findSurfaceByHandler(msgContent.sender);
                if(svc != null && svc.playType == 1){
                    currentPlaybackListener.onComplete();
                }
            }
            case EUIMSG.SEEK_TO_POS: {
                SurfaceViewComponent svc = findSurfaceByHandler(msgContent.sender);
                if(msg.arg1 == 0 && svc != null){
                    svc.seekPercentage = 0;
//                    if(isSeeking && !isPlaying)
//                        onResume();
                } /*else if(svc != null){
                    seekByPos(svc.seekPercentage, svc);
                }*/
            }
            break;
            case EUIMSG.DEV_START_TALK: {
                if(msg.arg1 == 0){
                    mRecordThread.Start();
                    mRecordThread.Pause(false);
                }
            }
            break;
            case EUIMSG.MEDIA_FRAME_LOSS: {
                Log.d(TAG, "OnFunSDKResult: Media Frame Loss");
            }
        }
        return 0;
    }
}
