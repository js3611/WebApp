package com.example.json;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import android.util.JsonReader;
import android.util.JsonToken;

import com.example.helpers.MyMath;
import com.example.helpers.metadata.MessageDetails;
import com.example.helpers.metadata.UserDetails;
import com.example.moneyapp.R;
import com.example.moneyapp.transaction.TransactionDetail;


public class JsonCustomReader {

	private static final String TAG = "JSON";

	public static int readJSONRetCode(JsonReader jr, InputStream in)
			throws UnsupportedEncodingException, IOException {		
		jr.nextName();
		return jr.nextInt();
	}

	
	public static UserDetails readJSONUser(JsonReader jr, InputStream in) 
			throws UnsupportedEncodingException, IOException {
		
		int userid = 0;
		String surname = "";
		String firstName = "";
		int calendarid = 0;
		int wishlist = 0;
		String password = "";
		String phoneNo = "";
		int profilePicture = R.drawable.ic_launcher;
		double amount = 0;

		if (jr.peek() == JsonToken.NAME) {
			jr.nextName();
		}
		
		jr.beginObject();
		// Read content of user
		while (jr.hasNext()) {
			String name = jr.nextName();
			if (name.equals("userid") || name.equals("friendid")) {
				userid = jr.nextInt();
			} else if (name.equals("firstname")
					|| name.equals("friend_firstname")) {
				firstName = jr.nextString();
			} else if (name.equals("surname") || name.equals("friend_surname")) {
				surname = jr.nextString();
			} else if (name.equals("calendarid")) {
				calendarid = jr.nextInt();
			} else if (name.equals("wishlist")) {
				wishlist = jr.nextInt();
			} else if (name.equals("password")) {
				password = jr.nextString();
			} else if (name.equals("phonenumber")) {
				phoneNo = jr.nextString();
			} else if (name.equals("amount")){
				amount = jr.nextDouble();
			} else {
				jr.skipValue();
			}
		}
		jr.endObject();
		UserDetails ud = new UserDetails(userid, surname, firstName,
				calendarid, wishlist, password, phoneNo, profilePicture);
		ud.setAmount(amount);
		//Log.v("JSON", ud.toString());
		return ud;
		
	}

	
	public static ArrayList<UserDetails> readJSONFriends(JsonReader jr, InputStream in)
			throws UnsupportedEncodingException, IOException {
		ArrayList<UserDetails> details = new ArrayList<UserDetails>();
		/* skip name for array */
		jr.nextName();
		/* begin processing array of data */
		jr.beginArray();
		while (jr.hasNext()) {
			details.add(readJSONUser(jr, in));
		}
		jr.endArray();
		return details;
		
	}


	public static ArrayList<TransactionDetail> readJSONTransactions(JsonReader jr, InputStream in) throws UnsupportedEncodingException, IOException {
		
		ArrayList<TransactionDetail> details = new ArrayList<TransactionDetail>();
		//Skip name
		jr.nextName();
		//Read Array
		jr.beginArray();
		while (jr.hasNext()) {
			details.add(readData(jr,in));
		}
		jr.endArray();
		
		return details;
	}

	public static TransactionDetail readData(JsonReader jr,InputStream in) throws IOException {
		int icon = R.drawable.ic_launcher;
		int transactionID = 0;
		String owesuser = null;
		String user = null;
		int owesuserid = 0;
		int userid = 0;
		String subject = null;
		double price = 0;
		double partial_pay = 0;
		String date = null;
		String deadline = null;
		String description = "";
	
		jr.beginObject();
		while (jr.hasNext()) {
			String name = jr.nextName();
			if (name.equals("transid")) {
				transactionID = jr.nextInt();
			} else if (name.equals("userid")) {
				userid = jr.nextInt();
			} else if (name.equals("owesuserid")) {
				owesuserid = jr.nextInt();
			} else if (name.equals("user_fname")) {
				user = jr.nextString();
			} else if (name.equals("owesuser_fname")) {
				owesuser = jr.nextString();
			} else if (name.equals("name")) {
				subject = jr.nextString();
			} else if (name.equals("amount") || name.equals("total_amount")) {
				price = MyMath.round(jr.nextDouble());
			} else if (name.equals("partial_pay")) {
				partial_pay = MyMath.round(jr.nextDouble());
			} else if (name.equals("_date")) {
				date = jr.nextString();
			} else if (name.equals("description")) {
				description = jr.nextString();
			} else {
				jr.skipValue();
			}
		}
		jr.endObject();
		TransactionDetail td = new TransactionDetail(icon, transactionID,
				owesuser, user, owesuserid, userid, subject, price,
				partial_pay, date, deadline);
		td.setDescription(description);
		//Log.v("JSON", "Read transaction detail: " + td.toString());
		return td;
	}

	public static ArrayList<MessageDetails> readJsonMessages(InputStream in)
			throws UnsupportedEncodingException, IOException {
		JsonReader jr = new JsonReader(new BufferedReader(new InputStreamReader(in, "UTF-8")));
		ArrayList<MessageDetails> details = new ArrayList<MessageDetails>();
		/* skip name for array */
		jr.nextName();
		/* begin processing array of data */
		jr.beginArray();
		while (jr.hasNext()) {
			details.add(readMessage(jr));
		}
		jr.endArray();
		return details;
		
	}
	
	private static MessageDetails readMessage(JsonReader jr) throws IOException {
		int icon = R.drawable.ic_launcher;
		int conversationID = 0;
		String last_message_date = null;
		String last_message_time = null;
		int user1 = 0;
		int user2 = 0;
		boolean group_chat = false;;
		int groupid = 0;
		String group_name = null;
		String content = null;
		int senderid = 0;
		String firstname = null;
		String date = null;
		String time = null;
		
		
		jr.beginObject();
		while (jr.hasNext()) {
			String name = jr.nextName();
			if (name.equals("conversationid")) {
				conversationID = jr.nextInt();
			} else if (name.equals("last_message_date")) {
				last_message_date = jr.nextString();
			} else if (name.equals("last_message_time")) {
				last_message_time = jr.nextString();
			} else if (name.equals("user1")) {
				user1 = jr.nextInt();
			} else if (name.equals("user2")) {
				user2 = jr.nextInt();
			} else if (name.equals("group_chat")) {
				group_chat = jr.nextBoolean();
			} else if (name.equals("groupid")) {
				groupid = jr.nextInt();
			} else if (name.equals("group_name")) {
				group_name = jr.nextString();
			} else if (name.equals("content")) {
				content = jr.nextString();
			} else if (name.equals("senderid")) {
				senderid = jr.nextInt();
			} else if (name.equals("firstname")) {
				firstname = jr.nextString();
			} else if (name.equals("date")) {
				date = jr.nextString();
			} else if (name.equals("time")) {
				time = jr.nextString();
			} else {
				jr.skipValue();
			}
		}
		jr.endObject();
		MessageDetails md = new MessageDetails(icon, conversationID, last_message_date, last_message_time,
								user1, user2, group_chat, groupid, group_name, content, senderid,
								firstname, date, time);
		//Log.v("JSON", "Read transaction detail: " + td.toString());
		return md;
	}
	
	public static ArrayList<TransactionDetail> readJsonPerPerson(InputStream in)
			throws UnsupportedEncodingException, IOException {
	
		ArrayList<TransactionDetail> details = new ArrayList<TransactionDetail>();
		JsonReader jr = new JsonReader(new BufferedReader(new InputStreamReader(in, "UTF-8")));
		jr.setLenient(true);
		jr.beginObject();
		jr.nextName();
		int returnCode = jr.nextInt();
		// skip name for array
		jr.skipValue();// jr.nextName();
	
		jr.beginArray();
		while (jr.hasNext()) {
			details.add(readData(jr,in));
		}
		jr.endArray();
	
		jr.endObject();
		return details;
	}

}
