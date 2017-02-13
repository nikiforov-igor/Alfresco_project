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
	String REVIEW_INFO_NAMESPACE = "http://www.it.ru/logicECM/model/review-info/1.0";
	String REVIEW_ASPECTS_NAMESPACE = "http://www.it.ru/logicECM/model/review-aspects/1.0";
    QName ASSOC_REVIEW_TS_REVIEW_TABLE = QName.createQName(REVIEW_TS_NAMESPACE, "review-table-assoc");
    QName ASSOC_REVIEW_TS_REVIEWER = QName.createQName(REVIEW_TS_NAMESPACE, "reviewer-assoc");
    QName ASSOC_REVIEW_TS_INITIATOR = QName.createQName(REVIEW_TS_NAMESPACE, "initiator-assoc");
    QName TYPE_REVIEW_TS_REVIEW_TABLE = QName.createQName(REVIEW_TS_NAMESPACE, "review-table");
    QName TYPE_REVIEW_TS_REVIEW_TABLE_ITEM = QName.createQName(REVIEW_TS_NAMESPACE, "review-table-item");
    QName TYPE_REVIEW_LIST_REVIEW_LIST_ITEM = QName.createQName(REVIEW_LIST_NAMESPACE, "review-list-item");
    QName TYPE_REVIEW_GLOBAL_SETTINGS = QName.createQName(REVIEW_GLOBAL_SETTINGS_NAMESPACE, "settings");
	QName TYPE_REVIEW_INFO = QName.createQName(REVIEW_INFO_NAMESPACE, "info");
    QName ASSOC_REVIEW_LIST_REVIEWER = QName.createQName(REVIEW_LIST_NAMESPACE, "reviewer-assoc");
    QName PROP_REVIEW_TS_STATE = QName.createQName(REVIEW_TS_NAMESPACE, "review-state");
    QName PROP_REVIEW_TS_REVIEW_FINISH_DATE = QName.createQName(REVIEW_TS_NAMESPACE, "review-finish-date");
    QName PROP_REVIEW_TS_REVIEW_START_DATE = QName.createQName(REVIEW_TS_NAMESPACE, "review-start-date");
    QName PROP_REVIEW_TS_ACTIVE_REVIEWERS = QName.createQName(REVIEW_TS_NAMESPACE, "active-reviewers");
    QName PROP_REVIEW_GLOBAL_SETTINGS_DEFAULT_REVIEW_TERM = QName.createQName(REVIEW_GLOBAL_SETTINGS_NAMESPACE, "defaultReviewTerm");
    QName PROP_REVIEW_GLOBAL_SETTINGS_TERM_TO_NOTIFY_BEFORE_DEADLINE = QName.createQName(REVIEW_GLOBAL_SETTINGS_NAMESPACE, "termToNotifyBeforeDeadline");
	QName PROP_REVIEW_INFO_REVIEW_START_DATE = QName.createQName(REVIEW_INFO_NAMESPACE, "review-start-date");
	QName PROP_REVIEW_INFO_REVIEW_STATE = QName.createQName(REVIEW_INFO_NAMESPACE, "review-state");
	QName ASSOC_REVIEW_INFO_INITIATOR = QName.createQName(REVIEW_INFO_NAMESPACE, "initiator-assoc");
	QName ASSOC_REVIEW_REVIEW_LIST = QName.createQName(REVIEW_INFO_NAMESPACE, "review-list-assoc");
	QName ASSOC_REVIEW_INFO = QName.createQName(REVIEW_INFO_NAMESPACE, "info-assoc");

    QName PROP_RELATED_REVIEW_RECORDS_CHANGE_COUNT = QName.createQName(REVIEW_ASPECTS_NAMESPACE, "related-review-records-change-count");
	QName ASSOC_RELATED_REVIEW_RECORDS = QName.createQName(REVIEW_ASPECTS_NAMESPACE, "related-review-records-assoc");

    String CONSTRAINT_REVIEW_GLOBAL_SETTINGS_SELECT_BY_ORGANISATION = "ORGANISATION";
    String CONSTRAINT_REVIEW_GLOBAL_SETTINGS_SELECT_BY_UNIT= "UNIT";
    QName PROP_REVIEW_GLOBAL_SETTINGS_SELECT_BY = QName.createQName(REVIEW_GLOBAL_SETTINGS_NAMESPACE, "selectBy");

    Boolean needReviewByCurrentUser(NodeRef document);

    List<NodeRef> getExcludeUsersList(NodeRef document);

    List<NodeRef> getActiveReviewersForDocument(NodeRef document);

    void markReviewed(NodeRef document);

    Boolean canSendToReview(NodeRef document);

    Boolean canCancelReview(NodeRef document);

    Boolean deleteRowAllowed(NodeRef nodeRef);

    void processItem(NodeRef nodeRef) throws WriteTransactionNeededException;

    NodeRef getSettings();

    int getReviewTerm();

    int getReviewNotificationTerm();

    NodeRef getDocumentByReviewTableItem(NodeRef nodeRef);

	boolean reviewAllowed(NodeRef documentRef);

    boolean isReviewersByOrganization();

    List<NodeRef> getPotentialReviewers();

    List<NodeRef> getPotentialReviewers(List<NodeRef> unit);

    List<NodeRef> getAllowedReviewList();

    /**
     * При изменении статуса записи ознакомления в каждом инициирующем документе,
     * из которого есть ассоциация на текущую запись, увеличить значение счетчика
     *
     * @param document Инициирующий документ
     */
    void addRelatedReviewChangeCount(NodeRef document);

    /**
     * Метод сброса сигнала после обработки
     * @param document Инициирующий документ
     */
    void resetRelatedReviewChangeCount(NodeRef document);
}
