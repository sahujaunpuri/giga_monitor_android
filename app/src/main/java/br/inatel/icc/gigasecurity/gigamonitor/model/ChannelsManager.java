package br.inatel.icc.gigasecurity.gigamonitor.model;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.CamcorderProfile;
import android.media.MediaCodec;
import android.media.MediaFormat;
import android.media.MediaMetadataRetriever;
import android.media.MediaMuxer;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.Surface;
import android.widget.FrameLayout;
import android.widget.Toast;
import android.widget.VideoView;

import com.basic.G;
import com.google.android.gms.cast.MediaInfo;
import com.google.android.gms.common.images.Size;
import com.lib.EFUN_ERROR;
import com.lib.EPTZCMD;
import com.lib.EUIMSG;
import com.lib.FunSDK;
import com.lib.IFunSDKResult;
import com.lib.MsgContent;
import com.lib.SDKCONST;
import com.lib.sdk.struct.H264_DVR_FILE_DATA;
import com.video.opengl.GLSurfaceView20;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import br.inatel.icc.gigasecurity.gigamonitor.R;
import br.inatel.icc.gigasecurity.gigamonitor.activities.DeviceListActivity;
import br.inatel.icc.gigasecurity.gigamonitor.activities.MediaVideoActivity;
import br.inatel.icc.gigasecurity.gigamonitor.adapters.ChannelRecyclerViewAdapter;
import br.inatel.icc.gigasecurity.gigamonitor.core.DeviceManager;
import br.inatel.icc.gigasecurity.gigamonitor.listeners.PlaybackListener;
import br.inatel.icc.gigasecurity.gigamonitor.task.AudioRecordThread;
import br.inatel.icc.gigasecurity.gigamonitor.ui.SurfaceViewComponent;
//import br.inatel.icc.gigasecurity.gigamonitor.util.FunLog;
//import br.inatel.icc.gigasecurity.gigamonitor.util.MediaConverter;
//import br.inatel.icc.gigasecurity.gigamonitor.util.MediaConverter;
import br.inatel.icc.gigasecurity.gigamonitor.util.MediaService;
import br.inatel.icc.gigasecurity.gigamonitor.util.Utils;


public abstract class ChannelsManager implements IFunSDKResult {
    String TAG = "DeviceChannelsManager";

    public ArrayList<SurfaceViewComponent> surfaceViewComponents;
    public ArrayList<GLSurfaceView20> mySurfaceViews;
    public DeviceManager mDeviceManager;
    public Context mContext;
    public int numQuad, lastNumQuad;
    public int lastFirstVisibleItem;
    public int lastLastVisibleItem;
    public int lastFirstItemBeforeSelectChannel;
    public int currentPage;
    public int channelNumber;

    public FrameLayout.LayoutParams pbParam;
    public FrameLayout.LayoutParams surfaceViewLayout;
    public ChannelRecyclerViewAdapter mRecyclerAdapter;

    public Device mDevice;
    public int mUserID = -1;
    public PlaybackListener currentPlaybackListener;
    public AudioRecordThread mRecordThread;
    public int recCounter = 0;
    private int talkHandler;
    public int playType = 0; //0 - live, 1 - playback live
    private H264_DVR_FILE_DATA fileToStart;
    public int[][] inverseMatrix;
    private int startTry;
    public int hdChannel = -1;

    public ChannelsManager(Device mDevice) {
        if (mUserID == -1) {
            mUserID = FunSDK.RegUser(this);
        }
        this.mDevice = mDevice;
        this.surfaceViewComponents = new ArrayList<>();
        this.mySurfaceViews = new ArrayList<GLSurfaceView20>();
        this.numQuad = 1;
        this.lastNumQuad = 1;
        this.mDeviceManager = DeviceManager.getInstance();
        this.mContext = mDeviceManager.currentContext;
        this.lastFirstVisibleItem = 0;
        this.lastLastVisibleItem = 0;
        this.currentPage = 0;
        surfaceViewLayout = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        pbParam = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
        channelNumber = mDevice.getChannelNumber();
        if (channelNumber > 1) {
            numQuad = 2;
            lastNumQuad = 2;
        }

        initMatrix();
    }

    public void initMatrix() {
        if (mDevice.getChannelNumber() <= 16) {
            inverseMatrix = new int[][]{
                    {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15},
                    {0, 2, 1, 3, 4, 6, 5, 7, 8, 10, 9, 11, 12, 14, 13, 15},
                    {0, 3, 6, 1, 4, 7, 2, 5, 8, 9, 12, 15, 10, 13, 11, 14},
                    {0, 4, 8, 12, 1, 5, 9, 13, 2, 6, 10, 14, 3, 7, 11, 15}
            };
        } else
            inverseMatrix = new int[][]{
                    {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31},
                    {0, 2, 1, 3, 4, 6, 5, 7, 8, 10, 9, 11, 12, 14, 13, 15, 16, 18, 17, 19, 20, 22, 21, 23, 24, 26, 25, 27, 28, 30, 29, 31},
                    {0, 3, 6, 1, 4, 7, 2, 5, 8, 9, 12, 15, 10, 13, 16, 11, 14, 17, 18, 21, 24, 19, 22, 25, 20, 23, 26, 27, 30, 28, 31, 29},
                    {0, 4, 8, 12, 1, 5, 9, 13, 2, 6, 10, 14, 3, 7, 11, 15, 16, 20, 24, 28, 17, 21, 25, 29, 18, 22, 26, 30, 19, 23, 27, 31}
            };
    }

    public abstract void createComponents();

    public void clearSurfaceViewComponents() {
        for(SurfaceViewComponent svc : surfaceViewComponents)
            svc = null;
        surfaceViewComponents.clear();
    }

    public void addSurfaceViewComponent(SurfaceViewComponent svc) {
        surfaceViewComponents.add(svc);
    }

    public void removeSurfaceViewComponent(SurfaceViewComponent svc) {
        if (svc.isConnected())
            onStop(svc);
        surfaceViewComponents.remove(svc);
    }

    public FrameLayout.LayoutParams changeSurfaceViewSize() {
        mDeviceManager.getScreenSize();
        int surfaceViewWidth = (int) Math.ceil((mDeviceManager.screenWidth / numQuad));// + numQuad;
        int surfaceViewHeight = ((mDeviceManager.screenHeight / 3) + 10) / numQuad;

        if (mContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            surfaceViewHeight = (mDeviceManager.screenHeight) / numQuad;
        }

        surfaceViewLayout.width = surfaceViewWidth;
        surfaceViewLayout.height = surfaceViewHeight;

        pbParam.width = surfaceViewWidth / 4;
        pbParam.height = surfaceViewHeight / 4;

        return surfaceViewLayout;
    }

    public GLSurfaceView20 getMySurfaceView(int channelId) {
        return mySurfaceViews.get(channelId);
    }


    /**
     * Grid Functions
     **/
    public abstract void reOrderSurfaceViewComponents();

    public abstract int getChannelSelected(int gridPositionSelected);

    public int scrollToItem(int currentFirstVisibleItem, int currentLastVisibleItem) {
        int itemToScroll = 0;
        int totalQuads = numQuad * numQuad;
        if (totalQuads > 1) {
            if (currentLastVisibleItem % totalQuads == totalQuads - 1) {
                itemToScroll = currentLastVisibleItem;
            } else if (currentFirstVisibleItem % totalQuads == 0) {
                itemToScroll = currentFirstVisibleItem;
            } else if (currentLastVisibleItem == channelNumber - 1) {
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

        return itemToScroll;
    }

    public int pageNumber(int channelNumber){
        return channelNumber % (numQuad*numQuad);
    }

    public int firstItemOfPage(int pageNumber){
        return pageNumber * (numQuad*numQuad);
    }

    public void resetScale() {
        ((Activity) mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (SurfaceViewComponent svc : surfaceViewComponents) {
                    if (svc.mScaleFactor > 1.F)
                        svc.mySurfaceView.resetScaleInfo();
                }
            }
        });
    }


    /**
     * Media Control Functions
     **/
    public void onPlayLive(SurfaceViewComponent svc) {
        mDeviceManager.addToStart(svc);
    }

    public void onStartVideo(final SurfaceViewComponent svc) {
        svc.mPlayerHandler = FunSDK.MediaRealPlay(mUserID, mDevice.connectionString, svc.mySurfaceViewChannelId, svc.streamType, svc.mySurfaceView, svc.mySurfaceViewOrderId);
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
        if (svc.isConnected()) {
            if (svc.isReceiveAudioEnabled) {
                stopAudio(svc);
            }
            if(svc.isREC) {
                svc.stoppingRec = true;
                stopRecord(svc);
            } else {
                FunSDK.MediaStop(svc.mPlayerHandler);
            }
            svc.setConnected(false);
        }
        svc.isPlaying = false;
    }

//    public void stopPlayback(SurfaceViewComponent svc) {
//        if (svc.isConnected()) {
//            FunSDK.MediaStop(svc.mPlayerHandler);
//            svc.setConnected(false);
//        }
//        svc.isPlaying = false;
//    }

    public void restartVideo(SurfaceViewComponent svc){
        svc.isLoading(true);
//        if(svc.isConnected()){
            onStop(svc);
            onStartVideo(svc);
//        }
    }

    public abstract void enableHD(SurfaceViewComponent svc);

    public abstract void disableHD(SurfaceViewComponent svc);

    public void toggleReceiveAudio(SurfaceViewComponent svc) {
        if (svc.isReceiveAudioEnabled) {
            stopAudio(svc);
        } else {
            enableAudio(svc);
        }
    }

    private void enableAudio(SurfaceViewComponent svc) {
        int volume = 100;
        FunSDK.MediaSetSound(svc.mPlayerHandler, volume, svc.mySurfaceViewOrderId);
        svc.isReceiveAudioEnabled = true;
    }

    private void stopAudio(SurfaceViewComponent svc) {
        int volume = -1;
        FunSDK.MediaSetSound(svc.mPlayerHandler, volume, svc.mySurfaceViewOrderId);
        svc.isReceiveAudioEnabled = false;
    }

    public void enableSendAudio(SurfaceViewComponent svc){
        talkHandler = FunSDK.DevStarTalk(mUserID, svc.deviceConnection, mUserID);
        mRecordThread = new AudioRecordThread(mDeviceManager.findDeviceById(svc.deviceId));
    }

    public void disableSendAudio() {
        if (mRecordThread != null) {
            mRecordThread.Stop();
            mRecordThread.Pause(true);
            mRecordThread = null;
        }
//        FunSDK.MediaSetSound(talkHandler, 50, 0);
        FunSDK.DevStopTalk(talkHandler);
    }

    public void takeSnapshot(SurfaceViewComponent svc, String archiveName) {
        if(svc.isConnected()){
            String path = Environment.getExternalStorageDirectory().getPath() + "/Pictures/Giga Monitor/" + archiveName + ".jpg";
            int result = FunSDK.MediaSnapImage(svc.mPlayerHandler, path, 0);
            if(result == 0){
                Utils.savePictureFile(path);
            }
        }
    }

    public void startRecord(SurfaceViewComponent svc, final String archiveName) {
        if(svc.isConnected()) {
            recCounter++;
            svc.recordFileName = Environment.getExternalStorageDirectory().getPath() + "/Movies/Giga Monitor/" + archiveName + ".mp4";
            FunSDK.MediaStartRecord(svc.mPlayerHandler, svc.recordFileName, 0);
            svc.isREC = true;
        }
    }

    public void stopRecord(final SurfaceViewComponent svc) {
        if(svc.isConnected()) {
            FunSDK.MediaStopRecord(svc.mPlayerHandler, 0);
            recCounter--;
        }
    }

    public void onPlayPlayback(final H264_DVR_FILE_DATA file, SurfaceViewComponent svc) {
        fileToStart = file;
        svc.mPlayerHandler = FunSDK.MediaNetRecordPlay(mUserID, svc.deviceConnection, G.ObjToBytes(fileToStart), svc.mySurfaceView, 0);
    }

    public void seekByTime(int absTime, SurfaceViewComponent svc) {
        if (svc.isConnected()) {
            FunSDK.MediaSeekToTime(svc.mPlayerHandler, 0, absTime, 0);
        }
    }

    public void seekByPos(int percentage, SurfaceViewComponent svc){
        if (svc.isConnected()) {
            svc.seekPercentage = percentage;
            FunSDK.MediaSeekToPos(svc.mPlayerHandler, percentage, 0);
        }
    }

    public void setCurrentPlaybackListener(PlaybackListener listener) {
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

    public void ptzControl(int command, SurfaceViewComponent svc, boolean stop){
        //EPTZCMD
        int toStop = -1;
        if (stop) {
            toStop = 1;
        } else {
            toStop = 0;
        }
        FunSDK.DevPTZControl(mUserID, svc.deviceConnection, svc.mySurfaceViewChannelId, command, toStop, 3, svc.mySurfaceViewChannelId);
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
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
                for(int i = start; i < surfaceViewComponents.size(); i++){
                    if(surfaceViewComponents.get(i).isConnected())
                        onStop(surfaceViewComponents.get(i));
                }
//            }
//        }).start();

    }

    public boolean verifyIfSomeChannelIsRecording() {
        boolean hasRecording = false;
        for (int i=0; i<surfaceViewComponents.size(); i++) {
            if (surfaceViewComponents.get(i).isREC()) {
                hasRecording = true;
                break;
            }
        }
        return hasRecording;
    }

    /***************/
    public SurfaceViewComponent findSurfaceByHandler(int handler){
        SurfaceViewComponent found = null;
        if(handler>0){
            for(SurfaceViewComponent svc : surfaceViewComponents){
                if(svc.mPlayerHandler == handler) {
                    found = svc;
                    break;
                }
            }
        }
        if(found == null) {
            Log.d(TAG, "findSurfaceByHandler: Surface not Found");
        }
        return found;
    }

    public void stopActions() {
        for (int j=0; j<surfaceViewComponents.size(); j++) {
            SurfaceViewComponent sfvc = surfaceViewComponents.get(j);
            if (sfvc.isREC()) {
                stopRecord(sfvc);
            }
            if (sfvc.isReceiveAudioEnabled) {
                toggleReceiveAudio(sfvc);
            }
            if (sfvc.isSendAudioEnabled) {
                disableSendAudio();
            }
        }
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
                        } else if (svc.isSeeking) {
                            currentPlaybackListener.onCompleteSeek();
                        }
                        Log.i(TAG, "START SUCCESS " + (svc.getMySurfaceViewChannelId()+1));
                    } else {
                        if(svc.playType == 0) {
                            svc.setConnected(false);
//                            if(startTry++ < 4)
                                onStartVideo(svc);
                            /*else {
                                startTry = 0;
                                mDeviceManager.logoutDevice(mDevice);
                            }*/
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
                    try {
                        SurfaceViewComponent svc = findSurfaceByHandler(msgContent.sender);
                        File file = new File(svc.recordFileName);
//                        File file = new File(msgContent.str);
                        svc.isREC = false;

                        if (file.length() > 1000) {
                            String message = null;
                            if (svc.playType == 0) {
                                message = "Gravação do canal " + (svc.mySurfaceViewChannelId + 1) + " finalizada";
                            } else {
                                message = mContext.getResources().getString(R.string.playback_record_message);
                            }
                            Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
                            if (svc.stoppingRec) {
                                onStop(svc);
                                svc.stoppingRec = false;
                            }
                            mDeviceManager.saveImage(file);
                        } else {
                            file.delete();
                            Toast.makeText(mContext, mContext.getResources().getString(R.string.snapshot_failed), Toast.LENGTH_SHORT).show();
                        }
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                } else{
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.record_failed), Toast.LENGTH_SHORT).show();
                }
//                recCounter--;
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
                        }
                    }
                }
            }
            break;
            case EUIMSG.ON_PLAY_END: {
                SurfaceViewComponent svc = findSurfaceByHandler(msgContent.sender);
                if(svc != null && svc.playType == 1 && !svc.isSeeking) {
                    currentPlaybackListener.onComplete();
                }
            }
            case EUIMSG.SEEK_TO_POS: {
                SurfaceViewComponent svc = findSurfaceByHandler(msgContent.sender);
                if(msg.arg1 == 0 && svc != null) {
                    svc.seekPercentage = 0;
                }
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
                /*if(playType == 0 && msgContent != null){
                    SurfaceViewComponent svc = findSurfaceByHandler(msgContent.sender);
                    if(svc != null)
                        restartVideo(svc);
                }*/
                Log.d(TAG, "OnFunSDKResult: Media Frame Loss");
            }
        }
        return 0;
    }
}
