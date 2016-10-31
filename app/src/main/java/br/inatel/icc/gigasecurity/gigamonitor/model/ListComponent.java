package br.inatel.icc.gigasecurity.gigamonitor.model;

import android.content.Context;
import android.content.res.Configuration;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.xm.ChnInfo;
import com.xm.video.MySurfaceView;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import br.inatel.icc.gigasecurity.gigamonitor.activities.DeviceListActivity;

/**
 * Created by filipecampos on 02/05/2016.
 */
public class ListComponent {

    public ArrayList<SurfaceViewComponent> surfaceViewComponents;
    public Device mDevice;
    public Context mContext;
    public int numQuad, lastNumQuad, surfaceViewWidth, surfaceViewHeight;
    public Display mDisplay;
    public int lastFirstVisibleItem;
    public int lastLastVisibleItem;
    public int lastFirstItemBeforeSelectChannel;

    private int[][] inverseMatrix = new int[][]{
            { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15 },
            { 0, 2, 1, 3, 4, 6, 5, 7, 8, 10, 9, 11, 12, 14, 13, 15 },
            { 0, 3, 6, 1, 4, 7, 2, 5, 8, 9, 12, 15, 10, 13, 11, 14 },
            { 0, 4, 8, 12, 1, 5, 9, 13, 2, 6, 10, 14, 3, 7, 11, 15 }
    };

    public ListComponent(Device mDevice) {
        this.mDevice = mDevice;
        this.surfaceViewComponents = new ArrayList<>();
        this.mContext = DeviceListActivity.mContext;
        this.numQuad = 1;
        this.lastNumQuad = 1;
        this.mDisplay = ((DeviceListActivity) mContext).getWindowManager().getDefaultDisplay();

        this.surfaceViewWidth = this.mDisplay.getWidth();
        this.surfaceViewHeight = this.mDisplay.getHeight()/3;
        this.lastFirstVisibleItem = 0;
        this.lastLastVisibleItem = 0;
    }


    public void createComponents() {

        for(int i = 0; i < mDevice.getChannelNumber(); i++) {
            SurfaceViewComponent surfaceViewComponent = new SurfaceViewComponent();

            surfaceViewComponent.mySurfaceViewID = DeviceListActivity.mySurfaceViewID++;

            surfaceViewComponent.mySurfaceViewChannelId = i;
            surfaceViewComponent.mySurfaceViewOrderId = i;

            //Create MySurfaceView
            surfaceViewComponent.mySurfaceView = new MySurfaceView(mContext, 0);
            surfaceViewComponent.mySurfaceView.init(mContext, surfaceViewComponent.mySurfaceViewID);

            surfaceViewComponent.mySurfaceView.setLayoutParams(new FrameLayout.LayoutParams(surfaceViewWidth, surfaceViewHeight));

            //Create ChannelInfo
            surfaceViewComponent.chnInfo = new ChnInfo();
            surfaceViewComponent.chnInfo.nStream = 1;

            //Create ProgressBar
            surfaceViewComponent.progressBar = new ProgressBar(mContext);
            surfaceViewComponent.progressBar.setIndeterminate(false);
            surfaceViewComponent.progressBar.setScrollBarStyle(View.SCROLLBARS_INSIDE_INSET);
            surfaceViewComponent.progressBar.setLayoutParams(new FrameLayout.LayoutParams(surfaceViewWidth/4, surfaceViewWidth/4, Gravity.CENTER));

            surfaceViewComponents.add(surfaceViewComponent);
        }
    }

    public void changeSurfaceViewSize(SurfaceViewComponent surfaceViewComponent, FrameLayout frameLayout) {
        surfaceViewWidth = (mDisplay.getWidth() / numQuad);
        surfaceViewHeight = ((mDisplay.getHeight() / 3) + 10) / numQuad;

        if(mContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            surfaceViewHeight = (mDisplay.getHeight()) / numQuad;
        }

        FrameLayout.LayoutParams lpSurfaceView = new FrameLayout.LayoutParams(surfaceViewWidth, surfaceViewHeight, Gravity.CENTER_HORIZONTAL);
        LinearLayout.LayoutParams lpFrameLayout = new LinearLayout.LayoutParams(surfaceViewWidth, surfaceViewHeight);
        FrameLayout.LayoutParams lpProgressBar = new FrameLayout.LayoutParams(surfaceViewWidth/4, surfaceViewHeight/4, Gravity.CENTER);


        surfaceViewComponent.mySurfaceView.setLayoutParams(lpSurfaceView);
        frameLayout.setLayoutParams(lpFrameLayout);
        surfaceViewComponent.progressBar.setLayoutParams(lpProgressBar);
    }

    public void reOrderSurfaceViewComponents() {
        for (SurfaceViewComponent svc : surfaceViewComponents) {
            svc.mySurfaceViewOrderId = inverseMatrix[numQuad-1][svc.mySurfaceViewChannelId];
        }

        Collections.sort(surfaceViewComponents, new Comparator<SurfaceViewComponent>() {
            public int compare(SurfaceViewComponent svc1, SurfaceViewComponent svc2) {
                return svc1.mySurfaceViewOrderId - svc2.mySurfaceViewOrderId;
            }
        });
    }

    public int getChannelSelected(int gridPositionSelected) {
        return inverseMatrix[numQuad-1][gridPositionSelected];
    }

    public int scrollToItem(int currentFirstVisibleItem, int currentLastVisibleItem) {
        int itemToScroll = 0;
        int totalQuads = 0;
        if (numQuad == 1) {
            totalQuads = 1;
        } else if (numQuad == 2) {
            totalQuads = 4;
        } else if (numQuad == 3) {
            totalQuads = 9;
        } else if (numQuad == 4) {
            totalQuads = 16;
        }
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

        //this.handleVisibleChannels(true);
        return itemToScroll;
    }


    public void handleVisibleChannels(boolean playVisible){
        //Log.v("Rocali", "Visible from " + this.lastFirstVisibleItem + " to " + this.lastLastVisibleItem + " numQuad "+this.numQuad);
        if (this.lastLastVisibleItem - this.lastFirstVisibleItem == this.numQuad*this.numQuad - 1) {
            Log.v("Rocali", "Visible from " + this.lastFirstVisibleItem + " to " + this.lastLastVisibleItem);
            for (SurfaceViewComponent svc : this.surfaceViewComponents) {
                if (playVisible && svc.mySurfaceViewChannelId >= this.lastFirstVisibleItem && svc.mySurfaceViewChannelId <= lastLastVisibleItem) {
                    if (!svc.isPlaying && svc.realPlayHandleID != 0) {

                        Log.v("Rocali", "Play " + svc.mySurfaceViewChannelId + " PHID "+svc.realPlayHandleID);
                        svc.mySurfaceView.onPlay();
                        svc.isPlaying = true;

                    }
                } else {
                    if (svc.isPlaying && svc.realPlayHandleID != 0) {

                        Log.v("Rocali", "Pause " + svc.mySurfaceViewChannelId + " PHID "+svc.realPlayHandleID);
                        svc.mySurfaceView.onPause();
                        svc.isPlaying = false;


                    }
                }
            }
        }
    }
}
