package com.dev.pagge.biobollen.placeitem;

import java.io.Serializable;

public class PlaceInfo extends PlaceItem implements Serializable {
	private static final long serialVersionUID = 6614191594757779040L;

	public int id;
	public String title;
	public String address;
	public String desc;
	public double lat;
	public double lng;
	public String img;
	public String cat;
	
	public PlaceInfo() {
		super(PlaceItem.TYPE_PLACE_INFO);
	}
}
