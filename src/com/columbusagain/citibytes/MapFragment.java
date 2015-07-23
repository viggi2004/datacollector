package com.columbusagain.citibytes;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
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
import android.widget.LinearLayout;

import com.columbusagain.citibytes.helper.Constants;
import com.columbusagain.citibytes.helper.HttpGet;
import com.columbusagain.citibytes.helper.JsonGetException;
import com.columbusagain.citibytes.util.NetworkChecker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapFragment extends Fragment  {
	
	private String mNorthEastLat, mNorthEastLng, mSouthWestLat, mSouthWestLng,mLatitude,mLongitude;
	
	double latitude, longitude, northeast_lat, northeast_lng, southwest_lat,
	southwest_lng;

	ActionBar mActionBar;
	
	private LinearLayout mMapLayout;
	
	Marker csu_ohio_marker;

	static final LatLng HAMBURG = new LatLng(53.558, 9.927);
	static final LatLng KIEL = new LatLng(53.551, 9.993);
	
	private GoogleMap map;
	
	private String mCurrentLat, mCurrentLng;
	
	private View rootView;
	
	private String mPin;
	
	private Activity mActivity;
	
	private SupportMapFragment mMapFragment;
	

	public MapFragment() {
		// Empty constructor required for fragment subclasses
	}
	
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		
		mActivity = activity;
		 mActionBar = ((CAActivity)mActivity).getSupportActionBar();
		
	}
	
	@Override
	public void onDetach() {
		// TODO Auto-generated method stub
		super.onDetach();
		//((BaseDrawerActivity)mActivity).stopLocationUpdate();
		mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		//mMapFragment.onDestroyView();
		//map = null;
		Log.i("MapFragment","onDetach");
	}
	
	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		map.setMyLocationEnabled(false);
		super.onDestroyView();
		//getChildFragmentManager().beginTransaction().hide(this).commit();
		Log.i("MapFragment","ondestroyView");
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		setHasOptionsMenu(true);
		super.onCreateView(inflater, container, savedInstanceState);
		
		//if(Constants.FLAG == 0){
			Log.i("MapFragment", "flag = "+Constants.FLAG+"EditButtonState = "+Constants.isEditBusinessClicked);
		//}
	    
	    if(Constants.FLAG == 0 ||  Constants.view==null ){
	    	rootView = inflater.inflate(R.layout.fragment_map_screen,
					container, false);
	    	Constants.view = rootView;
	    	Constants.FLAG = 1;
	    	Constants.isEditBusinessClicked = false;
	    }else{
	    	rootView = Constants.view;
	    }
	    
	    if (rootView != null) {
	        ViewGroup parent = (ViewGroup) rootView.getParent();
	        if (parent != null)
	            parent.removeView(rootView);
	    }
	    
	   
	    	
	    	if (map == null) {
				
				mMapFragment = SupportMapFragment.newInstance();		    
			     mMapFragment = ((SupportMapFragment) getFragmentManager().findFragmentById(R.id.map));
				//Log.i("MapFragment","map is null");
			map = mMapFragment
		               .getMap();
			}else{
				Log.i("MapFragment","map is not null");
			}
	    
		Log.i("oncreateView", "rootViewretain");
		//if(savedInstanceState == null){
			 mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
			Log.i("oncreateView", "rootViewCreate");
		
		mMapLayout = (LinearLayout) rootView.findViewById(R.id.map_layout);
		mPin = Constants.PIN;
		//mArea = Constants.AREA;
		mActionBar.setTitle(mPin);
		boolean isNetworkConnected = NetworkChecker.isConnected(getActivity());
		if(!isNetworkConnected){
		}else{
			new FindBoundaries().execute();
		}
	 	//	}
		
		
		

		

		map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
		map.setIndoorEnabled(false);
		map.setMyLocationEnabled(true);
		
		//ViewTreeObserver observer = mMapLayout.getViewTreeObserver();
		//observer.addOnGlobalLayoutListener(this);
		return rootView;

	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
		case R.id.action_edit:
			((CAActivity)mActivity).editBusinessFragment();
			Constants.isEditBusinessClicked = true;
			break;
			
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.map_fragment, menu);
		super.onCreateOptionsMenu(menu, inflater);

	}

		
	private class FindBoundaries extends AsyncTask<Void, Void, String> {
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

	}
	
	protected void init() {
		int layout_height = mMapLayout.getHeight();
		int layout_width = mMapLayout.getWidth();
		if (map != null) {

			map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
			map.setIndoorEnabled(false);
			map.setMyLocationEnabled(true);
			LatLngBounds MYBOUNDARIES = new LatLngBounds(new LatLng(
					southwest_lat, southwest_lng), new LatLng(northeast_lat,
					northeast_lng));

			LatLng myplace = new LatLng(latitude, longitude);
			Marker csu_ohio_marker = map.addMarker(new MarkerOptions()
					.position(myplace)
					.title("unknown place")
					.snippet("unknown")
					.icon(BitmapDescriptorFactory
							.fromResource(R.drawable.mappin)));
			map.animateCamera(CameraUpdateFactory.newLatLngBounds(MYBOUNDARIES,
					layout_width, layout_height, 1));
		}

	}
	

}
