/*
 *    Copyright 2013 APPNEXUS INC
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

import android.view.View;

public class MediatedBannerAdViewController implements Displayable {
	AdView owner;
	int width;
	int height;
	String uid;
	String className;

	Class<?> c;
	MediatedBannerAdView mAV;

	public MediatedBannerAdViewController(AdView owner, AdResponse response) {
		width = response.getWidth();
		height = response.getHeight();
		uid = response.getMediatedUID();
		className = response.getMediatedViewClassName();

		try {
			c = Class.forName(className);

		} catch (ClassNotFoundException e) {
			// TODO error message
		}

		try {
			mAV = (MediatedBannerAdView) c.newInstance();
		} catch (InstantiationException e) {
			// TODO error message
		} catch (IllegalAccessException e) {
			// TODO error message
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
