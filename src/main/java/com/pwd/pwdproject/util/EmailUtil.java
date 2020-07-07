package com.pwd.pwdproject.util;

import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component
public class EmailUtil {
	
	@Autowired
	private JavaMailSender sender;

	public void sendEmail(String toAddress, String subject , String body) {
		MimeMessage message = this.sender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);
	
		try {
			helper.setTo(toAddress);
			helper.setSubject(subject);
			helper.setText(body,true);
			sender.send(message);
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
}
