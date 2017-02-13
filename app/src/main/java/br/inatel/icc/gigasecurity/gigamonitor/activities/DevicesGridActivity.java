//package br.inatel.icc.gigasecurity.gigamonitor.activities;
//
//import android.app.Activity;
//import android.os.Bundle;
//import android.widget.GridView;
//
//import br.inatel.icc.gigasecurity.gigamonitor.R;
//import br.inatel.icc.gigasecurity.gigamonitor.adapters.DevicesGridAdapter;
//import com.xm.video.MySurfaceView;
//
//import java.util.ArrayList;
//
///**
// * File: DevicesGridActivity.java
// * Creation date: 09/09/2014
// * Author: denisvilela
// * <p/>
// * Purpose: Declaration of class DevicesGridActivity.java
// * Copyright 2014, INATEL Competence Center
// * <p/>
// * All rights are reserved. Reproduction in whole or part is
// * prohibited without the written consent of the copyright owner.
// */
//public class DevicesGridActivity extends Activity{
//    private static final String LOG_TAG = DevicesGridActivity.class.getSimpleName();
//
//    private GridView mGridView;
//
//    private ArrayList<MySurfaceView> mSurfaceViews;
//
//    enum NumCams {
//        ONE(1),
//        FOUR(4),
//        NINE(9),
//        SIXTEEN(16);
//
//        private int num;
//
//        private NumCams(final int num) {
//            this.num = num;
//        }
//
//        public int getNum() {
//            return num;
//        }
//    }
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_devices);
//        findViews();
//        initData();
//        setGridViewAdapter();
//    }
//
//    private void findViews() {
//        mGridView = (GridView) findViewById(R.id.grid_view_devices);
//    }
//
//    private void initData() {
//        mSurfaceViews = new ArrayList<MySurfaceView>(NumCams.FOUR.getNum());
//    }
//
//    private void setGridViewAdapter(){
//        mGridView.setAdapter(new DevicesGridAdapter(this, mSurfaceViews));
//    }
//}
