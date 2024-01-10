/*
 * ???????? 2006-11-20
 *
 * @author laol
 */
package com.csmc.util.mail;
import java.util.List;

import javax.mail.internet.InternetAddress;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

public class MailManagerImpl implements MailManager {

    private MailSender mailSender;
    
    private SimpleMailMessage message;

    public void setMailSender(MailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void setMessage(SimpleMailMessage message) {
        this.message = message;
    }

    public void sendMail(String[] mailers, String[] ccMailers, String context, String subject) {
        SimpleMailMessage msg = new SimpleMailMessage(this.message);
        msg.setSubject(subject);
        msg.setTo(mailers);
        msg.setBcc("liuhai82@rxgz.crmicro.com"); //调试：增加密送管理员邮箱
        if(ccMailers != null && ccMailers.length > 0) {
        	msg.setCc(ccMailers);
        }
        msg.setText(context);
        try{
            mailSender.send(msg);
        }
        catch(MailException ex) {
            System.err.println(ex.getMessage());            
        }
    }
    
    public void sendMail(String mailers, String ccMailers, String context, String subject) {
    	SimpleMailMessage msg = new SimpleMailMessage(this.message);
        
    	if(mailers != null && mailers.length() > 0) {
    		msg.setTo(mailers.split(";"));
    	}
    	msg.setBcc("liuhai82@rxgz.crmicro.com"); //调试：增加密送管理员邮箱
        if(ccMailers != null && ccMailers.length() > 0) {
        	msg.setCc(ccMailers.split(";"));
        }
        
        msg.setSubject(subject);
        msg.setText(context);
        
        try {
            mailSender.send(msg);
        } catch(MailException ex) {
            System.err.println(ex.getMessage());            
        }
    }
}