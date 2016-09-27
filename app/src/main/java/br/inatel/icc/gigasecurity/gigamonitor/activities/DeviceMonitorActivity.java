package br.inatel.icc.gigasecurity.gigamonitor.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.xm.ChnInfo;
import com.xm.video.MySurfaceView;

import br.inatel.icc.gigasecurity.gigamonitor.R;
import br.inatel.icc.gigasecurity.gigamonitor.core.DeviceManager;
import br.inatel.icc.gigasecurity.gigamonitor.model.Device;

/**
 * File: DeviceMonitorActivity.java
 * Creation date: 05/09/2014
 * Author: Dênis Vilela
 * <p/>
 * Purpose: Declaration of class DeviceMonitorActivity.java
 * Copyright 2014, INATEL Competence Center
 * <p/>
 * All rights are reserved. Reproduction in whole or part is
 * prohibited without the written consent of the copyright owner.
 */
public class DeviceMonitorActivity extends ActionBarActivity implements View.OnClickListener{
    private MySurfaceView mSurfaceView;
    private Device mDevice;

    private boolean playing = false;
    private DeviceManager mManager;

    private Button mStartVideoButton, mStopVideoButton, mStartAudioButton, mStopAudioButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_monitor);
        initComponents();

        mManager = DeviceManager.getInstance();

        mDevice = (Device) getIntent().getExtras().getSerializable("device");

        ChnInfo ci = new ChnInfo();
        mManager.startDeviceVideo(mDevice.getLoginID(), mSurfaceView, 0, ci);

        setVideoEnabled(true);
        setAudioEnabled(false);
    }

    private void initComponents() {
        mSurfaceView = (MySurfaceView) findViewById(R.id.surface_view_test_1);

        mStartVideoButton = (Button) findViewById(R.id.button_start_video);
        mStopVideoButton = (Button) findViewById(R.id.button_stop_video);
        mStartVideoButton.setOnClickListener(this);
        mStopVideoButton.setOnClickListener(this);

        mStartAudioButton = (Button) findViewById(R.id.button_start_audio);
        mStopAudioButton = (Button) findViewById(R.id.button_stop_audio);
        mStartAudioButton.setOnClickListener(this);
        mStopAudioButton.setOnClickListener(this);

        mSurfaceView.init(DeviceMonitorActivity.this, 0);
    }

    private void setVideoEnabled(boolean enabled) {
        if (enabled) {
            if(!playing){
                mSurfaceView.onPlay();
                mStartVideoButton.setText("Pause");
                playing = true;
            }else{
                mStartVideoButton.setText("Play");
                mSurfaceView.onPause();
                playing = false;
            }
        } else {
            mStartVideoButton.setText("Play");
            mSurfaceView.onPause();
            playing = false;
            mManager.pauseDeviceVideo(mSurfaceView);
        }

        // UI
        mStopVideoButton.setEnabled(enabled);
    }

    private void setAudioEnabled(boolean enabled) {
        mManager.setDeviceAudioEnabled(mSurfaceView, enabled);

        // UI
        mStartAudioButton.setEnabled(!enabled);
        mStopAudioButton.setEnabled(enabled);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_start_video:
                setVideoEnabled(true);
                break;
            case R.id.button_stop_video:
                setVideoEnabled(false);
                break;
            case R.id.button_start_audio:
                setAudioEnabled(true);
                break;
            case R.id.button_stop_audio:
                setAudioEnabled(false);
                break;
        }
    }

    @Override
    protected void onPause() {
        setVideoEnabled(false);
        setAudioEnabled(false);

        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.simple_menu, menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();


    }

    /*private void initImage(final Long loginID) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                NetSdk mNetSdk = NetSdk.getInstance(); //FIXME: change to mDeviceManager
                mNetSdk.SetupAlarmChan(loginID);
                mNetSdk.SetAlarmMessageCallBack();
                final ChnInfo chnInfo = new ChnInfo();
                chnInfo.ChannelNo = 0;
                chnInfo.nStream = 1;
                final long playHandle = mNetSdk.onRealPlay(0, loginID, chnInfo);
                if (playHandle > 0) {
                    final MyVideoData videoData = new MyVideoData(getApplicationContext(), 0);
                    mNetSdk.setDataCallback(playHandle);

                    mSurfaceView.setAudioCtrl(MyConfig.AudioState.CLOSED);
                    mSurfaceView.initData();
                }

                //                           videoData.initData();


            }
        });
        thread.run();

        mPlayback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        DateTime dateTime = new DateTime();

                        FindInfo findInfo = new FindInfo();
                        findInfo.nChannelN0 = 0;
                        findInfo.nFileType = SDKFileType.SDK_RECORD_ALL.getValue();

                        findInfo.startTime.year = dateTime.getYear();
                        findInfo.startTime.month = dateTime.getMonthOfYear();
                        findInfo.startTime.day = dateTime.getDayOfMonth();
                        findInfo.startTime.hour = 0;
                        findInfo.endTime.year = dateTime.getYear();
                        findInfo.endTime.month = dateTime.getMonthOfYear();
                        findInfo.endTime.day = dateTime.getDayOfMonth();
                        findInfo.endTime.hour = 24;

                        DeviceManager.getInstance().findPlaybackFile(loginID, findInfo);
                    }
                });
                thread.run();
            }
        });

    }*/
//    protected void Stop() {
//        mNetSdk.onStopAlarmMsg(true);
//        mWndsHolder.vv1.onStop();
//        if(mplayhandle != null) {
//            if(mplayhandle.length > 0)
//                mNetSdk.onStopRealPlay(mplayhandle[0]);
//        }
//    }

}
