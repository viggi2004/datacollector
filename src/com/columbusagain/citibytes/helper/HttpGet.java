package com.columbusagain.citibytes.helper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import android.util.Log;

public class HttpGet {
	private String mUrl;

	public HttpGet(String urlString) {
		mUrl = urlString;
	}

	public String executeGet() throws JsonGetException {
		URL url = null;
		HttpURLConnection conn = null;
		InputStream is = null;
		String response = null;
		BufferedReader reader = null;
		try {
			url = new URL(mUrl);
		} catch (MalformedURLException e) {
			e.printStackTrace();
			throw new JsonGetException("Url error!");
		}
		try {
			conn = (HttpURLConnection) url.openConnection();
		} catch (IOException e) {
			e.printStackTrace();
			throw new JsonGetException("open connection error!");
		}
		//conn.setRequestProperty("Accept-Encoding", "identity");
		try {
			conn.setRequestMethod("GET");
		} catch (ProtocolException e) {
			e.printStackTrace();
			throw new JsonGetException("GET error!");
		}
		conn.setDoInput(true);
		// Stream a request body whose length is not known in advance. Old
		// HTTP/1.0 only servers may not support this mode.
		conn.setChunkedStreamingMode(0);
		try {
			conn.connect();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new JsonGetException("connect error!");
		}
		try {
			if (conn.getResponseCode() != 200) {
				Log.d("HttpGet", ""+conn.getResponseCode());
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			throw new JsonGetException("IOError");
		}

		try {

			is = conn.getInputStream();
			reader = new BufferedReader(new InputStreamReader(is));
		} catch (IOException e) {
			is = conn.getErrorStream();
			e.printStackTrace();
			throw new JsonGetException("get input stream error!");
		}
		Log.d("http response", reader.toString());
		/*Log.d("http response",
				conn.getContentLength() + " " + conn.getContentEncoding());*/
		StringBuilder sb = new StringBuilder();
		String line = "";
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new JsonGetException("IOException");
		}
		response = sb.toString();
		/*if(response == ""){
			Log.i("HttpGet","Empty response");
			JSONObject empty_response_object =new JSONObject();
			String status = "error";
			String message = "empty response";
			try {
				empty_response_object.put("status", status);
				empty_response_object.put("message", message);
				response = empty_response_object.toString();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}*/
		
		Log.d("Response", response);
		try {
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
			throw new JsonGetException("IOException");
		}
		conn.disconnect();
		return response;

	}
}
