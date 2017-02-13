package br.inatel.icc.gigasecurity.gigamonitor.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

/**
 * File: BitmapUtils.java
 * Creation date: 04/09/2014
 * Author: denisvilela
 *
 * Purpose: Declaration of class DeviceManagerTest.java
 *
 * Copyright 2014, INATEL Competence Center

 * All rights are reserved. Reproduction in whole or part is
 * prohibited without the written consent of the copyright owner.
 */
public class BitmapUtil {
    private static final String LOG_TAG = BitmapUtil.class.getSimpleName();

    public static boolean saveIntoExternalStorage(File imageFile) {
        try {
            final Bitmap bm = BitmapFactory.decodeFile(imageFile.getPath());
            FileOutputStream fos = new FileOutputStream(imageFile);
            bm.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error saving bitmap into external storage", e);
            return false;
        }

        return true;
    }



    public static boolean saveIntoExternalStorage(Context context, Bitmap bm) {
        final File directory = getAlbumStorageDir(context);
        File imageFile = new File(directory, getImageName());
        try {
            FileOutputStream fos = new FileOutputStream(imageFile);
            bm.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error saving bitmap into external storage", e);
            return false;
        }

        return true;
    }

    public static File getAlbumStorageDir(Context context) {
        final String albumName = Utils.getApplicationName(context);
        final File directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                albumName);
        if (!directory.mkdirs()) {
            Log.e(LOG_TAG, "Directory not created");
        }
        return directory;
    }

    public static String getImageName() {
        return "Capture_" + Calendar.getInstance().getTimeInMillis() + ".jpg";
    }
}
