package com.example.moneyapp.message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.JsonReader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.example.helpers.AdminHelper;
import com.example.helpers.CustomHttpClient;
import com.example.helpers.DateGen;
import com.example.helpers.HttpReaders;
import com.example.helpers.MyToast;
import com.example.helpers.TransactionHelper;
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
public class PersonDetailFragment extends Fragment implements OnClickListener {
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
		ImageButton ib = (ImageButton) v.findViewById(R.id.sendMessage);
		ib.setOnClickListener(this);
		// Show the dummy content as text in a TextView.
		/*if (mItem != null) {
			((TextView) rootView.findViewById(R.id.person_detail))
					.setText(mItem.content);
		}*/
		
		conversationid = getArguments().getInt("conversationid");
		name = getArguments().getString("name");
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
//		EditText et = (EditText) v.findViewById(R.id.message);
		TextView et = (TextView) v.findViewById(R.id.message_text);
		String content = et.getText().toString();
		String date = DateGen.getDate();
		String time = DateGen.getTime();
		
		new SendMessageTask().execute(content,date,time,Integer.toString(conversationid));
	}
	
	private class SendMessageTask extends AsyncTask<String, Void, Boolean> {

		@Override
		protected Boolean doInBackground(String... params) {
		try{	
			String content = params[0];
			int userid = user.getUserid();
			String date=params[1];
			String time=params[2];
			String conversationid = params[3];
			String op = "sendMessage";
			
			Log.v(TAG,"userid ="+userid + ",name = "+ name +",convoId = "+conversationid);
			
			List<NameValuePair> nameValueP = new ArrayList<NameValuePair>(3);
			nameValueP.add(new BasicNameValuePair("op", op));
			nameValueP.add(new BasicNameValuePair("conversationid", conversationid));
			nameValueP.add(new BasicNameValuePair("userid", Integer.toString(userid)));
			nameValueP.add(new BasicNameValuePair("_date", date));
			nameValueP.add(new BasicNameValuePair("_time", time));
			nameValueP.add(new BasicNameValuePair("content", content));
			
			InputStream in = CustomHttpClient.executeHttpPost(MainActivity.URL+MainActivity.MESSAGE,nameValueP);
			
			//Log.v("person detail fragment", HttpReaders.readIt(in, 500));
			
			JsonReader jr =new JsonReader(new BufferedReader(
					new InputStreamReader(in)));
			jr.setLenient(true);
			jr.beginObject();
			int response = JsonCustomReader.readJSONRetCode(jr, in);
			jr.endObject();	
			Pair<String, Boolean> pair = AdminHelper
					.handleResponse(response);
			errorMessage = pair.getFirst();

			return pair.getSecond();
			
		}
		catch (Exception e) {
			Log.v(TAG, e.getMessage());
		}
			return false;
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			if (result) {
				TextView et = (TextView) v.findViewById(R.id.message_text);
				et.setText("");
				conversationid = getArguments().getInt("conversationid");
				name = getArguments().getString("name");
				new DownloadDetails().execute(Integer.toString(conversationid), Integer.toString(user.getUserid()),name);
				
				MyToast.toastMessage(thisActivity, "Message sent!");
		}  else
				MyToast.toastMessage(thisActivity, errorMessage);
		}
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.sendMessage:
			sendMessage();
		}
		
	}
	
}
