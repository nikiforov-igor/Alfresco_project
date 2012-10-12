package ru.it.lecm.base.workflow.policy;

import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.PropertyCheck;
import ru.it.lecm.base.workflow.WorkflowHelper;

import java.io.Serializable;
import java.util.Map;

/**
 * User: PMelnikov
 * Date: 05.10.12
 * Time: 11:30
 */
public class WorkflowPolicy implements NodeServicePolicies.OnUpdatePropertiesPolicy {

    public final static QName WORKFLOW_DOCUMENT_TASK_ASPECT = QName.createQName("http://www.it.ru/logicECM/workflow/1.0", "documentTask");
    public final static QName WORKFLOW_DOCUMENT_TASK_STATE_PROCESS_PROPERTY = QName.createQName("http://www.it.ru/logicECM/workflow/1.0", "stateProcess");
    //private final static QName TYPE_CONTENT = QName.createQName("http://www.it.ru/lecm/document/sample/1.0", "fieldset");

    private PolicyComponent policyComponent;
    private NodeService nodeService;

    public final void init() {
        PropertyCheck.mandatory(this, "nodeService", nodeService);
        PropertyCheck.mandatory(this, "policyComponent", policyComponent);

/*
        policyComponent.bindClassBehaviour(NodeServicePolicies.OnUpdatePropertiesPolicy.QNAME,
                TYPE_CONTENT, new JavaBehaviour(this, "onUpdateProperties"));
*/
    }

    @Override
    public void onUpdateProperties(NodeRef nodeRef, Map<QName, Serializable> before, Map<QName, Serializable> after) {
        if (nodeService.hasAspect(nodeRef, WORKFLOW_DOCUMENT_TASK_ASPECT)) {
            new WorkflowHelper().stopDocumentProcessing((String) nodeService.getProperty(nodeRef, WORKFLOW_DOCUMENT_TASK_STATE_PROCESS_PROPERTY));
        }
    }

    public void setPolicyComponent(PolicyComponent policyComponent) {
        this.policyComponent = policyComponent;
    }

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

}