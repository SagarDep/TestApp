package com.example.testapp.placeitem;

public abstract class PlaceItem implements Comparable<PlaceItem> {
	public static final int TYPE_PLACE_TITLE	= 0;
	public static final int TYPE_PLACE_INFO	= 1;
	public static final int TYPE_PLACE_SEP	= 2;
	
	private int type;
	
	public PlaceItem(int type) {
		this.type = type;
	}
	
	public int getType() {
		return type;
	}
	
	@Override
	public int compareTo(PlaceItem b) {
		if(type == TYPE_PLACE_TITLE) {
			PlaceCategory me = (PlaceCategory) this;
			PlaceCategory other = (PlaceCategory) b;
			return me.getTitle().compareTo(other.getTitle());
		} else {
			PlaceInfo me = (PlaceInfo) this;
			PlaceInfo other = (PlaceInfo) b;
			return me.getTitle().compareTo(other.getTitle());
		}
	}
}
