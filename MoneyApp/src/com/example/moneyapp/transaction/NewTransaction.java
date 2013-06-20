package com.example.moneyapp.transaction;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.JsonReader;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.helpers.ConnectionHelper;
import com.example.helpers.CustomHttpClient;
import com.example.helpers.DateGen;
import com.example.helpers.HttpReaders;
import com.example.helpers.MyMath;
import com.example.helpers.MyToast;
import com.example.helpers.TransactionHelper;
import com.example.helpers.metadata.Pair;
import com.example.helpers.metadata.UserDetails;
import com.example.helpers.metadata.UserInfo;
import com.example.json.JsonCustomReader;
import com.example.moneyapp.MainActivity;
import com.example.moneyapp.MainMenu;
import com.example.moneyapp.R;

public class NewTransaction extends Activity implements
		EqSplitDialog.NoticeDialogListener {

	/* Debug */
	private static final String TAG = "New Transaction";
	private String errorMessage = "";
	/* */

	// The List view
	private ListView personList;
	private PersonAdapter personAdapter;
	// A list of data for each entry, which the adapter retrieves from.
	private ArrayList<Pair<UserDetails, Double>> person_cost_pairs;
	/* List of people who owe */
	private List<UserDetails> owers = null;
	/* User data */
	private UserDetails user;
	private boolean eq_flag = false;
	private Activity thisActivity;
	private boolean include_myself = false;
	private double total_amount;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.transaction_new_transaction);

		/* Set fields */
		thisActivity = this;
		personList = (ListView) findViewById(R.id.PersonList);

		user = UserDetails.getUser(getIntent());
		person_cost_pairs = new ArrayList<Pair<UserDetails, Double>>();
		owers = new ArrayList<UserDetails>();
		
		if(getIntent().getBooleanExtra("fromPartialPay", false)) {
			EditText et = (EditText) findViewById(R.id.item_text);
			et.setText("Partial Pay");
			UserDetails repay = new UserDetails();
			int userid = getIntent().getIntExtra(Transactions.FRIENDID_STR,0);
			repay.setUserid(userid);
			person_cost_pairs.add(new Pair<UserDetails, Double>(repay, 0.0));
			owers.add(repay);
		}
		
		personAdapter = new PersonAdapter(person_cost_pairs, this);
		personList.setAdapter(personAdapter);
		registerForContextMenu(personList);

		/* set the behaviour of the list */
		setOnItemInListClicked();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.new_transaction, menu);
		return true;
	}

	private void setOnItemInListClicked() {
		personList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> a, View v, int pos, long id) {
				if (selectedNewTransaction(pos)) {

					Intent i = new Intent(NewTransaction.this, NewPerson.class)
							.putExtra(MainActivity.USER_KEY, user);
					// save the value before changing the view
					readPairListValues();
					// Send list of friends selected already
					if (owers != null) {
						ArrayList<UserInfo> owerinfos = new ArrayList<UserInfo>(
								owers.size());
						for (UserDetails user : owers) {
							if (user != null)
								owerinfos.add(new UserInfo(user));
						}
						i.putParcelableArrayListExtra(
								Transactions.FRIENDIDS_STR, owerinfos);
					}
					startActivityForResult(i, 1);
					// startActivity(getIntent().setClass(NewTransaction.this,
					// NewPerson.class));

					/*
					 * get new dialog fragment // DialogFragment.show() will
					 * take care of adding the fragment // in a transaction. We
					 * also want to remove any currently showing // dialog, so
					 * make our own transaction and take care of that here.
					 * FragmentTransaction ft =
					 * getFragmentManager().beginTransaction(); Fragment prev =
					 * getFragmentManager().findFragmentByTag("dialog"); if
					 * (prev != null) { ft.remove(prev); }
					 * ft.addToBackStack(null);
					 * 
					 * // Create and show the dialog. DialogFragment newFragment
					 * = FriendListFragment.newInstance(user.getUserid());
					 * newFragment.show(ft, "dialog");
					 */
				}

			}

			private void readPairListValues() {

				for (int i = 0; i < personList.getLastVisiblePosition()
						- personList.getFirstVisiblePosition(); i++) {
					View v = personList.getChildAt(i);
					EditText et = (EditText) v
							.findViewById(R.id.new_price_text);
					try {
						person_cost_pairs.get(i).setSecond(
								Double.parseDouble(et.getText().toString()));
					} catch (NumberFormatException e) {
						// do nothing
					}
				}

			}

			private boolean selectedNewTransaction(int pos) {
				return person_cost_pairs.size() == pos;
			}

		});

	}

	public void newTransactionHandler(View view) {

		if (!validateInput())
			return;

		EditText _transName = (EditText) findViewById(R.id.item_text);
		EditText _transDesc = (EditText) findViewById(R.id.item_description_text);
		EditText _transAmount = (EditText) findViewById(R.id.amount);

		int urgency = 1; // TODO GET FROM SPINNER

		String transUserid = Integer.toString(user.getUserid());
		String transName = _transName.getText().toString();
		String transDesc = _transDesc.getText().toString();
		String transAmount = _transAmount.getText().toString();
		String transDate = DateGen.getDate();
		String transUrgency = Integer.toString(urgency);
		String transOwerCount = Integer.toString(owers.size());
		String transOwers = oweridIdAmount();
		String transOwerIdPartialPairs = owerIdPartialPairs();

		new AddTransaction().execute(transUserid, transName, transDesc,
				transAmount, transDate, transUrgency, transOwerCount,
				transOwers, transOwerIdPartialPairs);

	}

	/*
	 * Helpers for new transaction
	 */
	private String owerIdPartialPairs() {
		StringBuilder strB = new StringBuilder();
		/* Set everyone to zero */
		/* Generate a string of the form "userid-amount,userid-amount,..." */
		for (Iterator<UserDetails> itr = owers.iterator(); itr.hasNext();) {
			UserDetails user = itr.next();
			strB.append(user.getUserid() + "-" + 0.0);
			if (itr.hasNext())
				strB.append(",");
		}
		String str = strB.toString();
		Log.v(TAG, str);

		return str;
	}

	/*
	 * Read amount from each person, produce the required string to send it to
	 * the server
	 */
	private String oweridIdAmount() {

		/*
		 * Need to read price entered in each edit text for (int i = 0; i <
		 * person_cost_pairs.size(); i++) { View view =
		 * personList.getChildAt(i); EditText text = (EditText)
		 * view.findViewById(R.id.new_price_text); String contents =
		 * text.getText().toString(); Pair<UserDetails, Double> person =
		 * person_cost_pairs.get(i);
		 * person.setSecond(Double.parseDouble(contents)); }
		 */

		for (int i = 0; i < person_cost_pairs.size(); i++) {
			Pair<UserDetails, Double> person = person_cost_pairs.get(i);
			Log.v(TAG, person.toString());
		}

		/* Create a map from the first name to the price */
		StringBuilder strB = new StringBuilder();
		Map<Integer, Double> personPriceMap = new HashMap<Integer, Double>();
		for (Pair<UserDetails, Double> pair : person_cost_pairs) {
			personPriceMap.put(pair.getFirst().getUserid(), pair.getSecond());
		}

		/* Generate a string of the form "userid:amount,userid:amount,..." */
		for (Iterator<UserDetails> itr = owers.iterator(); itr.hasNext();) {
			UserDetails user = itr.next();
			strB.append(user.getUserid() + ":"
					+ personPriceMap.get(user.getUserid()));
			if (itr.hasNext())
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
					params[3], params[4], params[5], params[6], params[7],
					params[8]);
		}

		@Override
		protected void onPostExecute(Boolean result) {
			// Checks the result of running addToTransactions
			if (result) {
				String msg = "Transaction added!";
				// Toast message
				MyToast.toastMessage(NewTransaction.this, msg);
				/* Need to somehow know where to go back to */

				if(getIntent().getBooleanExtra("fromPartialPay", false)) {
					startActivity(getIntent().setClass(thisActivity, PerPerson.class).putExtra("fromPartialPay", false));
				}
				
				thisActivity.finish();

				/*
				 * 
				 * SharedPreferences sharedPref =
				 * getSharedPreferences(getString(R.string.preference_file_key),
				 * Context.MODE_PRIVATE); int view_mode =
				 * sharedPref.getInt(getString(R.string.view_mode),
				 * MainMenu.PER_PERSON_VIEW); Intent intent = getIntent();
				 * //intent.putExtra(MainActivity.USER_KEY, user); view_mode =
				 * MainMenu.PER_TRANSACTION_VIEW; if (view_mode ==
				 * MainMenu.PER_PERSON_VIEW)
				 * intent.setClass(NewTransaction.this, PerPerson.class); else
				 * intent.setClass(NewTransaction.this, PerItem.class);
				 */

				// startActivity(intent);
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

	private boolean addToTransactions(String transUserid, String transName,
			String transDesc, String transAmount, String transDate,
			String transUrgency, String transOwerCount, String transOwers,
			String owerIdPartialPairs) {
		List<NameValuePair> nameValueP = new ArrayList<NameValuePair>(3);
		nameValueP.add(new BasicNameValuePair("op", "newTransaction"));
		nameValueP.add(new BasicNameValuePair("userid", transUserid));
		nameValueP.add(new BasicNameValuePair("name", transName));
		nameValueP.add(new BasicNameValuePair("desc", transDesc));
		nameValueP.add(new BasicNameValuePair("date", transDate));
		nameValueP.add(new BasicNameValuePair("total_amount", transAmount));
		nameValueP.add(new BasicNameValuePair("urgency", transUrgency));
		nameValueP
				.add(new BasicNameValuePair("user_owes_count", transOwerCount));
		nameValueP.add(new BasicNameValuePair("oweridIdAmount", transOwers));
		nameValueP.add(new BasicNameValuePair("owerIdPartialPairs",
				owerIdPartialPairs));

		try {
			// address should be the http address of the server side code.
			InputStream in = CustomHttpClient.executeHttpPost(MainActivity.URL
					+ MainActivity.TRANSACTION, nameValueP);

			// errorMessage = HttpReaders.readIt(in, 16);

			Log.v(TAG, errorMessage);
			// Handle JSONstring
			JsonReader jr = new JsonReader(new BufferedReader(
					new InputStreamReader(in)));
			jr.beginObject();
			int response = JsonCustomReader.readJSONRetCode(jr, in);
			jr.endObject();
			Pair<String, Boolean> pair = TransactionHelper
					.handleResponse(response);
			errorMessage = pair.getFirst();

			return pair.getSecond();

		} catch (Exception e) {

			errorMessage = e.getMessage();

		}

		return false;
	}

	/* Functionalities for the button */
	public void equalSplit(View view) {
		if (!hasTotal()) {
			MyToast.toastMessage(NewTransaction.this, "Enter amount");
			return;
		} 
		EditText _transAmount = (EditText) findViewById(R.id.amount);
		total_amount = Double.parseDouble(_transAmount.getText().toString());
		EqSplitDialog enterAD = new EqSplitDialog();
        enterAD.show(getFragmentManager(), "Equal Split");
	}

	private void eqsplit(double amount) {
		
		/* Get the amount individual needs to pay */
			int userCount = (include_myself)? 1 : 0;
		
			double individual_amount = MyMath.round(amount
					/ (person_cost_pairs.size() + userCount));

			for (int i = 0; i < person_cost_pairs.size(); i++) {

				if (i == person_cost_pairs.size() - 1 + userCount) {
					// the last entry will need
					// to pay the remaining
					individual_amount = MyMath.round(amount);
				}
				Log.v(TAG, "Individual_amount is: " + individual_amount);
				// text.setText(Double.toString(individual_amount));
				Pair<UserDetails, Double> person = person_cost_pairs.get(i);
				person.setSecond(individual_amount);
				amount -= individual_amount;

			}
			personAdapter.notifyDataSetChanged();
			eq_flag = true;
		
	}

	/* Once returned from the NewPerson */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			Log.v(TAG, "result ok");
			addOwers(data);
			if (eq_flag)
				eqsplit(total_amount);
			personAdapter.notifyDataSetChanged();
		} else {
			Log.v(TAG, "result not ok");
		}
	}

	/* Update owers view */
	private void addOwers(Intent data) {

		ArrayList<? extends Parcelable> owerinfo = data.getExtras()
				.getParcelableArrayList(Transactions.FRIENDIDS_STR);

		ArrayList<Pair<UserDetails, Double>> pcp2 = null;
		/* Must delete user from the pair list when someone is deleted */
		if (owerinfo.size() < person_cost_pairs.size()) {
			Log.v(TAG, "enter delete");
			pcp2 = new ArrayList<Pair<UserDetails, Double>>(owerinfo.size());
			for (Pair<UserDetails, Double> pair : person_cost_pairs) {
				UserDetails existent_user = pair.getFirst();
				for (Parcelable parcelable : owerinfo) {
					if (parcelable instanceof UserInfo) {
						UserInfo userinfo = (UserInfo) parcelable;
						UserDetails user = userinfo.getUserDetail();
						if (existent_user.equals(user)) {
							Log.v(TAG, existent_user.getFirstName()
									+ " still in list");

							// User still in the new list so insert it
							pcp2.add(pair);
							break;
						}
					}
				}
			}
			person_cost_pairs = pcp2;

			for (Pair<UserDetails, Double> pair : pcp2) {
				Log.v(TAG, pair.getFirst().getFirstName());
			}
		}

		for (Pair<UserDetails, Double> pair : person_cost_pairs) {
			Log.v(TAG, pair.getFirst().getFirstName());
		}

		/* Add to owers */
		owers = new ArrayList<UserDetails>();
		for (Parcelable parcelable : owerinfo) {
			if (parcelable instanceof UserInfo) {
				UserInfo userinfo = (UserInfo) parcelable;
				UserDetails user = userinfo.getUserDetail();

				Log.v(TAG, user.getFirstName() + " added ");

				owers.add(user);

				/* Do not add if the person is already in the list */
				Pair<UserDetails, Double> pair = new Pair<UserDetails, Double>(
						user, 0.0);
				if (!person_cost_pairs.contains(pair)) {
					person_cost_pairs.add(pair);
					Log.v(TAG, "adding " + user.getFirstName());
				}
			}
		}
		personAdapter = new PersonAdapter(person_cost_pairs,
				NewTransaction.this);
		personList.setAdapter(personAdapter);
		// personAdapter.notifyDataSetChanged();
		Log.v(TAG, "received friends");

	}

	/*
	 * Methods to validate the input
	 */
	public boolean validateInput() {

		ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		if (!ConnectionHelper.checkNetworkConnection(connMgr)) {
			MyToast.toastMessage(NewTransaction.this,
					"No network connection, retry after a few seconds");
			return false;
		}

		if (!hasItemName()) {
			MyToast.toastMessage(NewTransaction.this, "Missing an item name");
			return false;
		}

		if (!hasTotal()) {
			MyToast.toastMessage(NewTransaction.this, "Missing a total");
			return false;
		}

		if (!hasFriendEntry()) {
			MyToast.toastMessage(NewTransaction.this, "Missing a person");
			return false;
		}

		if (!validateAmount()) {
			MyToast.toastMessage(NewTransaction.this, "Amount doens't match");
			return false;
		}

		if (!hasValidPriceInput()) {
			return false;
		}

		return true;

	}

	private boolean hasValidPriceInput() {
		EditText _transAmount = (EditText) findViewById(R.id.amount);
		double amount = Double.parseDouble(_transAmount.getText().toString());

		/* Need to read price entered in each edit text */
		for (int i = 0; i < person_cost_pairs.size(); i++) {
			View view = personList.getChildAt(i);
			EditText text = (EditText) view.findViewById(R.id.new_price_text);
			String contents = text.getText().toString();
			Pair<UserDetails, Double> person = person_cost_pairs.get(i);
			try {
				person.setSecond(Double.parseDouble(contents));
			} catch (NumberFormatException e) {
				MyToast.toastMessage(NewTransaction.this,
						"Enter a number please");
				return false;
			}
			if (person.getSecond() <= MyMath.EPSILON) {
				MyToast.toastMessage(NewTransaction.this,
						"Invalid number.\n Enter a positive value ");
				return false;
			}
		}
		return true;
	}

	private boolean hasFriendEntry() {
		return owers != null && owers.size() > 0;
	}

	private boolean hasItemName() {
		EditText _transName = (EditText) findViewById(R.id.item_text);
		return !(_transName.getText() == null || _transName.getText()
				.toString().equals(""));

	}

	private boolean hasTotal() {
		EditText _transName = (EditText) findViewById(R.id.amount);
		return !(_transName.getText() == null || _transName.getText()
				.toString().equals(""));

	}

	private boolean validateAmount() {
		EditText _transAmount = (EditText) findViewById(R.id.amount);
		double amount = Double.parseDouble(_transAmount.getText().toString());
//		double individualAmount = amount 
		double sum = 0;
		/* Need to read price entered in each edit text */
		for (int i = 0; i < person_cost_pairs.size(); i++) {
			View view = personList.getChildAt(i);
			EditText text = (EditText) view.findViewById(R.id.new_price_text);
			String contents = text.getText().toString();
			Pair<UserDetails, Double> person = person_cost_pairs.get(i);
			person.setSecond(Double.parseDouble(contents));
			sum += person.getSecond();
		}

		if (include_myself) {
			double individual = amount / (person_cost_pairs.size() +1);
			_transAmount.setText(""+sum);
			return MyMath.practically_equal_loose(amount, sum+individual);
		}
		
		return MyMath.practically_equal(amount, sum);
	}

	@Override
	public void onDialogPositiveClick(DialogFragment dialog) {
		include_myself = true;
		eqsplit(total_amount);
	}

	@Override
	public void onDialogNegativeClick(DialogFragment dialog) {
		include_myself = false;
		eqsplit(total_amount);
	}

}
