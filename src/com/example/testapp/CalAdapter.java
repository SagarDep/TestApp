package com.example.testapp;

import java.util.ArrayList;
import java.util.HashMap;

import com.example.testapp.NewsAdapter.ViewHolder;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class CalAdapter extends BaseAdapter {

	private Activity activity;
	private ArrayList<Post> data;
	private LayoutInflater inflater;
	protected ViewHolder holder;
	
	public CalAdapter(Activity a, ArrayList<Post> d) {
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
		Log.i(Utils.TAG, "NEWS " + position);
		if(convertView == null) {
			vi = inflater.inflate(R.layout.row_cal, null);
			holder = new ViewHolder();
			holder.title = (TextView) vi.findViewById(R.id.title);
			holder.desc = (TextView) vi.findViewById(R.id.details);
			vi.setTag(holder);
		} else 
			holder = (ViewHolder) vi.getTag();

		holder.title.setText(data.get(position).getTitle().toUpperCase());
		holder.desc.setText(data.get(position).getDesc().toString().trim());
		
		return vi;
	}

	class ViewHolder {
		public TextView title;
		public TextView desc;
	}
}
