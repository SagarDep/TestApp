package com.dev.pagge.biobollen;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Spinner;

import com.actionbarsherlock.app.SherlockDialogFragment;

public class SettingsDialog extends SherlockDialogFragment {
	private SettingsDialogListener listener;
	public String minutesBeforeEvent = null;
	public boolean showNotifications;
	
	public interface SettingsDialogListener {
		public void onDialogPositiveClick(SettingsDialog dialog);
		public void onDialogNegativeClick(SettingsDialog dialog);
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
	Builder builder = new AlertDialog.Builder(getActivity());
	LayoutInflater inflater = getActivity().getLayoutInflater();
	
	View vi = inflater.inflate(R.layout.settings_cal, null);
	builder.setView(vi);
	
	final Spinner spinner = (Spinner) vi.findViewById(R.id.spinner1);
	
	if(minutesBeforeEvent != null)
		spinner.setSelection(Integer.parseInt(minutesBeforeEvent));
	else
		spinner.setSelection(30);
	
	spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> parent, View selectedItemView, int position, long id) {
			minutesBeforeEvent = parent.getItemAtPosition(position).toString();
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			
		}
	});
	
	final CheckBox check = (CheckBox) vi.findViewById(R.id.checkBox1);
	check.setChecked(showNotifications);
	
	if(!check.isChecked()) {
		spinner.setEnabled(false);
	}
	
	check.setOnCheckedChangeListener(new OnCheckedChangeListener() {
		
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			showNotifications = isChecked;
			if(isChecked)
				spinner.setEnabled(true);
			else
				spinner.setEnabled(false);
		}
	});
	
	builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if(!check.isChecked())
							minutesBeforeEvent = null;
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
