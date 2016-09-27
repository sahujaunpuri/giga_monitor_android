package br.inatel.icc.gigasecurity.gigamonitor.util;

import java.util.regex.Pattern;

public class MyUtils
{
  private static long lastClickTime;
  

//  public static String getFileType(Context paramContext, String paramString)
//  {
//    String str1 = paramContext.getString(com.mobile.myeye.config.Config.videotype_id[0]);
//    if (paramString.endsWith(".h264"))
//    {
//      int i = paramString.indexOf('[');
//      if (i > 0)
//      {
//        String str2 = paramString.substring(i + 1, i + 2);
//        if (str2.equals("A")) {
//          str1 = paramContext.getString(Config.videotype_id[1]);
//        }
//        if (str2.equals("M")) {
//          str1 = paramContext.getString(com.mobile.myeye.config.Config.videotype_id[2]);
//        }
//        if (str2.equals("R")) {
//          str1 = paramContext.getString(com.mobile.myeye.config.Config.videotype_id[3]);
//        }
//        if (str2.equals("H")) {
//          str1 = paramContext.getString(com.mobile.myeye.config.Config.videotype_id[4]);
//        }
//      }
//    }
//    return str1;
//  }
//
//  public static String getMacAddress()
//  {
//    String str1 = callCmd("busybox ifconfig", "HWaddr");
//    if (str1 == null) {
//      str1 = null;
//    }
//    while ((str1.length() <= 0) || (!str1.contains("HWaddr"))) {
//      return str1;
//    }
//    String str2 = str1.substring(6 + str1.indexOf("HWaddr"), -1 + str1.length());
//    Log.i("test", "Mac:" + str2 + " Mac.length: " + str2.length());
//    String[] arrayOfString;
//    if (str2.length() > 1)
//    {
//      String str3 = str2.replaceAll(" ", "");
//      str1 = "";
//      arrayOfString = str3.split(":");
//    }
//    for (int i = 0;; i++)
//    {
//      if (i >= arrayOfString.length)
//      {
//        Log.i("test", str1 + " result.length: " + str1.length());
//        return str1;
//      }
//      str1 = str1 + arrayOfString[i];
//    }
//  }

  
  public static boolean isEmail(String paramString)
  {
    return Pattern.compile("^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$").matcher(paramString).matches();
  }
  
  public static boolean isFastDoubleClick()
  {
    long l = System.currentTimeMillis();
    if (l - lastClickTime < 500L) {
      return true;
    }
    lastClickTime = l;
    return false;
  }
  
  public static boolean isSn(String paramString)
  {
    return (Pattern.compile("^[A-Za-z0-9]+$").matcher(paramString).matches()) && (paramString.length() <= 16);
  }
}



/* Location:           C:\Users\miller.faria\Desktop\xmeye-dex2jar.jar

 * Qualified Name:     com.mobile.myeye.utils.MyUtils

 * JD-Core Version:    0.7.0.1

 */