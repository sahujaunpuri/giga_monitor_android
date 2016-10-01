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

import java.util.ArrayList;

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

    public ListComponent(Device mDevice) {
        this.mDevice = mDevice;
        this.surfaceViewComponents = new ArrayList<>();
        this.mContext = DeviceListActivity.mContext;
        this.numQuad = 1;
        this.lastNumQuad = 1;
        this.mDisplay = ((DeviceListActivity) mContext).getWindowManager().getDefaultDisplay();

        this.surfaceViewWidth = this.mDisplay.getWidth();
        this.surfaceViewHeight = this.mDisplay.getHeight()/3;
    }


    public void createComponents() {

        for(int i = 0; i < mDevice.getChannelNumber(); i++) {
            SurfaceViewComponent surfaceViewComponent = new SurfaceViewComponent();

            surfaceViewComponent.mySurfaceViewID = DeviceListActivity.mySurfaceViewID++;

            //Create MySurfaceView
            surfaceViewComponent.mySurfaceView = new MySurfaceView(mContext, 0);
            surfaceViewComponent.mySurfaceView.init(mContext, surfaceViewComponent.mySurfaceViewID);

            /*MySurfaceView.OnPlayStateListener a = new MySurfaceView.OnPlayStateListener() {
                @Override
                public void onPlayState(int i, int i1) {
                    Log.d("PlayState", "Mysurfaceview: " + i + ", Status: " + i1);
                }
            };

            surfaceViewComponent.mySurfaceView.setOnPlayStateListener(a);*/

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
        Log.v("TESTE","teste");
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

}
