package ru.it.lecm.contractors.policies;

import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.Behaviour.NotificationFrequency;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import org.apache.commons.lang.StringUtils;
import ru.it.lecm.base.policies.LogicECMAssociationPolicy;
import ru.it.lecm.contractors.api.Contractors;

import java.io.Serializable;
import java.util.Map;


public class PhysicalPersonPolicy  extends LogicECMAssociationPolicy implements NodeServicePolicies.OnUpdatePropertiesPolicy{

    public final void init() {
        super.init();

        policyComponent.bindClassBehaviour(NodeServicePolicies.OnUpdatePropertiesPolicy.QNAME,
                Contractors.TYPE_PHYSICAL_PERSON, new JavaBehaviour(this, "onUpdateProperties", NotificationFrequency.TRANSACTION_COMMIT));

        policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnDeleteAssociationPolicy.QNAME,
                Contractors.TYPE_PHYSICAL_PERSON, new JavaBehaviour(this, "onDeleteAssociation"));

        policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnCreateAssociationPolicy.QNAME,
                Contractors.TYPE_PHYSICAL_PERSON, new JavaBehaviour(this, "onCreateAssociation"));
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
