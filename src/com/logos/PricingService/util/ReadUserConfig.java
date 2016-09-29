package com.logos.PricingService.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class ReadUserConfig {
	XPath xpath;
	Document doc;
	Logger logger = Logger.getGlobal();

	public ReadUserConfig(String fXmlFile) {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		try {
			dBuilder = dbFactory.newDocumentBuilder();
			doc = dBuilder.parse(new File(fXmlFile));
			doc.getDocumentElement().normalize();
			xpath = XPathFactory.newInstance().newXPath();
		} catch (ParserConfigurationException e) {
			Logger.getGlobal().severe(e.toString());
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
			Logger.getGlobal().severe(e.toString());
		} catch (IOException e) {
			e.printStackTrace();
			// FileLogger.logger.severe();
			Logger.getGlobal().severe(e.toString());
		}

	}

	public String getPassword(String user) {
		XPathExpression expr;
		Object result;
		Node node = null;
		try {
			expr = xpath.compile("//config/users//user[@name='" + user + "']");
			result = expr.evaluate(doc, XPathConstants.NODE);
			node = (Node) result;

		} catch (XPathExpressionException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if (node != null)
			return node.getAttributes().getNamedItem("password").getTextContent();
		else
			return null;
	}

	public List<String> getSymbols(String user) {
		List<String> sList = new ArrayList<String>();

		XPathExpression expr;
		try {
			expr = xpath.compile("//config/users//user[@name='" + user + "']//symbol/text()");
			Object result = expr.evaluate(doc, XPathConstants.NODESET);
			NodeList nodes = (NodeList) result;

			for (int i = 0; i < nodes.getLength(); i++) {
				sList.add(nodes.item(i).getNodeValue());
			}
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return sList;
	}

	public List<String> getMasterSymbolList() {
		List<String> sList = new ArrayList<String>();

		XPathExpression expr;
		try {
			expr = xpath.compile("//config/masterSymbolList//symbol/text()");
			Object result = expr.evaluate(doc, XPathConstants.NODESET);
			NodeList nodes = (NodeList) result;

			for (int i = 0; i < nodes.getLength(); i++) {
				sList.add(nodes.item(i).getNodeValue());
			}
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sList;
	}

	public String getFtpServerName() {
		XPathExpression expr;
		String serverName = "";
		try {
			expr = xpath.compile("//config/ftp_server/text()");
			Object result = expr.evaluate(doc, XPathConstants.NODESET);
			NodeList nodes = (NodeList) result;
			if (nodes != null)
				serverName = nodes.item(0).getNodeValue();

		} catch (XPathExpressionException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return serverName;
	}

	public String getFtpUserName() {
		XPathExpression expr;
		String userName = "";
		try {
			expr = xpath.compile("//config/ftp_user/text()");
			Object result = expr.evaluate(doc, XPathConstants.NODESET);
			NodeList nodes = (NodeList) result;
			if (nodes != null)
				userName = nodes.item(0).getNodeValue();
		} catch (XPathExpressionException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return userName;
	}

	public String getFtpPassword() {
		XPathExpression expr;
		String password = "";
		try {
			expr = xpath.compile("//config/ftp_password/text()");
			Object result = expr.evaluate(doc, XPathConstants.NODESET);
			NodeList nodes = (NodeList) result;
			if (nodes != null)
				password = nodes.item(0).getNodeValue();
		} catch (XPathExpressionException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return password;
	}

	public String getFtpLocalDirectory() {
		XPathExpression expr;
		String localDir = "";
		try {
			expr = xpath.compile("//config/ftp_local_file_dir/text()");
			Object result = expr.evaluate(doc, XPathConstants.NODESET);
			NodeList nodes = (NodeList) result;
			if (nodes != null)
				localDir = nodes.item(0).getNodeValue();

		} catch (XPathExpressionException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return getBaseDir() + localDir;
	}
	
	public String getFtpTemplateDirectory() {
		XPathExpression expr;
		String localDir = "";
		try {
			expr = xpath.compile("//config/ftp_template_file_dir/text()");
			Object result = expr.evaluate(doc, XPathConstants.NODESET);
			NodeList nodes = (NodeList) result;
			if (nodes != null)
				localDir = nodes.item(0).getNodeValue();

		} catch (XPathExpressionException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return getBaseDir() + localDir;
	}
	
	public String getPricebarDirectory() {
		XPathExpression expr;
		String localDir = "";
		try {
			expr = xpath.compile("//config/pricebar_dir/text()");
			Object result = expr.evaluate(doc, XPathConstants.NODESET);
			NodeList nodes = (NodeList) result;
			if (nodes != null)
				localDir = nodes.item(0).getNodeValue();

		} catch (XPathExpressionException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return getBaseDir() + localDir;
	}
	
	public String getRealtimeDirectory() {
		XPathExpression expr;
		String localDir = "";
		try {
			expr = xpath.compile("//config/realtime_dir/text()");
			Object result = expr.evaluate(doc, XPathConstants.NODESET);
			NodeList nodes = (NodeList) result;
			if (nodes != null)
				localDir = nodes.item(0).getNodeValue();

		} catch (XPathExpressionException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return getBaseDir() + localDir;
	}

	public int getFtpServerPort() {
		XPathExpression expr;
		String port = "0";
		try {
			expr = xpath.compile("//config/ftp_port/text()");
			Object result = expr.evaluate(doc, XPathConstants.NODESET);
			NodeList nodes = (NodeList) result;
			if (nodes != null)
				port = nodes.item(0).getNodeValue();

		} catch (XPathExpressionException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return Integer.parseInt(port);
	}

	public String getFtpScheduleDefinition() {
		XPathExpression expr;
		String name = "";
		try {
			expr = xpath.compile("//config/ftp_schedule_defnition/text()");
			Object result = expr.evaluate(doc, XPathConstants.NODESET);
			NodeList nodes = (NodeList) result;
			if (nodes != null)
				name = nodes.item(0).getNodeValue();
		} catch (XPathExpressionException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return name;
	}

	public String getFtpReportTemplate() {
		XPathExpression expr;
		String name = "";
		try {
			expr = xpath.compile("//config/ftp_report_template/text()");
			Object result = expr.evaluate(doc, XPathConstants.NODESET);
			NodeList nodes = (NodeList) result;
			if (nodes != null)
				name = nodes.item(0).getNodeValue();
		} catch (XPathExpressionException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return name;
	}

	public String getBaseDir() {
		XPathExpression expr;
		String name = "";
		try {
			expr = xpath.compile("//config/base_dir/text()");
			Object result = expr.evaluate(doc, XPathConstants.NODESET);
			NodeList nodes = (NodeList) result;
			if (nodes != null)
				name = nodes.item(0).getNodeValue();
		} catch (XPathExpressionException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return name;
	}

}
