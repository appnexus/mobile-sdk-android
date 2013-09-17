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

public class SettingsWrapper {
    private boolean isAdTypeBanner;
    private boolean isAllowPsas;
    private boolean isBrowserInApp;
    private String placementId;
    private String size;
    private String refresh;
    private String backgroundColor;
    private String closeDelay;
    private String memberId;
    private String dongle;

    private int width, height;

    public boolean isAdTypeBanner() {
        return isAdTypeBanner;
    }

    public void setAdTypeBanner(boolean adTypeBanner) {
        isAdTypeBanner = adTypeBanner;
    }

    public boolean isAllowPsas() {
        return isAllowPsas;
    }

    public void setAllowPsas(boolean allowPsas) {
        isAllowPsas = allowPsas;
    }

    public boolean isBrowserInApp() {
        return isBrowserInApp;
    }

    public void setBrowserInApp(boolean browserInApp) {
        isBrowserInApp = browserInApp;
    }

    public String getPlacementId() {
        return placementId;
    }

    public void setPlacementId(String placementId) {
        this.placementId = placementId;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
        parseSize();
    }

    public String getRefresh() {
        return refresh;
    }

    public void setRefresh(String refresh) {
        this.refresh = refresh;
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public String getCloseDelay() {
        return closeDelay;
    }

    public void setCloseDelay(String closeDelay) {
        this.closeDelay = closeDelay;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getDongle() {
        return dongle;
    }

    public void setDongle(String dongle) {
        this.dongle = dongle;
    }

    public static SettingsWrapper getSettingsWrapperFromPrefs(Context context) {
        SettingsWrapper settingsWrapper = new SettingsWrapper();
        settingsWrapper.isAdTypeBanner = Prefs.getAdType(context);
        settingsWrapper.isAllowPsas = Prefs.getAllowPSAs(context);
        settingsWrapper.isBrowserInApp = Prefs.getBrowserInApp(context);
        settingsWrapper.placementId = Prefs.getPlacementId(context);
        settingsWrapper.size = Prefs.getSize(context);
        settingsWrapper.parseSize();
        settingsWrapper.refresh = Prefs.getRefresh(context);
        settingsWrapper.backgroundColor = Prefs.getColor(context);
        settingsWrapper.closeDelay = Prefs.getCloseDelay(context);
        settingsWrapper.dongle = Prefs.getDongle(context);

        return settingsWrapper;
    }

    /**
     * format converters
     */

    public int getRefreshPeriod() {
        if (refresh.equals("off"))
            return 0;
        else {
            return (1000 * Integer.parseInt(refresh.replace(" seconds", "")));
        }
    }

    public int getCloseDelayPeriod() {
        if (closeDelay.equals("off"))
            return 0;
        else {
            try {
                return (1000 * Integer.parseInt(closeDelay.replace(" seconds", "")));
            } catch (NumberFormatException e) {
                return 0;
            }
        }
    }

    private void parseSize() {
        String[] dimens = size.split("x");
        try {
            width = Integer.parseInt(dimens[0]);
            height = Integer.parseInt(dimens[1]);
        } catch (NumberFormatException e) {
            width = 0;
            height = 0;
        }
    }
    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
