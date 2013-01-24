package com.example.testapp.scheduleitem;

import java.io.Serializable;

public abstract class ScheduleItem implements Comparable<ScheduleItem>, Serializable {
	private static final long serialVersionUID = 4534668534601715346L;
	public static final int TYPE_CALDATE = 0;
	public static final int TYPE_CALDESC = 1;
	public static final int TYPE_CALSEP  = 2;
	
	private int type;
	private int id;
	
	public ScheduleItem(int type, int id) {
		this.type = type;
		this.id = id;
	}
	
	public int getType() {
		return type;
	}
	
	@Override
	public int compareTo(ScheduleItem b) {
		return Integer.valueOf(id).compareTo(b.id);
	}
}
