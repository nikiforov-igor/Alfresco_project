package ru.it.lecm.signed.docflow.api;

import java.io.Serializable;
import java.util.Map;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;

import java.util.List;
import org.json.JSONArray;

/**
 *
 * @author vlevin
 */
public interface SignedDocflow {

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
	 * Получить ссылку на папку, в которой хранятся подписи.
	 *
	 * @return NodeRef на папку с подписями.
	 */
	NodeRef getSignedDocflowFolder();
    void generateTestSigns(final NodeRef contentToSignRef);

    List<Signature> getSignatures(NodeRef signedContentRef);

	/**
	 * Получает подписи для каждого вложения
	 * @param nodeRefList
	 * @return
	 */
	Map<NodeRef, List<Signature>> getSignaturesInfo(List<NodeRef> nodeRefList);

	/**
	 * Подписать контент (стандартный cm:content или вложение документа).
	 *
	 * @param signatureProperties Атрибутивный состав объекта подписи (lecm-signed-docflow:sign)
	 * @return На данный момент - карта типа "success": true
	 */
	Map<String, Object> signContent(Map<QName, Serializable> signatureProperties);
	
	/**
	 * Загрузка подписи к коненту
	 * 
	 * @param signatureProperties
	 * @return 
	 */
	Map<String, Object> loadSign(Map<QName, Serializable> signatureProperties);
	/**
	 * Получить подписи, привязанные к вложению документа или cm:content.
	 *
	 * @param contentRef NodeRef на подписанный контент.
	 * @return список ссылок на подписи
	 */
	List<NodeRef> getSignaturesByContent(NodeRef contentRef);

	/**
	 * Получить подписанный контент по подписи.
	 *
	 * @param signatureRef NodeRef на объект подписи.
	 * @return NodeRef на подписанный контент.
	 */
	NodeRef getContentBySignature(NodeRef signatureRef);

	/**
	 * @param signatureRef NodeRef на объект подписи
	 * @return отпечаток сертификата
	 */
	String getFingerprintBySignature(NodeRef signatureRef);

	/**
	 * @param signatureRef NodeRef на объект подписи
	 * @return содержимое подписи в виде строки Base64
	 */
	String getSignatureContentBySignature(NodeRef signatureRef);

	/**
	 * @param signatureRef NodeRef на объект подписи
	 * @return действительна ли данная подпись
	 */
	boolean isSignatureValid(NodeRef signatureRef);
	
	/**
	 * Обновление данных о подписи
	 * @param signatureRef
	 * @return 
	 */
	boolean updateSignature(NodeRef signatureRef, String singingDate, boolean isValid);
	
	/**
	 * Обновление данных о подписях
	 * @param json
	 * @return 
	 */
	Map<String, String> updateSignatures(JSONArray json);
	
	void addAttributesToOrganization();

	void addAttributesToPersonalData();
}
