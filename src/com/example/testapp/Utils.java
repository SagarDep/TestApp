package com.example.testapp;

import android.content.Context;
import android.widget.Toast;

public class Utils {
	public static final String TAG = "App";
	
	public static final int ECODE_NO_ERROR				 	= -1;
	public static final int ECODE_NO_INTERNET_CONNECTION	= 0;
	
	public static final String EMSG_NO_INTERNET_CONNECTION	= "Nätverk ej tillgängligt\nVisar data från 9 Jan 2013";
	
	public static void showToast(Context context, String msg, int time) {
		Toast.makeText(context, msg, time).show();
	}
}
