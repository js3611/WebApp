package com.example.moneyapp;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class GroupProfile extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_group_profile);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.group_profile, menu);
		return true;
	}

}
