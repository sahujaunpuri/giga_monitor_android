package br.inatel.icc.gigasecurity.gigamonitor.activities;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.lib.sdk.struct.H264_DVR_FILE_DATA;
import com.lib.sdk.struct.SDK_SYSTEM_TIME;

import java.util.concurrent.TimeUnit;

import br.inatel.icc.gigasecurity.gigamonitor.R;
import br.inatel.icc.gigasecurity.gigamonitor.listeners.PlaybackListener;
import br.inatel.icc.gigasecurity.gigamonitor.model.Device;
import br.inatel.icc.gigasecurity.gigamonitor.model.FileData;
import br.inatel.icc.gigasecurity.gigamonitor.model.DeviceChannelsManager;
import br.inatel.icc.gigasecurity.gigamonitor.ui.SurfaceViewComponent;

public class DevicePlaybackVideoActivity extends ActionBarActivity {

    private SurfaceViewComponent mSurfaceView;
    private DeviceChannelsManager mDeviceChannelsManager;
    private SeekBar mSeekBar;
    private ProgressBar mProgressBar;
    private TextView mStatusTextView;
    private ImageView ivPlayPause, ivStop, ivForward, ivBackward;
    private final String TAG = "playback";
    private int currentBar;

    private Device mDevice;
    private FileData mFileData;
    private Activity mActivity;
    private SDK_SYSTEM_TIME beginTime;

    private int mStartSecond;
    private int currentProgress = 0;
    public boolean progressBarDisabled = true;

    private PlaybackListener mPlaybackListener =
            new PlaybackListener() {
                @Override
                public void onComplete() {
                    currentProgress = 0;
                    stopButtonClick();
                }

                @Override
                public void onChangeProgress(int progress) {
                    if(!mSurfaceView.isSeeking) {
                        mSeekBar.setProgress(progress - mStartSecond);
                        currentProgress = progress;
                        Log.d(TAG, "onChangeProgress: " + currentProgress);

                    }/*else if(((progress*100)/(int)mFileData.getTotalTime()) == definedProgress){
                        mSurfaceView.setVisibility(View.VISIBLE);
                        mSurfaceView.isSeeking = false;
                        mDeviceChannelsManager.currentPlaybackListener.onPlayState(2);
                        definedProgress = -1;
                    }
                    Log.d(TAG, "onChangeProgress: progress:" + (progress*100)/(int)mFileData.getTotalTime() + " definedProgress:" + definedProgress);*/
                }

                @Override
                public void onCompleteSeek(){
                    mSurfaceView.isSeeking = false;
                }

                @Override
                public void onPlayState(int state){
                    if(state == 2 && !mSurfaceView.isSeeking){
                        progressBarDisabled = false;
                        String text = getResources().getString(R.string.label_playing);
                        updateStatusTextView(text);
                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ivPlayPause.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_playback));
                                mProgressBar.setVisibility(View.INVISIBLE);
                            }
                        });
                    }
                }

            };

    private SeekBar.OnSeekBarChangeListener mSeekBarChangeListener =
            new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if(fromUser /*&& (mSurfaceView.isConnected())*/){
                        currentBar = progress;
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
//                    mSurfaceView.setVisibility(View.INVISIBLE);
                    mDeviceChannelsManager.onStop(mSurfaceView);
                    mSurfaceView.isSeeking = true;
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    setPlaybackProgress(currentBar);
                    progressBarDisabled = true;
                    updateStatusTextView("Buscando");
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mProgressBar.setVisibility(View.VISIBLE);
                        }
                    });
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_playback_video);

        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);

        //Check Screen Orientation.
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getSupportActionBar().hide();
        }

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mActivity = this;

        // Get data from previous activity
        Bundle extras = getIntent().getExtras();
        mDevice = (Device) extras.getSerializable("device");
        mFileData = (FileData) extras.getSerializable("fileData");
        //*mDeviceChannelsManager = DeviceManager.getInstance().findChannelManagerByDevice(mDevice);
        mDeviceChannelsManager = new DeviceChannelsManager(mDevice);

        // Find views
        mSurfaceView                      = (SurfaceViewComponent) findViewById(R.id.surface_view_test_1);
        mProgressBar                      = (ProgressBar) findViewById(R.id.pb_playback);
        mSeekBar                          = (SeekBar) findViewById(R.id.seek_bar_playback);
        mStatusTextView                   = (TextView) findViewById(R.id.text_view_playback_status);
        ivPlayPause                       = (ImageView) findViewById(R.id.iv_play_playback);
        ivStop                            = (ImageView) findViewById(R.id.iv_stop_playback);
        ivForward                         = (ImageView) findViewById(R.id.iv_forward_playback);
        ivBackward                        = (ImageView) findViewById(R.id.iv_backward_playback);

        ivPlayPause.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_playback));

        //(SDK ANTIGA)botões desabilitados a pedido da giga, videos curtos estavam pulando muitos frames e encerrando a exibição
        ivBackward.setVisibility(View.GONE);
        ivForward.setVisibility(View.GONE);



        // Set listeners
        mDeviceChannelsManager.setCurrentPlaybackListener(mPlaybackListener);
        mSeekBar.setOnSeekBarChangeListener(mSeekBarChangeListener);

        ivPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playButtonClick();
            }
        });

        ivStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopButtonClick();
            }
        });

//        ivForward.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                forwardButtonClick();
//            }
//        });

//        ivBackward.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                backwardButtonClick();
//            }
//        });


        mSurfaceView.deviceConnection = mDevice.connectionString;
        mSurfaceView.playType = 1;
        mDeviceChannelsManager.playType = 1;

        mDeviceChannelsManager.addSurfaceViewComponent(mSurfaceView);
        mSurfaceView.mChannelsManager = this.mDeviceChannelsManager;
        beginTime = new SDK_SYSTEM_TIME();
        beginTime.st_6_second = mFileData.getFileData().st_3_beginTime.st_6_second;
        beginTime.st_5_minute = mFileData.getFileData().st_3_beginTime.st_5_minute;
        beginTime.st_4_hour = mFileData.getFileData().st_3_beginTime.st_4_hour;

//        mSurfaceView.setAudioCtrl(MyConfig.AudioState.OPENED);

        // Configure progress bar
        mSeekBar.setMax((int) mFileData.getTotalTime());
        mStartSecond = (int) mFileData.getStartTime();
        mSeekBar.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return progressBarDisabled;
            }
        });

        Log.d(TAG, "onCreate totalTime: " + (int)mFileData.getTotalTime());
        Log.d(TAG, "onCreate startSecond: " + mStartSecond);


    }

    public void playButtonClick() {
        if (mSurfaceView.isConnected() && !mSurfaceView.isPlaying()) {
            resume();
        } else if (mSurfaceView.isPlaying()) {
            pause();
        } else {
            play();
        }
    }

    public void stopButtonClick() {
        if (mSurfaceView.isConnected()) {
            stop();
            mSurfaceView.isSeeking = false;
            mSeekBar.setProgress(0);
        }
    }

//    public void backwardButtonClick() {
//        if(mManager.playbackSlow(mDevice.getPlaybackHandle(), mSurfaceView)){
//            Log.w("Playback Slow", "Success.");
//
//            String text = getResources().getString(R.string.label_playing_slow);
//
//            updateStatusTextView(text);
//        } else {
//            Log.w("Playback Slow", "Error.");
//        }
//    }

//    public void forwardButtonClick() {
//        if(mManager.playbackFaster(mDevice.getPlaybackHandle(), mSurfaceView)) {
//            Log.w("Playback Fast", "Success.");
//
//            String text = getResources().getString(R.string.label_playing_fast);
//
//            updateStatusTextView(text);
//        } else {
//            Log.w("Playback Fast", "Error.");
//        }
//    }

    public void updateStatusTextView(final CharSequence statusText) {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mStatusTextView.setText(statusText);
            }
        });
    }

    public void play() {
        String text = "Carregando vídeo";
        updateStatusTextView(text);
        mSurfaceView.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.VISIBLE);
        mSurfaceView.isLoading(true);
        mDeviceChannelsManager.onPlayPlayback(mFileData.getFileData(), mSurfaceView);
        mSeekBar.setProgress(0);
    }

    public void resume() {
        mDeviceChannelsManager.onResume(mSurfaceView);

        String text = getResources().getString(R.string.label_playing);

        updateStatusTextView(text);

        ivPlayPause.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_playback));
    }

    public void stop() {
        mDeviceChannelsManager.onStop(mSurfaceView);

        String text = getResources().getString(R.string.label_stopped);
        updateStatusTextView(text);
        progressBarDisabled = true;
        mFileData.getFileData().st_3_beginTime.st_6_second = beginTime.st_6_second;
        mFileData.getFileData().st_3_beginTime.st_5_minute = beginTime.st_5_minute;
        mFileData.getFileData().st_3_beginTime.st_4_hour = beginTime.st_4_hour;

        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mSurfaceView.setVisibility(View.INVISIBLE);
                mProgressBar.setVisibility(View.INVISIBLE);
                ivPlayPause.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_playback));
            }
        });
    }

    public void pause() {
        mDeviceChannelsManager.onPause(mSurfaceView);

        String text = getResources().getString(R.string.label_paused);
        updateStatusTextView(text);

        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ivPlayPause.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_playback));
            }
        });

    }

    public void setPlaybackProgress(int progress) {
//        Log.d(TAG, "setPlaybackProgress: SEEKING TO: " + (progress*100)/(int)mFileData.getTotalTime());
//        definedProgress = (progress*100)/(int)mFileData.getTotalTime();
//        mDeviceChannelsManager.seekByPos(definedProgress, mSurfaceView);


        Log.d(TAG, "setPlaybackProgress: time1" + mFileData.getFileData().st_3_beginTime.toString() + " progress:" + progress);
        Log.d(TAG, "setPlaybackProgress: time1" + beginTime.toString());
        SDK_SYSTEM_TIME newTime = mFileData.getFileData().st_3_beginTime;
        int seconds = beginTime.st_6_second + progress%60%60;
        newTime.st_6_second = seconds%60;
        int minutes = beginTime.st_5_minute + (int) TimeUnit.SECONDS.toMinutes(progress) + (seconds/60);
        newTime.st_5_minute = minutes%60;
        newTime.st_4_hour = beginTime.st_4_hour + (int) TimeUnit.SECONDS.toHours(progress) + (minutes/60);


        Log.d(TAG, "setPlaybackProgress: time2" + mFileData.getFileData().st_3_beginTime.toString());
        mDeviceChannelsManager.onPlayPlayback(mFileData.getFileData(), mSurfaceView);
    }

    @Override
    protected void onPause() {
        stop();
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
                mDeviceChannelsManager.onStop(mSurfaceView);
//                mDeviceChannelsManager.playType = 0;
//                DeviceManager.getInstance().updateSurfaceViewManagers();
                finish();
                return true;
            default:
                return false;
        }
    }
}
