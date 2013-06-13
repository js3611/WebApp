package com.example.moneyapp.transaction;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.helpers.metadata.UserDetails;
import com.example.moneyapp.R;

public class NewPersonAdapter extends CustomAdapter<UserDetails> {

	public NewPersonAdapter(ArrayList<UserDetails> data, Context c) {
		super(data, c);
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
			switch (type) {
			case TYPE_NORMAL_ENTRY:
				v = mInflator.inflate(
						R.layout.transaction_new_person_entry, null);

				viewHolder.image = (ImageView) v
						.findViewById(R.id.profile_icon);
				viewHolder.name_view = (TextView) v
						.findViewById(R.id.name_view);
				break;

			case TYPE_NEW_ENTRY:
				v = mInflator.inflate(R.layout.new_transaction_entry_layout, null);

				viewHolder.image = (ImageView) v.findViewById(R.id.add_icon);
				viewHolder.name_view = (TextView) v
						.findViewById(R.id.add_transaction);
				break;
			}
			v.setTag(viewHolder);
		} else {
			viewHolder = (NewPersonViewHolder) v.getTag();
		}
		/* Fill the content with data here */
		if (type == TYPE_NEW_ENTRY)
			return v;
		UserDetails detail = _data.get(position);
		viewHolder.image.setImageResource(detail.getProfilePicture());
		viewHolder.name_view.setText(detail.getFirstName()+ " " +detail.getSurname());
		return v;
	}

	public void onEntryClick(View view) {

	}

	/* Customised view holder */
	static class NewPersonViewHolder {
		ImageView image;
		TextView name_view;
	}
	
}
