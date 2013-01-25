package com.example.testapp;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.maps.MapsInitializer;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

public class Places extends Activity {

	private final long validTime 				= 900000L; // 15 minuter
	
	private ProgressDialog	showProgress;
	private ListView newsList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_places);
		
		try {
			MapsInitializer.initialize(Places.this);
		} catch (GooglePlayServicesNotAvailableException e) {
			Log.e(Utils.TAG, "PLACES MapsInitializer Failed!");
			e.printStackTrace();
		}
		
		newsList = (ListView) findViewById(R.id.places_list);
		showProgress = ProgressDialog.show(Places.this, "", Utils.MSG_LOADING_PLACES);
		
		long timeDiff = System.currentTimeMillis() - Utils.lastUpdateTime;
		
		if (Utils.markList != null && timeDiff < validTime) {
			Log.i(Utils.TAG, "PLACES USING CACHED VERSION " + "timeDiff =" + timeDiff + " (" + ((timeDiff / 1000.0) / 60.0) + " min)");
			newsList.setAdapter(new PlaceAdapter(Places.this, Utils.placeList));
			showProgress.dismiss();
		} else {
			Utils.initFromDB(this, showProgress, null, newsList);
		}
	}
}
