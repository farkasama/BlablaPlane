package com.example.jetty_jersey;

import javax.mail.internet.*;
import javax.mail.*;
import java.util.*;

public class TestMail {
	private final static String MAILER_VERSION = "Java";
	public static boolean envoyerMailSMTP(String serveur,String from, String to, boolean debug) {
		boolean result = false;
		try {
			Properties prop = System.getProperties();
			prop.put("mail.smtp.host", "smtp.gmail.com");
			prop.put("mail.smtp.port", "587");
			Session session = Session.getDefaultInstance(prop,null);
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from));
			InternetAddress[] inAddresses = new InternetAddress[1];
			inAddresses[0] = new InternetAddress(to);
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
			message.setSubject("Yo!!");
			message.setText("VAS_y");
			message.setHeader("X-Mailer", MAILER_VERSION);
			message.setSentDate(new Date());
			session.setDebug(debug);
			Transport.send(message);
			result = true;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	public static void main(String[] args) {
		TestMail.envoyerMailSMTP("10.0.0.3", "nassim.ouhenia@gmail.com", "nassim.ouhenia@gmail.com", true);
		
	}

}
