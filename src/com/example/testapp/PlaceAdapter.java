package com.example.testapp;

import java.util.ArrayList;

import com.example.testapp.placeitem.PlaceItem;
import com.example.testapp.placeitem.PlaceCategory;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class PlaceAdapter extends ArrayAdapter<PlaceItem> {

	private Activity activity;
	private ArrayList<PlaceItem> data;
	private LayoutInflater inflater;

	public PlaceAdapter(Activity a, ArrayList<PlaceItem> d) {
		super(a, 0, d);
		
		this.activity = a;
		this.data = d;
		this.inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		data = new ArrayList<PlaceItem>();
		PlaceCategory title = new PlaceCategory(PlaceCategory.TYPE_PLACE_TITLE);
		title.setTitle("TestTitle");
		data.add(title);
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
		
		vi = inflater.inflate(R.layout.row_places_title, null);
		
		TextView tv = (TextView) vi.findViewById(R.id.place_title);
		tv.setText("TestText.nu");
		
		return vi;
	}
}
