package com.dev.pagge.biobollen;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

public class AlarmReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle bundle = intent.getExtras();
		String message = bundle.getString("alarm_message");
		Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
		
		Intent i = new Intent(context, Map.class);
		i.putExtra("id", 6);
		
		PendingIntent pIntent = PendingIntent.getActivity(context, 0, i, 0);
		
		Notification noti = new NotificationCompat.Builder(context)
				.setContentTitle("Biobollen: Brännboll")
				.setContentText("13:00 St Hans Backar")
				.setContentIntent(pIntent)
				.setContentInfo("")
				.setSubText("Tryck för att visa på kartan")
				.setTicker("Brännboll 13:00")
				.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
				.setVibrate(new long[]{0,250,100,250})
				.setSmallIcon(R.drawable.marker_baseball)
				.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher))
				.build();
	
		NotificationManager notiManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		noti.flags |= Notification.FLAG_AUTO_CANCEL;
		notiManager.notify(464155, noti);
	}

}
