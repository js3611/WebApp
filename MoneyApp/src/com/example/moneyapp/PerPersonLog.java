package com.example.moneyapp;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.helpers.CustomAdapter;
import com.example.helpers.MessageAdapter;
import com.example.helpers.metadata.MessageDetails;

public class PerPersonLog extends Activity {

	    ListView msgList;
	    ArrayList<MessageDetails> details;
	    AdapterView.AdapterContextMenuInfo info;
	        
	        @Override
	        protected void onCreate(Bundle savedInstanceState) {
	            // TODO Auto-generated method stub
	            super.onCreate(savedInstanceState);
	            setContentView(R.layout.per_item_list_layout);
	            
	            msgList = (ListView) findViewById(R.id.MessageList);
	            
	            details = new ArrayList<MessageDetails>();
	                
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
	            
	            msgList.setAdapter(new MessageAdapter(details , this));
	}

}
