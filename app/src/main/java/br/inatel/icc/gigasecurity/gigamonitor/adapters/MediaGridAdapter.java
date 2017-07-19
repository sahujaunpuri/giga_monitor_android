package br.inatel.icc.gigasecurity.gigamonitor.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.lib.sdk.bean.VideoWidgetBean;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import br.inatel.icc.gigasecurity.gigamonitor.R;
import br.inatel.icc.gigasecurity.gigamonitor.activities.MediaActivity;

//import wseemann.media.FFmpegMediaMetadataRetriever;

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

    private ArrayList<Drawable> toDelete;

    public long videoClickDownTime = 0;
    private String path = Environment.getExternalStorageDirectory().getPath();
    private boolean pictureMode;
    public boolean selectItems;

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

        toDelete = new ArrayList<>();

        getImgFiles();
        getVideoFiles();

        Bitmap blankBitmap = Bitmap.createBitmap(2, 2, Bitmap.Config.ARGB_8888);
        blankDrawable = new BitmapDrawable(mContext.getResources(), blankBitmap);

        selectItems = false;

    }

    private void getImgFiles() {
        File imgFile = new File(path + "/Pictures/Giga Monitor/");
        File[] imgFiles = imgFile.listFiles();
        mImageFiles = new ArrayList<>();
        if(imgFiles == null)
            return;
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
        File videoFile = new File(path + "/Movies/Giga Monitor/"); //"/sdcard/Movies/Giga Monitor");
        File[] videoFiles = videoFile.listFiles();
        mVideoFiles = new ArrayList<>();
        if(videoFiles == null)
            return;
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
    public View getView(final int position, final View convertView, ViewGroup parent) {
        TextView imageView = null;
        TextView videoView = null;

        if (convertView == null) {
            if(this.pictureMode) {
                imageView = new TextView(mContext);
                imageView.setLayoutParams(new GridView.LayoutParams(GridView.AUTO_FIT, 120));
            } else {
                videoView = new TextView(mContext);
                videoView.setLayoutParams(new GridView.LayoutParams(GridView.AUTO_FIT, 120));
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
                    if (!selectItems) {
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.setDataAndType(getImageUri(position), "image/*");
                        mContext.startActivity(intent);
                    } else {
                        Drawable draw = getDrawable(position);
                        setImgDrawable(v, draw);
                    }
                }
            });

            fImageView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (!selectItems) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                        builder.setTitle(mContext.getResources().getString(R.string.label_action))
                                .setItems(new CharSequence[]{"Deletar", "Cancelar"},
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (which == 0) {
                                                    removeItem(position);
                                                }
                                            }
                                        });
                        builder.show();

                        return true;
                    } else {
                        return false;
                    }
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
                    if (!selectItems) {
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.setDataAndType(getVideoUri(position), "video/*");
                        mContext.startActivity(intent);
                    } else {
                        Drawable draw = getDrawable(position);
                        setImgDrawable(v, draw);
                    }
                }
            });


            videoView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (!selectItems) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

                        builder.setTitle(mContext.getResources().getString(R.string.media_delete_various))
                                .setItems(new CharSequence[]{"Sim", "Cancelar"},
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (which == 0) {
                                                    removeItem(position);
                                                }
                                            }
                                        });
                        builder.show();

                        return true;
                    } else {
                        return false;
                    }
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
                mImgDrawables.remove(position);
                mImgDrawables.add(position, drawable);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            tridToGetImgThumbnail.add(position, true);
        }
        return mImgDrawables.get(position);
    }

    private Drawable getDrawable(int position) {
        Drawable draw = null;
        if (pictureMode) {
            draw = mImgDrawables.get(position);
        } else {
            draw = mVideoDrawables.get(position);
        }
        return draw;
    }

    private void setImgDrawable(View view, Drawable draw) {
        try {
            if (toDelete.contains(draw)) {
//                draw.setAlpha(255);
                view.getBackground().setColorFilter(null);
                toDelete.remove(draw);
            } else {
//                draw.setAlpha(128);
                view.getBackground().setColorFilter(Color.DKGRAY, PorterDuff.Mode.OVERLAY);
                toDelete.add(draw);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Drawable getVideoDrawable(int position) {
        if (mVideoDrawables.get(position) == null) {
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(mVideoFiles.get(position).getPath());
            Bitmap bitmap = retriever.getFrameAtTime(1000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
//            Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(mVideoFiles.get(position).getPath(), MediaStore.Images.Thumbnails.MINI_KIND);
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

    public void removeItem(final int position) {
        if (pictureMode) {
            mImageFiles.get(position).delete();
            mImageFiles.remove(position);
            mImageUris.remove(position);
            mImgDrawables.remove(position);
            tridToGetImgThumbnail.remove(position);
        } else {
            mVideoFiles.get(position).delete();
            mVideoFiles.remove(position);
            mVideoUris.remove(position);
            mVideoDrawables.remove(position);
            tridToGetVideoThumbnail.remove(position);
        }
        notifyDataSetChanged();
    }

    public int getNumberOfMediasToDelete() {
        return toDelete.size();
    }

    public void deleteSelectedMedias() {
        for (int i=0; i<toDelete.size(); i=0) {
            Drawable itemToDelete = toDelete.get(i);
            removeItem(getDrawablePosition(itemToDelete));
            toDelete.remove(i);
        }
    }

    private int getDrawablePosition(Drawable drawable) {
        int position = -1;
        if (pictureMode) {
            if (mImgDrawables.contains(drawable)) {
                position = mImgDrawables.indexOf(drawable);
            }
        } else {
            if (mVideoDrawables.contains(drawable)) {
                position = mVideoDrawables.indexOf(drawable);
            }
        }
        return position;
    }

}
