package ru.it.lecm.signed.docflow.model;

import java.util.Map;

/**
 *
 * @author VLadimir Malygin
 * @since 08.08.2013 15:43:23
 * @see <p>mailto: <a href="mailto:vmalygin@it.ru">vmalygin@it.ru</a></p>
 */
public final class SignatureData extends UnicloudData {

	private String signerInfo;
	private boolean isSignatureValid;

	@Override
	protected Map<String, Object> putOwnProperties(Map<String, Object> properties) {
		properties.put("signerInfo", signerInfo);
		properties.put("isSignatureValid", isSignatureValid);
		return properties;
	}

	public String getSignerInfo() {
		return signerInfo;
	}

	public void setSignerInfo(String signerInfo) {
		this.signerInfo = signerInfo;
	}

	public boolean isIsSignatureValid() {
		return isSignatureValid;
	}

	public void setIsSignatureValid(boolean isSignatureValid) {
		this.isSignatureValid = isSignatureValid;
	}
}
