package com.example.moneyapp;

import java.util.ArrayList;

import com.example.moneyapp.transaction.PerItemAdapter;
import com.example.moneyapp.transaction.PerPerson;
import com.example.moneyapp.transaction.PersonAdapter;
import com.example.moneyapp.transaction.TransactionDetail;

import android.os.Bundle;
import android.app.Activity;
import android.util.Pair;
import android.view.Menu;
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

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.new_transaction, menu);
		return true;
	}

}
