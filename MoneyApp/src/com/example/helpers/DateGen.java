package com.example.helpers;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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
	
	public static String getTime() {
		DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
		Date date = new Date();
		return dateFormat.format(date);
	}
}
