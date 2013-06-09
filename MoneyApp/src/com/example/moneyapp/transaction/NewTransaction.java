package com.example.moneyapp.transaction;

import java.util.ArrayList;

import com.example.moneyapp.R;
import com.example.moneyapp.R.id;
import com.example.moneyapp.R.layout;
import com.example.moneyapp.R.menu;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Pair;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class NewTransaction extends Activity {

	//The List view
	ListView personList;
	//A list of data for each entry, which the adapter retrieves from.
	ArrayList<Pair<String, Double>> person_cost_pairs;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.transaction_new_transaction);
		
		personList = (ListView) findViewById(R.id.PersonList);
		person_cost_pairs = new ArrayList<Pair<String,Double>>();
		//Fill the screen with dummy entries

		personList.setAdapter(new PersonAdapter(person_cost_pairs, this));
		registerForContextMenu(personList);
		
		personList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> a, View v, int pos,
					long id) {
				if (selectedNewTransaction(pos)) {
					startActivity(new Intent(NewTransaction.this, NewPerson.class));
				}				

			}

			private boolean selectedNewTransaction(int pos) {
				
				return person_cost_pairs.size() == pos;
			}
			
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.new_transaction, menu);
		return true;
	}

}
