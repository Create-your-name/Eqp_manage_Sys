/*
 * 创建日期 2006-11-22
 *
 * @author laol
 */
package com.csmc.util.mail;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import com.csmc.util.Log;

import freemarker.template.Template;
import freemarker.template.TemplateException;

public class MimeMailManagerImpl implements MimeMailManager {

    public static final String module = MimeMailManagerImpl.class.getName();
    
    private JavaMailSender mailSender;
    
    private SimpleMailMessage message;

    private FreeMarkerConfigurer mailTemplateEngine;

    public void setMailSender(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void setMessage(SimpleMailMessage message) {
        this.message = message;
    }

    public void setMailTemplateEngine(FreeMarkerConfigurer mailTemplateEngine) {
		this.mailTemplateEngine = mailTemplateEngine;
	}

    public void sendMail(String[] sendTo, String subject, String templateName, Map model) {
    	// 生成html邮件内容
		String content = generateEmailContent(templateName, model);
		MimeMessage mimeMsg;
		try {
			if(content == null) throw new MessagingException("mail content is null!");
			
			message.setTo(sendTo);
			Log.logInfo(sendTo.toString(), module);
			message.setSubject(subject);
			mimeMsg = mailSender.createMimeMessage();
			
			MimeMessageHelper helper = new MimeMessageHelper(mimeMsg, true, "utf-8");
			helper.setTo(message.getTo());
			helper.setSubject(message.getSubject());
			helper.setFrom(message.getFrom());
			helper.setText(content, true);
			mailSender.send(mimeMsg);
			Log.logInfo("send mimemail success", module);
		} catch (MessagingException ex) {
			Log.logError(ex.getMessage(), module);
		} catch (MailException e) {
			Log.logError(e.getMessage(), module);
		}

    }

    public String generateEmailContent(String templateName, Map map) {
    	try {
			Template t = mailTemplateEngine.getConfiguration().getTemplate(templateName);
			return FreeMarkerTemplateUtils.processTemplateIntoString(t, map);
		} catch (TemplateException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
    }

    public void sendMail(final String body, final InternetAddress[] mailAddresses, final String subject) throws MailException {
        MimeMessagePreparator preparator = new MimeMessagePreparator() {
            
            public void prepare(MimeMessage mimeMessage) throws MessagingException {
                
                mimeMessage.addRecipients(Message.RecipientType.TO, mailAddresses);
                
                Multipart mp = new MimeMultipart(); 
                MimeBodyPart mbp = new MimeBodyPart(); 
                    
                // 设定邮件内容的类型为 text/plain 或 text/html 
                mbp.setContent(body,"text/html;charset=GB2312"); 
                mp.addBodyPart(mbp); 
                
                mimeMessage.setContent(mp);
                mimeMessage.setSubject(subject, "gb2312");
                mimeMessage.setFrom(new InternetAddress(message.getFrom()));
            }
        };
        
        try{
            mailSender.send(preparator);
        }
        catch(MailException ex) {
            System.err.println(ex.getMessage());            
        }
        
    }
}
