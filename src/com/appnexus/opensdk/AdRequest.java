/**
 * 
 */
package com.appnexus.opensdk;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.AsyncTask;
import android.util.Log;

/**
 * @author jacob
 * 
 */
public class AdRequest extends AsyncTask<AdRequestParams, Integer, AdResponse> {

	AdRequestParams mParams;

	/**
	 * 
	 */
	public AdRequest(AdRequestParams params) {
		mParams = params;
	}

	/**
	 * @see android.os.AsyncTask#doInBackground(Params[])
	 */
	@Override
	protected AdResponse doInBackground(AdRequestParams... params) {
		AdRequestParams p = params.length == 0 ? mParams : params[0];
		String query_string = p.toString();
		//String query_string = "http://m.google.com";
		Log.d("OPENSDK", "fetching: " + (query_string+="&tmp_id=2&format=html"));
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
			//TODO
			return null;
		} catch (IOException e) {
			if(e instanceof HttpHostConnectException){
				HttpHostConnectException he = (HttpHostConnectException)e;
				Log.e("OPENSDK", he.getHost().getHostName()+":"+he.getHost().getPort()+" is unreachable.");
			}
			//TODO
			return null;
		}
		return new AdResponse(out.toString(), r.getAllHeaders());
	}

	@Override
	protected void onPostExecute(AdResponse result) {
		// TODO urgently... how does the ad response send a displayable to the adview
	}

}
