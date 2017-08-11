package br.inatel.icc.gigasecurity.gigamonitor.util;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;

/**
 * Created by zappts on 11/08/17.
 */

public class MediaService extends Service {

    MediaPlayer mMediaPlayer;

    public MediaService() {
        mMediaPlayer = new MediaPlayer();
    }

    public void initMediaPlayer(String path) {
        try {
            mMediaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
                @Override
                public void onSeekComplete(MediaPlayer mp) {
                    Log.e("MEDIAWILLIAM", "SEEKCOMPLETE");
                }
            });
            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    Log.e("MEDIAWILLIAM", "COMPLETE");
                }
            });
            mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    Log.e("MEDIAWILLIAM", "Error");
                    return false;
                }
            });

            mMediaPlayer.prepare();

            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    if (mp != null) {
                        try {
                            Log.e("MEDIAWILLIAM", "Start");
                            mMediaPlayer.seekTo(1000);
                            mMediaPlayer.start();
                        } catch (Exception e) {
                            Log.e("MEDIAWILLIAM", "STARTEXCEPTION");
                            e.printStackTrace();
                        }
                    }
                }
            });

        } catch (IOException e) {
            Log.e("MEDIAWILLIAM", "IOEXCEPTION");
            e.printStackTrace();
        }
    }

    public void setPath(Context context, final String path) {
        try {
            mMediaPlayer.setDataSource(context, Uri.parse(path));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
