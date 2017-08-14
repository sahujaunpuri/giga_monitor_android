package br.inatel.icc.gigasecurity.gigamonitor.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.Toast;

import java.io.File;

import br.inatel.icc.gigasecurity.gigamonitor.R;
import br.inatel.icc.gigasecurity.gigamonitor.adapters.MediaGridAdapter;
import br.inatel.icc.gigasecurity.gigamonitor.listeners.MediaListener;

public class MediaActivity extends AppCompatActivity {

    public static GridView gvMedia;
    public static MediaGridAdapter mAdapter;
    public static ImageView ivImage, ivVideo;
    public boolean ivImageSelected = true;

    private String SELECT_TITLE_BUTTON = "Selecionar";
    private String CANCEL_TITLE_BUTTON = "Cancelar";
    private Menu menu;
    private MenuItem menuItemSelect;
    private MenuItem menuItemTrash;
    private MediaListener mMediaListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media);
        if(getIntent().getExtras() != null) {
            ivImageSelected = getIntent().getExtras().getBoolean("imageSelected");
        }

        mMediaListener = new MediaListener() {
            @Override
            public void onStartVideoActivity(final String path, final int position) {
                Intent i = new Intent(MediaActivity.this, MediaVideoActivity.class);
                Bundle extras = new Bundle();
                extras.putSerializable("mediaPath", path);
                extras.putSerializable("position", position);
                i.putExtras(extras);
                startActivityForResult(i, 1);
            }
        };

        mAdapter = new MediaGridAdapter(MediaActivity.this, mMediaListener);

        initComponents();

        gvMedia.setAdapter(mAdapter);

        gvMedia.setFriction(ViewConfiguration.getScrollFriction() * 10);

        gvMedia.setVerticalScrollBarEnabled(false);

        gvMedia.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mAdapter.selectItems) {
                    return true;
                } else {
                    return false;
                }
            }
        });

        ivImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!ivImageSelected) {
                    mAdapter.changeGridMode(true);
                    mAdapter.notifyDataSetChanged();
                    ivVideo.setImageDrawable(getResources().getDrawable(R.drawable.ic_video_off));
                    ivImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_camera_on));
                    ivImageSelected = true;
                    verifyButtonsVisibility();
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
                    verifyButtonsVisibility();
                }
            }
        });
    }

    private void verifyButtonsVisibility() {
        if (mAdapter.selectItems) {
            changeButtonsVisibility(false);
        }
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

    private void deleteMedias() {
        if (mAdapter.getNumberOfMediasToDelete() > 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getResources().getString(R.string.media_delete_various))
                    .setItems(new CharSequence[]{"Sim", "Cancelar"},
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (which == 0) {
                                        deselectAllViews();
                                        mAdapter.deleteSelectedMedias();
                                        menuItemTrash.setVisible(false);
                                        menuItemSelect.setTitle(SELECT_TITLE_BUTTON);
                                    }/* else {
                                        deselectAllViews();
                                    }*/
                                }
                            });
            builder.show();
        } else {
            Toast.makeText(getApplicationContext(), "Não há itens selecionados!", Toast.LENGTH_SHORT).show();
        }
    }

    private void changeButtonsVisibility(boolean visible) {
        if (visible) {
            menuItemSelect.setTitle(CANCEL_TITLE_BUTTON);
            menuItemTrash.setVisible(true);
            mAdapter.selectItems = true;
        } else {
            menuItemSelect.setTitle(SELECT_TITLE_BUTTON);
            menuItemTrash.setVisible(false);
            mAdapter.selectItems = false;
            deselectAllViews();
            mAdapter.clearToDeleteArray();
        }
    }

    private void deselectAllViews() {
        for (int i=0; i<gvMedia.getCount(); i++) {
            View child = gvMedia.getChildAt(i);
            if (child != null) {
                child.setBackgroundResource(R.drawable.transparent_media_background);
            }
        }
    }

    public void showUnsupportedVideoOptions(final int position, final String path) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        String text = getResources().getString(R.string.media_failed_message);
        String labelDelete = getResources().getString(R.string.action_delete);

        builder.setMessage(text)
                .setPositiveButton(labelDelete, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String filePath = path.substring(7);
                        filePath = filePath.replace("%20", " ");
                        filePath = filePath.replace("%", " ");

                        File file = new File(filePath);
                        file.delete();
                        mAdapter.removeItem(position);
                        Toast.makeText(getApplicationContext(), "Arquivo deletado", Toast.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton(R.string.transfer, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        transferFile(path);
                        mAdapter.removeItem(position);
                        Toast.makeText(getApplicationContext(), "Arquivo transferido com sucesso", Toast.LENGTH_LONG).show();
                    }
                });

        builder.show();
    }

    private void transferFile(final String path) {
        String filePath = path.substring(7);
        filePath = filePath.replace("%20", " ");
        filePath = filePath.replace("%", " ");
        File from = new File(filePath);
        int index = filePath.indexOf("Monitor");
        String fileName = filePath.substring(index + 8);
        File to = new File(Environment.getExternalStorageDirectory().getPath() + "/DCIM/Camera/" + fileName);
        from.renameTo(to);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getMenuInflater().inflate(R.menu.menu_media, menu);
        this.menu = menu;
        menuItemSelect = this.menu.findItem(R.id.select_media);
        menuItemTrash = this.menu.findItem(R.id.delete_medias);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.home:{
                finish();
                return true;
            }
            case R.id.select_media:{
                if (!mAdapter.selectItems) {
                    changeButtonsVisibility(true);
                    gvMedia.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE_MODAL);
                } else {
                    changeButtonsVisibility(false);
                    gvMedia.setChoiceMode(GridView.CHOICE_MODE_SINGLE);
                }
                break;
            }
            case R.id.delete_medias:{
                deleteMedias();
            }
            break;
        }
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            Bundle extras = data.getExtras();
            int pos = (int) extras.getSerializable("position");
            if(resultCode == RESULT_CANCELED) {
                String path = (String) extras.getSerializable("path");
                showUnsupportedVideoOptions(pos, path);
            } else if(resultCode == RESULT_OK) {
                mAdapter.startVideoPosition(pos);
            }
        }
    }
}
