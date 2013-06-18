package com.example.moneyapp.transaction;

import java.io.InputStream;
import java.util.ArrayList;

import com.example.helpers.CustomHttpClient;
import com.example.json.JsonCustomReader;
import com.example.moneyapp.MainActivity;
import com.example.moneyapp.R;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.ListView;

public class DownloadPerItem extends
		AsyncTask<String, Void, ArrayList<TransactionDetail>> {

	ArrayList<TransactionDetail> details;
	ListView transList;
	Context c;
	
	public DownloadPerItem(ListView transList, Context c, ArrayList<TransactionDetail> details) {
		this.c = c;
		this.transList = transList;
		this.details = details;
	}
	
	
	@Override
	protected ArrayList<TransactionDetail> doInBackground(String... params) {
		
		try {
			int userid = 2;
			String op = "viewLiveTransactions";
			String viewMode = "perItem";
			InputStream in = CustomHttpClient.executeHttpGet(MainActivity.URL+
					MainActivity.TRANSACTION + "?"+
					"op="+op+"&"+ 
					"viewMode="+viewMode+"&"+
					"userid="+userid );

			details = JsonCustomReader.readJsonPerPerson(in);
		} catch (Exception e) {
			TransactionDetail Detail;
			Detail = new TransactionDetail();
			Detail.setIcon(R.drawable.ic_launcher);
			Detail.setOwesuser("ERROR"+e.getMessage());
			Detail.setPrice(0);
			details.add(Detail);
		}

		return details;
	}
	
	@Override
	protected void onPostExecute(ArrayList<TransactionDetail> result) {
		//transList.setAdapter(new PerItemAdapter(result, c));
	}

}
