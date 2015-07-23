package com.columbusagain.citibytes.helper;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.LinearLayout;

public abstract class Attribute {

	public String uiType;
	
	public boolean isMandatory;
	
	private String displayText;
	
	public boolean isMultiValued;
	
	public String name;
	
	public Attribute(JSONObject jsonObj,String key) throws JSONException
	{
		this.uiType = jsonObj.getString("ui");
		this.isMandatory = jsonObj.getBoolean("mandatory");
		this.displayText = jsonObj.getString("form_display_text");
		this.isMultiValued = jsonObj.getBoolean("mulitvalued");
		this.name = key;
	}
	
	public abstract View render(Context context,Fragment parentFragment);
	
	public abstract boolean isValid(Context context);
	
	public abstract void buildJson(JSONObject josnObject);
	
	public abstract void disableEdit();
	
	public abstract void populateData(JSONObject jsonObject,LinearLayout container,Context context) throws JSONException;
	
	public String getKey()
	{
		return name;
	}
	
	public String getDisplayText()
	{
		return displayText;
	}
	
	
}
