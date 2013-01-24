package com.example.testapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;

public class Places extends Activity {

	private final long		validTime 		= 900000L; // 15 minuter
	private ProgressDialog	showProgress;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_places);
		
		showProgress = ProgressDialog.show(Places.this, "", Utils.MSG_LOADING_PLACES);
		
		long timeDiff = System.currentTimeMillis() - Utils.lastUpdateTime;
		
		if (Utils.imgArray == null || Utils.markList == null || (Utils.markMap != null && timeDiff < validTime))
			Utils.initFromDB(getApplicationContext(), showProgress, null);
		else
			Utils.initFromCache(null, showProgress);
		
	}

}
