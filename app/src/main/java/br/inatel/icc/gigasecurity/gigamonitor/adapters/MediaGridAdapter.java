package br.inatel.icc.gigasecurity.gigamonitor.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.games.GamesMetadata;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import br.inatel.icc.gigasecurity.gigamonitor.R;
import br.inatel.icc.gigasecurity.gigamonitor.activities.MediaActivity;
import br.inatel.icc.gigasecurity.gigamonitor.activities.MediaVideoActivity;
import br.inatel.icc.gigasecurity.gigamonitor.core.DeviceManager;
import br.inatel.icc.gigasecurity.gigamonitor.listeners.MediaListener;

//import wseemann.media.FFmpegMediaMetadataRetriever;

/**
 * Created by filipecampos on 24/02/2016.
 * Updated by williampenna on 08/08/2017.
 */
public class MediaGridAdapter extends BaseAdapter {

    private Context mContext;
    private DeviceManager mDeviceManager;
    private LayoutInflater mInflater;
    private ArrayList<File> mImageFiles;
    private ArrayList<File> mVideoFiles;

    private ArrayList<Uri> mImageUris;
    private ArrayList<Uri> mVideoUris;

    private ArrayList<Drawable> mImgDrawables;
    private ArrayList<Drawable> mVideoDrawables;

    private ArrayList<Boolean> tridToGetImgThumbnail;
    private ArrayList<Boolean> tridToGetVideoThumbnail;

    private ArrayList<String> toDelete;

    private String path = Environment.getExternalStorageDirectory().getPath();
    private boolean pictureMode;
    public boolean selectItems;

    public static Drawable blankDrawable;
    private MediaListener mMediaListener;

    public MediaGridAdapter(Context mContext, MediaListener mediaListener) {
        this.mContext = mContext;
        mMediaListener = mediaListener;
        this.mDeviceManager = DeviceManager.getInstance();
        this.mInflater = LayoutInflater.from(mContext);
        this.pictureMode = true;

        mImageUris = new ArrayList<>();
        mVideoUris = new ArrayList<>();

        mImgDrawables = new ArrayList<>();
        tridToGetImgThumbnail = new ArrayList<>();

        mVideoDrawables = new ArrayList<>();
        tridToGetVideoThumbnail = new ArrayList<>();

        toDelete = new ArrayList<>();

        getImgFiles();
        getVideoFiles();
//        getVideoFilesAndDrawable();

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
//        mVideoFiles = mDeviceManager.getmVideoFiles();
//        mVideoDrawables = mDeviceManager.getImages();
//        mVideoUris = mDeviceManager.getVideoUris();
//        tridToGetVideoThumbnail = mDeviceManager.getImagesBoolean();
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
        TextView imageView = new TextView(mContext);
        TextView videoView = new TextView(mContext);

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.recycler_view_media_item, parent, false);
            if(this.pictureMode) {
                imageView = (TextView) convertView.findViewById(R.id.media_text_view);
                convertView.setLayoutParams(new GridView.LayoutParams(GridView.AUTO_FIT, 120));
            } else {
                videoView = (TextView) convertView.findViewById(R.id.media_text_view);
                convertView.setLayoutParams(new GridView.LayoutParams(GridView.AUTO_FIT, 120));
            }
        } else {
            if(this.pictureMode) {
                imageView = (TextView) convertView.findViewById(R.id.media_text_view);
            } else {
                videoView = (TextView) convertView.findViewById(R.id.media_text_view);
            }
        }

        if(this.pictureMode) {

            final TextView fImageView = imageView;
            if (mImgDrawables.get(position) != null) {
                fImageView.setText("");
                fImageView.setBackground(mImgDrawables.get(position));
                fImageView.getBackground().setColorFilter(null);
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
        } else {
            final TextView fVideoView = videoView;
            if (mVideoDrawables.get(position) != null) {
                fVideoView.setText("");
                fVideoView.setBackground(mVideoDrawables.get(position));
            } else {
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
//                                        Drawable drawable = new BitmapDrawable(mContext.getResources(), bitmap);
                                        fVideoView.setText("");
                                        fVideoView.setBackground(drawable);
                                    }
                                });
                            }
                        }
                    }.execute();
                }
            }
        }
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!selectItems) {
                    try {
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        if (pictureMode) {
                            intent.setDataAndType(getImageUri(position), "image/*");
                            mContext.startActivity(intent);
                        } else {
                            mMediaListener.onStartVideoActivity(getVideoUri(position).toString(), position);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    setImgDrawable(v, position);
                }
            }
        });


        convertView.setOnLongClickListener(new View.OnLongClickListener() {
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

        return convertView;
    }

    public void startVideoPosition(final int position) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(getVideoUri(position), "video/*");
        mContext.startActivity(intent);
    }

    public Drawable getImgDrawable(final int position) {
        if (mImgDrawables.get(position) == null) {
            try {
                Uri uri = getImageUri(position);
                InputStream inputStream = mContext.getContentResolver().openInputStream(uri);
                Drawable drawable = Drawable.createFromStream(inputStream, uri.toString());
                mImgDrawables.remove(position);
                tridToGetImgThumbnail.remove(position);
                if (drawable != null) {
                    mImgDrawables.add(position, drawable);
                    tridToGetImgThumbnail.add(position, true);
                } else {
                    mImgDrawables.add(position, null);
                    tridToGetImgThumbnail.add(position, false);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
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

    public void setImgDrawable(View view, final int position) {
        try {
            String archiveDate;
            if (pictureMode) {
                archiveDate = mImageFiles.get(position).getName();
            } else {
                archiveDate = mVideoFiles.get(position).getName();
            }
            if (view != null && toDelete.contains(archiveDate)) {
                view.setBackgroundResource(R.drawable.transparent_media_background);
                toDelete.remove(archiveDate);
            } else if (view != null){
                view.setBackgroundResource(R.drawable.green_media_background);
                toDelete.add(archiveDate);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Drawable getVideoDrawable(final int position) {
        if (mVideoDrawables.get(position) == null) {
            try {
                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                retriever.setDataSource(mVideoFiles.get(position).getPath());
                Bitmap bitmap = retriever.getFrameAtTime(1000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
                if (bitmap != null) {
                    mVideoDrawables.add(position, new BitmapDrawable(mContext.getResources(), bitmap));
                    tridToGetVideoThumbnail.add(position, true);
                }

            } catch (RuntimeException e) {
                e.printStackTrace();
            }
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
            mVideoUris.add(position, uri);
        }
        return mVideoUris.get(position);
    }

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
//            mDeviceManager.deleteImage(position);
        }
        notifyDataSetChanged();
    }

    public int getNumberOfMediasToDelete() {
        return toDelete.size();
    }

    public void deleteSelectedMedias() {
        for (int i=0; i<toDelete.size(); i=0) {
            String itemToDelete = toDelete.get(i);
            removeItem(getImagePosition(itemToDelete));
            toDelete.remove(i);
        }
        selectItems = false;
    }

    private int getImagePosition(String date) {
        int position;
        if (pictureMode) {
            position = imagePosition(date);
        } else {
            position = videoPosition(date);
        }
        return position;
    }

    private int imagePosition(final String date) {
        int position = -1;
        for (int i=0; i<mImageFiles.size(); i++) {
            String name = mImageFiles.get(i).getName();
            if (name.contains(date)) {
                position = i;
                break;
            }
        }
        return position;
    }

    private int videoPosition(final String date) {
        int position = -1;
        for (int i=0; i<mVideoFiles.size(); i++) {
            String name = mVideoFiles.get(i).getName();
            if (name.contains(date)) {
                position = i;
                break;
            }
        }
        return position;
    }

    public void clearToDeleteArray() {
        toDelete.clear();
    }

}
