package com.example.moneyapp.transaction;

import java.util.ArrayList;

import com.example.moneyapp.R;
import com.example.moneyapp.transaction.PerItemAdapter.PerItemViewHolder;

import android.content.Context;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class PersonAdapter extends CustomAdapter<Pair<String, Double>> {

	public PersonAdapter(ArrayList<Pair<String, Double>> data, Context c) {
		super(data, c);
	}

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
						R.layout.transaction_new_transaction_person, null);

				viewHolder.image = (ImageView) v
						.findViewById(R.id.profile_icon);
				viewHolder.name_view = (TextView) v
						.findViewById(R.id.name_view);
				viewHolder.price_text = (EditText) v
						.findViewById(R.id.new_price_text);

				break;

			case TYPE_NEW_ENTRY:
				v = mInflator.inflate(R.layout.new_transaction_entry_layout,
						null);

				viewHolder.image = (ImageView) v.findViewById(R.id.add_icon);
				viewHolder.name_view = (TextView) v
						.findViewById(R.id.add_transaction);
				break;
			}
			v.setTag(viewHolder);
		} else {
			viewHolder = (PerItemViewHolder) v.getTag();
		}
		/* Fill the content with data here */
		if (type == TYPE_NEW_ENTRY) {
			viewHolder.name_view.setText("New Person");
			return v;
		}
		Pair<String, Double> detail = _data.get(position);
		return v;
	}

	public void onEntryClick(View view) {

	}

	/* Customised view holder */
	static class PerItemViewHolder {
		ImageView image;
		TextView name_view;
		EditText price_text;
	}
}
