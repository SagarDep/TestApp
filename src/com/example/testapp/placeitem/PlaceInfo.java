package com.example.testapp.placeitem;

public class PlaceInfo extends PlaceItem {
	private String title;
	private String addr;
	
	public PlaceInfo(int type) {
		super(type);
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String name) {
		this.title = name;
	}

	public String getAddr() {
		return addr;
	}

	public void setAddr(String addr) {
		this.addr = addr;
	}
}
