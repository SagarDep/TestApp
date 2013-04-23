package com.dev.pagge.biobollen;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
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
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuItem.OnMenuItemClickListener;
import com.actionbarsherlock.view.Window;
import com.dev.pagge.biobollen.SettingsDialog.SettingsDialogListener;
import com.dev.pagge.biobollen.scheduleitem.CalDate;
import com.dev.pagge.biobollen.scheduleitem.CalDesc;
import com.dev.pagge.biobollen.scheduleitem.CalSep;
import com.dev.pagge.biobollen.scheduleitem.ScheduleItem;

public class Calendar extends SherlockFragmentActivity  implements SettingsDialogListener {
	private final String REFRESH_MSG_CONNECTION_FAILURE	= "FAIL";
	private final String REFRESH_MSG_REFRESH_NOT_NEEDED	= "NOT_NEEDED";

	private static ArrayList<ScheduleItem> calendarItems= null;
	private static long lastUpdateTime					= -1L;
	private static String lastUpdateDate				= null;
	private static MenuItem refreshButton				= null;
	private static boolean showNotifications;
	private static String minutesBeforeEvent			= null;
	private static int notificationOffset				= -1;
	private CalendarTask calendarTask					= null;
	private ListView calendarList						= null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTheme(R.style.RedTheme);
		
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		
		ActionBar ab = getSupportActionBar();
		ab.setTitle("SCHEMA");
		ab.setDisplayHomeAsUpEnabled(true);
		ab.setDisplayShowHomeEnabled(false);
		ab.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);

		setContentView(R.layout.activity_cal);
		
		loadSettings();
		
		calendarList = (ListView) findViewById(R.id.cal_list);
		calendarTask = new CalendarTask(Calendar.this, false);
		calendarTask.execute("");
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		
		MenuItem settingsButton = menu.add(0, 0, 0, "Inställningar");
		settingsButton.setIcon(R.drawable.settings);
		settingsButton.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
		settingsButton.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				if(minutesBeforeEvent == null) {
					loadSettings();
				}
				
				SettingsDialog dialog = new SettingsDialog();
				dialog.minutesBeforeEvent = minutesBeforeEvent;
				dialog.showNotifications = showNotifications;
				dialog.show(getSupportFragmentManager(), "SettingsDialogFragment");
				
				return false;
			}
		});
		
		
		refreshButton = menu.add(0, 1, 0, Utils.REFRESH_BUTTON_TEXT);
		refreshButton.setIcon(R.drawable.refresh_white);
//		refreshButton.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		refreshButton.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
		if(calendarTask.getStatus() != AsyncTask.Status.FINISHED){
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
				new CalendarTask(Calendar.this, true).execute("");
				
				
//				int offsetTime = -1 * Integer.parseInt(minutesBeforeEvent);
//				
//				
//				
//				
//				java.util.Calendar cal = java.util.Calendar.getInstance();
//				cal.set(java.util.Calendar.HOUR_OF_DAY, 23);
//				cal.set(java.util.Calendar.MINUTE, 12);
////				cal.add(java.util.Calendar.SECOND, offsetTime);
//				
//				
//				Intent intent = new Intent(Calendar.this, AlarmReceiver.class);
//				intent.putExtra("alarm_message", "Waddup suckah?");
//				
//				PendingIntent sender = PendingIntent.getBroadcast(Calendar.this, Utils.REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//				AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
//				am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), sender);
				return false;
			}
		});
		
		
	    return true;
	}

	@Override
	public void onDialogPositiveClick(SettingsDialog dialog) {
//		Toast.makeText(this, dialog.minutesBeforeEvent + " " + (dialog.showNotifications? "true" : "false"), Toast.LENGTH_SHORT).show();
		showNotifications = dialog.showNotifications;
		minutesBeforeEvent = dialog.minutesBeforeEvent;
		saveSettings();
		if(showNotifications)
			updateAlarmManager();
	}
	

	@Override
	public void onDialogNegativeClick(SettingsDialog dialog) {
//		Toast.makeText(this, "Negativt svar", Toast.LENGTH_SHORT).show();
	}

	private void saveSettings() {
		SharedPreferences prefs = getSharedPreferences(Utils.PREFS_FILE, Context.MODE_PRIVATE);
		Editor editor = prefs.edit();
		editor.putBoolean(Utils.PREFS_KEY_SCHEDULE_NOTI, showNotifications);
		editor.putString(Utils.PREFS_KEY_SCHEDULE_NOTI_TIME, minutesBeforeEvent);
		editor.commit();
	}

	private void loadSettings() {
		SharedPreferences prefs = getSharedPreferences(Utils.PREFS_FILE, Context.MODE_PRIVATE);
		showNotifications = prefs.getBoolean(Utils.PREFS_KEY_SCHEDULE_NOTI, true);
		minutesBeforeEvent = prefs.getString(Utils.PREFS_KEY_SCHEDULE_NOTI_TIME, "30"); //ÄNDRA TILLBAKA
		notificationOffset = prefs.getInt(Utils.PREFS_KEY_SCHEDULE_NOTI_OFFSET, -1);
	}
	
	public void updateAlarmManager() {
		int offsetTime = -1 * Integer.parseInt(minutesBeforeEvent);
		
		
		
		
//		java.util.Calendar cal = java.util.Calendar.getInstance();
//		cal.add(java.util.Calendar.SECOND, 35);
//		cal.add(java.util.Calendar.SECOND, offsetTime);
//		
//		
//		Intent intent = new Intent(Calendar.this, AlarmReceiver.class);
//		intent.putExtra("alarm_message", "Waddup suckah?");
//		
//		PendingIntent sender = PendingIntent.getBroadcast(Calendar.this, Utils.REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//		AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
//		am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), sender);
		
		int offset = 0;
		AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
		for (int i = 0; i < calendarItems.size(); i++) {
			if(calendarItems.get(i).getType() == ScheduleItem.TYPE_CALDESC) {
				CalDesc item = (CalDesc) calendarItems.get(i);
				java.util.Calendar cal = java.util.Calendar.getInstance();
				
				if(item.getDay().equals("Fredag"))		cal.set(2013, 5-1, 3);
				else if(item.getDay().equals("Lördag"))	cal.set(2013, 5-1, 4);
				else									cal.set(2013, 5-1, 5);
				
				String[] start = item.getTime().split("-")[0].split(":");
				int hours = Integer.parseInt(start[0]);
				int minutes = Integer.parseInt(start[1]);
				
				cal.set(java.util.Calendar.HOUR_OF_DAY, hours);
				cal.set(java.util.Calendar.MINUTE, minutes);
				cal.set(java.util.Calendar.SECOND, 0);
				cal.add(java.util.Calendar.MINUTE, offsetTime);
				
				
				Intent intent = new Intent(Calendar.this, AlarmReceiver.class);
				intent.putExtra("TITLE", item.getDesc());
				intent.putExtra("TIME", item.getTime().split("-")[0]);
				intent.putExtra("PLACE", item.getPlace());
				intent.putExtra("PLACE_ID", item.getPlaceId());
				intent.putExtra("REQ", Utils.REQUEST_CODE + offset);
			
				PendingIntent sender = PendingIntent.getBroadcast(Calendar.this, Utils.REQUEST_CODE + offset, intent, PendingIntent.FLAG_UPDATE_CURRENT);
				am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), sender);
				
				Log.v(Utils.TAG, "ALARM ADDED   " + cal.toString());
				
				offset++;
				break;
			}
		}
		
		notificationOffset = offset;
		
		
		SharedPreferences prefs = getSharedPreferences(Utils.PREFS_FILE, Context.MODE_PRIVATE);
		Editor editor = prefs.edit();
		editor.putInt(Utils.PREFS_KEY_SCHEDULE_NOTI_OFFSET, notificationOffset);
		editor.commit();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    if (item.getItemId() == android.R.id.home) {
	    	if(calendarItems == null)
				lastUpdateDate = null;
	    	finish();
	        return true;
	    }
	    return super.onOptionsItemSelected(item);
	}
	class CalendarTask extends AsyncTask<String, Void, Integer> {
		private final int MSG_REFRESH_FROM_DOWNLOAD	= 0;
		private final int MSG_USE_CACHED_DATA		= 1;
		private final int MSG_ERROR_NO_DATA			= 4;
		private final int MSG_ERR_USE_CACHED_DATA	= 5;
		
		private Activity activity;
		private ProgressDialog showProgress;
		private JSONArray array;
		private boolean manualRefresh;

		public CalendarTask(Activity a, boolean manRef) {
			this.activity = a;
			this.array = null;
			this.manualRefresh = manRef;
		}
		
		@Override
		protected void onPreExecute() {
			if(calendarItems == null)	loadFromFile();
			if(calendarItems != null) {
				setSupportProgressBarIndeterminateVisibility(true);
				calendarList.setAdapter(new CalAdapter(Calendar.this, calendarItems));
			} else {
				showProgress = ProgressDialog.show(Calendar.this, "", Utils.MSG_LOADING_SCHEDULE, true, true, new DialogInterface.OnCancelListener() {
					@Override
					public void onCancel(DialogInterface dialog) {
//						CalendarTask.this.cancel(true);
//						if(calendarItems == null)
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
					this.array = updateCalendarInfo(updateDate);
					if(this.array != null)
						msg = MSG_REFRESH_FROM_DOWNLOAD;
					else
						msg = MSG_ERR_USE_CACHED_DATA;
				}
			} else { // CACHE EMPTY
				this.array = updateCalendarInfo(null);
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
					Log.i(Utils.TAG, "CAL USING FRESHLY DOWNLOADED " + ((manualRefresh) ? "MANUAL REFRESH" : "SYSTEM REFRESH"));
					initFromDownload();
					calendarList.setAdapter(new CalAdapter(Calendar.this, calendarItems));
					if(showProgress != null) showProgress.dismiss();
					lastUpdateTime = System.currentTimeMillis();
					saveToFile();
					updateAlarmManager();
					break;
				case MSG_ERR_USE_CACHED_DATA:
					Log.i(Utils.TAG, "CAL (no connection) USING CACHED VERSION");
					errMsg = Utils.errWithDate(Utils.ECODE_NO_INTERNET_CONNECTION, new Date(lastUpdateTime), true);
					Utils.showToast(activity, errMsg, Toast.LENGTH_LONG);
					break;
				case MSG_USE_CACHED_DATA:
					long timeDiff = System.currentTimeMillis() - lastUpdateTime;
					Log.i(Utils.TAG, "CAL USING CACHED VERSION " + "timeDiff =" + timeDiff + " (" + ((timeDiff / 1000.0) / 60.0) + " min)");
					lastUpdateTime = System.currentTimeMillis();
					if(manualRefresh)
						Utils.showToast(activity, "Inga nya händelser finns att hämta", Toast.LENGTH_LONG);
					break;
				default:
					Log.i(Utils.TAG, "CAL (no connection) NO DATA TO SHOW");
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
			HttpGet request = new HttpGet(Utils.DB_SCHEDULE_URL + Utils.DB_MODE_REFRESH);
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
					Log.e(Utils.TAG,"CAL  Failed to download JSON file");
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
		
		private JSONArray updateCalendarInfo(String updateDate) {
			StringBuilder builder = new StringBuilder();
			HttpClient client = new DefaultHttpClient();
			HttpGet request = new HttpGet(Utils.DB_SCHEDULE_URL + Utils.DB_MODE_GET);
			BufferedReader br = null;
			try {
				HttpResponse response = client.execute(request);
				int statusCode = response.getStatusLine().getStatusCode();
				if(statusCode == 200) {
					br = new BufferedReader(new InputStreamReader(response.getEntity().getContent()), 8192);
					String line;
					
					while((line = br.readLine()) != null)
						builder.append(line);
					
					if(updateDate != null)
						lastUpdateDate = updateDate;
					else
						lastUpdateDate = refreshNeeded();
					
					return new JSONArray(builder.toString());
				} else 
					Log.e(Utils.TAG,"CAL  Failed to download JSON file");
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
			HashMap<String,String> map = new HashMap<String, String>();
			calendarItems = new ArrayList<ScheduleItem>();
			calendarItems.add(new CalSep());
			try {
				for(int i = 0; i < this.array.length(); i++) {
					JSONObject o = this.array.getJSONObject(i);
					if(!map.containsKey(o.getString("scheduleDay"))) {
						if(map.size() != 0) {
							calendarItems.add(new CalSep());
						}
						map.put(o.getString("scheduleDay"), null);
						calendarItems.add(new CalDate(o.getString("scheduleDay"), o.getString("scheduleDate")));
					}
					String time = o.getString("scheduleFrom") + "-" + o.getString("scheduleTo");
					String title = o.getString("scheduleTitle");
					String place = o.getString("schedulePlace");
					String day = o.getString("scheduleDay");
					int id = o.getInt("placeId");
					calendarItems.add(new CalDesc(time, title, place, id, day));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			calendarItems.add(new CalSep());
		}
		
		private void saveToFile() {
			SharedPreferences prefs = getSharedPreferences(Utils.PREFS_FILE, Context.MODE_PRIVATE);
			Editor editor = prefs.edit();
			try {
				editor.putString(Utils.PREFS_KEY_SCHEDULE, ObjectSerializer.serialize(calendarItems));
				editor.putString(Utils.PREFS_KEY_SCHEDULE_UPDATE, lastUpdateDate);
				editor.putLong(Utils.PREFS_KEY_SCHEDULE_TIME, lastUpdateTime);
			} catch (IOException e) {
				e.printStackTrace();
				Log.e(Utils.TAG, "CAL save_to_file IOException");
			}
			editor.commit();
		}
		
		@SuppressWarnings("unchecked")
		private void loadFromFile() {
			SharedPreferences prefs = getSharedPreferences(Utils.PREFS_FILE, Context.MODE_PRIVATE);
			try {
				calendarItems = (ArrayList<ScheduleItem>) ObjectSerializer.deserialize(prefs.getString(Utils.PREFS_KEY_SCHEDULE, null));
				lastUpdateDate = prefs.getString(Utils.PREFS_KEY_SCHEDULE_UPDATE, null);
				lastUpdateTime = prefs.getLong(Utils.PREFS_KEY_SCHEDULE_TIME, -1L);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
