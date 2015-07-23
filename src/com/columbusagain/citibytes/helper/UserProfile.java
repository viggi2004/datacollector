package com.columbusagain.citibytes.helper;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

public class UserProfile {

	public static String PROFILE_NAME ;
	
	public static Bitmap PROFILE_PICTURE;
	
	public static String EMAIL_ID;
	
	public static String CITY;
	
	public static String SELECTED_PIN;
	
	public static String STATE;
	
	public static String STD;
	
	public static boolean isFromMapScreen;
	
	public static String PERSONAL_NUMBER;
	
	public static String BUSINESS_NUMBER;
	
	public static boolean isAdmin;
	
	public static void clearData(){
		PROFILE_NAME = null;
		
		PROFILE_PICTURE = null;
		
		EMAIL_ID = null;
		
		CITY = null;
		
		STATE = null;
		
		STD = null;
		
		PERSONAL_NUMBER = null;
		
		BUSINESS_NUMBER = null;
		
	}
	
	
	
	

}
