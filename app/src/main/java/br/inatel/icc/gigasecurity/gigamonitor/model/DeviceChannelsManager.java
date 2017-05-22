package br.inatel.icc.gigasecurity.gigamonitor.model;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Environment;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.basic.G;
import com.lib.EUIMSG;
import com.lib.FunSDK;
import com.lib.IFunSDKResult;
import com.lib.MsgContent;
import com.lib.sdk.struct.H264_DVR_FILE_DATA;
import com.video.opengl.GLSurfaceView20;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import br.inatel.icc.gigasecurity.gigamonitor.adapters.ChannelRecyclerViewAdapter;
import br.inatel.icc.gigasecurity.gigamonitor.core.DeviceManager;
import br.inatel.icc.gigasecurity.gigamonitor.listeners.PlaybackListener;
import br.inatel.icc.gigasecurity.gigamonitor.task.AudioRecordThread;
import br.inatel.icc.gigasecurity.gigamonitor.ui.SurfaceViewComponent;
import br.inatel.icc.gigasecurity.gigamonitor.util.Utils;


public class DeviceChannelsManager extends ChannelsManager implements IFunSDKResult {
    String TAG = "DeviceChannelsManager";

    public Device mDevice;
    public ChannelRecyclerViewAdapter mRecyclerAdapter;

    private int[][] inverseMatrix;

    public DeviceChannelsManager(Device mDevice) {
        super();

        this.mDevice = mDevice;
        initMatrix();
    }

    private void initMatrix(){
        if(mDevice.getChannelNumber()<=16) {
            inverseMatrix = new int[][]{
                    {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15},
                    {0, 2, 1, 3, 4, 6, 5, 7, 8, 10, 9, 11, 12, 14, 13, 15},
                    {0, 3, 6, 1, 4, 7, 2, 5, 8, 9, 12, 15, 10, 13, 11, 14},
                    {0, 4, 8, 12, 1, 5, 9, 13, 2, 6, 10, 14, 3, 7, 11, 15}
            };
        }else
            inverseMatrix = new int[][]{
                    {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31},
                    {0, 2, 1, 3, 4, 6, 5, 7, 8, 10, 9, 11, 12, 14, 13, 15, 16, 18, 17, 19, 20, 22, 21, 23, 24, 26, 25, 27, 28, 30, 29, 31},
                    {0, 3, 6, 1, 4, 7, 2, 5, 8, 9, 12, 15, 10, 13, 16, 11, 14, 17, 18, 21, 24, 19, 22, 25, 20, 23, 26, 27, 30, 28, 31, 29},
                    {0, 4, 8, 12, 1, 5, 9, 13, 2, 6, 10, 14, 3, 7, 11, 15, 16, 20, 24, 28, 17, 21, 25, 29, 18, 22, 26, 30, 19, 23, 27, 31}
            };
    }


    public void createComponents() {
        if(surfaceViewComponents.isEmpty()) {
            for (int i = 0; i < mDevice.getChannelNumber(); i++) {

                this.createComponent(i);

            }
            this.reOrderSurfaceViewComponents();
            changeSurfaceViewSize();
        }
    }


    public void createComponent(int position) {
        int i = position;
        GLSurfaceView20 mySurfaceView;
        mySurfaceView = new GLSurfaceView20(mContext);
        mySurfaceView.setLayoutParams(surfaceViewLayout);
        mySurfaceViews.add(i, mySurfaceView);


        SurfaceViewComponent surfaceViewComponent = new SurfaceViewComponent(mContext, this, i);

        surfaceViewComponent.mySurfaceViewChannelId = i;
        surfaceViewComponent.mySurfaceViewOrderId = i;
        surfaceViewComponent.deviceConnection = mDevice.connectionString;
        surfaceViewComponent.deviceId = mDevice.getId();

        if(mDeviceManager.isFavorite(mDevice.getId(), i))
            surfaceViewComponent.setFavorite(true);

//            surfaceViewComponent.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        surfaceViewComponent.setLayoutParams(surfaceViewLayout);

        surfaceViewComponents.add(i,surfaceViewComponent);
    }

    /** Grid Functions **/
    public void reOrderSurfaceViewComponents() {
        for (SurfaceViewComponent svc : surfaceViewComponents) {
            svc.mySurfaceViewOrderId = inverseMatrix[numQuad - 1][svc.mySurfaceViewChannelId];
        }

        Collections.sort(surfaceViewComponents, new Comparator<SurfaceViewComponent>() {
            public int compare(SurfaceViewComponent svc1, SurfaceViewComponent svc2) {
                return svc1.mySurfaceViewOrderId - svc2.mySurfaceViewOrderId;
            }
        });
    }

    public int getChannelSelected(int gridPositionSelected) {
        return inverseMatrix[numQuad - 1][gridPositionSelected];
    }

    public void resetScale(){
        ((Activity) mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for(SurfaceViewComponent svc : surfaceViewComponents){
                    if(svc.mScaleFactor > 1.F)
                        svc.mySurfaceView.resetScaleInfo();
                }
            }
        });
    }

    /** Media Control Functions **/
    public void onPlayLive(SurfaceViewComponent svc) {
        mDeviceManager.addToStart(svc);
    }

    public void onStartVideo(final SurfaceViewComponent svc){
        new Thread(new Runnable() {
            @Override
            public void run() {
                svc.mPlayerHandler = FunSDK.MediaRealPlay(mUserID, mDevice.connectionString, svc.mySurfaceViewChannelId, svc.streamType, mySurfaceViews.get(svc.mySurfaceViewChannelId), svc.mySurfaceViewOrderId);
            }
        }).start();

    }
}
