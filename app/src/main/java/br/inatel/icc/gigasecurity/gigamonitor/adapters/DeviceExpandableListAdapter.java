package br.inatel.icc.gigasecurity.gigamonitor.adapters;

import android.app.Activity;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import br.inatel.icc.gigasecurity.gigamonitor.model.ChannelsManager;
import br.inatel.icc.gigasecurity.gigamonitor.model.Device;
import br.inatel.icc.gigasecurity.gigamonitor.model.DeviceChannelsManager;
import br.inatel.icc.gigasecurity.gigamonitor.ui.OverlayMenu;
import br.inatel.icc.gigasecurity.gigamonitor.ui.SurfaceViewComponent;

/**
 * Created by filipecampos on 30/05/2016.
 */
public class DeviceExpandableListAdapter extends BaseExpandableListAdapter {

    private LayoutInflater mInflater;
    public ArrayList<Device> mDevices;
    public Context mContext;
    private DeviceManager mDeviceManager;
    public ArrayList<GroupViewHolder> groupViewHolder;
    private ArrayList<ChildViewHolder> childViewHolder;
    public ExpandableListView mExpandableListView;

    public DeviceExpandableListAdapter(Context mContext, ArrayList<Device> mDevices, ExpandableListView mExpandableListView) {
        this.mContext            = mContext;
        this.mDevices            = mDevices;
        this.mExpandableListView = mExpandableListView;
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
        notifyDataSetChanged();
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

        groupView.ivMore.setOnClickListener(createMoreListener(groupPosition));
        groupView.ivQuad.setOnClickListener(createQuadListener(groupPosition));

        groupViewHolder.add(groupView);

        return groupView;
    }

    private ChildViewHolder initChildViewHolder(ViewGroup parent, int groupPosition){
        ChildViewHolder currentChildViewHolder = new ChildViewHolder();

        currentChildViewHolder.convertView          =   (ViewGroup) mInflater.inflate(R.layout.expandable_list_view_child, parent, false);
        currentChildViewHolder.recyclerViewChannels =   (RecyclerView) currentChildViewHolder.convertView.findViewById(R.id.recycler_view_channels);
        currentChildViewHolder.tvMessage            =   (TextView) currentChildViewHolder.convertView.findViewById(R.id.tv_message_connecting);
        currentChildViewHolder.overlayMenu          =    new OverlayMenu(mContext, mDeviceManager.getDeviceChannelsManagers().get(groupPosition));
        currentChildViewHolder.overlayMenu.setVisibility(View.GONE);
        currentChildViewHolder.convertView.addView(currentChildViewHolder.overlayMenu);
        currentChildViewHolder.recyclerViewChannels.setVisibility(View.GONE);

        childViewHolder.add(currentChildViewHolder);
        return currentChildViewHolder;

    }

    private void initGridRecycler(int groupPosition, ChildViewHolder childViewHolder){
        childViewHolder.gridLayoutManager = new CustomGridLayoutManager(mContext, mDeviceManager.getDeviceChannelsManagers().get(groupPosition).numQuad, GridLayoutManager.HORIZONTAL, false);
        childViewHolder.recyclerViewChannels.setLayoutManager(childViewHolder.gridLayoutManager);
        childViewHolder.mRecyclerAdapter = new ChannelRecyclerViewAdapter(mContext, groupViewHolder.get(groupPosition).mDevice, childViewHolder, mDeviceManager.getDeviceChannelsManagers().get(groupPosition));
//        childViewHolder.mRecyclerAdapter.setHasStableIds(true);
        childViewHolder.recyclerViewChannels.setAdapter(childViewHolder.mRecyclerAdapter);
//        childViewHolder.recyclerViewChannels.setHasFixedSize(true);
        childViewHolder.recyclerViewChannels.setItemViewCacheSize(0);
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
                ((Activity)mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ChannelsManager deviceChannelsManager = mDeviceManager.getDeviceChannelsManagers().get(groupPosition);
                        deviceChannelsManager.numQuad = nextNumQuad(deviceChannelsManager.numQuad, groupViewHolder.get(groupPosition).mDevice.getChannelNumber());
                        deviceChannelsManager.lastNumQuad = deviceChannelsManager.numQuad;
                        deviceChannelsManager.stopChannels(0);
                        mDeviceManager.clearStart();

                        childViewHolder.get(groupPosition).gridLayoutManager.setSpanCount(mDeviceManager.getDeviceChannelsManagers().get(groupPosition).numQuad);
                        childViewHolder.get(groupPosition).mRecyclerAdapter.notifyDataSetChanged();
                        deviceChannelsManager.changeSurfaceViewSize();

                        deviceChannelsManager.resetScale();
                        deviceChannelsManager.reOrderSurfaceViewComponents();
                    }
                });

            }
        };
    }


    private RecyclerView.OnScrollListener createOnScrollListener(final int groupPosition){
        return new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrollStateChanged(final RecyclerView recyclerView,final int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if(newState == 0) {
                    final ChildViewHolder currentChildViewHolder = childViewHolder.get(groupPosition);
                    final int itemToScroll;
                    final int currentFirstVisibleItem = currentChildViewHolder.gridLayoutManager.findFirstVisibleItemPosition();
                    final int currentLastVisibleItem = currentChildViewHolder.gridLayoutManager.findLastVisibleItemPosition();
                    ChannelsManager mChannelManager = mDeviceManager.getDeviceChannelsManagers().get(groupPosition);
                    itemToScroll = mChannelManager.scrollToItem(currentFirstVisibleItem, currentLastVisibleItem);
                    if(mChannelManager.lastFirstVisibleItem != mChannelManager.lastLastVisibleItem || currentFirstVisibleItem != currentLastVisibleItem)
                        currentChildViewHolder.gridLayoutManager.smoothScrollToPosition(currentChildViewHolder.recyclerViewChannels, null, itemToScroll);
                }
            }
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
        if((mContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE && DeviceListActivity.previousGroup != -1)){
//            currentGroupViewHolder.convertView = mInflater.inflate(R.layout.blank_layout, parent, false);
            return new FrameLayout(mContext);
        } else{
            currentGroupViewHolder.ivMore.setOnClickListener(createMoreListener(groupPosition));
            currentGroupViewHolder.ivQuad.setOnClickListener(createQuadListener(groupPosition));
        }
        return currentGroupViewHolder.convertView;
    }

    @Override
    public View getChildView(final int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        GroupViewHolder currentGroupViewHolder = null;
        ChildViewHolder currentChildViewHolder = null;
        if(groupViewHolder.size() > groupPosition) {
            currentGroupViewHolder = groupViewHolder.get(groupPosition);
            currentGroupViewHolder.mDevice = mDevices.get(groupPosition);
        }if(childViewHolder.size() > groupPosition)
            currentChildViewHolder = childViewHolder.get(groupPosition);
        if(currentChildViewHolder != null) {
            if(!currentGroupViewHolder.mDevice.isLogged) {
                currentChildViewHolder.tvMessage.setText("Conectando...");
                currentChildViewHolder.tvMessage.setVisibility(View.VISIBLE);
                currentChildViewHolder.recyclerViewChannels.setVisibility(View.INVISIBLE);
                initGridRecycler(groupPosition, currentChildViewHolder);
                loginDevice(currentGroupViewHolder.mDevice, currentGroupViewHolder, currentChildViewHolder, groupPosition);
            } else if(mDeviceManager.getDeviceChannelsManagers().get(groupPosition).surfaceViewComponents.isEmpty()){
                initGridRecycler(groupPosition, currentChildViewHolder);
                updateChildView(mDevices.get(groupPosition), currentGroupViewHolder, currentChildViewHolder, groupPosition);
            } else {
                initGridRecycler(groupPosition, currentChildViewHolder);
                updateChildView(mDevices.get(groupPosition), currentGroupViewHolder, currentChildViewHolder, groupPosition);
//                showExpanded(groupPosition, currentGroupViewHolder, currentChildViewHolder);
                currentChildViewHolder.gridLayoutManager.scrollToPosition(mDeviceManager.getDeviceChannelsManagers().get(groupPosition).lastFirstVisibleItem);
            }
            setLayoutSize(groupPosition);
        }
        return currentChildViewHolder.convertView;
    }

    private void showExpanded(int groupPosition, final GroupViewHolder groupViewHolder, final ChildViewHolder childViewHolder) {
        ((DeviceListActivity) mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                
                childViewHolder.tvMessage.setVisibility(View.GONE);
                groupViewHolder.ivQuad.setVisibility(View.INVISIBLE);
                groupViewHolder.ivIndicator.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_indicator_minus));
                if (groupViewHolder.mDevice.getChannelNumber() == 0) {
                    childViewHolder.tvMessage.setText("Nenhum canal encontrado.");
                    childViewHolder.tvMessage.setVisibility(View.VISIBLE);
                    childViewHolder.recyclerViewChannels.setVisibility(View.GONE);
                    groupViewHolder.ivMore.setVisibility(View.INVISIBLE);
                }
                else if (groupViewHolder.mDevice.getChannelNumber() > 0 && groupViewHolder.mDevice.isLogged) {
                    childViewHolder.recyclerViewChannels.setVisibility(View.VISIBLE);
                    groupViewHolder.ivMore.setVisibility(View.VISIBLE);
                    if(groupViewHolder.mDevice.getChannelNumber() > 1)
                        groupViewHolder.ivQuad.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public void onGroupExpanded(int groupPosition){
        super.onGroupExpanded(groupPosition);
        final GroupViewHolder currentGroupViewHolder = groupViewHolder.get(groupPosition);
        if(currentGroupViewHolder.mDevice.isLogged && !mDeviceManager.getDeviceChannelsManagers().get(groupPosition).surfaceViewComponents.isEmpty()) {
            mDeviceManager.getDeviceChannelsManagers().get(groupPosition).createComponents();
        }
    }

    @Override
    public void onGroupCollapsed(int groupPosition){
        super.onGroupCollapsed(groupPosition);
        stopChannels(groupPosition);
        final GroupViewHolder currentGroupViewHolder = groupViewHolder.get(groupPosition);
        final ChildViewHolder currentChildViewHolder = childViewHolder.get(groupPosition);
        ((DeviceListActivity) mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                currentGroupViewHolder.ivMore.setVisibility(View.INVISIBLE);
                currentGroupViewHolder.ivIndicator.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_indicator_plus));
                currentGroupViewHolder.ivQuad.setVisibility(View.INVISIBLE);
                currentChildViewHolder.recyclerViewChannels.setVisibility(View.INVISIBLE);
                currentChildViewHolder.overlayMenu.setVisibility(View.GONE);
                currentChildViewHolder.recyclerViewChannels.removeAllViewsInLayout();
            }
        });

        mDeviceManager.getDeviceChannelsManagers().get(groupPosition).clearSurfaceViewComponents();

        mDeviceManager.removeLoginListener(currentGroupViewHolder.mDevice.connectionString);
        mDeviceManager.clearStart();

//        onChangeOrientation(groupPosition);
    }

    private void setLayoutSize(final int groupPosition) {
        DisplayMetrics displayMetrics = Resources.getSystem().getDisplayMetrics();
        int viewWidth = displayMetrics.widthPixels;
        int viewHeight;

        if(displayMetrics.widthPixels%2 != 0)
            viewWidth -= 1;

        if(mContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            viewHeight = displayMetrics.heightPixels;
        } else{
            viewHeight = ((displayMetrics.heightPixels / 3)+10);
        }

        final int width = viewWidth;
        final int height = viewHeight;

        ((Activity)mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                childViewHolder.get(groupPosition).overlayMenu.getLayoutParams().height = height;
                childViewHolder.get(groupPosition).overlayMenu.getLayoutParams().width = width;
                childViewHolder.get(groupPosition).overlayMenu.requestLayout();

                FrameLayout.LayoutParams lpRecyclerView = new FrameLayout.LayoutParams(width, height);
                childViewHolder.get(groupPosition).convertView.setLayoutParams(lpRecyclerView);
                childViewHolder.get(groupPosition).recyclerViewChannels.setLayoutParams(lpRecyclerView);
                childViewHolder.get(groupPosition).gridLayoutManager.requestLayout();
                mDeviceManager.getDeviceChannelsManagers().get(groupPosition).resetScale();
            }
        });
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    private void updateChildView(Device mDevice, GroupViewHolder groupViewHolder, ChildViewHolder childViewHolder, int position){
        if(mDevice.getChannelNumber()>1){
            mDeviceManager.getDeviceChannelsManagers().get(position).numQuad = 2;
            mDeviceManager.getDeviceChannelsManagers().get(position).lastNumQuad = 2;
        }
        if(mDeviceManager.getDeviceChannelsManagers().get(position).mySurfaceViews.isEmpty()) {
            mDeviceManager.getDeviceChannelsManagers().get(position).createComponents();
        }
        childViewHolder.gridLayoutManager.setSpanCount(mDeviceManager.getDeviceChannelsManagers().get(position).numQuad);
        childViewHolder.recyclerViewChannels.getAdapter().notifyDataSetChanged();
        showExpanded(position, groupViewHolder, childViewHolder);
    }

    private void loginDevice(final Device mDevice, final GroupViewHolder groupViewHolder, final ChildViewHolder childViewHolder, final int position) {
        mDeviceManager.loginDevice(mDevice, new LoginDeviceListener() {
            @Override
            public void onLoginSuccess(Device device) {
                        mDevice.isLogged = true;
                        updateChildView(mDevice, groupViewHolder, childViewHolder, position);
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
                            else if(error == -1)
                                errorMsg = "Dispositivo offline.";
                            else
                                errorMsg = "Erro de conexão";
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
        mDeviceManager.getDeviceChannelsManagers().get(groupPosition).changeSurfaceViewSize();
    }

    private void showMoreDialog(final GroupViewHolder groupViewHolder, final int groupPosition) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        ((DeviceListActivity) mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
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
                                                onGroupCollapsed(groupPosition);
                                                startPlaybackActivity(groupViewHolder.mDevice);
                                                break;
                                        }
                                    }
                                });
                builder.show();
            }
        });


    }

    private void startSettingsActivity(Device mDevice) {
//        if (mDevice.getLoginID() != 0) {
            Bundle extras = new Bundle();
            extras.putSerializable("device", mDevice.getId());

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
        for (SurfaceViewComponent svc : mDeviceManager.getDeviceChannelsManagers().get(groupPosition).surfaceViewComponents) {
            if(svc.isConnected())
                mDeviceManager.getDeviceChannelsManagers().get(groupPosition).onStop(svc);
        }
    }

    public void pauseChannels(int groupPosition) {
        for (SurfaceViewComponent svc : mDeviceManager.getDeviceChannelsManagers().get(groupPosition).surfaceViewComponents) {
            if (svc.isPlaying && svc.isConnected()) {
                mDeviceManager.getDeviceChannelsManagers().get(groupPosition).onPause(svc);
            }
        }
    }

    public void playChannels(int groupPosition) {
        for (SurfaceViewComponent svc : mDeviceManager.getDeviceChannelsManagers().get(groupPosition).surfaceViewComponents) {
            if (!svc.isPlaying && svc.isConnected()) {
                mDeviceManager.getDeviceChannelsManagers().get(groupPosition).onPlayLive(svc);
            }
        }
    }

    public void resumeChannels(int groupPosition) {
        for (SurfaceViewComponent svc : mDeviceManager.getDeviceChannelsManagers().get(groupPosition).surfaceViewComponents) {
            if (svc.isConnected()) {
                mDeviceManager.getDeviceChannelsManagers().get(groupPosition).onResume(svc);
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
        public TextView tvMessage;
        public ChannelRecyclerViewAdapter mRecyclerAdapter;

        public  CustomGridLayoutManager gridLayoutManager;

    }

}
