package com.example.moneyapp.transaction;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.moneyapp.MainMenu;
import com.example.moneyapp.R;

@SuppressLint("ValidFragment")
public class PayDialog extends DialogFragment {

	private static final String VIEW_MODE = "com.moneyapp.view_mode";
	protected static final int PARTIAL_PAY = 0;
	
	public PayDialog() {}
	
	public static PayDialog newInstance(int view_mode) {
		
		PayDialog f = new PayDialog();
		Bundle args = new Bundle();
		args.putInt(VIEW_MODE, view_mode);
		f.setArguments(args);
		return f;
				
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	    // Get the layout inflater
	    LayoutInflater inflater = getActivity().getLayoutInflater();

	    // Inflate and set the layout for the dialog
	    // Pass null as the parent view because its going in the dialog layout
	    //builder.setView(inflater.inflate(R.layout.transactions_pay_dialog, null));

		
		/* Add the buttons
		builder.setPositiveButton(R.string.okay, new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	// Send the positive button event back to the host activity
                       mListener.onDialogPositiveClick(PayDialog.this);
		           }
		       });
		*/
	    int view_mode = getArguments().getInt(VIEW_MODE);
	    String partial = null;
	    if (view_mode == MainMenu.PER_PERSON_VIEW) {
	    	partial = "Partially (via New Transaction)";
	    } else {
	    	partial = "Partially";
	    }
	    CharSequence[] display = {partial,"Fully"};
        builder.setItems(display, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int which) {
                	   if (which == PARTIAL_PAY) {
                		   Log.v("PayDialog", "Partial payment");
                		   mListener.onDialogPartialClick(PayDialog.this);
                		   
                	   } else {
                		   Log.v("PayDialog", "Full payment");
                		   mListener.onDialogFullyClick(PayDialog.this);
                		   //full payment
                	   }
               }
        });
    
		builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		               // User cancelled the dialog
		        	// Send the positive button event back to the host activity
                       mListener.onDialogNegativeClick(PayDialog.this);
		           }
		       });

		builder.setTitle("How would you like to pay?");
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
		
		public void onDialogPartialClick(DialogFragment dialog);

		public void onDialogFullyClick(DialogFragment dialog);
		
		
		
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
