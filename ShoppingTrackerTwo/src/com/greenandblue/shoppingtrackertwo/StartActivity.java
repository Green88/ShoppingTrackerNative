package com.greenandblue.shoppingtrackertwo;



import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.DialogFragment;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Toast;

public class StartActivity extends Activity implements OnClickListener, OnCheckedChangeListener,
OnItemSelectedListener, TimePickerFragment.OnCompletePickerListener{
	TextView tvSpent, tvLimit, tvBalance;
	CheckBox cbOverTheLimit, cbDailyBalance;
	EditText etLimitSum;
	Button btSetLimit;
	Spinner monthSpinner;
	SharedPreferences sp;
	int limit, spent, balance;
	long notificationTime = 0;
	private Intent myIntent;
	private PendingIntent pendingIntent;
	private AlarmManager alarmManager;
	 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start);
		sp = PreferenceManager.getDefaultSharedPreferences(this);
		etLimitSum = (EditText)findViewById(R.id.etLimitSumToSet);
		limit = sp.getInt("LIMIT", 0);
		etLimitSum.setText("" + limit);
		btSetLimit = (Button)findViewById(R.id.btSetLimit);
		btSetLimit.setOnClickListener(this);
		//set months spinner
		setUpSpinner();
		//set transactions text views
		setUpTextViews();
		//set notifications check boxes
		setUpCheckboxes();
		myIntent = new Intent(StartActivity.this , AlarmService.class);     
        alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        pendingIntent = PendingIntent.getService(StartActivity.this, 0, myIntent, 0);
		
	}

	private void setUpSpinner() {
		// TODO Auto-generated method stub
		monthSpinner = (Spinner)findViewById(R.id.spMonths);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
		        R.array.months_count, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		monthSpinner.setAdapter(adapter);
		int pos = sp.getInt("POSITION", 0);
		monthSpinner.setSelection(pos);
		monthSpinner.setOnItemSelectedListener(this);
	}

	private void setUpCheckboxes() {
		// TODO Auto-generated method stub
		
		cbOverTheLimit = (CheckBox)findViewById(R.id.cbOverTheLimit);
		cbDailyBalance = (CheckBox)findViewById(R.id.cbBalanceMessage);
		boolean notiOverLimit = sp.getBoolean("DoNotifyOverLimit", false);
		cbOverTheLimit.setChecked(notiOverLimit);
		boolean notiDailyBallance = sp.getBoolean("DoNotifyDaily", false);
		cbDailyBalance.setChecked(notiDailyBallance);

		cbOverTheLimit.setOnCheckedChangeListener(this);
		cbDailyBalance.setOnCheckedChangeListener(this);
		if(limit <= 0)
		{
			cbOverTheLimit.setEnabled(false);
		}
	}

	private void setUpTextViews() {
		// TODO Auto-generated method stub
		tvSpent = (TextView)findViewById(R.id.tvStartSpent);
		tvLimit = (TextView)findViewById(R.id.tvStartLimit);
		tvBalance = (TextView)findViewById(R.id.tvStartBalance);
		
		limit = sp.getInt("LIMIT", 0);
		if(limit > 0)
		{
		tvLimit.setText("" + limit);
		}
		else
		{
			tvLimit.setText("not defined");
		}
		
		PaymentsDB info = new PaymentsDB(getApplicationContext());
		info.open();
		spent = info.lastMonthSum();
		info.close();
		tvSpent.setText("" + spent);
		
		int balance;
		if(limit > 0)
		{
			balance = limit - spent;
			tvBalance.setText("" + balance);
		}
		else
		{
			tvBalance.setText("not defined");
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.start, menu);
		return super.onCreateOptionsMenu(menu);
		//return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
		Intent intent;
		Log.i("MENU", "before switch");
	    switch (item.getItemId()) {
	        case R.id.action_buy:
	        	Log.i("MENU", "before firing intent");
	        	intent = new Intent(StartActivity.this, BuyActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	        	startActivity(intent);
	            return true;
	        case R.id.action_edit:
	        	intent = new Intent(StartActivity.this, EditActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	        	startActivity(intent);
	            return true;
	        case R.id.action_info:
	        	intent = new Intent(StartActivity.this, StatisticsActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	        	startActivity(intent);
	        	return true;
	        /*case R.id.action_exit:
	        	finish();*/
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}

	@Override
	public void onCheckedChanged(CompoundButton checkbox, boolean isChecked) {
		// TODO Auto-generated method stub
		switch(checkbox.getId())
		{
		case R.id.cbOverTheLimit:
			Editor edit = sp.edit();
			if(isChecked)
			{
				edit.putBoolean("DoNotifyOverLimit", true);
				startService(new Intent(StartActivity.this, LimitNotificationService.class));
			}
			else
			{
				edit.putBoolean("DoNotifyOverLimit", false);
				stopService(new Intent(StartActivity.this, LimitNotificationService.class));
			}
			edit.commit();
			break;
		case R.id.cbBalanceMessage:
			Editor edit1 = sp.edit();
			if(isChecked)
			{
				showTimePickerDialog();
		        Log.i("In SWITCH-CASE", "After showDialog");
				edit1.putBoolean("DoNotifyDaily", true);
			}
			else
			{
				alarmManager.cancel(pendingIntent);
				edit1.putBoolean("DoNotifyDaily", false);	
			}
			edit1.commit();
			break;
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v.getId() == R.id.btSetLimit){
			String limitStr = etLimitSum.getText().toString();
			if(!limitStr.equals(""))
			{
				limit = Integer.parseInt(limitStr);
				Editor edit = sp.edit();
				edit.putInt("LIMIT", limit);
				balance = limit - spent;
				edit.putInt("BALANCE", balance);
				edit.commit();
				tvLimit.setText("" + limit);
				if(!cbOverTheLimit.isEnabled())
				{
					cbOverTheLimit.setEnabled(true);
				}
				tvBalance.setText("" + balance);
			}
		}
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int pos,
			long id) {
		// TODO Auto-generated method stub
		if (parent.getId() == R.id.spMonths)
		{
			String monthStr = (String)parent.getItemAtPosition(pos);
			int months = Integer.parseInt(monthStr);
			Editor edit = sp.edit();
			edit.putInt("MONTHS", months);
			Log.i("SPINNER", "No of month is "+ months);
			edit.putInt("POSITION", pos);
			Log.i("SPINNER", "No of position in list is " + pos);
			edit.commit();
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
		
	}

	public void showTimePickerDialog() {
	    DialogFragment newFragment = new TimePickerFragment();
	    newFragment.show(getFragmentManager(), "TimePickerFragment");
	}

	@Override
	public void onComplete(long time) {
		// TODO Auto-generated method stub
		notificationTime = time;
		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, notificationTime, 24*60*60*1000 , pendingIntent);
		Log.i("in OnComplete method", "noti time is "+ notificationTime);
		Toast.makeText(getApplicationContext(), "Notification set", Toast.LENGTH_LONG).show();
	}


}
