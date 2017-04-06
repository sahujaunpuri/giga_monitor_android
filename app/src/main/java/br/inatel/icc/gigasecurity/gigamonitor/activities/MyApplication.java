package br.inatel.icc.gigasecurity.gigamonitor.activities;

import android.app.Application;
import android.content.res.Configuration;
import android.util.Log;

/**
 * Created by zappts on 4/5/17.
 */

public class MyApplication extends Application {

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreate() {
        Log.d("ROCALI", "onCreate: ");
        super.onCreate();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Log.d("ROCALI", "onLowMemory: ");
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        Log.d("ROCALI", "onTerminate: ");
    }

}