package br.inatel.icc.gigasecurity.gigamonitor.util;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.xm.javaclass.SDK_SYSTEM_TIME;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Denis Vilela on 04/09/2014.
 */
public class Utils {

    public static String getApplicationName(Context context) {
        final int stringId = context.getApplicationInfo().labelRes;
        return context.getString(stringId);
    }

    public static boolean isValidIP(CharSequence ipAddress) {
        if (ipAddress == null) {
            return false;
        }
        final String ip = ipAddress.toString().trim();
        return Patterns.IP_ADDRESS.matcher(ip).matches();
    }

    public static boolean isValidDomain(CharSequence domain) {
        final String d = domain.toString().trim();
        return Patterns.DOMAIN_NAME.matcher(d).matches();
    }

    public static boolean isValidMac(CharSequence mac) {
        final String m = mac.toString().trim();
        return String.valueOf(m).matches("([\\da-fA-F]{2}(?:\\:|$)){6}");
    }

    private static Integer hexStringToInt( final String literal, final int base, final Integer defaultValue ) {

        String fmtLiteral = literal;
        if ( literal.startsWith( "0x" ) || literal.startsWith( "0X" ) ) {
            fmtLiteral = literal.substring( 2 );
        }

        Integer number = defaultValue;
        try {
            final Long longNum = Long.parseLong( fmtLiteral, base );
            number = (int) ( longNum & 0xFFFFFFFF );
        }
        catch ( final NumberFormatException ex ) {
            ex.printStackTrace();
        }

        return number;
    }

    private static int stringToHexIP( final String ip ) {

        int retIP = 0x00000000;
        final String regex = "((2((5[0-5])|[0-4][0-9])|(1([0-9]{2}))|(0|([1-9][0-9]))|([0-9]))\\.){3}(2((5[0-5])|[0-4][0-9])|(1([0-9]{2}))|(0|([1-9][0-9]))|([0-9]))";
        if ( ip != null && ip.matches( regex ) ) {
            final String octets[] = ip.split( "\\." );
            if ( ( octets.length == 4 ) ) {
                for ( int i = 0; i < 4; i++ ) {
                    final int octect = Integer.valueOf( octets[i] );
                    retIP = retIP | ( octect << ( i * 8 ) );
                }
            }
        }

        return ( retIP & 0xFFFFFFFF );
    }

    private static String hexIPToString( final int hexIP ) {

        String retIP = "O0.O1.O2.O3";
        for ( int i = 0; i < 4; i++ ) {
            final String octect = String.format( "%d", ( ( hexIP & ( 0xFF << ( i * 8 ) ) ) >> ( i * 8 ) ) & 0xFF );
            retIP = retIP.replaceFirst( "O" + i, octect );
        }

        return retIP;
    }


    public static String hexStringToIP( final String hexIP ) {

        return Utils.hexIPToString( Utils.hexStringToInt( hexIP, 16, 0x00000000 ) );
    }

    public static String ipToHexString( final String ip ) {

        return String.format( "0x%08X", Utils.stringToHexIP( ip ) );
    }

    public static String getFileTypeName(int type) {
        switch (type) {
            case 0:
                return "Record All"; // MyConfig.FileType.SDK_RECORD_ALL
            case 1:
                return "Record Alarm"; // MyConfig.FileType.SDK_RECORD_ALARM
            case 2:
                return "Record Detect"; // MyConfig.FileType.SDK_RECORD_DETECT
            case 3:
                return "Record Regular"; // MyConfig.FileType.SDK_RECORD_REGULAR
            case 4:
                return "Record Manual"; // MyConfig.FileType.SDK_RECORD_MANUAL
            case 10:
                return "Pic All"; // MyConfig.FileType.SDK_PIC_ALL
            case 11:
                return "Pic Alarm"; // MyConfig.FileType.SDK_PIC_ALARM
            case 12:
                return "Pic Detect"; // MyConfig.FileType.SDK_PIC_DETECT
            case 13:
                return "Pic Regular"; // MyConfig.FileType.SDK_PIC_REGULAR
            case 14:
                return "Pic Manual"; // MyConfig.FileType.SDK_PIC_MANUAL
            case 15:
                return "Type Num"; // MyConfig.FileType.SDK_TYPE_NUM
        }

        return "";
    }

    public static void hideKeyboard(Activity activity) {
        // Check if no view has focus:
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public static Date parseSDKTime(SDK_SYSTEM_TIME time) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, time.st_4_hour);
        c.set(Calendar.MINUTE, time.st_5_minute);
        c.set(Calendar.SECOND, time.st_6_second);
        c.set(Calendar.DAY_OF_MONTH, time.st_2_day);
        c.set(Calendar.MONTH, time.st_1_month);
        c.set(Calendar.YEAR, time.st_0_year);
        c.set(Calendar.DAY_OF_WEEK, time.st_3_wday);


        Log.d("teste",String.valueOf(c.get(Calendar.HOUR_OF_DAY)));
        Log.d("teste",String.valueOf(c.get(Calendar.MINUTE)));
        Log.d("teste",String.valueOf(c.get(Calendar.SECOND)));
        Log.d("teste",String.valueOf(c.get(Calendar.DAY_OF_MONTH)));
        Log.d("teste",String.valueOf(c.get(Calendar.MONTH)));
        Log.d("teste",String.valueOf(c.get(Calendar.YEAR)));
        Log.d("teste",String.valueOf(c.get(Calendar.DAY_OF_WEEK)));

        return c.getTime();
    }

    public static int[] parseIp(String host) {
        int[] frt = new int[4];
        InetAddress inetAddress;
        try {
            inetAddress = InetAddress.getByName(host);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return null;
        }

        byte[] ip = inetAddress.getAddress();
        frt[0] = ip[0];
        frt[1] = ip[1];
        frt[2] = ip[2];
        frt[3] = ip[3];
        return frt;
    }
}
