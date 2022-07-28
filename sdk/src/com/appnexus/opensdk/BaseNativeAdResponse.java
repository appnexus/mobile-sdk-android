/*
 *    Copyright 2019 APPNEXUS INC
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


import static com.appnexus.opensdk.utils.Settings.ImpressionType.*;

import android.view.View;

import com.appnexus.opensdk.utils.Clog;
import com.appnexus.opensdk.utils.Settings;
import com.appnexus.opensdk.utils.Settings.ImpressionType;
import com.appnexus.opensdk.viewability.ANOmidAdSession;
import com.appnexus.opensdk.viewability.ANOmidVerificationScriptParseUtil;
import com.iab.omid.library.appnexus.adsession.VerificationScriptResource;

import org.json.JSONObject;

import java.util.List;

public abstract class BaseNativeAdResponse implements NativeAdResponse {

    protected ANOmidAdSession anOmidAdSession = new ANOmidAdSession();
    private ANAdResponseInfo adResponseInfo;
    private Settings.ImpressionType impressionType = BEGIN_TO_RENDER;

    protected abstract boolean registerView(View view, NativeAdEventListener listener);

    protected abstract boolean registerViewList(View view, List<View> clickables, NativeAdEventListener listener);

    protected abstract void unregisterViews();

    protected abstract boolean registerNativeAdEventListener(NativeAdEventListener listener);

    @Override
    public ANAdResponseInfo getAdResponseInfo() {
        return adResponseInfo;
    }

    @Override
    public void setAdResponseInfo(ANAdResponseInfo adResponseInfo) {
        this.adResponseInfo = adResponseInfo;
    }

    protected void registerViewforOMID(View view){
        registerViewforOMID(view, null);
    }

    protected void registerViewforOMID(View view, List<View> friendlyObstructionsList){
        if(anOmidAdSession!=null && anOmidAdSession.isVerificationResourcesPresent()) {
            anOmidAdSession.initNativeAdSession(view);
            addFriendlyObstructions(friendlyObstructionsList);
        }
    }


    @Override
    public void destroy() {
        if(anOmidAdSession!=null){
            anOmidAdSession.stopAdSession();
        }
    }

    protected void setANVerificationScriptResources(JSONObject ad){
        VerificationScriptResource verificationScriptResource = ANOmidVerificationScriptParseUtil.parseViewabilityObjectfromAdObject(ad);
        if(verificationScriptResource!=null) {
            anOmidAdSession.addToVerificationScriptResources(verificationScriptResource);
        }
    }

    private void addFriendlyObstructions(List<View> friendlyObstructionViews) {
        if (friendlyObstructionViews == null) {
            return;
        }
        for (View view : friendlyObstructionViews) {
            if (view != null) {
                anOmidAdSession.addFriendlyObstruction(view);
            }
        }
    }

    protected long getAboutToExpireTime(String contentSource, int memberId) {
        long aboutToExpireTime = Settings.NATIVE_AD_RESPONSE_EXPIRATION_TIME;
        if (contentSource.equalsIgnoreCase("csm") || contentSource.equalsIgnoreCase("csr")) {
            aboutToExpireTime = Settings.NATIVE_AD_RESPONSE_EXPIRATION_TIME_CSM_CSR;
        } else if (contentSource.equalsIgnoreCase("rtb") && memberId == 11217) {
            aboutToExpireTime = Settings.NATIVE_AD_RESPONSE_EXPIRATION_TIME_TRIPLELIFT;
        } else if (contentSource.equalsIgnoreCase("rtb") && memberId == 12085) {
            aboutToExpireTime = Settings.NATIVE_AD_RESPONSE_EXPIRATION_TIME_MSAN;
        } else if (contentSource.equalsIgnoreCase("rtb") && memberId == 9642) {
            aboutToExpireTime = Settings.NATIVE_AD_RESPONSE_EXPIRATION_TIME_INDEX;
        } else if (contentSource.equalsIgnoreCase("rtb") && memberId == 12317) {
            aboutToExpireTime = Settings.NATIVE_AD_RESPONSE_EXPIRATION_TIME_INMOBI;
        }

        return aboutToExpireTime - getExpiryInterval(contentSource, memberId);
    }

    protected long getExpiryInterval(String contentSource, int memberId) {
        long interval = Settings.NATIVE_AD_ABOUT_TO_EXPIRE_INTERVAL_DEFAULT;
        long expiryInterval = Settings.NATIVE_AD_ABOUT_TO_EXPIRE_INTERVAL;
        if (expiryInterval <= 0) {
            Clog.e(Clog.baseLogTag, "expiryInterval can not be set less then zero, default interval will be used.");
        } else if((contentSource.equalsIgnoreCase("csm") || contentSource.equalsIgnoreCase("csr")) && expiryInterval >= Settings.NATIVE_AD_RESPONSE_EXPIRATION_TIME_CSM_CSR) {
            Clog.e(Clog.baseLogTag, "facebook expiryInterval can not be greater than 60 minutes, default interval will be used.");
        } else if (contentSource.equalsIgnoreCase("rtb") && memberId == 12085 && expiryInterval >= Settings.NATIVE_AD_RESPONSE_EXPIRATION_TIME_MSAN){
            Clog.e(Clog.baseLogTag, "for RTB & member 12085 expiryInterval can not be greater than 10 minutes, default interval will be used.");
        } else if (contentSource.equalsIgnoreCase("rtb") && memberId == 11217 && expiryInterval >= Settings.NATIVE_AD_RESPONSE_EXPIRATION_TIME_TRIPLELIFT){
            Clog.e(Clog.baseLogTag, "for RTB & member 11217 expiryInterval can not be greater than 5 minutes, default interval will be used.");
        } else if (contentSource.equalsIgnoreCase("rtb") && memberId == 9642 && expiryInterval >= Settings.NATIVE_AD_RESPONSE_EXPIRATION_TIME_INDEX){
            Clog.e(Clog.baseLogTag, "for RTB & member 9642 expiryInterval can not be greater than 5 minutes, default interval will be used.");
        } else if (contentSource.equalsIgnoreCase("rtb") && memberId == 12317 && expiryInterval >= Settings.NATIVE_AD_RESPONSE_EXPIRATION_TIME_INMOBI){
            Clog.e(Clog.baseLogTag, "for RTB & member 12317 expiryInterval can not be greater than 55 minutes, default interval will be used.");
        } else if(expiryInterval >= Settings.NATIVE_AD_RESPONSE_EXPIRATION_TIME){
            Clog.e(Clog.baseLogTag, "for RTB  expiryInterval can not be greater than 6 hours, default interval will be used.");
        } else {
            interval  = expiryInterval;
        }

        Clog.d(Clog.baseLogTag, "onAdAboutToExpire() will be called " + interval + "ms prior to expiry.");
        return interval;
    }

    public void setImpressionType(ImpressionType impressionType) {
        this.impressionType = impressionType;
    }

    public ImpressionType getImpressionType() {
        return impressionType;
    }
}
