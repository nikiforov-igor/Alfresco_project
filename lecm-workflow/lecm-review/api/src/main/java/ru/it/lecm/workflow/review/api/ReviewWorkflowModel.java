package ru.it.lecm.workflow.review.api;

import org.alfresco.service.namespace.QName;

/**
 *
 * @author vmalygin
 */
public final class ReviewWorkflowModel {

	public final static String REVIEW_RESULT_PREFIX = "lecm-review-result";
	public final static String REVIEW_RESULT_NAMESPACE = "http://www.it.ru/logicECM/model/review-result/1.0";

	public final static QName TYPE_REVIEW_RESULT_ITEM = QName.createQName(REVIEW_RESULT_NAMESPACE, "review-result-item");
	public final static QName PROP_REVIEW_RESULT_ITEM_RESULT = QName.createQName(REVIEW_RESULT_NAMESPACE, "review-result-item-result");

	private ReviewWorkflowModel() throws IllegalAccessException {
		throw new IllegalAccessException("You cannot create any instance of ReviewWorkflowModel class.");
	}
}
