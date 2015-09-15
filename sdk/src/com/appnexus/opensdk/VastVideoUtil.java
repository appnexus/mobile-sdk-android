package com.appnexus.opensdk;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.appnexus.opensdk.utils.Clog;
import com.appnexus.opensdk.vastdata.AdModel;
import com.appnexus.opensdk.vastdata.ClickTrackingModel;
import com.appnexus.opensdk.vastdata.CreativeModel;
import com.appnexus.opensdk.vastdata.LinearAdModel;
import com.appnexus.opensdk.vastdata.MediaFileModel;
import com.appnexus.opensdk.vastdata.TrackingModel;
import com.appnexus.opensdk.vastdata.VideoClickModel;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class VastVideoUtil {


    private static String TAG = "VastVideoUtil";
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
    public static final String VAST_READMEDIAFILES_HIGHT_ATTR = "height";
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

    // VAST JSON response String
    public static final String VAST_JSON_AD = "Ad";
    public static final String VAST_JSON_FILEURL = "FileUrl";
    public static final String VAST_JSON_POSITION = "Pos";
    public static final String VAST_JSON_IMPRESSIONURL = "ImpUrl";
    public static final String VAST_JSON_CLICKURL = "ClkUrl";
    public static final String VAST_JSON_H = "H";
    public static final String VAST_JSON_W = "W";
    public static final String VAST_JSON_ADTYPE = "AdType";
    public static final String VAST_JSON_TEXT = "Text";

    public static final String EVENT_FIRST_QUARTILE = "firstQuartile";
    public static final String EVENT_MIDPOINT = "midpoint";
    public static final String EVENT_THIRD_QUARTILE = "thirdQuartile";
    public static final String EVENT_COMPLETE = "complete";

    public static final String EVENT_MUTE = "mute";
    public static final String EVENT_UNMUTE = "unmute";
    public static final String EVENT_PAUSE = "pause";
    public static final String EVENT_RESUME= "resume";
    public static final String EVENT_FULLSCREEN = "fullscreen";
    public static final String EVENT_EXIT_FULLSCREEN = "exitFullscreen";
    public static final String EVENT_REWIND = "rewind";
    public static final String EVENT_START = "start";
    public static final String EVENT_CLICK_TRACKING = "ClickTracking";
    public static final String EVENT_CLICK_THROUGH ="ClickThrough";
    public static final String EVENT_CLOSE ="closeLinear";
    public static final String EVENT_SKIP ="skip";
    public static final String EVENT_IMPRESSION ="impression";


    //R constants for Video
    public static final int VIDEO_PAUSE =10001;
    public static final int VIDEO_MUTE=10002;
    public static final int VIDEO_FULLSCREEN=10003;
    public static final int VIDEO_FFWD=10004;
    public static final int VIDEO_REW=10005;
    public static final int VIDEO_NEXT=10006;
    public static final int VIDEO_PREV=10007;
    public static final int VIDEO_MEDIACONTROLLER_PROGRESS=10008;
    public static final int VIDEO_TIME_CURRENT=10009;
    public static final int VIDEO_TIME=10010;
    public static final int VIDEO_SKIP=10011;
    public static final int VIDEO_VIEW=10012;
    public static final int VIDEO_COUNTDOWN_TIMER=10013;
    public static final int VIDEO_CONTROLLER=10014;

    public static final int DEFAULT_SKIP_OFFSET = -9999;

    public static final String STRING_BANNER_IMAGE_NULL = "Banner image is null";
    public static final String STRING_DOMAIN_NOT_SET = "Domain name is not set.";
    public static final String STRING_URL_NOT_SET = "URL is null or blank.";
    public static final String STRING_MEDIA_CONTROLS ="Media Controls";

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
     * @param frameWidth
     * @return
     */
    public static String getVASTVideoURL(ArrayList<MediaFileModel> arrayList, int frameWidth) {
        String mediaUrl = "";

        int count = arrayList.size();
        ArrayList<MediaFileModel> mp4Videos = new ArrayList<MediaFileModel>();

        for (int i = 0; i < count; i++) {
            mediaUrl = arrayList.get(i).getUrl();
            String extension = VastVideoUtil.getExtensionFromUrl(mediaUrl);
            if (extension.startsWith("mp4") || extension.startsWith("MP4")) {
                mp4Videos.add(arrayList.get(i));
                // return mediaUrl;
            }
        }

        count = mp4Videos.size();
        if (count == 0) {
            return mediaUrl;
        } else if (count == 1) {
            mediaUrl = mp4Videos.get(0).getUrl();
            return mediaUrl;
        }

        try {
            int lastObjwidth = 0;
            int currentObjWidth = 0;
            int index;

            Collections.sort(mp4Videos, new MediaFileComparator());
            for (index = 0; index < count; index++) {
                currentObjWidth = Integer.parseInt(mp4Videos.get(index).getWidth());
                if (currentObjWidth > frameWidth) {
                    break;
                }
                lastObjwidth = currentObjWidth;
            }

            if (count == index) {
                index -= 1;
                mediaUrl = mp4Videos.get(index).getUrl();
            } else if (lastObjwidth != 0 && frameWidth == lastObjwidth) {
                mediaUrl = mp4Videos.get(index - 1).getUrl();
                currentObjWidth = lastObjwidth;
            } else {
                mediaUrl = mp4Videos.get(index).getUrl();
            }
            Log.d(TAG, "Rendition Selected- using player width:" + frameWidth + ", selected rendition width:" + currentObjWidth + ", URL:" + mediaUrl);
        } catch (Exception e) {
            return mediaUrl;
        }

        return mediaUrl;
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
                                Log.d("", "TRACKING EVENT - " + a.getEvent() + " | URL - " + a.getURL());
                                urlList.add(a.getURL());
                            }
                        }
                    }
                }
            }
        }catch (Exception e){
            Log.e("Error","Exception processing the tracking urls "+e.getMessage());
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
                    if (creativeModel != null) {
                        for (VideoClickModel videoClickModel : linearAdModel.getVideoClicksArrayList()) {
                            if (videoClickModel != null) {
                                for (ClickTrackingModel clickTrackingModel : videoClickModel.getClickTrackingArrayList()) {
                                    Log.d("", "Video Click Tracking url - " + clickTrackingModel.getURL());
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
            Log.e(TAG, "Error processing vast click url: " + e.getMessage());
        }


        return urlList;
    }

    /**
     * Checks whether a string is null or not
     *
     * @param input
     * @return
     */
    public static boolean isNullOrEmpty(String input) {
        if ((input == null) || (input.equals(""))) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * Return the locale string respective to device language.
     * @param textId
     * @return
     */
    /*
	public static String getLocal(String textId) {
		String locale = Locale.getDefault().toString();

		String displayText = null;
		String localeTextId = textId.concat("_");
		localeTextId = localeTextId.concat(locale);

		XLocales locales = new XLocales();
		Field filedVal;
		try {
			filedVal = locales.getClass().getField(localeTextId);
			displayText = (String) filedVal.get(locales);

		} catch (NoSuchFieldException e) {
			localeTextId = textId.concat("_en_US");
			try {
				filedVal = locales.getClass().getField(localeTextId);
				displayText = (String) filedVal.get(locales);
			} catch (IllegalAccessException e1) {
				e1.printStackTrace();
			} catch (NoSuchFieldException e1) {
				e1.printStackTrace();
			}
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}

		return displayText;
	}*/


    /**
     *
     * @param adSlotConfig
     * @return
     */
    public static LayoutParams getTimerPosition(VastVideoConfiguration adSlotConfig) {
        LayoutParams params = new LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);

        switch (adSlotConfig.getCountdownLabelPosition()) {
            case TOP_RIGHT:
                //Top-right
                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                break;

            case TOP_LEFT:
                //Top-Left
                params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                break;

            case TOP_CENTER:
                //Top-center
                params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                params.addRule(RelativeLayout.CENTER_HORIZONTAL);
                break;

            case BOTTOM_RIGHT:
                //Bottom-right
                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                break;

            case BOTTOM_LEFT:
                //Bottom-Left
                params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                break;

            case BOTTOM_CENTER:
                //Bottom-center
                params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                params.addRule(RelativeLayout.CENTER_HORIZONTAL);
                break;

            default:
                //Top-right
                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                break;
        }

        return params;
    }


    /**
     * Converts the time duration in MM:SS format
     *
     * @param aDuration
     * @return
     */
    public static String convertIntToHHSS(int aDuration) {
        String result = "";

        int hours = 0, minutes = 0, seconds = 0;

        hours = aDuration / 3600;
        minutes = (aDuration - hours * 3600) / 60;
        seconds = (aDuration - (hours * 3600 + minutes * 60));

        result = String.format("%02d:%02d", minutes, seconds);
        return result;
    }

    public static class MediaFileComparator implements Comparator<MediaFileModel> {

        @Override
        public int compare(MediaFileModel lhs, MediaFileModel rhs) {
            return Integer.parseInt(lhs.getWidth())
                    - Integer.parseInt(rhs.getWidth());
        }

    }

    /**
     * Returns the value in pixels.
     *
     * @param context
     * @param sizeInDP
     * @return
     */
    public static int getPixelSize(Context context, int sizeInDP) {
        float scale = getDensity(context);
        int pixelSize = (int) (sizeInDP / scale);
        return pixelSize;
    }

    /**
     * Returns the value according to device's density pixels.
     *
     * @param context
     * @param pixelSize
     * @return
     */
    public static int getSizeInDP(Context context, int pixelSize) {
        float scale = getDensity(context);
        int sizeInDP = (int) (pixelSize * scale);
        return sizeInDP;
    }

    /**
     * Get screen density in dots per inch
     *
     * @return screen density in dots per inch
     */
    public static float getDensity(Context context) {
        return getMetrics(context).density;
    }


    @SuppressLint("NewApi")
    private static DisplayMetrics getMetrics(Context context) {
        DisplayMetrics metrics = new DisplayMetrics();
        int API_LEVEL =  Build.VERSION.SDK_INT;
        if (API_LEVEL >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getRealMetrics(
                    metrics);
        }else{
            ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(
                    metrics);
        }
        return metrics;

    }

    /**
     * Get screen height in pixels
     *
     * @return screen height in pixels
     */
    public static int getScreenWidth(Context context) {
        int width = 0;
        if (context instanceof Activity) {
            View content = ((Activity)context).getWindow().findViewById(Window.ID_ANDROID_CONTENT);
            width = content.getWidth();
        }
        if(width == 0){
            width = getMetrics(context).widthPixels;
        }
        return width;
    }

    /**
     * Get screen width in pixels
     *
     * @return screen width in pixels
     */
    @SuppressLint("NewApi")
    public static int getScreenHeight(Context context, boolean isInterstitial) {
        int height = 0;
        View content = null;
        if (context instanceof Activity) {
            content = ((Activity)context).getWindow().findViewById(Window.ID_ANDROID_CONTENT);
            height = content.getHeight();
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1) {
                height = height - getStatusBarHeight(context);
            }
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    if (isInterstitial && (((Activity)context).getActionBar() != null)) {
                        final TypedArray styledAttributes = context.getTheme().obtainStyledAttributes(
                                new int[] { android.R.attr.actionBarSize });
                        int actionBarHeight = (int) styledAttributes.getDimension(0, 0);
                        styledAttributes.recycle();
                        Log.d(TAG, "actionbar height: "+actionBarHeight);
                        height = height + actionBarHeight;
                    }
                }
            } catch (Exception e) {
                Log.w(TAG, "Couldn't factor actionbar height: "+e.getMessage());
            }


        }

        if(height == 0){
            height = getMetrics(context).heightPixels - getStatusBarHeight(context);
        }
        return height;
    }


    /**
     *
     * @param context
     * @return
     */
    private static int getStatusBarHeight(Context context) {
        int LOW_DPI_STATUS_BAR_HEIGHT = 19;
        int MEDIUM_DPI_STATUS_BAR_HEIGHT = 25;
        int HIGH_DPI_STATUS_BAR_HEIGHT = 38;
        int XHIGH_DPI_STATUS_BAR_HEIGHT = 50;
        int XXHIGH_DPI_STATUS_BAR_HEIGHT = 75;
        int XXXHIGH_DPI_STATUS_BAR_HEIGHT = 100;

        int statusBarHeight = XXHIGH_DPI_STATUS_BAR_HEIGHT;

        switch (getDensityDpi(context)) {
            case DisplayMetrics.DENSITY_XXXHIGH:
                statusBarHeight = XXXHIGH_DPI_STATUS_BAR_HEIGHT;
                break;
            case DisplayMetrics.DENSITY_XXHIGH:
                statusBarHeight = XXHIGH_DPI_STATUS_BAR_HEIGHT;
                break;
            case DisplayMetrics.DENSITY_XHIGH:
                statusBarHeight = XHIGH_DPI_STATUS_BAR_HEIGHT;
                break;
            case DisplayMetrics.DENSITY_HIGH:
                statusBarHeight = HIGH_DPI_STATUS_BAR_HEIGHT;
                break;
            case DisplayMetrics.DENSITY_MEDIUM:
                statusBarHeight = MEDIUM_DPI_STATUS_BAR_HEIGHT;
                break;
            case DisplayMetrics.DENSITY_LOW:
                statusBarHeight = LOW_DPI_STATUS_BAR_HEIGHT;
                break;
            default:
                statusBarHeight = MEDIUM_DPI_STATUS_BAR_HEIGHT;
        }

        Log.d(TAG, "Status Bar height "+statusBarHeight);
        return statusBarHeight;
    }

    /**
     * Get screen density (hdpi, mdpi, ldpi)
     *
     * @return android screen density
     */
    public static int getDensityDpi(Context context) {
        return getMetrics(context).densityDpi;
    }


    public static InputStream getVastResponse() {

        try {
            //http://www.lotusfest.org/wp-content/uploads/2015/03/2014-Lotus-Festival-Video-Sample-One.mp4

            String vastResponse = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" +
                    "<VAST xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"vast2.xsd\" version=\"3.0\">\n" +
                    "  <Ad id=\"VAST Ad - Linear Video\">\n" +
                    "    <InLine>\n" +
                    "      <AdSystem><![CDATA[OAS]]></AdSystem>\n" +
                    "      <AdTitle><![CDATA[vast3_ManuallRenditions_vast]]></AdTitle>\n" +
                    "      <Impression><![CDATA[http://oasc-training7.247realmedia.com/sdk/sdktest/@Bottomss]]></Impression>\n" +
                    "      <Creatives>\n" +
                    "        <Creative>\n" +
                    "          <Linear skipOffset=\"00:00:05\">\n" +
                    "\t\t  <Icons></Icons>\n" +
                    "            <Duration>00:00:30</Duration>\n" +
                    "            <TrackingEvents>\n" +
                    "            </TrackingEvents>\n" +
                    "            <VideoClicks>\n" +
                    "              <ClickTracking><![CDATA[http://oasc-training7.247realmedia.com/sdk/sdktest/@Bottomss]]></ClickTracking>\n" +
                    "              <ClickThrough><![CDATA[http://www.appnexus.com]]></ClickThrough>\n" +
                    "            </VideoClicks>\n" +
                    "            <MediaFiles>\t\t\t  \n" +
                    "\t\t\t  <MediaFile delivery=\"progressive\" bitrate=\"500\" width=\"720\" height=\"480\" type=\"video/mp4\" scalable=\"true\" maintainAspectRatio=\"true\">\n" +
                    "\t\t\t\thttp://imageceu1.247realmedia.com/0/EU_Client/Eurosport_vast_campaign/keywest_tourism_boating.mp4\n" +
                    "              </MediaFile>\n" +
                    "            </MediaFiles>\n" +
                    "          </Linear>\n" +
                    "        </Creative>\n" +
                    "      </Creatives>\n" +
                    "    </InLine>\n" +
                    "  </Ad>\n" +
                    "</VAST>";

            InputStream stream = new ByteArrayInputStream(vastResponse.getBytes(StandardCharsets.UTF_8));


            return stream;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    public static String getSkipOffsetFromConfiguration(VastVideoConfiguration adSlotConfiguration, double videoLength) {
        int videoLengthInSecs = (int) Math.round((videoLength / 1000));

        if (adSlotConfiguration.getSkipOffset() < 0 && adSlotConfiguration.getSkipOffset() != VastVideoUtil.DEFAULT_SKIP_OFFSET) {
            Clog.i(TAG, "Skip Offset is less than 0. Setting the default value as 0 seconds");
            adSlotConfiguration.setSkipOffset(0, adSlotConfiguration.getSkipOffsetType());
        }

        if ((adSlotConfiguration.getSkipOffsetType() == VastVideoConfiguration.SKIP_OFFSET_TYPE.RELATIVE && adSlotConfiguration.getSkipOffset() > 100)
                || (adSlotConfiguration.getSkipOffsetType() == VastVideoConfiguration.SKIP_OFFSET_TYPE.ABSOLUTE && adSlotConfiguration.getSkipOffset() > videoLengthInSecs)) {
            Clog.i(TAG, "Skip Offset is greater than video length. Setting the total video length as skip offset");
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


    public static int calculateSkipOffset(String parsedSkipOffset, VastVideoConfiguration videoConfiguration, double videoLength) {

        Clog.d(TAG, "Parsed Skip Offset: " + parsedSkipOffset);
        float SKIP_OFFSET = 1.1f;
        int skipOffsetValue;
        if (parsedSkipOffset == null) {
            parsedSkipOffset = getSkipOffsetFromConfiguration(videoConfiguration, videoLength);
            Clog.d(TAG, "Skip Offset from configuration: " + parsedSkipOffset);
        }

        if (!VastVideoUtil.isNullOrEmpty(parsedSkipOffset)) {
            if (parsedSkipOffset.contains("%")) {
                SKIP_OFFSET = (Float.valueOf(parsedSkipOffset.substring(0,
                        parsedSkipOffset.length() - 1)) / 100);
                skipOffsetValue = (int) (SKIP_OFFSET * Math.round((videoLength / 1000)));
                Clog.d(TAG, "Relative skipOffsetValue: " + skipOffsetValue);
            } else {
                double skipOffset = Double.parseDouble(parsedSkipOffset);
                skipOffsetValue = (int) skipOffset;
                Clog.d(TAG, "Absolute skipOffsetValue: " + skipOffsetValue);
            }

        } else {
            skipOffsetValue = (int) Math.round((videoLength / 1000));
            Clog.d(TAG, "skipOffset default value for this video: " + skipOffsetValue);
        }

        return skipOffsetValue;
    }

}
