package br.inatel.icc.gigasecurity.gigamonitor.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
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

import br.inatel.icc.gigasecurity.gigamonitor.listeners.*;
import br.inatel.icc.gigasecurity.gigamonitor.R;
import br.inatel.icc.gigasecurity.gigamonitor.activities.DeviceListActivity;
//import br.inatel.icc.gigasecurity.gigamonitor.activities.DevicePlaybackActivity;
//import br.inatel.icc.gigasecurity.gigamonitor.activities.DeviceRemoteControlActivity;
import br.inatel.icc.gigasecurity.gigamonitor.config.ConfigMenuActivity;
import br.inatel.icc.gigasecurity.gigamonitor.core.DeviceManager;
import br.inatel.icc.gigasecurity.gigamonitor.model.Device;
import br.inatel.icc.gigasecurity.gigamonitor.model.LoginMethod;
import br.inatel.icc.gigasecurity.gigamonitor.model.SurfaceViewComponent;

import static android.support.v7.widget.RecyclerView.SCROLL_STATE_IDLE;

/**
 * Created by filipecampos on 30/05/2016.
 */
public class DeviceExpandableListAdapter extends BaseExpandableListAdapter {

    private LayoutInflater mInflater;
    private ArrayList<Device> mDevices;
    private Context mContext;
    private DeviceManager mDeviceManager;
    private GroupViewHolder[] groupViewHolder;
    private ChildViewHolder[] childViewHolder;

    public DeviceExpandableListAdapter(Context mContext, ArrayList<Device> mDevices) {
        this.mContext        = mContext;
        this.mDevices        = mDevices;
        init();

    }

    public void init() {
        this.mDeviceManager  = DeviceManager.getInstance();
        this.mInflater       = LayoutInflater.from(mContext);
        this.groupViewHolder = new GroupViewHolder[mDevices.size()];
        this.childViewHolder = new ChildViewHolder[mDevices.size()];
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

        groupView.tvDeviceName.setText(groupView.mDevice.getHostname());

        return groupView;
    }

    public View.OnClickListener createMoreListener(final int groupPosition){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMoreDialog(groupViewHolder[groupPosition],groupPosition);
            }
        };
    }

    private View.OnClickListener createQuadListener(final int groupPosition){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DeviceListActivity.listComponents.get(groupPosition).numQuad = nextNumQuad(DeviceListActivity.listComponents.get(groupPosition).numQuad,groupViewHolder[groupPosition].mDevice.getChannelNumber());
                DeviceListActivity.listComponents.get(groupPosition).lastNumQuad = DeviceListActivity.listComponents.get(groupPosition).numQuad;
                if(DeviceListActivity.listComponents.get(groupPosition).numQuad == 1)
                    DeviceListActivity.listComponents.get(groupPosition).stopChannels();


                childViewHolder[groupPosition].gridLayoutManager = new GridLayoutManager(mContext, DeviceListActivity.listComponents.get(groupPosition).numQuad, GridLayoutManager.HORIZONTAL, false);
                childViewHolder[groupPosition].recyclerViewChannels.setLayoutManager(childViewHolder[groupPosition].gridLayoutManager);
                childViewHolder[groupPosition].mRecyclerAdapter = new ChannelRecyclerViewAdapter(mContext, groupViewHolder[groupPosition].mDevice, DeviceListActivity.listComponents.get(groupPosition).numQuad, childViewHolder[groupPosition], DeviceListActivity.listComponents.get(groupPosition));
                childViewHolder[groupPosition].recyclerViewChannels.setAdapter(childViewHolder[groupPosition].mRecyclerAdapter);

                childViewHolder[groupPosition].recyclerViewChannels.setOnScrollListener(createOnScrollListener(groupPosition));
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
                    final int currentFirstVisibleItem = childViewHolder[groupPosition].gridLayoutManager.findFirstVisibleItemPosition();
                    final int currentLastVisibleItem = childViewHolder[groupPosition].gridLayoutManager.findLastVisibleItemPosition();

                    final int itemToScroll = DeviceListActivity.listComponents.get(groupPosition).scrollToItem(currentFirstVisibleItem,currentLastVisibleItem);
                    childViewHolder[groupPosition].gridLayoutManager.smoothScrollToPosition(childViewHolder[groupPosition].recyclerViewChannels, null, itemToScroll);
                }
            }
        };
    }



    @Override
    public View getGroupView(final int groupPosition, final boolean isExpanded, View convertView, ViewGroup parent) {

        if(groupViewHolder[groupPosition] == null) {
            groupViewHolder[groupPosition] = initGroupViewHolder(groupPosition, parent);

            groupViewHolder[groupPosition].ivMore.setOnClickListener(createMoreListener(groupPosition));
            groupViewHolder[groupPosition].ivQuad.setOnClickListener(createQuadListener(groupPosition));

        }

        if(mContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE && DeviceListActivity.previousGroup != -1) {
            groupViewHolder[groupPosition].convertView = mInflater.inflate(R.layout.blank_layout, parent, false);
        } else {
            groupViewHolder[groupPosition] = initGroupViewHolder(groupPosition, parent);

            if(isExpanded) {
                groupViewHolder[groupPosition].ivMore.setVisibility(View.VISIBLE);
                groupViewHolder[groupPosition].ivIndicator.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_indicator_minus));
                groupViewHolder[groupPosition].progressBar.setVisibility(View.GONE);

                if(groupViewHolder[groupPosition].mDevice.getChannelNumber() > 1) {
                    groupViewHolder[groupPosition].ivQuad.setVisibility(View.VISIBLE);
                }
            }

            groupViewHolder[groupPosition].ivMore.setOnClickListener(createMoreListener(groupPosition));
            groupViewHolder[groupPosition].ivQuad.setOnClickListener(createQuadListener(groupPosition));

        }
        return groupViewHolder[groupPosition].convertView;
    }

    @Override
    public View getChildView(final int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if(childViewHolder[groupPosition] == null) {

            childViewHolder[groupPosition] = new ChildViewHolder();

            childViewHolder[groupPosition].convertView =  mInflater.inflate(R.layout.expandable_list_view_child, parent, false);

            childViewHolder[groupPosition].recyclerViewChannels = (RecyclerView) childViewHolder[groupPosition].convertView.findViewById(R.id.recycler_view_channels);
            childViewHolder[groupPosition].tvMessage            = (TextView) childViewHolder[groupPosition].convertView.findViewById(R.id.tv_message_connecting);
            childViewHolder[groupPosition].ivHQ                 = (ImageView) childViewHolder[groupPosition].convertView.findViewById(R.id.iv_hq);
            childViewHolder[groupPosition].ivPlayPause          = (ImageView) childViewHolder[groupPosition].convertView.findViewById(R.id.iv_play_pause);
            childViewHolder[groupPosition].ivSnapshot           = (ImageView) childViewHolder[groupPosition].convertView.findViewById(R.id.iv_snapshot);
            childViewHolder[groupPosition].ivSnapvideo          = (ImageView) childViewHolder[groupPosition].convertView.findViewById(R.id.iv_snapvideo);
            childViewHolder[groupPosition].layoutMenu           = (LinearLayout) childViewHolder[groupPosition].convertView.findViewById(R.id.layout_menu);
            childViewHolder[groupPosition].tvChnNumber          = (TextView) childViewHolder[groupPosition].convertView.findViewById(R.id.tv_channel_number_recycler);


            if(!groupViewHolder[groupPosition].mDevice.isLogged) {
                //Inicializando grid e recycler temporariamente, apenas para não dar crash se não conectar
                childViewHolder[groupPosition].gridLayoutManager = new GridLayoutManager(mContext, DeviceListActivity.listComponents.get(groupPosition).numQuad, GridLayoutManager.HORIZONTAL, false);
                childViewHolder[groupPosition].recyclerViewChannels.setLayoutManager(childViewHolder[groupPosition].gridLayoutManager);
                childViewHolder[groupPosition].mRecyclerAdapter = new ChannelRecyclerViewAdapter(mContext, groupViewHolder[groupPosition].mDevice, DeviceListActivity.listComponents.get(groupPosition).numQuad, childViewHolder[groupPosition], DeviceListActivity.listComponents.get(groupPosition));
                childViewHolder[groupPosition].recyclerViewChannels.setAdapter(childViewHolder[groupPosition].mRecyclerAdapter);

                loginDevice(groupViewHolder[groupPosition].mDevice, groupViewHolder[groupPosition], childViewHolder[groupPosition], groupPosition);
            } else {
                childViewHolder[groupPosition].tvMessage.setVisibility(View.GONE);
                childViewHolder[groupPosition].recyclerViewChannels.setVisibility(View.VISIBLE);

                groupViewHolder[groupPosition].ivIndicator.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_indicator_minus));
                groupViewHolder[groupPosition].ivMore.setVisibility(View.VISIBLE);
                groupViewHolder[groupPosition].progressBar.setVisibility(View.GONE);

                if (groupViewHolder[groupPosition].mDevice.getChannelNumber() == 0) { //Wrong Password
                    childViewHolder[groupPosition].tvMessage.setText("Erro ao fazer login com o dipositivo.");

                    childViewHolder[groupPosition].tvMessage.setVisibility(View.VISIBLE);
                    childViewHolder[groupPosition].recyclerViewChannels.setVisibility(View.GONE);
                    groupViewHolder[groupPosition].ivMore.setVisibility(View.GONE);
                    groupViewHolder[groupPosition].progressBar.setVisibility(View.GONE);
                } else if (groupViewHolder[groupPosition].mDevice.getChannelNumber() > 1) {
                    groupViewHolder[groupPosition].ivQuad.setVisibility(View.VISIBLE);
                } else {
                    groupViewHolder[groupPosition].ivQuad.setVisibility(View.INVISIBLE);
                }


                childViewHolder[groupPosition].gridLayoutManager = new GridLayoutManager(mContext, DeviceListActivity.listComponents.get(groupPosition).numQuad, GridLayoutManager.HORIZONTAL, false);
                childViewHolder[groupPosition].recyclerViewChannels.setLayoutManager(childViewHolder[groupPosition].gridLayoutManager);
                childViewHolder[groupPosition].mRecyclerAdapter = new ChannelRecyclerViewAdapter(mContext, groupViewHolder[groupPosition].mDevice, DeviceListActivity.listComponents.get(groupPosition).numQuad, childViewHolder[groupPosition], DeviceListActivity.listComponents.get(groupPosition));
                childViewHolder[groupPosition].recyclerViewChannels.setAdapter(childViewHolder[groupPosition].mRecyclerAdapter);

                childViewHolder[groupPosition].recyclerViewChannels.setOnScrollListener(createOnScrollListener(groupPosition));
            }

            setLayoutSize(groupPosition);
            childViewHolder[groupPosition].gridLayoutManager.scrollToPosition(DeviceListActivity.listComponents.get(groupPosition).lastFirstVisibleItem);
        }


        return childViewHolder[groupPosition].convertView;
    }

    private void setLayoutSize(int groupPosition) {
        if(mContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Display mDisplay = ((DeviceListActivity) mContext).getWindowManager().getDefaultDisplay();
            int viewWidth = mDisplay.getWidth();
            int viewHeight = mDisplay.getHeight();

            FrameLayout.LayoutParams lpRecyclerView = new FrameLayout.LayoutParams(viewWidth, viewHeight);

            childViewHolder[groupPosition].recyclerViewChannels.setLayoutParams(lpRecyclerView);
        }
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
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

    private void loginDevice(final Device mDevice, final GroupViewHolder groupViewHolder, final ChildViewHolder childViewHolder, final int position) {

        mDeviceManager.loginDevice(mDevice, new LoginDeviceInterface() {
            @Override
            public void onLoginSuccess() {

                ((DeviceListActivity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if (mDevice.getChannelNumber() == 0) { //Wrong Password
                            childViewHolder.tvMessage.setText("Erro ao fazer login com o dipositivo.");
                        } else {
                            childViewHolder.tvMessage.setVisibility(View.GONE);
                            childViewHolder.recyclerViewChannels.setVisibility(View.VISIBLE);

                            groupViewHolder.ivIndicator.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_indicator_minus));
                            groupViewHolder.ivMore.setVisibility(View.VISIBLE);
                            groupViewHolder.progressBar.setVisibility(View.GONE);

                            if (mDevice.getChannelNumber() > 1) {
                                groupViewHolder.ivQuad.setVisibility(View.VISIBLE);
                                DeviceListActivity.listComponents.get(position).numQuad = 2;
                                DeviceListActivity.listComponents.get(position).lastNumQuad = 2;
                            } else {
                                groupViewHolder.ivQuad.setVisibility(View.INVISIBLE);
                                DeviceListActivity.listComponents.get(position).numQuad = 1;
                                DeviceListActivity.listComponents.get(position).lastNumQuad = 1;
                            }

                            DeviceListActivity.listComponents.get(position).createComponents();

                            childViewHolder.gridLayoutManager = new GridLayoutManager(mContext, DeviceListActivity.listComponents.get(position).numQuad, GridLayoutManager.HORIZONTAL, false);
                            childViewHolder.recyclerViewChannels.setLayoutManager(childViewHolder.gridLayoutManager);
                            childViewHolder.mRecyclerAdapter = new ChannelRecyclerViewAdapter(mContext, groupViewHolder.mDevice, DeviceListActivity.listComponents.get(position).numQuad, childViewHolder, DeviceListActivity.listComponents.get(position));
                            childViewHolder.recyclerViewChannels.setAdapter(childViewHolder.mRecyclerAdapter);

                            childViewHolder.recyclerViewChannels.setOnScrollListener(createOnScrollListener(position));
                        }

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
                            if (error == -11301) {
                                childViewHolder.tvMessage.setText("Erro ao fazer login com o dipositivo. Senha incorreta.");
                            } else {
                                childViewHolder.tvMessage.setText("Erro ao fazer login com o dipositivo.");
                            }
                        }
                    });
                }
            }

            @Override
            public void onLogout() {
            }

        });
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

                                        //startSettingsActivity(groupViewHolder.mDevice);

                                        break;
                                    case 1:
                                        //startDeviceRemoteControlActivity(groupViewHolder.mDevice);

                                        break;
                                    case 2:
//                                        startPlaybackActivity(groupViewHolder.mDevice);
                                        break;
                                }

                            }
                        });
        builder.show();

    }

    private void startSettingsActivity(Device mDevice) {
        if (mDevice.getLoginID() != 0) {
            Bundle extras = new Bundle();
            extras.putSerializable("device", mDevice);

            Intent intent = new Intent(mContext, ConfigMenuActivity.class);
            intent.putExtras(extras);

            mContext.startActivity(intent);
        }
    }

//    private void startPlaybackActivity(Device mDevice) {
//        Bundle extras = new Bundle();
//        extras.putSerializable("device", mDevice);
//
//        Intent intent = new Intent(mContext, DevicePlaybackActivity.class);
//        intent.putExtras(extras);
//
//        mContext.startActivity(intent);
//    }

//    private void startDeviceRemoteControlActivity(Device mDevice) {
//        Bundle extras = new Bundle();
//        extras.putSerializable("device", mDevice);
//
//        Intent intent = new Intent(mContext, DeviceRemoteControlActivity.class);
//        intent.putExtras(extras);
//
//        mContext.startActivity(intent);
//    }

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
//        DeviceListActivity.listComponents.get(groupPosition).handleChannels(false);

        for (SurfaceViewComponent svc : DeviceListActivity.listComponents.get(groupPosition).surfaceViewComponents) {
            svc.onStop();
            /*mDeviceManager.stopDeviceVideo2(svc.realPlayHandleID, svc.mySurfaceView, new DeviceManager.StopDeviceVideoListener() {
                @Override
                public void onSuccessStopDevice() {
                    Log.v("GIGA", "onSuccessStopDevice");
                }

                @Override
                public void onErrorStopDevice() {
                    Log.v("GIGA", "onErrorStopDevice");
                }
            });*/
        }
    }

    public void pauseChannels(int groupPosition) {
        for (SurfaceViewComponent svc : DeviceListActivity.listComponents.get(groupPosition).surfaceViewComponents) {
            if (svc.isPlaying && svc.connected) {
                svc.onPause();
            }
        }
    }

    public void playChannels(int groupPosition) {
        for (SurfaceViewComponent svc : DeviceListActivity.listComponents.get(groupPosition).surfaceViewComponents) {
            if (!svc.isPlaying && svc.connected) {
                svc.onPlayLive();
            }
        }
    }

}
