package com.dev.pagge.biobollen.scheduleitem;

public class CalDesc extends ScheduleItem {
	private static final long serialVersionUID = -3007364046540028974L;
	private String time;
	private String title;
	private String place;
	private int placeId;
	private String day;
	
	public CalDesc(String time, String title, String place, int placeId, String day) {
		super(TYPE_CALDESC);
		this.time = time;
		this.title = title;
		this.place = place;
		this.placeId = placeId;
		this.day = day;
	}
	
	public String getTime() {
		return time;
	}

	public String getDesc() {
		return title;
	}

	public String getPlace() {
		return place;
	}
	
	public int getPlaceId() {
		return placeId;
	}

	public String getDay() {
		return day;
	}
}
