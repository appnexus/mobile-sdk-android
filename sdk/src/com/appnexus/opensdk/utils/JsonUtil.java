package com.appnexus.opensdk.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class JsonUtil {
    // also returns null if array is empty
    public static JSONArray getJSONArray(JSONObject object, String key) {
        if (object == null) return null;
        try {
            JSONArray array =  object.getJSONArray(key);
            return array.length() > 0 ? array : null;
        } catch (JSONException ignored) {}
        return null;
    }

    public static ArrayList<String> getStringArrayList(JSONArray array) {
        if (array == null) return null;
        try {
            if (array.length() > 0) {
                int l = array.length();
                ArrayList<String> arrayList = new ArrayList<String>(l);
                for (int i = 0; i <  l; i ++) {
                    arrayList.add((String) array.get(i));
                }
                return arrayList;
            }
        } catch (JSONException ignored) {}
        catch (ClassCastException ignored) {}
        return null;
    }

    public static HashMap<String, Object> getStringObjectHashMap(JSONObject object) {
        if (object == null) return null;
        try {
            Iterator<String> keys = object.keys();
            HashMap<String, Object> map = new HashMap<String, Object>();
            while (keys.hasNext()) {
                String key = keys.next();
                map.put(key, object.getString(key));
            }
            return map;
        } catch (JSONException ignored) {}
        return null;
    }

    public static JSONObject getJSONObject(JSONObject object, String key) {
        if (object == null) return null;
        try{
            return object.getJSONObject(key);
        } catch (JSONException ignored){}
        return null;
    }

    public static JSONObject getJSONObjectFromArray(JSONArray array, int index) {
        if (array == null) return null;
        try {
            return array.getJSONObject(index);
        } catch (JSONException ignored) {}
        return null;
    }

    public static String getStringFromArray(JSONArray array, int index) {
        if (array == null) return "";
        try {
            return array.getString(index);
        } catch (JSONException ignored) {}
        return "";
    }

    public static String getJSONString(JSONObject object, String key) {
        if (object == null) return "";
        try {
            return object.getString(key);
        } catch (JSONException ignored) {}
        return "";
    }

    public static int getJSONInt(JSONObject object, String key) {
        if (object == null) return -1;
        try {
            return object.getInt(key);
        } catch (JSONException ignored) {}
        return -1;
    }

    public static double getJSONDouble(JSONObject object, String key) {
        if (object == null) return -1d;
        try {
            return object.getDouble(key);
        } catch (JSONException ignored) {
        }
        return -1d;
    }


    public static boolean getJSONBoolean(JSONObject object, String key) {
        if (object == null) return false;
        try {
            return object.getBoolean(key);
        } catch (JSONException ignored) {}
        return false;
    }
}
