package com.dev.pagge.biobollen.scheduleitem;

public class CalDate extends ScheduleItem {
	private static final long serialVersionUID = 1387881017667082813L;
	private String day;
	private String date;
	
	public CalDate(String day, String date) {
		super(TYPE_CALDATE);
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
