package com.Thd.Regression;

import static org.junit.Assert.*;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestService {
	Connection con = null;
	String hostOrderRefInput = "", hostOrderRefOutput = "";

	@Before
	public void setUpTest() throws IOException, SQLException {
		con = DbConnection.getDBConnection();
		hostOrderRefInput = ServletCaller.getExtnHostOrderref("ExtnHostOrderReference=");
		hostOrderRefOutput = DbConnection.executeQuery("select order_no from yfs_order_header where order_no=?",
						hostOrderRefInput);
	}

	@Test
	public void test() {
		assertEquals(hostOrderRefInput, hostOrderRefOutput);

	}

	@After
	public void closeConnection() throws SQLException {
		con.close();
	}

}
