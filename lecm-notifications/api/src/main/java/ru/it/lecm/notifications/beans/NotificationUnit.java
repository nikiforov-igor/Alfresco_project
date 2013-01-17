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
	 * Список на тип доставки уведомления.
	 * Берётся из подписки.
	 */
	private NodeRef typeRef;
	/**
	 * Список на получателя (сотрудник).
	 * Берётся из подписки.
	 */
	private NodeRef recipientRef;

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

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		NotificationUnit that = (NotificationUnit) o;

		if (autor != null ? !autor.equals(that.autor) : that.autor != null) return false;
		if (description != null ? !description.equals(that.description) : that.description != null) return false;
		if (recipientRef != null ? !recipientRef.equals(that.recipientRef) : that.recipientRef != null) return false;
		if (typeRef != null ? !typeRef.equals(that.typeRef) : that.typeRef != null) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = autor != null ? autor.hashCode() : 0;
		result = 31 * result + (description != null ? description.hashCode() : 0);
		result = 31 * result + (typeRef != null ? typeRef.hashCode() : 0);
		result = 31 * result + (recipientRef != null ? recipientRef.hashCode() : 0);
		return result;
	}
}
