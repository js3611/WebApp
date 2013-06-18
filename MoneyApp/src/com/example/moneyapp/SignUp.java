package com.example.moneyapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.JsonToken;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.helpers.AdminHelper;
import com.example.helpers.ConnectionHelper;
import com.example.helpers.CustomHttpClient;
import com.example.helpers.metadata.FriendsList;
import com.example.helpers.metadata.Pair;
import com.example.helpers.metadata.UserDetails;
import com.example.json.JsonCustomReader;

public class SignUp extends Activity {

	public static final String TAG = "SignUp";
	private TextView errorView;
	private String errorMessage;
	private UserDetails userDetails;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign_up);
		errorView = (TextView) findViewById(R.id.errorView);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.sign_up, menu);
		return true;
	}

	/*
	 * Invoked when the Register button is pressed. Reads input from user and
	 * call asynchronous task to handle
	 */
	public void signupHandler(View view) {
		EditText phonenumber = (EditText) findViewById(R.id.enterNumber);
		EditText firstnameText = (EditText) findViewById(R.id.enterFirstName);
		EditText surnameText = (EditText) findViewById(R.id.enterSurname);
		EditText enterPasswordText = (EditText) findViewById(R.id.enterPassword);
		EditText checkPasswordText = (EditText) findViewById(R.id.checkPassword);

		String enterPasswordString = enterPasswordText.getText().toString();
		String checkPasswordString = checkPasswordText.getText().toString();
		String firstnameString = firstnameText.getText().toString();
		String surnameString = surnameText.getText().toString();
		String phoneNum = phonenumber.getText().toString();

		ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

		if (ConnectionHelper.checkNetworkConnection(connMgr)
				&& passwordsMatch(enterPasswordString, checkPasswordString)) {
			new RegisterUser().execute(firstnameString, surnameString,
					enterPasswordString, phoneNum);

		} else if (!passwordsMatch(enterPasswordString, checkPasswordString)) {
			errorView.setText("Entered Passwords do not match!");
		} else if (!ConnectionHelper.checkNetworkConnection(connMgr)) {
			errorView.setText("No network connection");
		}

	}

	/* Asynchronous task to register user in database */
	private class RegisterUser extends AsyncTask<String, Void, Boolean> {

		@Override
		protected Boolean doInBackground(String... params) {
			return signUpWith(params[0], params[1], params[2], params[3]);
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (result) {
				// Toast message
				Context context = getApplicationContext();
				CharSequence feedbackMsg = "Sign up successful!";
				int duration = Toast.LENGTH_SHORT;
				Toast toast = Toast.makeText(context, feedbackMsg, duration);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();

				Intent intent = new Intent(SignUp.this, MainMenu.class);
				intent.putExtra(MainActivity.USER_KEY, userDetails);
				startActivity(intent);
			} else {
				errorView.setText(errorMessage);
			}
		}
	}

	private boolean signUpWith(String firstname, String surname,
			String password, String phone) {
		
		List<NameValuePair> nameValueP = new ArrayList<NameValuePair>(5);
		nameValueP.add(new BasicNameValuePair("op", "newAccount"));
		nameValueP.add(new BasicNameValuePair("firstname", firstname));
		nameValueP.add(new BasicNameValuePair("surname", surname));
		nameValueP.add(new BasicNameValuePair("password", password));
		nameValueP.add(new BasicNameValuePair("phone", phone));

		try {

			InputStream in = CustomHttpClient.executeHttpPost(MainActivity.URL+MainActivity.LOGIN, nameValueP);
			return processInput(in);

		} catch (ClientProtocolException e) {
			Log.v(TAG,"ClientProtocolException");
		} catch (IOException e) {
			Log.v(TAG,"IOException in postData");
		} catch (Exception e) {
			Log.v(TAG,"An Unknown error has occurred!");
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

			errorMessage = pair.getFirst();
			return pair.getSecond();

		} catch (UnsupportedEncodingException e) {
			errorMessage = e.getMessage();
		} catch (IOException e) {
			errorMessage = e.getMessage();
		}
		return false;
	}

	private boolean passwordsMatch(String entered, String check) {
		return check.equals(entered);
	}

}
