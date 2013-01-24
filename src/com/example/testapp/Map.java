package com.example.testapp;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

public class Map extends FragmentActivity {
	private final LatLng POS_LUND = new LatLng(55.711350, 13.190117);

	private final long									validTime 		= 900000L; // 15 minuter
	private GoogleMap 									map 			= null;

	private ProgressDialog showProgress;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);

		showProgress = ProgressDialog.show(Map.this, "", Utils.MSG_LOADING_MAP);
		this.map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
		map.setMyLocationEnabled(true);

		long timeDiff = System.currentTimeMillis() - Utils.lastUpdateTime;
		if (Utils.imgArray != null && (Utils.markMap != null || Utils.markList != null) && timeDiff < validTime)
			Utils.initFromCache(map, showProgress);
		else
			Utils.initFromDB(getApplicationContext(), showProgress, map);

		map.setInfoWindowAdapter(new InfoWindowAdapter() {
			@Override
			public View getInfoContents(Marker marker) {
				View v = getLayoutInflater().inflate(R.layout.map_info, null);

				MarkerInfo info = Utils.markMap.get(marker);
				Bitmap icon = Utils.imgArray.get(info.id);

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
}
