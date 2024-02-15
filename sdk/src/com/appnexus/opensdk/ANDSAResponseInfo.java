package com.appnexus.opensdk;

import com.appnexus.opensdk.utils.JsonUtil;
import com.appnexus.opensdk.utils.StringUtil;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;

/**
 * Represents the response information for Digital Services Act (DSA) related data and transparency information.
 * This class includes fields for information such as on whose behalf the ad is displayed, who paid for the ad,
 * ad rendering information, and a list of transparency information.
 *
 * <p>Example Usage:</p>
 * <pre>{@code
 * ANDSAResponseInfo dsaResponseInfo = getAdResponseInfo().getDSAResponseInfo();
 * String behalf = dsaResponseInfo.getBehalf();
 * String paid = dsaResponseInfo.getPaid();
 * ArrayList<ANDSATransparencyInfo> transparencyList = dsaResponseInfo.getTransparencyList();
 * for(int i = 0; i <= transparencyList.size(); i++) {
 *     ANDSATransparencyInfo tranparencyInfo = transparencyList.get(i);
 *     String domain = tranparencyInfo.getDomain();
 *     ArrayList<Integer> params = tranparencyInfo.getDSAParams();
 * }
 *
 * int adRender = dsaResponseInfo.getAdRender();
 * }</pre>
 */
public class ANDSAResponseInfo {
    private String behalf = "";
    private String paid = "";
    int adRender = -1;
    private ArrayList<ANDSATransparencyInfo> transparencyList = new ArrayList<>();
    private static final String RESPONSE_KEY_DSA_BEHALF = "behalf";
    private static final String RESPONSE_KEY_DSA_PAID = "paid";
    private static final String RESPONSE_KEY_DSA_TRANSPARENCY = "transparency";
    private static final String RESPONSE_KEY_DSA_DOMAIN = "domain";
    private static final String RESPONSE_KEY_DSA_PARAMS = "dsaparams";
    private static final String RESPONSE_KEY_DSA_AD_RENDER = "adrender";

    /**
     * Process the dsa response from ad object
     *
     * @param dsaObject JsonObject that contains info of dsa
     * @return ANDSAResponseInfo if no issue happened during processing
     */
    public ANDSAResponseInfo create(JSONObject dsaObject) {
        if (dsaObject == null) {
            return null;
        }

        ANDSAResponseInfo dsaResponseInfo = new ANDSAResponseInfo();
        String behalf = JsonUtil.getJSONString(dsaObject, RESPONSE_KEY_DSA_BEHALF);
        if (!StringUtil.isEmpty(behalf)) {
            dsaResponseInfo.setBehalf(behalf);
        }
        String paid = JsonUtil.getJSONString(dsaObject, RESPONSE_KEY_DSA_PAID);
        if (!StringUtil.isEmpty(paid)) {
            dsaResponseInfo.setPaid(paid);
        }
        JSONArray transparencyArray = JsonUtil.getJSONArray(dsaObject, RESPONSE_KEY_DSA_TRANSPARENCY);
        if (transparencyArray != null) {

            ArrayList<ANDSATransparencyInfo> transparencyList = new ArrayList<>();
            for (int j = 0; j < transparencyArray.length(); j++) {
                JSONObject transparencyObject = transparencyArray.optJSONObject(j);
                if (transparencyObject != null) {
                    String domain = JsonUtil.getJSONString(transparencyObject, RESPONSE_KEY_DSA_DOMAIN);
                    JSONArray paramsArray = JsonUtil.getJSONArray(transparencyObject, RESPONSE_KEY_DSA_PARAMS);

                    ANDSATransparencyInfo transparencyInfo = new ANDSATransparencyInfo(domain, JsonUtil.getIntegerArrayList(paramsArray));
                    transparencyList.add(transparencyInfo);
                }
            }
            dsaResponseInfo.setTransparencyList(transparencyList);
        }
        int adRender = JsonUtil.getJSONInt(dsaObject, RESPONSE_KEY_DSA_AD_RENDER);
        if (adRender >= 0) {
            dsaResponseInfo.setAdRender(adRender);
        }
        return dsaResponseInfo;
    }

    /**
     * Retrieve on whose behalf the ad is displayed.
     *
     * @return The behalf, returns an empty string if not set.
     */
    public String getBehalf() {
        return behalf;
    }

    /**
     * Set on whose behalf the ad is displayed.
     *
     * @param behalf The behalf to be set.
     */
    public void setBehalf(String behalf) {
        this.behalf = behalf;
    }

    /**
     * Retrieve who paid for the ad.
     *
     * @return The paid, returns an empty string if not set.
     */
    public String getPaid() {
        return paid;
    }

    /**
     * Set who paid for the ad.
     *
     * @param paid The paid to be set.
     */
    public void setPaid(String paid) {
        this.paid = paid;
    }

    /**
     * Retrieve indicating if the buyer/advertiser will render DSA transparency info.
     * 0 = buyer/advertiser will not render
     * 1 = buyer/advertiser will render
     *
     * @return The ad render that this ad belongs to.
     */
    public int getAdRender() {
        return adRender;
    }

    /**
     * Set indicating if the buyer/advertiser will render DSA transparency info.
     *
     * @param adRender The ad render value to be set.
     */
    public void setAdRender(int adRender) {
        this.adRender = adRender;
    }

    /**
     * Retrieve the transparency user parameters info
     */
    public ArrayList<ANDSATransparencyInfo> getTransparencyList() {
        return transparencyList;
    }

    /**
     * Set the transparency list using the provided list of ANDSATransparencyInfo.
     *
     * @param transparencyList The list of ANDSATransparencyInfo to be set.
     */
    public void setTransparencyList(ArrayList<ANDSATransparencyInfo> transparencyList) {
        this.transparencyList = transparencyList;
    }
}