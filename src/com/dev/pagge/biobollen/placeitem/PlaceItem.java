package com.dev.pagge.biobollen.placeitem;

import java.io.Serializable;

public abstract class PlaceItem implements Comparable<PlaceItem>, Serializable {
	private static final long serialVersionUID = 2829552673001681053L;

	public static final int TYPE_PLACE_CAT	= 0;
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
		if(type == TYPE_PLACE_CAT) {
			PlaceCategory me = (PlaceCategory) this;
			PlaceCategory other = (PlaceCategory) b;
			return me.category.compareTo(other.category);
		} else if (type == TYPE_PLACE_INFO){
			PlaceInfo me = (PlaceInfo) this;
			PlaceInfo other = (PlaceInfo) b;
			return me.title.compareTo(other.title);
		} else 
			return 0;
	}
}
