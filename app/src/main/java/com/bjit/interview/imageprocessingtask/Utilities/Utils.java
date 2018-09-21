package com.bjit.interview.imageprocessingtask.Utilities;

import android.os.Environment;

import java.io.File;

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
}
