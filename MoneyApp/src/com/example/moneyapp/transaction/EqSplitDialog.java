package com.example.moneyapp.transaction;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;

import com.example.moneyapp.MainMenu;
import com.example.moneyapp.R;
import com.example.moneyapp.transaction.PayDialog.NoticeDialogListener;

public class EqSplitDialog extends DialogFragment {
	private static final String VIEW_MODE = "com.moneyapp.view_mode";
	protected static final int PARTIAL_PAY = 0;
	
	public EqSplitDialog() {}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	    // Get the layout inflater
		/* Add the buttons		*/
		builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	// Send the positive button event back to the host activity
                       mListener.onDialogPositiveClick(EqSplitDialog.this);
		           }
		       });
    
		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
	           public void onClick(DialogInterface dialog, int id) {
	               // User cancelled the dialog
	        	// Send the positive button event back to the host activity
                dismiss();
	           }
	       });
		
		builder.setNeutralButton("No", new DialogInterface.OnClickListener() {
	           public void onClick(DialogInterface dialog, int id) {
	               // User cancelled the dialog
	        	// Send the positive button event back to the host activity
                mListener.onDialogNegativeClick(EqSplitDialog.this);
	           }
	       });

		builder.setTitle("Would you like to include yourself?");
		// Create the AlertDialog
		AlertDialog dialog = builder.create();
		return dialog;
	}

	/*
	 * The activity that creates an instance of this dialog fragment must
	 * implement this interface in order to receive event callbacks. Each method
	 * passes the DialogFragment in case the host needs to query it.
	 */
	public interface NoticeDialogListener {

		public void onDialogPositiveClick(DialogFragment dialog);
				
		public void onDialogNegativeClick(DialogFragment dialog);
	}

	// Use this instance of the interface to deliver action events
	NoticeDialogListener mListener;

	// Override the Fragment.onAttach() method to instantiate the
	// NoticeDialogListener
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		// Verify that the host activity implements the callback interface
		try {
			// Instantiate the NoticeDialogListener so we can send events to the
			// host
			mListener = (NoticeDialogListener) activity;
		} catch (ClassCastException e) {
			// The activity doesn't implement the interface, throw exception
			throw new ClassCastException(activity.toString()
					+ " must implement NoticeDialogListener");
		}
	}

}
