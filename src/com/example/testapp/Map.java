package com.example.testapp;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class Map extends FragmentActivity {
	private final LatLng POS_LUND = new LatLng(55.704580, 13.190632);
	private GoogleMap map;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);


		this.map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
		map.setMyLocationEnabled(true);
		map.addMarker(new MarkerOptions().position(new LatLng(55.711011, 13.210523)).title("E-Huset").icon(BitmapDescriptorFactory.fromResource(R.drawable.rightarrow)));
		map.addMarker(new MarkerOptions().position(new LatLng(55.709597, 13.210566)).title("M-Huset"));
		map.addMarker(new MarkerOptions().position(new LatLng(55.712402, 13.209150)).title("Kårhuset"));

		//TEST AV INFO WINDOW
		map.setInfoWindowAdapter(new InfoWindowAdapter(){

			@Override
			public View getInfoContents(Marker arg0) {
				View v = getLayoutInflater().inflate(R.layout.map_info, null);
				
				ImageView im = (ImageView) v.findViewById(R.id.map_info_icon);
				TextView tv1 = (TextView) v.findViewById(R.id.map_info_title);
				TextView tv2 = (TextView) v.findViewById(R.id.map_info_address);
				TextView tv3 = (TextView) v.findViewById(R.id.map_info_desc);
				
				im.setImageResource(R.drawable.pink_square_big);
				tv1.setText("E-Huset");
				tv2.setText("von Scheelevägen 11");
				tv3.setText("Where all the true nerds go to party. Where all the true nerds go to party. Where all the true nerds go to party. ");
				
				return v;
			}

			@Override
			public View getInfoWindow(Marker arg0) {
				// TODO Auto-generated method stub
				return null;
			}});


		LocationManager locManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

		LocationListener locationListener = new LocationListener() {
			public void onLocationChanged(Location location) {
//				Gör nåt vettigt med koord
//				targetMe(location);
			}

			public void onStatusChanged(String provider, int status, Bundle extras) {
			}

			public void onProviderEnabled(String provider) {
			}

			public void onProviderDisabled(String provider) {
			}
		};

		locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);

		CameraPosition camPos = CameraPosition.builder().target(POS_LUND).zoom(13).build();
		map.moveCamera(CameraUpdateFactory.newCameraPosition(camPos));

//		Location myLoc = locManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//		targetMe(POS_LUND);

//		if (myLoc != null && myLoc.distanceTo(POS_LUND) > 10000) {
//			targetMe(myLoc);
//		} else if (myLoc == null){
//			targetMe(POS_LUND);
//		}
	}
	
	private void targetMe(Location myLoc) {
		LatLng latLng = new LatLng(myLoc.getLatitude(), myLoc.getLongitude());
		CameraPosition camPos = new CameraPosition.Builder().target(latLng).zoom(13).build();
		map.animateCamera(CameraUpdateFactory.newCameraPosition(camPos));
	}
}
