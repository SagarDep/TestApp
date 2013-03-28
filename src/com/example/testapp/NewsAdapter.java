package com.example.testapp;

import java.util.ArrayList;

import com.example.testapp.newsitem.NewsItem;
import com.example.testapp.newsitem.NewsPost;


import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.util.Log;
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
		Log.v(Utils.TAG, "data size = " + data.size());
		
//		int mainColor = -1;
//		int lightColor = -1;
//		
//		switch(position % 5) {
//		case 0:
//			mainColor = activity.getResources().getColor(R.color.main_red);
//			lightColor = activity.getResources().getColor(R.color.main_light_red);
//			break;
//		case 1:
//			mainColor = activity.getResources().getColor(R.color.main_green);
//			lightColor = activity.getResources().getColor(R.color.main_light_green);
//			break;
//		case 2:
//			mainColor = activity.getResources().getColor(R.color.main_yellow);
//			lightColor = activity.getResources().getColor(R.color.main_light_yellow);
//			break;
//		case 3:
//			mainColor = activity.getResources().getColor(R.color.main_purple);
//			lightColor = activity.getResources().getColor(R.color.main_light_purple);
//			break;
//		case 4:
//			mainColor = activity.getResources().getColor(R.color.main_blue);
//			lightColor = activity.getResources().getColor(R.color.main_light_blue);
//			break;
//		}
		
		if(item != null) {
			
			if(item.getType() == NewsItem.TYPE_NEWS_POST) {
				vi = inflater.inflate(R.layout.row_news_post, null);
				
				NewsPost post = (NewsPost) item;
				
				TextView title = (TextView) vi.findViewById(R.id.news_post_title);
				TextView text = (TextView) vi.findViewById(R.id.news_post_details);
				TextView date = (TextView) vi.findViewById(R.id.news_post_date);
				
				title.setText(post.getTitle().toUpperCase());
				date.setText(Utils.formatDate(post.getDate()));
//				date.setBackgroundColor(mainColor);
				text.setText(Html.fromHtml(post.getText()));
//				text.setBackgroundColor(lightColor);
				
			} else {
				vi = inflater.inflate(R.layout.row_news_sep, null);
			}
		
		}
		return vi;
	}
}
