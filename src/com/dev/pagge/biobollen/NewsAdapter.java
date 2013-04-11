package com.dev.pagge.biobollen;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dev.pagge.biobollen.newsitem.NewsItem;
import com.dev.pagge.biobollen.newsitem.NewsPost;

public class NewsAdapter extends BaseExpandableListAdapter {

	private Activity activity;
	private LayoutInflater inflater;
	private ArrayList<String> titleList;
	private HashMap<String, NewsPost> map;
	
	public NewsAdapter(Activity a, ArrayList<NewsItem> d) {
		this.activity = a;
		this.inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		this.titleList = new ArrayList<String>();
		this.map = new HashMap<String, NewsPost>();
		
		for (NewsItem item : d) {
			if(item.getType() == NewsItem.TYPE_NEWS_POST) {
				NewsPost post = (NewsPost) item;
				String title = post.getTitle();
				titleList.add(title);
				map.put(title, post);
			}
		}
		
		Collections.sort(titleList);
	}

//	@Override
//	public int getCount() {
//		return data.size();
//	}
//
//	@Override
//	public Object getItem(int position) {
//		return data.get(position);
//	}
//
//	@Override
//	public long getItemId(int position) {
//		return position;
//	}
//	
//	@Override
//	public View getView(int position, View convertView, ViewGroup parent) {
//		View vi = convertView;
//		
//		NewsItem item = data.get(position);
//		
//		if(item != null) {
//			
//			if(item.getType() == NewsItem.TYPE_NEWS_POST) {
//				vi = inflater.inflate(R.layout.row_news_post, null);
//				
//				NewsPost post = (NewsPost) item;
//				
//				TextView title = (TextView) vi.findViewById(R.id.news_post_title);
//				TextView text = (TextView) vi.findViewById(R.id.news_post_details);
//				TextView date = (TextView) vi.findViewById(R.id.news_post_date);
//				
//				title.setText(post.getTitle().toUpperCase());
//				date.setText(Utils.formatDate(post.getDate()));
//				text.setText(Html.fromHtml(post.getText()));
//				
//			} else {
//				vi = inflater.inflate(R.layout.row_news_sep, null);
//			}
//		
//		}
//		return vi;
//	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
		View v;

		if(convertView == null)
			v = inflater.inflate(R.layout.row_news_title, null);
		else
			v = convertView;
		
		TextView tv = (TextView) v.findViewById(R.id.news_title);
		tv.setText(titleList.get(groupPosition));
		
		if(isExpanded) {
			ImageView im = (ImageView) v.findViewById(R.id.news_title_arrow);
			im.setImageResource(R.drawable.down_arrow_white);
		} else {
			ImageView im = (ImageView) v.findViewById(R.id.news_title_arrow);
			im.setImageResource(R.drawable.right_arrow_white);
		}
		
		return v;
	}
	
	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
		View v;
		
		if(convertView == null)
			v = inflater.inflate(R.layout.row_news_post, null);
		else
			v = convertView;
		
		NewsPost post = map.get(titleList.get(groupPosition));
		
//		TextView tvDate = (TextView) v.findViewById(R.id.news_post_date);
		TextView tvPost = (TextView) v.findViewById(R.id.news_post_details);
		
//		tvDate.setText(Utils.formatDate(post.getDate()));
		tvPost.setText(Html.fromHtml(post.getText()));
		
		return v;
	}
	
	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return map.get(titleList.get(groupPosition));
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return 1;
	}

	@Override
	public Object getGroup(int groupPosition) {
		return titleList.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return titleList.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}
}
