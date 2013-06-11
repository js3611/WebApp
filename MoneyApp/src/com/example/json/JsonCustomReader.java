package com.example.json;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import com.example.moneyapp.transaction.TransactionDetail;

import android.util.JsonReader;
import android.util.JsonToken;

public class JsonCustomReader {

	public static int readJsonRetCode(InputStream in)
			throws UnsupportedEncodingException, IOException {
		JsonReader jr = new JsonReader(new BufferedReader(new InputStreamReader(in,"UTF-8")));
		jr.setLenient(true);
		
		jr.beginObject();
		while (jr.peek() != JsonToken.NUMBER) {
			jr.skipValue();
		}
		int response = jr.nextInt();
		jr.endObject();
		return response;
	}

	public static ArrayList<TransactionDetail> readJsonPerPerson(InputStream in) 
			throws UnsupportedEncodingException, IOException{
		ArrayList<TransactionDetail> details = new ArrayList<TransactionDetail>();
		JsonReader jr = new JsonReader(new BufferedReader(new InputStreamReader(in,"UTF-8")));
		jr.setLenient(true);
		jr.beginObject();
		
		
		
		return details;
	}
	
}
