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

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.helpers.AdminHelper;
import com.example.helpers.CustomHttpClient;
import com.example.helpers.DateGen;
import com.example.helpers.MyToast;
import com.example.helpers.metadata.FriendsList;
import com.example.helpers.metadata.Pair;
import com.example.helpers.metadata.UserDetails;
import com.example.json.JsonCustomReader;
import com.example.moneyapp.MainActivity;
import com.example.moneyapp.R;

/* Fragment */
public class ProfilePageFragment extends Fragment {

	public static final String TAG = "ProfilePageFragment";
	private String errorMessage = "";
	
	private ViewGroup view;
	//private UserDetails friend;
	private int friendid;
	private int icon;
	private UserDetails user;
	private double total_price;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	public static ProfilePageFragment create() {
		ProfilePageFragment fragment = new ProfilePageFragment();
		return fragment;
	}

	public ProfilePageFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		ViewGroup rootView;
		
		// Set the title view to show the page number.
		rootView = (ViewGroup) inflater.inflate(R.layout.transaction_fragment_profile_view,
				container, false);
		
		view = rootView;
		
		//Fill in details
		Bundle extras = getActivity().getIntent().getExtras();
		if (extras != null) {
			user = (UserDetails) extras.getSerializable(MainActivity.USER_KEY);
			friendid = extras.getInt(Transactions.FRIENDID_STR);
			icon = extras.getInt(Transactions.ICON_STR);
			total_price = extras.getDouble(Transactions.PRICE_STR);
			Log.v(TAG, "friend id: "+friendid);
			Log.v(TAG, "total price: "+total_price);
		}
		
		String firstname = FriendsList.getFirstname(friendid);
		String surname = FriendsList.getSurname(friendid);
		
		((ImageView) view.findViewById(R.id.big_profile_icon)).setImageResource(icon);
		((TextView) view.findViewById(R.id.firstName)).setText(firstname);
		((TextView) view.findViewById(R.id.surname)).setText(surname);
		((TextView) view.findViewById(R.id.price)).setText(Math.abs(total_price) + " pounds");
		((TextView) view.findViewById(R.id.oweDirection)).setText(setDirection(total_price,firstname));
		if (total_price <= 0) {
			view.findViewById(R.id.pay_button).setEnabled(false);
		} else {
			view.findViewById(R.id.nudge_button).setEnabled(false);
		}
			
		return rootView;
	}

	private CharSequence setDirection(double price, String name) {
		if (price > 0) {
			return "You owe "+name +": ";
		} else {
			return name +" owes you: ";
		}
	}
	/*
	private class DownloadFriend extends AsyncTask<Integer, Void, Void>{

		@Override
		protected Void doInBackground(Integer... params) {
			try {
				int userid = params[0];
				String op = "viewFriendsProfile";
				String viewMode = "perPerson";
				String url = MainActivity.URL
						+ MainActivity.TRANSACTION + "?" + "op=" + op
						+ "&" + "viewMode=" + viewMode + "&"
						+ "friendid=" + userid;
				Log.v(TAG, url);
				InputStream in = CustomHttpClient
						.executeHttpGet(url);

				Pair<Integer, UserDetails> friendPair = JsonCustomReader
						.readJsonFriend(in);
				int retCode = friendPair.getFirst();
				friend = friendPair.getSecond();

			} catch (Exception e) {
				Log.v(TAG, e.getMessage());
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			((ImageView) view.findViewById(R.id.big_profile_icon)).setImageResource(friend.getProfilePicture());
			((TextView) view.findViewById(R.id.firstName)).setText(friend.getFirstName());
			((TextView) view.findViewById(R.id.surname)).setText(friend.getSurname());
			((TextView) view.findViewById(R.id.price)).setText(Math.abs(total_price) + " pounds");
			((TextView) view.findViewById(R.id.oweDirection)).setText(setDirection(total_price,friend.getFirstName()));
		}
		
	}*/
	
	public void makeFullPayment() {
		new MakePayment().execute(user.getUserid(),friendid);
	}
	
	private class MakePayment extends AsyncTask<Integer, Void, Boolean>{

		@Override
		protected Boolean doInBackground(Integer... params) {
			try {
				Log.v(TAG, "Making full payment");
				String url = MainActivity.URL + MainActivity.TRANSACTION;			
				List<NameValuePair> nameValueP = new ArrayList<NameValuePair>(3);
				nameValueP.add(new BasicNameValuePair("op", "personRepay"));
				nameValueP.add(new BasicNameValuePair("userid", Integer.toString(params[0])));
				nameValueP.add(new BasicNameValuePair("owesuserid", Integer.toString(params[1])));
				nameValueP.add(new BasicNameValuePair("date", DateGen.getDate()));
				
				
				InputStream in = CustomHttpClient
						.executeHttpPost(url,nameValueP);

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
			MyToast.toastMessage(getActivity(), errorMessage);
			if (!result) return;
			/* If successful, then reset all the entries */
			((ImageView) view.findViewById(R.id.big_profile_icon)).setImageResource(R.drawable.happy);
			((TextView) view.findViewById(R.id.price)).setText(0 + " pounds");
			((TextView) view.findViewById(R.id.oweDirection)).setText(setDirection(total_price,FriendsList.getFirstname(friendid)));
			((Button) view.findViewById(R.id.nudge_button)).setEnabled(false);
			((TextView) view.findViewById(R.id.oweDirection)).setEnabled(false);
		}
		
	}

}
