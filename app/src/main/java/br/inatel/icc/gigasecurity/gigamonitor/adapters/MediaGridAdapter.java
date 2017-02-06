package br.inatel.icc.gigasecurity.gigamonitor.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Browser;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.zip.CheckedOutputStream;

import br.inatel.icc.gigasecurity.gigamonitor.R;
import br.inatel.icc.gigasecurity.gigamonitor.activities.DeviceFormActivity;
import br.inatel.icc.gigasecurity.gigamonitor.activities.DeviceListActivity;
import br.inatel.icc.gigasecurity.gigamonitor.activities.MediaActivity;
//import wseemann.media.FFmpegMediaMetadataRetriever;

import static android.graphics.Paint.ANTI_ALIAS_FLAG;
import static android.graphics.Paint.FILTER_BITMAP_FLAG;

/**
 * Created by filipecampos on 24/02/2016.
 */
public class MediaGridAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<File> mImageFiles;
    private ArrayList<File> mVideoFiles;

    private ArrayList<Uri> mImageUris;
    private ArrayList<Uri> mVideoUris;

    private ArrayList<Drawable> mImgDrawables;
    private ArrayList<Drawable> mVideoDrawables;

    private ArrayList<Boolean> tridToGetImgThumbnail;
    private ArrayList<Boolean> tridToGetVideoThumbnail;

    public long videoClickDownTime = 0;
    private String path = Environment.getExternalStorageDirectory().getPath() + "/Giga Monitor";
    private boolean pictureMode;

    public static Drawable blankDrawable;

    public MediaGridAdapter(Context mContext) {
        this.mContext = mContext;
        this.pictureMode = true;

        mImageUris = new ArrayList<Uri>();
        mVideoUris = new ArrayList<Uri>();

        mImgDrawables = new ArrayList<Drawable>();
        tridToGetImgThumbnail = new ArrayList<Boolean>();

        mVideoDrawables = new ArrayList<Drawable>();
        tridToGetVideoThumbnail = new ArrayList<Boolean>();

        getImgFiles();
        getVideoFiles();



        Bitmap blankBitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
        blankDrawable = new BitmapDrawable(mContext.getResources(), blankBitmap);
    }

    private void getImgFiles() {
        File imgFile = new File("/sdcard/Pictures/Giga Monitor");
        File[] imgFiles = imgFile.listFiles();
        mImageFiles = new ArrayList<>();
        for(int i =0; i<imgFiles.length; i++) {
            mImageFiles.add(imgFiles[i]);
            //Aux Array

            mImageUris.add(null);
            mImgDrawables.add(null);
            tridToGetImgThumbnail.add(false);
        }
        Collections.sort(mImageFiles, new Comparator<File>() {
            public int compare(File img1, File img2) {
                Date dateimg1 = new Date(img1.lastModified());
                Date dateimg2 = new Date(img2.lastModified());
                return dateimg2.compareTo(dateimg1);
            }
        });
    }

    private void getVideoFiles() {
        File videoFile = new File("/sdcard/Movies/Giga Monitor");
        File[] videoFiles = videoFile.listFiles();
        mVideoFiles = new ArrayList<>();
        for(int i =0; i<videoFiles.length; i++) {
            if(videoFiles[i].getName().contains(".mp4")) {
                mVideoFiles.add(videoFiles[i]);

                //Aux Arrays
                mVideoUris.add(null);
                mVideoDrawables.add(null);
                tridToGetVideoThumbnail.add(false);
            }
        }
        Collections.sort(mVideoFiles, new Comparator<File>() {
            public int compare(File video1, File video2) {
                Date datevideo1 = new Date(video1.lastModified());
                Date datevideo2 = new Date(video2.lastModified());
                return datevideo2.compareTo(datevideo1);
            }
        });
    }

    @Override
    public int getCount() {
        if(this.pictureMode) {
            return mImageFiles.size();
        } else {
            return mVideoFiles.size();
        }
    }

    @Override
    public Object getItem(int position) {
        if(this.pictureMode) {
            return mImageFiles.get(position);
        } else {
            return mVideoFiles.get(position);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        TextView imageView = null;
        TextView videoView = null;

        if (convertView == null) {

            if(this.pictureMode) {

                imageView = new TextView(mContext);
                imageView.setLayoutParams(new GridView.LayoutParams(GridView.AUTO_FIT, 120));
                //imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

            } else {

                videoView = new TextView(mContext);
                videoView.setLayoutParams(new GridView.LayoutParams(GridView.AUTO_FIT, 120));
                //videoView.setScaleType(ImageView.ScaleType.CENTER_CROP);

            }

        } else {

            if(this.pictureMode) {
                imageView = (TextView) convertView;
            } else {
                videoView = (TextView) convertView;
            }

        }

        if(this.pictureMode) {

            final TextView fImageView = imageView;
            if (mImgDrawables.get(position) != null) {
                fImageView.setText("");
                fImageView.setBackground(mImgDrawables.get(position));
            } else {
                fImageView.setBackground(blankDrawable);
                fImageView.setText(mImageFiles.get(position).getName());

                if (!tridToGetImgThumbnail.get(position)) {

                    new AsyncTask<Void, Void, Drawable>() {
                        @Override
                        protected Drawable doInBackground(Void... params) {
                            return getImgDrawable(position);
                        }

                        @Override
                        protected void onPostExecute(final Drawable drawable) {
                            super.onPostExecute(drawable);

                            if (drawable != null) {
                                ((MediaActivity) mContext).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        fImageView.setText("");
                                        fImageView.setBackground(drawable);
                                    }
                                });
                            }

                        }
                    }.execute();
                }
            }

            fImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setDataAndType(getImageUri(position), "image/*");
                    mContext.startActivity(intent);
                }
            });

            fImageView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle(mContext.getResources().getString(R.string.label_action))
                            .setItems(new CharSequence[]{"Deletar", "Cancelar"},
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (which == 0) {

                                                mImageFiles.get(position).delete();

                                                Intent intent = new Intent(mContext, MediaActivity.class);
                                                intent.putExtra("imageSelected", true);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                mContext.startActivity(intent);


                                            }
                                        }
                                    });
                    builder.show();

                    return true;
                }
            });;
            return fImageView;

        } else {
            final TextView fVideoView = videoView;
            if (mVideoDrawables.get(position) != null) {
                fVideoView.setText("");
                fVideoView.setBackground(mVideoDrawables.get(position));
            }  else {
                fVideoView.setBackground(blankDrawable);
                fVideoView.setText(mVideoFiles.get(position).getName().substring(0, 19).replace("_", ":"));
                fVideoView.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);

                if (!tridToGetVideoThumbnail.get(position)) {

                    new AsyncTask<Void, Void, Drawable>() {
                        @Override
                        protected Drawable doInBackground(Void... params) {


                            return getVideoDrawable(position);
                        }

                        @Override
                        protected void onPostExecute(final Drawable drawable) {
                            super.onPostExecute(drawable);
                            if (drawable != null) {
                                ((MediaActivity) mContext).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        //Drawable drawable = new BitmapDrawable(mContext.getResources(), bitmap);
                                        fVideoView.setText("");
                                        fVideoView.setBackground(drawable);
                                    }
                                });
                            }
                        }
                    }.execute();
                }
            }

            fVideoView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_VIEW);
                            intent.setDataAndType(getVideoUri(position), "video/*");
                            mContext.startActivity(intent);


                }
            });


            videoView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

                    builder.setTitle(mContext.getResources().getString(R.string.label_action))
                            .setItems(new CharSequence[]{"Deletar", "Cancelar"},
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (which == 0) {
                                                mVideoFiles.get(position).delete();
                                                notifyDataSetChanged();

                                                Intent intent = new Intent(mContext, MediaActivity.class);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                intent.putExtra("imageSelected", false);

                                                mContext.startActivity(intent);


                                            }
                                        }
                                    });
                    builder.show();

                    return true;
                }
            });
            return fVideoView;
        }
    }

    public Drawable getImgDrawable(int position) {
        if (mImgDrawables.get(position) == null) {
            try {
                Uri uri = getImageUri(position);
                InputStream inputStream = mContext.getContentResolver().openInputStream(uri);
                Drawable drawable = Drawable.createFromStream(inputStream, uri.toString());
                mImgDrawables.add(position, drawable);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            tridToGetImgThumbnail.add(position, true);
        }
        return mImgDrawables.get(position);
    }

    public Drawable getVideoDrawable(int position) {
        if (mVideoDrawables.get(position) == null) {
            Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(mVideoFiles.get(position).getPath(), MediaStore.Images.Thumbnails.MINI_KIND);
            if (bitmap != null) {
                mVideoDrawables.add(position,new BitmapDrawable(mContext.getResources(), bitmap));
            }
            tridToGetVideoThumbnail.add(position,true);

        }
        return mVideoDrawables.get(position);
    }

    public Uri getImageUri(int position) {
        if (mImageUris.get(position) == null) {
            Uri uri = Uri.fromFile(mImageFiles.get(position));
            mImageUris.add(position,uri);
        }
        return mImageUris.get(position);
    }

    public Uri getVideoUri(int position) {
        if (mVideoUris.get(position) == null) {
            Uri uri = Uri.fromFile(mVideoFiles.get(position));
            mVideoUris.add(position,uri);
        }
        return mVideoUris.get(position);
    }

    private MediaPlayer.OnErrorListener mOnErrorListener = new MediaPlayer.OnErrorListener() {

        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            // Your code goes here
            return true;
        }
    };

    public void changeGridMode(boolean pictureMode) {
        this.pictureMode = pictureMode;
    }


}
