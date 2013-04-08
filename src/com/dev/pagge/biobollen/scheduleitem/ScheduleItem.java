package com.dev.pagge.biobollen.scheduleitem;

import java.io.Serializable;

public abstract class ScheduleItem implements Serializable {
	private static final long serialVersionUID = 4534668534601715346L;
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
