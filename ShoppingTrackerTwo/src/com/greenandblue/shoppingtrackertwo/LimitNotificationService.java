package com.greenandblue.shoppingtrackertwo;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class LimitNotificationService extends Service{
	private NotificationManager messagesManager;	
	private final static int NOTI_NO = 1;
	
	/**
	 * onCreate - new thread to check DB
	 */
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		Log.d("Testing", "Service got created");
	}
	
	/**
	 * onStart creates MessangerManager and starts LimitCheker in a new thread
	 */
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		super.onStartCommand(intent, flags, startId);
		Log.d("Testing", "Service started");
		messagesManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		Thread t = new Thread(null, new LimitChecker(), "BackgroundService");
		t.start();
		return 0;
	}
	
	/**
	 * runnable class implementing DB access and comparison with limit saved as shared preference
	 */
	class LimitChecker implements Runnable
	{
		/**
		 * DB access and comparison with limit saved as shared preference
		 * in case that limit is exceeded, shows notification message
		 */
		@Override
		public void run() {
			// TODO Auto-generated method stub
			SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
			int limit = sp.getInt("LIMIT", 0);
			Log.i("PreferenceLIMIT", "limit is " + limit);
			if(limit>0)
			{
				PaymentsDB info = new PaymentsDB(getApplicationContext());
				info.open();
				int total = info.lastMonthSum();
				Log.i("Testing", "total is" + total);
				info.close();
				if(limit<total)
				{
					Log.i("Testing", "Before display function");
					displayMessage("Month limit exceeded. The limit sum is " + limit + ". Spent: " + total);
				}
			}
		}	
	}
	
	/**
	 * onDestroy
	 */
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	/**
	 * this is to display the limit excess message to user as notification
	 * @param message
	 */
	public void displayMessage(String message) {
		// TODO Auto-generated method stub
		Log.i("Testing", "In display function");
		NotificationCompat.Builder builder =  
	            new NotificationCompat.Builder(this)  
	            .setSmallIcon(R.drawable.ic_launcher)  
	            .setContentTitle("Month shopping limit")  
	            .setContentText(message);
		Intent notificationIntent = new Intent(this, StartActivity.class);  
	    PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
	    builder.setContentIntent(contentIntent);
	    builder.setAutoCancel(true);
	    builder.setTicker(message);
	    messagesManager.notify(NOTI_NO, builder.build());
	    Log.i("Testing", "In display function after set notify");
	}


	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

}
