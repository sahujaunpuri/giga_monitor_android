package br.inatel.icc.gigasecurity.gigamonitor.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import br.inatel.icc.gigasecurity.gigamonitor.R;
import br.inatel.icc.gigasecurity.gigamonitor.model.FileDataGiga;
import br.inatel.icc.gigasecurity.gigamonitor.util.Utils;

import java.util.ArrayList;

/**
 * File: DevicePlaybacksAdapter.java
 * Creation date: 02/12/2014
 * Author: denisvilela
 * <p/>
 * Purpose: Declaration of class DevicePlaybacksAdapter.java
 * Copyright 2014, INATEL Competence Center
 * <p/>
 * All rights are reserved. Reproduction in whole or part is
 * prohibited without the written consent of the copyright owner.
 */
public class DevicePlaybacksAdapter extends BaseAdapter {
    private ArrayList<FileDataGiga> mFileDataGigaList;

    private LayoutInflater mInflater;

    public DevicePlaybacksAdapter(Context context, ArrayList<FileDataGiga> fileDataGigaList) {
        mInflater = LayoutInflater.from(context);
        mFileDataGigaList = fileDataGigaList;
    }

    @Override
    public int getCount() {
        return mFileDataGigaList.size();
    }

    @Override
    public FileDataGiga getItem(int position) {
        return mFileDataGigaList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            view = mInflater.inflate(R.layout.list_view_cell_playback, parent, false);
        }

        FileDataGiga fileDataGiga = getItem(position);

        String startTime = String.format("%02d:%02d:%02d",
                fileDataGiga.stBeginTime.hour,
                fileDataGiga.stBeginTime.minute,
                fileDataGiga.stBeginTime.second);

        String videoType = Utils.getFileTypeName(fileDataGiga.filetype);

        ((TextView) view.findViewById(R.id.text1)).setText(startTime);
        ((TextView) view.findViewById(R.id.text2)).setText(videoType);

        return view;
    }
}
