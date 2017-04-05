package ru.it.lecm.ord;

import java.util.*;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.dictionary.beans.DictionaryBean;
import ru.it.lecm.documents.beans.DocumentService;
import ru.it.lecm.documents.beans.DocumentTableService;
import ru.it.lecm.eds.api.EDSDocumentService;
import ru.it.lecm.ord.api.ORDDocumentService;
import ru.it.lecm.ord.api.ORDModel;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.workflow.review.api.ReviewService;

/**
 *
 * @author dbayandin
 */
public class ORDDocumentServiceImpl extends BaseBean implements ORDDocumentService{

	private DictionaryBean lecmDictionaryService;
	private DocumentService documentService;
	private DocumentTableService documentTableService;
    private OrgstructureBean orgstructureBean;

    public void setOrgstructureBean(OrgstructureBean orgstructureBean) {
        this.orgstructureBean = orgstructureBean;
    }

    public void setDocumentTableService(DocumentTableService documentTableService) {
		this.documentTableService = documentTableService;
	}

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
		String status = ORDModel.POINT_STATUSES.get(statusKey);
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

	@Override
	public Boolean haveNotPointsWithController(NodeRef document) {
		Boolean havePointsWithController = false;
		List<AssociationRef> ordControllerAssoc = nodeService.getTargetAssocs(document, ORDModel.ASSOC_ORD_CONTROLLER);
		if (ordControllerAssoc != null && ordControllerAssoc.size() != 0) {
			List<NodeRef> ordPoints = getOrdDocumentPoints(document);
			for (NodeRef point : ordPoints) {
				NodeRef pointController = null;
				List<AssociationRef> pointControllerAssocs = nodeService.getTargetAssocs(point, ORDModel.ASSOC_ORD_TABLE_CONTROLLER);
				if (pointControllerAssocs != null && pointControllerAssocs.size() > 0) {
					pointController = pointControllerAssocs.get(0).getTargetRef();
				}
				if (pointController != null) {
					havePointsWithController = true;
				}
			}
		}
		return !havePointsWithController && ordControllerAssoc != null && ordControllerAssoc.size() != 0;
	}

	@Override
	public Boolean haveNotPointsWithDueDate(NodeRef document) {
		Date executionDate = (Date) nodeService.getProperty(document, EDSDocumentService.PROP_EXECUTION_DATE);
		Boolean havePointsWithDueDate = false;
		if (executionDate != null) {
			List<NodeRef> ordPoints = getOrdDocumentPoints(document);
			for (NodeRef point : ordPoints) {
				String pointLimitDateRadio = (String) nodeService.getProperty(point, ORDModel.PROP_ORD_TABLE_ITEM_DATE_RADIO);
				if (!Objects.equals(pointLimitDateRadio, "LIMITLESS")) {
					havePointsWithDueDate = true;
				}
			}
		}
		return !havePointsWithDueDate && executionDate != null;
	}

	@Override
	public List<NodeRef> getOrdDocumentPoints(NodeRef document) {
		List<NodeRef> ordPoints = new ArrayList<>();
		NodeRef table = documentTableService.getTable(document, ORDModel.TYPE_ORD_ITEMS_TABLE);
		if (table != null) {
			ordPoints = documentTableService.getTableDataRows(table);
		}
		return ordPoints;
	}

	public Boolean isDocumentHavePointsAndProperties(NodeRef document) {
		Date executionDate = (Date) nodeService.getProperty(document, EDSDocumentService.PROP_EXECUTION_DATE);
		List<AssociationRef> ordControllerAssoc = nodeService.getTargetAssocs(document, ORDModel.ASSOC_ORD_CONTROLLER);
		List<NodeRef> points = getOrdDocumentPoints(document);
		return points.size() > 0 && (executionDate != null || (ordControllerAssoc != null && ordControllerAssoc.size() != 0));
	}

	public Boolean haveNotPointsWithDueDateAndController(NodeRef document) {
		return haveNotPointsWithDueDate(document) && haveNotPointsWithController(document);
	}

	public Boolean haveNotPointsWithControllerAndHaveDueDate(NodeRef document) {
		return haveNotPointsWithController(document) && !haveNotPointsWithDueDate(document);
	}

	public Boolean haveNotPointsWithDueDateAndHaveController(NodeRef document) {
		return haveNotPointsWithDueDate(document) && !haveNotPointsWithController(document);
	}

    @Override
    public Boolean currentUserIsReviewer(NodeRef document) {
        NodeRef table = documentTableService.getTable(document, ReviewService.TYPE_REVIEW_TS_REVIEW_TABLE);
        if (table != null) {
            List<NodeRef> reviewList = documentTableService.getTableDataRows(table);
            NodeRef currentEmployee = orgstructureBean.getCurrentEmployee();
            for (NodeRef reviewListRow : reviewList) {
                NodeRef itemEmployee = findNodeByAssociationRef(reviewListRow, ReviewService.ASSOC_REVIEW_TS_REVIEWER, OrgstructureBean.TYPE_EMPLOYEE, ASSOCIATION_TYPE.TARGET);
                if (currentEmployee.equals(itemEmployee)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void setDocumentService(DocumentService documentService) {
		this.documentService = documentService;
	}
}
