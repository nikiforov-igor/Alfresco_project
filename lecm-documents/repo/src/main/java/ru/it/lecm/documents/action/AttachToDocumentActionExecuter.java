package ru.it.lecm.documents.action;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.action.ParameterDefinitionImpl;
import org.alfresco.repo.action.executer.ActionExecuterAbstractBase;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ParameterDefinition;
import org.alfresco.service.cmr.dictionary.DataTypeDefinition;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.documents.beans.DocumentAttachmentsService;

import java.util.List;

/**
 * User: AIvkin
 * Date: 02.04.14
 * Time: 16:30
 */
public class AttachToDocumentActionExecuter extends ActionExecuterAbstractBase {
	private static final transient Logger logger = LoggerFactory.getLogger(AttachToDocumentActionExecuter.class);

	public static final String PARAM_DOCUMENT = "document";
	public static final String PARAM_CATEGORY = "category";

	private NodeService nodeService;
	private DocumentAttachmentsService documentAttachmentsService;

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	public void setDocumentAttachmentsService(DocumentAttachmentsService documentAttachmentsService) {
		this.documentAttachmentsService = documentAttachmentsService;
	}

	@Override
	protected void addParameterDefinitions(List<ParameterDefinition> paramList) {
		paramList.add(new ParameterDefinitionImpl(PARAM_DOCUMENT,
				DataTypeDefinition.NODE_REF, true, getParamDisplayLabel(PARAM_DOCUMENT), false));
		paramList.add(new ParameterDefinitionImpl(PARAM_CATEGORY,
				DataTypeDefinition.TEXT, true, getParamDisplayLabel(PARAM_CATEGORY)));
	}

	@Override
	protected void executeImpl(Action action, NodeRef actionedUponNodeRef) {
		if (nodeService.exists(actionedUponNodeRef)) {
			NodeRef document = (NodeRef) action.getParameterValue(PARAM_DOCUMENT);
			String category = (String) action.getParameterValue(PARAM_CATEGORY);

			NodeRef categoryRef = documentAttachmentsService.getCategory(category, document);
			if (categoryRef != null) {
				if (!documentAttachmentsService.isReadonlyCategory(categoryRef)) {
					String name = nodeService.getProperty(actionedUponNodeRef, ContentModel.PROP_NAME).toString();
					QName assocQname = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, name);
					nodeService.moveNode(actionedUponNodeRef, categoryRef, ContentModel.ASSOC_CONTAINS, assocQname);
				} else {
					throw new AlfrescoRuntimeException("Категория вложений \"" + category + "\" недоступна для вложений в документе " + document);
				}
			} else {
				throw new AlfrescoRuntimeException("Категория вложений \"" + category + "\" не найдена в документе " + document);
			}
		}
	}
}
