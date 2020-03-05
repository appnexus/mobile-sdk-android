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


import android.view.View;

import com.appnexus.opensdk.viewability.ANOmidAdSession;
import com.appnexus.opensdk.viewability.ANOmidVerificationScriptParseUtil;
import com.iab.omid.library.appnexus.adsession.VerificationScriptResource;

import org.json.JSONObject;

import java.util.List;

public abstract class BaseNativeAdResponse implements NativeAdResponse {

    protected ANOmidAdSession anOmidAdSession = new ANOmidAdSession();
    private ANAdResponseInfo adResponseInfo;

    protected abstract boolean registerView(View view, NativeAdEventListener listener);

    protected abstract boolean registerViewList(View view, List<View> clickables, NativeAdEventListener listener);

    protected abstract void unregisterViews();

    @Override
    public ANAdResponseInfo getAdResponseInfo() {
        return adResponseInfo;
    }

    @Override
    public void setAdResponseInfo(ANAdResponseInfo adResponseInfo) {
        this.adResponseInfo = adResponseInfo;
    }



    protected void registerViewforOMID(View view){
        if(anOmidAdSession!=null && anOmidAdSession.isVerificationResourcesPresent()) {
            anOmidAdSession.initNativeAdSession(view);
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
}
