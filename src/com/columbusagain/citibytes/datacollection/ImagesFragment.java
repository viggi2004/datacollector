package com.columbusagain.citibytes.datacollection;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Calendar;

import org.apache.http.util.ByteArrayBuffer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.view.ActionMode;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.columbusagain.citibytes.AdminActivity;
import com.columbusagain.citibytes.CAActivity;
import com.columbusagain.citibytes.DataCollectionFragment;
import com.columbusagain.citibytes.R;
import com.columbusagain.citibytes.adapters.ImageAdapter;
import com.columbusagain.citibytes.helper.Attribute;
import com.columbusagain.citibytes.helper.AttributeOnClickListener;
import com.columbusagain.citibytes.helper.BusinessAttributes;
import com.columbusagain.citibytes.helper.Constants;
import com.columbusagain.citibytes.helper.ImageDetails;
import com.columbusagain.citibytes.helper.OnGridViewItemClickListener;
import com.columbusagain.citibytes.helper.PhotoAttribute;
import com.columbusagain.citibytes.helper.UserProfile;
import com.columbusagain.citibytes.syncadapter.SyncAdapter;
import com.columbusagain.citibytes.util.NetworkChecker;
import com.luminous.pick.Action;

public class ImagesFragment extends Fragment implements OnClickListener,AttributeOnClickListener,OnGridViewItemClickListener, OnTouchListener {
	
	private Button mUploadButton,mContentVerifiedButton;
	
	private TextView mGalleryTitleText,mBusinessCardTitleText;
	
	private boolean isViewMode = false;
	
	private boolean isAdminEditMode = false;
	
	private MenuInflater mMenuInflater;
	
	private Dialog mProgressDialog;
	
	private ScrollView mLayoutScrollView;
	
	boolean isNeedUpdate = true;
	
	ArrayList<Uri> mDownloadedUriList = new ArrayList<Uri>();
	//public int mBusinessImages,mGalleryImages,mAttributeImages;
	
	public int mImageCount;
	
	private TextView mBusinessNameTextView,mViewBusinessNameText,mViewSubTitleText;
	
	private boolean isCameraIntentSelected;
	
	private BusinessAttributes mBusinessAttributes;
	
	private static final int BUSINESS_IMAGE_PICK = 500;
	
	private static final int GALLERY_IMAGE_PICK = 501;
	
	private static final int ATTRIBUTE_IMAGE_PICK = 502;
	
	private int mImageType;
	
	private LinearLayout mPhotoAttributeLayout;	
	
	private PhotoAttribute mPhotoAttribute;
	
	private String schema;
	
	//private ArrayList<Uri> mBusinessImages=new ArrayList<Uri>();
	
	private ArrayList<ImageDetails> mBusinessImageList = new ArrayList<ImageDetails>();
	private ArrayList<ImageDetails> mGalleryImageList = new ArrayList<ImageDetails>();
	
	//private ArrayList<Uri> mGalleryImages=new ArrayList<Uri>();
	
	private GridView mBusinessGridView,mGalleryGridView;
	
	private Uri mOutputFileUri = null;

	private ImageButton mImageButton1,mImageButton2,mImageButton3,mImageButton4,mImageButton5,mImageButton6;
	
	private Activity mActivity;
	
	private Context mContext;
	
	ImageAdapter mBusinessImageAdapter,mGalleryImageAdapter;
	
	private static final boolean POST_HONEYCOMB = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
	
	public ImagesFragment(){
		
	}
	
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		this.mActivity = activity;
		this.mContext = (Context)activity;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mImageCount = 0;
		/*mBusinessImages = 0;
		mGalleryImages = 0;
		mAttributeImages = 0;*/
		setHasOptionsMenu(true);
		View rootView = inflater.inflate(
				R.layout.datacollection_fragment_images, container, false);
		mBusinessCardTitleText = (TextView) rootView.findViewById(R.id.business_card_text);
		mGalleryTitleText = (TextView) rootView.findViewById(R.id.gallery_title_text);
		mBusinessNameTextView = (TextView) rootView.findViewById(R.id.business_name);
		mViewBusinessNameText = (TextView) rootView.findViewById(R.id.view_business_name);
		mViewSubTitleText = (TextView)rootView.findViewById(R.id.view_subtitle);
		mBusinessGridView=(GridView)rootView.findViewById(R.id.gridview1);
		mGalleryGridView = (GridView)rootView.findViewById(R.id.gridview2);
	    mLayoutScrollView = (ScrollView)rootView.findViewById(R.id.layout_scroll_view);
	   // mLayoutScrollView.onInterceptTouchEvent);
		//mLayoutScrollView.requestDisallowInterceptTouchEvent(false);
		mUploadButton = (Button) rootView.findViewById(R.id.upload_button);
		mContentVerifiedButton = (Button) rootView.findViewById(R.id.content_verify_button);
		if(!UserProfile.isAdmin)
			mContentVerifiedButton.setText("Submit");
		mPhotoAttributeLayout = (LinearLayout) rootView.findViewById(R.id.photo_attribute_layout);
		//initUi();
		//mBusinessImages.add(null);
		////////////////////////////////////////////////////////////////////
		ImageDetails imgDetails = new ImageDetails();		
		imgDetails.isCheckBoxEnabled = false;
		imgDetails.isChecked = false;
		imgDetails.imageUri = null;
		mBusinessImageList.add(imgDetails);
		////////////////////////////////////////////////////////////////////
		
        mBusinessImageAdapter = new ImageAdapter(this,mContext, mBusinessImageList, getParentFragment(),BUSINESS_IMAGE_PICK,"Card");
       // mGalleryImages.add(null);
        ////////////////////////////////////////////////////////////////////
        imgDetails = new ImageDetails();		
		imgDetails.isCheckBoxEnabled = false;
		imgDetails.isChecked = false;
		imgDetails.imageUri = null;
		mGalleryImageList.add(imgDetails);
		////////////////////////////////////////////////////////////////////
        
        mGalleryImageAdapter = new ImageAdapter(this,mContext, mGalleryImageList, getParentFragment(),GALLERY_IMAGE_PICK,"Other");
        mBusinessGridView.setAdapter(mBusinessImageAdapter);
        mGalleryGridView.setAdapter(mGalleryImageAdapter);
       // mBusinessGridView.setOnTouchListener(this);
       // mBusinessGridView.requestDisallowInterceptTouchEvent(false);
       // mBusinessGridView.setOnTouchListener(this);
       // mBusinessGridView.setOnItemClickListener(this);
       // mGalleryGridView.setOnItemClickListener(this);
        mUploadButton.setOnClickListener(this);
        mContentVerifiedButton.setOnClickListener(this);
       /* getParentFragment().registerForContextMenu(mBusinessGridView);
        getParentFragment().registerForContextMenu(mGalleryGridView);
        mBusinessGridView.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE_MODAL);
        mGalleryGridView.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE_MODAL);
        if(POST_HONEYCOMB){
        	postHoneyCombCAB();
        }else{
        	
        }*/
        
       // registerForContextMenu(mBusinessGridView);
       // registerForContextMenu(mGalleryGridView);
    
		/*
		mImageButton1 = (ImageButton) rootView.findViewById(R.id.image1);
		mImageButton2 = (ImageButton) rootView.findViewById(R.id.image2);
		mImageButton3 = (ImageButton) rootView.findViewById(R.id.image3);
		mImageButton4 = (ImageButton) rootView.findViewById(R.id.image4);
		mImageButton5 = (ImageButton) rootView.findViewById(R.id.image5);
		mImageButton6 = (ImageButton) rootView.findViewById(R.id.image6);
		
		mImageButton1.setOnClickListener(this);
		mImageButton2.setOnClickListener(this);
		mImageButton3.setOnClickListener(this);
		mImageButton4.setOnClickListener(this);
		mImageButton5.setOnClickListener(this);
		mImageButton6.setOnClickListener(this);
		*/
        
      /*  boolean isNetworkConnected = NetworkChecker.isConnected(mContext);
		if(!isNetworkConnected){
			Toast.makeText(mContext, "No Internet!", Toast.LENGTH_LONG).show();
		}else{
			new DownloadImages().execute();
		}
		*/
        
        try {
			populateData();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return rootView;

	}
	
	/*@SuppressLint({ "NewApi", "NewApi" })
	private void postHoneyCombCAB() {
		mBusinessGridView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> adapterView, View view,
					int position, long id) {
				mBusinessGridView.setItemChecked(position, mBusinessGridView.isItemChecked(position));
				return false;
			}
			
		});
		mBusinessGridView.setMultiChoiceModeListener(new MultiChoiceModeListener() {
			
			@Override
			public boolean onPrepareActionMode(android.view.ActionMode mode, Menu menu) {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public void onDestroyActionMode(android.view.ActionMode mode) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public boolean onCreateActionMode(android.view.ActionMode mode, Menu menu) {
				getActivity().getMenuInflater().inflate(R.menu.image_screen_context_menu,
                        menu);
				return true;
			}
			
			@Override
			public boolean onActionItemClicked(android.view.ActionMode mode,
					MenuItem item) {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public void onItemCheckedStateChanged(android.view.ActionMode mode,
					int position, long id, boolean checked) {
				// TODO Auto-generated method stub
				
			}
		});
		
	}*/

	@Override
	public void onClick(View view) {
		//String dir_name, file_name;

		//Intent chooserIntent;
		//String mBusinessId = "001";
		switch(view.getId()){
		case R.id.content_verify_button:
			((DataCollectionFragment)getParentFragment()).deleteImageDirectory();
			if(UserProfile.isAdmin){
				if(UserProfile.isFromMapScreen)
					((AdminActivity)mActivity).startMapFragment(((AdminActivity)mActivity).mPeriod);							
				else
					((AdminActivity)mActivity).startEmployeeDetailsFragment(((AdminActivity)mActivity).mLastSelectedType,((AdminActivity)mActivity).mSelectedEmployeeEmailId);
			}
			else
			((CAActivity)mContext).startHomeFragment();
			break;
		case R.id.upload_button:
			//Log.i("BusinessId",((CAActivity)mContext).mBusinessId);
			if(mBusinessImageList.size() <= 1){
				Toast.makeText(mContext, "Business card cannot be empty", Toast.LENGTH_LONG).show();
				break;
			}
			if(mBusinessAttributes !=null)
			if(!mBusinessAttributes.isValidInput(mContext, true)){
				Toast.makeText(mContext, "Please fill required fields", Toast.LENGTH_LONG).show();
				break;
			}
			if(UserProfile.isAdmin)
				((AdminActivity)mContext).disableKeyBoard();
			else
				((CAActivity)mContext).disableKeyBoard();
			ArrayList<Uri> images = new ArrayList<Uri>();
			//Strin
			JSONArray imageUriArray = new JSONArray();
			for(int i=0;i<mBusinessImageList.size()-1;i++){
				//images.add(mBusinessImageList.get(i).imageUri);
				imageUriArray.put(mBusinessImageList.get(i).imageUri);
			}
			for(int i=0;i<mGalleryImageList.size()-1;i++){
				//images.add(mGalleryImageList.get(i).imageUri);
				imageUriArray.put(mGalleryImageList.get(i).imageUri);
			}
			
			if(mBusinessAttributes !=null)
			mBusinessAttributes.getPhotoUri(imageUriArray);
			//SyncAdapter.mImages = images;
			Log.i("ImagesFragment", ""+imageUriArray.length());
			Log.i("ImagesFragment", ""+imageUriArray.toString());
			//((BaseDrawerActivity)mContext).new 
			JSONArray photo_path = new JSONArray();
			if(UserProfile.isAdmin){							
				try {
					((AdminActivity)mContext).businessJson.put("photo_url", photo_path);
				} catch (JSONException e) {
					e.printStackTrace();
				}	
				((AdminActivity)mContext).requestSync(imageUriArray);	
			}else{				
				try {
					((CAActivity)mContext).businessJson.put("photo_url", photo_path);
				} catch (JSONException e) {
					e.printStackTrace();
				}	
				((CAActivity)mContext).requestSync(imageUriArray);
			}
			Toast.makeText(mContext, "Business added successfully", Toast.LENGTH_LONG).show();
			if(UserProfile.isAdmin){
				if(UserProfile.isFromMapScreen)
					((AdminActivity)mActivity).startMapFragment(((AdminActivity)mActivity).mPeriod);							
				else
					((AdminActivity)mActivity).startEmployeeDetailsFragment(((AdminActivity)mActivity).mLastSelectedType,((AdminActivity)mActivity).mSelectedEmployeeEmailId);
			}else
			((CAActivity)mContext).startHomeFragment();
			
			//mImages.add(object)
			break;
		/*case R.id.image1:
			dir_name = "Card";
			file_name = "Image1.jpg";
			mOutputFileUri = ((BaseDrawerActivity) mActivity)
					.setImageUri(mBusinessId, dir_name, file_name);
			 chooserIntent = ((BaseDrawerActivity) mActivity).getImageCaptureIntent(
			1001, mOutputFileUri);
			
			// imageSource = "image1";
			 getParentFragment().startActivityForResult(chooserIntent, 1001);
			break;
		case R.id.image2:
			break;
		case R.id.image3:
			break;
		case R.id.image4:
			break;
		case R.id.image5:
			break;
		case R.id.image6:
			break;*/
		}
		
	}	
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		//data.get
		Log.i("ImagesFragment","onActivityResult111111");
		//Calendar calendar = Calendar.getInstance();
		String img_name;
		String selectedImagePath = "";
		Uri imageUri = null;
		int lastIndex_gal;
		int lastIndex;
		if (resultCode == getActivity().RESULT_OK) {
			switch (requestCode) {
			case BUSINESS_IMAGE_PICK:
				Log.i("ImagesFragment","RESULT_OK");
    			if(data!=null ){
    				Log.i("ImagesFragment","data not null");
    				//if(data.getData()!=null){
    					Log.i("ImagesFragment","data.getdata not null");
    					
    					//Log.i("data",data.getData().toString());
    					if(data.hasExtra("all_path")){
    						isCameraIntentSelected = false;
    						Log.i("ImagesFragment","all_path avalable");
    						Log.i("ImgesFragment", "multiple images available");
    						String[] all_path = data.getStringArrayExtra("all_path");
    						for(int i=0;i<all_path.length;i++){
    							Log.i("ImagesFragment", "ImagePath :"+all_path[i]);
    							selectedImagePath = "file://"+all_path[i];
    							if(selectedImagePath!=null){
    		    					imageUri = Uri.parse(selectedImagePath);
    		    					String business_id;
    		    					if(UserProfile.isAdmin)
    		    						business_id = ((AdminActivity)mContext).mBusinessId;
    		    					else
    		    						business_id = ((CAActivity)mContext).mBusinessId;
    		    					File file_main = new File(Environment.getExternalStorageDirectory()
    		    							+ "/DCIM/", business_id);
    		    					file_main.mkdir();
    		    					File file_sub = new File(file_main, "Card");
    		    					file_sub.mkdir();
    		    					img_name= String.valueOf(mImageCount);
    		    	    			mImageCount++;
    		    					File file_output = new File(file_sub, img_name+".jpg");
    		    					if (file_output.isFile())
    		    						file_output.delete();
    		    					
    		    					try {
    									copyFile(imageUri,file_output);
    									imageUri = Uri.fromFile(file_output);
    									lastIndex = mBusinessImageList.size()-1;
    				        			if(imageUri!=null){    			
    				        			//mBusinessImages.add(lastIndex,imageUri);
    				        			////////////////////////////////////////////////////////////////////
    				        			ImageDetails imgDetails = new ImageDetails();		
    				        			imgDetails.isCheckBoxEnabled = false;
    				        			imgDetails.isChecked = false;
    				        			imgDetails.imageUri = imageUri;
    				        			mBusinessImageList.add(lastIndex,imgDetails);
    				        			////////////////////////////////////////////////////////////////////
    				        			}
    								} catch (IOException e) {
    									// TODO Auto-generated catch block
    									e.printStackTrace();
    								}
    		    					}
    						}
    					}else{
    						Log.i("ImgesFragment", "multiple images not available");
        					isCameraIntentSelected = true;
        					if(UserProfile.isAdmin)
        						imageUri = ((AdminActivity)mContext).getImageUri();
        					else
        						imageUri = ((CAActivity)mContext).getImageUri();
        					lastIndex = mBusinessImageList.size()-1;
                			if(imageUri!=null){    			
                			//mBusinessImages.add(lastIndex,imageUri);
                			////////////////////////////////////////////////////////////////////
                			ImageDetails imgDetails = new ImageDetails();		
                			imgDetails.isCheckBoxEnabled = false;
                			imgDetails.isChecked = false;
                			imgDetails.imageUri = imageUri;
                			mBusinessImageList.add(lastIndex,imgDetails);
                			////////////////////////////////////////////////////////////////////
                			}
    					}
    					/*Arra
    					selectedImagePath = ((CAActivity)mActivity).getAbsolutePath(data.getData());
    					if(selectedImagePath!=null){
    					imageUri = Uri.parse(selectedImagePath);
    					File file_main = new File(Environment.getExternalStorageDirectory()
    							+ "/DCIM/", ((CAActivity)mContext).mBusinessId);
    					file_main.mkdir();
    					File file_sub = new File(file_main, "Card");
    					file_sub.mkdir();
    					img_name= String.valueOf(mImageCount);
    	    			mImageCount++;
    					File file_output = new File(file_sub, img_name+".jpg");
    					if (file_output.isFile())
    						file_output.delete();
    					
    					try {
							copyFile(imageUri,file_output);
							imageUri = Uri.fromFile(file_output);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
    					}*/
    				/*}else{
    					Log.i("ImagesFragment", "data.getData() is null");
    					isCameraIntentSelected = true;
    					imageUri = ((CAActivity)mContext).getImageUri();
    				}*/
    			
    			}else{
    				Log.i("ImagesFragment", "Data is null");
    				isCameraIntentSelected = true;
    				if(UserProfile.isAdmin)
    					imageUri = ((AdminActivity)mContext).getImageUri();
    				else
    					imageUri = ((CAActivity)mContext).getImageUri();
    				lastIndex = mBusinessImageList.size()-1;
        			if(imageUri!=null){    			
        			//mBusinessImages.add(lastIndex,imageUri);
        			////////////////////////////////////////////////////////////////////
        			ImageDetails imgDetails = new ImageDetails();		
        			imgDetails.isCheckBoxEnabled = false;
        			imgDetails.isChecked = false;
        			imgDetails.imageUri = imageUri;
        			mBusinessImageList.add(lastIndex,imgDetails);
        			////////////////////////////////////////////////////////////////////
        			}
    				
    			}
    			
    			//mPhotoUris.add(ImageUri.getPath());
    			//addImage(ImageUri);
    			/*int lastIndex = mBusinessImageList.size()-1;
    			if(imageUri!=null){    			
    			//mBusinessImages.add(lastIndex,imageUri);
    			////////////////////////////////////////////////////////////////////
    			ImageDetails imgDetails = new ImageDetails();		
    			imgDetails.isCheckBoxEnabled = false;
    			imgDetails.isChecked = false;
    			imgDetails.imageUri = imageUri;
    			mBusinessImageList.add(lastIndex,imgDetails);
    			////////////////////////////////////////////////////////////////////
    			}*/
    			lastIndex = mBusinessImageList.size()-1;
    			
    			img_name= String.valueOf(mImageCount);
    			mImageCount++;
    			
    			
    			
    			//int lastIndex =  mImageAdapter.addImage(ImageUri);
    			mBusinessImageAdapter.notifyDataSetChanged();
    			
    			
    			//Calendar calendar = Calendar.getInstance();
    			
    			
    			Log.i("MainActivity","activity result called");
    			if(UserProfile.isAdmin)
    				mOutputFileUri = ((AdminActivity)mContext).setImageUri(((AdminActivity)mContext).mBusinessId , "Card", img_name+".jpg");
    			else
    				mOutputFileUri = ((CAActivity)mContext).setImageUri(((CAActivity)mContext).mBusinessId , "Card", img_name+".jpg");
    			if(isCameraIntentSelected){
    				Intent camIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    				camIntent.putExtra(MediaStore.EXTRA_SIZE_LIMIT, 1024*1024);
    				camIntent.putExtra(MediaStore.EXTRA_OUTPUT, mOutputFileUri);
    				getParentFragment().startActivityForResult(camIntent, BUSINESS_IMAGE_PICK);
    			}
    			/*else{
    				Intent pickPhoto = new Intent(
    						Action.ACTION_MULTIPLE_PICK);
    				//,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
	                
    				getParentFragment().startActivityForResult(pickPhoto, BUSINESS_IMAGE_PICK);
    			}
    			/*Intent camIntent =((BaseDrawerActivity)mContext).getImageCaptureIntent(BUSINESS_IMAGE_PICK, mOutputFileUri);
 				camIntent.putExtra(MediaStore.EXTRA_OUTPUT, mOutputFileUri);
 				getParentFragment().startActivityForResult(camIntent, BUSINESS_IMAGE_PICK);*/
 				
    		
 				//Log.i("ImageUri",imageUri.toString());
    			
    			break;
    			
			case GALLERY_IMAGE_PICK:
				Log.i("ImagesFragment","RESULT_OK");
    			if(data!=null ){
    				//if(data.getData()!=null){
    				//	Log.i("","");
    					
    					if(data.hasExtra("all_path")){
    						isCameraIntentSelected = false;
    						Log.i("ImgesFragment", "multiple images available");
    						String[] all_path = data.getStringArrayExtra("all_path");
    						for(int i=0;i<all_path.length;i++){
    							selectedImagePath ="file://"+all_path[i];
    							if(selectedImagePath!=null){
    		    					imageUri = Uri.parse(selectedImagePath);
    		    					String business_id;
    		    					if(UserProfile.isAdmin)
    		    						business_id = ((AdminActivity)mContext).mBusinessId;
    		    					else
    		    						business_id = ((CAActivity)mContext).mBusinessId;
    		    					File file_main = new File(Environment.getExternalStorageDirectory()
    		    							+ "/DCIM/", business_id);
    		    					file_main.mkdir();
    		    					File file_sub = new File(file_main, "Other");
    		    					file_sub.mkdir();
    		    					img_name= String.valueOf(mImageCount);
    		    	    			mImageCount++;
    		    					File file_output = new File(file_sub, img_name+".jpg");
    		    					if (file_output.isFile())
    		    						file_output.delete();
    		    					
    		    					try {
    									copyFile(imageUri,file_output);
    									imageUri = Uri.fromFile(file_output);
    									lastIndex_gal = mGalleryImageList.size()-1;
    				        			if(imageUri!=null){
    				        				//mGalleryImages.add(lastIndex_gal,imageUri);
    				        		        ////////////////////////////////////////////////////////////////////
    				        				ImageDetails imgDetails = new ImageDetails();		
    				        				imgDetails.isCheckBoxEnabled = false;
    				        				imgDetails.isChecked = false;
    				        				imgDetails.imageUri = imageUri;
    				        				mGalleryImageList.add(lastIndex_gal,imgDetails);
    				        				////////////////////////////////////////////////////////////////////
    				        			}
    								} catch (IOException e) {
    									// TODO Auto-generated catch block
    									e.printStackTrace();
    								}
    		    					}
    						}
    					}else{
    						Log.i("ImgesFragment", "multiple images not available");
    						isCameraIntentSelected = true;
    						if(UserProfile.isAdmin)
    							imageUri = ((AdminActivity)mContext).getImageUri();
    						else
    							imageUri = ((CAActivity)mContext).getImageUri();
        					lastIndex_gal = mGalleryImageList.size()-1;
                			if(imageUri!=null){
                				//mGalleryImages.add(lastIndex_gal,imageUri);
                		        ////////////////////////////////////////////////////////////////////
                				ImageDetails imgDetails = new ImageDetails();		
                				imgDetails.isCheckBoxEnabled = false;
                				imgDetails.isChecked = false;
                				imgDetails.imageUri = imageUri;
                				mGalleryImageList.add(lastIndex_gal,imgDetails);
                				////////////////////////////////////////////////////////////////////
                			}
    					}
    					/*selectedImagePath = ((CAActivity)mActivity).getAbsolutePath(data.getData());
    					if(selectedImagePath!=null){
    					imageUri = Uri.parse(selectedImagePath);
    					File file_main = new File(Environment.getExternalStorageDirectory()
    							+ "/DCIM/", ((CAActivity)mContext).mBusinessId);
    					file_main.mkdir();
    					File file_sub = new File(file_main, "Other");
    					file_sub.mkdir();
    					img_name= String.valueOf(mImageCount);
    	    			mImageCount++;
    					File file_output = new File(file_sub, img_name+".jpg");
    					if (file_output.isFile())
    						file_output.delete();
    					
    					try {
							copyFile(imageUri,file_output);
							imageUri = Uri.fromFile(file_output);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
    					}*/
    			/*	}else{
    					isCameraIntentSelected = true;
    					imageUri = ((CAActivity)mContext).getImageUri();
    				}*/
    			
    			}else{
    				isCameraIntentSelected = true;
    				if(UserProfile.isAdmin)
    					imageUri = ((AdminActivity)mContext).getImageUri();
    				else
    					imageUri = ((CAActivity)mContext).getImageUri();
    				lastIndex_gal = mGalleryImageList.size()-1;
        			if(imageUri!=null){
        				//mGalleryImages.add(lastIndex_gal,imageUri);
        		        ////////////////////////////////////////////////////////////////////
        				ImageDetails imgDetails = new ImageDetails();		
        				imgDetails.isCheckBoxEnabled = false;
        				imgDetails.isChecked = false;
        				imgDetails.imageUri = imageUri;
        				mGalleryImageList.add(lastIndex_gal,imgDetails);
        				////////////////////////////////////////////////////////////////////
        			}
    				
    			}
    			
    			//mPhotoUris.add(ImageUri.getPath());
    			//addImage(ImageUri);
    			
    			
    			lastIndex_gal = mGalleryImageList.size()-1;
    			img_name= String.valueOf(mImageCount);
    			mImageCount++;
    			
    			//int lastIndex =  mImageAdapter.addImage(ImageUri);
    			mGalleryImageAdapter.notifyDataSetChanged();
    			
    			Log.i("MainActivity","activity result called");
    			if(UserProfile.isAdmin)
    				mOutputFileUri = ((AdminActivity)mContext).setImageUri(((AdminActivity)mContext).mBusinessId , "Other", img_name+".jpg");
    			else
    				mOutputFileUri = ((CAActivity)mContext).setImageUri(((CAActivity)mContext).mBusinessId , "Other", img_name+".jpg");
    			if(isCameraIntentSelected){
    				Intent camIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    				camIntent.putExtra(MediaStore.EXTRA_OUTPUT, mOutputFileUri);
    				getParentFragment().startActivityForResult(camIntent, GALLERY_IMAGE_PICK);
    			}
    			/*else{
    				Intent pickPhoto = new Intent(
    						Action.ACTION_MULTIPLE_PICK);
    				getParentFragment().startActivityForResult(pickPhoto, GALLERY_IMAGE_PICK);
    			}
    			/*mOutputFileUri = ((BaseDrawerActivity)mContext).setImageUri("PictureResize", "Demo", "image"+img_name+calendar.getTimeInMillis()+".jpg");
    			//Uri mOutputFileUri = mImageAdapter.setImageUri("PictureResize", "Demo", "image"+lastIndex+".jpg");
    			Intent camIntent_gal =((BaseDrawerActivity)mContext).getImageCaptureIntent(GALLERY_IMAGE_PICK, mOutputFileUri);
 				//Intent camIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    			camIntent_gal.putExtra(MediaStore.EXTRA_OUTPUT, mOutputFileUri);
 				getParentFragment().startActivityForResult(camIntent_gal, GALLERY_IMAGE_PICK);*/
 				
    		
 			//	Log.i("ImageUri",imageUri.toString());
    			
    			break;
    			
			case ATTRIBUTE_IMAGE_PICK:
				if(data!=null ){
    				//if(data.getData()!=null){
    				//	Log.i("","");
    				//	
    					if(data.hasExtra("all_path")){
    						isCameraIntentSelected = false;
    						Log.i("ImgesFragment", "multiple images available");
    						String[] all_path = data.getStringArrayExtra("all_path");
    						for(int i=0;i<all_path.length;i++){
    							selectedImagePath = "file://"+all_path[i];
    							if(selectedImagePath!=null){
    		    					imageUri = Uri.parse(selectedImagePath);
    		    					String business_id;
    		    					if(UserProfile.isAdmin)
    		    						business_id = ((AdminActivity)mContext).mBusinessId;
    		    					else
    		    						business_id = ((CAActivity)mContext).mBusinessId;
    		    					File file_main = new File(Environment.getExternalStorageDirectory()
    		    							+ "/DCIM/", business_id);
    		    					file_main.mkdir();
    		    					File file_sub = new File(file_main, mPhotoAttribute.name);
    		    					file_sub.mkdir();
    		    					img_name= String.valueOf(mImageCount);
    		    	    			mImageCount++;
    		    					File file_output = new File(file_sub, img_name+".jpg");
    		    					if (file_output.isFile())
    		    						file_output.delete();
    		    					
    		    					try {
    									copyFile(imageUri,file_output);
    									imageUri = Uri.fromFile(file_output);
    									if(imageUri!=null){
    		    	    					mPhotoAttribute.setImage(imageUri);
    		    	    				}
    								} catch (IOException e) {
    									// TODO Auto-generated catch block
    									e.printStackTrace();
    								}
    		    					}
    						}
    					}else{
    						Log.i("ImgesFragment", "multiple images not available");
    						isCameraIntentSelected = true;
    						if(UserProfile.isAdmin)
    							imageUri = ((AdminActivity)mContext).getImageUri();
    						else
    							imageUri = ((CAActivity)mContext).getImageUri();
    	    				if(imageUri!=null){
    	    					mPhotoAttribute.setImage(imageUri);
    	    				}
    					}
    					/*selectedImagePath = ((CAActivity)mActivity).getAbsolutePath(data.getData());
    					if(selectedImagePath!=null){
    					imageUri = Uri.parse(selectedImagePath);
    					File file_main = new File(Environment.getExternalStorageDirectory()
    							+ "/DCIM/", ((CAActivity)mContext).mBusinessId);
    					file_main.mkdir();
    					File file_sub = new File(file_main, mPhotoAttribute.name);
    					file_sub.mkdir();
    					img_name= String.valueOf(mImageCount);
    	    			mImageCount++;
    					File file_output = new File(file_sub, img_name+".jpg");
    					if (file_output.isFile())
    						file_output.delete();
    					
    					try {
							copyFile(imageUri,file_output);
							imageUri = Uri.fromFile(file_output);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
    					}*/
    				/*}else{
    					isCameraIntentSelected = true;
    					imageUri = ((CAActivity)mContext).getImageUri();
    				}*/
    			
    			}else{
    				isCameraIntentSelected = true;
    				if(UserProfile.isAdmin)
    					imageUri = ((AdminActivity)mContext).getImageUri();
    				else
    					imageUri = ((CAActivity)mContext).getImageUri();
    				if(imageUri!=null){
    					mPhotoAttribute.setImage(imageUri);
    				}
    				
    			}
				
				
    			img_name= String.valueOf(mImageCount);
    			mImageCount++;
    			//mGalleryImageAdapter.notifyDataSetChanged();
    			
    			Log.i("MainActivity","activity result called");
    			if(UserProfile.isAdmin)
    				mOutputFileUri = ((AdminActivity)mContext).setImageUri(((AdminActivity)mContext).mBusinessId , mPhotoAttribute.name, img_name+".jpg");
    			else
    				mOutputFileUri = ((CAActivity)mContext).setImageUri(((CAActivity)mContext).mBusinessId , mPhotoAttribute.name, img_name+".jpg");
    			if(isCameraIntentSelected){
    				Intent camIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    				camIntent.putExtra(MediaStore.EXTRA_OUTPUT, mOutputFileUri);
    				getParentFragment().startActivityForResult(camIntent, ATTRIBUTE_IMAGE_PICK);
    			}
    			/*else{
    				Intent pickPhoto = new Intent(
    						Action.ACTION_MULTIPLE_PICK);
    				getParentFragment().startActivityForResult(pickPhoto, ATTRIBUTE_IMAGE_PICK);
    			}*/
				break;
			case 1001:
				Log.i("ImagesFragment","onActivityResult");
				// imageUri = getImageUri(data);
				if (data != null) {
					if (data.getData() != null) {
						if(UserProfile.isAdmin)
							selectedImagePath = ((AdminActivity)mActivity).getAbsolutePath(data.getData());
						else
							selectedImagePath = ((CAActivity)mActivity).getAbsolutePath(data.getData());
						imageUri = Uri.parse(selectedImagePath);
					} else {
						imageUri = mOutputFileUri;
					}

				} else {
					imageUri = mOutputFileUri;

				}
				//mCardImages[0] = imageUri.toString();
				//Log.i("input1", mCardImages[0].toString());
				if(UserProfile.isAdmin)
					((AdminActivity)mActivity).resizeImage(imageUri, 768, 1024);
				else
					((CAActivity)mActivity).resizeImage(imageUri, 768, 1024);
				new BitmapWorkerTask(mImageButton1).execute(imageUri);
				break;
			}
		}
			}
	
	class BitmapWorkerTask extends AsyncTask<Uri, Void, Bitmap> {
		private final WeakReference<ImageView> imageViewReference;
		private int data = 0;

		public BitmapWorkerTask(ImageView imageView) {
			// Use a WeakReference to ensure the ImageView can be garbage
			// collected
			imageViewReference = new WeakReference<ImageView>(imageView);
		}

		// Decode image in background.
		@Override
		protected Bitmap doInBackground(Uri... params) {
			Uri imageUri = params[0];

			Bitmap bitmap = null;
			ImageView imageView = imageViewReference.get();
			// try {
			/*
			 * bitmap = android.provider.MediaStore.Images.Media.getBitmap(
			 * getContentResolver(), imageUri);
			 */
			// Log.i("Image Height & Width",String.valueOf(imageView.getWidth()+" "+imageView.getHeight()));
			bitmap = decodeSampledBitmapFromResource(imageUri, 96, 96);
			/*
			 * } catch (FileNotFoundException e) { // TODO Auto-generated catch
			 * block e.printStackTrace(); } catch (IOException e) { // TODO
			 * Auto-generated catch block e.printStackTrace(); }
			 */
			return bitmap;
		}

		// Once complete, see if ImageView is still around and set bitmap.
		@Override
		protected void onPostExecute(Bitmap bitmap) {

			if (imageViewReference != null && bitmap != null) {
				/*
				 * Log.i("CityBytes_ByteCount",
				 * String.valueOf(bitmap.getByteCount() / 1024));
				 */
				final ImageView imageView = imageViewReference.get();
				if (imageView != null) {
					imageView.setImageBitmap(bitmap);
				}
			}
		}
	}
	
	private static Bitmap decodeSampledBitmapFromResource(Uri imageUri,
			int reqWidth, int reqHeight) {

		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;

		BitmapFactory.decodeFile(imageUri.getPath(), options);

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeFile(imageUri.getPath(), options);
	}
	
	private static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			final int halfHeight = height / 2;
			final int halfWidth = width / 2;

			// Calculate the largest inSampleSize value that is a power of 2 and
			// keeps both
			// height and width larger than the requested height and width.
			while ((halfHeight / inSampleSize) > reqHeight
					&& (halfWidth / inSampleSize) > reqWidth) {
				inSampleSize *= 2;
			}
		}

		return inSampleSize;
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		mMenuInflater = inflater;
		inflater.inflate(R.menu.image_screen_menu, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}
	
	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		if(UserProfile.isAdmin)
		if(isAdminEditMode){
			mMenuInflater.inflate(R.menu.image_screen_menu, menu);
			menu.getItem(1).setVisible(false);
		}else{
			mMenuInflater.inflate(R.menu.admin_mode, menu);
			menu.getItem(0).setVisible(false);
		}
		super.onPrepareOptionsMenu(menu);
	}
	
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
		case R.id.action_edit:
			((DataCollectionFragment)getParentFragment()).editModeSetup();
			/*isViewMode = false;
			isAdminEditMode = true;
			mActivity.invalidateOptionsMenu();
			editModeSetup();*/
			break;
		case R.id.image_edit:
			if(isViewMode)
				return super.onOptionsItemSelected(item);
			for(int i=0;i<mBusinessImageList.size();i++){
				if(i!=mBusinessImageList.size()-1){
					mBusinessImageList.get(i).isCheckBoxEnabled = true;
				}
			}
			mBusinessImageAdapter.notifyDataSetChanged();
			for(int i=0;i<mGalleryImageList.size();i++){
				if(i!=mGalleryImageList.size()-1){
					mGalleryImageList.get(i).isCheckBoxEnabled = true;
				}
			}
			mGalleryImageAdapter.notifyDataSetChanged();
			if(mBusinessAttributes != null)
			mBusinessAttributes.setActionModeUi();
			if(UserProfile.isAdmin)
				((AdminActivity) mContext).startSupportActionMode(new ActionBarCallBack());
			else
				((CAActivity) mContext).startSupportActionMode(new ActionBarCallBack());
				//((Activity) mContext).st;
			//((Activity) mContext).openContextMenu(mBusinessGridView);
			//((Activity) mContext).openContextMenu(mGalleryGridView);
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void setEditMode(){
		isViewMode = false;
		isAdminEditMode = true;
		mActivity.invalidateOptionsMenu();
		editModeSetup();
		
	}
	
	

	/*@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		getActivity().getMenuInflater().inflate(R.menu.image_screen_context_menu, menu);
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = ((Activity) mContext).getMenuInflater();
	    inflater.inflate(R.menu.image_screen_context_menu, menu);
	    Log.i("MapFragment", "oncreateContextMenu");
	}*/
	
	
	
	class ActionBarCallBack implements ActionMode.Callback {
		  
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch(item.getItemId()){
            case R.id.delete_image:
            	Log.i("CAB", "delete button pressed");
            	for(int i=0;i<mBusinessImageList.size();i++){
            		if(mBusinessImageList.get(i).isChecked){
            			mBusinessImageList.remove(i);
            			
            			i--;
            			
            		}
            	}
            	
            	for(int i=0;i<mGalleryImageList.size();i++){
            		if(mGalleryImageList.get(i).isChecked){
            			mGalleryImageList.remove(i);
            			
            			i--;
            			
            		}
            	}
            	mBusinessImageAdapter.notifyDataSetChanged();
            	mGalleryImageAdapter.notifyDataSetChanged();
            	if(mBusinessAttributes != null)
            	mBusinessAttributes.deleteImages();
            	disableCheckBox();
            	if(mBusinessAttributes != null)
            	mBusinessAttributes.disableCheckBoxes();
            	mode.finish();
            	return true;
            	
            case R.id.close_context_menu:
            	disableCheckBox();
            	if(mBusinessAttributes != null)
            	mBusinessAttributes.disableCheckBoxes();
            	/*for(int i=0;i<mBusinessImageList.size();i++){
    				//if(i!=mBusinessImageList.size()-1){
    					mBusinessImageList.get(i).isCheckBoxEnabled = false;
    				//}
    			}
    			mBusinessImageAdapter.notifyDataSetChanged();
    			for(int i=0;i<mGalleryImageList.size();i++){
    				//if(i!=mGalleryImageList.size()-1){
    					mGalleryImageList.get(i).isCheckBoxEnabled = false;
    				//}
    			}
    			mGalleryImageAdapter.notifyDataSetChanged();*/
    			mode.finish();
            	return true;
            
            	
            }
            return false;
        }
        
        private void disableCheckBox(){
        	for(int i=0;i<mBusinessImageList.size();i++){
				//if(i!=mBusinessImageList.size()-1){
        			//mBusinessImageList.get(i).isChecked = false;
					mBusinessImageList.get(i).isCheckBoxEnabled = false;
				//}
			}
			mBusinessImageAdapter.notifyDataSetChanged();
			for(int i=0;i<mGalleryImageList.size();i++){
				//if(i!=mGalleryImageList.size()-1){
					//mBusinessImageList.get(i).isChecked = false;
					mGalleryImageList.get(i).isCheckBoxEnabled = false;
				//}
			}
			mGalleryImageAdapter.notifyDataSetChanged();
        	
        }
  
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // TODO Auto-generated method stub
            mode.getMenuInflater().inflate(R.menu.image_screen_context_menu, menu);
            //int doneButtonId = Resources.getSystem().getIdentifier("action_mode_close_button", "id", "android.support.v7.appcompat");
           
            //menu.findItem(android.support.v7.appcompat.R.id.action_mode_close_button).setVisible(false);
            //doneButton.setVisibility(View.GONE);
            return true;
        }
  
        @Override
        public void onDestroyActionMode(ActionMode mode) {
            // TODO Auto-generated method stub
  
        }
  
        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            // TODO Auto-generated method stub
  
            return false;
        }
}
	
	/*@SuppressLint({ "NewApi", "NewApi" })
    private void postHoneycombCAB() {
        getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        getListView().setOnItemLongClickListener(new OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                    int position, long id) {
                ((ListView) parent).setItemChecked(position,
                        ((ListView) parent).isItemChecked(position));
                return false;
            }
        });
        getListView().setMultiChoiceModeListener(new MultiChoiceModeListener() {

            private int nr = 0;

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                getActivity().getMenuInflater().inflate(R.menu.listcab_menu,
                        menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {
                case R.id.item1:
                    Toast.makeText(getActivity(), "Option1 clicked",
                            Toast.LENGTH_SHORT).show();
                    break;
                case R.id.item2:
                    Toast.makeText(getActivity(), "Option2 clicked",
                            Toast.LENGTH_SHORT).show();
                    break;

                }
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                nr = 0;
            }

            @Override
            public void onItemCheckedStateChanged(ActionMode mode,
                    int position, long id, boolean checked) {
                if (checked) {
                    nr++;
                } else {
                    nr--;
                }
                mode.setTitle(nr + " rows selected!");
            }
        });
    }*/
	
	/*mBusinessGridView.setMultiChoiceModeListener(new MultiChoiceModeListener() {
		
		@Override
		public boolean onPrepareActionMode(android.view.ActionMode mode, Menu menu) {
			// TODO Auto-generated method stub
			return false;
		}
		
		@Override
		public void onDestroyActionMode(android.view.ActionMode mode) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public boolean onCreateActionMode(android.view.ActionMode mode, Menu menu) {
			// TODO Auto-generated method stub
			return false;
		}
		
		@Override
		public boolean onActionItemClicked(android.view.ActionMode arg0,
				MenuItem arg1) {
			// TODO Auto-generated method stub
			return false;
		}
		
		@Override
		public void onItemCheckedStateChanged(android.view.ActionMode mode,
				int position, long id, boolean checked) {
			// TODO Auto-generated method stub
			
		}
	});*/
	
	public void initUi(JSONObject jsonObject){
		
		
		if(jsonObject == null)
			return;
		
		try {
			//JSONObject jsonObject = ((DataCollectionFragment)getParentFragment()).mUiSchemaJson;
			String status = jsonObject.getString("status");
			if(status.equalsIgnoreCase("success")){
				String schema_name = jsonObject.getString("schema_name");
				if(schema_name.equalsIgnoreCase(schema))
					return;
				else{
					mBusinessAttributes = null;
					mPhotoAttributeLayout.removeAllViews();
				}
				
					
				schema = jsonObject.getString("schema_name");
				JSONArray schema_arr = jsonObject.getJSONArray("schema");
				//JSONObject schemaJson = jsonObject.getJSONObject("schema");
				mBusinessAttributes = BusinessAttributes.init(schema_arr,this);
				
				mBusinessAttributes.render(mPhotoAttributeLayout,true, mContext,getParentFragment());
				
				if(mBusinessAttributes !=null){
					if(isNeedUpdate){
					for(int i=0;i<mDownloadedUriList.size();i++)
					mBusinessAttributes.setPhotos(mDownloadedUriList.get(i));
					isNeedUpdate = false;
					}
				}
			}else{
				mBusinessAttributes = null;
				mPhotoAttributeLayout.removeAllViews();
				schema = null;
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void setBusinessName(String businessName){
		if(mBusinessNameTextView != null)
		mBusinessNameTextView.setText(businessName);
		if(mViewBusinessNameText != null)
		mViewBusinessNameText.setText(businessName);
	}

	@Override
	public void onClick(Attribute attribute,int type) {
		if(attribute instanceof PhotoAttribute){
			mPhotoAttribute = (PhotoAttribute)attribute;
			if(isViewMode)
				return;
				else
			startIntentChooser(type);
		}
		
	}

	/*@Override
	public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
		Log.i("ImagesFragment", "onItemClick");
		String file_name = "";
		switch(view.getId()){
		
		case R.id.gridview1:
			if(position == mBusinessImageList.size() -1){
			file_name = String.valueOf(mBusinessImages);
			mBusinessImages++;
			mOutputFileUri = ((BaseDrawerActivity)mContext).setImageUri(((BaseDrawerActivity)mContext).mBusinessId , "Card", file_name+".jpg");
			startIntentChooser(BUSINESS_IMAGE_PICK);
			}
			break;
			
		case R.id.gridview2:
			if(position == mGalleryImageList.size() -1){
			file_name = String.valueOf(mGalleryImages);
			mGalleryImages++;
			mOutputFileUri = ((BaseDrawerActivity)mContext).setImageUri(((BaseDrawerActivity)mContext).mBusinessId , "Other", file_name+".jpg");
			startIntentChooser(GALLERY_IMAGE_PICK);
			}
			break;
			
			
		}
	}*/
	
	private void startIntentChooser(int ImageType){
		String mImagePath = "";
		String file_name = "";
		if(ImageType == 500){
			file_name = String.valueOf(mImageCount);
			mImageCount++;
			mImagePath = "Card";
		}else if(ImageType == 501){
			file_name = String.valueOf(mImageCount);
			mImageCount++;
			mImagePath = "Other";
		}else if(ImageType == 502){
			file_name = String.valueOf(mImageCount);
			mImageCount++;
			mImagePath = mPhotoAttribute.name;
			
		}
		if(UserProfile.isAdmin)
			mOutputFileUri = ((AdminActivity)mContext).setImageUri(((AdminActivity)mContext).mBusinessId , mImagePath, file_name+".jpg");
		else
			mOutputFileUri = ((CAActivity)mContext).setImageUri(((CAActivity)mContext).mBusinessId , mImagePath, file_name+".jpg");
		showIntentChooserDialog(ImageType);
		/*Intent camIntent = ((CAActivity) mContext)
				.getImageCaptureIntent(0, mOutputFileUri);
		camIntent.putExtra(MediaStore.EXTRA_OUTPUT, mOutputFileUri);
		getParentFragment().startActivityForResult(camIntent,
				ImageType);*/
	}

	private void showIntentChooserDialog(final int IMAGE_TYPE) {
		AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
		builder.setTitle("Select Source");
		builder.setItems(R.array.intent_array,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						if (which == 0) {
							//camera 
							Intent camIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		    				camIntent.putExtra(MediaStore.EXTRA_OUTPUT, mOutputFileUri);
		    				getParentFragment().startActivityForResult(camIntent, IMAGE_TYPE);
						} else {
							// gallery
							Intent galIntent = new Intent(
									Action.ACTION_MULTIPLE_PICK);
							getParentFragment().startActivityForResult(galIntent,
									IMAGE_TYPE);
						}
					}
				});

		builder.create().show();

	}
	@Override
	public void test(int imageType) {
		Log.i("ImagesFragment", "OnclickListener");
		if(isViewMode)
		return;
		else
		startIntentChooser(imageType);
	}
	
	public void disableEdit(){
		isViewMode = true;
		if(!UserProfile.isAdmin)
		mUploadButton.setClickable(false);
		//mBusinessGridView.setFocusable(false);
		//mGalleryGridView.setFocusable(false);
		//mBusinessAttributes.disableEdit();
	}
	
	public void populateData() throws JSONException{
		JSONObject businessJson;
		if(UserProfile.isAdmin)
			businessJson = ((AdminActivity) mContext).downloadedJson;
		else
			businessJson = ((CAActivity) mContext).downloadedJson;
		
		if (businessJson.length() == 0)
			return;
		
		if(businessJson.has("photo_url")){
			
			//JSONArray urlArray = businessJson.getJSONArray("photo_url");
			
			new DownloadImages().execute();
			
		/*	String[] url ;
			
			for(int i=0;i<urlArray.length();i++){
				url = urlArray.get(i).toString().split("/");
				String mImagePath = url[url.length-1] ;
				String file_name = String.valueOf(mImageCount);
				mImageCount++;
				mOutputFileUri = ((BaseDrawerActivity)mContext).setImageUri(((BaseDrawerActivity)mContext).mBusinessId , mImagePath, file_name+".jpg");
			}*/
			
		}
		
		if(businessJson.has("status")){
			((DataCollectionFragment) getParentFragment()).isImageDetailsScreenEnabled = true;
			String status = businessJson.getString("status");
			if(status.equalsIgnoreCase("BUSINESS_VERIFIED_COMPLETE")){
				disableEdit();
				viewModeSetup();
				setHasOptionsMenu(false);
				if(UserProfile.isAdmin){
					setHasOptionsMenu(true);
					mActivity.invalidateOptionsMenu();
				}
			}else if(status.equalsIgnoreCase("BUSINESS_VERIFIED_INCOMPLETE")){
				if(UserProfile.isAdmin){
					disableEdit();
					viewModeSetup();
					mActivity.invalidateOptionsMenu();
				}
			}else if(status.equalsIgnoreCase("TRANSIENT")){
				if(UserProfile.isAdmin){
					disableEdit();
					viewModeSetup();
					mActivity.invalidateOptionsMenu();
				}
			}
		}
	}
	
	private void viewModeSetup(){
		mBusinessNameTextView.setVisibility(TextView.GONE);
		mViewBusinessNameText.setVisibility(TextView.VISIBLE);
		mViewSubTitleText.setVisibility(TextView.VISIBLE);
		mBusinessImageList.remove(mBusinessImageList.size()-1);
		mBusinessImageAdapter.notifyDataSetChanged();
		mGalleryImageList.remove(mGalleryImageList.size()-1);
		mGalleryImageAdapter.notifyDataSetChanged();
		if(mGalleryImageList.size() == 0)
		mGalleryTitleText.setVisibility(TextView.GONE);
		
		mUploadButton.setVisibility(Button.GONE);
		mContentVerifiedButton.setVisibility(Button.VISIBLE);
		
		mBusinessCardTitleText.setText("BUSINESS CARD");
		
		
	}
	
	private void editModeSetup(){
		
		mBusinessNameTextView.setVisibility(TextView.VISIBLE);
		mViewBusinessNameText.setVisibility(TextView.GONE);
		mViewSubTitleText.setVisibility(TextView.GONE);
		ImageDetails imgDetails = new ImageDetails();		
		imgDetails.isCheckBoxEnabled = false;
		imgDetails.isChecked = false;
		imgDetails.imageUri = null;
		mBusinessImageList.add(imgDetails);
		mBusinessImageAdapter.notifyDataSetChanged();
		mGalleryImageList.add(imgDetails);
		mGalleryImageAdapter.notifyDataSetChanged();
		if(mBusinessAttributes != null)
			mBusinessAttributes.setViewModeUi(isViewMode);
		//if(mGalleryImageList.size() == 0)
		mGalleryTitleText.setVisibility(TextView.VISIBLE);
		
		mUploadButton.setVisibility(Button.VISIBLE);
		mContentVerifiedButton.setVisibility(Button.GONE);
		
		mBusinessCardTitleText.setText("BUSINESS CARD");
	}
	
	
	private void copyFile(Uri source,File destination) throws IOException{
		File imgFile = new  File(source.getPath());
		if(imgFile.exists()){
			/* Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
			//ByteArrayBuffer bytearray = new ByteArrayBuffer(bitmap.);
			 ByteArrayOutputStream stream = new ByteArrayOutputStream();
			bitmap.compress(CompressFormat.JPEG,100, stream);*/
			
			BufferedInputStream buf_in= new BufferedInputStream(new FileInputStream(imgFile.getAbsoluteFile()));
			
			
				//FileOutputStream fos = new FileOutputStream(destination);
				BufferedOutputStream buf_out = new BufferedOutputStream(new FileOutputStream(destination));
				//buf_out.write(buf_in.read(buffer));
				
				
				// Copy the bits from input stream to output stream
	            byte[] buff = new byte[1024];
	            int len;
	            while ((len = buf_in.read(buff)) > 0) {
	            	buf_out.write(buff, 0, len);
	            }
	            
	            buf_out.flush();
				buf_out.close();
				buf_in.close();
		}
	}
	private class DownloadImages extends AsyncTask<Void, Void, Void>{
		
		JSONObject businessJson = null;
		
		String url;
		
		String[] url_content;
		
		String mImagePath;
		
		String[] mFileUri;
		
		String mFileName;
		
		JSONArray urlArray;
		
		ArrayList<Uri> mUri = new ArrayList<Uri>();
		
		
		
		private void downloadPhoto(String url_string,File file) throws IOException{
			
				URL url = new URL(url_string);
				
				 URLConnection uconn = url.openConnection();
				 
				 InputStream is = uconn.getInputStream();
				  BufferedInputStream bufferinstream = new BufferedInputStream(is);
						
						
						
							BufferedOutputStream buf_out = new BufferedOutputStream(new FileOutputStream(file));
							
							
				            byte[] buff = new byte[1024];
				            int len;
				            while ((len = bufferinstream.read(buff)) > 0) {
				            	buf_out.write(buff, 0, len);
				            }
				            
				            buf_out.flush();
							buf_out.close();
							bufferinstream.close();
			      

			     
		}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mDownloadedUriList.clear();
			if(UserProfile.isAdmin)
				businessJson = ((AdminActivity) mContext).downloadedJson;
			else
				businessJson = ((CAActivity) mContext).downloadedJson;
			
			
		}

		@Override
		protected Void doInBackground(Void... params) {
			try {
				urlArray = businessJson.getJSONArray("photo_url");
				for(int i=0;i<urlArray.length();i++){
					url = urlArray.get(i).toString();
					url_content = url.split("/");
					String mImagePath = url_content[url_content.length-2] ;
					String file_name = String.valueOf(mImageCount);
					mImageCount++;
					String business_id;
					if(UserProfile.isAdmin)
						business_id = ((AdminActivity)mContext).mBusinessId;
					else
						business_id = ((CAActivity)mContext).mBusinessId;
					
					File file_main = new File(Environment.getExternalStorageDirectory()
							+ "/DCIM/", business_id);
					file_main.mkdir();
					File file_sub = new File(file_main, mImagePath);
					file_sub.mkdir();
					File file_output = new File(file_sub, file_name+".jpg");
					if (file_output.isFile())
						file_output.delete();
					//mOutputFileUri = ((BaseDrawerActivity)mContext).setImageUri(((BaseDrawerActivity)mContext).mBusinessId , mImagePath, file_name+".jpg");
					downloadPhoto(url,file_output);
					
					Log.i("ImagesFragment",url+":"+file_output.toString());
					
					mDownloadedUriList.add(Uri.fromFile(file_output));
					
					//File file= new File(mOutputFileUri.toString());
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			int lastIndex;
			if(mBusinessAttributes != null && isViewMode){
				mBusinessAttributes.setViewModeUi(isViewMode);
				Log.i("ImagesFragment", "business attributes available");
			}
			for(int i=0;i<mDownloadedUriList.size();i++){
				String[] uri = mDownloadedUriList.get(i).toString().split("/");
				Log.i("ImagesFragment","ImageUri : "+mDownloadedUriList.get(i).toString());
				
				if(uri[uri.length-2].equalsIgnoreCase("card")){
					if(mBusinessImageList.size() == 0)
						lastIndex = 0;
					else{
					lastIndex = mBusinessImageList.size()-1;
					if(mBusinessImageList.get(lastIndex).imageUri != null)
						lastIndex++;
					}
					ImageDetails imgDetails = new ImageDetails();		
					imgDetails.isCheckBoxEnabled = false;
					imgDetails.isChecked = false;
					imgDetails.imageUri = mDownloadedUriList.get(i);
					mBusinessImageList.add(lastIndex,imgDetails);
					
					/*if(mBusinessImageList.size()>1){
						int last_index = mBusinessImageList.size()-1;						
						if(mBusinessImageList.get(last_index).imageUri != null){
							Uri temp_uri = mBusinessImageList.get(last_index).imageUri;
							mBusinessImageList.get(last_index).imageUri = mBusinessImageList.get(last_index-1).imageUri;
							mBusinessImageList.get(last_index-1).imageUri = temp_uri;
						}						
					}*/
				}else if(uri[uri.length-2].equalsIgnoreCase("other")){
					mGalleryTitleText.setVisibility(TextView.VISIBLE);
					if(mGalleryImageList.size() == 0)
						lastIndex = 0;
					else{
					lastIndex = mGalleryImageList.size()-1;
					if(mGalleryImageList.get(lastIndex).imageUri != null)
						lastIndex++;
					}
					ImageDetails imgDetails = new ImageDetails();		
					imgDetails.isCheckBoxEnabled = false;
					imgDetails.isChecked = false;
					imgDetails.imageUri = mDownloadedUriList.get(i);
					mGalleryImageList.add(lastIndex,imgDetails);
				}else{
					if(mBusinessAttributes !=null){
						mBusinessAttributes.setPhotos(mDownloadedUriList.get(i));
					}
				}
				
				
			}
			
			mBusinessImageAdapter.notifyDataSetChanged();
			mGalleryImageAdapter.notifyDataSetChanged();
		}
		
	}

	@Override
	public boolean onTouch(View view, MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_SCROLL)
		{
		mBusinessGridView.scrollBy(0,100);
		}
		
		return false;
	}
	
}
