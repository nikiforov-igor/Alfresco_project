package ru.it.lecm.signed.docflow.api;

import org.alfresco.service.namespace.QName;

/**
 *
 * @author VLadimir Malygin
 * @since 12.08.2013 10:26:28
 * @see <p>mailto: <a href="mailto:vmalygin@it.ru">vmalygin@it.ru</a></p>
 */
public final class SignedDocflowModel {

	public final static String SIGNED_DOCFLOW_PREFIX = "lecm-signed-docflow";
	public final static String SIGNED_DOCFLOW_NAMESPACE = "http://www.it.ru/lecm/model/signed-docflow/1.0";
	/**
	 * &lt;type name="lecm-signed-docflow:sign/&gt;
	 */
	public final static QName TYPE_SIGN = QName.createQName(SIGNED_DOCFLOW_NAMESPACE, "sign");
	/**
	 * &lt;property name="lecm-signed-docflow:owner/&gt;
	 */
	public final static QName PROP_OWNER = QName.createQName(SIGNED_DOCFLOW_NAMESPACE, "owner");
	/**
	 * &lt;property name="lecm-signed-docflow:owner-position/&gt;
	 */
	public final static QName PROP_OWNER_POSITION = QName.createQName(SIGNED_DOCFLOW_NAMESPACE, "owner-position");
	/**
	 * &lt;property name="lecm-signed-docflow:owner-organization/&gt;
	 */
	public final static QName PROP_OWNER_ORGANIZATION = QName.createQName(SIGNED_DOCFLOW_NAMESPACE, "owner-organization");
	/**
	 * &lt;property name="lecm-signed-docflow:signing-date/&gt;
	 */
	public final static QName PROP_SIGNING_DATE = QName.createQName(SIGNED_DOCFLOW_NAMESPACE, "signing-date");
	/**
	 * &lt;property name="lecm-signed-docflow:serial-number/&gt;
	 */
	public final static QName PROP_SERIAL_NUMBER = QName.createQName(SIGNED_DOCFLOW_NAMESPACE, "serial-number");
	/**
	 * &lt;property name="lecm-signed-docflow:valid-from/&gt;
	 */
	public final static QName PROP_VALID_FROM = QName.createQName(SIGNED_DOCFLOW_NAMESPACE, "valid-from");
	/**
	 * &lt;property name="lecm-signed-docflow:valid-through/&gt;
	 */
	public final static QName PROP_VALID_THROUGH = QName.createQName(SIGNED_DOCFLOW_NAMESPACE, "valid-through");
	/**
	 * &lt;property name="lecm-signed-docflow:ca/&gt;
	 */
	public final static QName PROP_CA = QName.createQName(SIGNED_DOCFLOW_NAMESPACE, "ca");
	/**
	 * &lt;property name="lecm-signed-docflow:update-date/&gt;
	 */
	public final static QName PROP_UPDATE_DATE = QName.createQName(SIGNED_DOCFLOW_NAMESPACE, "update-date");
	/**
	 * &lt;property name="lecm-signed-docflow:is-valid/&gt;
	 */
	public final static QName PROP_IS_VALID = QName.createQName(SIGNED_DOCFLOW_NAMESPACE, "is-valid");
	/**
	 * &lt;property name="lecm-signed-docflow:is-our/&gt;
	 */
	public final static QName PROP_IS_OUR = QName.createQName(SIGNED_DOCFLOW_NAMESPACE, "is-our");
	/**
	 * &lt;property name="lecm-signed-docflow:fingerprint"/&gt;
	 */
	public final static QName PROP_CERT_FINGERPRINT = QName.createQName(SIGNED_DOCFLOW_NAMESPACE, "fingerprint");
	/**
	 * &lt;association name="lecm-signed-docflow:sign-to-content-association/&gt;
	 */
	public final static QName ASSOC_SIGN_TO_CONTENT = QName.createQName(SIGNED_DOCFLOW_NAMESPACE, "sign-to-content-association");
	/**
	 * &lt;aspect name="lecm-signed-docflow:signable"/&gt;
	 */
	public final static QName ASPECT_SIGNABLE = QName.createQName(SIGNED_DOCFLOW_NAMESPACE, "signable");
	/**
	 * &lt;aspect name="lecm-signed-docflow:docflowable"/&gt;
	 */
	public final static QName ASPECT_DOCFLOWABLE = QName.createQName(SIGNED_DOCFLOW_NAMESPACE, "docflowable");
	/**
	 * &lt;aspect name="lecm-signed-docflow:aspect-document-id/&gt;
	 */
	public final static QName ASPECT_DOCUMENT_ID = QName.createQName(SIGNED_DOCFLOW_NAMESPACE, "aspect-document-id");
	/**
	 * &lt;property name="lecm-signed-docflow:document-id/&gt;
	 */
	public final static QName PROP_DOCUMENT_ID = QName.createQName(SIGNED_DOCFLOW_NAMESPACE, "document-id");
	/**
	 * &lt;property name="lecm-signed-docflow:operator-code"&gt;
	 */
	public final static QName PROP_OPERATOR_CODE = QName.createQName(SIGNED_DOCFLOW_NAMESPACE, "operator-code");
	/**
	 * &lt;property name="lecm-signed-docflow:partner-key"&gt;
	 */
	public final static QName PROP_PARTNER_KEY = QName.createQName(SIGNED_DOCFLOW_NAMESPACE, "partner-key");
	/**
	 * &lt;property name="lecm-signed-docflow:organization-id"&gt;
	 */
	public final static QName PROP_ORGANIZATION_ID = QName.createQName(SIGNED_DOCFLOW_NAMESPACE, "organization-id");
	/**
	 * &lt;property name="lecm-signed-docflow:organization-edo-id"&gt;
	 */
	public final static QName PROP_ORGANIZATION_EDO_ID = QName.createQName(SIGNED_DOCFLOW_NAMESPACE, "organization-edo-id");
	/**
	 * &lt;aspect name="lecm-signed-docflow:personal-data-attrs-aspect"&gt;
	 */
	public final static QName ASPECT_PERSONAL_DATA_ATTRS = QName.createQName(SIGNED_DOCFLOW_NAMESPACE, "personal-data-attrs-aspect");
	/**
	 * &lt;property name="lecm-signed-docflow:auth-token"&gt;
	 */
	public final static QName PROP_AUTH_TOKEN = QName.createQName(SIGNED_DOCFLOW_NAMESPACE, "auth-token");
	/**
	 * &lt;property name="lecm-signed-docflow:cert-thumbprint"&gt;
	 */
	public final static QName PROP_CERT_THUMBPRINT = QName.createQName(SIGNED_DOCFLOW_NAMESPACE, "cert-thumbprint");
	/**
	 * &lt;property name="lecm-signed-docflow:auth-type"&gt;
	 */
	public final static QName PROP_AUTH_TYPE = QName.createQName(SIGNED_DOCFLOW_NAMESPACE, "auth-type");
	/**
	 * &lt;property name="lecm-signed-docflow:applet-key"&gt;
	 */
	public final static QName PROP_APPLET_LIC_KEY = QName.createQName(SIGNED_DOCFLOW_NAMESPACE, "applet-key");
	/**
	 * &lt;property name="lecm-signed-docflow:applet-cert"&gt;
	 */
	public final static QName PROP_APPLET_CERT = QName.createQName(SIGNED_DOCFLOW_NAMESPACE, "applet-cert");
	/**
	 * &lt;property name="lecm-signed-docflow:applet-container"&gt;
	 */
	public final static QName PROP_APPLET_CONTAINER = QName.createQName(SIGNED_DOCFLOW_NAMESPACE, "applet-container");
	/**
	 * &lt;property name="lecm-signed-docflow:content-ref"&gt;
	 */
	public final static QName PROP_CONTENT_REF = QName.createQName(SIGNED_DOCFLOW_NAMESPACE, "content-ref");
	/**
	 * &lt;property name="lecm-signed-docflow:document-ref"&gt;
	 */
	public final static QName PROP_DOCUMENT_REF = QName.createQName(SIGNED_DOCFLOW_NAMESPACE, "document-ref");
	/**
	 * &lt;aspect name="lecm-signed-docflow:contractor-interaction-aspect"&gt;
	 */
	public final static QName ASPECT_CONTRACTOR_INTERACTION = QName.createQName(SIGNED_DOCFLOW_NAMESPACE, "contractor-interaction-aspect");
	/**
	 * &lt;property name="lecm-signed-docflow:contractor-ref"&gt;
	 */
	public final static QName PROP_CONTRACTOR_REF = QName.createQName(SIGNED_DOCFLOW_NAMESPACE, "contractor-ref");
	/**
	 * &lt;property name="lecm-signed-docflow:interaction-type"&gt;
	 */
	public final static QName PROP_INTERACTION_TYPE = QName.createQName(SIGNED_DOCFLOW_NAMESPACE, "interaction-type");
	/**
	 * &lt;property name="lecm-signed-docflow:contractor-email"&gt;
	 */
	public final static QName PROP_CONTRACTOR_EMAIL = QName.createQName(SIGNED_DOCFLOW_NAMESPACE, "contractor-email");
	/**
	 * &lt;property name="lecm-signed-docflow:name"&gt;
	 */
	public final static QName PROP_NAME = QName.createQName(SIGNED_DOCFLOW_NAMESPACE, "name");

	private SignedDocflowModel() throws IllegalAccessException {
		throw new IllegalAccessException("You cannot create any instance of SignedDocflowModel class.");
	}
}
