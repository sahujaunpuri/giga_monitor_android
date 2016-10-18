package br.inatel.icc.gigasecurity.gigamonitor.activities;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.Window;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.xm.MyConfig;
import com.xm.NetSdk;

import java.io.File;
import java.util.ArrayList;

import br.inatel.icc.gigasecurity.gigamonitor.R;
import br.inatel.icc.gigasecurity.gigamonitor.adapters.MediaGridAdapter;

public class MediaActivity extends ActionBarActivity {

    public static GridView gvMedia;
    public static MediaGridAdapter mAdapter;
    public static ImageView ivImage, ivVideo;
    public boolean ivImageSelected = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media);

        initComponents();

        mAdapter = new MediaGridAdapter(MediaActivity.this);

        gvMedia.setAdapter(mAdapter);

        gvMedia.setFriction(ViewConfiguration.getScrollFriction() * 10);

        ivImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!ivImageSelected) {
                    mAdapter.changeGridMode(true);
                    mAdapter.notifyDataSetChanged();
                    ivVideo.setImageDrawable(getResources().getDrawable(R.drawable.ic_video_off));
                    ivImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_camera_on));
                    ivImageSelected = true;
                }
            }
        });


        ivVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ivImageSelected) {
                    mAdapter.changeGridMode(false);
                    mAdapter.notifyDataSetChanged();
                    ivVideo.setImageDrawable(getResources().getDrawable(R.drawable.ic_video_on));
                    ivImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_camera_off));
                    ivImageSelected = false;
                }
            }
        });
    }

    private void initComponents() {
        gvMedia = (GridView) findViewById(R.id.grid_view_media);
        ivImage = (ImageView) findViewById(R.id.iv_image);
        ivVideo = (ImageView) findViewById(R.id.iv_video);
    }



}
