package com.example.testapp.placeitem;

import java.io.Serializable;

public class PlaceCategory extends PlaceItem implements Serializable {
	private static final long serialVersionUID = -5229983019516181154L;

	public String category;
	
	public PlaceCategory() {
		super(PlaceItem.TYPE_PLACE_CAT);
	}

}
