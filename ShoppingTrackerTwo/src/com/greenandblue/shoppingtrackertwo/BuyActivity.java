package com.greenandblue.shoppingtrackertwo;

import java.util.Calendar;

import android.os.Bundle;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class BuyActivity extends Activity implements OnItemSelectedListener{
	
	Button btBuySave;
	EditText etProductName, etSum, etRemarks;
	Spinner spPayment, spProductType;
	//TextView tvResult;
	Entry purchaseEntry;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_buy);
		
		purchaseEntry = new Entry();
		//tvResult = (TextView) findViewById(R.id.tvResult);
		btBuySave = (Button) findViewById(R.id.btSaveBuy);
		etProductName = (EditText) findViewById(R.id.etProductName);
		etSum = (EditText) findViewById(R.id.etSum);
		etRemarks = (EditText) findViewById(R.id.etRemarks);
		
		spPayment = (Spinner) findViewById(R.id.spPayment);
		ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this,
		        R.array.payment_array, android.R.layout.simple_spinner_item);
		adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spPayment.setAdapter(adapter1);
		
		spProductType = (Spinner) findViewById(R.id.spProduct);
		ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,
		        R.array.product_type_array, android.R.layout.simple_spinner_item);
		adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spProductType.setAdapter(adapter2);
		
		
		spPayment.setOnItemSelectedListener(this);
		spProductType.setOnItemSelectedListener(this);
		
		btBuySave.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				boolean didItWork = true;
				purchaseEntry.setProductName(etProductName.getText().toString());
				purchaseEntry.setSum(Integer.parseInt(etSum.getText().toString()));
				purchaseEntry.setRemark(etRemarks.getText().toString());
				Calendar rightNow = Calendar.getInstance();
				long milli = rightNow.getTimeInMillis();
				purchaseEntry.setDate(milli);
				try{
					PaymentsDB db = new PaymentsDB(BuyActivity.this);
					db.open();
					db.createEntry(purchaseEntry);
					db.close();
					startService(new Intent(BuyActivity.this, LimitNotificationService.class));
				}
				catch(Exception e)
				{
					didItWork = false;
					String error = e.toString();
					Dialog d = new Dialog(BuyActivity.this);
					d.setTitle("Payment not added, try later");
					TextView tv = new TextView(BuyActivity.this);
					tv.setText(error);
					d.setContentView(tv);
					d.show();
				}
				finally{
					if(didItWork)
					{
						Toast toast = Toast.makeText(getApplicationContext(), "Payment added successfully", Toast.LENGTH_LONG);
						toast.show();
						startActivity(new Intent(BuyActivity.this, StartActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
						//finish();
					}
				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.buy, menu);
		return true;
	}

	@Override 
	public void onItemSelected(AdapterView<?> parent, View view, int pos,
			long id) {
		// TODO Auto-generated method stub
		switch(parent.getId())
		{
		case R.id.spPayment:
			purchaseEntry.setPaymentMethod((String)parent.getItemAtPosition(pos));
			break;
		case R.id.spProduct:
			purchaseEntry.setProductCategory((String)parent.getItemAtPosition(pos));
			break;
		}	
		
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
		
	}

}
