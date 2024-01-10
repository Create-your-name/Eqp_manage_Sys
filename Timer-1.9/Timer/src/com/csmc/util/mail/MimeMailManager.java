package com.csmc.util.mail;

import java.util.Map;

import javax.mail.internet.InternetAddress;

import org.springframework.mail.MailException;

public interface MimeMailManager {
	public void sendMail(String[] sendTo, String subject, String templateName, Map model);
	public void sendMail(final String body, final InternetAddress[] mailAddresses, final String subject) throws MailException;
}