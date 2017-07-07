package br.inatel.icc.gigasecurity.gigamonitor.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

import br.inatel.icc.gigasecurity.gigamonitor.R;
import br.inatel.icc.gigasecurity.gigamonitor.model.FileData;
import br.inatel.icc.gigasecurity.gigamonitor.util.Utils;

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
    private ArrayList<FileData> mFileDataList;

    private LayoutInflater mInflater;

    public DevicePlaybacksAdapter(Context context, ArrayList<FileData> fileDataList) {
        mInflater     = LayoutInflater.from(context);
        mFileDataList = fileDataList;
    }

    @Override
    public int getCount() {
        return mFileDataList.size();
    }

    @Override
    public FileData getItem(int position) {
        return mFileDataList.get(position);
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

        FileData fileData = getItem(position);

//        String startTime = String.format("%02d:%02d:%02d",
//                fileData.stBeginTime.hour,
//                fileData.stBeginTime.minute,
//                fileData.stBeginTime.second);

        String startTime = mFileDataList.get(position).mFileBeginTime;
        String endTime = mFileDataList.get(position).mFileEndTime;
        String videoType = Utils.getFileTypeName(fileData.mFileType);
        String videoQuality = "";
        if(mFileDataList.get(position).getStreamType() == 0){
            videoQuality = "HD";
        } else
            videoQuality = "SD";

        ((TextView) view.findViewById(R.id.text_start_time)).setText(startTime);
        ((TextView) view.findViewById(R.id.text_end_time)).setText(endTime);
        ((TextView) view.findViewById(R.id.text_record_type)).setText(videoType);
        ((TextView) view.findViewById(R.id.text_video_quality)).setText(videoQuality);

        return view;
    }
}
