package com.jumman.pralgomathic.imageprocessingtask.Utilities;

import android.util.Log;

/**
 * Created by Pralgomathic on 19-Sep-18.
 */

public class Constants {
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 2020;
    private static boolean isDebugLogOn = true;

    public static void debugLog(String tag, String message) {
        if (isDebugLogOn) {
            Log.d(tag + " -----> ", message);
        }
    }

    public static void errorLog(String tag, String message) {
        if (isDebugLogOn) {
            Log.d(tag + " -----> ", message);
        }
    }

}
