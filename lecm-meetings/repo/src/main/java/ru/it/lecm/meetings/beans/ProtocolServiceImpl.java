package ru.it.lecm.meetings.beans;

import java.util.Arrays;
import java.util.List;
import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.dictionary.beans.DictionaryBean;

/**
 *
 * @author snovikov
 */
public class ProtocolServiceImpl extends BaseBean implements ProtocolService {

	private DictionaryBean lecmDictionaryService;
	
	public void setLecmDictionaryService(DictionaryBean lecmDictionaryService) {
		this.lecmDictionaryService = lecmDictionaryService;
	}
		
	@Override
	public NodeRef getServiceRootFolder() {
		return null;
	}
	
	@Override
	public void changePointStatus(NodeRef point, ProtocolService.P_STATUSES statusKey){
		String status = ProtocolService.POINT_STATUSES.get(statusKey);
		if (null != status){
			NodeRef newPointStatus = lecmDictionaryService.getDictionaryValueByParam(ProtocolService.PROTOCOL_POINT_DICTIONARY_NAME, ContentModel.PROP_NAME, status);
			List<NodeRef> targetStatus = Arrays.asList(newPointStatus);
			nodeService.setAssociations(point, ProtocolService.ASSOC_PROTOCOL_POINT_STATUS, targetStatus);
		}
	}
	
	@Override
	public NodeRef getErrandLinkedPoint(NodeRef errand){
		List<AssociationRef> pointAssocs = nodeService.getSourceAssocs(errand, ProtocolService.ASSOC_PROTOCOL_POINT_ERRAND);
		if (pointAssocs.size()>0){
			return pointAssocs.get(0).getSourceRef();
		}
		return null;
	}
	
	@Override
	public String getPointStatus(NodeRef point){
		List<AssociationRef> statusAssocs = nodeService.getTargetAssocs(point, ProtocolService.ASSOC_PROTOCOL_POINT_STATUS);
		if (statusAssocs.size()>0){
			NodeRef status =  statusAssocs.get(0).getTargetRef();
			String statusName = (String) nodeService.getProperty(status, ContentModel.PROP_NAME);
			return statusName;
		}
		return null;
	}
	
	@Override
	public Boolean checkPointStatus(NodeRef point, ProtocolService.P_STATUSES statusKey){
		String status = getPointStatus(point);
		if (null != status){
			if ( ProtocolService.POINT_STATUSES.get(statusKey).equals(status) ){
				return true;
			}
			else{
				return false;
			}
		}
		return false;
	}
	
}
