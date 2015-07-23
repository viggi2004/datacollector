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
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MultiValueListViewAttribute extends Attribute implements OnClickListener  {

	private ArrayList<String> allowedValues = new ArrayList<String>();
	
	private ArrayList<String> mSelectedValues = new ArrayList<String>();
	
	private LinearLayout mMultiSelectionParentLayout,mMultiSelectionContainer;
	
	private JSONArray dependencyJson;
	
	private String activate_dependency_on;
	
	private LinearLayout mChildView;
	
	private boolean isDependencyOn = false;
	
	private BusinessAttributes mBusinessAttributes;

	private Context context;

	private WeakReference<AttributeOnClickListener> mAttributeListener;
	
	public MultiValueListViewAttribute(JSONObject jsonobject, String key,AttributeOnClickListener listener)
			throws JSONException {
		super(jsonobject, key);
		Log.i("MultiValueListViewAttribute", "MultiValueListViewAttribute Called");
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
		View view = inflater.inflate(R.layout.attribute_multi_selection_list, null);
		mMultiSelectionParentLayout = (LinearLayout) view.findViewById(R.id.multi_selection_layout);
		mMultiSelectionContainer = (LinearLayout) view.findViewById(R.id.multi_selections_container);
		mMultiSelectionParentLayout.setOnClickListener(this);
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

	
	public ArrayList<String> getAllowedValues()
	{
		return allowedValues;
	}
	
	public ArrayList<String> getSelectedValues(){
		return mSelectedValues;
	}
	
	
	
	public void setSelectedValues(ArrayList<String> selectedValues){
		this.mSelectedValues = selectedValues;
		
		mMultiSelectionContainer.removeAllViews();
		
		for(int i=0;i<selectedValues.size();i++){
			LayoutInflater inflater = (LayoutInflater)context.getSystemService
				      (Context.LAYOUT_INFLATER_SERVICE);
			final View view = inflater.inflate(R.layout.business_category_row_layout, null);
			final TextView business_category = (TextView) view.findViewById(R.id.business_category_text);
			ImageButton button_delete_category = (ImageButton) view.findViewById(R.id.delete_category);
			business_category.setText(mSelectedValues.get(i));
			button_delete_category.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					mMultiSelectionContainer.removeView(view);
					mSelectedValues.remove(business_category.getText().toString());
					setChildViewVisibility();
					
				}
			});
			
			mMultiSelectionContainer.addView(view);
			
		} 
		setChildViewVisibility();
		
	}
	
	private void setChildViewVisibility(){
		if(mChildView == null || activate_dependency_on == null)
			return;
		isDependencyOn =false;
		mChildView.setVisibility(LinearLayout.GONE);
		for(int i=0;i<mSelectedValues.size();i++){
			if(activate_dependency_on.equals(mSelectedValues.get(i))){
				mChildView.setVisibility(LinearLayout.VISIBLE);
				isDependencyOn = true;
			}
				
		}
	}
	
	
	
	@Override
	public void onClick(View view) {
		AttributeOnClickListener listener = this.mAttributeListener.get();
		if(listener != null)
			listener.onClick(this,0);
		
	}


	@Override
	public boolean isValid(Context context) {
		if(!this.isMandatory)
			return true;
		if(mSelectedValues.size()==0)
			return false;
		if(mBusinessAttributes !=null && isDependencyOn)
			return mBusinessAttributes.isValidInput(context,false);
		return true;
	}
	
	@Override
	public void buildJson(JSONObject josnObject) {
		JSONArray jsonArr = new JSONArray();
		for (int i = 0; i < mSelectedValues.size(); i++) {
			jsonArr.put(mSelectedValues.get(i));		
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
	public void disableEdit(){
		mMultiSelectionParentLayout.setClickable(false);
		for(int i=0;i<mMultiSelectionContainer.getChildCount();i++){
			View v = mMultiSelectionContainer.getChildAt(i);
			ImageButton button_delete_category = (ImageButton) v.findViewById(R.id.delete_category);
			button_delete_category.setClickable(false);			
		}
		
		if(mBusinessAttributes !=null && isDependencyOn)
			mBusinessAttributes.disableEdit();
	}

	@Override
	public void populateData(JSONObject jsonObject,LinearLayout container,Context context) throws JSONException {
		if(jsonObject.has(this.name)){
			
			JSONArray selectedArray = jsonObject.getJSONArray(this.name);
			showData(container,this.name,selectedArray,context);
			ArrayList<String> selectedValues = new ArrayList<String>();
			for(int i=0;i<selectedArray.length();i++)
				selectedValues.add(selectedArray.getString(i));
			this.setSelectedValues(selectedValues);
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

}
