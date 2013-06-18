package com.example.moneyapp.message;

import java.io.InputStream;
import java.util.ArrayList;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.moneyapp.MainActivity;
import com.example.moneyapp.R;
import com.example.moneyapp.dummy.DummyContent;
import com.example.moneyapp.transaction.TransactionDetail;
import com.example.helpers.CustomHttpClient;
import com.example.helpers.metadata.MessageDetails;
import com.example.helpers.metadata.UserDetails;
import com.example.json.JsonCustomReader;

/**
 * A fragment representing a single Person detail screen. This fragment is
 * either contained in a {@link MessageListActivity} in two-pane mode (on
 * tablets) or a {@link MessageDetailActivity} on handsets.
 */
public class PersonDetailFragment extends Fragment {
	/**
	 * The fragment argument representing the item ID that this fragment
	 * represents.
	 */
	public static final String ARG_ITEM_ID = "item_id";

	/**
	 * The dummy content this fragment is presenting.
	 */
	private DummyContent.DummyItem mItem;

	String TAG = "PersonDetailFragment";
	ArrayList<MessageDetails> details;
	private UserDetails user;
	
	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public PersonDetailFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (getArguments().containsKey(ARG_ITEM_ID)) {
			// Load the dummy content specified by the fragment
			// arguments. In a real-world scenario, use< a Loader
			// to load content from a content provider.
			mItem = DummyContent.ITEM_MAP.get(getArguments().getString(
					ARG_ITEM_ID));
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_person_detail,
				container, false);

		// Show the dummy content as text in a TextView.
		if (mItem != null) {
			((TextView) rootView.findViewById(R.id.person_detail))
					.setText(mItem.content);
		}

		return rootView;
	}
	
	private class DownloadDetails extends AsyncTask<Integer, Void, ArrayList<MessageDetails>> {

		@Override
		protected ArrayList<MessageDetails> doInBackground(Integer... params) {
			try {
				int conversationid = params[0];
				int userid = user.getUserid();
				String op = "messageDetails";
				String viewMode = "perPerson";
				InputStream in = CustomHttpClient.executeHttpGet(MainActivity.URL+
						MainActivity.TRANSACTION + "?"+
						"op="+op+"&"+ 
						"viewMode=" + viewMode + "&"+
						"userid=" + userid + "&" +
						"conversationid=" + conversationid);
				
				details = JsonCustomReader.readJsonMessages(in);
			} catch (Exception e) {
				Log.v(TAG, e.getMessage());
			}

			return details;
		}
		
		@Override
		protected void onPostExecute(ArrayList<MessageDetails> result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
		}
		
	}
	
}
