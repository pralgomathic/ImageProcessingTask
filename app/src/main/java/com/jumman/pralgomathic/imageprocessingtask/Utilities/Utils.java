package com.jumman.pralgomathic.imageprocessingtask.Utilities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pralgomathic on 21-Sep-18.
 */

public class Utils {

    public static String getExternalStoragePath(){
        File file =  Environment.getExternalStorageDirectory();
        String path = file.getAbsolutePath();
        return path;
    }
    public static String getOutputFileName(String outputType, String extension){
        long time = System.currentTimeMillis();
        String outputFilename = outputType +"_" + time + extension;
        return outputFilename;
    }
    public static String getFileExtension(String url){
        String extension = "";
        if(url.contains(".")) {
            extension = url.substring(url.lastIndexOf("."));

        }
        return extension;
    }
    public static boolean checkAndRequestPermissions(Context context, Activity activity) {
        int readpermission = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE);
        int writepermission = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        List<String> listPermissionsNeeded = new ArrayList<>();

        if (readpermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (writepermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(activity, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), Constants.REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }
}
