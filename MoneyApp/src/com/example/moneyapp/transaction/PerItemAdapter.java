package com.example.moneyapp.transaction;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.moneyapp.R;

public class PerItemAdapter extends CustomAdapter<TransactionDetail> {

	public PerItemAdapter(ArrayList<TransactionDetail> data, Context c) {
		super(data, c);
	}

	/* Fill the entry with data here */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		PerItemViewHolder viewHolder;
		int type = getItemViewType(position);
		/*
		 * convertView caches data. If v is null than you need to inflate the
		 * view using Inflator. In addition, uses viewHolder pattern to reduce
		 * number of "findViewById" operation which is expensive.
		 */
		if (v == null) {
			viewHolder = new PerItemViewHolder();
			switch (type) {
			case TYPE_NORMAL_ENTRY:
				v = mInflator.inflate(
						R.layout.transaction_per_item_list_entry_layout, null);

				viewHolder.image = (ImageView) v
						.findViewById(R.id.profile_icon);
				viewHolder.subject_view = (TextView) v
						.findViewById(R.id.subject_view);
				viewHolder.price_view = (TextView) v
						.findViewById(R.id.price_view);
				viewHolder.date_view = (TextView) v
						.findViewById(R.id.date_view);
				viewHolder.from_view = (TextView) v
						.findViewById(R.id.from_view);
				break;

			case TYPE_NEW_ENTRY:
				v = mInflator.inflate(R.layout.new_transaction_entry_layout, null);

				viewHolder.image = (ImageView) v.findViewById(R.id.add_icon);
				viewHolder.subject_view = (TextView) v
						.findViewById(R.id.add_transaction);
				break;
			}
			v.setTag(viewHolder);
		} else {
			viewHolder = (PerItemViewHolder) v.getTag();
		}
		/* Fill the content with data here */
		if (type == TYPE_NEW_ENTRY)
			return v;
		TransactionDetail detail = _data.get(position);
		viewHolder.image.setImageResource(detail.getIcon());
		viewHolder.date_view.setText(detail.getDate());
		viewHolder.subject_view.setText(detail.getSubject());
		viewHolder.from_view.setText(detail.getOwesuser());
		viewHolder.price_view.setText("" + detail.getPrice());
		return v;
	}

	public void onEntryClick(View view) {

	}

	/* Customised view holder */
	static class PerItemViewHolder {
		ImageView image;
		TextView subject_view;
		TextView price_view;
		TextView date_view;
		TextView from_view;
	}

}
