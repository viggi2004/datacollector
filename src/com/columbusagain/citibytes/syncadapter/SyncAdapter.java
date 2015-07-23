package com.columbusagain.citibytes.syncadapter;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.accounts.Account;
import android.annotation.SuppressLint;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncResult;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore.MediaColumns;
import android.util.Log;

import com.columbusagain.citibytes.CAActivity;
import com.columbusagain.citibytes.database.CitiBytesDB;
import com.columbusagain.citibytes.datacollection.ImagesFragment;
import com.columbusagain.citibytes.helper.Constants;
import com.columbusagain.citibytes.helper.HttpPost;

/**
 * Handle the transfer of data between a server and an
 * app, using the Android sync adapter framework.
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter {
	private Context mContext;
	//private ArrayList<HashMap<String,String>> savedData;
	//public static ArrayList<Uri> mImages = new ArrayList<Uri>();
	//ArrayList<Uri> mImagesToUpload = new ArrayList<Uri>();
	//mImagesToUpload = ImagesFragment.mImages;
	
	private boolean isUploaded = true;
	
	
    // Global variables
    // Define a variable to contain a content resolver instance
    ContentResolver mContentResolver;
    /**
     * Set up the sync adapter
     */
    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        /*
         * If your app uses a content resolver, get an instance of it
         * from the incoming Context
         */
        mContentResolver = context.getContentResolver();
        mContext=context;
      //  this.mImagesToUpload = Constants.mImages;
        //Log.i("SyncAdapter", "constructor :"+ImagesFragment.mImages.size());
    }
    /**
     * Set up the sync adapter. This form of the
     * constructor maintains compatibility with Android 3.0
     * and later platform versions
     */
	@SuppressLint("NewApi")
	public SyncAdapter(
            Context context,
            boolean autoInitialize,
            boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        /*
         * If your app uses a content resolver, get an instance of it
         * from the incoming Context
         */
        mContentResolver = context.getContentResolver();
        mContext=context;
    }
	@Override
	public void onPerformSync(Account account, Bundle extras, String authority,
			ContentProviderClient provider, SyncResult syncResult) {
		
		String sync_type = extras.getString("sync_type");
		if(sync_type.equalsIgnoreCase("add_business")){
			Log.i("SyncAdapter", "add business");
			addBusiness(syncResult);
		}else if(sync_type.equalsIgnoreCase("delete_business")){
			String businessId = extras.getString("business_id");
			Log.i("SyncAdapter", "delete business :" +businessId);
			deleteBusiness(businessId,syncResult);
		}

			
			
		
	}
	
	private void deleteBusiness(String businessId,SyncResult syncResult){
		try{
			HttpPost httpPost = new HttpPost(Constants.BASE_URL+"deleteBusiness.php");
			httpPost.setParam("business_id", businessId);
			
			String result = httpPost.executePost();
			
			if(result != null){
				JSONObject responseObject = new JSONObject(result);
				String status = responseObject.getString("status");
				if(status.equalsIgnoreCase("success")){
					syncResult.stats.numIoExceptions=0;
				}else{
					syncResult.stats.numIoExceptions++;
				}
			
			}else{
				syncResult.stats.numIoExceptions++;
			}
		}catch (Exception e){
			syncResult.stats.numIoExceptions++;
		}
	
	}
	
	private void addBusiness( SyncResult syncResult){
		Log.i("SyncAdapter", "onPerformSync");
		boolean isErrorOccured=false;
		CitiBytesDB citiBytesDb = new CitiBytesDB(mContext);
		ArrayList<HashMap<String,String>> allData = new ArrayList<HashMap<String,String>>();
		allData = citiBytesDb.getAllData();
		for(int i=0;i<allData.size();i++){
			isUploaded = true;
			String schema_name = null;
			HashMap<String,String> businessDetail = allData.get(i);
			String imageDetails = businessDetail.get("image_details");
			String mBusinessId = businessDetail.get("business_id");
			JSONObject business_details_json;
			try {
				business_details_json = new JSONObject(businessDetail.get("business_data"));
				if(business_details_json.has("schema_name"))
				schema_name = business_details_json.getString("schema_name");
				business_details_json.remove("schema_name");
				
				JSONArray image_path_array = business_details_json.getJSONArray("photo_url");
				//JSONArray schema_arr = business_details_json.getJSONArray("business_category");
				JSONArray imageArr = new JSONArray(imageDetails);
				JSONArray ErrorImageArr = new JSONArray();
				for(int j=0;j<imageArr.length();j++){
					String file_path = uploadImageToServer(Uri.parse(imageArr.getString(j)));
					if(file_path !=null && !file_path.equals("error") ){
						Log.i("Sync Adapter|||", file_path);
						image_path_array.put(file_path);
					}else{
						ErrorImageArr.put(imageArr.get(j));
						//Log.i("Sync Adapter|||", file_path);
						isUploaded = false;
					}
				}
				business_details_json.put("photo_url", image_path_array);
				citiBytesDb.updateBusinessDetails(mBusinessId, business_details_json.toString(), ErrorImageArr.toString());
				//citiBytesDb.updatePhotoDetails(mBusinessId,ErrorImageArr.toString());
				if(isUploaded){
					
					HttpPost httpPost = new HttpPost(
							Constants.BASE_URL+"saveOtherCoreAttributes.php");
					httpPost.setParam("json", business_details_json.toString());
					httpPost.setParam("business_id", mBusinessId);
					if(schema_name != null)
					httpPost.setParam("schema_name", schema_name);
					
					Log.i("Syncsdapter","business_details :"+business_details_json.toString());
					Log.i("Syncsdapter","business_id :"+mBusinessId);
					Log.i("Syncsdapter","schema_name :"+schema_name);
					
					String response = httpPost.executePost();
					//Log.i("response",response);
					if(response != null){
						Log.i("Syncsdapter",response);
					JSONObject resp_status=new JSONObject(response);
					String status=resp_status.getString("status");
					
					if(status.equals("success")){
						Log.i("CitiBytes", "success");
						
						String updated_time = resp_status.getString("last_updated_time");
						HashMap<String,String> businessTimeDetail = new HashMap<String,String>();
						businessTimeDetail = citiBytesDb.getAnalyticsData(businessDetail.get("business_id"));
						JSONObject analyticsJson = new JSONObject(businessTimeDetail.get("timer_data"));
						analyticsJson.put("date", updated_time);
						
						HttpPost httpPostObj = new HttpPost(Constants.BASE_URL+"analytics.php");
						httpPostObj.setParam("data", analyticsJson.toString());
						String analytics_response = httpPostObj.executePost();
						if(analytics_response != null){
						Log.i("SyncAdapter", "timer data :"+businessTimeDetail.get("timer_data"));
						
						try {
							JSONObject responseJson = new JSONObject(analytics_response);
							String analytics_status = responseJson.getString("status");
							Log.i("syncActivity", "status :"+status);
							if(analytics_status.equalsIgnoreCase("success")){
								Log.i("status", status);
								citiBytesDb.deleteRow(businessDetail.get("business_id"));
								deleteImageDirectory(businessDetail.get("business_id"));
								citiBytesDb.deleteTimerRow(businessTimeDetail.get("timer_data"));								
								
							}else{
								isErrorOccured = true;
							}
						} catch (JSONException e) {
							e.printStackTrace();
							isErrorOccured = true;
						}
						}else{
							isErrorOccured = true;
						}
						
						
						//isErrorOccured=false;
						Log.i("Status","uploaded successfully");
					}else{
						isErrorOccured=true;
						Log.i("Response Code",status);
						Log.i("CitiBytes", "Error");
					}
					}else{
						isErrorOccured=true;
					}
				}
				else{
					Log.i("Status","uploaded not successfully");
					isErrorOccured=true;
					
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}			
			if(isErrorOccured){					
				syncResult.stats.numIoExceptions++;
			}else{
				syncResult.stats.numIoExceptions=0;
			}
			
	}
	
	
	private void deleteImageDirectory(String folder_name){
		Log.i("DataCollectionFragment", "deleteFile");
		File file_path = new File(Environment.getExternalStorageDirectory()
				+ "/DCIM/", folder_name);
		File[] pictures_directory = file_path.listFiles();
		
		//No photos available
		if(pictures_directory == null)
			return;
		
		//Iterate over the list of all files under each picture directory
		for(int i=0;i<pictures_directory.length;i++){
			File pictureDirectory = pictures_directory[i];
			File[] pictures = pictureDirectory.listFiles();
			
			//Delete each picture under a directory
			for(int j=0;j<pictures.length;j++){
				File picture = pictures[j];
				picture.delete();
			}
	
			pictureDirectory.delete();
		}
			
		file_path.delete();
	}
	
	public String getAbsolutePath(Uri uri) {
        String[] projection = { MediaColumns.DATA };
        ContentResolver cr = mContentResolver;
        cr.notifyChange(uri, null);
        Cursor cursor =  cr.query(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else
            return null;
    }
	//bitmap = decodeSampledBitmapFromResource
	
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
	
	private String uploadImageToServer(Uri imageUri) {
		
		File imgFile = new  File(imageUri.getPath());
		byte[] image ;
		if(imgFile.exists()){
			 						 
			 int file_size = (int)imgFile.length();
			 if(file_size>(1024*700)){		
				 Log.i("SyncAdapter", "bitmap upload");
				 Bitmap bitmap =convertBitmap(imgFile);
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
				 ByteArrayOutputStream stream = new ByteArrayOutputStream();
				 bitmap.compress(CompressFormat.JPEG,80, stream);	
				 image = stream.toByteArray();
			 }else{		
				 Log.i("SyncAdapter", "file upload");
				 image = new byte[file_size];
				 FileInputStream fileInputStream;
				try {
					fileInputStream = new FileInputStream(imgFile);
					fileInputStream.read(image);
					 Bitmap bitmap =convertBitmap(imgFile);
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
					 ByteArrayOutputStream stream = new ByteArrayOutputStream();
					 bitmap.compress(CompressFormat.JPEG,100, stream);	
					 image = stream.toByteArray();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
	               
			 }
			 
				Log.i("ImageUri",imageUri.toString());
				return upload(image, imageUri.toString());
		}
		return null;
		
	}
	
	private int exifToDegrees(int exifOrientation) {        
	    if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) { return 90; } 
	    else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {  return 180; } 
	    else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {  return 270; }            
	    return 0;    
	 }
	
	private Bitmap convertBitmap(File file)   {
		 
        Bitmap bitmap=null;
        BitmapFactory.Options bfOptions=new BitmapFactory.Options();
        bfOptions.inDither=false;                     //Disable Dithering mode
        bfOptions.inPurgeable=true;                   //Tell to gc that whether it needs free memory, the Bitmap can be cleared
        bfOptions.inInputShareable=true;              //Which kind of reference will be used to recover the Bitmap data after being clear, when it will be used in the future
        bfOptions.inTempStorage=new byte[32 * 1024]; 
 
 
        FileInputStream fs=null;
        try {
            fs = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
 
        try {
            if(fs!=null)
            {
                bitmap=BitmapFactory.decodeFileDescriptor(fs.getFD(), null, bfOptions);
            }
            } catch (IOException e) {
 
            e.printStackTrace();
        } finally{ 
            if(fs!=null) {
                try {
                    fs.close();
                } catch (IOException e) {
 
                    e.printStackTrace();
                }
            }
        }
 
        return bitmap;
    }
	
	private String uploadImage(byte[] file, String userid,String filePath) {
		
		String[] file_path=filePath.split("/");
		int path_size=file_path.length;
		Log.d("CitiBytes", "File " + file);
		String boundary = "-----" + System.currentTimeMillis() + "-----";
		String LINE_FEED = "\r\n";
		String response="";
		//Boolean response = false;
		String urlString = Constants.DEV_URL+"uploadImage.php";
		String charset = "UTF-8";
		URL url = null;
		String filepath = "file_path";
		HttpURLConnection httpUrlConnection = null;
		OutputStream outputStream = null;
		PrintWriter writer;
		InputStream inputStream = null;
		FileInputStream fis = null;
		try {
			Log.i("CitiBytes","Message1");
			url = new URL(urlString);
			Log.i("CitiBytes","Message1a");
			httpUrlConnection = (HttpURLConnection) url.openConnection();
			Log.i("CitiBytes","Message2");
			httpUrlConnection.setDoOutput(true);
			httpUrlConnection.setDoInput(true);
			httpUrlConnection.setRequestMethod("POST");
			httpUrlConnection.setRequestProperty("Content-Type",
					"multipart/form-data; boundary=" + boundary);
			Log.i("CitiBytes","Message2");
			httpUrlConnection.connect();
			Log.i("CitiBytes","Message3");
			outputStream = httpUrlConnection.getOutputStream();
			Log.i("SyncAdapter", ""+httpUrlConnection.getResponseCode());
			writer = new PrintWriter(new OutputStreamWriter(outputStream,
					charset), true);
			writer.append("--" + boundary).append(LINE_FEED);
			writer.append(
					"Content-Disposition: form-data; name=\"" + filepath + "\"")
					.append(LINE_FEED);
			writer.append("Content-Type: text/plain; charset=" + charset)
					.append(LINE_FEED);
			writer.append(LINE_FEED);
			writer.append(file_path[path_size-3]+"/"+file_path[path_size-2]+"/"+file_path[path_size-1]).append(LINE_FEED);
			writer.append("--" + boundary).append(LINE_FEED);
			
			Log.i("SyncAdapter", "File Path : "+file_path[path_size-3]+"/"+file_path[path_size-2]+"/"+file_path[path_size-1]);
			
			writer.append(
					"Content-Disposition: form-data; name=\"file\"; filename=\""
							+ file_path[path_size-1] + "\"").append(LINE_FEED);
			Log.i("FilePath",filePath);
			writer.append("Content-Type: text/json").append(LINE_FEED);
			writer.append("Content-Transfer-Encoding: binary")
					.append(LINE_FEED);
			//writer.append("Content-");
			writer.append(LINE_FEED);
			writer.flush();
			Log.i("CitiBytes","Message4");
			outputStream.write(file);
			outputStream.flush();

			writer.append(LINE_FEED);
			writer.append("--" + boundary + "--").append(LINE_FEED);
			writer.flush();
			writer.close();
			outputStream.close();
			Log.i("CitiBytes","Message5");

			if (httpUrlConnection.getResponseCode() == HttpsURLConnection.HTTP_OK) {
				inputStream = new BufferedInputStream(
						httpUrlConnection.getInputStream());
				BufferedReader bufferedReader = new BufferedReader(
						new InputStreamReader(inputStream));
				StringBuilder responseMessage = new StringBuilder();
				String line = "";
				while ((line = bufferedReader.readLine()) != null)
					responseMessage.append(line);
				inputStream.close();

				String responseMsg = responseMessage.toString();
				Log.d("CitiBytes", "Response " + responseMsg);
				JSONObject responseObject = new JSONObject(responseMsg);
				response = responseObject.getString("status");
				if(response.equals("ok")){
					response = responseObject.getString("file_path");
				}
				
			}else{
				Log.d("CitiBytes",String.valueOf(httpUrlConnection.getResponseCode()));
			}

		} catch (IOException ie) {
			ie.printStackTrace();
			Log.e("CitiBytes", "iofailed");
		} catch (JSONException e) {
			Log.e("CitiBytes", "JsonParsing failed", e);
		} finally {
			httpUrlConnection.disconnect();
		}
		//if(response.equals("ok"))
		return response;
		//else return false;
	}
	
	private String upload(byte[] data,String filePath) {
		String[] file_path=filePath.split("/");
		int path_size=file_path.length;
		String filename = "file_path";
		//String filePath = "";
		String boundary = "-----" + System.currentTimeMillis() + "-----";
		String LINE_FEED = "\r\n";
		String response = null;
		String charset = "UTF-8";
		URL url = null;
		HttpURLConnection httpUrlConnection = null;
		OutputStream outputStream = null;
		PrintWriter writer;
		InputStream inputStream = null;
		try {
			url = new URL(Constants.BASE_URL+"uploadImage.php");
			httpUrlConnection = (HttpURLConnection) url.openConnection();
			httpUrlConnection.setDoOutput(true);
			httpUrlConnection.setDoInput(true);
			httpUrlConnection.setRequestMethod("POST");
			httpUrlConnection.setRequestProperty("Content-Type",
					"multipart/form-data; boundary=" + boundary);
			httpUrlConnection.connect();
			outputStream = httpUrlConnection.getOutputStream();

			writer = new PrintWriter(new OutputStreamWriter(outputStream,
					charset), true);

			writer.append("--" + boundary).append(LINE_FEED);
			writer.append(
					"Content-Disposition: form-data; name=\"" + filename + "\"")
					.append(LINE_FEED);
			writer.append("Content-Type: text/plain; charset=" + charset)
					.append(LINE_FEED);
			writer.append(LINE_FEED);
			writer.append(file_path[path_size-3]+"/"+file_path[path_size-2]+"/"+file_path[path_size-1]).append(LINE_FEED);

			//writer.append(LINE_FEED);
			
			Log.i("SyncAdapter", "File Path : "+file_path[path_size-3]+"/"+file_path[path_size-2]+"/"+file_path[path_size-1]);

			writer.append("--" + boundary).append(LINE_FEED);
			writer.append(
					"Content-Disposition: form-data; name=\"file\"; filename=\""
							+ file_path[path_size-1] + "\"").append(LINE_FEED);
			writer.append("Content-Type: image/jpeg").append(LINE_FEED);
			writer.append("Content-Transfer-Encoding: binary")
					.append(LINE_FEED);
			writer.append(LINE_FEED);
			writer.flush();
			outputStream.write(data);
			outputStream.flush();
			writer.append(LINE_FEED);
			writer.flush();

			writer.append(LINE_FEED);
			writer.append("--" + boundary + "--").append(LINE_FEED);
			writer.flush();
			writer.close();
			outputStream.close();
			
			Log.i("responsecode",""+httpUrlConnection.getResponseCode());

			if (httpUrlConnection.getResponseCode() == HttpsURLConnection.HTTP_OK) {
				inputStream = new BufferedInputStream(
						httpUrlConnection.getInputStream());
				BufferedReader bufferedReader = new BufferedReader(
						new InputStreamReader(inputStream));
				StringBuilder responseMessage = new StringBuilder();
				String line = "";
				while ((line = bufferedReader.readLine()) != null)
					responseMessage.append(line);
				inputStream.close();
				response = responseMessage.toString();
				Log.d("CitiBytes", "Response " + response);
				JSONObject responseObject = new JSONObject(response);
				response = responseObject.getString("status");
				if(response.equals("ok")){
					response = responseObject.getString("file_path");
				}
			}
		} catch (IOException ie) {
			ie.printStackTrace();
		} catch (JSONException e) {
			Log.e("CitiBytes", "JsonParsing failed", e);
		} finally {
			httpUrlConnection.disconnect();
		}
		return response;
	}
}
