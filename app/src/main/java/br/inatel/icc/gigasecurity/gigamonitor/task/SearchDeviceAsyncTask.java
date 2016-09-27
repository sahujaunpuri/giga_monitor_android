package br.inatel.icc.gigasecurity.gigamonitor.task;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import br.inatel.icc.gigasecurity.gigamonitor.R;
import br.inatel.icc.gigasecurity.gigamonitor.activities.DeviceSearchListActivity;
import br.inatel.icc.gigasecurity.gigamonitor.core.DeviceManager;
import br.inatel.icc.gigasecurity.gigamonitor.model.Device;

import java.util.ArrayList;

/**
 * File: SearchDeviceAsycTask.java
 * Creation date: 01/10/2014
 * Author: rinaldo.bueno
 * <p/>
 * Purpose: Declaration of class SearchDeviceAsycTask.java
 * Copyright 2014, INATEL Competence Center
 * <p/>
 * All rights are reserved. Reproduction in whole or part is
 * prohibited without the written consent of the copyright owner.
 */
public class SearchDeviceAsyncTask extends AsyncTask<Void, Void, ArrayList<Device>> {
    ArrayList<Device> mDevices;
    DeviceSearchListActivity mDeviceSearchListActivity;
    ProgressDialog mProgressDialog;
    Context mContext;

    public SearchDeviceAsyncTask(Context context) {
        mContext = context;
    }

    @Override
    protected ArrayList<Device> doInBackground(Void... voids) {
        mDevices = DeviceManager.getInstance().searchDevices();

        return mDevices;
    }

    @Override
    protected void onPreExecute() {

        mProgressDialog = ProgressDialog.show(mDeviceSearchListActivity, "", mContext.getResources().getString(R.string.searching_dialog_messsage, true));
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(ArrayList<Device> devices) {
        mProgressDialog.dismiss();
        super.onPostExecute(devices);
    }
}
