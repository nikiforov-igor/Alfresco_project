package ru.it.lecm.workflow.review.api;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import org.springframework.beans.factory.InitializingBean;
import ru.it.lecm.base.beans.WriteTransactionNeededException;

import java.util.List;

/**
 * Created by dkuchurkin on 11.04.2016.
 */
public interface ReviewService extends InitializingBean {
    String CONSTRAINT_REVIEW_TS_STATE_IN_PROCESS = "NOT_REVIEWED";
    String CONSTRAINT_REVIEW_TS_STATE_REVIEWED = "REVIEWED";
    String CONSTRAINT_REVIEW_TS_STATE_NOT_STARTED = "NOT_STARTED";
    String CONSTRAINT_REVIEW_TS_STATE_CANCELLED = "CANCELLED";
    String REVIEW_TS_NAMESPACE = "http://www.it.ru/logicECM/model/review-ts/1.0";
    String REVIEW_LIST_NAMESPACE = "http://www.it.ru/logicECM/model/review-list/1.0";
    String REVIEW_GLOBAL_SETTINGS_NAMESPACE = "http://www.it.ru/logicECM/model/review/workflow/global-settings/1.0";
    QName ASSOC_REVIEW_TS_REVIEW_TABLE = QName.createQName(REVIEW_TS_NAMESPACE, "review-table-assoc");
    QName ASSOC_REVIEW_TS_REVIEWER = QName.createQName(REVIEW_TS_NAMESPACE, "reviewer-assoc");
    QName ASSOC_REVIEW_TS_INITIATOR = QName.createQName(REVIEW_TS_NAMESPACE, "initiator-assoc");
    QName TYPE_REVIEW_TS_REVIEW_TABLE = QName.createQName(REVIEW_TS_NAMESPACE, "review-table");
    QName TYPE_REVIEW_TS_REVIEW_TABLE_ITEM = QName.createQName(REVIEW_TS_NAMESPACE, "review-table-item");
    QName TYPE_REVIEW_LIST_REVIEW_LIST_ITEM = QName.createQName(REVIEW_LIST_NAMESPACE, "review-list-item");
    QName TYPE_REVIEW_GLOBAL_SETTINGS = QName.createQName(REVIEW_GLOBAL_SETTINGS_NAMESPACE, "settings");
    QName ASSOC_REVIEW_LIST_REVIEWER = QName.createQName(REVIEW_LIST_NAMESPACE, "reviewer-assoc");
    QName PROP_REVIEW_TS_STATE = QName.createQName(REVIEW_TS_NAMESPACE, "review-state");
    QName PROP_REVIEW_TS_REVIEW_FINISH_DATE = QName.createQName(REVIEW_TS_NAMESPACE, "review-finish-date");
    QName PROP_REVIEW_TS_ACTIVE_REVIEWERS = QName.createQName(REVIEW_TS_NAMESPACE, "active-reviewers");
    QName PROP_REVIEW_GLOBAL_SETTINGS_DEFAULT_REVIEW_TERM = QName.createQName(REVIEW_GLOBAL_SETTINGS_NAMESPACE, "defaultReviewTerm");
    QName PROP_REVIEW_GLOBAL_SETTINGS_TERM_TO_NOTIFY_BEFORE_DEADLINE = QName.createQName(REVIEW_GLOBAL_SETTINGS_NAMESPACE, "termToNotifyBeforeDeadline");

    Boolean needReviewByCurrentUser(NodeRef document);

    List<NodeRef> getExcludeUsersList(NodeRef document);

    List<NodeRef> getActiveReviewersForDocument(NodeRef document);

    void markReviewed(NodeRef document);

    Boolean canSendToReview(NodeRef document);

    Boolean canCancelReview(NodeRef document);

    Boolean deleteRowAllowed(NodeRef nodeRef);

    void processItem(NodeRef nodeRef) throws WriteTransactionNeededException;

    NodeRef getSettings();

    int getApprovalTerm();
}
