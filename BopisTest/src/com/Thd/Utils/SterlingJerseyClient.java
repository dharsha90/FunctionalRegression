package com.Thd.Utils;

import org.apache.log4j.Logger;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;



public class SterlingJerseyClient 
{
	private static final Logger logger = Logger.getLogger( SterlingJerseyClient.class );

	/**
	 * This method creates a jersey client and hits the Sterling. It
	 * gets the response for the postRequest sent.
	 * 
	 * @param url
	 * @param postRequest
	 * @throws ProcessingException
	 */
	public String send(String url, String postRequest) throws Exception 
	{	
		long startTime = System.currentTimeMillis();
		
	
		if (logger.isDebugEnabled()) {
			logger.debug("SterlingJerseyClient.send(url, queryString) started");
			logger.debug("  url - " + url);
			logger.debug("  queryString - " + postRequest);
		}
		
		// Create Jersey client
		Client client = Client.create();
		client.setConnectTimeout(600000);
		client.setReadTimeout(600000);
		
		String token = "kQSGLL7Stb5W5IsL1TInjc40LnIYCH3iptOnucnL5EWCBo8H7OuDAaaeVakf4WjP9W3brIBd47RH5oJb7S5EhlJBDjBlgsc7r3LgVds54pIt34GUqa8TDYpFAeTHuVFje8HVpUoPYGlplnqWRNmrZWTOSjphu0KSWLsOfENXrrlyMF2gpzHGYUO3m18guLhX3yzrEw5qsL6H3QnDta5l94gsrjKl5gIPav1tTQKWShZ7h7ylTqZ5Si9BcMYm0ej9CJsbAVoOrcioTr8ygncJYDs710UJZdCOUDSq";
		
		// Create WebResource 
		WebResource webResource = client.resource(url);
		ClientResponse response = null;
		
		try {				
			response = webResource.header("THDService-Auth", token).type("application/x-www-form-urlencoded").post(ClientResponse.class, postRequest);
		} 
		catch (Exception e)
		{
			logger.error("Unexpected error from webresource: " + e.getMessage(), e);
			throw new Exception("Unexpected error from webresource: " + e.getMessage());			
		}
		// Retrieve status and check if it is OK
		int status = response.getStatus();
		if (logger.isDebugEnabled())
		{
		
			logger.debug("Response status from SterlingJerseyClient: " + status);
		}
		if (status != 200) {
			logger.error("Unsuccesful Status response from Sterling: " + status);
			throw new Exception("Unsuccesful response Status: " + status);
		}
		String sterlingResponse = response.getEntity(String.class);
		if(logger.isDebugEnabled()){
			logger.debug("Response XML received from Sterling is: "+sterlingResponse);
		}
		
		if (sterlingResponse == null) {
			logger.error("Application Error during call to Sterling - Response is null");
			throw new Exception("Application Error during call to Sterling Response is null");
		}
		 
		// Log results
		if (logger.isDebugEnabled()) {
			logger.debug("End sendMessage()");
		}
		
		return sterlingResponse;
	}
	
}
