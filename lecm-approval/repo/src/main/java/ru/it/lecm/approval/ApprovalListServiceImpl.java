package ru.it.lecm.approval;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.namespace.RegexQNamePattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.approval.api.ApprovalListService;
import ru.it.lecm.base.beans.BaseBean;

/**
 *
 * @author vlevin
 */
public class ApprovalListServiceImpl extends BaseBean implements ApprovalListService {

	private final static Logger logger = LoggerFactory.getLogger(ApprovalListServiceImpl.class);

	@Override
	public NodeRef getServiceRootFolder() {
		return null;
	}

	/**
	 * Создание папки "Согласование/Параллельное согласование" внутри документа
	 *
	 * @param parent
	 * @return
	 */
	private NodeRef getOrCreateApprovalFolder(NodeRef parent) {
		return null;
	}

	@Override
	public NodeRef createApprovalList(List<NodeRef> employees, NodeRef bpmPackage) {
		//через bpmPackage получить ссылку на документ
		QName TYPE_CONTRACT_DOCUMENT = QName.createQName("http://www.it.ru/logicECM/contract/1.0", "document");
		List<ChildAssociationRef> children = nodeService.getChildAssocs(bpmPackage, TYPE_CONTRACT_DOCUMENT, RegexQNamePattern.MATCH_ALL);
		if (children.size() > 1) {
			logger.warn("There are more than one lecm-contract:document found!");
		}
		NodeRef documentRef = children.get(0).getChildRef();
		//внутри этого документа получить или создать папку "Согласование/Параллельное согласование"
		NodeRef approvalFolder = getOrCreateApprovalFolder(documentRef);
		//Лист согласования вер. 1.0"
		String docVersion = (String) nodeService.getProperty(documentRef, ContentModel.PROP_VERSION_LABEL);
//		String documentName = (String) nodeService.getProperty(documentRef, ContentModel.PROP_NAME);
		String approvalListName = "Лист согласования " + docVersion;
		QName assocName = QName.createQName(APPROVAL_LIST_NAMESPACE, approvalListName);
		Map<QName, Serializable> properties = new HashMap<QName, Serializable>();
		properties.put(ContentModel.PROP_NAME, approvalListName);
		properties.put(PROP_APPROVAL_LIST_DOCUMENT_VERSION, docVersion);
		properties.put(PROP_APPROVAL_LIST_APPROVE_DATE, "ДАТА НАЧАЛА СОГЛАСОВАНИЯ");
		NodeRef approvalListRef = nodeService.createNode(approvalFolder, ContentModel.ASSOC_CONTAINS, assocName, TYPE_APPROVAL_LIST, properties).getChildRef();

		return approvalListRef;
	}

	@Override
	public void logDecision(NodeRef approvalListRef, String userName, String decision, Date decisionDate, String comment, NodeRef commentRef) {
		throw new UnsupportedOperationException("Not supported yet.");
	}
}
