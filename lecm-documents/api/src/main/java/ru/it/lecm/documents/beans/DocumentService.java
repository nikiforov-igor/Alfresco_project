package ru.it.lecm.documents.beans;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;

import java.io.Serializable;
import java.util.Map;

/**
 * User: AIvkin
 * Date: 28.02.13
 * Time: 16:03
 */
public interface DocumentService {
    public static final String DOCUMENT_NAMESPACE_URI = "http://www.it.ru/logicECM/document/1.0";
    public static final String DOCUMENT_ASPECTS_NAMESPACE_URI = "http://www.it.ru/lecm/document/aspects/1.0";

    public static final QName TYPE_BASE_DOCUMENT = QName.createQName(DOCUMENT_NAMESPACE_URI, "base");
	public static final QName PROP_DOCUMENT_REGNUM = QName.createQName(DOCUMENT_NAMESPACE_URI, "regnum");

    public static final QName PROP_PRESENT_STRING = QName.createQName(DOCUMENT_NAMESPACE_URI, "present-string");
    public static final QName PROP_LIST_PRESENT_STRING = QName.createQName(DOCUMENT_NAMESPACE_URI, "list-present-string");
    public static final QName ASSOC_DOC_MEMBERS = QName.createQName(DOCUMENT_NAMESPACE_URI, "doc-members-assoc");
    public static final String CONSTRAINT_PRESENT_STRING = "present-string-constraint";

    public static final QName PROP_RATING = QName.createQName(DOCUMENT_ASPECTS_NAMESPACE_URI, "rating");
    public static final QName PROP_RATED_PERSONS_COUNT = QName.createQName(DOCUMENT_ASPECTS_NAMESPACE_URI, "rated-persons-count");

    /**
     * Метод для получения рейтинга документа
     * documentNodeRef - document nodeRef
     * @return document rating
     */
    public String getRating(NodeRef documentNodeRef);

    /**
     * Метод для получения количества сотрудников, оценивших документ
     * documentNodeRef - document nodeRef
     * @return persons count
     */
    public Integer getRatedPersonCount(NodeRef documentNodeRef);

    /**
     * Метод для получения рейтинга документа, выставленного текущим сотрудником
     * documentNodeRef - document nodeRef
     * @return my rating of the document
     */
    public Integer getMyRating(NodeRef documentNodeRef);

    /**
     * Метод для выставления рейтинга документа текущим сотрудником
     * documentNodeRef - document nodeRef
     * rating - rating
     * @return setted rating
     */
    public Integer setMyRating(NodeRef documentNodeRef, Integer rating);

    /**
     * Метод получения аттрибутов документа
     * @param nodeRef
     * @return attributes
     */
    public Map<QName, Serializable> getProperties(NodeRef nodeRef);

    public NodeRef createDocument(String type, Map<String, String> properties);

    public NodeRef editDocument(NodeRef nodeRef, Map<String, String> properties);

    public Map<QName, Serializable> changeProperties (NodeRef documentRef, Map<QName, Serializable> properties);

	public boolean isDocument(NodeRef ref);

    /** Получение пути для папки черновиков
     * @return xpath до директории с черновиками
     */
    String getDraftPath();

    String getDraftPath(String rootName);
    
    /**
     * Получение пути для папки Documents
     * @return xpath до директории
     */
    String getDocumentsFolderPath();

    /**
     * Получение ноды с черновиками
     * @return NodeRef
     */
    NodeRef getDraftRoot();

    NodeRef getDraftRoot(String rootName);
}
