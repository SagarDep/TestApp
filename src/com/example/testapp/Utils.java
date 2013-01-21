package com.example.testapp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

@SuppressLint("SimpleDateFormat")
public class Utils {
	public static final String TAG							= "APP";
	
	public static final String PREFS_FILE					= "prefsFile";
	public static final String PREFS_KEY_NEWS				= "news";
	public static final String PREFS_KEY_NEWS_DATE			= "newsDate";
	public static final String PREFS_KEY_SCHEDULE			= "schedule";
	public static final String PREFS_KEY_SCHEDULE_DATE		= "scheduleDate";
	public static final String PREFS_KEY_MAP				= "map";
	public static final String PREFS_KEY_MAP_DATE			= "mapDate";
	
	public static final String DB_MARKER_URL				= "http://nutty.rymdraket.net/android/markers.php";
	public static final String DB_IMAGE_URL					= "http://nutty.rymdraket.net/android/imgs/";
	
	public static final String MSG_LOADING_NEWS				= "Laddar nyheter...";
	public static final String MSG_LOADING_SCHEDULE 		= "Laddar schema...";
	public static final String MSG_LOADING_MAP				= "Laddar karta...";

	public static final int ECODE_NO_ERROR				 	= -1;
	public static final int ECODE_NO_INTERNET_CONNECTION	=  0;
	
	public static final String EMSG_NO_INTERNET_CONNECTION	= "Nätverk ej tillgängligt.";
	
	private static final SimpleDateFormat DATE_FORMAT		= new SimpleDateFormat("d MMM HH:mm");

	private static HashMap<String, String> dayMap			= new HashMap<String, String>();
	private static HashMap<String, String> monthMap			= new HashMap<String, String>(); 
	
	static {
		dayMap.put("mon,", "Mån,");
		dayMap.put("tue,", "Tis,");
		dayMap.put("wed,", "Ons,");
		dayMap.put("thu,", "Tor,");
		dayMap.put("fri,", "Fre,");
		dayMap.put("sat,", "Lör,");
		dayMap.put("sun,", "Sön,");
		
		monthMap.put("jan", "Jan");
		monthMap.put("feb", "Feb");
		monthMap.put("mar", "Mar");
		monthMap.put("apr", "Apr");
		monthMap.put("may", "Maj");
		monthMap.put("jun", "Jun");
		monthMap.put("jul", "Jul");
		monthMap.put("aug", "Aug");
		monthMap.put("sep", "Sep");
		monthMap.put("oct", "Okt");
		monthMap.put("nov", "Nov");
		monthMap.put("dec", "Dec");
		
		DATE_FORMAT.setLenient(false);
	}
	
	public static String errWithDate(int errCode, Date date, boolean newLine) {
		String msg = "";
		switch(errCode) {
			case ECODE_NO_INTERNET_CONNECTION:
				msg = EMSG_NO_INTERNET_CONNECTION;
				msg += (newLine) ? "\n" : " ";
				msg += "Visar data från ";
				msg += translateMonth(DATE_FORMAT.format(date).toString(), 1);
				Log.i(TAG, "UTIL FORM " + msg);
				break;
		}
		return msg;
	}
	
	public static void showToast(Context context, String msg, int duration) {
		Toast.makeText(context, msg, duration).show();
	}
	
	private static String translateMonth(String date, int monthIndex) {
		String[] split = date.toLowerCase().split(" ");
		split[monthIndex] = monthMap.get(split[monthIndex]);
		String res = "";
		for (String s : split)
			res += s + " ";
		return res;
	}
	
	public static String translateDate(String pubDate) {
		String[] split = pubDate.toLowerCase().split(" ");
		split[0] = dayMap.get(split[0]);
		split[2] = monthMap.get(split[2]);
		return split[0] + " " + split[1] + " " + split[2] + " " + split[3];
	}
}
