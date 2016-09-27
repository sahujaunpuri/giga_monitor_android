package br.inatel.icc.gigasecurity.gigamonitor.activities;


import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.xm.ChnInfo;
import com.xm.MyConfig;
import com.xm.NetSdk;
import com.xm.video.MySurfaceView;

import br.inatel.icc.gigasecurity.gigamonitor.R;
import br.inatel.icc.gigasecurity.gigamonitor.core.DeviceManager;
import br.inatel.icc.gigasecurity.gigamonitor.model.Device;
import br.inatel.icc.gigasecurity.gigamonitor.model.FileDataGiga;

public class PTZActivity extends ActionBarActivity {

    private MySurfaceView mSurfaceView;
    private TextView mStatusTextView;

    private NetSdk mNetSdk = null;

    private Device mDevice;
    private FileDataGiga mFileDataGiga;
    private DeviceManager mManager;
    //private Activity mActivity;

    private int mSurfaceViewID;
    private boolean mPlaying = false;
    //private boolean mPaused;


    private Button mLeftTop, mTop, mRightTop, mLeft, mRight, mLeftDown, mDown, mRightDown, mStop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ptz);

        // Find Views
        mSurfaceView = (MySurfaceView) findViewById(R.id.surface_view);

        //mStatusTextView = (TextView) findViewById(R.id.text_view_playback_status);

        mLeftTop = (Button) findViewById(R.id.button_ptz_left_top);
        mTop = (Button) findViewById(R.id.button_ptz_top);
        mRightTop = (Button) findViewById(R.id.button_ptz_right_top);
        mLeft = (Button) findViewById(R.id.button_ptz_left);
        mStop = (Button) findViewById(R.id.button_ptz_stop);
        mRight = (Button) findViewById(R.id.button_ptz_right);
        mLeftDown = (Button) findViewById(R.id.button_ptz_left_down);
        mDown = (Button) findViewById(R.id.button_ptz_down);
        mRightDown = (Button) findViewById(R.id.button_ptz_right_down);

        // Init MySurfaceView
        mSurfaceViewID = 0;
        mSurfaceView.init(PTZActivity.this, mSurfaceViewID);

        mManager = DeviceManager.getInstance();
        mNetSdk = NetSdk.getInstance();

        // Get data from previous activity
        Bundle extras = getIntent().getExtras();
        mDevice = (Device) extras.getSerializable("device");

        ChnInfo ci = new ChnInfo();
        mManager.startDeviceVideo(mDevice.getLoginID(), mSurfaceView, 0, ci);

        setVideo(true);
        getButtonActions();
    }

    public void getButtonActions() {
            //LeftTop Action
            mLeftTop.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        mNetSdk.PTZControl(mDevice.getLoginID(), 0, MyConfig.PTZ_ControlType.PAN_LEFTTOP, false, 45, 0L, 0L);
                        return true;

                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                        mNetSdk.PTZControl(mDevice.getLoginID(), 0, MyConfig.PTZ_ControlType.PAN_LEFTTOP, true, 5L, 0L, 0L);
                        return false;
                    }
                    return false;
                }
            });

        //Top Action
        mTop.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    mNetSdk.PTZControl(mDevice.getLoginID(), 0, MyConfig.PTZ_ControlType.TILT_UP, false, 5L, 0L, 0L);
                    return true;

                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    mNetSdk.PTZControl(mDevice.getLoginID(), 0, MyConfig.PTZ_ControlType.TILT_UP, true, 5L, 0L, 0L);
                    return false;
                }
                return false;
            }
        });

        //RightTop Action
        mRightTop.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    mNetSdk.PTZControl(mDevice.getLoginID(), 0, MyConfig.PTZ_ControlType.PAN_RIGTHTOP, false, 5L, 0L, 0L);
                    return true;

                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    mNetSdk.PTZControl(mDevice.getLoginID(), 0, MyConfig.PTZ_ControlType.PAN_RIGTHTOP, true, 5L, 0L, 0L);
                    return false;
                }
                return false;
            }
        });

        //Left Action
        mLeft.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    mNetSdk.PTZControl(mDevice.getLoginID(), 0, MyConfig.PTZ_ControlType.PAN_LEFT, false, 5L, 0L, 0L);
                    return true;

                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    mNetSdk.PTZControl(mDevice.getLoginID(), 0, MyConfig.PTZ_ControlType.PAN_LEFT, true, 5L, 0L, 0L);
                    return false;
                }
                return false;
            }
        });

        //Right Action
        mRight.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    mNetSdk.PTZControl(mDevice.getLoginID(), 0, MyConfig.PTZ_ControlType.PAN_RIGHT, false, 5L, 0L, 0L);
                    return true;

                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    mNetSdk.PTZControl(mDevice.getLoginID(), 0, MyConfig.PTZ_ControlType.PAN_RIGHT, true, 5L, 0L, 0L);
                    return false;
                }
                return false;
            }
        });

        //LeftDown Action
        mLeftDown.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    mNetSdk.PTZControl(mDevice.getLoginID(), 0, MyConfig.PTZ_ControlType.PAN_LEFTDOWN, false, 5L, 0L, 0L);
                    return true;

                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    mNetSdk.PTZControl(mDevice.getLoginID(), 0, MyConfig.PTZ_ControlType.PAN_LEFTDOWN, true, 5L, 0L, 0L);
                    return false;
                }
                return false;
            }
        });

        //Down Action
        mDown.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    mNetSdk.PTZControl(mDevice.getLoginID(), 0, MyConfig.PTZ_ControlType.TILT_DOWN, false, 5L, 0L, 0L);
                    return true;

                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    mNetSdk.PTZControl(mDevice.getLoginID(), 0, MyConfig.PTZ_ControlType.TILT_DOWN, true, 5L, 0L, 0L);
                    return false;
                }
                return false;
            }
        });

        //RightDown Action
        mRightDown.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    mNetSdk.PTZControl(mDevice.getLoginID(), 0, MyConfig.PTZ_ControlType.PAN_RIGTHDOWN, false, 5L, 0L, 0L);
                    return true;

                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    mNetSdk.PTZControl(mDevice.getLoginID(), 0, MyConfig.PTZ_ControlType.PAN_RIGTHDOWN, true, 5L, 0L, 0L);
                    return false;
                }
                return false;
            }
        });
    }

    public void setVideo(boolean enable) {
        if(enable) {
            if(!mPlaying) {
                mSurfaceView.onPlay();
                mPlaying = true;
            } else {
                mSurfaceView.onPause();
                mPlaying = false;
            }
        } else {
            mSurfaceView.onPause();
            mPlaying = false;
            mManager.pauseDeviceVideo(mSurfaceView);
        }
    }

    /*
    public void playButtonClick(View v) {
        if (mPaused) {
            resume();
        } else if (mPlaying) {
            pause();
        } else {
            play();
        }
    }*/

//    public void stopButtonClick(View v) {
//        if (mPlaying && v == mStop) {
//            stop();
//        }
//    }

    public void stopButtonClick(View v){
        // TODO
    }

    public void updateStatusTextView(final CharSequence statusText) {
        mStatusTextView.setText(statusText);
    }

    public void play() {
        mManager.playbackPlay(mDevice, mFileDataGiga, mSurfaceView, mSurfaceViewID);
        mPlaying = true;
        mSurfaceView.onPlay();
        updateStatusTextView("Playing");
    }

    public void resume() {
        mManager.playbackResume(mDevice.getPlaybackHandle(), mSurfaceView);
        //mPaused = false;
        updateStatusTextView("Playing");
    }

    public void stop() {
        mManager.playbackStop(mDevice.getPlaybackHandle(), mSurfaceView);
        mPlaying = false;
        //mPaused = false;
        updateStatusTextView("Stopped");
    }

    public void pause() {
        mManager.playbackPause(mDevice.getPlaybackHandle(), mSurfaceView);
        //mPaused = true;
        mSurfaceView.onPause();
        updateStatusTextView("Paused");
    }

    public void setPlaybackProgress(int percentProgress) {
        mManager.setPlaybackProgress(mDevice.getPlaybackHandle(), percentProgress);
    }

    @Override
    protected void onPause() {
        //stop();
        setVideo(false);
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.simple_menu, menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return false;
        }
    }
}
