package com.example.testapp;

import java.util.ArrayList;

import newsitem.NewsItem;
import newsitem.NewsPost;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class NewsAdapter extends BaseAdapter {

	private Activity activity;
	private ArrayList<NewsItem> data;
	private LayoutInflater inflater;
	
	public NewsAdapter(Activity a, ArrayList<NewsItem> d) {
		this.activity = a;
		this.data = d;
		this.inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View vi = convertView;
		
		NewsItem item = data.get(position);
		
		if(item != null) {
			
			if(item.getType() == NewsItem.TYPE_NEWS_POST) {
				vi = inflater.inflate(R.layout.row_news_post, null);
				
				NewsPost post = (NewsPost) item;
				
				TextView title = (TextView) vi.findViewById(R.id.title);
				TextView text = (TextView) vi.findViewById(R.id.details);
				TextView date = (TextView) vi.findViewById(R.id.date);
				
				title.setText(post.getTitle().toUpperCase());
				text.setText(post.getText());
				date.setText(Html.fromHtml(post.getDate()).toString().trim());
				
			} else {
				vi = inflater.inflate(R.layout.row_news_sep, null);
			}
		
		}
		return vi;
	}
}
