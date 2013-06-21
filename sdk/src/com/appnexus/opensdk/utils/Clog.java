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

public class Clog {
	public static boolean clogged = false;

	public static void v(String LogTag, String message) {
		if (!clogged && message != null)
			Log.v(LogTag, message);
	}

	public static void v(String LogTag, String message, Throwable tr) {
		if (!clogged && message != null)
			Log.v(LogTag, message, tr);
	}

	public static void d(String LogTag, String message) {
		if (!clogged && message != null)
			Log.d(LogTag, message);
	}

	public static void d(String LogTag, String message, Throwable tr) {
		if (!clogged && message != null)
			Log.d(LogTag, message, tr);
	}

	public static void i(String LogTag, String message) {
		if (!clogged && message != null)
			Log.i(LogTag, message);
	}

	public static void i(String LogTag, String message, Throwable tr) {
		if (!clogged && message != null)
			Log.i(LogTag, message, tr);
	}

	public static void w(String LogTag, String message) {
		if (!clogged && message != null)
			Log.w(LogTag, message);
	}

	public static void w(String LogTag, String message, Throwable tr) {
		if (!clogged && message != null)
			Log.w(LogTag, message, tr);
	}

	public static void e(String LogTag, String message) {
		if (!clogged && message != null)
			Log.e(LogTag, message);
	}

	public static void e(String LogTag, String message, Throwable tr) {
		if (!clogged && message != null)
			Log.e(LogTag, message, tr);
	}

	public static String baseLogTag = "OPENSDK";
	public static String publicFunctionsLogTag = baseLogTag + "-INTERFACE";
	public static String httpReqLogTag = baseLogTag + "-REQUEST";
	public static String httpRespLogTag = baseLogTag + "-RESPONSE";
	public static String xmlLogTag = baseLogTag + "-XML";
	public static String jsLogTag = baseLogTag + "-JS";
	public static String mraidLogTag = baseLogTag + "-MRAID";
	public static String browserLogTag = baseLogTag + "-APPBROWSER";
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
}
