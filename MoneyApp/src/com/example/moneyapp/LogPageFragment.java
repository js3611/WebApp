package com.example.moneyapp;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class LogPageFragment extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_log_page_fragment);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.log_page, menu);
		return true;
	}

}
