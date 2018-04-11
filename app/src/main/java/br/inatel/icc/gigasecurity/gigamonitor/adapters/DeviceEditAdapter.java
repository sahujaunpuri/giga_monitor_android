package br.inatel.icc.gigasecurity.gigamonitor.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;

import br.inatel.icc.gigasecurity.gigamonitor.R;
import br.inatel.icc.gigasecurity.gigamonitor.activities.DeviceEditListActivity;
import br.inatel.icc.gigasecurity.gigamonitor.activities.DeviceFormActivity;
import br.inatel.icc.gigasecurity.gigamonitor.core.DeviceManager;
import br.inatel.icc.gigasecurity.gigamonitor.model.Device;

/**
 * Created by filipecampos on 09/07/2015.
 * Updated by williampenna on 25/08/2017.
 */
public class DeviceEditAdapter extends BaseAdapter {

    private ArrayList<Device> mDevices;
    private DeviceManager mDeviceManager;
    private Context mContex;
    public boolean modifiedDevice;

    public DeviceEditAdapter(Context context, ArrayList<Device> devices, boolean modifiedDevice) {
        this.mContex = context;
        this.mDevices = devices;
        this.mDeviceManager = DeviceManager.getInstance();
        modifiedDevice = modifiedDevice;
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContex.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item_edit_device, null);
            holder = new ViewHolder();

            holder.delete = (ImageView) convertView.findViewById(R.id.iv_delete_device);
            holder.deviceName = (TextView) convertView.findViewById(R.id.tv_item_edit_device);
            holder.deviceIcon = (ImageView) convertView.findViewById(R.id.image_view_device_icon);
            holder.enable = (Switch) convertView.findViewById(R.id.switch_enable_device);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.deviceIcon.setImageDrawable(mContex.getResources().getDrawable(R.drawable.dvr_icon));

        if(mDevices.get(position).getDeviceName().equals("Favoritos")) {
            holder.delete.setVisibility(View.INVISIBLE);
            holder.enable.setVisibility(View.INVISIBLE);
        } else {
            holder.delete.setVisibility(View.VISIBLE);
            holder.enable.setVisibility(View.VISIBLE);
        }

        holder.deviceName.setText(mDevices.get(position).deviceName);
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteConfirmation(position);
            }
        });

        if(!mDevices.get(position).getDeviceName().equals("Favoritos"))
            holder.deviceName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startDeviceFormActivity(position);
                }
            });

        holder.enable.setChecked(mDevices.get(position).isEnable());

        holder.enable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDevices.get(position).setEnable(holder.enable.isChecked());
                modifiedDevice = true;
            }
        });

        convertView.setTag(holder);
        return convertView;
    }

    private void startDeviceFormActivity(int position) {
        Bundle args = new Bundle();

        args.putInt("index", position);

        Intent intent = new Intent(mContex, DeviceFormActivity.class);
        intent.putExtras(args);

        mContex.startActivity(intent);
    }

    private void showDeleteConfirmation(final int deletePosition) {
        AlertDialog.Builder alert = new AlertDialog.Builder(mContex, R.style.Base_Theme_AppCompat_Light_Dialog);

        alert.setTitle(R.string.action_delete);
        alert.setMessage(R.string.action_delete_confirmation);
        alert.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                DeviceEditListActivity.lv.removeItem(deletePosition);
            }
        });
        alert.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alert.show();
    }

    private class ViewHolder {
        private Switch enable;
        private ImageView delete;
        private TextView deviceName;
        private ImageView deviceIcon;
    }
}
