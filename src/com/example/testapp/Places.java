package com.example.testapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ListView;

public class Places extends Activity {

	private final long validTime 				= 900000L; // 15 minuter
	
	private ProgressDialog	showProgress;
	private ListView newsList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_places);
		
		newsList = (ListView) findViewById(R.id.places_list);
		showProgress = ProgressDialog.show(Places.this, "", Utils.MSG_LOADING_PLACES);
		showProgress.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		
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
