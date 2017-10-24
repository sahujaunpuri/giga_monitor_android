package br.inatel.icc.gigasecurity.gigamonitor.adapters;

import android.content.Context;
import android.graphics.Color;
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
    TextView textViewDeviceName;
    ImageView imageViewDeviceStar;
    TextView textViewChannelName;
    ImageView imageViewChannelStar;

    public FavoritesDevicesAdapter(Context context, ArrayList<Device> Devices){
        this.mContext = context;
        this.mDeviceManager = DeviceManager.getInstance();

//        for (int index = 0; index < Devices.size(); index++) {
//            if (Devices.get(index).getSerialNumber().equals("Favoritos")) {
//                Devices.remove(index);
//            }
//        }

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
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        return createDeviceView(groupPosition, isExpanded, convertView, parent);
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        return createChannelView(groupPosition, childPosition, isLastChild, convertView, parent);
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    public View createDeviceView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        Device mDevice = mDevices.get(groupPosition);

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_group_fav_devices, null);
        }

        textViewDeviceName = (TextView) convertView.findViewById(R.id.fav_list_item_device_name);
        textViewDeviceName.setText(mDevice.deviceName);

        imageViewDeviceStar = (ImageView) convertView.findViewById(R.id.fav_list_item_select_device);
        imageViewDeviceStar.setOnClickListener(addDeviceToFavorites(mDevice));


        return convertView;
    }

    public View createChannelView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent){
        SurfaceViewComponent mChannel =  mDeviceManager.findChannelManagerByDevice(mDeviceManager.getDevices().get(groupPosition)).surfaceViewComponents.get(childPosition);

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item_fav_channels, null);
        }

        textViewChannelName = (TextView) convertView.findViewById(R.id.fav_list_item_channel_name);
        textViewChannelName.setText("Channel " + childPosition);

        imageViewChannelStar = (ImageView) convertView.findViewById(R.id.fav_list_item_select_channel);
        imageViewChannelStar.setOnClickListener(addChannelToFavorites(mChannel));

        return convertView;
    }

    private View.OnClickListener addDeviceToFavorites(final Device device){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageViewDeviceStar.setColorFilter(Color.parseColor("#FFFF00"));
                for (int channel = 0; channel < device.getChannelNumber(); channel ++) {
                    mDeviceManager.addFavorite(mDeviceManager.findChannelManagerByDevice(device).surfaceViewComponents.get(channel));
                }
            }
        };
    }

    private View.OnClickListener addChannelToFavorites(final SurfaceViewComponent channel){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageViewChannelStar.setColorFilter(Color.parseColor("#FFFF00"));
                mDeviceManager.addFavorite(channel);
            }
        };
    }

    private class ViewHolder {
        private ImageView star;
        private TextView name;
    }

}
