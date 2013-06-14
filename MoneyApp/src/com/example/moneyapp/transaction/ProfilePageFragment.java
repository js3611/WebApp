package com.example.moneyapp.transaction;

import java.io.InputStream;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.helpers.CustomHttpClient;
import com.example.helpers.metadata.UserDetails;
import com.example.json.JsonCustomReader;
import com.example.moneyapp.MainActivity;
import com.example.moneyapp.R;

/* Fragment */
public class ProfilePageFragment extends Fragment {

	public static final String TAG = "ProfilePageFragment";
	
	private ViewGroup view;
	private UserDetails friend;
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
		
		((ImageView) view.findViewById(R.id.big_profile_icon)).setImageResource(R.drawable.ic_launcher);
		((TextView) view.findViewById(R.id.firstName)).setText("");
		((TextView) view.findViewById(R.id.surname)).setText("");
		((TextView) view.findViewById(R.id.price)).setText("");
		((TextView) view.findViewById(R.id.oweDirection)).setText("");

		
		
		//Fill in details
		Bundle extras = getActivity().getIntent().getExtras();
		if (extras != null) {
			user = (UserDetails) extras.getSerializable(MainActivity.USER_KEY);
			int friendid = extras.getInt(Transactions.FRIENDID_STR);
			total_price = extras.getDouble(Transactions.PRICE_STR);
			Log.v(TAG, "friend id: "+friendid);
			Log.v(TAG, "total price: "+total_price);
			new DownloadFriend().execute(friendid);		
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
	
	private class DownloadFriend extends AsyncTask<Integer, Void, Void>{

		@Override
		protected Void doInBackground(Integer... params) {
			try {
				int userid = params[0];
				String op = "viewFriendsProfile";
				String viewMode = "perPerson";
				String url = MainActivity.url
						+ MainActivity.TRANSACTION + "?" + "op=" + op
						+ "&" + "viewMode=" + viewMode + "&"
						+ "friendid=" + userid;
				Log.v(TAG, url);
				InputStream in = CustomHttpClient
						.executeHttpGet(url);
//				InputStream in = CustomHttpClient
//						.executeHttpGet(MainActivity.url
//								+ MainActivity.TRANSACTION + "?" + "op=" + op
//								+ "&" + "viewMode=" + viewMode + "&"
//								+ "friendid=" + userid);

				Pair<Integer, UserDetails> friendPair = JsonCustomReader
						.readJsonFriend(in);
				int retCode = friendPair.first;
				friend = friendPair.second;

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
			((TextView) view.findViewById(R.id.price)).setText("£"+Math.abs(total_price));
			((TextView) view.findViewById(R.id.oweDirection)).setText(setDirection(total_price,friend.getFirstName()));
		}
		
	}

}
