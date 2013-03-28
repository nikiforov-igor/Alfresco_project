package ru.it.lecm.orgstructure.policies;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.Behaviour;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * User: dbashmakov
 * Date: 28.03.13
 * Time: 11:17
 */
public class OrgstructureGenerateNamesPolicy extends BaseBean
        implements NodeServicePolicies.OnCreateNodePolicy, NodeServicePolicies.OnUpdatePropertiesPolicy {

    private PolicyComponent policyComponent;

    private final Map<QName, Integer> TYPES = new HashMap<QName, Integer>() {{
        put(OrgstructureBean.TYPE_ORGANIZATION_UNIT, 1);
        put(OrgstructureBean.TYPE_WORK_GROUP, 2);
        put(OrgstructureBean.TYPE_EMPLOYEE, 3);
    }};

    private final Map<QName, QName[]> AFFECTED_PROPS = new HashMap<QName, QName[]>() {{
        put(OrgstructureBean.TYPE_ORGANIZATION_UNIT, new QName[]{OrgstructureBean.PROP_ORG_ELEMENT_FULL_NAME});
        put(OrgstructureBean.TYPE_EMPLOYEE, new QName[]{OrgstructureBean.PROP_EMPLOYEE_FIRST_NAME, OrgstructureBean.PROP_EMPLOYEE_MIDDLE_NAME, OrgstructureBean.PROP_EMPLOYEE_LAST_NAME});
        put(OrgstructureBean.TYPE_WORK_GROUP, new QName[]{OrgstructureBean.PROP_ORG_ELEMENT_FULL_NAME});
    }};

    public void setPolicyComponent(PolicyComponent policyComponent) {
        this.policyComponent = policyComponent;
    }

    public final void init() {
        policyComponent.bindClassBehaviour(NodeServicePolicies.OnCreateNodePolicy.QNAME,
                OrgstructureBean.TYPE_EMPLOYEE, new JavaBehaviour(this, "onCreateNode", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));
        policyComponent.bindClassBehaviour(NodeServicePolicies.OnUpdatePropertiesPolicy.QNAME,
                OrgstructureBean.TYPE_EMPLOYEE, new JavaBehaviour(this, "onUpdateProperties", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));

        policyComponent.bindClassBehaviour(NodeServicePolicies.OnCreateNodePolicy.QNAME,
                OrgstructureBean.TYPE_ORGANIZATION_UNIT, new JavaBehaviour(this, "onCreateNode", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));
        policyComponent.bindClassBehaviour(NodeServicePolicies.OnUpdatePropertiesPolicy.QNAME,
                OrgstructureBean.TYPE_ORGANIZATION_UNIT, new JavaBehaviour(this, "onUpdateProperties", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));

        policyComponent.bindClassBehaviour(NodeServicePolicies.OnCreateNodePolicy.QNAME,
                OrgstructureBean.TYPE_WORK_GROUP, new JavaBehaviour(this, "onCreateNode", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));
        policyComponent.bindClassBehaviour(NodeServicePolicies.OnUpdatePropertiesPolicy.QNAME,
                OrgstructureBean.TYPE_WORK_GROUP, new JavaBehaviour(this, "onUpdateProperties", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));
    }

    @Override
    public void onCreateNode(ChildAssociationRef childAssocRef) {
        NodeRef object = childAssocRef.getChildRef();
        NodeRef parent = childAssocRef.getParentRef();
        QName type = nodeService.getType(object);
        String newNodeName = null;
        switch (TYPES.get(type)) {
            case 1: {
                newNodeName = generateOrgElementName(parent, object);
            }
            break;
            case 2: {
                newNodeName = generateOrgElementName(parent, object);
            }
            break;
            case 3: {
                newNodeName = generateEmployeeName(parent, object);
            }
        }
        if (newNodeName != null && !newNodeName.isEmpty()) {
            nodeService.setProperty(object, ContentModel.PROP_NAME, newNodeName);
        }
    }

    private String generateEmployeeName(NodeRef parent, NodeRef employee) {
        //lecm-orgstr_employee-last-name lecm-orgstr_employee-first-name[1]. lecm-orgstr_employee-middle-name[1]"
        String firstName = (String) nodeService.getProperty(employee, OrgstructureBean.PROP_EMPLOYEE_FIRST_NAME);
        String middleName = (String) nodeService.getProperty(employee, OrgstructureBean.PROP_EMPLOYEE_MIDDLE_NAME);
        String lastName = (String) nodeService.getProperty(employee, OrgstructureBean.PROP_EMPLOYEE_LAST_NAME);
        String newName = lastName + " " + firstName.charAt(0) + "." + middleName.charAt(0);
        return getUniqueNodeName(parent, newName);
    }

    private String generateOrgElementName(NodeRef parent, NodeRef element) {
        String newName = (String) nodeService.getProperty(element, OrgstructureBean.PROP_ORG_ELEMENT_FULL_NAME);
        return getUniqueNodeName(parent, newName);
    }

    private String getUniqueNodeName(NodeRef parent, String newName) {
        boolean exist = nodeService.getChildByName(parent, ContentModel.ASSOC_CONTAINS, newName) != null;
        int count = 0;
        while (exist) {
            count++;
            String countName = newName + "_" + count;
            exist = nodeService.getChildByName(parent, ContentModel.ASSOC_CONTAINS, countName) != null;
            if (!exist) {
                newName = countName;
            }
        }
        return newName.replaceAll("[*\'\"]", "").replaceAll("\\.$", "").trim();
    }

    @Override
    public void onUpdateProperties(NodeRef nodeRef, Map<QName, Serializable> before, Map<QName, Serializable> after) {
        if (before.size() == after.size() && hasChangedProperties(nodeRef, before, after)) {
            onCreateNode(nodeService.getPrimaryParent(nodeRef));
        }
    }

    private boolean hasChangedProperties(NodeRef object, Map<QName, Serializable> before, Map<QName, Serializable> after) {
        boolean result = false;
        QName type = nodeService.getType(object);
        QName[] affected = AFFECTED_PROPS.get(type);
        for (QName prop : affected) {
            Object prev = before.get(prop);
            Object cur = after.get(prop);
            if (cur != null && !cur.equals(prev)) {
                result = true;
                break;
            }
        }
        return result;
    }
}
