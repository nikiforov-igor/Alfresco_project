package ru.it.lecm.orgstructure.policies;

import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import ru.it.lecm.orgstructure.beans.OrgstructureAspectsModel;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

/**
 * User: dbashmakov
 * Date: 23.07.2014
 * Time: 12:02
 */
public class OrgstructureEmployeeOrganizationPolicy extends SecurityJournalizedPolicyBase
        implements NodeServicePolicies.OnCreateAssociationPolicy, NodeServicePolicies.OnDeleteAssociationPolicy {

    @Override
    public void init() {
        super.init();
        policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnCreateAssociationPolicy.QNAME,
                OrgstructureBean.TYPE_EMPLOYEE, OrgstructureAspectsModel.ASSOC_LINKED_ORGANIZATION,
                new JavaBehaviour(this, "onCreateAssociation"));
        policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnDeleteAssociationPolicy.QNAME,
                OrgstructureBean.TYPE_EMPLOYEE, OrgstructureAspectsModel.ASSOC_LINKED_ORGANIZATION,
                new JavaBehaviour(this, "onDeleteAssociation"));
    }


    @Override
    public void onCreateAssociation(AssociationRef nodeAssocRef) {
        NodeRef employee = nodeAssocRef.getSourceRef();
        NodeRef organization = nodeAssocRef.getTargetRef();
        String userName = orgstructureService.getEmployeeLogin(employee);
        if (userName != null) {
            orgstructureService.getUserOrganizationsCache().put(userName, organization);
        }
    }

    @Override
    public void onDeleteAssociation(AssociationRef nodeAssocRef) {
        // очистка кеша Пользователь-Организация
        NodeRef employee = nodeAssocRef.getSourceRef();
        String userName = orgstructureService.getEmployeeLogin(employee);
        if (userName != null) {
            if (orgstructureService.getUserOrganizationsCache().contains(userName) && orgstructureService.getUserOrganizationsCache().get(userName) != null) {
                orgstructureService.getUserOrganizationsCache().remove(userName);
            }
        }
    }
}
