package com.columbusagain.citibytes.datacollection;

import java.util.Calendar;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.columbusagain.citibytes.AdminActivity;
import com.columbusagain.citibytes.CAActivity;
import com.columbusagain.citibytes.DataCollectionFragment;
import com.columbusagain.citibytes.ManualLocationActivity;
import com.columbusagain.citibytes.R;
import com.columbusagain.citibytes.helper.UserProfile;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationRequest;

public class LocationDetailsFragment extends Fragment implements OnClickListener,GooglePlayServicesClient.ConnectionCallbacks,
GooglePlayServicesClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {
	
	public Button mProceedButton;
	
	// A request to connect to Location Services
    private LocationRequest mLocationRequest;

    // Stores the current instantiation of the location client in this object
    private LocationClient mLocationClient;
	
	private boolean isEditMode = false,isViewMode = false;
	
	private MenuItem mRefreshButton;
	
	private TextView mBusinessNameTextView;
	
	private MenuInflater mMenuInflater;
	
	private ScrollView mEditLayout,mViewLayout;
	
	private TextView mViewBusinessNameText;
	
	private Button mViewNextButton;
	
	private boolean isAdminEditMode = false;
	
	private MenuItem mMenuItem;
	
	//private LocationManager mLocationManager;
	
	private Button mSetManualLocationButton;
	
	boolean isGPSEnabled = false, canGetLocation = false;
	
	private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 0;

	private static final long MIN_TIME_BW_UPDATES = 0;
	
	private TextView mLatitudeTextView,mLongitudeTextView,mAccuracyTextView,mUpdateInfoText;
	
	private TextView mViewLatitudeText,mViewLongitudeText,mViewAccuracyText;
	
	private Context mContext;
	
	private Menu optionsMenu;
	
	private Activity mActivity;
	
	private String mLocationProvider = "";
	
	private boolean isLocationSet = false;
	
	public LocationDetailsFragment(){
		
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.mActivity = activity;
		this.mContext = (Context)activity;
		Log.i("LocationDetails","onAttach");
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.i("LocationDetails", "onCreateView");
		setHasOptionsMenu(true);
		View rootView = inflater.inflate(R.layout.datacollection_fragment_location_details,
				container, false);
		initViewLayout(rootView);
		mEditLayout = (ScrollView) rootView.findViewById(R.id.edit_layout);
		mViewLayout = (ScrollView) rootView.findViewById(R.id.view_layout);		
		mLatitudeTextView = (TextView) rootView.findViewById(R.id.latitude_text_view);
		mLongitudeTextView = (TextView) rootView.findViewById(R.id.longitude_text_view);
		mAccuracyTextView = (TextView) rootView.findViewById(R.id.accuracy_text_view);
		mSetManualLocationButton = (Button) rootView.findViewById(R.id.set_manual_location_button);
		mUpdateInfoText = (TextView) rootView.findViewById(R.id.last_update_info);
		mBusinessNameTextView = (TextView) rootView.findViewById(R.id.business_name);
		mProceedButton = (Button) rootView.findViewById(R.id.proceed);
		mSetManualLocationButton.setOnClickListener(this);
		mProceedButton.setOnClickListener(this);
		// Create a new global location parameters object
        mLocationRequest = LocationRequest.create();
        /*
         * Set the update interval
         */
        mLocationRequest.setInterval(5000);

        // Use high accuracy
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        // Set the interval ceiling to one minute
        mLocationRequest.setFastestInterval(1000);
        
        mLocationClient = new LocationClient(mContext, this, this);
		try {
			populateSavedData();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return rootView;
	}
	
	private void initViewLayout(View rootView){
		Log.i("LocationDetailsFragment", "initViewLayout Called");
		mViewLatitudeText = (TextView) rootView.findViewById(R.id.view_latitude_text);
		mViewLongitudeText = (TextView) rootView.findViewById(R.id.view_longitude_text);
		mViewAccuracyText = (TextView) rootView.findViewById(R.id.view_accuracy_text);
		mViewBusinessNameText = (TextView) rootView.findViewById(R.id.view_business_name);
		mViewNextButton = (Button) rootView.findViewById(R.id.view_next);
		mViewNextButton.setOnClickListener(this);
		
	}
	
	 public void startUpdates(View v) {

	        if (servicesConnected()) {
	            startPeriodicUpdates();
	        }
	    }
	 
	 public void stopUpdates(View v) {

	        if (servicesConnected()) {
	            stopPeriodicUpdates();
	        }
	    }
	 
	 /**
	     * Verify that Google Play services is available before making a request.
	     *
	     * @return true if Google Play services is available, otherwise false
	     */
	    private boolean servicesConnected() {

	        // Check that Google Play services is available
	        int resultCode =
	                GooglePlayServicesUtil.isGooglePlayServicesAvailable(mContext);

	        // If Google Play services is available
	        if (ConnectionResult.SUCCESS == resultCode) {
	            // In debug mode, log the status
	           // Log.d(LocationUtils.APPTAG, getString(R.string.play_services_available));

	            // Continue
	            return true;
	        // Google Play services was not available for some reason
	        } else {
	            // Display an error dialog
	            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(resultCode, getActivity(), 0);
	            if (dialog != null) {
	                ErrorDialogFragment errorFragment = new ErrorDialogFragment();
	                errorFragment.setDialog(dialog);
	                errorFragment.show(getChildFragmentManager(), "LocationDetailsFragment");
	            }
	            return false;
	        }
	    }
	 
	 /**
	     * In response to a request to start updates, send a request
	     * to Location Services
	     */
	    private void startPeriodicUpdates() {
	    	if(mLocationClient.isConnected())
	        mLocationClient.requestLocationUpdates(mLocationRequest, this);
	      //  mConnectionState.setText(R.string.location_requested);
	    }

	    /**
	     * In response to a request to stop updates, send a request to
	     * Location Services
	     */
	    private void stopPeriodicUpdates() {
	    	if(mLocationClient.isConnected())
	        mLocationClient.removeLocationUpdates(this);
	       // mConnectionState.setText(R.string.location_updates_stopped);
	    }
	 
	 
	@Override
	public void onStart() {
		super.onStart();
		
		/*
         * Connect the client. Don't re-start any requests here;
         * instead, wait for onResume()
         */
        mLocationClient.connect();
		Log.i("LocationDetails", "onStart");
		
		/*mLocationManager = (LocationManager) mContext
				.getSystemService(mContext.LOCATION_SERVICE);
		isGPSEnabled = mLocationManager
				.isProviderEnabled(LocationManager.GPS_PROVIDER);*/
		
		/*if (isGPSEnabled) {
			if(!isLocationSet){
				startPeriodicUpdates();
			/*mLocationManager.requestLocationUpdates(
					LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES,
					MIN_DISTANCE_CHANGE_FOR_UPDATES,
					this);*/
		/*	setRefreshActionButtonState(true);
			}
			//mProgressBar.setVisibility(ProgressBar.VISIBLE);
			//mRefreshGPS.setVisibility(Button.GONE);

		} else {
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					mContext);
			alertDialogBuilder.setTitle("NO GPS");
			alertDialogBuilder.setMessage("Turn on the GPS")
					.setPositiveButton("OK",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									Intent intent = new Intent(
											Settings.ACTION_LOCATION_SOURCE_SETTINGS);
									startActivity(intent);

								}
							});
			alertDialogBuilder.show();

		}*/
		
		
	}
	
	@Override
	public void onStop() {
		Log.i("LocationDetailsFragment", "onStop");
		// TODO Auto-generated method stub
		
		 // If the client is connected
        if (mLocationClient.isConnected()) {
            stopPeriodicUpdates();
        }

        // After disconnect() is called, the client is considered "dead".
        mLocationClient.disconnect();

		super.onStop();
		//stopPeriodicUpdates();
		/*if(mLocationManager!=null){
			mLocationManager.removeUpdates(this);
		}*/
	}

	@Override
	public void onLocationChanged(Location location) {
		
		//Log.i("LocationDetailsFragment", "onLocationChanged");
		
		mLatitudeTextView.setText( String.valueOf(location.getLatitude()));
		mLongitudeTextView.setText(String.valueOf(location.getLongitude()));
		mAccuracyTextView.setText(String.valueOf(location.getAccuracy()));
		mLocationProvider = "GPS";
		//location.getTime();
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(location.getTime());
		calendar.get(Calendar.HOUR_OF_DAY);
		calendar.get(Calendar.MINUTE);
		//Log.i("LocationUpdat", String.valueOf(calendar.get(Calendar.HOUR_OF_DAY))+","+String.valueOf(calendar.get(Calendar.MINUTE)));
		mUpdateInfoText.setVisibility(TextView.VISIBLE);
		mUpdateInfoText.setText("Last Updated Time : "+String.valueOf(calendar.get(Calendar.HOUR_OF_DAY))+":"+String.valueOf(calendar.get(Calendar.MINUTE))+":"+String.valueOf(calendar.get(Calendar.SECOND)));
		//Log.i("Location", msg)
		//Date updateDate = new Date(location.getTime());
		
		//updateDate.
		//mUpdateInfoText.setVisibility(TextView.VISIBLE);
		if(((DataCollectionFragment)getParentFragment()).mViewPager.getCurrentItem() == 1){
		stopLocationUpdate();
		isLocationSet = true;
		setRefreshActionButtonState(false);
		}
		
	}

	

	@Override
	public void onClick(View v) {
		
		switch(v.getId()){
		case R.id.view_next:
			((DataCollectionFragment)getParentFragment()).mViewPager.setCurrentItem(2);	
			break;
		case R.id.set_manual_location_button:
			/*if(mLocationManager!=null){
				mLocationManager.removeUpdates(this);
			}*/
			if(mLocationClient.isConnected())
			stopPeriodicUpdates();
			Intent intent = new Intent(getActivity(),ManualLocationActivity.class);
			intent.putExtra("latitude",Double.valueOf(mLatitudeTextView.getText().toString()));
			intent.putExtra("longitude", Double.valueOf(mLongitudeTextView.getText().toString()));
			getParentFragment().startActivityForResult(intent, 200);
			
			setRefreshActionButtonState(false);
			break;
		case R.id.proceed:
			if(UserProfile.isAdmin)
				((AdminActivity)mContext).disableKeyBoard();
			else
				((CAActivity)mContext).disableKeyBoard();
			/*Log.i("BusinessId",((CAActivity)mContext).mBusinessId);
			((CAActivity)mContext).disableKeyBoard();*/
			if(isValidInput()){
			buildJson();
			((DataCollectionFragment)getParentFragment()).isBusinessCategoryScreenEnabled = true;
			((DataCollectionFragment)getParentFragment()).mViewPager.setCurrentItem(2);			
			}else{
				Toast.makeText(mContext, "Please fill required fields", Toast.LENGTH_LONG).show();
			}
			break;
		default:
			break;
		}
		
	}
	
	public boolean isValidInput(){
		if(mLatitudeTextView == null || mLongitudeTextView == null || mAccuracyTextView == null )
			return false;
		String latitude = mLatitudeTextView.getText().toString();
		String longitude = mLongitudeTextView.getText().toString();
		String accuracy = mAccuracyTextView.getText().toString();
		
		if(latitude.length() > 0 && longitude.length() > 0 && accuracy.length() > 0 && mLocationProvider.length()>0){
			return true;
		}
		
		return false;
	}
	
	public void buildJson(){
		if(UserProfile.isAdmin)
			((AdminActivity)mActivity).businessJson = new JSONObject();
		else
			((CAActivity)mActivity).businessJson = new JSONObject();
		String latitude = mLatitudeTextView.getText().toString();
		String longitude = mLongitudeTextView.getText().toString();
		String accuracy = mAccuracyTextView.getText().toString();
			try {
				if(UserProfile.isAdmin){
				((AdminActivity)mContext).businessJson.put("last_updated_user", UserProfile.EMAIL_ID);
				((AdminActivity)mContext).businessJson.put("latitude", latitude);
				((AdminActivity)mContext).businessJson.put("longitude", longitude);
				((AdminActivity)mContext).businessJson.put("location_accuracy", accuracy);
				((AdminActivity)mContext).businessJson.put("location_provider", mLocationProvider);
				}else{
					((CAActivity)mContext).businessJson.put("last_updated_user", UserProfile.EMAIL_ID);
					((CAActivity)mContext).businessJson.put("latitude", latitude);
					((CAActivity)mContext).businessJson.put("longitude", longitude);
					((CAActivity)mContext).businessJson.put("location_accuracy", accuracy);
					((CAActivity)mContext).businessJson.put("location_provider", mLocationProvider);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if(UserProfile.isAdmin)
			Log.i("LocationDetailsFragment", ((AdminActivity)mContext).businessJson.toString());
			else
				Log.i("LocationDetailsFragment", ((CAActivity)mContext).businessJson.toString());
		
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.i("LocationDetails", "onActivityResult");
		if(resultCode == getActivity().RESULT_OK){
			switch(requestCode){
			case 200:
				isLocationSet = true;
				Log.i("LocationDetailsFragment", "onactivityResult");
				mLatitudeTextView.setText( data.getStringExtra("latitude"));
				mLongitudeTextView.setText(data.getStringExtra("longitude"));
				mAccuracyTextView.setText("0");
				mUpdateInfoText.setVisibility(TextView.INVISIBLE);
				mLocationProvider = "MANUAL";
				break;
			default:
				break;
			}
		}
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		Log.i("LocationDetailsFragment", "onCreateOptionsMenu");
		//mMenuItem = menu.getItem(0);
		mMenuInflater = inflater;
		inflater.inflate(R.menu.location_selection_menu, menu);
		
		super.onCreateOptionsMenu(menu, inflater);
		this.optionsMenu = menu;
		this.mRefreshButton = menu.findItem(R.id.location_menuRefresh);
		if(!isLocationSet){
			setRefreshActionButtonState(true);
		}else{
			setRefreshActionButtonState(false);
		}
	}
	
	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		if(UserProfile.isAdmin){
			if(isAdminEditMode){
				
				mMenuInflater.inflate(R.menu.location_selection_menu, menu);
				menu.getItem(1).setVisible(false);
				//isAdminEditMode = false;
			}else{
				
				mMenuInflater.inflate(R.menu.admin_mode, menu);
				menu.getItem(0).setVisible(false);
			}
		}
		super.onPrepareOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_edit:
			((DataCollectionFragment)getParentFragment()).editModeSetup();
			/*isAdminEditMode = true;
			isViewMode = false;
			//((DataCollectionFragment) getParentFragment()).isLocationScreenEnabled = false;
			mActivity.invalidateOptionsMenu();
			mEditLayout.setVisibility(ScrollView.VISIBLE);
			mViewLayout.setVisibility(ScrollView.GONE);*/
			break;
	    case R.id.location_menuRefresh:
	    	if(isViewMode )
	    		return true;
	    	
	    	if(mLocationClient != null){
	    		startPeriodicUpdates();
	    	/*mLocationManager.requestLocationUpdates(
					LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES,
					MIN_DISTANCE_CHANGE_FOR_UPDATES,
					this);*/
	    	isLocationSet = false;
			setRefreshActionButtonState(true);
	    	}
	        // Complete with your code
	    return true;
	    }
	    return super.onOptionsItemSelected(item);
	}
	
	public void stopLocationUpdate(){
		stopPeriodicUpdates();
		/*if(mLocationManager!=null){
			mLocationManager.removeUpdates(this);
		}*/
	}
	
	public void setRefreshActionButtonState(final boolean refreshing) {
		Log.i("LocationDetailsFragment", "setRefreshActionButtonState");
		if (mRefreshButton != null) {
			if (refreshing) {
            	MenuItemCompat.setActionView(mRefreshButton, R.layout.custom_menu_item_progress);
               // refreshItem.setActionView(R.layout.custom_menu_item_progress);
            } else {
            	MenuItemCompat.setActionView(mRefreshButton, null);
               // refreshItem.setActionView(null);
            }
		}
	  /*  if (optionsMenu != null) {
	        final MenuItem refreshItem = optionsMenu
	            .findItem(R.id.location_menuRefresh);
	        if (refreshItem != null) {
	            if (refreshing) {
	            	MenuItemCompat.setActionView(refreshItem, R.layout.custom_menu_item_progress);
	               // refreshItem.setActionView(R.layout.custom_menu_item_progress);
	            } else {
	            	MenuItemCompat.setActionView(refreshItem, null);
	               // refreshItem.setActionView(null);
	            }
	        }
	    }*/
	}
	public void setBusinessName(String businessName){
		if(mBusinessNameTextView != null)
		mBusinessNameTextView.setText(businessName);
		if(mViewBusinessNameText != null)
		mViewBusinessNameText.setText(businessName);
	}
	
	public void setEditMode(){
		isAdminEditMode = true;
		isViewMode = false;
		mActivity.invalidateOptionsMenu();
		mEditLayout.setVisibility(ScrollView.VISIBLE);
		mViewLayout.setVisibility(ScrollView.GONE);
		
	}
	
	public void stopUpdate(){
		stopLocationUpdate();
		isLocationSet = true;
		setRefreshActionButtonState(false);
		
	}
	
	private void disableRefresh(){
		stopLocationUpdate();		
		//mSetManualLocationButton.setClickable(false);
		//mProceedButton.setClickable(false);
	}
	
	public void populateSavedData() throws JSONException{
		JSONObject businessJson;
		if(UserProfile.isAdmin)
			businessJson = ((AdminActivity) mContext).downloadedJson;
		else
			businessJson = ((CAActivity) mContext).downloadedJson;
		if (businessJson.length() == 0)
			return;
		
		if(businessJson.has("status")){
			
			stopUpdate();
			((DataCollectionFragment) getParentFragment()).isBusinessCategoryScreenEnabled = true;
			String status = businessJson.getString("status");
			if(status.equalsIgnoreCase("BUSINESS_VERIFIED_COMPLETE")){
				mEditLayout.setVisibility(ScrollView.GONE);
				mViewLayout.setVisibility(ScrollView.VISIBLE);
				disableRefresh();
				isViewMode = true;
				//setHasOptionsMenu(false);
				if(UserProfile.isAdmin){
					//setHasOptionsMenu(true);
					mActivity.invalidateOptionsMenu();
				}
			}else if(status.equalsIgnoreCase("BUSINESS_VERIFIED_INCOMPLETE")){
				
				if(UserProfile.isAdmin){
					isViewMode = true;
					//setHasOptionsMenu(false);
					mEditLayout.setVisibility(ScrollView.GONE);
					mViewLayout.setVisibility(ScrollView.VISIBLE);
					mActivity.invalidateOptionsMenu();
					//setHasOptionsMenu(true);
				}
			}else if(status.equalsIgnoreCase("TRANSIENT")){
				if(UserProfile.isAdmin){
					isViewMode = true;
					mEditLayout.setVisibility(ScrollView.GONE);
					mViewLayout.setVisibility(ScrollView.VISIBLE);
					//setHasOptionsMenu(false);
					mActivity.invalidateOptionsMenu();
					setHasOptionsMenu(true);
				}
			}
		}
		
		if(businessJson.has("latitude")){
			String latitude_text = businessJson.getString("latitude");
			mLatitudeTextView.setText(latitude_text);
				mViewLatitudeText.setText(latitude_text);
		}
		if(businessJson.has("longitude")){
			String longitude_text = businessJson.getString("longitude");
			mLongitudeTextView.setText(longitude_text);
			mViewLongitudeText.setText(longitude_text);
		}
		if(businessJson.has("location_accuracy")){
			String accuracy = businessJson.getString("location_accuracy");
			mAccuracyTextView.setText(accuracy);
			mViewAccuracyText.setText(accuracy);
		}
		if(businessJson.has("location_provider")){
			mLocationProvider = businessJson.getString("location_provider");
			
		}
		
		
		
		
		
		
		
		
		
	}
	
	 /**
     * Define a DialogFragment to display the error dialog generated in
     * showErrorDialog.
     */
    public static class ErrorDialogFragment extends DialogFragment {

        // Global field to contain the error dialog
        private Dialog mDialog;

        /**
         * Default constructor. Sets the dialog field to null
         */
        public ErrorDialogFragment() {
            super();
            mDialog = null;
        }

        /**
         * Set the dialog to display
         *
         * @param dialog An error dialog
         */
        public void setDialog(Dialog dialog) {
            mDialog = dialog;
        }

        /*
         * This method must return a Dialog to the DialogFragment.
         */
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return mDialog;
        }
    }

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onConnected(Bundle bundle) {
		// TODO Auto-generated method stub
		JSONObject jsonObject;
		if(UserProfile.isAdmin)
			jsonObject = ((AdminActivity)mContext).downloadedJson;
		else
			jsonObject = ((CAActivity)mContext).downloadedJson;
		
		if(jsonObject.has("status")){
			
		}else{
			startPeriodicUpdates();
		}
		
		
	}
	
	

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub
		
	}

}
