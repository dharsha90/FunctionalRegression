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

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xml.sax.SAXException;

import com.Thd.Regression.ServletCaller;
import com.Thd.Regression.SetUpInputXml;

public class BopisTest {
	
	String ReadData = "";
	static Connection con = null;
	static String BOPISInputXMLPath, BOPISOutputXMLPath,BOPISInputXSDPath, BOPISOutputXSDPath,
			hostOrderRefInput = "", hostOrderRefOutput = "",
			orderHeaderKey = "", orderNo = "", status_Description = "",
			extnLockID, transactionKey;
	private static PreparedStatement stmt = null;
	private static ResultSet rs = null;
	private static Properties Property;

	@BeforeClass
	public static void setUpBopisTest() throws IOException, SQLException {
		System.out.println("Enter HostOrderReference Number ");
		Scanner in = new Scanner(System.in);
		hostOrderRefInput = in.next();
		
		if (hostOrderRefInput==null) hostOrderRefInput="R" + System.currentTimeMillis();
		
		Property= new Properties();
		Property.load(BopisTest.class.getResourceAsStream("/config.properties"));
		BOPISInputXMLPath = Property.getProperty("BOPIS_Input_XML_Folder");
		BOPISOutputXMLPath = Property.getProperty("BOPIS_Output_XML_Folder");
		BOPISInputXSDPath= Property.getProperty("BOPIS_Input_XSD_Folder");
		BOPISOutputXSDPath= Property.getProperty("BOPIS_Output_XSD_Folder");
		//con = DbConnection.getDBConnection();

	}

	@Test
	public void setUpCreateOrderTest() throws IOException, SQLException, SAXException, InterruptedException {
		ReadData = SetUpInputXml.setCreateOrderInputXml(hostOrderRefInput);//input given by user
		String apiResult = CodeTester.callAPI("HDProcessTransaction", ReadData, BOPISOutputXMLPath+"CreateOrderResponse.xml", false);
		//Thread.sleep(5000);
		System.out.println("First api result is " + apiResult);
		/*stmt = con.prepareStatement("select order_header_key,order_no,extn_host_order_ref from yfs_order_header where extn_host_order_ref=? and DOCUMENT_TYPE='0001'");
		stmt.setString(1, hostOrderRefInput);
		rs = stmt.executeQuery();
		if (rs.next()) {
			orderHeaderKey = rs.getString(1);
			orderNo = rs.getString(2);
			hostOrderRefOutput = rs.getString(3);
		}
		
		stmt = con.prepareStatement("select Description from yfs_status where status=(select status from yfs_order_release_status where status=? and order_header_key=?)");
		stmt.setString(1, "3350");
		stmt.setString(2, orderHeaderKey);
		rs = stmt.executeQuery();
		while (rs.next()) {
			status_Description = rs.getString(1);
			if (status_Description.equals("Included In Shipment")) {
				break;
			}
		}*/
	}

	@Test
	public void testCreateOrder() {
		assertEquals(status_Description.toLowerCase(), "included in shipment");
		assertEquals(hostOrderRefOutput, hostOrderRefInput);
	}

	@Test
	public void setUpHDRecallTest() throws IOException, SQLException, SAXException {
		ReadData = SetUpInputXml.setRecallInputXml();
		CodeTester.callAPI("HDRecall", BOPISInputXMLPath+"Recall_Order.xml", BOPISOutputXMLPath+"Recall_Order_Response.xml", true);
		hostOrderRefInput = ServletCaller.getExtnHostOrderref("ExtnHostOrderReference=");
		stmt = con.prepareStatement("select extn_lock_id from yfs_order_hold_type where order_header_key=?");
		stmt.setString(1, orderHeaderKey);
		rs = stmt.executeQuery();
		while (rs.next()) {
			extnLockID = rs.getString(1);
			if (extnLockID != null) {
				break;
			}

		}
	}
	
	//revisit lock id (remove)

	@Test
	public void testHDRecall() {
		//assertEquals(extnLockID, extnLockID);
		assertNotNull(extnLockID);
	}

	@Test
	public void setUpPickConfirmationTest() throws IOException, SQLException, SAXException {
		ReadData = SetUpInputXml.setPickInputXml(orderNo);
		CodeTester.callAPI("HDPickConfirmation", BOPISInputXMLPath+"PICK_Request.xml", BOPISOutputXMLPath+"PICK_Response.xml", true);
		hostOrderRefInput = ServletCaller.getExtnHostOrderref("ExtnHostOrderReference=");
		stmt = con.prepareStatement("select Description from yfs_status where status=(select status from yfs_order_release_status where status=? and order_header_key=?)");
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
		//assertEquals(extnLockID, extnLockID);
		assertEquals(status_Description.toLowerCase(),"shipment picked");
	}

	@Test
	public void setUpReleaseOrderTest() throws IOException, SQLException, SAXException {
		ReadData = SetUpInputXml.setReleaseInputXml();
		CodeTester.callAPI("HDProcessTransaction", BOPISInputXMLPath+"Final_Release_Request.xml", BOPISOutputXMLPath+"Final_Release_Response.xml", true);
		hostOrderRefInput = ServletCaller.getExtnHostOrderref("ExtnHostOrderReference=");
		stmt = con.prepareStatement("select Description from yfs_status where status=(select status from yfs_order_release_status where status=? and order_header_key=?)");
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
		stmt = con.prepareStatement("select TRANSACTION_KEY from yfs_task_q where data_key=?");
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
