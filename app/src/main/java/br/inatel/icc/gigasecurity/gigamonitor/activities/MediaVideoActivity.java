package br.inatel.icc.gigasecurity.gigamonitor.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.MediaController;
import android.widget.VideoView;

import com.video.opengl.GLSurfaceView20;

import br.inatel.icc.gigasecurity.gigamonitor.R;

/**
 * Created by zappts on 10/08/17.
 */

public class MediaVideoActivity extends AppCompatActivity {

    Context mContext;
    VideoView mVideoView;
    String videoPath;

    public MediaVideoActivity() {
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();
        videoPath = (String) extras.getSerializable("mediaPath");

        setContentView(R.layout.media_video);

        mVideoView = (VideoView) findViewById(R.id.media_video_view);
        mVideoView.setVideoPath(videoPath);
        mVideoView.setMediaController(new MediaController(this));
        mVideoView.requestFocus();
        mVideoView.start();

    }
}
