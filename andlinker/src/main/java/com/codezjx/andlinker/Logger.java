package com.codezjx.andlinker;

import android.os.Build;
import android.util.Log;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by codezjx on 2018/1/23.<br/>
 */
final class Logger {

    private Logger() {
        // private constructor
    }

    private static final String DEFAULT_LOG_TAG = "Logger";
    private static final int MAX_TAG_LENGTH = 23;
    // 3 method calls inside Logger
    private static final int CALL_STACK_INDEX = 3;
    private static final Pattern ANONYMOUS_CLASS = Pattern.compile("(\\$\\d+)+$");
    static boolean sEnable = true;

    static void v(String msg) {
        log(Log.VERBOSE, null, msg, null);
    }

    static void v(String tag, String msg) {
        log(Log.VERBOSE, tag, msg, null);
    }

    static void d(String msg) {
        log(Log.DEBUG, null, msg, null);
    }
    
    static void d(String tag, String msg) {
        log(Log.DEBUG, tag, msg, null);
    }

    static void i(String msg) {
        log(Log.INFO, null, msg, null);
    }

    static void i(String tag, String msg) {
        log(Log.INFO, tag, msg, null);
    }

    static void w(String msg) {
        log(Log.WARN, null, msg, null);
    }

    static void w(String tag, String msg) {
        log(Log.WARN, tag, msg, null);
    }

    static void w(String msg, Throwable t) {
        log(Log.WARN, null, msg, t);
    }

    static void w(String tag, String msg, Throwable t) {
        log(Log.WARN, tag, msg, t);
    }

    static void e(String msg) {
        log(Log.ERROR, null, msg, null);
    }

    static void e(String tag, String msg) {
        log(Log.ERROR, tag, msg, null);
    }

    static void e(String msg, Throwable t) {
        log(Log.ERROR, null, msg, t);
    }

    static void e(String tag, String msg, Throwable t) {
        log(Log.ERROR, tag, msg, t);
    }

    private static void log(int priority, String tag,  String msg, Throwable t) {
        if (!sEnable) {
            return;
        }
        String curTag = (tag != null && tag.trim().length() > 0) ? tag : getTag();
        String curMsg = (t != null) ? (msg + '\n' + getStackTraceString(t)) : msg;
        Log.println(priority, curTag, curMsg);
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

    private static String getTag() {
        StackTraceElement[] stackTrace = new Throwable().getStackTrace();
        if (stackTrace.length <= CALL_STACK_INDEX) {
            Log.e(DEFAULT_LOG_TAG, "Synthetic stacktrace didn't have enough elements, use the default logger tag, are you using proguard?");
            return DEFAULT_LOG_TAG;
        }
        return createStackElementTag(stackTrace[CALL_STACK_INDEX]);
    }

    private static String createStackElementTag(StackTraceElement element) {
        String tag = element.getClassName();
        Matcher m = ANONYMOUS_CLASS.matcher(tag);
        if (m.find()) {
            tag = m.replaceAll("");
        }
        tag = tag.substring(tag.lastIndexOf('.') + 1);
        // Tag length limit was removed in API 24.
        if (tag.length() <= MAX_TAG_LENGTH || Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return tag;
        }
        return tag.substring(0, MAX_TAG_LENGTH);
    }
}
