package com.example.moneyapp.transaction;

import com.example.moneyapp.R;
import com.example.moneyapp.R.layout;
import com.example.moneyapp.R.menu;

import android.os.Bundle;
import android.app.Activity;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class LogPageFragment extends Fragment {

	public static LogPageFragment create() {
		LogPageFragment fragment = new LogPageFragment();
		return fragment;
	}

	public LogPageFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		ViewGroup rootView;

		// Set the title view to show the page number.
		rootView = (ViewGroup) inflater.inflate(R.layout.fragment_log_view,
				container, false);
		// profile
		((TextView) rootView.findViewById(android.R.id.text1)).setText("Log");
		
		((TextView) rootView.findViewById(R.id.price_view2))
		.setText("Under construction.... \n Hopefully table goes here....:)");

		return rootView;
	}

}
