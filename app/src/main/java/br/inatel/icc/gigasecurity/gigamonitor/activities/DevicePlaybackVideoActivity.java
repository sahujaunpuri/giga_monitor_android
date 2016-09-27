package br.inatel.icc.gigasecurity.gigamonitor.activities;

import android.app.Activity;
import android.content.res.Configuration;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import br.inatel.icc.gigasecurity.gigamonitor.R;
import br.inatel.icc.gigasecurity.gigamonitor.core.DeviceManager;
import br.inatel.icc.gigasecurity.gigamonitor.model.Device;
import br.inatel.icc.gigasecurity.gigamonitor.model.FileDataGiga;
import com.xm.MyConfig;
import com.xm.NetSdk;
import com.xm.video.MySurfaceView;

public class DevicePlaybackVideoActivity extends ActionBarActivity {

    private MySurfaceView mSurfaceView;
    private SeekBar mSeekBar;
    private TextView mStatusTextView;
    private ImageView ivPlayPause, ivStop, ivForward, ivBackward;

    private MySurfaceView.OnPlayBackPosListener mPlaybackPositionListener =
            new MySurfaceView.OnPlayBackPosListener() {

                @Override
                public void onPlayBackPos(int progress) {
                    if (progress == -1) return;
                    mSeekBar.setProgress(progress - mStartSecond);
                }
            };

    private NetSdk.OnRPlayBackCompletedListener mPlaybackCompleteListener =
            new NetSdk.OnRPlayBackCompletedListener() {
                @Override
                public void onRPlayBackCompleted(long l, long l2, long l3, long l4) {
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            stop();
                            mSeekBar.setProgress(0);
                        }
                    });
                }
            };

    private SeekBar.OnSeekBarChangeListener mSeekBarChangeListener =
            new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    pause();
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    setPlaybackProgress( (100*seekBar.getProgress()) / seekBar.getMax());
                    resume();
                }
            };

    private Device mDevice;
    private FileDataGiga mFileDataGiga;
    private DeviceManager mManager;
    private Activity mActivity;

    private int mSurfaceViewID;
    private boolean mPlaying, mPaused;
    private int mStartSecond;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_playback_video);

        //Check Screen Orientation.
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getSupportActionBar().hide();
        }

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mActivity = this;
        mManager = DeviceManager.getInstance();

        // Find views
        mSurfaceView    = (MySurfaceView) findViewById(R.id.surface_view_test_1);
        mSeekBar        = (SeekBar) findViewById(R.id.seek_bar_playback);
        mStatusTextView = (TextView) findViewById(R.id.text_view_playback_status);
        ivPlayPause     = (ImageView) findViewById(R.id.iv_play_playback);
        ivStop          = (ImageView) findViewById(R.id.iv_stop_playback);
        ivForward       = (ImageView) findViewById(R.id.iv_forward_playback);
        ivBackward      = (ImageView) findViewById(R.id.iv_backward_playback);

        // Set listeners
        mSurfaceView.setOnPlayBackPosListener(mPlaybackPositionListener);
        mManager.setOnPlaybackCompleteListener(mPlaybackCompleteListener);
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

        ivForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forwardButtonClick();
            }
        });

        ivBackward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backwardButtonClick();
            }
        });

        // Get data from previous activity
        Bundle extras = getIntent().getExtras();
        mDevice = (Device) extras.getSerializable("device");
        mFileDataGiga = (FileDataGiga) extras.getSerializable("fileData");

        // Init MySurfaceView
        mSurfaceViewID = DeviceListActivity.mySurfaceViewID++;

        mSurfaceView.init(this, mSurfaceViewID);
        mSurfaceView.setAudioCtrl(MyConfig.AudioState.OPENED);

        // Configure progress bar
        mSeekBar.setMax(mFileDataGiga.getTotalTime());
        mStartSecond = mFileDataGiga.getStartSecond();

        play();
    }

    public void playButtonClick() {
        if (mPaused) {
            resume();
        } else if (mPlaying) {
            pause();
        } else {
            play();
        }
    }

    public void stopButtonClick() {
        if (mPlaying) {
            stop();
            mSeekBar.setProgress(0);
        }
    }

    public void backwardButtonClick() {
        if(mManager.playbackSlow(mDevice.getPlaybackHandle(), mSurfaceView)){
            Log.w("Playback Slow", "Success.");

            String text = getResources().getString(R.string.label_playing_slow, true);

            updateStatusTextView(text);
        } else {
            Log.w("Playback Slow", "Error.");
        }
    }

    public void forwardButtonClick() {
        if(mManager.playbackFaster(mDevice.getPlaybackHandle(), mSurfaceView)) {
            Log.w("Playback Fast", "Success.");

            String text = getResources().getString(R.string.label_playing_fast, true);

            updateStatusTextView(text);
        } else {
            Log.w("Playback Fast", "Error.");
        }
    }

    public void updateStatusTextView(final CharSequence statusText) {
        mStatusTextView.setText(statusText);
    }

    public void play() {
        mManager.playbackPlay(mDevice, mFileDataGiga, mSurfaceView, mSurfaceViewID);
        mPlaying = true;

        String text = getResources().getString(R.string.label_playing, true);

        updateStatusTextView(text);

        ivPlayPause.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_playback));
    }

    public void resume() {
        mManager.playbackResume(mDevice.getPlaybackHandle(), mSurfaceView);
        mPaused = false;

        String text = getResources().getString(R.string.label_playing, true);

        updateStatusTextView(text);

        ivPlayPause.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_playback));
    }

    public void stop() {
        mManager.playbackStop(mDevice.getPlaybackHandle(), mSurfaceView);
        mPlaying = false;
        mPaused = false;

        String text = getResources().getString(R.string.label_stopped, true);

        updateStatusTextView(text);

        ivPlayPause.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_playback));
    }

    public void pause() {
        mManager.playbackPause(mDevice.getPlaybackHandle(), mSurfaceView);
        mPaused = true;

        String text = getResources().getString(R.string.label_paused, true);

        updateStatusTextView(text);

        ivPlayPause.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_playback));
    }

    public void setPlaybackProgress(int percentProgress) {
        if(mManager.setPlaybackProgress(mDevice.getPlaybackHandle(), percentProgress)) {
            Log.w("Set Playback Progress", "Success.");
        } else {
            Log.w("Set Playback Progress", "Error");
        }
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
                finish();
                return true;
            default:
                return false;
        }
    }
}
