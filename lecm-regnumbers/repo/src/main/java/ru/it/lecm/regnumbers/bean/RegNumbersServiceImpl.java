package ru.it.lecm.regnumbers.bean;

import org.alfresco.repo.search.impl.lucene.SolrJSONResultSet;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.PropertyCheck;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationEvent;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.businessjournal.beans.BusinessJournalService;
import ru.it.lecm.dictionary.beans.DictionaryBean;
import ru.it.lecm.documents.beans.DocumentConnectionService;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.regnumbers.RegNumbersService;
import ru.it.lecm.regnumbers.template.Parser;
import ru.it.lecm.regnumbers.template.ParserImpl;
import ru.it.lecm.regnumbers.template.TemplateParseException;
import ru.it.lecm.regnumbers.template.TemplateRunException;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.extensions.surf.util.AbstractLifecycleBean;
/**
 *
 * @author vlevin
 */
public class RegNumbersServiceImpl extends BaseBean implements RegNumbersService {

	private final static Logger logger = LoggerFactory.getLogger(RegNumbersServiceImpl.class);
	private final static String SEARCH_QUERY_TEMPLATE_WITH_REGDATE = "TYPE:\"%s\" AND =regnumberTemplate:\"%s\" AND @lecm\\-document\\-aspects:reg\\-data\\-date:[\"%d-01-01T00:00:00\" TO \"%d-12-31T23:59:59\"]";
	private final static String SEARCH_QUERY_TEMPLATE = "TYPE:\"%s\" AND =regnumberTemplate:\"%s\"";
	/**
	 * Ищем регистрационные номера в этих полях документа
	 */
	private final static String REGNUMBER_SEARCH_TEMPLATE = "%lecm\\-document:regnum";
	private NodeRef templateDictionaryNode;

	private SearchService searchService;
	private NamespaceService namespaceService;
	private DictionaryBean dictionaryService;
	private DocumentService documentService;
	private OrgstructureBean orgstructureService;
	private DocumentConnectionService documentConnectionService;
    private BusinessJournalService businessJournalService;

	public final void init() {
		PropertyCheck.mandatory(this, "transactionService", transactionService);
		PropertyCheck.mandatory(this, "nodeService", nodeService);
		PropertyCheck.mandatory(this, "searchService", searchService);
		PropertyCheck.mandatory(this, "namespaceService", namespaceService);
		PropertyCheck.mandatory(this, "dictionaryService", dictionaryService);
		PropertyCheck.mandatory(this, "documentService", documentService);
		PropertyCheck.mandatory(this, "orgstructureService", orgstructureService);
		PropertyCheck.mandatory(this, "documentConnectionService", documentConnectionService);
		PropertyCheck.mandatory(this, "businessJournalService", businessJournalService);
		logger.info("!!!!!!!!!!!!!!!!!! init");
	}

	public NodeRef getTemplateDictionaryNode() {
		if (templateDictionaryNode == null) {
			templateDictionaryNode = dictionaryService.getDictionaryByName(RegNumbersService.REGNUMBERS_TEMPLATE_DICTIONARY_NAME);
		}
		return templateDictionaryNode;
	}
		
	@Override
	protected void onShutdown(ApplicationEvent event)
	{
		logger.info("!!!!!!!!!!!!!!!!!! onShutdown :"+event);
	}

	public void setNamespaceService(final NamespaceService namespaceService) {
		this.namespaceService = namespaceService;
	}

	public void setSearchService(SearchService searchService) {
		this.searchService = searchService;
	}

	public void setDictionaryService(DictionaryBean dictionaryService) {
		this.dictionaryService = dictionaryService;
	}

	public void setDocumentConnectionService(DocumentConnectionService documentConnectionService) {
		this.documentConnectionService = documentConnectionService;
	}

	public void setDocumentService(DocumentService documentService) {
		this.documentService = documentService;
	}

	public void setOrgstructureService(OrgstructureBean orgstructureService) {
		this.orgstructureService = orgstructureService;
	}

    public void setBusinessJournalService(BusinessJournalService businessJournalService) {
        this.businessJournalService = businessJournalService;
    }

    @Override
	public String getNumber(NodeRef documentNode, String templateStr) throws TemplateParseException, TemplateRunException {
		Parser parser = new ParserImpl(getApplicationContext());
		return parser.runTemplate(templateStr, documentNode);
	}

	@Override
	public String getNumber(NodeRef documentNode, NodeRef templateNode) throws TemplateParseException, TemplateRunException {
		return getNumber(documentNode, getTemplateString(templateNode));
	}

	@Override
	public boolean isNumberUnique(String number, QName documentType) {
		return isNumberUnique(number, documentType, null);
	}

	@Override
	public boolean isNumberUnique(String number, QName documentType, Date regDate) {
		String query;
		boolean isUnique;
        String numberTransformed = StringUtils.trim(number).toUpperCase().replace("\"", "\\\"");
		SearchParameters sp = new SearchParameters();
		sp.addStore(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
		sp.setLanguage(SearchService.LANGUAGE_FTS_ALFRESCO);
		if (regDate != null) {
			DateTime dt = new DateTime(regDate);
			int year = dt.getYear();

			query = String.format(SEARCH_QUERY_TEMPLATE_WITH_REGDATE, documentType.toString(), numberTransformed, year, year);
		} else {
			query = String.format(SEARCH_QUERY_TEMPLATE, documentType.toString(), numberTransformed);
		}
		sp.setQuery(query);

        if (documentType.toPrefixString(namespaceService).equals("lecm-contract:document")) {
            sp.addQueryTemplate("regnumberTemplate", "%lecm\\-contract:regNumSystem");
        } else {
            sp.addQueryTemplate("regnumberTemplate", REGNUMBER_SEARCH_TEMPLATE);
        }

		ResultSet results = null;
		try {
			sp.setMaxItems(0);
			results = searchService.query(sp);
			if (results instanceof SolrJSONResultSet) {
				isUnique = ((SolrJSONResultSet) results).getNumberFound() == 0;
			} else {
				sp.setMaxItems(-1);
				results = searchService.query(sp);
				isUnique = results.length() == 0;
			}
		} finally {
			if (results != null) {
				results.close();
			}
		}

		return isUnique;
	}

	@Override
	public boolean isNumberUnique(String number) {
		return isNumberUnique(number, DocumentService.TYPE_BASE_DOCUMENT);
	}

	@Override
	public String validateTemplate(String templateStr, boolean verbose) {
		String result = "";
		Parser parser = new ParserImpl(getApplicationContext());
		try {
			parser.parseTemplate(templateStr);
		} catch (TemplateParseException ex) {
			result = String.format("%s because of following: %s", ex.getMessage(), ex.getCause().getMessage());
			if (verbose) {
				result += ExceptionUtils.getStackTrace(ex);
			}
		}

		return result;
	}

	@Override
	public String getTemplateString(NodeRef templateNode) {
		return (String) nodeService.getProperty(templateNode, PROP_TEMPLATE_STRING);
	}

	@Override
	public void setDocumentNumber(NodeRef documentNode, QName documentProperty, String templateStr) throws TemplateParseException, TemplateRunException {
		nodeService.setProperty(documentNode, documentProperty, getNumber(documentNode, templateStr));
	}

	@Override
	public void setDocumentNumber(NodeRef documentNode, QName documentProperty, NodeRef templateNode) throws TemplateParseException, TemplateRunException {
		setDocumentNumber(documentNode, documentProperty, getTemplateString(templateNode));
	}

	@Override
	public void setDocumentNumber(NodeRef documentNode, String documentPropertyPrefix, String templateStr) throws TemplateParseException, TemplateRunException {
		setDocumentNumber(documentNode, QName.createQName(documentPropertyPrefix, namespaceService), templateStr);
	}

	@Override
	public void setDocumentNumber(NodeRef documentNode, String documentPropertyPrefix, NodeRef templateNode) throws TemplateParseException, TemplateRunException {
		setDocumentNumber(documentNode, QName.createQName(documentPropertyPrefix, namespaceService), getTemplateString(templateNode));
	}

	@Override
	public void setDocumentNumber(String dictionaryTemplateCode, NodeRef documentNode, String documentPropertyPrefix) throws TemplateParseException, TemplateRunException {
		NodeRef templateDictionary = getTemplateNodeByCode(dictionaryTemplateCode);
		if (templateDictionary != null) {
			setDocumentNumber(documentNode, documentPropertyPrefix, templateDictionary);
		}
	}

	// в данном бине не используется каталог в /app:company_home/cm:Business platform/cm:LECM/
	@Override
	public NodeRef getServiceRootFolder() {
		return null;
	}

	@Override
	public NodeRef getTemplateNodeByCode(String dictionaryTemplateCode) {
		NodeRef result = null;

		List<ChildAssociationRef> dictionaryValuesAssocs = nodeService.getChildAssocsByPropertyValue(getTemplateDictionaryNode(), RegNumbersService.PROP_TEMPLATE_SERVICE_ID, dictionaryTemplateCode);

		for (ChildAssociationRef assoc : dictionaryValuesAssocs) {
			NodeRef templateNode = assoc.getChildRef();
			if (!isArchive(templateNode)) {
				result = templateNode;
				break;
			}
		}

		return result;
	}

	@Override
	public void registerProject(NodeRef documentNode, String dictionaryTemplateCode) throws TemplateParseException, TemplateRunException {
		registerProject(documentNode, dictionaryTemplateCode, false);
	}

	@Override
	public void registerDocument(NodeRef documentNode, String dictionaryTemplateCode) throws TemplateParseException, TemplateRunException {
		registerDocument(documentNode, dictionaryTemplateCode, false);
	}

	@Override
	public void registerProject(NodeRef documentNode, String dictionaryTemplateCode, boolean onlyReserve) throws TemplateParseException, TemplateRunException {
		register(documentNode, dictionaryTemplateCode, onlyReserve, true);
	}

	@Override
	public void registerDocument(NodeRef documentNode, String dictionaryTemplateCode, boolean onlyReserve) throws TemplateParseException, TemplateRunException {
		register(documentNode, dictionaryTemplateCode, onlyReserve, false);
	}

	@Override
	public void registerProject(NodeRef templateRef, NodeRef documentNode) throws TemplateParseException, TemplateRunException {
		registerProject(documentNode, getTemplateString(templateRef));
	}

	@Override
	public void registerDocument(NodeRef documentNode, NodeRef templateRef) throws TemplateParseException, TemplateRunException {
		registerDocument(documentNode, getTemplateString(templateRef));
	}

	@Override
	public boolean isRegistered(NodeRef documentNode, boolean isProject) {
		QName regAspectName = isProject ? DocumentService.ASPECT_HAS_REG_PROJECT_DATA : DocumentService.ASPECT_HAS_REG_DOCUMENT_DATA;

		if (nodeService.hasAspect(documentNode, regAspectName)) {
			if (isProject) {
				Serializable projectNumber = nodeService.getProperty(documentNode, DocumentService.PROP_REG_DATA_PROJECT_NUMBER);
				return projectNumber != null && !DocumentService.DEFAULT_REG_NUM.equals(projectNumber.toString());
			} else {
				Serializable isRegistered = nodeService.getProperty(documentNode, DocumentService.PROP_REG_DATA_DOC_IS_REGISTERED);
				if (isRegistered != null) {
					return (Boolean) isRegistered;
				}
			}
		}
		return false;
	}

	/**
	 * Получить регистрационный номер для документа по указанному шаблону и
	 * записать его в документа.
	 *
	 *
	 * @param documentNode ссылка на экземпляр документа, которому необходимо
	 * присвоить номер.
	 * @param onlyReserve флаг нужно ли реально регистрировать документ
	 * или только зарезервировать номер
	 * @param isProjectRegister флаг, происходит ли регистрация проекта документа. Если false - значит регистрируется
	 * документ
	 * @throws TemplateParseException В шаблоне есть синтаксическа ошибка:
	 * незакрытые одинарные скобки, пропушен плюс, неверные символы в названии
	 * функций. Детали см. в эксепшене.
	 * @throws TemplateRunException Ошибка на этапе выполнения шаблона:
	 * неверное имя метода, функции или объекта, неверные параметры функции или
	 * метода. Детали см. в эксепшене.
	 */
	private void register(NodeRef documentNode, String dictionaryTemplateCode, boolean onlyReserve, boolean isProjectRegister) throws TemplateParseException, TemplateRunException {
		if (isRegistered(documentNode, isProjectRegister)) {
			return;
		}
		NodeRef templateDictionary = getTemplateNodeByCode(dictionaryTemplateCode);
		if (documentNode != null) {
            QName regAspectName = isProjectRegister ? DocumentService.ASPECT_HAS_REG_PROJECT_DATA : DocumentService.ASPECT_HAS_REG_DOCUMENT_DATA;
            QName propNumber = isProjectRegister ? DocumentService.PROP_REG_DATA_PROJECT_NUMBER : DocumentService.PROP_REG_DATA_DOC_NUMBER;
            QName propDate = isProjectRegister ? DocumentService.PROP_REG_DATA_PROJECT_DATE : DocumentService.PROP_REG_DATA_DOC_DATE;
            QName propIsRegistered = isProjectRegister ? null : DocumentService.PROP_REG_DATA_DOC_IS_REGISTERED;

            if (!nodeService.hasAspect(documentNode, regAspectName)) {
                nodeService.addAspect(documentNode, regAspectName, null);
            }
            NodeRef currentEmployee = orgstructureService.getCurrentEmployee();
            Serializable number = nodeService.getProperty(documentNode, propNumber);
            String regNumber = null;
            
            // Сохранить дату, если она была зарезервирована. В противном случае запомнить текущую дату.
            Date regDate = (Date) nodeService.getProperty(documentNode, propDate);
            if (regDate == null) {
            	regDate = new Date();
            }
            
            if (number != null && !number.toString().isEmpty() && !DocumentService.DEFAULT_REG_NUM.equals(number.toString())) {
                regNumber = number.toString();
                //номер уже есть
                if (propIsRegistered != null && !onlyReserve) {
                    nodeService.setProperty(documentNode, propIsRegistered, Boolean.TRUE);
                }
            } else {
                //регистрируем
                NodeRef repeatedDocument = getRepeatedDocument(documentNode);
                QName documentType = nodeService.getType(documentNode);
                // нам нужно получить уникальный регистрационный номер документа.
                // есть вероятность, что сгененрированный номер уже используется, потому что задан руками
				do {
					String prevRegNum = regNumber;
                    if (repeatedDocument != null) {
                        regNumber = getRepeatedNumber(documentNode);
                    } else {
                        regNumber = templateDictionary != null ? getNumber(documentNode, templateDictionary) : getNumber(documentNode, dictionaryTemplateCode);
                    }
					if (regNumber.equals(prevRegNum)){
						throw new TemplateRunException("Can't generate unique regNumber for document "+documentNode.toString()+": reg. number didn't modyfied after retry.");
					}
                } while (!isNumberUnique(regNumber, documentType, regDate));

				regNumber = StringUtils.trim(regNumber).toUpperCase();
                nodeService.setProperty(documentNode, propNumber, regNumber);
                if (propIsRegistered != null) {
                    nodeService.setProperty(documentNode, propIsRegistered, !onlyReserve);
                }

                if (currentEmployee != null && !isProjectRegister) {
                    List<NodeRef> targetRefs = new ArrayList<>();
                    targetRefs.add(currentEmployee);
                    nodeService.setAssociations(documentNode, DocumentService.ASSOC_REG_DATA_DOC_REGISTRATOR, targetRefs);
                }
            }

            nodeService.setProperty(documentNode, propDate, regDate);
            documentService.setDocumentActualNumber(documentNode, regNumber);
            documentService.setDocumentActualDate(documentNode, regDate);

            if (!onlyReserve) {
                DateFormat dFormat = new SimpleDateFormat("dd.MM.yyyy");
                String regDateString = dFormat.format(regDate);

                String text;
                if (isProjectRegister) {
                    text = "#initiator зарегистрировал(а) проект документа ";
                } else {
                    text = "#initiator зарегистрировал(а) документ ";
                }
                businessJournalService.log(orgstructureService.getEmployeeLogin(currentEmployee), documentNode, "REGISTRATION", text + wrapperLink(documentNode, regNumber + " от " + regDateString, documentService.getDocumentUrl(documentNode)), null);
            }
        }
	}

	private NodeRef getRepeatedDocument(NodeRef documentRef) {
		QName documentType = nodeService.getType(documentRef);
		List<NodeRef> connectedDocument = documentConnectionService.getConnectedDocuments(documentRef, DocumentConnectionService.DICTIONARY_VALUE_REPAED_TO, documentType, true);

		if (connectedDocument.size() > 0) {
			if (connectedDocument.size() > 1) {
				logger.warn(String.format("Document %s has more than 1 repeated documents", documentRef.toString()));
			}

			return connectedDocument.get(0);
		}
		return null;
	}

	private String getRepeatedNumber(NodeRef documentRef) {
		List<NodeRef> connectedDocumentSequence = getAllRepeated(documentRef);
		Integer maxIndex = connectedDocumentSequence.size() - 1;
		if (maxIndex > 0) {
			NodeRef originDocumentRef = connectedDocumentSequence.get(maxIndex);

			String originalDocumentRegNumber = documentService.getDocumentRegNumber(originDocumentRef);
			if (originalDocumentRegNumber != null) {
				return String.format("%s-(%d)", originalDocumentRegNumber, maxIndex);
			}
		}
		return null;
	}

	private List<NodeRef> getAllRepeated(NodeRef documentRef) {
		List<NodeRef> documentsRefs = getConnectionChain(documentRef); //получаем ветку имеющимся методом - нужен оригинальный документ

		NodeRef currentDocumentRef;

		int i = documentsRefs.size() - 1;
		//обходим дерево повторных документов, добавляя в список (оригинальный документ всегда остается в конце списка)
		while (i >= 0) {
			currentDocumentRef = documentsRefs.get(i);
			List<NodeRef> connectedWithDocumentRefs = documentConnectionService.getConnectedWithDocument(currentDocumentRef, DocumentConnectionService.DICTIONARY_VALUE_REPAED_TO, nodeService.getType(documentRef));
			for (NodeRef connectedWithDocumentRef : connectedWithDocumentRefs) {
				if (!documentsRefs.contains(connectedWithDocumentRef)) {
					documentsRefs.add(i++, connectedWithDocumentRef);
				}
			}
			i--;
		}

		return documentsRefs;
	}

	private List<NodeRef> getConnectionChain(NodeRef documentRef) {
		List<NodeRef> documentsRefs = new ArrayList<NodeRef>();

		NodeRef currentDocumentRef = documentRef;
		Boolean originIsReached = Boolean.FALSE;
		while (!originIsReached) {
			documentsRefs.add(currentDocumentRef);
			List<NodeRef> connectedDocumentRefs = documentConnectionService.getConnectedDocuments(currentDocumentRef, DocumentConnectionService.DICTIONARY_VALUE_REPAED_TO, nodeService.getType(documentRef));
			if (connectedDocumentRefs.isEmpty()) {
				originIsReached = Boolean.TRUE;
			} else {
				if (connectedDocumentRefs.size() > 1) {
					logger.warn(String.format("Document %s has more than 1 repeated documents", currentDocumentRef.toString()));
				}

				currentDocumentRef = connectedDocumentRefs.get(0);
			}
		}

		return documentsRefs;
	}
}
