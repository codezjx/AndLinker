package com.codezjx.alinker;

import android.util.Log;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Created by codezjx on 2018/1/23.<br/>
 */
public final class Logger {
    
    static boolean sEnable = true;

    public static void v(String tag, String msg) {
        if (sEnable) {
            Log.v(tag, msg);
        }
    }
    
    public static void d(String tag, String msg) {
        if (sEnable) {
            Log.d(tag, msg);
        }
    }

    public static void i(String tag, String msg) {
        if (sEnable) {
            Log.i(tag, msg);
        }
    }

    public static void w(String tag, String msg) {
        if (sEnable) {
            Log.w(tag, msg);
        }
    }

    public static void w(String tag, String msg, Throwable t) {
        w(tag, msg + '\n' + getStackTraceString(t));
    }

    public static void e(String tag, String msg) {
        if (sEnable) {
            Log.e(tag, msg);
        }
    }

    public static void e(String tag, String msg, Throwable t) {
        e(tag, msg + '\n' + getStackTraceString(t));
    }
    
    private static String getStackTraceString(Throwable t) {
        if (t == null) {
            return "";
        }
        // Don't replace this with Log.getStackTraceString() - it hides
        // UnknownHostException, which is not what we want.
        StringWriter sw = new StringWriter(256);
        PrintWriter pw = new PrintWriter(sw, false);
        t.printStackTrace(pw);
        pw.flush();
        return sw.toString();
    }
}
