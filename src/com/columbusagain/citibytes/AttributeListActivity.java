package com.columbusagain.citibytes;



import java.util.ArrayList;

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
import com.columbusagain.citibytes.helper.Timer;

public class AttributeListActivity extends ActionBarActivity implements OnItemClickListener, OnClickListener, OnQueryTextListener {
	
	private Button mDoneButton;
	
	private ArrayList<String> mSelectedValues = new ArrayList<String>();
	
	private ArrayList<AllowedValues> mAdapterValues = new ArrayList<AllowedValues>();
	
	private boolean isSingleSelectionListView;
	
	private ArrayList<AllowedValues> mAllowedValues = new ArrayList<AllowedValues>();
	
	private ActionBar mActionbar;

	private Context mContext = this;
	
	private int mCurrentSelectedIndex = -1;

	private ListView mAttributesListView;

	private CategoriesAdapter mAttributesListAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_category_list);
		
		Intent intent = getIntent();
		String title = intent.getStringExtra("title");
		isSingleSelectionListView = intent.getBooleanExtra("is_single_selection_list", false);
		//selectedValue = intent.getStringExtra("selected_value");
		mSelectedValues = intent.getStringArrayListExtra("selected_values");
		//mAdapterValues = intent.getStringArrayListExtra("allowed_values");
		mActionbar = getSupportActionBar();
		mActionbar.setTitle(title);
		mActionbar.setDisplayHomeAsUpEnabled(true);
        mActionbar.setHomeButtonEnabled(true);
        mActionbar.setLogo(R.drawable.logo_navbar);
        
        mDoneButton = (Button) findViewById(R.id.done_button);
        
        ArrayList<String> listViewValues = intent.getStringArrayListExtra("allowed_values");
        initListData(listViewValues);
       
		mAttributesListView = (ListView) findViewById(R.id.category_list_view);
		mAttributesListAdapter = new CategoriesAdapter();
		mAttributesListView.setAdapter(mAttributesListAdapter);
		mAttributesListView.setOnItemClickListener(this);
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
		MenuItemCompat.setOnActionExpandListener(searchItem,
	            new MenuItemCompat.OnActionExpandListener() {

	    	@Override
			public boolean onMenuItemActionCollapse(MenuItem arg0) {
				 // Do something when collapsed
		    	   Log.i("HomeFragment", "onMenuItemActionCollapse");
		    	   updateListView("");
		            return true;       // Return true to collapse action view
			}
			@Override
			public boolean onMenuItemActionExpand(MenuItem arg0) {
				Log.i("HomeFragment", "onMenuItemActionExpand");
	            // Do something when expanded
	            return true;      // Return true to expand action view
			}

	});
		return super.onCreateOptionsMenu(menu);
		
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
		case android.R.id.home:
			Intent returnIntent = new Intent();
			setResult(RESULT_CANCELED, returnIntent);
			finish();
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void initListData(ArrayList<String> listViewValues){
		
		
        for (int i = 0; i < listViewValues.size(); i++) {
        	String currentValue = listViewValues.get(i);
        	AllowedValues allowedValue = new AllowedValues();
        	allowedValue.value = currentValue;
        	allowedValue.isSelected = false;
        	
        	mAllowedValues.add(allowedValue);
        		
        	}
			
		for (int i = 0; i < listViewValues.size(); i++) {
			AllowedValues allowedValue = mAllowedValues.get(i);		
			String currentValue = allowedValue.value;	        		
        		for (int j = 0; j < mSelectedValues.size(); j++) {
        			String selectedValue = mSelectedValues.get(j);
        			if(currentValue.equalsIgnoreCase(selectedValue)){
            			allowedValue.isSelected = true;
            			mCurrentSelectedIndex = i;
            			break;
        			}
        			
				}
		}
		mAdapterValues.clear();
		for(int i=0;i<mAllowedValues.size();i++){
			mAdapterValues.add(mAllowedValues.get(i));
		}
		
	}
	
	
	
	private class CategoriesAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return mAdapterValues.size();
		}

		@Override
		public String getItem(int position) {
			return mAdapterValues.get(position).value;
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
			String value = mAdapterValues.get(position).value;
			boolean isSelected = mAdapterValues.get(position).isSelected;
			viewHolder.categoryTextView.setText(value);
			if(isSelected)
				viewHolder.checkImage.setVisibility(ImageView.VISIBLE);
			else
				viewHolder.checkImage.setVisibility(ImageView.GONE);
			
				
			return convertView;
		}

		private class ViewHolder {

			public TextView categoryTextView;
			public ImageView checkImage;
		}

	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
		//TextView selectedText = (TextView) view.findViewById(R.id.area_text_view);
		//String selectedValue = selectedText.getText().toString();
		if(isSingleSelectionListView){
			deselectAll();
			//if(mCurrentSelectedIndex != -1)
			//mAdapterValues.get(mCurrentSelectedIndex).isSelected = false;
			mAdapterValues.get(position).isSelected = true;
			mCurrentSelectedIndex = position;
		}else{
			mAdapterValues.get(position).isSelected = ! mAdapterValues.get(position).isSelected;
		}
				
		
		mAttributesListAdapter.notifyDataSetChanged();
		
	}
	
	private void deselectAll(){
		for(int i=0;i<mAllowedValues.size();i++){
			mAllowedValues.get(i).isSelected = false;
		}
		mAttributesListAdapter.notifyDataSetChanged();
	}
	
	private ArrayList<String> getSelectedValues(){
		ArrayList<String> selectedValues = new ArrayList<String>();
		for (int i = 0; i < mAllowedValues.size(); i++) {
			if(mAllowedValues.get(i).isSelected)
			selectedValues.add(mAllowedValues.get(i).value);
		}
		return selectedValues;
	}

	
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		Intent returnIntent = new Intent();
		setResult(RESULT_CANCELED, returnIntent);
		finish();
	}
	
	

	@Override
	public void onClick(View view) {
		switch(view.getId()){
		case R.id.done_button:
			Intent returnIntent = new Intent();
			if(isSingleSelectionListView)
			returnIntent.putExtra("selected_value", mAllowedValues.get(mCurrentSelectedIndex).value);
			else
				returnIntent.putStringArrayListExtra("selected_values", getSelectedValues());
			setResult(RESULT_OK, returnIntent);
			finish();
			break;
		default:
			break;
		}
		
	}
	
	private class AllowedValues{
		private String value;
		private boolean isSelected;
	}

	@Override
	public boolean onQueryTextChange(String text) {
		updateListView(text);
		return false;
	}

	@Override
	public boolean onQueryTextSubmit(String text) {
		return false;
	}
	
	private void updateListView(String queryText){
		mAdapterValues.clear();
		if(queryText.length()==0){
			for(int i=0;i<mAllowedValues.size();i++){
				mAdapterValues.add(mAllowedValues.get(i));
			}			
			
		}else{
			for(int i=0;i<mAllowedValues.size();i++){
				if(mAllowedValues.get(i).value.toLowerCase().contains(queryText.toLowerCase()))
				mAdapterValues.add(mAllowedValues.get(i));
			}	
		}
		
		mAttributesListAdapter.notifyDataSetChanged();
	}
	
	
	@Override
	protected void onResume() {
		super.onResume();
		Timer.startTimer();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		Timer.stopTimer();
	}
	

}
