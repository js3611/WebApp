package com.example.helpers;

import java.util.Calendar;

public class DateGen {

	public static String getDate() {
		Calendar cal = Calendar.getInstance();
		int day = cal.get(Calendar.DATE);
		int month = cal.get(Calendar.MONTH);
		int year = cal.get(Calendar.YEAR);
		StringBuilder strB = new StringBuilder();
		strB.append(year).append("-");
		if(month<10)
			strB.append(0);
		strB.append(month).append("-");
		if(day<10)
			strB.append(0);
		strB.append(day);
		return strB.toString();
	}
	
}
