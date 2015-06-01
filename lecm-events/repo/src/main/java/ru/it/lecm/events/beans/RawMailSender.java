package ru.it.lecm.events.beans;

import javax.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;

/**
 *
 * @author vkuprin
 */
public class RawMailSender implements Runnable {

	private final static Logger logger = LoggerFactory.getLogger(RawMailSender.class);
	private final MimeMessage rawMessage;
	private final JavaMailSender mailService;
	
	
	public RawMailSender(MimeMessage rawMessage, JavaMailSender mailSender) {
		this.rawMessage = rawMessage;
		this.mailService = mailSender;
	}
	
	@Override
	public void run() {
		try {
			mailService.send(rawMessage);
		} catch (Exception e) {
			logger.error("Error send mail", e);
		}
	}
	
	
}
