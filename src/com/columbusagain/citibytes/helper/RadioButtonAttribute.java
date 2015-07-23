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
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

public class RadioButtonAttribute extends Attribute implements OnCheckedChangeListener {

	private ArrayList<String> allowedValues = new ArrayList<String>();

	private String defaultValue;
	
	private JSONArray dependencyJson;
	
	private String activate_dependency_on;
	
	private BusinessAttributes mBusinessAttributes;
	
	private RadioButton[] radio_button = {null,null,null};
	
	private boolean isDependencyOn = false;
	
	private WeakReference<AttributeOnClickListener> mAttributeListener;
	
	private LinearLayout mChildView;
	
	//private int mViewId;

	private int defaultValueIndex = -1;

	public RadioButtonAttribute(JSONObject jsonobject,String key,AttributeOnClickListener listener) throws JSONException {
		super(jsonobject,key);
		Log.i("RadioButtonAttribute", "RadioButtonAttribute Called");
		if(jsonobject.has("default_value"))
		this.defaultValue = jsonobject.getString("default_value");

		for (int i = 0; i < jsonobject.getJSONArray("allowed_values")
				.length(); i++)
			allowedValues.add(jsonobject.getJSONArray("allowed_values")
					.getString(i));
		if(defaultValue != null)
		this.defaultValueIndex = getDefaultIndex();
		try{
		this.dependencyJson = jsonobject.getJSONArray("dependencies");
		this.activate_dependency_on = jsonobject.getString("activate_dependency_on");
		}catch(JSONException e){
			
		}
		
		mAttributeListener = new WeakReference<AttributeOnClickListener>(listener);
		if(dependencyJson !=null)
		mBusinessAttributes = BusinessAttributes.init(dependencyJson,listener);		

	}
	@Override
	public View render(Context context,Fragment parentFragment) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.attribute_radio_button, null);
		radio_button[0]=(RadioButton) view.findViewById(R.id.radio_button_one);
		radio_button[1]=(RadioButton) view.findViewById(R.id.radio_button_two);
		radio_button[2]=(RadioButton) view.findViewById(R.id.radio_button_three);
		
		for (int i = 0; i < allowedValues.size(); i++) {
			String input_value = allowedValues.get(i);
			radio_button[i].setText(input_value);
			if(activate_dependency_on!=null){				
				if(activate_dependency_on.equals(input_value)){					
					radio_button[i].setOnCheckedChangeListener(this);
				}
			}
		}
		if(defaultValueIndex>0)
		radio_button[defaultValueIndex].setChecked(true);
		
		if (allowedValues.size() > 2) 			
			radio_button[2].setVisibility(RadioButton.VISIBLE);
		mChildView = new LinearLayout(context);
		mChildView.setOrientation(LinearLayout.VERTICAL);
		mChildView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT ));
		mChildView.setVisibility(LinearLayout.GONE);
		if(mBusinessAttributes !=null)
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
		for (int i = 0; i < radio_button.length; i++) {
			if(radio_button[i].isChecked())
				isValidInput = true;			
		}
		if(isValidInput){
		if(mBusinessAttributes !=null && isDependencyOn)
		return mBusinessAttributes.isValidInput(context,false);
		}
			return isValidInput;
	}
	@Override
	public void buildJson(JSONObject josnObject) {
		for (int i = 0; i < radio_button.length; i++) {
			if(radio_button[i].isChecked())
				try {
					josnObject.put(this.name, radio_button[i].getText().toString());
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		
	}
		if(mBusinessAttributes !=null && isDependencyOn)
			mBusinessAttributes.buildJson(josnObject,false);
	}
	
	@Override
	public void populateData(JSONObject jsonObject,LinearLayout container,Context context) throws JSONException {
		if(jsonObject.has(this.name)){
			String selectOption = jsonObject.getString(this.name);
			showData(container,this.name,selectOption,context);
			for (int i = 0; i < radio_button.length; i++)
					if(radio_button[i].getText().toString().equalsIgnoreCase(selectOption)){
						radio_button[i].setChecked(true);
						break;
				}
				
		}
		if(mBusinessAttributes !=null && isDependencyOn)
			mBusinessAttributes.populateData(jsonObject,container,context);
	}
	
	private void showData(LinearLayout container,String key,String value,Context context) throws JSONException{
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.view_single_value_business_category, null,false);
		TextView key_text_view = (TextView) view.findViewById(R.id.key);
		TextView value_text = (TextView) view.findViewById(R.id.value);
		key_text_view.setText(key);
		value_text.setText(value);
		
		container.addView(view);
	}
	
	@Override
	public void disableEdit(){
		for (int i = 0; i < radio_button.length; i++) {
				radio_button[i].setClickable(false);
		}
		
		if(mBusinessAttributes !=null && isDependencyOn)
			mBusinessAttributes.disableEdit();
	}
	
	@Override
	public void onCheckedChanged(CompoundButton view, boolean isChecked) {
		
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
