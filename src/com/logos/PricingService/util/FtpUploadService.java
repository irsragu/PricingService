package com.logos.PricingService.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

public class FtpUploadService {

	private FTPClient ftpClient;
	String server = "hosted.datascope.reuters.com";
	int port = 21;
	String user = "r9009814";
	String pass = "logos949";

	public FtpUploadService(String server, int port, String user, String pass) {
		super();
		this.server = server;
		this.port = port;
		this.user = user;
		this.pass = pass;
	}

	public void initialize() {
		ftpClient = new FTPClient();
		try {
			ftpClient.connect(server, port);
			ftpClient.login(user, pass);
			ftpClient.enterLocalPassiveMode();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public boolean uploadFile(String filePath, String fileName) {
		boolean done = false;
		try {
			initialize();
			ftpClient.setFileType(FTP.ASCII_FILE_TYPE);

			// File("C:\\workspace\\PricingService\\config\\Daily_EOD_Run_SCHED_Definition.xml");
			File localFile = new File(filePath + fileName);

			String remoteFile = "incoming/" + fileName;
			InputStream inputStream = new FileInputStream(localFile);

			System.out.println("Start uploading first file");

			done = ftpClient.storeFile(remoteFile, inputStream);
			if (done) {
				System.out.println("The first file is uploaded successfully.");
			}
			inputStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (ftpClient.isConnected()) {
					ftpClient.logout();
					ftpClient.disconnect();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return done;
	}
}
