package ru.it.lecm.delegation.policies;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.node.NodeServicePolicies.OnUpdateNodePolicy;
import org.alfresco.repo.policy.Behaviour;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.util.PropertyCheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.delegation.IDelegation;
import ru.it.lecm.delegation.beans.DelegationBean;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

import java.io.Serializable;

/**
 * Policy которая обеспечивает автоматическое создание параметров делегирования
 * для сотрудников
 *
 * @author VLadimir Malygin
 * @since 12.12.2012 12:21:40
 * @see <p>mailto: <a href="mailto:vmalygin@it.ru">vmalygin@it.ru</a></p>
 */
//BeforeDeleteNodePolicy OnRestoreNodePolicy пока не буду реализовывать, потому что непонятно а надо ли
public class DelegationOptsPolicy implements OnUpdateNodePolicy {

    private final static Logger logger = LoggerFactory.getLogger(DelegationOptsPolicy.class);

    private PolicyComponent policyComponent;
    private IDelegation delegationService;
    private NodeService nodeService;

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    public final void init() {
        PropertyCheck.mandatory(this, "policyComponent", policyComponent);

        policyComponent.bindClassBehaviour(OnUpdateNodePolicy.QNAME, OrgstructureBean.TYPE_EMPLOYEE, new JavaBehaviour(this, "onUpdateNode", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));
    }

    public void setPolicyComponent(PolicyComponent policyComponent) {
        this.policyComponent = policyComponent;
    }

    public void setDelegationService(IDelegation delegationService) {
        this.delegationService = delegationService;
    }

    @Override
    //TODO здесь проверяются и создаются настройки делегирования. может быть делать это onCreate, а не onUpdateNode
    public void onUpdateNode(final NodeRef nodeRef) {
        // nodeRef это employeeRef

        Serializable employeeName = nodeService.getProperty(nodeRef, ContentModel.PROP_NAME);
        Serializable employeeFirstName = nodeService.getProperty(nodeRef, OrgstructureBean.PROP_EMPLOYEE_LAST_NAME);

        if (Boolean.FALSE.equals(nodeService.getProperty(nodeRef, BaseBean.IS_ACTIVE))) {
            NodeRef delegationOpts = delegationService.getDelegationOpts(nodeRef);
            if (null != delegationOpts) {
                delegationService.stopDelegation(nodeRef);
                nodeService.setProperty(delegationOpts, DelegationBean.PROP_IS_OWNER_EMPLOYEE_EXISTS, false);
            }
        }

        if (employeeName == null || employeeFirstName == null || employeeName.toString().length() == 0 || employeeFirstName.toString().length() == 0) {
            return;
        }

        if (employeeName.toString().contains(employeeFirstName.toString())) {
            if (null == delegationService.getDelegationOpts(nodeRef)) {
                delegationService.createDelegationOpts(nodeRef);
            }
            logger.debug("employee with nodeRef '{}' successfully updated", nodeRef);
        } else {
            logger.warn("employee with name '{}'  and first name '{}' not updated", employeeName.toString(), employeeFirstName.toString());
        }

        if (Boolean.TRUE.equals(nodeService.getProperty(nodeRef, BaseBean.IS_ACTIVE))) {
            NodeRef delegationOpts = delegationService.getDelegationOpts(nodeRef);
            if (null != delegationOpts) {
                nodeService.setProperty(delegationOpts, DelegationBean.PROP_IS_OWNER_EMPLOYEE_EXISTS, true);
            }
        }
    }
}
