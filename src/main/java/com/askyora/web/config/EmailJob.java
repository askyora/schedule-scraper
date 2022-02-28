package com.askyora.web.config;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class EmailJob extends QuartzJobBean {

	private static final Logger log = LoggerFactory.getLogger(EmailJob.class);

	@Value("${spring.mail.username}")
	String username;
	@Value("${spring.mail.password}")
	String password;

	@Value("${spring.mail.host}")
	String host;

	@Value("${spring.mail.port}")
	String port;

	@Value("${spring.mail.properties.mail.smtp.auth}")
	String auth;

	@Value("${spring.mail.properties.mail.smtp.starttls.enable}")
	String tls;

	@Override
	protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		log.info("Executing Job with key {}", jobExecutionContext.getJobDetail().getKey());

		JobDataMap jobDataMap = jobExecutionContext.getMergedJobDataMap();
		String subject = jobDataMap.getString("subject");
		String body = jobDataMap.getString("body");
		String recipientEmail = jobDataMap.getString("email");
		String from = jobDataMap.getString("from");
		log.debug("sub {} body {} email {} from {}", subject, body, recipientEmail, from);
		send(from, recipientEmail, subject, body);

	}

	private void send(String from, String to, String subject, String body

	) {

		Properties prop = new Properties();
		prop.put("mail.smtp.host", host);
		prop.put("mail.smtp.port", port);
		prop.put("mail.smtp.auth", auth);
		prop.put("mail.smtp.starttls.enable", tls); // TLS

		Session session = Session.getInstance(prop, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});

		try {
			log.info("Send username : {}", username);
			javax.mail.Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
			message.setSubject(subject);
			message.setText(body);
			Transport.send(message);
			log.debug("Send subject: {} body: {} email: {} from: {}", subject, body, to, from);
		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}

}
