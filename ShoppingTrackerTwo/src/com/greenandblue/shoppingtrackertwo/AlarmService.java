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

/**
 * @author Tal
 * Class sets daily notification for sum spent in current month
 *
 */
public class AlarmService extends Service {
	private NotificationManager messagesManager;	
	private final static int NOTI_NO = 2;
	
	/**
	 * onCreate
	 */
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		Log.i("Testing", "Alarm Service got created");
		
	}
	
	/**
	 * onStart creates MessangerManager and starts LimitAndSpentSumCheker in a new thread
	 */
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		super.onStartCommand(intent, flags, startId);
		messagesManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		Log.i("Testing", "Alarm Service starting");
		Thread t = new Thread(null, new LimitAndSpentSumCheker(), "BackgroundService");
		t.start();
		return 0;
	}

	/**
	 * runnable class that monitors spent sum and set limit and notifies the user
	 *
	 */
	class LimitAndSpentSumCheker implements Runnable
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
			PaymentsDB info = new PaymentsDB(getApplicationContext());
			info.open();
			int total = info.lastMonthSum();
			Log.i("Testing", "total is" + total);
			info.close();
			if(limit>0)
			{
				if(limit<total)
				{
					String messageForUser = "Warning! You are over the month limit. Spent in current month: " + total;
					messageForUser += ". Month limit is " + limit;
					displayMessage(messageForUser);
					
				}
				else
				{
					displayMessage("Spent in current month: " + total);
				}
			}
			else
			{
				displayMessage("Spent in current month: " + total);
			}

			Log.i("ALARM", "in thread - stop");
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
	 * onBind
	 */
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * this is to display message to user about spent monthly sum as notification 
	 * @param message as String
	 */
	public void displayMessage(String message) {
		// TODO Auto-generated method stub
		NotificationCompat.Builder builder =  
	            new NotificationCompat.Builder(this)  
	            .setSmallIcon(R.drawable.ic_launcher)  
	            .setContentTitle("Shopping notification")  
	            .setContentText(message);
		Intent notificationIntent = new Intent(this, StartActivity.class);  
	    PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);  
	    builder.setContentIntent(contentIntent); 
	    builder.setAutoCancel(true);
	    builder.setTicker(message);
	    messagesManager.notify(NOTI_NO, builder.build());
	}

}