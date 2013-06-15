package com.example.helpers;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

public class MyToast {
	public static void toastMessage(Context context, String msg) {
		CharSequence feedbackMsg = msg;
		Toast toast = Toast.makeText(context, feedbackMsg, Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
		
	}
}
