package com.Thd.Regression;

import java.io.IOException;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

public class StartTest {
	static String ReadData = "";

	public static void main(String[] args) throws IOException {
		ServletCaller.loadProperties();
		
		Result bopisResult = JUnitCore.runClasses(com.Thd.Bopis.BopisTest.class);
		for (Failure failure : bopisResult.getFailures()) {
			System.out.println(failure.toString());
		}
		System.out.println("Result of Bopis Order Testing "+ bopisResult.wasSuccessful());

		

	}

}
