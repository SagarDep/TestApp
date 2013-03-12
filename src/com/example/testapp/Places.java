package com.example.testapp;

import java.util.ArrayList;
import java.util.Date;

import org.json.JSONArray;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuItem.OnMenuItemClickListener;
import com.actionbarsherlock.view.Window;
import com.example.testapp.placeitem.PlaceItem;

public class Places extends SherlockActivity {
	private final long TIME_ONE_MINUTE = 60000;
	
	private final String REFRESH_MSG_CONNECTION_FAILURE	= "FAIL";
	private final String REFRESH_MSG_REFRESH_NOT_NEEDED	= "NOT_NEEDED";
	
	
	private static ArrayList<PlaceItem> placeItems		= null;
	private static long lastUpdateTime					= -1L;
	private static String lastUpdateDate				= null;
	private static MenuItem refreshButton				= null;
	private PlaceTask placeTask 						= null;
	private ListView placeList							= null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTheme(R.style.GreenTheme);
		
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		
		ActionBar ab = getSupportActionBar();
		ab.setTitle("PLATSER");
		ab.setDisplayHomeAsUpEnabled(true);
		ab.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);		

		setContentView(R.layout.activity_places);
		
		placeList = (ListView) findViewById(R.id.places_list);
		placeTask = new PlaceTask(Places.this, false);
		placeTask.execute("");
		
		
//		placeList = (ListView) findViewById(R.id.places_list);
//		showProgress = ProgressDialog.show(Places.this, "", Utils.MSG_LOADING_PLACES);
//		showProgress.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
//		
//		long timeDiff = System.currentTimeMillis() - Utils.lastUpdateTime;
//		
//		if (Utils.markList != null && timeDiff < validTime) {
//			Log.i(Utils.TAG, "PLACES USING CACHED VERSION " + "timeDiff =" + timeDiff + " (" + ((timeDiff / 1000.0) / 60.0) + " min)");
//			placeList.setAdapter(new PlaceAdapter(Places.this, Utils.placeList));
//			showProgress.dismiss();
//		} else {
//			Utils.initFromDB(this, showProgress, null, placeList);
//		}
	}
	
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		refreshButton = menu.add(0, 0, 0, Utils.REFRESH_BUTTON_TEXT);
//		refreshButton.setIcon(R.drawable.refresh);
		refreshButton.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		if(placeTask.getStatus() != AsyncTask.Status.FINISHED){
			refreshButton.setTitle(Utils.REFRESH_BUTTON_TEXT_PRESSED);
			refreshButton.setEnabled(false);
		}
		refreshButton.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			
			@Override
			public boolean onMenuItemClick(MenuItem menuItem) {
				refreshButton.setTitle(Utils.REFRESH_BUTTON_TEXT_PRESSED);
				refreshButton.setEnabled(false);
				new PlaceTask(Places.this, true).execute("");
				return false;
			}
		});
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    if (item.getItemId() == android.R.id.home) {
	    	if(placeItems == null)
				lastUpdateDate = null;
	    	finish();
	        return true;
	    }
	    return super.onOptionsItemSelected(item);
	}
	
	class PlaceTask extends AsyncTask<String, Void, Integer> {
		private final int MSG_REFRESH_FROM_DOWNLOAD	= 0;
		private final int MSG_USE_CACHED_DATA		= 1;
		private final int MSG_ERROR_NO_DATA			= 4;
		private final int MSG_ERR_USE_CACHED_DATA	= 5;
		
		private Activity activity;
		private ProgressDialog showProgress;
		private JSONArray array;
		private boolean manualRefresh;

		public PlaceTask(Activity a, boolean manRef) {
			this.activity = a;
			this.array = null;
			this.manualRefresh = manRef;
		}
		
		@Override
		protected void onPreExecute() {
			if(placeItems == null)	loadFromFile();
			if(placeItems != null) {
				setSupportProgressBarIndeterminateVisibility(true);
				placeList.setAdapter(new PlaceAdapter(Places.this, placeItems));
			} else {
				showProgress = ProgressDialog.show(Places.this, "", Utils.MSG_LOADING_PLACES, true, true, new DialogInterface.OnCancelListener() {
					@Override
					public void onCancel(DialogInterface dialog) {
//						PlaceTask.this.cancel(true);
//						if(placeItems == null)
//							lastUpdateDate = null;
						finish();
					}
				});
				showProgress.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
			}
		}
		
		@Override
		protected Integer doInBackground(String... params) {
			int msg = -1;
			
			if(lastUpdateDate != null) { // CACHE AVAILABLE
				String updateDate = (System.currentTimeMillis() - lastUpdateTime < TIME_ONE_MINUTE && !manualRefresh) ? REFRESH_MSG_REFRESH_NOT_NEEDED : refreshNeeded();
				if(updateDate == REFRESH_MSG_REFRESH_NOT_NEEDED)
					msg = MSG_USE_CACHED_DATA;
				else if(updateDate == REFRESH_MSG_CONNECTION_FAILURE)
					msg = MSG_ERR_USE_CACHED_DATA;
				else {
					this.array = updatePlaceInfo(updateDate);
					if(this.array != null)
						msg = MSG_REFRESH_FROM_DOWNLOAD;
					else
						msg = MSG_ERR_USE_CACHED_DATA;
				}
			} else { // CACHE EMPTY
				this.array = updatePlaceInfo(null);
				if(this.array != null)
					msg = MSG_REFRESH_FROM_DOWNLOAD;
				else
					msg = MSG_ERROR_NO_DATA;
			}
			return msg;
		}

		@Override
		protected void onPostExecute(final Integer msg) {
			String errMsg;
			switch(msg) {
				case MSG_REFRESH_FROM_DOWNLOAD:
					Log.i(Utils.TAG, "PLACE USING FRESHLY DOWNLOADED " + ((manualRefresh) ? "MANUAL REFRESH" : "SYSTEM REFRESH"));
					initFromDownload();
					placeList.setAdapter(new PlaceAdapter(Places.this, placeItems));
					if(showProgress != null) showProgress.dismiss();
					lastUpdateTime = System.currentTimeMillis();
					saveToFile();
					break;
				case MSG_ERR_USE_CACHED_DATA:
					Log.i(Utils.TAG, "PLACE (no connection) USING CACHED VERSION");
					errMsg = Utils.errWithDate(Utils.ECODE_NO_INTERNET_CONNECTION, new Date(lastUpdateDate), true);
					Utils.showToast(activity, errMsg, Toast.LENGTH_LONG);
					break;
				case MSG_USE_CACHED_DATA:
					long timeDiff = System.currentTimeMillis() - lastUpdateTime;
					Log.i(Utils.TAG, "PLACE USING CACHED VERSION " + "timeDiff =" + timeDiff + " (" + ((timeDiff / 1000.0) / 60.0) + " min)");
					lastUpdateTime = System.currentTimeMillis();
					if(manualRefresh)
						Utils.showToast(activity, "Ingen ny info att hämta", Toast.LENGTH_LONG);
					break;
				default:
					Log.i(Utils.TAG, "PLACE (no connection) NO DATA TO SHOW");
					Utils.showToast(activity, Utils.EMSG_NO_INTERNET_CONNECTION, Toast.LENGTH_LONG);
					if(showProgress != null) showProgress.dismiss();
					break;
			}
			setSupportProgressBarIndeterminateVisibility(false);
			refreshButton.setTitle(Utils.REFRESH_BUTTON_TEXT);
			refreshButton.setEnabled(true);
		}

		private String refreshNeeded() {
			// TODO Auto-generated method stub
			return null;
		}
		
		private JSONArray updatePlaceInfo(String updateDate) {
			// TODO Auto-generated method stub
			return null;
		}
		
		private void initFromDownload() {
			// TODO Auto-generated method stub
			
		}

		private void loadFromFile() {
			// TODO Auto-generated method stub
			
		}
		
		private void saveToFile() {
			// TODO Auto-generated method stub
			
		}
	}
}




















