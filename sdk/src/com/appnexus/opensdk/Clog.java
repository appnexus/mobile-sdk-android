package com.appnexus.opensdk;

import android.content.Context;
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
	
	
	protected static String baseLogTag = "OPENSDK";
	protected static String publicFunctionsLogTag = baseLogTag+"-INTERFACE";
	protected static String httpReqLogTag = baseLogTag+"-REQUEST";
	protected static String httpRespLogTag = baseLogTag+"-RESPONSE";
	protected static String xmlLogTag = baseLogTag+"-XML";
	protected static String jsLogTag = baseLogTag+"-JS";
	protected static Context error_context;
	protected static String getString(int id){
		return error_context.getString(id);
	}
	protected static String getString(int id, long l){
		if(clogged) return null;
		return String.format(error_context.getString(id), l);
	}
	protected static String getString(int id, String s){
		if(clogged) return null;
		return String.format(error_context.getString(id), s);
	}
	protected static String getString(int id, String s, int i){
		if(clogged) return null;
		return String.format(error_context.getString(id), s, i);
	}
	protected static String getString(int id, int a, int b, int c, int d){
		if(clogged) return null;
		return String.format(error_context.getString(id), a, b, c, d);
	}
	protected static String getString(int id, boolean b){
		if(clogged) return null;
		return String.format(error_context.getString(id), b);
	}
	protected static String getString(int id, String s, String ss){
		if(clogged) return null;
		return String.format(error_context.getString(id), s, ss);
	}
}
