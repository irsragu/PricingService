package com.logos.PricingService.util;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public class UpdateREportTemplateXML {

	public void update() {
		String fXmlFile = System.getProperty("user.dir") + "/config/config.xml";
		ReadUserConfig rx = new ReadUserConfig(fXmlFile);
		String server = rx.getFtpServerName();
		int port = rx.getFtpServerPort();
		String user = rx.getFtpUserName();
		String pass = rx.getFtpPassword();

		String filePath = rx.getFtpTemplateDirectory();
		String templateFileName = rx.getFtpReportTemplate();
		Document doc = null;
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = null;
		try {
			docBuilder = docFactory.newDocumentBuilder();
			doc = docBuilder.parse(filePath + templateFileName);
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Node endDate = doc.getElementsByTagName("EndDate").item(0);
		Node monNode = ((Element) endDate).getElementsByTagName("Month").item(0);
		Node dayNode = ((Element) endDate).getElementsByTagName("Day").item(0);
		Node yearNode = ((Element) endDate).getElementsByTagName("Year").item(0);

		SimpleDateFormat dateFormat = new SimpleDateFormat("MM");
		Date date = new Date();
		String month = dateFormat.format(date);
		dateFormat = new SimpleDateFormat("yyyy");
		String year = dateFormat.format(date);
		dateFormat = new SimpleDateFormat("dd");
		String day = dateFormat.format(date);

		monNode.setTextContent(month);
		dayNode.setTextContent(day);
		yearNode.setTextContent(year);

		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = null;
		try {
			transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(filePath + templateFileName));
			transformer.transform(source, result);
			FtpUploadService ftpUploadService = new FtpUploadService(server, port, user, pass);
			ftpUploadService.uploadFile(filePath, templateFileName);
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
