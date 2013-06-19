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
		case 10:
			errorMessage = "Unsupported Operation";
			break;
		case 20:
			errorMessage = "No transaction to show";
			return new Pair<String, Boolean>(errorMessage, true);
		case 21:
			return goodResult;
		case 22:
			errorMessage = "No transactio to show";
			break;
		case 23:
			return goodResult;
		case 24:
			errorMessage = "failed to partial pay";
			break;
		case 31:
			errorMessage =  "Failed to load list of friends";
			break;
		case 32:
			return goodResult;
		case 33:
			errorMessage = "No friends";
			break;
		case 34:
			errorMessage = "No new requests";
			break;
		case 35:
			errorMessage = "No requests";
			break;
		case 36:
			return goodResult;
		case 37:
			errorMessage = "Failed to send request";
			break;
		case 38:
			return goodResult;
		case 39:
			return goodResult;
		case 310:
			errorMessage = "confirm failed";
			break;
		case 311:
			return goodResult;
		case 312:
			errorMessage = "No such user exists";
			break;
		case 313:
			errorMessage = "existing debts between users"; 
			break;
		case 314:
			errorMessage = "something went wrong with delete";
			break;
		case 315:
			return goodResult;
		case 316:
			return goodResult;
		case 317:
			errorMessage = "something went wrong";
			break;
		case 318:
			errorMessage = "Edit friend failed";
			break;
		case 319:
			return goodResult;
		case 320:
			errorMessage = "Friend request pending";
			break;
		case 321:
			errorMessage  ="This person is in your friend list already";
			break;
		case 42:
			errorMessage  ="You have no current Messages";
			break;
		case 43:
			return goodResult;
		case 44:
			errorMessage  ="Could not retrieve messages! Please try again";
			break;
		case 45:
			return goodResult;
		case 46:
			errorMessage  ="Could not send message. Please try again";
			break;
		case 47:
			return goodResult;
		case 48:
			errorMessage  ="Could not make new message. Please try again";
			break;
		case 410:
			return goodResult;
		case 411:
			errorMessage  ="Could not create this group! please try again";
			break;
		case 412:
			errorMessage  ="Could not add conversation to database, please try again";
			break;
		case 413:
			errorMessage  ="Could not send message to group. Please try again";
			break;
		case 414:
			return goodResult;	
		default:
			errorMessage = "Something went wrong!";
			break;
		}		
		return new Pair<String, Boolean>(errorMessage, false);
	}

	
	
}
