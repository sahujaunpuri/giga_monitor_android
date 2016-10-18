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

import com.xm.MyConfig;
import com.xm.NetSdk;

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
    private ArrayList<Drawable> mVideoDrawables;
    private ArrayList<Boolean> mVideoHasThumbnail;

    public long videoClickDownTime = 0;
    private boolean pictureMode;

    public static Drawable blankDrawable;

    public MediaGridAdapter(Context mContext) {
        this.mContext = mContext;
        this.pictureMode = true;

        mImageUris = new ArrayList<Uri>();
        mVideoUris = new ArrayList<Uri>();
        mVideoDrawables = new ArrayList<Drawable>();
        mVideoHasThumbnail = new ArrayList<Boolean>();

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
            this.mImageUris.add(null);
        }
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
                mVideoHasThumbnail.add(false);
            }
        }
        Collections.sort(mVideoFiles, new Comparator<File>() {
            public int compare(File video1, File video2) {
                Date datevideo1 = new Date(video1.lastModified());
                Date datevideo2 = new Date(video2.lastModified());
                return datevideo1.compareTo(datevideo2);
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
            fImageView.setBackground(blankDrawable);
            fImageView.setText(mImageFiles.get(position).getName());
            ///fImageView.setImageBitmap(blankBitmap);

            new AsyncTask<Void, Void, Uri>() {
                @Override
                protected Uri doInBackground(Void... params) {
                    return getImageUri(position);
                }

                @Override
                protected void onPostExecute(Uri uri) {
                    super.onPostExecute(uri);
                    Drawable drawable;
                    try {
                        InputStream inputStream = mContext.getContentResolver().openInputStream(uri);
                        drawable = Drawable.createFromStream(inputStream, uri.toString() );
                        fImageView.setText("");
                        fImageView.setBackground(drawable);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    //fImageView.setBackground();
                    //fImageView.setImageURI(uri);
                }
            }.execute();

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
                    builder.setTitle(mContext.getResources().getString(R.string.label_action, true))
                            .setItems(new CharSequence[]{"Deletar", "Cancelar"},
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (which == 0) {

                                                mImageFiles.get(position).delete();

                                                Intent intent = new Intent(mContext, MediaActivity.class);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                mContext.startActivity(intent);


                                            }
                                        }
                                    });
                    builder.show();

                    return true;
                }
            });;
            return imageView;

        } else {
            final TextView fVideoView = videoView;
            if (mVideoHasThumbnail.get(position)) {
                fVideoView.setText("");
                fVideoView.setBackground(mVideoDrawables.get(position));
            } else {
                fVideoView.setBackground(blankDrawable);
                fVideoView.setText(mVideoFiles.get(position).getName().substring(0, 19).replace("_", ":"));
                fVideoView.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);

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
                        //fVideoView.setImageBitmap(bitmap);
                    }
                }.execute();
            }

            //Log.v("Rocali",mVideoFiles.get(position).getName());


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

                    builder.setTitle(mContext.getResources().getString(R.string.label_action, true))
                            .setItems(new CharSequence[]{"Deletar", "Cancelar"},
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (which == 0) {
                                                mVideoFiles.get(position).delete();

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
            return fVideoView;
        }
    }

    public Drawable getVideoDrawable(int position) {
        if (mVideoDrawables.get(position) == null) {
            Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(mVideoFiles.get(position).getPath(), MediaStore.Images.Thumbnails.MINI_KIND);
            if (bitmap != null) {
                mVideoDrawables.add(position,new BitmapDrawable(mContext.getResources(), bitmap));
                mVideoHasThumbnail.add(position,true);
            }

        }
        return mVideoDrawables.get(position);
    }

    public Uri getImageUri(int position) {
        Log.v("Rocali","getImageUri "+position);
        if (mImageUris.get(position) == null) {
            Uri uri = Uri.fromFile(mImageFiles.get(position));
            mImageUris.add(position,uri);
        }
        return mImageUris.get(position);
    }

    public Uri getVideoUri(int position) {
        Log.v("Rocali","getVideoUri "+position);
        if (mVideoUris.get(position) == null) {
            Uri uri = Uri.fromFile(mVideoFiles.get(position));
            mVideoUris.add(position,uri);
        }
        return mVideoUris.get(position);
    }

    public Bitmap createBitmapToText(String text) {
        text = text.substring(0,19);
        text = text.replace("_",":");
        Paint paint = new Paint(ANTI_ALIAS_FLAG);
        paint.setTextSize(20);
        paint.setColor(Color.BLACK);
        paint.setTextAlign(Paint.Align.CENTER);
        Bitmap image = Bitmap.createBitmap(200, 150, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(image);
        int xPos = (canvas.getWidth() / 2);
        int yPos = (int) ((canvas.getHeight() / 2) - ((paint.descent() + paint.ascent()) / 2));
        canvas.drawText(text, xPos, yPos, paint);
        return image;
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
