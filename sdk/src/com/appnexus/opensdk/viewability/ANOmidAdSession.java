/*
 *    Copyright 2018 APPNEXUS INC
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
package com.appnexus.opensdk.viewability;

import android.view.View;
import android.webkit.WebView;

import com.appnexus.opensdk.SDKSettings;
import com.appnexus.opensdk.utils.Clog;
import com.appnexus.opensdk.utils.StringUtil;
import com.iab.omid.library.appnexus.ScriptInjector;
import com.iab.omid.library.appnexus.adsession.AdEvents;
import com.iab.omid.library.appnexus.adsession.AdSession;
import com.iab.omid.library.appnexus.adsession.AdSessionConfiguration;
import com.iab.omid.library.appnexus.adsession.AdSessionContext;
import com.iab.omid.library.appnexus.adsession.CreativeType;
import com.iab.omid.library.appnexus.adsession.ImpressionType;
import com.iab.omid.library.appnexus.adsession.Owner;
import com.iab.omid.library.appnexus.adsession.VerificationScriptResource;

import java.util.ArrayList;
import java.util.List;

public class ANOmidAdSession {

    private AdSession omidAdSession;

    List<VerificationScriptResource> verificationScriptResources = new ArrayList<>();

    public String prependOMIDJSToHTML(String html) {
        if (!SDKSettings.getOMEnabled())
            return html;

        try {
            String htmlString = html;
            if (!StringUtil.isEmpty(ANOmidViewabilty.getInstance().getOmidJsServiceContent())) {
                htmlString = ScriptInjector.injectScriptContentIntoHtml(ANOmidViewabilty.getInstance().getOmidJsServiceContent(),
                        html);
            }
            return htmlString;
        } catch (Exception e) {
            e.printStackTrace();
            // Return original HTML if there was an error
            return html;
        }
    }

    public void initAdSession(WebView webView, boolean isVideoAd) {
        if (!SDKSettings.getOMEnabled())
            return;

        try {
            String customReferenceData = "";
            AdSessionContext adSessionContext = AdSessionContext.createHtmlAdSessionContext(ANOmidViewabilty.getInstance().getAppnexusPartner(), webView, null, customReferenceData);

            Owner owner = isVideoAd ? Owner.JAVASCRIPT : Owner.NATIVE;


            CreativeType creativeType = (isVideoAd ? CreativeType.VIDEO : CreativeType.HTML_DISPLAY);
            ImpressionType impressionType = (isVideoAd ? ImpressionType.DEFINED_BY_JAVASCRIPT : ImpressionType.VIEWABLE);
            Owner eventsOwner = isVideoAd ? Owner.JAVASCRIPT : Owner.NONE;

            AdSessionConfiguration adSessionConfiguration =
                    AdSessionConfiguration.createAdSessionConfiguration(creativeType, impressionType, owner, eventsOwner, false);


            omidAdSession = AdSession.createAdSession(adSessionConfiguration, adSessionContext);
            omidAdSession.registerAdView(webView);
            omidAdSession.start();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (NullPointerException exception) {
            Clog.e(Clog.baseLogTag, "OMID Ad Session - Exception", exception);
        }
    }

    public void initNativeAdSession(View view) {
        if (!SDKSettings.getOMEnabled())
            return;
        try {

            AdSessionContext adSessionContext = AdSessionContext.createNativeAdSessionContext(ANOmidViewabilty.getInstance().getAppnexusPartner(), ANOmidViewabilty.getInstance().getOmidJsServiceContent(), verificationScriptResources, null, null);
            AdSessionConfiguration adSessionConfiguration =
                    AdSessionConfiguration.createAdSessionConfiguration(CreativeType.NATIVE_DISPLAY, ImpressionType.VIEWABLE, Owner.NATIVE, null, false);


            omidAdSession = AdSession.createAdSession(adSessionConfiguration, adSessionContext);
            omidAdSession.registerAdView(view);
            omidAdSession.start();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (NullPointerException exception) {
            Clog.e(Clog.baseLogTag, "OMID Ad Session - Exception", exception);
        }
    }

    public void addToVerificationScriptResources(VerificationScriptResource verificationScriptResource) {
        verificationScriptResources.add(verificationScriptResource);

    }

    public boolean isVerificationResourcesPresent() {
        if (verificationScriptResources != null && !verificationScriptResources.isEmpty()) {
            return true;
        }
        return false;
    }

    public void fireImpression() {
        if (!SDKSettings.getOMEnabled())
            return;

        if (omidAdSession != null) {
            try {
                AdEvents adEvents = AdEvents.createAdEvents(omidAdSession);
                adEvents.loaded();
                adEvents.impressionOccurred();
            } catch (IllegalArgumentException | IllegalStateException e) {
                e.printStackTrace();
            }
        }
    }

    public void stopAdSession() {
        if (!SDKSettings.getOMEnabled())
            return;

        if (omidAdSession != null) {
            omidAdSession.finish();
            omidAdSession = null;
        }
    }

    /**
     * For removing Friendly Obstruction View
     *
     * @param view to be removed
     */
    public void removeFriendlyObstruction(View view) {
        if (!SDKSettings.getOMEnabled())
            return;
        if (omidAdSession != null) {
            omidAdSession.removeFriendlyObstruction(view);
        }
    }

    /**
     * For clearing the Friendly Obstruction Views
     */
    public void removeAllFriendlyObstructions() {
        if (!SDKSettings.getOMEnabled())
            return;
        if (omidAdSession != null) {
            omidAdSession.removeAllFriendlyObstructions();
        }
    }

    public void addFriendlyObstruction(View friendlyObstructionView) {
        if (friendlyObstructionView != null && omidAdSession != null) {
            omidAdSession.addFriendlyObstruction(friendlyObstructionView, null, null);
        }
    }
}
