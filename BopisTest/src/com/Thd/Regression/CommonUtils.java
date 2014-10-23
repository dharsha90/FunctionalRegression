package com.Thd.Regression;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import java.util.Properties;


public class CommonUtils {
	static String ReadData, HOSTNAME, PROTOCOL, URL, USERNAME, PASSWORD,PROGID;
	static int PORT;
	static Properties Property = new Properties();

	public static void loadProperties() throws IOException {
		Property.load(CommonUtils.class.getResourceAsStream("/config.properties"));
		HOSTNAME = Property.getProperty("HostName");
		PORT = Integer.parseInt((String) Property.getProperty("Port"));
		PROTOCOL = Property.getProperty("Protocol");
		URL = Property.getProperty("Url");
		USERNAME = Property.getProperty("UserName");
		PASSWORD = Property.getProperty("Password");
		PROGID = Property.getProperty("ProgId");

	}

	public static String getExtnHostOrderref(String strData, String searchStr){
		int startPosition = 0, endPosition = 0;
		String data = "";
		
		startPosition = strData.indexOf(searchStr);
		endPosition = strData.indexOf(" ", startPosition+1+searchStr.length());
		
		data = strData.substring(startPosition + 1 + searchStr.length(), endPosition-1);
		
		return data;
	}

	public static String convertXmlToString(String path) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(new File(path)));
		String line;
		StringBuilder readContent = new StringBuilder();
		while ((line = br.readLine()) != null) {
			readContent.append(line.trim() + "\n");
		}
		br.close();
		return readContent.toString();
	}

	/*public static void sendPostRequest(String serviceName, String isService,
			String Data,String outputXMLPath) throws IOException {
		
		 * HttpHost proxy = new HttpHost("127.0.0.1"); DefaultHttpClient
		 * httpClient = new DefaultHttpClient();
		 * httpClient.getParams().setParameter("8888", proxy);
		 
		ReadData = Data;
		//HttpHost proxy = new HttpHost("127.0.0.1");
		DefaultHttpClient httpClient = new DefaultHttpClient();
		//httpClient.getParams().setParameter("8888", proxy);
		//System.setProperty("http.proxyHost", "127.0.0.1");
		//System.setProperty("https.proxyHost", "127.0.0.1");
		//System.setProperty("http.proxyPort", "8888");
		//System.setProperty("https.proxyPort", "8888");
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
		String response = "";
		try {
			response = EntityUtils.toString(postResponse.getEntity());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		File file=new File(outputXMLPath);
		file.createNewFile();
		BufferedWriter bw=new BufferedWriter(new FileWriter(file));
		bw.write(response);
		bw.close();
		System.out.println("Response xml ");
		System.out.println(response);
	}*/

}
