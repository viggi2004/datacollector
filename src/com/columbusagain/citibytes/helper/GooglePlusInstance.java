package com.columbusagain.citibytes.helper;

import com.google.android.gms.plus.PlusClient;

public class GooglePlusInstance {
	
	private static PlusClient mPlusClient;
	
	private GooglePlusInstance(){
		//this.mPlusClient = null;
	}
	
	public static PlusClient getInstance(PlusClient plusClient){
		if(mPlusClient == null){
			//new GooglePlusInstance();
			mPlusClient = plusClient;
		}
		
		
		return mPlusClient;
	}
	
	public static void savePlusClientInstance(PlusClient plusClient){
		mPlusClient = plusClient;
	}
}
