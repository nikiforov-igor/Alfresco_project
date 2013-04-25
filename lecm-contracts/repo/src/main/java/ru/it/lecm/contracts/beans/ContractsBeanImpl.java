package ru.it.lecm.contracts.beans;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.ResultSetRow;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.GUID;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.dictionary.beans.DictionaryBean;
import ru.it.lecm.documents.beans.DocumentConnectionService;
import ru.it.lecm.documents.beans.DocumentMembersService;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.notifications.beans.Notification;
import ru.it.lecm.notifications.beans.NotificationsService;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.regnumbers.RegNumbersService;
import ru.it.lecm.regnumbers.template.TemplateParseException;
import ru.it.lecm.regnumbers.template.TemplateRunException;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * User: mshafeev
 * Date: 16.04.13
 * Time: 14:16
 */
public class ContractsBeanImpl extends BaseBean {
	public static final String CONTRACTS = "Contracts";
	public static final String DOCUMENT_CONNECTION_ON_BASIS_DICTIONARY_VALUE_CODE = "onBasis";
	public static final String CONTRACTS_NAMESPACE_URI = "http://www.it.ru/logicECM/contract/1.0";
	public static final String CONTRACTS_ASPECTS_NAMESPACE_URI = "http://www.it.ru/logicECM/contract/aspects/1.0";
	public static final String ADDITIONAL_DOCUMENT_NAMESPACE_URI = "http://www.it.ru/logicECM/contract/additional-document/1.0";

	public static final QName TYPE_CONTRACTS_DOCUMENT = QName.createQName(CONTRACTS_NAMESPACE_URI, "document");
	public static final QName TYPE_CONTRACTS_ADDICTIONAL_DOCUMENT = QName.createQName(ADDITIONAL_DOCUMENT_NAMESPACE_URI, "additionalDocument");
    public static final QName TYPE_CONTRACTS_START_DATE = QName.createQName(CONTRACTS_NAMESPACE_URI, "startDate");
    public static final QName TYPE_CONTRACTS_END_DATE = QName.createQName(CONTRACTS_NAMESPACE_URI, "endDate");

    public static final QName ASSOC_ADDITIONAL_DOCUMENT_TYPE = QName.createQName(ADDITIONAL_DOCUMENT_NAMESPACE_URI, "additionalDocumentType");
    public static final QName ASSOC_DOCUMENT = QName.createQName(ADDITIONAL_DOCUMENT_NAMESPACE_URI, "document-assoc");
	public static final QName ASSOC_DELETE_REASON = QName.createQName(CONTRACTS_ASPECTS_NAMESPACE_URI, "reasonDelete-assoc");
	public static final QName ASSOC_CONTRACT_TYPE = QName.createQName(CONTRACTS_NAMESPACE_URI, "typeContract-assoc");
	public static final QName ASSOC_CONTRACT_SUBJECT = QName.createQName(CONTRACTS_NAMESPACE_URI, "subjectContract-assoc");

	public static final QName ASPECT_CONTRACT_DELETED = QName.createQName(CONTRACTS_ASPECTS_NAMESPACE_URI, "deleted");
	public static final QName ASPECT_PRIMARY_DOCUMENT_DELETE = QName.createQName(CONTRACTS_ASPECTS_NAMESPACE_URI, "primaryDocumentDeletedAspect");

    public static final QName PROP_PRIMARY_DOCUMENT_DELETE = QName.createQName(CONTRACTS_ASPECTS_NAMESPACE_URI, "primaryDocumentDeleted");
	public static final QName PROP_REGNUM_PROJECT = QName.createQName(CONTRACTS_NAMESPACE_URI, "regNumProject");
	public static final QName PROP_REGNUM_SYSTEM = QName.createQName(CONTRACTS_NAMESPACE_URI, "regNumSystem");
	public static final QName PROP_ADDITIONAL_DOCUMENT_NUMBER = QName.createQName(ADDITIONAL_DOCUMENT_NAMESPACE_URI, "number");
	public static final QName PROP_DATE_REG_CONTRACT = QName.createQName(CONTRACTS_NAMESPACE_URI, "dateRegContracts");
	public static final QName PROP_DATE_REG_CONTRACT_PROJECT = QName.createQName(CONTRACTS_NAMESPACE_URI, "dateRegProjectContracts");

	public static final String CONTRACT_REGNUM_TEMPLATE = "{#employeeOrgUnitCode(doc.creator)}-{#formatNumber('0000', doc.counterYearDoctype)}/{#formatDate('yy', doc.creationDate)}";
	public static final String CONTRACT_PROJECT_REGNUM_TEMPLATE = "{#employeeOrgUnitCode(doc.creator)}-{doc.associatedAttributePath('lecm-contract:subjectContract-assoc/lecm-contract-dic:contract-subjects-code')}-{#formatNumber('0000', doc.counterYearDoctype)}/{#formatDate('yy', doc.creationDate)}";
	public static final String ADDITIONAL_DOCUMENT_PROJECT_REGNUM_TEMPLATE = "{doc.counterPlainDoctype}";

	public static final String BUSINESS_ROLE_CONTRACT_CURATOR_ID = "CONTRACT_CURATOR";

    private SearchService searchService;
	private DictionaryBean dictionaryService;
    private DocumentService documentService;
    private DocumentConnectionService documentConnectionService;
    private DocumentMembersService documentMembersService;
    private OrgstructureBean orgstructureService;
	private RegNumbersService regNumbersService;
	private NotificationsService notificationService;

    DateFormat DateFormatISO8601 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mmZ");

    public void setSearchService(SearchService searchService) {
        this.searchService = searchService;
    }

	public void setDocumentService(DocumentService documentService) {
		this.documentService = documentService;
	}

	public void setDocumentConnectionService(DocumentConnectionService documentConnectionService) {
		this.documentConnectionService = documentConnectionService;
	}

	public void setDictionaryService(DictionaryBean dictionaryService) {
		this.dictionaryService = dictionaryService;
	}

    public void setDocumentMembersService(DocumentMembersService documentMembersService) {
        this.documentMembersService = documentMembersService;
    }

	public void setRegNumbersService(RegNumbersService regNumbersService) {
		this.regNumbersService = regNumbersService;
	}

	public void setNotificationService(NotificationsService notificationService) {
		this.notificationService = notificationService;
	}

    public void setOrgstructureService(OrgstructureBean orgstructureService) {
        this.orgstructureService = orgstructureService;
    }

	@Override
    public NodeRef getServiceRootFolder() {
        return null;
    }

	public DocumentService getDocumentService() {
		return documentService;
	}

	public NodeRef getDraftRoot() {
		return  documentService.getDraftRoot(CONTRACTS);
	}

	public String getDraftPath() {
		return  documentService.getDraftPath(CONTRACTS);
	}

    /**
     * Поиск договоров
     * @param path путь где следует искать
     * @param statuses статусы договоров
     * @return
     */
    public List<NodeRef> getContracts(ArrayList<String> path, ArrayList<String> statuses) {
        List<NodeRef> records = new ArrayList<NodeRef>();
        SearchParameters sp = new SearchParameters();
        sp.addStore(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
        sp.setLanguage(SearchService.LANGUAGE_LUCENE);
        String query = "";

        // формируем запрос
        query = "TYPE:\"" + TYPE_CONTRACTS_DOCUMENT + "\"";

        if (path.size() > 0) {
            query = query + " AND (";
            for (int i = 0; i < path.size(); i++) {
                query = query + "PATH:\"" + path.get(i) + "//*\"";
                if ((i + 1) < path.size()) {
                    query = query + " OR ";
                }
            }
            query = query + ")";
        }

        if (statuses.size() > 0) {
            query = query + " AND (";
            for (int i = 0; i < statuses.size(); i++) {
                query = query + "@lecm\\-statemachine\\:status:\"" + statuses.get(i).trim() + "\"";
                if ((i + 1) < statuses.size()) {
                    query = query + " OR ";
                }
            }
            query = query + ")";
        }

        ResultSet results = null;
        sp.setQuery(query);
        try {
            results = searchService.query(sp);
            for (ResultSetRow row : results) {
                records.add(row.getNodeRef());
            }
        } finally {
            if (results != null) {
                results.close();
            }
        }
        return records;
    }

    /**
     * Метод получения всех участников
     * @return
     */
    public List<NodeRef> getAllMembers() {
        NodeRef membersUnit = documentMembersService.getMembersUnit(TYPE_CONTRACTS_DOCUMENT);
        return findNodesByAssociationRef(membersUnit, DocumentMembersService.ASSOC_UNIT_EMPLOYEE, null, ASSOCIATION_TYPE.TARGET);
    }

    public List<NodeRef> getAllMembers(String sortColumnLocalName, final boolean sortAscending) {
        List<NodeRef> members = getAllMembers();
        final QName sortFieldQName = sortColumnLocalName != null && sortColumnLocalName.length() > 0 ? QName.createQName(OrgstructureBean.ORGSTRUCTURE_NAMESPACE_URI, sortColumnLocalName) : OrgstructureBean.PROP_EMPLOYEE_FIRST_NAME;

        class NodeRefComparator<T extends Serializable & Comparable<T>> implements Comparator<NodeRef> {
            @Override
            public int compare(NodeRef nodeRef1, NodeRef nodeRef2) {
                T obj1 = (T) nodeService.getProperty(nodeRef1, sortFieldQName);
                T obj2 = (T) nodeService.getProperty(nodeRef2, sortFieldQName);

                return sortAscending ? obj1.compareTo(obj2) : obj2.compareTo(obj1);
            }
        }
        if (members.size() > 0 && nodeService.getProperties(members.get(0)).containsKey(sortFieldQName)){
            Collections.sort(members, new NodeRefComparator<String>());
        };
        return members;
    }

	public String createDocumentOnBasis(NodeRef typeRef, NodeRef documentRef) {
		ChildAssociationRef additionalDocumentAssociationRef = nodeService.createNode(getDraftRoot(),
				ContentModel.ASSOC_CONTAINS, QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI,
				GUID.generate()), TYPE_CONTRACTS_ADDICTIONAL_DOCUMENT);

		if (additionalDocumentAssociationRef != null && additionalDocumentAssociationRef.getChildRef() != null) {
			NodeRef additionalDocumentRef = additionalDocumentAssociationRef.getChildRef();
			nodeService.createAssociation(additionalDocumentRef, typeRef, ASSOC_ADDITIONAL_DOCUMENT_TYPE);
			nodeService.createAssociation(additionalDocumentRef, documentRef, ASSOC_DOCUMENT);

			NodeRef connectionType = dictionaryService.getDictionaryValueByParam(
					DocumentConnectionService.DOCUMENT_CONNECTION_TYPE_DICTIONARY_NAME,
					DocumentConnectionService.PROP_CONNECTION_TYPE_CODE,
					DOCUMENT_CONNECTION_ON_BASIS_DICTIONARY_VALUE_CODE);

			if (connectionType != null) {
				documentConnectionService.createConnection(additionalDocumentRef, documentRef, connectionType);
			}
            return additionalDocumentRef.toString();
		}
        return null;
    }

    /**
     * Добавить причину удаления к документу
     * @param reasonRef ссылка на узел причины удаления в справочнике "Причины удаления"
     * @param documentRef ссылка на документ
     */
    public void appendDeleteReason(NodeRef reasonRef, NodeRef documentRef) {
	    nodeService.addAspect(documentRef, ASPECT_CONTRACT_DELETED, null);
        nodeService.createAssociation(documentRef, reasonRef, ASSOC_DELETE_REASON);
    }

    public List<NodeRef> getContractsByFilter(Date begin, Date end, List<NodeRef> employeesList, List<NodeRef> docsList) {
        List<NodeRef> records = new ArrayList<NodeRef>();
        SearchParameters sp = new SearchParameters();
        sp.addStore(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
        sp.setLanguage(SearchService.LANGUAGE_LUCENE);
        String query = "";

        // формируем базовый запрос - ищем договора и документы к договорам в папке Черновики и Документы
        query = "(TYPE:\"" + TYPE_CONTRACTS_DOCUMENT + "\" OR TYPE:\"" + TYPE_CONTRACTS_ADDICTIONAL_DOCUMENT + "\") AND " +
                "(PATH:\"" + documentService.getDraftPath() + "//*\" OR PATH:\"" + documentService.getDocumentsFolderPath() + "//*\")";

        final String MIN = begin != null ? DateFormatISO8601.format(begin) : "MIN";
        final String MAX = end != null ? DateFormatISO8601.format(end) : "MAX";

        // Фильтр по датам последнего изменения статуса
        query += " AND @lecm\\-document\\:status-changed-date:[" + MIN + " TO " + MAX + "]";

        // фильтр по сотрудниками-создателям
        if (employeesList != null && !employeesList.isEmpty()) {
            boolean addOR = false;
            String employeesFilter = "";
            for (NodeRef employeeRef : employeesList) {
                String personName = orgstructureService.getEmployeeLogin(employeeRef);
                if (personName != null && !personName.isEmpty()) {
                    employeesFilter += (addOR ? " OR " : "") + "@cm\\:creator:" + personName + "";
                    addOR = true;
                }
            }
            query += " AND (" + employeesFilter + ")";
        }

        // фильтр по конкретным документам (например, тем в которых данный сотрудник - участник)
        if (docsList != null && !docsList.isEmpty()) {
            boolean addOR = false;
            String docsFilter = "";
            for (NodeRef docRef : docsList) {
                docsFilter += (addOR ? " OR " : "") + "ID:" + docRef.toString().replace(":", "\\:");
                addOR = true;
            }
            query += " AND (" + docsFilter + ")";
        }

        ResultSet results = null;
        sp.setQuery(query);
        try {
            results = searchService.query(sp);
            for (ResultSetRow row : results) {
                records.add(row.getNodeRef());
            }
        } finally {
            if (results != null) {
                results.close();
            }
        }
        return records;
    }

	public List<NodeRef> getAllContractDocuments(NodeRef contractRef) {
		return findNodesByAssociationRef(contractRef, ASSOC_DOCUMENT, TYPE_CONTRACTS_ADDICTIONAL_DOCUMENT, ASSOCIATION_TYPE.SOURCE);
	}

	public void registrationContractProject(NodeRef contractRef) throws TemplateParseException, TemplateRunException {
		String documentNumber = regNumbersService.getNumber(contractRef, CONTRACT_PROJECT_REGNUM_TEMPLATE);
		nodeService.setProperty(contractRef, PROP_REGNUM_PROJECT, documentNumber);
		nodeService.setProperty(contractRef, PROP_DATE_REG_CONTRACT_PROJECT, new Date());

		// уведомление
		List<NodeRef> curators = orgstructureService.getEmployeesByBusinessRole(BUSINESS_ROLE_CONTRACT_CURATOR_ID);
		StringBuilder notificationText = new StringBuilder();
		notificationText.append("Зарегистрирован проект договора номер ");
		notificationText.append(wrapperLink(contractRef, documentNumber, DOCUMENT_LINK_URL));
		notificationText.append(", вид договора ").append(getContractType(contractRef));
		notificationText.append(", тематика ").append(getContractSubject(contractRef));
		notificationText.append(", исполнитель ");
		NodeRef executor = getContractExecutor(contractRef);
		String executorName = nodeService.getProperty(executor, ContentModel.PROP_NAME).toString();
		notificationText.append(wrapperLink(executor, executorName, LINK_URL));

		Notification notification = new Notification();
		notification.setRecipientEmployeeRefs(curators);
		notification.setAutor(authService.getCurrentUserName());
		notification.setDescription(notificationText.toString());
		notification.setObjectRef(contractRef);
		notification.setInitiatorRef(orgstructureService.getCurrentEmployee());
		notificationService.sendNotification(this.notificationChannels, notification);
	}

	public void registrationContract(NodeRef contractRef) throws TemplateParseException, TemplateRunException {
		regNumbersService.setDocumentNumber(contractRef, PROP_REGNUM_SYSTEM, CONTRACT_REGNUM_TEMPLATE);
		nodeService.setProperty(contractRef, PROP_DATE_REG_CONTRACT, new Date());
	}

	public void registrationContractDocumentProject(NodeRef documentRef) throws TemplateParseException, TemplateRunException {
		String documentNumber = regNumbersService.getNumber(documentRef, ADDITIONAL_DOCUMENT_PROJECT_REGNUM_TEMPLATE);
		nodeService.setProperty(documentRef, PROP_ADDITIONAL_DOCUMENT_NUMBER, documentNumber);

		// уведомление
		List<NodeRef> curators = orgstructureService.getEmployeesByBusinessRole(BUSINESS_ROLE_CONTRACT_CURATOR_ID);
		NodeRef contract = findNodeByAssociationRef(documentRef, ASSOC_DOCUMENT, null, ASSOCIATION_TYPE.TARGET);
		if (contract != null) {
			StringBuilder notificationText = new StringBuilder();
			notificationText.append("Зарегистрирован проект документ вида ");

			NodeRef documentTypeRef = findNodeByAssociationRef(documentRef, ASSOC_ADDITIONAL_DOCUMENT_TYPE, null, ASSOCIATION_TYPE.TARGET);
			notificationText.append(wrapperLink(documentRef, nodeService.getProperty(documentTypeRef, ContentModel.PROP_NAME).toString(), DOCUMENT_LINK_URL));

			notificationText.append(" к договору номер ");
			notificationText.append(wrapperLink(contract, nodeService.getProperty(contract, PROP_REGNUM_SYSTEM).toString(), DOCUMENT_LINK_URL));
			notificationText.append(", вид договора ").append(getContractType(contract));
			notificationText.append(", тематика ").append(getContractSubject(contract));
			notificationText.append(", исполнитель ");
			NodeRef executor = getContractExecutor(contract);
			String executorName = nodeService.getProperty(executor, ContentModel.PROP_NAME).toString();
			notificationText.append(wrapperLink(executor, executorName, LINK_URL));

			Notification notification = new Notification();
			notification.setRecipientEmployeeRefs(curators);
			notification.setAutor(authService.getCurrentUserName());
			notification.setDescription(notificationText.toString());
			notification.setObjectRef(documentRef);
			notification.setInitiatorRef(orgstructureService.getCurrentEmployee());
			notificationService.sendNotification(this.notificationChannels, notification);
		}
	}

	public String getContractType(NodeRef contractRef) {
		NodeRef type = findNodeByAssociationRef(contractRef, ASSOC_CONTRACT_TYPE, null, ASSOCIATION_TYPE.TARGET);
		if (type != null) {
			return nodeService.getProperty(type, ContentModel.PROP_NAME).toString();
		}
		return null;
	}

	public String getContractSubject(NodeRef contractRef) {
		NodeRef subject = findNodeByAssociationRef(contractRef, ASSOC_CONTRACT_SUBJECT, null, ASSOCIATION_TYPE.TARGET);
		if (subject != null) {
			return nodeService.getProperty(subject, ContentModel.PROP_NAME).toString();
		}
		return null;
	}

	public NodeRef getContractExecutor(NodeRef contractRef) {
		String creator = (String) nodeService.getProperty(contractRef, ContentModel.PROP_CREATOR);
		return orgstructureService.getEmployeeByPerson(creator);
	}

    public List<NodeRef> getAdditionalDocs(String filter) {
        List<NodeRef> records = new ArrayList<NodeRef>();
        SearchParameters sp = new SearchParameters();
        sp.addStore(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
        sp.setLanguage(SearchService.LANGUAGE_LUCENE);
        String query = "";

        // формируем базовый запрос - ищем документы к договорам в папке Черновики и Документы
        query = "TYPE:\"" + TYPE_CONTRACTS_ADDICTIONAL_DOCUMENT + "\" AND " +
                "(PATH:\"" + documentService.getDraftPath() + "//*\" OR PATH:\"" + documentService.getDocumentsFolderPath() + "//*\")";


        if (filter != null && filter.length() > 0) {
            query += " AND (" + filter + ")";
        }

        ResultSet results = null;
        sp.setQuery(query);
        try {
            results = searchService.query(sp);
            for (ResultSetRow row : results) {
                records.add(row.getNodeRef());
            }
        } finally {
            if (results != null) {
                results.close();
            }
        }
        return records;
    }
}
