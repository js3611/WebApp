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

import com.example.helpers.AdminHelper;
import com.example.helpers.ConnectionHelper;
import com.example.helpers.HttpReaders;
import com.example.helpers.StringFilter;


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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SignIn extends Activity {

	TextView errorView;
	String errorMessage;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign_in);
		errorView = (TextView) findViewById(R.id.errorView);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.sign_in, menu);
		return true;
	}

	
public void signupHandler (View view) {
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
	
	if (ConnectionHelper.checkNetworkConnection(connMgr) && 
			passwordsMatch(enterPasswordString, checkPasswordString))
	{
		new registerUser().execute(firstnameString, surnameString, enterPasswordString, phoneNum);
							
	}
	else if (!passwordsMatch(enterPasswordString, checkPasswordString)) {
		errorView.setText("Entered Passwords do not match!");
	}
	else if (!ConnectionHelper.checkNetworkConnection(connMgr)){
		errorView.setText("No network connection");
	}

}

private class registerUser extends AsyncTask<String, Void, Boolean> {
	
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

private boolean signUpWith(String firstname, String surname, String password, String phone) {
	HttpClient httpClient = new DefaultHttpClient();
	//Need to choose the right local host every time
	//edge02: http://146.169.52.2:59999/login
	//fusion21: 146.169.53.101
	HttpPost httpPost = new HttpPost("http://146.169.53.189:59999/login");
	
	try {
		//Add data
		List<NameValuePair> nameValueP = new ArrayList<NameValuePair>(5);
		nameValueP.add(new BasicNameValuePair("op", "newAccount"));
		nameValueP.add(new BasicNameValuePair("firstname", firstname));
		nameValueP.add(new BasicNameValuePair("surname",surname));
		nameValueP.add(new BasicNameValuePair("password", password));
		nameValueP.add(new BasicNameValuePair("phone", phone));						
		
		httpPost.setEntity(new UrlEncodedFormEntity(nameValueP));
		//execute Post request
		HttpResponse res = httpClient.execute(httpPost);
		
		int response = HttpReaders.readInt(res.getEntity().getContent(), 1);
		Pair<String, Boolean> responseTuple =  AdminHelper.handleResponse(response);	
		errorMessage = responseTuple.first;
		return responseTuple.second;
		
	} catch (ClientProtocolException e) {
		errorView.setText("ClientProtocolException"); 
	} catch (IOException e) {
		errorView.setText("IOException in postData");
	}
	return false;
}
	
private boolean passwordsMatch (String entered, String check) {
	return check.equals(entered);
}


}
