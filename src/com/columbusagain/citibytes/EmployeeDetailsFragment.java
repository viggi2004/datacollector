package com.columbusagain.citibytes;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.columbusagain.citibytes.EmployeeFragment.EmployeeListAdapter;
import com.columbusagain.citibytes.EmployeeFragment.EmployeeListAdapter.ViewHolder;
import com.columbusagain.citibytes.helper.Constants;
import com.columbusagain.citibytes.helper.HttpGet;
import com.columbusagain.citibytes.helper.HttpPost;
import com.columbusagain.citibytes.helper.JsonGetException;
import com.columbusagain.citibytes.helper.Timer;
import com.columbusagain.citibytes.helper.UserProfile;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

public class EmployeeDetailsFragment extends Fragment implements OnQueryTextListener, OnClickListener {
	
	private Activity mActivity;
	
	private ActionBar mActionBar;
	
	private TextView mDisplayNameText,mEmployeeRoleText,mEmployeeContactNumber;
	
	private ArrayList<EmployeeBusinessDetails> mBusinessList = new ArrayList<EmployeeBusinessDetails>();
	
	private ArrayList<EmployeeBusinessDetails> mAdapterBusinessList = new ArrayList<EmployeeBusinessDetails>();
	
	private BusinessListAdapter mBusinessListAdapter;
	
	private TextView mEmptyTextView;
	
	private String mEmployeeEmailId;
	
	private boolean isAdminUser = false;
	
	private final String TAG = "EmployeeDetailsFragment";
	
	private String mListType;
	
	private String mEmailId;
	
	private String mQueryText = "";
	
	private Button mManageRoleButton;
	
	private static Dialog mProgressDialog;
	
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mActivity = activity;
		mActionBar = ((AdminActivity)mActivity).getSupportActionBar();
		mActionBar.setTitle("Employee");
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		 UserProfile.isFromMapScreen = false;
		 mProgressDialog = new Dialog(mActivity, R.style.ProgressDialog);
		 mProgressDialog.setContentView(R.layout.progress_dialog);
		 setHasOptionsMenu(true);
		 View rootView = inflater.inflate(R.layout.fragment_employee, container, false);
		 mDisplayNameText = (TextView) rootView.findViewById(R.id.display_name);
		 mEmployeeRoleText = (TextView) rootView.findViewById(R.id.role);
		 mEmployeeContactNumber = (TextView) rootView.findViewById(R.id.personal_number);
		 mManageRoleButton = (Button) rootView.findViewById(R.id.button_manage_role);
		 ListView listView =  (ListView) rootView.findViewById(R.id.list_view);
		 LinearLayout emptyView = (LinearLayout) rootView.findViewById(R.id.empty_view);
		 mBusinessListAdapter = new BusinessListAdapter(mAdapterBusinessList);
		 mEmptyTextView = (TextView) rootView.findViewById(R.id.empty_text);
		 listView.setAdapter(mBusinessListAdapter);
		 listView.setEmptyView(emptyView);
		 mListType = getArguments().getString("query_type");
		 mEmailId = getArguments().getString("email_id");
		 mManageRoleButton.setOnClickListener(this);
		 Log.i(TAG, mListType);
		 
		 new EmployeeDetailsDownloader().execute();
		return rootView;
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
	    searchAutoComplete.setHint("Enter Business Name");
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
	
	private class EmployeeDetailsDownloader extends AsyncTask<Void, Void, String>{

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mProgressDialog.show();
			mBusinessList.clear();
			Log.i("EmployeeDetailsFragment", "Email : "+mEmailId);
			Log.i("EmployeeDetailsFragment", "Period : "+mListType);
		}
		@Override
		protected String doInBackground(Void... params) {
			HttpPost httpPost = new HttpPost(Constants.BASE_URL+"admin/getUserCollectedBusiness.php");
			httpPost.setParam("email_id", mEmailId);
			httpPost.setParam("period", mListType);
			httpPost.setParam("city", UserProfile.CITY);
			
			return httpPost.executePost();
		}
		
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			mProgressDialog.dismiss();
			if(result != null){
				try {
					JSONObject response_json = new JSONObject(result);
					String status = response_json.getString("status");
					if(status.equalsIgnoreCase("success")){
						Log.i("EmployeeDetailsFragment", result);
						JSONObject user_profile = response_json.getJSONObject("user_profile");
						updateUserProfile(user_profile);
						int count = response_json.getInt("count");
						if(count>0){
							JSONArray business_collected = response_json.getJSONArray("collected_business");
							for(int i=0;i<business_collected.length();i++){
								JSONObject business = business_collected.getJSONObject(i);
								String business_name = business.getString("business_name");
								String business_id = business.getString("business_id");
								addBusiness(business_id, business_name);
							}
							mBusinessListAdapter.notifyDataSetChanged();
							onQueryTextChange(mQueryText);
						}else{
							mEmptyTextView.setText("No Business Found");
							mEmptyTextView.setVisibility(TextView.VISIBLE);
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
			/*addBusiness("0000000001", "Government Elementary School");
			addBusiness("0000000002", "Government High School");
			addBusiness("0000000003", "Municipal Higher Secondary School");
			addBusiness("0000000004", "V.R.S College of Engineering and Technology");
			addBusiness("0000000005", "Wipro");
			addBusiness("0000000006", "Mobiwhiz Technologies");
			addBusiness("0000000006", "American Megatrends Private Ltd");
			addBusiness("0000000006", "ColumbusAgain Software Technologies");
			//mBusinessListAdapter.notifyDataSetChanged();
			onQueryTextChange(mQueryText);
			if(mBusinessList.size() == 0)
				mEmptyTextView.setVisibility(TextView.VISIBLE);*/
		}
		
	}
	
	public void updateUserProfile(JSONObject jsonObject) throws JSONException{
		mEmployeeEmailId = jsonObject.getString("email_id");
		String display_name = jsonObject.getString("display_name");
		isAdminUser= jsonObject.getBoolean("is_admin");
		String contact_number = jsonObject.getString("personal_number");
		
		mDisplayNameText.setText(display_name);
		if(isAdminUser)
		 mEmployeeRoleText.setText("Role : Admin");
		else
			mEmployeeRoleText.setText("Role : Content Associate");
		 mEmployeeContactNumber.setText("Phone No. :"+contact_number);
	}
	
	public void addBusiness(String business_id,String business_name){
		EmployeeBusinessDetails employeeBusinessDetail = new EmployeeBusinessDetails();
		employeeBusinessDetail.business_id = business_id;
		employeeBusinessDetail.business_name = business_name;
		mBusinessList.add(employeeBusinessDetail);
	}
	
	public class EmployeeBusinessDetails{
		public String business_name;
		public String business_id;
	}
	
	private class BusinessListAdapter extends BaseAdapter{
		
		ArrayList<EmployeeBusinessDetails> mEmployeeBusinessList = new ArrayList<EmployeeBusinessDetails>();
		
		BusinessListAdapter(ArrayList<EmployeeBusinessDetails> employee_business_details){
			this.mEmployeeBusinessList = employee_business_details;
		}

		@Override
		public int getCount() {
			return mEmployeeBusinessList.size();
		}

		@Override
		public Object getItem(int position) {
			return mEmployeeBusinessList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			String business_name = mEmployeeBusinessList.get(position).business_name;
			final String business_id = mEmployeeBusinessList.get(position).business_id;
			ViewHolder viewHolder = null;
			if(convertView == null){
				LayoutInflater inflater = (LayoutInflater)mActivity.getSystemService
					      (Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(R.layout.row_fragment_employee,
						parent, false);
				viewHolder = new ViewHolder();
				viewHolder.header_view = (TextView) convertView.findViewById(R.id.header);
				viewHolder.content_view = (TextView) convertView.findViewById(R.id.content);
				convertView.setTag(viewHolder);
			}else{
				viewHolder = (ViewHolder) convertView.getTag();
			}
			if(position == 0){
				viewHolder.header_view.setVisibility(TextView.VISIBLE);
			}else{
				viewHolder.header_view.setVisibility(TextView.GONE);
			}
			if(mListType.equalsIgnoreCase("today")){
				viewHolder.header_view.setText("Business Collected Today");
			}else if(mListType.equalsIgnoreCase("weekly")){
				viewHolder.header_view.setText("Business Collected This Week");
			}else if(mListType.equalsIgnoreCase("monthly")){
				viewHolder.header_view.setText("Business Collected This Month");
			}
			viewHolder.content_view.setText(business_name);
			viewHolder.content_view.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					mProgressDialog.show();
					new DownloadBusinessJson().execute(business_id);
				}
			});
			return convertView;
		}
		
		public class ViewHolder{
			public TextView header_view;
			public TextView content_view;
		}
		
	}
	
	private class DownloadBusinessJson extends AsyncTask<String, Void, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mProgressDialog.show();
		}

		@Override
		protected String doInBackground(String... params) {
			/*if(Constants.PIN == null )
				return null;*/
			String result = null;
			HttpGet httpGet = new HttpGet(Constants.BASE_URL
					+ "getBusinessDetails.php?business_id=" + params[0]);
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
			//if (mProgressDialog.isShowing()) {
				mProgressDialog.dismiss();
			//}
			if (result != null) {
				Log.i(TAG, result);

				try {
					JSONObject responseJson = new JSONObject(result);
					String business_id = responseJson.getString("business_id");
					startEditBusiness(business_id, responseJson);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				Toast.makeText(mActivity, "No Network", Toast.LENGTH_LONG)
						.show();
			}
		}

	}

	private void startEditBusiness(String business_id, JSONObject businessJson) {
		Timer.resetTimer();
		((AdminActivity) mActivity).downloadedJson = businessJson;
		((AdminActivity) mActivity).mBusinessId = business_id;
		((AdminActivity) mActivity).editBusinessFragment();
		Constants.isEditBusinessClicked = true;

	}

	@Override
	public boolean onQueryTextChange(String queryText) {
		mQueryText = queryText;
		Log.i(TAG, "onQueryTextChange CALLED");
		mAdapterBusinessList.clear();
		for(int i=0;i<mBusinessList.size();i++)
			if(queryText.length() == 0 || mBusinessList.get(i).business_name.toLowerCase().startsWith(queryText.toLowerCase()) )
				mAdapterBusinessList.add(mBusinessList.get(i));		
		if(mBusinessListAdapter != null)
			mBusinessListAdapter.notifyDataSetChanged();
		return false;
	}

	@Override
	public boolean onQueryTextSubmit(String arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.button_manage_role:
			final RadioButton admin_button,content_associate_button;
			final Dialog manage_role_dialog = new Dialog(mActivity);
			manage_role_dialog
			.requestWindowFeature(Window.FEATURE_NO_TITLE);
			manage_role_dialog
			.setContentView(R.layout.custom_dialog_manage_role);
			admin_button = (RadioButton) manage_role_dialog.findViewById(R.id.admin);
			content_associate_button = (RadioButton) manage_role_dialog.findViewById(R.id.content_associate);
			if(isAdminUser)
				admin_button.setChecked(true);
			else
				content_associate_button.setChecked(true);
			Button done_button = (Button) manage_role_dialog.findViewById(R.id.done_button);
			done_button.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
					if(admin_button.isChecked()){
						if(!isAdminUser)
						new ManageRole().execute(true);
					}else if(content_associate_button.isChecked()){
						if(isAdminUser)
						new ManageRole().execute(false);
					}
					manage_role_dialog.dismiss();
				}
			});
			manage_role_dialog.show();
			break;
		}
		
	}
	
	private class ManageRole extends AsyncTask<Boolean, Void, String>{

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mProgressDialog.show();
		}
		
		@Override
		protected String doInBackground(Boolean... params) {
			HttpPost httpPost = new HttpPost(Constants.BASE_URL+"admin/changeRole.php");
			httpPost.setParam("email_id",String.valueOf(mEmployeeEmailId));
			httpPost.setParam("is_admin", String.valueOf(params[0]));
			return httpPost.executePost();
		}
		
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			mProgressDialog.dismiss();
			if(result != null){
				Log.i(TAG, "Manage Role : "+result);
				try {
					JSONObject responseJson = new JSONObject(result);
					String status = responseJson.getString("status");
					if(status.equalsIgnoreCase("success")){
						new EmployeeDetailsDownloader().execute();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
	}

}
