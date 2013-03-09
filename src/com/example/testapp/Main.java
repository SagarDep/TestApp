package com.example.testapp;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.maps.MapsInitializer;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageButton;

public class Main extends Activity {
	private static final String sponsor_just_nu = "http://www.justnu.se/";
	
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
	
	private void initButtons() {
		//BLUE
		OnTouchListener onTouchListener = getOnTouchListener(R.id.main_btn_light_blue, R.color.main_blue, R.color.main_light_blue);
		OnClickListener onClickListener = getOnClickListener(News.class);
		ImageButton small = (ImageButton) findViewById(R.id.main_btn_blue);
		ImageButton big   = (ImageButton) findViewById(R.id.main_btn_light_blue);
		small.setOnTouchListener(onTouchListener);
		small.setOnClickListener(onClickListener);
		big.setOnTouchListener(onTouchListener);
		big.setOnClickListener(onClickListener);

		//RED
		onTouchListener = getOnTouchListener(R.id.main_btn_light_red, R.color.main_red, R.color.main_light_red);
		onClickListener = getOnClickListener(Calendar.class);
		small = (ImageButton) findViewById(R.id.main_btn_red);
		big	  = (ImageButton) findViewById(R.id.main_btn_light_red);
		small.setOnTouchListener(onTouchListener);
		small.setOnClickListener(onClickListener);
		big.setOnTouchListener(onTouchListener);
		big.setOnClickListener(onClickListener);
		
		//GREEN
		onTouchListener = getOnTouchListener(R.id.main_btn_light_green, R.color.main_green, R.color.main_light_green);
		onClickListener = getOnClickListener(Places.class);
		small = (ImageButton) findViewById(R.id.main_btn_green);
		big	  = (ImageButton) findViewById(R.id.main_btn_light_green);
		small.setOnTouchListener(onTouchListener);
		small.setOnClickListener(onClickListener);
		big.setOnTouchListener(onTouchListener);
		big.setOnClickListener(onClickListener);
		
		//YELLOW
		onTouchListener = getOnTouchListener(R.id.main_btn_light_yellow, R.color.main_yellow, R.color.main_light_yellow);
		onClickListener = getOnClickListener(Map.class);
		small = (ImageButton) findViewById(R.id.main_btn_yellow);
		big	  = (ImageButton) findViewById(R.id.main_btn_light_yellow);
		small.setOnTouchListener(onTouchListener);
		small.setOnClickListener(onClickListener);
		big.setOnTouchListener(onTouchListener);
		big.setOnClickListener(onClickListener);

		//PURPLE
		onTouchListener = getOnTouchListener(R.id.main_btn_light_purple, R.color.main_purple, R.color.main_light_purple);
		onClickListener = getOnClickListener(Map.class); //SKA ÄNDRAS TILL CONTACT NÄR DEN FINNS
		small = (ImageButton) findViewById(R.id.main_btn_purple);
		big	  = (ImageButton) findViewById(R.id.main_btn_light_purple);
		small.setOnTouchListener(onTouchListener);
		small.setOnClickListener(onClickListener);
		big.setOnTouchListener(onTouchListener);
		big.setOnClickListener(onClickListener);
	
	}
	
	
	
	private OnClickListener getOnClickListener(final Class<?> class1) {
		return new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.v(Utils.TAG, "MSG FROM MAIN onClick");
				Intent myIntent = new Intent(v.getContext(), class1);
				startActivityForResult(myIntent, 0);
			}
		};
	}

	private OnTouchListener getOnTouchListener(final int resButton, final int resOnPress, final int resOnRelease) {
		return new OnTouchListener(){
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				ImageButton button = (ImageButton) findViewById(resButton);
				switch(event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					Log.v(Utils.TAG, "MSG FROM MAIN onTouch DOWN");
					button.setBackgroundColor(getResources().getColor(resOnPress));
					break;
				case MotionEvent.ACTION_UP:
					Log.v(Utils.TAG, "MSG FROM MAIN onTouch UP");
					button.setBackgroundColor(getResources().getColor(resOnRelease));
					break;
				default:
					break;
				}
				return button.onTouchEvent(event);
			}
		};
	}
}
