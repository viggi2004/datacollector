package com.columbusagain.citibytes;

import com.columbusagain.citibytes.helper.Constants;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Marker;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class HomeMapFragment extends Fragment {
	
	//private String mPin;
	
	private Activity mAcitivity;
	
	private Context mContext;
	
	private GoogleMap map;
	
	private String mNorthEastLat, mNorthEastLng, mSouthWestLat, mSouthWestLng,mLatitude,mLongitude;
	
	double latitude, longitude, northeast_lat, northeast_lng, southwest_lat,
	southwest_lng;
	
	Marker mMarker;
	
	private SupportMapFragment mMapFragment;
	
	public HomeMapFragment(){
		
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View rootView = inflater.inflate(R.layout.fragment_map_screen,
				container, false);
		//mPin = Constants.PIN;
		if (map == null) {
			
			mMapFragment = SupportMapFragment.newInstance();		    
		     mMapFragment = ((SupportMapFragment) getFragmentManager().findFragmentById(R.id.map));
			//Log.i("MapFragment","map is null");
		map = mMapFragment
	               .getMap();
		}else{
			Log.i("MapFragment","map is not null");
		}
		return rootView;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.mAcitivity = activity;
		this.mContext = activity;
	}

	@Override
	public void onDetach() {
		super.onDetach();
	}
}
