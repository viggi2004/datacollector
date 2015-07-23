package com.columbusagain.citibytes.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.columbusagain.citibytes.R;
import com.columbusagain.citibytes.helper.AreaDetails;

public class PinCodePendingListAdapter extends BaseAdapter{

Context mContext ;
	
    private ArrayList<AreaDetails> mAreaList = new ArrayList<AreaDetails>();
	
	public PinCodePendingListAdapter(Context context,ArrayList<AreaDetails> area_list) {
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
			convertView = inflater.inflate(R.layout.list_content,
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
		return convertView;
	}
	
	public class ViewHolder{
		LinearLayout mLinearLayout;
		TextView mPincodeText;
		TextView mAreaNameText;
	}


}
