package br.inatel.icc.gigasecurity.gigamonitor.ui;

import android.app.Activity;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.lib.EPTZCMD;
import com.video.opengl.GLSurfaceView20;

import br.inatel.icc.gigasecurity.gigamonitor.model.ChannelsManager;

public class SurfaceViewComponent extends FrameLayout {
    String TAG = "SurfaceViewLayout";
    String TAG2 = "SurfaceViewLayoutTouch";
    public ChannelsManager mChannelsManager;
    public GLSurfaceView20 mySurfaceView;
    public ProgressBar progressBar;
    public TextView message;
    public OverlayPTZ ptzOverlay;

    private Context mContext;
    public int deviceId;
    public String deviceConnection;
    public int mySurfaceViewChannelId; //ordem original
    public int mySurfaceViewNewChannelId; // ordem modificada pelo usuario
    public int mySurfaceViewOrderId;  //ordem modificada para grid
    public String recordFileName;

    // state variables
    public int mPlayerHandler = 0;
    public int streamType = 1;  //HD:0, SD:1
    private boolean isConnected = false;
    public boolean isPlaying = false;
    public boolean isREC = false;
    public int playType = 0; //0 - live, 1 - playback live, 2 - view de preenchimento
    public boolean isSeeking = false;
    public int seekPercentage = 0;
    public boolean isFavorite = false;
    public boolean isSendAudioEnabled = false;
    public boolean isReceiveAudioEnabled = false;
    public boolean isScaling = false;
    public float mScaleFactor = 1.F;
    public boolean isVisible = false;
    public boolean stoppingRec = false;
    public boolean isPTZEnabled = false;
    private float previousX = 0, previousY = 0, dx, dy;
    private int previsousPTZCommand;
    private boolean longPress = false;
    private boolean isLoading = false;

    public SurfaceViewComponent(Context context, ChannelsManager channelsManager, int channelId) {
        super(context);
        this.mContext = context;
        this.mySurfaceViewChannelId = channelId;
        this.mySurfaceViewNewChannelId = channelId;
        this.mChannelsManager = channelsManager;
        init();
    }

    public SurfaceViewComponent(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public SurfaceViewComponent(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        playType = 1;
        init();
    }

    private void init(){
        this.setLongClickable(true);
        mScaleDetector = new ScaleGestureDetector(mContext, new ScaleListener());
        mClickListener = new GestureDetector(mContext, new SimpleGestureDetector());

        //SurfaceView
        if(playType == 0 && mySurfaceView == null) {
            mySurfaceView = mChannelsManager.getMySurfaceView(mySurfaceViewNewChannelId);
//        mySurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);

        }

        if (mySurfaceView == null)
            mySurfaceView = new GLSurfaceView20(mContext);
        mySurfaceView.setLongClickable(true);
        mySurfaceView.setOnZoomListener(mScaleListener);
        ViewGroup parent = (ViewGroup) mySurfaceView.getParent();
        if (parent != null)
            parent.removeAllViews();
        this.addView(mySurfaceView);


        //ProgressBar
        progressBar = new ProgressBar(mContext);
        progressBar.setIndeterminate(true);
        progressBar.setScrollBarStyle(View.SCROLLBARS_INSIDE_INSET);
//        FrameLayout.LayoutParams pbParam = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, Gravity.CENTER);
//        progressBar.setLayoutParams(mDeviceChannelsManager.pbParam);
//        this.addView(progressBar);

        if(mChannelsManager != null) {
            mySurfaceView.setLayoutParams(mChannelsManager.surfaceViewLayout);
//            this.addView(mySurfaceView);
            progressBar.setLayoutParams(mChannelsManager.pbParam);
            this.addView(progressBar);
        }
    }

    public SurfaceViewComponent surfaceViewComponent(){
        return this;
    }

//    public void setViewSize(final int width, final int height){
//        FrameLayout.LayoutParams pbParam = new FrameLayout.LayoutParams(width, height, Gravity.CENTER);
//        progressBar.setLayoutParams(pbParam);
//        FrameLayout.LayoutParams svParam = new FrameLayout.LayoutParams(width, height, Gravity.CENTER);
//        mySurfaceView.setLayoutParams(svParam);
//
//        this.requestLayout();
//    }

    public boolean isHD(){
        return (streamType == 0);
    }

    public void setStreamType(int type){
        this.streamType = type;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

//    public void setIsPlaying(boolean isPlaying) {
//        this.isPlaying = isPlaying;
//    }

    public boolean isConnected(){
        return isConnected;
    }

    public void setConnected(boolean bool){
        isConnected = bool;
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

    public boolean isPTZEnabled() {
        return isPTZEnabled;
    }

    public void setPTZEnabled(boolean PTZEnabled) {
        isPTZEnabled = PTZEnabled;
        mChannelsManager.enablePTZ(PTZEnabled, this);
    }

//    public void enablePTZ() {
//        ivTouch = new ImageView(mContext);
//        ivTouch.setImageResource(R.drawable.ic_touch_white_36dp);
//        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, Gravity.CENTER);
//        this.addView(ivTouch, params);
//        ivTouch.setScaleX(0.6f);
//        ivTouch.setScaleY(0.6f);
//
//        LayoutInflater inflater = (LayoutInflater)mDeviceManager.currentContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        ptzOverlay = inflater.inflate(R.layout.ptz_overlay, null);
//        this.addView(ptzOverlay);
//        ivTouch = (ImageView) findViewById(R.id.touch_icon);
//        Log.d(TAG, "enablePTZ: ");
//    }

    public void disablePTZ() {
        isPTZEnabled = false;
//        this.removeView(ivTouch);
//        ivTouch = null;
//        this.removeView(ptzOverlay);
//        ptzOverlay = null;
        Log.d(TAG, "disablePTZ: ");
    }

    public void ptz(int command, boolean state) {
        mChannelsManager.ptzControl(command, this, state);
    }

    public int getMySurfaceViewChannelId() {
        return mySurfaceViewNewChannelId;
    }

//    public void setMySurfaceViewChannelId(int mySurfaceViewChannelId) {
//        this.mySurfaceViewChannelId = mySurfaceViewChannelId;
//    }
//
//    public int getDeviceId(){
//        return deviceId;
//    }

    public void isLoading(final boolean isLoading){
        ((Activity) mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(isLoading) {
                    progressBar.setVisibility(VISIBLE);
                    setLoading(true);
                }else {
                    progressBar.setVisibility(GONE);
                    setLoading(false);
//                    mySurfaceView.setVisibility(VISIBLE);
                }
            }
        });
    }

    private void handlePTZ(){
        int command = -1;
        int sensitivity = 20;
        int sensitivityDiagonal = 10;
//        float absDX = Math.abs(dx);
//        float absDY = Math.abs(dy);
        if(dx>sensitivityDiagonal && dy>sensitivityDiagonal){
            //direita baixo
//            label.setText(" DB");
            command = EPTZCMD.PAN_RIGTHDOWN;
        } else if(dx>sensitivityDiagonal && dy<-sensitivityDiagonal){
            //direita cima
//            label.setText(" DC");
            command = EPTZCMD.PAN_RIGTHTOP;
        } else if(dx<-sensitivityDiagonal && dy>sensitivityDiagonal){
            //esquerda baixo
            command = EPTZCMD.PAN_LEFTDOWN;
//            label.setText(" EB");
        } else if(dx<-sensitivityDiagonal && dy<-sensitivityDiagonal){
            //esquerda cima
            command = EPTZCMD.PAN_LEFTTOP;
//            label.setText(" EC");
        } else if(dx>sensitivity){
            //direita
            command = EPTZCMD.PAN_RIGHT;
//            label.setText(" D");
            Log.d(TAG2, "onInterceptTouchEvent: MOVE RIGHT");
        }else if(dx<-sensitivity){
            //esquerda
            command = EPTZCMD.PAN_LEFT;
//            label.setText(" E");
            Log.d(TAG2, "onInterceptTouchEvent: MOVE LEFT");
        } else if(dy>sensitivity){
            //baixo
            command = EPTZCMD.TILT_DOWN;
//            label.setText(" B");
            Log.d(TAG2, "onInterceptTouchEvent: MOVE DOWN");
        } else if(dy<-sensitivity){
            //cima
            command = EPTZCMD.TILT_UP;
//            label.setText(" C");
            Log.d(TAG2, "onInterceptTouchEvent: MOVE UP");
        }
        if(command != -1) {
            mChannelsManager.ptzControl(command, this, false);
            dx = dy = 0;
            previsousPTZCommand = command;
        }
    }

    private void interruptScroll(){
        this.getParent().requestDisallowInterceptTouchEvent(true);
        if(mChannelsManager.mRecyclerAdapter!=null)
            mChannelsManager.mRecyclerAdapter.disableListScrolling();

    }

    public void resumeScroll(){
        try {
            mySurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
            this.getParent().requestDisallowInterceptTouchEvent(false);
            if (mChannelsManager.mRecyclerAdapter != null) {
                mChannelsManager.mRecyclerAdapter.enableListScrolling();
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

//    public void setIvTouch(int visibility) {
//        Log.d(TAG, "setIvTouch: ");
//        if(ivTouch != null)
//            this.ivTouch.setVisibility(visibility);
//    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        isVisible = false;
        if(playType == 0)
            mChannelsManager.mRecyclerAdapter.closeOverlayMenu();
//            mChannelsManager.mRecyclerAdapter.openOverlayMenu(this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(isConnected()) {
                    mChannelsManager.onStop(surfaceViewComponent());
                }
            }
        }).start();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        isVisible = true;
    }


    /** Touch Handlers **/
    private ScaleGestureDetector mScaleDetector;
    private GestureDetector mClickListener;
    public GLSurfaceView20.OnZoomListener mScaleListener = new GLSurfaceView20.OnZoomListener(){
        @Override
        public void onScale(float v, View view, MotionEvent motionEvent) {
//            float oldScale = mScaleFactor;
            mScaleFactor = v;
//            if (mScaleFactor > 1.0 && isPTZEnabled() && oldScale != mScaleFactor) {
//                ivTouch.setVisibility(INVISIBLE);
//                ptzOverlay.setVisibility(INVISIBLE);
//            } else if (isPTZEnabled() && oldScale != mScaleFactor) {
//                ivTouch.setVisibility(VISIBLE);
//            }
//            Log.d(TAG, "onScale: " + v);

        }

        @Override
        public void onBoundary(boolean b, boolean b1) {
        }

        @Override
        public void onTrans(float var1, float var2){
        }
    };

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
//        boolean ret = false;
        final int action = ev.getActionMasked();
        final int pointerCount = ev.getPointerCount();

        if(mScaleFactor > 1.F || isPTZEnabled())
            interruptScroll();
        mySurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
        if(mChannelsManager.mRecyclerAdapter != null)
            mClickListener.onTouchEvent(ev);

        mScaleDetector.onTouchEvent(ev);
        if(isScaling)
            return false;

        switch (action) {
            case MotionEvent.ACTION_MOVE: {
                if(mScaleFactor == 1.F && !isPTZEnabled()){
                    resumeScroll();
                    return false;
                } else if(isPTZEnabled() && !isScaling && mScaleFactor == 1.F && longPress && pointerCount == 1){
                    dx += ev.getX() - previousX;
                    dy += ev.getY() - previousY;

                    handlePTZ();

//                    Log.d(TAG2, "onInterceptTouchEvent: MOVE dx:" + dx + " dy:" + dy);
                }

                previousX = ev.getX();
                previousY = ev.getY();
            }
            break;
            case MotionEvent.ACTION_DOWN: {
                handler.postDelayed(mLongPressed, 500);
                previousX = ev.getX();
                previousY = ev.getY();
            }
            break;
            case MotionEvent.ACTION_UP: {
                handler.removeCallbacks(mLongPressed);
                if(isPTZEnabled() && longPress && pointerCount == 1 && ptzOverlay != null){
                    if(mScaleFactor == 1.0f)
                        ptzOverlay.setPartialVisibility(View.VISIBLE);
//                        ivTouch.setVisibility(VISIBLE);
                    mChannelsManager.ptzControl(previsousPTZCommand, this, true);
//                    ptzOverlay.setVisibility(INVISIBLE);
                    longPress = false;
                    Log.d(TAG, "onInterceptTouchEvent: touch up");
                }

                previousX = ev.getX();
                previousY = ev.getY();
            }
            break;
            case MotionEvent.ACTION_CANCEL: {
            }
        }

        return false;
    }

    final Handler handler = new Handler();
    Runnable mLongPressed = new Runnable() {
        public void run() {
            try {
                longPress = true;
                ((Activity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "long press: ");
                        if(/*ivTouch != null && */isPTZEnabled()) {
                            if (ptzOverlay != null) {
                                ptzOverlay.setPartialVisibility(View.INVISIBLE);
                            }
//                        ivTouch.setVisibility(GONE);
//                        ptzOverlay.setVisibility(VISIBLE);
                        }
                    }
                });
            } catch (Exception error){
                error.printStackTrace();
            }
        }
    };

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {

        @Override
        public boolean onScaleBegin(ScaleGestureDetector scaleGestureDetector) {
            Log.d(TAG2, "onScaleBegin: ");
//            ivTouch.setVisibility(View.GONE);
//            ptzOverlay.setPartialVisibility(View.INVISIBLE);
//            ptzOverlay.setVisibility(INVISIBLE);
            isScaling = true;
            interruptScroll();
            handler.removeCallbacks(mLongPressed);
            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector scaleGestureDetector){
            Log.d(TAG2, "onScaleEnd: ");
            resumeScroll();
            isScaling = false;
//            if (mScaleFactor > 1.0 && isPTZEnabled()) {
//                ivTouch.setVisibility(INVISIBLE);
//                ptzOverlay.setVisibility(INVISIBLE);
//            } else if (isPTZEnabled()) {
//                ivTouch.setVisibility(VISIBLE);
//                ptzOverlay.setVisibility(VISIBLE);
//            }
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
            mChannelsManager.mRecyclerAdapter.openOverlayMenu(surfaceViewComponent());
            resumeScroll();
            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent motionEvent) {
            //switch to quad 1
            Log.d(TAG2, "onDoubleTap: ");
            ((Activity) mContext).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(isREC){
                        mChannelsManager.stopRecord(surfaceViewComponent());
                        isREC = false;
                        String mensagem = "Gravação finalizada";
                        Toast.makeText(mContext, mensagem, Toast.LENGTH_SHORT).show();
                    }
                    setPTZEnabled(false);
                    if(mChannelsManager.mDevice.getSerialNumber().equals("Favoritos")) {
                        mChannelsManager.mRecyclerAdapter.singleQuad(mySurfaceViewOrderId);
                    }else {
                        mChannelsManager.mRecyclerAdapter.singleQuad(mySurfaceViewOrderId);
                    }
                }
            });

            return true;
        }

        /*@Override
        public void onLongPress(MotionEvent e) {
            super.onLongPress(e);
            Log.d(TAG2, "onLongPress: ");
            longPress = true;
            ((Activity) mContext).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(ivTouch != null && isPTZEnabled()) {
                        ivTouch.setVisibility(INVISIBLE);
                        ptzOverlay.setVisibility(VISIBLE);
                    }
                }
            });
        }*/
    }

    public boolean isLoading() {
        return isLoading;
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
    }

}
