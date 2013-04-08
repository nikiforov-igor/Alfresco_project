package ru.it.lecm.notifications.beans;

import org.alfresco.service.cmr.repository.NodeRef;

import java.util.Date;
import java.util.List;

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
	private String autor;
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
    private NodeRef initiatorRef;

    public Notification() {
	}

	public String getAutor() {
		return autor;
	}

	public void setAutor(String autor) {
		this.autor = autor;
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

    public void setInitiatorRef(NodeRef initiatorRef) {
        this.initiatorRef = initiatorRef;
    }

    public NodeRef getInitiatorRef() {
        return initiatorRef;
    }
}
