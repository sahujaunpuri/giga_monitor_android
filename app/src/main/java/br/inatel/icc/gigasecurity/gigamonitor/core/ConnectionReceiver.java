package br.inatel.icc.gigasecurity.gigamonitor.core;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.util.List;

/**
 * Created by zappts on 6/23/17.
 */

public class ConnectionReceiver extends BroadcastReceiver{
    private static final String TAG = "NetworkStateReceiver";
    @Override
    public void onReceive(final Context context, final Intent intent) {
        Log.d(TAG, "Network connectivity change");

        if (intent.getExtras() != null && isApplicationOnForeground(context)) {
            DeviceManager mDeviceManager = DeviceManager.getInstance();
            final ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
            final NetworkInfo info = connectivityManager.getActiveNetworkInfo();

            if (info != null && info.isConnectedOrConnecting()) {
                mDeviceManager.loginAllDevices();
                mDeviceManager.networkType = info.getType() == ConnectivityManager.TYPE_WIFI ? 1 : 0;
                Log.i(TAG, "Network " + info.getTypeName() + " connected");
//            } else if (intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, Boolean.FALSE)) {
//                Log.d(TAG, "There's no network connectivity");
//            }
            } else {
                mDeviceManager.networkType = -1;
                mDeviceManager.setDevicesLogout();
            }
        }
    }

    public boolean isApplicationOnForeground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses == null) {
            return false;
        }
        final String packageName = context.getPackageName();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND && appProcess.processName.equals(packageName)) {
                return true;
            }
        }
        return false;
    }
}
