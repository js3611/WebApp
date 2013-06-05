package com.example.moneyapp.transaction;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.example.helpers.metadata.MessageDetails;
import com.example.moneyapp.R;

public class PerPerson extends Activity {

	//The List view
	ListView msgList;
	//A list of data for each entry, which the adapter retrieves from.
	ArrayList<MessageDetails> details;
	//what is this?
	//	AdapterView.AdapterContextMenuInfo info;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.per_item_list_layout);

		msgList = (ListView) findViewById(R.id.MessageList);

		//Create a list which holds data for each entry
		details = new ArrayList<MessageDetails>();
		//Fill the screen with dummy entries
		details = addDummies(details);

		msgList.setAdapter(new MessageAdapter(details, this));

		registerForContextMenu(msgList);

		msgList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> a, View v, int pos, long id) {

				// TextView tv = (TextView) v.findViewById(R.id.From);
				// String s = tv.getText().toString();

				startActivity(new Intent(PerPerson.this, PerPersonProfile.class));

			}
		});

	}

	private ArrayList<MessageDetails> addDummies(ArrayList<MessageDetails> details) {
		MessageDetails Detail;
		Detail = new MessageDetails();
		Detail.setIcon(R.drawable.jo);
		Detail.setFrom("jo");
		Detail.setSub("Dinner");
		Detail.setDesc("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nulla auctor.");
		Detail.setTime("12/12/2012 12:12");
		details.add(Detail);

		Detail = new MessageDetails();
		Detail.setIcon(R.drawable.thai);
		Detail.setFrom("thai");
		Detail.setSub("Party");
		Detail.setDesc("Dolor sit amet, consectetur adipiscing elit. Nulla auctor.");
		Detail.setTime("13/12/2012 10:12");
		details.add(Detail);

		Detail = new MessageDetails();
		Detail.setIcon(R.drawable.terence);
		Detail.setFrom("terence");
		Detail.setSub("Mail");
		Detail.setDesc("Lorem ipsum dolor sit amet, consectetur adipiscing elit.");
		Detail.setTime("13/12/2012 02:12");
		details.add(Detail);
		
		return details;
	}

}
