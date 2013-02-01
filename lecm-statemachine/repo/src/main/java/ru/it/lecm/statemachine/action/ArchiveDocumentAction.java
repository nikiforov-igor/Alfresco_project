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

import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: PMelnikov
 * Date: 10.01.13
 * Time: 14:32
 */
public class ArchiveDocumentAction extends StateMachineAction {

	private String archiveFolderPath = "/Archive";
	private String status = "UNKNOWN";

	@Override
	public void execute(DelegateExecution execution) {
		NodeService nodeService = getServiceRegistry().getNodeService();
		NodeRef wPackage = ((ActivitiScriptNode) execution.getVariable("bpm_package")).getNodeRef();
		List<ChildAssociationRef> documents = nodeService.getChildAssocs(wPackage);
		for (ChildAssociationRef document : documents) {
			String name = (String) nodeService.getProperty(document.getChildRef(), ContentModel.PROP_NAME);
			nodeService.setProperty(document.getChildRef(), QName.createQName("http://www.it.ru/logicECM/statemachine/1.0", "status"), status);
			NodeRef folder = createArchivePath(document.getChildRef());
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
			} else if ("status".equalsIgnoreCase(name)) {
				status = value;
			}
		}
	}

	private NodeRef createArchivePath(NodeRef node) {
		//Проверяем структуру
		Pattern pattern = Pattern.compile("\\{(.*?):(.*?)\\}");
		Matcher matcher = pattern.matcher(archiveFolderPath);
		String path = archiveFolderPath;
		while (matcher.find()) {
			String prefix = matcher.group(1);
			String attributeName = matcher.group(2);
			QName attribute = QName.createQName(prefix, attributeName, getServiceRegistry().getNamespaceService());
			String value = getServiceRegistry().getNodeService().getProperty(node, attribute).toString();
			path = path.replace("{" + prefix + ":" + attributeName + "}", value);
		}

		NodeService nodeService = getServiceRegistry().getNodeService();
		NodeRef archiveFolder = getCompanyHome();
		StringTokenizer tokenizer = new StringTokenizer(path, "/");
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
		return archiveFolder;
	}

}
