package com.dev.pagge.biobollen;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.dev.pagge.biobollen.placeitem.PlaceCategory;
import com.dev.pagge.biobollen.placeitem.PlaceInfo;
import com.dev.pagge.biobollen.placeitem.PlaceItem;
import com.dev.pagge.biobollen.placeitem.PlaceSep;
import com.example.testapp.R;

public class Places extends SherlockActivity {
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
		ab.setSubtitle("Tryck för att visa platsen på kartan");
		ab.setDisplayHomeAsUpEnabled(true);
		ab.setDisplayShowHomeEnabled(false);
		ab.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);		

		setContentView(R.layout.activity_places);
		
		placeList = (ListView) findViewById(R.id.places_list);
		placeTask = new PlaceTask(Places.this, false);
		placeTask.execute("");
	}
	
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		refreshButton = menu.add(0, 0, 0, Utils.REFRESH_BUTTON_TEXT);
		refreshButton.setIcon(R.drawable.refresh_white);
		refreshButton.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		if(placeTask.getStatus() != AsyncTask.Status.FINISHED){
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
				new PlaceTask(Places.this, true).execute("");
				return false;
			}
		});
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    if (item.getItemId() == android.R.id.home) {
//	    	if(placeItems == null)
//				lastUpdateDate = null;
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
				Log.v(Utils.TAG, "PLACE placeItems == null");
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
				String updateDate = (System.currentTimeMillis() - lastUpdateTime < Utils.TIME_FIVE_MINUTES && !manualRefresh) ? REFRESH_MSG_REFRESH_NOT_NEEDED : refreshNeeded();
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
					errMsg = Utils.errWithDate(Utils.ECODE_NO_INTERNET_CONNECTION, new Date(lastUpdateTime), true);
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
			if(refreshButton != null) {
				refreshButton.setTitle(Utils.REFRESH_BUTTON_TEXT);
				refreshButton.setEnabled(true);
				refreshButton.setIcon(R.drawable.refresh_white);
			}
		}

		private String refreshNeeded() {
			HttpClient client = new DefaultHttpClient();
			HttpGet request = new HttpGet(Utils.DB_PLACES_URL + Utils.DB_MODE_REFRESH);
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
					Log.e(Utils.TAG,"PLACE  Failed to download JSON file");
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
		
		private JSONArray updatePlaceInfo(String updateDate) {
			StringBuilder builder = new StringBuilder();
			HttpClient client = new DefaultHttpClient();
			HttpGet request = new HttpGet(Utils.DB_PLACES_URL + Utils.DB_MODE_GET);
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
					
					JSONArray array = new JSONArray(builder.toString());
					
					File[] files = getFilesDir().listFiles(new FilenameFilter() {
					    public boolean accept(File dir, String name) {
					        return name.toLowerCase().endsWith(".png");
					    }
					});
					Set<String> set = new HashSet<String>();
					if(files != null)
						for (int i = 0; i < files.length; i++) 
							set.add(files[i].getName());
					
					for (int i = 1; i <= array.length(); i++) {
						Bitmap image = null;
						InputStream is = null;
						FileOutputStream out = null;
						String fileName = i + ".png";
						if(!set.contains(fileName)) {
							Log.v(Utils.TAG, "PLACE NEED TO DOWNLOAD IMAGE " + fileName);
							try {
								is = new URL(Utils.DB_IMAGE_URL + i + ".png").openStream();
								image = BitmapFactory.decodeStream(is);
								out = activity.openFileOutput(fileName, Context.MODE_PRIVATE);
								image.compress(Bitmap.CompressFormat.PNG, 100, out);
							} catch (MalformedURLException e) {
								e.printStackTrace();
							} catch (FileNotFoundException e) {
								Log.e(Utils.TAG, "PLACE Could not save image " + fileName + " to private storage");
							} catch (IOException e) {
								Log.w(Utils.TAG, "PLACE image " + fileName + " could not be found.");
							} finally {
								if(is != null) is.close();
								if(out != null) out.close();
							}
						}
					}
					
					return array;
				} else 
					Log.e(Utils.TAG,"PLACE  Failed to download JSON file");
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
			ArrayList<PlaceInfo> list = new ArrayList<PlaceInfo>();
			HashMap<String, String> set = new HashMap<String, String>();
			for (int i = 0; i < this.array.length(); i++) {
				try {
					JSONObject o = this.array.getJSONObject(i);
					PlaceInfo p = new PlaceInfo();
					p.id = o.getInt("ID");
					p.title = o.getString("TITLE");
					p.address = o.getString("ADDRESS");
					p.desc = o.getString("DESC");
					p.lat = o.getDouble("LATITUDE");
					p.lng = o.getDouble("LONGITUDE");
					p.img = o.getString("CATEGORY");
					p.cat = Utils.translateCategory(p.img);
					
					set.put(p.cat, null);
					
					list.add(p);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			
			String[] keys = set.keySet().toArray(new String[set.keySet().size()]);
			
			Arrays.sort(keys);
			
			placeItems = new ArrayList<PlaceItem>();
			placeItems.add(new PlaceSep());
			for (String category : keys) {
				PlaceCategory cat = new PlaceCategory();
				cat.category = category;
				
				placeItems.add(cat);
				ArrayList<PlaceInfo> places = new ArrayList<PlaceInfo>();
				for (PlaceInfo p : list) 
					if(p.cat.equals(category)) {
						places.add(p);
						cat.img = p.img;
					}
				
				Collections.sort(places);
				for (PlaceInfo placeInfo : places) 
					placeItems.add(placeInfo);
				
				placeItems.add(new PlaceSep());
			}
		}

		private void saveToFile() {
			SharedPreferences prefs = getSharedPreferences(Utils.PREFS_FILE, Context.MODE_PRIVATE);
			Editor editor = prefs.edit();
			try {
				editor.putString(Utils.PREFS_KEY_PLACE, ObjectSerializer.serialize(placeItems));
				editor.putString(Utils.PREFS_KEY_PLACE_UPDATE, lastUpdateDate);
				editor.putLong(Utils.PREFS_KEY_PLACE_TIME, lastUpdateTime);
			} catch (IOException e) {
				e.printStackTrace();
				Log.e(Utils.TAG, "PLACE save_to_file IOException");
			}
			editor.commit();
		}

		@SuppressWarnings("unchecked")
		private void loadFromFile() {
			SharedPreferences prefs = getSharedPreferences(Utils.PREFS_FILE, Context.MODE_PRIVATE);
			try {
				placeItems = (ArrayList<PlaceItem>) ObjectSerializer.deserialize(prefs.getString(Utils.PREFS_KEY_PLACE, null));
				lastUpdateDate = prefs.getString(Utils.PREFS_KEY_PLACE_UPDATE, null);
				lastUpdateTime = prefs.getLong(Utils.PREFS_KEY_PLACE_TIME, -1L);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
