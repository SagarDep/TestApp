package com.example.testapp;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.maps.MapsInitializer;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class Main extends Activity {
	private static final String sponsor_1_address = "http://www.google.com";
	private static final String sponsor_2_address = "http://www.facebook.com";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		try {
			MapsInitializer.initialize(Main.this);
		} catch (GooglePlayServicesNotAvailableException e) {
			Log.e(Utils.TAG, "PLACES MapsInitializer Failed!");
			e.printStackTrace();
		}
		
		Utils.initMarkerIcons(this);
		
		initButtons();
	}
	
	private void initButtons() {
		Button news = (Button) findViewById(R.id.button1);
		news.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent myIntent = new Intent(v.getContext(), News.class);
				startActivityForResult(myIntent, 0);
			}
		});
		
		Button cal = (Button) findViewById(R.id.button2);
		cal.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent myIntent = new Intent(v.getContext(), Calendar.class);
				startActivityForResult(myIntent, 0);
			}
		});
		
		Button places = (Button) findViewById(R.id.button3);
		places.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent myIntent = new Intent(v.getContext(), Places.class);
				startActivityForResult(myIntent, 0);
			}
		});

		Button map = (Button) findViewById(R.id.button4);
		map.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent myIntent = new Intent(v.getContext(), Map.class);
				startActivityForResult(myIntent, 0);
			}
		});
		
		ImageButton sponsor1 = (ImageButton) findViewById(R.id.imageView2);
		sponsor1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(sponsor_1_address));
				startActivity(myIntent);
			}
		});
		
		ImageButton sponsor2 = (ImageButton) findViewById(R.id.imageView3);
		sponsor2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(sponsor_2_address));
				startActivity(myIntent);
			}
		});
	}
	
}
