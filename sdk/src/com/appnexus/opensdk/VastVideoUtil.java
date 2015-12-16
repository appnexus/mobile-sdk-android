/*
 *    Copyright 2015 APPNEXUS INC
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

import android.content.Context;
import android.media.CamcorderProfile;
import android.os.Build;

import com.appnexus.opensdk.utils.Clog;
import com.appnexus.opensdk.utils.Connectivity;
import com.appnexus.opensdk.utils.StringUtil;
import com.appnexus.opensdk.vastdata.AdModel;
import com.appnexus.opensdk.vastdata.ClickTrackingModel;
import com.appnexus.opensdk.vastdata.CreativeModel;
import com.appnexus.opensdk.vastdata.LinearAdModel;
import com.appnexus.opensdk.vastdata.MediaFileModel;
import com.appnexus.opensdk.vastdata.TrackingModel;
import com.appnexus.opensdk.vastdata.VideoClickModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class VastVideoUtil {

    public static final int MAX_VIDEO_HEIGHT = 720;
    public final static String VAST_ADTAGURI_TAG = "VASTAdTagURI";
    public final static String VAST_START_TAG = "VAST";
    public final static String VAST_AD_TAG = "Ad";
    public final static String VAST_INLINE_TAG = "InLine";
    public final static String VAST_WRAPPER_TAG = "Wrapper";
    public final static String VAST_IMPRESSION_TAG = "Impression";
    public final static String VAST_CREATIVES_TAG = "Creatives";
    public final static String VAST_CREATIVE_TAG = "Creative";
    public final static String VAST_LINEAR_TAG = "Linear";
    public final static String VAST_COMPANIONADS_TAG = "CompanionAds";
    public final static String VAST_COMPANION_TAG = "Companion";
    public final static String VAST_STATICRESOURCE_TAG = "StaticResource";
    public final static String VAST_IFRAMERESOURCE_TAG = "IFrameResource";
    public final static String VAST_HTMLRESOURCE_TAG = "HTMLResource";
    public final static String VAST_COMPANIONCLICKTHROUGH_TAG = "CompanionClickThrough";
    public final static String VAST_COMPANIONCLICKTRACKING_TAG = "CompanionClickTracking";
    public final static String VAST_NONLINEARADS_TAG = "NonLinearAds";
    public final static String VAST_NONLINEAR_TAG = "NonLinear";
    public final static String VAST_NONLINEARCLICKTHROUGH_TAG = "NonLinearClickThrough";
    public final static String VAST_NONLINEARCLICKTRACKING_TAG = "NonLinearClickTracking";
    public final static String VAST_DURATION_TAG = "Duration";
    public final static String VAST_TRACKINGEVENTS_TAG = "TrackingEvents";
    public final static String VAST_TRACKING_TAG = "Tracking";
    public final static String VAST_MEDIAFILES_TAG = "MediaFiles";
    public final static String VAST_MEDIAFILE_TAG = "MediaFile";
    public final static String VAST_VIDEOCLICKS_TAG = "VideoClicks";
    public final static String VAST_CLICKTHROUGH_TAG = "ClickThrough";
    public final static String VAST_CLICKTRACKING_TAG = "ClickTracking";
    public final static String VAST_ADSYSTEM_TAG = "AdSystem";
    public final static String VAST_ADTITLE_TAG = "AdTitle";
    public final static String VAST_DESCRIPTION_TAG = "Description";
    public final static String VAST_ADVERTISER_TAG = "Advertiser";
    public final static String VAST_PRICING_TAG = "Pricing";
    public final static String VAST_SURVEY_TAG = "Survey";
    public final static String VAST_ERROR_TAG = "Error";
    public static final String VAST_ADPARAMETERS_TAG = "AdParameters";
    public static final String VAST_ALTTEXT_TAG = "AltText";

    // VAST parser Read Media Files Attribute
    public static final String VAST_READMEDIAFILES_ID_ATTR = "id";
    public static final String VAST_READMEDIAFILES_DELIVERY_ATTR = "delivery";
    public static final String VAST_READMEDIAFILES_TYPE_ATTR = "type";
    public static final String VAST_READMEDIAFILES_BITRATE_ATTR =  "bitrate";
    public static final String VAST_READMEDIAFILES_MINBITRATE_ATTR = "minBitrate";
    public static final String VAST_READMEDIAFILES_MAXBITRATE_ATTR =  "maxBitrate";
    public static final String VAST_READMEDIAFILES_WIDTH_ATTR =  "width";
    public static final String VAST_READMEDIAFILES_HEIGHT_ATTR = "height";
    public static final String VAST_READMEDIAFILES_SCALABLE_ATTR =  "scalable";
    public static final String VAST_READMEDIAFILES_MAINTAINASPECTRATIO_ATTR = "maintainAspectRatio";
    public static final String VAST_READMEDIAFILES_CODEC_ATTR =  "codec";
    public static final String VAST_READMEDIAFILES_APIFRAMEWORK_ATTR = "apiFramework";

    // VAST parser Read Companion Attribute
    public static final String VAST_READCOMPANION_ID_ATTR =  "id";
    public static final String VAST_READCOMPANION_WIDTH_ATTR =   "width";
    public static final String VAST_READCOMPANION_HEIGHT_ATTR =   "height";
    public static final String VAST_READCOMPANION_ASSETWIDTH_ATTR =  "assetWidth";
    public static final String VAST_READCOMPANION_ASSETHIGHT_ATTR =  "assetHeight";
    public static final String VAST_READCOMPANION_EXPANDEDWIDTH_ATTR =   "expandedWidth";
    public static final String VAST_READCOMPANION_EXPANDEDHIGHT_ATTR =   "expandedHeight";
    public static final String VAST_READCOMPANION_APIFRAMEWORK_ATTR =  "apiFramework";
    public static final String VAST_READCOMPANION_ADSLOT_ATTR =  "adSlotID";

    // VAST Read NonLinear Attribute

    public static final String VAST_READNONLINEAR_ID_ATTR =  "id";
    public static final String VAST_READNONLINEAR_WIDTH_ATTR =   "width";
    public static final String VAST_READNONLINEAR_HEIGHT_ATTR =   "height";
    public static final String VAST_READNONLINEAR_SCALABLE_ATTR =   "scalable";
    public static final String VAST_READNONLINEAR_MAINTAINASPECTRATIO_ATTR =   "maintainAspectRatio";
    public static final String VAST_READNONLINEAR_EXPANDEDWIDTH_ATTR =   "expandedWidth";
    public static final String VAST_READNONLINEAR_EXPANDEDHIGHT_ATTR =   "expandedHeight";
    public static final String VAST_READNONLINEAR_APIFRAMEWORK_ATTR =  "apiFramework";
    public static final String VAST_READNONLINEAR_MINSUGGESTIONDURATION_ATTR =  "minSuggestedDuration";


    public static final String EVENT_FIRST_QUARTILE = "firstQuartile";
    public static final String EVENT_MIDPOINT = "midpoint";
    public static final String EVENT_THIRD_QUARTILE = "thirdQuartile";
    public static final String EVENT_COMPLETE = "complete";

    public static final String EVENT_MUTE = "mute";
    public static final String EVENT_UNMUTE = "unmute";
    public static final String EVENT_PAUSE = "pause";
    public static final String EVENT_RESUME= "resume";
    public static final String EVENT_START = "start";
    public static final String EVENT_SKIP ="skip";

    public static final int VIDEO_SKIP=10011;
    public static final int VIDEO_VIEW=10012;

    public static final int DEFAULT_SKIP_OFFSET = -9999;


    /**
     * Converts string to seconds
     *
     * @param timestampStr
     * @return
     */
    public static int convertStringtoSeconds(String timestampStr) {
        String[] tokens = timestampStr.split(":");
        int duration = 0;
        try {
            int hours = Integer.parseInt(tokens[0]);
            int minutes = Integer.parseInt(tokens[1]);
            int seconds = Integer.parseInt(tokens[2]);
            duration = 3600 * hours + 60 * minutes + seconds;
        } catch (NumberFormatException e) {
        }
        return duration;
    }

    /**
     * Gets extension from url
     *
     * @param url
     * @return
     */
    public static String getExtensionFromUrl(String url) {
        String extension = "";
        int i = url.lastIndexOf('.');
        if (i >= 0) {
            extension = url.substring(i + 1);
        }
        return extension;
    }

    /**
     * Returns VAST Video url according to frame width to support video renditions.
     *
     * @param arrayList
     * @param context
     * @return
     */
    public static String getVASTVideoURL(ArrayList<MediaFileModel> arrayList, Context context) {

        int frameWidth = getBestSupportedFrameWidth(context);
        String mediaUrl = "";

        int count = arrayList.size();
        ArrayList<MediaFileModel> supportedVideoFormats = new ArrayList<MediaFileModel>();

        for (int i = 0; i < count; i++) {
            mediaUrl = arrayList.get(i).getUrl();
            String extension = VastVideoUtil.getExtensionFromUrl(mediaUrl);
            if (isFormatSupported(extension)) {
                supportedVideoFormats.add(arrayList.get(i));
            }
        }

        count = supportedVideoFormats.size();
        if (count == 0) {
            return mediaUrl;
        } else if (count == 1) {
            mediaUrl = supportedVideoFormats.get(0).getUrl();
            return mediaUrl;
        }

        try {
            int selectedIndex = 0;
            Collections.sort(supportedVideoFormats, new MediaFileComparator());
            for (int index = 0; index < count; index++) {
                int currentObjWidth = Integer.parseInt(supportedVideoFormats.get(index).getWidth());
                if (frameWidth <= currentObjWidth) {
                    break;
                }
                selectedIndex = index;
                Clog.i(Clog.vastLogTag, "Rendition currentObjWidth:" + currentObjWidth + " Index: "+index);
            }

            mediaUrl = supportedVideoFormats.get(selectedIndex).getUrl();
            Clog.i(Clog.vastLogTag, "Rendition Selected - using player width:" + frameWidth + ", selected width:" +  supportedVideoFormats.get(selectedIndex).getWidth() + " selectedIndex: "+selectedIndex);
        } catch (Exception e) {
            return mediaUrl;
        }

        return mediaUrl;
    }

    private static int getBestSupportedFrameWidth(Context context) {
        CamcorderProfile profile = getCamcorderProfile(context);

        Clog.i(Clog.vastLogTag, "Rendition Max Width: " + profile.videoFrameWidth);
        Clog.i(Clog.vastLogTag, "Rendition Max Height: " + profile.videoFrameHeight);

        return profile.videoFrameWidth;
    }

    private static CamcorderProfile getCamcorderProfile(Context context) {
        if(Connectivity.isConnectionFast(context)) {
            CamcorderProfile profile = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH);
            if(Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
                // Set a max limit to 720
                if(profile.videoFrameHeight > MAX_VIDEO_HEIGHT) {
                    profile = CamcorderProfile.get(CamcorderProfile.QUALITY_720P);
                }
            }
            return profile;
        }else{
            return CamcorderProfile.get(CamcorderProfile.QUALITY_LOW);
        }

    }

    private static boolean isFormatSupported(String extension) {
        return extension.startsWith("mp4") || extension.startsWith("MP4") || extension.startsWith("3gp")
                || extension.startsWith("3GP") || extension.startsWith("mkv") || extension.startsWith("MKV");
    }



    /**
     * Returns a list of VAST event URLs
     *
     * @param vastAd
     * @param eventType
     * @return
     */
    public static List<String> getVastEventURLList(
            AdModel vastAd, String eventType) {
        ArrayList<String> urlList = new ArrayList<String>();
        try {
            if (vastAd != null && vastAd.getCreativesArrayList() != null && vastAd.getCreativesArrayList().size()> 0) {
                for (CreativeModel creativeModel : vastAd.getCreativesArrayList()) {
                    if (creativeModel != null) {
                        for (TrackingModel a : creativeModel.getLinearAdModel().getTrackingEventArrayList()) {
                            if (a.getEvent().equalsIgnoreCase(eventType)) {
                                Clog.i(Clog.vastLogTag, "TRACKING EVENT - " + a.getEvent() + " | URL - " + a.getURL());
                                urlList.add(a.getURL());
                            }
                        }
                    }
                }
            }
        }catch (Exception e){
            Clog.e(Clog.vastLogTag, "Exception processing the tracking urls " + e.getMessage());
        }
        return urlList;
    }


    /**
     * Returns a list of VAST event URLs
     *
     * @return
     */
    public static List<String> getVastClickURLList(AdModel vastAd) {
        ArrayList<String> urlList = new ArrayList<String>();
        try {
            if (vastAd != null && vastAd.getCreativesArrayList() != null && vastAd.getCreativesArrayList().size()> 0) {
                for (CreativeModel creativeModel : vastAd.getCreativesArrayList()) {
                    LinearAdModel linearAdModel = creativeModel.getLinearAdModel();
                    if (linearAdModel != null) {
                        for (VideoClickModel videoClickModel : linearAdModel.getVideoClicksArrayList()) {
                            if (videoClickModel != null) {
                                for (ClickTrackingModel clickTrackingModel : videoClickModel.getClickTrackingArrayList()) {
                                    if (clickTrackingModel.getURL() != null) {
                                        urlList.add(clickTrackingModel.getURL());
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            Clog.e(Clog.vastLogTag, "Error processing vast click url: " + e.getMessage());
        }
        return urlList;
    }


    public static class MediaFileComparator implements Comparator<MediaFileModel> {

        @Override
        public int compare(MediaFileModel lhs, MediaFileModel rhs) {
            int widthDifference = Integer.parseInt(lhs.getWidth())
                    - Integer.parseInt(rhs.getWidth());
            if(widthDifference == 0){
                return Integer.parseInt(lhs.getBitrate()) -Integer.parseInt(rhs.getBitrate());
            }
            return widthDifference;
        }

    }


    public static String getSkipOffsetFromConfiguration(VastVideoConfiguration adSlotConfiguration, double videoLength) {
        int videoLengthInSecs = (int) Math.round((videoLength / 1000));

        if (adSlotConfiguration.getSkipOffset() < 0 && adSlotConfiguration.getSkipOffset() != VastVideoUtil.DEFAULT_SKIP_OFFSET) {
            Clog.i(Clog.vastLogTag, "Skip Offset is less than 0. Setting the default value as 0 seconds");
            adSlotConfiguration.setSkipOffset(0, adSlotConfiguration.getSkipOffsetType() == VastVideoConfiguration.SKIP_OFFSET_TYPE.RELATIVE);
        }

        if ((adSlotConfiguration.getSkipOffsetType() == VastVideoConfiguration.SKIP_OFFSET_TYPE.RELATIVE && adSlotConfiguration.getSkipOffset() > 100)
                || (adSlotConfiguration.getSkipOffsetType() == VastVideoConfiguration.SKIP_OFFSET_TYPE.ABSOLUTE && adSlotConfiguration.getSkipOffset() > videoLengthInSecs)) {
            Clog.i(Clog.vastLogTag,"Skip Offset is greater than video length. Setting the total video length as skip offset");
            return null;
        }

        if (adSlotConfiguration.getSkipOffset() >= 0) {
            String skipOffset = String.valueOf(adSlotConfiguration.getSkipOffset());
            if (adSlotConfiguration.getSkipOffsetType() == VastVideoConfiguration.SKIP_OFFSET_TYPE.RELATIVE) {
                skipOffset = skipOffset+"%";
            }
            return skipOffset;
        }
        return null;
    }


    public static long calculateSkipOffset(String parsedSkipOffset, VastVideoConfiguration videoConfiguration, double videoLength) {

        int skipOffsetValue;
        if (parsedSkipOffset == null) {
            parsedSkipOffset = getSkipOffsetFromConfiguration(videoConfiguration, videoLength);
            Clog.i(Clog.vastLogTag, "Skip Offset from configuration: " + parsedSkipOffset);
        }

        if (!StringUtil.isEmpty(parsedSkipOffset)) {
            if (parsedSkipOffset.contains("%")) {
                float skipPercentage = (Float.valueOf(parsedSkipOffset.substring(0,
                        parsedSkipOffset.length() - 1)) / 100);
                skipOffsetValue = (int) (skipPercentage * Math.round((videoLength / 1000)));
                Clog.i(Clog.vastLogTag, "Relative skipOffsetValue: " + skipOffsetValue);
            } else {
                double skipOffset = Double.parseDouble(parsedSkipOffset);
                skipOffsetValue = (int) skipOffset;
                Clog.i(Clog.vastLogTag, "Absolute skipOffsetValue: " + skipOffsetValue);
            }

        } else {
            Clog.i(Clog.vastLogTag, "skipOffset default value for this video: " + videoLength);
            return (long)videoLength;
        }
        return skipOffsetValue * 1000;
    }

}
