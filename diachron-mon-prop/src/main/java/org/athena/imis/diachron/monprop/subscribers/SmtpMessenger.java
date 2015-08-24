package org.athena.imis.diachron.monprop.subscribers;



import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;


public class SmtpMessenger implements Messenger {

	
	private void sendEmailToAddress(String AddressesCommaSeparated, String mailBody,
			String mailSubject, String parametersPrefix) throws Exception, MessagingException,
			UnsupportedEncodingException, AddressException, URISyntaxException,
			NoSuchProviderException {

		final String outgoingUser = "";//srv.getValueByLabel(parametersPrefix+"_OUTGOING_SMTP_USER").getValue();
		final String outgoingPwd = "";//srv.getValueByLabel(parametersPrefix+"_OUTGOING_SMTP_PWD").getValue();
		final String outgoingMail = "";//srv.getValueByLabel(parametersPrefix+"_OUTGOING_SMTP_MAIL").getValue();
		final String outgoingName = "";//srv.getValueByLabel(parametersPrefix+"_OUTGOING_SMTP_FROM").getValue();
		final String outgoingSMTP = "";//srv.getValueByLabel(parametersPrefix+"_OUTGOING_SMTP_HOST").getValue();
		final String outgoingSMTPPort = "";//srv.getValueByLabel(parametersPrefix+"_OUTGOING_SMTP_PORT").getValue();;
				
		Properties props = new Properties();
		//*
		props.put("mail.transport.protocol", "smtp");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.host", outgoingSMTP);
		props.put("mail.smtp.port", outgoingSMTPPort);
		//props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        //props.put("mail.smtp.socketFactory.fallback", "false");
		//*/
		//props.put("mail.smtp.localhost", "ppp079167055194.access.hol.gr");

		
		props.put("mail.transport.protocol", "smtps");
	    props.put("mail.smtps.auth", "true");
		props.put("mail.smtps.host", outgoingSMTP);
		props.put("mail.smtps.port", outgoingSMTPPort);
		//* /
	    props.put("mail.smtp.starttls.enable", "true");
 
		Session session = Session.getInstance(props,
		  new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(outgoingUser, outgoingPwd);
			}
		  });
 
		
		Message message = new MimeMessage(session);
		message.setFrom(new InternetAddress(outgoingMail, outgoingName));
		message.setRecipients(Message.RecipientType.TO,
			InternetAddress.parse(AddressesCommaSeparated));
		message.setSubject(mailSubject);
		/*
			// simple html message
			message.setSubject("Testing Subject");
			// plain text message
			//message.setText("Dear <br/>Mail Crawler,"
			//	+ "\n\n No spam to my email, please!");
 
			message.setContent("<h1>Hello world</h1>"
					+ "<img src=\"http://www.betvolution.com/img/logo_betvolution_beta.png\">"
					, "text/html");
	        */
        
		// HTML message with image embedded
        // This HTML mail have to 2 part, the BODY and the embedded image
        //
        MimeMultipart multipart = new MimeMultipart("related");

        // first part  (the html)
        BodyPart messageBodyPart = new MimeBodyPart();
        //String htmlText = "<H1>Hello xa</H1><img src=\"cid:image\">";
        messageBodyPart.setContent(mailBody, "text/html; charset=utf-8");

        // add it
        multipart.addBodyPart(messageBodyPart);
        
        /*
        // second part (the image)
        messageBodyPart = new MimeBodyPart(); 
        File file = new File((new Utils()).getClass().getResource("/betvolution_mail.jpg").toURI());
        DataSource fds = new FileDataSource(file);
        messageBodyPart.setDataHandler(new DataHandler(fds));
        messageBodyPart.setDisposition(MimeBodyPart.INLINE);
        messageBodyPart.setHeader("Content-ID","<image>");

        // add it
        multipart.addBodyPart(messageBodyPart);
		*/
        // put everything together
        message.setContent(multipart);

        //Transport t = session.getTransport("smtps");
        Transport t = session.getTransport("smtp");
        try {
        	t.connect(outgoingSMTP, outgoingUser, outgoingPwd);
        	t.sendMessage(message, message.getAllRecipients());
        } finally {
        	t.close();
        }
		//Transport.send(message);
		 
	}

	@Override
	public void sendMessage(
			org.athena.imis.diachron.monprop.subscribers.Message message) {
		// TODO Auto-generated method stub
		System.out.println("SENDING>>>>");
		
	}

	@Override
	public Object publishMessages(
			List<org.athena.imis.diachron.monprop.subscribers.Message> messages) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getMessageType() {
		return "EMAIL";
	}
}
