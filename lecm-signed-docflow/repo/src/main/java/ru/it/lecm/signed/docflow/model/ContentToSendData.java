package ru.it.lecm.signed.docflow.model;

import java.util.List;
import org.alfresco.service.cmr.repository.NodeRef;

/**
 * Набор данных предназначенный для отпраки контрагенту, указанным способом
 * @author VLadimir Malygin
 * @since 12.08.2013 16:09:53
 * @see <p>mailto: <a href="mailto:vmalygin@it.ru">vmalygin@it.ru</a></p>
 */
public class ContentToSendData {

	/**
	 * список NodeRef-ов вложений, которые мы будем отправлять
	 */
	private List<NodeRef> content;

	/**
	 * NodeRef-а контрагента, которому мы отправляем документы
	 */
	private NodeRef partner;

	/**
	 * email контрагента, которому мы отправляем документы
	 */
	private String email;

	/**
	 * способ взаимодействия с контрагентом
	 * SPECOP - через спецоператора
	 * EMAIL - посредством электронной почты
	 */
	private String interactionType;

	public List<NodeRef> getContent() {
		return content;
	}

	public void setContent(List<NodeRef> content) {
		this.content = content;
	}

	public NodeRef getPartner() {
		return partner;
	}

	public void setPartner(NodeRef partner) {
		this.partner = partner;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getInteractionType() {
		return interactionType;
	}

	public void setInteractionType(String interactionType) {
		this.interactionType = interactionType;
	}
}
