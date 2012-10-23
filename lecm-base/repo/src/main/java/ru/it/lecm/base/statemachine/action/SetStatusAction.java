package ru.it.lecm.base.statemachine.action;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.impl.persistence.entity.VariableInstanceEntity;
import org.activiti.engine.impl.util.xml.Element;
import org.alfresco.repo.workflow.activiti.ActivitiScriptNode;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;

import java.util.List;

/**
 * User: PMelnikov
 * Date: 17.10.12
 * Time: 14:53
 */
public class SetStatusAction extends StateMachineAction {

    private String status = "UNKNOWN";

    public SetStatusAction(List<Element> attributes) {
        Element attribute = attributes.get(0);
        String name = attribute.attribute("name");
        String value = attribute.attribute("value");
        if ("status".equalsIgnoreCase(name)) {
            status = value.toUpperCase();
        }
    }

    @Override
    public void execute(DelegateExecution execution) {
        NodeRef nodeRef = ((ActivitiScriptNode) execution.getVariable("bpm_package")).getNodeRef();
        NodeService nodeService = getServiceRegistry().getNodeService();
        List<ChildAssociationRef> children = nodeService.getChildAssocs(nodeRef);
        for (ChildAssociationRef child : children) {
            nodeService.setProperty(child.getChildRef(), QName.createQName("http://www.it.ru/logicECM/statemachine/1.0", "status"), status);
        }
    }

    public String getType() {
        return "setStatus";
    }

}
