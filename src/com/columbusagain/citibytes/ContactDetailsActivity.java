package com.columbusagain.citibytes;

import org.json.JSONException;
import org.json.JSONObject;

import com.columbusagain.citibytes.helper.Constants;
import com.columbusagain.citibytes.helper.HttpGet;
import com.columbusagain.citibytes.helper.HttpPost;
import com.columbusagain.citibytes.helper.JsonGetException;
import com.columbusagain.citibytes.helper.UserProfile;
import com.columbusagain.citibytes.util.NetworkChecker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.plus.PlusClient;
import com.google.android.gms.plus.model.people.Person;
import com.google.android.gms.plus.model.people.Person.Image;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ContactDetailsActivity extends ActionBarActivity implements OnClickListener  {
	
	
	
	//private String mUserEmailId;
	
	private Dialog mProgressDialog;
	
	private EditText mMobileNumberEditText;
	
	private EditText mPhoneNumberEditText;	
	
	private Button mSubmitButton;
	
	private Context mContext = this;
	
	//private String mDisplayName = null;
	
	//private String mProfilePictureUrl = null;
	
	//private String mProfileSubTitle = null;
	
	//private PlusClient mPlusClient;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contact_details);
		ActionBar actionbar = getSupportActionBar();
		actionbar.setTitle("Contact Details");
		actionbar.setLogo(R.drawable.logo_navbar);
		mProgressDialog = new Dialog(this,R.style.ProgressDialog);
		mProgressDialog.setContentView(R.layout.progress_dialog);
	/*	mPlusClient = new PlusClient.Builder(this, this, this).setActions(
				MomentUtil.ACTIONS).build();*/
		
		mMobileNumberEditText = (EditText) findViewById(R.id.mobile_contact);
		mPhoneNumberEditText = (EditText) findViewById(R.id.phone_contact);
		mSubmitButton = (Button) findViewById(R.id.submit_button);
		mSubmitButton.setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//getMenuInflater().inflate(R.menu.contact_details, menu);
		return false;
	}
	
	@Override
	protected void onStop() {
		Log.i("ContactDetailsActivity","OnStop");
      //  mPlusClient.disconnect();
		super.onStop();
	}

	@Override
	protected void onStart() {
		Log.i("ContactDetailsActivity","OnStart");
		super.onStart();
		// mPlusClient.connect();

	}
	
	/*public void onSubmitButtonClick(View view){
	//	UserProfile.PROFILE_NAME = mDisplayName;
		boolean isNetworkConnected = NetworkChecker.isConnected(this);
		if(!isNetworkConnected){
			Toast.makeText(getApplicationContext(), "No Internet!", Toast.LENGTH_LONG).show();
		}else{
			mProgressDialog.show();
			new ValidateUser().execute();
		}
		//Intent intent = new Intent(ContactDetailsActivity.this,AreaSearchActivity.class);
		//intent.putExtra(AreaSearchActivity.PROFILE_NAME, mDisplayName);
		//intent.putExtra(AreaSearchActivity.PROFILE_PICTURE_URL, mProfilePictureUrl);
		//startActivity(intent);
	}*/
	
	/*public void signOut(View view){
		Log.i("ContactDetailsActivity","signOut");
		if (mPlusClient.isConnected()) {
            mPlusClient.clearDefaultAccount();
            mPlusClient.disconnect();
            mPlusClient.connect();
        }
	
		
	}*/

	/*@Override
	public void onConnectionFailed(ConnectionResult result) {
		Log.i("ContactDetailsActivity","onConnectionFailed");
		if(mProgressDialog.isShowing()){
			mProgressDialog.dismiss();
		}
		Intent intent = new Intent(ContactDetailsActivity.this,LoginActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
		finish();
		
	}*/

	/*@Override
	public void onConnected(Bundle connectionHint) {
		Log.i("ContactDetailsActivity","onConnected");
		if(mPlusClient.getCurrentPerson()!=null){
			Person currentPerson = mPlusClient.getCurrentPerson();
			Image personPhoto = currentPerson.getImage();
			//mUserEmailId = mPlusClient.getAccountName();
			//mDisplayName = currentPerson.getDisplayName();
			mProfilePictureUrl = personPhoto.getUrl();
			
			
		}
		
		//mDisplayName
	}*/

	/*@Override
	public void onDisconnected() {
		Log.i("ContactDetailsActivity","onDisconnected");
		if(mProgressDialog.isShowing()){
			mProgressDialog.dismiss();
		}
		//Intent intent = new Intent(ContactDetailsActivity.this,LoginActivity.class);
		//startActivity(intent);
		//finish();
	}*/
	
	private String buildJson(){
		String mobile_contact = mMobileNumberEditText.getText().toString();
		String phone_contact = mPhoneNumberEditText.getText().toString();
		JSONObject userDetails = new JSONObject();
		try {
			userDetails.put("email_id", UserProfile.EMAIL_ID);
			if(UserProfile.PROFILE_NAME.length()>0)			
			userDetails.put("display_name", UserProfile.PROFILE_NAME);
			else
				userDetails.put("display_name", "NIJANDHAN L");
			userDetails.put("personal_number",mobile_contact );
			userDetails.put("business_number", phone_contact);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.i("ContactDetailsActivity", userDetails.toString());
		return userDetails.toString();
		
	}
	
	private class ValidateUser extends AsyncTask<Void, Void, String>{

		@Override
		protected String doInBackground(Void... params) {
			
			HttpPost httpPost = new HttpPost(Constants.BASE_URL+"addNewUser.php");
			httpPost.setParam("json", buildJson());
			String response = httpPost.executePost();
			
			return response;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if(mProgressDialog.isShowing()){
				mProgressDialog.dismiss();
			}
			if(result!=null){
				try {
					JSONObject response_obj = new JSONObject(result);
					String status = response_obj.getString("status");
					
					if("success".equals(status)){
						Log.i("ContactDetailsActivity", "Success");
						new GetUserProfile().execute();
						/*Intent intent = new Intent(ContactDetailsActivity.this,CAActivity.class);
						startActivity(intent);
						finish();*/
						
						
					}else{
						Log.i("ContactDetailsActivity", "error");
						String error_message = response_obj.getString("error");
						Toast.makeText(getApplicationContext(), error_message, Toast.LENGTH_LONG).show();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}

		
		
	}

	@Override
	public void onClick(View view) {
		switch(view.getId()){
		case R.id.submit_button:
			if(!isValidInput())
				break;
			boolean isNetworkConnected = NetworkChecker.isConnected(this);
			if(!isNetworkConnected){
				Toast.makeText(getApplicationContext(), "No Internet!", Toast.LENGTH_LONG).show();
			}else{
				mProgressDialog.show();
				new ValidateUser().execute();
			}
			break;
		}
		
	}
	
	private boolean isValidInput(){
		String mobile_contact = mMobileNumberEditText.getText().toString();
		if(mobile_contact.length() == 0){
			Toast.makeText(mContext, "Please fill required fields", Toast.LENGTH_LONG).show();
			mMobileNumberEditText.requestFocus();
			
			return false;
		}else if(mobile_contact.length()<10){
			Toast.makeText(mContext, "Invalid mobile number", Toast.LENGTH_LONG).show();
			mMobileNumberEditText.requestFocus();
			
			return false;
		}
		String phone_contact = mPhoneNumberEditText.getText().toString();
		if(phone_contact.length() == 0){
			Toast.makeText(mContext, "Please fill required fields", Toast.LENGTH_LONG).show();
			mPhoneNumberEditText.requestFocus();
			return false;
		}else if(phone_contact.length()<8){
			Toast.makeText(mContext, "Invalid contact number", Toast.LENGTH_LONG).show();
			mPhoneNumberEditText.requestFocus();
			return false;
		}
		return true;
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
					response = null;
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
						JSONObject responseJson = new JSONObject(result);
						String status = responseJson.getString("status");
						if(status.equalsIgnoreCase("success")){
							login(responseJson);
						}
						
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
	 
	 private void login(JSONObject jsonObject) throws JSONException{
		 boolean isAdmin = jsonObject.getBoolean("is_admin");
		// Log.i("SplashScreen", "Returning User");
		 Intent intent;
		 if(isAdmin){
			 UserProfile.isAdmin = true;
			 intent= new Intent(ContactDetailsActivity.this,AdminActivity.class);
		 }else{
			 UserProfile.isAdmin = false;
			 intent= new Intent(ContactDetailsActivity.this,CAActivity.class);
				
		 }
		 startActivity(intent);
			finish();
	 }

}
