package com.SpringTest.rest.util;


import java.io.IOException;
import java.io.InputStream;
import org.springframework.integration.ftp.session.DefaultFtpSessionFactory;
import org.springframework.integration.ftp.session.FtpSession;

public class UpDownServiceImpl {

	private DefaultFtpSessionFactory gimmeFactory() {
		DefaultFtpSessionFactory factory = new DefaultFtpSessionFactory();
		factory.setHost("ftpupload.net");
		factory.setUsername("");
		factory.setPassword(Constants.FTP_PASSWORD); //.gitgnore
		factory.setClientMode(2);
		return factory;
	}

	public void upload(InputStream reso,String path ) {
		FtpSession session = gimmeFactory().getSession();
		//InputStream reso = UpDownServiceImpl.class.getClassLoader().getResourceAsStream("test2.png");
		try {
			session.write(reso, path);

		}catch (IOException e) {
			e.printStackTrace();
		}
		session.close();
	}
	
	public InputStream download(String path) {
		FtpSession session = gimmeFactory().getSession();
		//ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		try {
			return session.readRaw(path);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		session.close();
		return null;
	}
}