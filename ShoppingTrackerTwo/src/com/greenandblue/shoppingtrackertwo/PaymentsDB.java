package com.greenandblue.shoppingtrackertwo;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class PaymentsDB {
	
	public static final String KEY_ROWID = "_id";
	public static final String KEY_PAYMENT = "payment_type";
	public static final String KEY_PRODUCT = "product_type";
	public static final String KEY_NAME = "product_name";
	public static final String KEY_SUM = "sum";
	public static final String KEY_REMARK = "remark";
	public static final String KEY_DATE = "date";
	
	public static final String DB_NAME = "ShoppingDB";
	public static final String DB_TABLE = "PaymentsTable";
	public static final int DB_VERSION = 1;
	private static final SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
	
	private DBHelper paymentHelper;
	private final Context myContext;
	private SQLiteDatabase paymentDatabase;
	
	/**
	 * This is helper for managing DB: create and upgrade
	 */
	private static class DBHelper extends SQLiteOpenHelper{

		public DBHelper(Context context) {
			super(context, DB_NAME, null, DB_VERSION);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			// TODO Auto-generated method stub
			db.execSQL("CREATE TABLE " + DB_TABLE + " (" + 
			KEY_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
					KEY_PAYMENT + " TEXT NOT NULL, " + 
					KEY_PRODUCT + " TEXT NOT NULL, " +
					KEY_NAME + " TEXT NOT NULL, " +
					KEY_SUM + " INTEGER NOT NULL, " + 
					KEY_REMARK + " TEXT NOT NULL, " +
					KEY_DATE + " INTEGER NOT NULL);"
					);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
			db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE);
			onCreate(db);
		}
	}
	
	/**
	 * constructor
	 */
	public PaymentsDB(Context c)
	{
		myContext = c;
	}
	
	/**
	 * this is for writable DB opening
	 * @return DB
	 * @throws SQLException
	 */
	public PaymentsDB open() throws SQLException
	{
		paymentHelper = new DBHelper(myContext);
		paymentDatabase = paymentHelper.getWritableDatabase();
		Log.i("MYBDTAG", "db opened");
		return this;
	}
	
	/**
	 * This closes open DB
	 */
	public void close()
	{
		paymentHelper.close();
		Log.i("MYBDTAG", "db closed");
	}
	
	/**
	 * This puts table values in a new row
	 */
	public long createEntry(Entry entry) {
		ContentValues cv = new ContentValues();
		cv.put(KEY_PAYMENT, entry.getPaymentMethod());
		cv.put(KEY_PRODUCT, entry.getProductCategory());
		cv.put(KEY_NAME, entry.getProductName());
		cv.put(KEY_SUM, entry.getSum());
		cv.put(KEY_REMARK, entry.getRemark());
		cv.put(KEY_DATE, entry.getDate());
		return paymentDatabase.insert(DB_TABLE, null, cv);	
	}
	
	
	/**
	 * this is to update table entry
	 * @param Entry
	 */
	public void updateEntry(Entry entry) {
		// TODO Auto-generated method stub
		ContentValues cvUpdate = new ContentValues();
		cvUpdate.put(KEY_PAYMENT, entry.getPaymentMethod());
		cvUpdate.put(KEY_PRODUCT, entry.getProductCategory());
		cvUpdate.put(KEY_NAME, entry.getProductName());
		cvUpdate.put(KEY_SUM, entry.getSum());
		cvUpdate.put(KEY_REMARK, entry.getRemark());
		paymentDatabase.update(DB_TABLE, cvUpdate, KEY_DATE + "=" + entry.getDate(), null);
	}
	
	/**
	 * this is to delete table entry, search with date column
	 * @param date as long
	 */
	public void deleteEntry(long date) {
		// TODO Auto-generated method stub
		paymentDatabase.delete(DB_TABLE, KEY_DATE + "=" + date, null);
	}
	
	
	/**
	 * this is to get sum of sum values for current month entries
	 * from 1 in current month to the current date
	 * @return sum as int
	 */
	public int lastMonthSum()
	{
		int sum = 0;
		Calendar cal = Calendar.getInstance();
	    cal.set(Calendar.DAY_OF_MONTH, 1);
	    cal.set(Calendar.HOUR_OF_DAY, 0);
	    cal.set(Calendar.MINUTE, 0);
	    cal.set(Calendar.SECOND, 0);
	    long startOfMonth = cal.getTimeInMillis();
	    String sql = "SELECT SUM(" + KEY_SUM + ") FROM " + DB_TABLE + " WHERE " + KEY_DATE + ">"+ startOfMonth;
	    Cursor cursor = paymentDatabase.rawQuery(sql, null);
	    if(cursor.moveToFirst()) {
	    	sum = cursor.getInt(0);
	    	}
		
		return sum;
	}
	
	/**
	 * this is to get sums grouped by product for current month
	 * used by js functionality of Highcharts
	 * @return string of sums separated with "|"
	 */
	public String monthlySums(int month)
	{
		String str = "";
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, -month);
		long monthAgo = cal.getTimeInMillis();
		
		String sql = "SELECT " + KEY_PRODUCT + ", SUM(" + KEY_SUM + ") FROM " + DB_TABLE; 
		sql += " WHERE " + KEY_DATE + ">" + monthAgo + " GROUP BY " + KEY_PRODUCT;
		Cursor cursor = paymentDatabase.rawQuery(sql, null);
		int iSum = cursor.getColumnIndex("SUM(" + KEY_SUM + ")");
		for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext())
		{
			str+= cursor.getInt(iSum);
			str+="|";
		}
		str = str.substring(0, str.length()-1);
		return str;
	}
	
	/**
	 * this is to get products grouped by product for current month
	 * used by js functionality of Highcharts
	 * @return string of products
	 */
	public String monthlyProducts(int month)
	{
		String str = "";
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, -month);
		long monthAgo = cal.getTimeInMillis();
		
		String sql = "SELECT " + KEY_PRODUCT + ", SUM(" + KEY_SUM + ") FROM " + DB_TABLE;
		sql += " WHERE " + KEY_DATE + ">" + monthAgo + " GROUP BY " + KEY_PRODUCT;
		Cursor cursor = paymentDatabase.rawQuery(sql, null);
		int iProduct = cursor.getColumnIndex(KEY_PRODUCT);
		for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext())
		{
			str+= cursor.getString(iProduct); 
			str+="|";
		}
		str = str.substring(0, str.length()-1);
		return str;
	}
	
	/**
	 * gets all records from month ago till today,
	 * used by EditActivity
	 * @return Cursor
	 */
	public Cursor getAllRecords()
	{
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, -1);
		long monthAgo = cal.getTimeInMillis();
		
		String[] columns = new String[]{KEY_ROWID, KEY_PAYMENT, KEY_PRODUCT, KEY_NAME, KEY_SUM, KEY_REMARK, KEY_DATE};
		Cursor cursor = paymentDatabase.query(DB_TABLE, columns, KEY_DATE + ">" + monthAgo, null, null, null, null);
		if (cursor != null) {
			cursor.moveToFirst();
		}
		return cursor;
	}
	
	/**
	 * find specific entry in the table by date identifier
	 * @param date as long 
	 * @return Entry
	 */
	public Entry findEntryByDate(long date)
	{
		Entry entry = new Entry();
		String[] columns = new String[]{KEY_ROWID, KEY_PAYMENT, KEY_PRODUCT, KEY_NAME, KEY_SUM, KEY_REMARK, KEY_DATE};
		Cursor cursor = paymentDatabase.query(DB_TABLE, columns, KEY_DATE + "=" + date, null, null, null, null);
		if(cursor != null)
		{
			cursor.moveToFirst();
			entry.setPaymentMethod(cursor.getString(1));
			entry.setProductCategory(cursor.getString(2));
			entry.setProductName(cursor.getString(3));
			entry.setSum(cursor.getInt(4));
			entry.setRemark(cursor.getString(5));
			entry.setDate(cursor.getLong(6));
		}
		return entry;
	}
	
	/**
	 * TO DELETE
	 * This is to get all entries in the table
	 * used as help function during application debug
	 * @return all table as a String
	 */
	public String getData() {
		String[] columns = new String[]{KEY_ROWID, KEY_PAYMENT, KEY_NAME, KEY_PRODUCT, KEY_SUM, KEY_REMARK, KEY_DATE};
		Cursor cursor = paymentDatabase.query(DB_TABLE, columns, null, null, null, null, null);
		String result = "";
		Calendar cal;
		int iRow = cursor.getColumnIndex(KEY_ROWID);
		int iPay = cursor.getColumnIndex(KEY_PAYMENT);
		int iProduct = cursor.getColumnIndex(KEY_PRODUCT);
		int iName = cursor.getColumnIndex(KEY_NAME);
		int iSum = cursor.getColumnIndex(KEY_SUM);
		int iRemark = cursor.getColumnIndex(KEY_REMARK);
		int iDate = cursor.getColumnIndex(KEY_DATE);
		for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext())
		{
			long lng = cursor.getLong(iDate);
			cal = Calendar.getInstance();
	        cal.setTimeInMillis(lng);
			result = result + cursor.getString(iRow) + "| " + cursor.getString(iPay)+ "| "+ cursor.getString(iProduct) + "| " + cursor.getString(iName)+"| "+ cursor.getInt(iSum) + "| " + cursor.getString(iRemark) + "| " + formatter.format(cal.getTime()) +"<br>";
		}
		return result;
	}
	

}
