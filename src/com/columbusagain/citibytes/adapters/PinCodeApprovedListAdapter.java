package com.columbusagain.citibytes.adapters;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.columbusagain.citibytes.CAActivity;
import com.columbusagain.citibytes.HomeFragment;
import com.columbusagain.citibytes.R;
import com.columbusagain.citibytes.helper.AreaDetails;
import com.columbusagain.citibytes.helper.Constants;
import com.columbusagain.citibytes.helper.UserProfile;

public class PinCodeApprovedListAdapter extends BaseAdapter {
	
	Context mContext ;
	Activity mActivity;
	HashMap<String,String> mCurrentAreaDetails = new HashMap<String, String>();
	
	 private ArrayList<AreaDetails> mAreaList = new ArrayList<AreaDetails>();
	
	
	
	public PinCodeApprovedListAdapter(Context context,ArrayList<AreaDetails> area_list) {
		this.mAreaList = area_list;
		this.mContext = context;
		this.mActivity = (Activity)context;
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
			convertView = inflater.inflate(R.layout.list_content,
					parent, false);
			viewHolder = new ViewHolder();
			viewHolder.mContentLayout = (LinearLayout) convertView
					.findViewById(R.id.content);
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
		viewHolder.mAreaNameText.setTag(mAreaList.get(position).mPin);
		if(mAreaList.get(position).isHeader){
			viewHolder.mLinearLayout .setVisibility(LinearLayout.VISIBLE);
		}else{
			viewHolder.mLinearLayout .setVisibility(LinearLayout.GONE);
		}
		
		viewHolder.mContentLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				TextView tv = (TextView) v.findViewById(R.id.area_name);
				String area,pin;
				area = tv.getText().toString();
				pin = tv.getTag().toString();
				Log.i("SelectedArea", area);
				Log.i("SelectedPin", pin);
				Constants.AREA = area;
				Constants.PIN = pin;
				HomeFragment.northeast_lat = 0;
				HomeFragment.northeast_lng = 0;
				HomeFragment.southwest_lat = 0;
				HomeFragment.southwest_lng = 0;
				((CAActivity) mActivity).startHomeFragment();
				
			}
		});
		
		return convertView;
	}
	
	public class ViewHolder{
		LinearLayout mLinearLayout;
		LinearLayout mContentLayout;
		TextView mPincodeText;
		TextView mAreaNameText;
	}

}
