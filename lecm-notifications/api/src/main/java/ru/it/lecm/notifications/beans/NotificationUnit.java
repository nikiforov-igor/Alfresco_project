package ru.it.lecm.notifications.beans;

import org.alfresco.service.cmr.repository.NodeRef;

import java.util.Date;

/**
 * User: AIvkin
 * Date: 16.01.13
 * Time: 16:28
 *
 * Атомарное уведомление. Используется как контейнер для свойст атомарного уведомления, при отправке.
 */
public class NotificationUnit {
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
	 * Дата формирования уведомления.
	 * Если ничего не передано, будет подставлена текущая дата.
	 */
	private Date formingDate;
	/**
	 * Список на тип доставки уведомления.
	 * Берётся из подписки.
	 */
	private NodeRef typeRef;
	/**
	 * Список на получателя (сотрудник).
	 * Берётся из подписки.
	 */
	private NodeRef recipientRef;
	/**
	 * Объект уведомления
	 */
	private NodeRef objectRef;

	/**
	 * Тело почтового уведомления
	 */
	private String body;

	/**
	 * Тема почтового уведомления
	 */
	private String subject;

	/**
	 * Код шаблона уведомления
	 */
	private String template;

	public NotificationUnit() {
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

	public NodeRef getRecipientRef() {
		return recipientRef;
	}

	public void setRecipientRef(NodeRef recipientRef) {
		this.recipientRef = recipientRef;
	}

	public NodeRef getTypeRef() {
		return typeRef;
	}

	public void setTypeRef(NodeRef typeRef) {
		this.typeRef = typeRef;
	}

	public NodeRef getObjectRef() {
		return objectRef;
	}

	public void setObjectRef(NodeRef objectRef) {
		this.objectRef = objectRef;
	}

	public String getBody() {
		return body;
	}

	public String getSubject() {
		return subject;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		NotificationUnit that = (NotificationUnit) o;

		if (autor != null ? !autor.equals(that.autor) : that.autor != null) return false;
		if (description != null ? !description.equals(that.description) : that.description != null) return false;
		if (formingDate != null ? !formingDate.equals(that.formingDate) : that.formingDate != null) return false;
		if (recipientRef != null ? !recipientRef.equals(that.recipientRef) : that.recipientRef != null) return false;
		if (typeRef != null ? !typeRef.equals(that.typeRef) : that.typeRef != null) return false;
		if (template != null ? !template.equals(that.template) : that.template != null) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = autor != null ? autor.hashCode() : 0;
		result = 31 * result + (description != null ? description.hashCode() : 0);
		result = 31 * result + (formingDate != null ? formingDate.hashCode() : 0);
		result = 31 * result + (typeRef != null ? typeRef.hashCode() : 0);
		result = 31 * result + (recipientRef != null ? recipientRef.hashCode() : 0);
		result = 31 * result + (template != null ? template.hashCode() : 0);
		return result;
	}
}
