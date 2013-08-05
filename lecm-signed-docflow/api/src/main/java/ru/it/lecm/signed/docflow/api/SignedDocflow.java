package ru.it.lecm.signed.docflow.api;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;

/**
 *
 * @author vlevin
 */
public interface SignedDocflow {

	String SIGNED_DOCFLOW_PREFIX = "lecm-signed-docflow";
	String SIGNED_DOCFLOW_NAMESPACE = "http://www.it.ru/lecm/model/signed-docflow/1.0";

    /**
     * &lt;type name="lecm-signed-docflow:sign/&gt;
     */
	QName TYPE_SIGN = QName.createQName(SIGNED_DOCFLOW_NAMESPACE, "sign");

    /**
     * &lt;property name="lecm-signed-docflow:owner/&gt;
     */
	QName PROP_OWNER = QName.createQName(SIGNED_DOCFLOW_NAMESPACE, "owner");

    /**
     * &lt;property name="lecm-signed-docflow:owner-position/&gt;
     */
	QName PROP_OWNER_POSITION = QName.createQName(SIGNED_DOCFLOW_NAMESPACE, "owner-position");

    /**
     * &lt;property name="lecm-signed-docflow:owner-organization/&gt;
     */
	QName PROP_OWNER_ORGANIZATION = QName.createQName(SIGNED_DOCFLOW_NAMESPACE, "owner-organization");

    /**
     * &lt;property name="lecm-signed-docflow:signing-date/&gt;
     */
	QName PROP_SIGNING_DATE = QName.createQName(SIGNED_DOCFLOW_NAMESPACE, "signing-date");

    /**
     * &lt;property name="lecm-signed-docflow:serial-number/&gt;
     */
	QName PROP_SERIAL_NUMBER = QName.createQName(SIGNED_DOCFLOW_NAMESPACE, "serial-number");

    /**
     * &lt;property name="lecm-signed-docflow:valid-from/&gt;
     */
	QName PROP_VALID_FROM = QName.createQName(SIGNED_DOCFLOW_NAMESPACE, "valid-from");

    /**
     * &lt;property name="lecm-signed-docflow:valid-through/&gt;
     */
	QName PROP_VALID_THROUGH = QName.createQName(SIGNED_DOCFLOW_NAMESPACE, "valid-through");

    /**
     * &lt;property name="lecm-signed-docflow:ca/&gt;
     */
	QName PROP_CA = QName.createQName(SIGNED_DOCFLOW_NAMESPACE, "ca");

    /**
     * &lt;property name="lecm-signed-docflow:update-date/&gt;
     */
	QName PROP_UPDATE_DATE = QName.createQName(SIGNED_DOCFLOW_NAMESPACE, "update-date");

    /**
     * &lt;property name="lecm-signed-docflow:is-valid/&gt;
     */
	QName PROP_IS_VALID = QName.createQName(SIGNED_DOCFLOW_NAMESPACE, "is-valid");

    /**
     * &lt;property name="lecm-signed-docflow:is-our/&gt;
     */
	QName PROP_IS_OUR = QName.createQName(SIGNED_DOCFLOW_NAMESPACE, "is-our");

    /**
     * &lt;association name="lecm-signed-docflow:sign-to-content-association/&gt;
     */
	QName ASSOC_SIGN_TO_CONTENT = QName.createQName(SIGNED_DOCFLOW_NAMESPACE, "sign-to-content-association");

    /**
     * &lt;aspect name="lecm-signed-docflow:signable"/&gt;
     */
	QName ASPECT_SIGNABLE = QName.createQName(SIGNED_DOCFLOW_NAMESPACE, "signable");

    /**
     * &lt;aspect name="lecm-signed-docflow:docflowable"/&gt;
     */
	QName ASPECT_DOCFLOWABLE = QName.createQName(SIGNED_DOCFLOW_NAMESPACE, "docflowable");

    /**
     * &lt;aspect name="lecm-signed-docflow:aspect-document-id/&gt;
     */
	QName ASPECT_DOCUMENT_ID = QName.createQName(SIGNED_DOCFLOW_NAMESPACE, "aspect-document-id");

    /**
     * &lt;property name="lecm-signed-docflow:document-id/&gt;
     */
	QName PROP_DOCUMENT_ID = QName.createQName(SIGNED_DOCFLOW_NAMESPACE, "document-id");

	/**
	 * &lt;aspect name="lecm-signed-docflow:organization-attrs-aspect"&gt;
	 */
	QName ASPECT_ORGANIZATION_ATTRS = QName.createQName(SIGNED_DOCFLOW_NAMESPACE, "organization-attrs-aspect");

	/**
	 * &lt;property name="lecm-signed-docflow:operator-code"&gt;
	 */
	QName PROP_OPERATOR_CODE = QName.createQName(SIGNED_DOCFLOW_NAMESPACE, "operator-code");

	/**
	 * &lt;property name="lecm-signed-docflow:partner-key"&gt;
	 */
	QName PROP_PARTNER_KEY = QName.createQName(SIGNED_DOCFLOW_NAMESPACE, "partner-key");

	/**
	 * &lt;property name="lecm-signed-docflow:organization-id"&gt;
	 */
	QName PROP_ORGANIZATION_ID = QName.createQName(SIGNED_DOCFLOW_NAMESPACE, "organization-id");

	/**
	 * &lt;property name="lecm-signed-docflow:organization-edo-id"&gt;
	 */
	QName PROP_ORGANIZATION_EDO_ID = QName.createQName(SIGNED_DOCFLOW_NAMESPACE, "organization-edo-id");

	/**
	 * &lt;aspect name="lecm-signed-docflow:personal-data-attrs-aspect"&gt;
	 */
	QName ASPECT_PERSONAL_DATA_ATTRS = QName.createQName(SIGNED_DOCFLOW_NAMESPACE, "personal-data-attrs-aspect");

	/**
	 * &lt;property name="lecm-signed-docflow:auth-token"&gt;
	 */
	QName PROP_AUTH_TOKEN = QName.createQName(SIGNED_DOCFLOW_NAMESPACE, "auth-token");

	/**
	 * &lt;property name="lecm-signed-docflow:cert-thumbprint"&gt;
	 */
	QName PROP_CERT_THUMBPRINT = QName.createQName(SIGNED_DOCFLOW_NAMESPACE, "cert-thumbprint");

	/**
	 * &lt;property name="lecm-signed-docflow:auth-type"&gt;
	 */
	QName PROP_AUTH_TYPE = QName.createQName(SIGNED_DOCFLOW_NAMESPACE, "auth-type");

	/**
	 * Проверяет, есть ли у NodeRef'ы аспект <strong>docflowable</strong>
     *
     * @param nodeRef NodeRef'а у которой следует проверить наличие аспекта <strong>docflowable</strong>
	 */
	boolean isDocflowable(NodeRef nodeRef);

	/**
	 * Проверяет, есть ли у NodeRef'ы аспект <strong>signable</strong>
     *
     * @param nodeRef NodeRef'а у которой следует проверить наличие аспекта <strong>signable</strong>
	 */
	boolean isSignable(NodeRef nodeRef);

	/**
	 * Добавляет аспект <strong>docflowable</strong> к NodeRef'е
     *
     * @param nodeRef NodeRef'а, к которой следует добавить аспект <strong>docflowable</strong>
	 */
	void addDocflowableAspect(NodeRef nodeRef);

	/**
	 * Удаляет аспект <strong>docflowable</strong> у NodeRef'ы
     *
     * @param nodeRef NodeRef'а, у которой следует удалить аспект <strong>docflowable</strong>
	 */
	void removeDocflowableAspect(NodeRef nodeRef);

	/**
	 * Добавляет аспект <strong>signable</strong> к NodeRef'е
     *
     * @param nodeRef NodeRef'а, к которой следует добавить аспект <strong>signable</strong>
	 */
	void addSignableAspect(NodeRef nodeRef);

	/**
	 * Удаляет аспект <strong>signable</strong> у NodeRef'ы
     *
     * @param nodeRef NodeRef'а, у которой следует удалить аспект <strong>signable</strong>
	 */
	void removeSignableAspect(NodeRef nodeRef);

	NodeRef getSignedDocflowFolder();
}
