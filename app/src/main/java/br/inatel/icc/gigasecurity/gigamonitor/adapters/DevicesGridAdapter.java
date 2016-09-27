package br.inatel.icc.gigasecurity.gigamonitor.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.xm.video.MySurfaceView;

import java.util.ArrayList;

/**
 * File: DevicesGridAdapter.java
 * Creation date: 09/09/2014
 * Author: denisvilela
 * <p/>
 * Purpose: Declaration of class DevicesGridAdapter.java
 * Copyright 2014, INATEL Competence Center
 * <p/>
 * All rights are reserved. Reproduction in whole or part is
 * prohibited without the written consent of the copyright owner.
 */
public class DevicesGridAdapter extends BaseAdapter{

    private ArrayList<MySurfaceView> mSurfaceViews;
    private Context mContext;

    public DevicesGridAdapter(Context c, ArrayList<MySurfaceView> surfaceViews) {
        mContext = c;
        mSurfaceViews = surfaceViews;
    }

    @Override
    public int getCount() {
        return mSurfaceViews.size();
    }

    @Override
    public MySurfaceView getItem(int i) {
        return mSurfaceViews.get(i);
    }

    @Override
    public long getItemId(int i) {
        return getItem(i).getId();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        return mSurfaceViews.get(i);
    }
}
