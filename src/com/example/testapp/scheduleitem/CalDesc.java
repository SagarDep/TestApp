package com.example.testapp.scheduleitem;

public class CalDesc extends ScheduleItem {
	private static final long serialVersionUID = -559586710111000941L;
	private String time;
	private String title;
	private String place;
	
	public CalDesc(int type, int id, String time, String title, String place) {
		super(type, id);
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
