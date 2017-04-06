package br.inatel.icc.gigasecurity.gigamonitor.model;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import br.inatel.icc.gigasecurity.gigamonitor.activities.DeviceListActivity;
import br.inatel.icc.gigasecurity.gigamonitor.core.DeviceManager;

/**
 * Created by filipecampos on 02/05/2016.
 */
public class ListComponent {

    public ArrayList<SurfaceViewComponent> surfaceViewComponents;
    public Device mDevice;
    public DeviceManager mDeviceManager;
    public Context mContext;
    public int numQuad, lastNumQuad;
    public int lastFirstVisibleItem;
    public int lastLastVisibleItem;
    public int lastFirstItemBeforeSelectChannel;

    private int[][] inverseMatrix = new int[][]{
            {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15},
            {0, 2, 1, 3, 4, 6, 5, 7, 8, 10, 9, 11, 12, 14, 13, 15},
            {0, 3, 6, 1, 4, 7, 2, 5, 8, 9, 12, 14, 10, 13, 15, 11},
            {0, 4, 8, 12, 1, 5, 9, 13, 2, 6, 10, 14, 3, 7, 11, 15}
    };

    public ListComponent(Device mDevice) {
        this.mDevice = mDevice;
        this.surfaceViewComponents = new ArrayList<>();
        this.mContext = DeviceListActivity.mContext;
        this.numQuad = 1;
        this.lastNumQuad = 1;
        this.mDeviceManager = DeviceManager.getInstance();

        this.lastFirstVisibleItem = 0;
        this.lastLastVisibleItem = 0;
    }


    public void createComponents() {

        for (int i = 0; i < mDevice.getChannelNumber(); i++) {
            SurfaceViewComponent surfaceViewComponent = new SurfaceViewComponent(mContext);

            surfaceViewComponent.mySurfaceViewChannelId = i;
            surfaceViewComponent.mySurfaceViewOrderId = i;
            surfaceViewComponent.deviceSn = mDevice.getSerialNumber();

            if(mDeviceManager.isFavorite(mDevice.getSerialNumber(), i))
                surfaceViewComponent.setFavorite(true);

            surfaceViewComponent.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));

            surfaceViewComponents.add(surfaceViewComponent);
        }

        this.reOrderSurfaceViewComponents();
    }



    public void changeSurfaceViewSize(SurfaceViewComponent surfaceViewComponent) {
        mDeviceManager.getScreenSize();
        int surfaceViewWidth = (mDeviceManager.screenWidth / numQuad);
        int surfaceViewHeight = ((mDeviceManager.screenHeight / 3) + 10) / numQuad;

        if (mContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            surfaceViewHeight = (mDeviceManager.screenHeight) / numQuad;
        }
        surfaceViewComponent.setViewSize(surfaceViewWidth, surfaceViewHeight, numQuad);
    }

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

    public int scrollToItem(int currentFirstVisibleItem, int currentLastVisibleItem, int amountScrolled) {
        int itemToScroll = 0;
        int totalQuads = numQuad*numQuad;
        if (totalQuads > 1) {
            if (currentLastVisibleItem % totalQuads == totalQuads - 1) {
                itemToScroll = currentLastVisibleItem;
            } else if (currentFirstVisibleItem % totalQuads == 0) {
                itemToScroll = currentFirstVisibleItem;
            } else if (currentLastVisibleItem == mDevice.getChannelNumber() - 1) {
                itemToScroll = currentLastVisibleItem;
            } else {
                itemToScroll = currentFirstVisibleItem;
            }
        } else if (totalQuads == 1) {
            if (lastFirstVisibleItem != currentFirstVisibleItem) {
                itemToScroll = currentFirstVisibleItem;
            } else if (lastLastVisibleItem != currentLastVisibleItem){
                itemToScroll = currentLastVisibleItem;
            }
        }
        this.lastFirstVisibleItem = currentFirstVisibleItem;
        this.lastLastVisibleItem = currentLastVisibleItem;

        this.handleVisibleChannels();
        return itemToScroll;
    }

    public void handleVisibleChannels() {
        if (this.lastLastVisibleItem - this.lastFirstVisibleItem == this.numQuad * this.numQuad - 1 || this.lastLastVisibleItem == this.surfaceViewComponents.size() - 1) {
            for (final SurfaceViewComponent svc : this.surfaceViewComponents) {
                if (svc.mySurfaceViewChannelId >= this.lastFirstVisibleItem && svc.mySurfaceViewChannelId <= lastLastVisibleItem) {
                    if (!svc.isPlaying /*&& svc.isConnected*/) {
                        svc.progressBar.setVisibility(View.VISIBLE);
                        svc.onStartVideo();
                    }
                } else {
                    if (svc.isPlaying && svc.isConnected) {
                        svc.onStop();
                    }
                }
            }
        }
    }

    public void stopChannels(int start){
        for(int i = start; i <surfaceViewComponents.size(); i++){
            if(surfaceViewComponents.get(i).isConnected)
                surfaceViewComponents.get(i).onStop();
        }
    }

    public void resetScale(){
        for(SurfaceViewComponent svc : surfaceViewComponents){
            if(svc.mScaleFactor > 1.F)
                svc.mySurfaceView.resetScaleInfo();
        }
    }

}
