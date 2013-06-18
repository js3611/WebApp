package com.example.moneyapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;

import com.example.helpers.metadata.UserDetails;
import com.example.moneyapp.friend.Friend;
import com.example.moneyapp.message.MessageListActivity;
import com.example.moneyapp.transaction.PerItem;
import com.example.moneyapp.transaction.PerPerson;

public class MainMenu extends Activity {

	public static final String TAG = "MainMenu";
	public static final int PER_PERSON_VIEW = 1;
	public static final int PER_TRANSACTION_VIEW = 2;
	
	private UserDetails user;
	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_menu);
		getActionBar().setDisplayHomeAsUpEnabled(false);

		/* Get user data passed */
		Intent intent = getIntent();
		user = UserDetails.getUser(intent);
		Log.v(TAG, user.toString());			
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // SDK must be over HONEYCOMB for the calendar to show up
            getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_menu, menu);
		return true;
	}

	public void toCalendar(View view){
		Intent intent = getIntent();
		intent.setClass(MainMenu.this, Calendar.class);
		startActivity(intent);
	}
	
	public void toTransactions(View view){
		
		SharedPreferences sharedPref = this.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
		int view_mode = sharedPref.getInt(getString(R.string.view_mode), PER_PERSON_VIEW);
		Intent intent = getIntent();
		//intent.putExtra(MainActivity.USER_KEY, user);
		view_mode = PER_TRANSACTION_VIEW;
		if (view_mode == PER_PERSON_VIEW) 
			intent.setClass(MainMenu.this, PerPerson.class);
		else 
			intent.setClass(MainMenu.this, PerItem.class);
		
		startActivity(intent);
	}
	
	public void toMessages(View view){
		Intent intent = getIntent();
		intent.setClass(MainMenu.this, MessageListActivity.class);
		startActivity(intent);
	}
	
	public void toWishList(View view){
		Intent intent = getIntent();
		intent.setClass(MainMenu.this, WishList.class);
		startActivity(intent);
	}
	
	public void toSettings(View view){
		Intent intent = getIntent();
		intent.setClass(MainMenu.this, Settings.class);
		startActivity(intent);
	}
	
	public void toFriend(View view){
		Intent intent = getIntent();
		intent.setClass(MainMenu.this, Friend.class);
		startActivity(intent);
	}
	
	
}
