package com.example.moneyapp.transaction;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.helpers.metadata.MessageDetails;
import com.example.moneyapp.R;

public class MessageAdapter extends BaseAdapter {

	private ArrayList<MessageDetails> _data;
	Context _c;
	
    public MessageAdapter (ArrayList<MessageDetails> data, Context c){
        _data = data;
        _c = c;
    }
	
	//Returns the number of items in the Adapter
	@Override
	public int getCount() {
		return _data.size();
	}

	@Override
	public Object getItem(int position) {
		return _data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (v == null) {
			LayoutInflater vi = (LayoutInflater)_c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.per_item_list_entry_layout, null);
		
		}
        ImageView image = (ImageView) v.findViewById(R.id.iconView);
        TextView fromView = (TextView) v.findViewById(R.id.From);
        TextView subView = (TextView) v.findViewById(R.id.subject);
        TextView descView = (TextView) v.findViewById(R.id.content);
        TextView timeView = (TextView) v.findViewById(R.id.time);

        MessageDetails msg = _data.get(position);
        String name = msg.getFrom();
        if (name.equals("jo")) {
        	 image.setImageResource(R.drawable.jo);
        } else if (name.equals("thai")) {
        	 image.setImageResource(R.drawable.thai); 
        } else {
        	 image.setImageResource(R.drawable.terence);
        }
        fromView.setText(name);
        subView.setText("Subject: "+msg.getSub());
        descView.setText(msg.getDesc());
        timeView.setText(msg.getTime());                             
                     
		return v;
	}

	public void onEntryClick(View view) {
		 
	}
	
	
}
