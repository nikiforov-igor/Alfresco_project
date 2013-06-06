package ru.it.lecm.documents.beans;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
    public static final String CONSTRAINT_PRESENT_STRING = "present-string-constraint";

    public static final QName PROP_RATING = QName.createQName(DOCUMENT_ASPECTS_NAMESPACE_URI, "rating");
    public static final QName PROP_RATED_PERSONS_COUNT = QName.createQName(DOCUMENT_ASPECTS_NAMESPACE_URI, "rated-persons-count");

    public static final QName PROP_STATUS_CHANGED_DATE = QName.createQName(DOCUMENT_NAMESPACE_URI, "status-changed-date");
    public static final QName PROP_DOCUMENT_CREATOR = QName.createQName(DOCUMENT_NAMESPACE_URI, "creator");
    public static final QName PROP_DOCUMENT_CREATOR_REF = QName.createQName(DOCUMENT_NAMESPACE_URI, "creator-ref");
    public static final QName PROP_DOCUMENT_MODIFIER = QName.createQName(DOCUMENT_NAMESPACE_URI, "modifier");
    public static final QName PROP_DOCUMENT_MODIFIER_REF = QName.createQName(DOCUMENT_NAMESPACE_URI, "modifier-ref");
    public static final QName PROP_DOCUMENT_IS_TRANSMIT = QName.createQName(DOCUMENT_NAMESPACE_URI, "istransmit");
    public static final QName PROP_DOCUMENT_DEPRIVE_RIGHT = QName.createQName(DOCUMENT_NAMESPACE_URI, "deprive-right");

    public static final DateFormat DateFormatISO8601 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mmZ");

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


	/**
	 * Полечение документа из workflow listener
	 * @param packageRef Package items nodeRef
	 * @return Ссылка на документ
	 */
	public NodeRef getDocumentFromPackageItems(NodeRef packageRef);

    public List<NodeRef> getMembers(QName docType);

    public List<NodeRef> getDocuments(List<QName> docTypes, List<String> paths, ArrayList<String> statuses);

    public List<NodeRef> getDocumentsByFilter(List<QName> docTypes, QName dateProperty, Date begin, Date end, List<String> paths, List<String> statuses, List<NodeRef> inititatorsList, List<NodeRef> docsList);

    public String getDraftRootLabel(String docType);
}
