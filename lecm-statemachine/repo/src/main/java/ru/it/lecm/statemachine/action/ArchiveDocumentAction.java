package ru.it.lecm.statemachine.action;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.impl.util.xml.Element;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.workflow.activiti.ActivitiScriptNode;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

/**
 * User: PMelnikov
 * Date: 10.01.13
 * Time: 14:32
 */
public class ArchiveDocumentAction extends StateMachineAction {

	private String archiveFolderPath = "/Archive";
	private NodeRef archiveFolder = null;

	@Override
	public void execute(DelegateExecution execution) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-d");
		String dateFolder = format.format(new Date());
		NodeService nodeService = getServiceRegistry().getNodeService();
		NodeRef folder = nodeService.getChildByName(archiveFolder, ContentModel.ASSOC_CONTAINS, dateFolder);
		if (folder == null) {
			folder = createFolder(archiveFolder, dateFolder);
		}

		NodeRef wPackage = ((ActivitiScriptNode) execution.getVariable("bpm_package")).getNodeRef();
		List<ChildAssociationRef> documents = nodeService.getChildAssocs(wPackage);
		for (ChildAssociationRef document : documents) {
			String name = (String) nodeService.getProperty(document.getChildRef(), ContentModel.PROP_NAME);
			nodeService.moveNode(document.getChildRef(), folder, ContentModel.ASSOC_CONTAINS, QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, QName.createValidLocalName(name)));
		}

	}

	@Override
	public void init(Element actionElement, String processId) {
		List<Element> attributes = actionElement.elements("attribute");
		for (Element attribute : attributes) {
			String name = attribute.attribute("name");
			String value = attribute.attribute("value");
			if ("archiveFolder".equalsIgnoreCase(name)) {
				archiveFolderPath = value;
			}
		}

		//Проверяем структуру
		NodeService nodeService = getServiceRegistry().getNodeService();
		archiveFolder = getCompanyHome();
		StringTokenizer tokenizer = new StringTokenizer(archiveFolderPath, "/");
		while (tokenizer.hasMoreTokens()) {
			String folderName = tokenizer.nextToken();
			if (!"".equals(folderName)) {
				NodeRef folder = nodeService.getChildByName(archiveFolder, ContentModel.ASSOC_CONTAINS, folderName);
				if (folder == null) {
					folder = createFolder(archiveFolder, folderName);
				}
				archiveFolder = folder;
			}
		}

	}

}
