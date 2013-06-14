package com.example.moneyapp.transaction;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.util.Pair;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.helpers.ConnectionHelper;
import com.example.helpers.CustomHttpClient;
import com.example.helpers.TransactionHelper;
import com.example.helpers.metadata.UserDetails;
import com.example.helpers.metadata.UserInfo;
import com.example.json.JsonCustomReader;
import com.example.moneyapp.MainActivity;
import com.example.moneyapp.R;

public class NewTransaction extends Activity {

	private static final String TAG = "New Transaction";

	private String errorMessage;

	// The List view
	private ListView personList;
	// A list of data for each entry, which the adapter retrieves from.
	private ArrayList<Pair<String, Double>> person_cost_pairs;
	private UserDetails user;
	private List<UserDetails> owers = null; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.transaction_new_transaction);

		/* Set fields */
		personList = (ListView) findViewById(R.id.PersonList);
		person_cost_pairs = new ArrayList<Pair<String, Double>>();
		user = UserDetails.getUser(getIntent());
		
		addOwers();

		personList.setAdapter(new PersonAdapter(person_cost_pairs, this));
		registerForContextMenu(personList);

		/* set the behaviour of the list*/
		setOnItemInListClicked();

	}

	private void setOnItemInListClicked() {
		personList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> a, View v, int pos, long id) {
				if (selectedNewTransaction(pos)) {
					startActivity(getIntent().setClass(NewTransaction.this,
							NewPerson.class)); 
					/*get new dialog fragment
					// DialogFragment.show() will take care of adding the fragment
				    // in a transaction.  We also want to remove any currently showing
				    // dialog, so make our own transaction and take care of that here.
				    FragmentTransaction ft = getFragmentManager().beginTransaction();
				    Fragment prev = getFragmentManager().findFragmentByTag("dialog");
				    if (prev != null) {
				        ft.remove(prev);
				    }
				    ft.addToBackStack(null);

				    // Create and show the dialog.
				    DialogFragment newFragment = FriendListFragment.newInstance(user.getUserid());
				    newFragment.show(ft, "dialog");
					*/
				}

			}

			private boolean selectedNewTransaction(int pos) {
				return person_cost_pairs.size() == pos;
			}

		});
	}

	private boolean fromAddFriend() {
		return getIntent().getExtras().getBoolean(Transactions.ON_RETURN_FROM_ADD, false);
	}

	private void addOwers() {
		if(fromAddFriend()) {
			owers = new ArrayList<UserDetails>();
			ArrayList<? extends Parcelable> owerinfo = getIntent().getExtras().getParcelableArrayList(Transactions.FRIENDIDS_STR); 
			for (Parcelable parcelable : owerinfo) {
				if (parcelable instanceof UserInfo) {
					UserInfo userinfo = (UserInfo) parcelable;
					UserDetails user = userinfo.getUserDetail();
					owers.add(user);
					person_cost_pairs.add(new Pair<String, Double>(user.getFirstName(), 0.0));
				}
			}
			Log.v(TAG,"received friends");
		} else {
			//??? 
			owers = null;
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.new_transaction, menu);
		return true;
	}

	public void newTransactionHandler(View view) {
		
		if(owers == null) {
			Log.v(TAG, "no one to owe");
			return;
		}
		
		EditText _transName = (EditText) findViewById(R.id.item_text);
		EditText _transDesc = (EditText) findViewById(R.id.item_description_text);
		EditText _transAmount = (EditText) findViewById(R.id.amount);

		Calendar cal = Calendar.getInstance();
		int day = cal.get(Calendar.DATE);
		int month = cal.get(Calendar.MONTH);
		int year = cal.get(Calendar.YEAR);

		int urgency = 1; // TODO GET FROM SPINNER

		String transName = _transName.getText().toString();
		String transDesc = _transDesc.getText().toString();
		String transAmount = _transAmount.getText().toString();
		String transDate = Integer.toString((year * 1000) + (month * 10) + day);
		String transUrgency = Integer.toString(urgency);
		String transOwerCount = Integer.toString(owers.size());
		String transOwers = oweridIdAmount();

		ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

		if (ConnectionHelper.checkNetworkConnection(connMgr)) {
			 new AddTransaction()
			 	.execute(transName, transDesc, transAmount,
					 	 transDate, transUrgency, transOwerCount, transOwers);
		} else if (!ConnectionHelper.checkNetworkConnection(connMgr)) {
			Context context = getApplicationContext();
			CharSequence feedbackMsg = "No network connection, retry after a few seconds";
			int duration = Toast.LENGTH_SHORT;
			Toast toast = Toast.makeText(context, feedbackMsg, duration);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
		}

	}

	/* Read amount from each person, produce the required string to send it to the server */
	private String oweridIdAmount() {
		/* Need to read data from each entry*/
		
		StringBuilder strB = new StringBuilder();
		Map<String, Double> personPriceMap = new HashMap<String, Double>();
		for (Pair<String, Double> pair : person_cost_pairs) {
			personPriceMap.put(pair.first,pair.second);
		}
		
		for (Iterator<UserDetails> itr = owers.iterator(); itr.hasNext();) {
			UserDetails user = itr.next();
			strB.append(user.getUserid() + ":" + personPriceMap.get(user.getFirstName()));
			if(itr.hasNext())
				strB.append(",");
		}
		String str = strB.toString();
		Log.v(TAG, str);
		
		return str;
	}

	private class AddTransaction extends AsyncTask<String, Void, Boolean> {

		@Override
		protected Boolean doInBackground(String... params) {
			return addToTransactions(params[0], params[1], params[2],
					params[3], params[4],params[5],params[6]);
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

				Intent intent = getIntent().setClass(NewTransaction.this,
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
			String transAmount, String transDate, String transUrgency, String transOwerCount, String transOwers) {
		List<NameValuePair> nameValueP = new ArrayList<NameValuePair>(3);
		nameValueP.add(new BasicNameValuePair("op", "newTransaction"));
		// nameValueP.add(new BasicNameValuePair("userid", USERID META DATA));
		nameValueP.add(new BasicNameValuePair("name", transName));
		nameValueP.add(new BasicNameValuePair("desc", transDesc));
		nameValueP.add(new BasicNameValuePair("date", transDate));
		nameValueP.add(new BasicNameValuePair("total_amount", transAmount));
		nameValueP.add(new BasicNameValuePair("urgency", transUrgency));
		nameValueP.add(new BasicNameValuePair("user_owes_count", transOwerCount));
		nameValueP.add(new BasicNameValuePair("oweridIdAmounr", transOwers));
		

		try {
			// address should be the http address of the server side code.
			InputStream in = CustomHttpClient.executeHttpPost(MainActivity.url
					+ MainActivity.TRANSACTION, nameValueP);
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
