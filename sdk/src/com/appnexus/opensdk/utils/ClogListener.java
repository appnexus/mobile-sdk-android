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
    public enum LOG_LEVEL {
        V,
        D,
        I,
        W,
        E
    }

    public abstract void onReceiveMessage(LOG_LEVEL level, String LogTag, String message);

    public abstract void onReceiveMessage(LOG_LEVEL level, String LogTag, String message, Throwable tr);

    public abstract boolean isVerboseLevelEnabled();

    public abstract boolean isDebugLevelEnabled();

    public abstract boolean isInfoLevelEnabled();

    public abstract boolean isWarningLevelEnabled();

    public abstract boolean isErrorLevelEnabled();

}
