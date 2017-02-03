package ru.it.lecm.events.mail.incoming;

import net.fortuna.ical4j.model.parameter.PartStat;
import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.util.PropertyCheck;
import org.slf4j.LoggerFactory;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.base.beans.WriteTransactionNeededException;
import ru.it.lecm.contractors.api.Contractors;
import ru.it.lecm.events.beans.EventsNotificationsService;
import ru.it.lecm.events.beans.EventsService;
import ru.it.lecm.events.ical.CalendarReply;
import ru.it.lecm.events.ical.ICalUtils;
import ru.it.lecm.events.mail.IMAPClient;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

import javax.mail.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author vkuprin
 */
public class MailReciever extends BaseBean {

	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(MailReciever.class);

	private static final String CALENDAR_CONTENT_TYPE = "text/calendar";
	private static final String ATTACHMENT_CALENDAR_CONTENT_TYPE = "application/ics";

	private String destinationFolderName;

	public static final String persistantFileName = "mail_params";

	private EventsService eventsService;

	private Map<NodeRef, Map<String, Date>> answers = new HashMap<>();

	private String mailHost;
	private String mailUsername;
	private String mailPassword;
	private String mailProtocol;
	private String mailInboxFolder;
	private String mailDestinationFolder;
	private ContentService contentService;
	private OrgstructureBean orgstructureBean;
	private EventsNotificationsService eventsNotificationsService;

	public EventsNotificationsService getEventsNotificationsService() {
		return eventsNotificationsService;
	}

	public void setEventsNotificationsService(EventsNotificationsService eventsNotificationsService) {
		this.eventsNotificationsService = eventsNotificationsService;
	}

	public OrgstructureBean getOrgstructureBean() {
		return orgstructureBean;
	}

	public void setOrgstructureBean(OrgstructureBean orgstructureBean) {
		this.orgstructureBean = orgstructureBean;
	}

	public ContentService getContentService() {
		return contentService;
	}

	public void setContentService(ContentService contentService) {
		this.contentService = contentService;
	}

	public String getMailHost() {
		return mailHost;
	}

	public void setMailHost(String mailHost) {
		this.mailHost = mailHost;
	}

	public String getMailUsername() {
		return mailUsername;
	}

	public void setMailUsername(String mailUsername) {
		this.mailUsername = mailUsername;
	}

	public String getMailPassword() {
		return mailPassword;
	}

	public void setMailPassword(String mailPassword) {
		this.mailPassword = mailPassword;
	}

	public String getMailProtocol() {
		return mailProtocol;
	}

	public void setMailProtocol(String mailProtocol) {
		this.mailProtocol = mailProtocol;
	}

	public String getMailInboxFolder() {
		return mailInboxFolder;
	}

	public void setMailInboxFolder(String mailInboxFolder) {
		this.mailInboxFolder = mailInboxFolder;
	}

	public String getMailDestinationFolder() {
		return mailDestinationFolder;
	}

	public void setMailDestinationFolder(String mailDestinationFolder) {
		this.mailDestinationFolder = mailDestinationFolder;
	}

	public String getDestinationFolderName() {
		return destinationFolderName;
	}

	public void setDestinationFolderName(String destinationFolderName) {
		this.destinationFolderName = destinationFolderName;
	}

	public EventsService getEventsService() {
		return eventsService;
	}

	public void setEventsService(EventsService eventsService) {
		this.eventsService = eventsService;
	}

	public void init() {
		PropertyCheck.mandatory(this, "eventsService", eventsService);
		PropertyCheck.mandatory(this, "nodeService", nodeService);

		PropertyCheck.mandatory(this, "mailHost", mailHost);
		PropertyCheck.mandatory(this, "mailUsername", mailUsername);
		PropertyCheck.mandatory(this, "mailPassword", mailPassword);
		PropertyCheck.mandatory(this, "mailProtocol", mailProtocol);
		PropertyCheck.mandatory(this, "mailInboxFolder", mailInboxFolder);
		PropertyCheck.mandatory(this, "mailDestinationFolder", mailDestinationFolder);
	}

	@Override
	public NodeRef getServiceRootFolder() {
		if (eventsService instanceof BaseBean) {
			return ((BaseBean) eventsService).getServiceRootFolder();
		} else {
			throw new RuntimeException();
		}
	}

	public Date getPrevCheckTime() {
		NodeRef persistantNode = nodeService.getChildByName(getServiceRootFolder(), ContentModel.ASSOC_CONTAINS, persistantFileName);
		if (null == persistantNode) {
			return null;
		} else {
			try {
				Date result = DateFormatISO8601.parse(contentService.getReader(persistantNode, ContentModel.PROP_CONTENT).getContentString());
				return result;
			} catch (Exception ex) {
				return null;
			}
		}
	}

	public void updatePrevChecktime(Date newTime) throws WriteTransactionNeededException {
		NodeRef persistantNode = nodeService.getChildByName(getServiceRootFolder(), ContentModel.ASSOC_CONTAINS, persistantFileName);
		if (null == persistantNode) {
			persistantNode = createNode(getServiceRootFolder(), ContentModel.TYPE_CONTENT, persistantFileName, null);
			hideNode(persistantNode, true);
		}
		ContentWriter cw = contentService.getWriter(persistantNode, ContentModel.PROP_CONTENT, true);
		cw.putContent(DateFormatISO8601.format(newTime));
		//nodeService.setProperty(persistantNode, ContentModel.PROP_CONTENT, DateFormatISO8601.format(newTime));
	}

	public void recieveMail() throws WriteTransactionNeededException {
        //Queue<String> icals = new LinkedList<>();
        if (mailHost.equals("${mail.ical.host}")) {
            logger.warn("iCal settings is not set");
            return;
        }
		IMAPClient client = new IMAPClient(mailHost, mailUsername, mailPassword, mailProtocol, mailInboxFolder);
		Date updateTime = new Date();
		Date prevCheckTime = getPrevCheckTime();

		try {
			client.connect();
			List<Message> messages = client.getMessages();
			ICalUtils iCalUtils = new ICalUtils();
			for (int i = messages.size() - 1; i > -1; i--) {
				Message message = messages.get(i);
				if (!message.getFlags().contains(Flags.Flag.SEEN)) {
					try {
						Object content = message.getContent();
						if (content instanceof Multipart) {
							String ical = getIcal((Multipart) message.getContent());

							if (null != ical) {
								CalendarReply reply = iCalUtils.readReply(ical);
								processReply(reply);
								message.getFlags().add(Flags.Flag.SEEN);
							}
						}
					} catch (Exception e) {
						logger.error("Error while parsing message " + message.getMessageNumber(), e);
					}
				}
				if (null != prevCheckTime && message.getSentDate().before(prevCheckTime)) {
					updatePrevChecktime(updateTime);
					break;
				}
			}
			if (null == prevCheckTime) {
				updatePrevChecktime(updateTime);
			}
		} catch (MessagingException ex) {
			Logger.getLogger(MailReciever.class.getName()).log(Level.SEVERE, null, ex);
		} finally {
			client.disconnect();
		}

	}

	private void processReply(CalendarReply reply) {
		String id = reply.getUid().split("@")[0];
		NodeRef event = new NodeRef(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE, id);
		if (nodeService.exists(event)) {
			String mail = reply.getAttendeeMail();
			List<NodeRef> members = eventsService.getEventMembers(event);
			for (NodeRef member : members) {
				String memberMail = nodeService.getProperty(member, OrgstructureBean.PROP_EMPLOYEE_EMAIL).toString();
				if (memberMail.equalsIgnoreCase(mail)) {
					Date time = null;
					if (null != answers.get(event)) {
						time = answers.get(event).get(mail);
					} else {
						answers.put(event, new HashMap<String, Date>());
					}
					if (null == time || time.before(reply.getTimeStamp())) {
						NodeRef tableRow = eventsService.getMemberTableRow(event, member);
						if (reply.getAnswer().equals(PartStat.ACCEPTED.getValue())) {
							nodeService.setProperty(tableRow, EventsService.PROP_EVENT_MEMBERS_STATUS, EventsService.CONSTRAINT_EVENT_MEMBERS_STATUS_CONFIRMED);
						} else if (reply.getAnswer().equals(PartStat.DECLINED.getValue())) {
							nodeService.setProperty(tableRow, EventsService.PROP_EVENT_MEMBERS_STATUS, EventsService.CONSTRAINT_EVENT_MEMBERS_STATUS_DECLINED);
						} else if (reply.getAnswer().equals(PartStat.TENTATIVE.getValue())) {
							nodeService.setProperty(tableRow, EventsService.PROP_EVENT_MEMBERS_STATUS, EventsService.CONSTRAINT_EVENT_MEMBERS_STATUS_EMPTY);
						}
						eventsNotificationsService.notifyOrganizerMemberStatusChanged(event, member);
						answers.get(event).put(memberMail, reply.getTimeStamp());
					}

				}
			}
			List<NodeRef> invitedMembers = eventsService.getEventInvitedMembers(event);
			for (NodeRef member : invitedMembers) {
				String memberMail = nodeService.getProperty(member, Contractors.PROP_REPRESENTATIVE_EMAIL).toString();
				if (memberMail.equalsIgnoreCase(mail)) {
					Date time = null;
					if (null != answers.get(event)) {
						time = answers.get(event).get(mail);
					} else {
						answers.put(event, new HashMap<String, Date>());
					}
					if (null == time || time.before(reply.getTimeStamp())) {
						String status = null;
						if (reply.getAnswer().equals(PartStat.ACCEPTED.getValue())) {
							status = EventsService.CONSTRAINT_EVENT_MEMBERS_STATUS_CONFIRMED;
						} else if (reply.getAnswer().equals(PartStat.DECLINED.getValue())) {
							status = EventsService.CONSTRAINT_EVENT_MEMBERS_STATUS_DECLINED;
						} else if (reply.getAnswer().equals(PartStat.TENTATIVE.getValue())) {
							status = EventsService.CONSTRAINT_EVENT_MEMBERS_STATUS_EMPTY;
						}
						eventsNotificationsService.notifyOrganizerInvitedMemberStatusChanged(event, member, status);
						answers.get(event).put(memberMail, reply.getTimeStamp());
					}
				}
			}

		}
	}

	private String getIcal(BodyPart bodyPart) throws IOException, MessagingException {
		if (bodyPart.isMimeType(CALENDAR_CONTENT_TYPE) || bodyPart.isMimeType(ATTACHMENT_CALENDAR_CONTENT_TYPE)) {
			StringBuilder ical = new StringBuilder();

			InputStream is = bodyPart.getInputStream();
			//BufferedReader br = new BufferedReader(new InputStreamReader(is, bodyPart.getHeader("charset").toString()));
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String line = br.readLine();
			while (line != null) {
				if (line.charAt(0) == ' ') {
					ical.append(line.substring(1));
				} else {
					ical.append("\n").append(line);
				}
				line = br.readLine();
			}
			return ical.toString().substring(1);
		} else if (bodyPart.isMimeType("multipart/alternative") || bodyPart.isMimeType("multipart/relative")) {
			Multipart multipart = (Multipart) bodyPart.getContent();
			return getIcal(multipart);
		}
		return null;
	}

	private String getIcal(Multipart multipart) throws IOException, MessagingException {
		for (int i = 0; i < multipart.getCount(); i++) {
			BodyPart bp = multipart.getBodyPart(i);
			String ical = getIcal(bp);
			if (null != ical) {
				return ical;
			}
		}
		return null;
	}

}
