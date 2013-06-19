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
	private int userid;
	ArrayList<MessageDetails> details;
	private String errorMessage = "";
	int next_party;
	String name;
	ListView messageList;
	MessageAdapter mca;
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
		//details = addDummies(details);

		//setListAdapter(new MessageAdapter(details, getActivity()));
		user = UserDetails.getUser(getActivity().getIntent());
	
		userid = user.getUserid();
		new DownloadMessages().execute();
		
		
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
		String name;
		int other_party;
		int conversationid = details.get(position).getConversationID();
		if (details.get(position).getGroup_chat()) {
			name = details.get(position).getFirstname();
			other_party = details.get(position).getGroupid();
		}else {
			name = details.get(position).getGroup_name();
			if (details.get(position).getUser1() == userid)
				other_party = details.get(position).getUser2();
			else
				other_party = details.get(position).getUser1();
		}
		
		// Notify the active callbacks interface (the activity, if the
		// fragment is attached to one) that an item has been selected.
		//mCallbacks.onItemSelected(DummyContent.ITEMS.get(position).id);
		
		Bundle arguments = new Bundle();
		arguments.putString(PersonDetailFragment.ARG_ITEM_ID, getActivity().getIntent()
				.getStringExtra(PersonDetailFragment.ARG_ITEM_ID));
		arguments.putInt("conversationid",conversationid);
		arguments.putString("name", name);
		arguments.putInt("other_person", other_party);
		
		PersonDetailFragment frag = new PersonDetailFragment();
		frag.setArguments(arguments);
		getFragmentManager().beginTransaction().replace(R.id.person_detail_container, frag).commit();
		
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
	
	private class DownloadMessages extends AsyncTask<Void, Void, ArrayList<MessageDetails>> {

		@Override
		protected ArrayList<MessageDetails> doInBackground(Void... params) {
			String op = "messageList";
			InputStream in;
			try {
				details = new ArrayList<MessageDetails>();
				in = CustomHttpClient.executeHttpGet(MainActivity.URL+
						MainActivity.MESSAGE + "?"+
						"op="+op+"&"+ "userid=" + userid);
				processInput(in);
				return details;
			} catch (Exception e) {
				Log.v(TAG,e.getMessage());
			}
			return details;
		}
		
		@Override
		protected void onPostExecute(ArrayList<MessageDetails> result) {
			super.onPostExecute(result);
				
			
			mca = new MessageAdapter(result, getActivity(),user);
			setListAdapter(mca);
			mca.notifyDataSetChanged();
		}
		
		
	}
	
	private boolean processInput(InputStream in){
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

}
