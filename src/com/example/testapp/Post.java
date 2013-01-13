package com.example.testapp;

import java.io.Serializable;

public class Post implements Comparable<Post>, Serializable {
	private static final long serialVersionUID = 5252540264609668096L;
	private int id;
	private String title;
	private String thumbnail;
	private String url;
	private String desc;
	private String pubDate;
	
	public Post(int id) {
		this.id = id;
	}
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getThumbnail() {
		return thumbnail;
	}
	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public String getPubDate() {
		return pubDate;
	}
	public void setPubDate(String pubDate) {
		this.pubDate = pubDate;
	}
	
	@Override
	public int compareTo(Post b) {
		return Integer.valueOf(id).compareTo(b.id);
	}
}

