package com.columbusagain.citibytes;

import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;

import android.app.Application;

@ReportsCrashes(
        formKey = "",
        formUri = "http://dev.citibytes.com:5985/acra-citibytes/_design/acra-storage/_update/report",
        reportType = org.acra.sender.HttpSender.Type.JSON,
        httpMethod = org.acra.sender.HttpSender.Method.PUT,
        formUriBasicAuthLogin="reportuser",
        formUriBasicAuthPassword="columbus"
       
        )

public class CitiBytesApplication extends Application{
	
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		   // The following line triggers the initialization of ACRA
       // ACRA.init(this);
	}

}
