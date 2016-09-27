package br.inatel.icc.gigasecurity.gigamonitor.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;
import java.util.ArrayList;

import br.inatel.icc.gigasecurity.gigamonitor.R;
import br.inatel.icc.gigasecurity.gigamonitor.activities.DeviceFormActivity;
import br.inatel.icc.gigasecurity.gigamonitor.activities.MediaActivity;

/**
 * Created by filipecampos on 24/02/2016.
 */
public class MediaGridAdapter extends BaseAdapter {

    private Context mContext;
    private File[] mImagesFile;
    private ArrayList<File> mVideoFile;
    public long videoClickDownTime = 0;

    public MediaGridAdapter(Context mContext, File[] mImagesFile, ArrayList<File> mVideoFile) {
        this.mContext = mContext;
        this.mImagesFile = mImagesFile;
        this.mVideoFile = mVideoFile;
    }

    @Override
    public int getCount() {
        if(mVideoFile == null) {
            return mImagesFile.length;
        } else {
            return mVideoFile.size();
        }
    }

    @Override
    public Object getItem(int position) {
        if(mVideoFile == null) {
            return mImagesFile[position];
        } else {
            return mVideoFile.get(position);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ImageView imageView = null;
        VideoView videoView = null;

        if (convertView == null) {

            if(mVideoFile == null) {

                imageView = new ImageView(mContext);
                imageView.setLayoutParams(new GridView.LayoutParams(210, 210));
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

            } else {

                videoView = new VideoView(mContext);
                videoView.setLayoutParams(new GridView.LayoutParams(210, 210));

                videoView.setOnErrorListener(mOnErrorListener);

            }

        } else {

            if(mVideoFile == null) {
                imageView = (ImageView) convertView;
            } else {
                videoView = (VideoView) convertView;
            }

        }

        if(mVideoFile == null) {

            final Uri uri = Uri.fromFile(mImagesFile[position]);

            imageView.setImageURI(uri);


            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setDataAndType(uri, "image/*");
                    mContext.startActivity(intent);
                }
            });

            imageView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle(mContext.getResources().getString(R.string.label_action, true))
                            .setItems(new CharSequence[]{"Deletar", "Cancelar"},
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (which == 0) {

                                                mImagesFile[position].delete();

                                                Intent intent = new Intent(mContext, MediaActivity.class);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                mContext.startActivity(intent);


                                            }
                                        }
                                    });
                    builder.show();

                    return true;
                }
            });

            return imageView;

        } else {

            final Uri uri = Uri.fromFile(mVideoFile.get(position));

            videoView.setVideoURI(uri);

            final VideoView finalVideoView = videoView;

            new Thread(new Runnable() {
                @Override
                public void run() {

                    try{

                        finalVideoView.start();

                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        finalVideoView.pause();

                    } catch (Exception e) {
                        Log.w("ERRO: ", "Erro ao reproduzir video");
                    }

                }
            }).start();

            videoView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    int action = event.getAction();

                    if (action == MotionEvent.ACTION_DOWN) {

                        videoClickDownTime = System.currentTimeMillis();
                    }

                    if (action == MotionEvent.ACTION_UP) {

                        long videoClickUpTime = System.currentTimeMillis();

                        if (videoClickUpTime - videoClickDownTime <= 800) {

                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_VIEW);
                            intent.setDataAndType(uri, "video/*");
                            mContext.startActivity(intent);

                        } else {

                            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

                            builder.setTitle(mContext.getResources().getString(R.string.label_action, true))
                                    .setItems(new CharSequence[]{"Deletar", "Cancelar"},
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    if (which == 0) {
                                                        mVideoFile.get(position).delete();

                                                        Intent intent = new Intent(mContext, MediaActivity.class);
                                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

                                                        mContext.startActivity(intent);
                                                    }
                                                }
                                            });
                            builder.show();

                        }


                    }

                    return true;
                }
            });


            videoView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle(mContext.getResources().getString(R.string.label_action, true))
                            .setItems(new CharSequence[]{"Deletar", "Cancelar"},
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (which == 0) {

                                            } else {

                                            }
                                        }
                                    });
                    builder.show();

                    return true;
                }
            });

            return videoView;
        }
    }


    private MediaPlayer.OnErrorListener mOnErrorListener = new MediaPlayer.OnErrorListener() {

        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            // Your code goes here
            return true;
        }
    };
}
