package br.inatel.icc.gigasecurity.gigamonitor.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.lib.SDKCONST;
import com.lib.sdk.struct.H264_DVR_FILE_DATA;
import com.lib.sdk.struct.SDK_SYSTEM_TIME;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

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

    public static int stringToHexIP( final String ip ) {

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

    public static String stringIpToHexString(String ip){
        String[] parts = ip.split("\\.");
        String hexIp = "0x";
        int segment;
        for(int i = 3; i >= 0; i--){
            segment = Integer.parseInt(parts[i]);
            if(segment < 16)
                hexIp = hexIp.concat("0");
            hexIp = hexIp.concat(Integer.toHexString(segment).toUpperCase());
        }
        return hexIp;
    }

    public static String reverseIp(String ip){
        String[] segment = new String[4];
        int j = 0;
        for(int i = 0; i < 4; i++){
            segment[i] = "";
            while(ip.charAt(j) != '.'){
                segment[i] += ip.charAt(j);
            }
            j++;
        }

        return segment[3] + "." + segment[2] + "." + segment[1] + "." + segment[0];
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
            case SDKCONST.FileType.SDK_RECORD_ALL:
                return "Record All";
            case SDKCONST.FileType.SDK_RECORD_ALARM:
                return "Record Alarm";
            case SDKCONST.FileType.SDK_RECORD_DETECT:
                return "Record Detect";
            case SDKCONST.FileType.SDK_RECORD_REGULAR:
                return "Record Regular";
            case SDKCONST.FileType.SDK_RECORD_MANUAL:
                return "Record Manual";
            case SDKCONST.FileType.SDK_PIC_ALL:
                return "Pic All"; // MyConfig.FileType.SDK_PIC_ALL
            case SDKCONST.FileType.SDK_PIC_ALARM:
                return "Pic Alarm"; // MyConfig.FileType.SDK_PIC_ALARM
            case SDKCONST.FileType.SDK_PIC_DETECT:
                return "Pic Detect"; // MyConfig.FileType.SDK_PIC_DETECT
            case SDKCONST.FileType.SDK_PIC_REGULAR:
                return "Pic Regular"; // MyConfig.FileType.SDK_PIC_REGULAR
            case SDKCONST.FileType.SDK_PIC_MANUAL:
                return "Pic Manual"; // MyConfig.FileType.SDK_PIC_MANUAL
            case SDKCONST.FileType.SDK_TYPE_NUM:
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

    public static String currentDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH_mm_ss", Locale.ENGLISH);
        return sdf.format(new Date());
    }

    public static Date parseStringToDate(String str){
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.ENGLISH);
        try {
            return sdf.parse(str);
            /*Calendar calendar = GregorianCalendar.getInstance();
            calendar.setTime(date);
            Log.d("utils", "parseStringToTime: " + date.getTime());*/
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static int parseStringToBits(String str){
        String[] bits = str.split("=");
        Log.d("parse", "parseStringToBits: " + Arrays.toString(bits));
        return Integer.parseInt(bits[1]);
    }

    public static boolean savePictureFile(String path){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inDither = true;
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        File file = new File(path);
        File imgPath = new File(path + File.separator + file.getName());
        if (imgPath.exists()) {
            Log.d("BITMAP", "saveFile: ERROR File exists");
        } else {
            try {
                int bytesum = 0;
                int byteread = 0;
                File oldfile = new File(path);
                if (oldfile.exists()) {
                    InputStream inStream = new FileInputStream(path);
                    FileOutputStream fs = new FileOutputStream(path + File.separator + file.getName());
                    byte[] buffer = new byte[1444];
                    while ((byteread = inStream.read(buffer)) != -1) {
                        bytesum += byteread;
                        System.out.println(bytesum);
                        fs.write(buffer, 0, byteread);
                    }
                    fs.close();
                    inStream.close();
                }
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    public static int getIntFileType(String fileName) {
        int fileType = 0;
        if (fileName.endsWith(".h264")) {
            int pos = fileName.indexOf('[');
            if (pos > 0 && pos < fileName.length()) {
                String type = fileName.substring(pos + 1, pos + 2);
                if (type.equals("A"))
                    fileType = SDKCONST.VidoFileType.VI_DETECT;
                else if (type.equals("M"))
                    fileType = SDKCONST.VidoFileType.VI_MANUAL;
                else if (type.equals("R"))
                    fileType = SDKCONST.VidoFileType.VI_MANUAL;
                else if (type.equals("H"))
                    fileType = SDKCONST.VidoFileType.VI_REGULAR;
                else if (type.equals("K"))
                    fileType = SDKCONST.VidoFileType.VI_KEY;
            }
        } else if (fileName.endsWith(".jpg")) {
            int pos = fileName.indexOf('[');
            if (pos > 0 && pos < fileName.length()) {
                String type = fileName.substring(pos + 1, pos + 2);
                if (type.equals("A"))
                    fileType = SDKCONST.PicFileType.PIC_DETECT;
                else if (type.equals("M"))
                    fileType = SDKCONST.PicFileType.PIC_MANUAL;
                else if (type.equals("R"))
                    fileType = SDKCONST.PicFileType.PIC_MANUAL;
                else if (type.equals("H"))
                    fileType = SDKCONST.PicFileType.PIC_REGULAR;
                else if (type.equals("K"))
                    fileType = SDKCONST.PicFileType.PIC_KEY;
                else if (type.equals("B"))
                    fileType = SDKCONST.PicFileType.PIC_BURST_SHOOT;
                else if (type.equals("L"))
                    fileType = SDKCONST.PicFileType.PIC_TIME_LAPSE;
            }
        }

        return fileType;
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

    public static String getGigaPassword(String input){
        String hash = md5(input);
        char[] output = new char[8];
        for (int i=0; i<=7; i++) {
            output[i] = (char) ((hash.charAt((2 * i)) + hash.charAt((2 * i + 1))) % 62);
            if ((output[i] >= 0) && (output[i] <= 9)) {
                output[i] += 48;
            } else {
                if ((output[i] >= 10) && (output[i] <= 35)) {
                    output[i] += 55;
                } else {
                    output[i] += 61;
                }
            }
        }
        Log.d("CRIPTO", "getGigaPassword: " + output.toString());
        return output.toString();
    }

    public static final String md5(final String s) {
        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance(MD5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString().substring(0, 16);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String intToIp(int addr) {
        return  ((addr & 0xFF) + "." +
                ((addr >>>= 8) & 0xFF) + "." +
                ((addr >>>= 8) & 0xFF) + "." +
                ((addr >>>= 8) & 0xFF));
    }

}
