package br.inatel.icc.gigasecurity.gigamonitor.activities;
import android.app.Activity;
import android.os.Bundle;

import br.inatel.icc.gigasecurity.gigamonitor.R;
import com.xm.video.MySurfaceView;

/**
 * File: DeviceMonitorActivity.java
 * Creation date: 05/09/2014
 * Author: Dênis Vilela
 *
 * Purpose: Declaration of class DeviceMonitorActivity.java
 * Copyright 2014, INATEL Competence Center
 *
 * All rights are reserved. Reproduction in whole or part is
 * prohibited without the written consent of the copyright owner.
 */
public class SurfaceViewTestActivity extends Activity{
    private MySurfaceView mSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_monitor);
        initComponents();
    }

    private void initComponents() {
        mSurfaceView = (MySurfaceView) findViewById(R.id.surface_view_test_1);

    }

    public MySurfaceView[] getSurfaceViews() {
        return new MySurfaceView[] {mSurfaceView};
    }
}
