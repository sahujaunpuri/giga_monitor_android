//package br.inatel.icc.gigasecurity.gigamonitor.task;
//
//import android.app.ProgressDialog;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.os.AsyncTask;
//
//import br.inatel.icc.gigasecurity.gigamonitor.R;
//import br.inatel.icc.gigasecurity.gigamonitor.activities.DeviceListActivity;
//import br.inatel.icc.gigasecurity.gigamonitor.core.DeviceManager;
//import br.inatel.icc.gigasecurity.gigamonitor.model.Device;
//import br.inatel.icc.gigasecurity.gigamonitor.model.LoginMethod;
//
///**
// * File: SearchDeviceAsycTask.java
// * Creation date: 01/10/2014
// * Author: rinaldo.bueno
// * <p/>
// * Purpose: Declaration of class SearchDeviceAsycTask.java
// * Copyright 2014, INATEL Competence Center
// * <p/>
// * All rights are reserved. Reproduction in whole or part is
// * prohibited without the written consent of the copyright owner.
// */
//public class LoginDeviceAsyncTask extends AsyncTask<Void, Void, Long> {
//
//    public Device mDevice;
//    public Long mLoginID;
//    public LoginTaskListener mLoginTaskListener;
//    public LoginMethod mLoginMethod;
//    public DeviceManager mDeviceManager;
//
//    public static ProgressDialog mProgressDialog;
//    public static Context mContext;
//
//    public LoginDeviceAsyncTask(Context context, Device device, LoginMethod lm,
//                                LoginTaskListener loginTaskListener) {
//        mContext = context;
//        mDevice = device;
//        mLoginTaskListener = loginTaskListener;
//        mLoginMethod = lm;
//        mDeviceManager = DeviceManager.getInstance();
//    }
//
//    @Override
//    protected Long doInBackground(Void... voids) {
//        mLoginID = mDeviceManager.loginOnDevice(mDevice, mLoginMethod);
//
//        return mLoginID;
//    }
//
//    @Override
//    protected void onPreExecute() {
//        if(mProgressDialog != null) {
//            mProgressDialog.dismiss();
//        }
//
//        mProgressDialog = ProgressDialog.show(mContext, "", mContext.getResources().getString(R.string.label_connecting));
//        mProgressDialog.setCancelable(true);
//
//        mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
//            @Override
//            public void onCancel(DialogInterface dialog) {
//                DeviceListActivity.closeList();
//            }
//        });
//
//        super.onPreExecute();
//    }
//
//    @Override
//    protected void onPostExecute(Long loginID) {
//        //mProgressDialog.dismiss();
//
//        if(loginID == 0 || loginID == -11301){
//            mLoginTaskListener.onError(loginID);
//        }else{
//            mLoginTaskListener.onLogin(loginID);
//        }
//
//        super.onPostExecute(mLoginID);
//    }
//
//    public static void changeProgressDialogMsg(final String msg) {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                ((DeviceListActivity) mContext).runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        if(mProgressDialog != null) {
//                            mProgressDialog.setMessage(msg);
//                        }
//                    }
//                });
//            }
//        }).start();
//    }
//
//    public static void closeProgressDialogMsg() {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    Thread.sleep(5000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//
//                ((DeviceListActivity) mContext).runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (mProgressDialog != null) {
//                            mProgressDialog.dismiss();
//                        }
//                    }
//                });
//            }
//        }).start();
//    }
//
//    public interface LoginTaskListener {
//        void onLogin(Long loginID);
//        void onError(Long loginID);
//
//    }
//}
