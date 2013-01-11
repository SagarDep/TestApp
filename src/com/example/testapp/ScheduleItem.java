package com.example.testapp;

public abstract class ScheduleItem {
	public static final int TYPE_CALDATE = 0;
	public static final int TYPE_CALDESC = 1;
	public static final int TYPE_CALSEP  = 2;
	
	private int type;
	
	public ScheduleItem(int type) {
		this.type = type;
	}
	
	public int getType() {
		return type;
	}
}
