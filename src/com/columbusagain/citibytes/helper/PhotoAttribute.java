package com.columbusagain.citibytes.helper;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.columbusagain.citibytes.DataCollectionFragment;
import com.columbusagain.citibytes.R;
import com.columbusagain.citibytes.adapters.ImageAdapter;
import com.columbusagain.citibytes.datacollection.ImagesFragment;

import android.content.Context;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;


public class PhotoAttribute extends Attribute implements OnGridViewItemClickListener {
	
	private static final String ImagesFragment = null;
	
	private static final int ATTRIBUTE_IMAGE_PICK = 502;

	private WeakReference<AttributeOnClickListener> mAttributeListener;
	
	private ArrayList<ImageDetails> mAttributeImageList = new ArrayList<ImageDetails>();
	
	private ArrayList<Uri> mImageUriList = new ArrayList<Uri>();
	
	private GridView mPhotosGridView;
	
	private ImageAdapter mAttributeImageAdapter;
	
	private Context context;

	public PhotoAttribute(JSONObject jsonobject,String key,AttributeOnClickListener listener) throws JSONException {
		super(jsonobject,key);
		this.mAttributeListener = new WeakReference<AttributeOnClickListener>(listener);
		Log.i("PhotoAttribute", "photoAttribute Called");
	}

	@Override
	public View render(Context context,Fragment parentFragment) {
		this.context = context;
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.attribute_photos, null);
		mPhotosGridView = (GridView) view.findViewById(R.id.photo_grid);
		ImageDetails imgDetails = new ImageDetails();		
		imgDetails.isCheckBoxEnabled = false;
		imgDetails.isChecked = false;
		imgDetails.imageUri = null;
		mAttributeImageList.add(imgDetails);
		mAttributeImageAdapter = new ImageAdapter(this,context, mAttributeImageList,parentFragment,ATTRIBUTE_IMAGE_PICK,this.name);
		mPhotosGridView.setAdapter(mAttributeImageAdapter);
		//mPhotosGridView.setOnItemClickListener(this);
		Log.i("PhotoAttribute", "render Called");
		return view;
	}
	

	@Override
	public boolean isValid(Context context) {
		Log.i("PhotoAttribute", "isValid Called");
		if(mAttributeImageList.size() <= 1)		
		return false;
		
		return true;
	}

	@Override
	public void buildJson(JSONObject josnObject) {
		Log.i("PhotoAttribute", "buildJson Called");
		
	}
	
	public void viewModeSetup(boolean isViewMode){
		if(isViewMode){
		mAttributeImageList.remove(mAttributeImageList.size()-1);
		mAttributeImageAdapter.notifyDataSetChanged();
		}else{
			ImageDetails imgDetails = new ImageDetails();		
			imgDetails.isCheckBoxEnabled = false;
			imgDetails.isChecked = false;
			imgDetails.imageUri = null;
			mAttributeImageList.add(imgDetails);
			mAttributeImageAdapter.notifyDataSetChanged();
		}
		Log.i("PhotoAttribute", "viewModeSetup called");
	}
	
	public void setImage(Uri imageUri){
		int lastIndexList;
		if(mAttributeImageList.size() == 0)
			lastIndexList = 0;
		else{
			lastIndexList = mAttributeImageList.size()-1;
		if(mAttributeImageList.get(lastIndexList).imageUri != null)
			lastIndexList++;
		}
			
		if(imageUri!=null){
			//mImageUriList.add(lastIndexList,imageUri);
			ImageDetails imgDetails = new ImageDetails();		
			imgDetails.isCheckBoxEnabled = false;
			imgDetails.isChecked = false;
			imgDetails.imageUri = imageUri;
			mAttributeImageList.add(lastIndexList,imgDetails);
			mAttributeImageAdapter.notifyDataSetChanged();
	}
		if(mAttributeImageList.get(0).imageUri == null){
			mAttributeImageList.remove(0);
		mAttributeImageAdapter.notifyDataSetChanged();
		}
		
	}
	/*@Override
	public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
		Log.i("PhotoAttribute", "OnItemClickListener");
		AttributeOnClickListener listener = this.mAttributeListener.get();
		if(listener != null)
			listener.onClick(this);
		
	}*/

	@Override
	public void test(int imageType) {
		Log.i("PhotoAttribute", "OnclickListener");
		AttributeOnClickListener listener = this.mAttributeListener.get();
		if(listener != null)
			listener.onClick(this,imageType);
		
	}
	
	public void enableCheckBox(){
		for(int i=0;i<mAttributeImageList.size();i++){
			if(i!=mAttributeImageList.size()-1){
				mAttributeImageList.get(i).isCheckBoxEnabled = true;
			}
		}
		mAttributeImageAdapter.notifyDataSetChanged();
	}
	
	public void disableCheckBox(){
		for(int i=0;i<mAttributeImageList.size();i++){
				mAttributeImageList.get(i).isCheckBoxEnabled = false;
		}
		mAttributeImageAdapter.notifyDataSetChanged();
	}
	
	public void deleteSelectedImages(){
		for(int i=0;i<mAttributeImageList.size();i++){
				if(mAttributeImageList.get(i).isChecked){
					mAttributeImageList.remove(i);
					i--;
				}
		}
		mAttributeImageAdapter.notifyDataSetChanged();
	}
	
	public void addImageUri(JSONArray imageArray){
		for(int i=0;i<mAttributeImageList.size()-1;i++){
			imageArray.put(mAttributeImageList.get(i).imageUri);
	}
		
	}
	
	public void setPhotos(Uri uri){
		String[] uri_string = uri.toString().split("/");
		if(uri_string[uri_string.length-2].equalsIgnoreCase(this.name)){
			setImage(uri);
		}
	}
	

	@Override
	public void populateData(JSONObject jsonObject,LinearLayout container,Context context) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void disableEdit(){
		//mPhotosGridView.setClickable(false);
	}

}
