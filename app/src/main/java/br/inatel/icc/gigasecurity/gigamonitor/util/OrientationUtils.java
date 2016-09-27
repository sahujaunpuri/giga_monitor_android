package br.inatel.icc.gigasecurity.gigamonitor.util;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;

/** Static methods related to device orientation. */
public class OrientationUtils {
    private OrientationUtils() {}
    
    /** Returns true if device in in landscape mode, otherwise false. */
    public static boolean isLandscape(Context context) {
        return context.getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_LANDSCAPE;
    }
    
    /** Returns true if device in in portrait mode, otherwise false. */
    public static boolean isPortrait(Context context) {
        return context.getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_PORTRAIT;
    }

    /** Locks the device window in landscape mode. */
    public static void setOrientationLandscape(Activity activity) {
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }
    
    /** Locks the device window in portrait mode. */
    public static void setOrientationPortrait(Activity activity) {
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }
    
    /** Allows user to freely use portrait or landscape mode. */
    public static void unlockOrientation(Activity activity) {
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
    }

    /** Locks orientation . */
    public static void lockOrientation(Activity activity) {
        if(isLandscape(activity)){
            setOrientationLandscape(activity);
        }else{
            setOrientationPortrait(activity);
        }
    }
    
}