package com.columbusagain.citibytes.helper;

import java.util.Calendar;

/*
 * 
 */
public class Timer {
	
	private static int mSpentTime = 0;
	
	private static long  mStartTime;
	
	private static long mStopTime;
	
	public static void resetTimer(){
		mStartTime = 0;		
		mSpentTime = 0;
		startTimer();
	}
	
	public static void startTimer(){
		Calendar calendar  = Calendar.getInstance();		
		mStartTime = calendar.getTimeInMillis();
	}
	
	public static void stopTimer(){
		Calendar calendar  = Calendar.getInstance();		
		mStopTime = calendar.getTimeInMillis();
		calculateTimeDuration();
	}
	
	private static void calculateTimeDuration(){
		mSpentTime += mStopTime - mStartTime;
	}
	
	public static int getTotalTime(){
		stopTimer();
		return mSpentTime;
	}

}
