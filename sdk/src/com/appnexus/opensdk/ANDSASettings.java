package com.appnexus.opensdk;

import java.util.ArrayList;

/**
 * This class encapsulates the settings related to the Digital Services Act (DSA)
 * and transparency information required for ad rendering.
 *
 * <p>Example Usage:</p>
 * <pre>{@code
 * // Create an instance of ANDSASettings
 * ANDSASettings.setDSARequired(1);
 * ANDSASettings.setPubRender(0);
 * // ANDSASettings.setDataToPub(1);
 *
 * // Create a list of transparency information
 * ArrayList<ANDSATransparencyInfo> transparencyList = new ArrayList<>();
 * transparencyList.add(new ANDSATransparencyInfo("example.com", new ArrayList<>(Arrays.asList(1, 2, 3))));
 * transparencyList.add(new ANDSATransparencyInfo("example.net", new ArrayList<>(Arrays.asList(4, 5, 6))));
 *
 * // Set the transparency list in ANDSASettings
 * ANDSASettings.setTransparencyList(transparencyList);
 * }</pre>
 */
public class ANDSASettings {
    private static int dsaRequired = -1;
    private static int pubRender = -1;
    private static int dataToPub = -1;
    private static ArrayList<ANDSATransparencyInfo> transparencyList = new ArrayList<>();

    /**
     * Retrieve the DSA information requirement.
     *
     * @return The DSA information requirement value.
     */
    public static int getDSARequired() {
        return dsaRequired;
    }

    /**
     * Set the DSA information requirement.
     * 0 = Not required
     * 1 = Supported, bid responses with or without DSA object will be accepted
     * 2 = Required, bid responses without DSA object will not be accepted
     * 3 = Required, bid responses without DSA object will not be accepted, Publisher is an Online Platform
     *
     * @param dsaRequired The DSA information requirement value to be set.
     */
    public static void setDSARequired(int dsaRequired) {
        ANDSASettings.dsaRequired = dsaRequired;
    }

    /**
     * Retrieve if the publisher renders the DSA transparency info.
     *
     * @return The value indicating whether the publisher renders DSA transparency info.
     */
    public static int getPubRender() {
        return pubRender;
    }

    /**
     * Set if the publisher renders the DSA transparency info.
     * 0 = Publisher can't render
     * 1 = Publisher could render depending on adrender
     * 2 = Publisher will render
     *
     * @param pubRender The value indicating whether the publisher renders DSA transparency info.
     */
    public static void setPubRender(int pubRender) {
        ANDSASettings.pubRender = pubRender;
    }

    /**
     * Retrieve the transparency data if needed for audit purposes.
     *
     * @return The transparency data value.
     */
    public static int getDataToPub() {
        return dataToPub;
    }

    // Publisher app should not be setting dataToPub value. This is not supported in /ut/v3
    // This is here only for Testing purpose
    /**
     * Set the transparency data if needed for audit purposes.
     * 0 = do not send transparency data
     * 1 = optional to send transparency data
     * 2 = send transparency data
     *
     * @param dataToPub The transparency data value to be set.
     */
    public static void setDataToPub(int dataToPub) {
        ANDSASettings.dataToPub = dataToPub;
    }

    /**
     * Retrieve the transparency user parameters info.
     *
     * @return The list of ANDSATransparencyInfo containing transparency information.
     */
    public static ArrayList<ANDSATransparencyInfo> getTransparencyList() {
        return transparencyList;
    }

    /**
     * Set the transparency list using the provided list of ANDSATransparencyInfo.
     *
     * @param transparencyList The list of ANDSATransparencyInfo to be set.
     */
    public static void setTransparencyList(ArrayList<ANDSATransparencyInfo> transparencyList) {
        ANDSASettings.transparencyList = transparencyList;
    }
}