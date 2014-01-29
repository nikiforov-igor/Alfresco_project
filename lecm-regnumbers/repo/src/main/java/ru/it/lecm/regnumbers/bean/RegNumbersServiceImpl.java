package ru.it.lecm.regnumbers.bean;


import org.alfresco.model.ContentModel;
import org.alfresco.repo.search.impl.lucene.SolrJSONResultSet;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.PropertyCheck;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.dictionary.beans.DictionaryBean;
import ru.it.lecm.documents.beans.DocumentConnectionService;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.regnumbers.RegNumbersService;
import ru.it.lecm.regnumbers.template.Parser;
import ru.it.lecm.regnumbers.template.ParserImpl;
import ru.it.lecm.regnumbers.template.TemplateParseException;
import ru.it.lecm.regnumbers.template.TemplateRunException;
import ru.it.lecm.documents.beans.DocumentService;

import java.io.Serializable;
import java.util.*;

/**
 *
 * @author vlevin
 */
public class RegNumbersServiceImpl extends BaseBean implements RegNumbersService, ApplicationContextAware {

	final private static Logger logger = LoggerFactory.getLogger(RegNumbersServiceImpl.class);
	private final String searchQuery = "TYPE:\"%s\" AND ALL:\"%s\"";
	private ApplicationContext applicationContext;
	private SearchService searchService;
	private NamespaceService namespaceService;
	private DictionaryBean dictionaryService;
	private DocumentService documentService;
	private OrgstructureBean orgstructureService;
	private DocumentConnectionService documentConnectionService;

	public final void init() {
		PropertyCheck.mandatory(this, "transactionService", transactionService);
		PropertyCheck.mandatory(this, "nodeService", nodeService);
	}

	public void setNamespaceService(final NamespaceService namespaceService) {
		this.namespaceService = namespaceService;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
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

	@Override
	public String getNumber(NodeRef documentNode, String templateStr) throws TemplateParseException, TemplateRunException {
		Parser parser = new ParserImpl(applicationContext);
		return parser.runTemplate(templateStr, documentNode);
	}

	@Override
	public String getNumber(NodeRef documentNode, NodeRef templateNode) throws TemplateParseException, TemplateRunException {
		return getNumber(documentNode, getTemplateString(templateNode));
	}

	@Override
	public boolean isNumberUnique(String number) {
		boolean isUnique;
		SearchParameters sp = new SearchParameters();
		sp.addStore(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
		sp.setLanguage(SearchService.LANGUAGE_FTS_ALFRESCO);
		sp.setQuery(String.format(searchQuery, DocumentService.TYPE_BASE_DOCUMENT.toString(), number));

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
	public String validateTemplate(String templateStr, boolean verbose) {
		String result = "";
		Parser parser = new ParserImpl(applicationContext);
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
		return dictionaryService.getDictionaryValueByParam(RegNumbersService.REGNUMBERS_TEMPLATE_DICTIONARY_NAME, RegNumbersService.PROP_TEMPLATE_SERVICE_ID, dictionaryTemplateCode);
	}

    @Override
    public void registerProject(NodeRef documentNode, String dictionaryTemplateCode)  throws TemplateParseException, TemplateRunException {
        registerProject(documentNode, dictionaryTemplateCode, false);
    }

    @Override
    public void registerDocument(NodeRef documentNode, String dictionaryTemplateCode)  throws TemplateParseException, TemplateRunException{
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

    /**
     * Получить регистрационный номер для документа по указанному шаблону и
     * записать его в документа.
     *
     *
     * @param documentNode ссылка на экземпляр документа, которому необходимо
     * присвоить номер.
     * @param onlyReserve флаг нужно ли реально регистрировать документ
     * или только зарезервировать номер
     * @param isProjectRegister флаг, происходит ли регистрация проекта документа. Если false - значит регистрируется документ
     * @throws TemplateParseException В шаблоне есть синтаксическа ошибка:
     * незакрытые одинарные скобки, пропушен плюс, неверные символы в названии
     * функций. Детали см. в эксепшене.
     * @throws TemplateRunException Ошибка на этапе выполнения шаблона:
     * неверное имя метода, функции или объекта, неверные параметры функции или
     * метода. Детали см. в эксепшене.
     */
    private void register(NodeRef documentNode, String dictionaryTemplateCode, boolean onlyReserve, boolean isProjectRegister)  throws TemplateParseException, TemplateRunException {
        NodeRef templateDictionary = getTemplateNodeByCode(dictionaryTemplateCode);
        if (templateDictionary != null && documentNode != null) {
            QName regAspectName = isProjectRegister ? DocumentService.ASPECT_HAS_REG_PROJECT_DATA : DocumentService.ASPECT_HAS_REG_DOCUMENT_DATA;
            QName regAssocName =  isProjectRegister ? DocumentService.ASSOC_REG_PROJECT_DATA : DocumentService.ASSOC_REG_DOCUMENT_DATA;

            if (!nodeService.hasAspect(documentNode, regAspectName)) {
                nodeService.addAspect(documentNode, regAspectName, null);
            }

            List<AssociationRef> prDataAssocs = nodeService.getTargetAssocs(documentNode, regAssocName);
            if (prDataAssocs != null && !prDataAssocs.isEmpty()) { // рег данные уже созданы - только изменение флага регистрации
                if (!onlyReserve) {
                    nodeService.setProperty(prDataAssocs.get(0).getTargetRef(), DocumentService.PROP_REG_DATA_IS_REGISTERED, Boolean.TRUE);
                }
            } else {
	            String regNumber;
	            NodeRef repeatedDocument = getRepeatedDocument(documentNode);
	            if (repeatedDocument != null) {
		            regNumber = getRepeatedNumber(documentNode);
	            } else {
		            regNumber = getNumber(documentNode, templateDictionary);
	            }

                Map<QName, Serializable> properties = new HashMap<QName, Serializable>();
                properties.put(DocumentService.PROP_REG_DATA_DATE, new Date());
                properties.put(DocumentService.PROP_REG_DATA_NUMBER, regNumber);
                properties.put(DocumentService.PROP_REG_DATA_IS_REGISTERED, !onlyReserve);

                QName assocQName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, UUID.randomUUID().toString());

                NodeRef regAttributesRef = nodeService.createNode(documentNode, ContentModel.ASSOC_CONTAINS, assocQName,
                        DocumentService.TYPE_REG_DATA_ATTRIBUTES, properties).getChildRef();

                NodeRef currentEmployee = orgstructureService.getCurrentEmployee();
                if (currentEmployee != null) {
                    nodeService.createAssociation(regAttributesRef, currentEmployee, DocumentService.ASSOC_REG_DATA_REGISTRATOR);
                }
                nodeService.createAssociation(documentNode, regAttributesRef, regAssocName);
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
		Integer maxIndex = connectedDocumentSequence.size()-1;
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
				if (connectedDocumentRefs.size() > 1)
					logger.warn(String.format("Document %s has more than 1 repeated documents", currentDocumentRef.toString()));

				currentDocumentRef = connectedDocumentRefs.get(0);
			}
		}

		return documentsRefs;
	}
}
