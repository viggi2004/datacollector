package com.columbusagain.citibytes.adapters;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.columbusagain.citibytes.R;
import com.columbusagain.citibytes.helper.AreaDetails;
import com.columbusagain.citibytes.helper.Constants;
import com.columbusagain.citibytes.helper.HttpPost;
import com.columbusagain.citibytes.helper.UserProfile;
import com.columbusagain.citibytes.util.NetworkChecker;

public class PincodeRequestListAdapter extends BaseAdapter {
Context mContext ;
	
	private ArrayList<AreaDetails> mAreaList = new ArrayList<AreaDetails>();
	
	private Dialog mProgressDialog;
	
	public PincodeRequestListAdapter(Context context,ArrayList<AreaDetails> area_list) {
		this.mAreaList = area_list;
		this.mContext = context;
	}

	@Override
	public int getCount() {
		
		return mAreaList.size();
	}

	@Override
	public Object getItem(int position) {
		return mAreaList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if(convertView == null){
			LayoutInflater inflater = (LayoutInflater)mContext.getSystemService
				      (Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.list_content_pin_request,
					parent, false);
			viewHolder = new ViewHolder();
			viewHolder.mLinearLayout = (LinearLayout) convertView
					.findViewById(R.id.header);
			viewHolder.mPincodeText = (TextView) convertView
					.findViewById(R.id.pincode);
			viewHolder.mAreaNameText = (TextView) convertView
					.findViewById(R.id.area_name);
			convertView.setTag(viewHolder);
		}else{
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.mPincodeText.setText(mAreaList.get(position).mPin);
		viewHolder.mAreaNameText.setText(mAreaList.get(position).mAreaName);
		if(mAreaList.get(position).isHeader){
			viewHolder.mLinearLayout .setVisibility(LinearLayout.VISIBLE);
		}else{
			viewHolder.mLinearLayout .setVisibility(LinearLayout.GONE);
		}
		
		viewHolder.mLinearLayout .setOnClickListener(new OnClickListener() {
			
			
			@Override
			public void onClick(View v) {
				TextView tv=(TextView)v.findViewById(R.id.pincode);
				Log.i("pincode", tv.getText().toString());
				reqPin(tv.getText().toString());
			}
		});
		return convertView;
	}
	
	public class ViewHolder{
		LinearLayout mLinearLayout;
		TextView mPincodeText;
		TextView mAreaNameText;
	}
	
	private void reqPin(String pin){
		String email = UserProfile.EMAIL_ID;
		String pincode = pin;
		String city = "bangalore";
		
		JSONObject json = new JSONObject();
		try {
			json.put("email_id", email);
			json.put("pincode", pincode);
			json.put("city", city);
			mProgressDialog = new Dialog(mContext, R.style.ProgressDialog);
			mProgressDialog.setContentView(R.layout.progress_dialog);
			 boolean isNetworkConnected = NetworkChecker.isConnected(mContext);
 			if(!isNetworkConnected){
 			}else{
 			mProgressDialog.show();
 			new RequestPincode().execute(json.toString());
 			}
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private class RequestPincode extends AsyncTask<String, Void, String>{
		
		@Override
		protected String doInBackground(String... params) {
			HttpPost httpPost = new HttpPost(Constants.BASE_URL+"addNewPincodeRequest.php");
			httpPost.setParam("json", params[0]);
			return httpPost.executePost();
		}		

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if(mProgressDialog.isShowing())
			mProgressDialog.cancel();
			if(result!=null){
				try {
					JSONObject responseJson = new JSONObject(result);
					String status = responseJson.getString("status");
					if("success".equals(status)){
						startDialog();
					}
					Log.i("status", status);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}

		
		
	}
	
	public void startDialog(){	
	
	final Dialog dialog = new Dialog(mContext);
	dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
	dialog.setContentView(R.layout.custom_dialog_pincode_request);
	
	TextView text = (TextView) dialog.findViewById(R.id.text);
	text.setText("Pincode request has been sent for approval");
	
	Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonOK);
	// if button is clicked, close the custom dialog
	dialogButton.setOnClickListener(new OnClickListener() {
		@Override
		public void onClick(View v) {
			dialog.dismiss();
		}
	});

	dialog.show();
	}
	
	

}
