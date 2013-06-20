package com.example.moneyapp.transaction;

import java.util.ArrayList;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.helpers.metadata.FriendsList;
import com.example.helpers.metadata.Pair;
import com.example.helpers.metadata.UserDetails;
import com.example.moneyapp.R;

/* Within "new transaction"*/
public class PersonAdapter extends CustomAdapter<Pair<UserDetails, Double>> {

	public PersonAdapter(ArrayList<Pair<UserDetails, Double>> person_cost_pairs, Context c) {
		super(person_cost_pairs, c);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		final PerItemViewHolder viewHolder;
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
				viewHolder.price_text.setOnFocusChangeListener(new OnFocusChangeListener() {
					
					@Override
					public void onFocusChange(View v, boolean hasFocus) {
						double epsilon = 0.000001;
						EditText et = (EditText) viewHolder.price_text;
						try {
						if(Double.parseDouble(et.getText().toString()) <= epsilon) 
							et.setText("");
						} catch (NumberFormatException e) {
							et.setText("");
						}
					}
				});
				
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
		Pair<UserDetails, Double> detail = _data.get(position);
		viewHolder.image.setImageResource(R.drawable.ic_launcher);
		viewHolder.name_view.setText(FriendsList.getFirstname(detail.getFirst().getUserid()));
		viewHolder.price_text.setText(""+detail.getSecond());
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
	
	@Override
	public Object getItem(int position) {
		return _data.get(position).getSecond();
	}
	
	
}
