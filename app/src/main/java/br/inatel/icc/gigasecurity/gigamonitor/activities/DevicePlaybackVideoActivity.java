package br.inatel.icc.gigasecurity.gigamonitor.activities;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.format.Time;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.basic.G;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.lib.FunSDK;
import com.lib.SDKCONST;
import com.lib.sdk.bean.OPCompressPicBean;
import com.lib.sdk.struct.H264_DVR_FILE_DATA;
import com.lib.sdk.struct.H264_DVR_FINDINFO;
import com.lib.sdk.struct.SDK_SYSTEM_TIME;
import com.lib.sdk.struct.SDK_SearchByTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import br.inatel.icc.gigasecurity.gigamonitor.R;
import br.inatel.icc.gigasecurity.gigamonitor.adapters.DeviceExpandableListAdapter;
import br.inatel.icc.gigasecurity.gigamonitor.core.DeviceManager;
import br.inatel.icc.gigasecurity.gigamonitor.listeners.PlaybackListener;
import br.inatel.icc.gigasecurity.gigamonitor.listeners.PlaybackSearchListener;
import br.inatel.icc.gigasecurity.gigamonitor.model.Device;
import br.inatel.icc.gigasecurity.gigamonitor.model.FileData;
import br.inatel.icc.gigasecurity.gigamonitor.model.DeviceChannelsManager;
import br.inatel.icc.gigasecurity.gigamonitor.ui.SurfaceViewComponent;
import br.inatel.icc.gigasecurity.gigamonitor.util.OPCompressPic;
import br.inatel.icc.gigasecurity.gigamonitor.util.Utils;

public class DevicePlaybackVideoActivity extends ActionBarActivity {

    private SurfaceViewComponent mSurfaceView;
    private DeviceChannelsManager mDeviceChannelsManager;
    private RelativeLayout playbackLayout;
    private LinearLayout playbackControls;
    private LinearLayout playbackStatus;
    private Menu menu;
    private SeekBar mSeekBar;
    private TextView seekBarTextView;
    private float seekBarTextPosition;
    private ImageView thumbnail;
    private ProgressBar mProgressBar;
    private String initialTimeVideo;
    private TextView initialTime, mStatusTextView, endTime;
    private ImageView ivPlayPause, ivStop, ivForward, ivBackward;
    private final String TAG = "playback";
    private int currentBar;

    private Device mDevice;
    private DeviceManager mDeviceManager;
    private FileData mFileData;
    private Activity mActivity;
    private SDK_SYSTEM_TIME beginTime;
    private ArrayList<FileData> thumbnailsList = new ArrayList<>();

    private int mStartSecond;
    private int currentProgress = 0;
    public boolean progressBarDisabled = true;
    private Handler mHandler;
    private Runnable mAction;
    private boolean isAdvancing = false;

    private int surfaceViewHeight;
    private int seekBarHeight;

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
                        advanceSeek(progress);
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
                        try {
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
                            Date dt = simpleDateFormat.parse(initialTimeVideo);
                            Calendar cal = Calendar.getInstance();
                            cal.setTime(dt);
                            cal.add(Calendar.SECOND, progress);
                            seekBarTextView.setText(simpleDateFormat.format(cal.getTime()));

//                            seekBar.setThumb(new BitmapDrawable((BitmapFactory.decodeResource(getResources(), R.drawable.seekbar_progress_thumb))));
//                            ShapeDrawable thumb = new ShapeDrawable(new RectShape());
//                            thumb.getPaint().setColor(Color.rgb(0, 0, 0));
//                            thumb.setIntrinsicHeight(-80);
//                            thumb.setIntrinsicWidth(30);
//                            seekBar.setThumb(thumb);

                            int val = (progress * (seekBar.getWidth() - 2 * seekBar.getThumbOffset())) / seekBar.getMax();
                            seekBarTextView.setX(seekBar.getX() + val + seekBar.getThumbOffset() / 2);

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        currentBar = progress;
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
//                    mSurfaceView.setVisibility(View.INVISIBLE);
                    mDeviceChannelsManager.onStop(mSurfaceView);
                    mSurfaceView.isSeeking = true;
//                    seekBarTextView.setVisibility(View.VISIBLE);
                    playbackLayout.addView(seekBarTextView);
                    thumbnail = new ImageView(mActivity);
//                    FileData fileData = thumbnailsList.get(0);
//                    Utils.savePictureFile(fileData.getFileName());
//                    Bitmap bitmap = BitmapFactory.decodeFile(fileData.getFileName(), );
//                    thumbnail.setImageBitmap(bitmap);
//                    playbackLayout.addView(thumbnail);
//                    thumbnail.setVisibility(View.VISIBLE);
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    final Animation animationFadeIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.abc_slide_out_top);
                    seekBarTextView.startAnimation(animationFadeIn);

                    setPlaybackProgress(currentBar);
//                    seekBarTextView.setVisibility(View.GONE);
                    playbackLayout.removeView(seekBarTextView);
//                    playbackLayout.removeView(thumbnail);
//                    thumbnail.setVisibility(View.GONE);
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

        mActivity = this;
        mDeviceManager = DeviceManager.getInstance();

        // Get data from previous activity
        Bundle extras = getIntent().getExtras();
        int deviceID = (int) extras.getSerializable("device");
        mDevice = DeviceManager.getInstance().findDeviceById(deviceID);
        mFileData = (FileData) extras.getSerializable("fileData");
        mDeviceChannelsManager = new DeviceChannelsManager(mDevice);

        // Find views
        playbackLayout = (RelativeLayout) findViewById(R.id.playback_relative_layout);
        mSurfaceView = (SurfaceViewComponent) findViewById(R.id.surface_view_test_1);
        mProgressBar = (ProgressBar) findViewById(R.id.pb_playback);
        playbackControls = (LinearLayout) findViewById(R.id.view_playback_controls);
        playbackStatus = (LinearLayout) findViewById(R.id.view_playback_status);
        mSeekBar = (SeekBar) findViewById(R.id.seek_bar_playback);
        initialTime = (TextView) findViewById(R.id.text_view_playback_initial_time);
        mStatusTextView = (TextView) findViewById(R.id.text_view_playback_status);
        endTime = (TextView) findViewById(R.id.text_view_playback_end_time);
        ivPlayPause = (ImageView) findViewById(R.id.iv_play_playback);
        ivStop = (ImageView) findViewById(R.id.iv_stop_playback);
        ivForward = (ImageView) findViewById(R.id.iv_forward_playback);
        ivBackward = (ImageView) findViewById(R.id.iv_backward_playback);
        ivPlayPause.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_playback));

        setSeekBarTextViewPosition();

        seekBarTextView = new TextView(this);//TextView) findViewById(R.id.thumbnail_text_view2);
        seekBarTextView.setBackgroundResource(R.drawable.playback_time_box);
        seekBarTextView.setY(seekBarTextPosition);
        seekBarTextView.setLayoutParams(new ActionBar.LayoutParams(120, 35));
        seekBarTextView.setGravity(Gravity.CENTER);

        thumbnail = new ImageView(this);
        thumbnail.setY(seekBarTextPosition + 35);
        thumbnail.setLayoutParams(new ActionBar.LayoutParams(160, 100));
        thumbnail.setVisibility(View.GONE);

        surfaceViewHeight = mSurfaceView.getLayoutParams().height;
        seekBarHeight = mSeekBar.getLayoutParams().height;

        initialTimeVideo = mFileData.getBeginTimeStr();
        initialTime.setText(mFileData.getBeginTimeStr());
        endTime.setText(mFileData.getEndTimeStr());

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

        ivForward.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (mHandler != null) {
                        return true;
                    }
                    int valueOfSecondsToAdvance = 2;
                    String text = ">> 2x";
                    advanceOrDelayVideo(text, valueOfSecondsToAdvance);
                    return true;
                } else if (event.getAction() == event.ACTION_UP) {
                    if (mHandler == null) {
                        return true;
                    }
                    stopAdvancingVideoAndPlay();
                    return true;
                }
                return false;
            }
        });

        ivBackward.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (mHandler != null) {
                        return true;
                    }
                    String text = "<< 2x";
                    int valueOfSecondsToBack = -2;
                    advanceOrDelayVideo(text, valueOfSecondsToBack);
                    return true;
                } else if (event.getAction() == event.ACTION_UP) {
                    if (mHandler == null) {
                        return true;
                    }
                    stopAdvancingVideoAndPlay();
                    return true;
                }
                return false;
            }
        });

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

        requestDeviceSearchPicture();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
//        thumbnailView.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void showMessage() {
        Toast.makeText(this, "O vídeo precisa estar em andamento!", Toast.LENGTH_SHORT).show();
    }

    private void setSeekBarTextViewPosition() {
        seekBarTextPosition = mSurfaceView.getY() + mSurfaceView.getLayoutParams().height - 40;
    }

    private void advanceOrDelayVideo(String text, int value) {
        if (mSurfaceView.isPlaying()) {
            mDeviceChannelsManager.onStop(mSurfaceView);
            mSurfaceView.isSeeking = true;
            updateStatusTextView(text);
            populateRunnable(value);
            mHandler = new Handler();
            mHandler.postDelayed(mAction, 500);
            isAdvancing = true;
        } else {
            Toast.makeText(this, "O vídeo deve estar em andamento!", Toast.LENGTH_SHORT).show();
        }
    }

    private void stopAdvancingVideoAndPlay() {
        if (isAdvancing) {
            isAdvancing = false;
            mHandler.removeCallbacks(mAction);
            mHandler = null;
            mSurfaceView.isSeeking = false;
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                Date d = sdf.parse(initialTime.getText().toString());
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(d);
                mFileData.getFileData().st_3_beginTime.st_6_second = calendar.get(Calendar.SECOND);
                mFileData.getFileData().st_3_beginTime.st_5_minute = calendar.get(Calendar.MINUTE);
                mFileData.getFileData().st_3_beginTime.st_4_hour = calendar.get(Calendar.HOUR_OF_DAY);
                currentProgress = 0;
                play();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    private void populateRunnable(final int value) {
        mAction = new Runnable() {
            @Override public void run() {
                advanceSeek(currentProgress + value);
                currentProgress += value;
                mHandler.postDelayed(this, 500);
            }
        };
    }

    private void advanceSeek(int progress) {
        mSeekBar.setProgress(progress - mStartSecond);
        if (currentProgress != 0 && progress != currentProgress) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
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
            initialTime.setText(initialTimeVideo);
        }
    }

    private void snapshotPlayback() {
        if (mSurfaceView.isConnected() && mSurfaceView.isPlaying()) {
            mDeviceChannelsManager.takeSnapshot(mSurfaceView);
            flashView();
        } else {
            Toast.makeText(this, "O vídeo precisa estar em andamento para que a foto seja tirada!", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(this, "O vídeo precisa estar em andamento para que a gravação seja iniciada!", Toast.LENGTH_SHORT).show();
        }
    }

    private void downloadFile() {
        byte[] file = G.ObjToBytes(mFileData);
//        String path = Environment.getExternalStorageDirectory().getPath() + "/Pictures/Giga Monitor/" + Utils.currentDateTime() + ".jpg";
        String path = Environment.getExternalStorageDirectory().getPath() + "/Movies/Giga Monitor/" + Utils.currentDateTime() + ".mp4";
        mDeviceManager.downloadFile(mDevice, file, path);
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
        mSeekBar.setProgress(currentProgress);
    }

    public void resume() {
        mDeviceChannelsManager.onResume(mSurfaceView);

        String text = getResources().getString(R.string.label_playing);

        updateStatusTextView(text);

        ivPlayPause.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_playback));
    }

    public void stop() {
        if (mSurfaceView.isREC()) {
            recordPlayback();
        }
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
        if (mSurfaceView.isREC()) {
            Toast.makeText(this, "O vídeo está sendo gravado. Pare a gravação para parar o vídeo!", Toast.LENGTH_SHORT).show();
        } else {
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

    private void flashView() {
        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(mSurfaceView, "alpha", 1f, .3f);
        fadeOut.setDuration(100);
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(mSurfaceView, "alpha", .3f, 1f);
        fadeIn.setDuration(100);

        final AnimatorSet mAnimationSet = new AnimatorSet();

        mAnimationSet.play(fadeIn).after(fadeOut);

        mAnimationSet.start();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Log.e(TAG, "Landscape");
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            changeVisibility(View.GONE);
            getSupportActionBar().hide();
        } else {
            Log.e(TAG, "Portrait");
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
            changeVisibility(View.VISIBLE);
            getSupportActionBar().show();
        }
        setLayoutSize();

        super.onConfigurationChanged(newConfig);
    }

    private void changeVisibility(int visibility) {
        playbackControls.setVisibility(visibility);
        playbackStatus.setVisibility(visibility);
    }

    private void setLayoutSize() {
        DisplayMetrics displayMetrics = Resources.getSystem().getDisplayMetrics();
        int viewWidth = displayMetrics.widthPixels;
        int viewHeight;

        if(displayMetrics.widthPixels%2 != 0)
            viewWidth -= 1;

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            viewHeight = displayMetrics.heightPixels;
            playbackLayout.setPadding(0, 0, 0, 0);
        } else {
            viewHeight = ((displayMetrics.heightPixels / 3)+10);
            playbackLayout.setPadding(16, 16, 16, 16);
        }
        final int width = viewWidth;
        final int height = viewHeight;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    mSurfaceView.getLayoutParams().height = (height / 22) * 20;
                    mSeekBar.getLayoutParams().height = (height / 22) * 2;
                } else {
                    mSurfaceView.getLayoutParams().height = surfaceViewHeight;
                    mSeekBar.getLayoutParams().height = seekBarHeight;
                }
                mSurfaceView.getLayoutParams().width = width;
                setSeekBarTextViewPosition();
                seekBarTextView.setY(seekBarTextPosition);
                mDeviceChannelsManager.resetScale();
            }
        });
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
            case R.id.download:
                downloadFile();
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

    private ArrayList<FileData> infoToArray(H264_DVR_FILE_DATA files[]){
        ArrayList<FileData> playbacks = new ArrayList<FileData>();
        for (H264_DVR_FILE_DATA file : files) {
            FileData funFileData = new FileData(file, null);
            playbacks.add(funFileData);
        }
        return playbacks;
    }

    private void requestDeviceSearchPicture() {
//        String path = Environment.getExternalStorageDirectory().getPath() + "/Pictures/Giga Monitor/" + Utils.currentDateTime() + ".jpg";
//        H264_DVR_FILE_DATA info = mFileData.getFileData();
//        OPCompressPic opCompressPic = new OPCompressPic();
//        opCompressPic.setPicName("Thumbnail");
//        opCompressPic.setHeight(100);
//        opCompressPic.setWidth(160);
//        opCompressPic.setIsGeo(1);
//        int result = FunSDK.DevSearchPicture(mDeviceManager.getHandler(),                  //获取缩略图该接口只提供运动相机使用 A interface só está disponível para câmeras de movimento
//                mDevice.connectionString,
//                COMPRESS_PICTURE_REQ, 50000, 2000,
//                opCompressPic.getSendMsg().getBytes(),
//                20, -1,
//                path, 145);
//        return (result == 0);
        H264_DVR_FINDINFO info = new H264_DVR_FINDINFO();

        info.st_0_nChannelN0 = mFileData.getFileData().st_0_ch;
        info.st_1_nFileType = SDKCONST.PicFileType.PIC_KEY;
        Date beginDate = mFileData.getBeginDate();
        Calendar beginCalendar = Calendar.getInstance();
        beginCalendar.setTime(beginDate);
        Date finalDate = mFileData.getEndDate();
        Calendar endCalendar = Calendar.getInstance();
        endCalendar.setTime(finalDate);
        info.st_2_startTime.st_0_dwYear = beginCalendar.get(Calendar.YEAR);
        info.st_2_startTime.st_1_dwMonth = beginCalendar.get(Calendar.MONTH) + 1;
        info.st_2_startTime.st_2_dwDay = beginCalendar.get(Calendar.DAY_OF_MONTH);
        info.st_2_startTime.st_3_dwHour = beginCalendar.get(Calendar.HOUR_OF_DAY);
        info.st_2_startTime.st_4_dwMinute = beginCalendar.get(Calendar.MINUTE);
        info.st_2_startTime.st_5_dwSecond = 0;
        info.st_3_endTime.st_0_dwYear = endCalendar.get(Calendar.YEAR);
        info.st_3_endTime.st_1_dwMonth = endCalendar.get(Calendar.MONTH) + 1;
        info.st_3_endTime.st_2_dwDay = endCalendar.get(Calendar.DAY_OF_MONTH);
        info.st_3_endTime.st_3_dwHour = endCalendar.get(Calendar.HOUR_OF_DAY);
        info.st_3_endTime.st_4_dwMinute = endCalendar.get(Calendar.MINUTE);
        info.st_3_endTime.st_5_dwSecond = 59;
        info.st_6_StreamType = 2;

        mDeviceManager.findThumbnailList(mDevice, info, new PlaybackSearchListener() {
            @Override
            public void onEmptyListFound() {
                Log.e("ThumbnailList", "Empty");
            }

            @Override
            public void onFindList(H264_DVR_FILE_DATA[] files) {
                thumbnailsList = infoToArray(files);
                String numberOfFiles = String.valueOf(thumbnailsList.size());
                Log.e("ThumbnailList", numberOfFiles);
            }
        });
    }
}
