package br.inatel.icc.gigasecurity.gigamonitor.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import br.inatel.icc.gigasecurity.gigamonitor.R;
import br.inatel.icc.gigasecurity.gigamonitor.core.DeviceManager;
import br.inatel.icc.gigasecurity.gigamonitor.model.Device;
import br.inatel.icc.gigasecurity.gigamonitor.ui.SurfaceViewComponent;

/**
 * Created by ZapptsDev on 10/24/17.
 */

public class FavoritesDevicesAdapter extends BaseExpandableListAdapter {

    private ArrayList<Device> mDevices;
    private DeviceManager mDeviceManager;
    private Context mContext;
    ImageView imageViewChannelStar;

    public FavoritesDevicesAdapter(Context context, ArrayList<Device> Devices){
        this.mContext = context;
        this.mDeviceManager = DeviceManager.getInstance();
        this.mDevices = Devices;
    }

    @Override
    public int getGroupCount() {
        return mDevices.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mDevices.get(groupPosition).getChannelNumber();
    }

    @Override
    public Object getGroup(int groupPosition) {
       return mDevices.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mDevices.get(groupPosition).channelsManager.surfaceViewComponents.get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        final Device mDevice = mDevices.get(groupPosition);

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_group_fav_devices, null);
        }

        imageViewChannelStar = (ImageView) convertView.findViewById(R.id.fav_list_item_select_device);

        if (isDeviceFav(mDevice)) {
            imageViewChannelStar.setImageResource(R.drawable.ic_star_yellow_24dp);
        }

        return createDeviceView(groupPosition, isExpanded, convertView, parent);
    }

    public View createDeviceView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        final Device mDevice = mDevices.get(groupPosition);


        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.list_group_fav_devices, null);

        holder = new ViewHolder();
        holder.name = (TextView) convertView.findViewById(R.id.fav_list_item_device_name);
        holder.star = (ImageView) convertView.findViewById(R.id.fav_list_item_select_device);

        if (mDevices.get(groupPosition).getId() == ("Favoritos").hashCode()) {
            holder.star.setVisibility(View.INVISIBLE);
        } else {
            holder.star.setVisibility(View.VISIBLE);
        }

        if (isDeviceFav(mDevice)) {
            holder.star.setImageResource(R.drawable.ic_star_yellow_24dp);
        }

        convertView.setTag(holder);

        holder.name.setText(mDevices.get(groupPosition).deviceName);

        holder.star.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isDeviceFav(mDevice)) {
                    holder.star.setImageResource(R.drawable.ic_star_white_36dp);
                    removeFavDevice(mDevice);
                } else if (mDevice.getChannelNumber() > 0) {
                    holder.star.setImageResource(R.drawable.ic_star_yellow_24dp);
                    for (int channel = 0; channel < mDevice.getChannelNumber(); channel ++) {
                        if (!mDeviceManager.findChannelManagerByDevice(mDevice).surfaceViewComponents.get(channel).isFavorite()) {
                            mDeviceManager.addFavorite(mDeviceManager.findChannelManagerByDevice(mDevice).surfaceViewComponents.get(channel));
                        }
                    }
                }
                notifyDataSetChanged();
            }
        });

        convertView.setTag(holder);
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final SurfaceViewComponent mChannel =  mDeviceManager.findChannelManagerByDevice(mDeviceManager.getDevices().get(groupPosition)).surfaceViewComponents.get(childPosition);

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item_fav_channels, null);
        }

        imageViewChannelStar = (ImageView) convertView.findViewById(R.id.fav_list_item_select_channel);

        if (mChannel.isFavorite()) {
            imageViewChannelStar.setImageResource(R.drawable.ic_star_yellow_24dp);
        }

        return createChannelView(groupPosition, childPosition, isLastChild, convertView, parent);
    }

    public View createChannelView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent){
        final ViewHolder holder;
        final SurfaceViewComponent mChannel =  mDeviceManager.findChannelManagerByDevice(mDeviceManager.getDevices().get(groupPosition)).surfaceViewComponents.get(childPosition);

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.list_item_fav_channels, null);

        holder = new ViewHolder();
        holder.name = (TextView) convertView.findViewById(R.id.fav_list_item_channel_name);
        holder.star = (ImageView) convertView.findViewById(R.id.fav_list_item_select_channel);

        if (mChannel.isFavorite()) {
            holder.star.setImageResource(R.drawable.ic_star_yellow_24dp);
        }

        convertView.setTag(holder);

        int position = childPosition + 1;

        holder.name.setText("Canal " + position);
        holder.star.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mChannel.isFavorite()) {
                    holder.star.setImageResource(R.drawable.ic_star_grey_24dp);
                    mDeviceManager.removeFavorite(mChannel);
                } else {
                    holder.star.setImageResource(R.drawable.ic_star_yellow_24dp);
                    mDeviceManager.addFavorite(mChannel);
                }
                notifyDataSetChanged();
            }
        });

        return convertView;
    }

    private class ViewHolder {
        private ImageView star;
        private TextView name;
    }

    private boolean isDeviceFav(Device device) {
        int mChannelsCount = 0;
        for (int channel = 0; channel < device.getChannelNumber(); channel ++) {
           if (mDeviceManager.findChannelManagerByDevice(device).surfaceViewComponents.get(channel).isFavorite()) {
               mChannelsCount += 1;
           }
        }

        if (mChannelsCount == mDeviceManager.findChannelManagerByDevice(device).channelNumber && mDeviceManager.findChannelManagerByDevice(device).channelNumber > 0) {
            return true;
        } else {
            return false;
        }
    }

    private void removeFavDevice(Device device) {
        for (int channel = 0; channel < device.getChannelNumber(); channel ++) {
            mDeviceManager.removeFavorite(mDeviceManager.findChannelManagerByDevice(device).surfaceViewComponents.get(channel));
            notifyDataSetChanged();
        }
    }

}
