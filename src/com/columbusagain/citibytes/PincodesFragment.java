package com.columbusagain.citibytes;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.columbusagain.citibytes.PincodeRequestsFragment.AcceptedRequests;
import com.columbusagain.citibytes.PincodeRequestsFragment.PendingRequests;
import com.columbusagain.citibytes.PincodeRequestsFragment.PincodeRequestsPagerAdapter;
import com.columbusagain.citibytes.PincodeRequestsFragment.PincodeRequestsListAdapter.ViewHolder;
import com.columbusagain.citibytes.helper.Constants;
import com.columbusagain.citibytes.helper.HttpGet;
import com.columbusagain.citibytes.helper.JsonGetException;
import com.columbusagain.citibytes.helper.UserProfile;

import android.app.Activity;
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
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.ActionBar.TabListener;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.support.v7.widget.SearchView.SearchAutoComplete;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class PincodesFragment extends Fragment implements TabListener, OnPageChangeListener, OnQueryTextListener {
	
	private Activity mActivity;
	
	private Context mContext;
	
	private String mQueryText = "";
	
	private ActionBar mActionBar;
	
	private ViewPager mViewPager;
	
	private final String TAG = "PincodesFragment";
	
	private TextView mTodayDateText,mWeekDateText,mMonthDateText,mTillDateDateText;
	
	private TextView mTodayBusinessCountText,mWeekBusinessCountText,mMonthBusinessCountText,mTillDateBusinessCountText;
	
	private ListView mTodayListView,mWeekListView,mMonthListView,mTillDateListView;
	
	private PincodeListAdapter mTodayListAdapter,mWeekListAdapter,mMonthListAdapter,mTillDateListAdapter;
	
	private ArrayList<PincodeDetails> mTodayContent = new ArrayList<PincodeDetails>();
	private ArrayList<PincodeDetails> mWeekContent = new ArrayList<PincodeDetails>();
	private ArrayList<PincodeDetails> mMonthContent = new ArrayList<PincodeDetails>();
	private ArrayList<PincodeDetails> mTillDateContent = new ArrayList<PincodeDetails>();
	
	private ArrayList<PincodeDetails> mTodayAdapterContent = new ArrayList<PincodeDetails>();
	private ArrayList<PincodeDetails> mWeekAdapterContent = new ArrayList<PincodeDetails>();
	private ArrayList<PincodeDetails> mMonthAdapterContent = new ArrayList<PincodeDetails>();
	private ArrayList<PincodeDetails> mTillDateAdapterContent = new ArrayList<PincodeDetails>();
	
	//private LinearLayout mTodayEmptyView,mWeekEmptyView,mMonthEmptyView,mTillDateEmptyView;
	
	private TextView mTodayEmptyTextView,mWeekEmptyTextView,mMonthEmptyTextView,mTillDateEmptyTextView;
	
	private final int TODAY_FRAGMENT = 0;
	
	private final int WEEK_FRAGMENT = 1;
	
	private final int MONTH_FRAGMENT = 2;
	
	private final int TILL_DATE_FRAGMENT = 3;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mActivity = activity;
		
		mContext = mActivity;
		
	}
	
	
	
	@Override
	public void onDetach() {
		super.onDetach();
		mActionBar.removeAllTabs();
		mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
	}

	public PincodesFragment() {
        // Empty constructor required for fragment subclasses
    }
	 @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		 mActionBar = ((AdminActivity)mActivity).getSupportActionBar();
		 setHasOptionsMenu(true);
		 View rootView = inflater.inflate(R.layout.activity_area_search, container, false);
		 mViewPager = (ViewPager) rootView.findViewById(R.id.pager);
		 mViewPager.setAdapter(new PincodeFragmentAdapter(getChildFragmentManager()));
		 mViewPager.setOnPageChangeListener(this);
		 mViewPager.setOffscreenPageLimit(4);
		 actionbarSetup();
		 new PincodesDownloader("today").execute();
		 new PincodesDownloader("weekly").execute();
		 new PincodesDownloader("monthly").execute();
		 new PincodesDownloader("tilldate").execute();
		 return rootView;
		 
	 }
	 
	 @Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
		case R.id.sort_ascending:
			sortList(false);
			break;
		case R.id.sort_descending:
			sortList(true);
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	 
	 @Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.pincode_coverage_menu, menu);
		MenuItem searchItem = menu.findItem(R.id.action_search);
		final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
	    SearchView.SearchAutoComplete searchAutoComplete = (SearchView.SearchAutoComplete) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
	    
	     LinearLayout searchPlate = (LinearLayout) searchView.findViewById(android.support.v7.appcompat.R.id.search_plate);
	     ImageView close_button = (ImageView) searchView.findViewById(android.support.v7.appcompat.R.id.search_close_btn);
	     close_button.setImageResource(R.drawable.cross);
	    searchPlate.setBackgroundResource(R.drawable.nav_search);
	    searchAutoComplete.setCompoundDrawablesWithIntrinsicBounds(R.drawable.action_search, 0, 0, 0);
	    searchAutoComplete.setHint("Enter pincode");
	    searchAutoComplete.setHintTextColor(getResources().getColor(R.color.searchbar_hint));
	    searchAutoComplete.setTextColor(Color.WHITE);
	    searchView.setInputType(InputType.TYPE_CLASS_NUMBER);
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
	public void onTabReselected(Tab arg0, FragmentTransaction arg1) {
		
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction arg1) {
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(Tab arg0, FragmentTransaction arg1) {
		
	}
	
	private void actionbarSetup(){
		mActionBar.setTitle("Pincode");
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
		mActionBar.addTab(mActionBar.newTab().setText("TILL DATE")
				.setTabListener(this));
	}
	
	public class TodayFragment extends Fragment{
		
		public TodayFragment(){
			
		}
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.pincode_list_fragment, container, false);
			LinearLayout mTodayEmptyView = (LinearLayout)rootView.findViewById(R.id.empty_view);
			mTodayEmptyTextView = (TextView) rootView.findViewById(R.id.empty_text);
			mTodayListView = (ListView) rootView.findViewById(R.id.list_view);
			mTodayDateText = (TextView) rootView.findViewById(R.id.date);			
			mTodayBusinessCountText = (TextView) rootView.findViewById(R.id.business_collected);
			mTodayListAdapter = new PincodeListAdapter(mTodayAdapterContent);
			mTodayListView.setAdapter(mTodayListAdapter);
			mTodayListView.setEmptyView(mTodayEmptyView);
			 
			 return rootView;
		}
	}
	
	
	
	public class WeekFragment extends Fragment{
		
		public WeekFragment(){
			
		}
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.pincode_list_fragment, container, false);
			LinearLayout mWeekEmptyView = (LinearLayout)rootView.findViewById(R.id.empty_view);
			mWeekEmptyTextView = (TextView) rootView.findViewById(R.id.empty_text);
			mWeekListView = (ListView) rootView.findViewById(R.id.list_view);
			mWeekDateText = (TextView) rootView.findViewById(R.id.date);
			mWeekBusinessCountText = (TextView) rootView.findViewById(R.id.business_collected);
			mWeekListAdapter = new PincodeListAdapter(mWeekAdapterContent);
			mWeekListView.setAdapter(mWeekListAdapter);
			mWeekListView.setEmptyView(mWeekEmptyView);
			 
			 return rootView;
		}
	}
	
	public class MonthFragment extends Fragment{
		
		public MonthFragment(){
			
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.pincode_list_fragment, container, false);
			LinearLayout mMonthEmptyView = (LinearLayout)rootView.findViewById(R.id.empty_view);
			mMonthEmptyTextView = (TextView) rootView.findViewById(R.id.empty_text);
			mMonthListView = (ListView) rootView.findViewById(R.id.list_view);
			mMonthDateText = (TextView) rootView.findViewById(R.id.date);
			mMonthBusinessCountText = (TextView) rootView.findViewById(R.id.business_collected);
			mMonthListAdapter = new PincodeListAdapter(mMonthAdapterContent);
			mMonthListView.setAdapter(mMonthListAdapter);
			mMonthListView.setEmptyView(mMonthEmptyView);
			 
			 return rootView;
		}
	}
	
	public class TillDateFragment extends Fragment{
		
		public TillDateFragment(){
			
		}
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.pincode_list_fragment, container, false);
			LinearLayout mTillDateEmptyView = (LinearLayout)rootView.findViewById(R.id.empty_view);
			mTillDateEmptyTextView = (TextView) rootView.findViewById(R.id.empty_text);
			mTillDateListView = (ListView) rootView.findViewById(R.id.list_view);
			mTillDateDateText = (TextView) rootView.findViewById(R.id.date);
			mTillDateBusinessCountText = (TextView) rootView.findViewById(R.id.business_collected);
			mTillDateListAdapter = new PincodeListAdapter(mTillDateAdapterContent);
			mTillDateListView.setAdapter(mTillDateListAdapter);
			mTillDateListView.setEmptyView(mTillDateEmptyView);
			 
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
			case TILL_DATE_FRAGMENT:
				fragment = new TillDateFragment();
				
				return fragment;
			default:
				return null;
				
			}
			
		}

		@Override
		public int getCount() {
			return 4;
		}
		
	}

	@Override
	public void onPageScrollStateChanged(int position) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void onPageScrolled(int position, float arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void onPageSelected(int position) {
		mActionBar.setSelectedNavigationItem(position);
		
	}



	@Override
	public boolean onQueryTextChange(String text) {
		mQueryText = text;
		Log.i(TAG, "QueryText :"+mQueryText);
		filterTodayList(text);
		filterWeekList(text);
		filterMonthList(text);
		filterTillDateList(text);
		/*if(text.length() == 0){		
			initTodayList();
			initWeekList();
			initMonthList();
			initTillDateList();
		}*/
		
	/*	switch(mViewPager.getCurrentItem()){
		case TODAY_FRAGMENT:
			if(text.length() == 0)
				initTodayList();
			break;
		case WEEK_FRAGMENT:
			if(text.length() == 0)
				initWeekList();
			break;
		case MONTH_FRAGMENT:
			if(text.length() == 0)
				initMonthList();
			break;
		case TILL_DATE_FRAGMENT:
			if(text.length() == 0)
				initTillDateList();
			break;
		}*/
		return false;
	}



	@Override
	public boolean onQueryTextSubmit(String text) {
		Log.i(TAG,"onQueryTextSubmit");
		// TODO Auto-generated method stub
		return false;
	}
	
	private void filterTodayList(String queryText){
		Log.i(TAG, "TODAY LIST UPDATE CALLED");
		mTodayAdapterContent.clear();
		for(int i=0;i<mTodayContent.size();i++)
			if(queryText.length() == 0 || mTodayContent.get(i).pincode.startsWith(queryText) )
				mTodayAdapterContent.add(mTodayContent.get(i));		
		if(mTodayListAdapter != null){
			mTodayListAdapter.notifyDataSetChanged();
			mTodayEmptyTextView.setVisibility(TextView.VISIBLE);
		}
	}
	
	private void filterWeekList(String queryText){
		Log.i(TAG, "WEEK LIST UPDATE CALLED");
		mWeekAdapterContent.clear();
		for(int i=0;i<mWeekContent.size();i++)
			if(queryText.length() == 0 || mWeekContent.get(i).pincode.startsWith(queryText) )
			mWeekAdapterContent.add(mWeekContent.get(i));
		
		if(mWeekListAdapter != null){
		mWeekListAdapter.notifyDataSetChanged();
		mWeekEmptyTextView.setVisibility(TextView.VISIBLE);
		}
	}
	
	private void filterMonthList(String queryText){
		Log.i(TAG, "MONTH LIST UPDATE CALLED");
		mMonthAdapterContent.clear();
		for(int i=0;i<mMonthContent.size();i++)
			if(queryText.length() == 0 || mMonthContent.get(i).pincode.startsWith(queryText) )
			mMonthAdapterContent.add(mMonthContent.get(i));
		
		if(mMonthListAdapter != null){
		mMonthListAdapter.notifyDataSetChanged();
		mMonthEmptyTextView.setVisibility(TextView.VISIBLE);
		}
	}
	
	private void filterTillDateList(String queryText){
		Log.i(TAG, "TILL DATE LIST UPDATE CALLED");
		mTillDateAdapterContent.clear();
		for(int i=0;i<mTillDateContent.size();i++)
			if(queryText.length() == 0 || mTillDateContent.get(i).pincode.startsWith(queryText) )
			mTillDateAdapterContent.add(mTillDateContent.get(i));
		
		if(mTillDateListAdapter != null){
		mTillDateListAdapter.notifyDataSetChanged();
		mTillDateEmptyTextView.setVisibility(TextView.VISIBLE);
		}
	}
	
	/*private void initTodayList(){
		Log.i(TAG, "TODAY LIST UPDATE CALLED");
		mTodayAdapterContent.clear();
		for(int i=0;i<mTodayContent.size();i++)
			mTodayAdapterContent.add(mTodayContent.get(i));
		
		if(mTodayListAdapter != null)
		mTodayListAdapter.notifyDataSetChanged();
	}
	
	private void initWeekList(){
		Log.i(TAG, "WEEK LIST UPDATE CALLED");
		mWeekAdapterContent.clear();
		for(int i=0;i<mWeekContent.size();i++)
			mWeekAdapterContent.add(mWeekContent.get(i));
		
		if(mWeekListAdapter != null)
		mWeekListAdapter.notifyDataSetChanged();
	}
	
	private void initMonthList(){
		Log.i(TAG, "MONTH LIST UPDATE CALLED");
		mMonthAdapterContent.clear();
		for(int i=0;i<mMonthContent.size();i++)
			mMonthAdapterContent.add(mMonthContent.get(i));
		
		if(mMonthListAdapter != null)
		mMonthListAdapter.notifyDataSetChanged();
	}
	
	private void initTillDateList(){
		Log.i(TAG, "TILL DATE LIST UPDATE CALLED");
		mTillDateAdapterContent.clear();
		for(int i=0;i<mTillDateContent.size();i++)
			mTillDateAdapterContent.add(mTillDateContent.get(i));
		
		if(mTillDateListAdapter != null)
		mTillDateListAdapter.notifyDataSetChanged();
	}*/
	
	
	public class PincodeListAdapter extends BaseAdapter{
		
		ArrayList<PincodeDetails> mPincodeDetails = new ArrayList<PincodeDetails>();
		
		PincodeListAdapter(ArrayList<PincodeDetails> pincodeDetailsList){
			this.mPincodeDetails = pincodeDetailsList;
		}
		

		@Override
		public int getCount() {
			return mPincodeDetails.size();
		}

		@Override
		public Object getItem(int position) {
			return mPincodeDetails.get(position);
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final String pincode = mPincodeDetails.get(position).pincode;
			final String period = mPincodeDetails.get(position).period;
			int business_count = mPincodeDetails.get(position).business_count;
			ViewHolder viewHolder = null;
			if(convertView == null){
				LayoutInflater inflater = (LayoutInflater)mContext.getSystemService
					      (Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(R.layout.pincode_coverage_row,
						parent, false);
				viewHolder = new ViewHolder();
				viewHolder.content_layout = (LinearLayout) convertView.findViewById(R.id.content);
				viewHolder.pincode = (TextView) convertView.findViewById(R.id.pincode);
				viewHolder.business_count = (TextView) convertView.findViewById(R.id.business_count);
				viewHolder.header_layout = (LinearLayout) convertView.findViewById(R.id.header);
				
				convertView.setTag(viewHolder);
			}else{
				viewHolder = (ViewHolder) convertView.getTag();
			}
			if(position == 0)
				viewHolder.header_layout.setVisibility(LinearLayout.VISIBLE);
			else
				viewHolder.header_layout.setVisibility(LinearLayout.GONE);
			viewHolder.pincode.setText(pincode);
			viewHolder.business_count.setText(String.valueOf(business_count));
			viewHolder.content_layout.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Constants.PIN = pincode;
					((AdminActivity)mContext).startMapFragment(period);
				}
			});
			return convertView;
		}
		
		public class ViewHolder{
			LinearLayout content_layout;
			LinearLayout header_layout;
			TextView pincode;
			TextView business_count;
		}
		 
	}
	
	public class PincodeDetails{
		public String pincode;
		public int business_count;
		public String period;
	}
	
	private class PincodesDownloader extends AsyncTask<Void, Void, String>{
		String type;
		
		PincodesDownloader(String type){
			this.type = type;
		}

		@Override
		protected String doInBackground(Void... params) {
			String result = null;
			HttpGet httpGet = new HttpGet(Constants.BASE_URL+"admin/getPincodeAnalytics.php?city="+UserProfile.CITY+"&period="+type);
			try {
				result = httpGet.executeGet();
			} catch (JsonGetException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			return result;
		}
		
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if(result != null){
				try {
					JSONObject json = new JSONObject(result);
					String status = json.getString("status");
					if(status.equalsIgnoreCase("success")){
						listUpdate(json,type);
					}else{
						Toast.makeText(mContext, "Please Try Again", Toast.LENGTH_LONG).show();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else{
				Toast.makeText(mContext, "No Network", Toast.LENGTH_LONG).show();
			}
		}
		
	}
	
	private void listUpdate(JSONObject resultJson,String type) throws JSONException{
		String date = resultJson.getString("date");
		int count = resultJson.getInt("count");
		int total_businesses = resultJson.getInt("total_business_collected");
		String totalBusinesses = String.valueOf(total_businesses);
		if(type.equalsIgnoreCase("today")){
			mTodayContent.clear();
			mTodayDateText.setText(date);
			mTodayBusinessCountText.setText(totalBusinesses);
		}else if(type.equalsIgnoreCase("weekly")){
			mWeekContent.clear();
			mWeekDateText.setText(date);
			mWeekBusinessCountText.setText(totalBusinesses);
		}else if(type.equalsIgnoreCase("monthly")){
			mMonthContent.clear();
			mMonthDateText.setText(date);
			mMonthBusinessCountText.setText(totalBusinesses);
		}else if(type.equalsIgnoreCase("tilldate")){
			mTillDateContent.clear();
			mTillDateDateText.setText(date);
			mTillDateBusinessCountText.setText(totalBusinesses);
		}	
		if(count == 0){
			return;
		}
		JSONArray analytics_arr = resultJson.getJSONArray("analytics");
		for(int i=0;i<analytics_arr.length();i++){
			JSONObject pincodeJson = analytics_arr.getJSONObject(i);
			PincodeDetails pincodeDetails = new PincodeDetails();
			pincodeDetails.pincode = pincodeJson.getString("pincode");
			pincodeDetails.business_count = pincodeJson.getInt("business_collected");
			if(type.equalsIgnoreCase("today")){
				pincodeDetails.period = "today";
				mTodayContent.add(pincodeDetails);
			}else if(type.equalsIgnoreCase("weekly")){
				pincodeDetails.period = "weekly";
				mWeekContent.add(pincodeDetails);
			}else if(type.equalsIgnoreCase("monthly")){
				pincodeDetails.period = "monthly";
				mMonthContent.add(pincodeDetails);
			}else if(type.equalsIgnoreCase("tilldate")){
				pincodeDetails.period = "tilldate";
				mTillDateContent.add(pincodeDetails);
			}
		}		
		onQueryTextChange(mQueryText);
		
	}
	
	private void sortList(boolean isDescendingOrder){
		sortTodayList(isDescendingOrder);
		sortWeekList(isDescendingOrder);
		sortMonthList(isDescendingOrder);
		sortTillDateList(isDescendingOrder);
		onQueryTextChange(mQueryText);
	}
	
	private void sortTodayList(boolean isDescending){
		for(int i=0;i<mTodayContent.size();i++){
		for(int j=0;j<mTodayContent.size()-1;j++){
			if((mTodayContent.get(j).business_count<mTodayContent.get(j+1).business_count && isDescending) || (mTodayContent.get(j).business_count>mTodayContent.get(j+1).business_count && !isDescending)){
				swapTodayList(j);
			}				
		}
		}
	}
	
	private void swapTodayList(int position){
		PincodeDetails temp = mTodayContent.get(position+1);
		mTodayContent.set(position+1, mTodayContent.get(position));
		mTodayContent.set(position, temp);
	}
	
	private void sortWeekList(boolean isDescending){
		for(int i=0;i<mWeekContent.size();i++){
		for(int j=0;j<mWeekContent.size()-1;j++){
			if((mWeekContent.get(j).business_count<mWeekContent.get(j+1).business_count && isDescending) || (mWeekContent.get(j).business_count>mWeekContent.get(j+1).business_count && !isDescending)){
				swapWeekList(j);
			}				
		}
		}
	}
	
	private void swapWeekList(int position){
		PincodeDetails temp = mWeekContent.get(position+1);
		mWeekContent.set(position+1, mWeekContent.get(position));
		mWeekContent.set(position, temp);
	}
	
	private void sortMonthList(boolean isDescending){
		for(int i=0;i<mMonthContent.size();i++){
		for(int j=0;j<mMonthContent.size()-1;j++){
			if((mMonthContent.get(j).business_count<mMonthContent.get(j+1).business_count && isDescending) || (mMonthContent.get(j).business_count>mMonthContent.get(j+1).business_count && !isDescending)){
				swapMonthList(j);
			}				
		}
		}
	}
	
	private void swapMonthList(int position){
		PincodeDetails temp = mMonthContent.get(position+1);
		mMonthContent.set(position+1, mMonthContent.get(position));
		mMonthContent.set(position, temp);
	}
	
	private void sortTillDateList(boolean isDescending){
		for(int i=0;i<mTillDateContent.size();i++){
		for(int j=0;j<mTillDateContent.size()-1;j++){
			if((mTillDateContent.get(j).business_count<mTillDateContent.get(j+1).business_count && isDescending) || (mTillDateContent.get(j).business_count>mTillDateContent.get(j+1).business_count && !isDescending)){
				swapTillDateList(j);
			}				
		}
		}
	}
	
	private void swapTillDateList(int position){
		PincodeDetails temp = mTillDateContent.get(position+1);
		mTillDateContent.set(position+1, mTillDateContent.get(position));
		mTillDateContent.set(position, temp);
	}
	
	
	

}
