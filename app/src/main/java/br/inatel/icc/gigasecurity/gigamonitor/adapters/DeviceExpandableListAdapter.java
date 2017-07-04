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
import android.util.Log;
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
    private final String TAG = "ExpListAdapter";
    private LayoutInflater mInflater;
    public ArrayList<Device> mDevices;
    public Context mContext;
    private DeviceManager mDeviceManager;
    public ArrayList<GroupViewHolder> groupViewHolder;
    public ArrayList<ChildViewHolder> childViewHolder;
    private ExpandableListView mExpandableListView;
    private int scroll;

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


    private GroupViewHolder initGroupViewHolder(final int groupPosition, View convertView, ViewGroup parent){
        GroupViewHolder groupViewHolder = new GroupViewHolder();

        groupViewHolder.convertView  = convertView;
        groupViewHolder.blank        = mInflater.inflate(R.layout.blank_layout, parent, false);
        groupViewHolder.ivQuad       = (ImageView) convertView.findViewById(R.id.iv_grid_list_device);
        groupViewHolder.ivMore       = (ImageView) convertView.findViewById(R.id.iv_device_more);
        groupViewHolder.ivIndicator  = (ImageView) convertView.findViewById(R.id.iv_indicator);
        groupViewHolder.tvDeviceName = (TextView) convertView.findViewById(R.id.tv_hostname_list_device);
        groupViewHolder.mDevice      = mDevices.get(groupPosition);
        groupViewHolder.position    = groupPosition;
        groupViewHolder.tvDeviceName.setText(groupViewHolder.mDevice.deviceName);
        groupViewHolder.ivMore.setOnClickListener(createMoreListener(groupPosition));
        groupViewHolder.ivQuad.setOnClickListener(createQuadListener(groupPosition));

        this.groupViewHolder.add(groupViewHolder);

        return groupViewHolder;
    }


    private ChildViewHolder initChildViewHolder(ViewGroup parent, int groupPosition){
        ChildViewHolder currentChildViewHolder = childViewHolder.get(groupPosition);

        currentChildViewHolder.convertView              =  mInflater.inflate(R.layout.expandable_list_view_child, parent, false);
        currentChildViewHolder.recyclerViewChannels     =  (RecyclerView) currentChildViewHolder.convertView.findViewById(R.id.recycler_view_channels);
        currentChildViewHolder.tvMessage                =  (TextView) currentChildViewHolder.convertView.findViewById(R.id.tv_message_connecting);
        currentChildViewHolder.overlayMenu              =  (OverlayMenu) currentChildViewHolder.convertView.findViewById(R.id.overlay_menu);
        currentChildViewHolder.tvMessage                =  (TextView) currentChildViewHolder.convertView.findViewById(R.id.tv_message_connecting);
        currentChildViewHolder.position                 =   groupPosition;
        currentChildViewHolder.overlayMenu.setDeviceChannelsManager(mDeviceManager.getDeviceChannelsManagers().get(groupPosition));
        currentChildViewHolder.overlayMenu.setLayoutParams(currentChildViewHolder.recyclerViewChannels.getLayoutParams());

        if(groupViewHolder.get(groupPosition).mDevice.getSerialNumber().equals("Favoritos")){
            currentChildViewHolder.tvMessage.setText("Nenhum favorito adicionado.");
        }else{
            currentChildViewHolder.tvMessage.setText(groupViewHolder.get(groupPosition).mDevice.message);
        }

//        this.childViewHolder.add(currentChildViewHolder);
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
//                        deviceChannelsManager.currentPage = deviceChannelsManager.pageNumber(deviceChannelsManager.lastFirstVisibleItem);
//                        childViewHolder.get(groupPosition).gridLayoutManager.scrollToPositionWithOffset(deviceChannelsManager.firstItemOfPage(deviceChannelsManager.currentPage), 0);
//                        Log.d(TAG, "run: " + deviceChannelsManager.currentPage + " " + deviceChannelsManager.lastFirstVisibleItem + " " + deviceChannelsManager.firstItemOfPage(deviceChannelsManager.currentPage));
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
                    ChannelsManager mChannelManager = mDeviceManager.getDeviceChannelsManagers().get(groupPosition);
                    int itemToScroll = mChannelManager.lastFirstVisibleItem;
                    int pageToScroll = mChannelManager.currentPage;
                    final int currentFirstVisibleItem = currentChildViewHolder.gridLayoutManager.findFirstVisibleItemPosition();
                    final int currentLastVisibleItem = currentChildViewHolder.gridLayoutManager.findLastVisibleItemPosition();

//                    if(scroll > mDeviceManager.screenWidth/3 /*currentFirstVisibleItem > mChannelManager.lastFirstVisibleItem*/){ //nextpage
////                        itemToScroll = mChannelManager.lastFirstVisibleItem + (mChannelManager.numQuad*mChannelManager.numQuad);
//                        pageToScroll++;
//                    }else if(scroll < -(mDeviceManager.screenWidth/3) /*currentFirstVisibleItem < mChannelManager.lastFirstVisibleItem*/){ //previouspage
//                        pageToScroll--;
////                        itemToScroll = mChannelManager.lastFirstVisibleItem - (mChannelManager.numQuad*mChannelManager.numQuad);
//                    }
//                    scroll = 0;
//                    itemToScroll = mChannelManager.firstItemOfPage(pageToScroll);
//                    mChannelManager.lastFirstVisibleItem = itemToScroll;
//                    mChannelManager.currentPage = pageToScroll;
//                    currentChildViewHolder.gridLayoutManager.scrollToPositionWithOffset(itemToScroll, 0);
//                    currentChildViewHolder.gridLayoutManager.smoothScrollToPosition(currentChildViewHolder.recyclerViewChannels, null, itemToScroll);
                    itemToScroll = mChannelManager.scrollToItem(currentFirstVisibleItem, currentLastVisibleItem);
                    if(mChannelManager.lastFirstVisibleItem != mChannelManager.lastLastVisibleItem || currentFirstVisibleItem != currentLastVisibleItem) {
//                        currentChildViewHolder.gridLayoutManager.smoothScrollToPosition(currentChildViewHolder.recyclerViewChannels, null, itemToScroll);
                        currentChildViewHolder.recyclerViewChannels.smoothScrollToPosition(itemToScroll);
                        Log.d(TAG, "onScrollStateChanged: scrolled to " + itemToScroll);
                    }
//                    scroll = 0;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                Log.d(TAG, "onScrolled: dx" + dx + " dy" + dy);
//                scroll +=dx;
//                if(Math.abs(scroll)>mDeviceManager.screenWidth)
//                    recyclerView.stopScroll();

                super.onScrolled(recyclerView, dx, dy);
            }
        };
    }

    @Override
    public View getGroupView(final int groupPosition, final boolean isExpanded, View convertView, ViewGroup parent) {
        if(convertView == null || groupViewHolder.size() <= groupPosition){
            convertView = mInflater.inflate(R.layout.expandable_list_view_row, parent, false);
            GroupViewHolder groupViewHolder = initGroupViewHolder(groupPosition, convertView, parent);
            groupViewHolder.blank.setTag(groupViewHolder);
            convertView.setTag(groupViewHolder);
            childViewHolder.add(new ChildViewHolder());
        }
        GroupViewHolder groupViewHolder = (GroupViewHolder) convertView.getTag();
        if(groupViewHolder.position != groupPosition)
            groupViewHolder = this.groupViewHolder.get(groupPosition);
        if((mContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE && DeviceListActivity.previousGroup != -1)){
            return groupViewHolder.blank;
        } else {
            return groupViewHolder.convertView;
        }
    }

    @Override
    public View getChildView(final int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ChildViewHolder childViewHolder;
        if(convertView == null || this.childViewHolder.get(groupPosition).recyclerViewChannels == null){
            childViewHolder = initChildViewHolder(parent, groupPosition);
            convertView = childViewHolder.convertView;
            initGridRecycler(groupPosition, childViewHolder);
            convertView.setTag(childViewHolder);
        } else if (((ChildViewHolder) convertView.getTag()).position != groupPosition){
            childViewHolder = this.childViewHolder.get(groupPosition);
            convertView = childViewHolder.convertView;
        } else {
            childViewHolder = (ChildViewHolder) convertView.getTag();
        }

        GroupViewHolder currentGroupViewHolder = groupViewHolder.get(groupPosition);


        if(!currentGroupViewHolder.mDevice.isLogged) {
            loginDevice(currentGroupViewHolder.mDevice, currentGroupViewHolder, childViewHolder, groupPosition);
        } else {
            updateChildView(mDevices.get(groupPosition), currentGroupViewHolder, childViewHolder, groupPosition);
            childViewHolder.gridLayoutManager.scrollToPosition(mDeviceManager.getDeviceChannelsManagers().get(groupPosition).lastFirstVisibleItem);
            showExpanded(groupPosition, currentGroupViewHolder, childViewHolder);
        }

        setLayoutSize(groupPosition, childViewHolder);
        return convertView;
    }

    private void showExpanded(int groupPosition, final GroupViewHolder groupViewHolder, final ChildViewHolder childViewHolder) {
        ((DeviceListActivity) mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                
                childViewHolder.tvMessage.setVisibility(View.GONE);
                groupViewHolder.ivQuad.setVisibility(View.INVISIBLE);
                groupViewHolder.ivIndicator.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_indicator_minus));
                if (groupViewHolder.mDevice.getChannelNumber() == 0) {
                    if(groupViewHolder.mDevice.getSerialNumber().equals("Favoritos"))
                        childViewHolder.tvMessage.setText("Nenhum favorito adicionado.");
                    else
                        childViewHolder.tvMessage.setText("Nenhum canal encontrado.");
                    childViewHolder.tvMessage.setVisibility(View.VISIBLE);
                    childViewHolder.recyclerViewChannels.setVisibility(View.GONE);
                    groupViewHolder.ivMore.setVisibility(View.INVISIBLE);
                }
                else if (groupViewHolder.mDevice.getChannelNumber() > 0 && groupViewHolder.mDevice.isLogged) {
                    childViewHolder.recyclerViewChannels.setVisibility(View.VISIBLE);
                    groupViewHolder.ivMore.setVisibility(View.VISIBLE);
                    if(groupViewHolder.mDevice.getChannelNumber() > 1 )
                        groupViewHolder.ivQuad.setVisibility(View.VISIBLE);
                }
            }
        });
    }

//    @Override
//    public void onGroupExpanded(int groupPosition){
//        super.onGroupExpanded(groupPosition);
//    }

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
//                currentChildViewHolder.recyclerViewChannels = null;
            }
        });

//        mDeviceManager.getDeviceChannelsManagers().get(groupPosition).clearSurfaceViewComponents();

        mDeviceManager.removeLoginListener(currentGroupViewHolder.mDevice.getId());
        mDeviceManager.clearStart();

//        onChangeOrientation(groupPosition);
    }

    private void setLayoutSize(final int groupPosition, final ChildViewHolder childViewHolder) {
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
                childViewHolder.convertView.getLayoutParams().height = height;
                childViewHolder.convertView.getLayoutParams().width = width;
//                childViewHolder.recyclerViewChannels.getLayoutParams().height = height;
//                childViewHolder.recyclerViewChannels.getLayoutParams().width = width;
//                childViewHolder.overlayMenu.getLayoutParams().height = height;
//                childViewHolder.overlayMenu.getLayoutParams().width = width;
//                childViewHolder.overlayMenu.requestLayout();
//                childViewHolder.gridLayoutManager.requestLayout();
                mDeviceManager.getDeviceChannelsManagers().get(groupPosition).resetScale();
            }
        });
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    private void updateChildView(Device mDevice, GroupViewHolder groupViewHolder, ChildViewHolder childViewHolder, int position){
        /*if(mDevice.getChannelNumber()>1){
            mDeviceManager.getDeviceChannelsManagers().get(position).numQuad = 2;
            mDeviceManager.getDeviceChannelsManagers().get(position).lastNumQuad = 2;
        }*/
        if(mDeviceManager.getDeviceChannelsManagers().get(position).mySurfaceViews.isEmpty()) {
            updateGrid(position, mDeviceManager.getDeviceChannelsManagers().get(position));
        }

//        showExpanded(position, groupViewHolder, childViewHolder);
    }

    public void updateGrid(int position, ChannelsManager channelsManager){
        if(childViewHolder.get(position).gridLayoutManager != null) {
            initGridRecycler(position, childViewHolder.get(position));
            channelsManager.createComponents();
            childViewHolder.get(position).gridLayoutManager.setSpanCount(channelsManager.numQuad);
            childViewHolder.get(position).recyclerViewChannels.setVisibility(View.VISIBLE);
            childViewHolder.get(position).recyclerViewChannels.getAdapter().notifyDataSetChanged();
//            notifyDataSetChanged();
        }
        if(channelsManager.surfaceViewComponents.isEmpty() && childViewHolder.get(position).recyclerViewChannels != null) {
            childViewHolder.get(position).recyclerViewChannels.setVisibility(View.INVISIBLE);
            childViewHolder.get(position).tvMessage.setVisibility(View.VISIBLE);
        }
    }

    private void loginDevice(final Device mDevice, final GroupViewHolder groupViewHolder, final ChildViewHolder childViewHolder, final int position) {
        mDeviceManager.loginDevice(mDevice, new LoginDeviceListener() {
            @Override
            public void onLoginSuccess(Device device) {
                mDevice.isLogged = true;
                mDeviceManager.clearStart();
                updateGrid(position, mDeviceManager.getDeviceChannelsManagers().get(position));
                showExpanded(position, groupViewHolder, childViewHolder);
                childViewHolder.gridLayoutManager.scrollToPosition(mDeviceManager.getDeviceChannelsManagers().get(position).lastFirstVisibleItem);

//                mDeviceManager.getDeviceChannelsManagers().get(position).createComponents();
//                childViewHolder.gridLayoutManager.setSpanCount(mDeviceManager.getDeviceChannelsManagers().get(position).numQuad);
//                childViewHolder.recyclerViewChannels.getAdapter().notifyDataSetChanged();
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
        if(!groupViewHolder.mDevice.getSerialNumber().equals("Favoritos"))
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
                                                    mExpandableListView.collapseGroup(groupPosition);
                                                    mDeviceManager.collapse = groupPosition;
                                                    startPlaybackActivity(groupViewHolder.mDevice);
                                                    break;
                                            }
                                        }
                                    });
                    builder.show();
                }
            });
        else
            ((DeviceListActivity) mContext).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    builder.setTitle("")
                            .setItems(new CharSequence[]{"Limpar Favoritos"},
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            switch (which) {
                                                case 0:
                                                    mDeviceManager.cleanFavorites();
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
//        extras.putSerializable("device", mDevice);
        extras.putSerializable("device", mDevice.getId());

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

    public void collapseGroup(final int position){
        if(mExpandableListView.isGroupExpanded(position))
            ((Activity)mContext).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mExpandableListView.collapseGroup(position);
                }
            });

    }

    public void setMessage(final int position, final String message){
        if(childViewHolder.size() > position && childViewHolder.get(position).tvMessage != null)
            ((Activity)mContext).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    childViewHolder.get(position).tvMessage.setText(message);
                    childViewHolder.get(position).tvMessage.invalidate();
                }
            });
    }


    public class GroupViewHolder {
        public TextView tvDeviceName;
        public Device mDevice;
        public ImageView ivIndicator;
        public ImageView ivMore;
        public ImageView ivQuad;
        public View convertView;
        public View blank;
        public int position;
    }

    public class ChildViewHolder {
        public View convertView;
        public RecyclerView recyclerViewChannels;
        public OverlayMenu overlayMenu;
        public TextView tvMessage;
        public ChannelRecyclerViewAdapter mRecyclerAdapter;
        public CustomGridLayoutManager gridLayoutManager;
        public int position;
    }

}
