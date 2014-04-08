package ru.it.lecm.delegation.policies;

import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.Behaviour;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.util.PropertyCheck;
import ru.it.lecm.base.beans.LecmBaseException;
import ru.it.lecm.base.beans.LecmBasePropertiesService;
import ru.it.lecm.delegation.IDelegation;

/**
 * User: pmelnikov
 * Date: 08.04.14
 * Time: 9:19
 */
public class UpdateProcuracyPolicy implements NodeServicePolicies.OnUpdateNodePolicy {

    private PolicyComponent policyComponent;
    private LecmBasePropertiesService propertiesService;

    public final void init () {
        PropertyCheck.mandatory(this, "policyComponent", policyComponent);

        policyComponent.bindClassBehaviour (NodeServicePolicies.OnUpdateNodePolicy.QNAME, IDelegation.TYPE_DELEGATION_OPTS, new JavaBehaviour(this, "onUpdateNode", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));
        policyComponent.bindClassBehaviour (NodeServicePolicies.OnUpdateNodePolicy.QNAME, IDelegation.TYPE_DELEGATION_OPTS_CONTAINER, new JavaBehaviour(this, "onUpdateNode", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));
        policyComponent.bindClassBehaviour (NodeServicePolicies.OnUpdateNodePolicy.QNAME, IDelegation.TYPE_PROCURACY, new JavaBehaviour(this, "onUpdateNode", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));
        policyComponent.bindClassBehaviour (NodeServicePolicies.OnUpdateNodePolicy.QNAME, IDelegation.TYPE_TASK_DELEGATION, new JavaBehaviour(this, "onUpdateNode", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));
    }

    public void setPolicyComponent (PolicyComponent policyComponent) {
        this.policyComponent = policyComponent;
    }

    @Override
    public void onUpdateNode (final NodeRef nodeRef) {
        try {
            Object editorEnabled = propertiesService.getProperty("ru.it.lecm.properties.delegation.editor.enabled");
            boolean enabled;
            if (editorEnabled == null) {
                enabled = true;
            } else {
                enabled = Boolean.valueOf((String) editorEnabled);
            }

            if (!enabled) {
                throw new IllegalStateException("Cannot read delegation properties");
            }
        } catch (LecmBaseException e) {
            throw new IllegalStateException("Cannot read delegation properties");
        }
    }

    public void setPropertiesService(LecmBasePropertiesService propertiesService) {
        this.propertiesService = propertiesService;
    }
}
