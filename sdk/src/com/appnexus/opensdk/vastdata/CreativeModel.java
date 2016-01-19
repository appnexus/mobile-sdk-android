package com.appnexus.opensdk.vastdata;

import java.util.ArrayList;

public class CreativeModel {
	
	private String id;
	private String sequence;
	private String adID;
	private String apiFramework;
	private LinearAdModel linearAdModel ;
	private ArrayList<String> creativeExtension;
	private ArrayList<CompanionAdModel> companionAdArrayList;
	private ArrayList<NonLinearAdModel> nonLinearAdArrayList;
	
	
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}
	/**
	 * @return the sequence
	 */
	public String getSequence() {
		return sequence;
	}
	/**
	 * @param sequence the sequence to set
	 */
	public void setSequence(String sequence) {
		this.sequence = sequence;
	}
	/**
	 * @return the adID
	 */
	public String getAdID() {
		return adID;
	}
	/**
	 * @param adID the adID to set
	 */
	public void setAdID(String adID) {
		this.adID = adID;
	}
	/**
	 * @return the apiFramework
	 */
	public String getApiFramework() {
		return apiFramework;
	}
	/**
	 * @param apiFramework the apiFramework to set
	 */
	public void setApiFramework(String apiFramework) {
		this.apiFramework = apiFramework;
	}
	/**
	 * @return the creativeExtension
	 */
	public ArrayList<String> getCreativeExtension() {
		return creativeExtension;
	}
	/**
	 * @param creativeExtension the creativeExtension to set
	 */
	public void setCreativeExtension(ArrayList<String> creativeExtension) {
		this.creativeExtension = creativeExtension;
	}
	
	/**
	 * @return the companionAdArrayList
	 */
	public ArrayList<CompanionAdModel> getCompanionAdArrayList() {
		return companionAdArrayList;
	}
	/**
	 * @param companionAdArrayList the companionAdArrayList to set
	 */
	public void setCompanionAdArrayList(ArrayList<CompanionAdModel> companionAdArrayList) {
		this.companionAdArrayList = companionAdArrayList;
	}
	/**
	 * @return the nonLinearAdArrayList
	 */
	public ArrayList<NonLinearAdModel> getNonLinearAdArrayList() {
		return nonLinearAdArrayList;
	}
	/**
	 * @param nonLinearAdArrayList the nonLinearAdArrayList to set
	 */
	public void setNonLinearAdArrayList(ArrayList<NonLinearAdModel> nonLinearAdArrayList) {
		this.nonLinearAdArrayList = nonLinearAdArrayList;
	}
	/**
	 * @return the linearAdModel
	 */
	public LinearAdModel getLinearAdModel() {
		return linearAdModel;
	}
	/**
	 * @param linearAdModel the linearAdModel to set
	 */
	public void setLinearAdModel(LinearAdModel linearAdModel) {
		this.linearAdModel = linearAdModel;
	}
	

}
