package br.inatel.icc.gigasecurity.gigamonitor.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import br.inatel.icc.gigasecurity.gigamonitor.R;
import br.inatel.icc.gigasecurity.gigamonitor.activities.DeviceListActivity;
import br.inatel.icc.gigasecurity.gigamonitor.activities.DevicePlaybackActivity;
import br.inatel.icc.gigasecurity.gigamonitor.activities.DeviceRemoteControlActivity;
import br.inatel.icc.gigasecurity.gigamonitor.config.ConfigMenuActivity;
import br.inatel.icc.gigasecurity.gigamonitor.core.DeviceManager;
import br.inatel.icc.gigasecurity.gigamonitor.listeners.LoginDeviceListener;
import br.inatel.icc.gigasecurity.gigamonitor.managers.CustomGridLayoutManager;
import br.inatel.icc.gigasecurity.gigamonitor.model.Device;
import br.inatel.icc.gigasecurity.gigamonitor.model.SurfaceViewManager;
import br.inatel.icc.gigasecurity.gigamonitor.ui.OverlayMenu;
import br.inatel.icc.gigasecurity.gigamonitor.ui.SurfaceViewComponent;

import static android.support.v7.widget.RecyclerView.SCROLL_STATE_IDLE;

/**
 * Created by filipecampos on 30/05/2016.
 */
public class DeviceExpandableListAdapter extends BaseExpandableListAdapter {

    private LayoutInflater mInflater;
    private ArrayList<Device> mDevices;
    private Context mContext;
    private DeviceManager mDeviceManager;
    private ArrayList<GroupViewHolder> groupViewHolder;
    private ArrayList<ChildViewHolder> childViewHolder;
    private int amountScrolled = 0;

    public DeviceExpandableListAdapter(Context mContext, ArrayList<Device> mDevices) {
        this.mContext        = mContext;
        this.mDevices        = mDevices;
        init();

    }

    public void init() {
        this.mDeviceManager     = DeviceManager.getInstance();
        this.mInflater          = LayoutInflater.from(mContext);
        this.groupViewHolder    = new ArrayList<GroupViewHolder>(mDevices.size());
        this.childViewHolder    = new ArrayList<ChildViewHolder>(mDevices.size());
    }

    public void setDevices(ArrayList<Device> devices){
        mDevices = devices;
    }

    public void removeGroup(int position){
        childViewHolder.remove(position);
        groupViewHolder.remove(position);
        notifyDataSetChanged();
    }

    @Override
    public int getGroupCount() {
        return mDevices.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mDevices.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return null;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    private GroupViewHolder initGroupViewHolder(final int groupPosition, ViewGroup parent){
        GroupViewHolder groupView = new GroupViewHolder();

        groupView.convertView = mInflater.inflate(R.layout.expandable_list_view_row, parent, false);

        groupView.ivQuad       = (ImageView) groupView.convertView.findViewById(R.id.iv_grid_list_device);
        groupView.ivMore       = (ImageView) groupView.convertView.findViewById(R.id.iv_device_more);
        groupView.ivIndicator  = (ImageView) groupView.convertView.findViewById(R.id.iv_indicator);
        groupView.tvDeviceName = (TextView) groupView.convertView.findViewById(R.id.tv_hostname_list_device);
        groupView.progressBar  = (ProgressBar) groupView.convertView.findViewById(R.id.pb_expandable_list);
        groupView.mDevice      = mDevices.get(groupPosition);

        groupView.tvDeviceName.setText(groupView.mDevice.deviceName);

        groupViewHolder.add(groupView);
        return groupView;
    }

    private ChildViewHolder initChildViewHolder(ViewGroup parent, int groupPosition){
        ChildViewHolder currentChildViewHolder = new ChildViewHolder();

        currentChildViewHolder.convertView          =   (ViewGroup) mInflater.inflate(R.layout.expandable_list_view_child, parent, false);
        currentChildViewHolder.recyclerViewChannels =   (RecyclerView) currentChildViewHolder.convertView.findViewById(R.id.recycler_view_channels);
        currentChildViewHolder.tvMessage            =   (TextView) currentChildViewHolder.convertView.findViewById(R.id.tv_message_connecting);
        currentChildViewHolder.overlayMenu          =    new OverlayMenu(mContext, mDeviceManager.getSurfaceViewManagers().get(groupPosition));
        currentChildViewHolder.overlayMenu.setVisibility(View.GONE);
        currentChildViewHolder.convertView.addView(currentChildViewHolder.overlayMenu);
        currentChildViewHolder.recyclerViewChannels.setVisibility(View.GONE);

        childViewHolder.add(currentChildViewHolder);
        return currentChildViewHolder;

    }

    private void initGridRecycler(int groupPosition, ChildViewHolder childViewHolder){
        childViewHolder.gridLayoutManager = new CustomGridLayoutManager(mContext, mDeviceManager.getSurfaceViewManagers().get(groupPosition).numQuad, GridLayoutManager.HORIZONTAL, false);
        childViewHolder.recyclerViewChannels.setLayoutManager(childViewHolder.gridLayoutManager);
        childViewHolder.mRecyclerAdapter = new ChannelRecyclerViewAdapter(mContext, groupViewHolder.get(groupPosition).mDevice, mDeviceManager.getSurfaceViewManagers().get(groupPosition).numQuad, childViewHolder, mDeviceManager.getSurfaceViewManagers().get(groupPosition));
//        childViewHolder.mRecyclerAdapter.setHasStableIds(true);
        childViewHolder.recyclerViewChannels.setAdapter(childViewHolder.mRecyclerAdapter);
        childViewHolder.recyclerViewChannels.setHasFixedSize(true);
    }

    private void showExpanded(int groupPosition, GroupViewHolder groupViewHolder, ChildViewHolder childViewHolder) {
        childViewHolder.tvMessage.setVisibility(View.GONE);
        groupViewHolder.ivQuad.setVisibility(View.INVISIBLE);

        if (groupViewHolder.mDevice.getChannelNumber() <= 0) { //Wrong Password
            childViewHolder.tvMessage.setText("Nenhum canal encontrado.");
            childViewHolder.tvMessage.setVisibility(View.VISIBLE);
            childViewHolder.recyclerViewChannels.setVisibility(View.GONE);
            groupViewHolder.ivMore.setVisibility(View.INVISIBLE);
        }
        else if (groupViewHolder.mDevice.getChannelNumber() > 0 && groupViewHolder.mDevice.isLogged) {
            childViewHolder.recyclerViewChannels.setVisibility(View.VISIBLE);
            groupViewHolder.ivQuad.setVisibility(View.VISIBLE);
            groupViewHolder.ivMore.setVisibility(View.VISIBLE);
        }
        childViewHolder.recyclerViewChannels.setOnScrollListener(createOnScrollListener(groupPosition));

    }

    private View.OnClickListener createMoreListener(final int groupPosition){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMoreDialog(groupViewHolder.get(groupPosition),groupPosition);
            }
        };
    }

    private View.OnClickListener createQuadListener(final int groupPosition){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SurfaceViewManager surfaceViewManager = mDeviceManager.getSurfaceViewManagers().get(groupPosition);
                surfaceViewManager.numQuad = nextNumQuad(surfaceViewManager.numQuad, groupViewHolder.get(groupPosition).mDevice.getChannelNumber());
                surfaceViewManager.lastNumQuad = surfaceViewManager.numQuad;
                if(surfaceViewManager.numQuad == 1)
                    surfaceViewManager.stopChannels(1);

                childViewHolder.get(groupPosition).gridLayoutManager.setSpanCount(mDeviceManager.getSurfaceViewManagers().get(groupPosition).numQuad);
                childViewHolder.get(groupPosition).mRecyclerAdapter.notifyDataSetChanged();
                surfaceViewManager.resetScale();
            }
        };
    }


    private RecyclerView.OnScrollListener createOnScrollListener(final int groupPosition){
        return new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrollStateChanged(final RecyclerView recyclerView,final int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if(newState == SCROLL_STATE_IDLE) {
                    ChildViewHolder currentChildViewHolder = childViewHolder.get(groupPosition);
                    final int itemToScroll;
                    final int currentFirstVisibleItem = currentChildViewHolder.gridLayoutManager.findFirstVisibleItemPosition();
                    final int currentLastVisibleItem = currentChildViewHolder.gridLayoutManager.findLastVisibleItemPosition();
                    itemToScroll = mDeviceManager.getSurfaceViewManagers().get(groupPosition).scrollToItem(currentFirstVisibleItem, currentLastVisibleItem, amountScrolled);
                    currentChildViewHolder.gridLayoutManager.smoothScrollToPosition(currentChildViewHolder.recyclerViewChannels, null, itemToScroll);
                }
            }

//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy){
////                if(scrolled)
////                    amountScrolled += dx;
//            }
        };
    }

    @Override
    public View getGroupView(final int groupPosition, final boolean isExpanded, View convertView, ViewGroup parent) {
        GroupViewHolder currentGroupViewHolder = null;
        if(groupViewHolder.size() > groupPosition)
            currentGroupViewHolder = groupViewHolder.get(groupPosition);
        if(currentGroupViewHolder == null) {
            currentGroupViewHolder = initGroupViewHolder(groupPosition, parent);
            initChildViewHolder(parent, groupPosition);
        }
        if(mContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE && DeviceListActivity.previousGroup != -1) {
//            currentGroupViewHolder.convertView = mInflater.inflate(R.layout.blank_layout, parent, false);
            return new FrameLayout(mContext);
        } else {
            currentGroupViewHolder.ivMore.setOnClickListener(createMoreListener(groupPosition));
            currentGroupViewHolder.ivQuad.setOnClickListener(createQuadListener(groupPosition));
        }
        return currentGroupViewHolder.convertView;
    }

    @Override
    public View getChildView(final int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        GroupViewHolder currentGroupViewHolder = null;
        ChildViewHolder currentChildViewHolder = null;
        if(groupViewHolder.size() > groupPosition)
            currentGroupViewHolder = groupViewHolder.get(groupPosition);
        if(childViewHolder.size() > groupPosition)
            currentChildViewHolder = childViewHolder.get(groupPosition);
        if(currentChildViewHolder != null) {
            if(!currentGroupViewHolder.mDevice.isLogged) {
                //Inicializando grid e recycler temporariamente, apenas para não dar crash se não conectar
                currentChildViewHolder.tvMessage.setText("Conectando...");
                initGridRecycler(groupPosition, currentChildViewHolder);
                loginDevice(currentGroupViewHolder.mDevice, currentGroupViewHolder, currentChildViewHolder, groupPosition);
            } else {
                showExpanded(groupPosition, currentGroupViewHolder, currentChildViewHolder);
            }
            currentChildViewHolder.gridLayoutManager.scrollToPosition(mDeviceManager.getSurfaceViewManagers().get(groupPosition).lastFirstVisibleItem);
            setLayoutSize(groupPosition);

        }

        return currentChildViewHolder.convertView;
    }

    @Override
    public void onGroupExpanded(int groupPosition){
        GroupViewHolder currentGroupViewHolder = groupViewHolder.get(groupPosition);
        ChildViewHolder currentChildViewHolder = childViewHolder.get(groupPosition);
        currentGroupViewHolder.ivIndicator.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_indicator_minus));
        if(currentGroupViewHolder.mDevice.getChannelNumber() > 1 && currentGroupViewHolder.mDevice.isLogged) {
            currentGroupViewHolder.ivMore.setVisibility(View.VISIBLE);
            currentGroupViewHolder.ivQuad.setVisibility(View.VISIBLE);
            currentChildViewHolder.recyclerViewChannels.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onGroupCollapsed(int groupPosition){
        stopChannels(groupPosition);
        GroupViewHolder currentGroupViewHolder = groupViewHolder.get(groupPosition);
        ChildViewHolder currentChildViewHolder = childViewHolder.get(groupPosition);
        currentGroupViewHolder.ivMore.setVisibility(View.INVISIBLE);
        currentGroupViewHolder.ivIndicator.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_indicator_plus));
        currentGroupViewHolder.ivQuad.setVisibility(View.INVISIBLE);
        currentChildViewHolder.recyclerViewChannels.setVisibility(View.INVISIBLE);
        onChangeOrientation(groupPosition);
    }

    private void setLayoutSize(int groupPosition) {
        int viewWidth;
        int viewHeight;
        DisplayMetrics displayMetrics = Resources.getSystem().getDisplayMetrics();

        if(mContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            viewWidth = displayMetrics.widthPixels;
            viewHeight = displayMetrics.heightPixels;
        } else{
            viewWidth = displayMetrics.widthPixels;
            viewHeight = ((displayMetrics.heightPixels / 3)+10);
        }

        childViewHolder.get(groupPosition).overlayMenu.getLayoutParams().height = viewHeight;
        childViewHolder.get(groupPosition).overlayMenu.getLayoutParams().width = viewWidth;
        childViewHolder.get(groupPosition).overlayMenu.requestLayout();

        FrameLayout.LayoutParams lpRecyclerView = new FrameLayout.LayoutParams(viewWidth, viewHeight, Gravity.CENTER);
        childViewHolder.get(groupPosition).convertView.setLayoutParams(lpRecyclerView);
        childViewHolder.get(groupPosition).recyclerViewChannels.setLayoutParams(lpRecyclerView);
        mDeviceManager.getSurfaceViewManagers().get(groupPosition).resetScale();
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    private void loginDevice(final Device mDevice, final GroupViewHolder groupViewHolder, final ChildViewHolder childViewHolder, final int position) {
        mDeviceManager.loginDevice(mDevice, new LoginDeviceListener() {
            @Override
            public void onLoginSuccess(Device device) {
                ((DeviceListActivity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mDevice.isLogged = true;
                        if(mDevice.getChannelNumber()>1){
                            mDeviceManager.getSurfaceViewManagers().get(position).numQuad = 2;
                            mDeviceManager.getSurfaceViewManagers().get(position).lastNumQuad = 2;
                        }
                        mDeviceManager.getSurfaceViewManagers().get(position).createComponents();
                        childViewHolder.gridLayoutManager.setSpanCount(mDeviceManager.getSurfaceViewManagers().get(position).numQuad);
                        showExpanded(position, groupViewHolder, childViewHolder);
                        childViewHolder.recyclerViewChannels.getAdapter().notifyDataSetChanged();
                    }
                });
            }

            @Override
            public void onLoginError(final long error, Device device) {
                if(device != null && device.getId() == mDevice.getId()) {
                    ((DeviceListActivity) mContext).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mDevice.isLogged = false;
                            groupViewHolder.ivQuad.setVisibility(View.INVISIBLE);
                            groupViewHolder.ivMore.setVisibility(View.INVISIBLE);
                            childViewHolder.recyclerViewChannels.setVisibility(View.GONE);
                            childViewHolder.tvMessage.setVisibility(View.VISIBLE);
                            String errorMsg;
                            if(error == -11301)
                                errorMsg = "Login ou senha incorretos.";
                            else if(error == -11307)
                                errorMsg = "Dispositivo não encontrado.";
                            else
                                errorMsg = "Dispositivo offline.";
                            childViewHolder.tvMessage.setText(errorMsg);
                        }
                    });
                }
            }

            @Override
            public void onLogout() {
            }

        });
    }

    public void onChangeOrientation(int groupPosition){
        childViewHolder.get(groupPosition).gridLayoutManager = new CustomGridLayoutManager(mContext, mDeviceManager.getSurfaceViewManagers().get(groupPosition).numQuad, GridLayoutManager.HORIZONTAL, false);
        childViewHolder.get(groupPosition).recyclerViewChannels.setLayoutManager(childViewHolder.get(groupPosition).gridLayoutManager);
//        if(mContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
            childViewHolder.get(groupPosition).recyclerViewChannels.setHasFixedSize(true);
    }

    private void showMoreDialog(final GroupViewHolder groupViewHolder, final int groupPosition) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("")
                .setItems(new CharSequence[]{"Configurações", "Controle Remoto", "Playback"},
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        startSettingsActivity(groupViewHolder.mDevice);
                                        break;
                                    case 1:
                                        startDeviceRemoteControlActivity(groupViewHolder.mDevice);
                                        break;
                                    case 2:
                                        startPlaybackActivity(groupViewHolder.mDevice);
                                        break;
                                }
                            }
                        });
        builder.show();
    }

    private void startSettingsActivity(Device mDevice) {
//        if (mDevice.getLoginID() != 0) {
            Bundle extras = new Bundle();
            extras.putSerializable("device", mDevice.getSerialNumber());

            Intent intent = new Intent(mContext, ConfigMenuActivity.class);
            intent.putExtras(extras);

            mContext.startActivity(intent);
//        }
    }

    private void startPlaybackActivity(Device mDevice) {
        Bundle extras = new Bundle();
        extras.putSerializable("device", mDevice);

        Intent intent = new Intent(mContext, DevicePlaybackActivity.class);
        intent.putExtras(extras);

        mContext.startActivity(intent);
    }

    private void startDeviceRemoteControlActivity(Device mDevice) {
        Bundle extras = new Bundle();
        extras.putSerializable("device", mDevice);

        Intent intent = new Intent(mContext, DeviceRemoteControlActivity.class);
        intent.putExtras(extras);

        mContext.startActivity(intent);
    }

    public int nextNumQuad(int numQuad,int totalChannels) {
        int nextNumQuad = 1;
        if (numQuad == 1) {
            nextNumQuad = 2;
        } else if (numQuad == 2) {
            if(totalChannels > 4) {
                nextNumQuad = 3;
            } else {
                nextNumQuad = 1;
            }
        } else if (numQuad == 3) {
            if(totalChannels > 9) {
                nextNumQuad = 4;
            } else {
                nextNumQuad = 1;
            }
        } else if (numQuad == 4) {
            nextNumQuad = 1;
        }
        return nextNumQuad;
    }

    public void stopChannels(int groupPosition) {
        for (SurfaceViewComponent svc : mDeviceManager.getSurfaceViewManagers().get(groupPosition).surfaceViewComponents) {
            if(svc.isConnected())
                mDeviceManager.getSurfaceViewManagers().get(groupPosition).onStop(svc);
        }
    }

    public void pauseChannels(int groupPosition) {
        for (SurfaceViewComponent svc : mDeviceManager.getSurfaceViewManagers().get(groupPosition).surfaceViewComponents) {
            if (svc.isPlaying && svc.isConnected()) {
                mDeviceManager.getSurfaceViewManagers().get(groupPosition).onPause(svc);
            }
        }
    }

    public void playChannels(int groupPosition) {
        for (SurfaceViewComponent svc : mDeviceManager.getSurfaceViewManagers().get(groupPosition).surfaceViewComponents) {
            if (!svc.isPlaying && svc.isConnected()) {
                mDeviceManager.getSurfaceViewManagers().get(groupPosition).onPlayLive(svc);
            }
        }
    }

    public void resumeChannels(int groupPosition) {
        for (SurfaceViewComponent svc : mDeviceManager.getSurfaceViewManagers().get(groupPosition).surfaceViewComponents) {
            if (svc.isConnected()) {
                mDeviceManager.getSurfaceViewManagers().get(groupPosition).onResume(svc);
            }
        }
    }


    public class GroupViewHolder {
        public TextView tvDeviceName;
        public Device mDevice;
        public ImageView ivIndicator;
        public ImageView ivMore;
        public ImageView ivQuad;
        public ProgressBar progressBar;
        public View convertView;
    }

    public class ChildViewHolder {
        public ViewGroup convertView;
        public RecyclerView recyclerViewChannels;
        public OverlayMenu overlayMenu;
        public RelativeLayout layoutMenu;
        public ImageView ivHQ, ivPlayPause, ivSnapshot, ivSnapvideo, ivFavorite, ivSendAudio, ivReceiveAudio;
        public TextView tvChnNumber, tvMessage;
        public ChannelRecyclerViewAdapter mRecyclerAdapter;

        public  CustomGridLayoutManager gridLayoutManager;

    }

}
