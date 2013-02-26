package com.example.testapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.apache.http.HttpEntity;
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
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.example.testapp.scheduleitem.CalDate;
import com.example.testapp.scheduleitem.CalDesc;
import com.example.testapp.scheduleitem.CalSep;
import com.example.testapp.scheduleitem.ScheduleItem;

public class Calendar extends Activity {

	private final long validTime = 300000L; // 5 minuter

	private static ArrayList<ScheduleItem> scheduleList = null;
	private static long lastUpdateTime = -1L;
	private static int lastUpdateCount = -1;

	private ProgressDialog showProgress;
	private ListView newsList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cal);

		newsList = (ListView) findViewById(R.id.cal_list);
		showProgress = ProgressDialog.show(Calendar.this, "", Utils.MSG_LOADING_SCHEDULE);

		long timeDiff = System.currentTimeMillis() - lastUpdateTime;

		if (scheduleList != null && timeDiff < validTime) {
			Log.i(Utils.TAG, "CAL USING CACHED VERSION " + "timeDiff =" + timeDiff + " (" + ((timeDiff / 1000.0) / 60.0) + " min)");
			newsList.setAdapter(new CalAdapter(Calendar.this, scheduleList));
			showProgress.dismiss();
		} else {
			new CalendarTask(Calendar.this, showProgress).execute("");
		}
	}
	
	class CalendarTask extends AsyncTask<String, Void, Integer> {
		private final int MSG_NO_REFRESH_NEEDED		= 0;
		private final int MSG_REFRESH_FROM_DOWNLOAD	= 1;
		private final int MSG_USE_CACHED_DATA		= 2;
		private final int MSG_LOAD_FROM_FILE		= 3;
		private final int MSG_ERROR_NO_DATA			= 4;
		
		private Activity activity;
		private ProgressDialog showProgress;
		private JSONArray array;

		public CalendarTask(Activity a, ProgressDialog pd) {
			this.activity = a;
			this.showProgress = pd;
			this.array = null;
		}
		
		@Override
		protected void onPreExecute() {
			showProgress.show();
		}
		
		@Override
		protected Integer doInBackground(String... params) {
			int msg = -1;
			if(lastUpdateCount != -1) { //Vi har information sen innan
				if (refreshNeeded()) {
					this.array = updateScheduleInfo();
					if(this.array != null)
						msg = MSG_REFRESH_FROM_DOWNLOAD;
					else
						msg = MSG_USE_CACHED_DATA;
				}
			} else { //Vi har inget alls inläst! Läs in in lastUpdateCount från fil om den finns
				loadFromFile(true);
				if(lastUpdateCount != -1) {
					if (refreshNeeded()) {
						this.array = updateScheduleInfo();
						if(this.array != null)
							msg = MSG_REFRESH_FROM_DOWNLOAD;
						else
							msg = MSG_LOAD_FROM_FILE;
					}
				} else {
					this.array = updateScheduleInfo();
					if(this.array != null)
						msg = MSG_REFRESH_FROM_DOWNLOAD;
					else
						msg = MSG_ERROR_NO_DATA;
				}
			}
			return msg;
		}
		
		@Override
		protected void onPostExecute(final Integer msg) {
			String errMsg;
			switch(msg) {
				case MSG_NO_REFRESH_NEEDED:
					Log.i(Utils.TAG, "CAL NO REFRESH NEEDED");
					showProgress.dismiss();
					break;
				case MSG_REFRESH_FROM_DOWNLOAD:
					Log.i(Utils.TAG, "CAL USING FRESHLY DOWNLOADED");
					initFromDownload();
					newsList.setAdapter(new CalAdapter(Calendar.this, scheduleList));
					showProgress.dismiss();
					lastUpdateTime = System.currentTimeMillis();
					saveToFile();
					break;
				case MSG_USE_CACHED_DATA: //CACHED OLD BUT NO CONNECTION
					Log.i(Utils.TAG, "CAL (no connection) USING CACHED VERSION");
					newsList.setAdapter(new CalAdapter(Calendar.this, scheduleList));
					showProgress.dismiss();
					errMsg = Utils.errWithDate(Utils.ECODE_NO_INTERNET_CONNECTION, new Date(lastUpdateTime), true);
					Utils.showToast(activity, errMsg, Toast.LENGTH_LONG);
					break;
				case MSG_LOAD_FROM_FILE:
					Log.i(Utils.TAG, "CAL (no connection) USING STORED VERSION");
					loadFromFile(false);
					newsList.setAdapter(new CalAdapter(Calendar.this, scheduleList));
					showProgress.dismiss();
					errMsg = Utils.errWithDate(Utils.ECODE_NO_INTERNET_CONNECTION, new Date(lastUpdateTime), true);
					Utils.showToast(activity, errMsg, Toast.LENGTH_LONG);
					break;
				default:
					Log.i(Utils.TAG, "CAL (no connection) NO DATA TO SHOW");
					Utils.showToast(activity, Utils.EMSG_NO_INTERNET_CONNECTION, Toast.LENGTH_LONG);
					showProgress.dismiss();
					break;
			}
		}
		
		private boolean refreshNeeded() {
			HttpClient client = new DefaultHttpClient();
			HttpGet request = new HttpGet(Utils.DB_SCHEDULE_URL + Utils.DB_MODE_REFRESH);
			try {
				HttpResponse response = client.execute(request);
				int statusCode = response.getStatusLine().getStatusCode();
				if(statusCode == 200) {
					HttpEntity entity = response.getEntity();
					InputStream content = entity.getContent();
					BufferedReader br = new BufferedReader(new InputStreamReader(content));
					String line = br.readLine();
					br.close();
					content.close();
					int count = new JSONObject(line).getInt("count");
					return count > lastUpdateCount;
				} else 
					Log.e(Utils.TAG,"CAL  Failed to download JSON file");
			} catch (ClientProtocolException e) {	e.printStackTrace();
			} catch (IOException e) {				e.printStackTrace();
			} catch (JSONException e) {				e.printStackTrace();
			}
			return true;
		}

		private JSONArray updateScheduleInfo() {
			StringBuilder builder = new StringBuilder();
			HttpClient client = new DefaultHttpClient();
			HttpGet request = new HttpGet(Utils.DB_SCHEDULE_URL + Utils.DB_MODE_GET);
			
			try {
				HttpResponse response = client.execute(request);
				int statusCode = response.getStatusLine().getStatusCode();
				if(statusCode == 200) {
					HttpEntity entity = response.getEntity();
					InputStream content = entity.getContent();
					BufferedReader br = new BufferedReader(new InputStreamReader(content));
					String line;
					
					while((line = br.readLine()) != null)
						builder.append(line);
					
					br.close();
					content.close();
					return new JSONArray(builder.toString());
				} else 
					Log.e(Utils.TAG,"CAL  Failed to download JSON file");
			} catch (ClientProtocolException e) {	e.printStackTrace();
			} catch (IOException e) {				e.printStackTrace();
			} catch (JSONException e) {				e.printStackTrace();
			}

			return null;
		}
		
		private void initFromDownload() {
			HashMap<String,String> map = new HashMap<String, String>();
			scheduleList = new ArrayList<ScheduleItem>();
			try {
				int counter = 0;
				for(int i = 0; i < this.array.length(); i++) {
					JSONObject o = this.array.getJSONObject(i);
					if(!map.containsKey(o.getString("scheduleDay"))) {
						if(map.size() != 0) {
							scheduleList.add(new CalSep(ScheduleItem.TYPE_CALSEP, counter));
							counter++;
						}
						map.put(o.getString("scheduleDay"), null);
						scheduleList.add(new CalDate(ScheduleItem.TYPE_CALDATE, counter, o.getString("scheduleDay"), o.getString("scheduleDate")));
						counter++;
					}
					int type = ScheduleItem.TYPE_CALDESC;
					String time = o.getString("scheduleFrom") + "-" + o.getString("scheduleTo");
					String title = o.getString("scheduleTitle");
					String place = o.getString("schedulePlace");
					scheduleList.add(new CalDesc(type, counter, time, title, place));
					counter++;
				}
				
				lastUpdateCount = this.array.length();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		private void saveToFile() {
			SharedPreferences prefs = getSharedPreferences(Utils.PREFS_FILE, Context.MODE_PRIVATE);
			Editor editor = prefs.edit();
			try {
				editor.putString(Utils.PREFS_KEY_SCHEDULE, ObjectSerializer.serialize(scheduleList));
				editor.putLong(Utils.PREFS_KEY_SCHEDULE_DATE, lastUpdateTime);
				editor.putInt(Utils.PREFS_KEY_SCHEDULE_COUNT, lastUpdateCount);
			} catch (IOException e) {
				e.printStackTrace();
				Log.e(Utils.TAG, "CAL save_to_file IOException");
			}
			editor.commit();
		}
		
		@SuppressWarnings("unchecked")
		private void loadFromFile(boolean loadOnlyCount) {
			SharedPreferences prefs = getSharedPreferences(Utils.PREFS_FILE, Context.MODE_PRIVATE);
			
			if(loadOnlyCount)
				lastUpdateCount = prefs.getInt(Utils.PREFS_KEY_SCHEDULE_COUNT, -1);
			else {
				try {
					lastUpdateCount = prefs.getInt(Utils.PREFS_KEY_SCHEDULE_COUNT, -1);
					lastUpdateTime = prefs.getLong(Utils.PREFS_KEY_SCHEDULE_DATE, -1L);
					scheduleList = (ArrayList<ScheduleItem>) ObjectSerializer.deserialize(prefs.getString(Utils.PREFS_KEY_SCHEDULE, null));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
