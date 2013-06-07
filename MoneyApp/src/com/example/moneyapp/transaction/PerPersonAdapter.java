package com.example.moneyapp.transaction;

import java.util.ArrayList;

import com.example.helpers.metadata.MessageDetails;
import com.example.moneyapp.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class PerPersonAdapter extends BaseAdapter{

	private ArrayList<PerPersonTransactionDetail> _data;
	Context _c;
	
    public PerPersonAdapter (ArrayList<PerPersonTransactionDetail> data, Context c){
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

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (v == null) {
			LayoutInflater vi = (LayoutInflater)_c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.per_person_list_entry_layout, null);
		
		}
        ImageView image = (ImageView) v.findViewById(R.id.profile_icon);
        TextView nameView = (TextView) v.findViewById(R.id.nameView);
        TextView priceView = (TextView) v.findViewById(R.id.priceView);
        ImageView next = (ImageView) v.findViewById(R.id.next_icon);

        PerPersonTransactionDetail detail = _data.get(position);
        String name = detail.getName();
        if (name.equals("jo")) {
        	 image.setImageResource(R.drawable.jo);
        } else if (name.equals("thai")) {
        	 image.setImageResource(R.drawable.thai); 
        } else {
        	 image.setImageResource(R.drawable.terence);
        }
        nameView.setText(name);
        priceView.setText("The price is"+detail.getPrice());
        next.setImageResource(R.drawable.next_button_icon);
                     
		return v;
	}

	public void onEntryClick(View view) {
		 
	}
	
}
