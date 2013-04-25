package com.dev.pagge.biobollen;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
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
import com.dev.pagge.biobollen.contactitem.ContactItem;
import com.dev.pagge.biobollen.contactitem.ContactPerson;
import com.dev.pagge.biobollen.contactitem.ContactSep;
import com.dev.pagge.biobollen.contactitem.ContactTitle;

public class Contacts extends SherlockActivity {
	private final String REFRESH_MSG_CONNECTION_FAILURE	= "FAIL";
	private final String REFRESH_MSG_REFRESH_NOT_NEEDED	= "NOT_NEEDED";
	
	private static ArrayList<ContactItem> contactItems			= null;
	private static long lastUpdateTime							= -1L;
	private static String lastUpdateDate						= null;
	private static MenuItem refreshButton						= null;
	private ContactTask contactTask								= null;
	private ListView contactList								= null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTheme(R.style.PurpleTheme);
		
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		
		ActionBar ab = getSupportActionBar();
		ab.setTitle("KONTAKT");
		ab.setSubtitle("Tryck för att ringa ett nummer");
		ab.setDisplayHomeAsUpEnabled(true);
		ab.setDisplayShowHomeEnabled(false);
		ab.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		
		setContentView(R.layout.activity_contacts);
		
		contactList = (ListView) findViewById(R.id.contact_list);
		contactTask = new ContactTask(Contacts.this, false);
		contactTask.execute("");
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		refreshButton = menu.add(0, 0, 0, Utils.REFRESH_BUTTON_TEXT);
		refreshButton.setIcon(R.drawable.refresh_dark);
//		refreshButton.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		refreshButton.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
		if(contactTask.getStatus() != AsyncTask.Status.FINISHED){
			refreshButton.setIcon(null);
			refreshButton.setTitle(Utils.REFRESH_BUTTON_TEXT_PRESSED);
			refreshButton.setEnabled(false);
		}
		refreshButton.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			
			@Override
			public boolean onMenuItemClick(MenuItem menuItem) {
				refreshButton.setIcon(null);
				refreshButton.setTitle(Utils.REFRESH_BUTTON_TEXT_PRESSED);
				refreshButton.setEnabled(false);
				new ContactTask(Contacts.this, true).execute("");
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

	class ContactTask extends AsyncTask<String, Void, Integer> {
		private final int MSG_REFRESH_FROM_DOWNLOAD	= 0;
		private final int MSG_USE_CACHED_DATA		= 1;
		private final int MSG_ERROR_NO_DATA			= 4;
		private final int MSG_ERR_USE_CACHED_DATA	= 5;
		
		private Activity activity;
		private ProgressDialog showProgress;
		private JSONArray array;
		private boolean manualRefresh;

		public ContactTask(Activity a, boolean manRef) {
			this.activity = a;
			this.array = null;
			this.manualRefresh = manRef;
		}
		
		@Override
		protected void onPreExecute() {
			if(contactItems == null) loadFromFile();
			if(contactItems != null) {
				setSupportProgressBarIndeterminateVisibility(true);
				contactList.setAdapter(new ContactAdapter(Contacts.this, contactItems));
			} else {
				showProgress = ProgressDialog.show(Contacts.this, "", Utils.MSG_LOADING_CONTACTS, true, true, new DialogInterface.OnCancelListener() {
					
					@Override
					public void onCancel(DialogInterface dialog) {
//						ContactTask.this.cancel(true); //FIXME:Kanske inte vill köra denna raden, kanske bättre att låta tasken köra klart och bara dölja progressBaren
//						if(newsItems == null)
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
				String updateDate = (System.currentTimeMillis() - lastUpdateTime < Utils.TIME_FIVE_MINUTES && !manualRefresh) ? REFRESH_MSG_REFRESH_NOT_NEEDED : refreshNeeded();
				if(updateDate == REFRESH_MSG_REFRESH_NOT_NEEDED)
					msg = MSG_USE_CACHED_DATA;
				else if(updateDate == REFRESH_MSG_CONNECTION_FAILURE)
					msg = MSG_ERR_USE_CACHED_DATA;
				else {
					this.array = updateNewsInfo(updateDate);
					if(this.array != null)
						msg = MSG_REFRESH_FROM_DOWNLOAD;
					else
						msg = MSG_ERR_USE_CACHED_DATA;
				}
			} else { // CACHE EMPTY
				this.array = updateNewsInfo(null);
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
					Log.i(Utils.TAG, "CONTACTS USING FRESHLY DOWNLOADED " + ((manualRefresh) ? "MANUAL REFRESH" : "SYSTEM REFRESH"));
					initFromDownload();
					contactList.setAdapter(new ContactAdapter(Contacts.this, contactItems));
					if(showProgress != null) showProgress.dismiss();
					lastUpdateTime = System.currentTimeMillis();
					saveToFile();
					break;
				case MSG_ERR_USE_CACHED_DATA:
					Log.i(Utils.TAG, "CONTACTS (no connection) USING CACHED VERSION");
					errMsg = Utils.errWithDate(Utils.ECODE_NO_INTERNET_CONNECTION, new Date(lastUpdateTime), true);
					Utils.showToast(activity, errMsg, Toast.LENGTH_LONG);
					break;
				case MSG_USE_CACHED_DATA:
					long timeDiff = System.currentTimeMillis() - lastUpdateTime;
					Log.i(Utils.TAG, "CONTACTS REFRESH NOT NEEDED, USING CACHED VERSION " + "timeDiff =" + timeDiff + " (" + ((timeDiff / 1000.0) / 60.0) + " min)");
					lastUpdateTime = System.currentTimeMillis();
					if(manualRefresh)
						Utils.showToast(activity, "Ingen data finns att hämta", Toast.LENGTH_LONG);
					break;
				default:
					Log.i(Utils.TAG, "CONTACTS (no connection) NO DATA TO SHOW");
					Utils.showToast(activity, Utils.EMSG_NO_INTERNET_CONNECTION, Toast.LENGTH_LONG);
					if(showProgress != null) showProgress.dismiss();
					break;
			}
			setSupportProgressBarIndeterminateVisibility(false);
			if(refreshButton != null) {
				refreshButton.setTitle(Utils.REFRESH_BUTTON_TEXT);
				refreshButton.setEnabled(true);
				refreshButton.setIcon(R.drawable.refresh_white);
			}
		}
		
		private String refreshNeeded() {
			HttpClient client = new DefaultHttpClient();
			HttpGet request = new HttpGet(Utils.DB_CONTACTS_URL + Utils.DB_MODE_REFRESH);
			BufferedReader br = null;
			try {
				HttpResponse response = client.execute(request);
				int statusCode = response.getStatusLine().getStatusCode();
				if(statusCode == 200) {
					br = new BufferedReader(new InputStreamReader(response.getEntity().getContent()), 8192);
					String line = br.readLine();
					JSONObject o = new JSONObject(line.substring(1, line.length()-1));
					String updateDate = o.getString("UPDATE_TIME");
					if(lastUpdateDate != null)
						return Utils.compareTime(updateDate, lastUpdateDate) > 0 ? updateDate : REFRESH_MSG_REFRESH_NOT_NEEDED;
					else
						return updateDate;
				} else 
					Log.e(Utils.TAG,"CONTACTS  Failed to download JSON file");
			} catch (ClientProtocolException e) {	e.printStackTrace();
			} catch (IOException e) {				e.printStackTrace();
			} catch (JSONException e) {				e.printStackTrace();
			} finally {
				try {
					if(br != null)	br.close();
				} catch (IOException e) {	e.printStackTrace();	}
			}
			return REFRESH_MSG_CONNECTION_FAILURE;
		}
		
		private JSONArray updateNewsInfo(String updateDate) {
			StringBuilder builder = new StringBuilder();
			HttpClient client = new DefaultHttpClient();
			HttpGet request = new HttpGet(Utils.DB_CONTACTS_URL + Utils.DB_MODE_GET);
			BufferedReader br = null;
			try {
				HttpResponse response = client.execute(request);
				int statusCode = response.getStatusLine().getStatusCode();
				if(statusCode == 200) {
					br = new BufferedReader(new InputStreamReader(response.getEntity().getContent()), 8192);
					String line;
					
					while((line = br.readLine()) != null) 
						builder.append(line);
					
					br.close();
					
					if(updateDate != null)
						lastUpdateDate = updateDate;
					else 
						lastUpdateDate = refreshNeeded();
					
					return new JSONArray(builder.toString());
				} else 
					Log.e(Utils.TAG,"CONTACTS  Failed to download JSON file");
			} catch (ClientProtocolException e) {	e.printStackTrace();
			} catch (IOException e) {				e.printStackTrace();
			} catch (JSONException e) {				e.printStackTrace();
			} finally {
				try {
					if(br != null)	br.close();
				} catch (IOException e) {	e.printStackTrace();	}
			}
			return null;
		}

		private void initFromDownload() {
			ArrayList<ContactPerson> list = new ArrayList<ContactPerson>();
			HashMap<String, String> set = new HashMap<String, String>();
			for (int i = 0; i < array.length(); i++) {
				try {
					JSONObject o = this.array.getJSONObject(i);
					ContactPerson p = new ContactPerson();
					p.name = o.getString("name");
					p.phone = o.getString("phone");
					p.title = o.getString("title");
					set.put(p.title, null);
					
					list.add(p);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			
			String[] keys = set.keySet().toArray(new String[set.keySet().size()]);
			
//			Log.v(Utils.TAG, "PRE SORT");
//			for (String s : keys) {
//				Log.v(Utils.TAG, s);
//			}

			Arrays.sort(keys);

//			Log.v(Utils.TAG, "AFTER SORT");
//			for (String s : keys) {
//				Log.v(Utils.TAG, s);
//			}
			
			contactItems = new ArrayList<ContactItem>();
			contactItems.add(new ContactSep());
			for (String s : keys) {
				ContactTitle title = new ContactTitle();
				title.title = s;
				
				contactItems.add(title);
				ArrayList<ContactPerson> persons = new ArrayList<ContactPerson>();
				for(ContactPerson p : list)
					if(p.title.equals(s))
						persons.add(p);
				
				Collections.sort(persons);
				for(ContactPerson p : persons)
					contactItems.add(p);
				
				contactItems.add(new ContactSep());
			}
		}

		private void saveToFile() {
			SharedPreferences prefs = getSharedPreferences(Utils.PREFS_FILE, Context.MODE_PRIVATE);
			Editor editor = prefs.edit();
			try {
				editor.putString(Utils.PREFS_KEY_CONTACT, ObjectSerializer.serialize(contactItems));
				editor.putString(Utils.PREFS_KEY_CONTACT_UPDATE, lastUpdateDate);
				editor.putLong(Utils.PREFS_KEY_CONTACT_TIME, lastUpdateTime);
			} catch (IOException e) {
				e.printStackTrace();
				Log.e(Utils.TAG, "CONTACTS save_to_file IOException");
			}
			editor.commit();
		}
		
		@SuppressWarnings("unchecked")
		private void loadFromFile() {
			SharedPreferences prefs = getSharedPreferences(Utils.PREFS_FILE, Context.MODE_PRIVATE);
			try {
				contactItems = (ArrayList<ContactItem>) ObjectSerializer.deserialize(prefs.getString(Utils.PREFS_KEY_CONTACT, null));
				lastUpdateDate = prefs.getString(Utils.PREFS_KEY_CONTACT_UPDATE, null);
				lastUpdateTime = prefs.getLong(Utils.PREFS_KEY_CONTACT_TIME, -1L);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}