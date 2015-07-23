package com.columbusagain.citibytes.database;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class CitiBytesDB extends SQLiteOpenHelper {

	// Database Version
	private static final int DATABASE_VERSION = 3;

	// Database Name
	private static final String DATABASE_NAME = "citibytes";
	
	

	// Contacts table name
	
	private static final String TABLE_BUSINESS_DATAS = "business_datas";
	
	private static final String TABLE_TIME_SPENT_ON_BUSINESS = "spent_time";

	private static final String BUSINESS_ID = "business_id";
	private static final String BUSINESS_DATA = "business_data";
	private static final String IMAGES = "images";
	private static final String TIMER_DATA = "timer_data";
	

	
	private static final String CREATE_TABLE_BUSINESS_DATAS = "CREATE TABLE " + TABLE_BUSINESS_DATAS + "("+BUSINESS_ID+" TEXT PRIMARY KEY,"+IMAGES+" TEXT,"+BUSINESS_DATA + " TEXT);";
	
	private static final String CREATE_TABLE_TIMER_DATA = "CREATE TABLE "+TABLE_TIME_SPENT_ON_BUSINESS+"("+BUSINESS_ID+" TEXT,"+TIMER_DATA+" TEXT PRIMARY KEY);";

	public CitiBytesDB(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_TABLE_BUSINESS_DATAS);
		db.execSQL(CREATE_TABLE_TIMER_DATA);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_BUSINESS_DATAS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_TIME_SPENT_ON_BUSINESS);

		// Create tables again
		onCreate(db);

	}
	
	public void deleteTimerRow(String timer_data){
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_TIME_SPENT_ON_BUSINESS, TIMER_DATA + " = ?",
				new String[] { timer_data });
		db.close();
	}
	
	public void insertTimeCalculation(String businessId,String timer_data){
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(BUSINESS_ID, businessId);
		values.put(TIMER_DATA, timer_data);
		db.insert(TABLE_TIME_SPENT_ON_BUSINESS, null, values);
		db.close();
	}
	
	public HashMap<String,String> getAnalyticsData(String business_id){
		HashMap<String,String> analytics_data=new HashMap<String, String>();
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_TIME_SPENT_ON_BUSINESS, new String[] { BUSINESS_ID,TIMER_DATA},BUSINESS_ID + " = ?",
				new String[] { business_id }, null, null, null);
		if (cursor != null && cursor.moveToFirst()) {			
				analytics_data.put("business_id", cursor.getString(0));
				analytics_data.put("timer_data", cursor.getString(1));	
		}
		
		cursor.close();
		db.close();
		
		return analytics_data;
	}
	
	
	public void insertBusiness(String businessId,String jsonData,String imageDetails){
		SQLiteDatabase db = this.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put(BUSINESS_ID, businessId);
		values.put(BUSINESS_DATA, jsonData);
		values.put(IMAGES, imageDetails);
		db.insert(TABLE_BUSINESS_DATAS, null, values);
		
		
		db.close();
		
		
	}
	
	public String[] getBusinessDetails(String businessId){
		
		String[] result=new String[2];
		
		SQLiteDatabase db = this.getReadableDatabase();
		
		Cursor cursor = db.query(TABLE_BUSINESS_DATAS, new String[] { BUSINESS_ID,BUSINESS_DATA,IMAGES }, BUSINESS_ID + " = ?",
				new String[] { businessId }, null, null, null, null);
		
		if (cursor != null && cursor.moveToFirst()) {
			result[0] = cursor.getString(0);
			result[1] = cursor.getString(1);
			result[2] = cursor.getString(2);
			
		}
		
		cursor.close();
		db.close();
		
		
		
		return result;
	}
	
	
	
	public boolean isBusinessExist(String BusinessId){
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_BUSINESS_DATAS, new String[] { BUSINESS_ID}, null, null, null, null, null);
		
		if(cursor!=null && cursor.moveToFirst()){
			do{
				if(BusinessId.equals(cursor.getString(0))){
					cursor.close();
					db.close();
					return true;
				}
			}while(cursor.moveToNext());
		}
		
		cursor.close();
		db.close();
		
		return false;
		
	}
	
public ArrayList<HashMap<String,String>> getAllBusinessList(){
		
		ArrayList<HashMap<String,String>> businessDetails=new ArrayList<HashMap<String,String>>();
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_BUSINESS_DATAS, new String[] { BUSINESS_ID,BUSINESS_DATA,IMAGES}, null, null, null, null, null);
		if (cursor != null && cursor.moveToFirst()) {
			do{
				HashMap<String,String> businessData=new HashMap<String, String>();
				businessData.put("business_id", cursor.getString(0));
				businessData.put("business_data", cursor.getString(1));
				businessData.put("image_details", cursor.getString(2));
				businessDetails.add(businessData);
			}while(cursor.moveToNext());
			
			
		}
		
		cursor.close();
		db.close();
		
		return businessDetails;
		
	}
	
	

    public void updatePhotoDetails(String business_id,String image_data){
    	SQLiteDatabase db = this.getWritableDatabase();
	    
 	 
 	    ContentValues values = new ContentValues();
 	    values.put(IMAGES, image_data);
 	    db.update(TABLE_BUSINESS_DATAS, values, BUSINESS_ID + " = ?",
 				new String[] { business_id });
 	    db.close();
    }
	
	public void updateBusinessDetails(String businessId,String businessData,String imageData) {
	    SQLiteDatabase db = this.getWritableDatabase();
	    ContentValues values = new ContentValues();
	    values.put(BUSINESS_DATA, businessData);
	    values.put(IMAGES, imageData);
	    db.update(TABLE_BUSINESS_DATAS, values, BUSINESS_ID + " = ?",
				new String[] { businessId });
	    db.close();
	}
	
	
	
	
	public ArrayList<HashMap<String, String>> getAllData(){
		ArrayList<HashMap<String, String>> result_set=new ArrayList<HashMap<String, String>>();
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_BUSINESS_DATAS, new String[] { BUSINESS_ID,BUSINESS_DATA,IMAGES }, null, null, null, null, null);
		if (cursor != null && cursor.moveToFirst()) {
			do{
				HashMap<String,String> business_details=new HashMap<String,String>();
				business_details.put("business_id", cursor.getString(0));
				business_details.put("business_data", cursor.getString(1));
				business_details.put("image_details", cursor.getString(2));
				
				result_set.add(business_details);
			}while(cursor.moveToNext());
			
			
		}
		cursor.close();
		db.close();
		return result_set;
		
	}
	
	
	public void removeBsinessData(String businessData){
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_BUSINESS_DATAS, BUSINESS_DATA + " = ?",
				new String[] { businessData });
		db.close();
	}
	
	public void deleteRow(String businessId){
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_BUSINESS_DATAS, BUSINESS_ID + " = ?",
				new String[] { businessId });
		db.close();
		
	}
	
	

}
