package br.inatel.icc.gigasecurity.gigamonitor.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import br.inatel.icc.gigasecurity.gigamonitor.R;
import br.inatel.icc.gigasecurity.gigamonitor.activities.DeviceListActivity;
import br.inatel.icc.gigasecurity.gigamonitor.core.DeviceManager;
import br.inatel.icc.gigasecurity.gigamonitor.managers.CustomGridLayoutManager;
import br.inatel.icc.gigasecurity.gigamonitor.model.ChannelsManager;
import br.inatel.icc.gigasecurity.gigamonitor.model.Device;
import br.inatel.icc.gigasecurity.gigamonitor.ui.OverlayMenu;
import br.inatel.icc.gigasecurity.gigamonitor.ui.OverlayPTZ;
import br.inatel.icc.gigasecurity.gigamonitor.ui.SurfaceViewComponent;


/**
 * Created by filipecampos on 28/04/2016.
 */
public class ChannelRecyclerViewAdapter extends RecyclerView.Adapter<ChannelRecyclerViewAdapter.MyViewHolder>  {

    private final String TAG = "RecyclerViewAdapter";
    public Device mDevice;
    public Context mContext;
    private static int msvSelected = -1;
    public DeviceManager mDeviceManager;
    private DeviceExpandableListAdapter.ChildViewHolder childViewHolder;
    public final ChannelsManager deviceChannelsManager;

    public ChannelRecyclerViewAdapter(Context mContext, Device mDevice, DeviceExpandableListAdapter.ChildViewHolder chieldViewHolder, ChannelsManager deviceChannelsManager) {
        this.mContext = mContext;
        this.mDevice = mDevice;
        this.childViewHolder = chieldViewHolder;
        this.mDeviceManager  = DeviceManager.getInstance();
        this.deviceChannelsManager = deviceChannelsManager;
        deviceChannelsManager.mRecyclerAdapter = this;
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

//            deviceChannelsManager.reOrderSurfaceViewComponents();

            final SurfaceViewComponent currentSurfaceView = deviceChannelsManager.surfaceViewComponents.get(position);

            myViewHolder.frameLayout.removeAllViews();
            ViewGroup parent = (ViewGroup) currentSurfaceView.getParent();

            if (parent != null) {
                parent.removeAllViews();
            }


            if (mDevice.isLogged){
                Log.d("ChannelsRecyclerViewAdapter", " Play device: " + mDevice.deviceName + " Channel: " + position);
                currentSurfaceView.isLoading(true);
                deviceChannelsManager.onStartVideo(currentSurfaceView);
            }

            myViewHolder.frameLayout.addView(currentSurfaceView);
        } catch (IndexOutOfBoundsException e){
            Log.d(TAG, "onBindViewHolder: " + e.toString());
        }


    }

    @Override
    public int getItemCount() {
        return mDevice.getChannelNumber();
    }

    @Override
    public long getItemId(int position){
//        return deviceChannelsManager.getChannelSelected(position);
        return position;
    }

    public void disableListScrolling(){
        childViewHolder.gridLayoutManager.setScrollEnabled(false);
    }

    public void enableListScrolling(){
        childViewHolder.gridLayoutManager.setScrollEnabled(true);
    }

    public void singleQuad(int position){
        if(deviceChannelsManager.numQuad == 1 && deviceChannelsManager.surfaceViewComponents.size() == 1) {
            return;
        }

        deviceChannelsManager.resetScale();
        msvSelected = deviceChannelsManager.getChannelSelected(position);
        deviceChannelsManager.onStop(deviceChannelsManager.surfaceViewComponents.get(msvSelected));
//        surfaceViewComponent.isLoading(true);

        if(deviceChannelsManager.numQuad == 1) {
            deviceChannelsManager.numQuad = deviceChannelsManager.lastNumQuad;
        } else {
            deviceChannelsManager.numQuad = 1;
            CustomGridLayoutManager lm = (CustomGridLayoutManager) childViewHolder.recyclerViewChannels.getLayoutManager();
            deviceChannelsManager.lastFirstItemBeforeSelectChannel = lm.findFirstVisibleItemPosition();
        }
        childViewHolder.gridLayoutManager.setSpanCount(deviceChannelsManager.numQuad);
        childViewHolder.mRecyclerAdapter.notifyDataSetChanged();

        if(deviceChannelsManager.numQuad == 1) {
            childViewHolder.gridLayoutManager.scrollToPositionWithOffset((position), 0);
            deviceChannelsManager.lastFirstVisibleItem = deviceChannelsManager.getChannelSelected(position);
            deviceChannelsManager.lastLastVisibleItem = deviceChannelsManager.getChannelSelected(position);
        } else {
            childViewHolder.gridLayoutManager.scrollToPositionWithOffset(deviceChannelsManager.lastFirstItemBeforeSelectChannel, 0);
//            childViewHolder.recyclerViewChannels.scrollToPosition(deviceChannelsManager.lastFirstItemBeforeSelectChannel);
            deviceChannelsManager.lastFirstVisibleItem = deviceChannelsManager.lastFirstItemBeforeSelectChannel;
            deviceChannelsManager.lastLastVisibleItem = deviceChannelsManager.lastFirstItemBeforeSelectChannel + deviceChannelsManager.numQuad;
        }
        deviceChannelsManager.reOrderSurfaceViewComponents();

        deviceChannelsManager.changeSurfaceViewSize();
    }

    public void openOverlayMenu(final SurfaceViewComponent surfaceViewComponent) {
        final OverlayMenu overlayMenu = childViewHolder.overlayMenu;

        ((DeviceListActivity) mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(overlayMenu.getVisibility() == View.VISIBLE) {
                    overlayMenu.setVisibility(View.GONE);
                    if (deviceChannelsManager.ptzChannel > -1) {
                        final OverlayPTZ overlayPTZ = childViewHolder.overlayPTZ;
                        overlayPTZ.setSurfaceViewComponent(surfaceViewComponent);
                        overlayPTZ.setVisibility(View.VISIBLE);
                        surfaceViewComponent.ptzOverlay = overlayPTZ;
                    }
                }else {
                    overlayMenu.setSurfaceViewComponent(surfaceViewComponent);
                    overlayMenu.updateIcons();
                    overlayMenu.setVisibility(View.VISIBLE);
                    if (deviceChannelsManager.ptzChannel > -1) {
                        final OverlayPTZ overlayPTZ = childViewHolder.overlayPTZ;
                        overlayPTZ.setSurfaceViewComponent(surfaceViewComponent);
                        overlayPTZ.setVisibility(View.GONE);
                    }
                }
            }
        });

    }

    public void closeOverlayMenu(){
        final OverlayMenu overlayMenu = childViewHolder.overlayMenu;
        if(overlayMenu.getVisibility() == View.VISIBLE)
            overlayMenu.setVisibility(View.GONE);
    }


}
