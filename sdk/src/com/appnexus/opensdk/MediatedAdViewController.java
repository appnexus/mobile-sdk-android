package com.appnexus.opensdk;

import android.view.View;

public class MediatedAdViewController implements Displayable {
	AdView owner;
	int width;
	int height;
	String uid;
	String className;
	
	Class c;

	public MediatedAdViewController(AdView owner, AdResponse response) {
		width = response.getWidth();
		height = response.getHeight();
		uid = response.getMediatedUID();
		className = response.getMediatedViewClassName();
		
		try {
			c = Class.forName(className);
		} catch (ClassNotFoundException e) {
			//TODO error message
		}
	}

	@Override
	public View getView() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean failed() {
		// TODO Auto-generated method stub
		return false;
	}

}
