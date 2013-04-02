package com.example.testapp;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
				
				vi.setOnClickListener(new OnClickListener(){
					
					@Override
					public void onClick(View vi) {
						Intent myIntent = new Intent(vi.getContext(), Map.class);
						myIntent.putExtra("id", info.id);
						activity.startActivityForResult(myIntent, 0);
					}
					
				});
				
			} else {
				vi = inflater.inflate(R.layout.row_places_sep, null);
			}
		}
		return vi;
	}
}
