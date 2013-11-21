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

import java.lang.reflect.Method;

import android.webkit.WebView;

public class WebviewUtil {

	/**
	 * Call WebView onResume in API version safe manner
	 * @param wv
	 */
	public static void onResume(WebView wv)
	{
		if (wv == null) {
			return;
		}
		try {
            Method onResume = WebView.class.getDeclaredMethod("onResume");
            onResume.invoke(wv);
        } catch (Exception e) {
        	// Expect this exception in API < 11
        }
	}
	
	/**
	 * Call WebView onPause in API version safe manner
	 * @param wv
	 */
	public static void onPause(WebView wv)
	{
		if (wv == null) {
			return;
		}
		try {
            Method onPause = WebView.class.getDeclaredMethod("onPause");
            onPause.invoke(wv);
        } catch (Exception e) {
        	// Expect this exception in API < 11
        }
	}
}
