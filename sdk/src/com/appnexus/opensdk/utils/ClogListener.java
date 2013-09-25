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

public abstract class ClogListener {

    /**
     * Log level options correspond to Android Logcat options:
     * Verbose, Debug, Info, Warning, Error
     */
    public enum LOG_LEVEL {
        V,
        D,
        I,
        W,
        E
    }

    /**
     * Callback for all messages set to Clog after listener is registered.
     * Implement special handling of Clog messages here.
     * Make sure not to print to Clog in this method, or else
     * the program will deadlock.
     *
     * @param level the level of verbosity
     * @param LogTag the log tag associated with the message
     * @param message the String message passed to Clog
     */
    public abstract void onReceiveMessage(LOG_LEVEL level, String LogTag, String message);

    /**
     * Callback for all messages set to Clog after listener is registered.
     * Implement special handling of Clog messages here.
     * Make sure not to print to Clog in this method, or else
     * the program will deadlock.
     *
     * @param level the level of verbosity
     * @param LogTag the log tag associated with the message
     * @param message the String message passed to Clog
     * @param tr the throwable associated with the Clog message
     */
    public abstract void onReceiveMessage(LOG_LEVEL level, String LogTag, String message, Throwable tr);

    /**
     * Specify filtering level for Clog messages
     *
     * @return minimum level of verbosity to filter messages at.
     * For example, returning V (Verbose) will receive all messages.
     * Returning E (Error) will receive only E-level messages
     */
    public abstract LOG_LEVEL getLogLevel();
}