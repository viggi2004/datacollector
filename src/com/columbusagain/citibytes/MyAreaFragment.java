package com.columbusagain.citibytes;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONException;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.pdf.PdfDocument;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.columbusagain.citibytes.adapters.PinCodeApprovedListAdapter;
import com.columbusagain.citibytes.adapters.PinCodePendingListAdapter;
import com.columbusagain.citibytes.adapters.PincodeRequestListAdapter;
import com.columbusagain.citibytes.helper.Constants;
import com.columbusagain.citibytes.helper.HttpGet;
import com.columbusagain.citibytes.helper.JsonGetException;
import com.columbusagain.citibytes.helper.AreaDetails;
import com.columbusagain.citibytes.helper.UserProfile;
import com.columbusagain.citibytes.jsonparser.AllPincodes;
import com.columbusagain.citibytes.jsonparser.ApprovedPincodes;
import com.columbusagain.citibytes.jsonparser.PendingPincodes;
import com.columbusagain.citibytes.util.NetworkChecker;

public class MyAreaFragment extends Fragment implements ActionBar.TabListener{
	
    /**
     * The {@link ViewPager} that will display the three primary sections of the app, one at a
     * time.
     */
    ViewPager mViewPager;
    
    private static boolean isApprovedPincodesDownloaded = false;

	private static boolean isPendingPindesDownloadded = false;

	private static boolean isToRequestPincodesDownloaded = false;
    
	//public static String PROFILE_SUBTITLE = "profile_sub title";
	
	//public static String PROFILE_PICTURE_URL = "profile_picture_url";
	
	//public static String PROFILE_NAME = "profile_name";
	
	private ActionBar mActionbar;
	
	//private PincodeFragmentPagerAdapter mPagerAdapter;
	
	private static Activity mActivity;	
	
	private static Dialog mProgressDialog;
	
	public MyAreaFragment() {
		
    }
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		
		isApprovedPincodesDownloaded = false;
		isPendingPindesDownloadded = false;
	    isToRequestPincodesDownloaded = false;
		
		
		this.mActivity= activity;
		
		mActivity = activity;
		
		
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
		Log.i("MyAreaFragment","onDestroy");
	}
	@Override
	public void onDestroyOptionsMenu() {
		// TODO Auto-generated method stub
		super.onDestroyOptionsMenu();
		Log.i("MyAreaFragment","onDestroyOptionsMenu");
	}
	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
		Log.i("MyAreaFragment","onDestroyView");
	}
	
	@Override
	public void onDetach() {
		// TODO Auto-generated method stub
		super.onDetach();
		
		mActionbar.removeAllTabs();		
		mActionbar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		Log.i("MyAreaFragment","onDetach");
		//getChildFragmentManager().beginTransaction().hide(this).commit();
		
		
	}
	 @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		 
		 mActionbar = ((CAActivity)mActivity).getSupportActionBar();
		 if(mActionbar != null)
		 mActionbar.setTitle(UserProfile.CITY);
		 Log.i("MyAreaFragment","onattach");
		 Log.i("MyAreaFragment", "oncreateView");
		 View rootView = inflater.inflate(R.layout.activity_area_search, container, false);
		  
		 	mProgressDialog = new Dialog(mActivity,R.style.ProgressDialog);
		 	mProgressDialog.setContentView(R.layout.progress_dialog);
		 	//mProgressDialog.show();
	        mActionbar.setDisplayHomeAsUpEnabled(true);
	        mActionbar.setHomeButtonEnabled(true);
	        mActionbar.setLogo(R.drawable.logo_navbar);
	        mActionbar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
	        mViewPager = (ViewPager) rootView.findViewById(R.id.pager);
	        //mPagerAdapter = new PincodeFragmentPagerAdapter(getChildFragmentManager());
	        mViewPager.setAdapter(new PincodeFragmentPagerAdapter(getChildFragmentManager()));
	        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
	            @Override
	            public void onPageSelected(int position) {
	            	mActionbar.setSelectedNavigationItem(position);
	            	
	            }
	        });

	        mActionbar.addTab(mActionbar.newTab()
					.setText("APPROVED").setTabListener(this));
	        mActionbar.addTab(mActionbar.newTab().setText("PENDING")
					.setTabListener(this));
	        mActionbar.addTab(mActionbar.newTab().setText("TO REQUEST")
					.setTabListener(this));
	        mViewPager.setOffscreenPageLimit(3);
			mViewPager.setCurrentItem(2, true);
	        
	   
	    
		 return rootView;
		 
	 }

	 @Override
		public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
			 inflater.inflate(R.menu.area_search, menu);
			super.onCreateOptionsMenu(menu, inflater);
			
			
		}
	@Override
	public void onTabReselected(Tab arg0, FragmentTransaction arg1) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onTabSelected(Tab tab, FragmentTransaction arg1) {
		mViewPager.setCurrentItem(tab.getPosition());
		
	}
	@Override
	public void onTabUnselected(Tab arg0, FragmentTransaction arg1) {
		// TODO Auto-generated method stub
		
	}
	
	public class PincodeFragmentPagerAdapter extends FragmentPagerAdapter{

		public PincodeFragmentPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			Fragment fragment;
			switch(position){
			
			case 0:
				fragment = new ApprovedFragment();
				return fragment;
			case 1:
				fragment = new PendingFragment();
				return fragment;
			case 2:
				fragment = new ToRequestFragment();
				return fragment;
			default:
				break;
					
			}
			return null;
		}

		@Override
		public int getCount() {				
			return 3;
		}
		
    	
    }
	
	public static class ApprovedFragment extends Fragment implements OnQueryTextListener{
		private TextView mEmptyTextView;
    	private ListView mListView;
    	private View rootView;
    	private String mQueryText="";
    	PinCodeApprovedListAdapter mListAdapter;
    	private ArrayList<AreaDetails> mApprovedAreaList = new ArrayList<AreaDetails>();
    	private ArrayList<AreaDetails> mAdapterAreaList = new ArrayList<AreaDetails>();
    	
    	ApprovedPincodes approvedPincodes;
    	public ApprovedFragment() {
        }
    	
    	
    	 @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
    		 
    		 setHasOptionsMenu(true);
    		 
    		 Log.i("ApprovedFragment", "OnCreateView");
    		 rootView = inflater.inflate(R.layout.fragment_to_request, container, false);
    		 
    			mListView = (ListView) rootView.findViewById(R.id.list_view);
    			mEmptyTextView = (TextView) rootView.findViewById(R.id.empty_text);
    			mListView.setEmptyView(rootView.findViewById(R.id.empty_view));
    			mListAdapter = new PinCodeApprovedListAdapter( getActivity(),mAdapterAreaList);
    			mListView.setAdapter(mListAdapter);
    		
    		 boolean isNetworkConnected = NetworkChecker.isConnected(mActivity);
    			if(!isNetworkConnected){
    			}else{
    			mProgressDialog.show();
    				new GetApprovedPincodeRequests().execute();
    			}
    		 
    			    		 
    		return rootView;
    		 
    	 }
    	 
	    	 
	    	 @Override
	    	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
	    		 Log.i("ApprovedFragment","menu");
	    		 inflater.inflate(R.menu.area_search, menu);
	    			MenuItem searchItem = menu.findItem(R.id.action_search);
	    			SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
	    		    SearchView.SearchAutoComplete searchAutoComplete = (SearchView.SearchAutoComplete) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
	    		    
	    		     LinearLayout searchPlate = (LinearLayout) searchView.findViewById(android.support.v7.appcompat.R.id.search_plate);
	    		     ImageView close_button = (ImageView) searchView.findViewById(android.support.v7.appcompat.R.id.search_close_btn);
	    		     close_button.setImageResource(R.drawable.cross);
	    		    searchPlate.setBackgroundResource(R.drawable.nav_search);
	    		    searchAutoComplete.setCompoundDrawablesWithIntrinsicBounds(R.drawable.action_search, 0, 0, 0);
	    		    searchAutoComplete.setHint("Approved");
	    		    searchAutoComplete.setHintTextColor(getResources().getColor(R.color.searchbar_hint));
	    		    searchAutoComplete.setTextColor(Color.WHITE);
	    		    searchView.setOnQueryTextListener(this);
	    		super.onCreateOptionsMenu(menu, inflater);
	    		updateList();
	    		
	    	}
    	 
    	 private class GetApprovedPincodeRequests extends AsyncTask<Void, Void, String>{

 			@Override
 			protected String doInBackground(Void... params) {
 				if(UserProfile.CITY == null || UserProfile.EMAIL_ID == null )
 					return null;
 				HttpGet httpGet = new HttpGet(Constants.BASE_URL+"getApprovedPincodeRequests.php?email="+UserProfile.EMAIL_ID+"&city="+UserProfile.CITY.toLowerCase());
 				//Log.i("AreaaSearchActivity",UserProfile.EMAIL_ID);
 				
 				String response=null;
				try {
					response = httpGet.executeGet();
				} catch (JsonGetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					response = null;
				}
 				
 			//	String response = httpPost.executePost();	 	 				
 				return response;
 			}
 			
 			@Override
 			protected void onPostExecute(String result) {
 				super.onPostExecute(result);
 				isApprovedPincodesDownloaded = true;
 				if(mProgressDialog.isShowing() && isApprovedPincodesDownloaded && isPendingPindesDownloadded && isToRequestPincodesDownloaded){
 					mProgressDialog.cancel();
 				}
 			
    				
 				if(result!=null){
 					//Log.i("response", result);
 					approvedPincodes = new ApprovedPincodes(result);
 					approvedPincodes.requestParsing();
 					if("success".equals(approvedPincodes.getStatus())){
 						if(approvedPincodes.getCount()>0){
 							mApprovedAreaList = approvedPincodes.getApprovedPincodes();
 							mAdapterAreaList.clear();
 							onQueryTextChange(mQueryText);
 							/*for(int i=0;i<mApprovedAreaList.size();i++)
 								mAdapterAreaList.add(mApprovedAreaList.get(i));
 							mListAdapter.notifyDataSetChanged();*/
 							//mListAdapter = new PinCodeApprovedListAdapter(getActivity(), mApprovedAreaList);
	 						//mListView.setAdapter(mListAdapter);
	 						
 						}else{
 							mAdapterAreaList.clear();
 							mListAdapter.notifyDataSetChanged();
 							mEmptyTextView.setText("No Approved Pincode Requests");
 							mEmptyTextView.setVisibility(TextView.VISIBLE);
 						}
 						
 						
 						
 					}else{
 						//mEmptyTextView.setVisibility(TextView.GONE);
						try {
							String error = approvedPincodes.getErrorMessage();
							Toast.makeText(mActivity, error, Toast.LENGTH_LONG).show();
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
 						
 					}
 				}else{
 					//mEmptyTextView.setVisibility(TextView.GONE);
 				}
 			}
 			
 			
 	    }

		
		private void updateList() {	
			if(mApprovedAreaList != null && mListAdapter!=null && mListView!=null){
				mAdapterAreaList.clear();
				for(int i=0;i<mApprovedAreaList.size();i++)
					mAdapterAreaList.add(mApprovedAreaList.get(i));
				mListAdapter.notifyDataSetChanged();
				//mListAdapter = new PinCodeApprovedListAdapter(getActivity(), mApprovedAreaList);
				//mListView.setAdapter(mListAdapter);
			}
			
			Log.i("ApprovedFragment","callback");
			boolean isNetworkConnected = NetworkChecker.isConnected(mActivity);
			if(!isNetworkConnected){
			}else{
			//mProgressDialog.show();
				new GetApprovedPincodeRequests().execute();
			}
			
		}
		@Override
		public boolean onQueryTextChange(String text) {
			mQueryText = text;
			Log.i("ApprovedFragment", "onQueryTextChange "+text);
			if(approvedPincodes!=null){
				mAdapterAreaList.clear();
				for(int i=0;i<mApprovedAreaList.size();i++)
					mAdapterAreaList.add(mApprovedAreaList.get(i));
				mListAdapter.notifyDataSetChanged();
				//mListAdapter = new PinCodeApprovedListAdapter(getActivity(), mApprovedAreaList);
				//mListView.setAdapter(mListAdapter);
			if(text.length()>0){
				ArrayList<AreaDetails> newPincodeList = new ArrayList<AreaDetails>();
				newPincodeList = approvedPincodes.searchPins(text);
				mAdapterAreaList.clear();
				for(int i=0;i<newPincodeList.size();i++)
					mAdapterAreaList.add(newPincodeList.get(i));
				mListAdapter.notifyDataSetChanged();
				//mListAdapter = new PinCodeApprovedListAdapter(getActivity(), newPincodeList);
				//mListView.setAdapter(mListAdapter);
			}
			}
			return false;
		}
		@Override
		public boolean onQueryTextSubmit(String arg0) {
			// TODO Auto-generated method stub
			return false;
		}
    	
    }
    
    public static class PendingFragment extends Fragment implements OnQueryTextListener{
    	private TextView mEmptyTextView;
    	private ListView mListView;
    	PinCodePendingListAdapter mListAdapter;
    	private String mQueryText="";
    	private View rootView;
    	PendingPincodes pendingPincodes;
    	private ArrayList<AreaDetails> mPendingAreaList = new ArrayList<AreaDetails>();
    	private ArrayList<AreaDetails> mAdapterAreaList = new ArrayList<AreaDetails>();
    	public PendingFragment() {
        }
    	 @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
    		 setHasOptionsMenu(true);
    		 rootView = inflater.inflate(R.layout.fragment_to_request, container, false);
    		 for(int i=0;i<mPendingAreaList.size();i++)
    			 mAdapterAreaList.add(mPendingAreaList.get(i));
    		 mListAdapter = new PinCodePendingListAdapter( getActivity(),mAdapterAreaList);
    			///adapter = new PinCodePendingListAdapter( getActivity(),mAreaList);
    			
    			mListView = (ListView) rootView.findViewById(R.id.list_view);
    			mListView.setEmptyView(rootView.findViewById(R.id.empty_view));
    			mEmptyTextView = (TextView) rootView.findViewById(R.id.empty_text);
    			mListView.setAdapter(mListAdapter);
    			///mListView.setAdapter(adapter);
    		 
    			boolean isNetworkConnected = NetworkChecker.isConnected(mActivity);
    			if(!isNetworkConnected){
    			}else{
    			mProgressDialog.show();
    				new GetPendingPincodeRequests().execute();
    			}
    		 return rootView;
    		 
    	 }
    	 
	    	 
	    	 @Override
		    	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		    		// Log.i("Menu","Fragment");
		    		 inflater.inflate(R.menu.area_search, menu);
		    			MenuItem searchItem = menu.findItem(R.id.action_search);
		    			SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
		    		    SearchView.SearchAutoComplete searchAutoComplete = (SearchView.SearchAutoComplete) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
		    		    
		    		     LinearLayout searchPlate = (LinearLayout) searchView.findViewById(android.support.v7.appcompat.R.id.search_plate);
		    		     ImageView close_button = (ImageView) searchView.findViewById(android.support.v7.appcompat.R.id.search_close_btn);
		    		     close_button.setImageResource(R.drawable.cross);
		    		    searchPlate.setBackgroundResource(R.drawable.nav_search);
		    		    searchAutoComplete.setCompoundDrawablesWithIntrinsicBounds(R.drawable.action_search, 0, 0, 0);
		    		    searchAutoComplete.setHint("Pending");
		    		    searchAutoComplete.setHintTextColor(getResources().getColor(R.color.searchbar_hint));
		    		    searchAutoComplete.setTextColor(Color.WHITE);
		    		    searchView.setOnQueryTextListener(this);
		    		super.onCreateOptionsMenu(menu, inflater);
		    		updateList();
		    	}
    	 
    	 private class GetPendingPincodeRequests extends AsyncTask<Void, Void, String>{

	 			@Override
	 			protected String doInBackground(Void... params) {
	 				if(UserProfile.CITY == null || UserProfile.EMAIL_ID == null )
	 					return null;
	 				HttpGet httpGet = new HttpGet(Constants.BASE_URL+"getPendingPincodeRequests.php?email="+UserProfile.EMAIL_ID+"&city="+UserProfile.CITY.toLowerCase());
	 						 				
	 				//Log.i("AreaaSearchActivity",UserProfile.EMAIL_ID);
	 				
	 				String response=null;
					try {
						response = httpGet.executeGet();
					} catch (JsonGetException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						response = null;
					}
	 				
	 				return response;
	 			}
	 			
	 			@Override
	 			protected void onPostExecute(String result) {
	 				super.onPostExecute(result);
	 				isPendingPindesDownloadded = true;
	 				if(mProgressDialog.isShowing() && isApprovedPincodesDownloaded && isPendingPindesDownloadded && isToRequestPincodesDownloaded){
	 					mProgressDialog.dismiss();
	 				}
	 				
	 				if(result!=null){
	 					//Log.i("response", result);
	 					pendingPincodes = new PendingPincodes(result);
	 					pendingPincodes.requestParsing();
	 					if("success".equals(pendingPincodes.getStatus())){
	 						if(pendingPincodes.getCount()>0){
	 							mAdapterAreaList.clear();
	 							mPendingAreaList = pendingPincodes.getPendingPincodes();
	 							onQueryTextChange(mQueryText);
	 						/*	for(int i=0;i<mPendingAreaList.size();i++)
	 				    			 mAdapterAreaList.add(mPendingAreaList.get(i));
	 							mListAdapter.notifyDataSetChanged();*/
	 							//mListAdapter = new PinCodePendingListAdapter( getActivity(),mPendingAreaList);
	 							//mListView.setAdapter(mListAdapter);
	 						}else{
	 							mAdapterAreaList.clear();
	 							mListAdapter.notifyDataSetChanged();
	 							mEmptyTextView.setText("No Pending Pincode Requests");
	 							mEmptyTextView.setVisibility(TextView.VISIBLE);
	 						}
	 						
	 						
	 						
	 					}else{
	 						//mEmptyTextView.setVisibility(TextView.GONE);
							try {
								String error = pendingPincodes.getErrorMessage();
								Toast.makeText(mActivity, error, Toast.LENGTH_LONG).show();
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
	 						
	 					}
	 				}else{
	 					//mEmptyTextView.setVisibility(TextView.GONE);
	 				}
	 				
	 				
	 			}

	 			
	 	    }

		
		private void updateList() {
			//Log.i("PendingFragment","callback");
			if(mPendingAreaList != null && mListAdapter!=null && mListView!=null){
				mAdapterAreaList.clear();
				for(int i=0;i<mPendingAreaList.size();i++)
	    			 mAdapterAreaList.add(mPendingAreaList.get(i));
				mListAdapter.notifyDataSetChanged();
				//mListAdapter = new PinCodePendingListAdapter( getActivity(),mPendingAreaList);
				//	mListView.setAdapter(mListAdapter);
			}
			boolean isNetworkConnected = NetworkChecker.isConnected(mActivity);
			if(!isNetworkConnected){
			}else{
			//mProgressDialog.show();
				new GetPendingPincodeRequests().execute();
			}
			
		}
		@Override
		public boolean onQueryTextChange(String text) {
			mQueryText = text;
			if(pendingPincodes!=null){
				mAdapterAreaList.clear();
				for(int i=0;i<mPendingAreaList.size();i++)
	    			 mAdapterAreaList.add(mPendingAreaList.get(i));
				mListAdapter.notifyDataSetChanged();
				if(text.length()>0){
					ArrayList<AreaDetails> newPincodeList = new ArrayList<AreaDetails>();
					newPincodeList = pendingPincodes.searchPins(text);
					mAdapterAreaList.clear();
					for(int i=0;i<newPincodeList.size();i++)
		    			 mAdapterAreaList.add(newPincodeList.get(i));
					mListAdapter.notifyDataSetChanged();
					//mListAdapter = new PinCodePendingListAdapter( mActivity,newPincodeList);
					
					//mListView.setAdapter(mListAdapter);
				}
			}
			
			return false;
		}
		@Override
		public boolean onQueryTextSubmit(String arg0) {
			// TODO Auto-generated method stub
			return false;
		}
    	
    	
    }
    
    public static class ToRequestFragment extends Fragment implements OnQueryTextListener{
    	private TextView mEmptyTextView;
    	private ListView mListView;
    	private View rootView;
    	private String mQueryText="";
    	private PincodeRequestListAdapter mListAdapter;
    	private ArrayList<AreaDetails> mAllAreaList = new ArrayList<AreaDetails>();
    	private ArrayList<AreaDetails> mAdapterAreaList = new ArrayList<AreaDetails>();
    	AllPincodes allPincodes;
    	public ToRequestFragment() {
        }
    	
    	
    	 @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
    		 setHasOptionsMenu(true);
    		// Log.i("ToRequestFragment", "OnCreateView");
    		 if(savedInstanceState == null){
    		 rootView = inflater.inflate(R.layout.fragment_to_request, container, false);
    		 }
    		
    		 for(int i=0;i<mAllAreaList.size();i++)
    			 mAdapterAreaList.add(mAllAreaList.get(i));
    		 mListAdapter = new PincodeRequestListAdapter(mActivity, mAdapterAreaList);
    			
    			mListView = (ListView) rootView.findViewById(R.id.list_view);
    			mEmptyTextView = (TextView) rootView.findViewById(R.id.empty_text);
    			mListView.setEmptyView(rootView.findViewById(R.id.empty_view));
    			mListView.setAdapter(mListAdapter);
    		
    			
    			boolean isNetworkConnected = NetworkChecker.isConnected(mActivity);
    			if(!isNetworkConnected){
    			}else{
    			mProgressDialog.show();
    				new GetAllPincodes().execute();
    			}
    	//	 }	
    		 return rootView;
    		 
    	 }
    	 
    	 
    	 @Override
	    	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
	    	//	 Log.i("Menu","Fragment");
	    		 inflater.inflate(R.menu.area_search, menu);
	    			MenuItem searchItem = menu.findItem(R.id.action_search);
	    			SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
	    		    SearchView.SearchAutoComplete searchAutoComplete = (SearchView.SearchAutoComplete) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
	    		    
	    		     LinearLayout searchPlate = (LinearLayout) searchView.findViewById(android.support.v7.appcompat.R.id.search_plate);
	    		     ImageView close_button = (ImageView) searchView.findViewById(android.support.v7.appcompat.R.id.search_close_btn);
	    		     close_button.setImageResource(R.drawable.cross);
	    		    searchPlate.setBackgroundResource(R.drawable.nav_search);
	    		    searchAutoComplete.setCompoundDrawablesWithIntrinsicBounds(R.drawable.action_search, 0, 0, 0);
	    		    searchAutoComplete.setHint("To Request");
	    		    searchAutoComplete.setHintTextColor(getResources().getColor(R.color.searchbar_hint));
	    		    searchAutoComplete.setTextColor(Color.WHITE);
	    		    searchView.setOnQueryTextListener(this);
	    		super.onCreateOptionsMenu(menu, inflater);
	    		updateList();
	    	}
    	 
    	 
    	 private class GetAllPincodes extends AsyncTask<Void, Void, String>{

	 			@Override
	 			protected String doInBackground(Void... params) {
	 				if(UserProfile.CITY == null || UserProfile.EMAIL_ID == null )
	 					return null;
	 				HttpGet httpGet = new HttpGet(Constants.BASE_URL+"getAllPincodes.php?email="+UserProfile.EMAIL_ID+"&city="+UserProfile.CITY.toLowerCase());
	 							
	 				//Log.i("AreaaSearchActivity",UserProfile.EMAIL_ID);
	 				
	 				String response=null;
					try {
						response = httpGet.executeGet();
					} catch (JsonGetException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						response = null;
					}
	 				
	 			//	String response = httpPost.executePost();	 	 				
	 				return response;
	 			}
	 			
	 			@Override
	 			protected void onPostExecute(String result) {
	 				super.onPostExecute(result);
	 				isToRequestPincodesDownloaded = true;
	 				if(mProgressDialog.isShowing() && isApprovedPincodesDownloaded && isPendingPindesDownloadded && isToRequestPincodesDownloaded){
	 					mProgressDialog.dismiss();
	 				}
	 				
	 				if(result!=null){
	 					//Log.i("response", result);
	 					allPincodes = new AllPincodes(result);
	 					allPincodes.requestParsing();
	 					if("success".equals(allPincodes.getStatus())){
	 						if(allPincodes.getCount()>0){
	 						mAllAreaList = allPincodes.getAllPincodes();
	 						mAdapterAreaList.clear();
	 						onQueryTextChange(mQueryText);
	 						/*mAdapterAreaList.clear();
	 						for(int i=0;i<mAllAreaList.size();i++)
	 			    			 mAdapterAreaList.add(mAllAreaList.get(i));
	 						mListAdapter.notifyDataSetChanged();*/
	 						}else{
	 							mAdapterAreaList.clear();
	 							mListAdapter.notifyDataSetChanged();
	 							mEmptyTextView.setText("No Pincodes Found");
	 							mEmptyTextView.setVisibility(TextView.VISIBLE);
	 						}
	 						
	 					}else{
							try {
								String error = allPincodes.getErrorMessage();
								Toast.makeText(mActivity, error, Toast.LENGTH_LONG).show();
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
	 						
	 					}
	 				}
	 				
	 				
	 			}

	 			
	 	    }


		
		public void updateList() {
			//Log.i("ToRequestFragment","callback");
			if(mAllAreaList != null && mListAdapter!=null && mListView!=null){
				mAdapterAreaList.clear();
					for(int i=0;i<mAllAreaList.size();i++)
		    			 mAdapterAreaList.add(mAllAreaList.get(i));
					mListAdapter.notifyDataSetChanged();
				
			}
			
			boolean isNetworkConnected = NetworkChecker.isConnected(mActivity);
			if(!isNetworkConnected){
			}else{
			//mProgressDialog.show();
				new GetAllPincodes().execute();
			}
			
		}
		@Override
		public boolean onQueryTextChange(String text) {
			mQueryText = text;
			if(allPincodes!=null){
				mAdapterAreaList.clear();
				for(int i=0;i<mAllAreaList.size();i++)
	    			 mAdapterAreaList.add(mAllAreaList.get(i));
				mListAdapter.notifyDataSetChanged();
				
			if(text.length()>0){
				ArrayList<AreaDetails> newPincodeList = new ArrayList<AreaDetails>();
				newPincodeList = allPincodes.searchPins(text);
				mAdapterAreaList.clear();
				for(int i=0;i<newPincodeList.size();i++)
	    			 mAdapterAreaList.add(newPincodeList.get(i));
				mListAdapter.notifyDataSetChanged();
			}
			}
			
			
			return false;
		}
		@Override
		public boolean onQueryTextSubmit(String arg0) {
			// TODO Auto-generated method stub
			return false;
		}
    	
    	
    }
    
   
    
   
}
