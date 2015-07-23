package com.columbusagain.citibytes;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.columbusagain.citibytes.MyAreaFragment.PincodeFragmentPagerAdapter;
import com.columbusagain.citibytes.adapters.PinCodeApprovedListAdapter.ViewHolder;
import com.columbusagain.citibytes.helper.Constants;
import com.columbusagain.citibytes.helper.HttpPost;
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
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.ActionBar;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import android.widget.Toast;

public class LeaderBoardFragment extends Fragment implements OnTabChangeListener, OnPageChangeListener {
	
	ActionBar mActionBar;
	
	ViewPager mViewPager;
	
	ImageView mProfileImageView;
	
	private TabHost mTabHost;
	
	private Context mContext;
	
	private LinearLayout mUserRankLayout;
	
	private TextView mUserRankTextView,mUserBusinessCountTextView,mUserNameTextView,mEmailTextView;
	
	ListView mMonthRankListView,mWeekRankListView;
	
	LeaderBoardListAdapter mMonthListAdapter,mWeekListAdapter;
	
	ArrayList<ListContent> mWeekListContent = new ArrayList<ListContent>();
	
	ArrayList<ListContent> mMonthListContent = new ArrayList<ListContent>();	
	
	private SpannableString mUserWeekRank,mUserMonthRank,mUserMonthBusinessCount,mUserWeekBusinessCount;
	
	private String mMonthTotalUsers,mWeekTotalUsers;
	
	private Activity mActivity;
	
	private View mRootView;
	
	private TextView mWeekEmptyTextView,mMonthEmptyTextView;
	
	public static final String TAB_WEEK = "week";
	
	public static final String TAB_MONTH = "month";
	
	private static Dialog mProgressDialog;
	
	//private TextView mTab1TextView;
	//private TextView mTab2TextView;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.mActivity = activity;
		this.mContext = activity;
		mActionBar = ((CAActivity)mContext).getSupportActionBar();
		mActionBar.setTitle("Leader Board");
	}
	
	public LeaderBoardFragment() {
    }
	 @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		 mProgressDialog = new Dialog(mActivity,R.style.ProgressDialog);
		 	mProgressDialog.setContentView(R.layout.progress_dialog);
		 mRootView = inflater.inflate(R.layout.fragment_leader_board, container, false);
		 mProfileImageView = (ImageView)mRootView.findViewById(R.id.profile_pic);
		 mProfileImageView.setImageBitmap(UserProfile.PROFILE_PICTURE);
		 mTabHost = (TabHost) mRootView.findViewById(android.R.id.tabhost);
		 mUserRankLayout = (LinearLayout) mRootView.findViewById(R.id.user_rank_layout);
		 mUserRankTextView = (TextView) mRootView.findViewById(R.id.user_rank_text_view);
		 mUserBusinessCountTextView = (TextView) mRootView.findViewById(R.id.user_business_count_text_view);
		 mUserNameTextView = (TextView) mRootView.findViewById(R.id.display_name_text_view);
		 mUserNameTextView.setText(UserProfile.PROFILE_NAME);
		 mEmailTextView = (TextView) mRootView.findViewById(R.id.email_text_view);
		 mEmailTextView.setText(UserProfile.EMAIL_ID);
		 mViewPager = (ViewPager) mRootView.findViewById(R.id.pager);
		 mViewPager.setAdapter(new LeaderBoardPagerAdapter(getChildFragmentManager()));
	      /*  mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
	            @Override
	            public void onPageSelected(int position) {
	            	mTabHost.setCurrentTab(position);
	            	//mActionbar.setSelectedNavigationItem(position);
	            	
	            }
	        });*/
		// mTab1TextView = (TextView) mRootView.findViewById(R.id.tab1_text);
		// mTab2TextView = (TextView) mRootView.findViewById(R.id.tab2_text);
		 setupTabs();
		 mTabHost.setOnTabChangedListener(this);
		 mTabHost.setCurrentTab(0);
		 mViewPager.setOnPageChangeListener(this);
		// showTab1Content();
		 boolean isNetworkConnected = NetworkChecker.isConnected(mContext);
			if(!isNetworkConnected){
				Toast.makeText(mContext, "No Internet!", Toast.LENGTH_LONG).show();
			}else{
				//isPopulateData = false;
				mProgressDialog.show();
				new GetLeaderBoardContent().execute();
			} 
		 return mRootView;
		 
	 }
	 
	 @Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}
	 
	 
	 private void setupTabs() {
			mTabHost.setup(); // you must call this before adding your tabs!
			TabSpec tabSpec = mTabHost.newTabSpec(TAB_WEEK);
			tabSpec.setIndicator("THIS WEEK");
			tabSpec.setContent(R.id.tab_1);
			mTabHost.addTab(tabSpec);
			
			tabSpec = mTabHost.newTabSpec(TAB_MONTH);
			tabSpec.setIndicator("THIS MONTH");
			tabSpec.setContent(R.id.tab_2);
			mTabHost.addTab(tabSpec);
		}
	 
	/* private void showTab1Content(){
		 mTab1TextView.setVisibility(TextView.VISIBLE);
		 mTab2TextView.setVisibility(TextView.GONE);
	 }
	 
	 private void showTab2Content(){
		 mTab1TextView.setVisibility(TextView.GONE);
		 mTab2TextView.setVisibility(TextView.VISIBLE);
		 
	 }*/

		 @Override
		public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
			 inflater.inflate(R.menu.area_search, menu);
			super.onCreateOptionsMenu(menu, inflater);
			
			
		}
	@Override
	public void onTabChanged(String tabId) {
		
		Log.i("LeaderBoardFragment", "onTabChanged");
		if(TAB_MONTH.equals(tabId)){
			Log.i("LeaderBoardFragment", TAB_MONTH);
			mViewPager.setCurrentItem(1);
		//	showTab2Content();
		}else{
			mViewPager.setCurrentItem(0);
			//Log.i("LeaderBoardFragment", TAB_WEEK);
			//showTab1Content();
		}
		
	}
	
	private class GetLeaderBoardContent extends AsyncTask<Void, Void, String>{
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(Void... params) {
			HttpPost httpPost = new HttpPost(Constants.BASE_URL+"getLeaderBoard.php");
			httpPost.setParam("email_id", UserProfile.EMAIL_ID);
			return httpPost.executePost();
		}
		
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if(mProgressDialog.isShowing()){
				mProgressDialog.dismiss();
			}
			if(result != null){
				
				try {
					JSONObject responseJson = new JSONObject(result);
					String status = responseJson.getString("status");
					if(status.equalsIgnoreCase("success")){
						parseLeaderBoardData(responseJson);
					}else{
						Toast.makeText(mContext, "Please try again", Toast.LENGTH_LONG).show();
					}
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Log.i("LeaderBoard", "Response :"+result);
			}else{
				Toast.makeText(mContext, "No network", Toast.LENGTH_LONG).show();
			}
		}
		
	}
	
	private void parseLeaderBoardData(JSONObject jsonObj) throws JSONException{
		JSONObject rankObj;
		String email,count,rank;
		if(jsonObj.has("month_rank")){
		JSONArray monthRankArray = jsonObj.getJSONArray("month_rank");
		for(int i=0;i<monthRankArray.length();i++){
			rankObj = monthRankArray.getJSONObject(i);
			email = rankObj.getString("email_id");
			count = rankObj.getString("business_count");
			ListContent listContent = new ListContent();
			listContent.email = email;
			listContent.business_collected = count;
			mMonthListContent.add(listContent);
			
		}
		}
		if(jsonObj.has("week_rank")){
		JSONArray weekRankArray = jsonObj.getJSONArray("week_rank");
		for(int i=0;i<weekRankArray.length();i++){
			rankObj = weekRankArray.getJSONObject(i);
			email = rankObj.getString("email_id");
			count = rankObj.getString("business_count");
			ListContent listContent = new ListContent();
			listContent.email = email;
			listContent.business_collected = count;
			mWeekListContent.add(listContent);
			
		}
		}
		setListContents();
		
		mMonthTotalUsers = jsonObj.getString("month_total_users");
		mWeekTotalUsers = jsonObj.getString("weekly_total_users");
		
		if(jsonObj.has("user_month_rank")){
			rankObj = jsonObj.getJSONObject("user_month_rank");
			rank = rankObj.getString("rank");
			count = rankObj.getString("business_count");
			//mUserMonthRank =rank+"/"+mMonthTotalUsers;
			//mUserMonthBusinessCount = count;
			
			mUserMonthRank =new SpannableString(rank+"/"+mMonthTotalUsers);
			mUserMonthRank.setSpan(new RelativeSizeSpan(2f), 0,rank.length(), 0);  // set size
			mUserMonthRank.setSpan(new ForegroundColorSpan(Color.BLACK), 0, rank.length(), 0);// set color
			mUserMonthBusinessCount = new SpannableString(count);
			mUserMonthBusinessCount.setSpan(new RelativeSizeSpan(2f), 0,count.length(), 0);  // set size
			mUserMonthBusinessCount.setSpan(new ForegroundColorSpan(Color.BLACK), 0, rank.length(), 0);// set color
		}
		
		if(jsonObj.has("user_week_rank")){
			rankObj = jsonObj.getJSONObject("user_week_rank");
			rank = rankObj.getString("rank");
			count = rankObj.getString("business_count");
			//SpannableString ss1=  new SpannableString(rank);
			// ss1.setSpan(new RelativeSizeSpan(2f), 0,rank.length(), 0); // set size
			// ss1.setSpan(new ForegroundColorSpan(Color.RED), 0, rank.length(), 0);// set color
			
			mUserWeekRank =new SpannableString(rank+"/"+mWeekTotalUsers);
			mUserWeekRank.setSpan(new RelativeSizeSpan(2f), 0,rank.length(), 0);  // set size
			mUserWeekRank.setSpan(new ForegroundColorSpan(Color.BLACK), 0, rank.length(), 0);// set color
			mUserWeekBusinessCount = new SpannableString(count);
			mUserWeekBusinessCount.setSpan(new RelativeSizeSpan(2f), 0,count.length(), 0);  // set size
			mUserWeekBusinessCount.setSpan(new ForegroundColorSpan(Color.BLACK), 0, rank.length(), 0);// set color
		}
		int currentPage = mViewPager.getCurrentItem();
		
		if(currentPage == 0){
			
			if(mUserWeekRank != null){
				mUserRankLayout.setVisibility(LinearLayout.VISIBLE);
				mUserRankTextView.setText(mUserWeekRank);
				mUserBusinessCountTextView.setText(mUserWeekBusinessCount);
			}else{
				mUserRankLayout.setVisibility(LinearLayout.GONE);
				mUserBusinessCountTextView.setText("0");
			}
			
		}else{
			if(mUserMonthRank != null){
				mUserRankLayout.setVisibility(LinearLayout.VISIBLE);
				mUserRankTextView.setText(mUserMonthRank);
			 mUserBusinessCountTextView.setText(mUserMonthBusinessCount);
			}else{
				mUserRankLayout.setVisibility(LinearLayout.GONE);
				mUserBusinessCountTextView.setText("0");
			}
			
		}
		
	}
	
	private void setListContents(){
		if(mMonthListAdapter == null){
		mMonthListAdapter = new LeaderBoardListAdapter(mMonthListContent);
		mMonthRankListView.setAdapter(mMonthListAdapter);
		mMonthEmptyTextView.setVisibility(TextView.VISIBLE);
		}
		else
			mMonthListAdapter.notifyDataSetChanged();
		if(mWeekListAdapter == null){
		mWeekListAdapter = new LeaderBoardListAdapter(mWeekListContent);
		mWeekRankListView.setAdapter(mWeekListAdapter);
		
		mWeekEmptyTextView.setVisibility(TextView.VISIBLE);
		}
		else
			mWeekListAdapter.notifyDataSetChanged();
	}
	
	public class WeekFragment extends Fragment{
		LinearLayout mEmptyView;
		public WeekFragment(){
			
		}
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View view = inflater.inflate(R.layout.leader_board_list, container, false);
			mWeekRankListView = (ListView) view.findViewById(R.id.rank_list);			
			mEmptyView = (LinearLayout) view.findViewById(R.id.empty_view);
			mWeekRankListView.setEmptyView(mEmptyView);
			mWeekEmptyTextView = (TextView) view.findViewById(R.id.empty_text);
			mWeekEmptyTextView.setText("None");
			return view;
		}
	}
	
	public class MonthFragment extends Fragment{
		
		LinearLayout mEmptyView;
		public MonthFragment(){
			
		}
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View view = inflater.inflate(R.layout.leader_board_list, container, false);
			mMonthRankListView = (ListView) view.findViewById(R.id.rank_list);
			mEmptyView = (LinearLayout) view.findViewById(R.id.empty_view);
			mMonthRankListView.setEmptyView(mEmptyView);
			mMonthEmptyTextView = (TextView) view.findViewById(R.id.empty_text);
			mMonthEmptyTextView.setText("None");
			return view;
		}
	}
	
	public class LeaderBoardPagerAdapter extends FragmentPagerAdapter{

		public LeaderBoardPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			Fragment fragment;
			switch(position){
			case 0:
				fragment = new WeekFragment();
				return fragment;
			case 1:
				fragment = new MonthFragment();
				return fragment;
			default:
				return null;
				
			}
			
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return 2;
		}
		
	}
	
	public class LeaderBoardListAdapter extends BaseAdapter{
		
		ArrayList<ListContent> mListContents  = new ArrayList<ListContent>();
		
		
		public LeaderBoardListAdapter(ArrayList<ListContent> listcontent){
			this.mListContents = listcontent;
			
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mListContents.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return mListContents.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder = null;
			if(convertView == null){
				LayoutInflater inflater = (LayoutInflater)mContext.getSystemService
					      (Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(R.layout.leader_board_row,
						parent, false);
				viewHolder = new ViewHolder();
				viewHolder.mEmailText = (TextView) convertView
						.findViewById(R.id.email);
				viewHolder.mBusinessCountText = (TextView) convertView
						.findViewById(R.id.count);
				viewHolder.mHeaderLayout = (LinearLayout)convertView.findViewById(R.id.header_layout);
				
				convertView.setTag(viewHolder);
			}else{
				viewHolder = (ViewHolder) convertView.getTag();
			}
			viewHolder.mEmailText.setText(mListContents.get(position).email);
			viewHolder.mBusinessCountText.setText(mListContents.get(position).business_collected);
			if(position == 0){
				viewHolder.mHeaderLayout.setVisibility(LinearLayout.VISIBLE);
			}else{
				viewHolder.mHeaderLayout.setVisibility(LinearLayout.GONE);
			}
			
			
			return convertView;
		}
		
		public class ViewHolder{
			LinearLayout mHeaderLayout;
			TextView mEmailText;
			TextView mBusinessCountText;
		}
		
	}
	
	public class ListContent{
		String email;
		String business_collected;
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPageSelected(int position) {
		mTabHost.setCurrentTab(position);
			
		switch(position){
		case 0:
			
			if(mUserWeekRank != null){
				mUserRankLayout.setVisibility(LinearLayout.VISIBLE);
				mUserRankTextView.setText(mUserWeekRank);
				mUserBusinessCountTextView.setText(mUserWeekBusinessCount);
			}else{
				mUserRankLayout.setVisibility(LinearLayout.GONE);
				mUserBusinessCountTextView.setText("0");
			}
			break;
		case 1:
			if(mUserMonthRank != null){
				mUserRankLayout.setVisibility(LinearLayout.VISIBLE);
				mUserRankTextView.setText(mUserMonthRank);
			 mUserBusinessCountTextView.setText(mUserMonthBusinessCount);
			}else{
				mUserRankLayout.setVisibility(LinearLayout.GONE);
				mUserBusinessCountTextView.setText("0");
			}
			break;
		}
		
	}

}
