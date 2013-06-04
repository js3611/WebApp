package com.example.helpers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.moneyapp.R;

public class CustomAdapter extends ArrayAdapter<String> {
	private final Context context;
	private final String[] values;
 
	public CustomAdapter(Context context, String[] values) {
		super(context, R.layout.list_faces, values);
		this.context = context;
		this.values = values;
	}
 
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
			.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
 
		View rowView = inflater.inflate(R.layout.list_faces, parent, false);
		TextView textView = (TextView) rowView.findViewById(R.id.label);
		ImageView imageView = (ImageView) rowView.findViewById(R.id.logo);
		textView.setText(values[position]);
 
		// Change icon based on name
		String s = values[position];
 
		System.out.println(s);
 
		if (s.equals("jo")) {
			imageView.setImageResource(R.drawable.jo);
		} else if (s.equals("thai")) {
			imageView.setImageResource(R.drawable.thai);
		} else {
			imageView.setImageResource(R.drawable.terence);
		} 
 
		return rowView;
	}
}