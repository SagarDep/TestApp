package com.example.testapp.placeitem;

public class PlaceInfo extends PlaceItem {
	private String name;
	private String addr;
	
	public PlaceInfo(int type) {
		super(type);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddr() {
		return addr;
	}

	public void setAddr(String addr) {
		this.addr = addr;
	}
}
