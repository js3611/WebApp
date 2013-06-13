package com.example.json;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import com.example.helpers.metadata.UserDetails;
import com.example.moneyapp.R;
import com.example.moneyapp.transaction.TransactionDetail;

import android.util.JsonReader;
import android.util.JsonToken;
import android.util.Pair;

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

	public static Pair<Integer, UserDetails> readJsonUser(InputStream in)
			throws UnsupportedEncodingException, IOException {
		JsonReader jr = new JsonReader(new BufferedReader(
				new InputStreamReader(in, "UTF-8")));
		jr.setLenient(true);

		int userid = 0;
		String surname = "";
		String firstName = "";
		int calendarid = 0;
		int wishlist = 0;
		String password = ""; // WHY? what if someone wants to change password?
		String phoneNo = "";
		int profilePicture = 0;

		jr.beginObject();
		// Read return code
		jr.nextName();
		int retCode = jr.nextInt();
		// Read content of user
		while (jr.hasNext()) {
			String name = jr.nextName();
			if (name.equals("userid")) {
				userid = jr.nextInt();
			} else if (name.equals("firstname")) {
				firstName = jr.nextString();
			} else if (name.equals("surname")) {
				surname = jr.nextString();
			} else if (name.equals("calendarid")) {
				calendarid = jr.nextInt();
			} else if (name.equals("wishlist")) {
				wishlist = jr.nextInt();
			} else if (name.equals("password")) {
				password = jr.nextString();
			} else if (name.equals("phonenumber")) {
				phoneNo = jr.nextString();
			} else {
				jr.skipValue();
			}
		}
		jr.endObject();
		UserDetails ud = new UserDetails(userid, surname, firstName, calendarid, wishlist, password, phoneNo, profilePicture);
		return new Pair<Integer, UserDetails>(retCode, ud);
	}

	public static ArrayList<TransactionDetail> readJsonPerPerson(InputStream in)
			throws UnsupportedEncodingException, IOException {

		ArrayList<TransactionDetail> details = new ArrayList<TransactionDetail>();
		JsonReader jr = new JsonReader(new BufferedReader(
				new InputStreamReader(in, "UTF-8")));
		jr.setLenient(true);
		jr.beginObject();
		jr.nextName();
		int returnCode = jr.nextInt();
		// skip name for array
		jr.skipValue();// jr.nextName();

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
		int transactionID = 0;
		String owesuser = null;
		String user = null;
		String subject = null;
		double price = 0;
		double partial_pay = 0;
		String date = null;
		String deadline = null;

		jr.beginObject();
		while (jr.hasNext()) {
			String name = jr.nextName();
			if (name.equals("transid")) {
				transactionID = jr.nextInt();
			} else if (name.equals("userid")) {
				user = jr.nextString();
			} else if (name.equals("owesuserid")) {
				owesuser = jr.nextString();
			} else if (name.equals("name")) {
				subject = jr.nextString();
			} else if (name.equals("amount") || name.equals("total_amount")) {
				price = jr.nextDouble();
			} else if (name.equals("partial_pay")) {
				partial_pay = jr.nextDouble();
			} else if (name.equals("_date")) {
				date = jr.nextString();
			} else {
				jr.skipValue();
			}
		}
		jr.endObject();
		return new TransactionDetail(icon, transactionID, owesuser, user,
				subject, price, partial_pay, date, deadline);
	}

	public static Pair<Integer, ArrayList<UserDetails>> readJsonUsers(
			InputStream in) {
		// TODO Auto-generated method stub
		return null;
	}

}
