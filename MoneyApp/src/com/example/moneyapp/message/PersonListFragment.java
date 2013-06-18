package com.example.moneyapp.message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.helpers.AdminHelper;
import com.example.helpers.CustomHttpClient;
import com.example.helpers.HttpReaders;
import com.example.helpers.metadata.MessageDetails;
import com.example.helpers.metadata.Pair;
import com.example.helpers.metadata.UserDetails;
import com.example.json.JsonCustomReader;
import com.example.moneyapp.MainActivity;
import com.example.moneyapp.R;
import com.example.moneyapp.dummy.DummyContent;

/**
 * A list fragment representing a list of People. This fragment also supports
 * tablet devices by allowing list items to be given an 'activated' state upon
 * selection. This helps indicate which item is currently being viewed in a
 * {@link PersonDetailFragment}.
 * <p>
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class PersonListFragment extends ListFragment {

	/**
	 * The serialization (saved instance state) Bundle key representing the
	 * activated item position. Only used on tablets.
	 */
	private static final String STATE_ACTIVATED_POSITION = "activated_position";

	/**
	 * The fragment's current callback object, which is notified of list item
	 * clicks.
	 */
	private Callbacks mCallbacks = sDummyCallbacks;

	/**
	 * The current activated item position. Only used on tablets.
	 */
	private int mActivatedPosition = ListView.INVALID_POSITION;

	private String TAG = "PersonListFragment";
	private UserDetails user;
	ArrayList<MessageDetails> details;
	private String errorMessage = "";
	String next_party;
	String next_name;
	/**
	 * A callback interface that all activities containing this fragment must
	 * implement. This mechanism allows activities to be notified of item
	 * selections.
	 */
	public interface Callbacks {
		/**
		 * Callback for when an item has been selected.
		 */
		public void onItemSelected(String id);
	}

	/**
	 * A dummy implementation of the {@link Callbacks} interface that does
	 * nothing. Used only when this fragment is not attached to an activity.
	 */
	private static Callbacks sDummyCallbacks = new Callbacks() {
		@Override
		public void onItemSelected(String id) {
		}
	};

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public PersonListFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ArrayList<MessageDetails> details;
		details = new ArrayList<MessageDetails>();
		//Fill the screen with dummy entries
		details = addDummies(details);

		setListAdapter(new MessageAdapter(details, getActivity()));
		UserDetails user = UserDetails.getUser(getActivity().getIntent());
		/*
		// TODO: replace with a real list adapter.
		setListAdapter(new ArrayAdapter<DummyContent.DummyItem>(getActivity(),
				android.R.layout.simple_list_item_activated_1,
				android.R.id.text1, DummyContent.ITEMS)); */
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		// Restore the previously serialized activated item position.
		if (savedInstanceState != null
				&& savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
			setActivatedPosition(savedInstanceState
					.getInt(STATE_ACTIVATED_POSITION));
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		// Activities containing this fragment must implement its callbacks.
		if (!(activity instanceof Callbacks)) {
			throw new IllegalStateException(
					"Activity must implement fragment's callbacks.");
		}

		mCallbacks = (Callbacks) activity;
	}

	@Override
	public void onDetach() {
		super.onDetach();

		// Reset the active callbacks interface to the dummy implementation.
		mCallbacks = sDummyCallbacks;
	}

	@Override
	public void onListItemClick(ListView listView, View view, int position,
			long id) {
		super.onListItemClick(listView, view, position, id);

		//EDIT FROM HERE
		int conversationid = 3;//gotten from clicking on the list bit
		new DownloadDetails().execute(conversationid);
		
		// Notify the active callbacks interface (the activity, if the
		// fragment is attached to one) that an item has been selected.
		//mCallbacks.onItemSelected(DummyContent.ITEMS.get(position).id);
		
		Bundle arguments = new Bundle();
		arguments.putString(PersonDetailFragment.ARG_ITEM_ID, getActivity().getIntent()
				.getStringExtra(PersonDetailFragment.ARG_ITEM_ID));
		arguments.putString("next_name", next_name);
		arguments.putString("next_party",next_party);
		arguments.putParcelableArrayList("messageDetails", details);
		
		
		PersonDetailFragment frag = new PersonDetailFragment();
		frag.setArguments(arguments);
		getFragmentManager().beginTransaction().commit();
		
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (mActivatedPosition != AdapterView.INVALID_POSITION) {
			// Serialize and persist the activated item position.
			outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
		}
	}

	/**
	 * Turns on activate-on-click mode. When this mode is on, list items will be
	 * given the 'activated' state when touched.
	 */
	public void setActivateOnItemClick(boolean activateOnItemClick) {
		// When setting CHOICE_MODE_SINGLE, ListView will automatically
		// give items the 'activated' state when touched.
		getListView().setChoiceMode(
				activateOnItemClick ? AbsListView.CHOICE_MODE_SINGLE
						: AbsListView.CHOICE_MODE_NONE);
	}

	private void setActivatedPosition(int position) {
		if (position == AdapterView.INVALID_POSITION) {
			getListView().setItemChecked(mActivatedPosition, false);
		} else {
			getListView().setItemChecked(position, true);
		}

		mActivatedPosition = position;
	}
	
	
	private class DownloadDetails extends AsyncTask<Integer, Void, ArrayList<MessageDetails>> {

		@Override
		protected ArrayList<MessageDetails> doInBackground(Integer... params) {
			try {
				int conversationid = params[0];
				// populate with non sample
				int userid = 4;
				String name = "Fix";
				//int userid = user.getUserid();
				String op = "messageDetails";
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
			// TODO Auto-generated method stub
			super.onPostExecute(result);
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
			String next_party = jr.nextString();
			jr.nextName();
			String name = jr.nextString();
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
	
	public static ArrayList<MessageDetails> addDummies(ArrayList<MessageDetails> details) {
		MessageDetails Detail;
		Detail = new MessageDetails();
		Detail.setIcon(R.drawable.terence);
		Detail.setFirstname("terence");
		Detail.setLast_message_date("2013-04-05");
		Detail.setLast_message_time("11:00:00");
		Detail.setContent("PLEASE");
		Detail.setUser1(2);
		Detail.setUser2(4);
		Detail.setGroup_chat(false);		
		details.add(Detail);

		Detail = new MessageDetails();
		Detail.setIcon(R.drawable.thai);
		Detail.setFirstname("thai");
		Detail.setLast_message_date("2013-05-05");
		Detail.setLast_message_time("11:00:00");
		Detail.setContent("WORK");
		Detail.setUser1(2);
		Detail.setUser2(3);
		Detail.setGroup_chat(false);		
		details.add(Detail);
		
		Detail = new MessageDetails();
		Detail.setIcon(R.drawable.jo);
		Detail.setFirstname("jo");
		Detail.setLast_message_date("2013-05-05");
		Detail.setLast_message_time("11:00:00");
		Detail.setContent("YES");
		Detail.setUser1(2);
		Detail.setUser2(2);
		Detail.setGroup_chat(false);		
		details.add(Detail);

		Detail = new MessageDetails();
		Detail.setIcon(R.drawable.pleasure);
		Detail.setLast_message_date("2013-06-05");
		Detail.setLast_message_time("06:00:00");
		Detail.setContent("Snippet of last message");
		Detail.setUser1(2);
		Detail.setGroup_chat(true);	
		Detail.setGroupid(2);
		Detail.setGroup_name("Test Group");
		details.add(Detail);
		
		return details;
	}

}
