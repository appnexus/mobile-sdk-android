package com.appnexus.opensdk;

import java.io.IOException;
import java.io.InputStream;

import com.appnexus.opensdk.R;
import android.content.res.Resources;

public class MRAIDTests {
	
	static public String getMoPubTestJs(Resources r){
		InputStream ins = r.openRawResource(R.raw.mopub_test);
		try {
			byte[] buffer = new byte[ins.available()];
			ins.read(buffer);
			return new String(buffer, "UTF-8");
		} catch (IOException e) {
			return null;
		}	
	}

}
