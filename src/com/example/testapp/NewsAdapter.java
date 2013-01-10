package com.example.testapp;

import java.util.ArrayList;
import java.util.HashMap;

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
	private ArrayList<Post> data;
	private LayoutInflater inflater;
	protected ViewHolder holder;
	private HashMap<String, String> dayMap;
	private HashMap<String, String> monthMap;
	
	public NewsAdapter(Activity a, ArrayList<Post> d) {
		this.dayMap = new HashMap<String, String>();
		this.monthMap = new HashMap<String, String>();
		
		initMaps();
		
		this.activity = a;
		this.data = d;
		this.inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	private void initMaps() {
		dayMap.put("Mon,", "Mån,");
		dayMap.put("Tue,", "Tis,");
		dayMap.put("Wed,", "Ons,");
		dayMap.put("Thu,", "Tor,");
		dayMap.put("Fri,", "Fre,");
		dayMap.put("Sat,", "Lör,");
		dayMap.put("Sun,", "Sön,");
		
		monthMap.put("Jan", "Jan");
		monthMap.put("Feb", "Feb");
		monthMap.put("Mar", "Mar");
		monthMap.put("Apr", "Apr");
		monthMap.put("May", "Maj");
		monthMap.put("Jun", "Jun");
		monthMap.put("Jul", "Jul");
		monthMap.put("Aug", "Aug");
		monthMap.put("Sep", "Sep");
		monthMap.put("Oct", "Okt");
		monthMap.put("Nov", "Nov");
		monthMap.put("Dec", "Dec");
		
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
		Log.i(Utils.TAG, "NEWS " + position);
		if(convertView == null) {
			vi = inflater.inflate(R.layout.row_news, null);
			holder = new ViewHolder();
			holder.title = (TextView) vi.findViewById(R.id.title);
			holder.desc = (TextView) vi.findViewById(R.id.details);
			holder.pubDate = (TextView) vi.findViewById(R.id.date);
			vi.setTag(holder);
		} else 
			holder = (ViewHolder) vi.getTag();
		
		String sweDate = translateDate(data.get(position).getPubDate());

		holder.title.setText(data.get(position).getTitle().toUpperCase());
		holder.pubDate.setText(sweDate.toLowerCase());
		holder.desc.setText(Html.fromHtml(data.get(position).getDesc()).toString().trim());
		
		return vi;
	}

	private String translateDate(String pubDate) {
		String[] split = pubDate.split(" ");
		
		split[0] = dayMap.get(split[0]);
		split[2] = monthMap.get(split[2]);
		
		return split[0] + " " + split[1] + " " + split[2] + " " + split[3];
	}

	class ViewHolder {
		public TextView title;
		public TextView pubDate;
		public TextView desc;
	}
}
