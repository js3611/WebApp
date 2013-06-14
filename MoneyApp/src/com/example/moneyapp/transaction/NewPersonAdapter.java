package com.example.moneyapp.transaction;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.helpers.metadata.UserDetails;
import com.example.moneyapp.R;

/* Displays friends */
public class NewPersonAdapter extends BaseAdapter {

	protected ArrayList<UserDetails> _data;
	Context _c;
	LayoutInflater mInflator;

	public NewPersonAdapter(ArrayList<UserDetails> data, Context c) {
		_data = data;
		_c = c;
		mInflator = (LayoutInflater) _c
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	/* Fill the entry with data here */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		NewPersonViewHolder viewHolder;
		int type = getItemViewType(position);
		/*
		 * convertView caches data. If v is null than you need to inflate the
		 * view using Inflator. In addition, uses viewHolder pattern to reduce
		 * number of "findViewById" operation which is expensive.
		 */
		if (v == null) {
			viewHolder = new NewPersonViewHolder();
			v = mInflator.inflate(R.layout.transaction_new_person_entry, null);

			viewHolder.image = (ImageView) v.findViewById(R.id.profile_icon);
			viewHolder.name_view = (TextView) v.findViewById(R.id.name_view);
			viewHolder.checkButton1 = (CheckBox) v.findViewById(R.id.checkBox1);
			v.setTag(viewHolder);
		} else {
			viewHolder = (NewPersonViewHolder) v.getTag();
		}
		/* Fill the content with data here */
		UserDetails detail = _data.get(position);
		viewHolder.image.setImageResource(detail.getProfilePicture());
		viewHolder.name_view.setText(detail.getFirstName() + " "
				+ detail.getSurname());
		return v;
	}

	public void onEntryClick(View view) {

	}

	@Override
	public int getCount() {
		return _data.size();
	}

	@Override
	public Object getItem(int pos) {
		return _data.get(pos);
	}

	@Override
	public long getItemId(int pos) {
		return pos;
	}
	
	/* Customised view holder */
	static class NewPersonViewHolder {
		ImageView image;
		TextView name_view;
		CheckBox checkButton1;
	}

}
