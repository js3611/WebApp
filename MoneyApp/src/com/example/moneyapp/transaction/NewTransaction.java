package com.example.moneyapp.transaction;

import java.util.ArrayList;
import java.util.Calendar;

import com.example.helpers.ConnectionHelper;
import com.example.moneyapp.MainMenu;
import com.example.moneyapp.R;
import com.example.moneyapp.SignIn;
import com.example.moneyapp.R.id;
import com.example.moneyapp.R.layout;
import com.example.moneyapp.R.menu;
import com.example.moneyapp.SignIn.registerUser;

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

	// The List view
	ListView personList;
	// A list of data for each entry, which the adapter retrieves from.
	ArrayList<Pair<String, Double>> person_cost_pairs;

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

	
	
	
	public void newTransactionHandler (View view) {
		EditText _transName = (EditText) findViewById(R.id.item_name);
		EditText _transDesc = (EditText) findViewById(R.id.item_description);
		EditText _transAmount = (EditText) findViewById(R.);
		
		Calendar cal = Calendar.getInstance();
		int day = cal.get(Calendar.DATE);
		int month = cal.get(Calendar.MONTH);
		int year = cal.get(Calendar.YEAR);
		
		int urgency = ;
	
		String transName = _transName.getText().toString();
		String transDesc = _transDesc.getText().toString();
		String transAmount = _transAmount.getText().toString();
		String transDate = Integer.toString((year*1000)+(month*10)+day);
		String transUrgency = Integer.toString(urgency);
		
		ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		
		
		new addTransaction().execute(transName, transDesc, transAmount, transDate, transUrgency);
								
		}
		else if (!passwordsMatch(enterPasswordString, checkPasswordString)) {
			errorView.setText("Entered Passwords do not match!");
		}
		else if (!ConnectionHelper.checkNetworkConnection(connMgr)){
			errorView.setText("No network connection");
		}
 
		
	}
	
	private class addTransaction extends AsyncTask<String, Void, Boolean> {
		
		@Override
		protected Boolean doInBackground(String... params) {
			return signUpWith(params[0],params[1],params[2],params[3]);
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			if (result) {
				
				// Toast message
				Context context = getApplicationContext();
				CharSequence feedbackMsg = "Sign up successful!";
				int duration = Toast.LENGTH_SHORT;
				Toast toast = Toast.makeText(context, feedbackMsg, duration);
				toast.setGravity(Gravity.CENTER,0,0);
				toast.show();	
				
				Intent intent = new Intent(SignIn.this, MainMenu.class);
				startActivity(intent);	
			} else {
				errorView.setText(errorMessage);
			}
		}
	}	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
