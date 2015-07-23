package com.columbusagain.citibytes;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.OnNavigationListener;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnCloseListener;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MenuItem.OnActionExpandListener;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.columbusagain.citibytes.helper.BusinessDetails;
import com.columbusagain.citibytes.helper.BusinessList;
import com.columbusagain.citibytes.helper.Constants;
import com.columbusagain.citibytes.helper.HttpGet;
import com.columbusagain.citibytes.helper.HttpPost;
import com.columbusagain.citibytes.helper.JsonGetException;
import com.columbusagain.citibytes.helper.Timer;
import com.columbusagain.citibytes.helper.UserProfile;
import com.columbusagain.citibytes.util.NetworkChecker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class HomeFragment extends Fragment implements OnNavigationListener,
		OnQueryTextListener, OnInfoWindowClickListener, OnItemClickListener {

	private static final int BUSINESS_VERIFIED_COMPLETE = 001;

	private static final int TRANSIENT = 002;

	private static final int BUSINESS_VERIFIED_INCOMPLETE = 003;

	private ListView mBusinessListView;
	
	private LinearLayout mHeaderLayout;
	
	private TextView mBusinessCount;

	private BusinessListAdapter mBusinessListAdapter;

	private Menu optionsMenu;
	
	private String mPeriod;

	// private static String BUSINESS_COMPLETE_EDITABLE = "TRANSIENT";

	// private static String BUSINESS_COMPLETE_NON_EDITABLE =
	// "BUSINESS_VERIFIED_COMPLETE";

	// private static String BUSINESS_INCOMPLETE_EDITABLE =
	// "BUSINESS_VERIFIED_INCOMPLETE";

	private BusinessList mBusinessList;

	private ArrayList<BusinessDetails> mBusinessDetails = new ArrayList<BusinessDetails>();

	private ArrayList<BusinessDetails> mAdapterBusinessList = new ArrayList<BusinessDetails>();

	private String mNorthEastLat, mNorthEastLng, mSouthWestLat, mSouthWestLng;

	public static double northeast_lat, northeast_lng, southwest_lat,
			southwest_lng;

	MenuItem mSearchItem;

	MenuItem mRefreshItem;

	private static Dialog mProgressDialog;

	// private LinearLayout mMainLayout;

	private Activity mActivity;

	private Context mContext;

	private String mPin;

	private GoogleMap map;

	private ActionBar mActionBar;

	private View mRootView;

	private FrameLayout mMapFrame, mListFrame;

	private SupportMapFragment mMapFragment;

	private boolean isFirstTime = true;

	public HomeFragment() {

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		UserProfile.isFromMapScreen = true;
		mProgressDialog = new Dialog(mActivity, R.style.ProgressDialog);
		mProgressDialog.setContentView(R.layout.progress_dialog);
		//if(!UserProfile.isAdmin)
		setHasOptionsMenu(true);
		if(UserProfile.isAdmin)
			mActionBar = ((AdminActivity) mActivity).getSupportActionBar();
		else
			mActionBar = ((CAActivity) mActivity).getSupportActionBar();
		super.onCreateView(inflater, container, savedInstanceState);
		/*
		 * View rootView = inflater.inflate(R.layout.fragment_home, container,
		 * false);
		 */
		if (Constants.FLAG == 0 || Constants.view == null) {
			mRootView = inflater.inflate(R.layout.fragment_home, container,
					false);
			Constants.view = mRootView;
			Constants.FLAG = 1;
			Constants.isEditBusinessClicked = false;
		} else {
			mRootView = Constants.view;
		}

		if (mRootView != null) {
			ViewGroup parent = (ViewGroup) mRootView.getParent();
			if (parent != null)
				parent.removeView(mRootView);
		}
		
		mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		/*
		 * SpinnerAdapter mSpinnerAdapter =
		 * ArrayAdapter.createFromResource(mContext, R.array.navigation_list,
		 * R.layout.action_bar_spinner_text);
		 */
		mActionBar.setDisplayShowTitleEnabled(false);
		mActionBar.setListNavigationCallbacks(new MySpinnerAdapter(), this);

		mMapFrame = (FrameLayout) mRootView.findViewById(R.id.map_layout);
		mListFrame = (FrameLayout) mRootView.findViewById(R.id.list_layout);

		mBusinessListView = (ListView) mRootView.findViewById(R.id.list_view);
		mHeaderLayout = (LinearLayout) mRootView.findViewById(R.id.header_layout);
		mBusinessCount = (TextView) mRootView.findViewById(R.id.business_count);
		mBusinessListAdapter = new BusinessListAdapter();
		mBusinessListView.setAdapter(mBusinessListAdapter);
		if(UserProfile.isAdmin)
			mPeriod = getArguments().getString("period");
		//if(!UserProfile.isAdmin)
		mBusinessListView.setOnItemClickListener(this);
		if (map == null) {

			mMapFragment = SupportMapFragment.newInstance();
			mMapFragment = ((SupportMapFragment) getFragmentManager()
					.findFragmentById(R.id.map));
			// Log.i("MapFragment","map is null");
			map = mMapFragment.getMap();
		} else {
			Log.i("MapFragment", "map is not null");
		}
		map.clear();
		// mMainLayout = (LinearLayout)
		// mRootView.findViewById(R.id.main_layout);
		showMap();
		mPin = Constants.PIN;
		// mArea = Constants.AREA;
		mActionBar.setTitle(mPin);
		
		if( northeast_lat == 0 && northeast_lng == 0 && southwest_lat== 0 && southwest_lng == 0){
		boolean isNetworkConnected = NetworkChecker.isConnected(getActivity());
		if (!isNetworkConnected) {
		} else {
			mProgressDialog.show();
			new FindBoundaries().execute();
			
		}
		}else{
			init();
		}
		// }
		if(UserProfile.isAdmin)
			 new DownloadAllBusiness().execute();
		map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
		map.setIndoorEnabled(false);
		map.setMyLocationEnabled(true);
		return mRootView;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mActivity = activity;
		
		this.mContext = activity;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		//mActionBar.
		mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
	}

	@Override
	public void onDetach() {
		super.onDetach();
		//mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		//if(UserProfile.isAdmin)
			//inflater.inflate(R.menu.home, menu);
		//else{
		inflater.inflate(R.menu.map_fragment, menu);
		this.optionsMenu = menu;
		// mSearchItem = menu.getItem(0);
		mRefreshItem = menu.findItem(R.id.action_refresh);
		if(UserProfile.isAdmin){
			menu.getItem(1).setVisible(false);
			menu.getItem(2).setVisible(false);
		}
		else
		if (isFirstTime) {
			onOptionsItemSelected(mRefreshItem);
			isFirstTime = false;
		}

		mSearchItem = menu.findItem(R.id.action_search);
		SearchView searchView = (SearchView) MenuItemCompat
				.getActionView(mSearchItem);
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
		searchAutoComplete.setHint("Search Address");
		searchAutoComplete.setHintTextColor(getResources().getColor(
				R.color.searchbar_hint));
		searchAutoComplete.setTextColor(Color.WHITE);
		searchView.setOnQueryTextListener(this);
		MenuItemCompat.setOnActionExpandListener(mSearchItem,
				new MenuItemCompat.OnActionExpandListener() {

					@Override
					public boolean onMenuItemActionCollapse(MenuItem arg0) {
						// Do something when collapsed
						Log.i("HomeFragment", "onMenuItemActionCollapse");
						restoreAdapterData();
						return true; // Return true to collapse action view
					}

					@Override
					public boolean onMenuItemActionExpand(MenuItem arg0) {
						Log.i("HomeFragment", "onMenuItemActionExpand");
						// Do something when expanded
						return true; // Return true to expand action view
					}

				});
		/*
		 * mSearchItem.setOnActionExpandListener(new OnActionExpandListener() {
		 * 
		 * @Override public boolean onMenuItemActionCollapse(MenuItem arg0) { //
		 * Do something when collapsed Log.i("HomeFragment",
		 * "onMenuItemActionCollapse"); restoreAdapterData(); return true; //
		 * Return true to collapse action view }
		 * 
		 * @Override public boolean onMenuItemActionExpand(MenuItem arg0) {
		 * Log.i("HomeFragment", "onMenuItemActionExpand"); // Do something when
		 * expanded return true; // Return true to expand action view } });
		 */
		if (mMapFrame != null) {
			if (mMapFrame.isShown()) {
				mRefreshItem.setVisible(true);
				mSearchItem.setVisible(false);
			} else {
				mSearchItem.setVisible(true);
				mRefreshItem.setVisible(false);

			}
		} else {
			mSearchItem.setVisible(false);
			mRefreshItem.setVisible(true);
		}
		//}
		super.onCreateOptionsMenu(menu, inflater);
	}

	public void setRefreshActionButtonState(final boolean refreshing) {
		Log.i("LocationDetailsFragment", "setRefreshActionButtonState");
		if (optionsMenu != null) {
			final MenuItem refreshItem = optionsMenu
					.findItem(R.id.action_refresh);
			if (refreshItem != null) {
				if (refreshing) {
					MenuItemCompat.setActionView(refreshItem,
							R.layout.custom_menu_item_progress);
					// refreshItem.setActionView(R.layout.custom_menu_item_progress);
				} else {
					MenuItemCompat.setActionView(refreshItem, null);
					// refreshItem.setActionView(null);
				}
			}
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case R.id.action_refresh:
			boolean isNetworkConnected = NetworkChecker
					.isConnected(getActivity());
			if (!isNetworkConnected) {
			} else {
				if (map != null)
					map.clear();
				setRefreshActionButtonState(true);
				mProgressDialog.show();
				new DownloadAllBusiness().execute();
			}

			// Complete with your code
			return true;
		case R.id.action_edit:
			Timer.resetTimer();
			((CAActivity) mActivity).downloadedJson = new JSONObject();
			((CAActivity) mActivity).mBusinessId = null;
			((CAActivity) mActivity).editBusinessFragment();
			Constants.isEditBusinessClicked = true;
			break;

		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onNavigationItemSelected(int position, long itemId) {

		if (position == 0) {
			showMap();
		} else {
			showList();
		}

		return false;
	}

	private void showMap() {
		mMapFrame.setVisibility(FrameLayout.VISIBLE);
		mListFrame.setVisibility(FrameLayout.GONE);
		if (mSearchItem != null)
			mSearchItem.setVisible(false);
		if (mRefreshItem != null)
			mRefreshItem.setVisible(true);
	}

	private void showList() {
		mMapFrame.setVisibility(FrameLayout.GONE);
		mListFrame.setVisibility(FrameLayout.VISIBLE);
		if (mSearchItem != null)
			mSearchItem.setVisible(true);
		if (mRefreshItem != null)
			mRefreshItem.setVisible(false);
	}

	private class FindBoundaries extends AsyncTask<Void, Void, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();

		}

		@Override
		protected String doInBackground(Void... params) {
			if(Constants.PIN == null )
				return null;
			String response = null;
			HttpGet httpGet_st = new HttpGet(
					"http://maps.googleapis.com/maps/api/geocode/json?components=postal_code:"
							+ mPin + "&sensor=false&region=in");

			try {
				response = httpGet_st.executeGet();
			} catch (JsonGetException e) {
				e.printStackTrace();
				if (e.toString().equals("Url error!")
						|| e.toString().equals("open connection error!")
						|| e.toString().equals("connect error!")) {
					response = "No Network";
				}
			}
			return response;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (mProgressDialog.isShowing()) {
				mProgressDialog.dismiss();
			}
			if (result != null && !(result.equals("No Network"))) {

				try {
					JSONObject json_obj = new JSONObject(result);
					String status = json_obj.getString("status");
					if (status.equals("OK")) {
						JSONArray result_arr = json_obj.getJSONArray("results");
						JSONObject result_obj = result_arr.getJSONObject(0);
						JSONObject geoMetry = result_obj
								.getJSONObject("geometry");
						JSONObject bounds = geoMetry.getJSONObject("bounds");
						JSONObject northeast = bounds
								.getJSONObject("northeast");
						JSONObject soutwest = bounds.getJSONObject("southwest");
						// JSONObject my_loc =
						// geoMetry.getJSONObject("location");

						// mLatitude=my_loc.getString("lat");
						// mLongitude=my_loc.getString("lng");
						mSouthWestLat = soutwest.getString("lat");
						mSouthWestLng = soutwest.getString("lng");
						mNorthEastLat = northeast.getString("lat");
						mNorthEastLng = northeast.getString("lng");

						// latitude = Double.valueOf(mLatitude);
						// longitude = Double.valueOf(mLongitude);
						northeast_lat = Double.valueOf(mNorthEastLat);
						northeast_lng = Double.valueOf(mNorthEastLng);
						southwest_lat = Double.valueOf(mSouthWestLat);
						southwest_lng = Double.valueOf(mSouthWestLng);
						init();

					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} else {
				Toast.makeText(mContext, "No network", Toast.LENGTH_LONG).show();
				// mNotification.setVisibility(TextView.VISIBLE);
				// mNotification.setText("Network connection error");
			}
		}

	}

	protected void init() {
		int layout_height = mMapFrame.getHeight();
		int layout_width = mMapFrame.getWidth();
		if (map != null) {
			//if(!UserProfile.isAdmin)
			map.setOnInfoWindowClickListener(this);
			map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
			map.setIndoorEnabled(false);
			map.setMyLocationEnabled(true);
			LatLngBounds MYBOUNDARIES = new LatLngBounds(new LatLng(
					southwest_lat, southwest_lng), new LatLng(northeast_lat,
					northeast_lng));

			// LatLng myplace = new LatLng(latitude, longitude);
			/*
			 * Marker marker = map.addMarker(new MarkerOptions()
			 * .position(myplace) .title("unknown place") .snippet("unknown")
			 * .icon(BitmapDescriptorFactory .fromResource(R.drawable.mappin)));
			 */
			map.animateCamera(CameraUpdateFactory.newLatLngBounds(MYBOUNDARIES,
					layout_width, layout_height, 1));
		}

	}

	@Override
	public boolean onQueryTextChange(String text) {
		if (mBusinessListAdapter != null) {
			if (text.length() == 0)
				restoreAdapterData();
			else
				filterAdapterData(text);
		}

		return false;
	}

	@Override
	public boolean onQueryTextSubmit(String arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	private class MySpinnerAdapter extends BaseAdapter {

		String[] values = { "Map", "List" };

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return values.length;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return values[position];
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				LayoutInflater inflater = (LayoutInflater) mContext
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(R.layout.spinner_text, parent,
						false);

			}
			TextView tv = (TextView) convertView;
			tv.setTextColor(Color.WHITE);
			tv.setText(mPin);
			return convertView;
		}

		@Override
		public View getDropDownView(int position, View convertView,
				ViewGroup parent) {
			if (convertView == null) {
				LayoutInflater inflater = (LayoutInflater) mContext
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(R.layout.spinner_text, parent,
						false);

			}
			TextView tv = (TextView) convertView;
			tv.setText(values[position]);
			// TODO Auto-generated method stub
			return convertView;
		}

	}

	private class DownloadAllBusiness extends AsyncTask<Void, Void, String> {
		@Override
		protected String doInBackground(Void... params) {
			if(Constants.PIN == null )
					return null;
			HttpPost httpPost;
			if(UserProfile.isAdmin){
				httpPost = new HttpPost(Constants.BASE_URL
						+ "admin/getBusinessInAPincode.php");
				httpPost.setParam("period",mPeriod);
			}else{
				httpPost = new HttpPost(Constants.BASE_URL
						+ "getAllBusinessInAPincode.php");
			}
			
			httpPost.setParam("pincode", Constants.PIN);

			return httpPost.executePost();
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (mProgressDialog.isShowing()) {
				mProgressDialog.dismiss();
			}
			setRefreshActionButtonState(false);
			if (result != null) {
				try {
					JSONObject jsonObject = new JSONObject(result);
					String status = jsonObject.getString("status");
					if ("success".equalsIgnoreCase(status)) {
						Log.i("HomeFragment", jsonObject.toString());
						int count = jsonObject.getInt("count");
						
						if (count > 0) {
							if(UserProfile.isAdmin){
								mHeaderLayout.setVisibility(LinearLayout.VISIBLE);
								mBusinessCount.setText(String.valueOf(count));
								}
							JSONObject object = jsonObject
									.getJSONObject("businesses");
							mBusinessList = BusinessList.init(object);
							mBusinessDetails = mBusinessList.getBusinessList();
							displayBusinessDetails();
						}
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} else {
				Toast.makeText(mContext, "No Network", Toast.LENGTH_LONG)
						.show();
			}
		}

	}

	private void displayBusinessDetails() {
		restoreAdapterData();
		for (int i = 0; i < mBusinessDetails.size(); i++) {
			if (mBusinessDetails.get(i).businessId != null)
				Log.i("HomeFragment", "BusinessId : "
						+ mBusinessDetails.get(i).businessId);
			if (mBusinessDetails.get(i).business_name != null)
				Log.i("HomeFragment",
						"BusinessName : "
								+ mBusinessDetails.get(i).business_name);
			if (mBusinessDetails.get(i).address != null)
				Log.i("HomeFragment", "Address : "
						+ mBusinessDetails.get(i).address);
			if (mBusinessDetails.get(i).latitude != null)
				Log.i("HomeFragment", "Latitude : "
						+ mBusinessDetails.get(i).latitude);
			if (mBusinessDetails.get(i).longitude != null)
				Log.i("HomeFragment", "Longitude : "
						+ mBusinessDetails.get(i).longitude);
			if (mBusinessDetails.get(i).status != null) {
				String business_status = mBusinessDetails.get(i).status;
				Log.i("HomeFragment", "Status : "
						+ mBusinessDetails.get(i).status);
				int businessType = -1;
				if (business_status
						.equalsIgnoreCase("BUSINESS_VERIFIED_COMPLETE"))
					businessType = BUSINESS_VERIFIED_COMPLETE;
				else if (business_status.equalsIgnoreCase("TRANSIENT"))
					businessType = TRANSIENT;
				else if (business_status
						.equalsIgnoreCase("BUSINESS_VERIFIED_INCOMPLETE"))
					businessType = BUSINESS_VERIFIED_INCOMPLETE;
				putDotOnMap(businessType, mBusinessDetails.get(i));
			}
		}

	}

	private void putDotOnMap(int businesstype, BusinessDetails businessDetails) {
		Marker marker;
		Double latitude = Double.parseDouble(businessDetails.latitude);
		Double longitude = Double.parseDouble(businessDetails.longitude);
		String business_name = businessDetails.business_name;
		String address_line_1 = businessDetails.address_line_1;
		String address_line_2 = businessDetails.address_line_2;
		// String address = businessDetails.address;
		String business_id = businessDetails.businessId;
		switch (businesstype) {
		case BUSINESS_VERIFIED_COMPLETE:
			// final String complete_business_id = businessDetails.businessId;
			marker = map.addMarker(new MarkerOptions()
					.position(new LatLng(latitude, longitude))
					.title(business_name + "/" + business_id)
					.snippet(address_line_1)
					.icon(BitmapDescriptorFactory
							.fromResource(R.drawable.map_green_dot)));
			/*
			 * map.setOnInfoWindowClickListener(new OnInfoWindowClickListener()
			 * {
			 * 
			 * @Override public void onInfoWindowClick(Marker marker) {
			 * Toast.makeText(mContext, complete_business_id,
			 * Toast.LENGTH_LONG).show(); } });
			 */
			// marker.
			break;
		case TRANSIENT:
			// final String transient_business_id = businessDetails.businessId;
			marker = map.addMarker(new MarkerOptions()
					.position(new LatLng(latitude, longitude))
					.title(business_name + "/" + business_id)
					.snippet(address_line_1)
					.icon(BitmapDescriptorFactory
							.fromResource(R.drawable.map_red_dot)));
			break;
		case BUSINESS_VERIFIED_INCOMPLETE:
			marker = map.addMarker(new MarkerOptions()
					.position(new LatLng(latitude, longitude))
					.title(business_name + "/" + business_id)
					.snippet(address_line_1)
					.icon(BitmapDescriptorFactory
							.fromResource(R.drawable.map_orange_dot)));
			break;
		default:
			break;
		}

	}

	private class BusinessListAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return mAdapterBusinessList.size();
		}

		@Override
		public Object getItem(int position) {
			return mAdapterBusinessList.get(position).business_name;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			BusinessDetails busiessDetail = mAdapterBusinessList.get(position);
			ViewHolder viewHolder = null;
			if (convertView == null) {
				LayoutInflater inflater = (LayoutInflater) mContext
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(R.layout.business_list_content,
						parent, false);
				viewHolder = new ViewHolder();
				viewHolder.color_business_indicator = (TextView) convertView
						.findViewById(R.id.business_indicator);
				viewHolder.text_business_name = (TextView) convertView
						.findViewById(R.id.business_name);
				viewHolder.text_address_line_one = (TextView) convertView
						.findViewById(R.id.address_line_one);
				viewHolder.text_address_line_two = (TextView) convertView
						.findViewById(R.id.address_line_two);
				viewHolder.text_address_line_three = (TextView) convertView
						.findViewById(R.id.address_line_three);
				viewHolder.text_address_line_four = (TextView) convertView
						.findViewById(R.id.address_line_four);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}

			String businessName = busiessDetail.business_name;
			String addressOne = busiessDetail.address_line_1;
			String addressTwo = busiessDetail.address_line_2;
			String addressThree = UserProfile.CITY + "-" + Constants.PIN;
			String addressFour = UserProfile.STATE + ",India";

			if (busiessDetail.status
					.equalsIgnoreCase("BUSINESS_VERIFIED_COMPLETE")) {
				viewHolder.color_business_indicator
						.setBackgroundColor(getResources().getColor(
								R.color.map_green));
			} else if (busiessDetail.status.equalsIgnoreCase("TRANSIENT")) {
				viewHolder.color_business_indicator
						.setBackgroundColor(getResources().getColor(
								R.color.map_red));
			} else if (busiessDetail.status
					.equalsIgnoreCase("BUSINESS_VERIFIED_INCOMPLETE")) {
				viewHolder.color_business_indicator
						.setBackgroundColor(getResources().getColor(
								R.color.map_orange));
			}

			viewHolder.text_business_name.setText(businessName);
			viewHolder.text_address_line_one.setText(addressOne);
			if (addressTwo != null) {
				viewHolder.text_address_line_two
						.setVisibility(TextView.VISIBLE);
				viewHolder.text_address_line_two.setText(addressTwo);
			} else {
				viewHolder.text_address_line_two.setVisibility(TextView.GONE);
			}

			viewHolder.text_address_line_three.setText(addressThree);
			viewHolder.text_address_line_four.setText(addressFour);

			return convertView;
		}

		public class ViewHolder {
			private TextView color_business_indicator;
			private TextView text_business_name;
			private TextView text_address_line_one;
			private TextView text_address_line_two;
			private TextView text_address_line_three;
			private TextView text_address_line_four;

		}

	}

	private void restoreAdapterData() {
		mAdapterBusinessList.clear();
		for (int i = 0; i < mBusinessDetails.size(); i++)
			mAdapterBusinessList.add(mBusinessDetails.get(i));

		mBusinessListAdapter.notifyDataSetChanged();
	}

	private void filterAdapterData(String query) {
		mAdapterBusinessList.clear();
		for (int i = 0; i < mBusinessDetails.size(); i++) {
			if (mBusinessDetails.get(i).business_name.toLowerCase().contains(
					query.toLowerCase()))
				mAdapterBusinessList.add(mBusinessDetails.get(i));
		}

		mBusinessListAdapter.notifyDataSetChanged();
	}

	@Override
	public void onInfoWindowClick(Marker mark) {
		String title = mark.getTitle();
		String[] title_arr = title.split("/");

		// Toast.makeText(mContext, title_arr[1], Toast.LENGTH_LONG).show();
		mProgressDialog.show();
		new DownloadBusinessJson().execute(title_arr[1]);

	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View view,
			int position, long id) {
		BusinessDetails business_detail = mAdapterBusinessList.get(position);
		// Toast.makeText(mContext, business_detail.businessId,
		// Toast.LENGTH_LONG).show();
		mProgressDialog.show();
		new DownloadBusinessJson().execute(business_detail.businessId);

	}

	private class DownloadBusinessJson extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			if(Constants.PIN == null )
				return null;
			String result = null;
			HttpGet httpGet = new HttpGet(Constants.BASE_URL
					+ "getBusinessDetails.php?business_id=" + params[0]);
			try {
				result = httpGet.executeGet();
			} catch (JsonGetException e) {				
				e.printStackTrace();
				result = null;
			}
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (mProgressDialog.isShowing()) {
				mProgressDialog.dismiss();
			}
			if (result != null) {
				Log.i("HomeFragment", result);

				try {
					JSONObject responseJson = new JSONObject(result);
					String business_id = responseJson.getString("business_id");
					startEditBusiness(business_id, responseJson);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				Toast.makeText(mContext, "No Network", Toast.LENGTH_LONG)
						.show();
			}
		}

	}

	private void startEditBusiness(String business_id, JSONObject businessJson) {
		Timer.resetTimer();
		if(UserProfile.isAdmin){
			((AdminActivity) mActivity).downloadedJson = businessJson;
			((AdminActivity) mActivity).mBusinessId = business_id;
			((AdminActivity) mActivity).editBusinessFragment();
		}else{
			((CAActivity) mActivity).downloadedJson = businessJson;
			((CAActivity) mActivity).mBusinessId = business_id;
			((CAActivity) mActivity).editBusinessFragment();
		}
		Constants.isEditBusinessClicked = true;

	}

}
