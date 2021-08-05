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

package com.appnexus.opensdk;

import android.text.TextUtils;

/**
 * This class contains the result codes sent to the
 * <code>onAdRequestFailed</code> method in {@link AdListener},
 * except for <code>SUCCESS</code>.
 * <p>
 * Mediation adaptors should use these result codes to
 * inform the AppNexus SDK of success or failure.
 * <p>
 * Note that the result code passed to the app developer signifies
 * the final result of an ad request, not each individual result passed
 * by mediation networks.
 */
public class ResultCode {

    /**
     * The ad loaded successfully.
     */
    public static int SUCCESS = 0;
    /**
     * The ad request failed due to an invalid configuration (for example, size
     * or placement ID not set).
     */
    public static int INVALID_REQUEST = 1;
    /**
     * The network did not return an ad in this call.
     */
    public static int UNABLE_TO_FILL = 2;
    /**
     * Used internally when the third-party SDK is not available.
     */
    public static int MEDIATED_SDK_UNAVAILABLE = 3;
    /**
     * The ad request failed due to a network error.
     */
    public static int NETWORK_ERROR = 4;
    /**
     * An internal error is detected in the interacting with the
     * third-party SDK.
     */
    public static int INTERNAL_ERROR = 5;
    /**
     * Called loadAd too many times
     */
    public static int REQUEST_TOO_FREQUENT = 6;
    /**
     * Pass errors from adapters to users
     */
    public static int CUSTOM_ADAPTER_ERROR = 7;

    /**
     * Ad Request Interrupted by the User
     */
    public static int REQUEST_INTERRUPTED_BY_USER = 8;

    private int errorCode;
    private String message;

    private ResultCode(int code, String message) {
        if (TextUtils.isEmpty(message)) {
            switch (code) {
                case 0:
                    message = "SUCCESS";
                    break;
                case 1:
                    message = "INVALID_REQUEST";
                    break;
                case 2:
                    message = "UNABLE_TO_FILL";
                    break;
                case 3:
                    message = "MEDIATED_SDK_UNAVAILABLE";
                    break;
                case 4:
                    message = "NETWORK_ERROR";
                    break;
                case 5:
                    message = "INTERNAL_ERROR";
                    break;
                case 6:
                    message = "REQUEST_TOO_FREQUENT";
                    break;
                case 7:
                    message = "CUSTOM_ADAPTER_ERROR";
                    break;
                case 8:
                    message = "REQUEST_INTERRUPTED_BY_USER";
                    break;
                default:
                    message = "UNKNOWN";
            }
        }

        this.errorCode = code;
        this.message = message;
    }

    private ResultCode() {
    }

    /**
     * This initializer method can be used to obtain a new instance of ResultCode, by passing in the code
     * @param code the code ranging from 0 t0 7 (The ResultCode range)
     * */
    public static ResultCode getNewInstance(int code) {
        return new ResultCode(code, "");
    }

    /**
     * This initializer method can be used to obtain a new instance of ResultCode, by passing in the code ansd the message
     * @param code the code ranging from 0 t0 7 (The ResultCode range)
     * @param message the message to be passed in with the ResultCode */
    public static ResultCode getNewInstance(int code, String message) {
        return new ResultCode(code, message);
    }

    /**
     * Getter for the Code assigned to this instance of ResultCode
     * */
    public int getCode() {
        return this.errorCode;
    }

    /**
     * Getter for the Message assigned to this instance of ResultCode
     * */
    public String getMessage() {
        return this.message;
    }

    /**
     * Setter for the Message assigned to this instance of ResultCode
     * */
    public void setMessage(String message) {
        if (this.getCode() == ResultCode.CUSTOM_ADAPTER_ERROR) {
            this.message = message;
        }
    }

    /**
     * This method has been overridden to smartly compare the ResultCode
     * */
    @Override
    public boolean equals(Object obj) {
        return (obj != null && obj instanceof ResultCode && ((ResultCode)obj).getCode() == this.getCode()) || obj instanceof Integer && obj == Integer.valueOf(this.getCode());
    }
}
