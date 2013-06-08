package com.example.moneyapp.transaction;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.moneyapp.R;

public class PerItemAdapter extends BaseAdapter {
	private ArrayList<TransactionDetail> _data;
	Context _c;
	
    public PerItemAdapter (ArrayList<TransactionDetail> data, Context c){
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

	/* Fill the entry with data here */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		
		if (v == null) {
			LayoutInflater vi = (LayoutInflater)_c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.transaction_per_item_list_entry_layout, null);
		
		}
        ImageView image = (ImageView) v.findViewById(R.id.profile_icon);
        TextView subject_view = (TextView) v.findViewById(R.id.subject_view);
        TextView price_view = (TextView) v.findViewById(R.id.price_view);
        TextView date_view = (TextView) v.findViewById(R.id.date_view);
        TextView from_view = (TextView) v.findViewById(R.id.from_view);

        TransactionDetail detail = _data.get(position);
        image.setImageResource(detail.getIcon());
        date_view.setText(detail.getDate());
        subject_view.setText(detail.getSubject());
        from_view.setText(detail.getFrom());
    	price_view.setText(""+detail.getPrice());
                    
		return v;
	}

	public void onEntryClick(View view) {
		 
	}
}
