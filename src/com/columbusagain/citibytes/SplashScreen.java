package com.columbusagain.citibytes;


import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

import com.columbusagain.citibytes.helper.Constants;
import com.columbusagain.citibytes.helper.HttpGet;
import com.columbusagain.citibytes.helper.HttpPost;
import com.columbusagain.citibytes.helper.JsonGetException;
import com.columbusagain.citibytes.helper.UserProfile;
import com.columbusagain.citibytes.util.NetworkChecker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

public class SplashScreen extends ActionBarActivity implements ConnectionCallbacks, OnConnectionFailedListener {
	
    // Profile pic image size in pixels
    private static final int PROFILE_PIC_SIZE = 200;
 
    // Google client to interact with Google API
    private GoogleApiClient mGoogleApiClient;
	
	private Dialog mProgressDialog;
	
	private Context mContext = this;
	
	private String mUserEmailId;
	
	private Intent mStartLoginActivity;
	
	//private PlusClient mPlusClient;
	
	private String mProfileImageUrl;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.activity_splash_screen);
		ActionBar actionbar = getSupportActionBar();
		actionbar.hide();
		/*HomeFragment.northeast_lat = 0;
		HomeFragment.northeast_lng = 0;
		HomeFragment.southwest_lat = 0;
		HomeFragment.southwest_lng = 0;*/
		mProgressDialog = new Dialog(this,R.style.ProgressDialog);
		mProgressDialog.setContentView(R.layout.progress_dialog);
		boolean isNetworkConnected = NetworkChecker.isConnected(this);
		if(!isNetworkConnected){
			Toast.makeText(getApplicationContext(), "No Internet!", Toast.LENGTH_LONG).show();
		}else{
			mProgressDialog.show();
		}
		
		// Initializing google plus api client
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN).build();
		
		/*mPlusClient = new PlusClient.Builder(this, this, this).setActions(
				MomentUtil.ACTIONS).build();*/
		//mPlusClient = GooglePlusInstance.getInstance(mPlusClient);
		mStartLoginActivity = new Intent(SplashScreen.this, LoginActivity.class);

		/*new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				mProgressDialog.cancel();
				startActivity(mStartLoginActivity);
				finish();
			}

		}, Constants.SPLASH_TIMEOUT);*/
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.splash_screen, menu);
		return true;
	}
	
	@Override
	protected void onStart() {
		Log.i("SplashScreen", "onStart");
		
		super.onStart();
		mGoogleApiClient.connect();
	}
	
	@Override
	protected void onStop() {
		Log.i("SplashScreen", "onStop");
		//mPlusClient.disconnect();
		super.onStop();
		if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		Log.i("SplashScreen", "onConnect");

	        if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
	            Person currentPerson = Plus.PeopleApi
	                    .getCurrentPerson(mGoogleApiClient);
	            UserProfile.PROFILE_NAME = currentPerson.getDisplayName();
	            String personPhotoUrl = currentPerson.getImage().getUrl();
	            mUserEmailId = Plus.AccountApi.getAccountName(mGoogleApiClient);
	            UserProfile.EMAIL_ID = mUserEmailId;
	            mProfileImageUrl = personPhotoUrl.substring(0,
	                    personPhotoUrl.length() - 2)
	                    + PROFILE_PIC_SIZE;
	            if(mProfileImageUrl != null)
	            new ImageDownloader().execute();
	            
	            new ValidateUser().execute();
	 
	        } else {
	            Toast.makeText(getApplicationContext(),
	                    "Please try again", Toast.LENGTH_LONG).show();
	            	mProgressDialog.dismiss();
	        }

		/*
		if (mPlusClient.getCurrentPerson() != null) {
            Person currentPerson = mPlusClient.getCurrentPerson();
            UserProfile.PROFILE_NAME = currentPerson.getDisplayName();
           // String personName = currentPerson.getDisplayName();
            Image personPhoto = currentPerson.getImage();
            String imageUri = null;
            mUserEmailId = mPlusClient.getAccountName();
            UserProfile.EMAIL_ID = mUserEmailId;
            Log.i("SplashScreen", mUserEmailId);
            if(personPhoto.hasUrl()){
            	String[] mImageUrl = personPhoto.getUrl().split("\\?");
            	mProfileImageUrl = mImageUrl[0]+"?sz=200";
            	new ImageDownloader().execute();
            }
            new ValidateUser().execute();
            //new ImageDownloader().execute();
		}else{
        	if(mProgressDialog.isShowing())
        		mProgressDialog.dismiss();
        	Toast.makeText(mContext, "Please Try Again", Toast.LENGTH_LONG).show();
        }*/
		
		
		
	}

	

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		Log.i("SplashScreen", "onConnectionFailed");
		if(mProgressDialog.isShowing()){
			mProgressDialog.dismiss();
		}
		Intent intent = new Intent(SplashScreen.this,LoginActivity.class);
		startActivity(intent);
		finish();
		
	}
	
	private class ImageDownloader extends AsyncTask<Void,Void,Void>{

		@Override
		protected Void doInBackground(Void... params) {	
			try {
				URL url = new URL(mProfileImageUrl);
				UserProfile.PROFILE_PICTURE = BitmapFactory.decodeStream(url.openConnection().getInputStream());
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
		
		
		
    	
    }
	
	private class ValidateUser extends AsyncTask<Void, Void, String>{

		@Override
		protected String doInBackground(Void... params) {
			
			
			
			HttpPost httpPost = new HttpPost(Constants.BASE_URL+"isReturningUser.php");
			httpPost.setParam("email", mUserEmailId);
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
						boolean isNewUser = response_obj.getBoolean("is_new_user");
						if(isNewUser){
							Log.i("SplashScreen", "New User");
							Intent intent = new Intent(SplashScreen.this,ContactDetailsActivity.class);
							startActivity(intent);
							finish();
							
							
						}else{
							new GetUserProfile().execute();
							/*Log.i("SplashScreen", "Returning User");
							Intent intent = new Intent(SplashScreen.this,CAActivity.class);
							startActivity(intent);
							finish();*/
							
							
						}
						
						
						
					}else{
						Toast.makeText(getApplicationContext(), "Please Try Again", Toast.LENGTH_LONG).show();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}

		
		
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
		 Log.i("SplashScreen", "Returning User");
		 Intent intent;
		 if(isAdmin){
			 UserProfile.isAdmin = true;
			 intent= new Intent(SplashScreen.this,AdminActivity.class);
		 }else{
			 UserProfile.isAdmin = false;
			 intent= new Intent(SplashScreen.this,CAActivity.class);
				
		 }
		 startActivity(intent);
			finish();
	 }

	@Override
	public void onConnectionSuspended(int arg0) {
		// TODO Auto-generated method stub
		
	}

}
