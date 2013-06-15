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
import com.example.helpers.DateGen;
import com.example.helpers.HttpReaders;
import com.example.helpers.TransactionHelper;
import com.example.helpers.metadata.Pair;
import com.example.helpers.metadata.UserDetails;
import com.example.helpers.metadata.UserInfo;
import com.example.json.JsonCustomReader;
import com.example.moneyapp.MainActivity;
import com.example.moneyapp.R;

public class NewTransaction extends Activity {

	/* Debug */
	private static final String TAG = "New Transaction";
	private String errorMessage = "";
	private double epsilon = 0.0000001;

	// The List view
	private ListView personList;
	private PersonAdapter personAdapter;
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

		user = UserDetails.getUser(getIntent());
		
		person_cost_pairs = new ArrayList<Pair<String, Double>>();
		personAdapter =new PersonAdapter(person_cost_pairs, this);		
		personList.setAdapter(personAdapter);
		registerForContextMenu(personList);

		/* set the behaviour of the list*/
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
					
					Intent i = new Intent(getApplicationContext(), NewPerson.class).putExtra(MainActivity.USER_KEY,user);
					startActivityForResult(i, 1);
					//startActivity(getIntent().setClass(NewTransaction.this,
						//	NewPerson.class));
					
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


	public void newTransactionHandler(View view) {
		
		if (!validateInput()) 
			return;
		
		EditText _transName = (EditText) findViewById(R.id.item_text);
		EditText _transDesc = (EditText) findViewById(R.id.item_description_text);
		EditText _transAmount = (EditText) findViewById(R.id.amount);
	
		Calendar cal = Calendar.getInstance();
		int day = cal.get(Calendar.DATE);
		int month = cal.get(Calendar.MONTH);
		int year = cal.get(Calendar.YEAR);
	
		int urgency = 1; // TODO GET FROM SPINNER
	
		String transUserid =  Integer.toString(user.getUserid());
		String transName = _transName.getText().toString();
		String transDesc = _transDesc.getText().toString();
		String transAmount = _transAmount.getText().toString();
		String transDate = DateGen.getDate();
		String transUrgency = Integer.toString(urgency);
		String transOwerCount = Integer.toString(owers.size());
		String transOwers = oweridIdAmount();
		String transOwerIdPartialPairs = owerIdPartialPairs();
	

		new AddTransaction().execute(transUserid, transName, transDesc, transAmount,
					 	 transDate, transUrgency, transOwerCount, transOwers,transOwerIdPartialPairs);
	
	}


	/* Update owers view */
	private void addOwers(Intent data) {

			owers = new ArrayList<UserDetails>();
			ArrayList<? extends Parcelable> owerinfo = data.getExtras().getParcelableArrayList(Transactions.FRIENDIDS_STR); 
			for (Parcelable parcelable : owerinfo) {
				if (parcelable instanceof UserInfo) {
					UserInfo userinfo = (UserInfo) parcelable;
					UserDetails user = userinfo.getUserDetail();
					owers.add(user);
					person_cost_pairs.add(new Pair<String, Double>(user.getFirstName(), 0.0));
					Log.v(TAG,"adding"+user.getFirstName());
				}
			}
			Log.v(TAG,"received friends");
		
	}



	private String owerIdPartialPairs() {
		StringBuilder strB = new StringBuilder();
		/* Set everyone to zero */
		/* Generate a string of the form "userid-amount,userid-amount,..."*/
		for (Iterator<UserDetails> itr = owers.iterator(); itr.hasNext();) {
			UserDetails user = itr.next();
			strB.append(user.getUserid() + "-" + 0.0);
			if(itr.hasNext())
				strB.append(",");
		}
		String str = strB.toString();
		Log.v(TAG, str);
		
		return str;
	}

	/* Read amount from each person, produce the required string to send it to the server */
	private String oweridIdAmount() {
		
		/* Need to read price entered in each edit text */
		for (int i = 0; i < person_cost_pairs.size(); i++) {
			View view = personList.getChildAt(i);
			EditText text = (EditText) view.findViewById(R.id.new_price_text);
			String contents = text.getText().toString();
			Pair<String, Double> person = person_cost_pairs.get(i);
			person.setSecond(Double.parseDouble(contents));
		}
	
		/* Create a map from the first name to the price*/
		StringBuilder strB = new StringBuilder();
		Map<String, Double> personPriceMap = new HashMap<String, Double>();
		for (Pair<String, Double> pair : person_cost_pairs) {
			personPriceMap.put(pair.getFirst(),pair.getSecond());
		}
		
		/* Generate a string of the form "userid:amount,userid:amount,..."*/
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
					params[3], params[4],params[5],params[6],params[7],params[8]);
		}

		@Override
		protected void onPostExecute(Boolean result) {
			// Checks the result of running addToTransactions
			if (result) {
				String msg = "Transaction added!";
				// Toast message
				toastMessage(msg);
				/* Need to somehow know where to go back to */
				Intent intent = getIntent().setClass(NewTransaction.this,
						PerPerson.class);
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

	private boolean addToTransactions(String transUserid, String transName, String transDesc,
			String transAmount, String transDate, String transUrgency, String transOwerCount, String transOwers, String owerIdPartialPairs) {
		List<NameValuePair> nameValueP = new ArrayList<NameValuePair>(3);
		nameValueP.add(new BasicNameValuePair("op", "newTransaction"));
		nameValueP.add(new BasicNameValuePair("userid",transUserid));
		nameValueP.add(new BasicNameValuePair("name", transName));
		nameValueP.add(new BasicNameValuePair("desc", transDesc));
		nameValueP.add(new BasicNameValuePair("date", transDate));
		nameValueP.add(new BasicNameValuePair("total_amount", transAmount));
		nameValueP.add(new BasicNameValuePair("urgency", transUrgency));
		nameValueP.add(new BasicNameValuePair("user_owes_count", transOwerCount));
		nameValueP.add(new BasicNameValuePair("oweridIdAmount", transOwers));
		nameValueP.add(new BasicNameValuePair("owerIdPartialPairs", owerIdPartialPairs));
		
		try {
			// address should be the http address of the server side code.
			InputStream in = CustomHttpClient.executeHttpPost(MainActivity.URL
					+ MainActivity.TRANSACTION, nameValueP);
			
			//errorMessage = HttpReaders.readIt(in, 16);
			
			Log.v(TAG, errorMessage);
			// Handle JSONstring
			int response = JsonCustomReader.readJsonRetCode(in);
			Pair<String, Boolean> pair = TransactionHelper
					.handleResponse(response);
			errorMessage = pair.getFirst();
			return pair.getSecond();

		} catch (Exception e) {
			
			errorMessage = e.getMessage();

		}

		return false;
	}
	
	private void toastMessage(String msg) {
		Context context = getApplicationContext();
		CharSequence feedbackMsg = msg;
		int duration = Toast.LENGTH_SHORT;
		Toast toast = Toast.makeText(context, feedbackMsg, duration);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode==RESULT_OK) {
			Log.v(TAG,"result ok");
			addOwers(data);
			personAdapter.notifyDataSetChanged();
		} else {
			Log.v(TAG,"result not ok");
		}
	}
	
	public boolean validateInput() {
		
		ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		if (!ConnectionHelper.checkNetworkConnection(connMgr)) {
			toastMessage("No network connection, retry after a few seconds");
			return false; 
		}
		
		if(!hasItemName()) {
			toastMessage("Missing an item name");
			return false;
		}
		
		if(!hasTotal()) {
			toastMessage("Missing a total");
			return false;
		}
		
		if(!hasFriendEntry()) {
			toastMessage("Missing a person");
			return false;
		}
		
		if(!validateAmount()) {
			toastMessage("Amount doens't match");
			return false;
		}
		
		if(!hasValidPriceInput()) {
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
			Pair<String, Double> person = person_cost_pairs.get(i);
			try {
				person.setSecond(Double.parseDouble(contents));
			} catch (NumberFormatException e) {
				toastMessage("Enter a number please");
				return false;
			}
			if(person.getSecond() <= epsilon) {
				toastMessage("Invalid number.\n Enter a positive value ");
				return false;
			}
		}
		return true;
	}


	private boolean hasFriendEntry() {
		return owers != null && owers.size()>0;
	}


	private boolean hasItemName() {
		EditText _transName = (EditText) findViewById(R.id.item_text);
		return !(_transName.getText()==null || _transName.getText().toString().equals(""));
		
	}
	
	private boolean hasTotal() {
		EditText _transName = (EditText) findViewById(R.id.amount);
		return !(_transName.getText()==null || _transName.getText().toString().equals(""));
		
	}

	private boolean validateAmount() {
		EditText _transAmount = (EditText) findViewById(R.id.amount);
		double amount = Double.parseDouble(_transAmount.getText().toString());
		
		double sum = 0;
		/* Need to read price entered in each edit text */
		for (int i = 0; i < person_cost_pairs.size(); i++) {
			View view = personList.getChildAt(i);
			EditText text = (EditText) view.findViewById(R.id.new_price_text);
			String contents = text.getText().toString();
			Pair<String, Double> person = person_cost_pairs.get(i);
			person.setSecond(Double.parseDouble(contents));
			sum += person.getSecond();
		}
		
		return practically_equal(amount,sum);
	}

	private boolean practically_equal(double amount, double sum) {
		return Math.abs(amount-sum) < epsilon;
	}
}
