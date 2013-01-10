package com.example.testapp;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;

public class Main extends Activity {
	private final String sponsor_1_address = "http://www.google.com";
	private final String sponsor_2_address = "http://www.facebook.com";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		initButtons();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
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
