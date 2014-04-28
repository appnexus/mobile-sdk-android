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

package com.appnexus.opensdk;

/**
 * This enum contains the result codes sent to the
 * <code>onAdRequestFailed</code> method in {@link AdListener},
 * except for <code>SUCCESS</code>.
 *
 * Mediation adaptors should use these result codes to
 * inform the AppNexus SDK of success or failure.
 *
 * Note that the result code passed to the app developer signifies
 * the final result of an ad request, not each individual result passed
 * by mediation networks.
 */
public enum ResultCode {
    /**
     * The ad loaded successfully.
     */
    SUCCESS,
    /**
     * The ad request failed due to an invalid configuration (for example, size
     * or placement ID not set).
     */
    INVALID_REQUEST,
    /**
     * The network did not return an ad in this call.
     */
    UNABLE_TO_FILL,
    /**
     * Used internally when the third-party SDK is not available.
     */
    MEDIATED_SDK_UNAVAILABLE,
    /**
     * The ad request failed due to a network error.
     */
    NETWORK_ERROR,
    /**
     * An internal error is detected in the interacting with the
     * third-party SDK.
     */
    INTERNAL_ERROR
}
