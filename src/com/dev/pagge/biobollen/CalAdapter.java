package com.dev.pagge.biobollen;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.dev.pagge.biobollen.scheduleitem.CalDate;
import com.dev.pagge.biobollen.scheduleitem.CalDesc;
import com.dev.pagge.biobollen.scheduleitem.ScheduleItem;

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
				
				title.setText(calDate.getDay().toUpperCase());
				date.setText(calDate.getDate());
				
			} else if(item.getType() == ScheduleItem.TYPE_CALDESC) {
				vi = inflater.inflate(R.layout.row_cal_desc, null);
				CalDesc calDesc = (CalDesc) item;
				
				TextView time = (TextView) vi.findViewById(R.id.desc_time);
				TextView desc = (TextView) vi.findViewById(R.id.desc_desc);
				TextView place = (TextView) vi.findViewById(R.id.desc_place);
				
				time.setText(calDesc.getTime());
				desc.setText(" " + calDesc.getDesc());
				if(calDesc.getPlace().equals(""))
					place.setText("");
				else
					place.setText("Plats: " + calDesc.getPlace());
//				vi.setOnClickListener(new OnClickListener(){
//					
//					@Override
//					public void onClick(View vi) {
//						TextView tv = (TextView) vi.findViewById(R.id.desc_time);
//						Utils.showToast(activity, "Test " + tv.getText(), Toast.LENGTH_SHORT);
//					}
//					
//				});
			} else {
				vi = inflater.inflate(R.layout.row_cal_sep, null);
			}
		}
		return vi;
	}
}
