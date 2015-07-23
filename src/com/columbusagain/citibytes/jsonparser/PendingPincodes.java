package com.columbusagain.citibytes.jsonparser;

import java.util.ArrayList;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.widget.Toast;

import com.columbusagain.citibytes.helper.AreaDetails;

public class PendingPincodes {

	private String jsonString;

	private String status;

	private JSONObject pincodes;

	private int count;

	AreaDetails areaDetails;
	

	private ArrayList<AreaDetails> mPincodes = new ArrayList<AreaDetails>();


	public PendingPincodes(String jsonString) {
		this.jsonString = jsonString;
		//this.mContext = context;
	}
	
	

	public void requestParsing() {

		try {
			JSONObject json_response = new JSONObject(jsonString);
			status = json_response.getString("status");
			count = json_response.getInt("count");
			pincodes = json_response.getJSONObject("pincodes");
			if ("success".equals(status)) {
				if (count > 0) {
					Iterator<String> iter = pincodes.keys();
					while (iter.hasNext()) {
						String key = iter.next();

						try {
							// String pin = (String) pincodes.get(key);
							JSONArray area_arr = pincodes.getJSONArray(key);
							for (int i = 0; i < area_arr.length(); i++) {
								// pincodeDetails
								String area = area_arr.getString(i);
								areaDetails = new AreaDetails();
								areaDetails.mAreaName = area;
								areaDetails.mPin = key;
								if (i == 0) {
									areaDetails.isHeader = true;
								} else {
									areaDetails.isHeader = false;
								}
								mPincodes.add(areaDetails);
							}

						} catch (JSONException e) {
							// Something went wrong!
						}
					}

				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public String getStatus()  {
	
		return status;
	}
	
	public String getErrorMessage() throws JSONException{
		JSONObject json_response = new JSONObject(jsonString);
		String error_message = json_response.getString("error");
		return error_message;
	}

	public int getCount(){
		
		return count;
	}

	public ArrayList<AreaDetails> getPendingPincodes() {
		return mPincodes;
	}


	public ArrayList<AreaDetails> searchPins(String searchKey) {
		ArrayList<String> newSearchKey = new ArrayList<String>();
		ArrayList<AreaDetails> matchingList = new ArrayList<AreaDetails>();
		if (!Character.isLetter(searchKey.charAt(0))) {
			for (int i = 0; i < mPincodes.size(); i++) {
				if (mPincodes.get(i).mPin.startsWith(searchKey)) {
					matchingList.add(mPincodes.get(i));
				}
			}
		} else {
			for (int i = 0; i < mPincodes.size(); i++) {
				if (mPincodes.get(i).mAreaName.toLowerCase().startsWith(
						searchKey.toLowerCase())) {
					boolean isExist = false;
					for (int j = 0; j < newSearchKey.size(); j++) {
						if (newSearchKey.get(j).equals(mPincodes.get(i).mPin)) {
							isExist = true;
							break;
						}
					}
					if (!isExist)
						newSearchKey.add(mPincodes.get(i).mPin);
				}
			}
			for (int i = 0; i < newSearchKey.size(); i++) {

				for (int j = 0; j < mPincodes.size(); j++) {

					if (mPincodes.get(j).mPin.equals(newSearchKey.get(i))
							&& mPincodes.get(j).mAreaName.toLowerCase()
									.startsWith(searchKey.toLowerCase())) {
						mPincodes.get(j).isHeader = true;
						break;
					}

				}

			}

			for (int i = 0; i < newSearchKey.size(); i++) {

				for (int j = 0; j < mPincodes.size(); j++) {
					if (mPincodes.get(j).mPin.equals(newSearchKey.get(i))
							&& mPincodes.get(j).mAreaName.toLowerCase()
									.startsWith(searchKey.toLowerCase())) {
						matchingList.add(mPincodes.get(j));
					}

				}

			}

		}
		return matchingList;
	}
}
