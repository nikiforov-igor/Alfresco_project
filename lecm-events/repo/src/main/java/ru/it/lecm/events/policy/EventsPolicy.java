package ru.it.lecm.events.policy;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.Behaviour;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.namespace.RegexQNamePattern;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.base.beans.WriteTransactionNeededException;
import ru.it.lecm.documents.beans.DocumentTableService;
import ru.it.lecm.events.beans.EventsService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * User: AIvkin
 * Date: 01.04.2015
 * Time: 9:50
 */
public class EventsPolicy extends BaseBean {
    private PolicyComponent policyComponent;
    private DocumentTableService documentTableService;

    public void setPolicyComponent(PolicyComponent policyComponent) {
        this.policyComponent = policyComponent;
    }

    public void setDocumentTableService(DocumentTableService documentTableService) {
        this.documentTableService = documentTableService;
    }

    @Override
    public NodeRef getServiceRootFolder() {
        return null;
    }

    public final void init() {
        policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnCreateAssociationPolicy.QNAME,
                EventsService.TYPE_EVENT, EventsService.ASSOC_EVENT_TEMP_MEMBERS,
                new JavaBehaviour(this, "onCreateAddMembers", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));

        policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnCreateAssociationPolicy.QNAME,
                EventsService.TYPE_EVENT, EventsService.ASSOC_EVENT_TEMP_RESOURCES,
                new JavaBehaviour(this, "onCreateAddResources", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));
    }

    public void onCreateAddMembers(AssociationRef nodeAssocRef) {
        //Мероприятие
        NodeRef event = nodeAssocRef.getSourceRef();
        //Участник
        NodeRef member = nodeAssocRef.getTargetRef();

        NodeRef tableDataRootFolder = documentTableService.getRootFolder(event);
        if (tableDataRootFolder != null) {
            Set<QName> typeSet = new HashSet<>(1);
            typeSet.add(EventsService.TYPE_EVENT_MEMBERS_TABLE);
            List<ChildAssociationRef> childAssocs = nodeService.getChildAssocs(tableDataRootFolder, typeSet);
            if (childAssocs != null && childAssocs.size() == 1) {
                NodeRef table = childAssocs.get(0).getChildRef();
                if (table != null) {
                    // создаем строку
                    try {
                        NodeRef createdNode = createNode(table, EventsService.TYPE_EVENT_MEMBERS_TABLE_ROW, null, null);
                        if (createdNode != null) {
                            nodeService.createAssociation(createdNode, member, EventsService.ASSOC_EVENT_MEMBERS_TABLE_EMPLOYEE);
                        }
                    } catch (WriteTransactionNeededException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        }
    }

    public void onCreateAddResources(AssociationRef nodeAssocRef) {
        //Мероприятие
        NodeRef event = nodeAssocRef.getSourceRef();
        //Участник
        NodeRef member = nodeAssocRef.getTargetRef();

        NodeRef tableDataRootFolder = documentTableService.getRootFolder(event);
        if (tableDataRootFolder != null) {
            Set<QName> typeSet = new HashSet<>(1);
            typeSet.add(EventsService.TYPE_EVENT_RESOURCES_TABLE);
            List<ChildAssociationRef> childAssocs = nodeService.getChildAssocs(tableDataRootFolder, typeSet);
            if (childAssocs != null && childAssocs.size() == 1) {
                NodeRef table = childAssocs.get(0).getChildRef();
                if (table != null) {
                    // создаем строку
                    try {
                        NodeRef createdNode = createNode(table, EventsService.TYPE_EVENT_RESOURCES_TABLE_ROW, null, null);
                        if (createdNode != null) {
                            nodeService.createAssociation(createdNode, member, EventsService.ASSOC_EVENT_RESOURCES_TABLE_RESOURCE);
                        }
                    } catch (WriteTransactionNeededException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        }
    }
}
