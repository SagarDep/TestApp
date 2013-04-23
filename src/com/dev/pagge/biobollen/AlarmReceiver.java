package com.dev.pagge.biobollen;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

public class AlarmReceiver extends BroadcastReceiver {
	private boolean showNotifications;

	/*
	 TITLE
	 TIME
	 CATEGORY (för bild)
	 PLACE
	 PLACE_ID
	 */
	
	
	@Override
	public void onReceive(Context context, Intent intent) {
		loadSettings(context);
		if(showNotifications) {
			Bundle bundle = intent.getExtras();
			
			String title = bundle.getString("TITLE");
			String time = bundle.getString("TIME");
			String place = bundle.getString("PLACE");
			int placeId = bundle.getInt("PLACE_ID");
			int requestCode = bundle.getInt("REQ");
			
			//			String message = bundle.getString("alarm_message");
	//		Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
			
			Intent i = new Intent(context, Map.class);
			i.putExtra("id", placeId);
			
			PendingIntent pIntent = PendingIntent.getActivity(context, 0, i, 0);
			
			Notification noti = new NotificationCompat.Builder(context)
					.setContentTitle(title)
					.setContentText(time + " " + place)
					.setContentIntent(pIntent)
					.setContentInfo("")
					.setSubText("Tryck för att visa på kartan")
					.setTicker("BioBollen: " + title + " kl. " + time + "\nPlats: " + place)
					.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
					.setVibrate(new long[]{0,250,100,250})
					.setSmallIcon(R.drawable.marker_baseball)
					.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher))
					.build();
		
			NotificationManager notiManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
			noti.flags |= Notification.FLAG_AUTO_CANCEL;
			notiManager.notify(requestCode, noti);
		}
	}

	
	private void loadSettings(Context context) {
		SharedPreferences prefs = context.getSharedPreferences(Utils.PREFS_FILE, Context.MODE_PRIVATE);
		showNotifications = prefs.getBoolean(Utils.PREFS_KEY_SCHEDULE_NOTI, true);
	}
}
