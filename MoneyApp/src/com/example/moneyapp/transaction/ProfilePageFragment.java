package com.example.moneyapp.transaction;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.moneyapp.R;

public 
class ProfilePageFragment extends Fragment {

	public static final String ARG_PAGE = "page";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	public static ProfilePageFragment create() {
		ProfilePageFragment fragment = new ProfilePageFragment();
		return fragment;
	}
	
	public ProfilePageFragment() {}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		ViewGroup rootView;

		// Set the title view to show the page number.
		rootView = (ViewGroup) inflater.inflate(
				R.layout.fragment_profile_view, container, false);
		// profile
		((TextView) rootView.findViewById(android.R.id.text1))
				.setText("Profile");

		return rootView;
	}

}
