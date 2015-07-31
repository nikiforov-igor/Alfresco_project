package ru.it.lecm.events.mail;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeUtility;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author vlevin
 */
public class IMAPClient {

	final private String host;
	final private String username;
	final private String password;
	final private String protocol;
	final private String folderStr;
	final private Properties props;
	private Folder inbox;
	private Store store;
	//private File tmpDir;

	public IMAPClient(String host, String username, String password, String protocol, String folder) {
		this.host = host;
		this.username = username;
		this.password = password;
		this.protocol = protocol;
		this.folderStr = folder;

		props = System.getProperties();
		props.setProperty("mail.store.protocol", protocol);
	}

	public void connect() throws NoSuchProviderException, MessagingException {
		Session mailSession = Session.getDefaultInstance(props, null);
		store = mailSession.getStore(protocol);
		store.connect(host, username, password);
		//tmpDir = Utils.createTmpDir();

	}

	public void disconnect() {
//		FileUtils.deleteQuietly(tmpDir);
		try {
			if (inbox != null && inbox.isOpen()) {
				inbox.close(true);
			}
			if (store != null && store.isConnected()) {
				store.close();
			}
		} catch (MessagingException ex) {
		}
	}

	public List<Message> getMessagesByThemeSubstr(String themeSubstr) throws MessagingException {
		List<Message> result = new ArrayList<Message>();
		List<Message> messagesList = getMessages();
		for (Message message : messagesList) {
			String subject = message.getSubject();
			if (StringUtils.containsIgnoreCase(subject, themeSubstr)) {
				result.add(message);
			}
		}
		return result;
	}

	public List<Message> getMessages() throws MessagingException {
		inbox = store.getFolder(folderStr);
		inbox.open(Folder.READ_WRITE);
		Message[] messages = inbox.getMessages();

		return Arrays.asList(messages);
	}

	public void moveMessageFromInbox(Message message, String destFolderName) throws MessagingException {
		Message[] messageArray = new Message[1];
		messageArray[0] = message;
		Folder destFolder = store.getFolder(destFolderName);
		if (!destFolder.exists()) {
			destFolder.create(Folder.HOLDS_MESSAGES);
		}
		destFolder.open(Folder.READ_WRITE);
		inbox.setFlags(messageArray, new Flags(Flags.Flag.SEEN), true);
		inbox.copyMessages(messageArray, destFolder);
		inbox.setFlags(messageArray, new Flags(Flags.Flag.DELETED), true);
		destFolder.close(true);
	}

	public List<File> getMessageAttachment(Message message) throws IOException, MessagingException {
		
		List<File> attachments = new ArrayList<File>();

		Multipart multipart = (Multipart) message.getContent();

		for (int i = 0; i < multipart.getCount(); i++) {
			BodyPart bodyPart = multipart.getBodyPart(i);
			if (!Part.ATTACHMENT.equalsIgnoreCase(bodyPart.getDisposition())) {
				continue;
			}
			InputStream inputStream = bodyPart.getInputStream();
			String fileName = MimeUtility.decodeText(bodyPart.getFileName());
//			File file = new File(tmpDir, fileName);
//			FileOutputStream fileOutputStream = new FileOutputStream(file);
			byte[] buf = new byte[4096];
			int bytesRead;
			while ((bytesRead = inputStream.read(buf)) != -1) {
//				fileOutputStream.write(buf, 0, bytesRead);
			}
//			fileOutputStream.close();
//			attachments.add(file);
		}
		return attachments;
	}

//	public File getTmpDir() {
//		return tmpDir;
//	}
}
