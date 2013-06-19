package com.example.moneyapp.transaction;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.JsonToken;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.example.helpers.AdminHelper;
import com.example.helpers.CustomHttpClient;
import com.example.helpers.metadata.Pair;
import com.example.helpers.metadata.UserDetails;
import com.example.json.JsonCustomReader;
import com.example.moneyapp.MainActivity;
import com.example.moneyapp.MainMenu;
import com.example.moneyapp.R;

public class PerItem extends Activity {

	//Debug
	private static final String TAG = "PerItem";
	private String errorMessage;
	
	private PerItem thisActivity;
	//The List view
	ListView transList;
	//A list of data for each entry, which the adapter retrieves from.
	ArrayList<TransactionDetail> details;
	UserDetails user;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.transaction_per_person_list_layout);
		transList = (ListView) findViewById(R.id.PerPersonList);
		
		/* set fields */
		thisActivity = this;
		//Create a list which holds data for each entry
		details = new ArrayList<TransactionDetail>();
		user = UserDetails.getUser(getIntent());
		//registerForContextMenu(transList);

		transList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> a, View v, int pos, long id) {

				if (selectedNewTransaction(pos)) {
					startActivity(getIntent().setClass(PerItem.this, NewTransaction.class));
				} else { //normal detail window
					Intent intent = getIntent().setClass(thisActivity, PerItemDetails.class);
					intent.putExtra(Transactions.TRANSID_STR, details.get(pos).getTransactionID());
					intent.putExtra(Transactions.USERID_STR, details.get(pos).getUserid());
					intent.putExtra(Transactions.PRICE_STR, details.get(pos).getPrice()-details.get(pos).getPartial_pay());
					
					startActivity(intent);
				}
			}

			private boolean selectedNewTransaction(int pos) {
				return details.size() == pos;
			}
		});

	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
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
	
	
	@Override
	protected void onStart() {
		super.onStart();
		new DownloadContent().execute();
	}

	private class DownloadContent extends AsyncTask<String, Void, ArrayList<TransactionDetail>> {

		@Override
		protected ArrayList<TransactionDetail> doInBackground(String... params) {
			try {
				String op = "viewLiveTransactions";
				String viewMode = "perItem";
				InputStream in = CustomHttpClient.executeHttpGet(MainActivity.URL+
						MainActivity.TRANSACTION + "?"+
						"op="+op+"&"+ 
						"viewMode="+viewMode+"&"+
						"userid="+user.getUserid());
					
				processInput(in);
			
			} catch (Exception e) {
				TransactionDetail Detail;
				Detail = new TransactionDetail();
				Detail.setIcon(R.drawable.ic_launcher);
				Detail.setOwesuser("ERROR"+e.getMessage());
				Detail.setPrice(0);
				details.add(Detail);
			}

			return details;
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
				if (jr.peek() == JsonToken.END_OBJECT) {
					Log.v(TAG, "No Transactions");
					return true;
				}
				Log.v(TAG, "Reading transactions");
				/* Read TransactionDetails */
				ArrayList<TransactionDetail> rawData = JsonCustomReader
						.readJSONTransactions(jr, in);
				jr.endObject();
				
				Log.v(TAG, "parsed fine");
				
				details = rawData;
							
				return true;
			} catch (UnsupportedEncodingException e) {
				errorMessage = e.getMessage();
			} catch (IOException e) {
				errorMessage = e.getMessage();
			}
			return false;

		}
		
		@Override
		protected void onPostExecute(ArrayList<TransactionDetail> result) {
			super.onPostExecute(result);
			
			Collections.sort(details);
			transList.setAdapter(new PerItemAdapter(details, thisActivity,user));
			//registerForContextMenu(transList);
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.per_item, menu);
		return true;
	}
	
}
