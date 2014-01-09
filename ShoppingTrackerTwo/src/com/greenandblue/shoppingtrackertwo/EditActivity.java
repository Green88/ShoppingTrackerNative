package com.greenandblue.shoppingtrackertwo;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.SimpleCursorAdapter.ViewBinder;
import android.widget.TextView;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;

public class EditActivity extends Activity {

	private PaymentsDB db;
	private SimpleCursorAdapter dataAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit);
		
		db = new PaymentsDB(EditActivity.this);
		db.open();
		
		showEditList();
	}
	
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		db.close();
	}


	private void showEditList() {
		// TODO Auto-generated method stub
		Cursor cursor = db.getAllRecords();
		
		String[] columns = new String[] {
			  PaymentsDB.KEY_DATE,
			  PaymentsDB.KEY_PRODUCT,
			  PaymentsDB.KEY_NAME,
			  PaymentsDB.KEY_SUM
			  };
		int[] to = new int[] { R.id.tvDate, R.id.tvCategory, R.id.tvProduct, R.id.tvSum};

		dataAdapter = new SimpleCursorAdapter(this, R.layout.edit_list, cursor, columns, to, 0);
		dataAdapter.setViewBinder(new ViewBinder() {

		    public boolean setViewValue(View view, Cursor cursor, int columnIndex) {

		        if (columnIndex == 6) {
		                long createDate = cursor.getLong(columnIndex);
		                Calendar cal = Calendar.getInstance();
		                cal.setTimeInMillis(createDate);

		                String format = "dd/M/yyyy h:mm a";
		                SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);
		                String dateString = sdf.format(cal.getTime());
		                TextView textView = (TextView) view;
		                textView.setText(dateString);
		                return true;
		         }

		         return false;
		    }
		});
		
		
		ListView editList = (ListView) findViewById(R.id.lvEditList);
		editList.setAdapter(dataAdapter);
		
		editList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> listView, View view, int position,
					long id) {
				// TODO Auto-generated method stub
				Cursor cursor = (Cursor) listView.getItemAtPosition(position);
				
				//editing activity taking a date as extra
				Intent intent = new Intent(EditActivity.this, CompleteEditActivity.class);
				intent.putExtra("EntryDate", cursor.getLong(cursor.getColumnIndexOrThrow("date")));
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				
				startActivity(intent);
			}
			
		});
	}

}
