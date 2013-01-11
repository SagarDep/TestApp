package com.example.testapp;

public abstract class ScheduleItem {
	private static final int TYPE_CALDATE = 0;
	private static final int TYPE_CALDESC = 1;
	
	private int type;
	
	public ScheduleItem(int type) {
		this.type = type;
	}
	
	public int getType() {
		return type;
	}
}
