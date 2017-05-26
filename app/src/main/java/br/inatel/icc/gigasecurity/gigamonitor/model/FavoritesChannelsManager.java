package br.inatel.icc.gigasecurity.gigamonitor.model;

import com.lib.IFunSDKResult;
import com.video.opengl.GLSurfaceView20;

import java.util.ArrayList;
import java.util.HashMap;

import br.inatel.icc.gigasecurity.gigamonitor.ui.SurfaceViewComponent;

/**
 * Created by filipecampos on 02/05/2016.
 */
public class FavoritesChannelsManager extends ChannelsManager implements IFunSDKResult {
    String TAG = "FavoritesChannelsManager";

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
            ChannelsManager currentManager = mDeviceManager.findSurfaceViewManagerByDevice(currentDevice);
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

//        if(mDeviceManager.isFavorite(channelsManager.mDevice.getId(), i))
            surfaceViewComponent.isFavorite = channelsManager.surfaceViewComponents.get(i).isFavorite;

//            surfaceViewComponent.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        surfaceViewComponent.setLayoutParams(surfaceViewLayout);

        surfaceViewComponents.add(surfaceViewComponent);
    }

    @Override
    public void reOrderSurfaceViewComponents() {

    }

    @Override
    public int getChannelSelected(int gridPositionSelected) {
        return 0;
    }
}
