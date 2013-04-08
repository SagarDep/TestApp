package com.example.testapp;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.testapp.placeitem.PlaceCategory;
import com.example.testapp.placeitem.PlaceInfo;
import com.example.testapp.placeitem.PlaceItem;

public class PlaceAdapter extends ArrayAdapter<PlaceItem> {

	private Activity activity;
	private ArrayList<PlaceItem> data;
	private LayoutInflater inflater;
	
	public PlaceAdapter(Activity a, ArrayList<PlaceItem> d) {
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
	public PlaceItem getItem(int position) {
		return data.get(position);
	}
	
	@Override
	public long getItemId(int position) {
		return position;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View vi = convertView;
		
		PlaceItem item = getItem(position);
		
		if (item != null) {
			if (item.getType() == PlaceItem.TYPE_PLACE_CAT) {
				vi = inflater.inflate(R.layout.row_places_title, null);
				PlaceCategory category = (PlaceCategory) item;

				Bitmap icon = Utils.getMarkerIconBitmap(category.img);

				ImageView im = (ImageView) vi.findViewById(R.id.place_icon);
				TextView tv = (TextView) vi.findViewById(R.id.place_category);

				im.setImageBitmap(icon);
				tv.setText(category.category.toUpperCase());

			} else if (item.getType() == PlaceItem.TYPE_PLACE_INFO) {
				vi = inflater.inflate(R.layout.row_places_info, null);
				final PlaceInfo info = (PlaceInfo) item;

				final TextView title = (TextView) vi.findViewById(R.id.place_info_title);
				final TextView addr = (TextView) vi.findViewById(R.id.place_info_addr);
				
				title.setText(info.title);
				addr.setText(info.address);
				
				vi.setOnTouchListener(new OnTouchListener() {
					
					@Override
					public boolean onTouch(View v, MotionEvent event) {
						RelativeLayout rl = (RelativeLayout) v.findViewById(R.id.places_info_rl2);
						
						Rect hitBox = new Rect();
						rl.getHitRect(hitBox);
						
						Log.v(Utils.TAG, "OUTSIDE SWITCH ACTION " + event.getAction() + " X=" + (int) event.getX() + "  Y=" + (int) event.getY());
						Log.v(Utils.TAG, hitBox.toShortString());
						
						switch(event.getAction()) {
						case MotionEvent.ACTION_DOWN:
							title.setTextColor(v.getResources().getColor(R.color.white));
							addr.setTextColor(v.getResources().getColor(R.color.white));
							rl.setBackgroundColor(v.getResources().getColor(R.color.main_green));
							Log.v(Utils.TAG, "INSIDE SWITCH DOWN");
							break;
						case MotionEvent.ACTION_UP:
							if(hitBox.contains((int) event.getX(), (int) event.getY())) {
								title.setTextColor(v.getResources().getColor(R.color.main_text_title));
								addr.setTextColor(v.getResources().getColor(R.color.main_text_title));
								rl.setBackgroundColor(v.getResources().getColor(R.color.main_light_green));
								Intent myIntent = new Intent(v.getContext(), Map.class);
								myIntent.putExtra("id", info.id);
								activity.startActivityForResult(myIntent, 0);
								Log.v(Utils.TAG, "INSIDE SWITCH UP");
							}
							break;
						case MotionEvent.ACTION_CANCEL:
								title.setTextColor(v.getResources().getColor(R.color.main_text_title));
								addr.setTextColor(v.getResources().getColor(R.color.main_text_title));
								rl.setBackgroundColor(v.getResources().getColor(R.color.main_light_green));
								Log.v(Utils.TAG, "INSIDE SWITCH CANCEL");
							break;
						default:
							break;
						}
						
						return true;
					}
				});
				
			} else {
				vi = inflater.inflate(R.layout.row_places_sep, null);
			}
		}
		return vi;
	}
}
