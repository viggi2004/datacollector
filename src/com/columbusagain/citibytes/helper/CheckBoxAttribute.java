package com.columbusagain.citibytes.helper;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.columbusagain.citibytes.CAActivity;
import com.columbusagain.citibytes.R;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.RadioButton;

public class CheckBoxAttribute extends Attribute implements OnCheckedChangeListener{
	
	private ArrayList<String> allowedValues = new ArrayList<String>();

	private String defaultValue;

	private int defaultValueIndex = -1;
	
	private JSONArray dependencyJson;
	
	private String activate_dependency_on;
	
	private LinearLayout mChildView;
	
	private BusinessAttributes mBusinessAttributes;
	
	private boolean isDependencyOn = false;
	
	private WeakReference<AttributeOnClickListener> mAttributeListener;
	
	private CheckBox[] check_box = {null,null,null};

	public CheckBoxAttribute(JSONObject jsonObj, String key,AttributeOnClickListener listener)
			throws JSONException {
		super(jsonObj, key);
		Log.i("CheckBoxAttribute", "CheckBoxAttribute Called");
		if(jsonObj.has("default_value"))
		this.defaultValue = jsonObj.getString("default_value");

		for (int i = 0; i < jsonObj.getJSONArray("allowed_values")
				.length(); i++)
			allowedValues.add(jsonObj.getJSONArray("allowed_values")
					.getString(i));
		if(defaultValue != null)
		this.defaultValueIndex = getDefaultIndex();
		try{
			this.dependencyJson = jsonObj.getJSONArray("dependencies");
			this.activate_dependency_on = jsonObj.getString("activate_dependency_on");
			}catch(JSONException e){
				
			}
		mAttributeListener = new WeakReference<AttributeOnClickListener>(listener);
		if(dependencyJson!=null)
		mBusinessAttributes = BusinessAttributes.init(dependencyJson,listener);	
	}

	@Override
	public View render(Context context,Fragment parentFragment) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.attribute_check_box, null);
		check_box[0] =	(CheckBox) view.findViewById(R.id.check_box_one);
		check_box[1] =	(CheckBox) view.findViewById(R.id.check_box_two);
		check_box[2] =	(CheckBox) view.findViewById(R.id.check_box_three);
		
		
		
		for (int i = 0; i < allowedValues.size(); i++) {
			String input_value = allowedValues.get(i);
			check_box[i].setText(input_value);
			if(activate_dependency_on!=null){				
				if(activate_dependency_on.equals(input_value)){					
					check_box[i].setOnCheckedChangeListener(this);
				}
			}
		}
		if(defaultValueIndex >0)
		check_box[defaultValueIndex].setChecked(true);
		
		if (allowedValues.size() > 2) 			
			check_box[2].setVisibility(RadioButton.VISIBLE);
		mChildView = new LinearLayout(context);
		mChildView.setOrientation(LinearLayout.VERTICAL);
		mChildView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT ));
		mChildView.setVisibility(LinearLayout.GONE);
		if(mBusinessAttributes!=null)
		mBusinessAttributes.render(mChildView,false, context,null);
		
		if(mChildView.getChildCount()>0)
			((LinearLayout)view).addView(mChildView);

		return view;
	}
	
	
	
	private int getDefaultIndex() {
		for (int i = 0; i < allowedValues.size(); i++) {
			String allowedValue = allowedValues.get(i);
			if (allowedValue.equalsIgnoreCase(defaultValue)) {
				return i;
			}
		}
		return 0;
	}

	@Override
	public boolean isValid(Context context) {
		if(!this.isMandatory)
			return true;
		boolean isValidInput = false;
		for (int i = 0; i < check_box.length; i++) {
			if(check_box[i].isChecked())
				isValidInput = true;
				//return false;			
		}
		if(isValidInput){
		if(mBusinessAttributes !=null && isDependencyOn)
			return mBusinessAttributes.isValidInput(context,false);
		}
		
		return isValidInput;
	}
	
	@Override
	public void buildJson(JSONObject josnObject) {
		JSONArray jsonArr = new JSONArray();
		for (int i = 0; i < check_box.length; i++) {
			if(check_box[i].isChecked())
				jsonArr.put(check_box[i].getText().toString());
				//
		
	}
		try {
			josnObject.put(this.name, jsonArr);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(mBusinessAttributes !=null && isDependencyOn)
			mBusinessAttributes.buildJson(josnObject,false);
	}
	
	@Override
	public void populateData(JSONObject jsonObject,LinearLayout container,Context context) throws JSONException {
		
		if(jsonObject.has(this.name)){
			JSONArray jsonArr = jsonObject.getJSONArray(this.name);
			showData(container,this.name,jsonArr,context);
			for (int i = 0; i < check_box.length; i++)
				for(int j=0;j<jsonArr.length();j++){
					if(check_box[i].getText().toString().equalsIgnoreCase(jsonArr.getString(i))){
						check_box[i].setChecked(true);
						break;
					}else
						check_box[i].setChecked(false);
				}
				
		}
		if(mBusinessAttributes !=null && isDependencyOn)
			mBusinessAttributes.populateData(jsonObject,container,context);
		
	}
	
	private void showData(LinearLayout container,String key,JSONArray value_arr,Context context) throws JSONException{
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.view_multivalue_business_category, null,false);
		TextView key_text_view = (TextView) view.findViewById(R.id.key);
		key_text_view.setText(key);
		LinearLayout value_layout = (LinearLayout) view.findViewById(R.id.value_layout);
		
		for (int i = 0; i < value_arr.length(); i++) {
			final View v = value_layout.getChildAt(i);
			if(v!=null){
				TextView view_text = (TextView)v.findViewById(R.id.text);
				view_text.setText(value_arr.getString(i));
			}else{
			Log.i("BasicDetailsFragment", "landmark added");
		final View viewlayout = inflater.inflate(R.layout.view_layout_text_view,null,false);
		TextView view_text = (TextView)viewlayout.findViewById(R.id.text);
		view_text.setText(value_arr.getString(i));
		value_layout.addView(viewlayout);
		}
		}
		container.addView(view);
	}
	@Override
	public void disableEdit(){
		for (int i = 0; i < check_box.length; i++) {
				check_box[i].setClickable(false);
		}
		
		if(mBusinessAttributes !=null && isDependencyOn)
			mBusinessAttributes.disableEdit();
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if(mChildView == null || activate_dependency_on == null)
			return;
		if(isChecked){
			isDependencyOn = true;
			mChildView.setVisibility(LinearLayout.VISIBLE);
		}else{
			isDependencyOn = false;
			mChildView.setVisibility(LinearLayout.GONE);
		}
	}

	

	

	

}
