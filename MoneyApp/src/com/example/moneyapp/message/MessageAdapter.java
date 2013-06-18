package com.example.moneyapp.message;

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
		MessageViewHolder viewHolder;
		if (v == null) {
			viewHolder = new MessageViewHolder();
			
			LayoutInflater vi = (LayoutInflater)_c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.message_list_entry, null);
		
			viewHolder.image = (ImageView) v.findViewById(R.id.profile_icon);
	        viewHolder.name_view = (TextView) v.findViewById(R.id.name_view);
	        viewHolder.time_view = (TextView) v.findViewById(R.id.time_view);
	        viewHolder.date_view = (TextView) v.findViewById(R.id.date_view);
	        viewHolder.message_view = (TextView) v.findViewById(R.id.msg_view);
	        v.setTag(viewHolder);
		} else {
			viewHolder = (MessageViewHolder)v.getTag();
		}
        
		String name = null;
        MessageDetails msg = _data.get(position);
        
        if (msg.getGroup_chat()) 
        	name = msg.getFirstname();
        else
        	name = msg.getGroup_name(); 
        	
        if (name.equals("jo")) {
        	 viewHolder.image.setImageResource(R.drawable.jo);
        } else if (name.equals("thai")) {
        	viewHolder.image.setImageResource(R.drawable.thai); 
        } else {
        	 viewHolder.image.setImageResource(R.drawable.terence);
        }
        viewHolder.name_view.setText(name);
        //gets only part of the content of the last message (how to get last message? can use query on date and time matching on conversationid 
        viewHolder.message_view.setText(msg.getContent().substring(0, 15)); 
        viewHolder.date_view.setText(msg.getDate());
        viewHolder.time_view.setText(msg.getTime());
                     
		return v;
	}

	public void onEntryClick(View view) {
		 
	}
	
	static class MessageViewHolder {
		ImageView image;
		TextView name_view;
		TextView message_view;
		TextView date_view;
		TextView time_view;
	}
	
}
