package com.example.moneyapp.transaction;

import com.example.moneyapp.R;
import com.example.moneyapp.R.layout;
import com.example.moneyapp.R.menu;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class PerPersonLogProfile extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_per_person_log_profile);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.per_person_log_profile, menu);
		return true;
	}

}
