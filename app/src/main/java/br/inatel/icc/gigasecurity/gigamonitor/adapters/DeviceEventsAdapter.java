//package br.inatel.icc.gigasecurity.gigamonitor.adapters;
//
//import android.content.Context;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.BaseAdapter;
//import android.widget.TextView;
//
//import com.basic.G;
//import com.xm.javaclass.SDK_LogItem;
//import com.xm.javaclass.SDK_LogList;
//
//import java.text.DateFormat;
//
//import br.inatel.icc.gigasecurity.gigamonitor.R;
//
///**
// * File: DevicePlaybacksAdapter.java
// * Creation date: 02/12/2014
// * Author: denisvilela
// * <p/>
// * Purpose: Declaration of class DevicePlaybacksAdapter.java
// * Copyright 2014, INATEL Competence Center
// * <p/>
// * All rights are reserved. Reproduction in whole or part is
// * prohibited without the written consent of the copyright owner.
// */
//public class DeviceEventsAdapter extends BaseAdapter {
//    private SDK_LogList mLogList;
//
//    private String[] eventTypeNames;
//
//    DateFormat mDateFormat;
//    private LayoutInflater mInflater;
//
//    public DeviceEventsAdapter(Context context, SDK_LogList fileDataGigaList) {
//        mInflater = LayoutInflater.from(context);
//        mLogList = fileDataGigaList;
//
//        mDateFormat = android.text.format.DateFormat.getDateFormat(context.getApplicationContext());
//        eventTypeNames = context.getResources().getStringArray(R.array.event_code_types_names);
//
//    }
//
//    @Override
//    public int getCount() {
//        return mLogList.st_0_iNumLog;
//    }
//
//    @Override
//    public SDK_LogItem getItem(int position) {
//        return mLogList.st_1_Logs[position];
//    }
//
//    @Override
//    public long getItemId(int position) {
//        return position;
//    }
//
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        View view = convertView;
//
//        if (view == null) {
//            view = mInflater.inflate(R.layout.list_view_cell_playback, parent, false);
//        }
//
//        SDK_LogItem logItem = mLogList.st_1_Logs[position];
//
////        logItem.st_0_sType = eventTypeNames[logItem.st_0_sType];
//
//
////        sb.append("\nLogItem.st_4_iLogPosition: ").append(logItem.st_4_iLogPosition);
////        sb.append("\nLogItem.st_1_sUser: ").append(G.ToString(logItem.st_1_sUser));
////        sb.append("\nLogItem.st_3_stLogTime: ").append();
////
//
//        //FIXME: For some reason, Calendar is not setting the right day of the mounth.
//        //final String date = mDateFormat.format(Utils.parseSDKTime(logItem.st_3_stLogTime));
//        //Therefore, it is made with some brute force, remember to fix this some day.
//
//        final String date = logItem.st_3_stLogTime.st_2_day + "/" + logItem.st_3_stLogTime.st_1_month + "/" + logItem.st_3_stLogTime.st_0_year;
//        ((TextView) view.findViewById(R.id.text1)).setText(date);
//        ((TextView) view.findViewById(R.id.text2)).setText(G.ToString(logItem.st_0_sType));
//
//        return view;
//    }
//}
