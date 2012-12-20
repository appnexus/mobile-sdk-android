/**
 * 
 */
package com.appnexus.opensdk;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.AsyncTask;
import android.util.Log;

/**
 * @author jacob
 * 
 */
public class AdRequest extends AsyncTask<AdRequestParams, Integer, Ad> {
	AdWebView mAdWebView;
	AdRequestParams mParams;

	/**
	 * 
	 */
	public AdRequest(AdRequestParams params, AdWebView owner) {
		mParams = params;
		mAdWebView = owner;
	}

	/**
	 * @see android.os.AsyncTask#doInBackground(Params[])
	 */
	@Override
	protected Ad doInBackground(AdRequestParams... params) {
		AdRequestParams p = params.length == 0 ? mParams : params[0];
		String query_string = p.toString();
		Log.d("OPENSDK", "fetching: " + query_string);
		DefaultHttpClient h = new DefaultHttpClient();
		HttpResponse r = null;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			r = h.execute(new HttpGet(query_string));
			r.getEntity().writeTo(out);
			out.close();
		} catch (ClientProtocolException e) {
			Log.w("OPENSDK",
					"Couldn't reach the ad server... check your internet connection");
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return new Ad(out.toString(), r.getAllHeaders());
	}

	@Override
	protected void onPostExecute(Ad result) {
		if (result != null)
			mAdWebView.loadAd(result);
	}

}
