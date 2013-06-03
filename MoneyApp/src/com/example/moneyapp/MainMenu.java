package com.example.moneyapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

public class MainMenu extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_menu);
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
		Intent intent = new Intent(MainMenu.this, Messages.class);
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
