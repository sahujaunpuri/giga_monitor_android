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
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
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

import com.xm.MyConfig;
import com.xm.NetSdk;

import java.io.File;
import java.util.ArrayList;
import java.util.zip.CheckedOutputStream;

import br.inatel.icc.gigasecurity.gigamonitor.R;
import br.inatel.icc.gigasecurity.gigamonitor.activities.DeviceFormActivity;
import br.inatel.icc.gigasecurity.gigamonitor.activities.MediaActivity;
//import wseemann.media.FFmpegMediaMetadataRetriever;

import static android.graphics.Paint.ANTI_ALIAS_FLAG;
import static android.graphics.Paint.FILTER_BITMAP_FLAG;

/**
 * Created by filipecampos on 24/02/2016.
 */
public class MediaGridAdapter extends BaseAdapter {

    private Context mContext;
    private File[] mImagesFile;
    private ArrayList<File> mVideoFile;

    private ArrayList<Uri> mImageUris;
    private ArrayList<Uri> mVideoUris;
    private ArrayList<Bitmap> mVideoBitmaps;

    public long videoClickDownTime = 0;
    private boolean pictureMode;

    public static Bitmap blankBitmap;

    public MediaGridAdapter(Context mContext) {
        this.mContext = mContext;
        this.pictureMode = true;

        mImageUris = new ArrayList<Uri>();
        mVideoUris = new ArrayList<Uri>();
        mVideoBitmaps = new ArrayList<Bitmap>();

        getImgFiles();
        getVideoFiles();



        blankBitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
    }

    private void getImgFiles() {
        File imgFiles = new File("/sdcard/Pictures/Giga Monitor");
        this.mImagesFile = imgFiles.listFiles();

        int i = 0;
        while (i < this.mImagesFile.length){
            this.mImageUris.add(null);
            i++;
        }
    }

    private void getVideoFiles() {
        File videoFile = new File("/sdcard/Movies/Giga Monitor");
        File[] videoFiles = videoFile.listFiles();
        mVideoFile = new ArrayList<>();
        for(int i =0; i<videoFiles.length; i++) {
            if(videoFiles[i].getName().contains(".mp4")) {
                mVideoFile.add(videoFiles[i]);
                mVideoUris.add(null);
                mVideoBitmaps.add(null);
            }
        }
    }

    @Override
    public int getCount() {
        if(this.pictureMode) {
            return mImagesFile.length;
        } else {
            return mVideoFile.size();
        }
    }

    @Override
    public Object getItem(int position) {
        if(this.pictureMode) {
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
        ImageView videoView = null;

        if (convertView == null) {

            if(this.pictureMode) {

                imageView = new ImageView(mContext);
                imageView.setLayoutParams(new GridView.LayoutParams(GridView.AUTO_FIT, 120));
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

            } else {

                videoView = new ImageView(mContext);
                videoView.setLayoutParams(new GridView.LayoutParams(GridView.AUTO_FIT, 120));
                videoView.setScaleType(ImageView.ScaleType.CENTER_CROP);

            }

        } else {

            if(this.pictureMode) {
                imageView = (ImageView) convertView;
            } else {
                videoView = (ImageView) convertView;
            }

        }

        if(this.pictureMode) {

            final ImageView fImageView = imageView;
            fImageView.setImageBitmap(blankBitmap);

            new AsyncTask<Void, Void, Uri>() {
                @Override
                protected Uri doInBackground(Void... params) {
                    return getImageUri(position);
                }

                @Override
                protected void onPostExecute(Uri uri) {
                    super.onPostExecute(uri);
                    fImageView.setImageURI(uri);
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
            });;
            return imageView;

        } else {
            final ImageView fVideoView = videoView;
            fVideoView.setImageBitmap(blankBitmap);

            new AsyncTask<Void, Void, Bitmap>() {
                @Override
                protected Bitmap doInBackground(Void... params) {


                    return getVideoBitmap(position);
                }

                @Override
                protected void onPostExecute(Bitmap bitmap) {
                    super.onPostExecute(bitmap);
                    fVideoView.setImageBitmap(bitmap);
                }
            }.execute();

            Log.v("Rocali",mVideoFile.get(position).getName());


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
                                                mVideoFile.get(position).delete();

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

    public Bitmap getVideoBitmap(int position) {
        if (mVideoBitmaps.get(position) == null) {
            Bitmap thumb = ThumbnailUtils.createVideoThumbnail(mVideoFile.get(position).getPath(), MediaStore.Images.Thumbnails.MINI_KIND);
            if (thumb == null) {
                /*
                FFmpegMediaMetadataRetriever retriever = new  FFmpegMediaMetadataRetriever();
                try {
                    retriever.setDataSource("mnt/sdcard/video.mp4"); //file's path
                    thumb = retriever.getFrameAtTime(100000,FFmpegMediaMetadataRetriever.OPTION_CLOSEST_SYNC );

                } catch (Exception e) {
                    e.printStackTrace();
                }
                finally{
                    retriever.release();
                }

                MediaMetadataRetriever retriever = new MediaMetadataRetriever();

                try {
                    retriever.setDataSource(mVideoFile.get(position).getPath());

                    thumb = retriever.getFrameAtTime((long)100000,MediaMetadataRetriever.OPTION_CLOSEST_SYNC);

                } catch (IllegalArgumentException ex) {
                    ex.printStackTrace();
                } catch (RuntimeException ex) {
                    ex.printStackTrace();
                } finally {
                    try {
                        retriever.release();
                    } catch (RuntimeException ex) {
                        ex.printStackTrace();
                    }
                }*/


                Log.v("Rocali"," NO IMAGE"+mVideoFile.get(position).getName());
                thumb = createBitmapToText(mVideoFile.get(position).getName());
            }
            mVideoBitmaps.add(position,thumb);
        }
        return mVideoBitmaps.get(position);
    }

    public Uri getImageUri(int position) {
        Log.v("Rocali","getImageUri "+position);
        if (mImageUris.get(position) == null) {
            Uri uri = Uri.fromFile(mImagesFile[position]);
            mImageUris.add(position,uri);
        }
        return mImageUris.get(position);
    }

    public Uri getVideoUri(int position) {
        Log.v("Rocali","getVideoUri "+position);
        if (mVideoUris.get(position) == null) {
            Uri uri = Uri.fromFile(mVideoFile.get(position));
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
