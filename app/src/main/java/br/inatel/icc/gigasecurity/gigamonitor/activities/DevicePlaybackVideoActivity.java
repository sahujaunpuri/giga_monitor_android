package br.inatel.icc.gigasecurity.gigamonitor.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ClipData;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.internal.view.menu.MenuView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.lib.sdk.struct.H264_DVR_FILE_DATA;
import com.lib.sdk.struct.SDK_SYSTEM_TIME;
import com.lib.sdk.struct.Strings;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import br.inatel.icc.gigasecurity.gigamonitor.R;
import br.inatel.icc.gigasecurity.gigamonitor.core.DeviceManager;
import br.inatel.icc.gigasecurity.gigamonitor.listeners.PlaybackListener;
import br.inatel.icc.gigasecurity.gigamonitor.model.Device;
import br.inatel.icc.gigasecurity.gigamonitor.model.FileData;
import br.inatel.icc.gigasecurity.gigamonitor.model.DeviceChannelsManager;
import br.inatel.icc.gigasecurity.gigamonitor.ui.SurfaceViewComponent;

public class DevicePlaybackVideoActivity extends ActionBarActivity {

    private SurfaceViewComponent mSurfaceView;
    private DeviceChannelsManager mDeviceChannelsManager;
    private Menu menu;
    private SeekBar mSeekBar;
    private ProgressBar mProgressBar;
    private TextView initialTime, mStatusTextView, endTime;
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
                    if (!mSurfaceView.isSeeking) {
                        mSeekBar.setProgress(progress - mStartSecond);
                        if (currentProgress != 0 && progress != currentProgress) {
                            try {
                                SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");
                                Date d = sdf.parse(initialTime.getText().toString());
                                Calendar calendar = Calendar.getInstance();
                                calendar.setTime(d);
                                calendar.add(Calendar.SECOND, progress - currentProgress);
                                String dif = sdf.format(calendar.getTime());
                                initialTime.setText(dif);

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
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
                public void onCompleteSeek() {
                    mSurfaceView.isSeeking = false;
                }

                @Override
                public void onPlayState(int state) {
                    if (state == 2 && !mSurfaceView.isSeeking) {
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
                    if (fromUser /*&& (mSurfaceView.isConnected())*/) {
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
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_playback_video);

        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);

        //Check Screen Orientation.
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getSupportActionBar().hide();
        }

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mActivity = this;

        // Get data from previous activity
        Bundle extras = getIntent().getExtras();
        int deviceID = (int) extras.getSerializable("device");
        mDevice = DeviceManager.getInstance().findDeviceById(deviceID);
        mFileData = (FileData) extras.getSerializable("fileData");
        //*mDeviceChannelsManager = DeviceManager.getInstance().findChannelManagerByDevice(mDevice);
        mDeviceChannelsManager = new DeviceChannelsManager(mDevice);

        // Find views
        mSurfaceView = (SurfaceViewComponent) findViewById(R.id.surface_view_test_1);
        mProgressBar = (ProgressBar) findViewById(R.id.pb_playback);
        mSeekBar = (SeekBar) findViewById(R.id.seek_bar_playback);
        initialTime = (TextView) findViewById(R.id.text_view_playback_initial_time);
        mStatusTextView = (TextView) findViewById(R.id.text_view_playback_status);
        endTime = (TextView) findViewById(R.id.text_view_playback_end_time);
        ivPlayPause = (ImageView) findViewById(R.id.iv_play_playback);
        ivStop = (ImageView) findViewById(R.id.iv_stop_playback);
        ivForward = (ImageView) findViewById(R.id.iv_forward_playback);
        ivBackward = (ImageView) findViewById(R.id.iv_backward_playback);

        ivPlayPause.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_playback));

        //(SDK ANTIGA)botões desabilitados a pedido da giga, videos curtos estavam pulando muitos frames e encerrando a exibição
        ivBackward.setVisibility(View.GONE);
        ivForward.setVisibility(View.GONE);

        initialTime.setText("00:00");
        long timeDifference = mFileData.getEndDate().getTime() - mFileData.getBeginDate().getTime();
        if (timeDifference == 3600000) {
            endTime.setText("59:59");
        } else {
            SimpleDateFormat format = new SimpleDateFormat("mm:ss");
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(timeDifference);
            String dif = format.format(calendar.getTime());
            endTime.setText(dif);
        }

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
        mSeekBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return progressBarDisabled;
            }
        });

        Log.d(TAG, "onCreate totalTime: " + (int) mFileData.getTotalTime());
        Log.d(TAG, "onCreate startSecond: " + mStartSecond);


        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
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
            currentProgress = 0;
            initialTime.setText("00:00");
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

    private void snapshotPlayback() {
        if (mSurfaceView.isConnected() && mSurfaceView.isPlaying()) {
            mDeviceChannelsManager.takeSnapshot(mSurfaceView);
            flashView();
        } else {
            Log.v("SNAPSHOT", "The playback is not running to snapshot!");
        }
    }

    private void recordPlayback() {
        if (mSurfaceView.isConnected() && mSurfaceView.isPlaying()) {
            if (mSurfaceView.isREC()) {
                mDeviceChannelsManager.stopRecord(mSurfaceView);
                menu.getItem(1).setIcon(R.drawable.record);
            } else {
                mDeviceChannelsManager.startRecord(mSurfaceView);
                menu.getItem(1).setIcon(R.mipmap.red_record);
            }
        } else {
            Log.v("RECORD", "The playback is not running to record!");
        }
    }

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
        int seconds = beginTime.st_6_second + progress % 60 % 60;
        newTime.st_6_second = seconds % 60;
        int minutes = beginTime.st_5_minute + (int) TimeUnit.SECONDS.toMinutes(progress) + (seconds / 60);
        newTime.st_5_minute = minutes % 60;
        newTime.st_4_hour = beginTime.st_4_hour + (int) TimeUnit.SECONDS.toHours(progress) + (minutes / 60);


        Log.d(TAG, "setPlaybackProgress: time2" + mFileData.getFileData().st_3_beginTime.toString());
        mDeviceChannelsManager.onPlayPlayback(mFileData.getFileData(), mSurfaceView);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void flashView() {
        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(mSurfaceView, "alpha", 1f, .3f);
        fadeOut.setDuration(2000);
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(mSurfaceView, "alpha", .3f, 1f);
        fadeIn.setDuration(2000);

        final AnimatorSet mAnimationSet = new AnimatorSet();

        mAnimationSet.play(fadeIn).after(fadeOut);

        mAnimationSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mAnimationSet.start();
            }
        });
        mAnimationSet.start();
    }

    @Override
    protected void onPause() {
        stop();
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_video_playback, menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.menu = menu;
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
            case R.id.playback_snapshot:
                snapshotPlayback();
                return true;
            case R.id.playback_record:
                recordPlayback();
                return true;
            case R.id.playback_download:
                Log.v("PLAYBACK", "Download");
                return true;
            default:
                return false;
        }
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("DevicePlaybackVideo Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
}
