package com.columbusagain.citibytes.helper;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.sax.StartElementListener;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.columbusagain.citibytes.AttributeListActivity;
import com.columbusagain.citibytes.CAActivity;
import com.columbusagain.citibytes.R;
import com.columbusagain.citibytes.datacollection.BusinessCategoryFragment;

public class SingleValueListViewAttribute extends Attribute implements OnClickListener {
	
	private ArrayList<String> allowedValues = new ArrayList<String>();
	
	private WeakReference<AttributeOnClickListener> mAttributeListener;
	
	private JSONArray dependencyJson;
	
	private String activate_dependency_on;
		
	private boolean isDependencyOn = false;
	
	private LinearLayout mChildView;
	
	private BusinessAttributes mBusinessAttributes;
	
	private Context context;
	
	TextView mTextView;

	public SingleValueListViewAttribute(JSONObject jsonobject, String key,AttributeOnClickListener listener)
			throws JSONException {
		super(jsonobject, key);
		Log.i("SingleValueListViewAttribute", "SingleValueListViewAttribute Called");
		for (int i = 0; i < jsonobject.getJSONArray("allowed_values").length(); i++)
			allowedValues.add(jsonobject.getJSONArray("allowed_values")
					.getString(i));
		try{
			this.dependencyJson = jsonobject.getJSONArray("dependencies");
			this.activate_dependency_on = jsonobject.getString("activate_dependency_on");
			}catch(JSONException e){
				
			}
		
		mAttributeListener = new WeakReference<AttributeOnClickListener>(listener);
		if(dependencyJson!=null)
		mBusinessAttributes = BusinessAttributes.init(dependencyJson,listener);
	}
	

	@Override
	public View render(Context context,Fragment parentFragment) {
		this.context = context;
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.attribute_single_list_view, null);
		mTextView = (TextView)view.findViewById(R.id.single_value_text);
		
		mTextView.setOnClickListener(this);
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
	
	

	
	public ArrayList<String> getAlloweValues()
	{
		return allowedValues;
	}
	
	public ArrayList<String> getText(){
		ArrayList<String> selectedValue = new ArrayList<String>();
		selectedValue.add(mTextView.getText().toString());
		return selectedValue;
	}
	
	public void setText(String value){
		mTextView.setText(value);
		setChildViewVisibility();
	}
	
	@Override
	public void onClick(View view) {
		AttributeOnClickListener listener = this.mAttributeListener.get();
		if(listener != null)
			listener.onClick(this,0);
		
	}
	
	private void setChildViewVisibility(){
		if(mChildView == null || activate_dependency_on == null)
			return;
		if(activate_dependency_on.equals(mTextView.getText().toString())){
			isDependencyOn = true;
			mChildView.setVisibility(LinearLayout.VISIBLE);
		}else{
			isDependencyOn = false;
			mChildView.setVisibility(LinearLayout.GONE);
		}
			
			
	}


	@Override
	public boolean isValid(Context context) {
		if(!this.isMandatory)
			return true;
		if(mTextView.getText().length()==0)
			return false;
		if(mBusinessAttributes !=null && isDependencyOn)
			return mBusinessAttributes.isValidInput(context,false);
		return true;
	}


	@Override
	public void buildJson(JSONObject josnObject) {
		try {
			josnObject.put(this.name, mTextView.getText().toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		if(mBusinessAttributes !=null && isDependencyOn)
			mBusinessAttributes.buildJson(josnObject,false);
		
	}


	@Override
	public void populateData(JSONObject jsonObject,LinearLayout container,Context context) throws JSONException {
		if(jsonObject.has(this.name)){
			String selectOption = jsonObject.getString(this.name);
			
			if(selectOption !=null){
				mTextView.setText(selectOption);
				showData(container,this.name,selectOption,context);
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
		mTextView.setClickable(false);
		if(mBusinessAttributes !=null && isDependencyOn)
			mBusinessAttributes.disableEdit();
	}
	
	

	

}
