package com.example.testapp.scheduleitem;

public class CalDesc extends ScheduleItem {
	private static final long serialVersionUID = -559586710111000941L;
	private String time;
	private String desc;
	private String place;
	
	public CalDesc(int type, int id, String time, String desc, String place) {
		super(type, id);
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
