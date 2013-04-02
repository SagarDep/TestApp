package com.example.testapp;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.testapp.contactitem.ContactItem;
import com.example.testapp.contactitem.ContactPerson;
import com.example.testapp.contactitem.ContactTitle;

public class ContactAdapter extends ArrayAdapter<ContactItem> {
	private Activity activity;
	private ArrayList<ContactItem> data;
	private LayoutInflater inflater;

	public ContactAdapter(Activity a, ArrayList<ContactItem> d) {
		super(a, 0, d);
		
		this.activity = a;
		this.data = d;
		this.inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
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
	public ContactItem getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View vi = convertView;
		
		ContactItem item = getItem(position);
		
		if(item != null) {
			if(item.getType() == ContactItem.TYPE_CONTACT_TITLE) {
				vi = inflater.inflate(R.layout.row_contacts_title, null);
				
				ContactTitle title = (ContactTitle) item;
				TextView tv = (TextView) vi.findViewById(R.id.contact_title);
				
				String s = title.title;
				
				if(s.equals("GENERAL")) s += "ER";
				else s += "S FADDRAR";
				
				tv.setText(s);
				
			} else if(item.getType() == ContactItem.TYPE_CONTACT_PERSON) {
				vi = inflater.inflate(R.layout.row_contacts_info, null);

				ContactPerson person = (ContactPerson) item;
				TextView name = (TextView) vi.findViewById(R.id.contacts_info_name);
				TextView phone = (TextView) vi.findViewById(R.id.contacts_info_phone);
				
				name.setText(person.name);
				phone.setText(person.phone);
				
			} else {
				vi = inflater.inflate(R.layout.row_contacts_sep, null);
			}
		}
		
		return vi;
	}

}
