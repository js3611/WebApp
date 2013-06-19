package com.example.moneyapp.message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.JsonReader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;

import com.example.helpers.AdminHelper;
import com.example.helpers.CustomHttpClient;
import com.example.helpers.metadata.MessageDetails;
import com.example.helpers.metadata.Pair;
import com.example.helpers.metadata.UserDetails;
import com.example.json.JsonCustomReader;
import com.example.moneyapp.MainActivity;
import com.example.moneyapp.R;
import com.example.moneyapp.dummy.DummyContent;


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
	ArrayList<Parcelable> message_content ;
	int next_party;
	String next_name;
	String errorMessage;
	ArrayList<MessageDetails> details;
	UserDetails user;
	int conversationid;
	String name; 
	ListView messages;
	MessageContentAdapter mca;
	View v;

	private FragmentActivity thisActivity;
	
	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public PersonDetailFragment() {
	}

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		user = UserDetails.getUser(getActivity().getIntent());
		thisActivity = getActivity();
		
		
		if (getArguments().containsKey(ARG_ITEM_ID)) {
			// Load the dummy content specified by the fragment
			// arguments. In a real-world scenario, use< a Loader
			// to load content from a content provider.
			/*mItem = DummyContent.ITEM_MAP.get(getArguments().getString(
					ARG_ITEM_ID));*/
			next_party = getArguments().getInt("next_party");
			next_name = getArguments().getString("next_name");
			message_content = getArguments().getParcelableArrayList("messageDetails");
			
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.messages_list,
				container, false); 
		v = rootView;
		messages = (ListView) rootView.findViewById(R.id.messageContentList);
		// Show the dummy content as text in a TextView.
		/*if (mItem != null) {
			((TextView) rootView.findViewById(R.id.person_detail))
					.setText(mItem.content);
		}*/
		
		int conversationid = getArguments().getInt("conversationid");
		String name = getArguments().getString("name");
		new DownloadDetails().execute(Integer.toString(conversationid), Integer.toString(user.getUserid()),name);
		
		return rootView;
	}
	
	private class DownloadDetails extends AsyncTask<String, Void, ArrayList<MessageDetails>> {

		@Override
		protected ArrayList<MessageDetails> doInBackground(String... params) {
			try {
				int conversationid = Integer.parseInt(params[0]);
				// populate with non sample
				//int userid = 4;
	
				
				String name = params[2];
				int userid = Integer.parseInt(params[1]);
				String op = "messageDetails";
				
				Log.v(TAG,"userid ="+userid + ",name = "+ name +",convoId = "+conversationid);
				InputStream in = CustomHttpClient.executeHttpGet(MainActivity.URL+
						MainActivity.MESSAGE + "?"+
						"op="+op+"&"+ 
						"name="+ name+ "&" +
						"userid=" + userid + "&" +
						"conversationid=" + conversationid);
				processInput(in);					
				return details;
			} catch (Exception e) {
				Log.v(TAG, e.getMessage());
			}

			return details;
		}
		
		@Override
		protected void onPostExecute(ArrayList<MessageDetails> result) {
			super.onPostExecute(result);
			for (MessageDetails messageDetails : result) {
				Log.v(TAG,messageDetails.toString());
			}
		
			mca = new MessageContentAdapter(result, thisActivity);
			messages.setAdapter(mca);
			mca.notifyDataSetChanged();
		}
		
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
				Log.v(TAG, "No Details found.");
				return false; 
			}
			
			jr.nextName();
			next_party = Integer.parseInt(jr.nextString());
			jr.nextName();
			name = jr.nextString();
			ArrayList<MessageDetails> messageDetails = JsonCustomReader.readJsonMessages(jr, in);
			jr.endObject();

			errorMessage = pair.getFirst();
			details = messageDetails;
			return true;
			
		} catch (UnsupportedEncodingException e) {
			errorMessage = e.getMessage();
		} catch (IOException e) {
			errorMessage = e.getMessage();
		}
		return false;		
	}
	
	public void sendMessage() {
		//Async task
		EditText et = (EditText) v.findViewById(R.id.message);
		String content = et.getText().toString();
		new SendMessageTask().execute(content);
	}
	
	private class SendMessageTask extends AsyncTask<String, Void, Void> {

		@Override
		protected Void doInBackground(String... params) {
			
			String content = params[0];
			int userid = user.getUserid();
			int friendid=0;
			String date=null;
			String time=null;
			String op = "messageDetails";
			
			Log.v(TAG,"userid ="+userid + ",name = "+ name +",convoId = "+conversationid);
//			InputStream in = CustomHttpClient.executeHttpGet(MainActivity.URL+
//					MainActivity.MESSAGE + "?"+
//					"op="+op+"&"+ 
//					"name="+ name+ "&" +
//					"userid=" + userid + "&" +
//					"conversationid=" + conversationid);
//			processInput(in);					
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
		}
	}
	
}
