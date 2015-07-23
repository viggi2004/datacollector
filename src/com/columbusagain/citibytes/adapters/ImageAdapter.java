package com.columbusagain.citibytes.adapters;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.columbusagain.citibytes.CAActivity;
import com.columbusagain.citibytes.R;
import com.columbusagain.citibytes.datacollection.ImagesFragment;
import com.columbusagain.citibytes.helper.ImageDetails;
import com.columbusagain.citibytes.helper.OnGridViewItemClickListener;
import com.columbusagain.citibytes.helper.PhotoAttribute;

public class ImageAdapter extends BaseAdapter {

	private ArrayList<ImageDetails> mBitmapDetailList;

	private ArrayList<Integer> mRemovedIndexArray = new ArrayList<Integer>();

	private static final int IMAGE_CAPTURE = 500;

	private int IMAGE_CAPTURE_TYPE;

	private Context mContext;
	
	private OnGridViewItemClickListener listener;

	// private ArrayList<Uri> mBitmapList;

	private Uri mImageUri, mOutputFileUri;

	private String mImagePath;

	private Fragment mParentFragmentFragment;

	public ImageAdapter(OnGridViewItemClickListener listener,Context context, ArrayList<ImageDetails> bitmapList,
			Fragment parentFragment, int image_type,String path) {
		this.mBitmapDetailList = bitmapList;
		this.mContext = context;
		this.listener = listener;
		// this.mBitmapList = bitmapList;
		this.mParentFragmentFragment = parentFragment;
		this.IMAGE_CAPTURE_TYPE = image_type;
		this.mImagePath = path;
		/*if(image_type == 500){
			mImagePath = "Card";
		}else if(image_type == 501){
			mImagePath = "Other";
		}else if(image_type == 501){
			
		}*/
			
	}

	/*
	 * public int addImage(Uri imageUri) { int lastElementIndex =
	 * mBitmapList.size() - 1; Log.i("ImageUri", imageUri.getPath());
	 * Log.i("LastIndex1", "" + lastElementIndex);
	 * mBitmapList.add(lastElementIndex, imageUri); lastElementIndex =
	 * mBitmapList.size() - 1; Log.i("LastIndex2", "" + lastElementIndex);
	 * Log.d("ImageAdapter", "bitmap list" + mBitmapList.toString() + " size" +
	 * lastElementIndex); return lastElementIndex; }
	 */

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mBitmapDetailList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mBitmapDetailList.get(position);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		Log.i("MainActivity", "GetViewCalled, position = " + position);
		Log.i("ImageAdapter", "bitmap size" + mBitmapDetailList.size());

		Uri imageUri = mBitmapDetailList.get(position).imageUri;
		if(imageUri != null)
		Log.i("ImageUri",imageUri.toString());
		ViewHolder viewHolder = null;
		if (convertView == null || imageUri == null) {
			LayoutInflater inflater = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.photos, null);

			viewHolder = new ViewHolder();
			viewHolder.mImageView = (ImageButton) convertView
					.findViewById(R.id.image);
			viewHolder.mCheckBox = (CheckBox) convertView
					.findViewById(R.id.check_box);			
			convertView.setTag(viewHolder);
		}else
			viewHolder = (ViewHolder) convertView.getTag();
		
		//ViewHolder holder = (ViewHolder) convertView.getTag();

		Log.i("Getview", "position " + position + "bitmaps count "
				+ mBitmapDetailList.size());
		if(mBitmapDetailList.get(position).isCheckBoxEnabled){
			viewHolder.mCheckBox.setVisibility(CheckBox.VISIBLE);
		}else{
			viewHolder.mCheckBox.setChecked(false);
			viewHolder.mCheckBox.setVisibility(CheckBox.GONE);
		}
		
		viewHolder.mCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					mBitmapDetailList.get(position).isChecked = true;
				}else{
					mBitmapDetailList.get(position).isChecked = false;
				}
				
			}
		});
		if (imageUri != null) {
			new BitmapWorkerTask(viewHolder.mImageView).execute(imageUri);
		}
		
		viewHolder.mImageView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (position == mBitmapDetailList.size() - 1) {
					listener.test(IMAGE_CAPTURE_TYPE);
				}
				
			}
			
		});

	/*	viewHolder.mImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (position == mBitmapDetailList.size() - 1) {
					//Calendar calendar = Calendar.getInstance();
					//String img_name = String.valueOf(calendar.getTimeInMillis());
					String file_name = "";
				/*	if(IMAGE_CAPTURE_TYPE == 500){
						file_name = String.valueOf(ImagesFragment.mBusinessImages);
						ImagesFragment.mBusinessImages++;
					}else if(IMAGE_CAPTURE_TYPE == 501){
						file_name = String.valueOf(ImagesFragment.mGalleryImages);
						ImagesFragment.mGalleryImages++;
					}else if(IMAGE_CAPTURE_TYPE == 502){
						file_name = String.valueOf(ImagesFragment.mAttributeImages);
						ImagesFragment.mAttributeImages++;
					}*/
					
					
					/*mOutputFileUri = ((BaseDrawerActivity)mContext).setImageUri(((BaseDrawerActivity)mContext).mBusinessId , mImagePath, file_name+".jpg");
					/*mOutputFileUri = ((BaseDrawerActivity) mContext)
							.setImageUri("PictureResize", "Demo_"
									+ IMAGE_CAPTURE_TYPE, "image" + position
									+ ".jpg");*/
				/*	Intent camIntent = ((BaseDrawerActivity) mContext)
							.getImageCaptureIntent(0, mOutputFileUri);
					/*
					 * Intent camIntent = new Intent(
					 * MediaStore.ACTION_IMAGE_CAPTURE);
					 */
		/*			camIntent.putExtra(MediaStore.EXTRA_OUTPUT, mOutputFileUri);
					mParentFragmentFragment.startActivityForResult(camIntent,
							IMAGE_CAPTURE_TYPE);

				} else {
					// mBitmapList.remove(position);
					// mRemovedIndexArray.add(position);
					// notifyDataSetChanged();

				}

			}
		});*/

		return convertView;
	}
	

	static class ViewHolder {
		public ImageButton mImageView;
		public CheckBox mCheckBox;
		public int position;
		// public
	}

	class BitmapWorkerTask extends AsyncTask<Uri, Void, Bitmap> {
		private final WeakReference<ImageView> imageViewReference;

		// private int data = 0;

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
			// ImageView imageView = imageViewReference.get();
			// try {
			/*
			 * bitmap = android.provider.MediaStore.Images.Media.getBitmap(
			 * getContentResolver(), imageUri);
			 */
			// Log.i("Image Height & Width",String.valueOf(imageView.getWidth()+" "+imageView.getHeight()));
			bitmap = decodeSampledBitmapFromResource(imageUri, 96, 96);
			if(bitmap == null)
				return bitmap;
			try {
				ExifInterface exif = new ExifInterface(imageUri.getPath());
				int rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
				int rotationInDegrees = exifToDegrees(rotation);
				Matrix matrix = new Matrix();
				if (rotation != 0f) {matrix.preRotate(rotationInDegrees);}
				bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
				Log.i("ImageAdapter", "Current Orientation :" +rotationInDegrees);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			/*
			 * } catch (FileNotFoundException e) { // TODO Auto-generated catch
			 * block e.printStackTrace(); } catch (IOException e) { // TODO
			 * Auto-generated catch block e.printStackTrace(); }
			 */
			return bitmap;
		}
		
		private int exifToDegrees(int exifOrientation) {        
		    if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) { return 90; } 
		    else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {  return 180; } 
		    else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {  return 270; }            
		    return 0;    
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

	/*
	 * public Uri setImageUri(String main_dir, String sub_dir, String file_name)
	 * { File file_main = new File(Environment.getExternalStorageDirectory() +
	 * "/DCIM/", main_dir); file_main.mkdir(); File file_sub = new
	 * File(file_main, sub_dir); file_sub.mkdir(); File file_output = new
	 * File(file_sub, file_name); // File file = new
	 * File(Environment.getExternalStorageDirectory() + // dir_name, file_name);
	 * Uri imgUri = Uri.fromFile(file_output); this.mImageUri = imgUri;
	 * this.mImagePath = file_output.getAbsolutePath(); return imgUri; }
	 * 
	 * public Uri getImageUri() { return mImageUri; }
	 * 
	 * public String getImagePath() { return mImagePath; }
	 * 
	 * public ArrayList<Integer> getremovePhotos() { return mRemovedIndexArray;
	 * }
	 */

}
