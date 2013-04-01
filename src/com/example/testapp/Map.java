package com.example.testapp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
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
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuItem.OnMenuItemClickListener;
import com.actionbarsherlock.view.Window;
import com.example.testapp.placeitem.PlaceInfo;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class Map extends SherlockFragmentActivity {
	private final LatLng POS_LUND = new LatLng(55.715363, 13.194580); //Gör om beroende på skärmstorlek
	private final long TIME_ONE_MINUTE = 60000;
	
	private final String REFRESH_MSG_CONNECTION_FAILURE = "FAIL";
	private final String REFRESH_MSG_REFRESH_NOT_NEEDED = "NOT_NEEDED";
	
	private static ArrayList<PlaceInfo> mapItems	= null;
	private static long lastUpdateTime				= -1L;
	private static String lastUpdateDate			= null;
	private static MenuItem refreshButton			= null;
	private static MapTask mapTask 					= null;
	private static GoogleMap map 					= null;
	private static HashMap<Integer, Marker> markers	= null;

	private static int markerId						= -1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTheme(R.style.YellowTheme);
		
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		
		ActionBar ab = getSupportActionBar();
		ab.setTitle("KARTA");
		ab.setSubtitle("Tryck för att visa info");
		ab.setDisplayHomeAsUpEnabled(true);
		ab.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		
		setContentView(R.layout.activity_map);
		
		Bundle extras = getIntent().getExtras();
		if(extras != null)
			markerId = extras.getInt("id");
		
		//KANSKE KAN SPARA MAP SOM STATIC OCH KOLLA OM != NULL?
		map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
		map.setMyLocationEnabled(true);
		map.getUiSettings().setZoomControlsEnabled(false);
		
		mapTask = new MapTask(Map.this, false);
		mapTask.execute("");
		
		map.setInfoWindowAdapter(new InfoWindowAdapter() {
			@Override
			public View getInfoContents(Marker marker) {
				View v = getLayoutInflater().inflate(R.layout.map_info, null);

				PlaceInfo info = null;
				for (PlaceInfo p : mapItems) {
					if(marker.getSnippet().equals("" + p.id)) {
						info = p;
						break;
					}
				}

				Bitmap icon = null;
				try {
					FileInputStream fi = openFileInput(info.id + ".png");
					icon = BitmapFactory.decodeStream(fi);
					fi.close();
				} catch (FileNotFoundException e) {
					Log.w(Utils.TAG, "MAP Image " + info.id + ".png not found in private storage");
				} catch (IOException e) {
					Log.w(Utils.TAG, "MAP IOException");
					e.printStackTrace();
				}
				
				ImageView im = (ImageView) v.findViewById(R.id.map_info_icon);
				TextView tv1 = (TextView) v.findViewById(R.id.map_info_title);
				TextView tv2 = (TextView) v.findViewById(R.id.map_info_address);
				TextView tv3 = (TextView) v.findViewById(R.id.map_info_desc);

				if (icon != null) // Else shows pink_square_big
					im.setImageBitmap(icon);
				tv1.setText(info.title);
				tv2.setText(info.address);
				tv3.setText(info.desc);

				return v;
			}

			@Override
			public View getInfoWindow(Marker arg0) {
				return null;
			}
		});
		
		int zoom = Integer.parseInt(getString(R.string.map_default_zoom));
		CameraPosition camPos = CameraPosition.builder().target(POS_LUND).zoom(zoom).build();
		map.moveCamera(CameraUpdateFactory.newCameraPosition(camPos));
	}
	
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		refreshButton = menu.add(0, 0, 0, Utils.REFRESH_BUTTON_TEXT);
		refreshButton.setIcon(R.drawable.refresh_white);
		refreshButton.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		if(mapTask.getStatus() != AsyncTask.Status.FINISHED){
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
//				map.clear(); //TA BORT SEN
				new MapTask(Map.this, true).execute("");
				return false;
			}
		});
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    if (item.getItemId() == android.R.id.home) {
	    	if(mapItems == null)
				lastUpdateDate = null;
	    	finish();
	        return true;
	    }
	    return super.onOptionsItemSelected(item);
	}
	
	
	class MapTask extends AsyncTask<String, Void, Integer> {
		private final int MSG_REFRESH_FROM_DOWNLOAD	= 0;
		private final int MSG_USE_CACHED_DATA		= 1;
		private final int MSG_ERROR_NO_DATA			= 4;
		private final int MSG_ERR_USE_CACHED_DATA	= 5;
		
		private Activity activity;
		private ProgressDialog showProgress;
		private JSONArray array;
		private boolean manualRefresh;

		public MapTask(Activity a, boolean manRef) {
			this.activity = a;
			this.array = null;
			this.manualRefresh = manRef;
		}
		
		//SKA ÄNDRAS
		@Override
		protected void onPreExecute() {
			if(mapItems == null)	loadFromFile();
			if(mapItems != null) {
				setSupportProgressBarIndeterminateVisibility(true);
				addMarkersToMap();
			} else {
				Log.v(Utils.TAG, "MAP mapItems == null");
				showProgress = ProgressDialog.show(Map.this, "", Utils.MSG_LOADING_MAP, true, true, new DialogInterface.OnCancelListener() {
					@Override
					public void onCancel(DialogInterface dialog) {
//						PlaceTask.this.cancel(true);
//						if(mapItems == null)
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

		//SKA ÄNDRAS
		@Override
		protected void onPostExecute(final Integer msg) {
			String errMsg;
			switch(msg) {
				case MSG_REFRESH_FROM_DOWNLOAD:
					Log.i(Utils.TAG, "MAP USING FRESHLY DOWNLOADED " + ((manualRefresh) ? "MANUAL REFRESH" : "SYSTEM REFRESH"));
					initFromDownload();
					addMarkersToMap();
					if(showProgress != null) showProgress.dismiss();
					lastUpdateTime = System.currentTimeMillis();
					saveToFile();
					break;
				case MSG_ERR_USE_CACHED_DATA:
					Log.i(Utils.TAG, "MAP (no connection) USING CACHED VERSION");
					errMsg = Utils.errWithDate(Utils.ECODE_NO_INTERNET_CONNECTION, new Date(lastUpdateTime), true);
					Utils.showToast(activity, errMsg, Toast.LENGTH_LONG);
					break;
				case MSG_USE_CACHED_DATA:
					long timeDiff = System.currentTimeMillis() - lastUpdateTime;
					Log.i(Utils.TAG, "MAP USING CACHED VERSION " + "timeDiff =" + timeDiff + " (" + ((timeDiff / 1000.0) / 60.0) + " min)");
					lastUpdateTime = System.currentTimeMillis();
					if(manualRefresh)
						Utils.showToast(activity, "Ingen ny info att hämta", Toast.LENGTH_LONG);
					break;
				default:
					Log.i(Utils.TAG, "MAP (no connection) NO DATA TO SHOW");
					Utils.showToast(activity, Utils.EMSG_NO_INTERNET_CONNECTION, Toast.LENGTH_LONG);
					if(showProgress != null) showProgress.dismiss();
					break;
			}
			setSupportProgressBarIndeterminateVisibility(false);
			if(refreshButton != null) {
				refreshButton.setIcon(R.drawable.refresh_white);
				refreshButton.setTitle(Utils.REFRESH_BUTTON_TEXT);
				refreshButton.setEnabled(true);
			}
			
			if(markerId > 0 && mapItems != null) {
				PlaceInfo info = null;
				for (PlaceInfo p : mapItems)
					if(p.id == markerId)
						info = p;
				
				if(info != null) {
					//KANSKE BORDE ZOOMA IN LUND OCH BARA TA UPP SKYLTEN ISTÄLLET?
					int zoom = Integer.parseInt(activity.getString(R.string.map_default_zoom));
					CameraPosition camPos = CameraPosition.builder().target(new LatLng(info.lat, info.lng)).zoom(zoom).build();
					map.moveCamera(CameraUpdateFactory.newCameraPosition(camPos));
					markers.get(markerId).showInfoWindow();
				} else {
					Utils.showToast(activity, "Kan inte vissa plats på kartan", Toast.LENGTH_LONG);
				}
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
					Log.e(Utils.TAG,"MAP  Failed to download JSON file");
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
							Log.v(Utils.TAG, "MAP NEED TO DOWNLOAD IMAGE " + fileName);
							try {
								is = new URL(Utils.DB_IMAGE_URL + i + ".png").openStream();
								image = BitmapFactory.decodeStream(is);
								out = activity.openFileOutput(fileName, Context.MODE_PRIVATE);
								image.compress(Bitmap.CompressFormat.PNG, 100, out);
							} catch (MalformedURLException e) {
								e.printStackTrace();
							} catch (FileNotFoundException e) {
								Log.e(Utils.TAG, "MAP Could not save image " + fileName + " to private storage");
							} catch (IOException e) {
								Log.w(Utils.TAG, "MAP image " + fileName + " could not be found.");
							} finally {
								if(is != null) is.close();
								if(out != null) out.close();
							}
						}
					}
					return array;
				} else 
					Log.e(Utils.TAG,"MAP  Failed to download JSON file");
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
			mapItems = new ArrayList<PlaceInfo>();
			
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
					p.cat = o.getString("CATEGORY");
					
					mapItems.add(p);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
		
		private void addMarkersToMap() {
			ArrayList<PlaceInfo> list = mapItems;
			markers = new HashMap<Integer, Marker>();
			map.clear();
			for (PlaceInfo i : list) {
				markers.put(i.id, map.addMarker(new MarkerOptions().position(new LatLng(i.lat, i.lng)).title(i.title).icon(Utils.getMarkerIcon(i.cat)).snippet(""+i.id)));
			}
		}
		
		private void saveToFile() {
			SharedPreferences prefs = getSharedPreferences(Utils.PREFS_FILE, Context.MODE_PRIVATE);
			Editor editor = prefs.edit();
			try {
				editor.putString(Utils.PREFS_KEY_MAP, ObjectSerializer.serialize(mapItems));
				editor.putString(Utils.PREFS_KEY_MAP_UPDATE, lastUpdateDate);
				editor.putLong(Utils.PREFS_KEY_MAP_TIME, lastUpdateTime);
			} catch (IOException e) {
				e.printStackTrace();
				Log.e(Utils.TAG, "MAP save_to_file IOException");
			}
			editor.commit();
		}

		@SuppressWarnings("unchecked")
		private void loadFromFile() {
			SharedPreferences prefs = getSharedPreferences(Utils.PREFS_FILE, Context.MODE_PRIVATE);
			try {
				mapItems = (ArrayList<PlaceInfo>) ObjectSerializer.deserialize(prefs.getString(Utils.PREFS_KEY_MAP, null));
				lastUpdateDate = prefs.getString(Utils.PREFS_KEY_MAP_UPDATE, null);
				lastUpdateTime = prefs.getLong(Utils.PREFS_KEY_MAP_TIME, -1L);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
