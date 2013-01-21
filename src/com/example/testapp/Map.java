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

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class Map extends FragmentActivity {
	private final LatLng POS_LUND = new LatLng(55.711350, 13.190117);

	private static HashMap<String, BitmapDescriptor> iconMap = null;
	private static SparseArray<Bitmap> imgArray = null;

	private static HashMap<Marker, MarkerInfo> markMap = null;
	private static Long lastUpdateTime = -1L;
	
	private final long validTime = 20000L; // 5 minuter

	private ProgressDialog showProgress;

	private GoogleMap map = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);

		showProgress = ProgressDialog.show(Map.this, "", Utils.MSG_LOADING_MAP);
		this.map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
		map.setMyLocationEnabled(true);

		if (imgArray == null) // Finns i minnet
			imgArray = new SparseArray<Bitmap>();

		if (iconMap == null)
			initMarkerIcons();

		long timeDiff = System.currentTimeMillis() - lastUpdateTime;

		if (markMap != null && timeDiff < validTime) { // Finns i minnet, lägg till if(updateTime....)
			addMarkers(null);
			showProgress.dismiss();
		} else
			new DBTask(this).execute("");

		map.setInfoWindowAdapter(new InfoWindowAdapter() {
			@Override
			public View getInfoContents(Marker marker) {
				View v = getLayoutInflater().inflate(R.layout.map_info, null);

				MarkerInfo info = markMap.get(marker);
				Bitmap icon = imgArray.get(info.id);

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
		
		int zoom = Integer.parseInt(this.getString(R.string.map_default_zoom));
		CameraPosition camPos = CameraPosition.builder().target(POS_LUND).zoom(zoom).build();
		map.moveCamera(CameraUpdateFactory.newCameraPosition(camPos));
	}

	private void initImgArray() {
		imgArray = new SparseArray<Bitmap>();
		Bitmap icon = null;
		for (int i = 1; i <= markMap.size(); i++) {
			try {
				FileInputStream fi = this.openFileInput(i + ".png");
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

	private void addMarkers(ArrayList<MarkerInfo> list) {
		if(list == null) {
			HashMap<Marker, MarkerInfo> newMap = new HashMap<Marker, MarkerInfo>();
			for (MarkerInfo m : markMap.values()) {
				Marker marker = map.addMarker(new MarkerOptions().position(new LatLng(m.lat, m.lng)).title(m.title).icon(iconMap.get(m.cat)));
				newMap.put(marker, m);
			}
			markMap = newMap;
		} else {
			HashMap<Marker, MarkerInfo> newMap = new HashMap<Marker, MarkerInfo>();
			for (MarkerInfo m : list) {
				Marker marker = map.addMarker(new MarkerOptions().position(new LatLng(m.lat, m.lng)).title(m.title).icon(iconMap.get(m.cat)));
				newMap.put(marker, m);
			}
			markMap = newMap;
		}
	}

	private void initMarkerIcons() {
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

	class DBTask extends AsyncTask<String, Void, String> {
		private JSONArray array;
		private boolean connectionOK = true;
		private Activity activity;

		public DBTask(Activity a) {
			this.activity = a;
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
			} catch (IOException e) { // Ingen kontakt med DB
				Log.e(Utils.TAG, "MAP no connection to DB");
				connectionOK = false;
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}

			if (connectionOK) {
				// Download images
				for (int i = 1; i <= array.length(); i++) {
					Bitmap icon = null;
					InputStream is;
					try {
						is = new URL(Utils.DB_IMAGE_URL + i + ".png").openStream();
						icon = BitmapFactory.decodeStream(is);
						if (icon != null) {
							imgArray.put(i, icon);
							saveImageToFile("" + i, icon);
						}
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
						Marker marker = map.addMarker(new MarkerOptions().position(new LatLng(m.lat, m.lng)).title(m.title).icon(iconMap.get(m.cat)));
						markMap.put(marker, m);
					}
					Log.i(Utils.TAG, "MAP USING FRESHLY DOWNLOADED");
					lastUpdateTime = System.currentTimeMillis();
					saveInfoToFile();
				} catch (JSONException e) {
					e.printStackTrace();
				}
				showProgress.dismiss();
			} else {
				if (markMap != null) {
					Log.i(Utils.TAG, "MAP (no connection)  USING CACHED VERSION");
					addMarkers(null);
					showProgress.dismiss();
					String errMsg = Utils.errWithDate(Utils.ECODE_NO_INTERNET_CONNECTION, new Date(lastUpdateTime), true);
					Utils.showToast(activity, errMsg, Toast.LENGTH_LONG);
				} else {
					ArrayList<MarkerInfo> list = null;
					SharedPreferences prefs = getSharedPreferences(Utils.PREFS_FILE, Context.MODE_PRIVATE);
					try {
						list = (ArrayList<MarkerInfo>) ObjectSerializer.deserialize(prefs.getString(Utils.PREFS_KEY_MAP, null));
						addMarkers(list);
						initImgArray();
						lastUpdateTime = prefs.getLong(Utils.PREFS_KEY_SCHEDULE_DATE, -1L);
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
						showProgress.dismiss();
						String errMsg = Utils.errWithDate(Utils.ECODE_NO_INTERNET_CONNECTION, new Date(lastUpdateTime), true);
						Utils.showToast(activity, errMsg, Toast.LENGTH_LONG);
					} else {
						Log.i(Utils.TAG, "MAP (no connection) NO DATA TO SHOW");
						showProgress.dismiss();
						Utils.showToast(activity, Utils.EMSG_NO_INTERNET_CONNECTION, Toast.LENGTH_LONG);
					}
				}
			}
		}
	}

	private void saveInfoToFile() {
		ArrayList<MarkerInfo> list = new ArrayList<MarkerInfo>(markMap.values());
		SharedPreferences prefs = getSharedPreferences(Utils.PREFS_FILE, Context.MODE_PRIVATE);
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

	private void saveImageToFile(String fileName, Bitmap image) {
		try {
			FileOutputStream out = this.openFileOutput(fileName + ".png", Context.MODE_PRIVATE);
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
