package com.example.testapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;

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
import android.os.Handler;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuItem.OnMenuItemClickListener;
import com.example.testapp.newsitem.NewsItem;
import com.example.testapp.newsitem.NewsPost;
import com.example.testapp.newsitem.NewsSep;

public class News extends SherlockActivity {
	private final long TIME_ONE_MINUTE = 60000;
	
	private final String REFRESH_MSG_CONNECTION_FAILURE	= "FAIL";
	private final String REFRESH_MSG_REFRESH_NOT_NEEDED	= "NOT_NEEDED";
	
	private final Handler handler = new Handler();
	
	private static ArrayList<NewsItem> newsItems		= null;
	private static long lastUpdateTime					= -1L;
	private static String lastUpdateDate				= null;
	private ListView newsList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTheme(R.style.BlueTheme);

		setContentView(R.layout.activity_news);
		
		ActionBar ab = getSupportActionBar();
		ab.setTitle("Nyheter");
		ab.setDisplayHomeAsUpEnabled(true);
		ab.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		
		newsList = (ListView) findViewById(R.id.news_list);
		new NewsTask(News.this).execute("");
	}
	
	public boolean onCreateOptionsMenu(Menu menu) {
//		MenuInflater inflater = getSupportMenuInflater();
//		inflater.inflate(R.menu.menu_items, menu);
		super.onCreateOptionsMenu(menu);
		MenuItem item = menu.add(0, 0, 0, "Refresh");
		item.setIcon(R.drawable.refresh);
		item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    if (item.getItemId() == android.R.id.home) {
	    	finish();
	        return true;
	    }
	    return super.onOptionsItemSelected(item);
	}
	
	class NewsTask extends AsyncTask<String, Void, Integer> {
		private final int MSG_REFRESH_FROM_DOWNLOAD	= 0;
		private final int MSG_USE_CACHED_DATA		= 1;
		private final int MSG_ERROR_NO_DATA			= 4;
		private final int MSG_ERR_USE_CACHED_DATA	= 5;
		
		private Activity activity;
		private ProgressDialog showProgress;
		private JSONArray array;
		
		public NewsTask(Activity a) {
			this.activity = a;
			this.array = null;
		}
		
		@Override
		protected void onPreExecute() {
			if(newsItems == null)	loadFromFile();
			if(newsItems != null)
				newsList.setAdapter(new NewsAdapter(News.this, newsItems));
			else {
				showProgress = ProgressDialog.show(News.this, "", Utils.MSG_LOADING_NEWS, true, true, new DialogInterface.OnCancelListener() {
					@Override
					public void onCancel(DialogInterface dialog) {
						NewsTask.this.cancel(true);
						if(newsItems == null)
							lastUpdateDate = null;
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
				String updateDate = (System.currentTimeMillis() - lastUpdateTime < TIME_ONE_MINUTE) ? REFRESH_MSG_REFRESH_NOT_NEEDED : refreshNeeded();
				if(updateDate == REFRESH_MSG_REFRESH_NOT_NEEDED) {
					msg = MSG_USE_CACHED_DATA;
				} else if(updateDate == REFRESH_MSG_CONNECTION_FAILURE) {
					msg = MSG_ERR_USE_CACHED_DATA;
				} else {
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
					Log.i(Utils.TAG, "NEWS USING FRESHLY DOWNLOADED");
					initFromDownload();
					if(newsList.getAdapter() == null)
						newsList.setAdapter(new NewsAdapter(News.this, newsItems));
					else
						Utils.showToast(activity, "Fler nyheter tillgängliga\nTryck Uppdatera för att visa dem", Toast.LENGTH_LONG);
					if(showProgress != null) showProgress.dismiss();
					lastUpdateTime = System.currentTimeMillis();
					saveToFile();
					break;
				case MSG_ERR_USE_CACHED_DATA:
					Log.i(Utils.TAG, "NEWS (no connection) USING CACHED VERSION");
					errMsg = Utils.errWithDate(Utils.ECODE_NO_INTERNET_CONNECTION, new Date(lastUpdateTime), true);
					Utils.showToast(activity, errMsg, Toast.LENGTH_LONG);
					break;
				case MSG_USE_CACHED_DATA:
					long timeDiff = System.currentTimeMillis() - lastUpdateTime;
					Log.i(Utils.TAG, "NEWS REFRESH NOT NEEDED, USING CACHED VERSION " + "timeDiff =" + timeDiff + " (" + ((timeDiff / 1000.0) / 60.0) + " min)");
					lastUpdateTime = System.currentTimeMillis();
					break;
				default:
					Log.i(Utils.TAG, "NEWS (no connection) NO DATA TO SHOW");
					Utils.showToast(activity, Utils.EMSG_NO_INTERNET_CONNECTION, Toast.LENGTH_LONG);
					if(showProgress != null) showProgress.dismiss();
					break;
			}
		}

		private String refreshNeeded() {
			HttpClient client = new DefaultHttpClient();
			HttpGet request = new HttpGet(Utils.DB_NEWS_URL + Utils.DB_MODE_REFRESH);
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
					Log.e(Utils.TAG,"NEWS  Failed to download JSON file");
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
			HttpGet request = new HttpGet(Utils.DB_NEWS_URL + Utils.DB_MODE_GET);
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
					Log.e(Utils.TAG,"NEWS  Failed to download JSON file");
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
			newsItems = new ArrayList<NewsItem>();
			try {
				for (int i = 0; i < this.array.length(); i++) {
					JSONObject o = this.array.getJSONObject(i);
					
					String title = o.getString("newsTitle");
					String text	 = o.getString("newsText");
					String date  = o.getString("newsDate");
					
					newsItems.add(new NewsPost(title, text, date));
					newsItems.add(new NewsSep());
				}
			} catch (JSONException e) {		e.printStackTrace();
			}
		}
		
	}
	
	private void saveToFile() {
		SharedPreferences prefs = getSharedPreferences(Utils.PREFS_FILE, Context.MODE_PRIVATE);
		Editor editor = prefs.edit();
		try {
			editor.putString(Utils.PREFS_KEY_NEWS, ObjectSerializer.serialize(newsItems));
			editor.putString(Utils.PREFS_KEY_NEWS_UPDATE, lastUpdateDate);
		} catch (IOException e) {
			e.printStackTrace();
			Log.e(Utils.TAG, "NEWS save_to_file IOException");
		}
		editor.commit();
	}
	
	@SuppressWarnings("unchecked")
	private void loadFromFile() {
		SharedPreferences prefs = getSharedPreferences(Utils.PREFS_FILE, Context.MODE_PRIVATE);
		try {
			newsItems = (ArrayList<NewsItem>) ObjectSerializer.deserialize(prefs.getString(Utils.PREFS_KEY_NEWS, null));
			lastUpdateDate = prefs.getString(Utils.PREFS_KEY_NEWS_UPDATE, null);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
