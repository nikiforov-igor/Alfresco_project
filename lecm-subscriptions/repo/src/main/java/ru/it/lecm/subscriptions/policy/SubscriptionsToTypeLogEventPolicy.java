package ru.it.lecm.subscriptions.policy;

import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.Behaviour;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.PropertyCheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.businessjournal.beans.BusinessJournalService;
import ru.it.lecm.businessjournal.beans.EventCategory;
import ru.it.lecm.subscriptions.beans.SubscriptionsService;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * User: AIvkin
 * Date: 07.02.13
 * Time: 9:35
 */
public class SubscriptionsToTypeLogEventPolicy implements NodeServicePolicies.OnUpdatePropertiesPolicy, NodeServicePolicies.BeforeDeleteNodePolicy {

	private final static Logger logger = LoggerFactory.getLogger(SubscriptionsToTypeLogEventPolicy.class);

	private PolicyComponent policyComponent;
	private static BusinessJournalService businessJournalService;
	private NodeService nodeService;

	public void setPolicyComponent(PolicyComponent policyComponent) {
		this.policyComponent = policyComponent;
	}

	public void setBusinessJournalService(BusinessJournalService businessJournalService) {
		this.businessJournalService = businessJournalService;
	}

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	public final void init () {
		PropertyCheck.mandatory(this, "policyComponent", policyComponent);

		policyComponent.bindClassBehaviour(NodeServicePolicies.OnUpdatePropertiesPolicy.QNAME,
				SubscriptionsService.TYPE_SUBSCRIPTION_TO_TYPE, new JavaBehaviour(this, "onUpdateProperties", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));
		policyComponent.bindClassBehaviour(NodeServicePolicies.BeforeDeleteNodePolicy.QNAME,
				SubscriptionsService.TYPE_SUBSCRIPTION_TO_TYPE, new JavaBehaviour(this, "beforeDeleteNode"));
	}

	@Override
	public void beforeDeleteNode(NodeRef nodeRef) {
		boolean exist = nodeService.exists(nodeRef);
		if (exist) {
			List<AssociationRef> employees = nodeService.getTargetAssocs(nodeRef, SubscriptionsService.ASSOC_DESTINATION_EMPLOYEE);
			if (employees != null) {
				for (AssociationRef employeeAssoc: employees) {
					List<String> objects = new ArrayList<String>();
					objects.add(employeeAssoc.getTargetRef().toString());
					try {
						businessJournalService.log(nodeRef, EventCategory.DELETE, "#initiator удалил(а) подписку #mainobject для Сотрудника #object1", objects);
					} catch (Exception e) {
						logger.error("Could not create the record business-journal", e);
					}
				}
			}

			List<AssociationRef> organizationUnits = nodeService.getTargetAssocs(nodeRef, SubscriptionsService.ASSOC_DESTINATION_ORGANIZATION_UNIT);
			if (organizationUnits != null) {
				for (AssociationRef organizationUnitAssoc: organizationUnits) {
					List<String> objects = new ArrayList<String>();
					objects.add(organizationUnitAssoc.getTargetRef().toString());
					try {
						businessJournalService.log(nodeRef, EventCategory.DELETE, "#initiator удалил(а) подписку #mainobject для подразделения #object1", objects);
					} catch (Exception e) {
						logger.error("Could not create the record business-journal", e);
					}
				}
			}

			List<AssociationRef> workGroups = nodeService.getTargetAssocs(nodeRef, SubscriptionsService.ASSOC_DESTINATION_WORK_GROUP);
			if (workGroups != null) {
				for (AssociationRef workGroupAssoc: workGroups) {
					List<String> objects = new ArrayList<String>();
					objects.add(workGroupAssoc.getTargetRef().toString());
					try {
						businessJournalService.log(nodeRef, EventCategory.DELETE, "#initiator удалил(а) подписку #mainobject для рабочей группы #object1", objects);
					} catch (Exception e) {
						logger.error("Could not create the record business-journal", e);
					}
				}
			}

			List<AssociationRef> businessRoles = nodeService.getTargetAssocs(nodeRef, SubscriptionsService.ASSOC_DESTINATION_BUSINESS_ROLE);
			if (businessRoles != null) {
				for (AssociationRef businessRoleAssoc: businessRoles) {
					List<String> objects = new ArrayList<String>();
					objects.add(businessRoleAssoc.getTargetRef().toString());
					try {
						businessJournalService.log(nodeRef, EventCategory.DELETE, "#initiator удалил(а) подписку #mainobject для бизнес-роли #object1", objects);
					} catch (Exception e) {
						logger.error("Could not create the record business-journal", e);
					}
				}
			}
		}
	}

	@Override
	public void onUpdateProperties(NodeRef nodeRef, Map<QName, Serializable> before, Map<QName, Serializable> after) {
		if ( nodeService.exists(nodeRef) && (before.size() == after.size()) ) {
			List<AssociationRef> employees = nodeService.getTargetAssocs(nodeRef, SubscriptionsService.ASSOC_DESTINATION_EMPLOYEE);
			if (employees != null) {
				for (AssociationRef employeeAssoc: employees) {
					List<String> objects = new ArrayList<String>();
					objects.add(employeeAssoc.getTargetRef().toString());
					try {
						businessJournalService.log(nodeRef, EventCategory.EDIT, "#initiator внёс(ла) изменения в подписку #mainobject для Сотрудника #object1", objects);
					} catch (Exception e) {
						logger.error("Could not create the record business-journal", e);
					}
				}
			}

			List<AssociationRef> organizationUnits = nodeService.getTargetAssocs(nodeRef, SubscriptionsService.ASSOC_DESTINATION_ORGANIZATION_UNIT);
			if (organizationUnits != null) {
				for (AssociationRef organizationUnitAssoc: organizationUnits) {
					List<String> objects = new ArrayList<String>();
					objects.add(organizationUnitAssoc.getTargetRef().toString());
					try {
						businessJournalService.log(nodeRef, EventCategory.EDIT, "#initiator внёс(ла) изменения в подписку #mainobject для подразделения #object1", objects);
					} catch (Exception e) {
						logger.error("Could not create the record business-journal", e);
					}
				}
			}

			List<AssociationRef> workGroups = nodeService.getTargetAssocs(nodeRef, SubscriptionsService.ASSOC_DESTINATION_WORK_GROUP);
			if (workGroups != null) {
				for (AssociationRef workGroupAssoc: workGroups) {
					List<String> objects = new ArrayList<String>();
					objects.add(workGroupAssoc.getTargetRef().toString());
					try {
						businessJournalService.log(nodeRef, EventCategory.EDIT, "#initiator внёс(ла) изменения в подписку #mainobject для рабочей группы #object1", objects);
					} catch (Exception e) {
						logger.error("Could not create the record business-journal", e);
					}
				}
			}

			List<AssociationRef> businessRoles = nodeService.getTargetAssocs(nodeRef, SubscriptionsService.ASSOC_DESTINATION_BUSINESS_ROLE);
			if (businessRoles != null) {
				for (AssociationRef businessRoleAssoc: businessRoles) {
					List<String> objects = new ArrayList<String>();
					objects.add(businessRoleAssoc.getTargetRef().toString());
					try {
						businessJournalService.log(nodeRef, EventCategory.EDIT, "#initiator внёс(ла) изменения в подписку #mainobject для бизнес-роли #object1", objects);
					} catch (Exception e) {
						logger.error("Could not create the record business-journal", e);
					}
				}
			}
		}
	}
}
