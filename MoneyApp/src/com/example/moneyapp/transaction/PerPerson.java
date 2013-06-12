package com.example.moneyapp.transaction;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.example.json.JsonCustomReader;
import com.example.moneyapp.R;

public class PerPerson extends Activity {
	
	private PerPerson thisActivity;
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
		
		thisActivity = this;
		
		transList = (ListView) findViewById(R.id.PerPersonList);
		details = new ArrayList<TransactionDetail>();

		new DownloadContent().execute("");
		
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
					intent.putExtra(Transactions.NAME_STR, detail.getOwesuser());
					intent.putExtra(Transactions.PRICE_STR, detail.getPrice());
					intent.putExtra(Transactions.ICON_STR, detail.getIcon());
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
				String str = "{\"returnCode\":1," +
									"\"data\":[{\"userfname\":\"jo\",\"owesfname\":\"terence\",\"price\":30}," +
									"{\"userfname\":\"jo\",\"owesfname\":\"terence\",\"price\":30}," +
									"{\"userfname\":\"jo\",\"owesfname\":\"terence\",\"price\":30}]}";
				InputStream in = new ByteArrayInputStream(str.getBytes());
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
			
			transList.setAdapter(new PerPersonAdapter(result, thisActivity));
			registerForContextMenu(transList);
		}
		
	}

	public static ArrayList<TransactionDetail> addDummies(
			ArrayList<TransactionDetail> details) {
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
