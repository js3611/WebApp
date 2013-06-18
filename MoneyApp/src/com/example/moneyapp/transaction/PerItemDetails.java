package com.example.moneyapp.transaction;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.JsonReader;
import android.util.JsonToken;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ListView;

import com.example.helpers.AdminHelper;
import com.example.helpers.CustomHttpClient;
import com.example.helpers.MyToast;
import com.example.helpers.metadata.FriendsList;
import com.example.helpers.metadata.Pair;
import com.example.helpers.metadata.UserDetails;
import com.example.json.JsonCustomReader;
import com.example.moneyapp.MainActivity;
import com.example.moneyapp.R;

public class PerItemDetails extends Activity {

	private String errorMessage;
	private PerItemDetails thisActivity;
	private UserDetails user;
	//ID of the person who made the transaction
	private int userid;
	//ID of the transaction
	private int transid;
	private TransactionDetail transaction;
	private ListView owerList;
	private PersonAdapter personAdapter;
	private ArrayList<Pair<UserDetails, Double>> person_cost_pairs;
	private boolean can_delete;
	private double individualAmount;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.transaction_per_item_details);
		// Show the Up button in the action bar.
		setupActionBar();
		
		/* Set fields*/
		person_cost_pairs = new ArrayList<Pair<UserDetails,Double>>();
		owerList = (ListView) findViewById(R.id.PersonList);
		user = UserDetails.getUser(getIntent());
		transid = getIntent().getExtras().getInt(Transactions.TRANSID_STR);
		userid = getIntent().getExtras().getInt(Transactions.USERID_STR);
		individualAmount = getIntent().getExtras().getDouble(Transactions.PRICE_STR);
		if (userid == user.getUserid()) {
			can_delete = true;
		}
		thisActivity = this;
		
		
		
	}

	@Override
	protected void onStart() {
		super.onStart();
		new DownloadContent().execute(user.getUserid(),transid);
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
		getMenuInflater().inflate(R.menu.per_item_details, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private class DownloadContent extends AsyncTask<Integer, Void, Boolean> {

		private static final String TAG = "PerItemDetails";
		
		@Override
		protected Boolean doInBackground(Integer... params) {
			try {
				String op = "transactionDetails";
				String viewMode = "perItem";
				InputStream in = CustomHttpClient.executeHttpGet(MainActivity.URL+
						MainActivity.TRANSACTION + "?"+
						"op="+op+"&"+ 
						"viewMode="+viewMode+"&"+
						"userid="+params[0] +"&"+
						"transid="+params[1]);
					
				return processInput(in);
			
			} catch (Exception e) {
				Log.v(TAG, e.getMessage());
			}

			return false;
		}
		
		private boolean processInput(InputStream in) {

			JsonReader jr;
			try {
				jr = new JsonReader(new BufferedReader(new InputStreamReader(
						in, "UTF-8")));

				jr.setLenient(true);
				jr.beginObject();

				/* Read ReturnCode */
				Pair<String, Boolean> pair = AdminHelper
						.handleResponse(JsonCustomReader
								.readJSONRetCode(jr, in));

				if (!pair.getSecond()) {
					errorMessage = pair.getFirst();
					Log.v(TAG, "No Transactions");
					return false;
				}
				Log.v(TAG, "Read fine");
				Log.v(TAG, "Reading status");
				
				jr.nextName();
				can_delete = jr.nextBoolean();
				
				Log.v(TAG, "Reading transactions");
				/* Read UsersTransactionDetails */
				//read the name
				jr.nextName();
				transaction = JsonCustomReader
						.readData(jr, in);
				Log.v(TAG, "Read transactions. Reading users");
				
				// Reading users
				ArrayList<UserDetails> users = JsonCustomReader.readJSONFriends(jr, in);
				fillPersonCostArray(users);
				jr.endObject();
				
				Log.v(TAG, "parsed fine");
											
				return true;
			} catch (UnsupportedEncodingException e) {
				errorMessage = e.getMessage();
			} catch (IOException e) {
				errorMessage = e.getMessage();
			}
			return false;

		}
		
		private void fillPersonCostArray(ArrayList<UserDetails> users) {
			for (UserDetails userDetails : users) {
				person_cost_pairs.add(new Pair<UserDetails, Double>(userDetails, userDetails.getAmount()));
			}
			
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			
			if(result){
			// Set views
				EditText item_text = (EditText) findViewById(R.id.item_text);
				EditText item_description_text = (EditText) findViewById(R.id.item_description_text);
				EditText total_text = (EditText) findViewById(R.id.amount_text);
				item_text.setText(transaction.getSubject());
				item_description_text.setText(transaction.getDescription());
				total_text.setText(Double.toString(transaction.getPrice()));
				
				if (can_delete) { //If the owner of the 
					personAdapter = new PersonAdapter(person_cost_pairs, thisActivity);
					owerList.setAdapter(personAdapter);
					//registerForContextMenu(transList);
				} else {
					
					ArrayList<Pair<UserDetails, Double>> owerArr = new ArrayList<Pair<UserDetails,Double>>();
					UserDetails newOwer = new UserDetails();
					newOwer.setFirstName(transaction.getUser());
					owerArr.add(new Pair<UserDetails, Double>(newOwer, individualAmount));
					
					personAdapter = new OwerAdapter(owerArr, thisActivity);
					
					owerList.setAdapter(personAdapter);
										
				}
			} else {
				MyToast.toastMessage(thisActivity, errorMessage);
			}
		}
	}
	
}
