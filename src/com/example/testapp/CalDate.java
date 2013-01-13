package com.example.testapp;

public class CalDate extends ScheduleItem {
	private static final long serialVersionUID = 1387881017667082813L;
	private String day;
	private String date;
	
	public CalDate(int type, int id, String day, String date) {
		super(type, id);
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
