package ru.it.lecm.signed.docflow.model;

import java.util.List;
import java.util.Map;
import ucloud.gate.proxy.exceptions.GateResponse;

/**
 *
 * @author VLadimir Malygin
 * @since 28.08.2013 17:53:46
 * @see <p>mailto: <a href="mailto:vmalygin@it.ru">vmalygin@it.ru</a></p>
 */
public final class ReceiveDocumentData extends UnicloudData {

	private boolean isRead;

	private List<String> signatures;

	public ReceiveDocumentData() {
		super();
	}

	public ReceiveDocumentData(GateResponse gateResponse) {
		super(gateResponse);
	}

	public boolean isIsRead() {
		return isRead;
	}

	public void setIsRead(boolean isRead) {
		this.isRead = isRead;
	}

	public List<String> getSignatures() {
		return signatures;
	}

	public void setSignatures(List<String> signatures) {
		this.signatures = signatures;
	}

	@Override
	protected Map<String, Object> putOwnProperties(Map<String, Object> properties) {
		properties.put("isRead", isRead);
		properties.put("signatures", signatures);
		return properties;
	}
}
