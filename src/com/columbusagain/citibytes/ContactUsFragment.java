package com.columbusagain.citibytes;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ContactUsFragment extends Fragment {
	
	ActionBar mActionBar;
	
	Activity mActivity;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mActivity = activity;
		if(activity instanceof CAActivity){
			mActionBar = ((CAActivity)mActivity).getSupportActionBar();
		}else{
			mActionBar = ((AdminActivity)mActivity).getSupportActionBar();
		}
		
		mActionBar.setTitle("Contact Us");
	}
	
	public ContactUsFragment() {
    }
	
	 @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		 View rootView = inflater.inflate(R.layout.contact_us_fragment, container, false);
		 return rootView;
		 
	 }
	

}
