package com.example.opensdkdemo;

import com.appnexus.opensdk.utils.Clog;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class DebugTabFragment extends Fragment {

	TextView request;
	TextView response;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		super.onCreateView(inflater, container, savedInstanceState);
		View out = inflater.inflate(R.layout.fragment_debug, null);
		
		
		request = (TextView) out.findViewById(R.id.request_text);
		response = (TextView) out.findViewById(R.id.response_text);
		//onCreate stuff here
		
		return out;
	}
	
	protected void refresh(){
		request.setText(Clog.getLastRequest());
		response.setText(Clog.getLastResponse());
	}
}
