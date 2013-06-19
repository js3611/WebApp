package com.example.moneyapp;

import com.example.moneyapp.setting.SettingFragment;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;

public class Settings extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		getFragmentManager()
			.beginTransaction()
			.replace(android.R.id.content, new SettingFragment())
			.commit();
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.settings, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			//NavUtils.navigateUpFromSameTask(this);
			Intent intent = getIntent().setClass(this,MainMenu.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
			return true;		
			}
		return super.onOptionsItemSelected(item);
	}

}
