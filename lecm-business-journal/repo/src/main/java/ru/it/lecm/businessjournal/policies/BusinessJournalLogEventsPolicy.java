package ru.it.lecm.businessjournal.policies;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.PropertyCheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.businessjournal.beans.BusinessJournalService;
import ru.it.lecm.businessjournal.beans.EventCategory;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

/**
 * @author dbashmakov
 *         Date: 04.02.13
 *         Time: 15:23
 */
public class BusinessJournalLogEventsPolicy implements NodeServicePolicies.OnUpdatePropertiesPolicy, NodeServicePolicies.OnCreateNodePolicy{

	public static final String LECM = "lecm";
	final protected Logger logger = LoggerFactory.getLogger(BusinessJournalLogEventsPolicy.class);

	private final Set<QName> NOT_AFFECTED_TYPES
			= new HashSet<QName>() {{
		add(BusinessJournalService.TYPE_BR_RECORD);
		add(OrgstructureBean.TYPE_ORGANIZATION_UNIT);
	}};

	private static PolicyComponent policyComponent;
	private static BusinessJournalService businessJournalService;
	private static NodeService nodeService;
	private static NamespaceService namespaceService;

	public void setNodeService(NodeService nodeService) {
		BusinessJournalLogEventsPolicy.nodeService = nodeService;
	}

	public void setNamespaceService(NamespaceService namespaceService) {
		BusinessJournalLogEventsPolicy.namespaceService = namespaceService;
	}

	public void setBusinessJournalService(BusinessJournalService businessJournalService) {
		BusinessJournalLogEventsPolicy.businessJournalService = businessJournalService;
	}

	public void setPolicyComponent(PolicyComponent policyComponent) {
		BusinessJournalLogEventsPolicy.policyComponent = policyComponent;
	}

	public final void init() {
		PropertyCheck.mandatory(this, "policyComponent", policyComponent);

		/*policyComponent.bindClassBehaviour(NodeServicePolicies.OnCreateNodePolicy.QNAME,
				ContentModel.TYPE_CMOBJECT, new JavaBehaviour(this, "onCreateNode", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));

		policyComponent.bindClassBehaviour(NodeServicePolicies.OnUpdatePropertiesPolicy.QNAME,
				ContentModel.TYPE_CMOBJECT, new JavaBehaviour(this, "onUpdateProperties", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));*/
	}

	@Override
	public void onCreateNode(ChildAssociationRef childAssocRef) {
		NodeRef nodeRef = childAssocRef.getChildRef();
		QName typeQName = nodeService.getType(nodeRef);
		String type = typeQName.toPrefixString(namespaceService);
		if (type.startsWith(LECM) && !NOT_AFFECTED_TYPES.contains(typeQName)) {
			try {
				businessJournalService.log(nodeRef, EventCategory.ADD, "Создан новый объект #mainobject", null);
			} catch (Exception e) {
				logger.error("Could not create the record business-journal", e);
			}
		}
	}

	@Override
	public void onUpdateProperties(NodeRef nodeRef, Map<QName, Serializable> before, Map<QName, Serializable> after) {
		QName typeQName = nodeService.getType(nodeRef);
		String type = typeQName.toPrefixString(namespaceService);
		if (type.startsWith(LECM) && !NOT_AFFECTED_TYPES.contains(typeQName) && before.size() == after.size()) {
			try {
				businessJournalService.log(nodeRef, EventCategory.EDIT, "Объект #mainobject изменен", null);
			} catch (Exception e) {
				logger.error("Could not create the record business-journal", e);
			}
		}
	}
}
