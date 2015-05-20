package ru.it.lecm.meetings.policy;

import java.util.List;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.Behaviour;
import org.alfresco.repo.policy.BehaviourFilter;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.documents.beans.DocumentTableService;
import ru.it.lecm.meetings.beans.ProtocolService;

/**
 *
 * @author snovikov
 */
public class ProtocolPolicy extends BaseBean {

	private BehaviourFilter behaviourFilter;
	private PolicyComponent policyComponent;
	private DocumentTableService documentTableService;
	
	public void setBehaviourFilter(BehaviourFilter behaviourFilter) {
		this.behaviourFilter = behaviourFilter;
	}
	
	public void setPolicyComponent(PolicyComponent policyComponent) {
		this.policyComponent = policyComponent;
	}
	
	public void setDocumentTableService(DocumentTableService documentTableService) {
		this.documentTableService = documentTableService;
	}
	
	@Override
	public NodeRef getServiceRootFolder() {
		return null;
	}
	
	public void init() {
		policyComponent.bindClassBehaviour(NodeServicePolicies.OnCreateNodePolicy.QNAME,
						ProtocolService.TYPE_PROTOCOL,
						new JavaBehaviour(this, "onCreateProtocol", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));
	}
	
	public void onCreateProtocol(ChildAssociationRef childAssocRef) {
		NodeRef document = childAssocRef.getChildRef();
		List<AssociationRef> items = nodeService.getTargetAssocs(document, ProtocolService.ASSOC_PROTOCOL_TEMP_ITEM);
		Integer index = 0;
		
		for (AssociationRef itemAssoc : items) {
			index++;
			NodeRef item = itemAssoc.getTargetRef();
			try {
				behaviourFilter.disableBehaviour(item);
				NodeRef table = documentTableService.getTable(document, ProtocolService.TYPE_PROTOCOL_TS_POINTS_TABLE);
				String assocName = nodeService.getProperty(item, ContentModel.PROP_NAME).toString();
				QName itemAssocQName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, assocName);
				nodeService.moveNode(item, table, ContentModel.ASSOC_CONTAINS, itemAssocQName);
				nodeService.setProperty(item, DocumentTableService.PROP_INDEX_TABLE_ROW, index);
			} finally {
				behaviourFilter.enableBehaviour(item);
			}
		}
	}
	
}
