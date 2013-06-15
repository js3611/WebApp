package com.example.helpers;

import com.example.helpers.metadata.Pair;



public class TransactionHelper {
		
		static String errorMessage;
		private static Pair<String,Boolean> goodResult = new Pair<String, Boolean>("", true);
		
		public static Pair<String, Boolean> handleResponse(int response) {
			switch (response) {
			case 1: //SUCCESSFUL ADDITION
				return goodResult;
			case 2: //DATABASE INSERT GONE WRONG
				errorMessage = "Could not add Transaction, please try again";
				break;
			case 3: //COULD NOT RECOGNISE OPERATION
				errorMessage = "Something went wrong!";
				break;
			case 4: 
				//errorMessage = 
				break;
			case 5: 
				return goodResult;
			case 6: 
				//errorMessage = 
				break;
			case 7: 
				//errorMessage = 
				break;
			case 8: //DEBT SUCCESSFULLY REPAID
				return goodResult;
			case 9: //COULD NOT CHANGE DEBT TO "PAID_OFF"
				errorMessage = "Could not clear debt, please try again";
				break;
			case 10: //DELETED TRANSACTION
				return goodResult;
			case 11: //
				errorMessage = "Could not delete Transaction at this time, please try again";
				break;
			case 12: 
				errorMessage = "Could not complete Transaction, something went wrong";
				break;
			default:
				errorMessage = "Something went wrong!";
				break;
			}
			
			return new Pair<String, Boolean>(errorMessage, false);
		}

	}

