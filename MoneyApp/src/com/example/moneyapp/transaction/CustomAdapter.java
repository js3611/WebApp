package com.example.moneyapp.transaction;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;

public abstract class CustomAdapter<T> extends BaseAdapter {

	protected static final int TYPE_NORMAL_ENTRY = 0;
	protected static final int TYPE_NEW_ENTRY = 1;
	protected static final int TYPE_MAX_COUNT = 2;

	protected ArrayList<T> _data;
	Context _c;
	LayoutInflater mInflator;
	
	public CustomAdapter(ArrayList<T> data, Context c) {
		_data = data;
		_c = c;
		mInflator = (LayoutInflater) _c
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	/*
	 * Returns the number of items in the Adapter. +1 for the "new transaction"
	 * entry
	 */
	@Override
	public int getCount() {
		return _data.size() + 1;
	}

	/* If new transaction is selected, need to deal specially */
	@Override
	public Object getItem(int position) {
		return _data.get(position);
	}
	
	@Override
	public long getItemId(int position) {
		return position;
	}
	
	@Override
	public int getViewTypeCount() {
		return TYPE_MAX_COUNT;
	}

	@Override
	public int getItemViewType(int position) {
		return position == _data.size() ? TYPE_NEW_ENTRY : TYPE_NORMAL_ENTRY;
	}

	
}
