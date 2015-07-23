package com.columbusagain.citibytes;



import java.util.ArrayList;
import java.util.Calendar;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.columbusagain.citibytes.helper.Categories;

public class CategoryListActivity extends ActionBarActivity implements OnItemClickListener, OnQueryTextListener, OnClickListener {
	
	private long mStartTime,mStopTime;
	
	private Button mDoneButton;
	
	private ArrayList<String> mSelectedCategories = new ArrayList<String>();
	
	private ActionBar mActionbar;

	private Context mContext = this;
	
	private ArrayList<Categories> mAdapterCategoryList = new ArrayList<Categories>();

	private String[] mDefaultCategories;

	private ListView mChainListView;

	private CategoriesAdapter mCategoryListAdapter;
	
	private Dialog mProgressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_category_list);
		
		mActionbar = getSupportActionBar();
		mActionbar.setTitle("Chain Name");
		mActionbar.setDisplayHomeAsUpEnabled(true);
        mActionbar.setHomeButtonEnabled(true);
        mActionbar.setLogo(R.drawable.logo_navbar);
        
        mProgressDialog = new Dialog(this,R.style.ProgressDialog);
		mProgressDialog.setContentView(R.layout.progress_dialog);
        
        mDoneButton = (Button) findViewById(R.id.done_button);
        mDefaultCategories = getResources().getStringArray(R.array.category_array);
       for(int i=0;i<Categories.mCategoryList.size();i++)
       mAdapterCategoryList.add(Categories.mCategoryList.get(i));
		mChainListView = (ListView) findViewById(R.id.category_list_view);
		mCategoryListAdapter = new CategoriesAdapter();
		mChainListView.setAdapter(mCategoryListAdapter);
		mChainListView.setOnItemClickListener(this);
		mDoneButton.setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.select_city, menu);
		MenuItem searchItem = menu.findItem(R.id.action_search);
		SearchView searchView = (SearchView) MenuItemCompat
				.getActionView(searchItem);
		SearchView.SearchAutoComplete searchAutoComplete = (SearchView.SearchAutoComplete) searchView
				.findViewById(android.support.v7.appcompat.R.id.search_src_text);

		LinearLayout searchPlate = (LinearLayout) searchView
				.findViewById(android.support.v7.appcompat.R.id.search_plate);
		ImageView close_button = (ImageView) searchView
				.findViewById(android.support.v7.appcompat.R.id.search_close_btn);
		close_button.setImageResource(R.drawable.cross);
		searchPlate.setBackgroundResource(R.drawable.nav_search);
		searchAutoComplete.setCompoundDrawablesWithIntrinsicBounds(
				R.drawable.action_search, 0, 0, 0);
		searchAutoComplete.setHint("Search City");
		searchAutoComplete.setHintTextColor(getResources().getColor(
				R.color.searchbar_hint));
		searchAutoComplete.setTextColor(Color.WHITE);
		searchView.setOnQueryTextListener(this);
		return super.onCreateOptionsMenu(menu);
	}
	
	private void updateListView(String text) {
		for (int i = 0; i < mAdapterCategoryList.size(); i++) {
			if (mAdapterCategoryList.get(i).categoryName.toUpperCase().contains(text.toUpperCase())) {
			}else{
				mAdapterCategoryList.remove(i);
				i--;
			}

		}
		mCategoryListAdapter.notifyDataSetChanged();
		return;
	}
	
	private class CategoriesAdapter extends BaseAdapter {

		

		

		@Override
		public int getCount() {
			return mAdapterCategoryList.size();
		}

		@Override
		public String getItem(int position) {
			return mAdapterCategoryList.get(position).categoryName;
		}

		@Override
		public long getItemId(int position) {

			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder = null;
			if (convertView == null) {
				LayoutInflater inflater = getLayoutInflater();
				convertView = inflater.inflate(R.layout.selectcity_row_layout,
						parent, false);
				viewHolder = new ViewHolder();
				viewHolder.categoryTextView = (TextView) convertView
						.findViewById(R.id.area_text_view);
				viewHolder.checkImage = (ImageView) convertView
						.findViewById(R.id.check_image);
				convertView.setTag(viewHolder);
			} else
				viewHolder = (ViewHolder) convertView.getTag();
			viewHolder.categoryTextView.setText(mAdapterCategoryList.get(position).categoryName);

			if (mAdapterCategoryList.get(position).isSelected) {
				viewHolder.checkImage.setVisibility(ImageView.VISIBLE);
				if(!mSelectedCategories.contains(mAdapterCategoryList.get(position).categoryName)){
					mSelectedCategories.add(mAdapterCategoryList.get(position).categoryName);
				}
			} else {
				viewHolder.checkImage.setVisibility(ImageView.GONE);
				if(mSelectedCategories.contains(mAdapterCategoryList.get(position).categoryName)){
					mSelectedCategories.remove(mAdapterCategoryList.get(position).categoryName);
				}
				
			}
			return convertView;
		}

		private class ViewHolder {

			public TextView categoryTextView;
			public ImageView checkImage;
		}

	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
		if(mAdapterCategoryList.get(position).isSelected){
			mAdapterCategoryList.get(position).isSelected = false;
		}else{
			mAdapterCategoryList.get(position).isSelected = true;
		}
		
		mCategoryListAdapter.notifyDataSetChanged();
		
	}

	@Override
	public boolean onQueryTextChange(String text) {
		Log.i("CategoryListActivity", "onQueryTextChange");
		mAdapterCategoryList.clear();
		for(int i=0;i<Categories.mCategoryList.size();i++){
			mAdapterCategoryList.add(Categories.mCategoryList.get(i));
		}
		
		if(text.length()>0){
		updateListView(text);
		}
		else
			mCategoryListAdapter.notifyDataSetChanged();
		return false;
	}

	@Override
	public boolean onQueryTextSubmit(String arg0) {
		return false;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			super.onBackPressed();
			Intent returnIntent = new Intent();
			setResult(RESULT_CANCELED, returnIntent);
			finish();			
			break;
		default:
			return super.onOptionsItemSelected(item);
			
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		Intent returnIntent = new Intent();
		setResult(RESULT_CANCELED, returnIntent);
		finish();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		Log.i("ChainNameActivity", "onResume");
		Calendar calendar  = Calendar.getInstance();		
		mStartTime = calendar.getTimeInMillis();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		Calendar calendar  = Calendar.getInstance();		
		mStopTime = calendar.getTimeInMillis();
		CAActivity.mTimeCalculation +=(int) mStopTime-mStartTime;
	}
	

	@Override
	public void onClick(View view) {
		switch(view.getId()){
		case R.id.done_button:
			Intent returnIntent = new Intent();
			returnIntent.putStringArrayListExtra("SelectedCategories", mSelectedCategories);
			setResult(RESULT_OK, returnIntent);
			finish();
			break;
		default:
			break;
		}
		
	}
	
	

}
