package com.example.testapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
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

import android.app.ProgressDialog;
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
	
	private static ArrayList<MarkerInfo> markers = null;
	private static HashMap<String, BitmapDescriptor> iconMap = null;
	private static HashMap<Marker, MarkerInfo> markMap = null;
	private static SparseArray<Bitmap> imgArray = null;
	private ProgressDialog showProgress;
	
	private GoogleMap map = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);

		
		showProgress = ProgressDialog.show(Map.this, "", Utils.MSG_LOADING_MAP);
		this.map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
		map.setMyLocationEnabled(true);
		
		if(imgArray == null) 
			imgArray = new SparseArray<Bitmap>();
		
		if(markMap == null) 
			markMap = new HashMap<Marker, MarkerInfo>();
		
		if(iconMap == null) 
			initMarkerIcons();
		
		if(markers == null) 
			new DBTask().execute("");
		else {
			addMarkers();
			showProgress.dismiss();
		}
		
		map.setInfoWindowAdapter(new InfoWindowAdapter(){
			@Override
			public View getInfoContents(Marker marker) {
				View v = getLayoutInflater().inflate(R.layout.map_info, null);
				
				MarkerInfo info = markMap.get(marker);
				Bitmap icon = null;
				
				Log.w(Utils.TAG, "MAP info == " + (info == null ? "null" : "something"));
				Log.w(Utils.TAG, "MAP imgArray == " + (imgArray == null ? "null" : "something"));
				
				if(imgArray.get(info.id) != null)
					icon = imgArray.get(info.id);
				else {
					InputStream is;
					try {
						is = new URL(Utils.DB_IMAGE_URL + info.id + ".png").openStream();
						icon = BitmapFactory.decodeStream(is);
						if(icon != null)
							imgArray.put(info.id, icon);
					} catch (MalformedURLException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
					
				}
				
				ImageView im = (ImageView) v.findViewById(R.id.map_info_icon);
				TextView tv1 = (TextView) v.findViewById(R.id.map_info_title);
				TextView tv2 = (TextView) v.findViewById(R.id.map_info_address);
				TextView tv3 = (TextView) v.findViewById(R.id.map_info_desc);
				
				if(icon != null)
					im.setImageBitmap(icon);
				tv1.setText(info.title);
				tv2.setText(info.address);
				tv3.setText(info.desc);
				
				return v;
			}

			@Override
			public View getInfoWindow(Marker arg0) {
				return null;
			}});
		CameraPosition camPos = CameraPosition.builder().target(POS_LUND).zoom(13).build();
		map.moveCamera(CameraUpdateFactory.newCameraPosition(camPos));
	}
	
	private void addMarkers() {
		HashMap<Marker, MarkerInfo> newMap = new HashMap<Marker, MarkerInfo>();
		for (MarkerInfo m : markMap.values()) {
			Marker marker = map.addMarker(new MarkerOptions().position(new LatLng(m.lat, m.lng)).title(m.title).icon(iconMap.get(m.cat)));
			newMap.put(marker, m);
		}
		markMap = newMap;
	}

	private void initMarkerIcons() {
		iconMap = new HashMap<String, BitmapDescriptor>();
		iconMap.put("HOSPITAL", BitmapDescriptorFactory.fromResource(R.drawable.marker_hospital));
		iconMap.put("MED", BitmapDescriptorFactory.fromResource(R.drawable.marker_med));
		iconMap.put("TRAIN", BitmapDescriptorFactory.fromResource(R.drawable.marker_train));
		iconMap.put("STORE", BitmapDescriptorFactory.fromResource(R.drawable.marker_store));
		iconMap.put("FASTFOOD", BitmapDescriptorFactory.fromResource(R.drawable.marker_fastfood));
	}
	
	class DBTask extends AsyncTask<String, Void, String> {

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
				if(statusCode == 200) {
					HttpEntity entity = response.getEntity();
					InputStream content = entity.getContent();
					BufferedReader br = new BufferedReader(new InputStreamReader(content));
					String line;
					while((line = br.readLine()) != null) {
						builder.append(line);
					}
					br.close();
					content.close();
				} else {
					Log.e(Utils.TAG, "MAP Failed to download JSON file");
				}
			} catch (ClientProtocolException e) {
				Log.e(Utils.TAG, "MAP connectToDB ClientProtocolException");
				e.printStackTrace();
			} catch (IOException e) {
				Log.e(Utils.TAG, "MAP connectToDB IOException");
				e.printStackTrace();
			}
			
			return builder.toString();
		}
		
		@Override
		protected void onPostExecute(final String success) {
			try {
				JSONArray array = new JSONArray(success);
				markers = new ArrayList<MarkerInfo>();
				for (int i = 0; i < array.length(); i++) {
					JSONObject o = array.getJSONObject(i);
					MarkerInfo m = new MarkerInfo();
					m.id		= o.getInt("ID");
					m.title		= o.getString("TITLE");
					m.address	= o.getString("ADDRESS");
					m.desc		= o.getString("DESC");
					m.lat		= o.getDouble("LATITUDE");
					m.lng		= o.getDouble("LONGITUDE");
					m.cat		= o.getString("CATEGORY");
					markers.add(m);
					Marker marker = map.addMarker(new MarkerOptions().position(new LatLng(m.lat, m.lng)).title(m.title).icon(iconMap.get(m.cat)));
					markMap.put(marker, m);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			showProgress.dismiss();
		}
		
	}
	
	class MarkerInfo {
		int id;
		String title;
		String address;
		String desc;
		double lat; 
		double lng;
		String cat;
	}
}
