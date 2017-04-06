package br.inatel.icc.gigasecurity.gigamonitor.adapters;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TimingLogger;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import br.inatel.icc.gigasecurity.gigamonitor.R;
import br.inatel.icc.gigasecurity.gigamonitor.activities.DeviceListActivity;
import br.inatel.icc.gigasecurity.gigamonitor.core.DeviceManager;
import br.inatel.icc.gigasecurity.gigamonitor.model.Device;
import br.inatel.icc.gigasecurity.gigamonitor.model.ListComponent;
import br.inatel.icc.gigasecurity.gigamonitor.model.SurfaceViewComponent;
import br.inatel.icc.gigasecurity.gigamonitor.ui.OverlayMenu;


/**
 * Created by filipecampos on 28/04/2016.
 */
public class ChannelRecyclerViewAdapter extends RecyclerView.Adapter<ChannelRecyclerViewAdapter.MyViewHolder>  {

    private final String TAG = "RecyclerViewAdapter";
    public Device mDevice;
    public Context mContext;
    private static int numQuad, msvSelected = -1,positionSelected = -1;
    public DeviceManager mDeviceManager;
    private DeviceExpandableListAdapter.ChildViewHolder childViewHolder;
    private long clickTime, lastClickTime = 0;
    public boolean doubleClick = false;
    public final Handler handler = new Handler(Looper.getMainLooper());
    public final ListComponent listComponent;

    public ChannelRecyclerViewAdapter(Context mContext, Device mDevice, int numQuad, DeviceExpandableListAdapter.ChildViewHolder chieldViewHolder, ListComponent listComponent) {
        this.mContext = mContext;
        this.mDevice = mDevice;
        this.numQuad = numQuad;
        this.childViewHolder = chieldViewHolder;
        this.mDeviceManager  = DeviceManager.getInstance();
        this.listComponent = listComponent;
    }

    public ChannelRecyclerViewAdapter getAdapter(){
        return this;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public FrameLayout frameLayout;

        public MyViewHolder(View view) {
            super(view);
            frameLayout     = (FrameLayout) view.findViewById(R.id.frame_layout_channel_recycler_view);
        }
    }


    @Override
    public ChannelRecyclerViewAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.channel_recycler_view_layout, parent, false);

        MyViewHolder newMyViewHolder = new MyViewHolder(itemView);
        return newMyViewHolder;
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
    }

    @Override
    public void onViewRecycled(MyViewHolder holder) {
        super.onViewRecycled(holder);

    }

    @Override
    public void onViewDetachedFromWindow(MyViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
    }



    @Override
    public void onBindViewHolder(final ChannelRecyclerViewAdapter.MyViewHolder myViewHolder, final int position) {

        try{ //TODO detectar porque esta sendo chamado quando tenta conectar pela segunda vez a um dispositivo offline
            final SurfaceViewComponent currentSurfaceView = listComponent.surfaceViewComponents.get(position);
            currentSurfaceView.mRecyclerAdapter = getAdapter();

            myViewHolder.frameLayout.removeAllViews();
            ViewGroup parent = (ViewGroup) currentSurfaceView.getParent();

            if (parent != null) {
                parent.removeAllViews();
            }

            myViewHolder.frameLayout.addView(currentSurfaceView);

            currentSurfaceView.progressBar.setVisibility(View.VISIBLE);

            if (!currentSurfaceView.isConnected)
                currentSurfaceView.onStartVideo();
//            currentSurfaceView.onPlayLive();

            listComponent.changeSurfaceViewSize(currentSurfaceView);
        } catch (IndexOutOfBoundsException e){
            Log.d(TAG, "onBindViewHolder: " + e.toString());
        }


    }

    @Override
    public int getItemCount() {
        return mDevice.getChannelNumber();
    }

/*    @Override
    public long getItemId(int position){
        return position;
    }*/

    public void disableListScrolling(){
        childViewHolder.gridLayoutManager.setScrollEnabled(false);
    }

    public void enableListScrolling(){
        childViewHolder.gridLayoutManager.setScrollEnabled(true);
    }

    public void singleQuad(SurfaceViewComponent surfaceViewComponent, int position){
        listComponent.resetScale();
        msvSelected = listComponent.getChannelSelected(position);
        surfaceViewComponent.progressBar.setVisibility(View.VISIBLE);

        if(listComponent.numQuad == 1) {
            listComponent.numQuad = listComponent.lastNumQuad;
        } else {
            listComponent.numQuad = 1;
            GridLayoutManager lm = (GridLayoutManager) childViewHolder.recyclerViewChannels.getLayoutManager();
            listComponent.lastFirstItemBeforeSelectChannel = lm.findFirstVisibleItemPosition();
        }
        childViewHolder.gridLayoutManager.setSpanCount(listComponent.numQuad);
        childViewHolder.mRecyclerAdapter.notifyDataSetChanged();

        if(listComponent.numQuad == 1) {
            childViewHolder.gridLayoutManager.scrollToPositionWithOffset(listComponent.getChannelSelected(position), 0);
            listComponent.lastFirstVisibleItem = listComponent.getChannelSelected(position);
            listComponent.lastLastVisibleItem = listComponent.getChannelSelected(position);
        } else {
            childViewHolder.gridLayoutManager.scrollToPositionWithOffset(listComponent.lastFirstItemBeforeSelectChannel, 0);
//            childViewHolder.recyclerViewChannels.scrollToPosition(listComponent.lastFirstItemBeforeSelectChannel);
            listComponent.lastFirstVisibleItem = listComponent.lastFirstItemBeforeSelectChannel;
            listComponent.lastLastVisibleItem = listComponent.lastFirstItemBeforeSelectChannel + listComponent.numQuad;
        }


    }

    public void openOverlayMenu(SurfaceViewComponent surfaceViewComponent, int channelPosition) {

        OverlayMenu overlayMenu = childViewHolder.overlayMenu;
        if(overlayMenu.getVisibility() == View.VISIBLE) {
            overlayMenu.setVisibility(View.INVISIBLE);
        }else {
            overlayMenu.setSurfaceViewComponent(surfaceViewComponent);
            overlayMenu.updateIcons();
            overlayMenu.setVisibility(View.VISIBLE);
        }

    }







}
