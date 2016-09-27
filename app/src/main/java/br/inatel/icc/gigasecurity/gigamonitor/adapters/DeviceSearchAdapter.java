package br.inatel.icc.gigasecurity.gigamonitor.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import br.inatel.icc.gigasecurity.gigamonitor.R;
import br.inatel.icc.gigasecurity.gigamonitor.core.DeviceManager;
import br.inatel.icc.gigasecurity.gigamonitor.model.Device;
import br.inatel.icc.gigasecurity.gigamonitor.model.LoginMethod;

/**
 * Created by filipecampos on 07/07/2015.
 */
public class DeviceSearchAdapter extends BaseAdapter {

    private ArrayList<Device> mDevices;
    private DeviceManager mDeviceManager;
    private LayoutInflater mInflater;
    private Context mContext;
    private View[] mView;

    public DeviceSearchAdapter(Context context, ArrayList<Device> devices) {
        this.mContext = context;
        this.mDevices = devices;
        this.mView = new View[mDevices.size()];
        this.mDeviceManager = DeviceManager.getInstance();
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mDevices.size();
    }

    @Override
    public Object getItem(int position) {
        return mDevices.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(mView[position] == null) {

            ItemHolder itemHolder = new ItemHolder();

            mView[position] = new View(mContext);
            mView[position] = mInflater.inflate(R.layout.list_view_search_devices, null);

            itemHolder.tvDeviceName     = (TextView) mView[position].findViewById(R.id.tv_search_device_name);
            itemHolder.tvSerialNumber   = (TextView) mView[position].findViewById(R.id.tv_search_serial_number);
            itemHolder.tvIpAddress      = (TextView) mView[position].findViewById(R.id.tv_search_ip_address);
            itemHolder.tvMacAddress     = (TextView) mView[position].findViewById(R.id.tv_search_mac_address);
            itemHolder.ivImg            = (ImageView) mView[position].findViewById(R.id.iv_device_image);

            itemHolder.tvDeviceName.setText(mDevices.get(position).getHostname());
            itemHolder.tvSerialNumber.setText(mDevices.get(position).getSerialNumber());
            itemHolder.tvIpAddress.setText(mDevices.get(position).getIpAddress());
            itemHolder.tvMacAddress.setText(mDevices.get(position).getMacAddress());

            mDeviceManager.loginOnDevice(mDevices.get(position), LoginMethod.LAN);

            if (mDevices.get(position).getChannelNumber() > 1) {
                itemHolder.ivImg.setImageDrawable(mContext.getResources().getDrawable(R.drawable.img_dvr));
            }

            /*try {
                mDeviceManager.logout(mDevices.get(position));
            } catch (Exception e) {
                e.printStackTrace();
            }*/

            mView[position].setTag(itemHolder);
        }

        convertView = mView[position];

        return convertView;
    }

    public class ItemHolder {
        public TextView tvDeviceName;
        public TextView tvSerialNumber;
        public TextView tvIpAddress;
        public TextView tvMacAddress;
        public ImageView ivImg;
    }
}
