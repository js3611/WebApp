package com.example.moneyapp.transaction;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.example.helpers.CustomHttpClient;
import com.example.json.JsonCustomReader;
import com.example.moneyapp.MainActivity;
import com.example.moneyapp.R;

public class PerItem extends Activity {

	//Debug
	private static final String TAG = "PerItem";
	private PerItem thisActivity;
	//The List view
	ListView transList;
	//A list of data for each entry, which the adapter retrieves from.
	ArrayList<TransactionDetail> details;
	String name = "Jo";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.transaction_per_person_list_layout);

		thisActivity = this;
		
		transList = (ListView) findViewById(R.id.PerPersonList);
		//Create a list which holds data for each entry
		details = new ArrayList<TransactionDetail>();

		new DownloadPerItem(transList, thisActivity, details).execute("");
//		new DownloadContent().execute("");
		registerForContextMenu(transList);

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
				InputStream in = CustomHttpClient.executeHttpGet(MainActivity.url+
						MainActivity.TRANSACTION + "?"+
						"op="+op+"&"+ 
						"viewMode="+viewMode+"&"+
						"userid="+userid );

				details = JsonCustomReader.readJsonPerPerson(in);
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
		
		@Override
		protected void onPostExecute(ArrayList<TransactionDetail> result) {
			super.onPostExecute(result);
			
			transList.setAdapter(new PerItemAdapter(result, thisActivity));
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
