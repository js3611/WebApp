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
import com.example.json.JsonCustomReader;

public class MainActivity extends Activity {

	public static final int DEFAULT_DATA_LENGTH = 1000;
	
	public static final String edge02 = "http://146.169.52.2:59999";
	public static final String pixel20 = "http://146.169.53.180:59999";
	public static final String joMachine = "http://129.31.224.228:8080/MoneyDatabase";
	public static final String url = joMachine;//"http://146.169.53.14:59999";
	public static final String login = "/Login";
	private TextView errorView;
	private String errorMessage;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		errorView = (TextView) findViewById(R.id.errorView);
		errorMessage = "";
		errorView.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
		errorView.setText("e.g. phone: 123, pw:123");
		errorView.setTextColor(Color.RED);
	}

	@Override
	protected void onStart() {
		super.onStart();
		/*
		 * Intent intent = new Intent(MainActivity.this, MainMenu.class);
		 * startActivity(intent);
		 */
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
			errorView.setText("Missing entries");
			errorView.setTextColor(Color.RED);
			return;
		}

		ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		if (ConnectionHelper.checkNetworkConnection(connMgr))
			new PasswordVerifier().execute(phoneNo, password);
		else
			errorView.setText("No network connection");
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
				Context context = getApplicationContext();
				CharSequence feedbackMsg = "Log in successful!";
				int duration = Toast.LENGTH_SHORT;
				Toast toast = Toast.makeText(context, feedbackMsg, duration);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();

				Intent intent = new Intent(MainActivity.this, MainMenu.class);
				startActivity(intent);
			} else {
				errorView.setText(errorMessage);
			}
		}

	}

	private boolean verifyPassword(String phoneNo, String password) {
		List<NameValuePair> nameValueP = new ArrayList<NameValuePair>(3);
		nameValueP.add(new BasicNameValuePair("op", "checkPassword"));
		nameValueP.add(new BasicNameValuePair("phone", phoneNo));
		nameValueP.add(new BasicNameValuePair("password", password));

		try {
			InputStream in = CustomHttpClient.executeHttpPost(url+login, nameValueP);
//			InputStream in = CustomHttpClient.executeHttpGet(url+login);
			// Handle JSONstring
//			errorMessage = HttpReaders.readIt(in, 1000);
			int response = JsonCustomReader.readJsonRetCode(in);			
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

}
