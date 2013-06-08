package com.example.moneyapp.transaction;

import java.util.ArrayList;

import com.example.moneyapp.R;
import com.example.moneyapp.R.layout;
import com.example.moneyapp.R.menu;

import android.os.Bundle;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class LogPageFragment extends Fragment {

	//The List view
	ListView logList;
	//A list of data for each entry, which the adapter retrieves from.
	ArrayList<TransactionDetail> details;
	
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
		rootView = (ViewGroup) inflater.inflate(R.layout.transaction_fragment_log_view,
				container, false);
		// profile
		((TextView) rootView.findViewById(R.id.log_title)).setText("Log Transaction");
		logList = (ListView) rootView.findViewById(R.id.log_list);

		//Create a list which holds data for each entry
		details = new ArrayList<TransactionDetail>();
		//Fill the screen with dummy entries
		details = PerPerson.addDummies(details);

		logList.setAdapter(new PerItemAdapter(details, getActivity()));

		registerForContextMenu(logList);

		logList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> a, View v, int pos, long id) {

				// TextView tv = (TextView) v.findViewById(R.id.From);
				// String s = tv.getText().toString();

				//startActivity(new Intent(this, PerItem.class));

			}
		});
		
		return rootView;
	}

}
