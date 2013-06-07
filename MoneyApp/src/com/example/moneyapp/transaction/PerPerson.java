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
import com.example.moneyapp.message.MessageAdapter;

public class PerPerson extends Activity {

	//The List view
	ListView transList;
	//A list of data for each entry, which the adapter retrieves from.
	ArrayList<PerPersonTransactionDetail> details;
	//what is this?
	//	AdapterView.AdapterContextMenuInfo info;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.per_person_list_layout);

		transList = (ListView) findViewById(R.id.PerPersonList);

		//Create a list which holds data for each entry
		details = new ArrayList<PerPersonTransactionDetail>();
		//Fill the screen with dummy entries
		details = addDummies(details);

		transList.setAdapter(new PerPersonAdapter(details, this));

		registerForContextMenu(transList);

		transList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> a, View v, int pos, long id) {

				// TextView tv = (TextView) v.findViewById(R.id.From);
				// String s = tv.getText().toString();

				startActivity(new Intent(PerPerson.this, PerPersonProfile.class));

			}
		});

	}

	public static ArrayList<PerPersonTransactionDetail> addDummies(ArrayList<PerPersonTransactionDetail> details) {
		PerPersonTransactionDetail Detail;
		Detail = new PerPersonTransactionDetail();
		Detail.setIcon(R.drawable.jo);
		Detail.setName("thai");
		Detail.setSubject("Dinner");
		Detail.setPrice(30);
		details.add(Detail);

		Detail = new PerPersonTransactionDetail();
		Detail.setIcon(R.drawable.jo);
		Detail.setName("terence");
		Detail.setSubject("lunch");
		Detail.setPrice(50);
		details.add(Detail);
		
		Detail = new PerPersonTransactionDetail();
		Detail.setIcon(R.drawable.jo);
		Detail.setName("jo");
		Detail.setSubject("Malaga");
		Detail.setPrice(340);
		details.add(Detail);
		
		Detail = new PerPersonTransactionDetail();
		Detail.setIcon(R.drawable.jo);
		Detail.setName("terence"); 
		Detail.setSubject("Dinner");
		Detail.setPrice(30);
		details.add(Detail);
		
		return details;
	}

}
