/*
 *    Copyright 2021 APPNEXUS INC
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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.appnexus.opensdk.utils.Clog;

import java.util.ArrayList;
import java.util.List;

/**
 * ScreenEventReceiver class holds the responsibility of registering / unregistering the receiver
 * The receiver is registered to get the Screen On and Off events
 * */
class ScreenEventReceiver {

    private BroadcastReceiver receiver;
    private List<ScreenEventListener> listenerList;
    private static ScreenEventReceiver instance;
    private Context appContext;

    private ScreenEventReceiver(){}

    private ScreenEventReceiver(Context context){
        if (appContext == null) {
            appContext = context.getApplicationContext();
        }
    }

    // Factory method to get the instance of ScreenEventReceiver
    public static ScreenEventReceiver getInstance(Context context) {
        if (instance == null) {
            instance = new ScreenEventReceiver(context);
        }
        return instance;
    }

    // To register the ScreenEventListener
    protected void registerListener(ScreenEventListener listener) {
        if (listenerList == null) {
            listenerList = new ArrayList<>();
        }
        listenerList.add(listener);
        if (listenerList.size() == 1) {
            registerReceiver();
        }
    }

    // To unregister the ScreenEventListener
    protected void unregisterListener(ScreenEventListener listener) {
        if (listenerList == null) {
            return;
        }

        if (listenerList.contains(listener)) {
            listenerList.remove(listener);
            if (listenerList.size() == 0) {
                unregisterReceiver();
            }
        }
    }

    // Responsible for initialising and registering the BroadcastReceiver
    private void registerReceiver() {
        if (receiver == null) {
            receiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if (listenerList != null && listenerList.size() > 0) {
                        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                            for (ScreenEventListener listener : listenerList) {
                                if (listener != null) {
                                    listener.onScreenOff();
                                }
                            }
                        } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                            for (ScreenEventListener listener : listenerList) {
                                if (listener != null) {
                                    listener.onScreenOn();
                                }
                            }
                        }
                    }
                }
            };
        }
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        if (appContext != null) {
            // for non-sticky filters, registerReceiver always returns null.
            appContext.registerReceiver(receiver, filter);
        } else {
            Clog.e(Clog.baseLogTag, "Lost app context");
        }
    }

    // Responsible for unregistering the BroadcastReceiver
    private void unregisterReceiver() {
        // Catch exception to protect against receiver failing to be registered.
        try {
            if (appContext != null && receiver != null) {
                appContext.unregisterReceiver(receiver);
            }
        } catch (IllegalArgumentException ignored) {}
    }

    // Checks if the ScreenEventListener is already registered
    protected boolean isAlreadyRegistered(ScreenEventListener screenEventListener) {
        return listenerList != null && listenerList.contains(screenEventListener);
    }
}
