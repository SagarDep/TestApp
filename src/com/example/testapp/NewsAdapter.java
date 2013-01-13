package com.example.testapp;

import java.util.ArrayList;

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
	private ArrayList<Post> data;
	private LayoutInflater inflater;
	protected ViewHolder holder;
	
	public NewsAdapter(Activity a, ArrayList<Post> d) {
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
		if(convertView == null) {
			vi = inflater.inflate(R.layout.row_news, null);
			holder = new ViewHolder();
			holder.title = (TextView) vi.findViewById(R.id.title);
			holder.desc = (TextView) vi.findViewById(R.id.details);
			holder.pubDate = (TextView) vi.findViewById(R.id.date);
			vi.setTag(holder);
		} else 
			holder = (ViewHolder) vi.getTag();
		
		String sweDate = Utils.translateDate(data.get(position).getPubDate());

		holder.title.setText(data.get(position).getTitle().toUpperCase());
		holder.pubDate.setText(sweDate.toLowerCase());
		holder.desc.setText(Html.fromHtml(data.get(position).getDesc()).toString().trim());
		
		return vi;
	}

	class ViewHolder {
		public TextView title;
		public TextView pubDate;
		public TextView desc;
	}
}
