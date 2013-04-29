package com.example.opensdkdemo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class DebugTabFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View out = inflater.inflate(R.layout.fragment_debug, null);
		
		//onCreate stuff here
		
		return out;
	}
}
