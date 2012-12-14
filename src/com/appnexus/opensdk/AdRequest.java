/**
 * 
 */
package com.appnexus.opensdk;

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
		// TODO Auto-generated method stub
		return new Ad();
	}
	
	@Override
	protected void onPostExecute(Ad result){
		
	}

	@Override
	public void run() {
		execute(mParams);
	}

}
