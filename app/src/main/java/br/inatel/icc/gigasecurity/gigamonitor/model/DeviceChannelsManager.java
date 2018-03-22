package br.inatel.icc.gigasecurity.gigamonitor.model;

import android.app.Activity;

import com.lib.IFunSDKResult;
import com.video.opengl.GLSurfaceView20;

import java.util.Collections;
import java.util.Comparator;

import br.inatel.icc.gigasecurity.gigamonitor.ui.SurfaceViewComponent;

public class DeviceChannelsManager extends ChannelsManager implements IFunSDKResult {
    String TAG = "DeviceChannelsManager";

    public DeviceChannelsManager(Device mDevice) {
        super(mDevice);
        createComponents();
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

        surfaceViewComponent.setLayoutParams(surfaceViewLayout);
        surfaceViewComponents.add(i,surfaceViewComponent);
    }

    /** Grid Functions **/
    public void reOrderSurfaceViewComponents() {
        initMatrix();
        for (SurfaceViewComponent svc : surfaceViewComponents) {
            svc.mySurfaceViewOrderId = inverseMatrix[numQuad - 1][svc.mySurfaceViewChannelId];
        }

        Collections.sort(surfaceViewComponents,
                new Comparator<SurfaceViewComponent>() {
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
//    public void onPlayLive(SurfaceViewComponent svc) {
//        mDeviceManager.addToStart(svc);
//    }

    public void enableHD(SurfaceViewComponent svc){
        if(hdChannel > -1) {
            disableHD(surfaceViewComponents.get(getChannelSelected(hdChannel)));
        }
        hdChannel = svc.mySurfaceViewChannelId;
        svc.setStreamType(0);
        restartVideo(svc);
    }

    public void disableHD(SurfaceViewComponent svc){
        hdChannel = -1;
        svc.setStreamType(1);
        if(svc.isConnected())
            restartVideo(svc);
    }

}
