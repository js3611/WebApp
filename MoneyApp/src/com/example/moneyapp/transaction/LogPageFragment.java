package com.example.moneyapp.transaction;

import java.io.InputStream;
import java.util.ArrayList;

import com.example.helpers.CustomHttpClient;
import com.example.helpers.metadata.UserDetails;
import com.example.json.JsonCustomReader;
import com.example.moneyapp.MainActivity;
import com.example.moneyapp.R;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class LogPageFragment extends Fragment {

	//The List view
	ListView logList;
	//A list of data for each entry, which the adapter retrieves from.
	ArrayList<TransactionDetail> details;
	private Activity thisActivity;
	private UserDetails user;
	
	
	public static LogPageFragment create() {
		LogPageFragment fragment = new LogPageFragment();
		return fragment;
	}

	public LogPageFragment() {
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		ViewGroup rootView;
		rootView = (ViewGroup) inflater.inflate(R.layout.transaction_fragment_log_view,
				container, false);

		/* Set fields */
		thisActivity = getActivity();
		user = (UserDetails) thisActivity.getIntent().getExtras().getSerializable(MainActivity.USER_KEY);

		// profile
		((TextView) rootView.findViewById(R.id.log_title)).setText("Log Transaction");
		logList = (ListView) rootView.findViewById(R.id.log_list);

		int friendid = thisActivity.getIntent().getExtras().getInt(Transactions.FRIENDID_STR);
		new DownloadContent().execute(friendid);

		registerForContextMenu(logList);

		logList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> a, View v, int pos, long id) {

				// TextView tv = (TextView) v.findViewById(R.id.From);
				// String s = tv.getText().toString();

				//startActivity(new Intent(this, PerItem.class));

			}
		});
		
		return rootView;
	}
	
	private class DownloadContent extends AsyncTask<Integer, Void, ArrayList<TransactionDetail>> {

		@Override
		protected ArrayList<TransactionDetail> doInBackground(Integer... params) {
			try {
				int friendid = params[0];
				int userid = user.getUserid();
				String op = "viewFriendsLog";
				String viewMode = "perPerson";
				InputStream in = CustomHttpClient.executeHttpGet(MainActivity.URL+
						MainActivity.TRANSACTION + "?"+
						"op="+op+"&"+ 
						"viewMode=" + viewMode + "&"+
						"userid=" + userid + "&" +
						"friendid=" + friendid);

				details = JsonCustomReader.readJsonPerPerson(in);
			} catch (Exception e) {
				TransactionDetail Detail;
				Detail = new TransactionDetail();
				Detail.setIcon(R.drawable.ic_launcher);
				Detail.setOwesuser("ERROR"+e.getMessage());
				Detail.setPrice(0);
				details.add(Detail);
			}

			return details;
		}
		
		@Override
		protected void onPostExecute(ArrayList<TransactionDetail> result) {
			super.onPostExecute(result);
			
			logList.setAdapter(new PerItemAdapter(result, thisActivity));
			//registerForContextMenu(transList);
		}
	}


}
