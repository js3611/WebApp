package com.example.moneyapp.transaction;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
		rootView = (ViewGroup) inflater.inflate(R.layout.transaction_fragment_profile_view,
				container, false);
		
		//Fill in details
		Bundle extras = getActivity().getIntent().getExtras();
		if (extras != null) {
			String name = extras.getString(Transactions.NAME_STR);
			double price = extras.getDouble(Transactions.PRICE_STR);
			int icon = extras.getInt(Transactions.ICON_STR);
			((ImageView) rootView.findViewById(R.id.big_profile_icon)).setImageResource(icon);
			((TextView) rootView.findViewById(R.id.firstName)).setText(name);
			((TextView) rootView.findViewById(R.id.surname)).setText(name);
			((TextView) rootView.findViewById(R.id.price)).setText("£"+Math.abs(price));
			((TextView) rootView.findViewById(R.id.oweDirection)).setText(setDirection(price,name));
			
			
		}
		
		return rootView;
	}

	private CharSequence setDirection(double price, String name) {
		if (price < 0) {
			return "You owe "+name +": ";
		} else {
			return name +" owes you: ";
		}
	}

}
