package br.inatel.icc.gigasecurity.gigamonitor.config;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import br.inatel.icc.gigasecurity.gigamonitor.R;
import br.inatel.icc.gigasecurity.gigamonitor.core.DeviceManager;

/**
 * File: AbstractConfig.java
 * Creation date: 06/10/2014
 * Author: rinaldo.bueno
 * <p/>
 * Purpose: Declaration of class AbstractConfig.java
 * Copyright 2014, INATEL Competence Center
 * <p/>
 * All rights are reserved. Reproduction in whole or part is
 * prohibited without the written consent of the copyright owner.
 */
public abstract class ConfigAbstract<E> {

    private String TAG = getConfigType() == null ? "ConfigAbstract" : getConfigType().getSimpleName();

    protected E mConfig;
    ProgressDialog mProgressDialog;

    public E getConfig(Long loginID) throws Exception {
        Log.d(TAG, String.format("Getting for %s", loginID));
        return DeviceManager.getInstance().getConfig(loginID, getConfigKey(), getConfigEntity());
    }

    public boolean setConfig(Long loginID) throws Exception {
        Log.d(TAG, String.format("Setting for %s", loginID));
        return DeviceManager.getInstance().setConfig(loginID, getConfigKey(), getConfigEntity());
    }

    public void getConfigTask(final Context context, final Long loginID, final ConfigGetTaskListener configListener) {
        AsyncTask<Void, Void, E> task = new AsyncTask<Void, Void, E>() {

            @Override
            protected E doInBackground(Void... voids) {
                try {
                    return getConfig(loginID);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d(TAG, String.format("Unable to get. loginId: %s", loginID));
                }

                Log.d(TAG, String.format("Unable to get. loginId: %s", loginID));
                return null;
            }

            @Override
            protected void onPreExecute() {
                openProgressDialog(context);

                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(E e) {
                boolean success = e != null;

                if(success){
                    fillFromNetSdkConfig(e);

                    Log.d(TAG, String.format("Get Successed. loginId: %s", loginID));
                }

                configListener.onPreFinish(success);
                closeProgressDialog(context);
                configListener.onFinish(success);

                super.onPostExecute(e);
            }
        };

        task.execute();
    }

    public void setConfigTask(final Context context, final Long loginID, final ConfigSetTaskListener configListener) {
        mConfig =  getAsNetSdkConfig();

        AsyncTask<E, Void, Boolean> task = new AsyncTask<E, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(E... es) {
                try {
                    return setConfig(loginID);
                } catch (Exception e) {
                    Log.d(TAG, String.format("Unable to get configuration. loginId: %s", loginID));
                    e.printStackTrace();
                }

                Log.d(TAG, String.format("Unable to get configuration. loginId: %s", loginID));
                return null;
            }

            @Override
            protected void onPreExecute() {
                openProgressDialog(context);
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(Boolean success) {
                if(success){
                    Log.d(TAG, String.format("Set Successed. loginId: %s", loginID));
                }
                closeProgressDialog(context);
                configListener.onFinish(success);
                super.onPostExecute(success);
            }

        };

        task.execute(mConfig);
    }

    private void openProgressDialog(Context context) {
        if (context != null) {
            mProgressDialog = ProgressDialog.show(context,
                    "", context.getResources().getString(R.string.wait_dialog_message, true));
        }
    }

    private void closeProgressDialog(Context context) {
        if (context != null && mProgressDialog != null ) {
            mProgressDialog.dismiss();
        }
    }


    public abstract void fillFromNetSdkConfig(E config);

    public abstract E getAsNetSdkConfig();

    public abstract long getConfigKey();

    public abstract Class<E> getConfigType();

    public interface ConfigGetTaskListener {
        void onPreFinish(Boolean success);

        void onFinish(Boolean success);
    }

    public interface ConfigSetTaskListener {
        void onFinish(Boolean success);
    }

    public E getConfigEntity() {

        if(mConfig == null)
            try {
                mConfig = getConfigType().newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

        return mConfig;
    }
}


