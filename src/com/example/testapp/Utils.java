package com.example.testapp;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.util.SparseArray;
import android.widget.Toast;

@SuppressLint("SimpleDateFormat")
public class Utils {
	public static final String TAG							= "APP";
	
	public static final String PREFS_FILE					= "prefsFile";
	public static final String PREFS_KEY_NEWS				= "news";
	public static final String PREFS_KEY_NEWS_DATE			= "newsDate";
	public static final String PREFS_KEY_SCHEDULE			= "schedule";
	public static final String PREFS_KEY_SCHEDULE_DATE		= "scheduleDate";
	public static final String PREFS_KEY_MAP				= "map";
	public static final String PREFS_KEY_MAP_DATE			= "mapDate";
	
	public static final String DB_MARKER_URL				= "http://nutty.rymdraket.net/android/markers.php";
	public static final String DB_IMAGE_URL					= "http://nutty.rymdraket.net/android/imgs/";
	
	public static final String MSG_LOADING_NEWS				= "Laddar nyheter...";
	public static final String MSG_LOADING_SCHEDULE 		= "Laddar schema...";
	public static final String MSG_LOADING_MAP				= "Laddar karta...";

	public static final int ECODE_NO_ERROR				 	= -1;
	public static final int ECODE_NO_INTERNET_CONNECTION	=  0;
	
	public static final String EMSG_NO_INTERNET_CONNECTION	= "Nätverk ej tillgängligt.";
	
	private static final SimpleDateFormat DATE_FORMAT		= new SimpleDateFormat("d MMM HH:mm");


	private static HashMap<String, String> dayMap				= null;
	private static HashMap<String, String> monthMap				= null;
	private static HashMap<String, BitmapDescriptor> iconMap	= null;
	
	public static HashMap<Marker, MarkerInfo>		markMap		= null;
	public static SparseArray<Bitmap>				imgArray	= null;
	
	public static Long	lastUpdateTime							= -1L;

	
	static {
		DATE_FORMAT.setLenient(false);

		dayMap = new HashMap<String, String>();
		dayMap.put("mon,", "Mån,");
		dayMap.put("tue,", "Tis,");
		dayMap.put("wed,", "Ons,");
		dayMap.put("thu,", "Tor,");
		dayMap.put("fri,", "Fre,");
		dayMap.put("sat,", "Lör,");
		dayMap.put("sun,", "Sön,");
		
		monthMap = new HashMap<String, String>();
		monthMap.put("jan", "Jan");
		monthMap.put("feb", "Feb");
		monthMap.put("mar", "Mar");
		monthMap.put("apr", "Apr");
		monthMap.put("may", "Maj");
		monthMap.put("jun", "Jun");
		monthMap.put("jul", "Jul");
		monthMap.put("aug", "Aug");
		monthMap.put("sep", "Sep");
		monthMap.put("oct", "Okt");
		monthMap.put("nov", "Nov");
		monthMap.put("dec", "Dec");
		
		iconMap = new HashMap<String, BitmapDescriptor>();
		iconMap.put("ATM", BitmapDescriptorFactory.fromResource(R.drawable.marker_atm));
		iconMap.put("BASEBALL", BitmapDescriptorFactory.fromResource(R.drawable.marker_baseball));
		iconMap.put("BMC", BitmapDescriptorFactory.fromResource(R.drawable.marker_bmc));
		iconMap.put("FASTFOOD", BitmapDescriptorFactory.fromResource(R.drawable.marker_fastfood));
		iconMap.put("FOOD", BitmapDescriptorFactory.fromResource(R.drawable.marker_food));
		iconMap.put("HOME", BitmapDescriptorFactory.fromResource(R.drawable.marker_home));
		iconMap.put("HOSPITAL", BitmapDescriptorFactory.fromResource(R.drawable.marker_hospital));
		iconMap.put("NATION", BitmapDescriptorFactory.fromResource(R.drawable.marker_nation));
		iconMap.put("STORE", BitmapDescriptorFactory.fromResource(R.drawable.marker_store));
		iconMap.put("TRAIN", BitmapDescriptorFactory.fromResource(R.drawable.marker_train));
	}
	
	public static String errWithDate(int errCode, Date date, boolean newLine) {
		String msg = "";
		switch(errCode) {
			case ECODE_NO_INTERNET_CONNECTION:
				msg = EMSG_NO_INTERNET_CONNECTION;
				msg += (newLine) ? "\n" : " ";
				msg += "Visar data från ";
				msg += translateMonth(DATE_FORMAT.format(date).toString(), 1);
				Log.i(TAG, "UTIL FORM " + msg);
				break;
		}
		return msg;
	}
	
	public static void showToast(Context context, String msg, int duration) {
		Toast.makeText(context, msg, duration).show();
	}
	
	private static String translateMonth(String date, int monthIndex) {
		String[] split = date.toLowerCase().split(" ");
		split[monthIndex] = monthMap.get(split[monthIndex]);
		String res = "";
		for (String s : split)
			res += s + " ";
		return res;
	}
	
	public static String translateDate(String pubDate) {
		String[] split = pubDate.toLowerCase().split(" ");
		split[0] = dayMap.get(split[0]);
		split[2] = monthMap.get(split[2]);
		return split[0] + " " + split[1] + " " + split[2] + " " + split[3];
	}
	
	public static BitmapDescriptor getMarkerIcon(String category) {
		return iconMap.get(category);
	}
	
	public static void initFromDB(Context context, ProgressDialog showProgress, GoogleMap map) {
		new DBTask(context, showProgress, map).execute("");
	}

	public static void initFromCache(GoogleMap map, ProgressDialog showProgress) {
		addMarkers(null, map);
		showProgress.dismiss();
	}
	
	private static void saveInfoToFile(Context activity) {
		ArrayList<MarkerInfo> list = new ArrayList<MarkerInfo>(markMap.values());
		SharedPreferences prefs = activity.getSharedPreferences(Utils.PREFS_FILE, Context.MODE_PRIVATE);
		Editor editor = prefs.edit();
		try {
			editor.putString(Utils.PREFS_KEY_MAP, ObjectSerializer.serialize(list));
			editor.putLong(Utils.PREFS_KEY_MAP_DATE, lastUpdateTime);
		} catch (IOException e) {
			Log.e(Utils.TAG, "MAP Could not save info to PREFS");
			e.printStackTrace();
		}
		editor.commit();
	}

	private static void saveImagesToFile(Context activity) {
		for(int i = 1; i <= imgArray.size(); i++) {
			String fileName = "" + i;
			Bitmap image = imgArray.get(i);
			try {
				FileOutputStream out = activity.openFileOutput(fileName + ".png", Context.MODE_PRIVATE);
				image.compress(Bitmap.CompressFormat.PNG, 100, out);
				out.close();
			} catch (FileNotFoundException e) {
				Log.e(Utils.TAG, "MAP Could not save image " + fileName + ".png to private storage");
				e.printStackTrace();
			} catch (IOException e) {
				Log.e(Utils.TAG, "MAP Could not save image " + fileName + ".png to private storage");
				e.printStackTrace();
			}
		}
	}
	
	private static void initImgArray(Context activity) {
		imgArray = new SparseArray<Bitmap>();
		Bitmap icon = null;
		for (int i = 1; i <= markMap.size(); i++) {
			try {
				FileInputStream fi = activity.openFileInput(i + ".png");
				icon = BitmapFactory.decodeStream(fi);
				imgArray.append(i, icon);
				fi.close();
			} catch (FileNotFoundException e) {
				Log.w(Utils.TAG, "MAP Image " + i + ".png not found in private storage");
			} catch (IOException e) {
				Log.w(Utils.TAG, "MAP IOException");
				e.printStackTrace();
			}
		}
	}

	private static void addMarkers(ArrayList<MarkerInfo> list, GoogleMap map) {
		if(list == null) {
			HashMap<Marker, MarkerInfo> newMap = new HashMap<Marker, MarkerInfo>();
			for (MarkerInfo m : markMap.values()) {
				Marker marker = map.addMarker(new MarkerOptions().position(new LatLng(m.lat, m.lng)).title(m.title).icon(Utils.getMarkerIcon(m.cat)));
				newMap.put(marker, m);
			}
			markMap = newMap;
		} else {
			HashMap<Marker, MarkerInfo> newMap = new HashMap<Marker, MarkerInfo>();
			for (MarkerInfo m : list) {
				Marker marker = map.addMarker(new MarkerOptions().position(new LatLng(m.lat, m.lng)).title(m.title).icon(Utils.getMarkerIcon(m.cat)));
				newMap.put(marker, m);
			}
			markMap = newMap;
		}
	}
	
	static class DBTask extends AsyncTask<String, Void, String> {
		private JSONArray array;
		private boolean connectionOK = true;
		private Context activity;
		private ProgressDialog showProgress;
		private GoogleMap map;

		public DBTask(Context a, ProgressDialog pd, GoogleMap map) {
			this.activity = a;
			this.showProgress = pd;
			this.map = map;
		}

		@Override
		protected void onPreExecute() {
			showProgress.show();
		}

		@Override
		protected String doInBackground(String... strings) {
			StringBuilder builder = new StringBuilder();
			HttpClient client = new DefaultHttpClient();
			HttpGet request = new HttpGet(Utils.DB_MARKER_URL);

			try {
				HttpResponse response = client.execute(request);
				StatusLine statusLine = response.getStatusLine();
				int statusCode = statusLine.getStatusCode();
				if (statusCode == 200) {
					HttpEntity entity = response.getEntity();
					InputStream content = entity.getContent();
					BufferedReader br = new BufferedReader(new InputStreamReader(content));
					String line;
					while ((line = br.readLine()) != null) {
						builder.append(line);
					}
					br.close();
					content.close();
					this.array = new JSONArray(builder.toString());
				} else {
					Log.e(Utils.TAG, "MAP Failed to download JSON file");
				}
			} catch (ClientProtocolException e) {
				Log.e(Utils.TAG, "MAP connectToDB ClientProtocolException");
				e.printStackTrace();
				connectionOK = false;
			} catch (IOException e) { // Ingen kontakt med DB
				Log.e(Utils.TAG, "MAP no connection to DB");
				connectionOK = false;
			} catch (JSONException e) {
				e.printStackTrace();
				connectionOK = false;
			}

			if (connectionOK) {
				// Download images
				imgArray = new SparseArray<Bitmap>();
				for (int i = 1; i <= array.length(); i++) {
					Bitmap icon = null;
					InputStream is;
					try {
						is = new URL(Utils.DB_IMAGE_URL + i + ".png").openStream();
						icon = BitmapFactory.decodeStream(is);
						if (icon != null)
							imgArray.put(i, icon);
					} catch (MalformedURLException e) {
						e.printStackTrace();
					} catch (IOException e) {
						Log.w(Utils.TAG, "MAP image " + i + ".png could not be found.");
					}
				}
			}
			return "";
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void onPostExecute(final String success) {
			if (connectionOK) {
				try {
					markMap = new HashMap<Marker, MarkerInfo>();
					for (int i = 0; i < array.length(); i++) {
						// Download markerinfo
						JSONObject o = array.getJSONObject(i);
						MarkerInfo m = new MarkerInfo();
						m.id = o.getInt("ID");
						m.title = o.getString("TITLE");
						m.address = o.getString("ADDRESS");
						m.desc = o.getString("DESC");
						m.lat = o.getDouble("LATITUDE");
						m.lng = o.getDouble("LONGITUDE");
						m.cat = o.getString("CATEGORY");

						// Save marker
						Marker marker = map.addMarker(new MarkerOptions().position(new LatLng(m.lat, m.lng)).title(m.title).icon(Utils.getMarkerIcon(m.cat)));
						markMap.put(marker, m);
					}
					Log.i(Utils.TAG, "MAP USING FRESHLY DOWNLOADED");
					lastUpdateTime = System.currentTimeMillis();
					
					// Save to SD
					saveInfoToFile(activity);
					saveImagesToFile(activity);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} else {
				String errMsg;
				if (markMap != null) {
					Log.i(Utils.TAG, "MAP (no connection)  USING CACHED VERSION");
					addMarkers(null, map);
					errMsg = Utils.errWithDate(Utils.ECODE_NO_INTERNET_CONNECTION, new Date(lastUpdateTime), true);
				} else {
					ArrayList<MarkerInfo> list = null;
					SharedPreferences prefs = activity.getSharedPreferences(Utils.PREFS_FILE, Context.MODE_PRIVATE);
					try {
						list = (ArrayList<MarkerInfo>) ObjectSerializer.deserialize(prefs.getString(Utils.PREFS_KEY_MAP, null));
						if(list != null) {
							addMarkers(list, map);
							initImgArray(activity);
							lastUpdateTime = prefs.getLong(Utils.PREFS_KEY_SCHEDULE_DATE, -1L);
						}
					} catch (IOException e) {
						Log.e(Utils.TAG, "MAP retrieve_from_file IOException");
						markMap = null;
						lastUpdateTime = -1L;
						e.printStackTrace();
					} catch (ClassCastException e) {
						Log.e(Utils.TAG, "MAP retrieve_from_file ClassCastException");
						markMap = null;
						lastUpdateTime = -1L;
						e.printStackTrace();
					}

					if (markMap != null) {
						Log.i(Utils.TAG, "MAP (no connection)  USING STORED VERSION");
						errMsg = Utils.errWithDate(Utils.ECODE_NO_INTERNET_CONNECTION, new Date(lastUpdateTime), true);
					} else {
						Log.i(Utils.TAG, "MAP (no connection) NO DATA TO SHOW");
						errMsg = Utils.EMSG_NO_INTERNET_CONNECTION;
					}
				}
				Utils.showToast(activity, errMsg, Toast.LENGTH_LONG);
			}
			showProgress.dismiss();
		}
	}
}
