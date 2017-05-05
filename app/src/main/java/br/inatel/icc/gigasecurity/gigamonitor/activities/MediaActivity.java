package br.inatel.icc.gigasecurity.gigamonitor.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.GridView;
import android.widget.ImageView;

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
        if(getIntent().getExtras() != null)
            ivImageSelected = getIntent().getExtras().getBoolean("imageSelected");

        mAdapter = new MediaGridAdapter(MediaActivity.this);

        initComponents();

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
        if(ivImageSelected){
            ivVideo.setImageDrawable(getResources().getDrawable(R.drawable.ic_video_off));
            ivImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_camera_on));
            mAdapter.changeGridMode(true);
            mAdapter.notifyDataSetChanged();
        } else{
            ivVideo.setImageDrawable(getResources().getDrawable(R.drawable.ic_video_on));
            ivImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_camera_off));
            mAdapter.changeGridMode(false);
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getMenuInflater().inflate(R.menu.simple_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }



}
