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

package com.appnexus.opensdkapp;

import android.content.Context;
import android.text.Layout;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Switch;

import java.lang.reflect.Field;

public class ANSwitch extends Switch {
    public ANSwitch(Context context) {
        super(context);
    }

    public ANSwitch(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ANSwitch(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void onPopulateAccessibilityEvent(AccessibilityEvent event) {
        super.onPopulateAccessibilityEvent(event);
        Log.e("OPENSDK", "???");
//        if (isChecked()) {
//            event.getText().add(getTextOff());
//        } else {
//            event.getText().add(getTextOn());
//        }
        Field f = null;
        try {
            f = this.getClass().getDeclaredField("mOffLayout");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        f.setAccessible(true);
        Object v = null;
        try {
            v = f.get(this);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        Layout mOffLayout = (Layout) v;

        CharSequence text = mOffLayout.getText();
        if (TextUtils.isEmpty(text)) {
            text = getTextOff();
        }
        event.getText().add(text);
        Log.e("OPENSDK", String.valueOf(mOffLayout));
//        if (isChecked()) {
//
//            Field f = null;
//            try {
//                f = this.getClass().getDeclaredField("mOffLayout");
//            } catch (NoSuchFieldException e) {
//                e.printStackTrace();
//            }
//
//            f.setAccessible(true);
//            Object v = null;
//            try {
//                v = f.get(this);
//            } catch (IllegalAccessException e) {
//                e.printStackTrace();
//            }
//            Layout mOffLayout = (Layout) v;
//
//            CharSequence text = mOffLayout.getText();
//            if (TextUtils.isEmpty(text)) {
//                text = getTextOff();
//            }
//            event.getText().add(text);
//        } else {
//            CharSequence text = mOnLayout.getText();
//            if (TextUtils.isEmpty(text)) {
//                text = mContext.getString(R.string.switch_off);
//            }
//            event.getText().add(text);
//        }
    }

    @Override
    public CharSequence getTextOn() {
        Log.e("OPENSDK", "text on");
        return "HI";
    }
}
