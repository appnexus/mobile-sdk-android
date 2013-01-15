package com.appnexus.opensdk;

import android.util.Log;

public class Clog{
	public static boolean clogged=false;
	static void v(String LogTag, String message){
		if(!clogged) Log.v(LogTag, message);
	}
	static void v(String LogTag, String message, Throwable tr){
		if(!clogged) Log.v(LogTag, message, tr);
	}
	static void d(String LogTag, String message){
		if(!clogged) Log.d(LogTag, message);
	}
	static void d(String LogTag, String message, Throwable tr){
		if(!clogged) Log.d(LogTag, message, tr);
	}
	static void i(String LogTag, String message){
		if(!clogged) Log.i(LogTag, message);
	}
	static void i(String LogTag, String message, Throwable tr){
		if(!clogged) Log.i(LogTag, message, tr);
	}
	static void w(String LogTag, String message){
		if(!clogged) Log.w(LogTag, message);
	}
	static void w(String LogTag, String message, Throwable tr){
		if(!clogged) Log.w(LogTag, message, tr);
	}
	static void e(String LogTag, String message){
		if(!clogged) Log.e(LogTag, message);
	}
	static void e(String LogTag, String message, Throwable tr){
		if(!clogged) Log.e(LogTag, message, tr);
	}
}
