package com.example.moneyapp.transaction;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnFocusChangeListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.helpers.metadata.Pair;
import com.example.helpers.metadata.UserDetails;
import com.example.moneyapp.R;
import com.example.moneyapp.transaction.NewPersonAdapter.NewPersonViewHolder;
import com.example.moneyapp.transaction.PersonAdapter.PerItemViewHolder;

public class OwerAdapter extends PersonAdapter {

	public OwerAdapter(ArrayList<Pair<UserDetails, Double>> person_cost_pairs,
			Context c) {
		super(person_cost_pairs, c);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		final PerItemViewHolder viewHolder;
		/*
		 * convertView caches data. If v is null than you need to inflate the
		 * view using Inflator. In addition, uses viewHolder pattern to reduce
		 * number of "findViewById" operation which is expensive.
		 */
		if (v == null) {
			viewHolder = new PerItemViewHolder();

				v = mInflator.inflate(
						R.layout.transaction_new_transaction_person, null);

				viewHolder.image = (ImageView) v
						.findViewById(R.id.profile_icon);
				viewHolder.name_view = (TextView) v
						.findViewById(R.id.name_view);
				viewHolder.price_text = (EditText) v
						.findViewById(R.id.new_price_text);
				viewHolder.price_text.setKeyListener(null);
				
			
			
			v.setTag(viewHolder);
		} else {
			viewHolder = (PerItemViewHolder) v.getTag();
		}
		/* Fill the content with data here */
		Pair<UserDetails, Double> detail = _data.get(position);
		viewHolder.image.setImageResource(R.drawable.ic_launcher);
		viewHolder.name_view.setText(detail.getFirst().getFirstName());
		viewHolder.price_text.setText(""+detail.getSecond());
		return v;
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
	
}
