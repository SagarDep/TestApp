package com.example.testapp;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.maps.MapsInitializer;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageButton;
import android.widget.TextView;

public class Main extends Activity {
	private final String sponsor_just_nu = "http://www.justnu.se/";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		try {
			MapsInitializer.initialize(Main.this);
		} catch (GooglePlayServicesNotAvailableException e) {
			Log.e(Utils.TAG, "MAIN  MapsInitializer Failed!");
			e.printStackTrace();
		}
		
		Utils.initMarkerIcons(this);
		
		initButtons();
	}
	
	@Override
	protected void onResume()  {
		super.onResume();
		resetButtons();
	}
	
	private void initButtons() {
		//BLUE
		OnTouchListener onTouchListener = getOnTouchListener(R.id.main_btn_light_blue, R.color.main_blue, R.color.main_light_blue, R.id.main_title_blue, R.id.main_desc_blue);
		OnClickListener onClickListener = getOnClickListener(News.class);
		ImageButton small = (ImageButton) findViewById(R.id.main_btn_blue);
		ImageButton big   = (ImageButton) findViewById(R.id.main_btn_light_blue);
		small.setOnTouchListener(onTouchListener);
		small.setOnClickListener(onClickListener);
		big.setOnTouchListener(onTouchListener);
		big.setOnClickListener(onClickListener);

		//RED
		onTouchListener = getOnTouchListener(R.id.main_btn_light_red, R.color.main_red, R.color.main_light_red, R.id.main_title_red, R.id.main_desc_red);
		onClickListener = getOnClickListener(Calendar.class);
		small = (ImageButton) findViewById(R.id.main_btn_red);
		big	  = (ImageButton) findViewById(R.id.main_btn_light_red);
		small.setOnTouchListener(onTouchListener);
		small.setOnClickListener(onClickListener);
		big.setOnTouchListener(onTouchListener);
		big.setOnClickListener(onClickListener);
		
		//GREEN
		onTouchListener = getOnTouchListener(R.id.main_btn_light_green, R.color.main_green, R.color.main_light_green, R.id.main_title_green, R.id.main_desc_green);
		onClickListener = getOnClickListener(Places.class);
		small = (ImageButton) findViewById(R.id.main_btn_green);
		big	  = (ImageButton) findViewById(R.id.main_btn_light_green);
		small.setOnTouchListener(onTouchListener);
		small.setOnClickListener(onClickListener);
		big.setOnTouchListener(onTouchListener);
		big.setOnClickListener(onClickListener);
		
		//YELLOW
		onTouchListener = getOnTouchListener(R.id.main_btn_light_yellow, R.color.main_yellow, R.color.main_light_yellow, R.id.main_title_yellow, R.id.main_desc_yellow);
		onClickListener = getOnClickListener(Map.class);
		small = (ImageButton) findViewById(R.id.main_btn_yellow);
		big	  = (ImageButton) findViewById(R.id.main_btn_light_yellow);
		small.setOnTouchListener(onTouchListener);
		small.setOnClickListener(onClickListener);
		big.setOnTouchListener(onTouchListener);
		big.setOnClickListener(onClickListener);

		//PURPLE
		onTouchListener = getOnTouchListener(R.id.main_btn_light_purple, R.color.main_purple, R.color.main_light_purple, R.id.main_title_purple, R.id.main_desc_purple);
		onClickListener = getOnClickListener(Contacts.class);
		small = (ImageButton) findViewById(R.id.main_btn_purple);
		big	  = (ImageButton) findViewById(R.id.main_btn_light_purple);
		small.setOnTouchListener(onTouchListener);
		small.setOnClickListener(onClickListener);
		big.setOnTouchListener(onTouchListener);
		big.setOnClickListener(onClickListener);
		
		ImageButton sponsor = (ImageButton) findViewById(R.id.main_sponsor);
		sponsor.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(sponsor_just_nu));
				startActivity(myIntent);
			}
		});	
	}
	
	private void resetButtons() {
		ImageButton button = (ImageButton) findViewById(R.id.main_btn_light_blue);
		TextView title = (TextView) findViewById(R.id.main_title_blue);
		TextView desc = (TextView) findViewById(R.id.main_desc_blue);
		button.setBackgroundColor(getResources().getColor(R.color.main_light_blue));
		title.setTextColor(getResources().getColor(R.color.main_text_title));
		desc.setTextColor(getResources().getColor(R.color.main_text_title));

		button = (ImageButton) findViewById(R.id.main_btn_light_red);
		title = (TextView) findViewById(R.id.main_title_red);
		desc = (TextView) findViewById(R.id.main_desc_red);
		button.setBackgroundColor(getResources().getColor(R.color.main_light_red));
		title.setTextColor(getResources().getColor(R.color.main_text_title));
		desc.setTextColor(getResources().getColor(R.color.main_text_title));

		button = (ImageButton) findViewById(R.id.main_btn_light_green);
		title = (TextView) findViewById(R.id.main_title_green);
		desc = (TextView) findViewById(R.id.main_desc_green);
		button.setBackgroundColor(getResources().getColor(R.color.main_light_green));
		title.setTextColor(getResources().getColor(R.color.main_text_title));
		desc.setTextColor(getResources().getColor(R.color.main_text_title));
		
		button = (ImageButton) findViewById(R.id.main_btn_light_yellow);
		title = (TextView) findViewById(R.id.main_title_yellow);
		desc = (TextView) findViewById(R.id.main_desc_yellow);
		button.setBackgroundColor(getResources().getColor(R.color.main_light_yellow));
		title.setTextColor(getResources().getColor(R.color.main_text_title));
		desc.setTextColor(getResources().getColor(R.color.main_text_title));

		button = (ImageButton) findViewById(R.id.main_btn_light_purple);
		title = (TextView) findViewById(R.id.main_title_purple);
		desc = (TextView) findViewById(R.id.main_desc_purple);
		button.setBackgroundColor(getResources().getColor(R.color.main_light_purple));
		title.setTextColor(getResources().getColor(R.color.main_text_title));
		desc.setTextColor(getResources().getColor(R.color.main_text_title));
	}
	
	private OnClickListener getOnClickListener(final Class<?> class1) {
		return new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent myIntent = new Intent(v.getContext(), class1);
				startActivityForResult(myIntent, 0);
			}
		};
	}

	private OnTouchListener getOnTouchListener(final int resButton, final int resOnPress, final int resOnRelease, final int resTitle, final int resDesc) {
		return new OnTouchListener(){
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				ImageButton button = (ImageButton) findViewById(resButton);
				TextView title = (TextView) findViewById(resTitle);
				TextView desc = (TextView) findViewById(resDesc);
				
				Rect hitBox = new Rect();
				button.getHitRect(hitBox);
				
				switch(event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					button.setBackgroundColor(getResources().getColor(resOnPress));
					title.setTextColor(getResources().getColor(R.color.white));
					desc.setTextColor(getResources().getColor(R.color.white));
					break;
				case MotionEvent.ACTION_UP:
					if(!hitBox.contains((int) event.getX(), (int) event.getY())) {
						button.setBackgroundColor(getResources().getColor(resOnRelease));
						title.setTextColor(getResources().getColor(R.color.main_text_title));
						desc.setTextColor(getResources().getColor(R.color.main_text_title));
					}
					break;
				default:
					break;
				}
				return button.onTouchEvent(event);
			}
		};
	}
}
