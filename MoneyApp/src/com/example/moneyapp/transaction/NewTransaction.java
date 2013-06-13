package com.example.moneyapp.transaction;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.example.helpers.AdminHelper;
import com.example.helpers.ConnectionHelper;
import com.example.helpers.CustomHttpClient;
import com.example.helpers.TransactionHelper;
import com.example.helpers.metadata.UserDetails;
import com.example.json.JsonCustomReader;
import com.example.moneyapp.MainActivity;
import com.example.moneyapp.MainMenu;
import com.example.moneyapp.R;
import com.example.moneyapp.SignIn;
import com.example.moneyapp.R.id;
import com.example.moneyapp.R.layout;
import com.example.moneyapp.R.menu;

import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Pair;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class NewTransaction extends Activity {

	String errorMessage;

	// The List view
	ListView personList;
	// A list of data for each entry, which the adapter retrieves from.
	ArrayList<Pair<String, Double>> person_cost_pairs;
	private UserDetails user;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.transaction_new_transaction);

		
		personList = (ListView) findViewById(R.id.PersonList);
		person_cost_pairs = new ArrayList<Pair<String, Double>>();
		// Fill the screen with dummy entries

		personList.setAdapter(new PersonAdapter(person_cost_pairs, this));
		registerForContextMenu(personList);

		personList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> a, View v, int pos, long id) {
				if (selectedNewTransaction(pos)) {
					startActivity(new Intent(NewTransaction.this,
							NewPerson.class));
				}

			}

			private boolean selectedNewTransaction(int pos) {
				return person_cost_pairs.size() == pos;
			}

		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.new_transaction, menu);
		return true;
	}

	public void newTransactionHandler(View view) {
		EditText _transName = (EditText) findViewById(R.id.item_name);
		EditText _transDesc = (EditText) findViewById(R.id.item_description);
		// EditText _transAmount = (EditText) findViewById(R.id.amount);

		Calendar cal = Calendar.getInstance();
		int day = cal.get(Calendar.DATE);
		int month = cal.get(Calendar.MONTH);
		int year = cal.get(Calendar.YEAR);

		int urgency = 1; // TODO GET FROM SPINNER

		String transName = _transName.getText().toString();
		String transDesc = _transDesc.getText().toString();
		// String transAmount = _transAmount.getText().toString();
		String transDate = Integer.toString((year * 1000) + (month * 10) + day);
		String transUrgency = Integer.toString(urgency);

		ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

		if (ConnectionHelper.checkNetworkConnection(connMgr)) {
			// new AddTransaction().execute(transName, transDesc, transAmount,
			// transDate, transUrgency);
		} else if (!ConnectionHelper.checkNetworkConnection(connMgr)) {
			Context context = getApplicationContext();
			CharSequence feedbackMsg = "No network connection, retry after a few seconds";
			int duration = Toast.LENGTH_SHORT;
			Toast toast = Toast.makeText(context, feedbackMsg, duration);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
		}

	}

	private class AddTransaction extends AsyncTask<String, Void, Boolean> {

		@Override
		protected Boolean doInBackground(String... params) {
			return addToTransactions(params[0], params[1], params[2],
					params[3], params[4]);
		}

		@Override
		protected void onPostExecute(Boolean result) {
			// Checks the result of running addToTransactions
			if (result) {

				// Toast message
				Context context = getApplicationContext();
				CharSequence feedbackMsg = "Transaction added!";
				int duration = Toast.LENGTH_SHORT;
				Toast toast = Toast.makeText(context, feedbackMsg, duration);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();

				Intent intent = new Intent(NewTransaction.this,
						TransactionDetail.class);
				startActivity(intent);
			} else {

				// Toast message
				Context context = getApplicationContext();
				CharSequence feedbackMsg = errorMessage;
				int duration = Toast.LENGTH_SHORT;
				Toast toast = Toast.makeText(context, feedbackMsg, duration);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
			}
		}
	}

	private boolean addToTransactions(String transName, String transDesc,
			String transAmount, String transDate, String transUrgency) {
		List<NameValuePair> nameValueP = new ArrayList<NameValuePair>(3);
		nameValueP.add(new BasicNameValuePair("op", "newTransaction"));
		// nameValueP.add(new BasicNameValuePair("userid", USERID META DATA));
		nameValueP.add(new BasicNameValuePair("name", transName));
		nameValueP.add(new BasicNameValuePair("desc", transDesc));
		nameValueP.add(new BasicNameValuePair("date", transDate));
		nameValueP.add(new BasicNameValuePair("total_amount", transAmount));
		nameValueP.add(new BasicNameValuePair("urgency", transUrgency));

		try {
			// address should be the http address of the server side code.
			InputStream in = CustomHttpClient.executeHttpPost(MainActivity.url
					+ MainActivity.login, nameValueP);
			// Handle JSONstring
			int response = JsonCustomReader.readJsonRetCode(in);
			Pair<String, Boolean> pair = TransactionHelper
					.handleResponse(response);
			errorMessage = pair.first;
			return pair.second;

		} catch (Exception e) {
			// Toast message
			Context context = getApplicationContext();
			CharSequence feedbackMsg = "An Unknown error has occurred!";
			int duration = Toast.LENGTH_SHORT;
			Toast toast = Toast.makeText(context, feedbackMsg, duration);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
		}

		return false;
	}
}
