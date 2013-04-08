package com.dev.pagge.biobollen.scheduleitem;

public class CalDesc extends ScheduleItem {
	private static final long serialVersionUID = -559586710111000941L;
	private String time;
	private String title;
	private String place;
	
	public CalDesc(String time, String title, String place) {
		super(TYPE_CALDESC);
		this.time = time;
		this.title = title;
		this.place = place;
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
}
