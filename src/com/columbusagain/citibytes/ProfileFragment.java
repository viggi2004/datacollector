package com.columbusagain.citibytes;

import org.json.JSONException;
import org.json.JSONObject;

import com.columbusagain.citibytes.helper.Constants;
import com.columbusagain.citibytes.helper.HttpGet;
import com.columbusagain.citibytes.helper.JsonGetException;
import com.columbusagain.citibytes.helper.UserProfile;
import com.columbusagain.citibytes.util.NetworkChecker;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ProfileFragment extends Fragment {
	
	private ActionBar mActionBar;
	
	ImageView mProfileImageView;
	
	TextView mDisplayNameText;
	
	TextView mEmailIdText;
	
	TextView mSubTitleText;
	
	TextView mPersonalNumber;
	
	TextView mBusinessNumber;
	
	private View mRootView;
	
	private Context mContext;
	
	private Activity mActivity;
	
	private static Dialog mProgressDialog;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.mContext = activity;
		this.mActivity = activity;
		
		
		
	}
	
	public ProfileFragment(){
		
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		if(UserProfile.isAdmin)
			mActionBar = ((AdminActivity)mActivity).getSupportActionBar();
		else
			mActionBar = ((CAActivity)mActivity).getSupportActionBar();
		
		mActionBar.setTitle("Profile");
		setHasOptionsMenu(true);
		 mRootView = inflater.inflate(R.layout.fragment_profile, container, false);
		 mProfileImageView = (ImageView)mRootView.findViewById(R.id.profile_pic);
		 mProfileImageView.setImageBitmap(UserProfile.PROFILE_PICTURE);
		 mDisplayNameText = (TextView)mRootView.findViewById(R.id.display_name);
		 mEmailIdText = (TextView)mRootView.findViewById(R.id.email_id);
		 mSubTitleText = (TextView)mRootView.findViewById(R.id.sub_title);
		 mPersonalNumber = (TextView)mRootView.findViewById(R.id.personal_number);
		 mBusinessNumber = (TextView)mRootView.findViewById(R.id.business_number);
		 mProgressDialog = new Dialog(mActivity,R.style.ProgressDialog);
		 mProgressDialog.setContentView(R.layout.progress_dialog);
		 
		 boolean isNetworkConnected = NetworkChecker.isConnected(mActivity);
			if(!isNetworkConnected){
			}else{
			mProgressDialog.show();
				new GetUserProfile().execute();
			}
		 
		return mRootView;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
		case R.id.action_edit:
			if(UserProfile.isAdmin)
				((AdminActivity)mContext).editProfilePage();
			else
			((CAActivity)mContext).editProfilePage();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.fragment_profile, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}
	
	private class GetUserProfile extends AsyncTask<Void, Void, String>{
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(Void... params) {
			String response = null;
			HttpGet httpGet = new HttpGet(Constants.BASE_URL+"getUserProfile.php?email_id="+UserProfile.EMAIL_ID);
			try {
				response = httpGet.executeGet();
			} catch (JsonGetException e) {
				e.printStackTrace();
			}
			return response;
		}
		
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if(mProgressDialog.isShowing()){
			mProgressDialog.cancel();
			}
			if(result != null){
				try {
					displayUserProfile(result);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Log.i("ProfileFragment", "Profile : "+result);
			}else{
				Toast.makeText(mContext, "No NetWork", Toast.LENGTH_LONG).show();
			}
		}
		
	}
	
	private void displayUserProfile(String result) throws JSONException{
		JSONObject jsonObject = new JSONObject(result);
		String status = jsonObject.getString("status");
		if("success".equals(status)){
			String email = jsonObject.getString("email_id");
			String display_name = jsonObject.getString("display_name");
			boolean isAdmin = jsonObject.getBoolean("is_admin");
			String personal_number = jsonObject.getString("personal_number");
			String business_number = jsonObject.getString("business_number");
			String created_ts = jsonObject.getString("created_ts");
			
			UserProfile.PERSONAL_NUMBER = personal_number;
			UserProfile.BUSINESS_NUMBER = business_number;
			 mDisplayNameText.setText(display_name);
			 mEmailIdText.setText(email);
			 if(isAdmin){
				 mSubTitleText.setText("Admin");
			 }else{
				 mSubTitleText.setText("Content Associate");
			 }
			 
			 mPersonalNumber.setText(personal_number);
			 mBusinessNumber.setText(business_number);
			
		}
	}

}
