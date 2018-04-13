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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import br.inatel.icc.gigasecurity.gigamonitor.R;
import br.inatel.icc.gigasecurity.gigamonitor.activities.DeviceListActivity;
import br.inatel.icc.gigasecurity.gigamonitor.activities.DevicePlaybackActivity;
import br.inatel.icc.gigasecurity.gigamonitor.activities.DeviceRemoteControlActivity;
import br.inatel.icc.gigasecurity.gigamonitor.activities.FavoritesDevicesListActivity;
import br.inatel.icc.gigasecurity.gigamonitor.config.ConfigMenuActivity;
import br.inatel.icc.gigasecurity.gigamonitor.core.DeviceManager;
import br.inatel.icc.gigasecurity.gigamonitor.listeners.LoginDeviceListener;
import br.inatel.icc.gigasecurity.gigamonitor.managers.CustomGridLayoutManager;
import br.inatel.icc.gigasecurity.gigamonitor.model.ChannelsManager;
import br.inatel.icc.gigasecurity.gigamonitor.model.Device;
import br.inatel.icc.gigasecurity.gigamonitor.ui.CustomTypeDialog;
import br.inatel.icc.gigasecurity.gigamonitor.ui.OverlayMenu;
import br.inatel.icc.gigasecurity.gigamonitor.ui.OverlayPTZ;
import br.inatel.icc.gigasecurity.gigamonitor.ui.SurfaceViewComponent;

/**
 * Created by filipecampos on 30/05/2016.
 */
public class DeviceExpandableListAdapter extends BaseExpandableListAdapter {
    private LayoutInflater mInflater;
    public ArrayList<Device> mDevices = new ArrayList<>();;
    public Context mContext;
    private DeviceManager mDeviceManager;
    public ArrayList<GroupViewHolder> groupViewHolder;
    public ArrayList<ChildViewHolder> childViewHolder;
    private ExpandableListView mExpandableListView;
    String TAG = "DeviceExpandableListAdapter";

    public DeviceExpandableListAdapter(Context mContext, ArrayList<Device> mDevices, ExpandableListView mExpandableListView) {
        this.mContext            = mContext;
        this.mExpandableListView = mExpandableListView;
        for (Device device: mDevices) {
            if(device.isEnable() && !mDevices.contains(device)) {
                this.mDevices.add(device);
            }
        }
        init();

    }

    public void init() {
        this.mDeviceManager     = DeviceManager.getInstance();
        this.mInflater          = LayoutInflater.from(mContext);
        this.groupViewHolder    = new ArrayList<GroupViewHolder>(mDevices.size());
        this.childViewHolder    = new ArrayList<ChildViewHolder>(mDevices.size());
    }

    public void setDevices(ArrayList<Device> devices){
        for (Device device: devices) {
            if(device.isEnable() && !mDevices.contains(device)) {
                mDevices.add(device);
            }
        }
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
        groupViewHolder.blank        = getBlankView(mInflater,parent);
        groupViewHolder.ivQuad       = (ImageView) convertView.findViewById(R.id.iv_grid_list_device);
        groupViewHolder.ivMore       = (ImageView) convertView.findViewById(R.id.iv_device_more);
        groupViewHolder.ivAddMore    = (ImageView) convertView.findViewById(R.id.iv_add_fav);
        groupViewHolder.ivRefresh    = (ImageView) convertView.findViewById(R.id.iv_refresh);
        groupViewHolder.ivOtimizar   = (ImageView) convertView.findViewById(R.id.iv_otimizar);
        groupViewHolder.tvDeviceName = (TextView) convertView.findViewById(R.id.tv_hostname_list_device);
        groupViewHolder.mDevice      = mDevices.get(groupPosition);
        groupViewHolder.position    = groupPosition;
        groupViewHolder.tvDeviceName.setText(groupViewHolder.mDevice.deviceName);
        groupViewHolder.ivMore.setOnClickListener(createMoreListener(groupPosition));
        groupViewHolder.ivQuad.setOnClickListener(createQuadListener(groupPosition));
        groupViewHolder.ivAddMore.setOnClickListener(openFavoritesList());
        groupViewHolder.ivRefresh.setOnClickListener(refreshDeviceConnection(groupViewHolder));
        groupViewHolder.ivOtimizar.setOnClickListener(otimizarDevice(groupViewHolder));

        this.groupViewHolder.add(groupViewHolder);

        return groupViewHolder;
    }

    public static View getBlankView(LayoutInflater mInflater,ViewGroup parent){
        return mInflater.inflate(R.layout.blank_layout, parent, false);
    }

    private ChildViewHolder initChildViewHolder(ViewGroup parent, int groupPosition){
        try {
            ChildViewHolder currentChildViewHolder = childViewHolder.get(groupPosition);
            currentChildViewHolder.convertView = mInflater.inflate(R.layout.expandable_list_view_child, parent, false);
            currentChildViewHolder.recyclerViewChannels = (RecyclerView) currentChildViewHolder.convertView.findViewById(R.id.recycler_view_channels);
            currentChildViewHolder.tvMessage = (TextView) currentChildViewHolder.convertView.findViewById(R.id.tv_message_connecting);
            currentChildViewHolder.overlayMenu = (OverlayMenu) currentChildViewHolder.convertView.findViewById(R.id.overlay_menu);
            currentChildViewHolder.overlayPTZ = (OverlayPTZ) currentChildViewHolder.convertView.findViewById(R.id.ptz_overlay_menu);
            currentChildViewHolder.position = groupPosition;
            currentChildViewHolder.overlayMenu.setDeviceChannelsManager(mDeviceManager.getDeviceChannelsManagers().get(groupPosition));
            currentChildViewHolder.overlayMenu.setLayoutParams(currentChildViewHolder.recyclerViewChannels.getLayoutParams());
            currentChildViewHolder.overlayPTZ.setLayoutParams(currentChildViewHolder.recyclerViewChannels.getLayoutParams());

            if (groupViewHolder.get(groupPosition).mDevice.getSerialNumber().equals("Favoritos")) {
                currentChildViewHolder.tvMessage.setText("Nenhum favorito adicionado.");
            } else {
                currentChildViewHolder.tvMessage.setText(groupViewHolder.get(groupPosition).mDevice.message);
            }

            return currentChildViewHolder;
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void initGridRecycler(int groupPosition, ChildViewHolder childViewHolder){
        childViewHolder.gridLayoutManager = new CustomGridLayoutManager(mContext, mDeviceManager.getDeviceChannelsManagers().get(groupPosition).numQuad, GridLayoutManager.HORIZONTAL, false);
        childViewHolder.recyclerViewChannels.setLayoutManager(childViewHolder.gridLayoutManager);
        childViewHolder.mRecyclerAdapter = new ChannelRecyclerViewAdapter(mContext, groupViewHolder.get(groupPosition).mDevice, childViewHolder, mDeviceManager.getDeviceChannelsManagers().get(groupPosition));
        childViewHolder.recyclerViewChannels.setAdapter(childViewHolder.mRecyclerAdapter);
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
                changeQuad(groupPosition);
            }
        };
    }

    public void changeQuad(final int groupPosition){
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
                setLayoutSize(groupPosition, childViewHolder.get(groupPosition), deviceChannelsManager.lastExpand);
                deviceChannelsManager.changeSurfaceViewSize();
                deviceChannelsManager.resetScale();
                deviceChannelsManager.reOrderSurfaceViewComponents();
            }
        });
    }

    private View.OnClickListener openFavoritesList(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, FavoritesDevicesListActivity.class);
                mContext.startActivity(intent);
            }
        };
    }


    private RecyclerView.OnScrollListener createOnScrollListener(final int groupPosition) {
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
        try {
            if (convertView == null || groupViewHolder.size() <= groupPosition) {
                convertView = mInflater.inflate(R.layout.expandable_list_view_row, parent, false);
                GroupViewHolder groupViewHolder = initGroupViewHolder(groupPosition, convertView, parent);
                groupViewHolder.blank.setTag(groupViewHolder);
                convertView.setTag(groupViewHolder);
                childViewHolder.add(new ChildViewHolder());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        GroupViewHolder groupViewHolder = (GroupViewHolder) convertView.getTag();
        try {
            if (groupViewHolder.position != groupPosition) {
                groupViewHolder = this.groupViewHolder.get(groupPosition);
            }
        } catch (NullPointerException e){
            e.printStackTrace();
        }
        if((mContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE && DeviceListActivity.previousGroup != -1)) {
            Log.d("getGroupView", "groupViewHolder.blank");
            return groupViewHolder.blank;
        } else {
            return groupViewHolder.convertView;
        }
    }

    public void updateQuad(final int groupPosition) {
        ((Activity)mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ChannelsManager deviceChannelsManager = mDeviceManager.getDeviceChannelsManagers().get(groupPosition);
                deviceChannelsManager.stopChannels(0);
                mDeviceManager.clearStart();
//                mDeviceManager.getDevices().get(groupPosition).getChannelsManager();
                childViewHolder.get(groupPosition).gridLayoutManager.setSpanCount(mDeviceManager.getDeviceChannelsManagers().get(groupPosition).numQuad);
                childViewHolder.get(groupPosition).mRecyclerAdapter.notifyDataSetChanged();
                setLayoutSize(groupPosition, childViewHolder.get(groupPosition));
                deviceChannelsManager.changeSurfaceViewSize(mDeviceManager.getDeviceChannelsManagers().get(groupPosition).lastExpand);
                deviceChannelsManager.resetScale();
                deviceChannelsManager.reOrderSurfaceViewComponents();
            }
        });
    }

    @Override
    public View getChildView(final int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ChildViewHolder childViewHolder;

        if (convertView == null || this.childViewHolder.get(groupPosition).recyclerViewChannels == null) {
            childViewHolder = initChildViewHolder(parent, groupPosition);
            convertView = childViewHolder.convertView;
            initGridRecycler(groupPosition, childViewHolder);
            convertView.setTag(childViewHolder);
        } else if (((ChildViewHolder) convertView.getTag()).position != groupPosition) {
            childViewHolder = this.childViewHolder.get(groupPosition);
            convertView = childViewHolder.convertView;
        } else {
            childViewHolder = (ChildViewHolder) convertView.getTag();
        }

        GroupViewHolder currentGroupViewHolder = groupViewHolder.get(groupPosition);

        if (!currentGroupViewHolder.mDevice.isLogged) {
            if (currentGroupViewHolder.mDevice.isFavorite && currentGroupViewHolder.mDevice.getChannelNumber() == 0) {

            } else {
                loginDevice(currentGroupViewHolder.mDevice, currentGroupViewHolder, childViewHolder, groupPosition);
                childViewHolder.tvMessage.setVisibility(View.VISIBLE);
                currentGroupViewHolder.ivRefresh.setVisibility(View.VISIBLE);
            }

        } else {
            updateChildView(mDevices.get(groupPosition), currentGroupViewHolder, childViewHolder, groupPosition);
            childViewHolder.gridLayoutManager.scrollToPosition(mDeviceManager.getDeviceChannelsManagers().get(groupPosition).lastFirstVisibleItem);
            updateQuad(groupPosition);
            showExpanded(groupPosition, currentGroupViewHolder, childViewHolder);

        }
        int option = mDeviceManager.getDeviceChannelsManagers().get(groupPosition).lastExpand;
        setLayoutSize(groupPosition, childViewHolder, option);
        Log.d(TAG, "Set layout ok!");
        return convertView;
    }

    private void showExpanded(final int groupPosition, final GroupViewHolder groupViewHolder, final ChildViewHolder childViewHolder) {
        ((DeviceListActivity) mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (groupViewHolder.mDevice.isOnline && groupViewHolder.mDevice.getChannelNumber() > 0) {
                    childViewHolder.tvMessage.setVisibility(View.GONE);
                }
                groupViewHolder.ivQuad.setVisibility(View.GONE);
                groupViewHolder.ivOtimizar.setVisibility(View.GONE);
                groupViewHolder.ivRefresh.setVisibility(View.VISIBLE);
                childViewHolder.tvMessage.setVisibility(View.VISIBLE);
                if (groupViewHolder.mDevice.isFavorite) {
                    groupViewHolder.ivRefresh.setVisibility(View.GONE);
                }
                if (groupViewHolder.mDevice.getChannelNumber() == 0) {
                    childViewHolder.tvMessage.setVisibility(View.VISIBLE);
                    groupViewHolder.ivRefresh.setVisibility(View.VISIBLE);
                    if(groupViewHolder.mDevice.isFavorite) {
                        childViewHolder.tvMessage.setText("Nenhum favorito adicionado.");
                        groupViewHolder.ivRefresh.setVisibility(View.GONE);
                        groupViewHolder.ivAddMore.setVisibility(View.VISIBLE);
                    }
                    else
                        childViewHolder.tvMessage.setText("Nenhum canal encontrado.");
                    childViewHolder.recyclerViewChannels.setVisibility(View.GONE);
                    groupViewHolder.ivMore.setVisibility(View.GONE);
                }
                else if (groupViewHolder.mDevice.getChannelNumber() > 0 && groupViewHolder.mDevice.isLogged) {
                    childViewHolder.tvMessage.setVisibility(View.GONE);
                    groupViewHolder.ivRefresh.setVisibility(View.GONE);
                    if (groupViewHolder.mDevice.isFavorite) {
                        groupViewHolder.ivAddMore.setVisibility(View.VISIBLE);
                    }
                    childViewHolder.recyclerViewChannels.setVisibility(View.VISIBLE);
                    groupViewHolder.ivMore.setVisibility(View.VISIBLE);
                    if(groupViewHolder.mDevice.getChannelNumber() > 1 ) {
                        groupViewHolder.ivQuad.setVisibility(View.VISIBLE);
                        groupViewHolder.ivOtimizar.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }



    @Override
    public void onGroupCollapsed(int groupPosition){
        try {
            super.onGroupCollapsed(groupPosition);
            stopChannels(groupPosition);
            final GroupViewHolder currentGroupViewHolder = groupViewHolder.get(groupPosition);
            final ChildViewHolder currentChildViewHolder = childViewHolder.get(groupPosition);
            ((DeviceListActivity) mContext).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    currentGroupViewHolder.ivAddMore.setVisibility(View.GONE);
                    currentGroupViewHolder.ivMore.setVisibility(View.GONE);
                    currentGroupViewHolder.ivQuad.setVisibility(View.GONE);
                    currentGroupViewHolder.ivRefresh.setVisibility(View.GONE);
                    currentGroupViewHolder.ivOtimizar.setVisibility(View.GONE);
                    currentChildViewHolder.recyclerViewChannels.setVisibility(View.GONE);
                    currentChildViewHolder.overlayMenu.setVisibility(View.GONE);
                    currentChildViewHolder.recyclerViewChannels.removeAllViewsInLayout();
                    currentChildViewHolder.recyclerViewChannels.removeAllViewsInLayout();
                }
            });
            mDeviceManager.removeLoginListener(currentGroupViewHolder.mDevice.getId());
            mDeviceManager.clearStart();
        } catch (IndexOutOfBoundsException |  NullPointerException e) {
            e.printStackTrace();
        }
    }

    private void setLayoutSize(final int groupPosition, final ChildViewHolder childViewHolder, int option) {
        DisplayMetrics displayMetrics = Resources.getSystem().getDisplayMetrics();
        int viewWidth = displayMetrics.widthPixels;
        int viewHeight;

        if(displayMetrics.widthPixels%2 != 0)
            viewWidth -= 1;


        if(mContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            viewHeight = displayMetrics.heightPixels;
            option = 0;
        } else{
            viewHeight = ((displayMetrics.heightPixels / 3)+10);
        }

        switch(option) {
            case 1:
                viewHeight += 150;
                break;
            case 2:
                viewHeight += 250;
                break;
            case 3:
                viewHeight += 500;
                break;
            default:
                break;
        }

        final int width = viewWidth;
        final int height = viewHeight;

        ((Activity)mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                childViewHolder.convertView.getLayoutParams().height = height;
                childViewHolder.convertView.getLayoutParams().width = width;
                mDeviceManager.getDeviceChannelsManagers().get(groupPosition).resetScale();
            }
        });
    }


    private void setLayoutSize(final int groupPosition, final ChildViewHolder childViewHolder) {
        int option = 0;
        DisplayMetrics displayMetrics = Resources.getSystem().getDisplayMetrics();
        int viewWidth = displayMetrics.widthPixels;
        int viewHeight;

        if(displayMetrics.widthPixels%2 != 0)
            viewWidth -= 1;


        if(mContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            viewHeight = displayMetrics.heightPixels;
            option = 0;
        } else{
            viewHeight = ((displayMetrics.heightPixels / 3)+10);
        }

        switch(option) {
            case 1:
                viewHeight += 150;
                break;
            case 2:
                viewHeight += 250;
                break;
            default:
                break;
        }

        final int width = viewWidth;
        final int height = viewHeight;

        ((Activity)mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                childViewHolder.convertView.getLayoutParams().height = height;
                childViewHolder.convertView.getLayoutParams().width = width;
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
        Log.d(TAG, "Update device: " + channelsManager.mDevice.deviceName);
        if(channelsManager.mDevice.getChannelNumber() > 0)
            channelsManager.createComponents();
        if(childViewHolder.get(position).gridLayoutManager != null) {
            initGridRecycler(position, childViewHolder.get(position));
            childViewHolder.get(position).gridLayoutManager.setSpanCount(channelsManager.numQuad);
            childViewHolder.get(position).recyclerViewChannels.setVisibility(View.VISIBLE);
            childViewHolder.get(position).recyclerViewChannels.getAdapter().notifyDataSetChanged();
//            notifyDataSetChanged();
        }
        if(channelsManager.surfaceViewComponents.isEmpty() && childViewHolder.get(position).recyclerViewChannels != null) {
            childViewHolder.get(position).recyclerViewChannels.setVisibility(View.GONE);
            childViewHolder.get(position).tvMessage.setVisibility(View.VISIBLE);
        }
    }

    private void loginDevice(final Device mDevice, final GroupViewHolder groupViewHolder, final ChildViewHolder childViewHolder, final int position) {
        mDeviceManager.loginDevice(mDevice, new LoginDeviceListener() {
            @Override
            public void onLoginSuccess(Device device) {
                mDevice.isLogged = true;
                mDeviceManager.clearStart();
                groupViewHolder.ivRefresh.setVisibility(View.VISIBLE);
                updateGrid(position, mDeviceManager.getDeviceChannelsManagers().get(position));
                showExpanded(position, groupViewHolder, childViewHolder);
                childViewHolder.gridLayoutManager.scrollToPosition(mDeviceManager.getDeviceChannelsManagers().get(position).lastFirstVisibleItem);
            }

            @Override
            public void onLoginError(final long error, Device device) {
                if(device != null && device.getId() == mDevice.getId()) {
                    ((DeviceListActivity) mContext).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mDevice.isLogged = false;
                            groupViewHolder.ivQuad.setVisibility(View.GONE);
                            groupViewHolder.ivMore.setVisibility(View.GONE);
                            groupViewHolder.ivOtimizar.setVisibility(View.GONE);
                            groupViewHolder.ivRefresh.setVisibility(View.VISIBLE);
                            childViewHolder.recyclerViewChannels.setVisibility(View.GONE);
                            String errorMsg;
                            if(error == -11301)
                                errorMsg = "Login ou senha incorretos.";
                            else if(error == -11302)
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


    public void expandDeviceScreen(final int devicePosition, final int option) {
        ((Activity)mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ChannelsManager deviceChannelsManager = mDeviceManager.getDeviceChannelsManagers().get(devicePosition);
                deviceChannelsManager.stopChannels(0);
                mDeviceManager.clearStart();
                childViewHolder.get(devicePosition).mRecyclerAdapter.notifyDataSetChanged();
                deviceChannelsManager.lastExpand = option;
                setLayoutSize(devicePosition, childViewHolder.get(devicePosition), option);
                deviceChannelsManager.changeSurfaceViewSize(option);
                deviceChannelsManager.resetScale();
                deviceChannelsManager.reOrderSurfaceViewComponents();
            }
        });

    }

    private void showMoreDialogExpandSelection(final int groupPosition) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            ((DeviceListActivity) mContext).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    builder.setTitle("Escolha o tamanho:")
                            .setItems(new CharSequence[]{"Normal","2x", "3x", "4x"},
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            int option = 0;
                                            switch (which) {
                                                case 0:
                                                    option = 0;
                                                    break;
                                                case 1:
                                                    option = 1;
                                                    break;
                                                case 2:
                                                    option = 2;
                                                    break;
                                                case 3:
                                                    option = 3;
                                                    break;
                                                default:
                                                    option = 0;
                                                    break;
                                            }
                                            expandDeviceScreen(groupPosition, option);
                                        }
                                    });
                    builder.show();
                }
            });
    }

    private void showMoreDialog(final GroupViewHolder groupViewHolder, final int groupPosition) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        if(!groupViewHolder.mDevice.getSerialNumber().equals("Favoritos"))
            ((DeviceListActivity) mContext).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    builder.setTitle("")
                            .setItems(new CharSequence[]{"Configurações", "Controle Remoto", "Playback", "Otimizar", "Expandir"},
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
                                                case 3:
                                                    showCustomDialog(groupViewHolder);
                                                    break;
                                                case 4:
                                                    showMoreDialogExpandSelection(groupPosition);
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
        try {
            Log.d(TAG, "Device index: " + mDevices.indexOf(mDevice));
            Log.d(TAG, "Device index: " + indexOfDeviceByName(mDevice.deviceName));

            Bundle extras = new Bundle();
            extras.putSerializable("device", mDevices.indexOf(mDevice));

            Intent intent = new Intent(mContext, DeviceRemoteControlActivity.class);
            intent.putExtras(extras);

            mContext.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int indexOfDeviceByName (String name) {
        for (int index = 0; index < mDevices.size(); index ++) {
            if (mDevices.get(index).getDeviceName().equals(name)){
                return index;
            }
        }
        return -1;
    }

    public int nextNumQuad(int numQuad,int totalChannels) {
        int nextNumQuad = 1;
        boolean thereIsMemoryAvailable = true;

        if (numQuad == 1) {
            nextNumQuad = 2;
        } else if (numQuad == 2) {
            double totalFreeMemory = mDeviceManager.checkMemory(mContext);
            thereIsMemoryAvailable = totalFreeMemory > 190;
            if (thereIsMemoryAvailable && totalChannels > 4) {
                nextNumQuad = 3;
            } else {
                nextNumQuad = 1;
            }
        } else if (numQuad == 3) {
            double totalFreeMemory = mDeviceManager.checkMemory(mContext);
            thereIsMemoryAvailable = totalFreeMemory > 210;
            if(thereIsMemoryAvailable && totalChannels > 9) {
                nextNumQuad = 4;
            } else {
                nextNumQuad = 1;
            }
        } else if (numQuad == 4) {
            nextNumQuad = 1;
        }

        Log.d("nextNumQuad:","" + nextNumQuad);

        if (!thereIsMemoryAvailable){
            Toast.makeText(mContext, "Faltou memória para abrir mais câmeras!", Toast.LENGTH_LONG).show();
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
        try {
            if (childViewHolder.size() > position && childViewHolder.get(position).tvMessage != null)
                ((Activity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        childViewHolder.get(position).tvMessage.setText(message);
                        childViewHolder.get(position).tvMessage.invalidate();
                        Log.d("setMessage()", position + " - " + message);
                    }
                });
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        }
    }

    public void stopActions() {
        Log.e("DeviceList", "StopActions");
        for (int position=0; position<mDeviceManager.getDeviceChannelsManagers().size(); position++) {
            for (SurfaceViewComponent svc : mDeviceManager.getDeviceChannelsManagers().get(position).surfaceViewComponents) {
                mDeviceManager.getDeviceChannelsManagers().get(position).stopActions();
            }
        }
    }

    public void verifyOverlayMenuVisibility() {
        for (int i=0; i<childViewHolder.size(); i++) {
            if (childViewHolder.get(i).overlayMenu.getVisibility() == View.VISIBLE) {
                childViewHolder.get(i).mRecyclerAdapter.closeOverlayMenu();
            }
        }
    }

    private View.OnClickListener refreshDeviceConnection(final GroupViewHolder groupViewHolder){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (groupViewHolder.mDevice.allAttempstFail){
                    groupViewHolder.mDevice.resetAttempts();
                    mDeviceManager.loginAttempt(groupViewHolder.mDevice);
                }
            }
        };
    }

    private View.OnClickListener otimizarDevice(final GroupViewHolder groupViewHolder){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCustomDialog(groupViewHolder);
            }
        };
    }

    private void showCustomDialog (final GroupViewHolder groupViewHolder) {
        new CustomTypeDialog(mContext, groupViewHolder.mDevice, new CustomTypeDialog.OnDialogClickListener() {
            @Override
            public void onDialogImageRunClick() {

            }
        });
    }


    public class GroupViewHolder {
        public TextView tvDeviceName;
        public Device mDevice;
        public ImageView ivMore;
        public ImageView ivQuad;
        public ImageView ivAddMore;
        public ImageView ivRefresh;
        public ImageView ivOtimizar;
        public View convertView;
        public View blank;
        public int position;
    }

    public class ChildViewHolder {
        public View convertView;
        public RecyclerView recyclerViewChannels;
        public OverlayMenu overlayMenu;
        public OverlayPTZ overlayPTZ;
        public TextView tvMessage;
        public ChannelRecyclerViewAdapter mRecyclerAdapter;
        public CustomGridLayoutManager gridLayoutManager;
        public int position;
    }
}
