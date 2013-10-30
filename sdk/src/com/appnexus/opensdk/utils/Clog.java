/*
 *    Copyright 2013 APPNEXUS INC
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.appnexus.opensdk.utils;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;

import static com.appnexus.opensdk.utils.ClogListener.LOG_LEVEL;

public class Clog {
    public static boolean clogged = false;

    public static void v(String LogTag, String message) {
        if (!clogged && message != null) {
            notifyListener(LOG_LEVEL.V, LogTag, message);
            Log.v(LogTag, message);
        }
    }

    public static void v(String LogTag, String message, Throwable tr) {
        if (!clogged && message != null) {
            notifyListener(LOG_LEVEL.V, LogTag, message, tr);
            Log.v(LogTag, message, tr);
        }
    }

    public static void d(String LogTag, String message) {
        if (!clogged && message != null) {
            notifyListener(LOG_LEVEL.D, LogTag, message);
            Log.d(LogTag, message);
        }
    }

    public static void d(String LogTag, String message, Throwable tr) {
        if (!clogged && message != null) {
            notifyListener(LOG_LEVEL.D, LogTag, message, tr);
            Log.d(LogTag, message, tr);
        }
    }

    public static void i(String LogTag, String message) {
        if (!clogged && message != null) {
            notifyListener(LOG_LEVEL.I, LogTag, message);
            Log.i(LogTag, message);
        }
    }

    public static void i(String LogTag, String message, Throwable tr) {
        if (!clogged && message != null) {
            notifyListener(LOG_LEVEL.I, LogTag, message, tr);
            Log.i(LogTag, message, tr);
        }
    }

    public static void w(String LogTag, String message) {
        if (!clogged && message != null) {
            notifyListener(LOG_LEVEL.W, LogTag, message);
            Log.w(LogTag, message);
        }
    }

    public static void w(String LogTag, String message, Throwable tr) {
        if (!clogged && message != null) {
            notifyListener(LOG_LEVEL.W, LogTag, message, tr);
            Log.w(LogTag, message, tr);
        }
    }

    public static void e(String LogTag, String message) {
        if (!clogged && message != null) {
            notifyListener(LOG_LEVEL.E, LogTag, message);
            Log.e(LogTag, message);
        }
    }

    public static void e(String LogTag, String message, Throwable tr) {
        if (!clogged && message != null) {
            notifyListener(LOG_LEVEL.E, LogTag, message, tr);
            Log.e(LogTag, message, tr);
        }
    }

    /**
     * Logging helper functions for SDK
     */

    public static final String baseLogTag = "OPENSDK";
    public static final String mediationLogTag = baseLogTag + "-MEDIATION";
    public static final String publicFunctionsLogTag = baseLogTag + "-INTERFACE";
    public static final String httpReqLogTag = baseLogTag + "-REQUEST";
    public static final String httpRespLogTag = baseLogTag + "-RESPONSE";
    public static final String xmlLogTag = baseLogTag + "-XML";
    public static final String jsLogTag = baseLogTag + "-JS";
    public static final String mraidLogTag = baseLogTag + "-MRAID";
    public static final String browserLogTag = baseLogTag + "-APPBROWSER";
    public static Context error_context;

    public static String getString(int id) {
        if (error_context == null)
            return null;
        return error_context.getString(id);
    }

    public static String getString(int id, long l) {
        if (clogged || error_context == null)
            return null;
        return String.format(error_context.getString(id), l);
    }

    public static String getString(int id, String s) {
        if (clogged || error_context == null)
            return null;
        return String.format(error_context.getString(id), s);
    }

    public static String getString(int id, String s, int i) {
        if (clogged || error_context == null)
            return null;
        return String.format(error_context.getString(id), s, i);
    }

    public static String getString(int id, int a, int b, int c, int d) {
        if (clogged || error_context == null)
            return null;
        return String.format(error_context.getString(id), a, b, c, d);
    }

    public static String getString(int id, boolean b) {
        if (clogged || error_context == null)
            return null;
        return String.format(error_context.getString(id), b);
    }

    public static String getString(int id, String s, String ss) {
        if (clogged || error_context == null)
            return null;
        return String.format(error_context.getString(id), s, ss);
    }

    public static String getString(int id, int i, String s, String ss) {
        if (clogged || error_context == null)
            return null;
        return String.format(error_context.getString(id), i, s, ss);
    }

    public static String getString(int id, String s, int i, String ss) {
        if (clogged || error_context == null)
            return null;
        return String.format(error_context.getString(id), s, i, ss);
    }

    public static String getString(int id, int i, String s) {
        if (clogged || error_context == null)
            return null;
        return String.format(error_context.getString(id), i, s);
    }

    public static String getString(int id, int w, int h, int offset_x, int offset_y, String custom_close_position, boolean allow_offscrean) {
        if (clogged || error_context == null)
            return null;
        return String.format(error_context.getString(id), w, h, offset_x, offset_y, custom_close_position, allow_offscrean);
    }

    public static String getString(int id, boolean b, int i){
        if (clogged || error_context == null)
            return null;
        return String.format(error_context.getString(id), b, i);
    }

    /**
     * lastRequest/lastResponse helper methods
     */


    private static String lastRequest = "";
    private static String lastResponse = "";

    synchronized public static void setLastRequest(String lastRequest) {
        Clog.lastRequest = lastRequest;
    }

    synchronized public static String getLastRequest() {
        return lastRequest;
    }

    synchronized public static void clearLastResponse() {
        Clog.lastResponse = "";
    }

    synchronized public static void setLastResponse(String lastResponse) {
        Clog.lastResponse = lastResponse;
    }

    synchronized public static String getLastResponse() {
        return lastResponse;
    }

    /**
     * ClogListener helper methods
     */

    private static final ArrayList<ClogListener> listeners = new ArrayList<ClogListener>();

    synchronized public static boolean registerListener(ClogListener listener) {
        return listener != null && listeners.add(listener);
    }

    synchronized public static boolean unregisterListener(ClogListener listener) {
        return listener != null && listeners.remove(listener);
    }

    synchronized public static void unregisterAllListeners() {
        listeners.clear();
    }

    private synchronized static void notifyListener(LOG_LEVEL level, String LogTag, String message) {
        notifyListener(level, LogTag, message, null);
    }

    private synchronized static void notifyListener(LOG_LEVEL level, String LogTag, String message, Throwable tr) {
        for (ClogListener listener: listeners) {
            if (level.ordinal() >= listener.getLogLevel().ordinal()) {
                if (tr != null)
                    listener.onReceiveMessage(level, LogTag, message, tr);
                else
                    listener.onReceiveMessage(level, LogTag, message);
            }
        }
    }
}
