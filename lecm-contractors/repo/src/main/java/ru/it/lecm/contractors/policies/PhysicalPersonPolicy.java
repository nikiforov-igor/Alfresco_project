package ru.it.lecm.contractors.policies;

import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.Behaviour.NotificationFrequency;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.PropertyCheck;
import org.apache.commons.lang.StringUtils;
import ru.it.lecm.contractors.api.Contractors;

import java.io.Serializable;
import java.util.Map;


public class PhysicalPersonPolicy implements NodeServicePolicies.OnUpdatePropertiesPolicy {
    private PolicyComponent policyComponent;
    private NodeService nodeService;

    public void setPolicyComponent(PolicyComponent policyComponent) {
        this.policyComponent = policyComponent;
    }

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;

    }

    public final void init() {
        PropertyCheck.mandatory(this, "policyComponent", policyComponent);
        PropertyCheck.mandatory(this, "nodeService", nodeService);

        policyComponent.bindClassBehaviour(NodeServicePolicies.OnUpdatePropertiesPolicy.QNAME,
                Contractors.TYPE_PHYSICAL_PERSON, new JavaBehaviour(this, "onUpdateProperties", NotificationFrequency.TRANSACTION_COMMIT));
    }

    @Override
    public void onUpdateProperties(NodeRef nodeRef, Map<QName, Serializable> before, Map<QName, Serializable> after) {

        String oldFullName = (String) before.get(Contractors.PROP_CONTRACTOR_FULLNAME);

        String lastName = (String) after.get(Contractors.PROP_PHYSICAL_PERSON_LAST_NAME);
        String firstName = (String) after.get(Contractors.PROP_PHYSICAL_PERSON_FIST_NAME);
        String middleName = (String) after.get(Contractors.PROP_PHYSICAL_PERSON_MIDDLE_NAME);

        if (StringUtils.isNotEmpty(lastName) && StringUtils.isNotEmpty(firstName)) {

            String newFullName = lastName + " " + firstName;
            String newShortName = lastName + " " + firstName.substring(0, 1) + ".";

            if (StringUtils.isNotEmpty(middleName)) {
                newFullName = newFullName + " " + middleName;
                newShortName = newShortName + " " + middleName.substring(0, 1) + ".";
            }

            if (oldFullName == null || !oldFullName.equals(newFullName)) {
                nodeService.setProperty(nodeRef, Contractors.PROP_CONTRACTOR_FULLNAME, newFullName);
                nodeService.setProperty(nodeRef, Contractors.PROP_CONTRACTOR_SHORTNAME, newShortName);
            }
        }

    }
}
