package com.example.moneyapp.transaction;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.moneyapp.R;

public class PerPersonAdapter extends BaseAdapter{

	private ArrayList<TransactionDetail> _data;
	Context _c;
	
    public PerPersonAdapter (ArrayList<TransactionDetail> data, Context c){
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
			v = vi.inflate(R.layout.transaction_per_person_list_entry_layout, null);
		
		}
        ImageView image = (ImageView) v.findViewById(R.id.profile_icon);
        TextView nameView = (TextView) v.findViewById(R.id.nameView);
        TextView priceView = (TextView) v.findViewById(R.id.priceView);

        TransactionDetail detail = _data.get(position);
        String name = detail.getFrom();
        image.setImageResource(detail.getIcon());
        nameView.setText(name);
    	priceView.setText(""+detail.getPrice());
                    
		return v;
	}

	public void onEntryClick(View view) {
		 
	}
	
}
