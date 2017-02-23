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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import br.inatel.icc.gigasecurity.gigamonitor.model.Device;
import br.inatel.icc.gigasecurity.gigamonitor.model.SurfaceViewComponent;

import static android.support.v7.widget.RecyclerView.SCROLL_STATE_IDLE;

//import br.inatel.icc.gigasecurity.gigamonitor.activities.DeviceRemoteControlActivity;

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
    private boolean scrolled = false;

    public DeviceExpandableListAdapter(Context mContext, ArrayList<Device> mDevices) {
        this.mContext        = mContext;
        this.mDevices        = mDevices;
        init();

    }

    public void init() {
        this.mDeviceManager  = DeviceManager.getInstance();
        this.mInflater       = LayoutInflater.from(mContext);
        this.groupViewHolder = new ArrayList<GroupViewHolder>();
        this.childViewHolder = new ArrayList<ChildViewHolder>();
    }

    public void setDevices(ArrayList<Device> devices){
        mDevices = devices;
    }

    public void removeGroup(int position){
        childViewHolder.remove(position);
        groupViewHolder.remove(position);
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

    private ChildViewHolder initChildViewHolder(ViewGroup parent){
        ChildViewHolder currentChildViewHolder = new ChildViewHolder();
        currentChildViewHolder.convertView =  mInflater.inflate(R.layout.expandable_list_view_child, parent, false);

        currentChildViewHolder.recyclerViewChannels = (RecyclerView) currentChildViewHolder.convertView.findViewById(R.id.recycler_view_channels);
        currentChildViewHolder.tvMessage            = (TextView) currentChildViewHolder.convertView.findViewById(R.id.tv_message_connecting);
        currentChildViewHolder.ivHQ                 = (ImageView) currentChildViewHolder.convertView.findViewById(R.id.iv_hq);
        currentChildViewHolder.ivPlayPause          = (ImageView) currentChildViewHolder.convertView.findViewById(R.id.iv_play_pause);
        currentChildViewHolder.ivSnapshot           = (ImageView) currentChildViewHolder.convertView.findViewById(R.id.iv_snapshot);
        currentChildViewHolder.ivSnapvideo          = (ImageView) currentChildViewHolder.convertView.findViewById(R.id.iv_snapvideo);
        currentChildViewHolder.layoutMenu           = (LinearLayout) currentChildViewHolder.convertView.findViewById(R.id.layout_menu);
        currentChildViewHolder.tvChnNumber          = (TextView) currentChildViewHolder.convertView.findViewById(R.id.tv_channel_number_recycler);

        childViewHolder.add(currentChildViewHolder);
        return currentChildViewHolder;
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

    private void initGridRecycler(int groupPosition, ChildViewHolder childViewHolder){
        childViewHolder.gridLayoutManager = new GridLayoutManager(mContext, DeviceListActivity.listComponents.get(groupPosition).numQuad, GridLayoutManager.HORIZONTAL, false);
        childViewHolder.recyclerViewChannels.setLayoutManager(childViewHolder.gridLayoutManager);
        childViewHolder.mRecyclerAdapter = new ChannelRecyclerViewAdapter(mContext, groupViewHolder.get(groupPosition).mDevice, DeviceListActivity.listComponents.get(groupPosition).numQuad, childViewHolder, DeviceListActivity.listComponents.get(groupPosition));
        childViewHolder.recyclerViewChannels.setAdapter(childViewHolder.mRecyclerAdapter);
    }

    private void showExpanded(int groupPosition, GroupViewHolder groupViewHolder, ChildViewHolder childViewHolder) {
        childViewHolder.tvMessage.setVisibility(View.GONE);
        childViewHolder.recyclerViewChannels.setVisibility(View.VISIBLE);

        if (groupViewHolder.mDevice.getChannelNumber() <= 0) { //Wrong Password
            childViewHolder.tvMessage.setText("Nenhum canal encontrado.");
            childViewHolder.tvMessage.setVisibility(View.VISIBLE);
            childViewHolder.recyclerViewChannels.setVisibility(View.GONE);
            groupViewHolder.ivMore.setVisibility(View.INVISIBLE);
        }
        else if (groupViewHolder.mDevice.getChannelNumber() > 1) {
            groupViewHolder.ivQuad.setVisibility(View.VISIBLE);
            groupViewHolder.ivMore.setVisibility(View.VISIBLE);
        } else {
            groupViewHolder.ivQuad.setVisibility(View.INVISIBLE);
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
                DeviceListActivity.listComponents.get(groupPosition).numQuad = nextNumQuad(DeviceListActivity.listComponents.get(groupPosition).numQuad,groupViewHolder.get(groupPosition).mDevice.getChannelNumber());
                DeviceListActivity.listComponents.get(groupPosition).lastNumQuad = DeviceListActivity.listComponents.get(groupPosition).numQuad;
                if(DeviceListActivity.listComponents.get(groupPosition).numQuad == 1)
                    DeviceListActivity.listComponents.get(groupPosition).stopChannels(1);

                childViewHolder.get(groupPosition).gridLayoutManager.setSpanCount(DeviceListActivity.listComponents.get(groupPosition).numQuad);
                childViewHolder.get(groupPosition).mRecyclerAdapter.notifyDataSetChanged();
            }
        };
    }

    private RecyclerView.OnScrollListener createOnScrollListener(final int groupPosition){
        return new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrollStateChanged(final RecyclerView recyclerView,final int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                ChildViewHolder currentChildViewHolder = childViewHolder.get(groupPosition);
                final int itemToScroll;

                if(newState == SCROLL_STATE_IDLE) {
                    final int currentFirstVisibleItem = currentChildViewHolder.gridLayoutManager.findFirstVisibleItemPosition();
                    Log.d("scroll", "onScrollStateChanged: " + amountScrolled);
                    if(Math.abs(amountScrolled) > 200 && !scrolled) {
                        final int currentLastVisibleItem = currentChildViewHolder.gridLayoutManager.findLastVisibleItemPosition();
                        itemToScroll = DeviceListActivity.listComponents.get(groupPosition).scrollToItem(currentFirstVisibleItem,currentLastVisibleItem);
                        scrolled = true;
                    }else{
                        if(scrolled)
                            scrolled = false;
                        itemToScroll = currentFirstVisibleItem;
                    }
                    currentChildViewHolder.gridLayoutManager.smoothScrollToPosition(currentChildViewHolder.recyclerViewChannels, null, itemToScroll);
                    amountScrolled = 0;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy){
                if(!scrolled)
                    amountScrolled += dx;
//                super.onScrolled(recyclerView, dx, dy);
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
            initChildViewHolder(parent);
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
                initGridRecycler(groupPosition, currentChildViewHolder);
                loginDevice(currentGroupViewHolder.mDevice, currentGroupViewHolder, currentChildViewHolder, groupPosition);
            } else {
                showExpanded(groupPosition, currentGroupViewHolder, currentChildViewHolder);
            }
            setLayoutSize(groupPosition);
            currentChildViewHolder.gridLayoutManager.scrollToPosition(DeviceListActivity.listComponents.get(groupPosition).lastFirstVisibleItem);
        }

        return currentChildViewHolder.convertView;
    }

    @Override
    public void onGroupExpanded(int groupPosition){
        GroupViewHolder currentGroupViewHolder = groupViewHolder.get(groupPosition);
        currentGroupViewHolder.ivIndicator.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_indicator_minus));
        if(currentGroupViewHolder.mDevice.getChannelNumber() > 1) {
            currentGroupViewHolder.ivMore.setVisibility(View.VISIBLE);
            currentGroupViewHolder.ivQuad.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onGroupCollapsed(int groupPosition){
        GroupViewHolder currentGroupViewHolder = groupViewHolder.get(groupPosition);
        currentGroupViewHolder.ivMore.setVisibility(View.INVISIBLE);
        currentGroupViewHolder.ivIndicator.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_indicator_plus));
        currentGroupViewHolder.ivQuad.setVisibility(View.INVISIBLE);

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
            viewHeight = ((displayMetrics.heightPixels / 3) + 10);
        }

        FrameLayout.LayoutParams lpRecyclerView = new FrameLayout.LayoutParams(viewWidth, viewHeight);
        childViewHolder.get(groupPosition).recyclerViewChannels.setLayoutParams(lpRecyclerView);
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    private void loginDevice(final Device mDevice, final GroupViewHolder groupViewHolder, final ChildViewHolder childViewHolder, final int position) {
        mDeviceManager.loginDevice(mDevice, new LoginDeviceListener() {
            @Override
            public void onLoginSuccess() {
                ((DeviceListActivity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(mDevice.getChannelNumber()>1){
                            DeviceListActivity.listComponents.get(position).numQuad = 2;
                            DeviceListActivity.listComponents.get(position).lastNumQuad = 2;
                        }
                        DeviceListActivity.listComponents.get(position).createComponents();
                        childViewHolder.gridLayoutManager.setSpanCount(DeviceListActivity.listComponents.get(position).numQuad);
                        showExpanded(position, groupViewHolder, childViewHolder);
                    }
                });
            }

            @Override
            public void onLoginError(final long error, Device device) {
                if(device != null && device.getId() == mDevice.getId()) {
                    ((DeviceListActivity) mContext).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            groupViewHolder.ivQuad.setVisibility(View.INVISIBLE);
                            groupViewHolder.ivMore.setVisibility(View.INVISIBLE);
                            childViewHolder.recyclerViewChannels.setVisibility(View.GONE);
                            childViewHolder.tvMessage.setVisibility(View.VISIBLE);
                            if(error == -1)
                                childViewHolder.tvMessage.setText("Dispositivo offline.");
                            else if(error == -11301)
                                childViewHolder.tvMessage.setText("Login ou senha incorretos.");
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
        childViewHolder.get(groupPosition).gridLayoutManager = new GridLayoutManager(mContext, DeviceListActivity.listComponents.get(groupPosition).numQuad, GridLayoutManager.HORIZONTAL, false);
        childViewHolder.get(groupPosition).recyclerViewChannels.setLayoutManager(childViewHolder.get(groupPosition).gridLayoutManager);
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
        for (SurfaceViewComponent svc : DeviceListActivity.listComponents.get(groupPosition).surfaceViewComponents) {
            if(svc.isConnected)
                svc.onStop();
        }
    }

    public void pauseChannels(int groupPosition) {
        for (SurfaceViewComponent svc : DeviceListActivity.listComponents.get(groupPosition).surfaceViewComponents) {
            if (svc.isPlaying && svc.isConnected) {
                svc.onPause();
            }
        }
    }

    public void playChannels(int groupPosition) {
        for (SurfaceViewComponent svc : DeviceListActivity.listComponents.get(groupPosition).surfaceViewComponents) {
            if (!svc.isPlaying && svc.isConnected) {
                svc.onPlayLive();
            }
        }
    }

    public void resumeChannels(int groupPosition) {
        for (SurfaceViewComponent svc : DeviceListActivity.listComponents.get(groupPosition).surfaceViewComponents) {
            if (svc.isConnected) {
                svc.onResume();
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
        public View convertView;
        public RecyclerView recyclerViewChannels;
        public LinearLayout layoutMenu;
        public ImageView ivHQ, ivPlayPause, ivSnapshot, ivSnapvideo;
        public TextView tvChnNumber, tvMessage;
        public ChannelRecyclerViewAdapter mRecyclerAdapter;

        public  GridLayoutManager gridLayoutManager;
    }

}
