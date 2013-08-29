package ru.it.lecm.signed.docflow;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.util.PropertyCheck;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.signed.docflow.api.SignedDocflowModel;

/**
 *
 * @author vlevin
 */
public class ReceiveContentByEmailService extends BaseBean {

	private final static Logger logger = LoggerFactory.getLogger(ReceiveContentByEmailService.class);
	private ZipSignedContentService zipSignedContentService;
	private String mailHost;
	private String mailUsername;
	private String mailPassword;
	private String mailProtocol;
	private String mailInboxFolder;
	private String mailDestinationFolder;

	public void setZipSignedContentService(ZipSignedContentService zipSignedContentService) {
		this.zipSignedContentService = zipSignedContentService;
	}

	public void setMailHost(String mailHost) {
		this.mailHost = mailHost;
	}

	public void setMailUsername(String mailUsername) {
		this.mailUsername = mailUsername;
	}

	public void setMailPassword(String mailPassword) {
		this.mailPassword = mailPassword;
	}

	public void setMailProtocol(String mailProtocol) {
		this.mailProtocol = mailProtocol;
	}

	public void setMailInboxFolder(String mailInboxFolder) {
		this.mailInboxFolder = mailInboxFolder;
	}

	public void setMailDestinationFolder(String mailDestinationFolder) {
		this.mailDestinationFolder = mailDestinationFolder;
	}

	public void init() {
		PropertyCheck.mandatory(this, "nodeService", nodeService);
		PropertyCheck.mandatory(this, "zipSignedContentService", zipSignedContentService);
		PropertyCheck.mandatory(this, "mailHost", mailHost);
		PropertyCheck.mandatory(this, "mailUsername", mailUsername);
		PropertyCheck.mandatory(this, "mailPassword", mailPassword);
		PropertyCheck.mandatory(this, "mailProtocol", mailProtocol);
		PropertyCheck.mandatory(this, "mailInboxFolder", mailInboxFolder);
		PropertyCheck.mandatory(this, "mailDestinationFolder", mailDestinationFolder);
	}

	public List<String> getSignaturesForContentByEmail(NodeRef contentRef) {
		final List<String> result = new ArrayList<String>();
		final String documentID = (String) nodeService.getProperty(contentRef, SignedDocflowModel.PROP_DOCUMENT_ID);
		final String contentName = (String) nodeService.getProperty(contentRef, ContentModel.PROP_NAME);
		if (documentID == null) {
			throw new IllegalArgumentException(String.format("Error! Node [%s] has never been sent to partner", contentRef.toString()));
		}
		IMAPClient mailClient = new IMAPClient(mailHost, mailUsername, mailPassword, mailProtocol, mailInboxFolder);
		try {
			mailClient.connect();
			final File tmpDir = mailClient.getTmpDir();
			List<Message> messages = mailClient.getMessagesByThemeSubstr(documentID);
			for (Message message : messages) {
				List<File> messageAttachments = mailClient.getMessageAttachment(message);
				for (File messageAttachment : messageAttachments) {
					final String attachmentName = messageAttachment.getName();
					final String assumedContentName = FilenameUtils.removeExtension(attachmentName);
					if (!StringUtils.equalsIgnoreCase(contentName, assumedContentName)) {
						continue;
					}
					List<File> unzippedFiles = zipSignedContentService.unzipFile(messageAttachment, tmpDir);
					for (File unzippedFile : unzippedFiles) {
						final String unzippedFileName = unzippedFile.getName();
						if (StringUtils.equalsIgnoreCase(assumedContentName, unzippedFileName)) {
							continue;
						}
						result.add(FileUtils.readFileToString(unzippedFile));
					}
					mailClient.moveMessageFromInbox(message, mailDestinationFolder);
				}
			}
		} catch (NoSuchProviderException ex) {
			logger.error("Can not find mail provider", ex);
			throw new RuntimeException(ex);
		} catch (MessagingException ex) {
			logger.error("Mail error!", ex);
			throw new RuntimeException(ex);
		} catch (IOException ex) {
			logger.error("File error!", ex);
			throw new RuntimeException(ex);
		} finally {
			// не забываем закрыть соединение и удалить временные файлы
			mailClient.disconnect();
		}
		return result;
	}

	@Override
	public NodeRef getServiceRootFolder() {
		return null;
	}
}
