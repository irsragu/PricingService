package com.logos.PricingService.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

public class FtpDownloadService {

	private FTPClient ftpClient;
	String server = "hosted.datascope.reuters.com";
	int port = 21;
	String user = "r9009814";
	String pass = "logos949";

	public FtpDownloadService(String server, int port, String user, String pass) {
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

	public boolean downloadFile(String localFilePath, String localFileName, String remoteFilePath,
			String remoteFileName) {
		boolean done = false;
		try {
			initialize();
			ftpClient.setFileType(FTP.ASCII_FILE_TYPE);
			String remoteFile = remoteFilePath + "/" + remoteFileName;
			File downloadFile = new File(localFilePath + localFileName);
			OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(downloadFile));
			done = ftpClient.retrieveFile(remoteFile, outputStream);
			outputStream.close();
			if (done)
				ftpClient.deleteFile(remoteFile);

		} catch (IOException e) {
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
