package br.inatel.icc.gigasecurity.gigamonitor.model;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.opengl.GLSurfaceView;
import android.os.Environment;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.lib.EUIMSG;
import com.lib.FunSDK;
import com.lib.IFunSDKResult;
import com.lib.MsgContent;
import com.video.opengl.GLSurfaceView20;

import java.io.File;
import java.io.FileOutputStream;
import java.io.Serializable;

import br.inatel.icc.gigasecurity.gigamonitor.R;
import br.inatel.icc.gigasecurity.gigamonitor.core.DeviceManager;
import br.inatel.icc.gigasecurity.gigamonitor.util.BitmapUtil;
import br.inatel.icc.gigasecurity.gigamonitor.util.Utils;

import static br.inatel.icc.gigasecurity.gigamonitor.activities.DeviceListActivity.mContext;
import static br.inatel.icc.gigasecurity.gigamonitor.activities.DeviceListActivity.mDeviceManager;

/**
 * Created by filipecampos on 03/12/2015.
 */
public class SurfaceViewComponent extends LinearLayout implements IFunSDKResult{

    private GLSurfaceView mySurfaceView;
    public ProgressBar progressBar;
    public String deviceSn;

    //IDs / Handlers
    String TAG = "SurfaceViewComp";
    public int mPlayerHandler = 0;
    public int mySurfaceViewID;
    public long realPlayHandleID;
    private int mUserID = -1;
    public int mySurfaceViewChannelId;
    public int mySurfaceViewOrderId;

    // state variables
    private int streamType = 1;  //HD:0, SD:1
    public boolean connected = false;
    public boolean isPlaying = false;
    public boolean isREC = false;

    public SurfaceViewComponent(Context context){
        super(context);
        if(mUserID == -1){
            mUserID = FunSDK.RegUser(this);
        }
        if(mySurfaceView == null){
            mySurfaceView = new GLSurfaceView20(getContext());
//            mySurfaceView.setLongClickable(true);

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            mySurfaceView.setLayoutParams(lp);
            this.addView(mySurfaceView);
        }
    }

    public void onPlayLive() {
        mDeviceManager.addToStart(this);

//        mPlayerHandler = FunSDK.MediaRealPlay(mUserID, deviceSn, mySurfaceViewChannelId, streamType, mySurfaceView, 0);
//        return mPlayerHandler;
        //MediaRealPlay(userID, serial number/IP:port, channel, stream type, surfaceview, nSeq)
    }

    public int onStartVideo(){
        mPlayerHandler = FunSDK.MediaRealPlay(mUserID, deviceSn, mySurfaceViewChannelId, streamType, mySurfaceView, 0);
        return mPlayerHandler;
    }

    public boolean isHD(){
        if(streamType == 0)
            return true;
        else
            return false;
    }

    public void setStreamType(int type){
        this.streamType = type;
    }

    public void onPause(){
        if (isPlaying) {
            FunSDK.MediaPause(mPlayerHandler, 1, mySurfaceViewOrderId);
        }
    }

    public void onResume(){
        FunSDK.MediaPause(mPlayerHandler, 0, mySurfaceViewOrderId);
    }

    public void onStop(){
        if ( mPlayerHandler != 0 ) {
            FunSDK.MediaStop(mPlayerHandler);
//            mPlayerHandler = 0;
        }
        isPlaying = false;
    }

    public void takeSnapshot(){
        if(mPlayerHandler != 0){
            String path = Environment.getExternalStorageDirectory().getPath() + "/Pictures/Giga Monitor/" + Utils.currentDateTime();
            int result = FunSDK.MediaSnapImage(mPlayerHandler, path, 0);
            if(result == 0){
                Utils.savePictureFile(path);
            }
        }
    }

    public void startRecord(){
        if(mPlayerHandler != 0) {
            String path = Environment.getExternalStorageDirectory().getPath() + "/Movies/Giga Monitor/" + Utils.currentDateTime() + ".mp4";
            FunSDK.MediaStartRecord(mPlayerHandler, path, 0);
        }
    }

    public void stopRecord(){
        if(mPlayerHandler != 0) {
            FunSDK.MediaStopRecord(mPlayerHandler, 0);
        }
    }

    public GLSurfaceView getMySurfaceView() {
        return mySurfaceView;
    }

    public void setMySurfaceView(final GLSurfaceView mySurfaceView) {
        this.mySurfaceView = mySurfaceView;
    }



    public ProgressBar getProgressBar() {
        return progressBar;
    }

    public void setProgressBar(ProgressBar progressBar) {
        this.progressBar = progressBar;
    }


    public int getMySurfaceViewID() {
        return mySurfaceViewID;
    }

    public void setMySurfaceViewID(int mySurfaceViewID) {
        this.mySurfaceViewID = mySurfaceViewID;
    }

    public long getRealPlayHandleID() {
        return realPlayHandleID;
    }

    public void setRealPlayHandleID(long realPlayHandleID) {
        this.realPlayHandleID = realPlayHandleID;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setIsPlaying(boolean isPlaying) {
        this.isPlaying = isPlaying;
    }

    public boolean isREC() {
        return isREC;
    }

    public void setREC(boolean REC) {
        isREC = REC;
    }

    public int getMySurfaceViewChannelId() {
        return mySurfaceViewChannelId;
    }

    public void setMySurfaceViewChannelId(int mySurfaceViewChannelId) {
        this.mySurfaceViewChannelId = mySurfaceViewChannelId;
    }

/*    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
        }
        return super.onInterceptTouchEvent(ev);
    }*/

    @Override
    public int OnFunSDKResult(Message msg, MsgContent msgContent) {
        Log.d(TAG, "msg.what : " + msg.what);
        Log.d(TAG, "msg.arg1 : " + msg.arg1);
        Log.d(TAG, "msg.arg2 : " + msg.arg2);
        if (null != msgContent) {
            Log.d(TAG, "msgContent.sender : " + msgContent.sender);
            Log.d(TAG, "msgContent.seq : " + msgContent.seq);
            Log.d(TAG, "msgContent.str : " + msgContent.str);
            Log.d(TAG, "msgContent.arg3 : " + msgContent.arg3);
            Log.d(TAG, "msgContent.pData : " + msgContent.pData);
        }
        switch (msg.what) {
            case EUIMSG.START_PLAY: {
                Log.i(TAG, "EUIMSG.START_PLAY");
                if (msg.arg1 == 0) {
                    this.connected = true;
                    if(mDeviceManager.startList.contains(this)) {
                        mDeviceManager.startList.remove(this);
                        mDeviceManager.requestStart();
                    }
                    Log.i(TAG, "START SUCCESS");
                } else {
                    if(mDeviceManager.startList.contains(this))
                        mDeviceManager.startList.remove(this);
                    onPlayLive();
                    Log.i(TAG, "START FAILED");
                }
            }
            break;
            case EUIMSG.STOP_PLAY: {
                Log.i(TAG, "EUIMSG.STOP_PLAY");
                if (msg.arg1 == 0) {
                    Log.i(TAG, "STOP SUCCESS");
                    connected = false;
                } else {
                    Log.i(TAG, "STOP FAILED");
                }
            }
            break;
            case EUIMSG.PAUSE_PLAY: {
                Log.i(TAG, "EUIMSG.PAUSE_PLAY");
                if (msg.arg1 == 1) {
                    this.isPlaying = true;
                    Log.i(TAG, "PLAY/PAUSE: playing");
                } else if(msg.arg1 == 2) {
                    this.isPlaying = false;
                    Log.i(TAG, "PLAY/PAUSE: paused");
                }
            }
            break;
            case EUIMSG.ON_PLAY_BUFFER_BEGIN: {
                Log.i(TAG, "EUIMSG.ON_PLAY_BUFFER_BEGIN");
                if (msg.arg1 == 0) {
                    Log.i(TAG, "PLAY BUFFER BEGIN");
                }
            }
            break;
            case EUIMSG.ON_PLAY_BUFFER_END: {
                Log.i(TAG, "EUIMSG.ON_PLAY_BUFFER_END");
                if (msg.arg1 == 0) {
                    this.progressBar.setVisibility(INVISIBLE);
                    this.isPlaying = true;
                    Log.i(TAG, "PLAY BUFFER END");
                }
            }
            break;
            case EUIMSG.SAVE_IMAGE_FILE: {
                Log.i(TAG, "EUIMSG.SAVE_IMAGE_FILE");
                if (msg.arg1 == 0) {
                        Toast.makeText(mContext, "Imagem salva com sucesso", Toast.LENGTH_SHORT).show();
                    Log.i(TAG, "PLAY BUFFER END");
                } else{
                    Toast.makeText(mContext, "Falha na captura da imagem", Toast.LENGTH_SHORT).show();
                }
            }
            break;
            case EUIMSG.START_SAVE_MEDIA_FILE: {
                if(msg.arg1 == 0){
                    Toast.makeText(mContext, "Gravação iniciada", Toast.LENGTH_SHORT).show();
                } else{
                    Toast.makeText(mContext, "Falha na gravação", Toast.LENGTH_SHORT).show();
                }
            }
            break;
            case EUIMSG.STOP_SAVE_MEDIA_FILE: {
                if(msg.arg1 == 0){
                    File file = new File(msgContent.str);
                    if(file.length() > 1000)
                        Toast.makeText(mContext, "Gravação finalizada", Toast.LENGTH_SHORT).show();
                    else{
                        file.delete();
                        Toast.makeText(mContext, "Falha na captura da imagem", Toast.LENGTH_SHORT).show();
                    }
                } else{
                    Toast.makeText(mContext, "Falha na gravação", Toast.LENGTH_SHORT).show();
                }
            }
            break;
        }
        return 0;
    }

}

