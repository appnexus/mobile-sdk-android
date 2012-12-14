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

/**
 * @author jacob
 *
 */
public class AdRequest extends AsyncTask<AdRequestParams, Integer, Ad> implements Runnable {

	AdRequestParams mParams;
	/**
	 * 
	 */
	public AdRequest(AdRequestParams params) {
		mParams=params;
	}

	/**
	 * @see android.os.AsyncTask#doInBackground(Params[])
	 */
	@Override
	protected Ad doInBackground(AdRequestParams... params) {
		AdRequestParams p = params[0];
		String query_string = p.toString();
		DefaultHttpClient h = new DefaultHttpClient();
		HttpResponse r=null;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			r=h.execute(new HttpGet(query_string));
			r.getEntity().writeTo(out);
			out.close();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return new Ad(out.toString());
	}
	
	@Override
	protected void onPostExecute(Ad result){
		
	}

	@Override
	public void run() {
		execute(mParams);
	}

}
