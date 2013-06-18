package com.example.moneyapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.JsonToken;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.EditText;

import com.example.helpers.AdminHelper;
import com.example.helpers.ConnectionHelper;
import com.example.helpers.CustomHttpClient;
import com.example.helpers.MyToast;
import com.example.helpers.StringFilter;
import com.example.helpers.metadata.FriendsList;
import com.example.helpers.metadata.Pair;
import com.example.helpers.metadata.UserDetails;
import com.example.json.JsonCustomReader;

public class MainActivity extends Activity {
	public static final String TAG = "MainActivity";
	public static final int DEFAULT_DATA_LENGTH = 1000;
	//public static final String thatcomputer = "http://146.169.53.38:59999";
	public static final String USER_KEY = "com.example.moneyapp.USERDETAILS";
	public static final String edge02 = "http://146.169.52.2:59999";
	public static final String pixel20 = "http://146.169.53.180:59999";
	public static final String joMachine = "http://129.31.229.10:8080/MoneyDatabase";
	public static final String joMachineEmulator = "http://10.0.2.2:8080/MoneyDatabase";
	public static final String URL = joMachine;// "http://146.169.53.14:59999";
	public static final String LOGIN = "/Login";
	public static final String TRANSACTION = "/Transaction";
	public static final String FRIENDS = "/Friends";
	private String errorMessage;
	private UserDetails user;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		errorMessage = "";
		user = null;
		EditText passwordText = (EditText) findViewById(R.id.password);
		EditText phoneText = (EditText) findViewById(R.id.phoneNumber);
		passwordText.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				EditText passwordText = (EditText) v.findViewById(R.id.password);
				passwordText.setText("");
			}
		});
		passwordText.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					EditText passwordText = (EditText) v
							.findViewById(R.id.password);
					passwordText.setText("");
				}
			}
		});
		phoneText.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					EditText phoneText = (EditText) v
							.findViewById(R.id.phoneNumber);
					phoneText.setText("");
				}
			}
		});
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
		//Log.v(TAG, "Log in on process");
		EditText passwordText = (EditText) findViewById(R.id.password);
		EditText phoneText = (EditText) findViewById(R.id.phoneNumber);
		String password = passwordText.getText().toString();
		String phoneNo = phoneText.getText().toString();

		if (StringFilter.isIllegal(password) || StringFilter.isIllegal(phoneNo)) {
			MyToast.toastMessage(MainActivity.this,"Missing entry");
			return;
		}

		ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		if (ConnectionHelper.checkNetworkConnection(connMgr))
			new PasswordVerifier().execute(phoneNo, password);
		else 
			MyToast.toastMessage(MainActivity.this,"No network connection");
		
			
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
				MyToast.toastMessage(MainActivity.this,"Login successful!");

				Intent intent = new Intent(MainActivity.this, MainMenu.class);
				intent.putExtra(USER_KEY, user);
				startActivity(intent);
			} else {
				MyToast.toastMessage(MainActivity.this,errorMessage);
			}
		}

	}

	private boolean verifyPassword(String phoneNo, String password) {
		List<NameValuePair> nameValueP = new ArrayList<NameValuePair>(3);
		nameValueP.add(new BasicNameValuePair("op", "checkPassword"));
		nameValueP.add(new BasicNameValuePair("phone", phoneNo));
		nameValueP.add(new BasicNameValuePair("password", password));

		try {
			InputStream in = CustomHttpClient.executeHttpPost(URL+ LOGIN,
					nameValueP);

			return processInput(in);
		} catch (Exception e) {
			errorMessage = e.toString();
		}

		return false;
	}

	private boolean processInput(InputStream in) {
		JsonReader jr;
		try {
			jr = new JsonReader(new BufferedReader(new InputStreamReader(in,
					"UTF-8")));

			jr.setLenient(true);
			jr.beginObject();

			/* Read ReturnCode */
			Pair<String, Boolean> pair = AdminHelper
					.handleResponse(JsonCustomReader.readJSONRetCode(jr, in));

			if (!pair.getSecond()) {
				errorMessage = pair.getFirst();
				return false;
			}
			/* Read User detail */
			user = JsonCustomReader.readJSONUser(jr, in);
			/* Read Friends */
			if (jr.peek() == JsonToken.END_OBJECT) {
				//Log.v(TAG, "No FRIENDS");
				FriendsList.createInstance(new ArrayList<UserDetails>());
				return true;
			}
			ArrayList<UserDetails> friends = JsonCustomReader.readJSONFriends(jr, in);
			FriendsList.createInstance(friends);
			jr.endObject();

			//Log.v(TAG, FriendsList.showFriends());
			return true;
		} catch (UnsupportedEncodingException e) {
			errorMessage = e.getMessage();
		} catch (IOException e) {
			errorMessage = e.getMessage();
		}
		return false;

	}

	public void signInHandler(View view) {
		Intent intent = new Intent(MainActivity.this, SignUp.class);
		startActivity(intent);
	}



	
}
