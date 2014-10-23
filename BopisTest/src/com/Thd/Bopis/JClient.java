package com.Thd.Bopis;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.Thd.Utils.SterlingJerseyClient;



public class JClient {

	public static String executeFlow(String APIName, String inputData, String outputPath){
		String queryString = null;
		try {
			queryString = createQueryString(APIName, inputData);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		SterlingJerseyClient sterlingClient = new com.Thd.Utils.SterlingJerseyClient();
		String sterlingResponseXML = null;
		try {
			sterlingResponseXML = removeXMLDeclaration(sterlingClient
					.send(getSterlingURL(), queryString));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("sterlingResponseXML: \n" + sterlingResponseXML);
		try {
			writeToFile(outputPath,sterlingResponseXML);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sterlingResponseXML;		
	}
	public static  String createQueryString(String interopApiName, String inputXML) 
			throws Exception {
				//if (logger.isDebugEnabled()) { logger.debug("Start Utils.createQueryString("+ interopApiName + ", " + inputXML + ")"); }

				String URLEncodedInputXML = null;
				try {
					URLEncodedInputXML = URLEncoder.encode(inputXML, "UTF-8");
				} catch (UnsupportedEncodingException e) {
					//logger.error("In createQueryString(): Problem encoding with UTF-8");
					throw new Exception("Problem encoding with UTF-8", e);
				}
				StringBuilder queryString = new StringBuilder();
				queryString.append("InteropApiName=");
				queryString.append(interopApiName);
				queryString.append("&IsFlow=Y");
				queryString.append("&YFSEnvironment.userId=admin");
				queryString.append("&InteropApiData=");
				queryString.append(URLEncodedInputXML);
				
				return queryString.toString();
	}
	
	/**
	 * Utility to remove the XML declaration from a Sterling response
	 * @param xml input XML with declaration
	 * @return xml without XML declaration
	 */
	public static  String removeXMLDeclaration(String xml) {
		Pattern p = Pattern.compile("<\\?xml.*?\\?>");
		Matcher m = p.matcher(xml);
		return m.replaceFirst("");
	}
	
	public  static String getSterlingURL() 
	{
		return "http://ccliqas1:15400/smcfs/interop/InteropHttpServlet";
	}


	/*
	 * Below method is a mock and returns static XML from file path
	 */
/*	public static String callAPI(String apiName, String data,
			String outputXmlPath, boolean isFlow, boolean mockServiceCall)
			throws FileNotFoundException, SAXException, IOException {
		if (mockServiceCall == false)
			return callAPI(apiName, data, outputXmlPath, isFlow);
		if (apiName.equals("HDProcessTransaction")) {
			return new Scanner(new File("xml/RequestXml/BOPIS_CreateOrder.xml")).useDelimiter("\\Z").next();
		}
		if (apiName.equals("HDRecall")){
			return new Scanner(new File("xml/ResponseXml/Recall_Order_Response.xml")).useDelimiter("\\Z").next();
		}

		return "<Test/>";
	}*/

	public static void writeToFile(String outputPath, String data) throws IOException {
			BufferedWriter bw = new BufferedWriter(new FileWriter(new File(
					outputPath), false));
			bw.write(data);
			bw.close();
	}

}