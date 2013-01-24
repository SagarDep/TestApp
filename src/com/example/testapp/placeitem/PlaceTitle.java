package com.example.testapp.placeitem;

import android.widget.TextView;

public class PlaceTitle extends PlaceItem {
	private String title;
	private String date;

	public PlaceTitle(int type) {
		super(type);
	}
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}
}
