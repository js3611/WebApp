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
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.example.helpers.CustomHttpClient;
import com.example.helpers.HttpReaders;
import com.example.helpers.metadata.UserDetails;
import com.example.json.JsonCustomReader;
import com.example.moneyapp.MainActivity;
import com.example.moneyapp.MainMenu;
import com.example.moneyapp.R;

public class PerPerson extends Activity {

	// Debug
	private static final String TAG = "PerPerson";
	private PerPerson thisActivity;
	// The List view
	private ListView transList;
	// A list of data for each entry, which the adapter retrieves from.
	private ArrayList<TransactionDetail> details;
	private UserDetails user;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.transaction_per_person_list_layout);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		/* Set fields */
		thisActivity = this;
		transList = (ListView) findViewById(R.id.PerPersonList);
		details = new ArrayList<TransactionDetail>();
		user = UserDetails.getUser(getIntent());

		/* Asynchronously populate the list view the data */
		new DownloadContent().execute(Integer.toString(user.getUserid()));

		/* Set the behaviour when entry of a list is clicked */
		setListEntryOnClickListener();

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

	private void setListEntryOnClickListener() {
		transList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> a, View v, int pos, long id) {

				if (selectedNewTransaction(pos)) {
					startActivity(getIntent().setClass(PerPerson.this,
							NewTransaction.class));

				} else { /* normal detail window */
					TransactionDetail detail = details.get(pos);
					Log.v(TAG, detail.toString());
					Intent intent = getIntent().setClass(PerPerson.this,
							PerPersonProfile.class);
					intent.putExtra(Transactions.NAME_STR, detail.getOwesuser());
					intent.putExtra(Transactions.PRICE_STR, detail.getPrice());
					intent.putExtra(Transactions.ICON_STR, detail.getIcon());
					intent.putExtra(Transactions.FRIENDID_STR,
							detail.getOwesuserid());
					startActivity(intent);
				}

			}

			private boolean selectedNewTransaction(int pos) {
				return details.size() == pos;
			}

		});
	}

	protected int getFriendID(TransactionDetail detail, String firstName) {
		if (detail.getOwesuser().equals(firstName))
			return detail.getUserid();
		else
			return detail.getOwesuserid();
	}

	private class DownloadContent extends
			AsyncTask<String, Void, ArrayList<TransactionDetail>> {

		@Override
		protected ArrayList<TransactionDetail> doInBackground(String... params) {
			try {
				int userid = Integer.parseInt(params[0]);
				String op = "viewFriendsOwe";
				String viewMode = "perPerson";

				InputStream in = CustomHttpClient
						.executeHttpGet(MainActivity.URL
								+ MainActivity.TRANSACTION  
								+ "?" + "op=" + op
								+ "&" + "viewMode=" + viewMode 
								+ "&" + "userid=" + userid);

				ArrayList<TransactionDetail> rawData = JsonCustomReader
						.readJsonPerPerson(in);
				details = processPerPerson(rawData);
			} catch (Exception e) {
				TransactionDetail Detail;
				Detail = new TransactionDetail();
				Detail.setIcon(R.drawable.ic_launcher);
				Detail.setOwesuser("ERROR" + e.getMessage());
				Detail.setPrice(0);
				details.add(Detail);
			}

			return details;
		}

		/* A method to combine the bills and diplay total difference */
		private ArrayList<TransactionDetail> processPerPerson(
				ArrayList<TransactionDetail> rawData) {

			ArrayList<TransactionDetail> newDetails = new ArrayList<TransactionDetail>();
			Map<String, Double> personPriceMap = new HashMap<String, Double>();
			Map<String, UserDetails> personDetailMap = new HashMap<String, UserDetails>();

			/* for each transaction */
			for (TransactionDetail transactionDetail : rawData) {

				Log.v(TAG, transactionDetail.toString());
				/* If the transaction is to user */
				if (user.getFirstName().equals(transactionDetail.getUser())) {
					/* get who owes */
					String owesUser = transactionDetail.getOwesuser();
					/* put the icon now */
					Log.v(TAG, "pays to " + owesUser);
					if (personPriceMap.containsKey(owesUser)) {
						/*
						 * If there is a person in the map already, subtract the
						 * value
						 */
						personPriceMap.put(
								owesUser,
								personPriceMap.get(owesUser)
										- (transactionDetail
												.getRemainingToPay()));
					} else {
						/*
						 * This is the case they owe you, so you have to pay
						 * negative amount
						 */
						personPriceMap.put(owesUser,
								-transactionDetail.getRemainingToPay());
						/* Add the detail of the person who you owe to a map */
						personDetailMap.put(
								owesUser,
								new UserDetails(transactionDetail
										.getOwesuserid(), "", owesUser, 0, 0,
										"", "", transactionDetail.getIcon()));
					}
					Log.v(TAG,
							"pays to " + owesUser + ": "
									+ personPriceMap.get(owesUser));
				} else {
					/* If user owes someone, then increase the total */
					String owesUser = transactionDetail.getUser();

					if (personPriceMap.containsKey(owesUser)) {
						personPriceMap.put(
								owesUser,
								personPriceMap.get(owesUser)
										+ (transactionDetail
												.getRemainingToPay()));
					} else {
						personPriceMap.put(owesUser,
								transactionDetail.getRemainingToPay());
						personDetailMap.put(owesUser, new UserDetails(
								transactionDetail.getUserid(), "", owesUser, 0,
								0, "", "", transactionDetail.getIcon()));
					}
					Log.v(TAG,
							"pays to" + owesUser + ": "
									+ personPriceMap.get(owesUser));

				}
			}

			Log.v(TAG, "After editing");

			for (Map.Entry<String, Double> entry : personPriceMap.entrySet()) {
				TransactionDetail tDetail = new TransactionDetail(
						personDetailMap.get(entry.getKey()).getProfilePicture(),
						0, entry.getKey(), user.getFirstName(), personDetailMap
								.get(entry.getKey()).getUserid(), user
								.getUserid(), "", entry.getValue(), (double) 0,
						"", "");
				Log.v(TAG, tDetail.toString());
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

		// String str = "{\"returnCode\":1," +
		// "\"data\":[{\"userfname\":\"jo\",\"owesfname\":\"terence\",\"price\":30},"
		// +
		// "{\"userfname\":\"jo\",\"owesfname\":\"terence\",\"price\":30}," +
		// "{\"userfname\":\"jo\",\"owesfname\":\"terence\",\"price\":30}]}";
		//
		// InputStream in = new ByteArrayInputStream(str.getBytes());

		return details;
	}

}
