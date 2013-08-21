package ru.it.lecm.signed.docflow.model;

import java.util.Map;

/**
 *
 * @author VLadimir Malygin
 * @since 12.08.2013 19:41:46
 * @see <p>mailto: <a href="mailto:vmalygin@it.ru">vmalygin@it.ru</a></p>
 */
public final class SendDocumentData extends UnicloudData {

	private String documentId;

	@Override
	protected Map<String, Object> putOwnProperties(Map<String, Object> properties) {
		return properties;
	}
	
	public String getDocumentId() {
		return documentId;
	}

	public void setDocumentId(String documentId) {
		this.documentId = documentId;
	}
}
