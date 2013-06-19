package com.example.moneyapp.transaction;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.example.moneyapp.MainMenu;
import com.example.moneyapp.R;
import com.example.moneyapp.transaction.PayDialog.NoticeDialogListener;

public class EnterAmountDialog extends DialogFragment {

	private EditText amount_text;
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	    // Get the layout inflater
	    LayoutInflater inflater = getActivity().getLayoutInflater();

	    View v = inflater.inflate(R.layout.transaction_per_item_partial_enter_amount, null);
		
	    amount_text = (EditText) v.findViewById(R.id.amount_text);
	    
	    builder.setView(v);
      
		builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
	           public void onClick(DialogInterface dialog, int id) {
	               // User cancelled the dialog
	        	// Send the positive button event back to the host activity
                mListener.onAmountDialogNegativeClick(EnterAmountDialog.this);
	           }
	       });
		
		builder.setPositiveButton(R.string.okay, new DialogInterface.OnClickListener() {
	           public void onClick(DialogInterface dialog, int id) {
	               // User cancelled the dialog
	        	// Send the positive button event back to the host activity
                mListener.onAmountDialogPositiveClick(EnterAmountDialog.this);
	           }
	       });

		builder.setTitle("Enter amount:");
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
		public void onAmountDialogPositiveClick(DialogFragment dialog);

		public void onAmountDialogNegativeClick(DialogFragment dialog);		
		
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
	
	public String getAmount() {
		return amount_text.getText().toString();
	}
	
}
