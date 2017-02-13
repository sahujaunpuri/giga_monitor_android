package br.inatel.icc.gigasecurity.gigamonitor.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import br.inatel.icc.gigasecurity.gigamonitor.model.Device;

/**
 * File: StreamingDevicesAdapter.java
 * Creation date: 07/11/2014
 * Author: denisvilela
 * <p/>
 * Purpose: Declaration of class StreamingDevicesAdapter.java
 * Copyright 2014, INATEL Competence Center
 * <p/>
 * All rights are reserved. Reproduction in whole or part is
 * prohibited without the written consent of the copyright owner.
 */
public class StreamingDevicesAdapter extends BaseAdapter {

    private ArrayList<Device> mDevices;

    private LayoutInflater mInflater;

    public StreamingDevicesAdapter(Context context, ArrayList<Device> devices) {
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mDevices = devices;
    }

    public boolean add(Device d) {
        return mDevices.add(d);
    }

    public boolean remove(Device d) {
        return mDevices.remove(d);
    }

    @Override
    public int getCount() {
        return mDevices.size();
    }

    @Override
    public Device getItem(int position) {
        return mDevices.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = mInflater.inflate(android.R.layout.simple_list_item_2, parent, false);
        }
        Device device = getItem(position);

        TextView tv1 = (TextView) view.findViewById(android.R.id.text1);
        tv1.setTextColor(Color.BLACK);
        tv1.setText(device.getHostname());

        TextView tv2 = (TextView) view.findViewById(android.R.id.text2);
        tv1.setTextColor(Color.DKGRAY);
        tv2.setText(device.getHostID());

        return view;
    }
}
