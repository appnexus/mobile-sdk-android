package com.appnexus.opensdk;

import java.util.ArrayList;

/**
 * Represents transparency information for Digital Services Act (DSA).
 * This class encapsulates an array of objects representing entities that applied user parameters
 * along with the parameters they applied.
 *
 * <p>Example usage:</p>
 * <pre>
 * {@code
 * ANDSATransparencyInfo transparencyInfo = new ANDSATransparencyInfo("example.com", new ArrayList<>(Arrays.asList(1, 2, 3)));
 * // Retrieve transparency information
 * String domain = transparencyInfo.getDomain();
 * ArrayList<Integer> dsaParams = transparencyInfo.getDSAParams();
 * }
 * </pre>
 */
public class ANDSATransparencyInfo {
    private String domain;
    private ArrayList<Integer> dsaParams;

    /**
     * Constructs an ANDSATransparencyInfo instance with the specified domain and dsaParams.
     *
     * @param domain The domain of the entity that applied user parameters.
     * @param dsaParams The list of user parameters used for the platform or sell-side.
     */
    public ANDSATransparencyInfo(String domain, ArrayList<Integer> dsaParams) {
        this.domain = domain;
        this.dsaParams = dsaParams;
    }

    /**
     * Retrieves the transparency user parameters, i.e., the domain of the entity that applied user parameters.
     *
     * @return The domain.
     */
    public String getDomain() {
        return domain;
    }

    /**
     * Sets the transparency user parameters, i.e., the domain.
     *
     * @param domain The domain to be set.
     */
    public void setDomain(String domain) {
        this.domain = domain;
    }

    /**
     * Retrieves the transparency user parameters, i.e., the list of user parameters used for the platform or sell-side.
     *
     * @return The list of user parameters.
     */
    public ArrayList<Integer> getDSAParams() {
        return dsaParams;
    }

    /**
     * Sets the transparency user parameters, i.e., the list of user parameters used for the platform or sell-side.
     *
     * @param dsaParams The list of user parameters to be set.
     */
    public void setDSAParams(ArrayList<Integer> dsaParams) {
        this.dsaParams = dsaParams;
    }
}