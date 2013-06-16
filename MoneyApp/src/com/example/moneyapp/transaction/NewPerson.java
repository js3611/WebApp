package com.example.moneyapp.transaction;

import java.io.InputStream;
import java.util.ArrayList;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.SearchView;

import com.example.helpers.CustomHttpClient;
import com.example.helpers.metadata.FriendsList;
import com.example.helpers.metadata.Pair;
import com.example.helpers.metadata.UserDetails;
import com.example.helpers.metadata.UserInfo;
import com.example.json.JsonCustomReader;
import com.example.moneyapp.MainActivity;
import com.example.moneyapp.R;

public class NewPerson extends Activity {

	// Debug
	private static final String TAG = "NewPerson";
	private NewPerson thisActivity;
	// The List view
	private ListView friendList;
	// A list of data for each entry, which the adapter retrieves from.
	private ArrayList<UserDetails> details;
	private UserDetails user;
	private UserDetails[] owers;
	private int numberOfOwers;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.transaction_new_person);
		((SearchView) findViewById(R.id.searchView1))
				.setIconifiedByDefault(false);

		// Get the intent, verify the action and get the query
		Intent intent = getIntent();
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			String query = intent.getStringExtra(SearchManager.QUERY);
			// doMySearch(query);
		}
		
		/* Set fields */ 
		thisActivity = this;
		friendList = (ListView) findViewById(R.id.friends_list);
		details = new ArrayList<UserDetails>();
		user = UserDetails.getUser(getIntent());
		numberOfOwers = 0;

		/* Get friends from FriendsList */
		details = FriendsList.getInstance();
		owers = new UserDetails[details.size()];
		NewPersonAdapter npa = new NewPersonAdapter(details, thisActivity);
		friendList.setAdapter(npa);
		registerForContextMenu(friendList);
		
		//new DownloadFriends().execute(user.getUserid());
		
		friendList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> a, View v, int pos, long id) {

				CheckBox cb = (CheckBox) v.findViewById(R.id.checkBox1);

				if(owers[pos] == null) {
					cb.setChecked(true);
					owers[pos]=details.get(pos);
					numberOfOwers++;
					Log.v(TAG, "Added: "+details.get(pos).getFirstName());
				}else{
					owers[pos] = null;
					cb.setChecked(false);
					numberOfOwers--;
					Log.v(TAG, "Removed: "+details.get(pos).getFirstName());
				}
			}

		});

	}
/*
	private class DownloadFriends extends AsyncTask<Integer, Void, ArrayList<UserDetails>> {

		@Override
		protected ArrayList<UserDetails> doInBackground(Integer... params) {
			try {
				int userid = params[0];
				String op = "getFriendsList";
				String viewMode = "perPerson";
				
				InputStream in = CustomHttpClient.executeHttpGet(MainActivity.URL+
						MainActivity.TRANSACTION + "?"+
						"op="+op+"&"+ 
						"viewMode="+viewMode+"&"+
						"userid="+userid );

				Pair<Integer,ArrayList<UserDetails>> rawData = JsonCustomReader
						.readJsonFriends(in);
				details = rawData.getSecond();
			} catch (Exception e) {
				UserDetails Detail;
				Detail = new UserDetails();
				Detail.setProfilePicture(R.drawable.ic_launcher);
				Detail.setFirstName("ERROR" + e.getMessage());
				details.add(Detail);
			}

			return details;
		}
		
		@Override
		protected void onPostExecute(ArrayList<UserDetails> result) {
			super.onPostExecute(result);
			
			owers = new UserDetails[result.size()];
			NewPersonAdapter npa = new NewPersonAdapter(result, thisActivity);
			friendList.setAdapter(npa);
			registerForContextMenu(friendList);
		}
		
	}
*/
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.new_person, menu);
		return true;
	}
	
	public void onAddClicked(View view) { 
	
		Intent intent = new Intent(getApplicationContext(), NewTransaction.class);
		ArrayList<UserInfo> owerinfos = new ArrayList<UserInfo>(numberOfOwers);
		for (UserDetails user : owers) {
			if(user!=null)
				owerinfos.add(new UserInfo(user));
		}
		intent.putParcelableArrayListExtra(Transactions.FRIENDIDS_STR, owerinfos);
		intent.putExtra(Transactions.ON_RETURN_FROM_ADD, true);
		
		setResult(RESULT_OK, intent);
		finish();
		
	}
}
