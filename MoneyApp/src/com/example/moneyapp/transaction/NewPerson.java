package com.example.moneyapp.transaction;

import java.io.InputStream;
import java.util.ArrayList;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Pair;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SearchView;

import com.example.helpers.CustomHttpClient;
import com.example.helpers.metadata.UserDetails;
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
	private String name = "Jo";

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
		
thisActivity = this;
		
		friendList = (ListView) findViewById(R.id.PerPersonList);
		details = new ArrayList<UserDetails>();

		new DownloadContent().execute("");
		
		friendList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> a, View v, int pos, long id) {


			}

			private boolean selectedNewTransaction(int pos) {

				return details.size() == pos;
				
			}

		});

	}
	
	private class DownloadContent extends AsyncTask<String, Void, ArrayList<UserDetails>> {

		@Override
		protected ArrayList<UserDetails> doInBackground(String... params) {
			try {
				int userid = 2;
				String op = "viewFriendsOwe";
				String viewMode = "perPerson";
				
				InputStream in = CustomHttpClient.executeHttpGet(MainActivity.url+
						MainActivity.TRANSACTION + "?"+
						"op="+op+"&"+ 
						"viewMode="+viewMode+"&"+
						"userid="+userid );
//				TransactionDetail Detail;
//				Detail = new TransactionDetail();
//				Detail.setIcon(R.drawable.ic_launcher);
//				Detail.setOwesuser(HttpReaders.readIt(in,500));
//				Detail.setPrice(0);
//				details.add(Detail);
				Pair<Integer,ArrayList<UserDetails>> rawData = JsonCustomReader
						.readJsonUsers(in);
				details = rawData.second;
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
			
			friendList.setAdapter(new NewPersonAdapter(result, thisActivity));
			registerForContextMenu(friendList);
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.new_person, menu);
		return true;
	}

}
