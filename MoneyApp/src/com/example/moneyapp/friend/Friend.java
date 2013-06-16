package com.example.moneyapp.friend;

import java.util.ArrayList;

import com.example.helpers.metadata.FriendsList;
import com.example.helpers.metadata.UserDetails;
import com.example.moneyapp.MainMenu;
import com.example.moneyapp.R;
import com.example.moneyapp.R.layout;
import com.example.moneyapp.R.menu;
import com.example.moneyapp.transaction.NewPersonAdapter;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.support.v4.app.NavUtils;

public class Friend extends Activity {

	private Friend thisActivity;
	private ListView friendList;
	private ArrayList<UserDetails> details;
	private UserDetails user;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.friend_main);
		// Show the Up button in the action bar.
		setupActionBar();
		
		thisActivity = this;
		friendList = (ListView) findViewById(R.id.activity_friend_list);
		details = new ArrayList<UserDetails>();
		user = UserDetails.getUser(getIntent());

		/* Get friends from FriendsList */
		details = FriendsList.getInstance();
		FriendAdapter npa = new FriendAdapter(details, thisActivity);
		friendList.setAdapter(npa);
		registerForContextMenu(friendList);
	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {

		getActionBar().setDisplayHomeAsUpEnabled(true);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.friend, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			Intent intent = getIntent().setClass(this, MainMenu.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
