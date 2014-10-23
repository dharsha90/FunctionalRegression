package com.Thd.Regression;

import java.io.File;
import java.io.IOException;

import com.Thd.Request.JavaBeans.*;
import com.Thd.Response.JavaBeans.*;

import jaxb.xml.processing.utils.XMLReader;
import jaxb.xml.processing.utils.XMLWriter;

public class SetUpInputXml {
	static String ReadData = "";

	public static String setCreateOrderInputXml(String extnHostOrderRef) throws IOException {
		XMLReader<com.Thd.Request.JavaBeans.BOPIS_Create_Order> xmlReader = new XMLReader<BOPIS_Create_Order>(
				com.Thd.Request.JavaBeans.BOPIS_Create_Order.class);
		xmlReader.addValidatingSchema(new File("xsd//RequestXsd//BOPIS_CreateOrder.xsd"));
		File source = new File("xml//RequestXml//BOPIS_CreateOrder.xml");
		BOPIS_Create_Order bco = xmlReader.readXMLData(source);
		bco.getExtn().setExtnHostOrderReference(extnHostOrderRef);
		bco.getExtn().setExtnHostOrderSystemIdentifier(extnHostOrderRef);
		XMLWriter<com.Thd.Request.JavaBeans.BOPIS_Create_Order> xmlWriter = new XMLWriter<BOPIS_Create_Order>(
				com.Thd.Request.JavaBeans.BOPIS_Create_Order.class);
		xmlWriter.setFormatOutput(true);
		// rco.setEnterpriseCode("DEFAULT");
		xmlWriter.writeXMLData(bco, source);
		return CommonUtils.convertXmlToString(source.toString());
	}

	public static String setRecallInputXml(String lockOrder) throws IOException {
		XMLReader<com.Thd.Request.JavaBeans.BOPIS_Create_Order> xmlReader = new XMLReader<BOPIS_Create_Order>(
				com.Thd.Request.JavaBeans.BOPIS_Create_Order.class);
		xmlReader.addValidatingSchema(new File(
				"xsd//RequestXsd//BOPIS_CreateOrder.xsd"));
		File source = new File("xml//RequestXml//BOPIS_CreateOrder.xml");
		BOPIS_Create_Order bco = xmlReader.readXMLData(source);
		XMLReader<com.Thd.Request.JavaBeans.Recall_Order> xmlReader1 = new XMLReader<Recall_Order>(
				com.Thd.Request.JavaBeans.Recall_Order.class);
		xmlReader1.addValidatingSchema(new File(
				"xsd//RequestXsd//Recall_Order.xsd"));
		File destination = new File("xml//RequestXml//Recall_Order.xml");
		Recall_Order rco = xmlReader1.readXMLData(destination);
		rco.getExtn().setExtnHostOrderReference(
				bco.getExtn().getExtnHostOrderReference());
		rco.getExtn().setExtnPutOrderOnLock("N");
		XMLWriter<com.Thd.Request.JavaBeans.Recall_Order> xmlWriter = new XMLWriter<Recall_Order>(
				com.Thd.Request.JavaBeans.Recall_Order.class);
		xmlWriter.setFormatOutput(true);
		// rco.setEnterpriseCode("DEFAULT");
		xmlWriter.writeXMLData(rco, destination);
		ReadData = CommonUtils
				.convertXmlToString("xml//RequestXml//Recall_Order.xml");
		return ReadData;
	}

	public static String setPickInputXml(String orderNo) throws IOException {
		XMLReader<com.Thd.Response.JavaBeans.Recall_Order_Response> xmlReader = new XMLReader<Recall_Order_Response>(
				com.Thd.Response.JavaBeans.Recall_Order_Response.class);
		xmlReader.addValidatingSchema(new File(
				"xsd//ResponseXsd//Recall_Order_Response.xsd"));
		File source = new File("xml//ResponseXml//Recall_Order_Response.xml");
		Recall_Order_Response rco = xmlReader.readXMLData(source);
		XMLReader<com.Thd.Request.JavaBeans.Pick_Confirmation> xmlReader1 = new XMLReader<Pick_Confirmation>(
				com.Thd.Request.JavaBeans.Pick_Confirmation.class);
		System.out.println("Harsha  "
				+ rco.getShipments().getShipment().getShipmentNo());
		File destination = new File("xml//RequestXml//PICK_Request.xml");
		Pick_Confirmation pco = xmlReader1.readXMLData(destination);
		pco.getShipment().setShipmentNo(
				rco.getShipments().getShipment().getShipmentNo());
		pco.getExtn().setExtnHostOrderReference(
				rco.getOrder().getExtn().getExtnHostOrderReference());
		pco.getShipment()
				.getShipmentLines()
				.getShipmentLine()
				.getOrder()
				.getExtn()
				.setExtnHostOrderReference(
						rco.getOrder().getExtn().getExtnHostOrderReference());
		XMLWriter<com.Thd.Request.JavaBeans.Pick_Confirmation> xmlWriter = new XMLWriter<Pick_Confirmation>(
				com.Thd.Request.JavaBeans.Pick_Confirmation.class);
		xmlWriter.setFormatOutput(true);
		xmlWriter.writeXMLData(pco, destination);
		ReadData = CommonUtils
				.convertXmlToString("xml//RequestXml//PICK_Request.xml");
		return ReadData;
	}

	public static String setReleaseInputXml() throws IOException {
		XMLReader<com.Thd.Response.JavaBeans.PICK> xmlReader = new XMLReader<PICK>(
				com.Thd.Response.JavaBeans.PICK.class);
		xmlReader.addValidatingSchema(new File(
				"xsd//ResponseXsd//PICK_Response.xsd"));
		File source = new File("xml//ResponseXml//PICK_Response.xml");
		PICK pco = xmlReader.readXMLData(source);
		XMLReader<com.Thd.Request.JavaBeans.Final_Release_Request> xmlReader1 = new XMLReader<Final_Release_Request>(
				com.Thd.Request.JavaBeans.Final_Release_Request.class);
		File destination = new File(
				"xml//RequestXml//Final_Release_Request.xml");
		Final_Release_Request frr = xmlReader1.readXMLData(destination);
		frr.getExtn().setExtnHostOrderReference(
				pco.getExtn().getExtnHostOrderReference());

		XMLWriter<com.Thd.Request.JavaBeans.Final_Release_Request> xmlWriter = new XMLWriter<Final_Release_Request>(
				com.Thd.Request.JavaBeans.Final_Release_Request.class);
		xmlWriter.setFormatOutput(true);
		xmlWriter.writeXMLData(frr, destination);
		ReadData = CommonUtils
				.convertXmlToString("xml//RequestXml//Final_Release_Request.xml");
		return ReadData;

	}
	public static Recall_Order_Response readHDRecall(){
		XMLReader<com.Thd.Response.JavaBeans.Recall_Order_Response> xmlReader = new XMLReader<Recall_Order_Response>(
				com.Thd.Response.JavaBeans.Recall_Order_Response.class);
		xmlReader.addValidatingSchema(new File(
				"xsd//ResponseXsd//Recall_Order_Response.xsd"));
		File source = new File("xml//ResponseXml//Recall_Order_Response.xml");
		return xmlReader.readXMLData(source); 
	}
	
	
	public static void main(String[] args) throws IOException {
		setCreateOrderInputXml("");
		setRecallInputXml("N");
		setPickInputXml("");
		setReleaseInputXml();
	}

}
