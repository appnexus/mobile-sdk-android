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

package com.appnexus.opensdk;

/**
 * Define the events relating the status of a NativeAdRequest
 */

public interface NativeAdRequestListener {
    /**
     * Called when a native ad has successfully loaded.
     * If the developer requested shouldLoadIcon true or shouldLoadImage true.
     * This method will be called only after the image resources have been retrieved.
     * If errors are encountered in resource retrieval this method will still be called.
     * Errors encountered in resource retrieval will result in the getImage() or getIcon()
     * returning null respectively.
     * @param response a NativeAdResponse
     */
    public void onAdLoaded(NativeAdResponse response);

    /**
     * Called when a native ad call has failed
     * @param errorcode reason the call failed. Error codes TBD
     */
    public void onAdFailed(ResultCode errorcode);
}
