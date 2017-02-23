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
import android.widget.TextView;

import java.util.ArrayList;

import br.inatel.icc.gigasecurity.gigamonitor.R;
import br.inatel.icc.gigasecurity.gigamonitor.activities.DeviceEditListActivity;
import br.inatel.icc.gigasecurity.gigamonitor.activities.DeviceFormActivity;
import br.inatel.icc.gigasecurity.gigamonitor.core.DeviceManager;
import br.inatel.icc.gigasecurity.gigamonitor.model.Device;

/**
 * Created by filipecampos on 09/07/2015.
 */
public class DeviceEditAdapter extends BaseAdapter {

    private ArrayList<Device> mDevices;
    private DeviceManager mDeviceManager;
    private Context mContex;
    private TextView tvDeviceName;
    private ImageView ivDeleteDevice;
    private LayoutInflater mInflater;

    public DeviceEditAdapter(Context context, ArrayList<Device> devices) {
        this.mContex = context;
        this.mDevices = devices;
        this.mInflater = LayoutInflater.from(context);
        this.mDeviceManager = DeviceManager.getInstance();
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

        if(convertView == null) {
            convertView = mInflater.inflate(R.layout.list_item_edit_device, null);

            tvDeviceName = (TextView) convertView.findViewById(R.id.tv_item_edit_device);
            ivDeleteDevice = (ImageView) convertView.findViewById(R.id.iv_delete_device);
        }

        tvDeviceName.setText(mDevices.get(position).deviceName);

        ivDeleteDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteConfirmation(position);
            }
        });

        tvDeviceName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDeviceFormActivity(position);
            }
        });

        return convertView;
    }

    private void startDeviceFormActivity(int position) {
        Bundle args = new Bundle();

        args.putSerializable("device", mDevices.get(position));
        args.putInt("index", position);

        Intent intent = new Intent(mContex, DeviceFormActivity.class);
        intent.putExtras(args);

        mContex.startActivity(intent);
    }

    private void showDeleteConfirmation(final int deletePosition) {
        AlertDialog.Builder alert = new AlertDialog.Builder(
                mContex);

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
}
