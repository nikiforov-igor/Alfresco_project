/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.it.lecm.internal.api;

import org.alfresco.service.namespace.QName;
import org.springframework.extensions.surf.util.I18NUtil;

import java.util.EnumMap;

/**
 *
 * @author ikhalikov
 */
public interface InternalService {
	public final static String INTERNAL_PREFIX = "lecm-internal";
	public final static String INTERNAL_NAMESPACE = "http://www.it.ru/logicECM/internal/1.0";

	public final static String ANSWERS_TABLE_PREFIX = "lecm-internal-table-structure";
	public final static String ANSWERS_TABLE_NAMESPACE = "http://www.it.ru/logicECM/internal/table-structure/1.0";

	public final static QName TYPE_INTERNAL = QName.createQName(INTERNAL_NAMESPACE, "document");
	public final static QName TYPE_ANSWER = QName.createQName(ANSWERS_TABLE_NAMESPACE, "answer");

    public final static QName ASSOC_INTERNAL_RECIPIENTS = QName.createQName(INTERNAL_NAMESPACE, "recipients-assoc");
    public final static QName PROP_ANSWER_TABLE_STATUS = QName.createQName(ANSWERS_TABLE_NAMESPACE, "answer-status");
	public final static QName PROP_ANSWER_TABLE_EMPLOYEE_ASSOC_REF = QName.createQName(ANSWERS_TABLE_NAMESPACE, "employee-assoc-ref");
	public final static QName PROP_ANSWER_TABLE_EMPLOYEE_ASSOC_TEXT = QName.createQName(ANSWERS_TABLE_NAMESPACE, "employee-assoc-text-content");
	public final static QName PROP_ANSWER_TABLE_ANSWER = QName.createQName(ANSWERS_TABLE_NAMESPACE, "answer-details");

	public final static QName ASSOC_ANSWER_TABLE_EMPLOYEE = QName.createQName(ANSWERS_TABLE_NAMESPACE, "employee-assoc");
	public final static QName ASSOC_ANSWER_TABLE = QName.createQName(ANSWERS_TABLE_NAMESPACE, "answers-assoc");

	public final static QName PROP_INTERNAL_RESPONSE_DATE = QName.createQName(INTERNAL_NAMESPACE, "response-date");

    enum INTERNAL_STATUSES {
        INTERNAL_DIRECTED_STATUS("Направлен"), INTERNAL_CLOSED_STATUS("Закрыт");
        private String historyValue = "";

        INTERNAL_STATUSES(String historyValue) {
            this.historyValue = historyValue;
        }

        public String getHistoryValue() {
            return historyValue;
        }
    }

	String getInternalStatusName(INTERNAL_STATUSES code);
}
