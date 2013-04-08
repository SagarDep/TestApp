package com.dev.pagge.biobollen.placeitem;

import java.io.Serializable;

public class PlaceCategory extends PlaceItem implements Serializable {
	private static final long serialVersionUID = -5229983019516181154L;

	public String category;
	public String img;
	
	public PlaceCategory() {
		super(PlaceItem.TYPE_PLACE_CAT);
	}

}
