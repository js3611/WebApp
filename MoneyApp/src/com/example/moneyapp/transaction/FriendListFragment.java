package com.example.moneyapp.transaction;

import java.io.InputStream;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.helpers.CustomHttpClient;
import com.example.helpers.metadata.UserDetails;
import com.example.json.JsonCustomReader;
import com.example.moneyapp.MainActivity;
import com.example.moneyapp.R;

public class FriendListFragment extends DialogFragment {

	private ListView friendList;
	// A list of data for each entry, which the adapter retrieves from.
	private ArrayList<UserDetails> details;
//	private UserDetails user;
	int userid;
	Activity thisActivity;

	/**
	 * Create a new instance of MyDialogFragment, providing "num" as an
	 * argument.
	 */
	static FriendListFragment newInstance(int userid) {
		FriendListFragment f = new FriendListFragment();

		// Supply num input as an argument.
		Bundle args = new Bundle();
		args.putInt("userid", userid);
		f.setArguments(args);

		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		userid = getArguments().getInt("userid");
		friendList = (ListView) getActivity().findViewById(R.id.friends_list);
		thisActivity = getActivity();
		details = new ArrayList<UserDetails>();
		new DownloadFriends().execute(userid);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.transaction_new_person, container,
				false);
		return v;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		
		return super.onCreateDialog(savedInstanceState);
	}
	private class DownloadFriends extends AsyncTask<Integer, Void, ArrayList<UserDetails>> {

		@Override
		protected ArrayList<UserDetails> doInBackground(Integer... params) {

			try {
				int userid = params[0];
				String op = "getFriendsList";
				String viewMode = "perPerson";
				
				InputStream in = CustomHttpClient.executeHttpGet(MainActivity.URL+
						MainActivity.TRANSACTION + "?"+
						"op="+op+"&"+ 
						"viewMode="+viewMode+"&"+
						"userid="+userid );

				//Pair<Integer,ArrayList<UserDetails>> rawData = JsonCustomReader
				//		.readJsonFriends(in);
				//details = rawData.second;
			} catch (Exception e) {
				UserDetails Detail;
				Detail = new UserDetails();
				Detail.setProfilePicture(R.drawable.ic_launcher);
				Detail.setFirstName("ERROR" + e.getMessage());
				details.add(Detail);
			}

			return details;
		}
		
		@Override
		protected void onPostExecute(ArrayList<UserDetails> result) {
			super.onPostExecute(result);
			
			Log.v("FriendListFragment", "no problem getting data");
			
//			friendList.setAdapter(new NewPersonAdapter(result, thisActivity));
			
			Log.v("FriendListFragment", "no problem getting data");
			
			
			

			   /* builder.setAdapter((ListAdapter) new NewPersonAdapter(result, getActivity()), new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						
					}

				});
*/
			
		}
		
	}
}
