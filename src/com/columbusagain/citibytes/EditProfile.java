package com.columbusagain.citibytes;

import org.json.JSONException;
import org.json.JSONObject;

import com.columbusagain.citibytes.helper.Constants;
import com.columbusagain.citibytes.helper.HttpPost;
import com.columbusagain.citibytes.helper.UserProfile;
import com.columbusagain.citibytes.util.NetworkChecker;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class EditProfile extends Fragment implements OnClickListener {
	
	private Context mContext;
	
	private Activity mActivity;
	
	private ActionBar mActionBar;
	
	private Button mSubmitButton;
	
	private EditText mMobileNumberEditText,mPhoneNumberEditText;
	
	private View mRootView;
	
	private static Dialog mProgressDialog;
	
	public EditProfile(){
		
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.mActivity = activity;
		this.mContext = activity;
		
		
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		if(UserProfile.isAdmin)
			mActionBar = ((AdminActivity)mActivity).getSupportActionBar();
		else		
			mActionBar = ((CAActivity)mActivity).getSupportActionBar();
		mActionBar.setTitle("Edit Profile");
		setHasOptionsMenu(true);
		mRootView = inflater.inflate(R.layout.activity_contact_details, container, false);
		mSubmitButton = (Button) mRootView.findViewById(R.id.submit_button);
		mMobileNumberEditText = (EditText) mRootView.findViewById(R.id.mobile_contact);
		mPhoneNumberEditText = (EditText) mRootView.findViewById(R.id.phone_contact);
		mMobileNumberEditText.setText(UserProfile.PERSONAL_NUMBER);
		mPhoneNumberEditText.setText(UserProfile.BUSINESS_NUMBER);
		mSubmitButton.setOnClickListener(this);
		mProgressDialog = new Dialog(mActivity,R.style.ProgressDialog);
		 mProgressDialog.setContentView(R.layout.progress_dialog);
		return mRootView;
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.home, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}
	

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.submit_button:
			if(!isValidInput())
				break;
			boolean isNetworkConnected = NetworkChecker.isConnected(mActivity);
			if(!isNetworkConnected){
			}else{
			mProgressDialog.show();
				new EditUserProfile().execute();
			}
			break;
		}
	}
	
	private class EditUserProfile extends AsyncTask<Void, Void, String>{

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}
		
		@Override
		protected String doInBackground(Void... params) {
			HttpPost httpPost = new HttpPost(Constants.BASE_URL+"editUserProfile.php");
			httpPost.setParam("data", buildRequestJson());
			return httpPost.executePost();
		}
		
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if(mProgressDialog.isShowing()){
				mProgressDialog.cancel();
			}
			
			if(result != null){
				JSONObject responseJson;
				try {
					responseJson = new JSONObject(result);
					String status = responseJson.getString("status");
					if(status.equalsIgnoreCase("success")){
						Toast.makeText(mContext, "Profile updated successfully", Toast.LENGTH_LONG).show();
						if(UserProfile.isAdmin)
							((AdminActivity)mContext).startProfilePage();
						else
						((CAActivity)mContext).startProfilePage();
					}else{
						Toast.makeText(mContext, "Please try again", Toast.LENGTH_LONG).show();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}else{
				Toast.makeText(mContext, "No network", Toast.LENGTH_LONG).show();
			}
		}
		
	}
	
	private String buildRequestJson(){
		String mobile_contact = mMobileNumberEditText.getText().toString(); 
		String phone_contact = mPhoneNumberEditText.getText().toString();
		JSONObject requestJson = new JSONObject();
		try {
			requestJson.put("email_id", UserProfile.EMAIL_ID);
			requestJson.put("personal_number", mobile_contact);
			requestJson.put("business_number", phone_contact);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return requestJson.toString();
	}
	
	
	
	private boolean isValidInput(){
		String mobile_contact = mMobileNumberEditText.getText().toString();
		if(mobile_contact.length() == 0){
			Toast.makeText(mContext, "Please fill required fields", Toast.LENGTH_LONG).show();
			mMobileNumberEditText.requestFocus();
			
			return false;
		}
		if(mobile_contact.length()<10){
			Toast.makeText(mContext, "Invalid mobile number", Toast.LENGTH_LONG).show();
			mMobileNumberEditText.requestFocus();
			
			return false;
		}
		String phone_contact = mPhoneNumberEditText.getText().toString();
		if(phone_contact.length()<8){
			Toast.makeText(mContext, "Invalid contact number", Toast.LENGTH_LONG).show();
			mPhoneNumberEditText.requestFocus();
			return false;
		}
		return true;
	}

	public void onBackPressed() {
		if(UserProfile.isAdmin)
			((AdminActivity)mContext).startProfilePage();
		else
		((CAActivity)mContext).startProfilePage();
	}

}
