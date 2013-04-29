package com.example.opensdkdemo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class MainTabFragment extends Fragment {
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View out = inflater.inflate(R.layout.fragment_control, null);
		
		Spinner sizes = (Spinner ) out.findViewById(R.id.size_dropdown);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(container.getContext(), R.array.sizes, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		sizes.setAdapter(adapter);
		
		Spinner refresh = (Spinner ) out.findViewById(R.id.refresh_dropdown);
		ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(container.getContext(), R.array.refresh, android.R.layout.simple_spinner_item);
		adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		refresh.setAdapter(adapter2);
		return out;
	}

}
