package com.example.testapp;

import java.util.ArrayList;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.example.testapp.contactitem.ContactItem;

public class ContactAdapter extends BaseAdapter {
	private Activity activity;
	private ArrayList<ContactItem> data;

	public ContactAdapter(Activity a, ArrayList<ContactItem> d) {
		this.activity = a;
		this.data = d;
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
		// TODO Auto-generated method stub
		return null;
	}

}
