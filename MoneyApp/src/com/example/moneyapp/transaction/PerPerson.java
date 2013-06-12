package com.example.moneyapp.transaction;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.example.helpers.CustomHttpClient;
import com.example.helpers.HttpReaders;
import com.example.json.JsonCustomReader;
import com.example.moneyapp.MainActivity;
import com.example.moneyapp.R;

public class PerPerson extends Activity {

	//Debug
	private static final String TAG = "PerPerson";
	private PerPerson thisActivity;
	// The List view
	ListView transList;
	// A list of data for each entry, which the adapter retrieves from.
	ArrayList<TransactionDetail> details;
	String name = "jo";

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
				int userid = 2;
				String op = "viewFriendsGet";
				String viewMode = "perPerson";
				
				InputStream in = CustomHttpClient.executeHttpGet(MainActivity.url+
						MainActivity.TRANSACTION + "?"+
						"op="+op+"&"+ 
						"viewMode="+viewMode+"&"+
						"userid="+userid );
				TransactionDetail Detail;
				Detail = new TransactionDetail();
				Detail.setIcon(R.drawable.ic_launcher);
				Detail.setOwesuser(HttpReaders.readIt(in,500));
				Detail.setPrice(0);
				details.add(Detail);
//				ArrayList<TransactionDetail> rawData = JsonCustomReader.readJsonPerPerson(in); 
//				details = processPerPerson(rawData);
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
		
		private ArrayList<TransactionDetail> processPerPerson(
				ArrayList<TransactionDetail> rawData) {
			ArrayList<TransactionDetail> newDetails = new ArrayList<TransactionDetail>();
			Map<String, Double> personPriceMap = new HashMap<String, Double>();
			Map<String, Integer> personIconMap = new HashMap<String, Integer>();

			
			
			for (TransactionDetail transactionDetail : rawData) {
				Log.v(TAG, transactionDetail.toString());
				if(name.equals(transactionDetail.getUser())) {
					String owesUser = transactionDetail.getOwesuser();
					personIconMap.put(owesUser, transactionDetail.getIcon());
					if(personPriceMap.containsKey(owesUser)) {
						personPriceMap.put(owesUser, personPriceMap.get(owesUser)+transactionDetail.getPrice()-transactionDetail.getPartial_pay());
					} else {
						personPriceMap.put(owesUser, transactionDetail.getPrice());
					}
				} else {
					String user = transactionDetail.getUser();
					personIconMap.put(user, transactionDetail.getIcon());
					if(personPriceMap.containsKey(user)) {
						personPriceMap.put(user, personPriceMap.get(user)+transactionDetail.getPrice()-transactionDetail.getPartial_pay());
					} else {
						personPriceMap.put(user, transactionDetail.getPrice());
					}
				}
			}
			
			for (Map.Entry<String, Double> entry : personPriceMap.entrySet()) {
				TransactionDetail tDetail = new TransactionDetail(personIconMap.get(entry.getKey()),entry.getKey(),name,"",entry.getValue(),"","");
				newDetails.add(tDetail);
			}
			return newDetails;
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

//		String str = "{\"returnCode\":1," +
//		"\"data\":[{\"userfname\":\"jo\",\"owesfname\":\"terence\",\"price\":30}," +
//		"{\"userfname\":\"jo\",\"owesfname\":\"terence\",\"price\":30}," +
//		"{\"userfname\":\"jo\",\"owesfname\":\"terence\",\"price\":30}]}";
//
//InputStream in = new ByteArrayInputStream(str.getBytes());

		
		return details;
	}

}
