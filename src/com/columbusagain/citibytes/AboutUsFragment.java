package com.columbusagain.citibytes;

import com.columbusagain.citibytes.helper.Constants;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class AboutUsFragment extends Fragment{
	
	public AboutUsFragment() {
        // Empty constructor required for fragment subclasses
    }
	 @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		 View rootView = inflater.inflate(R.layout.about_us_fragment, container, false);
		 int i = getArguments().getInt(Constants.ACTION_BAR_TITLE);
         String planet = getResources().getStringArray(R.array.navigation_array)[i];
         getActivity().setTitle(planet);
		 return rootView;
		 
	 }
	

}
