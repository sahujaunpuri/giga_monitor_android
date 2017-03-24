package br.inatel.icc.gigasecurity.gigamonitor.ui;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import br.inatel.icc.gigasecurity.gigamonitor.R;
import br.inatel.icc.gigasecurity.gigamonitor.model.SurfaceViewComponent;

/**
 * Created by zappts on 3/24/17.
 */

public class OverlayMenu extends RelativeLayout {
    private Context mContext;
    public SurfaceViewComponent surfaceViewComponent;
    public ImageView ivHQ, ivPlayPause, ivSnapshot, ivSnapvideo, ivFavorite, ivSendAudio, ivReceiveAudio;

    public OverlayMenu(Context context, SurfaceViewComponent surfaceViewComponent) {
        super(context);
        mContext = context;
        this.surfaceViewComponent = surfaceViewComponent;
        init();
    }

    public OverlayMenu(Context context, AttributeSet attrs, Context mContext) {
        super(context, attrs);
        this.mContext = mContext;
    }

    public OverlayMenu(Context context, AttributeSet attrs, int defStyleAttr, Context mContext) {
        super(context, attrs, defStyleAttr);
        this.mContext = mContext;
    }

    private void init(){
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.overlay_menu_layout, this, true);

        ivHQ                 = (ImageView) this.findViewById(R.id.iv_hq);
        ivPlayPause          = (ImageView) this.findViewById(R.id.iv_play_pause);
        ivSnapshot           = (ImageView) findViewById(R.id.iv_snapshot);
        ivSnapvideo          = (ImageView) findViewById(R.id.iv_snapvideo);
        ivFavorite           = (ImageView) findViewById(R.id.iv_favorite_star);
        ivReceiveAudio       = (ImageView) findViewById(R.id.iv_enable_receive_audio);
        ivSendAudio          = (ImageView) findViewById(R.id.iv_enable_send_audio);

        ivPlayPause.setVisibility(VISIBLE);


        if (surfaceViewComponent.isHD()) {
            ivHQ.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_hq_on));
        } else {
            ivHQ.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_hq_off));
        }

        if (surfaceViewComponent.isPlaying) {
            ivPlayPause.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_pause_white_36dp));
        } else {
            ivPlayPause.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_play_arrow_white_36dp));
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
}
