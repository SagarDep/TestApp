package com.example.testapp.scheduleitem;

public class CalDate extends ScheduleItem {
	private static final long serialVersionUID = 1387881017667082813L;
	private String day;
	private String date;
	
	public CalDate(int type, String day, String date) {
		super(type);
		this.day = day;
		this.date = date;
	}

	public String getDay() {
		return day;
	}

	public String getDate() {
		return date;
	}
}
