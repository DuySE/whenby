package com.example.service;

import java.io.UnsupportedEncodingException;
import java.util.Properties;

import org.springframework.stereotype.Service;

import com.example.demo.model.Email;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;

@Service
public class EmailService {

	public void sendEmail(Email email) throws MessagingException, UnsupportedEncodingException {
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");

		Session session = Session.getInstance(props, new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(email.getFrom(), "fgkk muzn htpv bomc");
			}
		});

		Message message = new MimeMessage(session);
		MimeBodyPart body = new MimeBodyPart();
		Multipart multipart = new MimeMultipart();

		message.setFrom(new InternetAddress("tutorialspoint@gmail.com", false));
		message.setRecipients(Message.RecipientType.TO, InternetAddress.parse("duynse@gmail.com"));
		message.setSubject(email.getSubject());

		body.setContent(email.getBody(), "text/html");

		multipart.addBodyPart(body);

		message.setContent(multipart);

		Transport.send(message);
	}
}
