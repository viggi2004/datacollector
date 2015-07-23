package com.columbusagain.citibytes.datacollection;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.columbusagain.citibytes.AdminActivity;
import com.columbusagain.citibytes.CAActivity;
import com.columbusagain.citibytes.DataCollectionFragment;
import com.columbusagain.citibytes.R;
import com.columbusagain.citibytes.helper.UserProfile;

import android.app.Activity;
import android.content.Context;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

public class BusinessDetailsFragment extends Fragment implements
		OnCheckedChangeListener, OnClickListener {
			
	private LinearLayout mViewMondayLayout,mViewTuesDayLayout,mViewWednessDayLayout,mViewThursdayLayout,mViewFridayLayout,mViewSaturDayLayout,mViewSundayLayout;
	
	private TextView mViewBusinessName,mViewBusinessDescription,mViewBusinessDescriptionTitle;
	
	private View mViewBusinessDescriptionSeparator;
	
	private ScrollView mEditLayout,mViewLayout;
	
	public Button mProceedButton,mViewNextButton;
	
	private EditText mBusinessDescriptionText;

	private TextView mBusinessNameTextView;
	private String[] mClockTimes;
	
	private boolean isAdminEditMode = false;
	
	private MenuInflater mMenuInflater;

	private ArrayList<String> mClockTimeList = new ArrayList<String>();

	private CheckBox mSelectAllDays, mSelectMonday, mSelectTuesDay,
			mSelectWednesDay, mSelectThursDay, mSelectFriDay, mSelectSaturDay,
			mSelectSunDay;

	private Spinner mSpinnerMonFrom, mSpinnerMonTo, mSpinnerTueFrom,
			mSpinnerTueTo, mSpinnerWedFrom, mSpinnerWedTo, mSpinnerThuFrom,
			mSpinnerThuTo, mSpinnerFriFrom, mSpinnerFriTo, mSpinnerSatFrom,
			mSpinnerSatTo, mSpinnerSunFrom, mSpinnerSunTo;

	private ArrayAdapter<String> mTimeAdapter;

	private Activity mActivity;

	private Context mContext;
	
	private ImageButton mApplyToAllButton;
	
	public BusinessDetailsFragment() {
	}
		
	

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.mActivity = activity;
		this.mContext = (Context) activity;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		setHasOptionsMenu(true);
		View rootView = inflater.inflate(
				R.layout.datacollection_fragment_business_details, container,
				false);

		mClockTimes = getResources().getStringArray(R.array.time_array);
		for (int i = 0; i < mClockTimes.length; i++) {
			mClockTimeList.add(mClockTimes[i]);
		}

		mTimeAdapter = new ArrayAdapter<String>(mContext,
				R.layout.spinner_text, mClockTimeList);
		mViewNextButton = (Button) rootView.findViewById(R.id.next);
		mEditLayout = (ScrollView) rootView.findViewById(R.id.edit_layout);
		mViewLayout = (ScrollView) rootView.findViewById(R.id.view_layout);
		mViewBusinessName = (TextView) rootView.findViewById(R.id.view_business_name);
		mViewBusinessDescription = (TextView)rootView.findViewById(R.id.view_business_description);
		mViewBusinessDescriptionTitle = (TextView) rootView.findViewById(R.id.view_business_description_title);
		mViewBusinessDescriptionSeparator = (View)rootView.findViewById(R.id.view_business_description_separator);
		mViewMondayLayout = (LinearLayout)rootView.findViewById(R.id.monday_layout);
		mViewTuesDayLayout = (LinearLayout)rootView.findViewById(R.id.tuesday_layout);
		mViewWednessDayLayout = (LinearLayout)rootView.findViewById(R.id.wednessday_layout);
		mViewThursdayLayout = (LinearLayout)rootView.findViewById(R.id.thursday_layout);
		mViewFridayLayout = (LinearLayout)rootView.findViewById(R.id.friday_layout);
		mViewSaturDayLayout = (LinearLayout)rootView.findViewById(R.id.saturday_layout);
		mViewSundayLayout = (LinearLayout)rootView.findViewById(R.id.sunday_layout);
		mSelectAllDays = (CheckBox) rootView
				.findViewById(R.id.check_box_select_all);
		mSelectMonday = (CheckBox) rootView.findViewById(R.id.check_box_monday);
		mSelectTuesDay = (CheckBox) rootView
				.findViewById(R.id.check_box_tuesday);
		mSelectWednesDay = (CheckBox) rootView
				.findViewById(R.id.check_box_wednesday);
		mSelectThursDay = (CheckBox) rootView
				.findViewById(R.id.check_box_thursday);
		mSelectFriDay = (CheckBox) rootView.findViewById(R.id.check_box_friday);
		mSelectSaturDay = (CheckBox) rootView
				.findViewById(R.id.check_box_saturday);
		mSelectSunDay = (CheckBox) rootView.findViewById(R.id.check_box_sunday);
		mBusinessDescriptionText = (EditText)rootView.findViewById(R.id.business_description);

		mSpinnerMonFrom = (Spinner) rootView.findViewById(R.id.spinnerMonFrom);
		mSpinnerMonTo = (Spinner) rootView.findViewById(R.id.spinnerMonTo);
		mSpinnerTueFrom = (Spinner) rootView.findViewById(R.id.spinnerTueFrom);
		mSpinnerTueTo = (Spinner) rootView.findViewById(R.id.spinnerTueTo);
		mSpinnerWedFrom = (Spinner) rootView.findViewById(R.id.spinnerWedFrom);
		mSpinnerWedTo = (Spinner) rootView.findViewById(R.id.spinnerWedTo);
		mSpinnerThuFrom = (Spinner) rootView.findViewById(R.id.spinnerThuFrom);
		mSpinnerThuTo = (Spinner) rootView.findViewById(R.id.spinnerThuTo);
		mSpinnerFriFrom = (Spinner) rootView.findViewById(R.id.spinnerFriFrom);
		mSpinnerFriTo = (Spinner) rootView.findViewById(R.id.spinnerFriTo);
		mSpinnerSatFrom = (Spinner) rootView.findViewById(R.id.spinnerSatFrom);
		mSpinnerSatTo = (Spinner) rootView.findViewById(R.id.spinnerSatTo);
		mSpinnerSunFrom = (Spinner) rootView.findViewById(R.id.spinnerSunFrom);
		mSpinnerSunTo = (Spinner) rootView.findViewById(R.id.spinnerSunTo);
		mApplyToAllButton = (ImageButton) rootView.findViewById(R.id.apply_to_all);
		mProceedButton = (Button) rootView.findViewById(R.id.proceed);
		
		mBusinessNameTextView = (TextView) rootView.findViewById(R.id.business_name);

		mSpinnerMonFrom.setAdapter(mTimeAdapter);
		mSpinnerMonTo.setAdapter(mTimeAdapter);
		mSpinnerTueFrom.setAdapter(mTimeAdapter);
		mSpinnerTueTo.setAdapter(mTimeAdapter);
		mSpinnerWedFrom.setAdapter(mTimeAdapter);
		mSpinnerWedTo.setAdapter(mTimeAdapter);
		mSpinnerThuFrom.setAdapter(mTimeAdapter);
		mSpinnerThuTo.setAdapter(mTimeAdapter);
		mSpinnerFriFrom.setAdapter(mTimeAdapter);
		mSpinnerFriTo.setAdapter(mTimeAdapter);
		mSpinnerSatFrom.setAdapter(mTimeAdapter);
		mSpinnerSatTo.setAdapter(mTimeAdapter);
		mSpinnerSunFrom.setAdapter(mTimeAdapter);
		mSpinnerSunTo.setAdapter(mTimeAdapter);
		mApplyToAllButton.setOnClickListener(this);
		
		mSpinnerMonFrom.setSelection(18, true);
		mSpinnerMonTo.setSelection(38, true);
		mSpinnerTueFrom.setSelection(18, true);
		mSpinnerTueTo.setSelection(38, true);
		mSpinnerWedFrom.setSelection(18, true);
		mSpinnerWedTo.setSelection(38, true);
		mSpinnerThuFrom.setSelection(18, true);
		mSpinnerThuTo.setSelection(38, true);
		mSpinnerFriFrom.setSelection(18, true);
		mSpinnerFriTo.setSelection(38, true);
		mSpinnerSatFrom.setSelection(18, true);
		mSpinnerSatTo.setSelection(38, true);
		mSpinnerSunFrom.setSelection(18, true);
		mSpinnerSunTo.setSelection(38, true);

		mSelectAllDays.setOnCheckedChangeListener(this);
		mSelectMonday.setOnCheckedChangeListener(this);
		mSelectTuesDay.setOnCheckedChangeListener(this);
		mSelectWednesDay.setOnCheckedChangeListener(this);
		mSelectThursDay.setOnCheckedChangeListener(this);
		mSelectFriDay.setOnCheckedChangeListener(this);
		mSelectSaturDay.setOnCheckedChangeListener(this);
		mSelectSunDay.setOnCheckedChangeListener(this);
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
	public void onCheckedChanged(CompoundButton view, boolean isSelected) {
		switch (view.getId()) {
		case R.id.check_box_select_all:
			if (isSelected) {
				mSelectMonday.setChecked(true);
				mSelectTuesDay.setChecked(true);
				mSelectWednesDay.setChecked(true);
				mSelectThursDay.setChecked(true);
				mSelectFriDay.setChecked(true);
				mSelectSaturDay.setChecked(true);
				mSelectSunDay.setChecked(true);
			} else {
				mSelectMonday.setChecked(false);
				mSelectTuesDay.setChecked(false);
				mSelectWednesDay.setChecked(false);
				mSelectThursDay.setChecked(false);
				mSelectFriDay.setChecked(false);
				mSelectSaturDay.setChecked(false);
				mSelectSunDay.setChecked(false);
			}
			break;
		case R.id.check_box_monday:
			
			break;
		case R.id.check_box_tuesday:
			break;
		case R.id.check_box_wednesday:
			break;
		case R.id.check_box_thursday:
			break;
		case R.id.check_box_friday:
			break;
		case R.id.check_box_saturday:
			break;
		case R.id.check_box_sunday:
			break;
		}

	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		mMenuInflater = inflater;
		inflater.inflate(R.menu.home, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}
	
	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		if(UserProfile.isAdmin)
		if(isAdminEditMode){
			mMenuInflater.inflate(R.menu.home, menu);
		}else{
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
	public void onClick(View view) {
		switch(view.getId()){
			case R.id.apply_to_all:
				int fromSelection = mSpinnerMonFrom.getSelectedItemPosition();
				int toSelection = mSpinnerMonTo.getSelectedItemPosition();
				mSpinnerTueFrom.setSelection(fromSelection, true);
				mSpinnerTueTo.setSelection(toSelection, true);
				mSpinnerWedFrom.setSelection(fromSelection, true);
				mSpinnerWedTo.setSelection(toSelection, true);
				mSpinnerThuFrom.setSelection(fromSelection, true);
				mSpinnerThuTo.setSelection(toSelection, true);
				mSpinnerFriFrom.setSelection(fromSelection, true);
				mSpinnerFriTo.setSelection(toSelection, true);
				mSpinnerSatFrom.setSelection(fromSelection, true);
				mSpinnerSatTo.setSelection(toSelection, true);
				mSpinnerSunFrom.setSelection(fromSelection, true);
				mSpinnerSunTo.setSelection(toSelection, true);
				break;
			case R.id.next: 
				((DataCollectionFragment)getParentFragment()).mViewPager.setCurrentItem(4);
				break;
			case R.id.proceed:
				if(UserProfile.isAdmin)
					((AdminActivity)mContext).disableKeyBoard();
				else
					((CAActivity)mContext).disableKeyBoard();
				if(isValidInput()){
					buildJson();
					((DataCollectionFragment)getParentFragment()).isImageDetailsScreenEnabled = true;
					((DataCollectionFragment)getParentFragment()).mViewPager.setCurrentItem(4);
				}else{
					Toast.makeText(mContext, "Please fill required fields", Toast.LENGTH_LONG).show();
				}
				
				break;
			default:
				break;
		}
		
		
	}
	
	public boolean isValidInput(){
		//if(mBusinessDescriptionText.getText().toString().length() == 0)
			//return false;
		if(mSelectMonday == null || mSelectTuesDay == null || mSelectWednesDay == null || mSelectThursDay == null || mSelectFriDay == null ||mSelectSaturDay == null || mSelectSunDay == null)
			return false;
		if(mSelectMonday.isChecked()|mSelectTuesDay.isChecked()|mSelectWednesDay.isChecked()|mSelectThursDay.isChecked()|mSelectFriDay.isChecked()|mSelectSaturDay.isChecked()|mSelectSunDay.isChecked())
		return true;
		
		return false;
	}
	
	private void buildJson(){
		try {
			if(mBusinessDescriptionText.getText().toString().length()> 0)
				if(UserProfile.isAdmin)
					((AdminActivity)mContext).businessJson.put("business_description", mBusinessDescriptionText.getText().toString());
				else
					((CAActivity)mContext).businessJson.put("business_description", mBusinessDescriptionText.getText().toString());
		JSONObject hours_of_operation = new JSONObject();
		JSONArray hoursData;
		if(mSelectMonday.isChecked()){
			hoursData = new JSONArray();
			hoursData.put(mSpinnerMonFrom.getSelectedItem().toString());
			hoursData.put(mSpinnerMonTo.getSelectedItem().toString());
			hours_of_operation.put("monday", hoursData);			
		}
		
		if(mSelectTuesDay.isChecked()){
			hoursData = new JSONArray();
			hoursData.put(mSpinnerTueFrom.getSelectedItem().toString());
			hoursData.put(mSpinnerTueTo.getSelectedItem().toString());
			hours_of_operation.put("tuesday", hoursData);			
		}
		
		if(mSelectWednesDay.isChecked()){
			hoursData = new JSONArray();
			hoursData.put(mSpinnerWedFrom.getSelectedItem().toString());
			hoursData.put(mSpinnerWedTo.getSelectedItem().toString());
			hours_of_operation.put("wednesday", hoursData);			
		}
		
		if(mSelectThursDay.isChecked()){
			hoursData = new JSONArray();
			hoursData.put(mSpinnerThuFrom.getSelectedItem().toString());
			hoursData.put(mSpinnerThuTo.getSelectedItem().toString());
			hours_of_operation.put("thursday", hoursData);			
		}
		
		if(mSelectFriDay.isChecked()){
			hoursData = new JSONArray();
			hoursData.put(mSpinnerFriFrom.getSelectedItem().toString());
			hoursData.put(mSpinnerFriTo.getSelectedItem().toString());
			hours_of_operation.put("friday", hoursData);			
		}
		
		if(mSelectSaturDay.isChecked()){
			hoursData = new JSONArray();
			hoursData.put(mSpinnerSatFrom.getSelectedItem().toString());
			hoursData.put(mSpinnerSatTo.getSelectedItem().toString());
			hours_of_operation.put("saturday", hoursData);			
		}
		
		if(mSelectSunDay.isChecked()){
			hoursData = new JSONArray();
			hoursData.put(mSpinnerSunFrom.getSelectedItem().toString());
			hoursData.put(mSpinnerSunTo.getSelectedItem().toString());
			hours_of_operation.put("sunday", hoursData);			
		}
		if(UserProfile.isAdmin){
			((AdminActivity)mContext).businessJson.put("hours_of_operation", hours_of_operation);		
			Log.i("BusinessJson",((AdminActivity)mContext).businessJson.toString());
		}else{
			((CAActivity)mContext).businessJson.put("hours_of_operation", hours_of_operation);		
			Log.i("BusinessJson",((CAActivity)mContext).businessJson.toString());
		}
		
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void setBusinessName(String businessName){
		if(mBusinessNameTextView != null )
		mBusinessNameTextView.setText(businessName);
		if(mViewBusinessName != null)
		mViewBusinessName.setText(businessName);
	}
	
	public void disableEdit(){
		mBusinessDescriptionText.setFocusable(false);
		mSelectAllDays.setClickable(false);
		mSelectMonday.setClickable(false);
		mSelectTuesDay.setClickable(false);
		mSelectWednesDay.setClickable(false);
		mSelectThursDay.setClickable(false);
		mSelectFriDay.setClickable(false);
		mSelectSaturDay.setClickable(false);
		mSelectSunDay.setClickable(false);
		mApplyToAllButton.setClickable(false);
		mProceedButton.setClickable(false);
		mSpinnerMonFrom.setClickable(false);
		mSpinnerMonTo.setClickable(false);
		mSpinnerTueFrom.setClickable(false);
		mSpinnerTueTo.setClickable(false);
		mSpinnerWedFrom.setClickable(false);
		mSpinnerWedTo.setClickable(false);
		mSpinnerThuFrom.setClickable(false);
		mSpinnerThuTo.setClickable(false);
		mSpinnerFriFrom.setClickable(false);
		mSpinnerFriTo.setClickable(false);
		mSpinnerSatFrom.setClickable(false);
		mSpinnerSatTo.setClickable(false);
		mSpinnerSunFrom.setClickable(false);
		mSpinnerSunTo.setClickable(false);
	}
	
	private int getItemPosition(String value){
		for(int i=0;i<mClockTimes.length;i++){
			if(value.equalsIgnoreCase(mClockTimes[i]))
				return i;
		}
		return -1;
	}
	
	public void populateSavedData() throws JSONException{
		int fromPosition = -1;
		int toPosition = -1;
		String fromValue;
		String toValue;
		JSONObject businessJson;
		if(UserProfile.isAdmin)
			businessJson = ((AdminActivity) mContext).downloadedJson;
		else
			businessJson = ((CAActivity) mContext).downloadedJson;
		if (businessJson.length() == 0)
			return;
		if(businessJson.has("business_description")){
			String businessDescription = businessJson.getString("business_description");
			mBusinessDescriptionText.setText(businessDescription);
			mViewBusinessDescription.setText(businessDescription);
			mViewBusinessDescription.setVisibility(TextView.VISIBLE);
			mViewBusinessDescriptionSeparator.setVisibility(View.VISIBLE);
			mViewBusinessDescriptionTitle.setVisibility(TextView.VISIBLE);
			
		}
		
		JSONObject hours_of_operation = businessJson.getJSONObject("hours_of_operation");
		JSONArray keys = hours_of_operation.names();
			for(int i=0;i<keys.length();i++){
				JSONArray day_object = hours_of_operation.getJSONArray(keys.getString(i));
				fromValue = day_object.getString(0);
				toValue = day_object.getString(1);
				fromPosition = getItemPosition(fromValue);
				toPosition = getItemPosition(toValue);
				
				if(keys.getString(i).equalsIgnoreCase("monday")){
					setBusinessHoursView(mViewMondayLayout,keys.getString(i),fromValue,toValue);
					mSelectMonday.setChecked(true);
					mSpinnerMonFrom.setSelection(fromPosition);
					mSpinnerMonTo.setSelection(toPosition);
				}else if(keys.getString(i).equalsIgnoreCase("tuesday")){
					setBusinessHoursView(mViewTuesDayLayout,keys.getString(i),fromValue,toValue);
					mSelectTuesDay.setChecked(true);
					mSpinnerTueFrom.setSelection(fromPosition);
					mSpinnerTueTo.setSelection(toPosition);
				}else if(keys.getString(i).equalsIgnoreCase("wednesday")){
					setBusinessHoursView(mViewWednessDayLayout,keys.getString(i),fromValue,toValue);
					mSelectWednesDay.setChecked(true);
					mSpinnerWedFrom.setSelection(fromPosition);
					mSpinnerWedTo.setSelection(toPosition);
				}else if(keys.getString(i).equalsIgnoreCase("thursday")){
					setBusinessHoursView(mViewThursdayLayout,keys.getString(i),fromValue,toValue);
					mSelectThursDay.setChecked(true);
					mSpinnerThuFrom.setSelection(fromPosition);
					mSpinnerThuTo.setSelection(toPosition);
				}else if(keys.getString(i).equalsIgnoreCase("friday")){
					setBusinessHoursView(mViewFridayLayout,keys.getString(i),fromValue,toValue);
					mSelectFriDay.setChecked(true);
					mSpinnerFriFrom.setSelection(fromPosition);
					mSpinnerFriTo.setSelection(toPosition);
				}else if(keys.getString(i).equalsIgnoreCase("saturday")){
					setBusinessHoursView(mViewSaturDayLayout,keys.getString(i),fromValue,toValue);
					mSelectSaturDay.setChecked(true);
					mSpinnerSatFrom.setSelection(fromPosition);
					mSpinnerSatTo.setSelection(toPosition);
				}else if(keys.getString(i).equalsIgnoreCase("sunday")){
					setBusinessHoursView(mViewSundayLayout,keys.getString(i),fromValue,toValue);
					mSelectSunDay.setChecked(true);
					mSpinnerSunFrom.setSelection(fromPosition);
					mSpinnerSunTo.setSelection(toPosition);
				}
				
				
			}
		
		
		
		if(businessJson.has("status")){
			((DataCollectionFragment) getParentFragment()).isImageDetailsScreenEnabled = true;
			String status = businessJson.getString("status");
			if(status.equalsIgnoreCase("BUSINESS_VERIFIED_COMPLETE")){
				mEditLayout.setVisibility(ScrollView.GONE);
				mViewLayout.setVisibility(ScrollView.VISIBLE);
				if(UserProfile.isAdmin)
					mActivity.invalidateOptionsMenu();
				//disableEdit();
			}else if(status.equalsIgnoreCase("BUSINESS_VERIFIED_INCOMPLETE")){
				if(UserProfile.isAdmin){
					mEditLayout.setVisibility(ScrollView.GONE);
					mViewLayout.setVisibility(ScrollView.VISIBLE);
					mActivity.invalidateOptionsMenu();
				}
			}else if(status.equalsIgnoreCase("TRANSIENT")){
				if(UserProfile.isAdmin){
					mEditLayout.setVisibility(ScrollView.GONE);
					mViewLayout.setVisibility(ScrollView.VISIBLE);
					mActivity.invalidateOptionsMenu();
				}
			}
		}
	}
	
	private void setBusinessHoursView(LinearLayout layout,String day,String from,String to){
		layout.setVisibility(LinearLayout.VISIBLE);
		TextView day_text = (TextView)layout.findViewById(R.id.day);
		TextView start_time_text = (TextView) layout.findViewById(R.id.start);
		TextView end_time_text = (TextView) layout.findViewById(R.id.end);
		day_text.setText(day);
		start_time_text.setText(from);
		end_time_text.setText(to);
		
	}

}
