package com.example.testapp;

import java.util.ArrayList;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
				final TextView name = (TextView) vi.findViewById(R.id.contacts_info_name);
				final TextView phone = (TextView) vi.findViewById(R.id.contacts_info_phone);
				
				name.setText(person.name);
				phone.setText(person.phone);
				
				
				vi.setOnTouchListener(new OnTouchListener() {
					
					@Override
					public boolean onTouch(View v, MotionEvent event) {
						RelativeLayout rl = (RelativeLayout) v.findViewById(R.id.contacts_info_rl2);
						
						Rect hitBox = new Rect();
						rl.getHitRect(hitBox);
						
						Log.v(Utils.TAG, "OUTSIDE SWITCH ACTION " + event.getAction() + " X=" + (int) event.getX() + "  Y=" + (int) event.getY());
						Log.v(Utils.TAG, hitBox.toString());
						
						switch(event.getAction()) {
						case MotionEvent.ACTION_DOWN:
							name.setTextColor(v.getResources().getColor(R.color.white));
							phone.setTextColor(v.getResources().getColor(R.color.white));
							rl.setBackgroundColor(v.getResources().getColor(R.color.main_purple));
							Log.v(Utils.TAG, "INSIDE SWITCH DOWN");
							break;
						case MotionEvent.ACTION_UP:
							if(hitBox.contains((int) event.getX(), (int) event.getY())) {
								name.setTextColor(v.getResources().getColor(R.color.main_text_title));
								phone.setTextColor(v.getResources().getColor(R.color.main_text_title));
								rl.setBackgroundColor(v.getResources().getColor(R.color.main_light_purple));
								try {
									Intent intent = new Intent(Intent.ACTION_DIAL);
									intent.setData(Uri.parse(("tel:"+phone.getText()).replace("-", "")));
									activity.startActivity(intent);
								} catch (ActivityNotFoundException activityException) {
				                    Log.e(Utils.TAG, "CONTACT CALL FAILED", activityException);
				                }
							}
							break;
						case MotionEvent.ACTION_CANCEL:
								name.setTextColor(v.getResources().getColor(R.color.main_text_title));
								phone.setTextColor(v.getResources().getColor(R.color.main_text_title));
								rl.setBackgroundColor(v.getResources().getColor(R.color.main_light_purple));
								Log.v(Utils.TAG, "INSIDE SWITCH CANCEL");
							break;
						default:
							break;
						}
						
						return true;
					}
				});
				
			} else {
				vi = inflater.inflate(R.layout.row_contacts_sep, null);
			}
		}
		
		return vi;
	}

}
