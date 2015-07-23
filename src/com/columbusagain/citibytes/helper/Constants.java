package com.columbusagain.citibytes.helper;

import android.view.View;

public class Constants {
	
	public static String ACTION_BAR_TITLE = "action_bar_title";

	public static int SPLASH_TIMEOUT = 2000;
	
	public static String DEV_URL = "http://ec2-52-74-140-206.ap-southeast-1.compute.amazonaws.com/";
	
	public static String PROD_URL = "http://ec2-52-74-140-206.ap-southeast-1.compute.amazonaws.com/";
	
	public static String BASE_URL = PROD_URL;

	public static View view;
	
	public static int FLAG=0;
	
	public static String PIN;
	
	public static String AREA;	
	
	public static boolean isEditBusinessClicked = false;
	
	public static void clearData(){
		PIN = null;
		AREA = null;		
	}
	
	//public static ArrayList<String> CHAIN_NAMES = new ArrayList<String>();
	
	
	/*public static ArrayList<String> getChainNames(){
		CHAIN_NAMES = 
		
		return CHAIN_NAMES;
	}*/

}
