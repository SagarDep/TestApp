package com.example.testapp.newsitem;

import java.io.Serializable;

public class NewsItem implements Serializable{
	private static final long serialVersionUID = 7364282351913283971L;
	
	public static int TYPE_NEWS_POST	= 0;
	public static int TYPE_NEWS_SEP		= 1;
	
	private int type;
	
	public NewsItem(int type) {
		this.type = type;
	}
	
	public int getType() {
		return type;
	}
}
