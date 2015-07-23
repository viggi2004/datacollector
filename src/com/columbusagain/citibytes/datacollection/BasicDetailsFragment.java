package com.columbusagain.citibytes.datacollection;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.columbusagain.citibytes.AdminActivity;
import com.columbusagain.citibytes.CAActivity;
import com.columbusagain.citibytes.ChainNamesActivity;
import com.columbusagain.citibytes.DataCollectionFragment;
import com.columbusagain.citibytes.R;
import com.columbusagain.citibytes.helper.Constants;
import com.columbusagain.citibytes.helper.HttpGet;
import com.columbusagain.citibytes.helper.HttpPost;
import com.columbusagain.citibytes.helper.UserProfile;

import android.R.integer;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class BasicDetailsFragment extends Fragment implements TextWatcher,
		OnClickListener, OnCheckedChangeListener, OnItemClickListener,
		OnFocusChangeListener {

	// private LinearLayout mMobileNumberRowLayout;
	
	private Button mViewNextButton;
	
	private LinearLayout mViewChainNameLayout,mViewEmailLayout,mViewWebsiteLayout,mViewLandmarkLayout,mViewMobileNumberLayout,mViewLandlineNumberLayout;
	
	private TextView mViewChainName,mViewAddressLine1,mViewAddressLine2,mViewCity,mViewPincode,mViewState,mViewContactPerson,mViewEmailId,mViewWebsite,mViewBusinessNameText,mViewlocationText;
	
	private ScrollView mEditLayout,mViewLayout;
	
	private TextView mStdCodeTextView;

	private String mSmsNumber = null;

	private String mCreatedTime;

	private String mCreatedByUser;

	private ImageView mResendOtpImageView;

	private Dialog mProgressDialog;

	private EditText mContactPersonNameEditText;

	private EditText mWebsiteEditText, mEmailEditText;

	private TextView mStateTextView;

	private EditText mAddressLine1, mAddressLine2;

	private EditText mBusinessNameEditText;

	private String mBusinessId;

	private Button mVerifyButton;

	private static final int CHAIN_NAMES = 100;

	private LinearLayout mLandmarksLayout, mContactDetailsLayout,
			mLandlineLayout;

	private EditText mLocationEditText;

	private TextView mCityEditText;
	
	private EditText mPinEditText;

	private Context mContext;

	private CheckBox mChainNameCheckBox;

	private ActionBar mActionBar;

	private Activity mActivity;

	private TextView mChainNameTextView;

	private ImageButton mAddContact, mRemoveContact;

	private ImageButton mAddLandline, mRemoveLandline;

	private MobileNumber mMobileNumberClass;
	
	private boolean isAdminEditMode = false;
	
	private MenuInflater mMenuInflater;

	private MobileNumberListAdapter mMobileNumbersAdapter;

	private ArrayList<MobileNumber> mMobileNumberList = new ArrayList<MobileNumber>();

	private Dialog otp_verification_dialog;
	
	public BasicDetailsFragment(){
		
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mActivity = activity;
		mContext = (Context) activity;
		
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if(UserProfile.isAdmin)
			((AdminActivity) mContext).mBusinessId = null;
		else
			((CAActivity) mContext).mBusinessId = null;
		setHasOptionsMenu(true);
		View rootView = inflater.inflate(
				R.layout.datacollection_fragment_basic_details, container,
				false);
		initViewLayout(rootView);
		if(UserProfile.isAdmin)
			mActionBar = ((AdminActivity) mActivity).getSupportActionBar();
		else
			mActionBar = ((CAActivity) mActivity).getSupportActionBar();
		mActionBar.setTitle("Collect");
		mEditLayout = (ScrollView) rootView.findViewById(R.id.edit_layout);
		mViewLayout = (ScrollView) rootView.findViewById(R.id.view_layout);
		mStdCodeTextView = (TextView)rootView.findViewById(R.id.std_code_text);
		mStdCodeTextView.setText(UserProfile.STD);
		mProgressDialog = new Dialog(getActivity(), R.style.ProgressDialog);
		mProgressDialog.setContentView(R.layout.progress_dialog);
		mLocationEditText = (EditText) rootView
				.findViewById(R.id.landmark_input);
		mChainNameTextView = (TextView) rootView.findViewById(R.id.chain_name);
		mLandmarksLayout = (LinearLayout) rootView
				.findViewById(R.id.landmarks_layout);
		mChainNameCheckBox = (CheckBox) rootView
				.findViewById(R.id.chain_name_check_box);
		mAddContact = (ImageButton) rootView.findViewById(R.id.add_mobile);
		mRemoveContact = (ImageButton) rootView
				.findViewById(R.id.remove_mobile);
		mContactDetailsLayout = (LinearLayout) rootView
				.findViewById(R.id.contact_details_layout);
		mLandlineLayout = (LinearLayout) rootView
				.findViewById(R.id.landline_layout);
		mAddLandline = (ImageButton) rootView.findViewById(R.id.add_landline);
		mRemoveLandline = (ImageButton) rootView
				.findViewById(R.id.add_landline);
		mCityEditText = (TextView) rootView.findViewById(R.id.city_text);
		mPinEditText = (EditText) rootView.findViewById(R.id.pin_text);
		mVerifyButton = (Button) rootView.findViewById(R.id.verify_button);
		mBusinessNameEditText = (EditText) rootView
				.findViewById(R.id.business_name_text);
		mAddressLine1 = (EditText) rootView.findViewById(R.id.address_line1);
		mAddressLine2 = (EditText) rootView.findViewById(R.id.address_line2);
		mStateTextView = (TextView) rootView.findViewById(R.id.state_text);
		mWebsiteEditText = (EditText) rootView
				.findViewById(R.id.website_edit_text);
		mEmailEditText = (EditText) rootView.findViewById(R.id.email_edit_text);
		mContactPersonNameEditText = (EditText) rootView
				.findViewById(R.id.contact_person_name);
		mResendOtpImageView = (ImageView) rootView
				.findViewById(R.id.resend_sms_img_view);
		mStateTextView.setText(UserProfile.STATE);
		mCityEditText.setText(UserProfile.CITY);
		mPinEditText.setText(Constants.PIN);
		UserProfile.SELECTED_PIN = Constants.PIN;
		mVerifyButton.setOnClickListener(this);
		mAddLandline.setOnClickListener(this);
		mRemoveLandline.setOnClickListener(this);
		mAddContact.setOnClickListener(this);
		mRemoveContact.setOnClickListener(this);
		mLocationEditText.addTextChangedListener(this);
		mLocationEditText.setOnFocusChangeListener(this);
		mChainNameTextView.setOnClickListener(this);
		mChainNameCheckBox.setOnCheckedChangeListener(this);
		mResendOtpImageView.setOnClickListener(this);
		try {
			populateSavedData();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return rootView;
	}
	
	private void initViewLayout(View rootView){
		mViewChainName = (TextView) rootView.findViewById(R.id.view_chain_name);
		mViewAddressLine1 = (TextView) rootView.findViewById(R.id.view_address_line_1);
		mViewAddressLine2 = (TextView) rootView.findViewById(R.id.view_address_line_2);
		mViewCity = (TextView) rootView.findViewById(R.id.view_city);
		mViewPincode = (TextView) rootView.findViewById(R.id.view_pincode);
		mViewState = (TextView) rootView.findViewById(R.id.view_state);
		mViewContactPerson = (TextView) rootView.findViewById(R.id.view_contact_person);
		mViewEmailId = (TextView) rootView.findViewById(R.id.view_email_id);
		mViewWebsite = (TextView) rootView.findViewById(R.id.view_website);
		mViewChainNameLayout = (LinearLayout) rootView.findViewById(R.id.view_chain_name_layout);
		mViewEmailLayout = (LinearLayout) rootView.findViewById(R.id.view_email_layout);
		mViewWebsiteLayout = (LinearLayout) rootView.findViewById(R.id.view_website_layout);
		mViewLandmarkLayout = (LinearLayout) rootView.findViewById(R.id.view_landmark_layout);
		mViewMobileNumberLayout = (LinearLayout) rootView.findViewById(R.id.view_mobile_number_layout);
		mViewLandlineNumberLayout = (LinearLayout) rootView.findViewById(R.id.view_landline_layout);
		mViewBusinessNameText = (TextView) rootView.findViewById(R.id.view_business_name);
		mViewlocationText = (TextView) rootView.findViewById(R.id.view_location_text);
		mViewNextButton = (Button) rootView.findViewById(R.id.view_next);
		mViewNextButton.setOnClickListener(this);
		mViewState.setText(UserProfile.STATE);
		mViewCity.setText(UserProfile.CITY);
	}

	private void customViewForLocation() {
		LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final View v = inflater.inflate(R.layout.blockquote_text, null, false);
		EditText tv = (EditText) v.findViewById(R.id.landmark_text);
		ImageButton delete_button = (ImageButton) v
				.findViewById(R.id.landmark_delete_button);
		delete_button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Log.i("BasicDetailsFragment", "Delete Landmark");
				mLandmarksLayout.removeView(v);
			}
		});
		String entered_text = mLocationEditText.getText().toString();
		entered_text = entered_text.replace(',', ' ').trim();
		tv.setText(entered_text);
		mLandmarksLayout.addView(v);
		mLocationEditText.setText("");
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		mMenuInflater = inflater;
		inflater.inflate(R.menu.home, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
		case R.id.action_edit:
			((DataCollectionFragment)getParentFragment()).editModeSetup();
			isAdminEditMode = true;
			((DataCollectionFragment) getParentFragment()).isLocationScreenEnabled = false;
			mActivity.invalidateOptionsMenu();
			mEditLayout.setVisibility(ScrollView.VISIBLE);
			mViewLayout.setVisibility(ScrollView.GONE);
			break;
		}
		return super.onOptionsItemSelected(item);
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
	public void afterTextChanged(Editable text) {
		// TODO Auto-generated method stub
		alterLandmarkText();
		Log.i("EnteredText2", text.toString() + "//");

	}

	@Override
	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
			int arg3) {

	}

	@Override
	public void onTextChanged(CharSequence string, int arg1, int arg2, int arg3) {
		Log.i("EnteredText", string + "//");
		if (string.length() > 1) {
			if (string.charAt(string.length() - 1) == ',') {
				customViewForLocation();
				Log.i("EnteredText3", string + "//");
			}
		}

	}

	@Override
	public void onClick(View v) {
		LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		switch (v.getId()) {
		case R.id.chain_name:
			Intent intent = new Intent(getActivity(), ChainNamesActivity.class);
			getParentFragment().startActivityForResult(intent, 100);
			break;
		case R.id.add_mobile:

			final View view = inflater.inflate(R.layout.contact_details_row,
					null);
			ImageButton mContact = (ImageButton) view
					.findViewById(R.id.remove_mobile);
			mContact.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					mContactDetailsLayout.removeView(view);
				}
			});
			mContactDetailsLayout.addView(view);
			break;
		case R.id.remove_mobile:
			break;
		case R.id.add_landline:

			final View landline_view = inflater.inflate(
					R.layout.landline_layout, null);
			TextView mStdCode = (TextView) landline_view.findViewById(R.id.std_code_text);
			mStdCode.setText(UserProfile.STD);
			ImageButton mLandline = (ImageButton) landline_view
					.findViewById(R.id.remove_landline);
			mLandline.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					mLandlineLayout.removeView(landline_view);
				}
			});
			mLandlineLayout.addView(landline_view);
			break;

		case R.id.remove_landline:
			break;

		case R.id.verify_button:
			/*
			 * if( !isNoDuplicateMobile()|| !isNoDuplicateLandline()) break;
			 */
			if(UserProfile.isAdmin)
				((AdminActivity) mContext).disableKeyBoard();
			else
				((CAActivity) mContext).disableKeyBoard();
			getMobileNumbersList();
			//mSmsNumber = null;
			if (checkMandatoryFields()) {
				mProgressDialog.show();
				new UploadBasicdetails().execute(buildJson());
			}

			break;
			
		case R.id.view_next:
			((DataCollectionFragment) getParentFragment()).mViewPager
			.setCurrentItem(1);
			break;

		case R.id.resend_sms_img_view:
			/*
			 * if( !isNoDuplicateMobile()|| !isNoDuplicateLandline()) break;
			 */
			//mSmsNumber = null;
			final Dialog otp_verification_dialog = new Dialog(mContext);
			otp_verification_dialog
					.requestWindowFeature(Window.FEATURE_NO_TITLE);
			otp_verification_dialog
					.setContentView(R.layout.custom_dialog_sms_resend);
			getMobileNumbersList();
			mMobileNumbersAdapter = new MobileNumberListAdapter();
			ListView mobile_numbers_list = (ListView) otp_verification_dialog
					.findViewById(R.id.mobile_numbers_list);
			mobile_numbers_list.setAdapter(mMobileNumbersAdapter);
			mobile_numbers_list.setOnItemClickListener(this);
			Button resend_button = (Button) otp_verification_dialog
					.findViewById(R.id.resend_button);
			if (mBusinessId == null)
				resend_button.setText("Send");
			resend_button.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					otp_verification_dialog.cancel();
					if (checkMandatoryFields()) {
						// if(isNoDuplicateLandline() && isNoDuplicateMobile()){
						mProgressDialog.show();
						new UploadBasicdetails().execute(buildJson());
						// }
					}
				}
			});
			otp_verification_dialog.show();
			break;

		default:
			break;
		}

	}

	private String buildJson() {

		JSONObject businessData = new JSONObject();
		try {
			if (mBusinessId != null) {
				businessData.put("business_id", mBusinessId);
			}
			if (mCreatedByUser != null) {
				businessData.put("created_by_user", mCreatedByUser);
			}

			if (mCreatedTime != null) {
				businessData.put("created_time", mCreatedTime);
			}
			// businessData.put("sms_number", mSmsNumber);
			// Business name saved for future use from other screens
			if(UserProfile.isAdmin){
			((AdminActivity) mActivity).mBusinessName = mBusinessNameEditText
					.getText().toString();
			businessData.put("business_name",
					((AdminActivity) mActivity).mBusinessName);
			}else{
				((CAActivity) mActivity).mBusinessName = mBusinessNameEditText
						.getText().toString();
				businessData.put("business_name",
						((CAActivity) mActivity).mBusinessName);
			}
			UserProfile.SELECTED_PIN = mPinEditText.getText().toString();
			businessData.put("pincode", mPinEditText.getText().toString());
			businessData.put("city", UserProfile.CITY);
			JSONArray landmarks = getLandmarkArray();
			if (landmarks.length() > 0)
				businessData.put("landmark", landmarks);
			JSONArray landlinenumbers = getLandLineArray();
			if (landlinenumbers.length() > 0)
				businessData.put("landline_number", landlinenumbers);
			businessData.put("mobile_number", getMobileNumbersArray());
			String address_one = mAddressLine1.getText().toString();
			String address_two = mAddressLine2.getText().toString();
			/*if (mAddressLine2.getText().toString().length() > 0)
				address = address + "," + mAddressLine2.getText().toString();
			businessData.put("address", address);*/
			businessData.put("address_line_1", address_one );
			if(address_two.length()>0)
			businessData.put("address_line_2", address_two );
			if (mChainNameCheckBox.isChecked()
					&& mChainNameTextView.getText().toString().length() > 0)
				businessData.put("chain_name", mChainNameTextView.getText()
						.toString());
			businessData.put("state", mStateTextView.getText().toString());
			if (mEmailEditText.getText().length() > 0)
				businessData.put("email", mEmailEditText.getText().toString());
			if (mWebsiteEditText.getText().length() > 0)
				businessData.put("website", mWebsiteEditText.getText()
						.toString());
			businessData.put("contact_person_name", mContactPersonNameEditText
					.getText().toString());

		} catch (JSONException e) {
			e.printStackTrace();
		}
		Log.i("BasicDetailsFragment", "Built JSON : " + businessData.toString());
		return businessData.toString();
	}

	private void alterLandmarkText() {
		for (int i = 0; i < mLandmarksLayout.getChildCount(); i++) {
			View v = (LinearLayout) mLandmarksLayout.getChildAt(i);
			EditText landmark_text = (EditText) v
					.findViewById(R.id.landmark_text);
			if (landmark_text.length() == 0) {
				mLandmarksLayout.removeViewAt(i);
				i--;
			}
		}
	}

	private JSONArray getLandmarkArray() {
		JSONArray mLandmarksArray = new JSONArray();
		for (int i = 0; i < mLandmarksLayout.getChildCount(); i++) {
			View v = (LinearLayout) mLandmarksLayout.getChildAt(i);
			EditText landmark_text = (EditText) v
					.findViewById(R.id.landmark_text);
			if (landmark_text.length() > 0)
				mLandmarksArray.put(landmark_text.getText().toString());
		}
		if (mLocationEditText.getText().length() > 0)
			mLandmarksArray.put(mLocationEditText.getText().toString());
		return mLandmarksArray;
	}

	
	private void getMobileNumbersList() {
		Log.i("BasicDetailsFragment", "getMobileNumbersList");
		mMobileNumberList.clear();
		for (int i = 0; i < mContactDetailsLayout.getChildCount(); i++) {
			View v = (LinearLayout) mContactDetailsLayout.getChildAt(i);
			EditText mobile_number = (EditText) v
					.findViewById(R.id.mobile_number);
			MobileNumber mobile_number_class = new MobileNumber();
			if (mobile_number.getText().toString().length() == 10) {
				mobile_number_class.mobile_number = mobile_number.getText()
						.toString();
				if (mMobileNumberList.size() == 0) {
					if (mSmsNumber == null)
						mSmsNumber = mobile_number_class.mobile_number;
					else if(!isSmsNumberAvailable())
						mSmsNumber = mobile_number_class.mobile_number;
					mobile_number_class.isSelected = true;
				} else
					mobile_number_class.isSelected = false;
				mMobileNumberList.add(mobile_number_class);
			}
		}

		return;
	}
	
	private boolean isSmsNumberAvailable(){
		for (int i = 0; i < mContactDetailsLayout.getChildCount(); i++) {
			View v = (LinearLayout) mContactDetailsLayout.getChildAt(i);
			EditText mobile_number = (EditText) v
					.findViewById(R.id.mobile_number);
			if(mSmsNumber.equalsIgnoreCase(mobile_number.getText().toString())){
				return true;
			}
		}
		
		return false;
	}

	private JSONArray getMobileNumbersArray() {
		Log.i("BasicDetailsFragment", "getMobileNumbersArray");
		JSONArray mMobileNumbersArray = new JSONArray();
		for (int i = 0; i < mContactDetailsLayout.getChildCount(); i++) {
			View v = (LinearLayout) mContactDetailsLayout.getChildAt(i);
			EditText mobile_number = (EditText) v
					.findViewById(R.id.mobile_number);
			if (i == 0) {
				if (mSmsNumber == null)
					mSmsNumber = mobile_number.getText().toString();
			}
			mMobileNumbersArray.put(mobile_number.getText().toString());

		}
		return mMobileNumbersArray;
	}
	
	private boolean landlineNumberVlidation() {
		for (int i = 0; i < mLandlineLayout.getChildCount(); i++) {
			View v = (LinearLayout) mLandlineLayout.getChildAt(i);
			EditText landline_number = (EditText) v
					.findViewById(R.id.landline_number);
			if (landline_number.getText().toString().length() <5 && landline_number.getText().toString().length() >0 ) {
				Toast.makeText(mContext, "Landline number should be atleast 5 digits",
						Toast.LENGTH_LONG).show();
				landline_number.requestFocus();
				return false;
			}

		}
		return true;
	}

	private boolean mobileNumberValidation() {
		for (int i = 0; i < mContactDetailsLayout.getChildCount(); i++) {
			View v = (LinearLayout) mContactDetailsLayout.getChildAt(i);
			EditText mobile_number = (EditText) v
					.findViewById(R.id.mobile_number);
			if (mobile_number.getText().toString().length() != 10) {
				Toast.makeText(mContext, "Enter valid mobile number",
						Toast.LENGTH_LONG).show();
				mobile_number.requestFocus();
				return false;
			}

		}
		return true;
	}

	private boolean isNoDuplicateMobile() {
		for (int i = 0; i < mContactDetailsLayout.getChildCount(); i++) {
			String number1 = getMobileNumber(i);
			for (int j = 0; j < mContactDetailsLayout.getChildCount(); j++) {
				String number2 = getMobileNumber(j);
				if (i != j) {
					if (number1.equals(number2)) {
						if(UserProfile.isAdmin)
							((AdminActivity) mContext).disableKeyBoard();
						else
							((CAActivity) mContext).disableKeyBoard();
						Toast.makeText(mContext, "Duplicate Mobile Number",
								Toast.LENGTH_LONG).show();
						return false;
					}

				}
			}
		}
		return true;
	}

	private String getMobileNumber(int index) {
		View v = (LinearLayout) mContactDetailsLayout.getChildAt(index);
		EditText mobile_number = (EditText) v.findViewById(R.id.mobile_number);
		return mobile_number.getText().toString();
	}

	private boolean isNoDuplicateLandline() {
		for (int i = 0; i < mLandlineLayout.getChildCount(); i++) {
			String number1 = getLandlineNumber(i);
			for (int j = 0; j < mLandlineLayout.getChildCount(); j++) {
				String number2 = getLandlineNumber(j);
				if (i != j) {
					if (number1.equals(number2)) {
						if(UserProfile.isAdmin)
							((AdminActivity) mContext).disableKeyBoard();
						else
							((CAActivity) mContext).disableKeyBoard();
						Toast.makeText(mContext, "Duplicate Landline Number",
								Toast.LENGTH_LONG).show();
						return false;
					}
				}
			}
		}
		return true;
	}

	private String getLandlineNumber(int index) {
		View v = (LinearLayout) mLandlineLayout.getChildAt(index);
		EditText mobile_number = (EditText) v
				.findViewById(R.id.landline_number);
		return mobile_number.getText().toString();
	}

	private JSONArray getLandLineArray() {
		JSONArray mLandLineArray = new JSONArray();
		for (int i = 0; i < mLandlineLayout.getChildCount(); i++) {
			View v = (LinearLayout) mLandlineLayout.getChildAt(i);
			EditText mobile_number = (EditText) v
					.findViewById(R.id.landline_number);
			if (mobile_number.getText().toString().length() > 0)
				mLandLineArray.put(mobile_number.getText().toString());

		}
		return mLandLineArray;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.i("BasicDetailsFragment", "onActivityResult:" + requestCode);
		if (resultCode == getActivity().RESULT_OK) {
			switch (requestCode) {
			case CHAIN_NAMES:
				mChainNameTextView.setText(data.getStringExtra("name"));
				Log.i("BasicDetailsFragment", "onActivityResult:" + requestCode);
				break;
			default:
				break;
			}

		}

	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		switch (buttonView.getId()) {
		case R.id.chain_name_check_box:
			if (isChecked) {
				mChainNameTextView.setVisibility(TextView.VISIBLE);
			} else {
				mChainNameTextView.setText("");
				mChainNameTextView.setVisibility(TextView.GONE);
			}
			break;
		}

	}

	private class UploadBasicdetails extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			HttpPost httpPost = new HttpPost(Constants.BASE_URL
					+ "saveSMSCoreAttributes.php");
			httpPost.setParam("json", params[0]);
			httpPost.setParam("email_id", UserProfile.EMAIL_ID);
			httpPost.setParam("sms_number", mSmsNumber);
			String response = httpPost.executePost();
			return response;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (mProgressDialog.isShowing()) {
				mProgressDialog.cancel();
			}
			Log.i("BasicDetailsActivity", "Sms Number : " + mSmsNumber);
			Log.i("BasicDetailsActivity", "Response : " + result);
			if (result != null) {
				JSONObject result_json;
				try {
					result_json = new JSONObject(result);
					String status = result_json.getString("status");
					if ("success".equals(status)) {

						mBusinessId = result_json.getString("business_id");
						if(UserProfile.isAdmin)
							((AdminActivity) mContext).mBusinessId = mBusinessId;
						else
							((CAActivity) mContext).mBusinessId = mBusinessId;
						mCreatedByUser = result_json
								.getString("created_by_user");
						mCreatedTime = result_json.getString("created_time");
						otp_verification_dialog = new Dialog(mContext);
						otp_verification_dialog
								.requestWindowFeature(Window.FEATURE_NO_TITLE);
						otp_verification_dialog
								.setContentView(R.layout.custom_dialog_otp_verification);
						final EditText otp_input = (EditText) otp_verification_dialog
								.findViewById(R.id.verification_code_input);
						Button ok_button = (Button) otp_verification_dialog
								.findViewById(R.id.ok_button);
						Button cancel_button = (Button) otp_verification_dialog
								.findViewById(R.id.cancel_button);
						ok_button.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								mProgressDialog.show();
								new VerifyOtp().execute(otp_input.getText()
										.toString());
							}
						});
						cancel_button.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								otp_verification_dialog.cancel();
							}
						});
						otp_verification_dialog.show();
					} else {
						String error = result_json.getString("error");
						Toast.makeText(mContext, error, Toast.LENGTH_LONG)
								.show();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}

			} else {
				Toast.makeText(mContext, "No Network", Toast.LENGTH_LONG)
						.show();
				Log.i("BasicDetailsActivity", "Response : error");
			}

		}

	}

	private class MobileNumberListAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return mMobileNumberList.size();
		}

		@Override
		public Object getItem(int position) {
			return mMobileNumberList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder = null;
			if (convertView == null) {
				LayoutInflater inflater = mActivity.getLayoutInflater();
				convertView = inflater.inflate(R.layout.selectcity_row_layout,
						parent, false);
				viewHolder = new ViewHolder();
				viewHolder.mobileNumberTextView = (TextView) convertView
						.findViewById(R.id.area_text_view);
				viewHolder.checkImage = (ImageView) convertView
						.findViewById(R.id.check_image);
				convertView.setTag(viewHolder);
			} else
				viewHolder = (ViewHolder) convertView.getTag();
			viewHolder.mobileNumberTextView.setText(mMobileNumberList
					.get(position).mobile_number);
			if(mSmsNumber != null)
			if (mMobileNumberList.get(position).mobile_number.equalsIgnoreCase(mSmsNumber)) {
				viewHolder.checkImage.setVisibility(ImageView.VISIBLE);
			} else {
				viewHolder.checkImage.setVisibility(ImageView.GONE);

			}

			return convertView;
		}

		private class ViewHolder {

			public TextView mobileNumberTextView;
			public ImageView checkImage;
		}

	}

	private class MobileNumber {
		private String mobile_number;
		private boolean isSelected;
	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View view,
			int position, long id) {
		for (int i = 0; i < mMobileNumberList.size(); i++) {
			mMobileNumberList.get(i).isSelected = false;
		}
		mMobileNumberList.get(position).isSelected = true;
		mSmsNumber = mMobileNumberList.get(position).mobile_number;
		mMobileNumbersAdapter.notifyDataSetChanged();

	}

	private class VerifyOtp extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			HttpPost httpPost = new HttpPost(Constants.BASE_URL
					+ "verifyOTP.php");
			httpPost.setParam("business_id", mBusinessId);
			httpPost.setParam("otp", params[0]);
			String result = httpPost.executePost();
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
				mProgressDialog.dismiss();
			Log.i("BasicDetailsFragment", "Response : " + result);
			if (result != null) {
				try {
					JSONObject result_json = new JSONObject(result);
					String status = result_json.getString("status");
					if ("success".equals(status)) {
						((DataCollectionFragment) getParentFragment()).isLocationScreenEnabled = true;
						if(UserProfile.isAdmin){
							((DataCollectionFragment) getParentFragment())
							.setBusinessName(((AdminActivity) mActivity).mBusinessName);
							//isAdminEditMode = false;
							//mEditLayout.setVisibility(ScrollView.GONE);
							//mViewLayout.setVisibility(ScrollView.VISIBLE);
							
						}else
						((DataCollectionFragment) getParentFragment())
								.setBusinessName(((CAActivity) mActivity).mBusinessName);
						((DataCollectionFragment) getParentFragment()).mViewPager
								.setCurrentItem(1);
						/*Log.i("BusinessId",
								((CAActivity) mContext).mBusinessId);*/
						// DataCollectionFragment.setCurrentPage(1);
						otp_verification_dialog.cancel();
					} else {
						String error = result_json.getString("error");
						Toast.makeText(mContext, error, Toast.LENGTH_LONG)
								.show();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}

			} else {
				Toast.makeText(mContext, "No Network!", Toast.LENGTH_LONG)
						.show();
			}
		}

	}

	private boolean checkMandatoryFields() {
		if (UserProfile.EMAIL_ID == null) {
			Toast.makeText(mContext, "Email ID missed", Toast.LENGTH_LONG)
					.show();
			return false;
		}

		if (mBusinessNameEditText.getText().length() == 0) {
			Toast.makeText(mContext, "Enter Business Name", Toast.LENGTH_LONG)
					.show();
			mBusinessNameEditText.requestFocus();
			return false;
		}
		
		if(mAddressLine1.getText().toString().length() == 0){
			return false;
		}
		/*if (mAddressLine1.getText().toString().length() == 0
				&& mAddressLine2.getText().toString().length() == 0) {
			Toast.makeText(mContext, "Address is missed", Toast.LENGTH_LONG)
					.show();
			mAddressLine1.requestFocus();
			return false;
		}*/
		if (UserProfile.CITY == null) {
			Toast.makeText(mContext, "City missed", Toast.LENGTH_LONG).show();
			return false;
		}
		if (mPinEditText.getText().toString().length()<6) {
			Toast.makeText(mContext, "Invalid PIN", Toast.LENGTH_LONG).show();
			return false;
		}
		if (mStateTextView.getText().length() == 0) {
			Toast.makeText(mContext, "Enter state", Toast.LENGTH_LONG).show();
			mStateTextView.requestFocus();
			return false;
		}
		
		if (mMobileNumberList.size() == 0) {
			Toast.makeText(mContext, "Invalid mobile number", Toast.LENGTH_LONG)
					.show();
			return false;
		}
		if (!mobileNumberValidation()) {
			return false;
		}
		if (mContactPersonNameEditText.getText().length() == 0) {
			Toast.makeText(mContext, "Enter contact person name",
					Toast.LENGTH_LONG).show();
			mContactPersonNameEditText.requestFocus();
			return false;

		}
		if(!landlineNumberVlidation()){
			return false;
		}
		
		if (mEmailEditText.getText().toString().length() > 0) {
			if(android.util.Patterns.EMAIL_ADDRESS.matcher(mEmailEditText.getText().toString()).matches()){
				// Continue
			}else{
				Toast.makeText(mContext, "Enter valid email address",
						Toast.LENGTH_LONG).show();
				mEmailEditText.requestFocus();
				return false;
			}
	
		}
		return true;
	}

	public void setBusinessName(String businessName) {
		Log.i("BasicDetailsFragment", businessName);
	}

	@Override
	public void onFocusChange(View view, boolean isFocused) {
		switch (view.getId()) {
		case R.id.landmark_input:
			if (isFocused) {

			} else {
				customViewForLocation();
			}
			break;
		}

	}

	public void disableEdit() {
		mBusinessNameEditText.setFocusable(false);
		mChainNameCheckBox.setClickable(false);
		mChainNameTextView.setClickable(false);
		mAddressLine1.setFocusable(false);
		mAddressLine2.setFocusable(false);
		mResendOtpImageView.setClickable(false);
		mEmailEditText.setFocusable(false);
		mWebsiteEditText.setFocusable(false);
		mVerifyButton.setClickable(false);
		mContactPersonNameEditText.setFocusable(false);
		mLocationEditText.setFocusable(false);
		disableLandlineLayout();
		disableLandmarkLayout();
		disableMobileNumberLayout();

	}
	
	private void disableMobileNumberLayout(){
		for(int i=0;i<mContactDetailsLayout.getChildCount();i++){
		View view = mContactDetailsLayout.getChildAt(i);
		EditText mobile_number_text = (EditText) view.findViewById(R.id.mobile_number);
		ImageButton mAdd = (ImageButton) view.findViewById(R.id.add_mobile);
		ImageButton mContact = (ImageButton) view
				.findViewById(R.id.remove_mobile);
		mobile_number_text.setFocusable(false);
		mContact.setClickable(false);
		mAdd.setClickable(false);
		}
		
		
	}
	
	private void disableLandmarkLayout(){
		
		for(int i=0;i<mLandmarksLayout.getChildCount();i++){
			View view = mLandmarksLayout.getChildAt(i);
			EditText tv = (EditText) view.findViewById(R.id.landmark_text);
			ImageButton delete_button = (ImageButton) view
					.findViewById(R.id.landmark_delete_button);
			tv.setFocusable(false);
			delete_button.setClickable(false);
		}
	}
		
	
	private void disableLandlineLayout(){
		for (int i = 0; i < mLandlineLayout.getChildCount(); i++) {
			View view = mLandlineLayout.getChildAt(i);
			EditText landline_number_text = (EditText) view.findViewById(R.id.landline_number);
			ImageButton mAdd = (ImageButton) view.findViewById(R.id.add_landline);
			ImageButton mLandline = (ImageButton) view
					.findViewById(R.id.remove_landline);
			landline_number_text.setFocusable(false);
			mLandline.setClickable(false);
			mAdd.setClickable(false);
		}		
	}

	public void populateSavedData() throws JSONException {
		JSONObject businessJson;
		if(UserProfile.isAdmin)
			businessJson = ((AdminActivity) mActivity).downloadedJson;
		else			
			businessJson = ((CAActivity) mActivity).downloadedJson;
		if (businessJson.length() == 0){
			return;
		}		
		
		if(businessJson.has("business_name")){
		String businessName = businessJson.getString("business_name");
		mBusinessNameEditText.setText(businessName);
		mViewBusinessNameText.setText(businessName);
		
		}

		if (businessJson.has("chain_name")) {
			mChainNameCheckBox.setChecked(true);
			String chain_name = businessJson.getString("chain_name");
			mChainNameTextView.setText(chain_name);
			mViewChainName.setText(chain_name);
			mViewChainNameLayout.setVisibility(LinearLayout.VISIBLE);
		}

		if(businessJson.has("landmark")){
		JSONArray landmarkArr = businessJson.getJSONArray("landmark");
		populateLandmarkText(landmarkArr);
		populateLandmarkView(landmarkArr);
		}
		if(businessJson.has("mobile_number")){
		JSONArray mobileNumberArr = businessJson.getJSONArray("mobile_number");		
		populateMobileNumbers(mobileNumberArr);
		populateMobileView(mobileNumberArr);
		}
		if(businessJson.has("created_by_user")){
		mCreatedByUser = businessJson.getString("created_by_user");
		}
		if(businessJson.has("created_time")){
		mCreatedTime = businessJson.getString("created_time");
		}
		
		if(businessJson.has("landline_number")){
			JSONArray landlineLineArr = businessJson.getJSONArray("landline_number");
			populateLandlineNumbers(landlineLineArr);
			populateLandlineView(landlineLineArr);
		}
		if(businessJson.has("contact_person_name")){
		String contact_person = businessJson.getString("contact_person_name");
		mContactPersonNameEditText.setText(contact_person);
		mViewContactPerson.setText(contact_person);
		}
		
		if(businessJson.has("business_id")){
		mBusinessId = businessJson.getString("business_id");
		if(UserProfile.isAdmin)
			((AdminActivity)mContext).mBusinessId = mBusinessId;
		else
			((CAActivity)mContext).mBusinessId = mBusinessId;
		}
		
		if(businessJson.has("address_line_1")){
		String address_line_one = businessJson.getString("address_line_1");
		mAddressLine1.setText(address_line_one);
		mViewAddressLine1.setText(address_line_one);
		}
		
		if(businessJson.has("address_line_2")){
			String address_line_two = businessJson.getString("address_line_2");
			mAddressLine2.setText(address_line_two);
			mViewAddressLine2.setText(address_line_two);
			mViewAddressLine2.setVisibility(TextView.VISIBLE);
		}
		if(businessJson.has("email")){
		String email = businessJson.getString("email");
		mEmailEditText.setText(email);
		mViewEmailId.setText(email);
		mViewEmailLayout.setVisibility(LinearLayout.VISIBLE);
		}
		
		if(businessJson.has("pincode")){
			String pincode = businessJson.getString("pincode");
			mPinEditText.setText(pincode);
			mViewPincode.setText(pincode);
			mViewlocationText.setText(pincode+","+UserProfile.CITY);
		}
		if(businessJson.has("website")){
		String website = businessJson.getString("website");
		mWebsiteEditText.setText(website);
		mViewWebsite.setText(website);
		mViewWebsiteLayout.setVisibility(LinearLayout.VISIBLE);
		}
		
		if(businessJson.has("status")){
			if(UserProfile.isAdmin)
				((AdminActivity)mContext).mBusinessName = businessJson.getString("business_name");
			else
				((CAActivity)mContext).mBusinessName = businessJson.getString("business_name");
			String status = businessJson.getString("status");
			if(status.equalsIgnoreCase("BUSINESS_VERIFIED_COMPLETE")){
				if(UserProfile.isAdmin){
					//isAdminEditMode = false;
					mActivity.invalidateOptionsMenu();
					if(isAdminEditMode){
						mEditLayout.setVisibility(ScrollView.VISIBLE);
						mViewLayout.setVisibility(ScrollView.GONE);
					}else{
						mEditLayout.setVisibility(ScrollView.GONE);
						mViewLayout.setVisibility(ScrollView.VISIBLE);
						((DataCollectionFragment) getParentFragment()).isLocationScreenEnabled = true;
					}
				}else{
				
					mEditLayout.setVisibility(ScrollView.GONE);
					mViewLayout.setVisibility(ScrollView.VISIBLE);
					((DataCollectionFragment) getParentFragment()).isLocationScreenEnabled = true;
				}
				//disableEdit();
				
			}else if(status.equalsIgnoreCase("BUSINESS_VERIFIED_INCOMPLETE")){
				if(UserProfile.isAdmin){
					//isAdminEditMode = false;
					mActivity.invalidateOptionsMenu();
					if(isAdminEditMode){
						mEditLayout.setVisibility(ScrollView.VISIBLE);
						mViewLayout.setVisibility(ScrollView.GONE);
						//((DataCollectionFragment) getParentFragment()).isLocationScreenEnabled = false;
					}else{
						mEditLayout.setVisibility(ScrollView.GONE);
						mViewLayout.setVisibility(ScrollView.VISIBLE);
						((DataCollectionFragment) getParentFragment()).isLocationScreenEnabled = true;
					}
				}else{
					((DataCollectionFragment) getParentFragment()).isLocationScreenEnabled = true;
					mEditLayout.setVisibility(ScrollView.GONE);
					mViewLayout.setVisibility(ScrollView.VISIBLE);
				}
				
				//disableEdit();
				
			}else if(status.equalsIgnoreCase("TRANSIENT")){
				if(UserProfile.isAdmin){
					/*if(isAdminEditMode)
						((DataCollectionFragment) getParentFragment()).isLocationScreenEnabled = false;
					else
						((DataCollectionFragment) getParentFragment()).isLocationScreenEnabled = true;*/
					//isAdminEditMode = false;
					mActivity.invalidateOptionsMenu();
					if(isAdminEditMode){
						mEditLayout.setVisibility(ScrollView.VISIBLE);
						mViewLayout.setVisibility(ScrollView.GONE);
					}else{
						mEditLayout.setVisibility(ScrollView.GONE);
						mViewLayout.setVisibility(ScrollView.VISIBLE);
						((DataCollectionFragment) getParentFragment()).isLocationScreenEnabled = true;
					}
				}else{
					((DataCollectionFragment) getParentFragment()).isLocationScreenEnabled = false;
				}
			}
		}
		
	}
	
	private void populateLandmarkView(JSONArray landmarkArr) throws JSONException{
		LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		for (int i = 0; i < landmarkArr.length(); i++) {
			final View v = mViewLandmarkLayout.getChildAt(i);
			if(v!=null){
				TextView view_text = (TextView)v.findViewById(R.id.text);
				view_text.setText(landmarkArr.getString(i));
			}else{
			Log.i("BasicDetailsFragment", "landmark added");
		final View viewlayout = inflater.inflate(R.layout.view_layout_text_view,null,false);
		TextView view_text = (TextView)viewlayout.findViewById(R.id.text);
		view_text.setText(landmarkArr.getString(i));
		mViewLandmarkLayout.addView(viewlayout);
		}
		}
	}
	
	private void populateMobileView(JSONArray mobileArr) throws JSONException{
		LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		for (int i = 0; i < mobileArr.length(); i++) {
			final View v = mViewMobileNumberLayout.getChildAt(i);
			if(v!=null){
				TextView view_text = (TextView)v.findViewById(R.id.text);
				view_text.setText("+91"+mobileArr.getString(i));
			}else{
			Log.i("BasicDetailsFragment", "mobile number added");
			final View viewlayout = inflater.inflate(R.layout.view_layout_text_view,null,false);
			TextView view_text = (TextView)viewlayout.findViewById(R.id.text);
			view_text.setText("+91"+mobileArr.getString(i));
			mViewMobileNumberLayout.addView(viewlayout);
			}
		}
	}
	
	private void populateLandlineView(JSONArray landlineArr) throws JSONException{
		LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		for (int i = 0; i < landlineArr.length(); i++) {
			final View v = mViewLandlineNumberLayout.getChildAt(i);
			if(v!=null){
				TextView view_text = (TextView)v.findViewById(R.id.text);
				view_text.setText(UserProfile.STD+landlineArr.getString(i));
			}else{
			Log.i("BasicDetailsFragment", "landline number added");
			final View viewlayout = inflater.inflate(R.layout.view_layout_text_view,null,false);
			TextView view_text = (TextView)viewlayout.findViewById(R.id.text);
			view_text.setText(UserProfile.STD+landlineArr.getString(i));
			mViewLandlineNumberLayout.addView(viewlayout);
			}
		}
	}

	private void populateLandmarkText(JSONArray landmarkArr)
			throws JSONException {
		LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		for (int i = 0; i < landmarkArr.length(); i++) {
			final View v = mLandmarksLayout.getChildAt(i);
			if(v!=null){
				EditText landmarkText = (EditText) v.findViewById(R.id.landmark_text);
				landmarkText.setText(landmarkArr.getString(i));
				ImageButton delete_button = (ImageButton) v
						.findViewById(R.id.landmark_delete_button);
				delete_button.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View view) {
						Log.i("BasicDetailsFragment", "Delete Landmark");
						mLandmarksLayout.removeView(v);
					}
				});
		}else{
			final View view = inflater.inflate(R.layout.blockquote_text,null);
			EditText tv = (EditText) view.findViewById(R.id.landmark_text);
			ImageButton delete_button = (ImageButton) view
					.findViewById(R.id.landmark_delete_button);
			delete_button.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Log.i("BasicDetailsFragment", "Delete Landmark");
					mLandmarksLayout.removeView(view);
				}
			});
			tv.setText(landmarkArr.getString(i));
			mLandmarksLayout.addView(view);
			
		}
			
		}

	}

	private void populateMobileNumbers(JSONArray mobileArr) throws JSONException {
		View default_view = mContactDetailsLayout.getChildAt(0);
		EditText primary_mobile_number_text = (EditText) default_view.findViewById(R.id.mobile_number);
		primary_mobile_number_text.setText(mobileArr.getString(0));
		
		LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		for (int i = 1; i < mobileArr.length(); i++) {
			View v = mContactDetailsLayout.getChildAt(i);
			if(v!=null){
				EditText landmarkText = (EditText) v.findViewById(R.id.mobile_number);
				landmarkText.setText(mobileArr.getString(i));
		}else{
			final View view = inflater.inflate(R.layout.contact_details_row,
					null);
			EditText mobile_number_text = (EditText) view.findViewById(R.id.mobile_number);
				mobile_number_text.setText(mobileArr.getString(i));
			ImageButton mContact = (ImageButton) view
					.findViewById(R.id.remove_mobile);
			mContact.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					mContactDetailsLayout.removeView(view);
				}
			});
			mContactDetailsLayout.addView(view);

		}
			
		}
	}

	private void populateLandlineNumbers(JSONArray landlineArr) throws JSONException {
		View default_view = mLandlineLayout.getChildAt(0);
		EditText primary_mobile_number_text = (EditText) default_view.findViewById(R.id.landline_number);
		primary_mobile_number_text.setText(landlineArr.getString(0));
		
		LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		for (int i = 1; i < landlineArr.length(); i++) {
			
			View v = mLandlineLayout.getChildAt(i);
			if(v!=null){
				EditText landmarkText = (EditText) v.findViewById(R.id.landline_number);
				TextView std_code_text = (TextView) v.findViewById(R.id.std_code_text);
				landmarkText.setText(landlineArr.getString(i));
				std_code_text.setText(UserProfile.STD);
		}else{
			
			final View landline_view = inflater.inflate(
					R.layout.landline_layout, null);
			EditText landline_number_text = (EditText) landline_view.findViewById(R.id.landline_number);			
			landline_number_text.setText(landlineArr.getString(i));
			TextView std_code_text = (TextView) landline_view.findViewById(R.id.std_code_text);
			std_code_text.setText(UserProfile.STD);
			ImageButton mLandline = (ImageButton) landline_view
					.findViewById(R.id.remove_landline);
			mLandline.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					mLandlineLayout.removeView(landline_view);
				}
			});
			mLandlineLayout.addView(landline_view);

		}
			
		}
	}
	
	

}
