package br.inatel.icc.gigasecurity.gigamonitor.util;

import java.io.StringWriter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Process;
import android.util.Log;

import java.io.PrintWriter;
import java.io.StringWriter;


/**
 * Created by rocali on 1/19/17.
 */

public class MyExceptionHandler implements
        java.lang.Thread.UncaughtExceptionHandler {
    private final Context myContext;
    private final Class<?> myActivityClass;

    public MyExceptionHandler(Context context, Class<?> c) {
        myContext = context;
        myActivityClass = c;
    }

    public void uncaughtException(Thread thread, Throwable exception) {

        StringWriter stackTrace = new StringWriter();
        exception.printStackTrace(new PrintWriter(stackTrace));
        Log.v("Rocali","MyExceptionHandler?? Oo");
        System.err.println(stackTrace);// You can use LogCat too*/
        String exceptionStr = stackTrace.toString();
        Log.d("ROCALI", "uncaughtException: " + exceptionStr);


//        SharedPreferences settings = myContext.getSharedPreferences(MyApplication.getInstance().PREFS_NAME, 0);
//        SharedPreferences.Editor editor = settings.edit();
//        editor.putBoolean("crashedLastTime", true);
//        editor.commit();

        //SharedPreferences settings = myContext.getSharedPreferences(MyApplication.getInstance().PREFS_NAME, 0);
        //boolean crashedLastTime = settings.getBoolean("crashedLastTime", false);
        //Log.v("Rocali","crashed "+crashedLastTime);

//        Intent intent = new Intent(myContext, SecondSplash.class);
//        myContext.startActivity(intent);
        //for restarting the Activity
        Process.killProcess(Process.myPid());
        System.exit(0);
    }
}