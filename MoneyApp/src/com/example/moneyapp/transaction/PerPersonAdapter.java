package com.example.moneyapp.transaction;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.moneyapp.R;

public class PerPersonAdapter extends CustomAdapter<TransactionDetail> {

	public PerPersonAdapter(ArrayList<TransactionDetail> data, Context c) {
		super(data, c);
	}

	/* Fill the entry with data here */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		PerPersonViewHolder viewHolder;
		int type = getItemViewType(position);

		if (v == null) {
			viewHolder = new PerPersonViewHolder();
			switch (type) {
			case TYPE_NORMAL_ENTRY:
				v = mInflator
						.inflate(
								R.layout.transaction_per_person_list_entry_layout,
								null);

				viewHolder.image = (ImageView) v
						.findViewById(R.id.profile_icon);
				viewHolder.name_view = (TextView) v.findViewById(R.id.nameView);
				viewHolder.price_view = (TextView) v
						.findViewById(R.id.priceView);

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
			viewHolder = (PerPersonViewHolder) v.getTag();
		}
		if (type == TYPE_NEW_ENTRY)
			return v;
		TransactionDetail detail = _data.get(position);
		String name = detail.getFrom();
		viewHolder.image.setImageResource(detail.getIcon());
		viewHolder.name_view.setText(name);
		viewHolder.price_view.setText("" + detail.getPrice());

		return v;
	}

	public void onEntryClick(View view) {

	}

	/* Customised view holder */
	static class PerPersonViewHolder {
		public TextView price_view;
		public TextView name_view;
		ImageView image;
	}

}
