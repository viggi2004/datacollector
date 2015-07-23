package com.columbusagain.citibytes.datacollection;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.columbusagain.citibytes.AdminActivity;
import com.columbusagain.citibytes.AttributeListActivity;
import com.columbusagain.citibytes.CAActivity;
import com.columbusagain.citibytes.CategoryListActivity;
import com.columbusagain.citibytes.DataCollectionFragment;
import com.columbusagain.citibytes.R;
import com.columbusagain.citibytes.helper.Attribute;
import com.columbusagain.citibytes.helper.AttributeOnClickListener;
import com.columbusagain.citibytes.helper.BusinessAttributes;
import com.columbusagain.citibytes.helper.Categories;
import com.columbusagain.citibytes.helper.Constants;
import com.columbusagain.citibytes.helper.HttpGet;
import com.columbusagain.citibytes.helper.HttpPost;
import com.columbusagain.citibytes.helper.JsonGetException;
import com.columbusagain.citibytes.helper.MultiValueListViewAttribute;
import com.columbusagain.citibytes.helper.SingleValueListViewAttribute;
import com.columbusagain.citibytes.helper.UserProfile;
import com.columbusagain.citibytes.util.NetworkChecker;

public class BusinessCategoryFragment extends Fragment implements OnClickListener,AttributeOnClickListener{
	
	private String schema;
	
	private Button mViewNextButton;
	
	private TextView mViewBusinessNameText;
	
	private LinearLayout mViewLayoutContainer;
	
	private ScrollView mEditLayout,mViewLayout;
	
	private MenuInflater mMenuInflater;
	
	private boolean isAdminEditMode = false;
	
	private ArrayList<String> mAllowedValues = new ArrayList<String>();
	
	private boolean isPopulateData = false;
	
	private static final int SINGLE_VALUE_SELECTION = 301;
	
	private static final int MULTI_VALUE_SELECTION = 302;
	
	public Button mProceedButton;
	
	private TextView mSingleValuedAttributeTextView;
	
	private MultiValueListViewAttribute mMultiValueListViewAttribute;
	
	private SingleValueListViewAttribute mSingleValueListviewAttribute;
	
	private TextView mBusinessNameTextView;
	
	private Context mContext;
	
	private LinearLayout mParentLayout;
	
	private LinearLayout mBusinessCategories;
	
	private Activity mActivity;
	
	//private Categories mCategories;
	
	private LinearLayout mBusinessCategoryLayout,mBusinessCategoriesContainer;
	
	private Dialog mProgressDialog;
	
	private BusinessAttributes mBusinessAttributes;
	
	private ArrayList<String> selectedCat = new ArrayList<String>();
	
	public BusinessCategoryFragment(){
		
	}
	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		//Categories.mCategoryList.clear();
		setHasOptionsMenu(true);
		View rootView = inflater.inflate(R.layout.datacollection_fragment_business_category,
				container, false);
		mViewBusinessNameText = (TextView) rootView.findViewById(R.id.view_business_name);
		mViewNextButton = (Button) rootView.findViewById(R.id.view_next);
		mEditLayout = (ScrollView) rootView.findViewById(R.id.edit_layout);
		mViewLayout = (ScrollView) rootView.findViewById(R.id.view_layout);
		mViewLayoutContainer = (LinearLayout) rootView.findViewById(R.id.view_layout_container);
		mBusinessCategoryLayout = (LinearLayout) rootView.findViewById(R.id.business_category_layout);
		mBusinessCategoriesContainer = (LinearLayout) rootView.findViewById(R.id.business_category_rows_container);
		mBusinessNameTextView = (TextView) rootView.findViewById(R.id.business_name);
		mParentLayout = (LinearLayout) rootView.findViewById(R.id.business_specific_layout);
		mProceedButton = (Button) rootView.findViewById(R.id.proceed_button);
		mBusinessCategories = (LinearLayout) rootView.findViewById(R.id.categories_layout);
		mBusinessCategoryLayout.setOnClickListener(this);
		mProgressDialog = new Dialog(getActivity(),R.style.ProgressDialog);
		mProgressDialog.setContentView(R.layout.progress_dialog);
		mProceedButton.setOnClickListener(this);
		mViewNextButton.setOnClickListener(this);
		
		try {
			populateSavedData();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return rootView;
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		mMenuInflater = inflater;
		inflater.inflate(R.menu.home, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}
	
	
	
	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		if(UserProfile.isAdmin){
			if(isAdminEditMode){
				mMenuInflater.inflate(R.menu.home, menu);
				//isAdminEditMode = false;
			}else
				mMenuInflater.inflate(R.menu.admin_mode, menu);
		}
		super.onPrepareOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
		case R.id.action_edit:
			((DataCollectionFragment)getParentFragment()).editModeSetup();
			/*isAdminEditMode = true;
			mActivity.invalidateOptionsMenu();
			mEditLayout.setVisibility(ScrollView.VISIBLE);
			mViewLayout.setVisibility(ScrollView.GONE);*/
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void setEditMode(){
		isAdminEditMode = true;
		mActivity.invalidateOptionsMenu();
		mEditLayout.setVisibility(ScrollView.VISIBLE);
		mViewLayout.setVisibility(ScrollView.GONE);
		
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.business_category_layout:
			/*if(selectedCat.size()>0){
				for(int i=0;i<Categories.mCategoryList.size();i++){
					for(int j=0;j<selectedCat.size();j++){
						if(selectedCat.get(j).equals(Categories.mCategoryList.get(i).categoryName)){
							Categories.mCategoryList.get(i).isSelected = true;
							break;
						}else{
							Categories.mCategoryList.get(i).isSelected = false;
						}
					}
					
				}
			}else{
				for(int i=0;i<Categories.mCategoryList.size();i++){
							Categories.mCategoryList.get(i).isSelected = false;
					}
					
				}*/
			
			if(mAllowedValues.size() == 0){
			boolean isNetworkConnected = NetworkChecker.isConnected(mContext);
			if(!isNetworkConnected){
				Toast.makeText(mContext, "No Internet!", Toast.LENGTH_LONG).show();
			}else{
				//isPopulateData = false;
				mProgressDialog.show();
				new GetBusinessCategories().execute();
			} 
			}else{
				Intent intent = new Intent(getActivity(),AttributeListActivity.class);
				intent.putStringArrayListExtra("allowed_values", mAllowedValues);
				intent.putExtra("title","Categories" );
				intent.putStringArrayListExtra("selected_values",selectedCat);
				intent.putExtra("is_single_selection_list", false);						
				getParentFragment().startActivityForResult(intent, 300);
				
			}
			break;
		case R.id.view_next:
			((DataCollectionFragment)getParentFragment()).mViewPager.setCurrentItem(3);
			break;
		case R.id.proceed_button:
			if(UserProfile.isAdmin)
				((AdminActivity)mContext).disableKeyBoard();
			else
				((CAActivity)mContext).disableKeyBoard();
			if(isValidInput()){
				//mBusinessAttributes.getPhotoJson();
				JSONArray business_categories_arr = new JSONArray();
				for(int i=0;i<selectedCat.size();i++){
					business_categories_arr.put(selectedCat.get(i));
				}
				try {
					
					if(UserProfile.isAdmin)
						((AdminActivity)mContext).businessJson.put("business_category",business_categories_arr);
					else
						((CAActivity)mContext).businessJson.put("business_category",business_categories_arr);
					
					//BaseDrawerActivity.businessJson.put("business_category_id", 1);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(mBusinessAttributes != null){
					JSONObject jsonObject = new JSONObject();
					mBusinessAttributes.buildJson(jsonObject,false);
					try {
						if(UserProfile.isAdmin)
							((AdminActivity)mContext).businessJson.put("business_specific_attributes", jsonObject);
						else
							((CAActivity)mContext).businessJson.put("business_specific_attributes", jsonObject);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}else{
					if(UserProfile.isAdmin){
						if(((AdminActivity)mContext).businessJson.has("business_specific_attributes")){
							((AdminActivity)mContext).businessJson.remove("business_specific_attributes");
						}
					}
					else{
						if(((CAActivity)mContext).businessJson.has("business_specific_attributes")){
							((CAActivity)mContext).businessJson.remove("business_specific_attributes");
						}
					}
				}
				if(UserProfile.isAdmin)
					Log.i("BusinessCategoryFragment", ((AdminActivity)mContext).businessJson.toString());
				else
					Log.i("BusinessCategoryFragment", ((CAActivity)mContext).businessJson.toString());
				Log.i("BusinessCategory", "valid");
				((DataCollectionFragment)getParentFragment()).isBusinessDetailsScreenEnabled = true;
				((DataCollectionFragment)getParentFragment()).mViewPager.setCurrentItem(3);			
			}else{
				Toast.makeText(mContext, "Please fill required fields", Toast.LENGTH_LONG).show();
				Log.i("BusinessCategory", "invalid");
			}
			break;
		default:
			break;
		}
		
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.mActivity = activity;
		this.mContext = (Context)activity;
	}
	
	private void updateCategoryLayout(){
		mBusinessCategoriesContainer.removeAllViews();
		for(int i=0;i<selectedCat.size();i++){
			LayoutInflater inflater = (LayoutInflater)mContext.getSystemService
				      (Context.LAYOUT_INFLATER_SERVICE);
			final View view = inflater.inflate(R.layout.business_category_row_layout, null);
			final TextView business_category = (TextView) view.findViewById(R.id.business_category_text);
			ImageButton button_delete_category = (ImageButton) view.findViewById(R.id.delete_category);
			business_category.setText(selectedCat.get(i));
			button_delete_category.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					mBusinessCategoriesContainer.removeView(view);
					selectedCat.remove(business_category.getText().toString());
					updateUi();
					
				}
			});
			
			mBusinessCategoriesContainer.addView(view);
			
			Log.i("ArrayListContent", selectedCat.get(i));
		} 
		
		updateUi();
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.i("BusinessCategoryFragment", "onActivityResult");
		if(resultCode == getActivity().RESULT_OK){
		switch(requestCode){
		case 300:
			Log.i("BusinessCategoryFragment", "onActivityResult");
			selectedCat.clear();
			ArrayList<String> selectedValues = data.getExtras().getStringArrayList("selected_values");
			for(int i=0;i<selectedValues.size();i++){
				selectedCat.add(selectedValues.get(i));
			}
			isPopulateData = false;
			updateCategoryLayout();
			//selectedCat = data.getExtras().getStringArrayList("selected_values");
			
			break;
		case SINGLE_VALUE_SELECTION:
			mSingleValueListviewAttribute.setText(data.getExtras().getString("selected_value"));
			mSingleValueListviewAttribute = null;
			break;
			
		case MULTI_VALUE_SELECTION:
			mMultiValueListViewAttribute.setSelectedValues(data.getExtras().getStringArrayList("selected_values"));
		default:
			break;
		
		}
		}
	}
	
	private void updateUi(){
		boolean isNetworkConnected = NetworkChecker.isConnected(mContext);
		if(!isNetworkConnected){
			Toast.makeText(mContext, "No Internet!", Toast.LENGTH_LONG).show();
		}else{
			mProgressDialog.show();
			//mBusinessAttributes = null;
			new UiSchemaDownloader().execute();
		}
	}
	
private class GetBusinessCategories extends AsyncTask<Void, Void, String>{
		
		

		@Override
		protected String doInBackground(Void... params) {
			String result = null;
			HttpGet httpGet = new HttpGet(Constants.BASE_URL+"getAllBusinessCategory.php");
			try {
				result = httpGet.executeGet();
			} catch (JsonGetException e) {
				e.printStackTrace();
				result = null;
			}
			return result;
		}
		
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if(mProgressDialog.isShowing()){
				mProgressDialog.cancel();
			}
			if(result != null){
				try {
					JSONObject response_json = new JSONObject(result);
					String status = response_json.getString("status");
					if("success".equals(status)){
						
						JSONArray business_category_arr = response_json.getJSONArray("business_categories");
						for(int i=0;i<business_category_arr.length();i++){
							mAllowedValues.add(business_category_arr.getString(i));
						/*mCategories = new Categories();
						mCategories.categoryName = business_category_arr.getString(i);
						mCategories.isSelected = false;
						Categories.mCategoryList.add(mCategories);*/
						}
						//if(!isPopulateData){
							Intent intent = new Intent(getActivity(),AttributeListActivity.class);
							intent.putStringArrayListExtra("allowed_values", mAllowedValues);
							intent.putExtra("title","Categories" );
							intent.putStringArrayListExtra("selected_values",selectedCat);
							intent.putExtra("is_single_selection_list", false);						
							getParentFragment().startActivityForResult(intent, 300);
						//}
					}else{
						Toast.makeText(mContext, "Error", Toast.LENGTH_LONG).show();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}else{
				Toast.makeText(mContext, "No Network", Toast.LENGTH_LONG).show();
			}
		
		}
		
	}
	
	public void setBusinessName(String businessName){
		if(mBusinessNameTextView != null)
		mBusinessNameTextView.setText(businessName);
		if(mViewBusinessNameText != null)
		mViewBusinessNameText.setText(businessName);
	}
	
	private class UiSchemaDownloader extends AsyncTask<Void, Void, String>{

		@Override
		protected String doInBackground(Void... params) {
			HttpPost httpPost = new HttpPost(Constants.BASE_URL+"getUISchema.php");
			httpPost.setParam("json", buildUiRequestJson());
			return httpPost.executePost();
		}
		
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if(mProgressDialog.isShowing()){
				mProgressDialog.cancel();
			}
			//mParentLayout.removeAllViews();
			Log.i("BusinessCategoryFragment", "onPostExecute");
			((DataCollectionFragment)getParentFragment()).mUiSchemaJson = null;
			if(result !=null){
				parseJson(result);
				
				Log.i("BusinessCategoryFragment", "UiSchema : "+result);
			}else{
				Toast.makeText(mContext, "No Internet", Toast.LENGTH_LONG).show();
			}
		}
		
	}
	
	private void parseJson(String jsonString){
		boolean isNeedUpdate = false;
		try {
			JSONObject jsonObject = new JSONObject(jsonString);
			((DataCollectionFragment)getParentFragment()).mUiSchemaJson = jsonObject;
			((DataCollectionFragment)getParentFragment()).upDateImageScreen();
			String status = jsonObject.getString("status");
			
			if(status.equalsIgnoreCase("success")){
				String schema_name = jsonObject.getString("schema_name");
				if(schema_name.equalsIgnoreCase(schema))
					return;
				else{
					mBusinessAttributes = null;
					mParentLayout.removeAllViews();
					isNeedUpdate = true;
				}
					
				schema = jsonObject.getString("schema_name");
				if(UserProfile.isAdmin)
					((AdminActivity)mContext).businessJson.put("schema_name",schema);
				else
					((CAActivity)mContext).businessJson.put("schema_name",schema);
				JSONArray schema_arr = jsonObject.getJSONArray("schema");
				//JSONObject schemaJson = jsonObject.getJSONObject("schema");
				mBusinessAttributes = BusinessAttributes.init(schema_arr,this);				
				mBusinessAttributes.render(mParentLayout,false, mContext,getParentFragment());
				
			}else{
				mBusinessAttributes = null;
				mParentLayout.removeAllViews();
				schema = null;
			}
			if(isPopulateData && isNeedUpdate){
				UiAccessabiltySetting();
			}else if(isPopulateData){
				setUI();
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	private void setUI() throws JSONException{
	 JSONObject businessJson;
		if(UserProfile.isAdmin)
			businessJson = ((AdminActivity) mContext).downloadedJson;
		else
			businessJson = ((CAActivity) mContext).downloadedJson;
		
		if(businessJson.length() == 0){
			return;
		}
		
		if(businessJson.has("status")){
			String status = businessJson.getString("status");
			if(status.equalsIgnoreCase("BUSINESS_VERIFIED_COMPLETE")){
				mEditLayout.setVisibility(ScrollView.GONE);
				mViewLayout.setVisibility(ScrollView.VISIBLE);
				if(UserProfile.isAdmin){
					if(isAdminEditMode){
						mEditLayout.setVisibility(ScrollView.VISIBLE);
						mViewLayout.setVisibility(ScrollView.GONE);
					}else{
						mEditLayout.setVisibility(ScrollView.GONE);
						mViewLayout.setVisibility(ScrollView.VISIBLE);
					}
					mActivity.invalidateOptionsMenu();
				}
				//disableEdit();
			}else if(status.equalsIgnoreCase("BUSINESS_VERIFIED_INCOMPLETE")){
				if(UserProfile.isAdmin){
					if(isAdminEditMode){
						mEditLayout.setVisibility(ScrollView.VISIBLE);
						mViewLayout.setVisibility(ScrollView.GONE);
					}else{
						mEditLayout.setVisibility(ScrollView.GONE);
						mViewLayout.setVisibility(ScrollView.VISIBLE);
					}
					mActivity.invalidateOptionsMenu();
				}
			}else if(status.equalsIgnoreCase("TRANSIENT")){
				if(UserProfile.isAdmin){
					if(isAdminEditMode){
						mEditLayout.setVisibility(ScrollView.VISIBLE);
						mViewLayout.setVisibility(ScrollView.GONE);
					}else{
						mEditLayout.setVisibility(ScrollView.GONE);
						mViewLayout.setVisibility(ScrollView.VISIBLE);
					}
					mActivity.invalidateOptionsMenu();
				}
			}
		}
	}
	 
	
	private void UiAccessabiltySetting() throws JSONException{
		JSONObject businessJson;
		if(UserProfile.isAdmin)
			businessJson = ((AdminActivity) mContext).downloadedJson;
		else
			businessJson = ((CAActivity) mContext).downloadedJson;
		
		if(businessJson.length() == 0){
			return;
		}

		if(businessJson.has("status")){
			if(mBusinessAttributes != null)
			mBusinessAttributes.populateData(businessJson,mViewLayoutContainer,mContext);
			((DataCollectionFragment) getParentFragment()).isBusinessDetailsScreenEnabled = true;
			String status = businessJson.getString("status");
			if(status.equalsIgnoreCase("BUSINESS_VERIFIED_COMPLETE")){
				mEditLayout.setVisibility(ScrollView.GONE);
				mViewLayout.setVisibility(ScrollView.VISIBLE);
				if(UserProfile.isAdmin){
					if(isAdminEditMode){
						mEditLayout.setVisibility(ScrollView.VISIBLE);
						mViewLayout.setVisibility(ScrollView.GONE);
					}else{
						mEditLayout.setVisibility(ScrollView.GONE);
						mViewLayout.setVisibility(ScrollView.VISIBLE);
					}
					mActivity.invalidateOptionsMenu();
				}
				//disableEdit();
			}else if(status.equalsIgnoreCase("BUSINESS_VERIFIED_INCOMPLETE")){
				if(UserProfile.isAdmin){
					if(isAdminEditMode){
						mEditLayout.setVisibility(ScrollView.VISIBLE);
						mViewLayout.setVisibility(ScrollView.GONE);
					}else{
						mEditLayout.setVisibility(ScrollView.GONE);
						mViewLayout.setVisibility(ScrollView.VISIBLE);
					}
					mActivity.invalidateOptionsMenu();
				}
			}else if(status.equalsIgnoreCase("TRANSIENT")){
				if(UserProfile.isAdmin){
					if(isAdminEditMode){
						mEditLayout.setVisibility(ScrollView.VISIBLE);
						mViewLayout.setVisibility(ScrollView.GONE);
					}else{
						mEditLayout.setVisibility(ScrollView.GONE);
						mViewLayout.setVisibility(ScrollView.VISIBLE);
					}
					mActivity.invalidateOptionsMenu();
				}
			}
		}
		
	}
	
	public boolean isValidInput(){
		
		JSONObject businessJson;
		if(UserProfile.isAdmin){
			businessJson = ((AdminActivity) mContext).downloadedJson;
		}else{
			businessJson = ((CAActivity) mContext).downloadedJson;			
		}
		try {
		if(businessJson.has("status")){
			String status = businessJson.getString("status");			 
			if(status.equalsIgnoreCase("BUSINESS_VERIFIED_COMPLETE"))
				if(UserProfile.isAdmin){
					if(!isAdminEditMode)
						return true;
				}else
				return true;
		}
		}
		catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(UserProfile.isAdmin){
			
		}else{
			
		}
		if(selectedCat.size()>0){
			if(mBusinessAttributes == null)
				return true;
			if(mBusinessAttributes.isValidInput(mContext,false))
				return true;
			
			return false;
		}
			
		//if(mBusinessAttributes!=null && selectedCat.size()>0 )
		////if(mBusinessAttributes.isValidInput(mContext,false))
		//return true;
		
		return false;
	}
	
	private String buildUiRequestJson(){
		JSONObject requestJson = new JSONObject();
		JSONArray business_categories_arr = new JSONArray();
		for(int i=0;i<selectedCat.size();i++){
			business_categories_arr.put(selectedCat.get(i));
		}
		try {
			
			requestJson.put("business_categories", business_categories_arr);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Log.i("BusinessCategoryFragment", requestJson.toString());
		
		return requestJson.toString();
	}
	
	public void showSingleValueListView(TextView textView,String displayText,ArrayList<String> allowedValues){
		mSingleValuedAttributeTextView = textView;
		Log.i("BusinessCategoryFragment", "single valued list view");
		Intent intent = new Intent(mContext,AttributeListActivity.class);
		intent.putStringArrayListExtra("allowed_values", allowedValues);
		intent.putExtra("title",displayText );
		intent.putExtra("Selected", mSingleValuedAttributeTextView.getText().toString());
		getParentFragment().startActivityForResult(intent, SINGLE_VALUE_SELECTION);
	}

	@Override
	public void onClick(Attribute attribute,int type) {
		
		if(attribute instanceof SingleValueListViewAttribute){
			mSingleValueListviewAttribute = (SingleValueListViewAttribute) attribute;
			Intent intent = new Intent(mContext,AttributeListActivity.class);
			intent.putStringArrayListExtra("allowed_values", mSingleValueListviewAttribute.getAlloweValues());
			intent.putExtra("title",mSingleValueListviewAttribute.getDisplayText() );
			intent.putStringArrayListExtra("selected_values", mSingleValueListviewAttribute.getText());
			intent.putExtra("is_single_selection_list", true);
			getParentFragment().startActivityForResult(intent, SINGLE_VALUE_SELECTION);
		
		}else if(attribute instanceof MultiValueListViewAttribute){
			mMultiValueListViewAttribute = (MultiValueListViewAttribute) attribute;
			Intent intent = new Intent(mContext,AttributeListActivity.class);
			intent.putStringArrayListExtra("allowed_values", mMultiValueListViewAttribute.getAllowedValues());
			intent.putExtra("title",mMultiValueListViewAttribute.getDisplayText() );
			intent.putStringArrayListExtra("selected_values", mMultiValueListViewAttribute.getSelectedValues());
			intent.putExtra("is_single_selection_list", false);
			getParentFragment().startActivityForResult(intent, MULTI_VALUE_SELECTION);
		}
			
	}
	
	private void disableCategoryLayoutOnclick(){
		
		mBusinessCategoryLayout.setClickable(false);
		
		for(int i=0;i<mBusinessCategoriesContainer.getChildCount();i++){
			final View view = mBusinessCategoriesContainer.getChildAt(i);
			ImageButton button_delete_category = (ImageButton) view.findViewById(R.id.delete_category);
			button_delete_category.setClickable(false);
		}
		
	}
	
	public void disableEdit(){
		mEditLayout.setVisibility(ScrollView.GONE);
		mViewLayout.setVisibility(ScrollView.VISIBLE);
		disableCategoryLayoutOnclick();
		mProceedButton.setClickable(false);
		if(mBusinessAttributes != null)
		mBusinessAttributes.disableEdit();
	}
	
	public void populateSavedData() throws JSONException{
		JSONObject businessJson;
		if(UserProfile.isAdmin)
			businessJson = ((AdminActivity) mContext).downloadedJson;
		else
			businessJson = ((CAActivity) mContext).downloadedJson;
		if (businessJson.length() == 0)
			return;
		
		if(businessJson.has("business_category")){
			JSONArray category_arr = businessJson.getJSONArray("business_category");
			populateBusinessCategories(category_arr);
			selectedCat.clear();
			for(int i=0;i<category_arr.length();i++){
				selectedCat.add(category_arr.getString(i));
			}
			isPopulateData = true;
			updateCategoryLayout();
		}
		
	}
	
	private void populateBusinessCategories(JSONArray categoryArray) throws JSONException{
		LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		for (int i = 0; i < categoryArray.length(); i++) {
			final View v = mBusinessCategories.getChildAt(i);
			if(v!=null){
				TextView view_text = (TextView)v.findViewById(R.id.text);
				view_text.setText(categoryArray.getString(i));
			}else{
			Log.i("BasicDetailsFragment", "landline number added");
			final View viewlayout = inflater.inflate(R.layout.view_layout_text_view,null,false);
			TextView view_text = (TextView)viewlayout.findViewById(R.id.text);
			view_text.setText(categoryArray.getString(i));
			mBusinessCategories.addView(viewlayout);
			}
		}
	}

}
