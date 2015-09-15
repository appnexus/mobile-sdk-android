package com.appnexus.opensdk;

import android.util.Xml;

import com.appnexus.opensdk.utils.Clog;
import com.appnexus.opensdk.vastdata.AdModel;
import com.appnexus.opensdk.vastdata.ClickTrackingModel;
import com.appnexus.opensdk.vastdata.CompanionAdModel;
import com.appnexus.opensdk.vastdata.CompanionClickTrackingModel;
import com.appnexus.opensdk.vastdata.CreativeModel;
import com.appnexus.opensdk.vastdata.LinearAdModel;
import com.appnexus.opensdk.vastdata.MediaFileModel;
import com.appnexus.opensdk.vastdata.NonLinearAdModel;
import com.appnexus.opensdk.vastdata.NonLinearClickTrackingModel;
import com.appnexus.opensdk.vastdata.ResourceModel;
import com.appnexus.opensdk.vastdata.TrackingModel;
import com.appnexus.opensdk.vastdata.VideoClickModel;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.security.KeyStore;
import java.util.ArrayList;

public class VastResponseParser {

	private String TAG = getClass().getSimpleName();

	private int skipOffset;

	private String impressionTrackerUrl;

	private String duration;

    private AdModel vastAd = new AdModel();


    /**
     * Method to read VAST InputStream
     * @param data response inputstream
     * @throws XmlPullParserException
     * @throws IOException
     * @throws Exception
     */
	public AdModel readVAST(InputStream data) throws XmlPullParserException,
			IOException, Exception {

        Clog.i(TAG, "--- Parsing starts ---");

		Clog.d(TAG, "Start reading VAST xml tag");
		XmlPullParser parser = Xml.newPullParser();
		parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
		parser.setInput(data, "UTF-8");
		parser.nextTag();
        parser.require(XmlPullParser.START_TAG, null, VastVideoUtil.VAST_START_TAG);
		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			if (parser.getName().equals(VastVideoUtil.VAST_AD_TAG)) {
				readAd(parser);
			}
		}

        return vastAd;
	}

	/**
	 * Method to read ad to identify Inline and wrapper.
	 * 
	 * @param p parser object
	 * @throws IOException
	 * @throws XmlPullParserException
	 */
	private void readAd(XmlPullParser p) throws IOException,
			XmlPullParserException, Exception {
		p.require(XmlPullParser.START_TAG, null, VastVideoUtil.VAST_AD_TAG);
		while (p.next() != XmlPullParser.END_TAG) {
			if (p.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = p.getName();
			if (name.equals(VastVideoUtil.VAST_INLINE_TAG)) {
				Clog.i(TAG,
                        "VAST file contains inline ad information.");
				readInLine(p);
			}
			if (name.equals(VastVideoUtil.VAST_WRAPPER_TAG)) {
				Clog.i(TAG,
                        "VAST file contains wrapped ad information.");
				readWrapper(p);
			}
		}
	}

	/**
	 * Method to read and parse Media files.
	 * 
	 * @param p parser object
	 * @return ArrayList
	 * @throws IOException
	 * @throws XmlPullParserException
	 */
	private ArrayList<MediaFileModel> readMediaFiles(XmlPullParser p)
			throws IOException, XmlPullParserException {
		p.require(XmlPullParser.START_TAG, null, VastVideoUtil.VAST_MEDIAFILES_TAG);
		ArrayList<MediaFileModel> mediaFileList = new ArrayList<MediaFileModel>();
		while (p.next() != XmlPullParser.END_TAG) {
			if (p.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = p.getName();
			if (name != null && name.equals(VastVideoUtil.VAST_MEDIAFILE_TAG)) {
				p.require(XmlPullParser.START_TAG, null,
						VastVideoUtil.VAST_MEDIAFILE_TAG);
				MediaFileModel mediaFileModel = new MediaFileModel();
				String id = p.getAttributeValue(null,
						VastVideoUtil.VAST_READMEDIAFILES_ID_ATTR);
				String delivery = p.getAttributeValue(null,
						VastVideoUtil.VAST_READMEDIAFILES_DELIVERY_ATTR);
				String type = p.getAttributeValue(null,
						VastVideoUtil.VAST_READMEDIAFILES_TYPE_ATTR);
				String bitrate = p.getAttributeValue(null,
						VastVideoUtil.VAST_READMEDIAFILES_BITRATE_ATTR);
				String minBitrate = p.getAttributeValue(null,
						VastVideoUtil.VAST_READMEDIAFILES_MINBITRATE_ATTR);
				String maxBitrate = p.getAttributeValue(null,
						VastVideoUtil.VAST_READMEDIAFILES_MAXBITRATE_ATTR);
				String width = p.getAttributeValue(null,
						VastVideoUtil.VAST_READMEDIAFILES_WIDTH_ATTR);
				String height = p.getAttributeValue(null,
						VastVideoUtil.VAST_READMEDIAFILES_HIGHT_ATTR);
				String scalable = p.getAttributeValue(null,
						VastVideoUtil.VAST_READMEDIAFILES_SCALABLE_ATTR);
				String maintainAspectRatio = p.getAttributeValue(null,
						VastVideoUtil.VAST_READMEDIAFILES_MAINTAINASPECTRATIO_ATTR);
				String codec = p.getAttributeValue(null,
						VastVideoUtil.VAST_READMEDIAFILES_CODEC_ATTR);
				String apiFramework = p.getAttributeValue(null,
						VastVideoUtil.VAST_READMEDIAFILES_APIFRAMEWORK_ATTR);

				mediaFileModel.setUrl(readText(p));
				mediaFileModel.setId(id);
				mediaFileModel.setDelivery(delivery);
				mediaFileModel.setType(type);
				mediaFileModel.setBitrate(bitrate);
				mediaFileModel.setMinBitrate(minBitrate);
				mediaFileModel.setMaxBitrate(maxBitrate);
				mediaFileModel.setWidth(width);
				mediaFileModel.setHeight(height);
				mediaFileModel.setScalable(scalable);
				mediaFileModel.setMaintainAspectRatio(maintainAspectRatio);
				mediaFileModel.setCodec(codec);
				mediaFileModel.setApiFramework(apiFramework);

				mediaFileList.add(mediaFileModel);
				p.require(XmlPullParser.END_TAG, null,
						VastVideoUtil.VAST_MEDIAFILE_TAG);
				Clog.i(TAG, "Mediafile url: " + mediaFileModel.getUrl());
			} else {
				skip(p);
			}
		}
		return mediaFileList;
	}

	/**
	 * Method to read and parse tracking events.
	 * 
	 * @param p parser object
	 * @return ArrayList
	 * @throws IOException
	 * @throws XmlPullParserException
	 */
	private ArrayList<TrackingModel> readTrackingEvents(XmlPullParser p)
			throws IOException, XmlPullParserException {
		p.require(XmlPullParser.START_TAG, null,
				VastVideoUtil.VAST_TRACKINGEVENTS_TAG);
		ArrayList<TrackingModel> trackingList = new ArrayList<TrackingModel>();
		while (p.next() != XmlPullParser.END_TAG) {
			if (p.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = p.getName();
			if (name != null && name.equals(VastVideoUtil.VAST_TRACKING_TAG)) {
				String ev = p.getAttributeValue(null, "event");
				p.require(XmlPullParser.START_TAG, null,
						VastVideoUtil.VAST_TRACKING_TAG);
				TrackingModel trackingModel = new TrackingModel();
				trackingModel.setEvent(ev);
				trackingModel.setURL(readText(p));
				trackingList.add(trackingModel);
				Clog.d(TAG, "Added VAST tracking \"" + ev + "\"");
				p.require(XmlPullParser.END_TAG, null,
						VastVideoUtil.VAST_TRACKING_TAG);
			} else {
				skip(p);
			}
		}
		return trackingList;
	}

	/**
	 * Method to read and parse companion click data.
	 * 
	 * @param p parser object
	 * @return
	 * @throws IOException
	 * @throws XmlPullParserException
	 */
	@SuppressWarnings("unused")
	private ArrayList<CompanionClickTrackingModel> readCompanionClicks(
			XmlPullParser p) throws IOException, XmlPullParserException {
		p.require(XmlPullParser.START_TAG, null, VastVideoUtil.VAST_VIDEOCLICKS_TAG);

		ArrayList<CompanionClickTrackingModel> clickTrackingList = new ArrayList<CompanionClickTrackingModel>();

		while (p.next() != XmlPullParser.END_TAG) {
			if (p.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = p.getName();

			if (name != null && name.equals(VastVideoUtil.VAST_CLICKTRACKING_TAG)) {

				p.require(XmlPullParser.START_TAG, null,
						VastVideoUtil.VAST_CLICKTRACKING_TAG);

				CompanionClickTrackingModel clickTrackingModel = new CompanionClickTrackingModel();
				String id = p.getAttributeValue(null, "id");
				clickTrackingModel.setId(id);
				clickTrackingModel.setURL(readText(p));
				Clog.d(TAG, "Companion click tracking url: "
                        + clickTrackingModel.getURL() + "  ---- "
                        + clickTrackingModel.getId());
				p.require(XmlPullParser.END_TAG, null,
						VastVideoUtil.VAST_CLICKTRACKING_TAG);
				clickTrackingList.add(clickTrackingModel);

			} else {
				skip(p);
			}
		}

		Clog.d(TAG,
                "clickTrackingList: " + clickTrackingList.size());
		return clickTrackingList;
	}

	/**
	 * Method to read and parse nonLinearAdClick data.
	 * 
	 * @param p parser object
	 * @return ArrayList
	 * @throws IOException
	 * @throws XmlPullParserException
	 */
	@SuppressWarnings("unused")
	private ArrayList<NonLinearClickTrackingModel> readNonLinearClicks(XmlPullParser p) throws IOException, XmlPullParserException {
		p.require(XmlPullParser.START_TAG, null, VastVideoUtil.VAST_VIDEOCLICKS_TAG);

		ArrayList<NonLinearClickTrackingModel> clickTrackingList = new ArrayList<NonLinearClickTrackingModel>();

		while (p.next() != XmlPullParser.END_TAG) {
			if (p.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = p.getName();

			if (name != null && name.equals(VastVideoUtil.VAST_CLICKTRACKING_TAG)) {

				p.require(XmlPullParser.START_TAG, null,
						VastVideoUtil.VAST_CLICKTRACKING_TAG);

				NonLinearClickTrackingModel clickTrackingModel = new NonLinearClickTrackingModel();
				String id = p.getAttributeValue(null, "id");
				/* this.clickTrackingUrl = readText(p); */
				clickTrackingModel.setId(id);
				clickTrackingModel.setURL(readText(p));
				p.require(XmlPullParser.END_TAG, null,
						VastVideoUtil.VAST_CLICKTRACKING_TAG);
				clickTrackingList.add(clickTrackingModel);

			} else {
				skip(p);
			}
		}

		Clog.d(TAG,
                "clickTrackingList: " + clickTrackingList.size());
		return clickTrackingList;
	}

	/**
	 * method to read and parse Video click data.
	 * 
	 * @param p parser object
	 * @return VideoClickModel
	 * @throws IOException
	 * @throws XmlPullParserException
	 */
	private VideoClickModel readVideoClicks(XmlPullParser p)
			throws IOException, XmlPullParserException {
		p.require(XmlPullParser.START_TAG, null, VastVideoUtil.VAST_VIDEOCLICKS_TAG);

		VideoClickModel videoClickModel = new VideoClickModel();
		ArrayList<ClickTrackingModel> clickTrackingList = new ArrayList<ClickTrackingModel>();

		while (p.next() != XmlPullParser.END_TAG) {
			if (p.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = p.getName();

			if (name != null && name.equals(VastVideoUtil.VAST_CLICKTHROUGH_TAG)) {
				p.require(XmlPullParser.START_TAG, null,
						VastVideoUtil.VAST_CLICKTHROUGH_TAG);
				/* this.clickThroughUrl = readText(p); */
				videoClickModel.setClickThroughURL(readText(p));
				Clog.d(TAG, "Parsed video clickthrough url: "
                        + videoClickModel.getClickThroughURL());
				p.require(XmlPullParser.END_TAG, null,
						VastVideoUtil.VAST_CLICKTHROUGH_TAG);

			} else if (name != null
					&& name.equals(VastVideoUtil.VAST_CLICKTRACKING_TAG)) {

				p.require(XmlPullParser.START_TAG, null,
						VastVideoUtil.VAST_CLICKTRACKING_TAG);

				ClickTrackingModel clickTrackingModel = new ClickTrackingModel();
				String id = p.getAttributeValue(null, "id");
				/* this.clickTrackingUrl = readText(p); */
				clickTrackingModel.setId(id);
				clickTrackingModel.setURL(readText(p));
				Clog.d(TAG, "Parsed video clicktracking url: "
                        + clickTrackingModel.getURL() + "  - "
                        + clickTrackingModel.getId());
				p.require(XmlPullParser.END_TAG, null,
						VastVideoUtil.VAST_CLICKTRACKING_TAG);
				clickTrackingList.add(clickTrackingModel);

				videoClickModel.setClickTrackingArrayList(clickTrackingList);

			} else {
				skip(p);
			}
		}

		Clog.d(
                TAG,
                "clickTrackingList: "
                        + videoClickModel.getClickTrackingArrayList());
		return videoClickModel;
	}

	/**
	 * Method to read and parse LinearAd data.
	 * 
	 * @param p parser object
	 * @param creativeModel creative ad object
	 * @throws IOException
	 * @throws XmlPullParserException
	 */
	private void readLinear(XmlPullParser p, CreativeModel creativeModel)
			throws IOException, XmlPullParserException {
		p.require(XmlPullParser.START_TAG, null, VastVideoUtil.VAST_LINEAR_TAG);
		LinearAdModel linearAdModel = new LinearAdModel();
		ArrayList<VideoClickModel> videoClicks = new ArrayList<VideoClickModel>();

		String skipoffsetStr = p.getAttributeValue(null, "skipOffset");
		if (skipoffsetStr != null && skipoffsetStr.indexOf(":") < 0) {
			Clog.d(TAG,
                    "Relative skip offset present. Ad would be skippable after x amount of time");
			linearAdModel.setSkipOffset(skipoffsetStr);
		} else if (skipoffsetStr != null && skipoffsetStr.indexOf(":") >= 0) {
			skipOffset = VastVideoUtil.convertStringtoSeconds(skipoffsetStr);
			// skipOffset = -1;
			linearAdModel.setSkipOffset(String.valueOf(skipOffset));

			Clog.d(TAG, "Absolute skipOffset present.");
		} else {
			Clog.d(TAG, "No skip offset present. Ad would not be skippable");
		}
		
		while (p.next() != XmlPullParser.END_TAG) {
			String name = p.getName();
			if (p.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}

			if (name != null && name.equals(VastVideoUtil.VAST_DURATION_TAG)) {
				p.require(XmlPullParser.START_TAG, null,
						VastVideoUtil.VAST_DURATION_TAG);
				this.duration = readText(p);
				p.require(XmlPullParser.END_TAG, null,
						VastVideoUtil.VAST_DURATION_TAG);

				Clog.d(TAG, "Video duration:- " + this.duration);
				linearAdModel.setDuration(this.duration);

			} else if (name != null
					&& name.equals(VastVideoUtil.VAST_TRACKINGEVENTS_TAG)) {
				/* readTrackingEvents(p); */
				linearAdModel.setTrackingEventArrayList(readTrackingEvents(p));
			} else if (name != null
					&& name.equals(VastVideoUtil.VAST_MEDIAFILES_TAG)) {
				/* readMediaFiles(p); */
				linearAdModel.setMediaFilesArrayList(readMediaFiles(p));
			} else if (name != null
					&& name.equals(VastVideoUtil.VAST_VIDEOCLICKS_TAG)) {
				videoClicks.add(readVideoClicks(p));
			} else {
				skip(p);
			}
		}
		linearAdModel.setVideoClicksArrayList(videoClicks);
		if (p.getEventType() == XmlPullParser.END_TAG) {
			String name = p.getName();
			if (name != null && name.equals(VastVideoUtil.VAST_LINEAR_TAG)) {

				if (linearAdModel.getTrackingEventArrayList() != null) {
					Clog.d(TAG, "getTrackingEventArrayList "
                            + linearAdModel.getTrackingEventArrayList().size());
				} else {
					Clog.d(TAG, "<TrackingEvents> tag is not present.");
					linearAdModel
							.setTrackingEventArrayList(new ArrayList<TrackingModel>());
				}

				if (linearAdModel.getMediaFilesArrayList() != null) {
					Clog.d(TAG, "getMediaFilesArrayList : " + linearAdModel.getMediaFilesArrayList().size());
				} else {
					Clog.d(TAG, "<MediaFiles> tag is not present.");
					linearAdModel.setMediaFilesArrayList(new ArrayList<MediaFileModel>());
				}

				creativeModel.setLinearAdModel(linearAdModel);

                vastAd.getCreativesArrayList().add(creativeModel);
			}

		}

	}

	/**
	 * Method to read and parse companion ad data.
	 * 
	 * @param p parser object
	 * @return CompanionAdModel
	 * @throws IOException
	 * @throws XmlPullParserException
	 */
	private CompanionAdModel readCompanion(XmlPullParser p) throws IOException,
			XmlPullParserException {
		p.require(XmlPullParser.START_TAG, null, VastVideoUtil.VAST_COMPANION_TAG);
		CompanionAdModel companionAdModel = new CompanionAdModel();
		ArrayList<CompanionClickTrackingModel> companionClickTrackingList = new ArrayList<CompanionClickTrackingModel>();
		while (p.next() != XmlPullParser.END_TAG) {
			if (p.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = p.getName();
			String id = p.getAttributeValue(null,
					VastVideoUtil.VAST_READCOMPANION_ID_ATTR);
			String width = p.getAttributeValue(null,
					VastVideoUtil.VAST_READCOMPANION_WIDTH_ATTR);
			String height = p.getAttributeValue(null,
					VastVideoUtil.VAST_READCOMPANION_HEIGHT_ATTR);
			String assetWidth = p.getAttributeValue(null,
					VastVideoUtil.VAST_READCOMPANION_ASSETWIDTH_ATTR);
			String assetHeight = p.getAttributeValue(null,
					VastVideoUtil.VAST_READCOMPANION_ASSETHIGHT_ATTR);
			String expandedWidth = p.getAttributeValue(null,
					VastVideoUtil.VAST_READCOMPANION_EXPANDEDWIDTH_ATTR);
			String expandedHeight = p.getAttributeValue(null,
					VastVideoUtil.VAST_READCOMPANION_EXPANDEDHIGHT_ATTR);
			String apiFramework = p.getAttributeValue(null,
					VastVideoUtil.VAST_READCOMPANION_APIFRAMEWORK_ATTR);
			String adSlotID = p.getAttributeValue(null,
					VastVideoUtil.VAST_READCOMPANION_ADSLOT_ATTR);

			companionAdModel.setId(id);
			companionAdModel.setWidth(width);
			companionAdModel.setHeight(height);
			companionAdModel.setAssetWidth(assetWidth);
			companionAdModel.setAssetHeight(assetHeight);
			companionAdModel.setExpandedWidth(expandedWidth);
			companionAdModel.setExpandedHeight(expandedHeight);
			companionAdModel.setApiFramework(apiFramework);
			companionAdModel.setAdSlotId(adSlotID);

			if (name != null && name.equals(VastVideoUtil.VAST_STATICRESOURCE_TAG)) {
				createResourceModel(companionAdModel);
				String creativeType = p.getAttributeValue(null, "creativeType");
				companionAdModel.getResource().setCreativeType(creativeType);
				companionAdModel.getResource().setStaticResource(readText(p));
			} else if (name != null
					&& name.equals(VastVideoUtil.VAST_IFRAMERESOURCE_TAG)) {
				createResourceModel(companionAdModel);
				companionAdModel.getResource().setiFrameResource(readText(p));

			} else if (name != null
					&& name.equals(VastVideoUtil.VAST_HTMLRESOURCE_TAG)) {
				createResourceModel(companionAdModel);
				companionAdModel.getResource().setHtmlResource(readText(p));
			} else if (name != null
					&& name.equals(VastVideoUtil.VAST_ADPARAMETERS_TAG)) {
				companionAdModel.setAdParameters(readText(p));
			} else if (name != null && name.equals(VastVideoUtil.VAST_ALTTEXT_TAG)) {
				companionAdModel.setAltText(readText(p));
			} else if (name != null
					&& name.equals(VastVideoUtil.VAST_COMPANIONCLICKTHROUGH_TAG)) {
				companionAdModel.setCampanionClickThroughURL(readText(p));
			} else if (name != null
					&& name.equals(VastVideoUtil.VAST_TRACKINGEVENTS_TAG)) {
				companionAdModel.setTrackingArrayList(readTrackingEvents(p));
			} else if (name != null
					&& name.equals(VastVideoUtil.VAST_COMPANIONCLICKTRACKING_TAG)) {
				CompanionClickTrackingModel companionClickTrackingModel = new CompanionClickTrackingModel();
				companionClickTrackingModel.setURL(readText(p));
				companionClickTrackingModel.setId(p.getAttributeValue(null,
						"id"));
				companionClickTrackingList.add(companionClickTrackingModel);
			} else {
				skip(p);
			}

		}
		companionAdModel
				.setCompanionClickTrackingArrayList(companionClickTrackingList);
		return companionAdModel;

	}

	/**
	 * Method to read and parse NonLinearAd data.
	 * 
	 * @param p parser object
	 * @return NonLinearAdModel
	 * @throws IOException
	 * @throws XmlPullParserException
	 */
	private NonLinearAdModel readNonLinear(XmlPullParser p) throws IOException,
			XmlPullParserException {
		p.require(XmlPullParser.START_TAG, null, VastVideoUtil.VAST_NONLINEAR_TAG);
		NonLinearAdModel nonLinearAdModel = new NonLinearAdModel();
		ArrayList<NonLinearClickTrackingModel> nonLinearClickTrackingList = new ArrayList<NonLinearClickTrackingModel>();
		while (p.next() != XmlPullParser.END_TAG) {
			if (p.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}

			String name = p.getName();
			String id = p.getAttributeValue(null,
					VastVideoUtil.VAST_READNONLINEAR_ID_ATTR);
			String width = p.getAttributeValue(null,
					VastVideoUtil.VAST_READNONLINEAR_WIDTH_ATTR);
			String height = p.getAttributeValue(null,
					VastVideoUtil.VAST_READNONLINEAR_HEIGHT_ATTR);
			String scalable = p.getAttributeValue(null,
					VastVideoUtil.VAST_READNONLINEAR_SCALABLE_ATTR);
			String maintainAspectRatio = p.getAttributeValue(null,
					VastVideoUtil.VAST_READNONLINEAR_MAINTAINASPECTRATIO_ATTR);
			String expandedWidth = p.getAttributeValue(null,
					VastVideoUtil.VAST_READNONLINEAR_EXPANDEDWIDTH_ATTR);
			String expandedHeight = p.getAttributeValue(null,
					VastVideoUtil.VAST_READNONLINEAR_EXPANDEDHIGHT_ATTR);
			String apiFramework = p.getAttributeValue(null,
					VastVideoUtil.VAST_READNONLINEAR_APIFRAMEWORK_ATTR);
			String minSuggestedDuration = p.getAttributeValue(null,
					VastVideoUtil.VAST_READNONLINEAR_MINSUGGESTIONDURATION_ATTR);

			nonLinearAdModel.setId(id);
			nonLinearAdModel.setWidth(width);
			nonLinearAdModel.setHeight(height);
			nonLinearAdModel.setScalable(scalable);
			nonLinearAdModel.setMaintainAspectRatio(maintainAspectRatio);
			nonLinearAdModel.setExpandedWidth(expandedWidth);
			nonLinearAdModel.setExpandedHeight(expandedHeight);
			nonLinearAdModel.setApiFramework(apiFramework);
			nonLinearAdModel.setMinSuggestedDuration(minSuggestedDuration);

			if (name != null && name.equals(VastVideoUtil.VAST_STATICRESOURCE_TAG)) {
				createNonLinearResourceModel(nonLinearAdModel);
				String creativeType = p.getAttributeValue(null, "creativeType");
				nonLinearAdModel.getResource().setCreativeType(creativeType);
				nonLinearAdModel.getResource().setStaticResource(readText(p));
			} else if (name != null
					&& name.equals(VastVideoUtil.VAST_IFRAMERESOURCE_TAG)) {
				createNonLinearResourceModel(nonLinearAdModel);
				nonLinearAdModel.getResource().setiFrameResource(readText(p));

			} else if (name != null
					&& name.equals(VastVideoUtil.VAST_HTMLRESOURCE_TAG)) {
				createNonLinearResourceModel(nonLinearAdModel);
				nonLinearAdModel.getResource().setHtmlResource(readText(p));
			} else if (name != null
					&& name.equals(VastVideoUtil.VAST_ADPARAMETERS_TAG)) {
				nonLinearAdModel.setAdParameters(readText(p));
			} else if (name != null
					&& name.equals(VastVideoUtil.VAST_NONLINEARCLICKTHROUGH_TAG)) {
				nonLinearAdModel.setNonLinearClickThroughURL(readText(p));
			} else if (name != null
					&& name.equals(VastVideoUtil.VAST_TRACKINGEVENTS_TAG)) {
				/* readTrackingEvents(p); */
				nonLinearAdModel
						.setTrackingEventArrayList(readTrackingEvents(p));
			} else if (name != null
					&& name.equals(VastVideoUtil.VAST_NONLINEARCLICKTRACKING_TAG)) {
				NonLinearClickTrackingModel nonLinearClickTrackingModel = new NonLinearClickTrackingModel();
				nonLinearClickTrackingModel.setURL(readText(p));
				nonLinearClickTrackingModel.setId(p.getAttributeValue(null,
						"id"));
				nonLinearClickTrackingList.add(nonLinearClickTrackingModel);
			} else {
				skip(p);
			}

		}

		nonLinearAdModel
				.setNonLinearClickTrackingArrayList(nonLinearClickTrackingList);
		return nonLinearAdModel;

	}

	/**
	 * Method to read CompanionAds.
	 * 
	 * @param p parser object
	 * @throws IOException
	 * @throws XmlPullParserException
	 */
	private void readCompanionAds(XmlPullParser p) throws IOException,
			XmlPullParserException {
		p.require(XmlPullParser.START_TAG, null,
				VastVideoUtil.VAST_COMPANIONADS_TAG);
		ArrayList<CompanionAdModel> companionAdList = new ArrayList<CompanionAdModel>();
		CompanionAdModel companionAdModel = new CompanionAdModel();
		while (p.next() != XmlPullParser.END_TAG) {
			String name = p.getName();
			if (p.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}

			if (name != null && name.equals(VastVideoUtil.VAST_COMPANION_TAG)) {
				companionAdModel = readCompanion(p);
			} else {
				skip(p);
			}
			companionAdList.add(companionAdModel);
		}

		Clog.w(TAG, "Companion Ad List " + companionAdList.size());
	}

	/**
	 * method to read and parse nonLinearAds data.
	 * 
	 * @param p parser object
	 * @throws IOException
	 * @throws XmlPullParserException
	 */
	@SuppressWarnings("unused")
	private void readNonLinearAds(XmlPullParser p) throws IOException,
			XmlPullParserException {
		p.require(XmlPullParser.START_TAG, null,
				VastVideoUtil.VAST_NONLINEARADS_TAG);

		ArrayList<NonLinearAdModel> nonLinearAdList = new ArrayList<NonLinearAdModel>();
		NonLinearAdModel nonLinearAdModel = new NonLinearAdModel();
		while (p.next() != XmlPullParser.END_TAG) {
			String name = p.getName();
			if (p.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}

			if (name != null && name.equals(VastVideoUtil.VAST_NONLINEAR_TAG)) {
				nonLinearAdModel = readNonLinear(p);
			} else {
				skip(p);
			}
			nonLinearAdList.add(nonLinearAdModel);
		}

		Clog.w(TAG, "companionAdList........ " + nonLinearAdList.size());
	}

	/**
	 * Method to create and set resource model.
	 * 
	 * @param companionAdModel
	 */
	private void createResourceModel(CompanionAdModel companionAdModel) {
		if (companionAdModel.getResource() == null) {
			ResourceModel resourceModel = new ResourceModel();
			companionAdModel.setResource(resourceModel);
		}
	}

	/**
	 * Method to create and set nonlinear resource model.
	 * 
	 * @param nonLinearAd
	 */
	private void createNonLinearResourceModel(NonLinearAdModel nonLinearAd) {
		if (nonLinearAd.getResource() == null) {
			ResourceModel resourceModel = new ResourceModel();
			nonLinearAd.setResource(resourceModel);
		}
	}

	/**
	 * Method to read and parse Creative data.
	 * 
	 * @param p parser object
	 * @param creativesArrayList
	 * @throws IOException
	 * @throws XmlPullParserException
	 */
	private void readCreative(XmlPullParser p,
			ArrayList<CreativeModel> creativesArrayList) throws IOException,
			XmlPullParserException {
		p.require(XmlPullParser.START_TAG, null, VastVideoUtil.VAST_CREATIVE_TAG);
		CreativeModel creativeModel = new CreativeModel();
		while (p.next() != XmlPullParser.END_TAG) {
			if (p.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = p.getName();
			if (name != null && name.equals(VastVideoUtil.VAST_LINEAR_TAG)) {
				readLinear(p, creativeModel);
			} else if (name != null
					&& name.equals(VastVideoUtil.VAST_COMPANIONADS_TAG)) {
				String skipoffsetStr = p.getAttributeValue(null, "skipOffset");
				if (skipoffsetStr != null && skipoffsetStr.contains(":")) {

					skipOffset = Integer.parseInt(skipoffsetStr.substring(0, skipoffsetStr.length() - 1));
					Clog.d(TAG, "Linear skipoffset is " + skipOffset + " [%]");

				} else if (skipoffsetStr != null && skipoffsetStr.contains(":")) {

					int skipoffsetInSeconds = VastVideoUtil.convertStringtoSeconds(skipoffsetStr);
					Clog.d(TAG, "Skip offset (in seconds) - " + skipoffsetInSeconds);
					// skipOffset = -1;
					skipOffset = skipoffsetInSeconds;
					Clog.w(TAG, "Absolute time value ignored for skipOffset in VAST xml. Only percentage values will pe parsed.");
				}
				readCompanionAds(p);
			} else {
				skip(p);
			}

		}

		if (p.getEventType() == XmlPullParser.END_TAG) {
			String name = p.getName();
			if (name != null && name.equals(VastVideoUtil.VAST_CREATIVE_TAG)) {

				creativesArrayList.add(creativeModel);
				Clog.d(TAG, "CREATIVE TAG ENDED creative list size - " + creativesArrayList.size());
			}

		}
	}

	/**
	 * Method to read and parse Creatives data.
	 * 
	 * @param p parser object
	 * @throws IOException
	 * @throws XmlPullParserException
	 */
	private void readCreatives(XmlPullParser p) throws IOException,
			XmlPullParserException {

		Clog.d(TAG, "Read Creative tag ");

		p.require(XmlPullParser.START_TAG, null, VastVideoUtil.VAST_CREATIVES_TAG);
		ArrayList<CreativeModel> creativesArrayList = new ArrayList<CreativeModel>();

		while (p.next() != XmlPullParser.END_TAG) {
			if (p.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = p.getName();

			if (name != null && name.equals(VastVideoUtil.VAST_CREATIVE_TAG)) {
				readCreative(p, creativesArrayList);
			} else {
				skip(p);
			}
		}
	}

	/**
	 * Method to get wrapped VAST after validating the valid listener.
	 * 
	 * @param p parser object
	 * @throws IOException
	 * @throws XmlPullParserException
	 */
	private void getWrappedVast(XmlPullParser p) throws IOException,
			XmlPullParserException, Exception {
		p.require(XmlPullParser.START_TAG, null, VastVideoUtil.VAST_ADTAGURI_TAG);
		String url = readText(p);
		p.require(XmlPullParser.END_TAG, null, VastVideoUtil.VAST_ADTAGURI_TAG);

		// Fetch the new VAST xml from vast ad tag url and parse it
		loadWrappedVast(url);

	}

	@SuppressWarnings("deprecation")
	private void loadWrappedVast(String url) throws Exception {
		boolean isError = false;
		try {
			DefaultHttpClient httpclient = getNewHttpClient();

			HttpGet httpget = new HttpGet(url);

			HttpResponse response = httpclient.execute(httpget);
			
			Clog.d("", "Fetching Wrapped Vast XML...");
			HttpEntity entity = response.getEntity();
			
			if (entity != null) {
				InputStream instream = entity.getContent();
                Clog.i("", "Fetched Wrapped Vast XML successfully!");
                Clog.d("", "Start reading wrapped Vast XML...");
                readVAST(instream);
				instream.close();
			}else{
				throw new Exception("Error fetching wrapped vast tag - Nil response");
			}

		} catch (MalformedURLException mue) {
			isError = true;
			Clog.e(TAG, "Error fetching wrapped vast tag - Malformed URL exception :\n" + mue.getMessage());
		} catch (ClientProtocolException e) {
			isError = true;
			Clog.e(TAG, "Error fetching wrapped vast tag - Client protocol exception :\n" + e.getMessage());
		} catch (IllegalStateException e) {
			isError = true;
			Clog.e(TAG, "Error fetching wrapped vast tag - IllegalStateException :\n" + e.getMessage());
		} catch (IOException e) {
			isError = true;
			Clog.e(TAG, "Error fetching wrapped vast tag - IO-Exception :\n" + e.getMessage());
		} catch (Exception e) {
			isError = true;
			Clog.e(TAG, "Error fetching wrapped vast tag - Exception :\n" + e.getMessage());
		} 
		if (isError) {
			throw new Exception("Error fetching wrapped VAST ad response");
		}
	}
	
	
	@SuppressWarnings("deprecation")
	private DefaultHttpClient getNewHttpClient() {
	     try {
	         KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
	         trustStore.load(null, null);

	         SSLSocketFactory sf = new ANSSLSocketFactory(trustStore);
	         sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

	         HttpParams params = new BasicHttpParams();
	         HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
	         HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

	         SchemeRegistry registry = new SchemeRegistry();
	         registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
	         registry.register(new Scheme("https", sf, 443));

	         ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);

	         return new DefaultHttpClient(ccm, params);
	     } catch (Exception e) {
	         return new DefaultHttpClient();
	     }
	}

	/**
	 * Method to read and parse wrapper data.
	 * 
	 * @param p
	 * @throws IOException
	 * @throws XmlPullParserException
	 */
	private void readWrapper(XmlPullParser p) throws IOException,
			XmlPullParserException, Exception {
		p.require(XmlPullParser.START_TAG, null, VastVideoUtil.VAST_WRAPPER_TAG);
		while (p.next() != XmlPullParser.END_TAG) {
			if (p.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = p.getName();
			if (name != null && name.equals(VastVideoUtil.VAST_IMPRESSION_TAG)) {
				p.require(XmlPullParser.START_TAG, null,
						VastVideoUtil.VAST_IMPRESSION_TAG);
				this.impressionTrackerUrl = readText(p);
				p.require(XmlPullParser.END_TAG, null,
						VastVideoUtil.VAST_IMPRESSION_TAG);

				Clog.d(TAG, "Wrapper Impression tracker url: "
                        + this.impressionTrackerUrl);

                vastAd.getImpressionArrayList().add(impressionTrackerUrl);
			} else if (name != null
					&& name.equals(VastVideoUtil.VAST_CREATIVES_TAG)) {
				readCreatives(p);
			} else if (name != null && name.equals(VastVideoUtil.VAST_ADTAGURI_TAG)) {
				getWrappedVast(p);
			} else {
				skip(p);
			}
		}
	}

	/**
	 * Method to read and parse Inline data.
	 * 
	 * @param p
	 * @throws IOException
	 * @throws XmlPullParserException
	 */
	private void readInLine(XmlPullParser p) throws IOException,
			XmlPullParserException {
		p.require(XmlPullParser.START_TAG, null, VastVideoUtil.VAST_INLINE_TAG);
		while (p.next() != XmlPullParser.END_TAG) {
			if (p.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}

			String name = p.getName();
			if (name != null && name.equals(VastVideoUtil.VAST_IMPRESSION_TAG)) {
				p.require(XmlPullParser.START_TAG, null,
						VastVideoUtil.VAST_IMPRESSION_TAG);
				this.impressionTrackerUrl = readText(p);
				p.require(XmlPullParser.END_TAG, null,
						VastVideoUtil.VAST_IMPRESSION_TAG);
                vastAd.getImpressionArrayList().add(impressionTrackerUrl);
				Clog.d(TAG, "Impression tracker url: "
                        + this.impressionTrackerUrl);
			} else if (name != null
					&& name.equals(VastVideoUtil.VAST_CREATIVES_TAG)) {
				readCreatives(p);
			} else if (name != null && name.equals(VastVideoUtil.VAST_ADSYSTEM_TAG)) {
                vastAd.setAdSystem(readText(p));
			} else if (name != null && name.equals(VastVideoUtil.VAST_ADTITLE_TAG)) {
                vastAd.setAdTitle(readText(p));
			} else if (name != null && name.equals(VastVideoUtil.VAST_DESCRIPTION_TAG)) {
                vastAd.setDescription(readText(p));
			} else if (name != null && name.equals(VastVideoUtil.VAST_ADVERTISER_TAG)) {
                vastAd.setAdvertiser(readText(p));
			} else if (name != null && name.equals(VastVideoUtil.VAST_PRICING_TAG)) {
                vastAd.setPricing(readText(p));
			} else if (name != null && name.equals(VastVideoUtil.VAST_SURVEY_TAG)) {
                vastAd.setSurvey(readText(p));
			} else if (name != null && name.equals(VastVideoUtil.VAST_ERROR_TAG)) {
                vastAd.setError(readText(p));
			} else {
				skip(p);
			}
			Clog.d(TAG, "Impression tracker url ArrayList: " + vastAd.getImpressionArrayList().size());

		}
	}

	private void skip(XmlPullParser p) throws XmlPullParserException,
			IOException {
		if (p.getEventType() != XmlPullParser.START_TAG) {
			throw new IllegalStateException();
		}
		int depth = 1;
		while (depth != 0) {
			switch (p.next()) {
			case XmlPullParser.END_TAG:
				depth--;
				break;
			case XmlPullParser.START_TAG:
				depth++;
				break;
			}
		}
	}

	private String readText(XmlPullParser parser) throws IOException,
			XmlPullParserException {
		String result = "";
		if (parser.next() == XmlPullParser.TEXT) {
			result = parser.getText();
			parser.nextTag();
		} else {
			Clog.w(TAG, "No text: " + parser.getName());
		}
		return result.trim();
	}
}
