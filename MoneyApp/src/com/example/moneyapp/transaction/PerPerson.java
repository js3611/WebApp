package com.example.moneyapp.transaction;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Pair;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.example.helpers.AdminHelper;
import com.example.helpers.ConnectionHelper;
import com.example.helpers.CustomHttpClient;
import com.example.helpers.HttpReaders;
import com.example.json.JsonCustomReader;
import com.example.moneyapp.MainActivity;
import com.example.moneyapp.R;

public class PerPerson extends Activity {
	
	// The List view
	ListView transList;
	// A list of data for each entry, which the adapter retrieves from.
	ArrayList<TransactionDetail> details;

	// what is this?
	// AdapterView.AdapterContextMenuInfo info;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.transaction_per_person_list_layout);

		transList = (ListView) findViewById(R.id.PerPersonList);

		// Create a list which holds data for each entry
		details = new ArrayList<TransactionDetail>();

		// Fill the screen with dummy entries
		details = addDummies(details);

		transList.setAdapter(new PerPersonAdapter(details, this));

		registerForContextMenu(transList);

		transList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> a, View v, int pos, long id) {

				// TextView tv = (TextView) v.findViewById(R.id.From);
				// String s = tv.getText().toString();
				if (selectedNewTransaction(pos)) {
					startActivity(new Intent(PerPerson.this, NewTransaction.class));
				} else { // normal detail window
					TransactionDetail detail = details.get(pos);
					Intent intent = new Intent(PerPerson.this, PerPersonProfile.class);
					intent.putExtra(Transactions.NAME_STR, detail.getFrom());
					intent.putExtra(Transactions.PRICE_STR, detail.getPrice());
					//intent.putExtra(Transactions.USER_STR, )
					startActivity(intent);
				}

			}

			private boolean selectedNewTransaction(int pos) {

				return details.size() == pos;
				
			}

		});

	}
/*
	@Override
	protected void onStart() {
		String phoneNo = "";
		String password = "";
		ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		if (ConnectionHelper.checkNetworkConnection(connMgr))
	System.out.println("meh");//		new DownloadContent().execute(phoneNo, password);
		else
			System.out.println("No network connection");
		
	}
*/
	private class DownloadContent extends AsyncTask<String, Void, ArrayList<TransactionDetail>> {

		@Override
		protected ArrayList<TransactionDetail> doInBackground(String... params) {
			try {
				InputStream in = CustomHttpClient.executeHttpGet(MainActivity.url
						+ MainActivity.login);
				// Handle JSONstring
				// errorMessage = HttpReaders.readIt(in, 1000);
				TransactionDetail Detail;
				Detail = new TransactionDetail();
				Detail.setIcon(R.drawable.ic_launcher);
				Detail.setFrom(HttpReaders.readIt(in, 100));
				Detail.setPrice(30);
				details.add(Detail);
				// details = JsonCustomReader.readJsonPerPerson(in);
			} catch (Exception e) {
				TransactionDetail Detail;
				Detail = new TransactionDetail();
				Detail.setIcon(R.drawable.ic_launcher);
				Detail.setFrom(""+e.getMessage());
				Detail.setPrice(0);
				details.add(Detail);
			}

			return details;
		}
		
		@Override
		protected void onPostExecute(ArrayList<TransactionDetail> result) {
			super.onPostExecute(result);
		}
		
	}

	public static ArrayList<TransactionDetail> addDummies(
			ArrayList<TransactionDetail> details) {
		TransactionDetail Detail;
		Detail = new TransactionDetail();
		Detail.setIcon(R.drawable.thai);
		Detail.setFrom("Thai");
		Detail.setSubject("Dinner");
		Detail.setPrice(30);
		details.add(Detail);

		Detail = new TransactionDetail();
		Detail.setIcon(R.drawable.terence);
		Detail.setFrom("Terence");
		Detail.setSubject("lunch");
		Detail.setPrice(50);
		details.add(Detail);

		Detail = new TransactionDetail();
		Detail.setIcon(R.drawable.jo);
		Detail.setFrom("Jo");
		Detail.setSubject("Malaga");
		Detail.setPrice(340);
		details.add(Detail);

		Detail = new TransactionDetail();
		Detail.setIcon(R.drawable.terence);
		Detail.setFrom("Terence");
		Detail.setSubject("Dinner");
		Detail.setPrice(30);
		details.add(Detail);

		return details;
	}

}
