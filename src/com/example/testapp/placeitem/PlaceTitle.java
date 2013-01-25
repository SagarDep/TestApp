package com.example.testapp.placeitem;

import com.google.android.gms.maps.model.BitmapDescriptor;

public class PlaceTitle extends PlaceItem {
	private BitmapDescriptor icon;
	private String title;

	public PlaceTitle(int type) {
		super(type);
	}
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public BitmapDescriptor getIcon() {
		return icon;
	}

	public void setIcon(BitmapDescriptor icon) {
		this.icon = icon;
	}
}
