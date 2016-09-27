package br.inatel.icc.gigasecurity.gigamonitor.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import br.inatel.icc.gigasecurity.gigamonitor.R;
import br.inatel.icc.gigasecurity.gigamonitor.activities.DeviceListActivity;
import br.inatel.icc.gigasecurity.gigamonitor.activities.DevicePlaybackActivity;
import br.inatel.icc.gigasecurity.gigamonitor.activities.DeviceRemoteControlActivity;
import br.inatel.icc.gigasecurity.gigamonitor.config.ConfigMenuActivity;
import br.inatel.icc.gigasecurity.gigamonitor.core.DeviceManager;
import br.inatel.icc.gigasecurity.gigamonitor.model.Device;
import br.inatel.icc.gigasecurity.gigamonitor.model.LoginMethod;

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

    @Override
    public View getGroupView(final int groupPosition, final boolean isExpanded, View convertView, ViewGroup parent) {

        if(groupViewHolder[groupPosition] == null) {
            groupViewHolder[groupPosition] = new GroupViewHolder();

            groupViewHolder[groupPosition].convertView = mInflater.inflate(R.layout.expandable_list_view_row, parent, false);

            groupViewHolder[groupPosition].ivQuad       = (ImageView) groupViewHolder[groupPosition].convertView.findViewById(R.id.iv_grid_list_device);
            groupViewHolder[groupPosition].ivMore       = (ImageView) groupViewHolder[groupPosition].convertView.findViewById(R.id.iv_device_more);
            groupViewHolder[groupPosition].ivIndicator  = (ImageView) groupViewHolder[groupPosition].convertView.findViewById(R.id.iv_indicator);
            groupViewHolder[groupPosition].tvDeviceName = (TextView) groupViewHolder[groupPosition].convertView.findViewById(R.id.tv_hostname_list_device);
            groupViewHolder[groupPosition].progressBar  = (ProgressBar) groupViewHolder[groupPosition].convertView.findViewById(R.id.pb_expandable_list);
            groupViewHolder[groupPosition].mDevice      = mDevices.get(groupPosition);

            groupViewHolder[groupPosition].tvDeviceName.setText(groupViewHolder[groupPosition].mDevice.getHostname());

            groupViewHolder[groupPosition].ivMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showMoreDialog(groupViewHolder[groupPosition]);
                }
            });

            groupViewHolder[groupPosition].ivQuad.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (DeviceListActivity.listComponents.get(groupPosition).numQuad == 1) {
                        DeviceListActivity.listComponents.get(groupPosition).numQuad = 2;
                        DeviceListActivity.listComponents.get(groupPosition).lastNumQuad = 2;

                    } else if (DeviceListActivity.listComponents.get(groupPosition).numQuad == 2) {

                        if(groupViewHolder[groupPosition].mDevice.getChannelNumber() > 4) {
                            DeviceListActivity.listComponents.get(groupPosition).numQuad = 3;
                            DeviceListActivity.listComponents.get(groupPosition).lastNumQuad = 3;
                        } else {
                            DeviceListActivity.listComponents.get(groupPosition).numQuad = 1;
                            DeviceListActivity.listComponents.get(groupPosition).lastNumQuad = 1;
                        }

                    } else if (DeviceListActivity.listComponents.get(groupPosition).numQuad == 3) {

                        if(groupViewHolder[groupPosition].mDevice.getChannelNumber() > 9) {
                            DeviceListActivity.listComponents.get(groupPosition).numQuad = 4;
                            DeviceListActivity.listComponents.get(groupPosition).lastNumQuad = 4;
                        } else {
                            DeviceListActivity.listComponents.get(groupPosition).numQuad = 1;
                            DeviceListActivity.listComponents.get(groupPosition).lastNumQuad = 1;
                        }

                    } else if (DeviceListActivity.listComponents.get(groupPosition).numQuad == 4) {
                        DeviceListActivity.listComponents.get(groupPosition).numQuad = 1;
                        DeviceListActivity.listComponents.get(groupPosition).lastNumQuad = 1;
                    }

                    childViewHolder[groupPosition].recyclerViewChannels.setLayoutManager(new GridLayoutManager(mContext, DeviceListActivity.listComponents.get(groupPosition).numQuad, GridLayoutManager.HORIZONTAL, false));
                    childViewHolder[groupPosition].mRecyclerAdapter = new ChannelRecyclerViewAdapter(mContext, groupViewHolder[groupPosition].mDevice, DeviceListActivity.listComponents.get(groupPosition).numQuad, childViewHolder[groupPosition], DeviceListActivity.listComponents.get(groupPosition));
                    childViewHolder[groupPosition].recyclerViewChannels.setAdapter(childViewHolder[groupPosition].mRecyclerAdapter);
                }
            });
        }

        if(mContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            groupViewHolder[groupPosition].convertView = mInflater.inflate(R.layout.blank_layout, parent, false);
        } else {
            groupViewHolder[groupPosition].convertView = mInflater.inflate(R.layout.expandable_list_view_row, parent, false);

            groupViewHolder[groupPosition].ivQuad       = (ImageView) groupViewHolder[groupPosition].convertView.findViewById(R.id.iv_grid_list_device);
            groupViewHolder[groupPosition].ivMore       = (ImageView) groupViewHolder[groupPosition].convertView.findViewById(R.id.iv_device_more);
            groupViewHolder[groupPosition].ivIndicator  = (ImageView) groupViewHolder[groupPosition].convertView.findViewById(R.id.iv_indicator);
            groupViewHolder[groupPosition].tvDeviceName = (TextView) groupViewHolder[groupPosition].convertView.findViewById(R.id.tv_hostname_list_device);
            groupViewHolder[groupPosition].progressBar  = (ProgressBar) groupViewHolder[groupPosition].convertView.findViewById(R.id.pb_expandable_list);
            groupViewHolder[groupPosition].mDevice      = mDevices.get(groupPosition);

            groupViewHolder[groupPosition].tvDeviceName.setText(groupViewHolder[groupPosition].mDevice.getHostname());

            if(isExpanded) {
                groupViewHolder[groupPosition].ivMore.setVisibility(View.VISIBLE);
                groupViewHolder[groupPosition].ivIndicator.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_indicator_minus));
                groupViewHolder[groupPosition].progressBar.setVisibility(View.GONE);

                if(groupViewHolder[groupPosition].mDevice.getChannelNumber() > 1) {
                    groupViewHolder[groupPosition].ivQuad.setVisibility(View.VISIBLE);
                }
            }

            groupViewHolder[groupPosition].ivMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showMoreDialog(groupViewHolder[groupPosition]);
                }
            });

            groupViewHolder[groupPosition].ivQuad.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (DeviceListActivity.listComponents.get(groupPosition).numQuad == 1) {
                        DeviceListActivity.listComponents.get(groupPosition).numQuad = 2;
                        DeviceListActivity.listComponents.get(groupPosition).lastNumQuad = 2;

                    } else if (DeviceListActivity.listComponents.get(groupPosition).numQuad == 2) {

                        if(groupViewHolder[groupPosition].mDevice.getChannelNumber() > 4) {
                            DeviceListActivity.listComponents.get(groupPosition).numQuad = 3;
                            DeviceListActivity.listComponents.get(groupPosition).lastNumQuad = 3;
                        } else {
                            DeviceListActivity.listComponents.get(groupPosition).numQuad = 1;
                            DeviceListActivity.listComponents.get(groupPosition).lastNumQuad = 1;
                        }

                    } else if (DeviceListActivity.listComponents.get(groupPosition).numQuad == 3) {

                        if(groupViewHolder[groupPosition].mDevice.getChannelNumber() > 9) {
                            DeviceListActivity.listComponents.get(groupPosition).numQuad = 4;
                            DeviceListActivity.listComponents.get(groupPosition).lastNumQuad = 4;

                        } else {
                            DeviceListActivity.listComponents.get(groupPosition).numQuad = 1;
                            DeviceListActivity.listComponents.get(groupPosition).lastNumQuad = 1;
                        }

                    } else if (DeviceListActivity.listComponents.get(groupPosition).numQuad == 4) {
                        DeviceListActivity.listComponents.get(groupPosition).numQuad = 1;
                        DeviceListActivity.listComponents.get(groupPosition).lastNumQuad = 1;
                    }

                    childViewHolder[groupPosition].recyclerViewChannels.setLayoutManager(new GridLayoutManager(mContext, DeviceListActivity.listComponents.get(groupPosition).numQuad, GridLayoutManager.HORIZONTAL, false));
                    childViewHolder[groupPosition].mRecyclerAdapter = new ChannelRecyclerViewAdapter(mContext, groupViewHolder[groupPosition].mDevice, DeviceListActivity.listComponents.get(groupPosition).numQuad, childViewHolder[groupPosition], DeviceListActivity.listComponents.get(groupPosition));
                    childViewHolder[groupPosition].recyclerViewChannels.setAdapter(childViewHolder[groupPosition].mRecyclerAdapter);

                }
            });
        }

        return groupViewHolder[groupPosition].convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
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


            if(groupViewHolder[groupPosition].mDevice.getLoginID() == 0) {
                loginDevice(groupViewHolder[groupPosition].mDevice, groupViewHolder[groupPosition], childViewHolder[groupPosition], groupPosition);
            } else {
                childViewHolder[groupPosition].tvMessage.setVisibility(View.GONE);
                childViewHolder[groupPosition].recyclerViewChannels.setVisibility(View.VISIBLE);

                groupViewHolder[groupPosition].ivIndicator.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_indicator_minus));
                groupViewHolder[groupPosition].ivMore.setVisibility(View.VISIBLE);
                groupViewHolder[groupPosition].progressBar.setVisibility(View.GONE);

                if (groupViewHolder[groupPosition].mDevice.getChannelNumber() > 1) {
                    groupViewHolder[groupPosition].ivQuad.setVisibility(View.VISIBLE);
                    //DeviceListActivity.listComponents.get(groupPosition).numQuad = DeviceListActivity.listComponents.get(groupPosition).lastNumQuad;
                } else {
                    groupViewHolder[groupPosition].ivQuad.setVisibility(View.INVISIBLE);
                    //DeviceListActivity.listComponents.get(groupPosition).numQuad = 1;
                }

                childViewHolder[groupPosition].recyclerViewChannels.setLayoutManager(new GridLayoutManager(mContext, DeviceListActivity.listComponents.get(groupPosition).numQuad, GridLayoutManager.HORIZONTAL, false));
                childViewHolder[groupPosition].mRecyclerAdapter = new ChannelRecyclerViewAdapter(mContext, groupViewHolder[groupPosition].mDevice, DeviceListActivity.listComponents.get(groupPosition).numQuad, childViewHolder[groupPosition], DeviceListActivity.listComponents.get(groupPosition));
                childViewHolder[groupPosition].recyclerViewChannels.setAdapter(childViewHolder[groupPosition].mRecyclerAdapter);
            }

            setLayoutSize(groupPosition);
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
        public int lastFirstVisiblePosition = 0;
    }

    private void loginDevice(final Device mDevice, final GroupViewHolder groupViewHolder, final ChildViewHolder childViewHolder, final int position) {

        mDeviceManager.loginDevice(mDevice, LoginMethod.TRY_ALL, new DeviceManager.LoginDeviceInterface() {
            @Override
            public void onLoginSuccess(long loginID) {

                ((DeviceListActivity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
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

                        childViewHolder.recyclerViewChannels.setLayoutManager(new GridLayoutManager(mContext, DeviceListActivity.listComponents.get(position).numQuad, GridLayoutManager.HORIZONTAL, false));
                        childViewHolder.mRecyclerAdapter = new ChannelRecyclerViewAdapter(mContext, groupViewHolder.mDevice, DeviceListActivity.listComponents.get(position).numQuad, childViewHolder, DeviceListActivity.listComponents.get(position));
                        childViewHolder.recyclerViewChannels.setAdapter(childViewHolder.mRecyclerAdapter);

                    }
                });
            }

            @Override
            public void onLoginError(long loginID) {
                ((DeviceListActivity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        groupViewHolder.ivQuad.setVisibility(View.INVISIBLE);
                        groupViewHolder.ivMore.setVisibility(View.INVISIBLE);
                        childViewHolder.recyclerViewChannels.setVisibility(View.GONE);
                        childViewHolder.tvMessage.setVisibility(View.VISIBLE);
                        childViewHolder.tvMessage.setText("Erro ao conectar com o dispositivo.");
                    }
                });
            }

            @Override
            public void onLoginCloud() {
                ((DeviceListActivity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        childViewHolder.tvMessage.setText("Conectando via Cloud...");
                    }
                });
            }

            @Override
            public void onLoginLAN() {
                ((DeviceListActivity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        childViewHolder.tvMessage.setText("Conectando via LAN...");
                    }
                });
            }

            @Override
            public void onLoginDDNS() {
                ((DeviceListActivity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        childViewHolder.tvMessage.setText("Conectando via DDNS...");
                    }
                });
            }
        });
    }

    private void showMoreDialog(final GroupViewHolder groupViewHolder) {

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("")
                .setItems(new CharSequence[]{"Configurações", "Controle Remoto", "Playback"},
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:

                                        if (groupViewHolder.mDevice.getLoginID() != 0) {
                                            Bundle extras = new Bundle();
                                            extras.putSerializable("device", groupViewHolder.mDevice);

                                            Intent intent = new Intent(mContext, ConfigMenuActivity.class);
                                            intent.putExtras(extras);

                                            mContext.startActivity(intent);
                                        }

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


}
