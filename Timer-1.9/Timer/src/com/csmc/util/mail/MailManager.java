/*
 * 创建日期 2006-11-20
 *
 * @author laol
 */
package com.csmc.util.mail;

import org.springframework.mail.MailException;

public interface MailManager {
    //SendMail
	public void sendMail(String[] mailers, String[] ccMailers, String Context, String subject);
    
	public void sendMail(String mailers, String ccMailers, String context, String subject);
	
}
