
package ru.it.lecm.nd;

import java.util.List;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.nd.api.NDDocumentService;
import ru.it.lecm.ord.api.ORDModel;
import ru.it.lecm.statemachine.StatemachineModel;

/**
 *
 * @author snovikov
 */
public class NDDocumentServiceImpl extends BaseBean implements NDDocumentService {

	@Override
	public NodeRef getServiceRootFolder() {
		return null;
	}

	/**
	 * Check ND has association with ORD in undeleted status
	 * @param nd  ND document for check
	 * @return check result
	 */
	@Override
	public boolean checkApprovalORDExists(NodeRef nd){
		List<AssociationRef> ordRefs = nodeService.getSourceAssocs(nd, ORDModel.ASSOC_ORD_ACCEPT);
		for (AssociationRef ordRef : ordRefs){
			NodeRef ord = ordRef.getSourceRef();
			String status = (String) nodeService.getProperty(ord, StatemachineModel.PROP_STATUS);
			if (!ORDModel.STATUSES.get(ORDModel.ORD_STATUSES.DELETED_STATUS).equals(status)){
				return true;
			}
		}
		return false;
	}

}
