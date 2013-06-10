package com.example.helpers;

import android.util.Pair;

public class AdminHelper {
	
	static String errorMessage;
	
	public static Pair<String, Boolean> handleResponse(int response) {
		switch (response) {
		case 1: //Correct password
			return new Pair<String, Boolean>("", true);
		case 2: //Wrong password
			errorMessage = "Invalid phone number or password (pass)";
			break;
		case 3: //Wrong user
			errorMessage = "Invalid phone number or password (phone)";
			break;
		case 4: //Tried too often. 
			errorMessage = "You've tried 3 times already. \n (Ideally shut down the app after dialog box)";
			break;
		case 5: // Sign up account successful
			return new Pair<String, Boolean>("", true);
		case 6: // Phone number / Account already exists
			errorMessage = "Phone Number is already registered!";
			return new Pair<String, Boolean>(errorMessage, false);
		case 7: // unknown error, nothing inserted
			errorMessage = "There was an error with sign up! Please retry.";
			return new Pair<String, Boolean>(errorMessage,false);
		default:
			errorMessage = "Something went wrong!";
			break;
		}
		
		return new Pair<String, Boolean>(errorMessage, false);
	}

}
