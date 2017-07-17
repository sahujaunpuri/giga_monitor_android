package br.inatel.icc.gigasecurity.gigamonitor.util;

import android.util.Log;

/**
 * Created by zappts on 17/07/17.
 */

public class FunLog {

    private final static String PRETAG = "FunLog.";

    public static int v(String tag, String msg) {
        return Log.v(PRETAG+tag, msg);
    }

    public static int w(String tag, String msg) {
        return Log.w(PRETAG+tag, msg);
    }

    public static int i(String tag, String msg) {
        return Log.i(PRETAG+tag, msg);
    }

    public static int d(String tag, String msg) {
        return Log.d(PRETAG+tag, msg);
    }

    public static int e(String tag, String msg) {
        return Log.e(PRETAG+tag, msg);
    }

}
