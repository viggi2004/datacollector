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

public class AttributeMultiValueListActivity extends ActionBarActivity implements OnItemClickListener, OnClickListener {
	
	private Button mDoneButton;
	
	private ArrayList<String> mSelectedValues = new ArrayList<String>();
	
	private ArrayList<String> mAdapterValues = new ArrayList<String>();
	
	private ActionBar mActionbar;

	private Context mContext = this;
	
	private String selectedValue = "";
	
	//private ArrayList<Categories> mAdapterValues = new ArrayList<Categories>();

	private ListView mAttributesListView;

	private CategoriesAdapter mAttributesListAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_category_list);
		
		Intent intent = getIntent();
		String title = intent.getStringExtra("title");
		mSelectedValues = intent.getStringArrayListExtra("selected_value");
		mAdapterValues = intent.getStringArrayListExtra("allowed_values");
		mActionbar = getSupportActionBar();
		mActionbar.setTitle(title);
		mActionbar.setDisplayHomeAsUpEnabled(true);
        mActionbar.setHomeButtonEnabled(true);
        mActionbar.setLogo(R.drawable.logo_navbar);
        
       
        
        mDoneButton = (Button) findViewById(R.id.done_button);
       
		mAttributesListView = (ListView) findViewById(R.id.category_list_view);
		mAttributesListAdapter = new CategoriesAdapter();
		mAttributesListView.setAdapter(mAttributesListAdapter);
		mAttributesListView.setOnItemClickListener(this);
		mDoneButton.setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		return false;
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
	
	
	
	private class CategoriesAdapter extends BaseAdapter {

		

		

		@Override
		public int getCount() {
			return mAdapterValues.size();
		}

		@Override
		public String getItem(int position) {
			return mAdapterValues.get(position);
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
			String value = mAdapterValues.get(position);
			viewHolder.categoryTextView.setText(value);
			for (int i = 0; i < mSelectedValues.size(); i++) {
				if(mSelectedValues.get(i).equalsIgnoreCase(value)){
					viewHolder.checkImage.setVisibility(ImageView.VISIBLE);
					break;
				}else{
					viewHolder.checkImage.setVisibility(ImageView.GONE);
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
		TextView selectedText = (TextView) view.findViewById(R.id.area_text_view);
		ImageView mSelectedValues = (ImageView) view.findViewById(R.id.check_image);
		
		selectedValue = selectedText.getText().toString();
		
		/*for (int i = 0; i < mSelectedValues; i++) {
			
			
		}*/
		
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
			returnIntent.putExtra("selected_value", selectedValue);
			setResult(RESULT_OK, returnIntent);
			finish();
			break;
		default:
			break;
		}
		
	}
	
	

}
