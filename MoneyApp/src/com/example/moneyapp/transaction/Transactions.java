package com.example.moneyapp.transaction;

import com.example.moneyapp.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

public class Transactions extends Activity {

	public final static String NAME_STR = "com.example.moneyapp.transaction.name";
	public final static String PRICE_STR = "com.example.moneyapp.transaction.price";
	public final static String PROFILEPIC_STR = "com.example.moneyapp.transaction.price";
	public final static String USER_STR = "com.example.moneyapp.transaction.userdetails";
	public static final String ICON_STR = "com.example.moneyapp.transaction.icon";
	protected static final String FRIENDID_STR = "com.example.moneyapp.transaction.friendid";
	public static final String FRIEND_STR = "com.example.moneyapp.friend";
	public static final String FRIENDIDS_STR = "com.example.moneyapp.friendids";
	public static final String ON_RETURN_FROM_ADD = "com.example.moneyapp.onReturnFromAddNewPerson";
	protected static final String TRANSID_STR = "com.example.moneyapp.transaction.transid";
	public static final String USERID_STR = "com.example.moneyapp.transaction.userid";
			
	
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

	public void jumpToPersonList(View view) {
		Intent intent= new Intent(this, PerPerson.class);
		startActivity(intent);
	}
	
	public void jumpToItemList(View view) {
		Intent intent= new Intent(this, PerItem.class);
		startActivity(intent);
	}
	
}
