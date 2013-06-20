package com.example.moneyapp.message;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.helpers.metadata.FriendsList;
import com.example.helpers.metadata.MessageDetails;
import com.example.helpers.metadata.UserDetails;
import com.example.moneyapp.R;

public class MessageAdapter extends BaseAdapter {

	private String TAG = "MessageAdapter";
	private ArrayList<MessageDetails> _data;
	Context _c;
	UserDetails user;
	
    public MessageAdapter (ArrayList<MessageDetails> data, Context c, UserDetails user){
        _data = data;
        _c = c;
        this.user = user;
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
		
			viewHolder.image = (ImageView) v.findViewById(R.id.icon_view);
	        viewHolder.name_view = (TextView) v.findViewById(R.id.name_view);
	        viewHolder.time_view = (TextView) v.findViewById(R.id.msgtime_view);
	        viewHolder.date_view = (TextView) v.findViewById(R.id.msgdate_view);
	        v.setTag(viewHolder);
		} else {
			viewHolder = (MessageViewHolder)v.getTag();
		}
        
		String name = null;
        MessageDetails msg = _data.get(position);

        
        if (!msg.getGroup_chat()) {
        	if (user.getUserid() == msg.getUser1()) {
        		
        		name = FriendsList.getFirstname(msg.getUser2());
        	} else {
        		name = FriendsList.getFirstname(msg.getUser1());
        	}
        	
        	viewHolder.image.setImageResource(R.drawable.pleasure);
        }	
        else {
        	name = msg.getGroup_name(); 
        	viewHolder.image.setImageResource(R.drawable.angry);
        }
        	
        viewHolder.name_view.setText(name);
        //gets only part of the content of the last message (how to get last message? can use query on date and time matching on conversationid 
        /*String snippet = msg.getContent(); 
        if (snippet!=null && snippet.length() > 15) {
        	snippet = msg.getContent().substring(0,15) +"...";
        } */
        // viewHolder.message_view.setText(snippet); 
        //viewHolder.date_view.setText(msg.getDate());
        //viewHolder.time_view.setText(msg.getTime());
        
        viewHolder.date_view.setText(msg.getLast_message_date());
        viewHolder.time_view.setText(msg.getLast_message_time());
                     
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
