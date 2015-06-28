package ru.it.lecm.events.beans;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.BodyPart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;
import javax.mail.util.ByteArrayDataSource;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.PropertyList;
import net.fortuna.ical4j.model.TimeZone;
import net.fortuna.ical4j.model.TimeZoneRegistry;
import net.fortuna.ical4j.model.TimeZoneRegistryFactory;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.model.parameter.AltRep;
import net.fortuna.ical4j.model.parameter.Cn;
import net.fortuna.ical4j.model.parameter.CuType;
import net.fortuna.ical4j.model.parameter.PartStat;
import net.fortuna.ical4j.model.parameter.Role;
import net.fortuna.ical4j.model.parameter.Rsvp;
import net.fortuna.ical4j.model.property.Attendee;
import net.fortuna.ical4j.model.property.CalScale;
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.DtEnd;
import net.fortuna.ical4j.model.property.DtStart;
import net.fortuna.ical4j.model.property.Location;
import net.fortuna.ical4j.model.property.Method;
import net.fortuna.ical4j.model.property.Organizer;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.Sequence;
import net.fortuna.ical4j.model.property.Status;
import net.fortuna.ical4j.model.property.Summary;
import net.fortuna.ical4j.model.property.Uid;
import net.fortuna.ical4j.model.property.Version;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.admin.SysAdminParams;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.TemplateService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.contractors.api.Contractors;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.notifications.beans.NotificationsService;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

/**
 *
 * @author vkuprin
 */
public class EventsNotificationsService extends BaseBean {

	private final static Logger logger = LoggerFactory.getLogger(EventsNotificationsService.class);

	private static final String MESSAGE_TEMPLATES_PATH = "/alfresco/templates/webscripts/ru/it/lecm/events/mail/";

	private static final String INVITED_MEMBERS_PLAIN_TEXT_NEW_EVENT_MESSAGE_TEMPLATE = MESSAGE_TEMPLATES_PATH + "invited-members-plain-text-new-event-message.ftl";
	private static final String INVITED_MEMBERS_PLAIN_TEXT_UPDATE_EVENT_MESSAGE_TEMPLATE = MESSAGE_TEMPLATES_PATH + "invited-members-plain-text-update-event-message.ftl";
	private static final String INVITED_MEMBERS_PLAIN_TEXT_CANCEL_EVENT_MESSAGE_TEMPLATE = MESSAGE_TEMPLATES_PATH + "invited-members-plain-text-cancel-event-message.ftl";
	private static final String INVITED_MEMBERS_HTML_NEW_EVENT_MESSAGE_TEMPLATE = MESSAGE_TEMPLATES_PATH + "invited-members-html-new-event-message.ftl";
	private static final String INVITED_MEMBERS_HTML_UPDATE_EVENT_MESSAGE_TEMPLATE = MESSAGE_TEMPLATES_PATH + "invited-members-html-update-event-message.ftl";
	private static final String INVITED_MEMBERS_HTML_CANCEL_EVENT_TEMPLATE = MESSAGE_TEMPLATES_PATH + "invited-members-html-cancel-event-message.ftl";

	private static final String MEMBERS_PLAIN_TEXT_NEW_EVENT_MESSAGE_TEMPLATE = MESSAGE_TEMPLATES_PATH + "members-plain-text-new-event-message.ftl";
	private static final String MEMBERS_PLAIN_TEXT_UPDATE_EVENT_MESSAGE_TEMPLATE = MESSAGE_TEMPLATES_PATH + "members-plain-text-update-event-message.ftl";
	private static final String MEMBERS_PLAIN_TEXT_CANCEL_EVENT_MESSAGE_TEMPLATE = MESSAGE_TEMPLATES_PATH + "members-plain-text-cancel-event-message.ftl";
	private static final String MEMBERS_HTML_NEW_EVENT_MESSAGE_TEMPLATE = MESSAGE_TEMPLATES_PATH + "members-html-new-event-message.ftl";
	private static final String MEMBERS_HTML_UPDATE_EVENT_MESSAGE_TEMPLATE = MESSAGE_TEMPLATES_PATH + "members-html-update-event-message.ftl";
	private static final String MEMBERS_HTML_CANCEL_EVENT_TEMPLATE = MESSAGE_TEMPLATES_PATH + "members-html-cancel-event-message.ftl";

	private static final String MEMBERS_STANDART_NOTIFICATIONS_NEW_EVENT_MESSAGE_TEMPLATE = MESSAGE_TEMPLATES_PATH + "members-standart-new-event-message.ftl";
	private static final String MEMBERS_STANDART_NOTIFICATIONS_UPDATE_EVENT_MESSAGE_TEMPLATE = MESSAGE_TEMPLATES_PATH + "members-standart-update-event-message.ftl";
	private static final String MEMBERS_STANDART_NOTIFICATIONS_CANCEL_EVENT_MESSAGE_TEMPLATE = MESSAGE_TEMPLATES_PATH + "members-standart-cancel-event-message.ftl";

	private static final String MULTIPART_SUBTYPE_ALTERNATIVE = "alternative";
	private static final String CONTENT_TYPE_ALTERNATIVE = "multipart/alternative";
	private static final String CONTENT_SUBTYPE_HTML = "html";

	private String prodId = "LECM-ALFRESCO";

	final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
	final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

	private Boolean sendIcalToMembers = false;
	private Boolean sendIcalToInvitedMembers = true;
	private String defaultFromEmail;

	private NotificationsService notificationsService;
	private TemplateService templateService;
	private NamespaceService namespaceService;
	private OrgstructureBean orgstructureBean;
	private EventsService eventsService;
	private ContentService contentService;
	private JavaMailSender mailService;
	private ThreadPoolExecutor threadPoolExecutor;

	public ThreadPoolExecutor getThreadPoolExecutor() {
		return threadPoolExecutor;
	}

	public void setThreadPoolExecutor(ThreadPoolExecutor threadPoolExecutor) {
		this.threadPoolExecutor = threadPoolExecutor;
	}

	public String getDefaultFromEmail() {
		return defaultFromEmail;
	}

	public void setDefaultFromEmail(String defaultFromEmail) {
		this.defaultFromEmail = defaultFromEmail;
	}

	public JavaMailSender getMailService() {
		return mailService;
	}

	public void setMailService(JavaMailSender mailService) {
		this.mailService = mailService;
	}

	public ContentService getContentService() {
		return contentService;
	}

	public void setContentService(ContentService contentService) {
		this.contentService = contentService;
	}

	public EventsService getEventsService() {
		return eventsService;
	}

	public void setEventsService(EventsService eventsService) {
		this.eventsService = eventsService;
	}

	public OrgstructureBean getOrgstructureBean() {
		return orgstructureBean;
	}

	public void setOrgstructureBean(OrgstructureBean orgstructureBean) {
		this.orgstructureBean = orgstructureBean;
	}

	public NamespaceService getNamespaceService() {
		return namespaceService;
	}

	public void setNamespaceService(NamespaceService namespaceService) {
		this.namespaceService = namespaceService;
	}

	public NotificationsService getNotificationsService() {
		return notificationsService;
	}

	public void setNotificationsService(NotificationsService notificationsService) {
		this.notificationsService = notificationsService;
	}

	public TemplateService getTemplateService() {
		return templateService;
	}

	public void setTemplateService(TemplateService templateService) {
		this.templateService = templateService;
	}

	public Boolean getSendIcalToMembers() {
		return sendIcalToMembers;
	}

	public void setSendIcalToMembers(Boolean sendIcalToMembers) {
		this.sendIcalToMembers = sendIcalToMembers;
	}

	public Boolean getSendIcalToInvitedMembers() {
		return sendIcalToInvitedMembers;
	}

	public void setSendIcalToInvitedMembers(Boolean sendIcalToInvitedMembers) {
		this.sendIcalToInvitedMembers = sendIcalToInvitedMembers;
	}

	public String getProdId() {
		return prodId;
	}

	public void setProdId(String ProdId) {
		this.prodId = ProdId;
	}

	@Override
	public NodeRef getServiceRootFolder() {
		return null;
	}

	public void notifyEventCancelled(NodeRef event) {
		if (null != event && Boolean.TRUE.equals(nodeService.getProperty(event, EventsService.PROP_EVENT_REMOVED))) {
			nodeService.setProperty(event, EventsService.PROP_EVENT_ICAL_NEXT_SEQUENCE, (Integer) nodeService.getProperty(event, EventsService.PROP_EVENT_ICAL_NEXT_SEQUENCE) + 1);
			Map<String, Object> eventTemplateModel = new HashMap<>(getEventTemplateModel(event));
			List<NodeRef> members = eventsService.getEventMembers(event);
			//сначала шлём участникам, т.к. у них ещё стандартные уведомления
			//Рссылаем стандартные уведомления
			String author = AuthenticationUtil.getSystemUserName();
			//отсылка через стандартные уведомления
			//Возможно, тоже нужно будет сделать персонализированую
			String text = templateService.processTemplate(MEMBERS_STANDART_NOTIFICATIONS_CANCEL_EVENT_MESSAGE_TEMPLATE, eventTemplateModel);
			notificationsService.sendNotification(author, event, text, members, null);
			String subject;
			if (nodeService.getType(event).isMatch(EventsService.TYPE_EVENT)) {
				subject = "Мероприятие ";
			} else {
				subject = "Совещание ";
			}
			subject += eventTemplateModel.get("title")
					+ " отменено.";
			//теперь рассылаем письма участникам
			if (sendIcalToMembers) {
				for (NodeRef recipient : members) {
					String email = (String) nodeService.getProperty(recipient, OrgstructureBean.PROP_EMPLOYEE_EMAIL);
					if (email != null && email.length() > 0) {
						eventTemplateModel.put("recipientMail", email);
						String plainText = templateService.processTemplate(MEMBERS_PLAIN_TEXT_CANCEL_EVENT_MESSAGE_TEMPLATE, eventTemplateModel);
						String htmlText = templateService.processTemplate(MEMBERS_HTML_CANCEL_EVENT_TEMPLATE, eventTemplateModel);
						VEvent vEvent = formCancelEvent(eventTemplateModel, formBasicEvent(eventTemplateModel));
						Calendar calendar = envelopEvent(vEvent, Method.CANCEL);
						sendMail(email, subject, plainText, htmlText, null, calendar);
					}
				}
			}
			//теперь рассылка приглашённым
			List<NodeRef> invitedMembers = eventsService.getEventInvitedMembers(event);
			for (NodeRef recipient : invitedMembers) {
				String email = (String) nodeService.getProperty(recipient, OrgstructureBean.PROP_EMPLOYEE_EMAIL);
				if (email != null && email.length() > 0) {
					eventTemplateModel.put("recipientMail", email);
					String plainText = templateService.processTemplate(INVITED_MEMBERS_PLAIN_TEXT_CANCEL_EVENT_MESSAGE_TEMPLATE, eventTemplateModel);
					String htmlText = templateService.processTemplate(INVITED_MEMBERS_HTML_CANCEL_EVENT_TEMPLATE, eventTemplateModel);
					Calendar calendar = null;
					if (sendIcalToInvitedMembers) {
						VEvent vEvent = formCancelEvent(eventTemplateModel, formBasicEvent(eventTemplateModel));
						calendar = envelopEvent(vEvent, Method.CANCEL);
					}
					sendMail(email, subject, plainText, htmlText, null, calendar);
				}
			}
		}
	}

	public void notifyEventCreated(NodeRef event, List<NodeRef> recipients) {
		notifyEvent(event, true, recipients);
	}

	public void notifyEvent(NodeRef event, boolean isNew, List<NodeRef> recipients) {
		List<NodeRef> members = eventsService.getEventMembers(event);
		List<NodeRef> sendTo = new ArrayList<>(recipients);
		Map<String, Object> eventTemplateModel = new HashMap<>(getEventTemplateModel(event));
		List<DataSource> attachments = new ArrayList<>(getEventAttachments(event));
		if (null != recipients && !recipients.isEmpty()) {
			sendTo.retainAll(members);
		}
		//Рссылаем стандартные уведомления
		String author = AuthenticationUtil.getSystemUserName();
		//отсылка через стандартные уведомления
		//Возможно, тоже нужно будет сделать персонализированую
		String text = templateService.processTemplate(isNew ? MEMBERS_STANDART_NOTIFICATIONS_NEW_EVENT_MESSAGE_TEMPLATE : MEMBERS_STANDART_NOTIFICATIONS_UPDATE_EVENT_MESSAGE_TEMPLATE, eventTemplateModel);
		notificationsService.sendNotification(author, event, text, sendTo, null);
		//теперь рассылаем письма участникам
		if (sendIcalToMembers) {
			for (NodeRef recipient : sendTo) {
				String email = (String) nodeService.getProperty(recipient, OrgstructureBean.PROP_EMPLOYEE_EMAIL);
				if (email != null && email.length() > 0) {
					eventTemplateModel.put("recipientMail", email);
					String plainText = templateService.processTemplate(isNew ? MEMBERS_PLAIN_TEXT_NEW_EVENT_MESSAGE_TEMPLATE : MEMBERS_PLAIN_TEXT_UPDATE_EVENT_MESSAGE_TEMPLATE, eventTemplateModel);
					String htmlText = templateService.processTemplate(isNew ? MEMBERS_HTML_NEW_EVENT_MESSAGE_TEMPLATE : MEMBERS_HTML_UPDATE_EVENT_MESSAGE_TEMPLATE, eventTemplateModel);
					//TODO internationalize
					String subject;
					if (nodeService.getType(event).isMatch(EventsService.TYPE_EVENT)) {
						subject = isNew ? "Приглашение на мероприятие " : "Обновление мероприятия ";
					} else {
						subject = isNew ? "Приглашение на совещание " : "Обновление совещания ";
					}
					subject += eventTemplateModel.get("title");

					VEvent vEvent = formInviteEvent(eventTemplateModel, formBasicEvent(eventTemplateModel));
					Calendar calendar = envelopEvent(vEvent, Method.REQUEST);
					sendMail(email, subject, plainText, htmlText, attachments, calendar);
				}
			}
		}
		//теперь рассылка приглашённым
		List<NodeRef> invitedMembers = eventsService.getEventInvitedMembers(event);
		sendTo = new ArrayList<>(recipients);
		if (null != recipients && !recipients.isEmpty()) {
			sendTo.retainAll(invitedMembers);
		}
		for (NodeRef recipient : sendTo) {
			String email = (String) nodeService.getProperty(recipient, OrgstructureBean.PROP_EMPLOYEE_EMAIL);
			if (email != null && email.length() > 0) {
				eventTemplateModel.put("recipientMail", email);
				String plainText = templateService.processTemplate(isNew ? INVITED_MEMBERS_PLAIN_TEXT_NEW_EVENT_MESSAGE_TEMPLATE : INVITED_MEMBERS_PLAIN_TEXT_UPDATE_EVENT_MESSAGE_TEMPLATE, eventTemplateModel);
				String htmlText = templateService.processTemplate(isNew ? INVITED_MEMBERS_HTML_NEW_EVENT_MESSAGE_TEMPLATE : INVITED_MEMBERS_HTML_UPDATE_EVENT_MESSAGE_TEMPLATE, eventTemplateModel);
				//TODO internationalize
				String subject;
				if (nodeService.getType(event).isMatch(EventsService.TYPE_EVENT)) {
					subject = isNew ? "Приглашение на мероприятие " : "Обновление мероприятия ";
				} else {
					subject = isNew ? "Приглашение на совещание " : "Обновление совещания ";
				}
				subject += eventTemplateModel.get("title");

				Calendar calendar = null;
				if (sendIcalToInvitedMembers) {
					VEvent vEvent = formInviteEvent(eventTemplateModel, formBasicEvent(eventTemplateModel));
					calendar = envelopEvent(vEvent, Method.REQUEST);
				}
				sendMail(email, subject, plainText, htmlText, attachments, calendar);
			}
		}

	}

	public void notifyEventUpdated(NodeRef event, List<NodeRef> recipients) {
		notifyEvent(event, false, recipients);
	}

	public void notifyAttendeeRemoved(NodeRef event, NodeRef attendee) {
		if (null == attendee || null == event) {
			return;
		}
		nodeService.setProperty(event, EventsService.PROP_EVENT_ICAL_NEXT_SEQUENCE, (Integer) nodeService.getProperty(event, EventsService.PROP_EVENT_ICAL_NEXT_SEQUENCE) + 1);
		QName attendeeType = nodeService.getType(attendee);
		List<NodeRef> sendTo = new ArrayList();
		sendTo.add(attendee);
		String attendeeMail;
		Map<String, Object> eventTemplateModel = new HashMap<>(getEventTemplateModel(event));
		List<DataSource> attachments = new ArrayList<>(getEventAttachments(event));
		if (OrgstructureBean.TYPE_EMPLOYEE.isMatch(attendeeType)) {
			//пошлём стандартное уведомление
			//Рссылаем стандартные уведомления
			String author = AuthenticationUtil.getSystemUserName();
			//отсылка через стандартные уведомления
			String text = templateService.processTemplate(MEMBERS_PLAIN_TEXT_CANCEL_EVENT_MESSAGE_TEMPLATE, eventTemplateModel);
			notificationsService.sendNotification(author, event, text, sendTo, null);
			if (sendIcalToMembers) {
				attendeeMail = nodeService.getProperty(attendee, OrgstructureBean.PROP_EMPLOYEE_EMAIL).toString();
				eventTemplateModel.put("recipientMail", attendeeMail);
				String plainText = templateService.processTemplate(MEMBERS_PLAIN_TEXT_CANCEL_EVENT_MESSAGE_TEMPLATE, eventTemplateModel);
				String htmlText = templateService.processTemplate(MEMBERS_HTML_CANCEL_EVENT_TEMPLATE, eventTemplateModel);
				//TODO internationalize
				String subject;
				if (nodeService.getType(event).isMatch(EventsService.TYPE_EVENT)) {
					subject = "Мероприятие ";
				} else {
					subject = "Совещание ";
				}
				subject += eventTemplateModel.get("title")
						+ " отменено.";
				VEvent vEvent = formRemoveAttendeeEvent(eventTemplateModel, formBasicEvent(eventTemplateModel), attendeeMail);
				Calendar calendar = envelopEvent(vEvent, Method.CANCEL);
				sendMail(attendeeMail, subject, plainText, htmlText, attachments, calendar);
			}

		} else if (Contractors.TYPE_REPRESENTATIVE.isMatch(attendeeType)) {
			attendeeMail = nodeService.getProperty(attendee, Contractors.PROP_REPRESENTATIVE_EMAIL).toString();
			eventTemplateModel.put("recipientMail", attendeeMail);
			String plainText = templateService.processTemplate(INVITED_MEMBERS_PLAIN_TEXT_CANCEL_EVENT_MESSAGE_TEMPLATE, eventTemplateModel);
			String htmlText = templateService.processTemplate(INVITED_MEMBERS_HTML_CANCEL_EVENT_TEMPLATE, eventTemplateModel);
			//TODO internationalize
			String subject;
			if (nodeService.getType(event).isMatch(EventsService.TYPE_EVENT)) {
				subject = "Мероприятие ";
			} else {
				subject = "Совещание ";
			}
			subject += eventTemplateModel.get("title")
					+ " отменено.";
			Calendar calendar = null;
			if (sendIcalToInvitedMembers) {
				VEvent vEvent = formRemoveAttendeeEvent(eventTemplateModel, formBasicEvent(eventTemplateModel), attendeeMail);
				calendar = envelopEvent(vEvent, Method.CANCEL);
			}
			sendMail(attendeeMail, subject, plainText, htmlText, attachments, calendar);
		}
	}

	private Map<String, Object> getEventTemplateModel(NodeRef event) {
		SysAdminParams params = serviceRegistry.getSysAdminParams();
		String host = params.getShareHost();
		Map<String, Object> mailTemplateModel = new HashMap<>();
		Map<QName, Serializable> properties = nodeService.getProperties(event);

		mailTemplateModel.put("uid", event.getId() + "@" + host);
		mailTemplateModel.put("title", properties.get(EventsService.PROP_EVENT_TITLE));
		Integer sequence = (Integer) properties.get(EventsService.PROP_EVENT_ICAL_NEXT_SEQUENCE);
		sequence = null == sequence ? 0 : sequence - 1;
		mailTemplateModel.put("sequence", sequence);
		mailTemplateModel.put("link", wrapAsEventLink(event));
		mailTemplateModel.put("type", nodeService.getType(event).toPrefixString(namespaceService));
		mailTemplateModel.put("description", properties.get(EventsService.PROP_EVENT_DESCRIPTION));
		NodeRef initiator = eventsService.getEventInitiator(event);
		mailTemplateModel.put("initiator", nodeService.getProperty(initiator, OrgstructureBean.PROP_EMPLOYEE_SHORT_NAME));
		mailTemplateModel.put("initiatorMail", nodeService.getProperty(initiator, OrgstructureBean.PROP_EMPLOYEE_EMAIL));
		NodeRef organization = orgstructureBean.getEmployeeOrganization(initiator);
		if (organization != null) {
			mailTemplateModel.put("organization", nodeService.getProperty(organization, Contractors.PROP_CONTRACTOR_FULLNAME));
		}

		java.util.Date fromDate = (java.util.Date) properties.get(EventsService.PROP_EVENT_FROM_DATE);
		java.util.Date toDate = (java.util.Date) properties.get(EventsService.PROP_EVENT_TO_DATE);
		Boolean allDay = (Boolean) properties.get(EventsService.PROP_EVENT_ALL_DAY);
		mailTemplateModel.put("fromDate", fromDate);
		mailTemplateModel.put("toDate", toDate);
		mailTemplateModel.put("allDay", null == allDay ? false : allDay);
		if (fromDate != null && toDate != null) {
			String fromDateString = dateFormat.format(fromDate);
			String toDateString = dateFormat.format(toDate);
			if (fromDateString.equals(toDateString)) {
				mailTemplateModel.put("date", fromDateString);
			} else {
				mailTemplateModel.put("date", " с " + fromDateString + " по " + toDateString);
			}
		}
		NodeRef location = eventsService.getEventLocation(event);
		if (location != null) {
			mailTemplateModel.put("location", nodeService.getProperty(location, EventsService.PROP_EVENT_LOCATION_ADDRESS));
		}

		List<NodeRef> invitedMembersRefs = eventsService.getEventInvitedMembers(event);
		List<NodeRef> membersRefs = eventsService.getEventMembers(event);
		Map<String, Map<String, Serializable>> attendees = new HashMap<>();
		for (NodeRef attendee : membersRefs) {
			Map<String, Serializable> attendeeMap = new HashMap<>();
			String email = (String) nodeService.getProperty(attendee, OrgstructureBean.PROP_EMPLOYEE_EMAIL);
			String surname = (String) nodeService.getProperty(attendee, OrgstructureBean.PROP_EMPLOYEE_LAST_NAME);
			String firstname = (String) nodeService.getProperty(attendee, OrgstructureBean.PROP_EMPLOYEE_FIRST_NAME);
			String name = (surname == null ? "" : surname) + " " + ((firstname == null) ? "" : firstname);
			NodeRef attendeeRow = eventsService.getMemberTableRow(event, attendee);
			Boolean mandatory;
			String decision;
			if (null == attendeeRow) {
				mandatory = getMemberMandatory(attendee, properties.get(EventsService.PROP_EVENT_MEMBERS_MANDATORY_JSON).toString());
				decision = EventsService.CONSTRAINT_EVENT_MEMBERS_STATUS_EMPTY;
			} else {
				mandatory = (Boolean) nodeService.getProperty(attendeeRow, EventsService.PROP_EVENT_MEMBERS_PARTICIPATION_REQUIRED);
				decision = eventsService.getEmployeeMemberStatus(event, attendee);
				decision = null == decision ? EventsService.CONSTRAINT_EVENT_MEMBERS_STATUS_EMPTY : decision;
			}
			attendeeMap.put("mandatory", mandatory);
			attendeeMap.put("decision", decision);
			attendeeMap.put("name", name);
			if (email != null && !email.isEmpty()) {
				attendees.put(email, attendeeMap);
			}
		}
		//приглашённые
		for (NodeRef attendee : invitedMembersRefs) {
			Map<String, Serializable> attendeeMap = new HashMap<>();
			String email = (String) nodeService.getProperty(attendee, Contractors.PROP_REPRESENTATIVE_EMAIL);
			String surname = (String) nodeService.getProperty(attendee, Contractors.PROP_REPRESENTATIVE_SURNAME);
			String firstname = (String) nodeService.getProperty(attendee, Contractors.PROP_REPRESENTATIVE_FIRSTNAME);
			String name = (surname == null ? "" : surname) + " " + ((firstname == null) ? "" : firstname);
			NodeRef attendeeRow = eventsService.getMemberTableRow(event, attendee);
			Boolean mandatory = false;
			String decision = EventsService.CONSTRAINT_EVENT_MEMBERS_STATUS_EMPTY;
			attendeeMap.put("mandatory", mandatory);
			attendeeMap.put("decision", decision);
			attendeeMap.put("name", name);
			if (email != null && !email.isEmpty()) {
				attendees.put(email, attendeeMap);
			}
		}
		mailTemplateModel.put("attendees", attendees);
		return Collections.unmodifiableMap(mailTemplateModel);
	}

	public String wrapAsEventLink(NodeRef documentRef) {
		return wrapperLink(documentRef, (String) nodeService.getProperty(documentRef, DocumentService.PROP_EXT_PRESENT_STRING), EventsService.EVENT_LINK_URL);
	}

	private VEvent formCancelEvent(Map<String, Object> mailTemplateModel, VEvent basicEvent) {
		VEvent vEvent = basicEvent;
		PropertyList vEventProperties = vEvent.getProperties();
		vEventProperties.add(new Sequence(mailTemplateModel.get("sequence").toString()));
		vEventProperties.add(Status.VEVENT_CANCELLED);
		Map<String, Map<String, Serializable>> attendees = (Map) mailTemplateModel.get("attendees");
		for (String personMail : attendees.keySet()) {
			Map person = attendees.get(personMail);
			String personName = person.get("name").toString();
			Boolean mandatory = (Boolean) person.get("mandatory");
			String decision = person.get("decision").toString();
			personName = null == personName ? "" : personName;
			Attendee attendee = new Attendee(URI.create("mailto:" + personMail));
			attendee.getParameters().add(new Cn(personName));
			attendee.getParameters().add(CuType.INDIVIDUAL);
			attendee.getParameters().add(Rsvp.FALSE);
			vEventProperties.add(attendee);
		}
		return vEvent;
	}

	private VEvent formRemoveAttendeeEvent(Map<String, Object> mailTemplateModel, VEvent basicEvent, String removedAttendeeMail) {
		VEvent vEvent = basicEvent;
		PropertyList vEventProperties = vEvent.getProperties();
		vEventProperties.add(new Sequence(mailTemplateModel.get("sequence").toString()));

		Map person = (Map) ((Map) mailTemplateModel.get("attendees")).get(removedAttendeeMail);
		if (person != null) {
			String personName = person.get("name").toString();
			personName = null == personName ? "" : personName;
			Attendee attendee = new Attendee(URI.create("mailto:" + removedAttendeeMail));
			attendee.getParameters().add(new Cn(personName));
			attendee.getParameters().add(CuType.INDIVIDUAL);
			attendee.getParameters().add(Rsvp.FALSE);
			vEventProperties.add(attendee);
		}
		return vEvent;
	}

	private VEvent formInviteEvent(Map<String, Object> mailTemplateModel, VEvent basicEvent) {
		VEvent vEvent = basicEvent;
		PropertyList vEventProperties = vEvent.getProperties();
		vEventProperties.add(Status.VEVENT_CONFIRMED);

		Map<String, Map<String, Serializable>> attendees = (Map) mailTemplateModel.get("attendees");
		for (String personMail : attendees.keySet()) {
			Map person = attendees.get(personMail);
			String personName = person.get("name").toString();
			Boolean mandatory = (Boolean) person.get("mandatory");
			String decision = person.get("decision").toString();
			personName = null == personName ? "" : personName;
			Attendee attendee = new Attendee(URI.create("mailto:" + personMail));
			attendee.getParameters().add(new Cn(personName));
			attendee.getParameters().add(CuType.INDIVIDUAL);
			if (personMail.equals(mailTemplateModel.get("initiatorMail"))) {
				attendee.getParameters().add(Role.CHAIR);
				attendee.getParameters().add(PartStat.ACCEPTED);
				attendee.getParameters().add(Rsvp.FALSE);
			} else {
				if (mandatory) {
					attendee.getParameters().add(Role.REQ_PARTICIPANT);
				} else {
					attendee.getParameters().add(Role.OPT_PARTICIPANT);
				}
				if (EventsService.CONSTRAINT_EVENT_MEMBERS_STATUS_CONFIRMED.equals(decision)) {
					attendee.getParameters().add(PartStat.ACCEPTED);
					attendee.getParameters().add(Rsvp.FALSE);
				} else if (EventsService.CONSTRAINT_EVENT_MEMBERS_STATUS_DECLINED.equals(decision)) {
					attendee.getParameters().add(PartStat.DECLINED);
					attendee.getParameters().add(Rsvp.FALSE);
				} else {
					attendee.getParameters().add(PartStat.NEEDS_ACTION);
					attendee.getParameters().add(Rsvp.TRUE);
				}
			}
			vEventProperties.add(attendee);
		}

		return vEvent;
	}

	private VEvent formBasicEvent(Map<String, Object> mailTemplateModel) {

		VEvent vEvent = new VEvent();
		TimeZoneRegistry registry = TimeZoneRegistryFactory.getInstance().createRegistry();
		TimeZone timezone = registry.getTimeZone(java.util.Calendar.getInstance().getTimeZone().getID());
		VTimeZone tz = timezone.getVTimeZone();
		PropertyList vEventProperties = vEvent.getProperties();
		vEventProperties.add(tz.getTimeZoneId());

		vEventProperties.add(new Organizer(URI.create("mailto:" + mailTemplateModel.get("initiatorMail"))));
		Integer sequence = (Integer) mailTemplateModel.get("sequence");
		vEventProperties.add(new Sequence(sequence));
		vEventProperties.add(new Uid(mailTemplateModel.get("uid").toString()));

		if ((Boolean) mailTemplateModel.get("allDay")) {
			net.fortuna.ical4j.model.Date dtStart = new net.fortuna.ical4j.model.Date(((Date) mailTemplateModel.get("fromDate")).getTime());
			vEventProperties.add(new DtStart(dtStart));
			net.fortuna.ical4j.model.Date dtEnd = new net.fortuna.ical4j.model.Date(((Date) mailTemplateModel.get("toDate")).getTime());
			vEventProperties.add(new DtEnd(dtEnd));
		} else {
			DateTime dtStart = new DateTime(((Date) mailTemplateModel.get("fromDate")).getTime());
			vEventProperties.add(new DtStart(dtStart));
			DateTime dtEnd = new DateTime(((Date) mailTemplateModel.get("toDate")).getTime());
			vEventProperties.add(new DtEnd(dtEnd));
		}

		vEventProperties.add(new Summary(mailTemplateModel.get("title").toString()));
		vEventProperties.add(new Location(mailTemplateModel.get("location").toString()));

		return vEvent;
	}

	private Calendar envelopEvent(VEvent vEvent, Method method) {
		Calendar calendar = new Calendar();
		calendar.getProperties().add(new ProdId(prodId));
		calendar.getProperties().add(Version.VERSION_2_0);
		//TODO I hope we will use only gregorian but remember this place
		calendar.getProperties().add(CalScale.GREGORIAN);
		calendar.getProperties().add(method);
		calendar.getComponents().add(vEvent);
		return calendar;
	}

	//TODO это копипаст из EventsPolicy. было бы не плохо их вынести в один метод.
	private boolean getMemberMandatory(NodeRef member, String membersMandatory) {
		try {
			JSONArray membersMandatoryJson = new JSONArray(membersMandatory);
			for (int i = 0; i < membersMandatoryJson.length(); i++) {
				JSONObject obj = membersMandatoryJson.getJSONObject(i);
				if (obj != null && member.toString().equals(obj.get("nodeRef"))) {
					return obj.getBoolean("mandatory");
				}
			}
		} catch (JSONException e) {
			logger.error("Error parse members mandatory json", e);
		}
		return false;
	}

	private List<DataSource> getEventAttachments(NodeRef event) {
		List<DataSource> attachmentDS = new ArrayList<>();
		List<NodeRef> attachments = new ArrayList<>();
		List<AssociationRef> attachmentsAssocs = nodeService.getSourceAssocs(event, DocumentService.ASSOC_PARENT_DOCUMENT);
		if (attachmentsAssocs != null) {
			for (AssociationRef attachment : attachmentsAssocs) {
				attachments.add(attachment.getSourceRef());
			}
		}
		for (final NodeRef attachment : attachments) {
			attachmentDS.add(new DataSource() {

				@Override
				public InputStream getInputStream() throws IOException {
					ContentReader reader = contentService.getReader(attachment, ContentModel.PROP_CONTENT);
					return reader.getContentInputStream();
				}

				@Override
				public OutputStream getOutputStream() throws IOException {
					throw new IOException("Read-only data");
				}

				@Override
				public String getContentType() {
					return contentService.getReader(attachment, ContentModel.PROP_CONTENT).getMimetype();
				}

				@Override
				public String getName() {
					return nodeService.getProperty(attachment, ContentModel.PROP_NAME).toString();
				}
			});

		}
		return Collections.unmodifiableList(attachmentDS);
	}

	private void sendMail(String sendTo, String subject, String plainText, String htmlText, List<DataSource> attachments) {
		sendMail(sendTo, subject, plainText, htmlText, attachments, null);
	}

	private void sendMail(String sendTo, String subject, final String plainText, final String htmlText, List<DataSource> attachments, Calendar calendar) {
		if (null != sendTo && !sendTo.isEmpty()) {
			try {
				if (null == plainText && null == htmlText) {
					throw new MailException("Mail text must be not null") {
					};
				}
				String contentId = "htmlText";
				MimeMessage message = mailService.createMimeMessage();
				MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
				helper.setTo(sendTo);
				helper.setFrom(defaultFromEmail);
				helper.setSubject(subject);
				//Создаём alternative
				MimeMultipart root = helper.getMimeMultipart();
				BodyPart messageBodyPart = new MimeBodyPart();
				root.addBodyPart(messageBodyPart);
				MimeMultipart messageBody = new MimeMultipart(MULTIPART_SUBTYPE_ALTERNATIVE);
				messageBodyPart.setContent(messageBody, CONTENT_TYPE_ALTERNATIVE);

				if (null != plainText) {
					//Устанавливаем plain
					// Create the plain text part of the message.
					//TODO сформировать сообщение в плйн текст
					MimeBodyPart plainTextPart = new MimeBodyPart();
					plainTextPart.setText(plainText);
					messageBody.addBodyPart(plainTextPart);
				}
				if (null != htmlText) {
					//Устанавливаем html
					// Create the HTML text part of the message.
					MimeBodyPart htmlTextPart = new MimeBodyPart();
					htmlTextPart.setText(htmlText, null, CONTENT_SUBTYPE_HTML);
					//htmlTextPart.setContentID(contentId);
					messageBody.addBodyPart(htmlTextPart);

				}
				if (null != calendar) {
					//Создаём описание с плейнтекст
					Description description = new Description();
					if (null != plainText) {
						description.setValue(plainText);
					}
					//добавляем хтмл
					if (null != htmlText) {
						description.getParameters().add(new AltRep("CID:" + contentId));
					}
					calendar.getComponent(VEvent.VEVENT).getProperties().add(description);
					//Устанавливаем calendar
					// Create the calendar part
					BodyPart calendarBodyPart = new MimeBodyPart();
					// Fill the message
					//calendarBodyPart.setHeader("Content-Class", "urn:content-classes:calendarmessage");
					//calendarBodyPart.setHeader("Content-ID", "calendar_message");
					String method = calendar.getMethod().getValue();
					calendarBodyPart.setDataHandler(new DataHandler(
							new ByteArrayDataSource(calendar.toString(), "text/calendar; charset=UTF-8; method=" + method)));
					messageBody.addBodyPart(calendarBodyPart);

					//htmlpart на который ссылается description
					if (null != htmlText) {
						helper.addInline(contentId, new ByteArrayDataSource(htmlText, "text/html"));
					}

					String fileName = "invite.ics";
					if (Method.CANCEL.equals(calendar.getMethod())) {
						fileName = "cancel.ics";
					}
					DataSource ds = new ByteArrayDataSource(calendar.toString(), "application/ics");
					helper.addAttachment(MimeUtility.encodeText(fileName), ds);
				}
				if (null != attachments) {
					for (DataSource attachment : attachments) {
						helper.addAttachment(MimeUtility.encodeText(attachment.getName(), "UTF-8", null), attachment);
					}
				}
				Runnable mailSender = new RawMailSender(message, mailService, transactionService);
				threadPoolExecutor.execute(mailSender);

			} catch (Exception e) {
				logger.error("Send mail to " + sendTo + " failed", e);
			}
		}
	}

}
