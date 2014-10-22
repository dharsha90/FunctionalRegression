package com.Thd.Bopis;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.shared.ycp.YFSContext;
import com.yantra.tools.ant.TestingContext;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfs.japi.YFSEnvironment;

/*
 * -XX:MaxPermSize=256m -Xms512m -Xmx512m -DvendorFile=C:/Sterling91/Foundation/properties/vendor.properties -Dvendor=shell
 * in vendor.props
 * shell=C:/Sterling91/properties/noapp.properties
 */
public class CodeTester {
	private static YIFApi api;

	static {
		try {
			System.setProperty("vendor", "shell");
			System.setProperty("vendorFile", "C:\\Sterling91\\Foundation\\properties\\vendor.properties");
			CodeTester.api = YIFClientFactory.getInstance().getApi("LOCAL");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static Document invokeAPI(YFSEnvironment env, String apiName,
			Document inDoc) throws Exception {
		return CodeTester.api.invoke(env, apiName, inDoc);
	}

	public static Document executeFlow(YFSEnvironment env, String apiName,
			Document inDoc) throws Exception {
		return CodeTester.api.executeFlow(env, apiName, inDoc);
	}

	public static void main(String[] args) throws Exception {
		String inputData = new Scanner(new File("c:\\temp\\file.xml")).useDelimiter("\\Z").next();
		System.out.println(inputData + "\n\n");
		callAPI("HDProcessTransaction", inputData, "C://Users//PXG8060//Desktop//outputXmlPath.xml", false);
	}

	public static String callAPI(String apiName, String data,
			String outputXmlPath, boolean isFlow) throws FileNotFoundException,
			SAXException, IOException {

		YFCDocument inDoc1 = null;
		YFCDocument getOutDoc = null;
		inDoc1 = YFCDocument.getDocumentFor(data);

		YFSContext ctx = null;
		try {
			TestingContext testingContext = TestingContext.getInstance("admin","password");
			ctx = testingContext.getYCPContext();
			if (isFlow) {
				getOutDoc = YFCDocument.getDocumentFor(CodeTester.invokeAPI(
						ctx.getEnvironment(), apiName, inDoc1.getDocument()));
				System.out.println(getOutDoc.toString());
			} else {
				getOutDoc = YFCDocument.getDocumentFor(CodeTester.executeFlow(
						ctx.getEnvironment(), apiName, inDoc1.getDocument()));
				System.out.println(getOutDoc.toString());
			}
			BufferedWriter bw=new BufferedWriter(new FileWriter(new File(outputXmlPath),false));
			bw.write(getOutDoc.toString());
			bw.close();

			ctx.commit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (ctx != null)
				ctx.close();
		}
		return getOutDoc.toString();

	}

}