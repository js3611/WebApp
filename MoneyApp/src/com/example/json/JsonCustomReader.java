package com.example.json;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import com.example.moneyapp.R;
import com.example.moneyapp.transaction.TransactionDetail;

import android.util.JsonReader;
import android.util.JsonToken;

public class JsonCustomReader {

	public static int readJsonRetCode(InputStream in)
			throws UnsupportedEncodingException, IOException {
		JsonReader jr = new JsonReader(new BufferedReader(
				new InputStreamReader(in, "UTF-8")));
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
			throws UnsupportedEncodingException, IOException {

		ArrayList<TransactionDetail> details = new ArrayList<TransactionDetail>();
		JsonReader jr = new JsonReader(new BufferedReader(
				new InputStreamReader(in, "UTF-8")));
		jr.setLenient(true);
		jr.beginObject();
		String name = jr.nextName();
		int returnCode= jr.nextInt();
		//skip name for array
		jr.skipValue();//jr.nextName();

		jr.beginArray();
		while (jr.hasNext()) {
			details.add(readData(jr));
		}
		jr.endArray();

		jr.endObject();
		return details;
	}

	private static TransactionDetail readData(JsonReader jr) throws IOException {
		int icon = R.drawable.ic_launcher;
		String owesuser = null;
		String user = null;
		String subject = null;
		double price = 0;
		String data = null;
		String deadline = null;

		jr.beginObject();
		while (jr.hasNext()) {
			String name = jr.nextName();
			if (name.equals("userfname")) {
				user = jr.nextString();
			} else if (name.equals("owesfname")) {
				owesuser = jr.nextString();
			} else if (name.equals("name")) {
				subject = jr.nextString();
			} else if (name.equals("price")) {
				price = jr.nextDouble();
			} else {
				jr.skipValue();
			}
		}
		jr.endObject();
		return new TransactionDetail(icon, owesuser, user, subject, price, data,
				deadline);
	}

}
