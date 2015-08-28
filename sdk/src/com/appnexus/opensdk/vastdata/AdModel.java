package com.appnexus.opensdk.vastdata;

import java.util.ArrayList;

public class AdModel {
	private String adSystem;
	private String adTitle;
	private String description;
	private String advertiser;
	private String pricing;
	private String survey;
	private String error;
	private ArrayList<String> impressionArrayList;
	private ArrayList<CreativeModel> creativesArrayList = new ArrayList<CreativeModel>();

	/**
	 * @return the adSystem
	 */
	public String getAdSystem() {
		return adSystem;
	}

	/**
	 * @param adSystem
	 *            the adSystem to set
	 */
	public void setAdSystem(String adSystem) {
		this.adSystem = adSystem;
	}

	/**
	 * @return the adTitle
	 */
	public String getAdTitle() {
		return adTitle;
	}

	/**
	 * @param adTitle
	 *            the adTitle to set
	 */
	public void setAdTitle(String adTitle) {
		this.adTitle = adTitle;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	
			
	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the advertiser
	 */
	public String getAdvertiser() {
		return advertiser;
	}

	/**
	 * @param advertiser
	 *            the advertiser to set
	 */
	public void setAdvertiser(String advertiser) {
		this.advertiser = advertiser;
	}

	/**
	 * @return the pricing
	 */
	public String getPricing() {
		return pricing;
	}

	/**
	 * @param pricing
	 *            the pricing to set
	 */
	public void setPricing(String pricing) {
		this.pricing = pricing;
	}

	/**
	 * @return the survey
	 */
	public String getSurvey() {
		return survey;
	}

	/**
	 * @param survey
	 *            the survey to set
	 */
	public void setSurvey(String survey) {
		this.survey = survey;
	}

	/**
	 * @return the error
	 */
	public String getError() {
		return error;
	}

	/**
	 * @param error
	 *            the error to set
	 */
	public void setError(String error) {
		this.error = error;
	}



	/**
	 * @return the creativesArrayList
	 */
	public ArrayList<CreativeModel> getCreativesArrayList() {
		return creativesArrayList;
	}

	/**
	 * @param creativesArrayList
	 *            the creativesArrayList to set
	 */
	public void setCreativesArrayList(ArrayList<CreativeModel> creativesArrayList) {
		this.creativesArrayList = creativesArrayList;
	}

	public ArrayList<String> getImpressionArrayList() {
		if (impressionArrayList == null){
			impressionArrayList = new ArrayList<String>();
		}
		return impressionArrayList;
	}

	public void setImpressionArrayList(ArrayList<String> impressionArrayList) {
		this.impressionArrayList = impressionArrayList;
	}
	
	

}
