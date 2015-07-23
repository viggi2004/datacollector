package com.columbusagain.citibytes;

import java.io.File;
import java.util.List;
import java.util.Vector;

import org.json.JSONException;
import org.json.JSONObject;

import com.columbusagain.citibytes.adapters.PagerAdapter;
import com.columbusagain.citibytes.datacollection.*;
import com.columbusagain.citibytes.helper.Timer;
import com.columbusagain.citibytes.helper.UserProfile;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.Window;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class DataCollectionFragment extends Fragment implements
		OnPageChangeListener, OnClickListener {
	private int mTimeCalculation = 0;
	
	private boolean isImageScreenEnteredFirstTime = true;
	
	private Context mContext;
	
	public boolean isAdminEditMode = false;
	
	//private boolean isBusinessCategoryCalledFirsTime = true;
	
	public JSONObject mUiSchemaJson;
	
	public ViewPager mViewPager;

	private PagerAdapter mPagerAdapter;

	List<Fragment> fragments;
	
	private Button mBtnIndPage1,mBtnIndPage2,mBtnIndPage3,mBtnIndPage4,mBtnIndPage5;
	
	private ImageView mIndicatorDivider1,mIndicatorDivider2,mIndicatorDivider3,mIndicatorDivider4;
	
	//private ImageView mPageIndicator;
	
	private Activity mActivity;
	
	private ActionBar mActionBar;
	
	public boolean isBasicDetailsScreenEnabled = false,
			isLocationScreenEnabled = false,
			isBusinessCategoryScreenEnabled = false,
			isBusinessDetailsScreenEnabled = false,
			isImageDetailsScreenEnabled = false;

	public DataCollectionFragment() {
		// Empty constructor required for fragment subclasses
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.mActivity = activity;
		this.mContext = activity;
		
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if(UserProfile.isAdmin)
			mActionBar = ((AdminActivity)mActivity).getSupportActionBar();
		else
			mActionBar = ((CAActivity)mActivity).getSupportActionBar();
		View rootView = inflater.inflate(R.layout.business_details_fragment,
				container, false);
		mViewPager = (ViewPager) rootView.findViewById(R.id.viewpager);
		mBtnIndPage1 = (Button)rootView.findViewById(R.id.page_indicator_btn_one);
		mBtnIndPage2 = (Button)rootView.findViewById(R.id.page_indicator_btn_two);
		mBtnIndPage3 = (Button)rootView.findViewById(R.id.page_indicator_btn_three);
		mBtnIndPage4 = (Button)rootView.findViewById(R.id.page_indicator_btn_four);
		mBtnIndPage5 = (Button)rootView.findViewById(R.id.page_indicator_btn_five);
		
		mBtnIndPage1.setOnClickListener(this);
		mBtnIndPage2.setOnClickListener(this);
		mBtnIndPage3.setOnClickListener(this);
		mBtnIndPage4.setOnClickListener(this);
		mBtnIndPage5.setOnClickListener(this);
		//mPageIndicator = (ImageView) rootView.findViewById(R.id.page_indicator);
		mViewPager.setOffscreenPageLimit(4);
		fragments = new Vector<Fragment>();
		
		
		fragments.add(Fragment.instantiate(getActivity(),
				BasicDetailsFragment.class.getName()));
		fragments.add(Fragment.instantiate(getActivity(),
				LocationDetailsFragment.class.getName()));
		fragments.add(Fragment.instantiate(getActivity(),
				BusinessCategoryFragment.class.getName()));
		fragments.add(Fragment.instantiate(getActivity(),
				BusinessDetailsFragment.class.getName()));
		fragments.add(Fragment.instantiate(getActivity(),
				ImagesFragment.class.getName()));

		this.mPagerAdapter = new PagerAdapter(super.getChildFragmentManager(),
				fragments);
		mViewPager.setAdapter(this.mPagerAdapter);

		mViewPager.setOnPageChangeListener(this);
		
		return rootView;

	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		//inflater.inflate(R.menu.home, menu);
		super.onCreateOptionsMenu(menu, inflater);
		//return false;

	}

	@Override
	public void onPageScrollStateChanged(int state) {
		
		Log.i("DataCollection", "onPageScrollStateChanged");
		Log.i("DataCollection", "position :"+state);
		
	}
	

	@Override
	public void onPageScrolled(int position, float positionOffset,
			int positionOffsetPixels) {
		Log.i("DataCollection", "onPageScrolled");
		Log.i("DataCollection", "position :"+position);
		if(position == 0){
			/*try {
				((BasicDetailsFragment)fragments.get(0)).populateSavedData();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
		}
		if(position == 1){
			if(!isLocationScreenEnabled){
				mViewPager.setCurrentItem(0);
			}else{
				/*try {
					((LocationDetailsFragment)fragments.get(1)).populateSavedData();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}*/
			}
			
		}
		if(position == 2){
			if(!isBusinessCategoryScreenEnabled){
				mViewPager.setCurrentItem(1);
			}else{
				/*try {
					((BusinessCategoryFragment)fragments.get(2)).populateSavedData();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}*/
			}
			
		}
		if(position == 3){
			if(!isBusinessDetailsScreenEnabled){
				mViewPager.setCurrentItem(2);
			}else{
				/*try {
					((BusinessDetailsFragment)fragments.get(3)).populateSavedData();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}*/
			}
			
		}
		if(position == 4){
			if(!isImageDetailsScreenEnabled){
				mViewPager.setCurrentItem(3);
			}
			
		}

	}

	@Override
	public void onPageSelected(int position) {
		Log.i("DataCollection", "onPageSelected");
		switch(position){
		case 0:
			try {
				((BasicDetailsFragment)fragments.get(0)).populateSavedData();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			mBtnIndPage1.setBackgroundResource(R.drawable.bludot);
			mBtnIndPage2.setBackgroundResource(R.drawable.red_dot);
			mBtnIndPage3.setBackgroundResource(R.drawable.red_dot);
			mBtnIndPage4.setBackgroundResource(R.drawable.red_dot);
			mBtnIndPage5.setBackgroundResource(R.drawable.red_dot);
			//mPageIndicator.setImageResource(R.drawable.page_001);
			break;
		case 1:
			if(UserProfile.isAdmin)
				setBusinessName(((AdminActivity)mActivity).mBusinessName);
			else
				setBusinessName(((CAActivity)mActivity).mBusinessName);
			/*try {
				((LocationDetailsFragment)fragments.get(1)).populateSavedData();
			} catch (JSONException e) {
				e.printStackTrace();
			}*/
			//((BaseDrawerActivity)mActivity).isSecondPageEnabled = true;
			mBtnIndPage1.setBackgroundResource(R.drawable.bludot);
			mBtnIndPage2.setBackgroundResource(R.drawable.bludot);
			mBtnIndPage3.setBackgroundResource(R.drawable.red_dot);
			mBtnIndPage4.setBackgroundResource(R.drawable.red_dot);
			mBtnIndPage5.setBackgroundResource(R.drawable.red_dot);
			//mPageIndicator.setImageResource(R.drawable.page_002);
			break;
		case 2:
			if(((LocationDetailsFragment)fragments.get(1)).isValidInput()){
				((LocationDetailsFragment)fragments.get(1)).mProceedButton.performClick();
			}else{
				Toast.makeText(mContext, "Please fill required fields", Toast.LENGTH_LONG).show();
				mViewPager.setCurrentItem(1);
			}
			/*if(isBusinessCategoryCalledFirsTime){
			try {
				((BusinessCategoryFragment)fragments.get(2)).populateSavedData();
			} catch (JSONException e) {
				e.printStackTrace();
			}
			isBusinessCategoryCalledFirsTime = false;
			}*/
			mBtnIndPage1.setBackgroundResource(R.drawable.bludot);
			mBtnIndPage2.setBackgroundResource(R.drawable.bludot);
			mBtnIndPage3.setBackgroundResource(R.drawable.bludot);
			mBtnIndPage4.setBackgroundResource(R.drawable.red_dot);
			mBtnIndPage5.setBackgroundResource(R.drawable.red_dot);
			//mPageIndicator.setImageResource(R.drawable.page_003);
			break;
		case 3:
			if(((BusinessCategoryFragment)fragments.get(2)).isValidInput()){
				((BusinessCategoryFragment)fragments.get(2)).mProceedButton.performClick();
			}else{
				Toast.makeText(mContext, "Please fill required fields", Toast.LENGTH_LONG).show();
				mViewPager.setCurrentItem(2);
			}
			/*try {
				((BusinessDetailsFragment)fragments.get(3)).populateSavedData();
			} catch (JSONException e) {
				e.printStackTrace();
			}*/
			mBtnIndPage1.setBackgroundResource(R.drawable.bludot);
			mBtnIndPage2.setBackgroundResource(R.drawable.bludot);
			mBtnIndPage3.setBackgroundResource(R.drawable.bludot);
			mBtnIndPage4.setBackgroundResource(R.drawable.bludot);
			mBtnIndPage5.setBackgroundResource(R.drawable.red_dot);
			//mPageIndicator.setImageResource(R.drawable.page_004);
			break;
		case 4:
			if(((BusinessDetailsFragment)fragments.get(3)).isValidInput()){
				((BusinessDetailsFragment)fragments.get(3)).mProceedButton.performClick();
			}else{
				Toast.makeText(mContext, "Please fill required fields", Toast.LENGTH_LONG).show();
				mViewPager.setCurrentItem(2);
			}
			/*if(isImageScreenEnteredFirstTime){
				try {
					((ImagesFragment)fragments.get(4)).populateData();
				} catch (JSONException e) {
					e.printStackTrace();
				}
				isImageScreenEnteredFirstTime = false;
			}*/
			mBtnIndPage1.setBackgroundResource(R.drawable.bludot);
			mBtnIndPage2.setBackgroundResource(R.drawable.bludot);
			mBtnIndPage3.setBackgroundResource(R.drawable.bludot);
			mBtnIndPage4.setBackgroundResource(R.drawable.bludot);
			mBtnIndPage5.setBackgroundResource(R.drawable.bludot);
			break;
		default:
			break;
				
			
		}
		
		


	}

	@Override
	public void onClick(View view) {
		switch(view.getId()){
		case R.id.page_indicator_btn_one:
			mViewPager.setCurrentItem(0);
			break;
		case R.id.page_indicator_btn_two:
			mViewPager.setCurrentItem(1);
			break;
		case R.id.page_indicator_btn_three:
			mViewPager.setCurrentItem(2);
			break;
		case R.id.page_indicator_btn_four:
			mViewPager.setCurrentItem(3);
			break;
		case R.id.page_indicator_btn_five:
			mViewPager.setCurrentItem(4);
			break;
		}
		
		
	}
	
	@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        List<Fragment> fragments = getChildFragmentManager().getFragments();
        if (fragments != null) {
            for (Fragment fragment : fragments) {
                fragment.onActivityResult(requestCode, resultCode, data);
                Log.i("DataCollectionFragment", "onActivityResult:"+requestCode);
            }
        }
    }
	
	private void AdminAppBackButton(){
		boolean isPhotoDelete = false;
		String message_1 = "Are you sure to discard this business data?";
		String message_2= "Your current changes will be lost.Do you want to go back?";
		if(((AdminActivity)mContext).mBusinessId != null){
			JSONObject businessJson = ((AdminActivity)mContext).downloadedJson;
			if (businessJson.length() == 0){
				isPhotoDelete = false;
				showCustomDialog(message_1,"delete_mode",isPhotoDelete);
			}else{
				if(businessJson.has("status")){
					String business_status;
					try {
						business_status = businessJson.getString("status");
						if(business_status.equalsIgnoreCase("BUSINESS_VERIFIED_COMPLETE")){
							isPhotoDelete = true;
							showCustomDialog(message_2,"non_delete_mode",isPhotoDelete);
							//((AdminActivity)mActivity).startEmployeeDetailsFragment(((AdminActivity)mActivity).mLastSelectedType,((AdminActivity)mActivity).mSelectedEmployeeEmailId);
						}else if(business_status.equalsIgnoreCase("BUSINESS_VERIFIED_INCOMPLETE")){
							isPhotoDelete = true;
							showCustomDialog(message_2,"non_delete_mode",isPhotoDelete);
						}else{
							isPhotoDelete = true;
							showCustomDialog(message_2,"non_delete_mode",isPhotoDelete);
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					//String business_type = businessJson.getString("");
					// go back without delete business
					//showCustomDialog(message_2,"non_delete_mode");
				}else{
					// delete business and go back
					isPhotoDelete = true;
					showCustomDialog(message_1,"delete_mode",isPhotoDelete);
				}
			}
		}else{
			//show dialog
			isPhotoDelete = false;
			showCustomDialog(message_1,"non_delete_mode",isPhotoDelete);
		}
	}
	
	private void CAAppBackButton(){
		boolean isPhotoDelete = false;
		String message_1 = "Are you sure to discard this business data?";
		String message_2= "Your current changes will be lost.Do you want to go back?";
		if(((CAActivity)mContext).mBusinessId != null){
			JSONObject businessJson = ((CAActivity)mContext).downloadedJson;
			if (businessJson.length() == 0){
				isPhotoDelete = false;
				showCustomDialog(message_1,"delete_mode",isPhotoDelete);
			}else{
				if(businessJson.has("status")){
					String business_status;
					try {
						business_status = businessJson.getString("status");
						if(business_status.equalsIgnoreCase("BUSINESS_VERIFIED_COMPLETE")){
							//showCustomDialog(message_2,"non_delete_mode");
							deleteImageDirectory();
							((CAActivity)mActivity).startHomeFragment();
						}else if(business_status.equalsIgnoreCase("BUSINESS_VERIFIED_INCOMPLETE")){
							isPhotoDelete = true;
							showCustomDialog(message_2,"non_delete_mode",isPhotoDelete);
						}else{
							isPhotoDelete = true;
							showCustomDialog(message_2,"non_delete_mode",isPhotoDelete);
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					//String business_type = businessJson.getString("");
					// go back without delete business
					//showCustomDialog(message_2,"non_delete_mode");
				}else{
					// delete business and go back
					isPhotoDelete = true;
					showCustomDialog(message_1,"delete_mode",isPhotoDelete);
				}
			}
		}else{
			//show dialog
			isPhotoDelete = false;
			showCustomDialog(message_1,"non_delete_mode",isPhotoDelete);
		}
	}
	 
	public void onBackPressed(){
		if(UserProfile.isAdmin)
			AdminAppBackButton();
		else
			CAAppBackButton();
				
		
	}
	
	public void deleteImageDirectory(){
		Log.i("DataCollectionFragment", "deleteFile");
		String business_id;
		if(UserProfile.isAdmin)
			business_id = ((AdminActivity)mContext).mBusinessId;
		else
			business_id = ((CAActivity)mContext).mBusinessId;
		File file_path = new File(Environment.getExternalStorageDirectory()
				+ "/DCIM/", business_id);
		File[] files = file_path.listFiles();
		if(files != null){
			
		
		for(int i=0;i<files.length;i++){
			Log.i("DataCollectionFragment", "file :"+files[i].toString());
			
			File[] sub_folder = files[i].listFiles();
			for(int j=0;j<sub_folder.length;j++){
				sub_folder[j].delete();
			}
			files[i].delete();
			
		}
		
		}
		file_path.delete();
	}
	
	
	
	private void showCustomDialog(String message , final String type,final boolean isPhotoDelete){
		final Dialog discard_dialog = new Dialog(mContext);
		discard_dialog
				.requestWindowFeature(Window.FEATURE_NO_TITLE);
		discard_dialog
				.setContentView(R.layout.delete_business_dialog);
		Button ok_button = (Button)  discard_dialog.findViewById(R.id.ok_button);
		Button cancel_button = (Button)  discard_dialog.findViewById(R.id.cancel_button);
		TextView message_text = (TextView) discard_dialog.findViewById(R.id.message);
		message_text.setText(message);
		ok_button.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				if(type.equalsIgnoreCase("delete_mode")){
					if(UserProfile.isAdmin)
						((AdminActivity)mContext).deleteBusiness();
					else
						((CAActivity)mContext).deleteBusiness();
					deleteImageDirectory();
					discard_dialog.dismiss();
					if(UserProfile.isAdmin){
						if(UserProfile.isFromMapScreen)
							((AdminActivity)mActivity).startMapFragment(((AdminActivity)mActivity).mPeriod);							
						else
							((AdminActivity)mActivity).startEmployeeDetailsFragment(((AdminActivity)mActivity).mLastSelectedType,((AdminActivity)mActivity).mSelectedEmployeeEmailId);
					}
					else
						((CAActivity)mActivity).startHomeFragment();
					//return;
				}else{
					if(isPhotoDelete){
						deleteImageDirectory();
					}
					discard_dialog.dismiss();
					if(UserProfile.isAdmin){
						if(UserProfile.isFromMapScreen)
							((AdminActivity)mActivity).startMapFragment(((AdminActivity)mActivity).mPeriod);							
						else
							((AdminActivity)mActivity).startEmployeeDetailsFragment(((AdminActivity)mActivity).mLastSelectedType,((AdminActivity)mActivity).mSelectedEmployeeEmailId);
					}
					else
						((CAActivity)mActivity).startHomeFragment();
					//return;
				}
				
			}
		});
		
		cancel_button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				discard_dialog.dismiss();
				return;
			}
		});
		
		discard_dialog.show();
	}
	
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		List<Fragment> fragments = getChildFragmentManager().getFragments();
        if (fragments != null) {
            for (Fragment fragment : fragments) {
            	fragment.onCreateContextMenu(menu, v, menuInfo);
            	Log.i("DataCollectionFragment", "oncreateContextMenu");
            }
        }
	}

	/*public static void setCurrentPage(int position){
		mViewPager.setCurrentItem(position);
	}*/
	
	public void setBusinessName(String businessName){
		((BasicDetailsFragment)fragments.get(0)).setBusinessName(businessName);
		((LocationDetailsFragment)fragments.get(1)).setBusinessName(businessName);
		((BusinessCategoryFragment)fragments.get(2)).setBusinessName(businessName);
		((BusinessDetailsFragment)fragments.get(3)).setBusinessName(businessName);
		((ImagesFragment)fragments.get(4)).setBusinessName(businessName);
		
		//((BasicDetailsFragment)fragments.get(0)).test();
	}
	
	public void editModeSetup(){
		((LocationDetailsFragment)fragments.get(1)).setEditMode();
		((BusinessCategoryFragment)fragments.get(2)).setEditMode();
		((BusinessDetailsFragment)fragments.get(3)).setEditMode();
		((ImagesFragment)fragments.get(4)).setEditMode();
	}
	
	public void upDateImageScreen(){
		((ImagesFragment)fragments.get(4)).initUi(mUiSchemaJson);
	}
	
	@Override
	public void onResume() {
		Log.i("DataCollection", "onResume");
		super.onResume();
		Timer.startTimer();
	}
	
	@Override
	public void onPause() {
		super.onPause();
		Log.i("DataCollection", "onPause");
		Timer.stopTimer();
	}
	

}
