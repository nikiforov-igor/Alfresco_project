package ru.it.lecm.outgoing.extensions;

import java.util.List;
import org.alfresco.repo.jscript.BaseScopableProcessorExtension;
import org.alfresco.repo.workflow.activiti.ActivitiScriptNode;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.outgoing.api.OutgoingModel;
import ru.it.lecm.regnumbers.RegNumbersService;
import ru.it.lecm.regnumbers.template.TemplateParseException;
import ru.it.lecm.regnumbers.template.TemplateRunException;

/**
 *
 * @author vmalygin
 */
public class OutgoingStatemachineJavascriptExtension extends BaseScopableProcessorExtension {

	private final static String OUTGOING_PRJ_TEMPLATE_CODE = "OUTGOING_PRJ_NUMBER";
	private final static String OUTGOING_DOC_TEMPLATE_CODE = "OUTGOING_DOC_NUMBER";
	private final static Logger logger = LoggerFactory.getLogger(OutgoingStatemachineJavascriptExtension.class);

	private NodeService nodeService;
	private DictionaryService dictionaryService;
	private RegNumbersService regNumbersService;

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	public void setDictionaryService(DictionaryService dictionaryService) {
		this.dictionaryService = dictionaryService;
	}

	public void setRegNumbersService(RegNumbersService regNumbersService) {
		this.regNumbersService = regNumbersService;
	}

	/**
	 * получение исходящего через bpm:package машины состояний
	 *
	 * @todo убрать этот метод потому что он скопирован из approval-repo
	 * @param bpmPackage bpm:package исходящего у машины состояний
	 * @return null если исходящего не существует. В лог будет написано сообщение об этом
	 */
	@Deprecated
	private NodeRef getOutgoingFromBpmPackage(NodeRef bpmPackage) {
		NodeRef outgoingRef = null;
		List<ChildAssociationRef> children = nodeService.getChildAssocs(bpmPackage);
		if (children != null) {
			for (ChildAssociationRef assocRef : children) {
				NodeRef candidateRef = assocRef.getChildRef();
				if (dictionaryService.isSubClass(nodeService.getType(candidateRef), OutgoingModel.TYPE_OUTGOING)) {
					outgoingRef = candidateRef;
					break;
				}
			}
			if (outgoingRef == null) {
				logger.error("There is no outgoing document of type {} in statemachine bpm:package");
			}
		} else {
			logger.error("List of statemachine bpm:package children is null");
		}
		return outgoingRef;
	}

	/**
	 * регистрация проекта исходящего
	 *
	 * @param bpmPackage bpm:package исходящего у машины состояний
	 */
	public void setOutgoingProjectRegNumber(ActivitiScriptNode bpmPackage) {
		NodeRef outgoingRef = getOutgoingFromBpmPackage(bpmPackage.getNodeRef());
		try {
			regNumbersService.registerProject(outgoingRef, OUTGOING_PRJ_TEMPLATE_CODE);
		} catch (TemplateParseException ex) {
			logger.error("Error registering ougoing project", ex);
		} catch (TemplateRunException ex) {
			logger.error("Error registering ougoing project", ex);
		}
	}

	/**
	 * регистрация документа исходящего
	 *
	 * @param bpmPackage bpm:package исходящего у машины состояний
	 */
	public void setOutgoingDocumentRegNumber(ActivitiScriptNode bpmPackage) {
		NodeRef outgoingRef = getOutgoingFromBpmPackage(bpmPackage.getNodeRef());
		try {
			regNumbersService.registerDocument(outgoingRef, OUTGOING_DOC_TEMPLATE_CODE);
		} catch (TemplateParseException ex) {
			logger.error("Error registering ougoing document", ex);
		} catch (TemplateRunException ex) {
			logger.error("Error registering ougoing document", ex);
		}
	}
}
