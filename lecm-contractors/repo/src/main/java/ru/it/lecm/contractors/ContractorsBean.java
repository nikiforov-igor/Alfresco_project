package ru.it.lecm.contractors;

import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.contractors.api.Contractors;
import java.util.List;

public class ContractorsBean extends BaseBean implements Contractors {

    private NodeService nodeService;

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    @Override
    public void assignAsPrimaryRepresentative(NodeRef representativeToAssignAsPrimary) {

        List<ChildAssociationRef> sourceRefs = nodeService.getParentAssocs(representativeToAssignAsPrimary);
        NodeRef contractor = sourceRefs.get(0).getParentRef(); // FIXME: Может работать неверно, если у Контрагента будет больше 1-ой ChildAssociationRef!

        List<ChildAssociationRef> representativesAssocs = nodeService.getChildAssocs(contractor);

        Boolean isPrimaryChanged  = false;
        Boolean isPrimaryAssigned = false;

        for (ChildAssociationRef representativesAssoc : representativesAssocs) {
            
            NodeRef representative = representativesAssoc.getChildRef();

            Boolean isPrimary = (Boolean) nodeService.getProperty(representative, QName.createQName("http://www.it.ru/lecm/contractors/model/contractor/1.0", "link-to-representative-association-is-primary"));

            if(isPrimary) {
                nodeService.setProperty(representative, QName.createQName("http://www.it.ru/lecm/contractors/model/contractor/1.0", "link-to-representative-association-is-primary"), false);
                isPrimaryChanged = true;
            }

            if (representativeToAssignAsPrimary.equals(representative)) {
                nodeService.setProperty(representative, QName.createQName("http://www.it.ru/lecm/contractors/model/contractor/1.0", "link-to-representative-association-is-primary"), true);
                isPrimaryAssigned = true;
            }

            if(isPrimaryChanged && isPrimaryAssigned)
                break;
        }
    }
}
