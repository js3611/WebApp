package com.example.moneyapp;

import com.example.moneyapp.message.MessageListActivity;
import com.example.moneyapp.transaction.Transactions;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

public class MainMenu extends Activity {

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Intent intent = getIntent();
		setContentView(R.layout.activity_main_menu);
		
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
		Intent intent = new Intent(MainMenu.this, Calendar.class);
		startActivity(intent);
	}
	
	public void toTransactions(View view){
		Intent intent = new Intent(MainMenu.this, Transactions.class);
		startActivity(intent);
	}
	
	public void toMessages(View view){
		Intent intent = new Intent(MainMenu.this, MessageListActivity.class);
		startActivity(intent);
	}
	
	public void toWishList(View view){
		Intent intent = new Intent(MainMenu.this, WishList.class);
		startActivity(intent);
	}
	
	public void toSettings(View view){
		Intent intent = new Intent(MainMenu.this, Settings.class);
		startActivity(intent);
	}
	
	
}
