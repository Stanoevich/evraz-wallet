package com.ngse.utility;

import android.content.Context;
import android.content.ContextWrapper;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Kulykov Anton on 26.07.2018
 */

public class DebugUtility {
    private static final boolean DEBUG = true;
    private static final boolean DEBUG_FILE = false;
    private static final String PRE_TAG = "TEST";
    private static final String API_TAG = "API";
    private static final String LOG_FOLDER = "logs";
    public static SimpleDateFormat dateTimeFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    private static Object syncObject;


    public static void logTest(String tag, String msg) {
        tag = PRE_TAG + " " + tag;
        Log.e(tag, msg);
    }

    public static void logApi(String tag, String msg) {
        tag = API_TAG + " " + tag;
        if (msg == null)
            msg = "";
        Log.e(tag, msg);
    }

    public static void logE(Context context, String tag, String msg) {
        tag = PRE_TAG + " " + tag;
        if (DEBUG_FILE) {
            logToFile(context, tag, msg);
        }
        if (!DEBUG) {
            return;
        }
        Log.e(tag, msg);
    }

    private static void logToFile(Context context, String tag, String msg) {
        if (context == null) {
            return;
        }
        synchronized (getSyncObject()) {
            String path = getTempPath(context, LOG_FOLDER);
            if (!isSDCardEnabled(context, LOG_FOLDER)) {
                return;
            }
            File logFile = new File(path + File.separator + "logNEW.txt");
            if (!logFile.exists()) {
                try {
                    logFile.createNewFile();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            try {
                // BufferedWriter for performance, true to set append to file flag
                BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
                buf.append(dateTimeFormatter.format(Calendar.getInstance().getTime()) + "  " + tag + "  " + msg);
                buf.newLine();
                buf.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static Object getSyncObject() {
        if (syncObject == null)
            syncObject = new Object();
        return syncObject;
    }

    public static String getTempPath(Context context, String folder) {
        String path = getRootFolder(context) +
                folder;

        return path;
    }

    public static String getRootFolder(Context context) {
        String packageNameString = context != null ? context.getApplicationContext().getPackageName() : "com.NGSE.Temp";
        return getRootFolderWithoutPackage(context) + packageNameString + File.separator;
    }

    public static String getRootFolderWithoutPackage(Context context) {
        String state = Environment.getExternalStorageState();
        boolean isTempStorageAccessibile = Environment.MEDIA_MOUNTED.equals(state);

        if (isTempStorageAccessibile || context == null) {
            return Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/";
        } else {
            ContextWrapper cw = new ContextWrapper(context);
            File directory = cw.getDir("media", Context.MODE_PRIVATE);
            return directory.getAbsolutePath();
        }
    }

    public static boolean isSDCardEnabled(Context context, String folder) {
        File tempFile = new File(getTempPath(context, folder));
        tempFile.mkdirs();
        return tempFile.canWrite();
    }
}