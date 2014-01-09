package com.greenandblue.shoppingtrackertwo;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Toast;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;

public class CompleteEditActivity extends Activity implements OnItemSelectedListener, OnClickListener{

	private long date;
	private PaymentsDB db;
	private Entry entry;
	Button btSaveChanges, btDeleteEntry;
	EditText etEditName, etEditSum, etEditRemarks;
	Spinner spEditPayment, spEditProduct;
	TextView tvDate;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_complete_edit);
		
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
		    date = extras.getLong("EntryDate");
		}
		
		db = new PaymentsDB(CompleteEditActivity.this);
		db.open();
		entry = db.findEntryByDate(date);
				
		tvDate = (TextView) findViewById(R.id.tvDate);
		Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(entry.getDate());
        String format = "dd/M/yyyy h:mm a";
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);
        String dateString = sdf.format(cal.getTime());
        tvDate.setText(dateString);
		
		btDeleteEntry = (Button) findViewById(R.id.btDeleteEntry);
		btSaveChanges = (Button) findViewById(R.id.btSaveChanges);
		btDeleteEntry.setOnClickListener(this);
		btSaveChanges.setOnClickListener(this);
		
		etEditName = (EditText)findViewById(R.id.etEditName);
		etEditName.setText(entry.getProductName());
		
		etEditSum = (EditText) findViewById(R.id.etEditSum);
		etEditSum.setText("" + entry.getSum());
		
		etEditRemarks = (EditText) findViewById(R.id.etEditRemark);
		etEditRemarks.setText(entry.getRemark());
		
		spEditPayment = (Spinner) findViewById(R.id.spEditPayment);
		ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this,
		        R.array.payment_array, android.R.layout.simple_spinner_item);
		adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spEditPayment.setAdapter(adapter1);
		int paymentPosition = adapter1.getPosition(entry.getPaymentMethod());
		spEditPayment.setSelection(paymentPosition);
		
		spEditProduct = (Spinner) findViewById(R.id.spEditProduct);
		ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,
		        R.array.product_type_array, android.R.layout.simple_spinner_item);
		adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spEditProduct.setAdapter(adapter2);
		int productPosition = adapter2.getPosition(entry.getProductCategory());
		spEditProduct.setSelection(productPosition);
		
		spEditPayment.setOnItemSelectedListener(this);
		spEditProduct.setOnItemSelectedListener(this);
		
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent;
		switch(v.getId())
		{
		case R.id.btSaveChanges:
			if(etEditName.getText().toString()!="")
			{
				entry.setProductName(etEditName.getText().toString());
			}
			if(etEditSum.getText().toString()!="")
			{
				entry.setSum(Integer.parseInt(etEditSum.getText().toString()));
			}
			if(etEditRemarks.getText().toString()!="")
			{
				entry.setRemark(etEditRemarks.getText().toString());
			}
			db.updateEntry(entry);
			intent = new Intent(CompleteEditActivity.this, StartActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			break;
			
		case R.id.btDeleteEntry:
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

			alertDialogBuilder.setTitle("Delete entry");

			alertDialogBuilder
			.setMessage("Delete the entry permanently?")
			.setCancelable(false)
			.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int id) {
					// if this button is clicked, close
					// current activity
					db.deleteEntry(entry.getDate());
					Toast.makeText(getApplicationContext(), "Deleted", Toast.LENGTH_SHORT).show();
					Intent i = new Intent(CompleteEditActivity.this, StartActivity.class);
					startActivity(i);
				}
			})
			.setNegativeButton("No",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int id) {
					// if this button is clicked, just close
					// the dialog box and do nothing
					dialog.cancel();
				}
			});
			
			// create alert dialog
			AlertDialog alertDialog = alertDialogBuilder.create();

			// show it
			alertDialog.show();
			break;
		}
		
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int pos,
			long id) {
		// TODO Auto-generated method stub
		switch(parent.getId())
		{
		case R.id.spEditPayment:
			entry.setPaymentMethod((String)parent.getItemAtPosition(pos));
			break;
		case R.id.spEditProduct:
			entry.setProductCategory((String)parent.getItemAtPosition(pos));
			break;
		}	
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		db.close();
	}
	
	

}
