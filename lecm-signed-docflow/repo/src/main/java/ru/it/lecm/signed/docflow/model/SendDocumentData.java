package ru.it.lecm.signed.docflow.model;

import java.util.Map;
import ucloud.gate.proxy.exceptions.GateResponse;

/**
 *
 * @author VLadimir Malygin
 * @since 12.08.2013 19:41:46
 * @see <p>mailto: <a href="mailto:vmalygin@it.ru">vmalygin@it.ru</a></p>
 */
public final class SendDocumentData extends UnicloudData {

	private String documentId;
	private String docflowId;

	public SendDocumentData() {
		super();
	}

	public SendDocumentData(final GateResponse gateResponse) {
		super(gateResponse);
	}

	@Override
	protected Map<String, Object> putOwnProperties(Map<String, Object> properties) {
		properties.put("documentId", documentId);
		properties.put("docflowId", documentId);
		return properties;
	}

	public String getDocumentId() {
		return documentId;
	}

	public void setDocumentId(String documentId) {
		this.documentId = documentId;
	}

	public String getDocflowId() {
		return docflowId;
	}

	public void setDocflowId(String docflowId) {
		this.docflowId = docflowId;
	}
}
