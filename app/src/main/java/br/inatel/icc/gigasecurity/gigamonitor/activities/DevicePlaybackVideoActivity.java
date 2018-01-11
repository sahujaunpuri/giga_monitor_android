package br.inatel.icc.gigasecurity.gigamonitor.activities;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.basic.G;
import com.lib.sdk.struct.H264_DVR_FILE_DATA;
import com.lib.sdk.struct.H264_DVR_FINDINFO;
import com.lib.sdk.struct.SDK_SYSTEM_TIME;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import br.inatel.icc.gigasecurity.gigamonitor.R;
import br.inatel.icc.gigasecurity.gigamonitor.core.DeviceManager;
import br.inatel.icc.gigasecurity.gigamonitor.listeners.DownloadPlaybackListener;
import br.inatel.icc.gigasecurity.gigamonitor.listeners.PlaybackListener;
import br.inatel.icc.gigasecurity.gigamonitor.listeners.PlaybackSearchListener;
import br.inatel.icc.gigasecurity.gigamonitor.model.Device;
import br.inatel.icc.gigasecurity.gigamonitor.model.DeviceChannelsManager;
import br.inatel.icc.gigasecurity.gigamonitor.model.FileData;
import br.inatel.icc.gigasecurity.gigamonitor.ui.SurfaceViewComponent;

public class DevicePlaybackVideoActivity extends ActionBarActivity {

    private SurfaceViewComponent mSurfaceView;
    private DeviceChannelsManager mDeviceChannelsManager;
    private LinearLayout playbackLayout;
    private LinearLayout playbackControls;
    private LinearLayout playbackStatus;
    private Menu menu;
    private SeekBar mSeekBar;
    private TextView seekBarTextView;
    private float seekBarTextPosition;
    private ImageView thumbnail, mImageViewDownload, mImageViewGallery;
    private ProgressBar mProgressBar;
    private String initialTimeVideo;
    private TextView initialTime, mStatusTextView, endTime, mTextViewBack;
    private ImageView ivPlayPause, ivStop, ivForward, ivBackward, mImageViewSnapshot, mImageViewRec;
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

    private ProgressDialog mProgressDialog;
    private String downloadPath;

    private PlaybackListener mPlaybackListener =
            new PlaybackListener() {
                @Override
                public void onComplete() {
                    Log.e("PlaybackListener", "Complete");
                    stopButtonClick();
                }

                @Override
                public void onChangeProgress(int progress) {
                    if (!mSurfaceView.isSeeking) {
                        advanceSeek(progress);
                        currentProgress = progress;
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
                    Log.e("Seek", "Completed");
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
                                ivPlayPause.setImageDrawable(getResources().getDrawable(R.drawable.pause_btn_style));
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
                    if (fromUser) {
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

//                            int val = (progress * (seekBar.getWidth() - 2 * seekBar.getThumbOffset())) / seekBar.getMax();
                            int val = (int)((float)(seekBar.getMeasuredWidth() / 2) - seekBarTextView.getWidth() / 2);
//                            seekBarTextView.setX(seekBar.getX() + val + seekBar.getThumbOffset() / 2);
                            seekBarTextView.setX(val);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        currentBar = progress;
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    if (mSurfaceView.isREC()) {
                        Toast.makeText(mActivity, "Pare a gravação para avançar!", Toast.LENGTH_SHORT).show();
                    } else {
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
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    final Animation animationFadeIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.abc_slide_out_top);
                    seekBarTextView.startAnimation(animationFadeIn);

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
                    setPlaybackProgress(currentBar);
                }
            };

    final DownloadPlaybackListener downloadListener = new DownloadPlaybackListener() {
        @Override
        public void onStartDownload(int fileSize) {
            mProgressDialog = new ProgressDialog(DevicePlaybackVideoActivity.this, ProgressDialog.THEME_HOLO_LIGHT);
            mProgressDialog.setMessage(getResources().getString(R.string.downloading));
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setProgressNumberFormat("%d/%d KB");
            String cancel = getResources().getString(R.string.cancel);
            mProgressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    mDeviceManager.cancelDownload();
                }
            });
            mProgressDialog.show();
        }

        @Override
        public void onProgressDownload(int currentProgress, int totalProgress) {
            mProgressDialog.setProgress(currentProgress/1000);
            mProgressDialog.setMax(totalProgress/1000);
        }

        @Override
        public void onFinishDownload() {
            mProgressDialog.dismiss();
            Toast.makeText(getApplicationContext(), "Download efetuado com sucesso", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancelDownload() {
            try {
                File file = new File(downloadPath);
                file.delete();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                mProgressDialog.dismiss();
            }
        }

        @Override
        public void onErrorDownload() {
            Toast.makeText(mActivity, getResources().getString(R.string.download_error), Toast.LENGTH_SHORT).show();
        }
    };

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

        mSurfaceView = mDeviceChannelsManager.surfaceViewComponents.get(0);

        // Find views
        playbackLayout = (LinearLayout) findViewById(R.id.playback_relative_layout);
        mSurfaceView = (SurfaceViewComponent) findViewById(R.id.surface_view_test_2);
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
        mTextViewBack = (TextView) findViewById(R.id.text_view_back);
        mImageViewDownload = (ImageView) findViewById(R.id.image_view_download);
        mImageViewGallery = (ImageView) findViewById(R.id.image_view_gallery);
        ivPlayPause.setImageDrawable(getResources().getDrawable(R.drawable.play_btn_style));
        mImageViewSnapshot = (ImageView) findViewById(R.id.image_view_snapshot);
        mImageViewRec = (ImageView) findViewById(R.id.image_view_rec);

        setSeekBarTextViewPosition();

        seekBarTextView = new TextView(this);//TextView) findViewById(R.id.thumbnail_text_view2);
        seekBarTextView.setBackgroundResource(R.drawable.playback_time_box);
        seekBarTextView.setY(seekBarTextPosition - 30);
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
                if (mSurfaceView.isREC()) {
                    Toast.makeText(mActivity, "Pare a gravação antes de parar o vídeo", Toast.LENGTH_SHORT).show();
                } else {
                    stopButtonClick();
                }
            }
        });

        ivForward.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (!mSurfaceView.isREC()) {
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
                } else {
                    Toast.makeText(mActivity, mActivity.getResources().getString(R.string.playback_being_recorded), Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });

        ivBackward.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (!mSurfaceView.isREC()) {
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
                } else {
                    Toast.makeText(mActivity, mActivity.getResources().getString(R.string.playback_being_recorded), Toast.LENGTH_SHORT).show();
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
        mSeekBar.setEnabled(true);
        mSeekBar.setMax((int) mFileData.getTotalTime());
        mStartSecond = (int) mFileData.getStartTime();
        mSeekBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return progressBarDisabled;
            }
        });

//        requestDeviceSearchPicture();
//        thumbnailView.setVisibility(View.INVISIBLE);

        mTextViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mSurfaceView.isREC()) {
                    Toast.makeText(mActivity, getResources().getString(R.string.playback_being_recorded), Toast.LENGTH_SHORT).show();
                } else {
                    finish();
                }
            }
        });

        mImageViewDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                downloadFile();
            }
        });

        mImageViewGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mSurfaceView.isREC()) {
                    Toast.makeText(mActivity, getResources().getString(R.string.playback_being_recorded), Toast.LENGTH_SHORT).show();
                } else {
                    startMediasActivity();
                }
            }
        });

        mImageViewSnapshot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                snapshotPlayback();
            }
        });

        mImageViewRec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recordPlayback();
            }
        });

        getSupportActionBar().hide();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void startMediaActivity() {
        Intent i = new Intent(this, MediaActivity.class);

        startActivity(i);
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
            Toast.makeText(this, getResources().getString(R.string.playback_running), Toast.LENGTH_SHORT).show();
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
            mSurfaceView.isSeeking = false;
            mSeekBar.setProgress(0);
            currentProgress = 0;
            initialTime.setText(initialTimeVideo);
            stop();
        }
    }

    private void snapshotPlayback() {
        if (mSurfaceView.isConnected() && mSurfaceView.isPlaying()) {
            String playbackName = actualTime();
            mDeviceChannelsManager.takeSnapshot(mSurfaceView, playbackName);
            flashView();
        } else {
            Toast.makeText(mActivity, getResources().getString(R.string.playback_running), Toast.LENGTH_SHORT).show();
        }
    }

    private void recordPlayback() {
        if (mSurfaceView.isConnected() && mSurfaceView.isPlaying()) {
            if (mSurfaceView.isREC()) {
                stopRecordingPlayback();
            } else {
                startRecordingPlayback();
            }
        } else {
            Toast.makeText(mActivity, getResources().getString(R.string.playback_running), Toast.LENGTH_SHORT).show();
        }
    }

    private void startRecordingPlayback() {
        String playbackRecordName = actualTime();
        mSurfaceView.setREC(true);
        mDeviceChannelsManager.startRecord(mSurfaceView, playbackRecordName);
        changeToRedRecordIcon(true);
        mSeekBar.setEnabled(false);
    }

    private void stopRecordingPlayback() {
        mSurfaceView.setREC(false);
        mDeviceChannelsManager.stopRecord(mSurfaceView);
        changeToRedRecordIcon(false);
        mSeekBar.setEnabled(true);
    }

    private void changeToRedRecordIcon(boolean icon) {
        MenuItem recordIcon = menu.findItem(R.id.playback_record);
        if (icon) {
            recordIcon.setIcon(R.mipmap.red_record);
        } else {
            recordIcon.setIcon(R.drawable.record);
        }
    }

    private String actualTime() {
        String playbackDate = String.format("%s-%s-%s", mFileData.getBeginDateStr().substring(8),
                mFileData.getBeginDateStr().substring(5, 7),
                mFileData.getBeginDateStr().substring(0, 4));
        String playbackTime = String.format("%s_%s_%s", initialTime.getText().toString().substring(0, 2),
                initialTime.getText().toString().substring(3, 5),
                initialTime.getText().toString().substring(6));
        String playbackName = playbackDate + " " + playbackTime;
        return playbackName;
    }

    private void downloadFile() {
        final AlertDialog.Builder alert = new AlertDialog.Builder(this, R.style.Base_Theme_AppCompat_Light_Dialog);
        alert.setTitle("Download");
        String message = String.valueOf(mFileData.getFileData().st_1_size);
        alert.setMessage("Tem certeza que deseja baixar um arquivo de " + message + " KB?");
        alert.setPositiveButton("Sim", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                byte[] file = G.ObjToBytes(mFileData.getFileData());
                String fileName = actualTime();
                downloadPath = Environment.getExternalStorageDirectory().getPath() + "/Movies/Giga Monitor/" + fileName + ".mp4";
                mDeviceManager.downloadFile(mDevice, file, downloadPath, downloadListener);
            }
        });
        alert.setNegativeButton("Não", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alert.show();
    }

    private void startMediasActivity() {
        if (mSurfaceView.isConnected() && mSurfaceView.isPlaying()) {
            stopButtonClick();
        }
        Intent intent = new Intent(this, MediaActivity.class);
        startActivity(intent);
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

        ivPlayPause.setImageDrawable(getResources().getDrawable(R.drawable.pause_btn_style));
    }

    public void stop() {
        changeToRedRecordIcon(false);
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
                ivPlayPause.setImageDrawable(getResources().getDrawable(R.drawable.play_btn_style));
            }
        });

        if (!mSeekBar.isEnabled()) {
            mSeekBar.setEnabled(true);
        }
    }

    public void pause() {
        if (mSurfaceView.isREC()) {
            Toast.makeText(this, getResources().getString(R.string.playback_stop_video_when_recording), Toast.LENGTH_SHORT).show();
        } else {
            mDeviceChannelsManager.onPause(mSurfaceView);

            String text = getResources().getString(R.string.label_paused);
            updateStatusTextView(text);

            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ivPlayPause.setImageDrawable(getResources().getDrawable(R.drawable.play_btn_style));
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
                if (mSurfaceView.isREC()) {
                    Toast.makeText(mActivity, getResources().getString(R.string.playback_being_recorded), Toast.LENGTH_SHORT).show();
                } else {
                    finish();
                }
                return true;
            case R.id.medias:
                if (mSurfaceView.isREC()) {
                    Toast.makeText(mActivity, getResources().getString(R.string.playback_being_recorded), Toast.LENGTH_SHORT).show();
                } else {
                    startMediasActivity();
                }
                return true;
            case R.id.playback_snapshot:
                snapshotPlayback();
                return true;
            case R.id.playback_record:
                recordPlayback();
                return true;
            case R.id.playback_download:
                downloadFile();
                return true;
            default:
                return false;
        }
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
        String path = Environment.getExternalStorageDirectory().getPath() + "/Pictures/Giga Monitor/Videos";
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

//        info.st_0_nChannelN0 = mFileData.getFileData().st_0_ch;
//        info.st_1_nFileType = SDKCONST.PicFileType.PIC_KEY;
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
//        info.st_6_StreamType = 2;

        mDeviceManager.findThumbnailList(mDevice, mFileData, info, new PlaybackSearchListener() {
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
