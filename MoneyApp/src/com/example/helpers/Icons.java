package com.example.helpers;

import com.example.moneyapp.R;

public class Icons {

	public static int getIcon(double val) {
		int icon;
		if (val < -100)
			icon = R.drawable.pleasure;
		else if (val <= 0)
			icon = R.drawable.happy;
		else if (val < 100)
			icon = R.drawable.unhappy;
		else
			icon = R.drawable.angry;
		return icon;
	}
	
}
