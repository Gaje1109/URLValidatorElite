package com.wilp.bits.config.utility;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;

import com.wilp.bits.email.EmailManagement;

public class ReadWriteProps {

	private static final Logger readWriteProps = Logger.getLogger(ReadWriteProps.class.getName());
	String methodsName = "";

	public String ReadPropsFile() {
		String accessKey = "";
		String secretKey = "";
		String emailauth = "";
		InputStream propertyStream = null;
		final String propertiesFileName = "URLValidatorConfig.properties";
		methodsName = "ReadPropsFile()";
		try {
			readWriteProps.info("Inside " + methodsName + " -- Start");
			readWriteProps.info("Properties File Name : " + propertiesFileName);
			ClassLoader configloader = ReadWriteProps.class.getClassLoader();
			propertyStream = configloader.getResourceAsStream(propertiesFileName);

			if (propertyStream != null) {
				readWriteProps.info(propertiesFileName + " File  found");
				Properties props = new Properties();
				props.load(propertyStream);

				accessKey = props.getProperty("accessKey");
				secretKey = props.getProperty("secretKey");
			} else {
				readWriteProps.info(propertiesFileName + " File Not found");
			}
			// inputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			readWriteProps.info("Exception occured in " + methodsName + " : " + e);
		} finally {
			try {
				propertyStream.close();
			} catch (IOException e) {
				readWriteProps.info("Exception occured in " + methodsName + " : " + e);
			}
		}
		readWriteProps.info("Inside " + methodsName + " -- End");
		return accessKey + "," + secretKey + "," + emailauth;
	}

	public String ReadEmailProps() {
		String emailauth = "";
		String toaddress = "";
		String fromaddress = "";
		InputStream propertyStream = null;
		final String emailPropertiesFileName = "URLValidator_email.properties";
		methodsName = "ReadEmailProps()";
		try {
			readWriteProps.info("Inside " + methodsName + " -- Start");
			readWriteProps.info("Properties File Name : " + emailPropertiesFileName);
			ClassLoader emailloader = ReadWriteProps.class.getClassLoader();
			propertyStream = emailloader.getResourceAsStream(emailPropertiesFileName);

			if (propertyStream != null) {
				readWriteProps.info(emailPropertiesFileName + " File  found");
				Properties props = new Properties();
				props.load(propertyStream);
				emailauth = props.getProperty("emailauth");
				toaddress = props.getProperty("emailto");
				fromaddress = props.getProperty("emailfrom");

			} else {
				readWriteProps.info(emailPropertiesFileName + " File Not found");
			}
			// inputStream.close();
		} catch (IOException e) {
			readWriteProps.info("Exception occured in " + methodsName + " : " + e);
		} catch (Exception e) {
			readWriteProps.info("Exception occured in " + methodsName + " : " + e);
		} finally {
			try {
				propertyStream.close();
			} catch (IOException e) {
				readWriteProps.info("Exception occured in " + methodsName + " : " + e);
			}
		}
		readWriteProps.info("Inside " + methodsName + " -- End");
		return emailauth + "&" + toaddress + "&" + fromaddress;
	}
}
