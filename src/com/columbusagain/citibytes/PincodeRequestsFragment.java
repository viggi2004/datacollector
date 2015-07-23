package com.columbusagain.citibytes;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.columbusagain.citibytes.LeaderBoardFragment.ListContent;
import com.columbusagain.citibytes.LeaderBoardFragment.MonthFragment;
import com.columbusagain.citibytes.LeaderBoardFragment.WeekFragment;
import com.columbusagain.citibytes.LeaderBoardFragment.LeaderBoardListAdapter.ViewHolder;
import com.columbusagain.citibytes.MyAreaFragment.PincodeFragmentPagerAdapter;
import com.columbusagain.citibytes.helper.AreaDetails;
import com.columbusagain.citibytes.helper.Constants;
import com.columbusagain.citibytes.helper.HttpGet;
import com.columbusagain.citibytes.helper.HttpPost;
import com.columbusagain.citibytes.helper.JsonGetException;
import com.columbusagain.citibytes.helper.UserProfile;
import com.columbusagain.citibytes.util.NetworkChecker;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class PincodeRequestsFragment extends Fragment implements
		ActionBar.TabListener, OnQueryTextListener {
	String QueryText = "";
	
	ArrayList<UserDetails> mPendingRequestsList= new ArrayList<UserDetails>();
	
	ArrayList<UserDetails> mApprovedRequestsList = new ArrayList<UserDetails>();
	
	ArrayList<UserDetails> mPendingRequestsAdapterList= new ArrayList<UserDetails>();
	
	ArrayList<UserDetails> mApprovedRequestsAdapterList = new ArrayList<UserDetails>();
	
	ListView mPendingPincodesListView,mApprovedPincodesListView;
	
	TextView mPendingEmptyTextView,mAcceptedEmptyTextView;
	
	PincodeRequestsListAdapter mPendingPincodesAdapter,mAcceptedPincodesAdapter;
	
	

	ViewPager mViewPager;
	
	private Context mContext;

	private ActionBar mActionbar;

	private static Activity mActivity;

	private static Dialog mProgressDialog;

	public PincodeRequestsFragment() {

	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mContext = activity;
		mActivity = activity;		
		
	}
	
	@Override
	public void onDetach() {
		super.onDetach();
		mActionbar.removeAllTabs();
		mActionbar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mActionbar = ((AdminActivity) mActivity).getSupportActionBar();
		mActionbar.setTitle("Pincode Requests");
		setHasOptionsMenu(true);
		View rootView = inflater.inflate(R.layout.activity_area_search, container, false);
		mProgressDialog = new Dialog(mActivity,R.style.ProgressDialog);
	 	mProgressDialog.setContentView(R.layout.progress_dialog);
	 	mActionbar.setDisplayHomeAsUpEnabled(true);
        mActionbar.setHomeButtonEnabled(true);
        mActionbar.setLogo(R.drawable.logo_navbar);
        mActionbar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        mViewPager = (ViewPager) rootView.findViewById(R.id.pager);
        mViewPager.setAdapter(new PincodeRequestsPagerAdapter(getChildFragmentManager()));
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
            	mActionbar.setSelectedNavigationItem(position);
            	onQueryTextChange(QueryText);
            	boolean isNetworkConnected = NetworkChecker.isConnected(mActivity);
            	switch(position){
            	case 0:   
            		
            		if(!isNetworkConnected){
            		}else{
            		//mProgressDialog.show();
            			new GetAcceptedPincodes().execute();
            		}            		
            		break;
            	case 1:
            		if(!isNetworkConnected){
            		}else{
            		//mProgressDialog.show();
            			new GetPendingPincodes().execute();
            			
            		}
            		break;
            	}
            	
            }
        });
        mActionbar.addTab(mActionbar.newTab()
				.setText("ACCEPTED").setTabListener(this));
        mActionbar.addTab(mActionbar.newTab().setText("PENDING REQUESTS")
				.setTabListener(this));
		return rootView;

	}
	
	public class PendingRequests extends Fragment{
		
		public PendingRequests(){
			
		}
		
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			//setHasOptionsMenu(true);
			View view = inflater.inflate(R.layout.leader_board_list, container, false);
			mPendingPincodesListView = (ListView) view.findViewById(R.id.rank_list);
			mPendingEmptyTextView = (TextView) view.findViewById(R.id.empty_text);			
			LinearLayout empty_view = (LinearLayout) view.findViewById(R.id.empty_view);
			mPendingPincodesListView.setEmptyView(empty_view);
			boolean isNetworkConnected = NetworkChecker.isConnected(mActivity);
			if(!isNetworkConnected){
			}else{
			mProgressDialog.show();
				new GetPendingPincodes().execute();
			}
			return view;
		}
		
		/*@Override
		public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
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
		}*/
	}
	
	public class AcceptedRequests extends Fragment{
		
		public AcceptedRequests(){
			
		}
		
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			//setHasOptionsMenu(true);
			View view = inflater.inflate(R.layout.leader_board_list, container, false);
			mApprovedPincodesListView = (ListView) view.findViewById(R.id.rank_list);
			LinearLayout empty_view = (LinearLayout) view.findViewById(R.id.empty_view);
			mAcceptedEmptyTextView = (TextView) view.findViewById(R.id.empty_text);
			mApprovedPincodesListView.setEmptyView(empty_view);
			boolean isNetworkConnected = NetworkChecker.isConnected(mActivity);
			if(!isNetworkConnected){
			}else{
			mProgressDialog.show();
				new GetAcceptedPincodes().execute();
			}
			return view;
		}
		
		
	}
	
	public class PincodeRequestsPagerAdapter extends FragmentPagerAdapter{

		public PincodeRequestsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			Fragment fragment;
			switch(position){
			case 0:
				fragment = new AcceptedRequests();
				return fragment;
			case 1:
				fragment = new PendingRequests();
				
				return fragment;
			default:
				return null;
				
			}
			
		}

		@Override
		public int getCount() {
			return 2;
		}
		
	}
	
public class PincodeRequestsListAdapter extends BaseAdapter{
		
		ArrayList<UserDetails> mListContents  = new ArrayList<UserDetails>();
		
		
		public PincodeRequestsListAdapter(ArrayList<UserDetails> listcontent){
			this.mListContents = listcontent;
			
		}

		@Override
		public int getCount() {
			return mListContents.size();
		}

		@Override
		public Object getItem(int position) {
			return mListContents.get(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder = null;
			if(convertView == null){
				LayoutInflater inflater = (LayoutInflater)mContext.getSystemService
					      (Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(R.layout.pincode_requests_row_layout,
						parent, false);
				viewHolder = new ViewHolder();
				viewHolder.mHeaderLayout = (LinearLayout)  convertView
						.findViewById(R.id.header_layout);
				viewHolder.mNameTextView = (TextView)  convertView
				.findViewById(R.id.name);
				viewHolder.mMobileNumberTextView = (TextView)  convertView
				.findViewById(R.id.mobile);
				viewHolder.mPincodeTextView = (TextView)  convertView
				.findViewById(R.id.pincode);
				viewHolder.mAcceptButton = (ImageButton)  convertView
				.findViewById(R.id.accept_button);
				viewHolder.mRejectButton = (ImageButton)  convertView
				.findViewById(R.id.reject_button);
				viewHolder.mProgressFrame = (FrameLayout) convertView.findViewById(R.id.progress_frame);
				convertView.setTag(viewHolder);
			}else{
				viewHolder = (ViewHolder) convertView.getTag();
			}
			
			if(mListContents.get(position).isAccepted){
				viewHolder.mAcceptButton.setVisibility(ImageButton.INVISIBLE);
			}else{
				viewHolder.mAcceptButton.setVisibility(ImageButton.VISIBLE);
			}
			
			if(mListContents.get(position).isHeader){
				viewHolder.mHeaderLayout.setVisibility(LinearLayout.VISIBLE);
			}else{
				viewHolder.mHeaderLayout.setVisibility(LinearLayout.GONE);
			}
			
			final UserDetails userDetail = mListContents.get(position);
			final String email = userDetail.email;
			final String pincode = userDetail.pincode;
			viewHolder.mMobileNumberTextView.setText(userDetail.personal_number);
			viewHolder.mPincodeTextView.setText(pincode);
			viewHolder.mNameTextView.setText(email);
			
			if(userDetail.isProcessing){
				if(!userDetail.isAccepted)
				viewHolder.mAcceptButton.setVisibility(ImageButton.GONE);
				viewHolder.mRejectButton.setVisibility(ImageButton.GONE);
				viewHolder.mProgressFrame.setVisibility(FrameLayout.VISIBLE);
			}else{
				if(!userDetail.isAccepted)
				viewHolder.mAcceptButton.setVisibility(ImageButton.VISIBLE);
				viewHolder.mRejectButton.setVisibility(ImageButton.VISIBLE);
				viewHolder.mProgressFrame.setVisibility(FrameLayout.GONE);
			}
			
			viewHolder.mAcceptButton.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					String json = buildJson(email,UserProfile.CITY,pincode);
					boolean isNetworkConnected = NetworkChecker.isConnected(mActivity);
					if(!isNetworkConnected){
					}else{
					//mProgressDialog.show();
					mListContents.get(position).isProcessing = true;
					notifyDataSetChanged();
						new ProcessPincodeRequests(json,mListContents.get(position),position,true).execute();
					}
					
				}
			});
			
			viewHolder.mRejectButton.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					String json = buildJson(email,UserProfile.CITY,pincode);
					boolean isNetworkConnected = NetworkChecker.isConnected(mActivity);
					if(!isNetworkConnected){
					}else{
					//mProgressDialog.show();
					mListContents.get(position).isProcessing = true;
					notifyDataSetChanged();
						new ProcessPincodeRequests(json,mListContents.get(position),position,false).execute();
					}
					
				}
			});
			
			return convertView;
		}
		
		public class ViewHolder{
			LinearLayout mHeaderLayout;
			TextView mNameTextView;
			TextView mMobileNumberTextView;
			TextView mPincodeTextView;
			ImageButton mAcceptButton;
			ImageButton mRejectButton;
			TextView mEmailText;
			FrameLayout mProgressFrame;
			TextView mBusinessCountText;
		}
		
	}

	private String buildJson(String email,String city,String pin){
		JSONObject json = new JSONObject();
		try {
			json.put("email_id", email);
			json.put("pincode", pin);
			json.put("city", city);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return json.toString();
	}



	@Override
	public void onTabReselected(Tab tab, FragmentTransaction fragmentTransaction) {

	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction fragmentTransaction) {
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction fragmentTransaction) {

	}
	
	private class ProcessPincodeRequests extends AsyncTask<Void, Void, String>{
		String json;
		UserDetails userDetails;
		int position;
		boolean isApprove;
		public ProcessPincodeRequests(String json,UserDetails userDetails,int position,boolean isApproved) {
			this.json = json;
			this.userDetails = userDetails;
			this.position = position;
			this.isApprove = isApproved;
		}

		@Override
		protected String doInBackground(Void... params) {
			if(UserProfile.CITY == null )
				return null;
			HttpPost httpPost;
			if(userDetails.isAccepted){
				httpPost = new HttpPost(Constants.BASE_URL+"admin/rejectAcceptedPincodeRequest.php");
				Log.i("PincodeRequestsFragment", Constants.BASE_URL+"admin/rejectAcceptedPincodeRequest.php");
				httpPost.setParam("json", json);
			}else{
				httpPost = new HttpPost(Constants.BASE_URL+"admin/processPendingPincodeRequest.php");
				Log.i("PincodeRequestsFragment", Constants.BASE_URL+"admin/processPendingPincodeRequest.php");
				httpPost.setParam("json", json);
				if(isApprove){
					httpPost.setParam("is_approved", "1");
				}else{
					httpPost.setParam("is_approved", "0");
				}
			}
			
			return httpPost.executePost();
		}
		
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if(mProgressDialog.isShowing()){
				mProgressDialog.dismiss();
			}
			
			if(result !=null){
				try {
					JSONObject jsonObj = new JSONObject(result);
					String status = jsonObj.getString("status");
					
					if(status.equalsIgnoreCase("success")){
						if(userDetails.isAccepted){
							Log.i("PincodeRequestsFragment", "accepted");
							
							//new GetAcceptedPincodes().execute();
							if(userDetails.isHeader){
								Log.i("PincodeRequestsFragment", "header");
								if(mApprovedRequestsAdapterList.size()>(position+1)){
									Log.i("PincodeRequestsFragment", "position available");
									if(mApprovedRequestsAdapterList.get(position).email.equalsIgnoreCase(mApprovedRequestsAdapterList.get(position+1).email)){
										mApprovedRequestsAdapterList.get(position+1).isHeader = true;
									Log.i("PincodeRequestsFragment", "header enabled");
									}
								}
								
							}
							for(int i=0;i<mApprovedRequestsList.size();i++){
								if(mApprovedRequestsList.get(i).email.equals(mApprovedRequestsAdapterList.get(position).email))
									if(mApprovedRequestsList.get(i).pincode.equals(mApprovedRequestsAdapterList.get(position).pincode))
										mApprovedRequestsList.remove(i);
							}
							mApprovedRequestsAdapterList.remove(position);
						}else{
							Log.i("PincodeRequestsFragment", "pending");
						
						if(userDetails.isHeader){
							Log.i("PincodeRequestsFragment", "pending isheader");
							if(mPendingRequestsAdapterList.size()>(position+1)){
								Log.i("PincodeRequestsFragment", "pending size available");
								if(mPendingRequestsAdapterList.get(position).email.equalsIgnoreCase(mPendingRequestsAdapterList.get(position+1).email)){
									mPendingRequestsAdapterList.get(position+1).isHeader = true;
									Log.i("PincodeRequestsFragment", "pending isHeader enabled");
								}
							}
							
						}
						
						for(int i=0;i<mPendingRequestsList.size();i++){
							if(mPendingRequestsList.get(i).email.equals(mPendingRequestsAdapterList.get(position).email))
								if(mPendingRequestsList.get(i).pincode.equals(mPendingRequestsAdapterList.get(position).pincode))
									mPendingRequestsList.remove(i);
						}
						mPendingRequestsAdapterList.remove(position);
						}
						
					}else{
						Toast.makeText(mContext, "Please Try Again", Toast.LENGTH_LONG).show();
					}
					userDetails.isProcessing = false;
					if(userDetails.isAccepted){
						mAcceptedPincodesAdapter.notifyDataSetChanged();
					}else{
					mPendingPincodesAdapter.notifyDataSetChanged();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}else{
				Toast.makeText(mContext, "No network", Toast.LENGTH_LONG).show();
			}
		}
		
	}
	
	
	
	private class GetAcceptedPincodes extends AsyncTask<Void, Void, String>{

		@Override
		protected String doInBackground(Void... params) {
			if(UserProfile.CITY == null )
				return null;
			String response = null;
			HttpGet httpGet = new HttpGet(Constants.BASE_URL+"admin/getApprovedPincodeRequests.php?city="+UserProfile.CITY);
			Log.i("PincodeRequestsFragment", "ApprovedPincodes"+Constants.BASE_URL+"admin/getApprovedPincodeRequests.php?city="+UserProfile.CITY);
			try {
				response = httpGet.executeGet();
			} catch (JsonGetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return response;
		}
		
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if(mProgressDialog.isShowing()){
				mProgressDialog.dismiss();
			}
			if(result != null){
				try {
					JSONObject jsonObj = new JSONObject(result);
					String status = jsonObj.getString("status");
					if(status.equalsIgnoreCase("success")){
						int count = jsonObj.getInt("count");
						if(count>0){
							JSONObject contentObj = jsonObj.getJSONObject("content");
							parsePendingRequests(contentObj,false);
							if(mAcceptedPincodesAdapter == null){
							mAcceptedPincodesAdapter = new PincodeRequestsListAdapter(mApprovedRequestsAdapterList);
							mApprovedPincodesListView.setAdapter(mAcceptedPincodesAdapter);
							}else{
								mAcceptedPincodesAdapter.notifyDataSetChanged();
							}
						}else{
							mAcceptedEmptyTextView.setText("No Accepted Requests");
							mAcceptedEmptyTextView.setVisibility(TextView.VISIBLE);
						}
					}else{
						Toast.makeText(mContext, "Please Try Again", Toast.LENGTH_LONG).show();
					}
					
				} catch (JSONException e) {
					e.printStackTrace();
				}
				Log.i("PincodeRequestsFragment", "GetPendingPincodes :"+result.toString());
			}else{
				
			}
		}
		
	}
	
	private class GetPendingPincodes extends AsyncTask<Void, Void, String>{
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(Void... params) {
			if(UserProfile.CITY == null )
				return null;
			String response = null;
			HttpGet httpGet = new HttpGet(Constants.BASE_URL+"admin/getPendingPincodeRequests.php?city="+UserProfile.CITY);
			Log.i("PincodeRequestsFragment", "PendingPincodes"+Constants.BASE_URL+"admin/getPendingPincodeRequests.php?city="+UserProfile.CITY);
			try {
				response = httpGet.executeGet();
			} catch (JsonGetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return response;
		}
		
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			
			if(mProgressDialog.isShowing()){
				mProgressDialog.dismiss();
			}
			
			if(result != null){
				try {
					JSONObject jsonObj = new JSONObject(result);
					String status = jsonObj.getString("status");
					if(status.equalsIgnoreCase("success")){
						int count = jsonObj.getInt("count");
						if(count>0){
							JSONObject contentObj = jsonObj.getJSONObject("content");
							parsePendingRequests(contentObj,true);
							if(mPendingPincodesAdapter == null){
							mPendingPincodesAdapter = new PincodeRequestsListAdapter(mPendingRequestsAdapterList);
							mPendingPincodesListView.setAdapter(mPendingPincodesAdapter);
							}else{
								mPendingPincodesAdapter.notifyDataSetChanged();
							}
						}else{
							mPendingEmptyTextView.setText("No Pending Requests");
							mPendingEmptyTextView.setVisibility(TextView.VISIBLE);
						}
					}else{
						Toast.makeText(mContext, "Please Try Again", Toast.LENGTH_LONG).show();
					}
					
				} catch (JSONException e) {
					e.printStackTrace();
				}
				Log.i("PincodeRequestsFragment", "GetPendingPincodes :"+result.toString());
			}else{
				
			}
		}
	}
	
	private void parsePendingRequests(JSONObject jsonObj,boolean isPendingPincodeList) throws JSONException{
		if(isPendingPincodeList){
			mPendingRequestsList.clear();
			mPendingRequestsAdapterList.clear();
		}
		else{
			mApprovedRequestsList.clear();
			mApprovedRequestsAdapterList.clear();
		}
		
		
		JSONArray jsonArray = jsonObj.names();
		for(int i=0;i<jsonArray.length();i++){
			String email = jsonArray.getString(i);
			JSONObject jsonObject = jsonObj.getJSONObject(email);
			String personal_number = jsonObject.getString("personal_number");
			JSONArray pincodesArray = jsonObject.getJSONArray("pincodes");
			for(int j=0;j<pincodesArray.length();j++){
				String pincode = pincodesArray.getString(j);
				UserDetails userData = new UserDetails();
				userData.email = email;
				userData.personal_number = personal_number;
				userData.pincode = pincode;
				userData.isProcessing = false;
				/*if(j==0)
					userData.isHeader = true;
				else*/
					userData.isHeader = false;
				if(isPendingPincodeList){
					userData.isAccepted = false;
				mPendingRequestsList.add(userData);
				}
				else{
					userData.isAccepted = true;
					mApprovedRequestsList.add(userData);
				}
			}
			
		}
		
		setDefaultHeaders(isPendingPincodeList);
		
		copyPincodesToAdapter(isPendingPincodeList);
	}
	
	private void copyPincodesToAdapter(boolean isPendingPincodeList){
		
		if(isPendingPincodeList){
			mPendingRequestsAdapterList.clear();
			for(int i=0;i<mPendingRequestsList.size();i++)
				mPendingRequestsAdapterList.add(mPendingRequestsList.get(i));
			
			
		}else{
			mApprovedRequestsAdapterList.clear();
			for(int i=0;i<mApprovedRequestsList.size();i++)
				mApprovedRequestsAdapterList.add(mApprovedRequestsList.get(i));
		}
		onQueryTextChange(QueryText);
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.area_search, menu);
		MenuItem searchItem = menu.findItem(R.id.action_search);
		SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
	    SearchView.SearchAutoComplete searchAutoComplete = (SearchView.SearchAutoComplete) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
	    
	     LinearLayout searchPlate = (LinearLayout) searchView.findViewById(android.support.v7.appcompat.R.id.search_plate);
	     ImageView close_button = (ImageView) searchView.findViewById(android.support.v7.appcompat.R.id.search_close_btn);
	     close_button.setImageResource(R.drawable.cross);
	    searchPlate.setBackgroundResource(R.drawable.nav_search);
	    searchAutoComplete.setCompoundDrawablesWithIntrinsicBounds(R.drawable.action_search, 0, 0, 0);
	    searchAutoComplete.setHint("Email / Pincode");
	    searchAutoComplete.setHintTextColor(getResources().getColor(R.color.searchbar_hint));
	    searchAutoComplete.setTextColor(Color.WHITE);
	    searchView.setOnQueryTextListener(this);
		super.onCreateOptionsMenu(menu, inflater);
	}
	
	private class UserDetails{
		String personal_number;
		String email;
		String pincode;
		boolean isHeader;
		boolean isAccepted;
		boolean isProcessing;
	}

	@Override
	public boolean onQueryTextChange(String text) {
		QueryText = text;
		switch(mViewPager.getCurrentItem()){
		case 0:
			if(mAcceptedPincodesAdapter == null)
				break;
			if(text.length()>0){
				updateApprovedPincodeList(text);				
					mAcceptedPincodesAdapter.notifyDataSetChanged();
				}else{
					mApprovedRequestsAdapterList.clear();
					if(mApprovedRequestsList.size()>0){
						for(int i=0;i<mApprovedRequestsList.size();i++)
							mApprovedRequestsAdapterList.add(mApprovedRequestsList.get(i));
					}
					setDefaultHeaders(false);
					mAcceptedPincodesAdapter.notifyDataSetChanged();
				}
			break;
		case 1:
			if(mPendingPincodesAdapter == null)
				break;
			if(text.length()>0){
				updatePendingPincodeList(text);
				
					mPendingPincodesAdapter.notifyDataSetChanged();
				}else{
					mPendingRequestsAdapterList.clear();
					if(mPendingRequestsList.size()>0){
						for(int i=0;i<mPendingRequestsList.size();i++)
							mPendingRequestsAdapterList.add(mPendingRequestsList.get(i));
					}
					
					setDefaultHeaders(true);
					
					mPendingPincodesAdapter.notifyDataSetChanged();
				}
			break;
		}
		
		return false;
	}

	@Override
	public boolean onQueryTextSubmit(String arg0) {
		// TODO Auto-generated method stub
		return false;
	}
	
	public void updateApprovedPincodeList(String searchKey) {
		mApprovedRequestsAdapterList.clear();
		
		ArrayList<String> newSearchKey = new ArrayList<String>();
		if (Character.isLetter(searchKey.charAt(0))) {
			for (int i = 0; i < mApprovedRequestsList.size(); i++) {
				if (mApprovedRequestsList.get(i).email.toLowerCase().startsWith(searchKey.toLowerCase())) {
					mApprovedRequestsAdapterList.add(mApprovedRequestsList.get(i));
				}
			}
		} else if (Character.isDigit(searchKey.charAt(0))){
			for (int i = 0; i < mApprovedRequestsList.size(); i++) {
				if (mApprovedRequestsList.get(i).pincode.startsWith(
						searchKey)) {
					boolean isExist = false;
					for (int j = 0; j < newSearchKey.size(); j++) {
						if (newSearchKey.get(j).equals(mApprovedRequestsList.get(i).email)) {
							isExist = true;
							break;
						}
					}
					if (!isExist)
						newSearchKey.add(mApprovedRequestsList.get(i).email);
				}
			}
			

			for (int i = 0; i < newSearchKey.size(); i++) {

				for (int j = 0; j < mApprovedRequestsList.size(); j++) {
					if (mApprovedRequestsList.get(j).email.equalsIgnoreCase(newSearchKey.get(i))
							&& mApprovedRequestsList.get(j).pincode.toLowerCase()
									.startsWith(searchKey.toLowerCase())) {
						mApprovedRequestsAdapterList.add(mApprovedRequestsList.get(j));
					}

				}

			}
			
			for(int i=0;i<mApprovedRequestsAdapterList.size();i++)
				mApprovedRequestsAdapterList.get(i).isHeader = false;
			
			for (int i = 0; i < newSearchKey.size(); i++) {

				for (int j = 0; j < mApprovedRequestsList.size(); j++) {

					if (mApprovedRequestsAdapterList.get(j).email.equals(newSearchKey.get(i))
							&& mApprovedRequestsAdapterList.get(j).pincode.toLowerCase()
									.startsWith(searchKey.toLowerCase())) {
						mApprovedRequestsAdapterList.get(j).isHeader = true;
						break;
					}

				}

			}

		}
	}
	
	public void updatePendingPincodeList(String searchKey) {
		mPendingRequestsAdapterList.clear();
		
		ArrayList<String> newSearchKey = new ArrayList<String>();
		if (Character.isLetter(searchKey.charAt(0))) {
			for (int i = 0; i < mPendingRequestsList.size(); i++) {
				if (mPendingRequestsList.get(i).email.startsWith(searchKey)) {
					mPendingRequestsAdapterList.add(mPendingRequestsList.get(i));
				}
			}
		} else {
			for (int i = 0; i < mPendingRequestsList.size(); i++) {
				if (mPendingRequestsList.get(i).pincode.toLowerCase().startsWith(
						searchKey.toLowerCase())) {
					boolean isExist = false;
					for (int j = 0; j < newSearchKey.size(); j++) {
						if (newSearchKey.get(j).equals(mPendingRequestsList.get(i).email)) {
							isExist = true;
							break;
						}
					}
					if (!isExist)
						newSearchKey.add(mPendingRequestsList.get(i).email);
				}
			}
			

			for (int i = 0; i < newSearchKey.size(); i++) {

				for (int j = 0; j < mPendingRequestsList.size(); j++) {
					if (mPendingRequestsList.get(j).email.equals(newSearchKey.get(i))
							&& mPendingRequestsList.get(j).pincode.toLowerCase()
									.startsWith(searchKey.toLowerCase())) {
						mPendingRequestsAdapterList.add(mPendingRequestsList.get(j));
					}

				}

			}
			
			for(int i=0;i<mPendingRequestsAdapterList.size();i++)
				mPendingRequestsAdapterList.get(i).isHeader = false;
			
			for (int i = 0; i < newSearchKey.size(); i++) {

				for (int j = 0; j < mPendingRequestsList.size(); j++) {

					if (mPendingRequestsAdapterList.get(j).email.equals(newSearchKey.get(i))
							&& mPendingRequestsAdapterList.get(j).pincode.toLowerCase()
									.startsWith(searchKey.toLowerCase())) {
						mPendingRequestsAdapterList.get(j).isHeader = true;
						break;
					}

				}

			}

		}
	}
	
	private void setDefaultHeaders(boolean isPendingPincodeRequest){
		if(isPendingPincodeRequest){
		for(int i=0;i<mPendingRequestsList.size();i++){
			if(i==0)
				mPendingRequestsList.get(i).isHeader = true;
			else{				
				mPendingRequestsList.get(i).isHeader = false;
			if(!mPendingRequestsList.get(i-1).email.equalsIgnoreCase(mPendingRequestsList.get(i).email)){
				mPendingRequestsList.get(i).isHeader = true;
			}
			}
		}
		}else{	
		
		for(int i=0;i<mApprovedRequestsList.size();i++){
			if(i==0)
				mApprovedRequestsList.get(i).isHeader = true;
			else{				
			mApprovedRequestsList.get(i).isHeader = false;
			if(!mApprovedRequestsList.get(i-1).email.equalsIgnoreCase(mApprovedRequestsList.get(i).email)){
			mApprovedRequestsList.get(i).isHeader = true;
			}
			}
		}
		}
		
		
	}

}
