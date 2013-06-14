package com.example.moneyapp;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.helpers.AdminHelper;
import com.example.helpers.ConnectionHelper;
import com.example.helpers.CustomHttpClient;
import com.example.helpers.HttpReaders;
import com.example.helpers.StringFilter;
import com.example.helpers.metadata.UserDetails;
import com.example.json.JsonCustomReader;

public class MainActivity extends Activity {
	public static final String TAG = "MainActivity";
	public static final int DEFAULT_DATA_LENGTH = 1000;
	public static final String USER_KEY = "com.example.moneyapp.USERDETAILS";
	public static final String edge02 = "http://146.169.52.2:59999";
	public static final String pixel20 = "http://146.169.53.180:59999";
	public static final String joMachine = "http://129.31.227.146:8080/MoneyDatabase";
	public static final String joMachineEmulator = "http://10.0.2.2:8080/MoneyDatabase";
	public static final String joMachineHome = "http://82.26.23.66:8080/MoneyDatabase";
	public static final String url = joMachineEmulator;// "http://146.169.53.14:59999";
	public static final String login = "/Login";
	public static final String TRANSACTION = "/Transaction";
	private String errorMessage;
	private UserDetails userDetails;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		errorMessage = "";
		userDetails = null;
	}

	@Override
	protected void onStart() {
		super.onStart();
 
//		Intent intent = new Intent(MainActivity.this, MainMenu.class);
//		startActivity(intent);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void loginHandler(View view) {

		EditText passwordText = (EditText) findViewById(R.id.password);
		EditText phoneText = (EditText) findViewById(R.id.phoneNumber);
		String password = passwordText.getText().toString();
		String phoneNo = phoneText.getText().toString();

		if (StringFilter.isIllegal(password) || StringFilter.isIllegal(phoneNo)) {
			toastMessage("Missing entry");
			return;
		}

		ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		if (ConnectionHelper.checkNetworkConnection(connMgr))
			new PasswordVerifier().execute(phoneNo, password);
		else 
			toastMessage("No network connection");
		
			
	}

	private class PasswordVerifier extends AsyncTask<String, Void, Boolean> {

		@Override
		protected Boolean doInBackground(String... params) {
			return verifyPassword(params[0], params[1]);
		}

		@Override
		protected void onPostExecute(Boolean result) {

			if (result) {
				// Toast message
				toastMessage("Login successful!");

				Intent intent = new Intent(MainActivity.this, MainMenu.class);
				intent.putExtra(USER_KEY, userDetails);
				startActivity(intent);
			} else {
				toastMessage(errorMessage);
			}
		}

	}

	private boolean verifyPassword(String phoneNo, String password) {
		List<NameValuePair> nameValueP = new ArrayList<NameValuePair>(3);
		nameValueP.add(new BasicNameValuePair("op", "checkPassword"));
		nameValueP.add(new BasicNameValuePair("phone", phoneNo));
		nameValueP.add(new BasicNameValuePair("password", password));

		try {
			InputStream in = CustomHttpClient.executeHttpPost(url + login,
					nameValueP);
			Log.v(TAG,"Readfine?");
			//errorMessage = HttpReaders.readIt(in, 1000);
			//Log.v(TAG, errorMessage);
			
			Pair<Integer,UserDetails> result = JsonCustomReader.readJsonUser(in);
			userDetails = result.second;

			Log.v(TAG,"still fine?");
			
			
			 //Handle JSONstring

			int response = result.first;			
			Pair<String, Boolean> pair = AdminHelper.handleResponse(response);
			errorMessage = pair.first;
			return pair.second;
		} catch (Exception e) {
			errorMessage = e.toString();
		}

		return false;
	}

	public void signInHandler(View view) {
		Intent intent = new Intent(MainActivity.this, SignIn.class);
		startActivity(intent);
	}


	private void toastMessage(String msg) {
		CharSequence feedbackMsg = msg;
		Toast toast = Toast.makeText(getApplicationContext(), feedbackMsg, Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
		
	}
	
}
