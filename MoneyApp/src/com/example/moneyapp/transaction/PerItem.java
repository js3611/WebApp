package com.example.moneyapp.transaction;

import java.util.ArrayList;

import com.example.moneyapp.MainMenu;
import com.example.moneyapp.R;
import com.example.moneyapp.R.layout;
import com.example.moneyapp.R.menu;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class PerItem extends Activity {

	//The List view
	ListView transList;
	//A list of data for each entry, which the adapter retrieves from.
	ArrayList<TransactionDetail> details;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.transaction_per_person_list_layout);

		transList = (ListView) findViewById(R.id.PerPersonList);

		//Create a list which holds data for each entry
		details = new ArrayList<TransactionDetail>();
		//Fill the screen with dummy entries
		details = addDummies(details);

		transList.setAdapter(new PerItemAdapter(details, this));

		registerForContextMenu(transList);

		transList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> a, View v, int pos, long id) {

				// TextView tv = (TextView) v.findViewById(R.id.From);
				// String s = tv.getText().toString();

				startActivity(new Intent(PerItem.this, MainMenu.class));

			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.per_item, menu);
		return true;
	}
	
	public static ArrayList<TransactionDetail> addDummies(ArrayList<TransactionDetail> details) {
		TransactionDetail Detail;
		Detail = new TransactionDetail();
		Detail.setIcon(R.drawable.thai);
		Detail.setFrom("Thai");
		Detail.setSubject("Dinner");
		Detail.setPrice(30);
		details.add(Detail);

		Detail = new TransactionDetail();
		Detail.setIcon(R.drawable.terence);
		Detail.setFrom("Terence");
		Detail.setSubject("lunch");
		Detail.setPrice(50);
		details.add(Detail);
		
		Detail = new TransactionDetail();
		Detail.setIcon(R.drawable.jo);
		Detail.setFrom("Jo");
		Detail.setSubject("Malaga");
		Detail.setPrice(340);
		details.add(Detail);
		
		Detail = new TransactionDetail();
		Detail.setIcon(R.drawable.terence);
		Detail.setFrom("Terence"); 
		Detail.setSubject("Dinner");
		Detail.setPrice(30);
		details.add(Detail);
		
		return details;
	}

}
