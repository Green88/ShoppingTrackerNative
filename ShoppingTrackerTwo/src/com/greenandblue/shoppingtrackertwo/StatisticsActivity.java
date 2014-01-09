package com.greenandblue.shoppingtrackertwo;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.view.Menu;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

public class StatisticsActivity extends Activity {
	
	private static final String HTML_ROOT = "file:///android_asset/www/";
	private WebView webView = null;
	private JavascriptAdapter adapter;
	private SharedPreferences sp;
	private int month;
	
	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_info);
		sp = PreferenceManager.getDefaultSharedPreferences(this);
		month = sp.getInt("MONTH", 1);
		adapter = new JavascriptAdapter();
		webView = new WebView(this); 
        setContentView(webView);
        webView.getSettings().setJavaScriptEnabled(true); 
        webView.addJavascriptInterface(adapter, "ob");
        webView.loadUrl(HTML_ROOT + "barchart.html"); 
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.info, menu);
		return true;
	}
	
	public class JavascriptAdapter
	{
		@JavascriptInterface 
		public String sumStatistics()
		{
			PaymentsDB info = new PaymentsDB(StatisticsActivity.this);
			info.open();
			String data = info.monthlySums(month);
			info.close();

			return data;
		}

		/**
		 * works in highcharts/js functionality
		 * @return product types as a string
		 */
		@JavascriptInterface 
		public String prodStatistics()
		{
			PaymentsDB info = new PaymentsDB(StatisticsActivity.this);
			info.open();
			String data = info.monthlyProducts(month);
			info.close();

			return data;
		}
	}

}
