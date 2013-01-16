package com.example.testapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
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
	
	private static HashMap<String, BitmapDescriptor> markMap = null;
	private static ArrayList<CustomMarker> markers = null;
	
	private GoogleMap map = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);

		this.map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
		map.setMyLocationEnabled(true);

		if(markMap == null)
			initMarkerIcons();
		
		if(markers == null) {
			String content = connectToDB();
			markers = getMarkers(content);
		}
		addMarkers();
		
		map.setInfoWindowAdapter(new InfoWindowAdapter(){
			@Override
			public View getInfoContents(Marker marker) {
				View v = getLayoutInflater().inflate(R.layout.map_info, null);
				
				ImageView im = (ImageView) v.findViewById(R.id.map_info_icon);
				TextView tv1 = (TextView) v.findViewById(R.id.map_info_title);
				TextView tv2 = (TextView) v.findViewById(R.id.map_info_address);
				TextView tv3 = (TextView) v.findViewById(R.id.map_info_desc);
				
				im.setImageResource(R.drawable.pink_square_big);
				tv1.setText(marker.getTitle());
				tv2.setText("von Scheelevägen 11");
				tv3.setText("Where all the true nerds go to party. Where all the true nerds go to party. Where all the true nerds go to party. ");
				
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
		if(markers != null) {
			for (CustomMarker m : markers) {
				map.addMarker(new MarkerOptions().position(new LatLng(m.lat, m.lng)).title(m.title).icon(markMap.get(m.cat)));
			}
		}
	}

	private ArrayList<CustomMarker> getMarkers(String content) {
		
		try {
			JSONArray array = new JSONArray(content);
			markers = new ArrayList<Map.CustomMarker>();
			for (int i = 0; i < array.length(); i++) {
				JSONObject o = array.getJSONObject(i);
				CustomMarker m = new CustomMarker();
				m.title		= o.getString("TITLE");
				m.address	= o.getString("ADDRESS");
				m.desc		= o.getString("DESC");
				m.lat		= o.getDouble("LATITUDE");
				m.lng		= o.getDouble("LONGITUDE");
				m.cat		= o.getString("CATEGORY");
				markers.add(m);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return markers;
	}

	private String connectToDB() {
		StringBuilder builder = new StringBuilder();
		HttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet("http://nutty.rymdraket.net/android/markers.php");
		
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
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return builder.toString();
	}

	private void initMarkerIcons() {
		markMap = new HashMap<String, BitmapDescriptor>();
		markMap.put("HOSPITAL", BitmapDescriptorFactory.fromResource(R.drawable.marker_hospital));
		markMap.put("MED", BitmapDescriptorFactory.fromResource(R.drawable.marker_med));
		markMap.put("TRAIN", BitmapDescriptorFactory.fromResource(R.drawable.marker_train));
		markMap.put("STORE", BitmapDescriptorFactory.fromResource(R.drawable.marker_store));
		markMap.put("FASTFOOD", BitmapDescriptorFactory.fromResource(R.drawable.marker_fastfood));
	}
	
	class CustomMarker {
		String title;
		String address;
		String desc;
		double lat; 
		double lng;
		String cat;
	}
}
