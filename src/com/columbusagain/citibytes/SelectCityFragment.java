package com.columbusagain.citibytes;

import java.util.ArrayList;

import com.columbusagain.citibytes.helper.UserProfile;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class SelectCityFragment extends Fragment implements OnItemClickListener, OnQueryTextListener {
	private String[] mCityArray;
	
	private ListView mAreaListView;
	
	private String[] mStateArray;
	
	private String[] mStdArray;
	
	private Activity mActivity;
	
	private ActionBar mActionBar;
	
	private CityList mCityList;
	
	private View mView;
	
	private boolean isFirstTime = true;
	
	private ArrayList<CityList> mDefaultCityList = new ArrayList<CityList>();
	
	private ArrayList<CityList> mAdapterCityList = new ArrayList<CityList>();
	
	//private int mSelectedPosition = -1;
	
	private CityArrayAdapter mCityAdapter;

	public SelectCityFragment() {
    }
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		Log.i("SelectCityFragment", "onAttach");
		mActivity = activity;
		
	}
	 @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		 Log.i("SelectCityFragment", "onCreateView");
		 if(UserProfile.isAdmin){
				mActionBar = ((AdminActivity)mActivity).getSupportActionBar();
			}else if(!UserProfile.isAdmin){
				mActionBar = ((CAActivity)mActivity).getSupportActionBar();
			}
			 if(mActionBar != null)
			 mActionBar.setTitle("Select Your City"); 
		 View rootView = inflater.inflate(R.layout.activity_select_city, container, false);
		 setHasOptionsMenu(true);
		 if(isFirstTime){
		 mCityArray = getResources().getStringArray(R.array.city_array);
		 mStateArray = getResources().getStringArray(R.array.state_array);
		 mStdArray = getResources().getStringArray(R.array.std_array);
		
		 for(int i=0;i<mCityArray.length;i++){
			 mCityList = new CityList();
			 mCityList.cityName = mCityArray[i];
			 mCityList.isSelected = false;
			 mCityList.state = mStateArray[i];
			 mCityList.std = mStdArray[i];
			 mDefaultCityList.add(mCityList);
	        }
		 for(int i=0;i<mDefaultCityList.size();i++)
			 mAdapterCityList.add(mDefaultCityList.get(i));
		 
		 isFirstTime = false;
		 }
		 mCityAdapter = new CityArrayAdapter();
		 
		 
		 mAreaListView = (ListView)rootView.findViewById(R.id.area_list_view);
		 
		 mAreaListView.setAdapter(mCityAdapter);
		 mAreaListView.setOnItemClickListener(this);
		 
		 
		
		 return rootView;
		 
	 }
	 
	 private void updateListView(String text) {
			for (int i = 0; i < mAdapterCityList.size(); i++) {
				if (mAdapterCityList.get(i).cityName.toUpperCase().startsWith(text.toUpperCase())) {
				}else{
					mAdapterCityList.remove(i);
					i--;
				}
			}
			mCityAdapter.notifyDataSetChanged();
			return;
		}
	 
	 
	 @Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		 Log.i("SelectCityFragment", "onCreateOptionsMenu");
		 inflater.inflate(R.menu.select_city, menu);
		 MenuItem searchItem = menu.findItem(R.id.action_search);
		    SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
		    SearchView.SearchAutoComplete searchAutoComplete = (SearchView.SearchAutoComplete) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
		    
		     LinearLayout searchPlate = (LinearLayout) searchView.findViewById(android.support.v7.appcompat.R.id.search_plate);
		     ImageView close_button = (ImageView) searchView.findViewById(android.support.v7.appcompat.R.id.search_close_btn);
		     close_button.setImageResource(R.drawable.cross);
		    searchPlate.setBackgroundResource(R.drawable.nav_search);
		    searchAutoComplete.setCompoundDrawablesWithIntrinsicBounds(R.drawable.action_search, 0, 0, 0);
		    searchAutoComplete.setHint("Search City");
		    searchAutoComplete.setHintTextColor(getResources().getColor(R.color.searchbar_hint));
		    searchAutoComplete.setTextColor(Color.WHITE);
		    searchView.setOnQueryTextListener(this);
		super.onCreateOptionsMenu(menu, inflater);
		
		
	}
	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
		
		TextView tv = (TextView) view.findViewById(R.id.area_text_view);
		UserProfile.CITY = mDefaultCityList.get(position).cityName;
		UserProfile.STATE = mDefaultCityList.get(position).state;
		UserProfile.STD = mDefaultCityList.get(position).std;
		for(int i=0;i<mDefaultCityList.size();i++){
			mDefaultCityList.get(i).isSelected = false;
		}
		for(int i=0;i<mAdapterCityList.size();i++){
			mAdapterCityList.get(i).isSelected = false;
		}
		mAdapterCityList.get(position).isSelected = true;
		/*if(mAdapterCityList.get(position).isSelected){
			mAdapterCityList.get(position).isSelected = false;
		}else{
			mAdapterCityList.get(position).isSelected = true;
		}*/
		
		mCityAdapter.notifyDataSetChanged();
		if(UserProfile.isAdmin){
			((AdminActivity)mActivity).startPincodeRequestsFragment();
		}else{
		((CAActivity) mActivity).startMyAreaActivity();
		}
		
	}
	
	
private class CityArrayAdapter extends BaseAdapter{
		

		@Override
		public int getCount() {
			return mAdapterCityList.size();
		}

		@Override
		public String getItem(int position) {
			return mAdapterCityList.get(position).cityName;
		}

		@Override
		public long getItemId(int position) {
			
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder = null;
			if (convertView == null) {
				LayoutInflater inflater = mActivity.getLayoutInflater();
				convertView = inflater.inflate(R.layout.selectcity_row_layout,
						parent, false);
				viewHolder = new ViewHolder();
				viewHolder.areaTextView = (TextView) convertView
						.findViewById(R.id.area_text_view);
				viewHolder.checkImage = (ImageView) convertView
						.findViewById(R.id.check_image);
				convertView.setTag(viewHolder);
			} else
				viewHolder = (ViewHolder) convertView.getTag();
			viewHolder.areaTextView.setText(mAdapterCityList.get(position).cityName);
			
			if (mAdapterCityList.get(position).isSelected) {
				viewHolder.checkImage.setVisibility(ImageView.VISIBLE);
			} else {
				viewHolder.checkImage.setVisibility(ImageView.GONE);
				
			}
			
			if(UserProfile.CITY !=null){
			
			if(viewHolder.areaTextView.getText().toString().toLowerCase().equals(UserProfile.CITY.toLowerCase())){
				viewHolder.checkImage.setVisibility(ImageView.VISIBLE);
			}else{
				//viewHolder.checkImage.setVisibility(ImageView.GONE);
			}
			}

			
			return convertView;
		}
		
		private class ViewHolder {
			
			public TextView areaTextView;
			public ImageView checkImage;
		}

		
	}


@Override
public boolean onQueryTextChange(String text) {
	 Log.i("SelectCityFragment", "onQueryTextChange");
	mAdapterCityList.clear();
	Log.i("ArrayListSize", ""+mDefaultCityList.size());
	for(int i=0;i<mDefaultCityList.size();i++){
		mAdapterCityList.add(mDefaultCityList.get(i));
	}
	
	if(text.length()>0){
	updateListView(text);
	}
	else
		mCityAdapter.notifyDataSetChanged();
	return false;
}
@Override
public boolean onQueryTextSubmit(String text) {
	return false;
}

private class CityList{
	private String cityName;
	private String state;
	private String std;
	private boolean isSelected;
}


	

}
