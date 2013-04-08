package com.dev.pagge.biobollen;

import java.util.ArrayList;

import com.dev.pagge.biobollen.newsitem.NewsItem;
import com.dev.pagge.biobollen.newsitem.NewsPost;
import com.example.testapp.R;


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
		
		if(item != null) {
			
			if(item.getType() == NewsItem.TYPE_NEWS_POST) {
				vi = inflater.inflate(R.layout.row_news_post, null);
				
				NewsPost post = (NewsPost) item;
				
				TextView title = (TextView) vi.findViewById(R.id.news_post_title);
				TextView text = (TextView) vi.findViewById(R.id.news_post_details);
				TextView date = (TextView) vi.findViewById(R.id.news_post_date);
				
				title.setText(post.getTitle().toUpperCase());
				date.setText(Utils.formatDate(post.getDate()));
				text.setText(Html.fromHtml(post.getText()));
				
			} else {
				vi = inflater.inflate(R.layout.row_news_sep, null);
			}
		
		}
		return vi;
	}
}
