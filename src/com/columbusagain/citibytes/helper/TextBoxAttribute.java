package com.columbusagain.citibytes.helper;

import java.lang.ref.WeakReference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.columbusagain.citibytes.CAActivity;
import com.columbusagain.citibytes.R;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TextBoxAttribute extends Attribute implements TextWatcher {
	
	private EditText mEditText;
	
	private JSONArray dependencyJson;
	
	private String activate_dependency_on;
	
	private LinearLayout mChildView;
	
	private BusinessAttributes mBusinessAttributes;
	
	private String mPrefixText;
	
	private boolean isDependencyOn = false;
	
	private WeakReference<AttributeOnClickListener> mAttributeListener;

	public TextBoxAttribute(JSONObject jsonObj, String key,AttributeOnClickListener listener)
			throws JSONException {
		super(jsonObj, key);
		Log.i("TextBoxAttribute", "TextBoxAttribute called");
		try{
			this.dependencyJson = jsonObj.getJSONArray("dependencies");
			this.activate_dependency_on = jsonObj.getString("activate_dependency_on");
					
			}catch(JSONException e){
				
			}
		try{
			this.mPrefixText = jsonObj.getString("ui_element_prefix");	
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
		View view= (View) inflater.inflate(R.layout.attribute_text_box, null);
		TextView prefix_text = (TextView)view.findViewById(R.id.prefix_text);
		mEditText = (EditText) view.findViewById(R.id.input_text);
		if(this.isMultiValued){
			mEditText.setSingleLine(false);
		}else{
			mEditText.setSingleLine(true);
		}
		if(mPrefixText !=null){
			prefix_text.setText(mPrefixText);
			prefix_text.setVisibility(TextView.VISIBLE);
		}
		
		mEditText.addTextChangedListener(this);
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
	
	

	@Override
	public boolean isValid(Context context) {
		if(!this.isMandatory)
			return true;
		if(mEditText.getText().length() == 0)
			return false;
		if(mBusinessAttributes !=null)
			return mBusinessAttributes.isValidInput(context,false);
		return true;
	}
	
	@Override
	public void buildJson(JSONObject josnObject) {
		try {
			josnObject.put(this.name,mEditText.getText().toString());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(mBusinessAttributes !=null)
			mBusinessAttributes.buildJson(josnObject,false);
		
	}
	
	@Override
	public void populateData(JSONObject jsonObject,LinearLayout container,Context context) throws JSONException {
		if(jsonObject.has(this.name)){
			String selectOption = jsonObject.getString(this.name);
			showData(container,this.name,selectOption,context);
			if(selectOption !=null){
				mEditText.setText(selectOption);
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
		mEditText.setFocusable(false);
		
		if(mBusinessAttributes !=null && isDependencyOn)
			mBusinessAttributes.disableEdit();
	}
	
	

	@Override
	public void afterTextChanged(Editable arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
			int arg3) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTextChanged(CharSequence text, int arg1, int arg2, int arg3) {
		if(mChildView == null || activate_dependency_on == null)
			return;
		if(text.equals(activate_dependency_on)){
			isDependencyOn = true;
			mChildView.setVisibility(LinearLayout.VISIBLE);
		}else{
			isDependencyOn = false;
			mChildView.setVisibility(LinearLayout.GONE);
		}
		
	}

	

	

}
