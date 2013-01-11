package com.example.testapp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;

import com.example.testapp.NewsAdapter.ViewHolder;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class CalAdapter extends ArrayAdapter<ScheduleItem> {
	
	private Activity activity;
	private ArrayList<ScheduleItem> data;
	private LayoutInflater inflater;
 	
	public CalAdapter(Activity a, ArrayList<ScheduleItem> d) {
		super(a, 0, d);
		this.activity = a;
		this.data = d;
		this.inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public ScheduleItem getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View vi = convertView;
		
		ScheduleItem item = data.get(position);
		if(item != null) {
			if(item.getType() == ScheduleItem.TYPE_CALDATE) {
				vi = inflater.inflate(R.layout.row_cal_date, null);

				CalDate calDate = (CalDate) item;
				
				TextView title = (TextView) vi.findViewById(R.id.date_title);
				TextView date = (TextView) vi.findViewById(R.id.date_date);
				
				title.setText(calDate.getTitle().toUpperCase());
				date.setText(calDate.getDate().toLowerCase());
				
			} else if(item.getType() == ScheduleItem.TYPE_CALDESC) {
				vi = inflater.inflate(R.layout.row_cal_desc, null);
				CalDesc calDesc = (CalDesc) item;
				
				TextView time = (TextView) vi.findViewById(R.id.desc_time);
				TextView desc = (TextView) vi.findViewById(R.id.desc_desc);
				TextView place = (TextView) vi.findViewById(R.id.desc_place);
				
				time.setText(calDesc.getTime());
				desc.setText(calDesc.getDesc());
				place.setText(calDesc.getPlace());
			} else {
				vi = inflater.inflate(R.layout.row_cal_sep, null);
			}
		}
		return vi;
	}
}
