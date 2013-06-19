package com.example.moneyapp.message;

import java.util.ArrayList;

import com.example.helpers.metadata.MessageDetails;
import com.example.helpers.metadata.UserDetails;
import com.example.moneyapp.R;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

public class MessageContentAdapter extends BaseAdapter {

	protected ArrayList<MessageDetails> _data;
	Context _c;
	LayoutInflater mInflator;
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
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
	
	public MessageContentAdapter(ArrayList<MessageDetails> data, Context downloadDetails) {
		this._data = data;
		this._c = downloadDetails;
		mInflator = (LayoutInflater) _c
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		NewMessageContentViewHolder viewHolder;
		
		if(v==null) {
			viewHolder = new NewMessageContentViewHolder();
			v = mInflator.inflate(R.layout.message_entry, null);
			
			viewHolder.image = (ImageView) v.findViewById(R.id.profile_icon);
			viewHolder.personName = (TextView) v.findViewById(R.id.personName);
			viewHolder.time = (TextView) v.findViewById(R.id.time);
			viewHolder.date = (TextView) v.findViewById(R.id.date);
			viewHolder.message = (TextView) v.findViewById(R.id.message);
			v.setTag(viewHolder);
		}
		else {
			viewHolder = (NewMessageContentViewHolder) v.getTag();
		}
		/*adds content*/
		MessageDetails detail = _data.get(position);
		viewHolder.personName.setText(detail.getFirstname());
		viewHolder.date.setText(detail.getDate());
		viewHolder.time.setText(detail.getTime());
		viewHolder.message.setText(detail.getContent());
		Log.v("getting the view", "getting the view");
		return v;
	}
	
	static class NewMessageContentViewHolder {
		ImageView image;
		TextView personName;
		TextView date;
		TextView time;
		TextView message;
	}

}
