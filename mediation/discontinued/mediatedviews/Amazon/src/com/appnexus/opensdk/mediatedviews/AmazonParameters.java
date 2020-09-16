/*
 *    Copyright 2014 APPNEXUS INC
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

package com.appnexus.opensdk.mediatedviews;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Class to extract optional server side parameters passed in json string.
 * Supports the following
 * {
 * "timeout" : 3000,
 * "floor_price" : 850000
 * }
 * floor_price is in units of micro dollars as per Amazon docs
 * "You can specify a floor price in micro-dollars. For example, if you wanted to
 *  earn a minimum of $0.85 per thousand ads returned, then you would specify '850000' as the value in micro-dollars"
 */
class AmazonParameters {

    public AmazonParameters(String parameter) {
        final String TIMEOUT = "timeout";
        final String FLOOR_PRICE = "floor_price";

        do {
            JSONObject req = null;
            if (parameter == null || parameter.length() == 0) {
                break;
            }
            try {
                req = new JSONObject(parameter);
            } catch (JSONException e) {
                // This is optional
            } finally {
                if (req == null) {
                    break;
                }
            }

            try {
                timeout = req.getInt(TIMEOUT);
                hasTimeout = true;

            } catch (JSONException e) {
            }
            try {
                floor = req.getInt(FLOOR_PRICE);
                hasFloor = true;
            } catch (JSONException e) {
            }

        } while (false);
    }


    public boolean hasTimeout;
    public int  timeout;

    public boolean hasFloor;
    public int floor;

}