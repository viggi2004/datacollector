package com.columbusagain.citibytes;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.columbusagain.citibytes.helper.Constants;
import com.columbusagain.citibytes.helper.HttpGet;
import com.columbusagain.citibytes.helper.JsonGetException;
import com.columbusagain.citibytes.util.NetworkChecker;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ChainNamesActivity extends ActionBarActivity implements
		OnQueryTextListener, OnItemClickListener, OnClickListener {
	
	private int mTimeDuration;
	
	private long mStartTime,mStopTime;
	
	private RelativeLayout mEmptyView;
	
	private Dialog mProgressDialog;

	private ActionBar mActionbar;

	private Context mContext = this;

	private String[] mChainNames;

	private ListView mChainListView;
	
	public String mQueryText = "";

	private ChainNamesAdapter mChainNamesAdapter;

	private ArrayList<String> mChainNamesList = new ArrayList<String>();
	
	private ArrayList<String> mDefaultChainNames = new ArrayList<String>();
	
	private Button mAddChainNameButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chain_names);

		mActionbar = getSupportActionBar();
		mActionbar.setTitle("Chain Name");
		mActionbar.setDisplayHomeAsUpEnabled(true);
		mActionbar.setHomeButtonEnabled(true);
		mActionbar.setLogo(R.drawable.logo_navbar);
		
		mAddChainNameButton = (Button) findViewById(R.id.add_chain_name);
		mEmptyView = (RelativeLayout) findViewById(R.id.empty_view);
		mChainListView = (ListView) findViewById(R.id.chain_list_view);
		
		mProgressDialog = new Dialog(this,R.style.ProgressDialog);
		mProgressDialog.setContentView(R.layout.progress_dialog);
		
		boolean isNetworkConnected = NetworkChecker.isConnected(this);
		if(!isNetworkConnected){
			Toast.makeText(getApplicationContext(), "No Internet!", Toast.LENGTH_LONG).show();
		}else{
			mProgressDialog.show();
			new ChainNamesDownloader().execute();
		}
		
		

		/*if (Constants.CHAIN_NAMES == null || Constants.CHAIN_NAMES.size() == 0) {
			Log.i("ChainNamesActivity", "chain names null");
			mChainNames = getResources().getStringArray(R.array.chain_names);
			for (int i = 0; i < mChainNames.length; i++) {
				Constants.CHAIN_NAMES.add(mChainNames[i]);
			}

		}
		for(int i=0;i<Constants.CHAIN_NAMES.size();i++)
		mChainNamesList.add(Constants.CHAIN_NAMES.get(i));*/
		
		Log.i("ChainNamesActivity", ""+mChainNamesList.size());

		//mChainNames = getResources().getStringArray(R.array.chain_names);
		/*mChainNamesAdapter = new ChainNamesAdapter();
		mChainListView.setEmptyView(mEmptyView);
		mChainListView.setAdapter(mChainNamesAdapter);
		mChainListView.setOnItemClickListener(this);*/
		mAddChainNameButton.setOnClickListener(this);

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

	private void updateListView(String text) {
		for (int i = 0; i < mChainNamesList.size(); i++) {
			if (mChainNamesList.get(i).toUpperCase().startsWith(text.toUpperCase())) {
				//newAreaList.add(mDefaultChainNames[i]);
			}else{
				mChainNamesList.remove(i);
				i--;
			}

		}
		if(mChainNamesAdapter != null)
		mChainNamesAdapter.notifyDataSetChanged();
		/*ArrayList<String> newAreaList = new ArrayList<String>();

		// ArrayList<String> newList = new ArrayList<String>();
		for (int i = 0; i < mChainNames.length; i++) {
			if (mChainNames[i].startsWith(text.toUpperCase())) {
				newAreaList.add(mChainNames[i]);
			}

		}
		String[] newList = new String[newAreaList.size()];
		for (int i = 0; i < newAreaList.size(); i++) {
			newList[i] = newAreaList.get(i);
		}
		mChainListView.setAdapter(new ChainNamesAdapter(newList, -1));*/
		return;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.select_city, menu);
		MenuItem searchItem = menu.findItem(R.id.action_search);
		SearchView searchView = (SearchView) MenuItemCompat
				.getActionView(searchItem);
		SearchView.SearchAutoComplete searchAutoComplete = (SearchView.SearchAutoComplete) searchView
				.findViewById(android.support.v7.appcompat.R.id.search_src_text);
		/*SearchView.SearchAutoComplete searchAutoComplete = (SearchView.SearchAutoComplete) searchView
				.findViewById(R.layout.search_view_edit_text);*/

		LinearLayout searchPlate = (LinearLayout) searchView
				.findViewById(android.support.v7.appcompat.R.id.search_plate);
		ImageView close_button = (ImageView) searchView
				.findViewById(android.support.v7.appcompat.R.id.search_close_btn);
		close_button.setImageResource(R.drawable.cross);
		searchPlate.setBackgroundResource(R.drawable.nav_search);
		searchAutoComplete.setCompoundDrawablesWithIntrinsicBounds(
				R.drawable.action_search, 0, 0, 0);
		searchAutoComplete.setHint("Chain Name");
		searchAutoComplete.setHintTextColor(getResources().getColor(
				R.color.searchbar_hint));
		searchAutoComplete.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
		searchAutoComplete.setTextColor(Color.WHITE);
		searchView.setOnQueryTextListener(this);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onQueryTextChange(String text) {
		// TODO Auto-generated method stub
		//updateListView(text);
		mQueryText = text;
		mChainNamesList.clear();
		for(int i=0;i<mDefaultChainNames.size();i++){
			mChainNamesList.add(mDefaultChainNames.get(i));
		}
		
		if(text.length()>0){
			updateListView(text);
			}
			else{
				if(mChainNamesAdapter != null)
				mChainNamesAdapter.notifyDataSetChanged();
				
			}
		
		
		
		return false;
	}

	@Override
	public boolean onQueryTextSubmit(String text) {
		// TODO Auto-generated method stub
		return false;
	}

	private class ChainNamesAdapter extends BaseAdapter {


		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mChainNamesList.size();
		}

		@Override
		public String getItem(int position) {
			// TODO Auto-generated method stub
			return mChainNamesList.get(position);
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
				viewHolder.areaTextView = (TextView) convertView
						.findViewById(R.id.area_text_view);
				viewHolder.checkImage = (ImageView) convertView
						.findViewById(R.id.check_image);
				convertView.setTag(viewHolder);
			} else
				viewHolder = (ViewHolder) convertView.getTag();
			viewHolder.areaTextView.setText(mChainNamesList.get(position));

			/*if (position == selectedPosition) {
				viewHolder.checkImage.setVisibility(ImageView.VISIBLE);
			} else {
				viewHolder.checkImage.setVisibility(ImageView.GONE);
			}*/
			return convertView;
		}

		private class ViewHolder {

			public TextView areaTextView;
			public ImageView checkImage;
		}

	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		Intent returnIntent = new Intent();
		setResult(RESULT_CANCELED, returnIntent);
		finish();
	}

	@Override
	public void onItemClick(AdapterView<?> adapter, View view, int posiotion,
			long id) {
		TextView tv = (TextView) view.findViewById(R.id.area_text_view);
		Intent returnIntent = new Intent();
		returnIntent.putExtra("name", tv.getText().toString());
		setResult(RESULT_OK, returnIntent);
		finish();

	}

	@Override
	public void onClick(View view) {
		switch(view.getId()){
		case R.id.add_chain_name:
			final Dialog mAddChainNameDialog = new Dialog(mContext);
			mAddChainNameDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			mAddChainNameDialog.setContentView(R.layout.custom_dialog_add_chain_names);
			final EditText chain_name_input = (EditText) mAddChainNameDialog.findViewById(R.id.chain_name_input);
			chain_name_input.setText(mQueryText);
			Button add_chain_name = (Button) mAddChainNameDialog.findViewById(R.id.add_chain_name);
			Button cancel_chain_name_request = (Button) mAddChainNameDialog.findViewById(R.id.cancel_chain_name_request);
			
			add_chain_name.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					String input_name = chain_name_input.getText().toString();
					try {
						input_name = URLEncoder.encode(input_name, "UTF-8");
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					new AddChainName().execute(input_name);
					//Constants.CHAIN_NAMES.add(input_name);					
					mAddChainNameDialog.cancel();
					//mChainNamesList.add(input_name);
					//mChainNamesAdapter.notifyDataSetChanged();
					
				}
			});
			
			cancel_chain_name_request.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					mAddChainNameDialog.cancel();
				}
			});
			mAddChainNameDialog.show();
			break;
		default:
			break;
		}
		
	}
	
	private class ChainNamesDownloader extends AsyncTask<Void, Void, String>{

		@Override
		protected String doInBackground(Void... params) {
			String result = null;
			HttpGet httpGet = new HttpGet(Constants.BASE_URL+"getChainList.php");
			try {
				result = httpGet.executeGet();
			} catch (JsonGetException e) {
				e.printStackTrace();
			}
			return result;
		}
		
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if(mProgressDialog.isShowing()){
				mProgressDialog.cancel();
			}
			if(result !=null){
				Log.i("ChainNamesActivity", "Response: "+result);
				try {
					JSONObject chainNamesObject = new JSONObject(result);
					String status = chainNamesObject.getString("status");
					if("success".equals(status)){
						JSONArray chain_names_array = chainNamesObject.getJSONArray("chain_list");
						for(int i=0;i<chain_names_array.length();i++){
							mDefaultChainNames.add(chain_names_array.getString(i));
						}
						
						initializeChainNamesListView();
					}else{
						Toast.makeText(mContext, status, Toast.LENGTH_LONG).show();
					}
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else{
				Toast.makeText(mContext, "No network!", Toast.LENGTH_LONG).show();
			}
		}
		
		
	}
	
	private class AddChainName extends AsyncTask<String, Void, String>{

		@Override
		protected String doInBackground(String... params) {
			String result = null;
			HttpGet httpGet = new HttpGet(Constants.BASE_URL+"addNewChain.php?chain_name="+params[0]);
			try {
				result = httpGet.executeGet();
			} catch (JsonGetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return result;
		}
		
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if(mProgressDialog.isShowing()){
				mProgressDialog.cancel();
			}
			
			if(result != null){
				JSONObject addChainNameObject;
				try {
					addChainNameObject = new JSONObject(result);
					String status = addChainNameObject.getString("status");
					if("success".equals(status)){
						Toast.makeText(mContext, "Chain name added successfully", Toast.LENGTH_LONG).show();
						new ChainNamesDownloader().execute();
					}else{
						String error = addChainNameObject.getString("error");
						Toast.makeText(mContext, error, Toast.LENGTH_LONG).show();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
			}else{
			Toast.makeText(mContext, "No Network!", Toast.LENGTH_LONG).show();
			}
		}
		
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
		Log.i("ChainNameActivity", "onPause");
		Calendar calendar  = Calendar.getInstance();		
		mStopTime = calendar.getTimeInMillis();
		CAActivity.mTimeCalculation +=(int) mStopTime-mStartTime;
	}
	
	private void initializeChainNamesListView(){
		
		
		
		mChainNamesAdapter = new ChainNamesAdapter();
		mChainListView.setEmptyView(mEmptyView);
		mChainListView.setAdapter(mChainNamesAdapter);
		mChainListView.setOnItemClickListener(this);
		onQueryTextChange(mQueryText);
	}
	

}
