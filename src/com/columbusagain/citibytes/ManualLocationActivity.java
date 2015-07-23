package com.columbusagain.citibytes;

import java.util.Calendar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.columbusagain.citibytes.helper.Constants;
import com.columbusagain.citibytes.helper.HttpGet;
import com.columbusagain.citibytes.helper.JsonGetException;
import com.columbusagain.citibytes.helper.Timer;
import com.columbusagain.citibytes.util.NetworkChecker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class ManualLocationActivity extends ActionBarActivity implements OnClickListener,GooglePlayServicesClient.ConnectionCallbacks,
GooglePlayServicesClient.OnConnectionFailedListener {
	
	private long mStartTime,mStopTime;
	
	private Button mDoneButton,mCancelButton;
	
	private LatLng mSelectedLatLng;
	
	private Location mLastlocation;
	
	private Context mContext = this;
	
	// Stores the current instantiation of the location client in this object
    private LocationClient mLocationClient;
	
    private Marker map_marker;
	
	//private String mNorthEastLat, mNorthEastLng, mSouthWestLat, mSouthWestLng,mLatitude,mLongitude;
	
	/*double latitude, longitude, northeast_lat, northeast_lng, southwest_lat,
	southwest_lng;*/
	
	private String mPin,mArea;
	
    private GoogleMap googleMap;
    
    private ActionBar mActionBar;
    
    private LinearLayout mParentLayout;
    
 
    
 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_location);
        
        mLocationClient = new LocationClient(this, this, this);
        mActionBar = getSupportActionBar();
        mActionBar.setLogo(R.drawable.logo_navbar);
        mActionBar.setTitle("Location");
        mActionBar.setDisplayHomeAsUpEnabled(true);
        
        mPin = Constants.PIN;
        mArea = Constants.AREA;
        
        mParentLayout = (LinearLayout) findViewById(R.id.map_parent_layout);
        mDoneButton = (Button) findViewById(R.id.done_button);
        mCancelButton = (Button) findViewById(R.id.cancel_button);
        
        Intent intent = getIntent();
        Double default_lat = intent.getDoubleExtra("latitude", 0);
        Double default_lng = intent.getDoubleExtra("longitude", 0);
        if(default_lat > 0 && default_lng > 0)
        mSelectedLatLng = new LatLng(default_lat, default_lng);
		mDoneButton.setOnClickListener(this);
		mCancelButton.setOnClickListener(this);
		
		LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();

       // mLastlocation = mLocationClient.getLastLocation();
     // Loading map
        initilizeMap();
       /* try {
            
            
            boolean isNetworkConnected = NetworkChecker.isConnected(this);
    		if(!isNetworkConnected){
    		}else{
    			new FindBoundaries().execute();
    		}
 
        } catch (Exception e) {
            e.printStackTrace();
        }*/
 
    }
 
    /**
     * function to load map. If map is not created it will create it for you
     * */
    private void initilizeMap() {
        if (googleMap == null) {
            googleMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
 
            // check if map is created successfully or not
            if (googleMap == null) {
                Toast.makeText(getApplicationContext(),
                        "Sorry! unable to create maps", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }
 
    @Override
    protected void onResume() {
        super.onResume();
        initilizeMap();
    }
    
 /*   private class FindBoundaries extends AsyncTask<Void, Void, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();

		}

		@Override
		protected String doInBackground(Void... params) {
			String response = null;
			HttpGet httpGet_st = new HttpGet(
					"http://maps.googleapis.com/maps/api/geocode/json?components=postal_code:"
							+ mPin + "&sensor=false&region=in");

			try {
				response = httpGet_st.executeGet();
			} catch (JsonGetException e) {
				e.printStackTrace();
				if (e.toString().equals("Url error!")
						|| e.toString().equals("open connection error!")
						|| e.toString().equals("connect error!")) {
					response = "No Network";
				}
			}
			return response;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (result != null && !(result.equals("No Network"))) {

				try {
					JSONObject json_obj = new JSONObject(result);
					String status = json_obj.getString("status");
					if (status.equals("OK")) {
						JSONArray result_arr = json_obj.getJSONArray("results");
						JSONObject result_obj = result_arr.getJSONObject(0);
						JSONObject geoMetry = result_obj
								.getJSONObject("geometry");
						JSONObject bounds = geoMetry.getJSONObject("bounds");
						JSONObject northeast = bounds
								.getJSONObject("northeast");
						JSONObject soutwest = bounds.getJSONObject("southwest");
						JSONObject my_loc = geoMetry.getJSONObject("location");
						
						mLatitude=my_loc.getString("lat");
						mLongitude=my_loc.getString("lng");						
						mSouthWestLat = soutwest.getString("lat");
						mSouthWestLng = soutwest.getString("lng");
						mNorthEastLat = northeast.getString("lat");
						mNorthEastLng = northeast.getString("lng");
						
						latitude = Double.valueOf(mLatitude);
						longitude = Double.valueOf(mLongitude);
						northeast_lat = Double.valueOf(mNorthEastLat);
						northeast_lng = Double.valueOf(mNorthEastLng);
						southwest_lat = Double.valueOf(mSouthWestLat);
						southwest_lng = Double.valueOf(mSouthWestLng);
						init();

					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} else {
				//mNotification.setVisibility(TextView.VISIBLE);
				//mNotification.setText("Network connection error");
			}
		}

	}*/
    
    protected void init() {
		int layout_height = mParentLayout.getHeight();
		int layout_width = mParentLayout.getWidth();
		if (googleMap != null) {

			googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
			googleMap.setIndoorEnabled(false);
			googleMap.setMyLocationEnabled(true);
			/*LatLngBounds MYBOUNDARIES = new LatLngBounds(new LatLng(
					southwest_lat, southwest_lng), new LatLng(northeast_lat,
					northeast_lng));*/
			if(mLastlocation != null){
				if(mSelectedLatLng == null)
				mSelectedLatLng = new LatLng(mLastlocation.getLatitude(), mLastlocation.getLongitude()); 
			/*}else{
				mSelectedLatLng = new LatLng(latitude, longitude);
			}*/
			//final LatLng myplace = new LatLng(latitude, longitude);
			//mSelectedLatLng = myplace;
			map_marker = googleMap.addMarker(new MarkerOptions()
					.position(mSelectedLatLng)
					.title("unknown place")
					.snippet("unknown")
					.draggable(true)
					.icon(BitmapDescriptorFactory
							.fromResource(R.drawable.mappin)));
			//csu_ohio_marker.setVisible(true);
			googleMap.setOnMapClickListener(new OnMapClickListener() {
				
				@Override
				public void onMapClick(LatLng latLng) {
					// TODO Auto-generated method stub
					map_marker.setPosition(latLng);
					mSelectedLatLng = latLng;
				}
			});
				googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mSelectedLatLng, 15));
			}else{
				Toast.makeText(mContext, "Location not available", Toast.LENGTH_LONG).show();
			}
			/*}else{
				googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(MYBOUNDARIES, layout_width, layout_height, 10));
			}*/
		}
		

	}

	@Override
	public void onClick(View v) {
		Intent returnIntent = new Intent();
		switch (v.getId()) {
		
		case R.id.done_button:
			LatLng selected_location = map_marker.getPosition();
			returnIntent.putExtra("latitude",String.valueOf(selected_location.latitude));
			returnIntent.putExtra("longitude", String.valueOf(selected_location.longitude));
			setResult(RESULT_OK, returnIntent);
			finish();	
			
			break;
		case R.id.cancel_button:
			setResult(RESULT_CANCELED, returnIntent);
			finish();
			break;

		default:
			break;
		}
		
	}
	
	
	@Override
	protected void onStop() {
		googleMap.setMyLocationEnabled(false);
		
		super.onStop();
		if(mLocationClient.isConnected())
		mLocationClient.disconnect();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
		case android.R.id.home:
			Intent returnIntent = new Intent();
			setResult(RESULT_CANCELED, returnIntent);
			finish();
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	 
	@Override
	protected void onResumeFragments() {
		super.onResumeFragments();
		
		Timer.startTimer();
		/*Log.i("ChainNameActivity", "onResume");
		Calendar calendar  = Calendar.getInstance();		
		mStartTime = calendar.getTimeInMillis();*/
		
	}

	
	@Override
	protected void onPause() {
		super.onPause();
		Timer.stopTimer();
		/*Calendar calendar  = Calendar.getInstance();		
		mStopTime = calendar.getTimeInMillis();
		CAActivity.mTimeCalculation +=(int) mStopTime-mStartTime;*/
		
	}
	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		mLocationClient.connect();
	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onConnected(Bundle arg0) {
		// TODO Auto-generated method stub
		mLastlocation = mLocationClient.getLastLocation();
		init();
		
	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub
		
	}
	

	
	
	

}
