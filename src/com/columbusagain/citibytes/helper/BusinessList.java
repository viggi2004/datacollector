package com.columbusagain.citibytes.helper;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class BusinessList {
	
	private ArrayList<BusinessDetails> businesslist = new ArrayList<BusinessDetails>();
	
	public static BusinessList init(JSONObject jsonObj) throws JSONException{
		BusinessList businesslist = new BusinessList();
		
		JSONArray attrArray = jsonObj.names();
		for (int i = 0; i < attrArray.length(); i++) {
			String key = attrArray.getString(i);
			JSONObject object = jsonObj.getJSONObject(key);			
			BusinessDetails business = new BusinessDetails(object, key);
			businesslist.addBusiness(business);
		}
		
		
		return businesslist;
		
		
	}
	
	public ArrayList<BusinessDetails> getBusinessList(){
		return businesslist;
	}
	
	private void addBusiness(BusinessDetails business){
		if(business == null)
			return;
		this.businesslist.add(business);
	}

}
