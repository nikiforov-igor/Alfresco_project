package ru.it.lecm.ord;

import java.util.Arrays;
import java.util.List;
import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.dictionary.beans.DictionaryBean;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.ord.api.ORDDocumentService;
import ru.it.lecm.ord.api.ORDModel;

/**
 *
 * @author dbayandin
 */
public class ORDDocumentServiceImpl extends BaseBean implements ORDDocumentService{

	private DictionaryBean lecmDictionaryService;
	private DocumentService documentService;


	public void setLecmDictionaryService(DictionaryBean lecmDictionaryService) {
		this.lecmDictionaryService = lecmDictionaryService;
	}

	@Override
	public String getDocumentURL(final NodeRef documentRef) {
		String presentString = (String) nodeService.getProperty(documentRef, DocumentService.PROP_PRESENT_STRING);
		return wrapperLink(documentRef, presentString, documentService.getDocumentUrl(documentRef));
	}

	@Override
	public NodeRef getServiceRootFolder() {
		return null;
	}

	@Override
	public void changePointStatus(NodeRef point, ORDModel.P_STATUSES statusKey){
		String status = (String) ORDModel.POINT_STATUSES.get(statusKey);
		if (null != status){
			NodeRef newPointStatus = lecmDictionaryService.getDictionaryValueByParam(ORDModel.ORD_POINT_DICTIONARY_NAME, ContentModel.PROP_NAME, status);
			List<NodeRef> targetStatus = Arrays.asList(newPointStatus);
			nodeService.setAssociations(point, ORDModel.ASSOC_ORD_TABLE_ITEM_STATUS, targetStatus);
		}
	}

	@Override
	public NodeRef getErrandLinkedPoint(NodeRef errand){
		List<AssociationRef> pointAssocs = nodeService.getSourceAssocs(errand, ORDModel.ASSOC_ORD_TABLE_ERRAND);
		if (pointAssocs.size()>0){
			return pointAssocs.get(0).getSourceRef();
		}
		return null;
	}

	@Override
	public String getPointStatus(NodeRef point){
		List<AssociationRef> statusAssocs = nodeService.getTargetAssocs(point, ORDModel.ASSOC_ORD_TABLE_ITEM_STATUS);
		if (statusAssocs.size()>0){
			NodeRef status =  statusAssocs.get(0).getTargetRef();
			String statusName = (String) nodeService.getProperty(status, ContentModel.PROP_NAME);
			return statusName;
		}
		return null;
	}

	@Override
	public Boolean checkPointStatus(NodeRef point, ORDModel.P_STATUSES statusKey){
		String status = getPointStatus(point);
		if (null != status){
			if ( ORDModel.POINT_STATUSES.get(statusKey).equals(status) ){
				return true;
			}
			else{
				return false;
			}
		}
		return false;
	}

	public void setDocumentService(DocumentService documentService) {
		this.documentService = documentService;
	}
}
