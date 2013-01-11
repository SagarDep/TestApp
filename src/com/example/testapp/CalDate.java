package com.example.testapp;

public class CalDate extends ScheduleItem {
	private String day;
	private String date;
	
	public CalDate(int type, String day, String date) {
		super(type);
		this.day = day;
		this.date = date;
	}

	public String getTitle() {
		return day;
	}

	public String getDate() {
		return date;
	}
}
