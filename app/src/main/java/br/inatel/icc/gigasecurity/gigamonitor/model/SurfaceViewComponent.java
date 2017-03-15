package br.inatel.icc.gigasecurity.gigamonitor.model;

import android.app.Activity;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.Environment;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
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
import java.util.Locale;

import br.inatel.icc.gigasecurity.gigamonitor.listeners.PlaybackListener;
import br.inatel.icc.gigasecurity.gigamonitor.util.Utils;

import static br.inatel.icc.gigasecurity.gigamonitor.activities.DeviceListActivity.mDeviceManager;

/**
 * Created by filipecampos on 03/12/2015.
 */
public class SurfaceViewComponent extends FrameLayout implements IFunSDKResult{

    public GLSurfaceView20 mySurfaceView;
    public ProgressBar progressBar;
    public String deviceSn;

    //IDs / Handlers
    String TAG = "SurfaceViewComp";
    public int mPlayerHandler = 0;
    public int mySurfaceViewID;
    public long realPlayHandleID;
    private int mUserID = -1;
    public int mySurfaceViewChannelId;
    public int mySurfaceViewOrderId;
    public PlaybackListener currentPlaybackListener;
    public int recHandler;

    // state variables
    private int streamType = 1;  //HD:0, SD:1
    public boolean isConnected = false;
    public boolean isPlaying = false;
    public boolean isREC = false;
    public int playType = 0; //0 - live, 1 - playback live
    public boolean isSeeking = false;
    private int seekPercentage = 0;

    FrameLayout.LayoutParams lp;
    Context mContext;
    Activity mActivity;


    public SurfaceViewComponent(Context context){
        super(context);
        this.setLongClickable(true);

        if(mUserID == -1){
            mUserID = FunSDK.RegUser(this);
        }
        init(context);
    }



    public SurfaceViewComponent(Context context, AttributeSet attrs){
        super(context, attrs);

        if(mUserID == -1){
            mUserID = FunSDK.RegUser(this);
        }
        init(context);

    }

    public SurfaceViewComponent(Context context, AttributeSet attrs, int defStyle){
        super(context, attrs, defStyle);

        if(mUserID == -1){
            mUserID = FunSDK.RegUser(this);
        }
        init(context);

    }

    private void init(Context context){
        mContext = context;
        mActivity = (Activity) context;
        this.setLongClickable(true);
        if(mySurfaceView == null){
            mySurfaceView = new GLSurfaceView20(getContext());
            mySurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
            mySurfaceView.setLongClickable(true);
            lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, Gravity.CENTER);
            mySurfaceView.setLayoutParams(lp);
            this.addView(mySurfaceView);

        }
        progressBar = new ProgressBar(context);
        progressBar.setIndeterminate(true);
        progressBar.setScrollBarStyle(View.SCROLLBARS_INSIDE_INSET);
        progressBar.setLayoutParams(new FrameLayout.LayoutParams(mySurfaceView.getWidth()/4, mySurfaceView.getWidth()/4, Gravity.CENTER));
    }


    public void setViewSize(int width, int height){
        lp.width = width;
        lp.height = height;
        mySurfaceView.requestLayout();

    }

    public void onPlayLive() {
        if(isConnected)
            onResume();
        else
            mDeviceManager.addToStart(this);
    }

    public int onStartVideo(){
        mPlayerHandler = FunSDK.MediaRealPlay(mUserID, deviceSn, mySurfaceViewChannelId, streamType, mySurfaceView, mySurfaceViewOrderId);
        return mPlayerHandler;
    }

    public boolean isHD(){
        if(streamType == 0)
            return true;
        else
            return false;
    }

    public void setStreamType(int type){
        this.streamType = type;
    }

    public void onPause(){
        if (isPlaying) {
            FunSDK.MediaPause(mPlayerHandler, 1, mySurfaceViewOrderId);
        }
    }

    public void onResume(){
        FunSDK.MediaPause(mPlayerHandler, 0, mySurfaceViewOrderId);
    }

    public void onStop(){
        if ( mPlayerHandler != 0 ) {
            FunSDK.MediaStop(mPlayerHandler);
            mPlayerHandler = 0;
            if(playType == 1)
                isConnected = false;
        }
        isPlaying = false;
    }

    public void restartVideo(){
        if(isConnected){
            onStop();
            mPlayerHandler = FunSDK.MediaRealPlay(mUserID, deviceSn, mySurfaceViewChannelId, streamType, mySurfaceView, mySurfaceViewOrderId);
        }
    }

    public void takeSnapshot(){
        if(mPlayerHandler != 0){
            String path = Environment.getExternalStorageDirectory().getPath() + "/Pictures/Giga Monitor/" + Utils.currentDateTime();
            int result = FunSDK.MediaSnapImage(mPlayerHandler, path, 0);
            if(result == 0){
                Utils.savePictureFile(path);
            }
        }
    }

    public void startRecord(){
        if(mPlayerHandler != 0) {
            String path = Environment.getExternalStorageDirectory().getPath() + "/Movies/Giga Monitor/" + Utils.currentDateTime() + ".mp4";
            FunSDK.MediaStartRecord(mPlayerHandler, path, 0);
            recHandler = mPlayerHandler;
            isREC = true;
        }
    }

    public void stopRecord(){
        if(mPlayerHandler != 0) {
            FunSDK.MediaStopRecord(recHandler, 0);
        }
    }

    public void onPlayPlayback(H264_DVR_FILE_DATA file){
        mPlayerHandler = FunSDK.MediaNetRecordPlay(mUserID, deviceSn, G.ObjToBytes(file), mySurfaceView, 0);
    }

    public void seekByTime(int absTime) {
        if (mPlayerHandler != 0) {
            FunSDK.MediaSeekToTime(mPlayerHandler, 0, absTime, 0);
        }
    }

    public void seekByPos(int percentage){
        if (mPlayerHandler != 0) {
            seekPercentage = percentage;
            FunSDK.MediaSeekToPos(mPlayerHandler, percentage, 0);
        }
    }

    public GLSurfaceView getMySurfaceView() {
        return mySurfaceView;
    }

    public void setMySurfaceView(final GLSurfaceView20 mySurfaceView) {
        this.mySurfaceView = mySurfaceView;
    }

    public ProgressBar getProgressBar() {
        return progressBar;
    }

    public void setProgressBar(ProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    public int getMySurfaceViewID() {
        return mySurfaceViewID;
    }

    public void setMySurfaceViewID(int mySurfaceViewID) {
        this.mySurfaceViewID = mySurfaceViewID;
    }

    public long getRealPlayHandleID() {
        return realPlayHandleID;
    }

    public void setRealPlayHandleID(long realPlayHandleID) {
        this.realPlayHandleID = realPlayHandleID;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setIsPlaying(boolean isPlaying) {
        this.isPlaying = isPlaying;
    }

    public boolean isREC() {
        return isREC;
    }

    public void setREC(boolean REC) {
        isREC = REC;
    }

    public int getMySurfaceViewChannelId() {
        return mySurfaceViewChannelId;
    }

    public void setMySurfaceViewChannelId(int mySurfaceViewChannelId) {
        this.mySurfaceViewChannelId = mySurfaceViewChannelId;
    }

    public void setCurrentPlaybackListener(PlaybackListener listener){
        this.currentPlaybackListener = listener;
    }

    private int parsePlayPosition(String str) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
            return (int)(sdf.parse(str).getTime()/1000);
        } catch (Exception e) {

        }
        return 0;
    }

//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        mScaleDetector.onTouchEvent(event);
//        if(isScaling) {
//            return true;
//        }
//        final int action = event.getActionMasked();
//        switch (action) {
//            case MotionEvent.ACTION_MOVE: {
////                    panImage(event.getX() - lastX, event.getY() - lastY);
//                    lastX = event.getX();
//                    lastY = event.getY();
//                Log.d(TAG, "onTouchEvent: MOVE");
//            }
//            break;
//            case MotionEvent.ACTION_DOWN: {
//                lastX = event.getX();
//                lastY = event.getY();
//                Log.d(TAG, "onTouchEvent: DOWN, surface: " + mySurfaceViewChannelId);
//            }
//            break;
//            case MotionEvent.ACTION_UP: {
//                Log.d(TAG, "onTouchEvent: UP");
//            }
//            break;
//            case MotionEvent.ACTION_CANCEL:{
//                Log.d(TAG, "onTouchEvent: CANCEL");
//                Log.d(TAG, "onTouchEvent: position " + event.getX() + " " + event.getY() + ", surface: " + mySurfaceViewChannelId);
//            }
//
//        }
//
//        return true;
//    }

//    @Override
//    public boolean onInterceptTouchEvent(MotionEvent ev) {
//        return true;
//    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if(this.isConnected && !this.isPlaying())
            this.onResume();
        if(this.isConnected) {
            this.progressBar.setVisibility(VISIBLE);
            this.onStartVideo();
        }
    }

    @Override
    public int OnFunSDKResult(Message msg, MsgContent msgContent) {
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
            case EUIMSG.START_PLAY: {
                Log.i(TAG, "EUIMSG.START_PLAY");
                    if (msg.arg1 == 0) {
                        this.isConnected = true;
                        if (mDeviceManager.isOnStartQueue(this)) {
                            mDeviceManager.removeFromStartQueue(this);
                            mDeviceManager.requestStart();
                        }
                        Log.i(TAG, "START SUCCESS");
                    } else {
                        mDeviceManager.requestStart();
                        Log.i(TAG, "START FAILED");
                    }
            }
            break;
            case EUIMSG.STOP_PLAY: {
                Log.i(TAG, "EUIMSG.STOP_PLAY");
                if (msg.arg1 == 0) {
                    Log.i(TAG, "STOP SUCCESS");
                    isConnected = false;

                } else {
                    Log.i(TAG, "STOP FAILED");
                }
            }
            break;
            case EUIMSG.PAUSE_PLAY: {
                Log.i(TAG, "EUIMSG.PAUSE_PLAY");
                if (msg.arg1 == 1) {
                    this.isPlaying = true;
                    Log.i(TAG, "PLAY/PAUSE: playing");
                } else if(msg.arg1 == 2) {
                    this.isPlaying = false;
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
                    this.isPlaying = true;
                    if(playType == 0)
                        this.progressBar.setVisibility(INVISIBLE);
                    else if(playType == 1){
                        currentPlaybackListener.onPlayState(2);
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
                    File file = new File(msgContent.str);
                    isREC = false;
                    if(file.length() > 1000)
                        Toast.makeText(mContext, "Gravação finalizada", Toast.LENGTH_SHORT).show();
                    else{
                        file.delete();
                        Toast.makeText(mContext, "Falha na captura da imagem", Toast.LENGTH_SHORT).show();
                    }
                } else{
                    Toast.makeText(mContext, "Falha na gravação", Toast.LENGTH_SHORT).show();
                }
            }
            break;
            case EUIMSG.ON_PLAY_INFO: {
                Log.d(TAG, "OnFunSDKResult: ON_PLAY_INFO");
                if (playType == 1) {
                    int progress = msg.arg2 - msg.arg1;
                    Log.d(TAG, "OnFunSDKResult: PROGRESS " + progress);
                    if(!isSeeking && progress > 0)
                        currentPlaybackListener.onChangeProgress(progress);
                    else if(isSeeking){
                        currentPlaybackListener.onChangeProgress(progress);
                    }
                }
            }
            break;
            case EUIMSG.ON_PLAY_END: {
                if(playType == 1){
                    currentPlaybackListener.onComplete();
                }
            }
            case EUIMSG.SEEK_TO_POS: {
                if(msg.arg1 == 0){
                    seekPercentage = 0;
//                    if(isSeeking && !isPlaying)
//                        onResume();
                } else{
                    seekByPos(seekPercentage);
                }
            }
            break;
        }
        return 0;
    }

}

