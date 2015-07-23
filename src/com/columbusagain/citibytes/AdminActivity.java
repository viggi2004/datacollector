package com.columbusagain.citibytes;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.provider.Settings;
import android.provider.MediaStore.MediaColumns;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.columbusagain.citibytes.database.CitiBytesDB;
import com.columbusagain.citibytes.helper.Constants;
import com.columbusagain.citibytes.helper.GooglePlusInstance;
import com.columbusagain.citibytes.helper.Timer;
import com.columbusagain.citibytes.helper.UserProfile;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.PlusClient;

public class AdminActivity extends ActionBarActivity implements
		OnClickListener, ConnectionCallbacks, OnConnectionFailedListener{
	
	public JSONObject businessJson = new JSONObject();
	
	private boolean isLogoutButtonClick = false;
	
	public JSONObject downloadedJson = new JSONObject();

	public String mBusinessId ;

	public String mBusinessName = "ColumbusAgain";
	
	public String mLastSelectedType;
	
	public String mSelectedEmployeeEmailId;
	
	private Uri imageUri;

	private String imgPath;
	
	public String mPeriod;
	
	// Google client to interact with Google API
    private GoogleApiClient mGoogleApiClient;	
		
	private LinearLayout mProfileLayout;	

	private Context mContext = this;

	private ActionBar mActionbar;

	private DrawerLayout mDrawerLayout;

	private LinearLayout mDrawerContent;

	private ActionBarDrawerToggle mDrawerToggle;

	private CharSequence mTitle;
	
	private Fragment mSelectCityFragment;

	private ImageView mProfileImageView;

	private TextView mProfileName;
	
	private TextView mProfileSubTitle;

	//private PlusClient mPlusClient;
	
	private FragmentManager mFragmentManager = getSupportFragmentManager();
	
	private TextView mManageRequests,mPincodeCoverage,mEmployeePerformance,mCityList,mAboutUs,mContactUs,mLogout;
	
	// Constants
		// The authority for the sync adapter's content provider
		public static final String AUTHORITY = "com.columbusagain.citibytes.syncadapter";
		// An account type, in the form of a domain name
		public static final String ACCOUNT_TYPE = "citibytes.com";
		// The account name
		public static final String ACCOUNT = "citibytesaccount";
		// Instance fields
		Account mAccount;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_admin);
		mAccount = CreateSyncAccount(this);
		HomeFragment.northeast_lat = 0;
		HomeFragment.northeast_lng = 0;
		HomeFragment.southwest_lat = 0;
		HomeFragment.southwest_lng = 0;
		
		// Initializing google plus api client
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN).build();

		/*mPlusClient = new PlusClient.Builder(this, this, this).setActions(
				MomentUtil.ACTIONS).build();*/
		
		mProfileLayout = (LinearLayout) findViewById(R.id.profile_layout);

		mProfileImageView = (ImageView) findViewById(R.id.profile_pic);

		mProfileName = (TextView) findViewById(R.id.display_name);
		
		mProfileSubTitle = (TextView) findViewById(R.id.display_sub_title);
		
		mManageRequests = (TextView) findViewById(R.id.manage_requests);
		mPincodeCoverage = (TextView) findViewById(R.id.pincode_coverage);
		mEmployeePerformance = (TextView) findViewById(R.id.employee_performance);
		mCityList = (TextView) findViewById(R.id.city_list);
		mAboutUs = (TextView) findViewById(R.id.about_us);
		mContactUs = (TextView) findViewById(R.id.contact_us);
		mLogout = (TextView) findViewById(R.id.logout);

		mProfileName.setText(UserProfile.PROFILE_NAME);
		
		if(UserProfile.isAdmin){
			mProfileSubTitle.setText("Admin");
		}else{
			mProfileSubTitle.setText("Content Associate");
		}

		mProfileImageView.setImageBitmap(UserProfile.PROFILE_PICTURE);

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

		mDrawerContent = (LinearLayout) findViewById(R.id.drawer_content);

		mProfileLayout.setOnClickListener(this);

		// set a custom shadow that overlays the main content when the drawer
		// opens
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
				GravityCompat.START);
		// set up the drawer's list view with items and click listener

		// enable ActionBar app icon to behave as action to toggle nav drawer
		mActionbar = getSupportActionBar();
		mActionbar.setDisplayHomeAsUpEnabled(true);
		mActionbar.setHomeButtonEnabled(true);
		mActionbar.setLogo(R.drawable.logo_navbar);
		ImageView view = (ImageView) findViewById(android.R.id.home);
		if (view != null)
			view.setPadding(30, 0, 10, 0);

		// ActionBarDrawerToggle ties together the the proper interactions
		// between the sliding drawer and the action bar app icon
		mDrawerToggle = new ActionBarDrawerToggle(this, /* host Activity */
		mDrawerLayout, /* DrawerLayout object */
		R.drawable.ic_drawer, /* nav drawer image to replace 'Up' caret */
		R.string.drawer_open, /* "open drawer" description for accessibility */
		R.string.drawer_close /* "close drawer" description for accessibility */
		) {
			public void onDrawerClosed(View view) {
				// mActionbar.setTitle(mTitle);
				ActivityCompat.invalidateOptionsMenu(AdminActivity.this); // creates
																				// call
																				// to
																				// onPrepareOptionsMenu()
			}

			public void onDrawerOpened(View drawerView) {
				// mActionbar.setTitle(mDrawerTitle);
				ActivityCompat.invalidateOptionsMenu(AdminActivity.this); // creates
																				// call
																				// to
																				// onPrepareOptionsMenu()
			}

			@Override
			public void onDrawerSlide(View drawerView, float slideOffset) {
				super.onDrawerSlide(drawerView, slideOffset);
				mDrawerLayout.bringChildToFront(drawerView);
				mDrawerLayout.requestLayout();
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);
		
		mManageRequests.setOnClickListener(this);
		mPincodeCoverage.setOnClickListener(this);
		mEmployeePerformance.setOnClickListener(this);
		mCityList.setOnClickListener(this);
		mAboutUs.setOnClickListener(this);
		mContactUs.setOnClickListener(this);
		mLogout.setOnClickListener(this);
		
		if (savedInstanceState == null) {
			 if (UserProfile.CITY != null) {
				startPincodeRequestsFragment();
			} else {
				startSelectYourCityFragment();
			}
		}
	}
	
	/**
	 * Create a new dummy account for the sync adapter
	 * 
	 * @param context
	 *            The application context
	 */
	public static Account CreateSyncAccount(Context context) {
		// Create the account type and default account
		Account newAccount = new Account(ACCOUNT, ACCOUNT_TYPE);
		// Get an instance of the Android account manager
		AccountManager accountManager = (AccountManager) context
				.getSystemService(ACCOUNT_SERVICE);
		/*
		 * Add the account and account type, no password or user data If
		 * successful, return the Account object, otherwise report an error.
		 */
		if (accountManager.addAccountExplicitly(newAccount, null, null)) {
			/*
			 * If you don't set android:syncable="true" in in your <provider>
			 * element in the manifest, then call context.setIsSyncable(account,
			 * AUTHORITY, 1) here.
			 */

			return newAccount;
		} else {
			/*
			 * The account exists or some other error occurred. Log this, report
			 * it, or handle it internally.
			 */
			// return null;
		}
		return newAccount;
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// getMenuInflater().inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	/* Called whenever we call invalidateOptionsMenu() */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// The action bar home/up action should open or close the drawer.
		// ActionBarDrawerToggle will take care of this.
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		// Handle action buttons
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		mActionbar.setTitle(mTitle);
	}

	/**
	 * When using the ActionBarDrawerToggle, you must call it during
	 * onPostCreate() and onConfigurationChanged()...
	 */

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggls
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.manage_requests:
			if(UserProfile.CITY != null)
			startPincodeRequestsFragment();
			mDrawerLayout.closeDrawer(mDrawerContent);
			break;
		case R.id.pincode_coverage:
			if(UserProfile.CITY != null)
			startPincodeCoverageFragment();
			mDrawerLayout.closeDrawer(mDrawerContent);
			break;

		case R.id.employee_performance:
			if(UserProfile.CITY != null)
			startEmployeeFragment();
			mDrawerLayout.closeDrawer(mDrawerContent);
			break;
		case R.id.city_list:
			startSelectYourCityFragment();
			mDrawerLayout.closeDrawer(mDrawerContent);
			break;
			
		case R.id.profile_layout:
			startProfilePage();
			mDrawerLayout.closeDrawer(mDrawerContent);
			break;
			
		case R.id.about_us:
			mDrawerLayout.closeDrawer(mDrawerContent);
			break;
			
		case R.id.contact_us:
			startContactUsFragment();
			mDrawerLayout.closeDrawer(mDrawerContent);
			break;
		case R.id.logout:
			isLogoutButtonClick = true;
			if (mGoogleApiClient.isConnected()) {
		        Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
		        mGoogleApiClient.disconnect();
		        
		      
		    }
			  mGoogleApiClient.connect();
			mDrawerLayout.closeDrawer(mDrawerContent);
			break;
		default:
			break;

		}

	}
	
	public void startEmployeeDetailsFragment(String type,String email_id){
		mSelectedEmployeeEmailId = email_id;
		mLastSelectedType = type;
		Fragment fragment;
		Bundle args = new Bundle();
		args.putString("query_type", type);		
		args.putString("email_id", email_id);
		fragment = new EmployeeDetailsFragment();
		fragment.setArguments(args);
		FragmentTransaction ft = mFragmentManager.beginTransaction();
		ft.replace(R.id.content_frame, fragment);
		ft.commit();
	}
	
	public void startProfilePage(){
		Fragment fragment;
		Bundle args = new Bundle();
		fragment = new ProfileFragment();
		args.putString(Constants.ACTION_BAR_TITLE, "Profile");
		fragment.setArguments(args);
		FragmentTransaction ft = mFragmentManager.beginTransaction();
		ft.replace(R.id.content_frame, fragment);
		ft.commit();
	}
	
	public void editProfilePage(){
		Fragment fragment;
		Bundle args = new Bundle();
		fragment = new EditProfile();
		args.putString(Constants.ACTION_BAR_TITLE, "Profile");
		fragment.setArguments(args);
		FragmentTransaction ft = mFragmentManager.beginTransaction();
		ft.replace(R.id.content_frame, fragment);
		ft.commit();
	}
	
	public void startEmployeeFragment(){
		if(mLastSelectedType == null)
			mLastSelectedType = "today";
		Fragment fragment;
		Bundle args = new Bundle();
		args.putString("list_type", mLastSelectedType);
		fragment = new EmployeeFragment();
		fragment.setArguments(args);
		FragmentTransaction ft = mFragmentManager.beginTransaction();
		ft.replace(R.id.content_frame, fragment);
		ft.commit();
	}
	
	public void startPincodeCoverageFragment(){
		Fragment fragment;
		fragment = new PincodesFragment();
		FragmentTransaction ft = mFragmentManager.beginTransaction();
		ft.replace(R.id.content_frame, fragment);
		ft.commit();
	}
	
	public void startMapFragment(String period) {
		mPeriod = period;
		Fragment fragment;
		Bundle args = new Bundle();
		fragment = new HomeFragment();
		args.putString("period", period);
		fragment.setArguments(args);
		FragmentTransaction ft = mFragmentManager.beginTransaction();
		ft.replace(R.id.content_frame, fragment);
		ft.commit();
	}
	
	public void startContactUsFragment(){
		Fragment fragment;
		fragment = new ContactUsFragment();
		FragmentTransaction ft = mFragmentManager.beginTransaction();
		ft.replace(R.id.content_frame, fragment);
		ft.commit();
	}
	
		public void startSelectYourCityFragment() {
			if (mSelectCityFragment == null) {
				Bundle args = new Bundle();
				mSelectCityFragment = new SelectCityFragment();
				args.putString(Constants.ACTION_BAR_TITLE, "Select Your City");
				mSelectCityFragment.setArguments(args);
			}
			FragmentTransaction ft = mFragmentManager.beginTransaction();
			ft.replace(R.id.content_frame, mSelectCityFragment);
			ft.commit();
		}
	

	public void startPincodeRequestsFragment() {
		
		Fragment fragment;
		fragment = new PincodeRequestsFragment();
		FragmentTransaction ft = mFragmentManager.beginTransaction();
		ft.replace(R.id.content_frame, fragment);
		ft.commit();
	}
	
	public void editBusinessFragment() {
		Fragment fragment;
		fragment = new DataCollectionFragment();
		FragmentTransaction ft = mFragmentManager.beginTransaction();
		ft.replace(R.id.content_frame, fragment);
		ft.commit();
	}
	
	

	public void deleteBusiness(){
		Log.i("BaseDrawerActivity", "deleteBusiness");
		// Pass the settings flags by inserting them in a bundle
		Bundle settingsBundle = new Bundle();
		settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
		settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
		settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_DO_NOT_RETRY,
				false);
		settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_IGNORE_BACKOFF,
				false);
		settingsBundle.putString("business_id", mBusinessId);
		settingsBundle.putString("sync_type","delete_business");
		/*
		 * Request the sync for the default account, authority, and manual sync
		 * settings
		 */
		ContentResolver.requestSync(mAccount, AUTHORITY, settingsBundle);
	}



	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		if(isLogoutButtonClick){
		UserProfile.clearData();
		Constants.clearData();		
		Intent intent = new Intent(AdminActivity.this, LoginActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
		finish();
		isLogoutButtonClick = false;
		}

	}

	@Override
	public void onConnected(Bundle arg0) {
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Constants.FLAG = 0;
	}


	

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		mGoogleApiClient.connect();
	}

	@Override
	protected void onResume() {
		super.onResume();
		
	}
	
	@Override
	protected void onPause() {
		super.onPause();
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		
		if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
	}


	@Override
	public void onConnectionSuspended(int arg0) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onBackPressed() {
		boolean isMapScreen = false;
		boolean isEmployeeScreen = false;
		boolean isDataCollectionFragment = false;
		boolean isEditProfileFragment = false;
	
		 List<Fragment> fragments = mFragmentManager.getFragments();
		 if (fragments != null) {
	        	for (Fragment fragment : fragments) {
	        		if(fragment instanceof DataCollectionFragment){
	        			isDataCollectionFragment = true;
	        			((DataCollectionFragment) fragment).onBackPressed();
	        		}else if(fragment instanceof HomeFragment){
	        			isMapScreen = true;
	        			startPincodeCoverageFragment();
	        		}else if(fragment instanceof EmployeeDetailsFragment){
	        			isEmployeeScreen = true;
	        			startEmployeeFragment();
	        		}else if(fragment instanceof EditProfile){
	        			isEditProfileFragment = true;
	        			((EditProfile) fragment).onBackPressed();
	        		}
	        	}
	        	
	        }
		 if(!isMapScreen && !isEmployeeScreen && !isDataCollectionFragment && !isEditProfileFragment)
        	 super.onBackPressed();
	}
	
	public void disableKeyBoard() {
		InputMethodManager inputManager = (InputMethodManager) this
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		inputManager.hideSoftInputFromWindow(this.getCurrentFocus()
				.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
	}
	
	public void requestSync(JSONArray imageUriArray) {
		new DataBaseTask().execute(imageUriArray.toString());
		Log.i("BaseDrawerActivity", "requestSync");

	}
	
	private class DataBaseTask extends AsyncTask<String, Void, Void> {

		@Override
		protected Void doInBackground(String... params) {
			insertDataToDB(params[0],buildAnalyticJson());
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			startSync();
		}

	}
	
	private String buildAnalyticJson(){
		/*TimeZone tz = TimeZone.getTimeZone("GMT+05:30");
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mmZ");
		df.setTimeZone(tz);
		String nowAsISO = df.format(new Date());
		Log.i("CAActivity", nowAsISO);*/
		String timeDuration = String.valueOf(Timer.getTotalTime()/1000);
		
		JSONObject analyticsJson = new JSONObject();
		
		try {
			analyticsJson.put("email_id", UserProfile.EMAIL_ID);
			analyticsJson.put("business_id", mBusinessId);
			analyticsJson.put("duration", timeDuration);
			//analyticsJson.put("date", nowAsISO);
			analyticsJson.put("is_admin",true);
			analyticsJson.put("pincode", UserProfile.SELECTED_PIN);
			analyticsJson.put("city", UserProfile.CITY);
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return analyticsJson.toString();
		
		
		
	}
	
	private void insertDataToDB(String imageDetails,String analyticsJson) {
		Log.i("BaseDrawerActivity", businessJson.toString());
		Log.i("BaseDrawerActivity", mBusinessId);
		//Calendar calendar = Calendar.getInstance();
		//String businessId = String.valueOf(calendar.getTimeInMillis());
		CitiBytesDB dataBase = new CitiBytesDB(mContext);
		dataBase.insertBusiness(mBusinessId, businessJson.toString(),
				imageDetails);
		dataBase.insertTimeCalculation(mBusinessId, analyticsJson);

	}
	
	private void startSync() {
		Log.i("CAActivity", "Total Time Spent :"+(Timer.getTotalTime()/1000));
		Log.i("BaseDrawerActivity", businessJson.toString());
		// Pass the settings flags by inserting them in a bundle
		Bundle settingsBundle = new Bundle();
		settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
		settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
		settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_DO_NOT_RETRY,
				false);
		settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_IGNORE_BACKOFF,
				false);
		settingsBundle.putString("business_id", mBusinessId);
		settingsBundle.putString("sync_type","add_business");
		/*
		 * Request the sync for the default account, authority, and manual sync
		 * settings
		 */
		ContentResolver.requestSync(mAccount, AUTHORITY, settingsBundle);
	}
	
	public Uri setImageUri(String main_dir, String sub_dir, String file_name) {

		// Store image in dcim
		File file_main = new File(Environment.getExternalStorageDirectory()
				+ "/DCIM/", main_dir);
		file_main.mkdir();
		File file_sub = new File(file_main, sub_dir);
		file_sub.mkdir();
		File file_output = new File(file_sub, file_name);
		if (file_output.isFile())
			file_output.delete();
		// File file = new File(Environment.getExternalStorageDirectory() +
		// dir_name, file_name);
		Uri imgUri = Uri.fromFile(file_output);
		this.imageUri = imgUri;
		this.imgPath = file_output.getAbsolutePath();
		return imgUri;
	}

	public Uri getImageUri() {
		return imageUri;
	}

	public String getImagePath() {
		return imgPath;
	}
	
	public String getAbsolutePath(Uri uri) {

		String[] projection = { MediaColumns.DATA };
		ContentResolver cr = getContentResolver();
		cr.notifyChange(uri, null);
		Cursor cursor = cr.query(uri, projection, null, null, null);
		if (cursor != null) {
			int column_index = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
			cursor.moveToFirst();
			return cursor.getString(column_index);
		} else
			return null;

	}
	
	public void resizeImage(Uri imageUri, int newWidth, int newHeight) {
		// Log.i("BusinessId", mBusinessId);
		Log.i("ImageUri", imageUri.toString());

		File imgFile = new File(imageUri.getPath());
		if (imgFile.exists()) {

			Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

			Log.i("Width and Height",
					"" + bitmap.getHeight() + "" + bitmap.getWidth());

			// ImageView myImage = (ImageView) findViewById(R.id.imageviewTest);
			// myImage.setImageBitmap(myBitmap);

			int width = bitmap.getWidth();
			int height = bitmap.getHeight();
			float scaleWidth = ((float) newWidth) / width;
			float scaleHeight = ((float) newHeight) / height;
			// create a matrix for the manipulation
			Matrix matrix = new Matrix();
			// resize the bit map
			matrix.postScale(scaleWidth, scaleHeight);
			// recreate the new Bitmap
			Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width,
					height, matrix, false);
			Log.i("Width and Height", "" + resizedBitmap.getHeight() + ""
					+ resizedBitmap.getWidth());

			try {
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				resizedBitmap.compress(CompressFormat.JPEG, 100, bos);
				byte[] bitmap_byte_arr = bos.toByteArray();
				// FileInputStream fis = new FileInputStream(resizedBitmap);

				FileOutputStream fos = new FileOutputStream(imageUri.getPath());
				fos.write(bitmap_byte_arr);
				fos.flush();
				fos.close();

				Log.i("Width and Height", "" + resizedBitmap.getHeight() + ""
						+ resizedBitmap.getWidth());

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		// File file=new File(imageUri.toString());

	}


}
