package ru.it.lecm.events.beans;

import javax.mail.internet.MimeMessage;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.service.transaction.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;

/**
 *
 * @author vkuprin
 */
public class RawMailSender implements Runnable, AuthenticationUtil.RunAsWork {

	private final static Logger logger = LoggerFactory.getLogger(RawMailSender.class);
	private final MimeMessage rawMessage;
	private final JavaMailSender mailService;
	private final TransactionService transactionService;

	
	public RawMailSender(MimeMessage rawMessage, JavaMailSender mailSender, TransactionService transactionService) {
		this.rawMessage = rawMessage;
		this.mailService = mailSender;
		this.transactionService = transactionService;
	}

	
	
	@Override
	public void run() {
		//Нужно запускать от системы, т.к. могут быть вложения.
		AuthenticationUtil.runAsSystem(this);
	}

	@Override
	public Void doWork() throws Exception {
		try {
			transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>() {
				@Override
				public Void execute() throws Throwable {
					mailService.send(rawMessage);
					return null;
				}
			}, true);
		} catch (Exception e) {
			logger.error("Error send mail", e);
		}
		return null;
	}

	
}
