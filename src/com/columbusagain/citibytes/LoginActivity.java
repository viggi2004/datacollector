package com.columbusagain.citibytes;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;

import com.columbusagain.citibytes.helper.Constants;
import com.columbusagain.citibytes.helper.HttpGet;
import com.columbusagain.citibytes.helper.HttpPost;
import com.columbusagain.citibytes.helper.JsonGetException;
import com.columbusagain.citibytes.helper.UserProfile;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.PlusClient;
import com.google.android.gms.plus.model.people.Person;

public class LoginActivity extends ActionBarActivity implements
		OnClickListener, ConnectionCallbacks, OnConnectionFailedListener{
	
	private static final int RC_SIGN_IN = 0;
 
    // Profile pic image size in pixels
    private static final int PROFILE_PIC_SIZE = 200;
 
    // Google client to interact with Google API
    private GoogleApiClient mGoogleApiClient;
    
    /**
     * A flag indicating that a PendingIntent is in progress and prevents us
     * from starting further intents.
     */
    private boolean mIntentInProgress;
 
    private boolean mSignInClicked;
	
	
	private Context mContext = this;
	
	private Dialog mProgressDialog;
	
	private String mProfileImageUrl;
	
	private String mUserEmailId;
	
	private static final int DIALOG_GET_GOOGLE_PLAY_SERVICES = 1;

	private static final int REQUEST_CODE_SIGN_IN = 1;
	private static final int REQUEST_CODE_GET_GOOGLE_PLAY_SERVICES = 2;

	private ImageView mProfilePicture;
	//private TextView mSignInStatus;
	private PlusClient mPlusClient;
	private SignInButton mSignInButton;
	private View mSignOutButton;
	private View mRevokeAccessButton;
	private ConnectionResult mConnectionResult;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sign_in_activity);
		ActionBar actionbar = getSupportActionBar();
		actionbar.setTitle("Login");
		//actionbar.
		
		mProgressDialog = new Dialog(this,R.style.ProgressDialog);
		mProgressDialog.setContentView(R.layout.progress_dialog);
		
		// Initializing google plus api client
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN).build();

		/*mPlusClient = new PlusClient.Builder(this, this, this).setActions(
				MomentUtil.AUTH_SCOPES).build();*/
		//mPlusClient = GooglePlusInstance.getInstance(mPlusClient);

		//mSignInStatus = (TextView) findViewById(R.id.sign_in_status);
		mSignInButton = (SignInButton) findViewById(R.id.sign_in_button);
		mSignInButton.setOnClickListener(this);
		mSignInButton.setSize(SignInButton.SIZE_WIDE);
		//mSignOutButton = findViewById(R.id.sign_out_button);
		//mSignOutButton.setOnClickListener(this);
		//mRevokeAccessButton = findViewById(R.id.revoke_access_button);
		//mRevokeAccessButton.setOnClickListener(this);
		Log.i("LoginActivity", "OnCreate");
	}
	
	@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    	Log.i("LoginActivity","Result");
    	
    	if (requestCode == RC_SIGN_IN) {
    		Log.i("LoginActivity", "response");
            if (resultCode != RESULT_OK) {
            	Log.i("LoginActivity", "result cancel");
                mSignInClicked = false;
                Toast.makeText(getApplicationContext(),
	                    "Please try again", Toast.LENGTH_LONG).show();
	            	mProgressDialog.dismiss();
            }else{
            	Log.i("LoginActivity", "result ok");
            }
     
            mIntentInProgress = false;
     
            if (!mGoogleApiClient.isConnecting()) {
            	mGoogleApiClient.disconnect();
                mGoogleApiClient.connect();
                Log.i("LoginActivity", "not connecting");
            }else{
            	Log.i("LoginActivity", "connecting");
            	mProgressDialog.dismiss();
            }
        }
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//getMenuInflater().inflate(R.menu.login, menu);
		return false;
	}

	@Override
	protected void onStop() {
		Log.i("LoginActivity","OnStop");
	/*	if (mPlusClient.isConnected()) {
            mPlusClient.clearDefaultAccount();
            mPlusClient.disconnect();
            mPlusClient.connect();
        }*/
		//mPlusClient.disconnect();
		super.onStop();
		if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
	}

	@Override
	protected void onStart() {
		Log.i("LoginActivity","OnStart");
		super.onStart();
		mGoogleApiClient.connect();
		// mPlusClient.connect();

	}

	/*@Override
	public void onAccessRevoked(ConnectionResult status) {
		 if (status.isSuccess()) {
	           // mSignInStatus.setText(R.string.revoke_access_status);
	        } else {
	           // mSignInStatus.setText(R.string.revoke_access_error_status);
	            mPlusClient.disconnect();
	        }
	        mPlusClient.connect();

	}*/

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		if (!result.hasResolution()) {
	        GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this,
	                0).show();
	        return;
	    }
	 
	    if (!mIntentInProgress) {
	        // Store the ConnectionResult for later usage
	        mConnectionResult = result;
	 
	        if (mSignInClicked) {
	            // The user has already clicked 'sign-in' so we attempt to
	            // resolve all
	            // errors until the user is signed in, or they cancel.
	            resolveSignInError();
	        }
	    }
		

	}
	
	/**
	 * Sign-in into google
	 * */
	private void signInWithGplus() {
	    if (!mGoogleApiClient.isConnecting()) {
	        mSignInClicked = true;
	        resolveSignInError();
	    }
	}
	 
	/**
	 * Method to resolve any signin errors
	 * */
	private void resolveSignInError() {
	    if (mConnectionResult.hasResolution()) {
	        try {
	            mIntentInProgress = true;
	            mConnectionResult.startResolutionForResult(this, RC_SIGN_IN);
	        } catch (SendIntentException e) {
	            mIntentInProgress = false;
	            mGoogleApiClient.connect();
	        }
	    }
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		 mSignInClicked = false;
		Log.i("LoginActivity","OnConnected");
	        if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
	            Person currentPerson = Plus.PeopleApi
	                    .getCurrentPerson(mGoogleApiClient);
	            UserProfile.PROFILE_NAME = currentPerson.getDisplayName();
	            String personPhotoUrl = currentPerson.getImage().getUrl();
	            //String personGooglePlusProfile = currentPerson.getUrl();
	            mUserEmailId = Plus.AccountApi.getAccountName(mGoogleApiClient);
	            UserProfile.EMAIL_ID = mUserEmailId;
	            
	 
	            // by default the profile url gives 50x50 px image only
	            // we can replace the value with whatever dimension we want by
	            // replacing sz=X
	            mProfileImageUrl = personPhotoUrl.substring(0,
	                    personPhotoUrl.length() - 2)
	                    + PROFILE_PIC_SIZE;
	            if(mProfileImageUrl != null)
	            new ImageDownloader().execute();
	            new ValidateUser().execute();
	           // new LoadProfileImage(imgProfilePic).execute(personPhotoUrl);
	 
	        } else {
	            Toast.makeText(getApplicationContext(),
	                    "Please try again", Toast.LENGTH_LONG).show();
	            	mProgressDialog.dismiss();
	        }
	   
    	/*if (mPlusClient.getCurrentPerson() != null) {
    		Log.i("LoginActivity","get current person not null");
            Person currentPerson = mPlusClient.getCurrentPerson();
            UserProfile.PROFILE_NAME = currentPerson.getDisplayName();
           // String personName = currentPerson.getDisplayName();
            Image personPhoto = currentPerson.getImage();
            String imageUri = null;
            mUserEmailId = mPlusClient.getAccountName();
            UserProfile.EMAIL_ID = mUserEmailId;
            
            if(personPhoto.hasUrl()){
            	String[] mImageUrl = personPhoto.getUrl().split("\\?");
            	mProfileImageUrl= mImageUrl[0]+"?sz=200";
            	 new ImageDownloader().execute();
            }
            Log.i("profile Picture", mProfileImageUrl);
            new ValidateUser().execute();
           
           /* if(imageUri!=null){
            	
            }*/
           // mProfilePicture.setImageBitmap(personPhoto.getUrl());
            //String personGooglePlusProfile = currentPerson.getUrl();
       /* }else{
        	if(mProgressDialog.isShowing())
        		mProgressDialog.dismiss();
        	Toast.makeText(mContext, "Please Try Again", Toast.LENGTH_LONG).show();
        }*/
    	//Log.i("LoginActivity","get current person null");
    	//String personPhoto = mPlusClient
       /* String currentPersonName = mPlusClient.getCurrentPerson() != null
                ? mPlusClient.getCurrentPerson().getDisplayName()
                : getString(R.string.unknown_person);*/
      // mSignInStatus.setText(getString(R.string.signed_in_status, currentPersonName));
       // updateButtons(true /* isSignedIn */);
                //new ImageDownloader().execute();
               /* boolean isNetworkConnected = NetworkChecker.isConnected(this);
        		if(!isNetworkConnected){
        			Toast.makeText(getApplicationContext(), "No Internet!", Toast.LENGTH_LONG).show();
        		}else{*/
        			//mProgressDialog.show();
        			
        		//}
    	

	}

	
	 @Override
	    protected Dialog onCreateDialog(int id) {
	        if (id != DIALOG_GET_GOOGLE_PLAY_SERVICES) {
	            return super.onCreateDialog(id);
	        }

	        int available = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
	        if (available == ConnectionResult.SUCCESS) {
	            return null;
	        }
	        if (GooglePlayServicesUtil.isUserRecoverableError(available)) {
	            return GooglePlayServicesUtil.getErrorDialog(
	                    available, this, REQUEST_CODE_GET_GOOGLE_PLAY_SERVICES);
	        }
	        return new AlertDialog.Builder(this)
	                .setMessage(R.string.plus_generic_error)
	                .setCancelable(true)
	                .create();
	    }

	@Override
	public void onClick(View view) {
		Log.i("LoginActivity","OnButtonClick");
        switch(view.getId()) {
            case R.id.sign_in_button:
            	
            	mProgressDialog.show();
            	signInWithGplus();
               /* int available = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
                if (available != ConnectionResult.SUCCESS) {
                    showDialog(DIALOG_GET_GOOGLE_PLAY_SERVICES);
                    return;
                }

                try {
                   // mSignInStatus.setText(getString(R.string.signing_in_status));
                    mConnectionResult.startResolutionForResult(this, REQUEST_CODE_SIGN_IN);
                } catch (IntentSender.SendIntentException e) {
                    // Fetch a new result to start.
                	
                    mPlusClient.connect();
                }*/
                break;
         /*   case R.id.sign_out_button:
                if (mPlusClient.isConnected()) {
                    mPlusClient.clearDefaultAccount();
                    mPlusClient.disconnect();
                    mPlusClient.connect();
                }
                break;
            case R.id.revoke_access_button:
                if (mPlusClient.isConnected()) {
                    mPlusClient.revokeAccessAndDisconnect(this);
                    updateButtons(false /* isSignedIn *///);
            /*    }
                break;*/
        }

	}
	
	 private void updateButtons(boolean isSignedIn) {
	        if (isSignedIn) {
	        	
	        	if(mProgressDialog.isShowing()){
					mProgressDialog.dismiss();
				}
	        	
	        	Intent intent = new Intent(LoginActivity.this,ContactDetailsActivity.class);
	        	startActivity(intent);
	        	finish();
	        	
	           // mSignInButton.setVisibility(View.INVISIBLE);
	          //  mSignOutButton.setEnabled(true);
	           // mRevokeAccessButton.setEnabled(true);
	        } else {
	            if (mConnectionResult == null) {
	                // Disable the sign-in button until onConnectionFailed is called with result.
	              //  mSignInButton.setVisibility(View.INVISIBLE);
	               // mSignInStatus.setText(getString(R.string.loading_status));
	            } else {
	                // Enable the sign-in button since a connection result is available.
	              //  mSignInButton.setVisibility(View.VISIBLE);
	               // mSignInStatus.setText(getString(R.string.signed_out_status));
	            }

	           // mSignOutButton.setEnabled(false);
	           // mRevokeAccessButton.setEnabled(false);
	        }
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
		protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();
		mProgressDialog.show();
		}

			@Override
			protected String doInBackground(Void... params) {
				
				/*try {
					Log.i("LoginActivity","photo Url :" +mProfileImageUrl);
					if(mProfileImageUrl != null){
					URL url = new URL(mProfileImageUrl);
					UserProfile.PROFILE_PICTURE = BitmapFactory.decodeStream(url.openConnection().getInputStream());
					}
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}*/
				
				HttpPost httpPost = new HttpPost(Constants.BASE_URL+"isReturningUser.php");
				httpPost.setParam("email", mUserEmailId);
				String response = httpPost.executePost();
				
				return response;
			}

			@Override
			protected void onPostExecute(String result) {
				super.onPostExecute(result);
				
				if(result!=null){
					try {
						JSONObject response_obj = new JSONObject(result);
						String status = response_obj.getString("status");
						
						if("success".equals(status)){
							boolean isNewUser = response_obj.getBoolean("is_new_user");
							if(isNewUser){
								Log.i("SplashScreen", "New User");
								Intent intent = new Intent(LoginActivity.this,ContactDetailsActivity.class);
								startActivity(intent);
								finish();
								
								
							}else{
								new GetUserProfile().execute();
								
								
								
							}
							
							
							
						}else{
							Toast.makeText(getApplicationContext(), "Please Try Again", Toast.LENGTH_LONG).show();
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
				
				if(mProgressDialog.isShowing()){
					mProgressDialog.dismiss();
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
			 intent= new Intent(LoginActivity.this,AdminActivity.class);
		 }else{
			 UserProfile.isAdmin = false;
			 intent= new Intent(LoginActivity.this,CAActivity.class);
				
		 }
		 startActivity(intent);
			finish();
	 }

	@Override
	public void onConnectionSuspended(int arg0) {
		mGoogleApiClient.connect();
		
	}

}
