package com.libremobileos.freeform.server.util;

import android.annotation.SuppressLint;
import android.util.Log;

public class Debug {
    private static final String MAIN_TAG = "LMOFreeform";

    @SuppressLint("LogTagMismatch")
    public static void dlog(String tag, String msg) {
        if (Log.isLoggable(MAIN_TAG, Log.DEBUG)) {
            Log.d(tag, msg);
        }
    }
}
