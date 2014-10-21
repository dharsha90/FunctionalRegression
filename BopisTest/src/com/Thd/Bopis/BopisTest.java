package com.Thd.Bopis;

import static org.junit.Assert.*;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;



import org.xml.sax.SAXException;

import src.CodeTester;

import com.Thd.Regression.DbConnection;
import com.Thd.Regression.ServletCaller;
import com.Thd.Regression.SetUpInputXml;

public class BopisTest {
	
	static String ReadData = "";
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
		Property= new Properties();
		Property.load(BopisTest.class.getResourceAsStream("/config.properties"));
		BOPISInputXMLPath = Property.getProperty("BOPIS_Input_XML_Folder");
		BOPISOutputXMLPath = Property.getProperty("BOPIS_Output_XML_Folder");
		BOPISInputXSDPath= Property.getProperty("BOPIS_Input_XSD_Folder");
		BOPISOutputXSDPath= Property.getProperty("BOPIS_Output_XSD_Folder");
		con = DbConnection.getDBConnection();

	}

	@Before
	public void setUpCreateOrderTest() throws IOException, SQLException, SAXException, InterruptedException {
		ReadData = SetUpInputXml.setCreateOrderInputXml("");//input given by user
		CodeTester.callAPI("HDProcessTransaction", BOPISInputXMLPath+"BOPIS_CreateOrder.xml", BOPISOutputXMLPath+"CreateOrderResponse.xml", true);
		Thread.sleep(5000);
		stmt = con.prepareStatement("select order_header_key,order_no,extn_host_order_ref from yfs_order_header where extn_host_order_ref=? and DOCUMENT_TYPE='0001'");
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

		}
	}

	@Test
	public void testCreateOrder() {
		assertEquals(status_Description, "included in shpiment");
		assertEquals(hostOrderRefOutput, hostOrderRefInput);
	}

	@Before
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

	@Test
	public void testHDRecall() {
		assertEquals(extnLockID, extnLockID);

	}

	@Before
	public void setUpPickConfirmationTest() throws IOException, SQLException, SAXException {
		ReadData = SetUpInputXml.setPickInputXml("");
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
		assertEquals(extnLockID, extnLockID);

	}

	@Before
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
		assertEquals(status_Description, "");
	}

	@Before
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
	public void closeBopisTest() throws SQLException {
		con.close();
	}

}
