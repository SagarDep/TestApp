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
		OnTouchListener listener = getOnTouchListener(R.id.main_btn_light_blue, R.id.main_btn_blue, R.id.main_btn_light_blue);
		Button small = (Button) findViewById(R.id.main_btn_blue);
		Button big = (Button) findViewById(R.id.main_btn_light_blue);
		small.setOnTouchListener(listener);
		big.setOnTouchListener(listener);
	}
	
	
	
	private OnTouchListener getOnTouchListener(final int resButton, final int resOnPress, final int resOnRelease) {
		return new OnTouchListener(){
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				ImageButton button = (ImageButton) findViewById(resButton);
				switch(event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					button.setBackgroundColor(getResources().getColor(resOnPress));
					break;
				case MotionEvent.ACTION_UP:
					button.setBackgroundColor(getResources().getColor(resOnRelease));
					break;
				default:
					break;
				}
				return false;
			}
		};
	}
}
