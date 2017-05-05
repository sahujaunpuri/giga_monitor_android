package br.inatel.icc.gigasecurity.gigamonitor.ui;

import android.app.Activity;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.video.opengl.GLSurfaceView20;

import br.inatel.icc.gigasecurity.gigamonitor.core.DeviceManager;
import br.inatel.icc.gigasecurity.gigamonitor.model.SurfaceViewManager;

/**
 * Created by zappts on 4/6/17.
 */

public class SurfaceViewComponent extends FrameLayout {
    String TAG = "SurfaceViewLayout";
    String TAG2 = "SurfaceViewLayoutTouch";
    public SurfaceViewManager mSurfaceViewManager;
    private DeviceManager mDeviceManager;
    public GLSurfaceView20 mySurfaceView;
    private ProgressBar progressBar;

    private FrameLayout.LayoutParams lp;
    private Context mContext;
    public int deviceId;
    public String deviceConnection;
    public int mySurfaceViewChannelId; //ordem original
    public int mySurfaceViewOrderId;  //ordem modificada para grid
    public String recordFileName;

    // state variables
    public int mPlayerHandler = 0;
    public int streamType = 1;  //HD:0, SD:1
    private boolean isConnected = false;
    public boolean isPlaying = false;
    public boolean isREC = false;
    public int playType = 0; //0 - live, 1 - playback live
    public boolean isSeeking = false;
    public int seekPercentage = 0;
    public boolean isFavorite = false;
    public boolean isSendAudioEnabled = false;
    public boolean isReceiveAudioEnabled = false;
    public boolean isScaling = false;
    public float mScaleFactor = 1.F;
    public boolean isVisible = false;
    public boolean stoppingRec = false;

    public SurfaceViewComponent(Context context, SurfaceViewManager surfaceViewManager, int id) {
        super(context);
        this.mContext = context;
        this.mySurfaceViewChannelId = id;
        this.mSurfaceViewManager = surfaceViewManager;
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
        mDeviceManager = DeviceManager.getInstance();
        mScaleDetector = new ScaleGestureDetector(mContext, new ScaleListener());
        mClickListener = new GestureDetector(mContext, new SimpleGestureDetector());

        //SurfaceView
        if(playType == 0) {
            mySurfaceView = mSurfaceViewManager.getMySurfaceView(mySurfaceViewChannelId);
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
//        progressBar.setLayoutParams(mSurfaceViewManager.pbParam);
//        this.addView(progressBar);

        if(mSurfaceViewManager != null) {
            mySurfaceView.setLayoutParams(mSurfaceViewManager.surfaceViewLayout);
//            this.addView(mySurfaceView);
            progressBar.setLayoutParams(mSurfaceViewManager.pbParam);
            this.addView(progressBar);
        }
    }

    public SurfaceViewComponent surfaceViewComponent(){
        return this;
    }

    public void setViewSize(final int width, final int height){
//        lp.width = width;
//        lp.height = height;

        FrameLayout.LayoutParams pbParam = new FrameLayout.LayoutParams(width, height, Gravity.CENTER);
        progressBar.setLayoutParams(pbParam);
        FrameLayout.LayoutParams svParam = new FrameLayout.LayoutParams(width, height, Gravity.CENTER);
        mySurfaceView.setLayoutParams(svParam);

//        progressBar.getLayoutParams().height = height/4;
//        progressBar.getLayoutParams().width = width/4;

//        this.getLayoutParams().height = height;
//        this.getLayoutParams().width = width;

        this.requestLayout();
    }

    public boolean isHD(){
        return (streamType == 0);
    }

    public void setStreamType(int type){
        this.streamType = type;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setIsPlaying(boolean isPlaying) {
        this.isPlaying = isPlaying;
    }

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

    public int getMySurfaceViewChannelId() {
        return mySurfaceViewChannelId;
    }

    public void setMySurfaceViewChannelId(int mySurfaceViewChannelId) {
        this.mySurfaceViewChannelId = mySurfaceViewChannelId;
    }

    public int getDeviceId(){
        return mSurfaceViewManager.mDevice.getId();
    }

    public void isLoading(final boolean isLoading){
        ((Activity) mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(isLoading) {
                    progressBar.setVisibility(VISIBLE);
                }else {
                    progressBar.setVisibility(GONE);
//                    mySurfaceView.setVisibility(VISIBLE);
                }
            }
        });
    }

    private void interruptScroll(){
        this.getParent().requestDisallowInterceptTouchEvent(true);
        if(mSurfaceViewManager.mRecyclerAdapter!=null)
            mSurfaceViewManager.mRecyclerAdapter.disableListScrolling();

    }

    public void resumeScroll(){
        mySurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        this.getParent().requestDisallowInterceptTouchEvent(false);
        if(mSurfaceViewManager.mRecyclerAdapter!=null)
            mSurfaceViewManager.mRecyclerAdapter.enableListScrolling();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        new Thread(new Runnable() {
            @Override
            public void run() {
                isVisible = false;
                if(isConnected()) {
                    mSurfaceViewManager.onStop(surfaceViewComponent());
                }
            }
        }).start();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        isVisible = true;
//        new Thread(new Runnable() {
//            @Override
//            public void run() {

//        this.removeView(mySurfaceView);
//        mySurfaceView = mSurfaceViewManager.getMySurfaceView(mySurfaceViewChannelId);
//        ViewGroup parent = (ViewGroup) mySurfaceView.getParent();
//        if(parent != null)
//            parent.removeView(mySurfaceView);
//        this.addView(mySurfaceView);
////        mySurfaceView.setOnZoomListener(mScaleListener);

//        progressBar.bringToFront();
//                if(isConnected) {
//                    surfaceViewComponent().isLoading(true);
//                    mSurfaceViewManager.onStartVideo(surfaceViewComponent());
//                    mSurfaceViewManager.onPlayLive(surfaceViewComponent());
//                }
//            }
//        }).start();

    }


    /** Touch Handlers **/
    private ScaleGestureDetector mScaleDetector;
    private GestureDetector mClickListener;
    public GLSurfaceView20.OnZoomListener mScaleListener = new GLSurfaceView20.OnZoomListener(){
        @Override
        public void onScale(float v, View view, MotionEvent motionEvent) {
            mScaleFactor = v;
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
        final int action = ev.getActionMasked();
        if(mScaleFactor > 1.F)
            interruptScroll();
        mySurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
        if(mSurfaceViewManager.mRecyclerAdapter != null)
            mClickListener.onTouchEvent(ev);
        mScaleDetector.onTouchEvent(ev);
        if(isScaling)
            return false;

        switch (action) {
            case MotionEvent.ACTION_MOVE: {
                if(mScaleFactor == 1.F){
                    resumeScroll();
                    return false;
                }
            }
            break;
            case MotionEvent.ACTION_DOWN: {
            }
            break;
            case MotionEvent.ACTION_UP: {
            }
            break;
            case MotionEvent.ACTION_CANCEL: {
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
            mSurfaceViewManager.mRecyclerAdapter.openOverlayMenu(surfaceViewComponent());
            Log.d(TAG2, "onSingleTapConfirmed: ");
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
                        mSurfaceViewManager.stopRecord(surfaceViewComponent());
                        isREC = false;
                        String mensagem = "Gravação finalizada";
                        Toast.makeText(mContext, mensagem, Toast.LENGTH_SHORT).show();
                    }
                    mSurfaceViewManager.mRecyclerAdapter.singleQuad(mySurfaceViewChannelId);
                    /*new Thread(new Runnable() {    //timer para evitar que um pequeno arraste no doubleclick também faça scroll
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(750);
                                resumeScroll();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();*/

                }
            });

            return true;
        }
    }


}
