package com.example.moneyapp;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class PerPersonLog extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_per_person_log);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.per_person_log, menu);
		return true;
	}

}
