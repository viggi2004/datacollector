package com.columbusagain.citibytes.helper;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.columbusagain.citibytes.R;
import com.columbusagain.citibytes.datacollection.BusinessCategoryFragment;

import android.content.Context;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class BusinessAttributes {
	
	private ArrayList<Attribute> attributes = new ArrayList<Attribute>();
	
	private JSONObject photoJson = new JSONObject();
	
	public boolean isPhotoUiSchema = false;
	
	public static BusinessAttributes init(JSONArray schema_arr,AttributeOnClickListener listener) throws JSONException{
		BusinessAttributes businessAttributes = new BusinessAttributes();
		
		//JSONArray attrArray = jsonObj.names();
		for (int i = 0; i < schema_arr.length(); i++) {
			JSONObject object = schema_arr.getJSONObject(i);
			String key = object.getString("attribute_name");
			Attribute attribute = businessAttributes.createAttribute(object,key,listener);
			businessAttributes.addAtribute(attribute);
		}
		return businessAttributes;
	}
	
	public void render(LinearLayout viewGroup,boolean isPhotoUi,Context context,Fragment parentFragment){
		Attribute attribute = null;
		for (int i = 0; i < attributes.size(); i++) {
			attribute = attributes.get(i);
			if(!isPhotoUi){
			if(!attribute.uiType.equalsIgnoreCase("photo")){
				TextView display_text = getDisplayTextView(context);
				display_text.setText(attribute.getDisplayText());
				if(attribute.isMandatory)
					display_text.append(" (*) ");
				viewGroup.addView(display_text);
				viewGroup.addView(attribute.render(context,parentFragment));
			}
			}else{
			if(attribute.uiType.equalsIgnoreCase("photo")){
				TextView display_text = getDisplayTextView(context);
				display_text.setText(attribute.getDisplayText());
				if(attribute.isMandatory)
					display_text.append(" (*) ");
				viewGroup.addView(display_text);
				viewGroup.addView(attribute.render(context,parentFragment));
			}
			}
			
		}
		
	}
	
	private TextView getDisplayTextView(Context context){
		LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = (View) inflater.inflate(R.layout.atttribute_display_text, null);
		
		return (TextView)view;
		
	}
	
	private Attribute createAttribute(JSONObject jsonObj,String key,AttributeOnClickListener listener) throws JSONException
	{
		String ui = jsonObj.getString("ui");
		if("radiobutton".equalsIgnoreCase(ui) )
				return new RadioButtonAttribute(jsonObj,key,listener);			
		else if("single_selection_listview".equals(ui))
			return new SingleValueListViewAttribute(jsonObj, key,listener);
		else if("multi_selection_listview".equals(ui))
			return new MultiValueListViewAttribute(jsonObj, key,listener);
		else if("checkbox".equalsIgnoreCase(ui))
			return new CheckBoxAttribute(jsonObj,key,listener);
		else if("textbox".equalsIgnoreCase(ui))
			return new TextBoxAttribute(jsonObj,key,listener);
		else if("photo".equalsIgnoreCase(ui))
			return new PhotoAttribute(jsonObj,key,listener);
			
		return null;
	}
	
	private void addAtribute(Attribute attribute)
	{
		if(attribute == null)
			return;
		this.attributes.add(attribute);
	}
	
	public boolean isValidInput(Context context,boolean isPhotoUi){
		Attribute attribute = null;
		for (int i = 0; i < attributes.size(); i++) {
			attribute = attributes.get(i);
			
			if(!isPhotoUi){
				if(!attribute.uiType.equalsIgnoreCase("photo")){
					if(attribute.isMandatory && !attribute.isValid(context))
						return false;
				}
				}else{
				if(attribute.uiType.equalsIgnoreCase("photo")){
					if(attribute.isMandatory && !attribute.isValid(context))
						return false;
				}
				}
			
		}
		return true;
	}
	
	public void buildJson(JSONObject jsonObject,boolean isPhotoUi){
		Attribute attribute = null;
		for (int i = 0; i < attributes.size(); i++) {
			attribute = attributes.get(i);
			if(!isPhotoUi){
				if(!attribute.uiType.equalsIgnoreCase("photo")){
					attribute.buildJson(jsonObject);
				}
				}else{
					if(!attribute.uiType.equalsIgnoreCase("photo")){
						attribute.buildJson(jsonObject);
					}
					
				}
			
		
		}
	}
	
	public void setViewModeUi(boolean isViewMode){
		Log.i("BusinessAttributes", "set view  mode ui called");
		Attribute attribute = null;
		for (int i = 0; i < attributes.size(); i++) {
			attribute = attributes.get(i);
			if(attribute instanceof PhotoAttribute){
					((PhotoAttribute) attribute).viewModeSetup(isViewMode);
				Log.i("BusinessAttributes", "PhotoAttribute called");
			}
		}
	}
	
	public void setActionModeUi(){
		Attribute attribute = null;
		for (int i = 0; i < attributes.size(); i++) {
			attribute = attributes.get(i);
			if(attribute instanceof PhotoAttribute){
				((PhotoAttribute) attribute).enableCheckBox();
			}
		}
	}
	
	public void setPhotos(Uri uri){
		Attribute attribute = null;
		for (int i = 0; i < attributes.size(); i++) {
			attribute = attributes.get(i);
			if(attribute instanceof PhotoAttribute){
				((PhotoAttribute) attribute).setPhotos(uri);
			}
		}
	}
	
	public void disableCheckBoxes(){
		Attribute attribute = null;
		for (int i = 0; i < attributes.size(); i++) {
			attribute = attributes.get(i);
			if(attribute instanceof PhotoAttribute){
				((PhotoAttribute) attribute).disableCheckBox();
			}
		}
	}
	public void deleteImages(){
		Attribute attribute = null;
		for (int i = 0; i < attributes.size(); i++) {
			attribute = attributes.get(i);
			if(attribute instanceof PhotoAttribute){
				((PhotoAttribute) attribute).deleteSelectedImages();
			}
		}
	}
	public void getPhotoUri(JSONArray jsonArray){
		Attribute attribute = null;
		for (int i = 0; i < attributes.size(); i++) {
			attribute = attributes.get(i);
			if(attribute instanceof PhotoAttribute){
				((PhotoAttribute) attribute).addImageUri(jsonArray);
			}
		}
	}
	public void getPhotoJson(){
		if(photoJson != null)
		Log.i("BusinessAttributes", photoJson.toString());
	}
	
	public static BusinessAttributes initForPhoto(JSONObject jsonObj,AttributeOnClickListener listener) throws JSONException{
		BusinessAttributes businessAttributes = new BusinessAttributes();
		JSONArray attrArray = jsonObj.names();
		
		return null;
	}
	
	public void populateData(JSONObject jsonObject,LinearLayout container,Context context) throws JSONException{
		Attribute attribute = null;
		for (int i = 0; i < attributes.size(); i++) {
			attribute = attributes.get(i);
			attribute.populateData(jsonObject,container,context);
		}
	}
	
	public void disableEdit(){
		
		Attribute attribute = null;
		for (int i = 0; i < attributes.size(); i++) {
			attribute = attributes.get(i);
			attribute.disableEdit();
		}
		
	}
	


}
