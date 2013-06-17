package com.example.moneyapp.transaction;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.JsonToken;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.example.helpers.AdminHelper;
import com.example.helpers.CustomHttpClient;
import com.example.helpers.metadata.Pair;
import com.example.json.JsonCustomReader;
import com.example.moneyapp.MainActivity;
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
		//new DownloadPerItem(transList, thisActivity, details).execute("");
		new DownloadContent().execute("");
		//registerForContextMenu(transList);

		transList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> a, View v, int pos, long id) {

				if (selectedNewTransaction(pos)) {
					startActivity(new Intent(PerItem.this, NewTransaction.class));
				} else { //normail detail window
					startActivity(new Intent(PerItem.this, Transactions.class));
				}
			}

			private boolean selectedNewTransaction(int pos) {
				return details.size() == pos;
			}
		});

	}

	private class DownloadContent extends AsyncTask<String, Void, ArrayList<TransactionDetail>> {

		@Override
		protected ArrayList<TransactionDetail> doInBackground(String... params) {
			try {
				int userid = 2;
				String op = "viewLiveTransactions";
				String viewMode = "perItem";
				InputStream in = CustomHttpClient.executeHttpGet(MainActivity.URL+
						MainActivity.TRANSACTION + "?"+
						"op="+op+"&"+ 
						"viewMode="+viewMode+"&"+
						"userid="+userid );
				
				
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
			
			transList.setAdapter(new PerItemAdapter(details, thisActivity));
			//registerForContextMenu(transList);
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.per_item, menu);
		return true;
	}
	
	public static ArrayList<TransactionDetail> addDummies(ArrayList<TransactionDetail> details) {
		TransactionDetail Detail;
		Detail = new TransactionDetail();
		Detail.setIcon(R.drawable.thai);
		Detail.setOwesuser("Thai");
		Detail.setSubject("Dinner");
		Detail.setPrice(30);
		details.add(Detail);

		Detail = new TransactionDetail();
		Detail.setIcon(R.drawable.terence);
		Detail.setOwesuser("Terence");
		Detail.setSubject("lunch");
		Detail.setPrice(50);
		details.add(Detail);
		
		Detail = new TransactionDetail();
		Detail.setIcon(R.drawable.jo);
		Detail.setOwesuser("Jo");
		Detail.setSubject("Malaga");
		Detail.setPrice(340);
		details.add(Detail);
		
		Detail = new TransactionDetail();
		Detail.setIcon(R.drawable.terence);
		Detail.setOwesuser("Terence"); 
		Detail.setSubject("Dinner");
		Detail.setPrice(30);
		details.add(Detail);
		
		return details;
	}

}
