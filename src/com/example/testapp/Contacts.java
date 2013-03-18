package com.example.testapp;

import android.os.Bundle;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;
import com.actionbarsherlock.view.MenuItem.OnMenuItemClickListener;

public class Contacts extends SherlockActivity {

	private MenuItem refreshButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setTheme(R.style.PurpleTheme);
		
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		
		ActionBar ab = getSupportActionBar();
		ab.setTitle("KONTAKTER");
		ab.setDisplayHomeAsUpEnabled(true);
		ab.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		
		setContentView(R.layout.activity_contacts);
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		refreshButton = menu.add(0, 0, 0, Utils.REFRESH_BUTTON_TEXT);
//		refreshButton.setIcon(R.drawable.refresh);
		refreshButton.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
//		if(mapTask.getStatus() != AsyncTask.Status.FINISHED){
//			refreshButton.setTitle(Utils.REFRESH_BUTTON_TEXT_PRESSED);
//			refreshButton.setEnabled(false);
//		}
		refreshButton.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			
			@Override
			public boolean onMenuItemClick(MenuItem menuItem) {
				refreshButton.setTitle(Utils.REFRESH_BUTTON_TEXT_PRESSED);
				refreshButton.setEnabled(false);
//				new MapTask(Map.this, true).execute("");
				return false;
			}
		});
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    if (item.getItemId() == android.R.id.home) {
//	    	if(mapItems == null)
//				lastUpdateDate = null;
	    	finish();
	        return true;
	    }
	    return super.onOptionsItemSelected(item);
	}
	
}
