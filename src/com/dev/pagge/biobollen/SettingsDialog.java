package com.dev.pagge.biobollen;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.Toast;

public class SettingsDialog extends DialogFragment {
	private SettingsDialogListener listener;
	
	public interface SettingsDialogListener {
		public void onDialogPositiveClick(DialogFragment dialog);
		public void onDialogNegativeClick(DialogFragment dialog);
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
	Builder builder = new AlertDialog.Builder(getActivity());
	
	builder.setMessage("Enter your settings here: ")
				.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						listener.onDialogPositiveClick(SettingsDialog.this);
					}
				})
				.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						listener.onDialogNegativeClick(SettingsDialog.this);
					}
				});
		
		return builder.create();
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			listener = (SettingsDialogListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement SettingsDialogListener");
		}
	}
}
