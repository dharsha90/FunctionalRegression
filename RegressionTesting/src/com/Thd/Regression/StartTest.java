package com.Thd.Regression;

import java.io.IOException;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

public class StartTest {
	static String ReadData = "";

	public static void main(String[] args) throws IOException {
		ServletCaller.loadProperties();
		ReadData=ServletCaller.convertXmlToString();
		ServletCaller.sendPostRequest("HDProcessTransaction", "Y",ReadData);
		Result result = JUnitCore.runClasses(TestService.class);
		for (Failure failure : result.getFailures()) {
			System.out.println(failure.toString());
		}
		System.out.println("Result of Create Order Testing "+ result.wasSuccessful());

	}

}
