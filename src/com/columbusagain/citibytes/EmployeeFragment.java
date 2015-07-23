package com.columbusagain.citibytes;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.ActionBar.TabListener;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.columbusagain.citibytes.PincodesFragment.PincodeDetails;
import com.columbusagain.citibytes.helper.Constants;
import com.columbusagain.citibytes.helper.HttpPost;
import com.columbusagain.citibytes.helper.UserProfile;

public class EmployeeFragment extends Fragment implements OnPageChangeListener, TabListener, OnQueryTextListener{
	
	private Activity mActivity;
	
	private ActionBar mActionBar;
	
	private ViewPager mViewPager;
	
	private String mQueryText = "";
	
	private final String TAG = "EmployeeFragment";
	
	private String list_type;
	
	private final int TODAY_FRAGMENT = 0;
	
	private final int WEEK_FRAGMENT = 1;
	
	private final int MONTH_FRAGMENT = 2;
	
	private TextView mTodayBusinessCollectedTextView,mWeekBusinessCollectedTextView,mMonthBusinessCollectedTextView;
	private TextView mTodayDateTextView,mWeekDateTextView,mMonthDateTextView;
	
	private ArrayList<EmployeeDetails> mTodayContent = new ArrayList<EmployeeDetails>();
	private ArrayList<EmployeeDetails> mWeekContent = new ArrayList<EmployeeDetails>();
	private ArrayList<EmployeeDetails> mMonthContent = new ArrayList<EmployeeDetails>();
	
	private ArrayList<EmployeeDetails> mTodayAdapterContent = new ArrayList<EmployeeDetails>();
	private ArrayList<EmployeeDetails> mWeekAdapterContent = new ArrayList<EmployeeDetails>();
	private ArrayList<EmployeeDetails> mMonthAdapterContent = new ArrayList<EmployeeDetails>();
	
	private TextView mTodayEmptyTextView,mWeekEmptyTextView,mMonthEmptyTextView;
	
	private EmployeeListAdapter mTodayListAdapter,mWeekListAdapter,mMonthListAdapter;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mActivity = activity;
		mActionBar = ((AdminActivity)mActivity).getSupportActionBar();
	}
	
	@Override
	public void onDetach() {
		super.onDetach();
		mActionBar.removeAllTabs();
		mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.area_search, menu);
		MenuItem searchItem = menu.findItem(R.id.action_search);
		final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
	    SearchView.SearchAutoComplete searchAutoComplete = (SearchView.SearchAutoComplete) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
	    
	     LinearLayout searchPlate = (LinearLayout) searchView.findViewById(android.support.v7.appcompat.R.id.search_plate);
	     ImageView close_button = (ImageView) searchView.findViewById(android.support.v7.appcompat.R.id.search_close_btn);
	     close_button.setImageResource(R.drawable.cross);
	    searchPlate.setBackgroundResource(R.drawable.nav_search);
	    searchAutoComplete.setCompoundDrawablesWithIntrinsicBounds(R.drawable.action_search, 0, 0, 0);
	    searchAutoComplete.setHint("Enter Email ID");
	    searchAutoComplete.setHintTextColor(getResources().getColor(R.color.searchbar_hint));
	    searchAutoComplete.setTextColor(Color.WHITE);
	    searchView.setInputType(InputType.TYPE_CLASS_TEXT);
	    searchView.setOnQueryTextListener(this);
	   
	    MenuItemCompat.setOnActionExpandListener(searchItem,
	            new MenuItemCompat.OnActionExpandListener() {

	    	@Override
			public boolean onMenuItemActionCollapse(MenuItem arg0) {
				 // Do something when collapsed
		    	   Log.i("HomeFragment", "onMenuItemActionCollapse");
		    	   mQueryText = "";
		    	   searchView.setQuery(mQueryText, true);
		            return true;       // Return true to collapse action view
			}
			@Override
			public boolean onMenuItemActionExpand(MenuItem arg0) {
				Log.i("HomeFragment", "onMenuItemActionExpand");
	            // Do something when expanded
	            return true;      // Return true to expand action view
			}

	});
		super.onCreateOptionsMenu(menu, inflater);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		 setHasOptionsMenu(true);
		 View rootView = inflater.inflate(R.layout.activity_area_search, container, false);
		 mViewPager = (ViewPager) rootView.findViewById(R.id.pager);
		 mViewPager.setAdapter(new PincodeFragmentAdapter(getChildFragmentManager()));
		 mViewPager.setOnPageChangeListener(this);
		 mViewPager.setOffscreenPageLimit(3);
		 actionbarSetup();
		 list_type = getArguments().getString("list_type");
		 if(list_type.equalsIgnoreCase("today")){
			 mViewPager.setCurrentItem(TODAY_FRAGMENT);
		 }else if(list_type.equalsIgnoreCase("weekly")){
			 mViewPager.setCurrentItem(WEEK_FRAGMENT);
		 }else if(list_type.equalsIgnoreCase("monthly")){
			 mViewPager.setCurrentItem(MONTH_FRAGMENT);
		 }else{
			 mViewPager.setCurrentItem(TODAY_FRAGMENT);
		 }
		
		 new EmployeeDetailsDownloader("today").execute();
		 new EmployeeDetailsDownloader("weekly").execute();
		 new EmployeeDetailsDownloader("monthly").execute();
		return rootView;
	}
	
	private void actionbarSetup(){
		mActionBar.setTitle("Employee");
		mActionBar.setDisplayHomeAsUpEnabled(true);
		mActionBar.setHomeButtonEnabled(true);
		mActionBar.setLogo(R.drawable.logo_navbar);
		mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		mActionBar.addTab(mActionBar.newTab()
				.setText("TODAY").setTabListener(this));
		mActionBar.addTab(mActionBar.newTab().setText("WEEK")
				.setTabListener(this));
		mActionBar.addTab(mActionBar.newTab().setText("MONTH")
				.setTabListener(this));
	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction arg1) {
		
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction arg1) {
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction arg1) {
		
	}

	@Override
	public void onPageScrollStateChanged(int position) {
		
	}

	@Override
	public void onPageScrolled(int position, float arg1, int arg2) {
		
	}

	@Override
	public void onPageSelected(int position) {
		mActionBar.setSelectedNavigationItem(position);
	}
	
	public class TodayFragment extends Fragment{
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.pincode_list_fragment, container, false);
			LinearLayout empty_view = (LinearLayout)rootView.findViewById(R.id.empty_view);
			mTodayEmptyTextView = (TextView)rootView.findViewById(R.id.empty_text);
			mTodayListAdapter = new EmployeeListAdapter(mTodayAdapterContent,"today");
			mTodayDateTextView = (TextView)rootView.findViewById(R.id.date);
			mTodayBusinessCollectedTextView = (TextView)rootView.findViewById(R.id.business_collected);
			ListView list_view = (ListView) rootView.findViewById(R.id.list_view);
			list_view.setAdapter(mTodayListAdapter);
			list_view.setEmptyView(empty_view);
			 return rootView;
		}
	}
	
	
	
	public class WeekFragment extends Fragment{
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.pincode_list_fragment, container, false);
			LinearLayout empty_view = (LinearLayout)rootView.findViewById(R.id.empty_view);
			mWeekEmptyTextView = (TextView)rootView.findViewById(R.id.empty_text);
			mWeekListAdapter = new EmployeeListAdapter(mWeekAdapterContent,"weekly");
			mWeekDateTextView = (TextView)rootView.findViewById(R.id.date);
			mWeekBusinessCollectedTextView = (TextView)rootView.findViewById(R.id.business_collected);
			ListView list_view = (ListView) rootView.findViewById(R.id.list_view);
			list_view.setAdapter(mWeekListAdapter);
			list_view.setEmptyView(empty_view);
			 
			 return rootView;
		}
	}
	
	public class MonthFragment extends Fragment{
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.pincode_list_fragment, container, false);
			LinearLayout empty_view = (LinearLayout)rootView.findViewById(R.id.empty_view);
			mMonthEmptyTextView = (TextView)rootView.findViewById(R.id.empty_text);
			mMonthListAdapter = new EmployeeListAdapter(mMonthAdapterContent,"monthly");
			mMonthDateTextView = (TextView)rootView.findViewById(R.id.date);
			mMonthBusinessCollectedTextView = (TextView)rootView.findViewById(R.id.business_collected);
			ListView list_view = (ListView) rootView.findViewById(R.id.list_view);
			list_view.setAdapter(mMonthListAdapter);
			list_view.setEmptyView(empty_view);
			 
			 return rootView;
		}
	}
	
	public class PincodeFragmentAdapter extends FragmentPagerAdapter{

		public PincodeFragmentAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			Fragment fragment;
			switch(position){
			case TODAY_FRAGMENT:
				fragment = new TodayFragment();
				return fragment;
			case WEEK_FRAGMENT:
				fragment = new WeekFragment();
				
				return fragment;
			case MONTH_FRAGMENT:
				fragment = new MonthFragment();
				
				return fragment;
			
			default:
				return null;
				
			}
			
		}

		@Override
		public int getCount() {
			return 3;
		}
		
	}

	@Override
	public boolean onQueryTextChange(String text) {
		mQueryText = text;
		filterTodayList(text);
		filterWeekList(text);
		filterMonthList(text);		
		return false;
	}

	@Override
	public boolean onQueryTextSubmit(String text) {
		// TODO Auto-generated method stub
		return false;
	}
	
	private void filterTodayList(String queryText){
		Log.i(TAG, "TODAY LIST UPDATE CALLED");
		mTodayAdapterContent.clear();
		for(int i=0;i<mTodayContent.size();i++)
			if(queryText.length() == 0 || mTodayContent.get(i).name.toLowerCase().startsWith(queryText.toLowerCase()) )
				mTodayAdapterContent.add(mTodayContent.get(i));		
		if(mTodayListAdapter != null)
			mTodayListAdapter.notifyDataSetChanged();
	}
	
	private void filterWeekList(String queryText){
		Log.i(TAG, "WEEK LIST UPDATE CALLED");
		mWeekAdapterContent.clear();
		for(int i=0;i<mWeekContent.size();i++)
			if(queryText.length() == 0 || mWeekContent.get(i).name.toLowerCase().startsWith(queryText.toLowerCase()) )
			mWeekAdapterContent.add(mWeekContent.get(i));
		
		if(mWeekListAdapter != null)
		mWeekListAdapter.notifyDataSetChanged();
		
	}
	
	private void filterMonthList(String queryText){
		Log.i(TAG, "MONTH LIST UPDATE CALLED");
		mMonthAdapterContent.clear();
		for(int i=0;i<mMonthContent.size();i++)
			if(queryText.length() == 0 || mMonthContent.get(i).name.toLowerCase().startsWith(queryText.toLowerCase()) )
			mMonthAdapterContent.add(mMonthContent.get(i));
		
		if(mMonthListAdapter != null)
		mMonthListAdapter.notifyDataSetChanged();
		
	}
	
public class EmployeeListAdapter extends BaseAdapter implements OnClickListener{
	
		TextView mBusinessCollectedText,mTimeSpentText;
		
		private String mListType; 
	
		boolean isTimeSpentAscending = false;
		boolean isTimeSpentDescending = false;
		boolean isBusinessCollectedAscending = false;
		boolean isBusinessCollectedDescending = false;
		
		ArrayList<EmployeeDetails> mEmployeeDetails = new ArrayList<EmployeeDetails>();
		
		EmployeeListAdapter(ArrayList<EmployeeDetails> mEmployeeDetails,String list_type){
			this.mEmployeeDetails = mEmployeeDetails;
			this.mListType = list_type;
		}
		

		@Override
		public int getCount() {
			return mEmployeeDetails.size();
		}

		@Override
		public Object getItem(int position) {
			return mEmployeeDetails.get(position);
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			int business_count = mEmployeeDetails.get(position).business_collected;
			String time_spent = mEmployeeDetails.get(position).formated_duration;
			final String name = mEmployeeDetails.get(position).name;
			ViewHolder viewHolder = null;
			if(convertView == null){
				LayoutInflater inflater = (LayoutInflater)mActivity.getSystemService
					      (Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(R.layout.employee_details_row,
						parent, false);
				viewHolder = new ViewHolder();	
				
				viewHolder.content = (LinearLayout) convertView.findViewById(R.id.content);
				viewHolder.header = (LinearLayout) convertView.findViewById(R.id.header);
				viewHolder.name_text = (TextView) convertView.findViewById(R.id.name);
				viewHolder.time_spent_text = (TextView) convertView.findViewById(R.id.time_spent);
				viewHolder.business_collected_text = (TextView)convertView.findViewById(R.id.business_collected);
				viewHolder.title_time_spent = (TextView)convertView.findViewById(R.id.title_time_spent);
				viewHolder.title_business_collected = (TextView)convertView.findViewById(R.id.title_business_collected);
				viewHolder.title_time_spent.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
				viewHolder.title_business_collected.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
				viewHolder.title_business_collected.setOnClickListener(this);
				viewHolder.title_time_spent.setOnClickListener(this);
				convertView.setTag(viewHolder);
			}else{
				viewHolder = (ViewHolder) convertView.getTag();
			}
			
			if(position == 0){
				viewHolder.header.setVisibility(LinearLayout.VISIBLE);
				mBusinessCollectedText = viewHolder.title_business_collected;
				mTimeSpentText = viewHolder.title_time_spent;
			}else{
				viewHolder.header.setVisibility(LinearLayout.GONE);
			}
			
			viewHolder.name_text.setText(name);
			viewHolder.time_spent_text.setText(time_spent);
			viewHolder.business_collected_text.setText(String.valueOf(business_count));
			
			viewHolder.content.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
				((AdminActivity)mActivity).startEmployeeDetailsFragment(mListType,name);
					
				}
			});
		
			return convertView;
		}
		
		public class ViewHolder{
			TextView title_time_spent;
			TextView title_business_collected;
			LinearLayout header;
			LinearLayout content;
			TextView name_text;
			TextView time_spent_text;
			TextView business_collected_text;
		}

		@Override
		public void onClick(View v) {
			switch(v.getId()){
			case R.id.title_time_spent:
				mBusinessCollectedText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.empty_drawable, 0);
				isBusinessCollectedAscending = false;
				isBusinessCollectedDescending = false;
				if(isTimeSpentAscending){
					mTimeSpentText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.arrow_up_float, 0);
					isTimeSpentAscending = false;
					isTimeSpentDescending = true;
					sortList(true,true,mListType);
				}else if(isTimeSpentDescending){
					mTimeSpentText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.arrow_down_float, 0);
					isTimeSpentAscending = true;
					isTimeSpentDescending = false;
					sortList(true,false,mListType);
				}else{
					mTimeSpentText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.arrow_down_float, 0);
					isTimeSpentAscending = true;
					isTimeSpentDescending = false;
					sortList(true,false,mListType);
				}
				
				break;
			case R.id.title_business_collected:
				mTimeSpentText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.empty_drawable, 0);
				isTimeSpentAscending = false;
				isTimeSpentDescending = false;
				if(isBusinessCollectedAscending){
					mBusinessCollectedText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.arrow_up_float, 0);
					isBusinessCollectedAscending = false;
					isBusinessCollectedDescending = true;
					sortList(false,true,mListType);
				}else if(isBusinessCollectedDescending){
					mBusinessCollectedText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.arrow_down_float, 0);
					isBusinessCollectedAscending = true;
					isBusinessCollectedDescending = false;
					sortList(false,false,mListType);
				}else{
					mBusinessCollectedText.setCompoundDrawablesWithIntrinsicBounds(0, 0,R.drawable.arrow_down_float, 0);
					isBusinessCollectedAscending = true;
					isBusinessCollectedDescending = false;
					sortList(false,false,mListType);
				}
				break;
			}
			
			
		}
		 
	}

private class EmployeeDetailsDownloader extends AsyncTask<Void,Void,String>{
	String type;
	EmployeeDetailsDownloader(String type){
		this.type = type;
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		if(type.equalsIgnoreCase("today")){
			mTodayContent.clear();
		}else if(type.equalsIgnoreCase("weekly")){
			mWeekContent.clear();
		}else if(type.equalsIgnoreCase("monthly")){
			mMonthContent.clear();
		}
		
		
		
	}

	@Override
	protected String doInBackground(Void... params) {
		
		HttpPost httpPost = new HttpPost(Constants.BASE_URL+"admin/getEmployeeAnalytics.php");
		httpPost.setParam("city", UserProfile.CITY);
		httpPost.setParam("period", type);		
		return httpPost.executePost();
	}
	
	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
				
		if(result!=null){
			try {
				JSONObject response_json = new JSONObject(result);
				String status = response_json.getString("status");
				if("success".equalsIgnoreCase(status)){
					Log.i(TAG, result);
					int total_business_collected = response_json.getInt("total_business_collected");
					int count = response_json.getInt("count");
					String date  = response_json.getString("date");
					if(type.equalsIgnoreCase("today")){
						mTodayDateTextView.setText(date);
						mTodayBusinessCollectedTextView.setText(String.valueOf(total_business_collected));
					}else if(type.equalsIgnoreCase("weekly")){
						mWeekDateTextView.setText(date);
						mWeekBusinessCollectedTextView.setText(String.valueOf(total_business_collected));
					}else if(type.equalsIgnoreCase("monthly")){
						mMonthDateTextView.setText(date);
						mMonthBusinessCollectedTextView.setText(String.valueOf(total_business_collected));
					}
					if(count>0){
						JSONArray analytics = response_json.getJSONArray("analytics");
						addPeopleInfo(analytics,type);
					}else{
						if(type.equalsIgnoreCase("today")){
							mTodayEmptyTextView.setText("No Business Collected");
							mTodayEmptyTextView.setVisibility(TextView.VISIBLE);							
						}else if(type.equalsIgnoreCase("weekly")){
							mWeekEmptyTextView.setText("No Business Collected");
							mWeekEmptyTextView.setVisibility(TextView.VISIBLE);
						}else if(type.equalsIgnoreCase("monthly")){
							mMonthEmptyTextView.setText("No Business Collected");
							mMonthEmptyTextView.setVisibility(TextView.VISIBLE);
						}
					}
				}else{
					String error = response_json.getString("error");
					Toast.makeText(mActivity, error, Toast.LENGTH_LONG).show();
				}
				
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}else{
			Toast.makeText(mActivity, "No Network", Toast.LENGTH_LONG).show();
		}
		
		onQueryTextChange(mQueryText);
	}
	
	private void addPeopleInfo(JSONArray analytics,String type) throws JSONException{
		for(int i=0;i<analytics.length();i++){
			JSONObject employee_detail = analytics.getJSONObject(i);
			EmployeeDetails employeeDetails = new EmployeeDetails();
			employeeDetails.name = employee_detail.getString("email_id");
			employeeDetails.time_spent = employee_detail.getInt("duration");
			employeeDetails.business_collected = employee_detail.getInt("business_collected");
			employeeDetails.formated_duration = employee_detail.getString("formatted_duration");
			if(type.equalsIgnoreCase("today")){
				mTodayContent.add(employeeDetails);
			}else if(type.equalsIgnoreCase("weekly")){
				mWeekContent.add(employeeDetails);
			}else if(type.equalsIgnoreCase("monthly")){
				mMonthContent.add(employeeDetails);
			}
			
		}
		
	}
	
}

	public class EmployeeDetails{
		public String name;
		public int time_spent;
		public int business_collected;
		public String formated_duration;
	}
	
	private void sortList(boolean isTimeSpentSort,boolean isDescendingOrder,String list_type){
		if(list_type.equalsIgnoreCase("today"))
		sortTodayList(isTimeSpentSort,isDescendingOrder);
		else if(list_type.equalsIgnoreCase("weekly"))
		sortWeekList(isTimeSpentSort,isDescendingOrder);
		else if(list_type.equalsIgnoreCase("monthly"))
		sortMonthList(isTimeSpentSort,isDescendingOrder);
		onQueryTextChange(mQueryText);
	}
	
	private void sortTodayList(boolean isTimeSpentSort,boolean isDescending){
		for(int i=0;i<mTodayContent.size();i++){
		for(int j=0;j<mTodayContent.size()-1;j++){
			if(isTimeSpentSort){
			if((mTodayContent.get(j).time_spent<mTodayContent.get(j+1).time_spent && isDescending) || (mTodayContent.get(j).time_spent>mTodayContent.get(j+1).time_spent && !isDescending)){
				swapTodayList(j);
			}	
			}else{
				if((mTodayContent.get(j).business_collected<mTodayContent.get(j+1).business_collected && isDescending) || (mTodayContent.get(j).business_collected>mTodayContent.get(j+1).business_collected && !isDescending)){
					swapTodayList(j);
				}
			}
		}
		}
	}
	
	private void swapTodayList(int position){
		EmployeeDetails temp = mTodayContent.get(position+1);
		mTodayContent.set(position+1, mTodayContent.get(position));
		mTodayContent.set(position, temp);
	}
	
	private void sortWeekList(boolean isTimeSpentSort,boolean isDescending){
		for(int i=0;i<mWeekContent.size();i++){
		for(int j=0;j<mWeekContent.size()-1;j++){
			if(isTimeSpentSort){
			if((mWeekContent.get(j).time_spent<mWeekContent.get(j+1).time_spent && isDescending) || (mWeekContent.get(j).time_spent>mWeekContent.get(j+1).time_spent && !isDescending)){
				swapWeekList(j);
			}	
			}else{
				if((mWeekContent.get(j).business_collected<mWeekContent.get(j+1).business_collected && isDescending) || (mWeekContent.get(j).business_collected>mWeekContent.get(j+1).business_collected && !isDescending)){
					swapWeekList(j);
				}
			}
		}
		}
	}
	
	private void swapWeekList(int position){
		EmployeeDetails temp = mWeekContent.get(position+1);
		mWeekContent.set(position+1, mWeekContent.get(position));
		mWeekContent.set(position, temp);
	}
	
	
	
	private void sortMonthList(boolean isTimeSpentSort,boolean isDescending){
		for(int i=0;i<mMonthContent.size();i++){
		for(int j=0;j<mMonthContent.size()-1;j++){
			if(isTimeSpentSort){
			if((mMonthContent.get(j).time_spent<mMonthContent.get(j+1).time_spent && isDescending) || (mMonthContent.get(j).time_spent>mMonthContent.get(j+1).time_spent && !isDescending)){
				swapMonthList(j);
			}	
			}else{
				if((mMonthContent.get(j).business_collected<mMonthContent.get(j+1).business_collected && isDescending) || (mMonthContent.get(j).business_collected>mMonthContent.get(j+1).business_collected && !isDescending)){
					swapMonthList(j);
				}
			}
		}
		}
	}
	
	private void swapMonthList(int position){
		EmployeeDetails temp = mMonthContent.get(position+1);
		mMonthContent.set(position+1, mMonthContent.get(position));
		mMonthContent.set(position, temp);
	}
	
	

}
