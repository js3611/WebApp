package com.example.moneyapp;

import com.example.helpers.CustomAdapter;

import android.app.Activity;
import android.app.ListActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ExampleListActivity extends ListActivity {
	static final String[] Faces = new String[] { "jo","thai","terence","ella" };

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// no more this
		// setContentView(R.layout.list_fruit);

	 
			setListAdapter(new CustomAdapter(this, Faces));
	 
		}
	 
		@Override
		protected void onListItemClick(ListView l, View v, int position, long id) {
	 
			//get selected items
			String selectedValue = (String) getListAdapter().getItem(position);
			Toast.makeText(this, selectedValue, Toast.LENGTH_SHORT).show();
	 
		}
	 
}
