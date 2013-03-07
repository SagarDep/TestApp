package com.example.testapp.newsitem;

public class NewsPost extends NewsItem {
	private static final long serialVersionUID = 7037835271931804852L;

	private String title;
	private String text;
	private String date;
	
	public NewsPost(String title, String text, String date) {
		super(TYPE_NEWS_POST);
		this.title = title;
		this.text = text;
		this.date = date;
	}
	
	public String getTitle() {
		return title;
	}
	
	public String getText() {
		return text;
	}
	
	public String getDate() {
		return date;
	}
}
