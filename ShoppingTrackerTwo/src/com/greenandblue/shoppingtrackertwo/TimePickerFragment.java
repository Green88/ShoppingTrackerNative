package com.greenandblue.shoppingtrackertwo;

import java.util.Calendar;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TimePicker;

public class TimePickerFragment extends DialogFragment implements OnTimeSetListener{
	
	public interface OnCompletePickerListener {
		
		public void onComplete(long time);
	}

	private OnCompletePickerListener listener;
	private Calendar c;
	
	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }
	
	@Override
	public void onTimeSet(TimePicker view, int selectedHour, int selectedMinute) {
		// TODO Auto-generated method stub
		
		c.set(Calendar.HOUR_OF_DAY, selectedHour);
	    c.set(Calendar.MINUTE, selectedMinute);
	    long notificationTime = c.getTimeInMillis();
	    this.listener.onComplete(notificationTime);
		
	}

	public void onAttach(Activity activity) {
		super.onAttach(activity);
	    try {
	        this.listener = (OnCompletePickerListener)activity;
	    }
	    catch (final ClassCastException e) {
	        throw new ClassCastException(activity.toString() + " must implement OnCompleteListener");
	    }
	}
}
