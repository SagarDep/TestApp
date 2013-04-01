package com.example.testapp;

import java.util.ArrayList;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.example.testapp.contactitem.ContactItem;
import com.example.testapp.contactitem.ContactPerson;
import com.example.testapp.contactitem.ContactTitle;

public class ContactAdapter extends BaseAdapter {
	private Activity activity;
	private ArrayList<ContactItem> data;

	public ContactAdapter(Activity a, ArrayList<ContactItem> d) {
		this.activity = a;
		this.data = d;
		
		for (ContactItem i : d) {
			if(i.getType() == ContactItem.TYPE_CONTACT_TITLE) {
				ContactTitle title = (ContactTitle) i;
				Log.v(Utils.TAG, title.title);
			} else if (i.getType() == ContactItem.TYPE_CONTACT_PERSON) {
				ContactPerson person = (ContactPerson) i;
				Log.v(Utils.TAG, person.name + " " + person.phone);
			} else {
				Log.v(Utils.TAG, "-----");
			}
			
		}
		
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
