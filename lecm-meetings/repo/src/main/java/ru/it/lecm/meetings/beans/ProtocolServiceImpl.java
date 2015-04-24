package ru.it.lecm.meetings.beans;

import java.util.Arrays;
import java.util.List;
import org.alfresco.model.ContentModel;
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
	
}
