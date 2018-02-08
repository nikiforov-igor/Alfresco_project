
package ru.it.lecm.nd;

import java.util.EnumMap;
import java.util.List;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.eds.api.EDSDocumentService;
import ru.it.lecm.nd.api.NDDocumentService;
import ru.it.lecm.nd.api.NDModel;
import ru.it.lecm.ord.api.ORDDocumentService;
import ru.it.lecm.ord.api.ORDModel;
import ru.it.lecm.statemachine.StatemachineModel;

/**
 *
 * @author snovikov
 */
public class NDDocumentServiceImpl extends BaseBean implements NDDocumentService {

	private EnumMap<NDModel.ND_STATUS,String> ndStatusesMap;

	private ORDDocumentService ordDocumentService;

	public void setOrdDocumentService(ORDDocumentService ordDocumentService) {
		this.ordDocumentService = ordDocumentService;
	}

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
			if (!ordDocumentService.getOrdStatusName(ORDModel.ORD_STATUS.DELETED).equals(status)){
				return true;
			}
		}
		return false;
	}

	@Override
	protected void initServiceImpl() {
		ndStatusesMap = new EnumMap<NDModel.ND_STATUS,String>(NDModel.ND_STATUS.class){{
			put(NDModel.ND_STATUS.ACTIVE, EDSDocumentService.getFromMessagesOrDefaultValue("lecm.nd.statemachine-status.active", "Действует"));
			put(NDModel.ND_STATUS.CANCELED, EDSDocumentService.getFromMessagesOrDefaultValue("lecm.nd.statemachine-status.cancelled", "Отменен"));
			put(NDModel.ND_STATUS.DELETED, EDSDocumentService.getFromMessagesOrDefaultValue("lecm.nd.statemachine-status.removed", "Удален"));
			put(NDModel.ND_STATUS.OUT_OF_DATE, EDSDocumentService.getFromMessagesOrDefaultValue("lecm.nd.statemachine-status.out-of-date", "Срок действия окончен"));
			put(NDModel.ND_STATUS.PUT_IN_WORK, EDSDocumentService.getFromMessagesOrDefaultValue("lecm.nd.statemachine-status.put-in-work", "Введен в действие"));
		}};
	}

	@Override
	public String getNDStatusName(NDModel.ND_STATUS code) {
		return ndStatusesMap != null ? ndStatusesMap.get(code) : null;
	}
}
