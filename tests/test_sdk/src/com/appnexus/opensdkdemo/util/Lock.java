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

package com.appnexus.opensdkdemo.util;

import com.appnexus.opensdk.utils.Clog;

public class Lock {
    public static final Object lock = new Object();
    public static boolean notified = false;

    public static void pause(long time) {
        try {
            synchronized (lock) {
                Clog.w(TestUtil.testLogTag, "pausing " + Thread.currentThread().getName());
                if (!notified)
                    lock.wait(time);
                else
                    Clog.w(TestUtil.testLogTag, "pause called when notified " + Thread.currentThread().getName());
                notified = false;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            return;
        }
        Clog.w(TestUtil.testLogTag, "unpausing " + Thread.currentThread().getName());
    }

    public static void unpause() {
        Clog.w(TestUtil.testLogTag, "notify from " + Thread.currentThread().getName());
        synchronized (lock) {
            lock.notifyAll();
            notified = true;
        }
    }
}