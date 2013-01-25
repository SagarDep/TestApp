package com.example.testapp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import com.example.testapp.placeitem.PlaceInfo;
import com.example.testapp.placeitem.PlaceItem;
import com.example.testapp.placeitem.PlaceCategory;
import com.example.testapp.placeitem.PlaceSep;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.maps.MapsInitializer;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

public class Places extends Activity {

	private final long validTime 				= 900000L; // 15 minuter
	
	private ProgressDialog	showProgress;
	private ListView newsList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_places);
		
		try {
			MapsInitializer.initialize(Places.this);
		} catch (GooglePlayServicesNotAvailableException e) {
			Log.e(Utils.TAG, "PLACES MapsInitializer Failed!");
			e.printStackTrace();
		}
		
		newsList = (ListView) findViewById(R.id.places_list);
		showProgress = ProgressDialog.show(Places.this, "", Utils.MSG_LOADING_PLACES);
		
		long timeDiff = System.currentTimeMillis() - Utils.lastUpdateTime;
		
		if (Utils.markList != null && timeDiff < validTime) {
			Log.i(Utils.TAG, "PLACES USING CACHED VERSION " + "timeDiff =" + timeDiff + " (" + ((timeDiff / 1000.0) / 60.0) + " min)");
			newsList.setAdapter(new PlaceAdapter(Places.this, Utils.placeList));
			showProgress.dismiss();
		} else {
			Utils.initFromDB(getApplicationContext(), showProgress, null);
			newsList.setAdapter(new PlaceAdapter(Places.this, Utils.placeList));
			showProgress.dismiss();
		}
		
		initPlaceList();
	}

	private void initPlaceList() {
		ArrayList<MarkerInfo> markers = Utils.markList;
		
		if(markers != null) {
			// Creating all info items and sorting them on category
			HashMap<String, ArrayList<PlaceInfo>> placeMap = new HashMap<String, ArrayList<PlaceInfo>>();
			for (MarkerInfo m : markers) {
				PlaceInfo placeInfo = new PlaceInfo(PlaceItem.TYPE_PLACE_INFO);
				placeInfo.setTitle(m.title);
				placeInfo.setAddr(m.address);

				if(placeMap.containsKey(m.cat)) {
					placeMap.get(m.cat).add(placeInfo);
				} else {
					ArrayList<PlaceInfo> array = new ArrayList<PlaceInfo>();
					array.add(placeInfo);
					placeMap.put(m.cat, array);
				}
			}
			
			// Sort the categories based on name
			String[] keys = placeMap.keySet().toArray(new String[placeMap.keySet().size()]);
			Arrays.sort(keys);
			
			
			Utils.placeList = new ArrayList<PlaceItem>();
			ArrayList<PlaceItem> list = Utils.placeList;
			
			for (String key : keys) {
				// Add category
				PlaceCategory placeCat = new PlaceCategory(PlaceItem.TYPE_PLACE_TITLE);
				placeCat.setTitle(key);
				placeCat.setIcon(Utils.getMarkerIcon(key));
				list.add(placeCat);
				
				// Add all items, sorted based on name
				PlaceInfo[] places = placeMap.get(key).toArray(new PlaceInfo[placeMap.get(key).size()]);
				Arrays.sort(places);
				for (PlaceInfo item : places) {
					list.add(item);
				}
				// Add a separator
				list.add(new PlaceSep(PlaceItem.TYPE_PLACE_SEP));
			}
		}
	}
	
	
	
	
	
}
