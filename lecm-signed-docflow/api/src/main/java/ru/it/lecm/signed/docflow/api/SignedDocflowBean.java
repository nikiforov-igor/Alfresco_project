package ru.it.lecm.signed.docflow.api;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;

/**
 *
 * @author vlevin
 */
public interface SignedDocflowBean {

	String SIGNED_DOCFLOW_PREFIX = "lecm-signed-docflow";
	String SIGNED_DOCFLOW_NAMESPACE = "http://www.it.ru/lecm/model/signed-docflow/1.0";
	// <type name="lecm-signed-docflow:sign">
	QName TYPE_SIGN = QName.createQName(SIGNED_DOCFLOW_NAMESPACE, "sign");
	// <property name="lecm-signed-docflow:owner">
	QName PROP_OWNER = QName.createQName(SIGNED_DOCFLOW_NAMESPACE, "owner");
	// <property name="lecm-signed-docflow:owner-position">
	QName PROP_OWNER_POSITION = QName.createQName(SIGNED_DOCFLOW_NAMESPACE, "owner-position");
	// <property name="lecm-signed-docflow:owner-organization">
	QName PROP_OWNER_ORGANIZATION = QName.createQName(SIGNED_DOCFLOW_NAMESPACE, "owner-organization");
	// <property name="lecm-signed-docflow:signing-date">
	QName PROP_SIGNING_DATE = QName.createQName(SIGNED_DOCFLOW_NAMESPACE, "signing-date");
	// <property name="lecm-signed-docflow:serial-number">
	QName PROP_SERIAL_NUMBER = QName.createQName(SIGNED_DOCFLOW_NAMESPACE, "serial-number");
	// <property name="lecm-signed-docflow:valid-from">
	QName PROP_VALID_FROM = QName.createQName(SIGNED_DOCFLOW_NAMESPACE, "valid-from");
	// <property name="lecm-signed-docflow:valid-through">
	QName PROP_VALID_THROUGH = QName.createQName(SIGNED_DOCFLOW_NAMESPACE, "valid-through");
	// <property name="lecm-signed-docflow:ca">
	QName PROP_CA = QName.createQName(SIGNED_DOCFLOW_NAMESPACE, "ca");
	// <property name="lecm-signed-docflow:update-date">
	QName PROP_UPDATE_DATE = QName.createQName(SIGNED_DOCFLOW_NAMESPACE, "update-date");
	// <property name="lecm-signed-docflow:is-valid">
	QName PROP_IS_VALID = QName.createQName(SIGNED_DOCFLOW_NAMESPACE, "is-valid");
	// <property name="lecm-signed-docflow:is-our">
	QName PROP_IS_OUR = QName.createQName(SIGNED_DOCFLOW_NAMESPACE, "is-our");
	// <association name="lecm-signed-docflow:sign-to-content-association">
	QName ASSOC_SIGN_TO_CONTENT = QName.createQName(SIGNED_DOCFLOW_NAMESPACE, "sign-to-content-association");
	// <aspect name="lecm-signed-docflow:signable"/>
	QName ASPECT_SIGNABLE = QName.createQName(SIGNED_DOCFLOW_NAMESPACE, "signable");
	// <aspect name="lecm-signed-docflow:docflowable"/>
	QName ASPECT_DOCFLOWABLE = QName.createQName(SIGNED_DOCFLOW_NAMESPACE, "docflowable");
	// <aspect name="lecm-signed-docflow:aspect-document-id">
	QName ASPECT_DOCUMENT_ID = QName.createQName(SIGNED_DOCFLOW_NAMESPACE, "aspect-document-id");
	// <property name="lecm-signed-docflow:document-id">
	QName PROP_DOCUMENT_ID = QName.createQName(SIGNED_DOCFLOW_NAMESPACE, "document-id");

	/**
	 * Проверяет, есть ли у переданной NodeRef'ы аспект
	 * lecm-signed-docflow:docflowable
	 */
	boolean isDocflowable(NodeRef nodeRef);

	/**
	 * Проверяет, есть ли у переданной NodeRef'ы аспект
	 * lecm-signed-docflow:signable
	 */
	boolean isSignable(NodeRef nodeRef);
}
