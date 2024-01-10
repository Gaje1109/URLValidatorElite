package com.wilp.bits.email;

import java.io.File;
import java.util.Properties;
import java.util.logging.Logger;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import com.wilp.bits.config.utility.ReadWriteProps;
import com.wilp.bits.url.URLValidator;

//Email Management
public class EmailManagement {

	private static final Logger emailManagement = Logger.getLogger(EmailManagement.class.getName());
	String methodsName = "";
	//Getting details from Properties file
	ReadWriteProps props = new ReadWriteProps();
	String[] keys = props.ReadEmailProps().split("&");
	String emailauth = keys[0];

	public void emailConfigurations(File createdfile) {
		methodsName = "emailConfigurations";
		emailManagement.info("Inside " + methodsName + " -- Start");
		String message = "Hi, " + "\nPlease find the report attached here with this email." + " " + "\nThanks,"
				+ "\nGajendran R," + "\n(Developer)";
		String subject = "MAH Reports";

		// Getting datas from properties file
		String to = keys[1].replace("\"", "");
		String from = keys[2];
		String host = "smtp.gmail.com";

		// sendEmail(message, subject, to, from, host);
		sendEmailAttachment(createdfile, message, subject, to, from, host);

		emailManagement.info("Inside " + methodsName + " -- End");
	}

	// Send the message with attachment
	private void sendEmailAttachment(File createdfile, String message, String subject, String to, String from,
			String host) {
		try {
			methodsName = "sendEmailAttachment()";
			emailManagement.info("Inside " + methodsName + " -- Start");
			// get the system properties
			Properties properties = System.getProperties();
			//emailManagement.info("PROPERTIES: " + properties);

			// Setting important information to properties object
			properties.put("mail.smtp.host", host);
			properties.put("mail.smtp.port", "465");
			properties.put("mail.smtp.ssl.enable", "true");
			properties.put("mail.smtp.auth", "true");

			// Step 1: Get the Session object
			Session session = Session.getInstance(properties, new Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication("202117bh009@wilp.bits-pilani.ac.in", emailauth);
				}
			});

			session.setDebug(true);

			// Compose the message[text, multimedia]
			MimeMessage mimemess = new MimeMessage(session);
			mimemess.setFrom(from);

			// Split 'to' into an array of email addresses
			String[] toAddresses = to.split(",");
			InternetAddress[] recipients = new InternetAddress[toAddresses.length];
			for (int i = 0; i < toAddresses.length; i++) {
				recipients[i] = new InternetAddress(toAddresses[i].trim());
			}

			// adding recipients to message
			mimemess.addRecipients(Message.RecipientType.TO, recipients);

			// adding subject to message
			mimemess.setSubject(subject);

			// adding attachment to message
			MimeMultipart mimepart = new MimeMultipart();
			MimeBodyPart textmime = new MimeBodyPart();
			MimeBodyPart filemime = new MimeBodyPart();
			textmime.setText(message);
			filemime.attachFile(createdfile);
			mimepart.addBodyPart(textmime);
			mimepart.addBodyPart(filemime);
			mimemess.setContent(mimepart);

			// send mail
			// Step 3: Send the message using Transport class
			Transport.send(mimemess);

			emailManagement.info("MAH Report sent with Attachment !!");
		} catch (Exception e) {
			emailManagement.info("Exception occured in " + methodsName + " : " + e);
		}
		emailManagement.info("Inside " + methodsName + " -- End");
	}

	private void sendEmail(String message, String subject, String to, String from, String host) {
		methodsName = "sendEmail()";
		emailManagement.info("Inside " + methodsName + " -- Start");
		try {
			// get the system properties
			Properties properties = System.getProperties();
			emailManagement.info("PROPERTIES: " + properties);

			// Setting important information to properties object
			properties.put("mail.smtp.host", host);
			properties.put("mail.smtp.port", "465");
			properties.put("mail.smtp.ssl.enable", "true");
			properties.put("mail.smtp.auth", "true");

			// Step 1: Get the Session object

			Session session = Session.getInstance(properties, new Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication("202117bh009@wilp.bits-pilani.ac.in", emailauth);

				}

			});

			session.setDebug(true);

			// Step 2: Compose the message[text, multimedia]
			MimeMessage mimemess = new MimeMessage(session);

			// from email
			mimemess.setFrom(from);

			// adding recipient to message
			mimemess.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

			// adding subject to message
			mimemess.setSubject(subject);

			// adding text to message
			mimemess.setText(message);

			// send mail
			// Step 3: Send the message using Transport class
			Transport.send(mimemess);

			emailManagement.info("Mail sent!!");

		} catch (Exception e) {
			emailManagement.info("Exception occured in " + methodsName + " : " + e);
		}
		emailManagement.info("Inside " + methodsName + " -- End");
	}
}