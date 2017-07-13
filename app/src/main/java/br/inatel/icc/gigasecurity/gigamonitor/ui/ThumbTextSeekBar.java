package br.inatel.icc.gigasecurity.gigamonitor.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import br.inatel.icc.gigasecurity.gigamonitor.R;

/**
 * Created by zappts on 13/07/17.
 */

public class ThumbTextSeekBar extends LinearLayout {

    public ThumbTextView tvThumb;
    public SeekBar seekBar;
    private SeekBar.OnSeekBarChangeListener onSeekBarChangeListener;

    public ThumbTextSeekBar(Context context) {
        super(context);
        init();
    }

    public ThumbTextSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.thumbnail_view, this);
        setOrientation(LinearLayout.VERTICAL);
        tvThumb = (ThumbTextView) findViewById(R.id.thumbnail_text_view);
        seekBar = (SeekBar) findViewById(R.id.seek_bar_playback);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (onSeekBarChangeListener != null)
                    onSeekBarChangeListener.onStopTrackingTouch(seekBar);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (onSeekBarChangeListener != null)
                    onSeekBarChangeListener.onStartTrackingTouch(seekBar);
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (onSeekBarChangeListener != null)
                    onSeekBarChangeListener.onProgressChanged(seekBar, progress, fromUser);
                tvThumb.attachToSeekBar(seekBar);
            }
        });

    }

    public void setOnSeekBarChangeListener(SeekBar.OnSeekBarChangeListener l) {
        this.onSeekBarChangeListener = l;
    }

    public void setThumbText(String text) {
        tvThumb.setText(text);
    }

    public void setProgress(int progress) {
        if (progress == seekBar.getProgress() && progress == 0) {
            seekBar.setProgress(1);
            seekBar.setProgress(0);
        } else {
            seekBar.setProgress(progress);
        }
    }

    public void setMax(int time) {
        seekBar.setMax(time);
    }

    public String getProgress() {
        String progress = String.valueOf(seekBar.getProgress());
        return progress;
    }

}
