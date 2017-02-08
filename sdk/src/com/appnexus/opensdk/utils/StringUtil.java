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

import android.content.res.Resources;

import java.io.InputStream;
import java.util.Scanner;

public class StringUtil {

	/**
	 * Implement an isEmpty for API < 9 
	 * @param s The string to validate
	 * @return True if and only if the string isn't null and has a non-zero length
	 */
	public static boolean isEmpty(String s) {
		return s == null || s.length() == 0;
	}

    // returns true if success, false if exception
    public static boolean appendRes(StringBuilder sb, Resources res, int resId) {
        InputStream inputStream = res.openRawResource(resId);
        Scanner scanner = new Scanner(inputStream, "UTF-8").useDelimiter("\\A");
        if(scanner.hasNext()){
            sb.append(scanner.next());
            scanner.close();
            return true;
        }else{
            scanner.close();
            return false;
        }
    }

    public static int getIntegerValue(String s){
        if(s == null) return 0;

        int value = 0;
        try {
            value = Integer.parseInt(s);
        }catch (NumberFormatException e){
            Clog.e(Clog.baseLogTag, "Exception while parsing integer value from string: "+s + " - "+e.getMessage());
        }
        return value;
    }
}
