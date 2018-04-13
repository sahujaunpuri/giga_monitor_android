package br.inatel.icc.gigasecurity.gigamonitor.model;

import android.util.Log;
import android.view.View;

import com.lib.FunSDK;
import com.lib.IFunSDKResult;
import com.video.opengl.GLSurfaceView20;

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
        changeSurfaceViewSize(mDevice.getChannelsManager().lastExpand);
    }

    public void createComponent(ChannelsManager channelsManager, int i, int position){
        try {
            GLSurfaceView20 mySurfaceView;
            mySurfaceView = new GLSurfaceView20(mContext);
            mySurfaceView.setLayoutParams(surfaceViewLayout);
            mySurfaceViews.add(mySurfaceView);

            SurfaceViewComponent surfaceViewComponent = new SurfaceViewComponent(mContext, this, position);
            surfaceViewComponent.mySurfaceViewChannelId = i;
            surfaceViewComponent.mySurfaceViewOrderId = position;
            if (channelsManager.mDevice == null) {
                return;
            }
            surfaceViewComponent.deviceConnection = channelsManager.mDevice.connectionString;
            surfaceViewComponent.deviceId = channelsManager.mDevice.getId();

            if(mDeviceManager.isFavorite(channelsManager.mDevice.getId(), i))
                surfaceViewComponent.isFavorite = true;

//            surfaceViewComponent.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
            surfaceViewComponent.setLayoutParams(surfaceViewLayout);
            surfaceViewComponents.add(surfaceViewComponent);


        } catch (Exception error) {
            error.printStackTrace();
            return;
        }
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
    }


    @Override
    public void onStartVideo(final SurfaceViewComponent svc) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (mDeviceManager.findDeviceById(svc.deviceId).isLogged)
                        svc.mPlayerHandler = FunSDK.MediaRealPlay(mUserID, svc.deviceConnection, svc.mySurfaceViewChannelId, svc.streamType, svc.mySurfaceView, svc.mySurfaceViewOrderId);
                } catch (Exception error) {
                    error.printStackTrace();
                }
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

    @Override
    public void enablePTZ(boolean enabled, SurfaceViewComponent svc){
        int channel = svc.mySurfaceViewOrderId;
        if(enabled){
            if(ptzChannel > -1 && ptzChannel != channel) {
                surfaceViewComponents.get(channel).ptzOverlay = surfaceViewComponents.get(ptzChannel).ptzOverlay;
                surfaceViewComponents.get(ptzChannel).disablePTZ();
                Log.d(TAG, "enablePTZ: enabled / disable ptzChannel:" + ptzChannel + " channel: " + channel);
            }
            ptzChannel = channel;
        }else if(ptzChannel > -1){
            if(surfaceViewComponents.get(ptzChannel).ptzOverlay != null)
                surfaceViewComponents.get(ptzChannel).ptzOverlay.setVisibility(View.GONE);
            surfaceViewComponents.get(ptzChannel).disablePTZ();
            Log.d(TAG, "enablePTZ: disabled / disable ptzChannel:" + ptzChannel + " channel: " + channel);
            ptzChannel = -1;
        }
    }
}
