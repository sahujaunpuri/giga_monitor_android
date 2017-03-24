package br.inatel.icc.gigasecurity.gigamonitor.model;

import android.app.Activity;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.Environment;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.basic.G;
import com.lib.EPTZCMD;
import com.lib.EUIMSG;
import com.lib.FunSDK;
import com.lib.IFunSDKResult;
import com.lib.MsgContent;
import com.lib.sdk.struct.H264_DVR_FILE_DATA;
import com.video.opengl.GLSurfaceView20;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Locale;

import br.inatel.icc.gigasecurity.gigamonitor.adapters.ChannelRecyclerViewAdapter;
import br.inatel.icc.gigasecurity.gigamonitor.core.DeviceManager;
import br.inatel.icc.gigasecurity.gigamonitor.listeners.PlaybackListener;
import br.inatel.icc.gigasecurity.gigamonitor.ui.OverlayMenu;
import br.inatel.icc.gigasecurity.gigamonitor.util.Utils;

/**
 * Created by filipecampos on 03/12/2015.
 */
public class SurfaceViewComponent extends RelativeLayout implements IFunSDKResult{

    public GLSurfaceView20 mySurfaceView;
    public ProgressBar progressBar;
    public String deviceSn;

    //IDs / Handlers
    String TAG = "SurfaceViewComp";
    String TAG2 = "svcTOUCH";
    public int mPlayerHandler = 0;
    public long realPlayHandleID;
    private int mUserID = -1;
    public int mySurfaceViewChannelId; //ordem original
    public int mySurfaceViewOrderId;  //ordem modificada para grid
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
    private float mScaleFactor = 1.F;
    public boolean isScaling = false;
    public boolean isFavorite = false;
    public boolean isSendAudioEnabled = false;
    public boolean isReceiveAudioEnabled = false;

    RelativeLayout.LayoutParams lp;
    Context mContext;
    Activity mActivity;
    DeviceManager mDeviceManager;
    OverlayMenu menu;

    public ChannelRecyclerViewAdapter mRecyclerAdapter;

    private ScaleGestureDetector mScaleDetector;
    private  GestureDetector mClickListener;
    private GLSurfaceView20.OnZoomListener mScaleListener = new GLSurfaceView20.OnZoomListener(){
        @Override
        public void onScale(float v, View view, MotionEvent motionEvent) {
            Log.d(TAG2, "onScale: " + v);
            mScaleFactor = v;
        }

        @Override
        public void onBoundary(boolean b, boolean b1) {
        }
    };

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

    private SurfaceViewComponent surfaceViewComponent(){
        return this;
    }

    private void init(Context context){
        mContext = context;
        mDeviceManager = DeviceManager.getInstance();
        mActivity = (Activity) context;
        this.setLongClickable(true);
        if(mySurfaceView == null){
            mySurfaceView = new GLSurfaceView20(getContext());
            mySurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
            mySurfaceView.setLongClickable(true);
            mySurfaceView.setOnZoomListener(mScaleListener);
            lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
            lp.addRule(RelativeLayout.CENTER_IN_PARENT);
            mySurfaceView.setLayoutParams(lp);
            this.addView(mySurfaceView);

        }
        progressBar = new ProgressBar(context);
        progressBar.setIndeterminate(true);
        progressBar.setScrollBarStyle(View.SCROLLBARS_INSIDE_INSET);

        RelativeLayout.LayoutParams pbParam = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        pbParam.addRule(RelativeLayout.CENTER_IN_PARENT);
        progressBar.setLayoutParams(pbParam);
        this.addView(progressBar);

//        menu = new OverlayMenu(context, this);
//        this.addView(menu, new RelativeLayout.LayoutParams(400, 400));
//        menu.setVisibility(VISIBLE);

        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
        mClickListener = new GestureDetector(context, new SimpleGestureDetector());
    }


    public void setViewSize(int width, int height){
        lp.width = width;
        lp.height = height;
        mySurfaceView.requestLayout();
        progressBar.getLayoutParams().height = height/4;
        progressBar.getLayoutParams().width = width/4;
        progressBar.requestLayout();

//        menu.getLayoutParams().width = width;
//        menu.getLayoutParams().height = height;
//        menu.requestLayout();

    }

    public void onPlayLive() {
        if(isConnected)
            onResume();
        else
            mDeviceManager.addToStart(this);
    }

    public int onStartVideo(){
        this.isConnected = true;
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

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
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

    public void ptzControl(int command){
        //EPTZCMD
        FunSDK.DevPTZControl(mPlayerHandler, deviceSn, mySurfaceViewChannelId, command, 0, 4, mySurfaceViewChannelId);
    }

    private void interruptScroll(){
        this.getParent().requestDisallowInterceptTouchEvent(true);
        if(mRecyclerAdapter!=null)
            mRecyclerAdapter.disableListScrolling();

    }

    private void resumeScroll(){
        this.getParent().requestDisallowInterceptTouchEvent(false);
        if(mRecyclerAdapter!=null)
            mRecyclerAdapter.enableListScrolling();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        final int action = ev.getActionMasked();
        if(mScaleFactor > 1.F)
            interruptScroll();
        if(mRecyclerAdapter != null)
            mClickListener.onTouchEvent(ev);
        mScaleDetector.onTouchEvent(ev);
        if(isScaling)
            return false;

        switch (action) {
            case MotionEvent.ACTION_MOVE: {
                Log.d(TAG2, "onTouchEvent: MOVE");
                if(mScaleFactor == 1.F){
                    resumeScroll();
                    return false;
                }
            }
            break;
            case MotionEvent.ACTION_DOWN: {
                Log.d(TAG2, "onTouchEvent: DOWN, surface: " + mySurfaceViewChannelId);
            }
            break;
            case MotionEvent.ACTION_UP: {
                Log.d(TAG2, "onTouchEvent: UP");
            }
            break;
            case MotionEvent.ACTION_CANCEL: {
                Log.d(TAG2, "onTouchEvent: CANCEL");
            }
        }
        return false;
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScaleBegin(ScaleGestureDetector scaleGestureDetector) {
            Log.d(TAG2, "onScaleBegin: ");
            isScaling = true;
            interruptScroll();
            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector scaleGestureDetector){
            Log.d(TAG2, "onScaleEnd: ");
            resumeScroll();
            isScaling = false;
        }

        @Override
        public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
            return true;
        }
    }

    private class SimpleGestureDetector extends GestureDetector.SimpleOnGestureListener{

        @Override
        public boolean onSingleTapConfirmed(MotionEvent motionEvent) {
            //open menu
//            mRecyclerAdapter.openOverlayMenu(surfaceViewComponent(), surfaceViewComponent().mySurfaceViewChannelId);
            surfaceViewComponent().menu.setVisibility(VISIBLE);
            Log.d(TAG2, "onSingleTapConfirmed: ");
            resumeScroll();
            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent motionEvent) {
            //switch to quad 1
            mRecyclerAdapter.singleQuad(surfaceViewComponent(), surfaceViewComponent().mySurfaceViewChannelId);
            Log.d(TAG2, "onDoubleTap: ");
            resumeScroll();
            return true;
        }
    }




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

