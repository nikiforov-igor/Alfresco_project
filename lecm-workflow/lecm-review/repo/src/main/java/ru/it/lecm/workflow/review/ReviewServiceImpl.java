package ru.it.lecm.workflow.review;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.base.beans.WriteTransactionNeededException;
import ru.it.lecm.documents.beans.DocumentTableService;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

/**
 *
 * @author vkuprin
 */
public class ReviewServiceImpl extends BaseBean {

	public static final String CONSTRAINT_REVIEW_TS_STATE_IN_PROCESS = "NOT_REVIEWED";
	public static final String CONSTRAINT_REVIEW_TS_STATE_REVIEWED = "REVIEWED";
	public static final String CONSTRAINT_REVIEW_TS_STATE_NOT_STARTED = "NOT_STARTED";
	public static final String CONSTRAINT_REVIEW_TS_STATE_CANCELLED = "CANCELLED";

	public static final String REVIEW_TS_NAMESPACE = "http://www.it.ru/logicECM/model/review-ts/1.0";
	public static final String REVIEW_LIST_NAMESPACE = "http://www.it.ru/logicECM/model/review-list/1.0";
	public static final QName ASSOC_REVIEW_TS_REVIEW_TABLE = QName.createQName(REVIEW_TS_NAMESPACE, "review-table-assoc");
	public static final QName ASSOC_REVIEW_TS_REVIEWER = QName.createQName(REVIEW_TS_NAMESPACE, "reviewer-assoc");
	public static final QName ASSOC_REVIEW_TS_INITIATOR = QName.createQName(REVIEW_TS_NAMESPACE, "initiator-assoc");
	public static final QName TYPE_REVIEW_TS_REVIEW_TABLE = QName.createQName(REVIEW_TS_NAMESPACE, "review-table");
	public static final QName TYPE_REVIEW_TS_REVIEW_TABLE_ITEM = QName.createQName(REVIEW_TS_NAMESPACE, "review-table-item");
	public static final QName TYPE_REVIEW_LIST_REWIEW_LIST_ITEM = QName.createQName(REVIEW_LIST_NAMESPACE, "review-list-item");
	public static final QName ASSOC_REVIEW_LIST_REWIEWER = QName.createQName(REVIEW_LIST_NAMESPACE, "reviewer-assoc");
	public static final QName PROP_REVIEW_TS_STATE = QName.createQName(REVIEW_TS_NAMESPACE, "review-state");
	public static final QName PROP_REVIEW_TS_REVIEW_FINISH_DATE = QName.createQName(REVIEW_TS_NAMESPACE, "review-finish-date");
	public static final QName PROP_REVIEW_TS_ACTIVE_REVIEWERS = QName.createQName(REVIEW_TS_NAMESPACE, "active-reviewers");

	private DocumentTableService documentTableService;
	private OrgstructureBean orgstructureBean;
	private SearchService searchService;

	public SearchService getSearchService() {
		return searchService;
	}

	public void setSearchService(SearchService searchService) {
		this.searchService = searchService;
	}

	public OrgstructureBean getOrgstructureBean() {
		return orgstructureBean;
	}

	public void setOrgstructureBean(OrgstructureBean orgstructureBean) {
		this.orgstructureBean = orgstructureBean;
	}

	public DocumentTableService getDocumentTableService() {
		return documentTableService;
	}

	public void setDocumentTableService(DocumentTableService documentTableService) {
		this.documentTableService = documentTableService;
	}

	@Override
	public NodeRef getServiceRootFolder() {
		return null;
	}

	public Boolean needReviewByCurrentUser(NodeRef document) {
		NodeRef tableData = findNodeByAssociationRef(document, ASSOC_REVIEW_TS_REVIEW_TABLE, TYPE_REVIEW_TS_REVIEW_TABLE, ASSOCIATION_TYPE.TARGET);
		Boolean result = false;
		if (null != tableData) {
			List<NodeRef> reviewList = documentTableService.getTableDataRows(tableData);
			NodeRef currentEmployee = orgstructureBean.getCurrentEmployee();
			for (NodeRef reviewListRow : reviewList) {
				NodeRef itemEmployee = findNodeByAssociationRef(reviewListRow, ASSOC_REVIEW_TS_REVIEWER, OrgstructureBean.TYPE_EMPLOYEE, ASSOCIATION_TYPE.TARGET);
				if (currentEmployee.equals(itemEmployee)) {
					String state = (String) nodeService.getProperty(reviewListRow, PROP_REVIEW_TS_STATE);
					result = result || CONSTRAINT_REVIEW_TS_STATE_IN_PROCESS.equals(state);
				}
			}
		}
		return result;
	}

	public List<NodeRef> getActiveReviewersForDocument(NodeRef document) {
		NodeRef tableData = findNodeByAssociationRef(document, ASSOC_REVIEW_TS_REVIEW_TABLE, TYPE_REVIEW_TS_REVIEW_TABLE, ASSOCIATION_TYPE.TARGET);
		List<NodeRef> result = new ArrayList<>();
		if (null != tableData) {
			List<NodeRef> reviewList = documentTableService.getTableDataRows(tableData);
			for (NodeRef reviewListRow : reviewList) {
				NodeRef itemEmployee = findNodeByAssociationRef(reviewListRow, ASSOC_REVIEW_TS_REVIEWER, OrgstructureBean.TYPE_EMPLOYEE, ASSOCIATION_TYPE.TARGET);
				String state = (String) nodeService.getProperty(reviewListRow, PROP_REVIEW_TS_STATE);
				if (CONSTRAINT_REVIEW_TS_STATE_IN_PROCESS.equals(state)) {
					result.add(itemEmployee);
				}
			}
		}
		return result;
	}

	public void markReviewed(final NodeRef document) {
		NodeRef tableData = findNodeByAssociationRef(document, ASSOC_REVIEW_TS_REVIEW_TABLE, TYPE_REVIEW_TS_REVIEW_TABLE, ASSOCIATION_TYPE.TARGET);
		if (null != tableData) {
			List<NodeRef> reviewList = documentTableService.getTableDataRows(tableData);
			NodeRef currentEmployee = orgstructureBean.getCurrentEmployee();
			for (final NodeRef reviewListRow : reviewList) {
				NodeRef itemEmployee = findNodeByAssociationRef(reviewListRow, ASSOC_REVIEW_TS_REVIEWER, OrgstructureBean.TYPE_EMPLOYEE, ASSOCIATION_TYPE.TARGET);
				if (currentEmployee.equals(itemEmployee)) {
					if (CONSTRAINT_REVIEW_TS_STATE_IN_PROCESS.equals(nodeService.getProperty(reviewListRow, PROP_REVIEW_TS_STATE))) {
						AuthenticationUtil.runAsSystem(new AuthenticationUtil.RunAsWork<Void>() {

							@Override
							public Void doWork() throws Exception {
								Map<QName, Serializable> properties = nodeService.getProperties(reviewListRow);
								properties.put(PROP_REVIEW_TS_STATE, CONSTRAINT_REVIEW_TS_STATE_REVIEWED);
								properties.put(PROP_REVIEW_TS_REVIEW_FINISH_DATE, new Date());
								nodeService.setProperties(reviewListRow, properties);
								return null;
							}
						});
					}
				}
			}
		}
	}

	public Boolean canSendToReview(NodeRef document) {
		NodeRef tableData = findNodeByAssociationRef(document, ASSOC_REVIEW_TS_REVIEW_TABLE, TYPE_REVIEW_TS_REVIEW_TABLE, ASSOCIATION_TYPE.TARGET);
		if (null != tableData) {
			NodeRef currentEmployee = orgstructureBean.getCurrentEmployee();
			List<NodeRef> reviewList = documentTableService.getTableDataRows(tableData);
			for (NodeRef reviewListRow : reviewList) {
				NodeRef itemInitiator = findNodeByAssociationRef(reviewListRow, ASSOC_REVIEW_TS_INITIATOR, OrgstructureBean.TYPE_EMPLOYEE, ASSOCIATION_TYPE.TARGET);
				String state = (String) nodeService.getProperty(reviewListRow, PROP_REVIEW_TS_STATE);
				if (currentEmployee.equals(itemInitiator) && CONSTRAINT_REVIEW_TS_STATE_NOT_STARTED.equals(state)) {
					return true;
				}
			}
		}
		return false;
	}

	public Boolean canCancelReview(NodeRef document) {
		NodeRef tableData = findNodeByAssociationRef(document, ASSOC_REVIEW_TS_REVIEW_TABLE, TYPE_REVIEW_TS_REVIEW_TABLE, ASSOCIATION_TYPE.TARGET);
		if (null != tableData) {
			NodeRef currentEmployee = orgstructureBean.getCurrentEmployee();
			List<NodeRef> reviewList = documentTableService.getTableDataRows(tableData);
			for (NodeRef reviewListRow : reviewList) {
				NodeRef itemInitiator = findNodeByAssociationRef(reviewListRow, ASSOC_REVIEW_TS_INITIATOR, OrgstructureBean.TYPE_EMPLOYEE, ASSOCIATION_TYPE.TARGET);
				String state = (String) nodeService.getProperty(reviewListRow, PROP_REVIEW_TS_STATE);
				if (currentEmployee.equals(itemInitiator) && CONSTRAINT_REVIEW_TS_STATE_IN_PROCESS.equals(state)) {
					return true;
				}
			}
		}
		return false;
	}

	public void processItem(NodeRef nodeRef) throws WriteTransactionNeededException {
		if (TYPE_REVIEW_TS_REVIEW_TABLE_ITEM.equals(nodeService.getType(nodeRef))) {
			NodeRef rootFolder = nodeService.getPrimaryParent(nodeRef).getParentRef();
			NodeRef currentEmployee = orgstructureBean.getCurrentEmployee();
			Set<NodeRef> employeeSet = new HashSet<>();
			employeeSet.addAll(findNodesByAssociationRef(nodeRef, ASSOC_REVIEW_TS_REVIEWER, OrgstructureBean.TYPE_EMPLOYEE, ASSOCIATION_TYPE.TARGET));
			Boolean noEmployee = employeeSet.isEmpty();
			List<NodeRef> list = findNodesByAssociationRef(nodeRef, ASSOC_REVIEW_TS_REVIEWER, TYPE_REVIEW_LIST_REWIEW_LIST_ITEM, ASSOCIATION_TYPE.TARGET);
			for (NodeRef record : list) {
				employeeSet.addAll(findNodesByAssociationRef(record, ASSOC_REVIEW_LIST_REWIEWER, OrgstructureBean.TYPE_EMPLOYEE, BaseBean.ASSOCIATION_TYPE.TARGET));
			}
			if (employeeSet.size()!=1 || (noEmployee)) {
				for (NodeRef employee : employeeSet) {
					NodeRef newItem = createNode(rootFolder, TYPE_REVIEW_TS_REVIEW_TABLE_ITEM, null, null);
					nodeService.setProperty(newItem, DocumentTableService.PROP_INDEX_TABLE_ROW, documentTableService.getTableDataRows(rootFolder).size() - 1);
					nodeService.createAssociation(newItem, employee, ASSOC_REVIEW_TS_REVIEWER);
					nodeService.createAssociation(newItem, currentEmployee, ASSOC_REVIEW_TS_INITIATOR);
				}
				nodeService.moveNode(nodeRef, repositoryStructureHelper.getUserTemp(false), ContentModel.ASSOC_CONTAINS, QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, UUID.randomUUID().toString()));
				nodeService.addAspect(nodeRef, ContentModel.ASPECT_TEMPORARY, null);
			}
		}
	}

}
