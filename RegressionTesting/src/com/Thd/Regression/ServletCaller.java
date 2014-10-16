package com.Thd.Regression;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

public class ServletCaller {
	static String ReadData, INPUTXMLPATH, HOSTNAME, PROTOCOL, URL, USERNAME,PASSWORD, PROGID;
	static int PORT;
	static Properties Property = new Properties();

	public static void loadProperties() throws IOException {
		Property.load(ServletCaller.class
				.getResourceAsStream("/config.properties"));
		INPUTXMLPATH = Property.getProperty("InputXmlPath");
		HOSTNAME = Property.getProperty("HostName");
		PORT = Integer.parseInt((String) Property.getProperty("Port"));
		PROTOCOL = Property.getProperty("Protocol");
		URL = Property.getProperty("Url");
		USERNAME = Property.getProperty("UserName");
		PASSWORD = Property.getProperty("Password");
		PROGID = Property.getProperty("ProgId");

	}

	

	public static String getExtnHostOrderref(String searchStr) throws IOException
	{
		int startPosition=ReadData.indexOf(searchStr)+searchStr.length();
		String data=ReadData.substring(startPosition+1, startPosition+4);
		return data;
	}
	public static String convertXmlToString() throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(new File(INPUTXMLPATH)));
		String line;
		StringBuilder readContent = new StringBuilder();
		while ((line = br.readLine()) != null) {
			readContent.append(line.trim()+"\n");
		}
		br.close();
		return readContent.toString();
	}

	public static void sendPostRequest(String serviceName, String isService,String Data) throws IOException {
		/*
		 * HttpHost proxy = new HttpHost("127.0.0.1"); DefaultHttpClient
		 * httpClient = new DefaultHttpClient();
		 * httpClient.getParams().setParameter("8888", proxy);
		 */
		ReadData=Data;
		HttpHost proxy = new HttpHost("127.0.0.1");
		DefaultHttpClient httpClient = new DefaultHttpClient();
		httpClient.getParams().setParameter("8888", proxy);
		System.setProperty("http.proxyHost", "127.0.0.1");
	    System.setProperty("https.proxyHost", "127.0.0.1");
	    System.setProperty("http.proxyPort", "8888");
	    System.setProperty("https.proxyPort", "8888");
		HttpHost host = new HttpHost(HOSTNAME, PORT, PROTOCOL);
		HttpPost httpPost = new HttpPost(URL);
		List<BasicNameValuePair> nvps = new ArrayList<BasicNameValuePair>();
		nvps.add(new BasicNameValuePair("ApiName", serviceName));
		nvps.add(new BasicNameValuePair("InteropApiData", Data));
		nvps.add(new BasicNameValuePair("InteropApiName", serviceName));
		nvps.add(new BasicNameValuePair("IsFlow", isService));
		nvps.add(new BasicNameValuePair("YFSEnvironment.userId", USERNAME));
		nvps.add(new BasicNameValuePair("YFSEnvironment.progId", PROGID));
		nvps.add(new BasicNameValuePair("YFSEnvironment.password", PASSWORD));

		UrlEncodedFormEntity entity = null;
		try {
			entity = new UrlEncodedFormEntity(nvps, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		httpPost.setEntity(entity);
		HttpResponse postResponse = null;
		try {
			postResponse = httpClient.execute(host, httpPost);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(postResponse.toString());
		String res = "";
		try {
			res = EntityUtils.toString(postResponse.getEntity());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(res);
	}

}
