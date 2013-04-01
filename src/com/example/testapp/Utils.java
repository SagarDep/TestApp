package com.example.testapp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.Toast;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

@SuppressLint("SimpleDateFormat")
public class Utils {
	public static final String TAG = "APP";

	public static final String PREFS_FILE 					= "prefsFile";
	public static final String PREFS_KEY_NEWS				= "news";
	public static final String PREFS_KEY_NEWS_UPDATE		= "newsUpdate";
	public static final String PREFS_KEY_NEWS_TIME			= "newsTime";
	public static final String PREFS_KEY_SCHEDULE			= "schedule";
	public static final String PREFS_KEY_SCHEDULE_UPDATE	= "scheduleUpdate";
	public static final String PREFS_KEY_SCHEDULE_TIME		= "scheduleTime";
	public static final String PREFS_KEY_PLACE				= "place";
	public static final String PREFS_KEY_PLACE_UPDATE		= "placeDate";
	public static final String PREFS_KEY_PLACE_TIME			= "placeTime";
	public static final String PREFS_KEY_MAP				= "map";
	public static final String PREFS_KEY_MAP_UPDATE			= "mapDate";
	public static final String PREFS_KEY_MAP_TIME			= "mapTime";

	public static final String REFRESH_BUTTON_TEXT			= "Uppdatera";
	public static final String REFRESH_BUTTON_TEXT_PRESSED	= "Letar...";
	
	public static final String DB_NEWS_URL		= "http://nutty.rymdraket.net/android/news.php?mode=";
	public static final String DB_SCHEDULE_URL	= "http://nutty.rymdraket.net/android/schedule.php?mode=";
	public static final String DB_PLACES_URL	= "http://nutty.rymdraket.net/android/places.php?mode=";
	public static final String DB_CONTACTS_URL	= "http://nutty.rymdraket.net/android/contacts.php?mode=";
	public static final String DB_MARKER_URL	= "http://nutty.rymdraket.net/android/markers.php";
	public static final String DB_IMAGE_URL		= "http://nutty.rymdraket.net/android/imgs/";

	public static final int DB_MODE_REFRESH		= 0;
	public static final int DB_MODE_GET			= 1;
	
	public static final String MSG_LOADING_NEWS		= "Laddar nyheter...";
	public static final String MSG_LOADING_SCHEDULE	= "Laddar schema...";
	public static final String MSG_LOADING_PLACES	= "Laddar platser...";
	public static final String MSG_LOADING_MAP		= "Laddar karta...";
	public static final String MSG_LOADING_CONTACTS	= "Laddar kontakter...";

	public static final int ECODE_NO_ERROR					= -1;
	public static final int ECODE_NO_INTERNET_CONNECTION	=  0;

	public static final String EMSG_NO_INTERNET_CONNECTION = "Nätverk ej tillgängligt.";

	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("d MMM HH:mm");

	private static HashMap<String, String> dayMap				= null;
	private static HashMap<String, String> monthMap				= null;
	private static HashMap<String, String> catMap				= null;
	private static HashMap<String, BitmapDescriptor> iconBMMap	= null;
	private static HashMap<String, Bitmap> iconBMDMap			= null;

	static {
		DATE_FORMAT.setLenient(false);

		dayMap = new HashMap<String, String>();
		dayMap.put("mon,", "Mån,");
		dayMap.put("tue,", "Tis,");
		dayMap.put("wed,", "Ons,");
		dayMap.put("thu,", "Tor,");
		dayMap.put("fri,", "Fre,");
		dayMap.put("sat,", "Lör,");
		dayMap.put("sun,", "Sön,");

		monthMap = new HashMap<String, String>();
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
		
		catMap = new HashMap<String, String>();
		catMap.put("ATM", "Bankomat");
		catMap.put("BASEBALL", "Brännboll");
		catMap.put("BMC", "BMC");
		catMap.put("FASTFOOD", "Snabbmat");
		catMap.put("FOOD", "Middag");
		catMap.put("HOME", "Hem");
		catMap.put("HOSPITAL", "Sjukhus");
		catMap.put("NATION", "Nationer");
		catMap.put("STORE", "Affärer");
		catMap.put("TRAIN", "Transport");
		
		
		iconBMMap = new HashMap<String, BitmapDescriptor>();
		iconBMMap.put("ATM", BitmapDescriptorFactory.fromResource(R.drawable.marker_atm));
		iconBMMap.put("BASEBALL", BitmapDescriptorFactory.fromResource(R.drawable.marker_baseball));
		iconBMMap.put("BMC", BitmapDescriptorFactory.fromResource(R.drawable.marker_bmc));
		iconBMMap.put("FASTFOOD", BitmapDescriptorFactory.fromResource(R.drawable.marker_fastfood));
		iconBMMap.put("FOOD", BitmapDescriptorFactory.fromResource(R.drawable.marker_food));
		iconBMMap.put("HOME", BitmapDescriptorFactory.fromResource(R.drawable.marker_home));
		iconBMMap.put("HOSPITAL", BitmapDescriptorFactory.fromResource(R.drawable.marker_hospital));
		iconBMMap.put("NATION", BitmapDescriptorFactory.fromResource(R.drawable.marker_nation));
		iconBMMap.put("STORE", BitmapDescriptorFactory.fromResource(R.drawable.marker_store));
		iconBMMap.put("TRAIN", BitmapDescriptorFactory.fromResource(R.drawable.marker_train));
	}

	public static String errWithDate(int errCode, Date date, boolean newLine) {
		String msg = "";
		switch (errCode) {
		case ECODE_NO_INTERNET_CONNECTION:
			msg = EMSG_NO_INTERNET_CONNECTION;
			msg += (newLine) ? "\n" : " ";
			msg += "Visar data från ";
			msg += translateMonth(DATE_FORMAT.format(date).toString(), 1);
			break;
		}
		return msg;
	}

	public static void showToast(Context context, String msg, int duration) {
		Toast.makeText(context, msg, duration).show();
	}

	public static int compareTime(String updateTime, String lastUpdateDate) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		try {
			Date current	= df.parse(updateTime);
			Date last		= df.parse(lastUpdateDate);
			
			return current.compareTo(last);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return -1;
	}
	
	public static String translateCategory(String cat) {
		return catMap.get(cat);
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
	
	public static String formatDate(String pubDate) {
		String[] date = pubDate.trim().split(" ")[0].split("-");
		
		String month = date[1];
		
		if(month.equals("01")) month = "Jan";
		else if(month.equals("02")) month = "Feb";
		else if(month.equals("03")) month = "Mar";
		else if(month.equals("04")) month = "Apr";
		else if(month.equals("05")) month = "Maj";
		else if(month.equals("06")) month = "Jun";
		else if(month.equals("07")) month = "Jul";
		else if(month.equals("08")) month = "Aug";
		else if(month.equals("09")) month = "Sep";
		else if(month.equals("10")) month = "Okt";
		else if(month.equals("11")) month = "Nov";
		else if(month.equals("12")) month = "Dec";
		
//		String day = date[2].substring(0,1).equals("0") ? date[2].substring(1, 2) : date[2];
		String day = date[2];
		
		return day + "\n" + month;
	}

	public static BitmapDescriptor getMarkerIcon(String category) {
		return iconBMMap.get(category);
	}

	public static Bitmap getMarkerIconBitmap(String category) {
		return iconBMDMap.get(category);
	}

	public static void initMarkerIcons(Activity activity) {
		Resources res = activity.getResources();
		iconBMDMap = new HashMap<String, Bitmap>();
		iconBMDMap.put("ATM", BitmapFactory.decodeResource(res, R.drawable.marker_atm));
		iconBMDMap.put("BASEBALL", BitmapFactory.decodeResource(res, R.drawable.marker_baseball));
		iconBMDMap.put("BMC", BitmapFactory.decodeResource(res, R.drawable.marker_bmc));
		iconBMDMap.put("FASTFOOD", BitmapFactory.decodeResource(res, R.drawable.marker_fastfood));
		iconBMDMap.put("FOOD", BitmapFactory.decodeResource(res, R.drawable.marker_food));
		iconBMDMap.put("HOME", BitmapFactory.decodeResource(res, R.drawable.marker_home));
		iconBMDMap.put("HOSPITAL", BitmapFactory.decodeResource(res, R.drawable.marker_hospital));
		iconBMDMap.put("NATION", BitmapFactory.decodeResource(res, R.drawable.marker_nation));
		iconBMDMap.put("STORE", BitmapFactory.decodeResource(res, R.drawable.marker_store));
		iconBMDMap.put("TRAIN", BitmapFactory.decodeResource(res, R.drawable.marker_train));
	}
}
