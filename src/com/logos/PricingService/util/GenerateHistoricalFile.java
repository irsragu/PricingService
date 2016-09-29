package com.logos.PricingService.util;

import java.util.Date;

public class GenerateHistoricalFile {

	public void generateHistoricalFile() {
		System.out.println("In the Generate Historical run ... generating the report at: " + new Date());
		UpdateREportTemplateXML updateREportTemplateXML = new UpdateREportTemplateXML();
		updateREportTemplateXML.update();

		System.out.println("Getting file now ....");
		String fXmlFile = System.getProperty("user.dir") + "/config/config.xml";
		ReadUserConfig rx = new ReadUserConfig(fXmlFile);
		String server = rx.getFtpServerName();
		int port = rx.getFtpServerPort();
		String user = rx.getFtpUserName();
		String pass = rx.getFtpPassword();

		String filePath = rx.getFtpTemplateDirectory();
		String schdulerFileName = rx.getFtpScheduleDefinition();

		FtpUploadService ftpUploadService = new FtpUploadService(server, port, user, pass);
		ftpUploadService.uploadFile(filePath, schdulerFileName);
		boolean fileDownloaded = false;
		try {

			FtpDownloadService ftpDownloadService = new FtpDownloadService(server, port, user, pass);
			// This is downloaded from DSS site. Do not change. This is
			// hard-coded intentionally
			String fileName = "Daily_Historical.csv";
			while (!fileDownloaded) {
				fileDownloaded = ftpDownloadService.downloadFile(rx.getFtpLocalDirectory(), fileName, "reports",
						fileName);
				if (!fileDownloaded) {
					Thread.sleep(60000);
					System.out.println("Waiting for 1 min and trying again to download");
				} else {
					System.out.println("Downloading the file......" + fileName);
				}
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
