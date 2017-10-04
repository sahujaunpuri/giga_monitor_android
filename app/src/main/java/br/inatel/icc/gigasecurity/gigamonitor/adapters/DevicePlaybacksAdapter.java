package br.inatel.icc.gigasecurity.gigamonitor.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.lib.SDKCONST;

import java.io.File;
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
public class DevicePlaybacksAdapter extends BaseAdapter implements Filterable{

    private Context mContext;
    private ArrayList<FileData> mFileDataList;
    private LayoutInflater mInflater;
    private ArrayList<FileData> allFiles;

    public DevicePlaybacksAdapter(Context context, ArrayList<FileData> fileDataList) {
        mContext = context;
        mInflater     = LayoutInflater.from(context);
        allFiles      = fileDataList;
        mFileDataList = fileDataList;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
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
        if(mFileDataList.isEmpty())
            return view;

        FileData fileData = getItem(position);

//        String startTime = String.format("%02d:%02d:%02d",
//                fileData.stBeginTime.hour,
//                fileData.stBeginTime.minute,
//                fileData.stBeginTime.second);

        String startTime = mFileDataList.get(position).mFileBeginTime;
        String endTime = mFileDataList.get(position).mFileEndTime;
        String videoType = Utils.getFileTypeName(fileData.mFileType);
        String videoQuality = "";
        if (mFileDataList.get(position).getStreamType() == 0) {
            videoQuality = "HD";
        } else
            videoQuality = "SD";

        ((TextView) view.findViewById(R.id.text_start_time)).setText(startTime);
        ((TextView) view.findViewById(R.id.text_end_time)).setText(endTime);
        ((TextView) view.findViewById(R.id.text_record_type)).setText(videoType);
        ((TextView) view.findViewById(R.id.text_video_quality)).setText(videoQuality);

        return view;
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence cs) {
                FilterResults results = new FilterResults();
                int type = Integer.valueOf(cs.toString());
                if (cs == null || cs.length() == 0 || type == 0) {
                    results.count = allFiles.size();
                    results.values = allFiles;
                } else {
//                    cs = cs.toString();
//                    int type = Utils.getIntFileType(cs.toString());
                    mFileDataList = new ArrayList<FileData>();
                    for (int i=0; i<allFiles.size(); i++) {
                        if (allFiles.get(i).mFileType == type) {
                            mFileDataList.add(allFiles.get(i));
                        }
                    }
                    results.count = mFileDataList.size();
                    results.values = mFileDataList;
                }
                if (results.count < 1) {
                    Toast.makeText(mContext, "Não há registros desse tipo!", Toast.LENGTH_SHORT).show();
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mFileDataList = (ArrayList<FileData>) filterResults.values;
                notifyDataSetChanged();
            }
        };

        return filter;
    }
}
