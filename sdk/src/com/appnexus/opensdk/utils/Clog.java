package com.appnexus.opensdk.utils;

import android.content.Context;
import android.util.Log;

public class Clog{
	public static boolean clogged=false;
	public static void v(String LogTag, String message){
		if(!clogged) Log.v(LogTag, message);
	}
	public static void v(String LogTag, String message, Throwable tr){
		if(!clogged) Log.v(LogTag, message, tr);
	}
	public static void d(String LogTag, String message){
		if(!clogged) Log.d(LogTag, message);
	}
	public static void d(String LogTag, String message, Throwable tr){
		if(!clogged) Log.d(LogTag, message, tr);
	}
	public static void i(String LogTag, String message){
		if(!clogged) Log.i(LogTag, message);
	}
	public static void i(String LogTag, String message, Throwable tr){
		if(!clogged) Log.i(LogTag, message, tr);
	}
	public static void w(String LogTag, String message){
		if(!clogged) Log.w(LogTag, message);
	}
	public static void w(String LogTag, String message, Throwable tr){
		if(!clogged) Log.w(LogTag, message, tr);
	}
	public static void e(String LogTag, String message){
		if(!clogged) Log.e(LogTag, message);
	}
	public static void e(String LogTag, String message, Throwable tr){
		if(!clogged) Log.e(LogTag, message, tr);
	}
	
	
	public static String baseLogTag = "OPENSDK";
	public static String publicFunctionsLogTag = baseLogTag+"-INTERFACE";
	public static String httpReqLogTag = baseLogTag+"-REQUEST";
	public static String httpRespLogTag = baseLogTag+"-RESPONSE";
	public static String xmlLogTag = baseLogTag+"-XML";
	public static String jsLogTag = baseLogTag+"-JS";
	public static String mraidLogTab = baseLogTag+"-MRAID";
	public static Context error_context;
	public static String getString(int id){
		return error_context.getString(id);
	}
	public static String getString(int id, long l){
		if(clogged) return null;
		return String.format(error_context.getString(id), l);
	}
	public static String getString(int id, String s){
		if(clogged) return null;
		return String.format(error_context.getString(id), s);
	}
	public static String getString(int id, String s, int i){
		if(clogged) return null;
		return String.format(error_context.getString(id), s, i);
	}
	public static String getString(int id, int a, int b, int c, int d){
		if(clogged) return null;
		return String.format(error_context.getString(id), a, b, c, d);
	}
	public static String getString(int id, boolean b){
		if(clogged) return null;
		return String.format(error_context.getString(id), b);
	}
	public static String getString(int id, String s, String ss){
		if(clogged) return null;
		return String.format(error_context.getString(id), s, ss);
	}
	public static String getString(int id, int i, String s, String ss){
		if(clogged) return null;
		return String.format(error_context.getString(id),i, s, ss);
	}
	public static String getString(int id, String s, int i, String ss){
		if(clogged) return null;
		return String.format(error_context.getString(id),s,i,ss);
	}
	public static String getString(int id, int i,
			String s) {
		if(clogged) return null;
		return String.format(error_context.getString(id),i,s);
	}
	
	private static String lastRequest="";
	private static String lastResponse="";
	
	public static void setLastRequest(String lastRequest){
		Clog.lastRequest=lastRequest;
	}
	
	public static String getLastRequest(){
		return lastRequest;
	}
	
	public static void setLastResponse(String lastResponse){
		Clog.lastResponse=lastResponse;
	}
	
	public static String getLastResponse(){
		return lastResponse;
	}
}
