package ru.it.lecm.workflow.review;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.PropertyMap;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.base.beans.WriteTransactionNeededException;
import ru.it.lecm.documents.beans.DocumentTableService;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.workflow.review.api.ReviewService;

import java.io.Serializable;
import java.util.*;

/**
 *
 * @author vkuprin
 */
public class ReviewServiceImpl extends BaseBean implements ReviewService {

	public static final String REVIEW_FOLDER = "REVIEW_FOLDER";
	private final static String REVIEW_GLOBAL_SETTINGS_NAME = "Глобальные настройки ознакомления";
	private final static int DEFAULT_REVIEW_TERM = 1;

	private DocumentTableService documentTableService;
	private OrgstructureBean orgstructureBean;
	private Integer defaultReviewTerm;
	private Integer defaultTermToNotify;

	public void setOrgstructureBean(OrgstructureBean orgstructureBean) {
		this.orgstructureBean = orgstructureBean;
	}

	public void setDocumentTableService(DocumentTableService documentTableService) {
		this.documentTableService = documentTableService;
	}

	public void setDefaultReviewTerm(Integer defaultReviewTerm) {
		this.defaultReviewTerm = (defaultReviewTerm != null) ? defaultReviewTerm : DEFAULT_REVIEW_TERM;
	}

	public void setDefaultTermToNotify(Integer defaultTermToNotify) {
		this.defaultTermToNotify = (defaultTermToNotify != null) ? defaultTermToNotify : DEFAULT_REVIEW_TERM;
	}

	public void init() {
		if (null == getSettings()) {
			AuthenticationUtil.runAsSystem(new AuthenticationUtil.RunAsWork<NodeRef>() {
				@Override
				public NodeRef doWork() throws Exception {
					RetryingTransactionHelper transactionHelper = transactionService.getRetryingTransactionHelper();
					return transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<NodeRef>() {
						@Override
						public NodeRef execute() throws Throwable {
							PropertyMap props = new PropertyMap();
							if (defaultReviewTerm != null) {
								props.put(PROP_REVIEW_GLOBAL_SETTINGS_DEFAULT_REVIEW_TERM, defaultReviewTerm);
								props.put(PROP_REVIEW_GLOBAL_SETTINGS_TERM_TO_NOTIFY_BEFORE_DEADLINE, defaultTermToNotify);
							}
							return createNode(getServiceRootFolder(), TYPE_REVIEW_GLOBAL_SETTINGS, REVIEW_GLOBAL_SETTINGS_NAME, props);
						}
					}, false, true);
				}
			});
		}
	}

	@Override
	public NodeRef getServiceRootFolder() {
		return getFolder(REVIEW_FOLDER);
	}

	@Override
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

	private List<NodeRef> getReviewersWithStatuses(NodeRef document, Set<String> statuses) {
		NodeRef tableData = findNodeByAssociationRef(document, ASSOC_REVIEW_TS_REVIEW_TABLE, TYPE_REVIEW_TS_REVIEW_TABLE, ASSOCIATION_TYPE.TARGET);
		List<NodeRef> result = new ArrayList<>();
		if (null != tableData) {
			List<NodeRef> reviewList = documentTableService.getTableDataRows(tableData);
			for (NodeRef reviewListRow : reviewList) {
				if (!nodeService.hasAspect(reviewListRow, ContentModel.ASPECT_TEMPORARY)) {
					NodeRef itemEmployee = findNodeByAssociationRef(reviewListRow, ASSOC_REVIEW_TS_REVIEWER, OrgstructureBean.TYPE_EMPLOYEE, ASSOCIATION_TYPE.TARGET);
					String state = (String) nodeService.getProperty(reviewListRow, PROP_REVIEW_TS_STATE);
					if (statuses.contains(state)) {
						result.add(itemEmployee);
					}
				}
			}
		}
		return result;
	}

	@Override
	public List<NodeRef> getExcludeUsersList(NodeRef document) {
		HashSet<String> statuses = new HashSet<>();
		statuses.add(CONSTRAINT_REVIEW_TS_STATE_NOT_STARTED);
		statuses.add(CONSTRAINT_REVIEW_TS_STATE_IN_PROCESS);
		statuses.add(CONSTRAINT_REVIEW_TS_STATE_REVIEWED);
		return getReviewersWithStatuses(document, statuses);
	}

	@Override
	public List<NodeRef> getActiveReviewersForDocument(NodeRef document) {
		HashSet<String> statuses = new HashSet<>();
		statuses.add(CONSTRAINT_REVIEW_TS_STATE_IN_PROCESS);
		return getReviewersWithStatuses(document, statuses);
	}

	@Override
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

	@Override
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

	@Override
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

	@Override
	public Boolean deleteRowAllowed(NodeRef nodeRef) {
		Boolean result = true;
		if (null != nodeRef && TYPE_REVIEW_TS_REVIEW_TABLE_ITEM.equals(nodeService.getType(nodeRef))) {
			NodeRef currentEmployee = orgstructureBean.getCurrentEmployee();
			NodeRef itemInitiator = findNodeByAssociationRef(nodeRef, ASSOC_REVIEW_TS_INITIATOR, OrgstructureBean.TYPE_EMPLOYEE, ASSOCIATION_TYPE.TARGET);
			String status = nodeService.getProperty(nodeRef, PROP_REVIEW_TS_STATE).toString();
			result = currentEmployee.equals(itemInitiator) && (CONSTRAINT_REVIEW_TS_STATE_NOT_STARTED.equals(status));
		}
		return result;
	}

	@Override
	public void processItem(NodeRef nodeRef) throws WriteTransactionNeededException {
		if (TYPE_REVIEW_TS_REVIEW_TABLE_ITEM.equals(nodeService.getType(nodeRef))) {
			NodeRef rootFolder = nodeService.getPrimaryParent(nodeRef).getParentRef();
			NodeRef currentEmployee = orgstructureBean.getCurrentEmployee();
			Set<NodeRef> employeeSet = new HashSet<>();
			employeeSet.addAll(findNodesByAssociationRef(nodeRef, ASSOC_REVIEW_TS_REVIEWER, OrgstructureBean.TYPE_EMPLOYEE, ASSOCIATION_TYPE.TARGET));
			Boolean noEmployee = employeeSet.isEmpty();
			List<NodeRef> list = findNodesByAssociationRef(nodeRef, ASSOC_REVIEW_TS_REVIEWER, TYPE_REVIEW_LIST_REVIEW_LIST_ITEM, ASSOCIATION_TYPE.TARGET);
			for (NodeRef record : list) {
				employeeSet.addAll(findNodesByAssociationRef(record, ASSOC_REVIEW_LIST_REVIEWER, OrgstructureBean.TYPE_EMPLOYEE, BaseBean.ASSOCIATION_TYPE.TARGET));
			}
			NodeRef newItemInfo = createNode(rootFolder, TYPE_REVIEW_INFO, null, null);
			nodeService.createAssociation(newItemInfo, currentEmployee, ASSOC_REVIEW_INFO_INITIATOR);

			if (employeeSet.size() != 1 || (noEmployee)) {
				List<NodeRef> excludeUsers = getExcludeUsersList(documentTableService.getDocumentByTableDataRow(nodeRef));
				employeeSet.removeAll(excludeUsers);
				for (NodeRef employee : employeeSet) {
					NodeRef newItem = createNode(rootFolder, TYPE_REVIEW_TS_REVIEW_TABLE_ITEM, null, null);
					nodeService.setProperty(newItem, DocumentTableService.PROP_INDEX_TABLE_ROW, documentTableService.getTableDataRows(rootFolder).size() - 1);
					nodeService.createAssociation(newItem, employee, ASSOC_REVIEW_TS_REVIEWER);
					nodeService.createAssociation(newItem, currentEmployee, ASSOC_REVIEW_TS_INITIATOR);
					nodeService.createAssociation(newItem, newItemInfo, ASSOC_REVIEW_INFO);
				}
				nodeService.moveNode(nodeRef, repositoryStructureHelper.getUserTemp(false), ContentModel.ASSOC_CONTAINS, QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, UUID.randomUUID().toString()));
				//nodeService.addAspect(nodeRef, ContentModel.ASPECT_TEMPORARY, null);
			} else {
				nodeService.removeAspect(nodeRef, ContentModel.ASPECT_TEMPORARY);
				nodeService.createAssociation(nodeRef, newItemInfo, ASSOC_REVIEW_INFO);
			}
		}
	}

	@Override
	public NodeRef getSettings() {
		return nodeService.getChildByName(getServiceRootFolder(), ContentModel.ASSOC_CONTAINS, REVIEW_GLOBAL_SETTINGS_NAME);
	}

	@Override
	public int getReviewTerm() {
		NodeRef settingsNode = getSettings();
		Integer approvalTerm = (Integer) nodeService.getProperty(settingsNode, PROP_REVIEW_GLOBAL_SETTINGS_DEFAULT_REVIEW_TERM);

		return approvalTerm != null ? approvalTerm : defaultReviewTerm;
	}

	@Override
	public int getReviewNotificationTerm() {
		NodeRef settingsNode = getSettings();
		Integer approvalTerm = (Integer) nodeService.getProperty(settingsNode, PROP_REVIEW_GLOBAL_SETTINGS_TERM_TO_NOTIFY_BEFORE_DEADLINE);

		return approvalTerm != null ? approvalTerm : defaultReviewTerm;
	}

	@Override
	public NodeRef getDocumentByReviewTableItem(NodeRef nodeRef) {
		for (int i = 0; i < 3; i++) {
			if (nodeRef == null) {
				return null;
			}
			nodeRef = nodeService.getPrimaryParent(nodeRef).getParentRef();
		}

		return nodeRef;
	}

}
