package com.Thd.Bopis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.Scanner;

import junit.framework.TestCase;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xml.sax.SAXException;

import com.Thd.Regression.CommonUtils;
import com.Thd.Regression.SetUpInputXml;
import com.Thd.Response.JavaBeans.Recall_Order_Response;

public class BopisTest{


	static String ReadData = "" , apiResponse = "";
	boolean mockServiceCall = true;
	static Connection con = null;
	static String BOPISInputXMLPath, BOPISOutputXMLPath, BOPISInputXSDPath,
			BOPISOutputXSDPath, hostOrderRefInput = "",
			hostOrderRefOutput = "", orderHeaderKey = "", orderNo = "",
			status_Description = "", extnLockID, transactionKey;
	private static PreparedStatement stmt = null;
	private static ResultSet rs = null;
	private static Properties Property;

	@BeforeClass
	public static void setUpBopisTest() throws IOException, SQLException {
		System.out.println("Enter HostOrderReference Number ");
		Scanner in = new Scanner(System.in);
		hostOrderRefInput = in.next();

		if (hostOrderRefInput == null)
			hostOrderRefInput = "R" + System.currentTimeMillis();

		Property = new Properties();
		Property.load(BopisTest.class.getResourceAsStream("/config.properties"));
		BOPISInputXMLPath = Property.getProperty("BOPIS_Input_XML_Folder");
		BOPISOutputXMLPath = Property.getProperty("BOPIS_Output_XML_Folder");
		BOPISInputXSDPath = Property.getProperty("BOPIS_Input_XSD_Folder");
		BOPISOutputXSDPath = Property.getProperty("BOPIS_Output_XSD_Folder");
		// con = DbConnection.getDBConnection();

	}

	@Test
	public void setUpCreateOrderTest() throws IOException, SQLException,
			SAXException, InterruptedException {
		ReadData = SetUpInputXml.setCreateOrderInputXml(hostOrderRefInput);
		apiResponse = JClient.executeFlow("HDProcessTransaction", ReadData,
				BOPISOutputXMLPath + "CreateOrderResponse.xml");
		// Thread.sleep(5000);
	}

	@Test
	public void testCreateOrder() {
		assertNotNull(apiResponse);
		hostOrderRefOutput = CommonUtils.getExtnHostOrderref(apiResponse,"ExtnHostOrderReference=");
		assertEquals(hostOrderRefOutput, hostOrderRefInput);
		/*try {
			stmt = con
					.prepareStatement("select count(1) from yfs_order_header where extn_host_order_ref=? and DOCUMENT_TYPE='0001'");

			stmt.setString(1, hostOrderRefInput);
			rs = stmt.executeQuery();
			if (rs.next()) {
				assertEquals("true", "true");
			}else{
				assertEquals("true", "false");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}*/
	}

	@Test
	public void setUpHDRecallTest() throws IOException, SQLException,
			SAXException {
		String inputXML = null;
		inputXML = SetUpInputXml.setRecallInputXml("N");
		String apiResult = JClient.executeFlow("HDRecall", inputXML,
				BOPISOutputXMLPath + "Recall_Order_Response.xml");
		hostOrderRefInput = CommonUtils
				.getExtnHostOrderref(apiResult,"ExtnHostOrderReference=");
	
	}

	// revisit lock id (remove)

	@Test
	public void testHDRecall() {
		// assertEquals(extnLockID, extnLockID);
		Recall_Order_Response rco = SetUpInputXml.readHDRecall();
		String strShipmentNo = rco.getShipments().getShipment().getShipmentNo();
		assertNotNull(strShipmentNo);
		//setUpHDRecallTest();
	}

	@Test
	public void setUpPickConfirmationTest() throws IOException, SQLException,
			SAXException {
		ReadData = null;
		ReadData = SetUpInputXml.setPickInputXml(orderNo);
		String apiResult = JClient.executeFlow("HDPickConfirmation", ReadData, BOPISOutputXMLPath + "PICK_Response.xml");
		hostOrderRefInput = CommonUtils
				.getExtnHostOrderref(apiResult,"ExtnHostOrderReference=");
		stmt = con
				.prepareStatement("select Description from yfs_status where status=(select status from yfs_order_release_status where status=? and order_header_key=?)");
		stmt.setString(1, "3350.200");
		stmt.setString(2, orderHeaderKey);
		rs = stmt.executeQuery();
		while (rs.next()) {
			status_Description = rs.getString(1);
			if (status_Description.equals("Shipment Picked")) {
				break;
			}

		}
	}

	@Test
	public void testPickConfirmation() {
		// assertEquals(extnLockID, extnLockID);
		assertEquals(status_Description.toLowerCase(), "shipment picked");
	}

	@Test
	public void setUpReleaseOrderTest() throws IOException, SQLException,
			SAXException {
		ReadData = null;
		ReadData = SetUpInputXml.setReleaseInputXml();
		String apiResult = JClient.executeFlow("HDProcessTransaction", ReadData, BOPISOutputXMLPath
				+ "Final_Release_Response.xml");
		hostOrderRefInput = CommonUtils
				.getExtnHostOrderref(apiResult, "ExtnHostOrderReference=");
		stmt = con
				.prepareStatement("select Description from yfs_status where status=(select status from yfs_order_release_status where status=? and order_header_key=?)");
		stmt.setString(1, "3700");
		stmt.setString(2, orderHeaderKey);
		rs = stmt.executeQuery();
		while (rs.next()) {
			status_Description = rs.getString(1);
			if (status_Description.equals("shipped")) {
				break;
			}
		}
	}

	@Test
	public void testReleaseOrder() {
		assertEquals(status_Description.toLowerCase(), "shipped");
	}

	@Test
	public void setUpPurgeOrderTest() throws IOException, SQLException {
		stmt = con
				.prepareStatement("select TRANSACTION_KEY from yfs_task_q where data_key=?");
		stmt.setString(1, orderHeaderKey);
		rs = stmt.executeQuery();
		while (rs.next()) {
			transactionKey = rs.getString(1);

		}
	}

	@Test
	public void testPurgeOrder() {
		assertEquals(transactionKey, "PURGE");
	}

	@AfterClass
	public static void closeBopisTest() throws SQLException {
		con.close();
	}

}
