package com.columbusagain.citibytes.helper;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;

import android.util.Log;

public class HttpPost {

	private String mUrl;

	private static final String CHARSET = "UTF-8";

	Map<String, String> paramsMap = new LinkedHashMap<String, String>();

	public HttpPost(String urlString) {
		mUrl = urlString;
	}

	public String executePost() {
		String response = null;
		String queryParam = null;
		URL url = null;
		HttpURLConnection httpUrlConnection = null;
		OutputStream outputStream = null;
		InputStream inputStream = null;
		try {
			queryParam = getParams();
			url = new URL(mUrl);
			httpUrlConnection = (HttpURLConnection) url.openConnection();
			httpUrlConnection.setDoOutput(true);
			httpUrlConnection.setDoInput(true);
			//httpUrlConnection.setConnectTimeout(8000);
			httpUrlConnection.setRequestMethod("POST");
			httpUrlConnection.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			httpUrlConnection.setRequestProperty("Content-Length", ""
					+ queryParam.length());
			httpUrlConnection.connect();

			outputStream = new BufferedOutputStream(
					httpUrlConnection.getOutputStream());
			outputStream.write(queryParam.getBytes(CHARSET));
			outputStream.flush();
			outputStream.close();
			Log.i("Response Code",""+httpUrlConnection.getResponseCode());
			if (httpUrlConnection.getResponseCode() != 200) {
				
				throw new Exception(httpUrlConnection.getResponseMessage());
			
			}
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
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(httpUrlConnection !=null)
			httpUrlConnection.disconnect();
		}
		//Log.i("response",response);
		return response;
	}

	public void setParam(String key, String value) {
		paramsMap.put(key, value);
	}

	private String getParams() {
		StringBuilder param = new StringBuilder();
		try {
			for (Map.Entry<String, String> entry : paramsMap.entrySet()) {
				param.append(entry.getKey());
				param.append("=");
				param.append(URLEncoder.encode(entry.getValue(), CHARSET));
				param.append("&");
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		// To remove the last appended &
		return param.substring(0, param.length() - 1);
	}
}
