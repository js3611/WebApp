package com.example.moneyapp.friend;

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
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;

import com.example.helpers.AdminHelper;
import com.example.helpers.CustomHttpClient;
import com.example.helpers.MyToast;
import com.example.helpers.metadata.FriendsList;
import com.example.helpers.metadata.Pair;
import com.example.helpers.metadata.UserDetails;
import com.example.json.JsonCustomReader;
import com.example.moneyapp.MainActivity;
import com.example.moneyapp.MainMenu;
import com.example.moneyapp.R;
import com.example.moneyapp.transaction.NewPersonAdapter;

public class Friend extends Activity implements
		NewFriendDialog.NoticeDialogListener {

	private static final String TAG = "Friend";
	private static final Integer CONFIRM_OP = 1;
	private static final Integer DELETE_OP = 2;
	private String errorMessage = "";
	private Friend thisActivity;
	private ListView friendList;
	private FriendAdapter friendAdapter;
	private ArrayList<UserDetails> details;
	private ArrayList<UserDetails> newFriends; 
	private UserDetails user;
	private UserDetails newFriend;
	private boolean[] tickIndex;
	private int opFlag;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.friend_main);
		// Show the Up button in the action bar.
		setupActionBar();

		thisActivity = this;
		friendList = (ListView) findViewById(R.id.activity_friend_list);
		details = new ArrayList<UserDetails>();
		user = UserDetails.getUser(getIntent());

		/* Get friends from FriendsList */
		details = FriendsList.getInstance();
		friendAdapter = new FriendAdapter(details, thisActivity);
		friendList.setAdapter(friendAdapter);
		registerForContextMenu(friendList);
		new DownloadNotificationTask().execute();
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
		getMenuInflater().inflate(R.menu.friend, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			Intent intent = getIntent().setClass(this, MainMenu.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			return true;
		case R.id.add:
			addFriend();
			return true;
		case R.id.refresh:
			refresh();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void refresh() {
		friendAdapter.notifyDataSetChanged(); 
	}

	private void addFriend() {
		Log.v(TAG, "Friend");
		NewFriendDialog dialog = new NewFriendDialog();
		dialog.show(getFragmentManager(), "Add friend");
	}

	@Override
	public void onDialogPositiveClick(DialogFragment dialog) {
		// Asynchronously add new friend
		String phoneNo = ((NewFriendDialog) dialog).getPhoneNumber();

		if (!verifyPhone(phoneNo))
			return;

		new SearchFriendTask().execute(phoneNo);

		// MyToast.toastMessage(thisActivity, "Notification sent");

	}

	private boolean verifyPhone(String phoneNo) {

		if (phoneNo.length()<=0 || phoneNo.length() > 13) {
			MyToast.toastMessage(thisActivity, "Invalid phone number");
			return false;
		}
		
		if (phoneNo.equals(user.getPhoneNo())) {
			MyToast.toastMessage(thisActivity, "I see you have no friend");
			return false;
		}

		char[] strArray = phoneNo.toCharArray();
		for (char c : strArray) {
			if (!Character.isDigit(c)) {
				MyToast.toastMessage(thisActivity, "Invalid phone number");
				return false;
			}
		}

		return true;
	}

	@Override
	public void onDialogNegativeClick(DialogFragment dialog) {
		// Do nothing
		Log.v(TAG, "canceled");
	}

	private class SearchFriendTask extends AsyncTask<String, Void, Boolean> {

		@Override
		protected Boolean doInBackground(String... params) {
			return addFriend(params[0]);
		}

		@Override
		protected void onPostExecute(Boolean result) {

			if (result) { // User exists
				// Another Dialog to confirm "add friend"
				Log.v(TAG,newFriend.getUserid()+"");
				new AddFriendTask().execute(user.getUserid(),newFriend.getUserid());
				// MyToast.toastMessage(thisActivity,"user exists!");

			} else {
				MyToast.toastMessage(thisActivity, errorMessage);
			}
		}

		private boolean addFriend(String phoneNo) {
			List<NameValuePair> nameValueP = new ArrayList<NameValuePair>(3);
			nameValueP.add(new BasicNameValuePair("op", "searchFriend"));
			nameValueP.add(new BasicNameValuePair("phonenumber", phoneNo));

			try {
				InputStream in = CustomHttpClient.executeHttpPost(
						MainActivity.URL + MainActivity.FRIENDS, nameValueP);
				return processInput(in);
			} catch (Exception e) {
				errorMessage = e.toString();
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
					return false;
				}

				/* Read User detail */
				newFriend = JsonCustomReader.readJSONUser(jr, in);
				jr.endObject();
				return pair.getSecond();

			} catch (UnsupportedEncodingException e) {
				errorMessage = e.getMessage();
			} catch (IOException e) {
				errorMessage = e.getMessage();
			}
			return false;
		}
	}
	
	private class AddFriendTask extends AsyncTask<Integer, Void, Boolean> {

		@Override
		protected Boolean doInBackground(Integer... params) {
			return addFriend(params[0],params[1]);
		}

		@Override
		protected void onPostExecute(Boolean result) {

			if (result) { 
				MyToast.toastMessage(thisActivity,"Notification sent!");

			} else {
				MyToast.toastMessage(thisActivity, errorMessage);
			}
		}

		private boolean addFriend(int userid, int friendid) {
			List<NameValuePair> nameValueP = new ArrayList<NameValuePair>(3);
			nameValueP.add(new BasicNameValuePair("op", "addFriend"));
			nameValueP.add(new BasicNameValuePair("userid", Integer.toString(userid)));
			nameValueP.add(new BasicNameValuePair("friendid", Integer.toString(friendid)));

			try {
				InputStream in = CustomHttpClient.executeHttpPost(
						MainActivity.URL + MainActivity.FRIENDS, nameValueP);
				return processInput(in);
			} catch (Exception e) {
				errorMessage = e.toString();
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
	}
	
	private class DownloadNotificationTask extends AsyncTask<String, Void, Boolean> {

		@Override
		protected Boolean doInBackground(String... params) {
			return downloadNotification();
		}

		@Override
		protected void onPostExecute(Boolean result) {

			if (result) { // Successfully Reads
				for (UserDetails user : newFriends) {
					Log.v(TAG, user.toString());
				}
				//Show dialog with people who wish to add
				showDialog();
			} else { //Failed to get notifications or no new notifications
				//MyToast.toastMessage(thisActivity, errorMessage);
			}
		}

		private boolean downloadNotification() {
			List<NameValuePair> nameValueP = new ArrayList<NameValuePair>(3);
			nameValueP.add(new BasicNameValuePair("op", "getRequests"));
			nameValueP.add(new BasicNameValuePair("userid", Integer.toString(user.getUserid())));

			try {
				InputStream in = CustomHttpClient.executeHttpPost(
						MainActivity.URL + MainActivity.FRIENDS, nameValueP);
								
				return processInput(in);
			} catch (Exception e) {
				errorMessage = e.toString();
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
					return false;
				}
				
				/* Read User detail */
				newFriends = JsonCustomReader.readJSONFriends(jr, in);
				jr.endObject();
				return pair.getSecond();

			} catch (UnsupportedEncodingException e) {
				errorMessage = e.getMessage();
			} catch (IOException e) {
				errorMessage = e.getMessage();
			}
			return false;
		}
	}

	
	/* Shows dialog when there are new friend requests */	
	private void showDialog() {
		
		Log.v(TAG, newFriends.size()+"");
		tickIndex = new boolean[newFriends.size()];
		
		/* Create the dialog through the builder */
		AlertDialog.Builder builder = new AlertDialog.Builder(thisActivity);
		builder.setTitle("New Friend Requests!");

		/* Inflate the dialog with customised view */
		LayoutInflater inflater = getLayoutInflater();
		View view = inflater.inflate(R.layout.friend_request_dialog, null);	
        ListView listView = (ListView) view.findViewById(R.id.RequestList);
        NewPersonAdapter npa = new NewPersonAdapter(newFriends, thisActivity);
        listView.setAdapter(npa);

        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> a, View v, int pos, long id) {
            	CheckBox cb = (CheckBox) v.findViewById(R.id.checkBox1);

				tickIndex[pos] = !tickIndex[pos];
        		cb.setChecked(tickIndex[pos]);

        		Log.v(TAG, "Added at "+pos);

              	if(!tickIndex[pos]) {
					Log.v(TAG, "Added: "+newFriends.get(pos).getFirstName());
				}else{
					Log.v(TAG, "Added: "+newFriends.get(pos).getFirstName());
				}
                
            }
        });
        builder.setView(view);
        builder.setPositiveButton(R.string.confirm,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						Log.v(TAG, "add friends, then update the view");
						confirmFriends();
					}
				});

		builder.setNegativeButton(R.string.delete,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						Log.v(TAG, "Delete pressed");
						deleteFriends();
					}
				});
		builder.setNeutralButton(R.string.cancel,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// Do nothing
					}
				});
        AlertDialog dialog = builder.create();
        dialog.show();
    }

	protected void deleteFriends() {
		opFlag = DELETE_OP;
		for (int i = 0; i < tickIndex.length; i++) {
			if (tickIndex[i]) {
				Log.v(TAG, newFriends.get(i).toString());
				new FriendTask().execute(opFlag, user.getUserid(),newFriends.get(i).getUserid(),i);
			}
		}
	}

	protected void confirmFriends() {
		opFlag = CONFIRM_OP;
		for (int i = 0; i < tickIndex.length; i++) {
			if (tickIndex[i]) {
				new FriendTask().execute(opFlag, user.getUserid(),newFriends.get(i).getUserid(),i);
			}
		}
		MyToast.toastMessage(thisActivity, "confirmed friends");
	}   

	private class FriendTask extends AsyncTask<Integer, Void, Boolean> {

		int position = 0;
		int opcode = 0;
		
		@Override
		protected Boolean doInBackground(Integer... params) {
			return addFriend(params[0],params[1],params[2],params[3]);
		}

		@Override
		protected void onPostExecute(Boolean result) {

			if (result) { 
				if (opcode == CONFIRM_OP) {
					FriendsList.addFriend(newFriends.get(position));
					friendAdapter.notifyDataSetChanged(); 		
				} 
				
			} else {
				MyToast.toastMessage(thisActivity, errorMessage);
			}
		}

		private boolean addFriend(int op, int userid, int friendid, int pos) {
			opcode = op;
			position = pos;
			
			List<NameValuePair> nameValueP = new ArrayList<NameValuePair>(3);
			if (op==CONFIRM_OP) {
				nameValueP.add(new BasicNameValuePair("op", "confirmRequest"));
			} else if (op==DELETE_OP){
				nameValueP.add(new BasicNameValuePair("op", "deleteFriend"));
			}

			nameValueP.add(new BasicNameValuePair("userid", Integer.toString(userid)));
			nameValueP.add(new BasicNameValuePair("friendid", Integer.toString(friendid)));

			try {
				InputStream in = CustomHttpClient.executeHttpPost(
						MainActivity.URL + MainActivity.FRIENDS, nameValueP); 
				return processInput(in);
				
			} catch (Exception e) {
				errorMessage = e.toString();
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
	}
	
}
