package ru.it.lecm.notifications.beans;

import org.alfresco.service.cmr.repository.NodeRef;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * User: AIvkin
 * Date: 16.01.13
 * Time: 10:42
 *
 * Обобщённое уведомление. Используется как контейнер для свойст обобщённого уведомления, при отправке.
 */
public class Notification {
	/**
	 * Инициатор уведомления.
	 * Берётся из бизнес журнала.
	 */
	private String author;
	/**
	 * Текст уведомления.
	 * Берётся из бизнес журнала.
	 */
	private String description;
	/**
	 * Ссылка на основной объект уведомления.
	 * Берётся из бизнес журнала.
	 */
	private NodeRef objectRef;
	/**
	 * Дата формирования уведомления.
	 * Если ничего не передано, будет подставлена текущая дата.
	 */
	private Date formingDate;
	/**
	 * Список ссылок на тип доставки уведомлений.
	 * Если null, то будут взяты настройки по-умолчанию для конкретного пользователя
	 *
	 * Берётся из подписки.
	 */
	private List<NodeRef> typeRefs;
	/**
	 * Список ссылок на получателей (пользователи).
	 * Берётся из подписки.
	 */
	private List<NodeRef> recipientEmployeeRefs;
	/**
	 * Список ссылок на получателей (должности).
	 * Берётся из подписки.
	 */
	private List<NodeRef> recipientPositionRefs;
	/**
	 * Список ссылок на получателей (подразделения).
	 * Берётся из подписки.
	 */
	private List<NodeRef> recipientOrganizationUnitRefs;
	/**
	 * Список ссылок на получателей (рабочие группы).
	 * Берётся из подписки.
	 */
	private List<NodeRef> recipientWorkGroupRefs;
	/**
	 * Список ссылок на получателей (бизнес-роли).
	 * Берётся из подписки.
	 */
	private List<NodeRef> recipientBusinessRoleRefs;

	private NodeRef initiatorRef;

	/**
	 * Проверять ли доступ до основного объекта. Задаётся автоматически.
	 */
	private boolean dontCheckAccessToObject = false;

	private List<NodeRef> delegateBusinessRoleRefs;

	/**
	 * включать в список рассылки секретарей получателей
	 */
	private boolean includeSeretaries = true;

	/**
	 * SPEL шаблон текста уведомления
	 */
	private String template;

	/**
	 * Шаблон темы письма для почтового уведомления
	 */
	private String subject;

	/**
	 * Ссылка на freemarker шаблон текста уведомления
	 */
	private NodeRef templateRef;

	/**
	 * Код шаблона из справочника
	 */
	private String templateCode;

	/**
	 * Данные для построения текста уведомления по его шаблону
	 */
	private Map<String, Object> templateModel;

	public Notification() {
	}

	public Notification(Map<String, Object> templateModel) {
		this.templateModel = templateModel;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public NodeRef getObjectRef() {
		return objectRef;
	}

	public void setObjectRef(NodeRef objectRef) {
		this.objectRef = objectRef;
	}

	public List<NodeRef> getTypeRefs() {
		return typeRefs;
	}

	public void setTypeRefs(List<NodeRef> typeRefs) {
		this.typeRefs = typeRefs;
	}

	public Date getFormingDate() {
		if (formingDate != null) {
			return formingDate;
		} else {
			return new Date();
		}
	}

	public void setFormingDate(Date formingDate) {
		this.formingDate = formingDate;
	}

	public List<NodeRef> getRecipientEmployeeRefs() {
		return recipientEmployeeRefs;
	}

	public void setRecipientEmployeeRefs(List<NodeRef> recipientEmployeeRefs) {
		this.recipientEmployeeRefs = recipientEmployeeRefs;
	}

	public List<NodeRef> getRecipientPositionRefs() {
		return recipientPositionRefs;
	}

	public void setRecipientPositionRefs(List<NodeRef> recipientPositionRefs) {
		this.recipientPositionRefs = recipientPositionRefs;
	}

	public List<NodeRef> getRecipientOrganizationUnitRefs() {
		return recipientOrganizationUnitRefs;
	}

	public void setRecipientOrganizationUnitRefs(List<NodeRef> recipientOrganizationUnitRefs) {
		this.recipientOrganizationUnitRefs = recipientOrganizationUnitRefs;
	}

	public List<NodeRef> getRecipientWorkGroupRefs() {
		return recipientWorkGroupRefs;
	}

	public void setRecipientWorkGroupRefs(List<NodeRef> recipientWorkGroupRefs) {
		this.recipientWorkGroupRefs = recipientWorkGroupRefs;
	}

	public List<NodeRef> getRecipientBusinessRoleRefs() {
		return recipientBusinessRoleRefs;
	}

	public void setRecipientBusinessRoleRefs(List<NodeRef> recipientBusinessRoleRefs) {
		this.recipientBusinessRoleRefs = recipientBusinessRoleRefs;
	}

	public void setInitiatorRef(NodeRef initiatorRef) {
		this.initiatorRef = initiatorRef;
	}

	public NodeRef getInitiatorRef() {
		return initiatorRef;
	}

	public boolean isDontCheckAccessToObject() {
		return dontCheckAccessToObject;
	}

	public void setDontCheckAccessToObject(boolean dontCheckAccessToObject) {
		this.dontCheckAccessToObject = dontCheckAccessToObject;
	}

	public List<NodeRef> getDelegateBusinessRoleRefs() {
		return delegateBusinessRoleRefs;
	}

	public void setDelegateBusinessRoleRefs(List<NodeRef> delegateBusinessRoleRefs) {
		this.delegateBusinessRoleRefs = delegateBusinessRoleRefs;
	}

	public boolean isIncludeSeretaries() {
		return includeSeretaries;
	}

	public void setIncludeSeretaries(boolean includeSeretaries) {
		this.includeSeretaries = includeSeretaries;
	}

	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public NodeRef getTemplateRef() {
		return templateRef;
	}

	public void setTemplateRef(NodeRef templateRef) {
		this.templateRef = templateRef;
	}

	public Map<String, Object> getTemplateModel() {
		return templateModel;
	}

	public String getTemplateCode() {
		return templateCode;
	}

	public void setTemplateCode(String templateCode) {
		this.templateCode = templateCode;
	}

	public void setTemplateModel(Map<String, Object> templateModel) {
		this.templateModel = templateModel;
	}
}
