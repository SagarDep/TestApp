package com.example.testapp;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.testapp.placeitem.PlaceCategory;
import com.example.testapp.placeitem.PlaceInfo;
import com.example.testapp.placeitem.PlaceItem;

public class PlaceAdapter extends ArrayAdapter<PlaceItem> {

	private Activity activity;
	private ArrayList<PlaceItem> data;
	private LayoutInflater inflater;
	private static Bitmap arrow;
	
	public PlaceAdapter(Activity a, ArrayList<PlaceItem> d) {
		super(a, 0, d);
		
		this.activity = a;
		this.data = d;
		this.inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		arrow = BitmapFactory.decodeResource(activity.getResources(), R.drawable.right_arrow);
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

				Bitmap icon = Utils.getMarkerIconBitmap(category.category);

				ImageView im = (ImageView) vi.findViewById(R.id.place_icon);
				TextView tv = (TextView) vi.findViewById(R.id.place_category);

				im.setImageBitmap(icon);
				tv.setText(category.category);

			} else if (item.getType() == PlaceItem.TYPE_PLACE_INFO) {
				vi = inflater.inflate(R.layout.row_places_info, null);
				PlaceInfo info = (PlaceInfo) item;

				TextView title = (TextView) vi.findViewById(R.id.place_info_title);
				TextView addr = (TextView) vi.findViewById(R.id.place_info_addr);
				ImageView im = (ImageView) vi.findViewById(R.id.place_info_arrow);
				
				title.setText(info.title);
				addr.setText(info.address);
				im.setImageBitmap(arrow);

			} else {
				vi = inflater.inflate(R.layout.row_places_sep, null);
			}
		}
		return vi;
	}
}
