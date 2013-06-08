package com.example.moneyapp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
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
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.helpers.AdminHelper;
import com.example.helpers.ConnectionHelper;
import com.example.helpers.HttpReaders;
import com.example.helpers.StringFilter;

public class MainActivity extends Activity {

	TextView errorView;
	String errorMessage;
	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		errorView = (TextView) findViewById(R.id.errorView);
		errorMessage ="";
		errorView.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
		errorView.setText("e.g. phone: 8001505129, pw:IP");
		errorView.setTextColor(Color.RED);
		EditText et = (EditText) findViewById(R.id.phoneNumber);
		et.setText("8001505129");
		EditText pt = (EditText) findViewById(R.id.password);
		pt.setText("IP");

	}

	@Override
	protected void onStart() {
		super.onStart();
		/*Intent intent = new Intent(MainActivity.this, MainMenu.class);
		startActivity(intent);*/
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
			new PasswordVerifier().execute(phoneNo,password);
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
				Intent intent = new Intent(MainActivity.this, MainMenu.class);
				startActivity(intent);
			} else {
				errorView.setText(errorMessage);
			}
		}
		
	}
	
	
	private boolean verifyPassword(String phoneNo, String password) {
		
		HttpClient httpClient = new DefaultHttpClient();
		//Need to choose the right local host every time
		//edge02: http://146.169.52.2:59999/login
		HttpPost httpPost = new HttpPost("http://146.169.53.189:59999/login");
		
		try {
			//Add data
			List<NameValuePair> nameValueP = new ArrayList<NameValuePair>(3);
			nameValueP.add(new BasicNameValuePair("op", "checkPassword"));
			nameValueP.add(new BasicNameValuePair("phone", phoneNo));
			nameValueP.add(new BasicNameValuePair("password", password));
			httpPost.setEntity(new UrlEncodedFormEntity(nameValueP));
			//execute Post request
			HttpResponse res = httpClient.execute(httpPost);
			
			int response = HttpReaders.readInt(res.getEntity().getContent(), 1);
			
			Pair<String, Boolean> responseTuple = AdminHelper.handleResponse(response);
			errorMessage = responseTuple.first;
			return responseTuple.second;
			
		} catch (ClientProtocolException e) {
			errorView.setText("ClientProtocolException"); 
		} catch (IOException e) {
			errorView.setText("IOException in postData");
		}		
		return false;		
	}

	public void signInHandler(View view) {
		Intent intent = new Intent(MainActivity.this, SignIn.class);
		startActivity(intent);
	}
	
	/*
	public void debugButton(View view) {
		errorView.setText(debug +" and the length is "+debug.length());
	}
	*/
	
}
