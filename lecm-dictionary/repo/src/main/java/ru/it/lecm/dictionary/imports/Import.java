package ru.it.lecm.dictionary.imports;

import org.alfresco.service.cmr.dictionary.AssociationDefinition;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.transaction.TransactionService;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;
import org.springframework.extensions.webscripts.servlet.FormData;
import org.springframework.extensions.webscripts.servlet.FormData.FormField;
import ru.it.lecm.dictionary.beans.DictionaryBean;
import ru.it.lecm.dictionary.beans.XMLImportBean;
import ru.it.lecm.dictionary.beans.XMLImporterInfo;

import javax.transaction.UserTransaction;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;


/**
 * User: mShafeev
 * Date: 23.10.12
 * Time: 12:01
 */
public class Import extends AbstractWebScript {
	private static final transient Logger logger = LoggerFactory.getLogger(Import.class);

	private DictionaryBean dictionaryBean;
	private DictionaryService dictionaryService;
	private NamespaceService namespaceService;
    private XMLImportBean xmlImportBean;
	private TransactionService transactionService;

    public void setXmlImportBean(XMLImportBean xmlImportBean) {
        this.xmlImportBean = xmlImportBean;
    }

	public void setDictionaryBean(DictionaryBean dictionaryBean) {
		this.dictionaryBean = dictionaryBean;
	}

	public void setTransactionService(TransactionService transactionService) {
		this.transactionService = transactionService;
	}

	public void setDictionaryService(DictionaryService dictionaryService) {
		this.dictionaryService = dictionaryService;
	}

	public void setNamespaceService(NamespaceService namespaceService) {
		this.namespaceService = namespaceService;
	}

	@Override
    public void execute(WebScriptRequest req, WebScriptResponse res) throws IOException {
	    JSONObject wf = new JSONObject();
	    JSONArray compositions = new JSONArray();
	    InputStream inputStream = null;
	    try {
		    FormData formData = (FormData) req.parseContent();
		    FormField[] fields = formData.getFields();

		    inputStream = fields[0].getInputStream();

		    boolean ignoreErrors = false;
		    if (req.getParameter("ignoreErrors") != null && req.getParameter("ignoreErrors").equals("true")) {
			    ignoreErrors = true;
		    }

            String receivedNodeRef = req.getParameter("nodeRef");
			final NodeRef rootDir;
            if (NodeRef.isNodeRef(receivedNodeRef)) {
                rootDir = new NodeRef(receivedNodeRef);
            } else {
                rootDir = dictionaryBean.getDictionariesRoot();
            }
		    final InputStream finalInputStream = inputStream;

		    UserTransaction ut = transactionService.getUserTransaction();
		    XMLImporterInfo importInfo = null;
		    try {
			    ut.begin();

			    try {
				    XMLImportBean.XMLImporter xmlDictionaryImporter = xmlImportBean.getXMLImporter(finalInputStream);
				    importInfo = xmlDictionaryImporter.readItems(rootDir);
			    } catch (XMLStreamException e) {
				    importInfo = new XMLImporterInfo();
				    logger.error(e.getMessage(), e);
			    }
			    if (importInfo.existErrors() && !ignoreErrors) {
				    ut.rollback();

				    importInfo.setCreatedElementsCount(0);
				    importInfo.setUpdatedElementsCount(0);
			    } else {
				    ut.commit();
			    }
		    } catch (Exception e) {
			    logger.error("Import error", e);
		    }

		    StringBuilder response = new StringBuilder();
		    response.append("Импортировано элементов: ").append(importInfo.getImportedElementsCount()).append("<br/>");
		    response.append("Создано элементов: ").append(importInfo.getCreatedElementsCount()).append("<br/>");
		    response.append("Обновлено элементов: ").append(importInfo.getUpdatedElementsCount()).append("<br/>");

		    Map<String, List<String>> notFoundAssoc = importInfo.getAssocNotFoundErrors();
		    if (notFoundAssoc != null && notFoundAssoc.size() > 0) {
			    response.append("<br/>");
			    response.append("Не найденные ассоциации: ").append("<br/>");
			    for (String assocName: notFoundAssoc.keySet()) {
				    AssociationDefinition assocDefinition = this.dictionaryService.getAssociation(QName.createQName(assocName, this.namespaceService));
				    response.append("&nbsp;&nbsp;&nbsp;").append(assocDefinition.getTitle()).append(":").append("<br/>");
				    for (String assocPath: notFoundAssoc.get(assocName)) {
					    response.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;").append(assocPath).append("<br/>");
				    }
			    }
		    }
		    wf.put("text", response.toString());

		    compositions.put(wf);
		    res.setContentType("text/plain");
		    res.setContentEncoding("UTF-8");
		    res.getWriter().write(compositions.toString());
	    } catch (JSONException e) {
		    logger.error(e.getMessage(), e);
	    } finally {
		    if (inputStream != null) {
			    inputStream.close();
		    }
	    }

    }
}
