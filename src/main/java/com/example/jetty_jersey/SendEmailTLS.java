package com.example.jetty_jersey;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class SendEmailTLS {
	
	public static String subjectReservation = "Confirm Your Blablaplane Reservation";
	public static String subjectConfirmation = "Confirmation Reply";
	
	
	public static boolean envoyerMail(String to, String subject, String msg) {
		
		final String username = "blablaplanes@gmail.com";
		final String password = "blablaplanes2019";
		 
		
		Properties prop = new Properties();
		prop.put("mail.smtp.host", "smtp.gmail.com");
		prop.put("mail.smtp.port", "465");
		prop.put("mail.smtp.auth", "true");
		prop.put("mail.smtp.socketFactory.port", "465");
		prop.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		
		Session session = Session.getInstance(prop, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});
		
		try {

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress("blablaplanes@gmail.com"));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
			message.setSubject(subject);
			message.setText(msg);

			Transport.send(message);
			
			System.out.println("send success");
			return true;

		} catch (MessagingException e) {
			System.out.println("Error send mail");
			return false;
		}
		
		
	}
	
}
