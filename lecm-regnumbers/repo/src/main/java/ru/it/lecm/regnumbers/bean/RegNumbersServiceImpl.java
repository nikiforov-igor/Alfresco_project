package ru.it.lecm.regnumbers.bean;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.alfresco.repo.search.impl.lucene.SolrJSONResultSet;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.PropertyCheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.dictionary.beans.DictionaryBean;
import ru.it.lecm.regnumbers.RegNumbersService;
import ru.it.lecm.regnumbers.template.Parser;
import ru.it.lecm.regnumbers.template.ParserImpl;
import ru.it.lecm.regnumbers.template.TemplateParseException;
import ru.it.lecm.regnumbers.template.TemplateRunException;
import ru.it.lecm.documents.beans.DocumentService;

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
			result = ex.getMessage() + " because of following: " + ex.getCause().getMessage();
			if (verbose) {
				StringWriter stringWriter = new StringWriter();
				PrintWriter printWriter = new PrintWriter(stringWriter);
				ex.printStackTrace(printWriter);

				result += stringWriter.toString();
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
}
