package com.example.testapp;

public class CalDesc extends ScheduleItem {
	private String time;
	private String desc;
	private String place;
	
	public CalDesc(int type, String time, String desc, String place) {
		super(type);
		this.time = time;
		this.desc = desc;
		this.place = place;
	}
	
	public String getTime() {
		return time;
	}

	public String getDesc() {
		return desc;
	}

	public String getPlace() {
		return place;
	}
}