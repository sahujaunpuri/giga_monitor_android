package br.inatel.icc.gigasecurity.gigamonitor.adapters;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.video.opengl.GLSurfaceView20;

import br.inatel.icc.gigasecurity.gigamonitor.R;
import br.inatel.icc.gigasecurity.gigamonitor.activities.DeviceListActivity;
import br.inatel.icc.gigasecurity.gigamonitor.core.DeviceManager;
import br.inatel.icc.gigasecurity.gigamonitor.managers.CustomGridLayoutManager;
import br.inatel.icc.gigasecurity.gigamonitor.model.Device;
import br.inatel.icc.gigasecurity.gigamonitor.model.SurfaceViewManager;
import br.inatel.icc.gigasecurity.gigamonitor.ui.OverlayMenu;
import br.inatel.icc.gigasecurity.gigamonitor.ui.SurfaceViewComponent;


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
    public final SurfaceViewManager surfaceViewManager;

    public ChannelRecyclerViewAdapter(Context mContext, Device mDevice, int numQuad, DeviceExpandableListAdapter.ChildViewHolder chieldViewHolder, SurfaceViewManager surfaceViewManager) {
        this.mContext = mContext;
        this.mDevice = mDevice;
        this.numQuad = numQuad;
        this.childViewHolder = chieldViewHolder;
        this.mDeviceManager  = DeviceManager.getInstance();
        this.surfaceViewManager = surfaceViewManager;
        surfaceViewManager.mRecyclerAdapter = this;
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

//            surfaceViewManager.reOrderSurfaceViewComponents();

            final SurfaceViewComponent currentSurfaceView = surfaceViewManager.surfaceViewComponents.get(position);

            myViewHolder.frameLayout.removeAllViews();
            ViewGroup parent = (ViewGroup) currentSurfaceView.getParent();

            if (parent != null) {
                parent.removeAllViews();
            }

//            currentSurfaceView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));

            currentSurfaceView.isLoading(true);
//            surfaceViewManager.onPlayLive(currentSurfaceView);
            surfaceViewManager.onStartVideo(currentSurfaceView);

            myViewHolder.frameLayout.addView(currentSurfaceView);

//            surfaceViewManager.changeSurfaceViewSize();
        } catch (IndexOutOfBoundsException e){
            Log.d(TAG, "onBindViewHolder: " + e.toString());
        }


    }

    @Override
    public int getItemCount() {
        return mDevice.getChannelNumber();
    }

    /*@Override
    public long getItemId(int position){
//        return surfaceViewManager.getChannelSelected(position);
        return position;
    }*/

    public void disableListScrolling(){
        childViewHolder.gridLayoutManager.setScrollEnabled(false);
    }

    public void enableListScrolling(){
        childViewHolder.gridLayoutManager.setScrollEnabled(true);
    }

    public void singleQuad(int position){
        if(surfaceViewManager.numQuad == 1 && surfaceViewManager.surfaceViewComponents.size() == 1) {
            return;
        }

        surfaceViewManager.resetScale();
        msvSelected = surfaceViewManager.getChannelSelected(position);
        surfaceViewManager.onStop(surfaceViewManager.surfaceViewComponents.get(msvSelected));
//        surfaceViewComponent.isLoading(true);

        if(surfaceViewManager.numQuad == 1) {
            surfaceViewManager.numQuad = surfaceViewManager.lastNumQuad;
        } else {
            surfaceViewManager.numQuad = 1;
            CustomGridLayoutManager lm = (CustomGridLayoutManager) childViewHolder.recyclerViewChannels.getLayoutManager();
            surfaceViewManager.lastFirstItemBeforeSelectChannel = lm.findFirstVisibleItemPosition();
        }
        childViewHolder.gridLayoutManager.setSpanCount(surfaceViewManager.numQuad);
        childViewHolder.mRecyclerAdapter.notifyDataSetChanged();

        if(surfaceViewManager.numQuad == 1) {
            childViewHolder.gridLayoutManager.scrollToPositionWithOffset((position), 0);
            surfaceViewManager.lastFirstVisibleItem = surfaceViewManager.getChannelSelected(position);
            surfaceViewManager.lastLastVisibleItem = surfaceViewManager.getChannelSelected(position);
        } else {
            childViewHolder.gridLayoutManager.scrollToPositionWithOffset(surfaceViewManager.lastFirstItemBeforeSelectChannel, 0);
//            childViewHolder.recyclerViewChannels.scrollToPosition(surfaceViewManager.lastFirstItemBeforeSelectChannel);
            surfaceViewManager.lastFirstVisibleItem = surfaceViewManager.lastFirstItemBeforeSelectChannel;
            surfaceViewManager.lastLastVisibleItem = surfaceViewManager.lastFirstItemBeforeSelectChannel + surfaceViewManager.numQuad;
        }
        surfaceViewManager.reOrderSurfaceViewComponents();

        surfaceViewManager.changeSurfaceViewSize();


    }

    public void openOverlayMenu(final SurfaceViewComponent surfaceViewComponent) {
        final OverlayMenu overlayMenu = childViewHolder.overlayMenu;

        ((DeviceListActivity) mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(overlayMenu.getVisibility() == View.VISIBLE) {
                    overlayMenu.setVisibility(View.GONE);
                }else {
                    overlayMenu.setSurfaceViewComponent(surfaceViewComponent);
                    overlayMenu.updateIcons();
                    overlayMenu.setVisibility(View.VISIBLE);
                }
            }
        });

    }







}
