package com.example.moneyapp;

import tests.ListViewLoader;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

public class Transactions extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_transactions);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.transactions, menu);
		return true;
	}

	public void jumpToList(View view) {
		Intent intent= new Intent(this, PerPersonLog.class);
		//		Intent intent= new Intent(this, ExampleListActivity.class);
		startActivity(intent);
	}
	
}
