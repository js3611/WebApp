package com.example.helpers;

import com.example.helpers.metadata.Pair;



public class AdminHelper {
	
	static String errorMessage;
	private static Pair<String,Boolean> goodResult = new Pair<String, Boolean>("", true);
	
	public static Pair<String, Boolean> handleResponse(int response) {
		switch (response) {
		case 0:
			errorMessage = "No operation specified. This shouldn't happen though.";
			break;
		case 1: //Correct password
			return goodResult;
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
			return goodResult;
		case 6: // Phone number / Account already exists
			errorMessage = "Phone Number is already registered!";
			break;
		case 7: // unknown error, nothing inserted
			errorMessage = "There was an error with sign up! Please retry.";
			break;
		case 20:
			errorMessage = "No transaction to show";
			return new Pair<String, Boolean>(errorMessage, true);
		default:
			errorMessage = "Something went wrong!";
			break;
		}		
		return new Pair<String, Boolean>(errorMessage, false);
	}

}
