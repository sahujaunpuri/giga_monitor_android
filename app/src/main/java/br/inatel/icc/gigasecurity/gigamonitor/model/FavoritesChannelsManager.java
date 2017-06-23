package br.inatel.icc.gigasecurity.gigamonitor.model;

import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.lib.FunSDK;
import com.lib.IFunSDKResult;
import com.video.opengl.GLSurfaceView20;

import br.inatel.icc.gigasecurity.gigamonitor.R;
import br.inatel.icc.gigasecurity.gigamonitor.activities.DeviceListActivity;
import br.inatel.icc.gigasecurity.gigamonitor.ui.SurfaceViewComponent;

/**
 * Created by filipecampos on 02/05/2016.
 */
public class FavoritesChannelsManager extends ChannelsManager implements IFunSDKResult {
    private final String TAG = "FavoritesManager";

    public FavoritesChannelsManager(Device mDevice) {
        super(mDevice);
        channelNumber = mDeviceManager.favoriteChannels;
    }


    @Override
    public void createComponents() {
        int position = 0;
        surfaceViewComponents.clear();
        mySurfaceViews.clear();
        if(mDevice.getChannelNumber()>1){
            numQuad = 2;
            lastNumQuad = 2;
        }else{
            numQuad = 1;
            lastNumQuad = 1;
        }
        for(FavoritePair favorite : mDeviceManager.favoritesList){
            Device currentDevice = mDeviceManager.findDeviceById(favorite.deviceId);
            ChannelsManager currentManager = mDeviceManager.findChannelManagerByDevice(currentDevice);
            createComponent(currentManager, favorite.channelNumber, position);
            position++;
        }
        changeSurfaceViewSize();
    }

    public void createComponent(ChannelsManager channelsManager, int i, int position){

        GLSurfaceView20 mySurfaceView;
        mySurfaceView = new GLSurfaceView20(mContext);
        mySurfaceView.setLayoutParams(surfaceViewLayout);
        mySurfaceViews.add(mySurfaceView);

        SurfaceViewComponent surfaceViewComponent = new SurfaceViewComponent(mContext, this, position);

        surfaceViewComponent.mySurfaceViewChannelId = i;
        surfaceViewComponent.mySurfaceViewOrderId = position;
        surfaceViewComponent.deviceConnection = channelsManager.mDevice.connectionString;
        surfaceViewComponent.deviceId = channelsManager.mDevice.getId();

        if(mDeviceManager.isFavorite(channelsManager.mDevice.getId(), i))
            surfaceViewComponent.isFavorite = true;

//            surfaceViewComponent.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        surfaceViewComponent.setLayoutParams(surfaceViewLayout);

        surfaceViewComponents.add(surfaceViewComponent);
    }

    public void refreshFromDevice(final int deviceId){
        new Thread(new Runnable() {
            @Override
            public void run() {
                for(SurfaceViewComponent svc : surfaceViewComponents){
                    if(svc.deviceId == deviceId && svc.isVisible && !svc.isConnected()){
                        restartVideo(svc);
                    }
                    /*if(svc.deviceId == deviceId){
//                        removeErrorIcon(svc);
                    }*/
                }
            }
        }).start();
    }

    public void connectionError(final int deviceId){
        new Thread(new Runnable() {
            @Override
            public void run() {
                for(SurfaceViewComponent svc : surfaceViewComponents){
                    if(svc.deviceId == deviceId){
                        setErrorIcon(svc);
                    }
                }
            }
        }).start();
    }

    public void setErrorIcon(final SurfaceViewComponent svc){

        svc.setConnected(false);
        svc.isLoading(false);

        /*((DeviceListActivity) mDeviceManager.currentContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(svc.errorIcon == null) {
                    svc.errorIcon = new ImageView(mContext);
                    svc.errorIcon.setImageResource(R.drawable.ic_error_outline_white_36dp);
                    svc.addView(svc.errorIcon, pbParam);
                }

                svc.progressBar.setVisibility(View.INVISIBLE);
                svc.mySurfaceView.setVisibility(View.INVISIBLE);
                svc.errorIcon.setVisibility(ImageView.VISIBLE);
            }
        });*/
    }

    public void removeErrorIcon(final SurfaceViewComponent svc){
        /*((DeviceListActivity) mDeviceManager.currentContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(svc.errorIcon != null){
                    svc.mySurfaceView.setVisibility(View.VISIBLE);
                    svc.errorIcon.setVisibility(ImageView.INVISIBLE);
//                    svc.removeView(svc.errorIcon);
//                    svc.errorIcon = null;
                }
            }
        });*/
    }

    @Override
    public void onStartVideo(final SurfaceViewComponent svc) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(mDeviceManager.findDeviceById(svc.deviceId).isLogged)
                    svc.mPlayerHandler = FunSDK.MediaRealPlay(mUserID, svc.deviceConnection, svc.mySurfaceViewChannelId, svc.streamType, svc.mySurfaceView, svc.mySurfaceViewOrderId);
            }
        }).start();
    }

    @Override
    public void reOrderSurfaceViewComponents() {

    }

    @Override
    public int getChannelSelected(int gridPositionSelected) {
        return 0;
    }

    public void enableHD(SurfaceViewComponent svc){
        Log.d(TAG, "enableHD: " + svc.mySurfaceViewOrderId + " disable: " + hdChannel);
        if(hdChannel > -1) {
            disableHD(surfaceViewComponents.get(hdChannel));
        }
        hdChannel = svc.mySurfaceViewOrderId;
        svc.setStreamType(0);
        restartVideo(svc);

    }

    public void disableHD(SurfaceViewComponent svc){
        Log.d(TAG, "disableHD: " + svc.mySurfaceViewOrderId);
        hdChannel = -1;
        svc.setStreamType(1);
        restartVideo(svc);
    }
}
