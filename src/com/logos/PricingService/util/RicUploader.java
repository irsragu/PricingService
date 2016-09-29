package com.logos.PricingService.util;

import java.io.File;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
//Not used. 
public class RicUploader {

	List<String> masterSymbolList;

	public RicUploader(List<String> masterSymbolList) {
		super();
		this.masterSymbolList = masterSymbolList;
	}

	public void uploadRicsToDSS() {
		String fXmlFile = System.getProperty("user.dir") + "/config/config.xml";
		ReadUserConfig rx = new ReadUserConfig(fXmlFile);
		String filePath = rx.getFtpTemplateDirectory() + "??";
		for (String s : masterSymbolList) {
			String symbolName = s;
			if (symbolName.endsWith("="))
				symbolName = symbolName.replace("=", "_");

			try {
				DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
				Document doc = dBuilder.newDocument();

				Element root = doc.createElement("ReportRequest");
				doc.appendChild(root);

				Element inputList = doc.createElement("InputList");
				root.appendChild(inputList);

				Element inputListAction = doc.createElement("InputListAction");
				inputListAction.appendChild(doc.createTextNode("Replace"));
				inputList.appendChild(inputListAction);

				Element name = doc.createElement("Name");
				name.appendChild(doc.createTextNode(symbolName));
				inputList.appendChild(name);

				Element instrument = doc.createElement("Instrument");
				inputList.appendChild(instrument);

				Element identifierType = doc.createElement("IdentifierType");
				identifierType.appendChild(doc.createTextNode("RIC"));
				instrument.appendChild(identifierType);

				Element identifier = doc.createElement("Identifier");
				identifier.appendChild(doc.createTextNode(s));
				instrument.appendChild(identifier);

				TransformerFactory transformerFactory = TransformerFactory.newInstance();
				Transformer transformer = transformerFactory.newTransformer();
				transformer.setOutputProperty(OutputKeys.INDENT, "yes");
				transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
				DOMSource domSource = new DOMSource(doc);
				doc.normalizeDocument();
				String fileName = symbolName + ".xml";
				File file = new File(filePath + fileName);
				StreamResult streamResult = new StreamResult(file);
				transformer.transform(domSource, streamResult);
				FtpUploadService ftpUploadService = new FtpUploadService("hosted.datascope.reuters.com", 21, "r9009814",
						"logos949");
				ftpUploadService.uploadFile(filePath, fileName);

			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (TransformerConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (TransformerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

}
