package br.inatel.icc.gigasecurity.gigamonitor.ui;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import br.inatel.icc.gigasecurity.gigamonitor.R;
import br.inatel.icc.gigasecurity.gigamonitor.activities.DeviceListActivity;
import br.inatel.icc.gigasecurity.gigamonitor.core.DeviceManager;
import br.inatel.icc.gigasecurity.gigamonitor.model.SurfaceViewComponent;

/**
 * Created by zappts on 3/24/17.
 */

public class OverlayMenu extends RelativeLayout {
    private Context mContext;
    public SurfaceViewComponent surfaceViewComponent;
    public ImageView ivHQ, ivPlayPause, ivSnapshot, ivSnapvideo, ivFavorite, ivSendAudio, ivReceiveAudio;
    public TextView ivTitle;
    public DeviceManager mDeviceManager;

    public OverlayMenu(Context context, SurfaceViewComponent surfaceViewComponent) {
        super(context);
        mContext = context;
        this.surfaceViewComponent = surfaceViewComponent;
        init();
    }

    public OverlayMenu(Context context, AttributeSet attrs, SurfaceViewComponent surfaceViewComponent) {
        super(context, attrs);
        this.mContext = context;
        this.surfaceViewComponent = surfaceViewComponent;
        init();
    }

    public OverlayMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        init();
    }

    private void init(){
        inflate(getContext(), R.layout.overlay_menu_layout, this);

        ivHQ                 = (ImageView) findViewById(R.id.iv_hq);
        ivPlayPause          = (ImageView) findViewById(R.id.iv_play_pause);
        ivSnapshot           = (ImageView) findViewById(R.id.iv_snapshot);
        ivSnapvideo          = (ImageView) findViewById(R.id.iv_snapvideo);
        ivFavorite           = (ImageView) findViewById(R.id.iv_favorite_star);
        ivReceiveAudio       = (ImageView) findViewById(R.id.iv_enable_receive_audio);
        ivSendAudio          = (ImageView) findViewById(R.id.iv_enable_send_audio);
        ivTitle              = (TextView)  findViewById(R.id.tv_channel_title);

        mDeviceManager = DeviceManager.getInstance();
        updateIcons();

        setClickListeners();
    }

    public void updateIcons(){

        ivPlayPause.setVisibility(VISIBLE);

        String title = "Canal " + surfaceViewComponent.getMySurfaceViewChannelId();
        ivTitle.setText(title);
        if (surfaceViewComponent.isHD()) {
            ivHQ.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_hq_on));
        } else {
            ivHQ.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_hq_off));
        }

        if(surfaceViewComponent.isConnected) {
            if (surfaceViewComponent.isPlaying) {
                ivPlayPause.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_pause_white_36dp));
            } else {
                ivPlayPause.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_play_arrow_white_36dp));
            }
        } else{
            ivPlayPause.setVisibility(INVISIBLE);
        }

        if (surfaceViewComponent.isREC()) {
            ivSnapvideo.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_snapvideo_on));
        } else {
            ivSnapvideo.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_snapvideo));
        }

        if(!surfaceViewComponent.isFavorite()){
            ivFavorite.clearColorFilter();
        } else {
            ivFavorite.setColorFilter(Color.parseColor("#FFFF00"));
        }

        if(!surfaceViewComponent.isSendAudioEnabled){
            ivSendAudio.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_mic_off_white_36dp));
        } else{
            ivSendAudio.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_mic_white_36dp));
        }

        if(!surfaceViewComponent.isReceiveAudioEnabled){
            ivReceiveAudio.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_volume_mute_white_36dp));
        }else {
            ivReceiveAudio.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_volume_up_white_36dp));
        }
    }

    private void setClickListeners(){
        ivPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (surfaceViewComponent.isConnected) {
                    if (surfaceViewComponent.isPlaying) {
                        surfaceViewComponent.onPause();
                        ivPlayPause.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_play_arrow_white_36dp));
                    } else {
                        surfaceViewComponent.onResume();
                        ivPlayPause.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_pause_white_36dp));
                    }
                }
            }
        });

        ivHQ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(surfaceViewComponent.isREC){
                    Toast.makeText(mContext, "Finalize a gravação.", Toast.LENGTH_SHORT).show();
                } else {
                    surfaceViewComponent.progressBar.setVisibility(View.VISIBLE);
                    if (!surfaceViewComponent.isHD()) {
                        surfaceViewComponent.setStreamType(0);
                        ivHQ.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_hq_on));
                    } else {
                        surfaceViewComponent.setStreamType(1);
                        ivHQ.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_hq_off));
                    }
                    surfaceViewComponent.restartVideo();
                }
            }
        });

        ivSnapshot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                surfaceViewComponent.takeSnapshot();
                startAnimationBlink(surfaceViewComponent);
            }
        });

        ivSnapvideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!surfaceViewComponent.isPlaying) {
                    Toast.makeText(mContext, "Vídeo pausado", Toast.LENGTH_SHORT).show();
                } else if(!surfaceViewComponent.isREC()){
                    surfaceViewComponent.setREC(true);
                    mDeviceManager.channelOnRec = true;
                    surfaceViewComponent.startRecord();
                    ivSnapvideo.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_snapvideo_on));
                } else {
                    surfaceViewComponent.setREC(false);
                    surfaceViewComponent.stopRecord();
                    ivSnapvideo.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_snapvideo));
                }
            }
        });

        ivFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(surfaceViewComponent.isFavorite()){
                    mDeviceManager.removeFavorite(mContext, surfaceViewComponent);
                    ivFavorite.clearColorFilter();
                } else {
                    mDeviceManager.addFavorite(mContext, surfaceViewComponent);
                    ivFavorite.setColorFilter(Color.parseColor("#FFFF00"));
                }
            }
        });

        ivSendAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(surfaceViewComponent.isSendAudioEnabled){
                    //disable
                    surfaceViewComponent.isSendAudioEnabled = false;
                    ivSendAudio.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_mic_off_white_36dp));
                } else{
                    //enable
                    surfaceViewComponent.isSendAudioEnabled = true;
                    ivSendAudio.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_mic_white_36dp));
                }
            }
        });

        ivReceiveAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(surfaceViewComponent.isReceiveAudioEnabled){
                    //disable
                    surfaceViewComponent.isReceiveAudioEnabled = false;
                    ivReceiveAudio.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_volume_mute_white_36dp));
                }else {
                    //enable
                    surfaceViewComponent.isReceiveAudioEnabled = true;
                    ivReceiveAudio.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_volume_up_white_36dp));
                }
            }
        });
    }

    private void startAnimationBlink(final SurfaceViewComponent svc) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final Animation mAnimationBlink = AnimationUtils.loadAnimation(mContext, R.anim.blink);
                ((DeviceListActivity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        svc.startAnimation(mAnimationBlink);
                    }
                });
            }
        }).start();
    }
}
