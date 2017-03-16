package ru.it.lecm.workflow.review.policy;

import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.Behaviour;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.PropertyCheck;
import org.apache.commons.lang.ObjectUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.documents.beans.DocumentTableService;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.workflow.review.api.ReviewService;

import java.io.Serializable;
import java.util.*;

/**
 *
 * @author vkuprin
 */
public class ItemChangedPolicy extends BaseBean implements NodeServicePolicies.OnUpdatePropertiesPolicy {
	private final static Logger logger = LoggerFactory.getLogger(ItemChangedPolicy.class);

	private PolicyComponent policyComponent;
	private OrgstructureBean orgstructureService;
	private ReviewService reviewService;
	private DocumentTableService tableService;
	private String statusesWithOrder;
	private DictionaryService dictionaryService;

	public void setStatusesWithOrder(String statusesWithOrder) {
		this.statusesWithOrder = statusesWithOrder;
	}

	public void setTableService(DocumentTableService tableService) {
		this.tableService = tableService;
	}
	
	public void setReviewService(ReviewService reviewService) {
		this.reviewService = reviewService;
	}

    public void setOrgstructureService(OrgstructureBean orgstructureService) {
		this.orgstructureService = orgstructureService;
	}

	public void setPolicyComponent(PolicyComponent policyComponent) {
		this.policyComponent = policyComponent;
	}

	public void setDictionaryService(DictionaryService dictionaryService) {
		this.dictionaryService = dictionaryService;
	}

    @Override
    public NodeRef getServiceRootFolder() {
        return null;
    }

	public void init() {
		PropertyCheck.mandatory(this, "policyComponent", policyComponent);
		PropertyCheck.mandatory(this, "nodeService", nodeService);
		PropertyCheck.mandatory(this, "orgstructureService", orgstructureService);
		PropertyCheck.mandatory(this, "reviewService", reviewService);

		policyComponent.bindClassBehaviour(NodeServicePolicies.OnUpdatePropertiesPolicy.QNAME,
				ReviewService.TYPE_REVIEW_TS_REVIEW_TABLE_ITEM, new JavaBehaviour(this, "onUpdateProperties", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));
	}

	@Override
	public void onUpdateProperties(NodeRef nodeRef, Map<QName, Serializable> before, Map<QName, Serializable> after) {
        if (!ObjectUtils.equals(before.get(ReviewService.PROP_REVIEW_TS_STATE), after.get(ReviewService.PROP_REVIEW_TS_STATE))) {
            NodeRef document = tableService.getDocumentByTableDataRow(nodeRef);
            List<NodeRef> employees = reviewService.getActiveReviewersForDocument(document);
            StringBuilder sb = new StringBuilder();
            for (NodeRef employee : employees) {
                sb.append(employee.toString()).append(";");
            }
            if (sb.length() > 0) {
                sb.deleteCharAt(sb.length() - 1);
            }
            nodeService.setProperty(document, ReviewService.PROP_REVIEW_TS_ACTIVE_REVIEWERS, sb.toString());

            List<NodeRef> initiatingDocuments = findNodesByAssociationRef(nodeRef, ReviewService.ASSOC_RELATED_REVIEW_RECORDS, null, ASSOCIATION_TYPE.SOURCE);
            if (initiatingDocuments != null) {
                for (NodeRef initiatingDocument: initiatingDocuments) {
                    reviewService.addRelatedReviewChangeCount(initiatingDocument);

                    /*Обновляем статистику в инициирующем документе*/
                    List<NodeRef> tableRows = findNodesByAssociationRef(initiatingDocument, ReviewService.ASSOC_RELATED_REVIEW_RECORDS, null, ASSOCIATION_TYPE.TARGET);
                    updateStatistics(tableRows, initiatingDocument, ReviewService.PROP_RELATED_REVIEW_STATE, ReviewService.PROP_RELATED_REVIEW_STATISTICS);
                }
            }

			/*Обновляем статистику*/
			NodeRef reviewTable = tableService.getTable(document, ReviewService.TYPE_REVIEW_TS_REVIEW_TABLE);
			if (reviewTable != null) {
                List<NodeRef> tableRows = tableService.getTableDataRows(reviewTable);
                updateStatistics(tableRows, document, ReviewService.PROP_REVIEW_STATE, ReviewService.PROP_REVIEW_STATISTICS);
			}
        }
    }

    private void updateStatistics(List<NodeRef> tableRows, NodeRef document, QName propState, QName propStatistic) {
        List<String> statuses = null;
        if (statusesWithOrder != null && !statusesWithOrder.isEmpty()) {
            statuses = Arrays.asList(statusesWithOrder.split(","));
        }

        /*Заполняем по умолчанию*/
        Map<String, Integer> itemsCountByState = new LinkedHashMap<>();
        if (statuses != null && !statuses.isEmpty()) {
            for (String status : statuses) {
                itemsCountByState.put(status, 0);
            }
        } else {
            for (ReviewService.REVIEW_ITEM_STATE reviewItemState : ReviewService.REVIEW_ITEM_STATE.values()) {
                itemsCountByState.put(reviewItemState.getLabel(dictionaryService), 0);
            }
        }

				/*По дефолту -> Не требуется*/
        ReviewService.REVIEW_STATE reviewState = ReviewService.REVIEW_STATE.NOT_REQUIRED;

        Boolean reviewInProcess = false;

        if (tableRows != null && !tableRows.isEmpty()) {
            for (NodeRef row : tableRows) {
                String rowState = (String) nodeService.getProperty(row, ReviewService.PROP_REVIEW_TS_STATE);
                try {
                    ReviewService.REVIEW_ITEM_STATE state = ReviewService.REVIEW_ITEM_STATE.valueOf(rowState);
                    String stateLabel = state.getLabel(dictionaryService);
                    itemsCountByState.put(stateLabel, itemsCountByState.containsKey(stateLabel) ? itemsCountByState.get(stateLabel) + 1 : 1);

                    if (!reviewInProcess) {
								/*Хотя бы одна задача по ознакомлению направлена на ознакомление -> В процессе*/
                        if (state.equals(ReviewService.REVIEW_ITEM_STATE.NOT_REVIEWED) || state.equals(ReviewService.REVIEW_ITEM_STATE.NOT_STARTED)) {
                            reviewInProcess = true;
                            reviewState = ReviewService.REVIEW_STATE.IN_PROCESS;
                        } else if (state.equals(ReviewService.REVIEW_ITEM_STATE.REVIEWED)) { /*Хотя бы одна задача завершена -> Завершено или В процессе*/
                            reviewState = ReviewService.REVIEW_STATE.COMPLETE;
                        }
                    }
                } catch (IllegalArgumentException ex) {
                    logger.error(ex.getMessage(), ex);
                }
            }
        }

        /*Состояние*/
        nodeService.setProperty(document, propState, reviewState.name());

        /*Статистика*/
        JSONArray jsonArray = new JSONArray();
        for (Map.Entry<String, Integer> entry : itemsCountByState.entrySet()) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("state", entry.getKey());
                jsonObject.put("count", entry.getValue());
            } catch (JSONException e) {
                logger.error(e.getMessage(), e);
            }

            jsonArray.put(jsonObject);
        }
        nodeService.setProperty(document, propStatistic, jsonArray.toString());
    }
}
