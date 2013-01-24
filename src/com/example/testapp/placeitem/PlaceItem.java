package com.example.testapp.placeitem;

public abstract class PlaceItem {
	public static final int TYP_PLACE_TITLE	= 0;
	public static final int TYP_PLACE_INFO	= 1;
	public static final int TYP_PLACE_SEP	= 2;
	
	private int type;
	
	public PlaceItem(int type) {
		this.type = type;
	}
	
	public int getType() {
		return type;
	}
}
