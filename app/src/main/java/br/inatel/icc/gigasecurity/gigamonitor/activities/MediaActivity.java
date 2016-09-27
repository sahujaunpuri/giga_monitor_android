package br.inatel.icc.gigasecurity.gigamonitor.activities;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;

import java.io.File;
import java.util.ArrayList;

import br.inatel.icc.gigasecurity.gigamonitor.R;
import br.inatel.icc.gigasecurity.gigamonitor.adapters.MediaGridAdapter;

public class MediaActivity extends ActionBarActivity {

    public static GridView gvMedia;
    public static MediaGridAdapter mAdapter;
    public static ImageView ivImage, ivVideo;
    public static File[] allImageFiles, allFiles;
    public static ArrayList<File> allVideoFiles = new ArrayList<>();
    public static File files;
    public boolean ivImageSelected = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media);

        initComponents();

        files = new File("/sdcard/Pictures/Giga Monitor");

        allFiles = files.listFiles();

        allImageFiles = files.listFiles();

        if (allImageFiles != null) {
            mAdapter = new MediaGridAdapter(MediaActivity.this, allImageFiles, null);

            gvMedia.setAdapter(mAdapter);
        }



        ivImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!ivImageSelected) {
                    files = new File("/sdcard/Pictures/Giga Monitor");

                    allImageFiles = files.listFiles();

                    if (allImageFiles != null) {
                        mAdapter = new MediaGridAdapter(MediaActivity.this, allImageFiles, null);

                        gvMedia.setAdapter(mAdapter);
                    }

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
                    allVideoFiles.clear();

                    files = new File("/sdcard/Movies/Giga Monitor");

                    allFiles = files.listFiles();

                    if (allFiles != null) {

                        for(int i =0; i<allFiles.length; i++) {
                            if(allFiles[i].getName().contains(".mp4")) {
                                allVideoFiles.add(allFiles[i]);
                            }
                        }

                        mAdapter = new MediaGridAdapter(MediaActivity.this, null, allVideoFiles);

                        gvMedia.setAdapter(mAdapter);
                    }

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
