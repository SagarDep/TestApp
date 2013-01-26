package com.example.testapp.placeitem;

import android.graphics.Bitmap;

public class PlaceCategory extends PlaceItem {
	private Bitmap icon;
	private String category;

	public PlaceCategory(int type) {
		super(type);
	}
	
	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public Bitmap getIcon() {
		return icon;
	}

	public void setIcon(Bitmap icon) {
		this.icon = icon;
	}
}
