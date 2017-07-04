package br.inatel.icc.gigasecurity.gigamonitor.ui;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import br.inatel.icc.gigasecurity.gigamonitor.R;
import br.inatel.icc.gigasecurity.gigamonitor.activities.DeviceListActivity;
import br.inatel.icc.gigasecurity.gigamonitor.core.DeviceManager;
import br.inatel.icc.gigasecurity.gigamonitor.model.ChannelsManager;
import br.inatel.icc.gigasecurity.gigamonitor.model.DeviceChannelsManager;

/**
 * Created by zappts on 3/24/17.
 */

public class OverlayMenu extends RelativeLayout {
    private Context mContext;
    public SurfaceViewComponent surfaceViewComponent;
    public ImageView ivHQ, ivPlayPause, ivSnapshot, ivSnapvideo, ivFavorite, ivSendAudio, ivReceiveAudio, ivPTZ;
    public TextView ivTitle;
    public DeviceManager mDeviceManager;
    public ChannelsManager deviceChannelsManager;
    public boolean isFavoriteMenu;

    public OverlayMenu(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public OverlayMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        init();
    }

    public OverlayMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        init();
    }

    public void setDeviceChannelsManager(ChannelsManager deviceChannelsManager) {
        this.deviceChannelsManager = deviceChannelsManager;
    }

    private void init(){
        inflate(getContext(), R.layout.overlay_menu_layout, this);

        ivHQ                 = (ImageView) findViewById(R.id.iv_hq);
        ivPTZ                = (ImageView) findViewById(R.id.iv_ptz);
        ivPlayPause          = (ImageView) findViewById(R.id.iv_play_pause);
        ivSnapshot           = (ImageView) findViewById(R.id.iv_snapshot);
        ivSnapvideo          = (ImageView) findViewById(R.id.iv_snapvideo);
        ivFavorite           = (ImageView) findViewById(R.id.iv_favorite_star);
        ivReceiveAudio       = (ImageView) findViewById(R.id.iv_enable_receive_audio);
        ivSendAudio          = (ImageView) findViewById(R.id.iv_enable_send_audio);
        ivTitle              = (TextView)  findViewById(R.id.tv_channel_title);

//        surfaceViewComponent = new SurfaceViewComponent(mContext, deviceChannelsManager);

        mDeviceManager = DeviceManager.getInstance();

        setClickListeners();
    }

    public void setSurfaceViewComponent(SurfaceViewComponent surfaceViewComponent){
        this.surfaceViewComponent = surfaceViewComponent;
        if(surfaceViewComponent.mChannelsManager.mDevice.getSerialNumber().equals("Favoritos"))
            isFavoriteMenu = true;
    }

    public void updateIcons(){

        ivPlayPause.setVisibility(VISIBLE);
        String title;
        if(deviceChannelsManager.mDevice.getSerialNumber().equals("Favoritos"))
            title = "";
        else
            title = "Canal " + (surfaceViewComponent.getMySurfaceViewChannelId() + 1);
        ivTitle.setText(title);
        if (surfaceViewComponent.isHD()) {
            ivHQ.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_hq_on));
        } else {
            ivHQ.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_hq_off));
        }

        if (surfaceViewComponent.isPTZEnabled()) {
            ivPTZ.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_ptz_on));
        } else {
            ivPTZ.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_ptz));
        }

        if(surfaceViewComponent.isConnected()) {
            if (surfaceViewComponent.isPlaying) {
                ivPlayPause.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_pause_white_48dp));
            } else {
                ivPlayPause.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_play_arrow_white_48dp));
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

        if(!surfaceViewComponent.isReceiveAudioEnabled && !surfaceViewComponent.isSendAudioEnabled){
            ivReceiveAudio.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_volume_mute_white_36dp));
        } else if(!surfaceViewComponent.isSendAudioEnabled){
            ivReceiveAudio.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_volume_up_white_36dp));
        } else{
            ivReceiveAudio.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_volume_off_white_36dp));
        }
    }

    private void setClickListeners(){
        ivPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (surfaceViewComponent.isConnected()) {
                    if (surfaceViewComponent.isPlaying) {
                        deviceChannelsManager.onPause(surfaceViewComponent);
                        ivPlayPause.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_play_arrow_white_48dp));
                    } else {
                        deviceChannelsManager.onResume(surfaceViewComponent);
                        ivPlayPause.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_pause_white_48dp));
                    }
                }
            }
        });

        ivHQ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(surfaceViewComponent.isREC){
                    Toast.makeText(mContext, "Finalize a gravação", Toast.LENGTH_SHORT).show();
                } else {
                    if (!surfaceViewComponent.isHD()) {
                        deviceChannelsManager.enableHD(surfaceViewComponent);
                        ivHQ.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_hq_on));
                    } else {
                        deviceChannelsManager.disableHD(surfaceViewComponent);
                        ivHQ.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_hq_off));
                    }
                }
            }
        });

        ivPTZ.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(surfaceViewComponent.isPTZEnabled()){
                    //disableptz
                    surfaceViewComponent.setPTZEnabled(false);
                    ivPTZ.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_ptz));
                } else {
                    //enableptz
                    surfaceViewComponent.setPTZEnabled(true);
                    ivPTZ.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_ptz_on));
                }

            }
        });

        ivSnapshot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deviceChannelsManager.takeSnapshot(surfaceViewComponent);
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
                    deviceChannelsManager.startRecord(surfaceViewComponent);
                    ivSnapvideo.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_snapvideo_on));
                } else {
                    surfaceViewComponent.setREC(false);
                    deviceChannelsManager.stopRecord(surfaceViewComponent);
                    ivSnapvideo.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_snapvideo));
                }
            }
        });

        ivFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(surfaceViewComponent.isFavorite()){
                    if(isFavoriteMenu) {
                        if(surfaceViewComponent.isREC()){
                            Toast.makeText(mContext, "Finalize a gravação", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        surfaceViewComponent.mChannelsManager.mRecyclerAdapter.openOverlayMenu(surfaceViewComponent);
                    }
                    mDeviceManager.removeFavorite(surfaceViewComponent);
                    ivFavorite.clearColorFilter();
                } else {
                    mDeviceManager.addFavorite(surfaceViewComponent);
                    ivFavorite.setColorFilter(Color.parseColor("#FFFF00"));
                }
            }
        });

        ivSendAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(surfaceViewComponent.isSendAudioEnabled){
                    deviceChannelsManager.disableSendAudio();
                    surfaceViewComponent.isSendAudioEnabled = false;
                    ivSendAudio.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_mic_off_white_36dp));

                    //reabilita recepção de audio
                    if(surfaceViewComponent.isReceiveAudioEnabled){
                        deviceChannelsManager.toggleReceiveAudio(surfaceViewComponent);
                    }
                    if(surfaceViewComponent.isReceiveAudioEnabled)
                        ivReceiveAudio.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_volume_up_white_36dp));
                    else
                        ivReceiveAudio.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_volume_mute_white_36dp));
                    ivReceiveAudio.setClickable(true);
                } else{
                    //desabilita recepção de audio para funcionar
                    if(surfaceViewComponent.isReceiveAudioEnabled){
                        deviceChannelsManager.toggleReceiveAudio(surfaceViewComponent);
                    }
                    ivReceiveAudio.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_volume_off_white_36dp));
                    ivReceiveAudio.setClickable(false);

                    deviceChannelsManager.enableSendAudio(surfaceViewComponent);
                    surfaceViewComponent.isSendAudioEnabled = true;
                    ivSendAudio.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_mic_white_36dp));
                }
            }
        });

        ivReceiveAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(surfaceViewComponent.isReceiveAudioEnabled){
                    surfaceViewComponent.isReceiveAudioEnabled = false;
                    ivReceiveAudio.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_volume_mute_white_36dp));
                }else {
                    surfaceViewComponent.isReceiveAudioEnabled = true;
                    ivReceiveAudio.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_volume_up_white_36dp));
                }
                deviceChannelsManager.toggleReceiveAudio(surfaceViewComponent);
            }
        });
    }

    private void startAnimationBlink(final SurfaceViewComponent svl) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final Animation mAnimationBlink = AnimationUtils.loadAnimation(mContext, R.anim.blink);
                ((DeviceListActivity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        svl.startAnimation(mAnimationBlink);
                    }
                });
            }
        }).start();
    }
}
