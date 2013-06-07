package com.example.moneyapp.transaction;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.moneyapp.R;

/* Fragment */
public class ProfilePageFragment extends Fragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	public static ProfilePageFragment create() {
		ProfilePageFragment fragment = new ProfilePageFragment();
		return fragment;
	}

	public ProfilePageFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		ViewGroup rootView;

		// Set the title view to show the page number.
		rootView = (ViewGroup) inflater.inflate(R.layout.fragment_profile_view,
				container, false);
		// profile
		((TextView) rootView.findViewById(android.R.id.text1))
				.setText("Profile");

		//Dummy profile
		((TextView) rootView.findViewById(R.id.price_view))
		.setText("30");
		((TextView) rootView.findViewById(R.id.profile_view))
		.setText("Tserence");
		
		

		
		return rootView;
	}

}
