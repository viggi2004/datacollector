package com.columbusagain.citibytes.helper;

import org.json.JSONException;
import org.json.JSONObject;

public class BusinessDetails {
	
	public String businessId;
	
	public String business_name;
	
	public String address;
	
	public String address_line_1;
	
	public String address_line_2;
	
	public String latitude;
	
	public String longitude;
	
	public String status;
	
	public BusinessDetails(JSONObject jsonObj,String key) throws JSONException{
		
		this.businessId = key;
		
		if(jsonObj.has("latitude"))		
		this.latitude = jsonObj.getString("latitude");
		if(jsonObj.has("longitude"))
		this.longitude = jsonObj.getString("longitude");
		if(jsonObj.has("status"))
		this.status = jsonObj.getString("status");
		if(jsonObj.has("address_line_1"))
		this.address_line_1 = jsonObj.getString("address_line_1");
		if(jsonObj.has("address_line_2"))
			this.address_line_2 = jsonObj.getString("address_line_2");
		if(jsonObj.has("business_name"))
		this.business_name = jsonObj.getString("business_name");
		
	}

}
