package com.example.testapp;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ListView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuItem.OnMenuItemClickListener;

public class Places extends SherlockActivity {

	private final long validTime 				= 900000L; // 15 minuter
	
	private final Handler handler = new Handler();
	
	private ProgressDialog	showProgress;
	private ListView newsList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTheme(R.style.Theme_Sherlock_Light_DarkActionBar);
		
		setContentView(R.layout.activity_places);
		
		ActionBar ab = getSupportActionBar();
		ab.setTitle("Platser");
		ab.setDisplayHomeAsUpEnabled(true);
		ab.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);		
		
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
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    if (item.getItemId() == android.R.id.home) {
	        finish();
	        return true;
	    }
	    return super.onOptionsItemSelected(item);
	}
}
