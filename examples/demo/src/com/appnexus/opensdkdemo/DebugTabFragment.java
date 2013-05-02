package com.appnexus.opensdkdemo;

import com.appnexus.opensdk.utils.Clog;
import com.appnexus.opensdkdemo.R;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class DebugTabFragment extends Fragment {

	TextView request;
	TextView response;
	Button email;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		super.onCreateView(inflater, container, savedInstanceState);
		final View out = inflater.inflate(R.layout.fragment_debug, null);
		
		
		request = (TextView) out.findViewById(R.id.request_text);
		response = (TextView) out.findViewById(R.id.response_text);
		email = (Button) out.findViewById(R.id.email);
		
		email.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				try{
				Intent emailIntent = new Intent(Intent.ACTION_SEND);
				emailIntent.setType("message/rfc822");
				emailIntent.putExtra(Intent.EXTRA_TEXT, "Request:\n"+Clog.getLastRequest()+"\n\n"+"Response:\n"+Clog.getLastResponse());
				
				
				startActivity(Intent.createChooser(emailIntent, "Select an app with which to send the debug information"));
				} catch (ActivityNotFoundException e){
					Toast.makeText(out.getContext(), "No E-Mail App Installed!", Toast.LENGTH_SHORT).show();
				}
			}
			
		});
		
		return out;
	}
	
	protected void refresh(){
		if(request!=null) request.setText(Clog.getLastRequest());
		if(response!=null) response.setText(Clog.getLastResponse());
	}
}
