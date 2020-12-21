/*
 *    Copyright 2020 APPNEXUS INC
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

public class GooglePlayAdsSettings {

    private static long secondPriceWaitInterval = 120;
    private static int totalRetries = 10;

    /**
     * @return the delay interval for Second Price timer (in ms), 120 ms by default.
     * */
    protected static long getSecondPriceWaitInterval() {
        return secondPriceWaitInterval;
    }

    /**
     * Set the delay interval for Second Price timer.
     * @param secondPriceWaitInterval the interval in milliseconds
     * */
    public static void setSecondPriceWaitInterval(long secondPriceWaitInterval) {
        GooglePlayAdsSettings.secondPriceWaitInterval = secondPriceWaitInterval;
    }

    /**
     * @return the number of desired retries Second Price timer, 10 ms by default.
     * */
    protected static int getTotalRetries() {
        return totalRetries;
    }

    /**
     * Set the number of retries for Second Price timer.
     * @param totalRetries the number of retries for the Second Price timer
     * */
    public static void setTotalRetries(int totalRetries) {
        GooglePlayAdsSettings.totalRetries = totalRetries;
    }
}
