package com.example.moneyapp.transaction;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.JsonReader;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.helpers.AdminHelper;
import com.example.helpers.CustomHttpClient;
import com.example.helpers.DateGen;
import com.example.helpers.HttpReaders;
import com.example.helpers.MyToast;
import com.example.helpers.metadata.FriendsList;
import com.example.helpers.metadata.Pair;
import com.example.helpers.metadata.UserDetails;
import com.example.json.JsonCustomReader;
import com.example.moneyapp.MainActivity;
import com.example.moneyapp.MainMenu;
import com.example.moneyapp.R;

public class PerItemDetails extends Activity implements PayDialog.NoticeDialogListener {

	private static final String TAG = "PerItemDetails";
	private String errorMessage;
	
	//Constants
	private int PARTIAL_PAY = 0;
	private int FULLY_PAY = 1;
	
	private PerItemDetails thisActivity;
	private UserDetails user;
	//ID of the person who made the transaction
	private int userid;
	//ID of the transaction
	private int transid;
	private TransactionDetail transaction;
	private ListView owerList;
	private PersonAdapter personAdapter;
	private ArrayList<Pair<UserDetails, Double>> person_cost_pairs;
	private boolean can_delete;
	private double individualAmount;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.transaction_per_item_details);
		// Show the Up button in the action bar.
		setupActionBar();
		
		/* Set fields*/
		person_cost_pairs = new ArrayList<Pair<UserDetails,Double>>();
		owerList = (ListView) findViewById(R.id.PersonList);
		user = UserDetails.getUser(getIntent());
		transid = getIntent().getExtras().getInt(Transactions.TRANSID_STR);
		userid = getIntent().getExtras().getInt(Transactions.USERID_STR);
		individualAmount = getIntent().getExtras().getDouble(Transactions.PRICE_STR);
		Log.v(TAG, "user id:"+userid+" individual amount: "+individualAmount);
		if (userid == user.getUserid()) {
			Log.v(TAG, "can delete");
			can_delete = true;
		}
		thisActivity = this;
		
		
		
	}

	@Override
	protected void onStart() {
		super.onStart();
		new DownloadContent().execute(user.getUserid(),transid);
	}
	
	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {

		getActionBar().setDisplayHomeAsUpEnabled(true);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.per_item_details, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private class DownloadContent extends AsyncTask<Integer, Void, Boolean> {

		private static final String TAG = "PerItemDetails";
		
		@Override
		protected Boolean doInBackground(Integer... params) {
			try {
				String op = "transactionDetails";
				String viewMode = "perItem";
				InputStream in = CustomHttpClient.executeHttpGet(MainActivity.URL+
						MainActivity.TRANSACTION + "?"+
						"op="+op+"&"+ 
						"viewMode="+viewMode+"&"+
						"userid="+params[0] +"&"+
						"transid="+params[1]);
					
				return processInput(in);
			
			} catch (Exception e) {
				Log.v(TAG, e.getMessage());
			}

			return false;
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
				Log.v(TAG, "Reading status");
				
				jr.nextName();
				/* can_delete = */ jr.nextBoolean();
				
				Log.v(TAG, "Reading transactions");
				/* Read UsersTransactionDetails */
				//read the name
				jr.nextName();
				transaction = JsonCustomReader
						.readData(jr, in);
				Log.v(TAG, "Read transactions. Reading users");
				
				// Reading users
				ArrayList<UserDetails> users = JsonCustomReader.readJSONFriends(jr, in);
				fillPersonCostArray(users);
				jr.endObject();
				
				Log.v(TAG, "parsed fine");
											
				return true;
			} catch (UnsupportedEncodingException e) {
				errorMessage = e.getMessage();
			} catch (IOException e) {
				errorMessage = e.getMessage();
			}
			return false;

		}
		
		private void fillPersonCostArray(ArrayList<UserDetails> users) {
			person_cost_pairs = new ArrayList<Pair<UserDetails,Double>>();
			for (UserDetails userDetails : users) {
				person_cost_pairs.add(new Pair<UserDetails, Double>(userDetails, userDetails.getAmount()));
			}
			
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			
			if(result){
			// Set views
				
				if (can_delete) { //If the user made the transaction, see list of ppl 
					personAdapter = new PersonAdapter(person_cost_pairs, thisActivity);
					owerList.setAdapter(personAdapter);
					Button payButton = (Button) findViewById(R.id.pay_button);
					payButton.setText("Edit");
					//registerForContextMenu(transList);
				} else { //Normally see who to pay to 

					//Modify label & amount to pay
					TextView tv = (TextView) findViewById(R.id.who_owe);
					tv.setText("Pays to: ");
					transaction.setPrice(individualAmount);
					
					ArrayList<Pair<UserDetails, Double>> owerArr = new ArrayList<Pair<UserDetails,Double>>();
					UserDetails newOwer = new UserDetails();

					//Get the user's info
					newOwer.setFirstName(FriendsList.getFirstname(userid));
					newOwer.setSurname(FriendsList.getSurname(userid));

					owerArr.add(new Pair<UserDetails, Double>(newOwer, individualAmount));
					personAdapter = new OwerAdapter(owerArr, thisActivity);
					owerList.setAdapter(personAdapter);
										
				}
				
				
				EditText item_text = (EditText) findViewById(R.id.item_text);
				EditText item_description_text = (EditText) findViewById(R.id.item_description_text);
				EditText total_text = (EditText) findViewById(R.id.amount_text);
				item_text.setText(transaction.getSubject());
				item_description_text.setText(transaction.getDescription());
				total_text.setText(Double.toString(transaction.getPrice()));

				
			} else {
				MyToast.toastMessage(thisActivity, errorMessage);
			}
		}
	}
	
	public void handlePayment(View view) {
		if (!can_delete) {
			Log.v(TAG, "Making a payment (Per Item)");
	        PayDialog payD = PayDialog.newInstance(MainMenu.PER_TRANSACTION_VIEW);
	        payD.show(getFragmentManager(), "Payment");
		} else {
			//update
		}
	}
	
	@Override
	public void onDialogPositiveClick(DialogFragment dialog) {
		// Do nothing, its not visible
	}

	@Override
	public void onDialogNegativeClick(DialogFragment dialog) {
		// Do nothing
		
	}

	@Override
	public void onDialogPartialClick(DialogFragment dialog) {
		//partialRepay		
		new MakePayment().execute("partialRepay",
					Integer.toString(transaction.getUserid()),
					Integer.toString(/*transaction.getOwesuserid()*/user.getUserid()),
					Integer.toString(transaction.getTransactionID()),
					Double.toString(5.0));
	}

	@Override
	public void onDialogFullyClick(DialogFragment dialog) {
		//debtRepaid
		new MakePayment().execute("debtRepay",
				Integer.toString(transaction.getUserid()),
				Integer.toString(transaction.getOwesuserid()));
	}
	
	private class MakePayment extends AsyncTask<String, Void, Boolean>{

		@Override
		protected Boolean doInBackground(String... params) {
			try {

				for (String string : params) {
					Log.v(TAG, "Printing params" + string);
				}
				
				String url = MainActivity.URL + MainActivity.TRANSACTION;			
				List<NameValuePair> nameValueP = new ArrayList<NameValuePair>(3);
				nameValueP.add(new BasicNameValuePair("op", params[0]));
				nameValueP.add(new BasicNameValuePair("userid", params[1]));
				nameValueP.add(new BasicNameValuePair("owesuserid", params[2]));
				nameValueP.add(new BasicNameValuePair("transid", params[3]));
				nameValueP.add(new BasicNameValuePair("new_partial_pay", params[4]));
				nameValueP.add(new BasicNameValuePair("date", DateGen.getDate()));
				
				InputStream in = CustomHttpClient
						.executeHttpPost(url,nameValueP);

				Log.v(TAG,HttpReaders.readIt(in, 1000));
				
				return processInput(in);


			} catch (Exception e) {
				Log.v(TAG, e.getMessage());
			}
			return false;
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

				errorMessage = pair.getFirst();
				return pair.getSecond();
				
			} catch (UnsupportedEncodingException e) {
				errorMessage = e.getMessage();
			} catch (IOException e) {
				errorMessage = e.getMessage();
			}
			return false;

			
		}

		@Override
		protected void onPostExecute(Boolean result) {
			
			if (!result) {
				MyToast.toastMessage(thisActivity, errorMessage);
				return;
			}
			MyToast.toastMessage(thisActivity, "Payment successful!");
			
			startActivity(getIntent().setClass(thisActivity, PerItem.class));
		}
		
	}
	
}
