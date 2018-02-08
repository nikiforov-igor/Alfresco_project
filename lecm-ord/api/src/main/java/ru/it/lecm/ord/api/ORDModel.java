package ru.it.lecm.ord.api;

import org.alfresco.service.namespace.QName;

/**
 *
 * @author snovikov
 */
public final class ORDModel {

	public final static String ORD_PREFIX = "lecm-ord";
	public final static String ORD_TABLE_PREFIX = "lecm-ord-table-structure";
	public final static String ORD_NAMESPACE = "http://www.it.ru/lecm/ORD/1.0";
	public final static String ORD_DIC_NAMESPACE = "http://www.it.ru/logicECM/ORD/dictionaries/1.0";
	public final static String ORD_TABLE_NAMESPACE = "http://www.it.ru/lecm/ORD/table-structure/1.0";

	public final static String DOCUMENT_FILE_REGISTER_NAMESPACE = "http://www.it.ru/logicECM/document/dictionaries/fileRegister/1.0";

	public final static QName TYPE_ORD = QName.createQName(ORD_NAMESPACE, "document");
	public final static QName ASSOC_ORD_CONTROLLER = QName.createQName(ORD_NAMESPACE, "controller-assoc");
	public final static QName ASSOC_ORD_CANCELED = QName.createQName(ORD_NAMESPACE, "canceled-assoc");
	public final static QName ASSOC_ORD_ACCEPT = QName.createQName(ORD_NAMESPACE, "accepted-assoc");

	public static final QName TYPE_ORD_ITEMS_TABLE = QName.createQName(ORD_TABLE_NAMESPACE, "itemsTable");
	public final static QName ASSOC_ORD_TABLE_ITEMS = QName.createQName(ORD_TABLE_NAMESPACE, "items-assoc");
	public final static QName TYPE_ORD_TABLE_ITEM = QName.createQName(ORD_TABLE_NAMESPACE, "item");
	public final static QName PROP_ORD_TABLE_TEMP_ITEM_INDEX = QName.createQName(ORD_TABLE_NAMESPACE, "temp-item-index");
	public final static QName PROP_ORD_TABLE_ITEM_CONTENT = QName.createQName(ORD_TABLE_NAMESPACE, "item-content");
	public final static QName PROP_ORD_TABLE_EXECUTION_DATE = QName.createQName(ORD_TABLE_NAMESPACE, "execution-date");
	public final static QName PROP_ORD_TABLE_EXECUTION_DATE_REAL = QName.createQName(ORD_TABLE_NAMESPACE, "execution-date-real");
	public final static QName PROP_ORD_TABLE_ITEM_TITLE = QName.createQName(ORD_TABLE_NAMESPACE, "title");
	public final static QName PROP_ORD_TABLE_ITEM_DATE_TEXT = QName.createQName(ORD_TABLE_NAMESPACE, "limitation-date-text");
	public final static QName PROP_ORD_TABLE_ITEM_DATE_RADIO = QName.createQName(ORD_TABLE_NAMESPACE, "limitation-date-radio");
	public final static QName PROP_ORD_TABLE_ITEM_DATE_DAYS = QName.createQName(ORD_TABLE_NAMESPACE, "limitation-date-days");
	public final static QName PROP_ORD_TABLE_ITEM_DATE_TYPE = QName.createQName(ORD_TABLE_NAMESPACE, "limitation-date-type");
	public final static QName PROP_ORD_TABLE_ITEM_REPORT_REQUIRED = QName.createQName(ORD_TABLE_NAMESPACE, "report-required");
	public final static QName ASSOC_ORD_TABLE_EXECUTOR = QName.createQName(ORD_TABLE_NAMESPACE, "executor-assoc");
	public final static QName ASSOC_ORD_TABLE_COEXECUTORS = QName.createQName(ORD_TABLE_NAMESPACE, "coexecutors-assoc");
	public final static QName ASSOC_ORD_TABLE_SUBJECT = QName.createQName(ORD_TABLE_NAMESPACE, "subject-assoc");
	public final static QName ASSOC_ORD_TABLE_CONTROLLER = QName.createQName(ORD_TABLE_NAMESPACE, "controller-assoc");
	public final static QName ASSOC_ORD_TABLE_ERRAND = QName.createQName(ORD_TABLE_NAMESPACE, "errand-assoc");
	public final static QName ASSOC_ORD_TABLE_ITEM_STATUS = QName.createQName(ORD_TABLE_NAMESPACE, "item-status-assoc");
	public final static QName ASSOC_ORD_TABLE_ITEM_AUTHOR = QName.createQName(ORD_TABLE_NAMESPACE, "author-assoc");
	public final static QName ASSOC_ORD_TABLE_ITEM_COMPILER = QName.createQName(ORD_TABLE_NAMESPACE,"compiler-assoc");
	public final static QName ASSOC_DOCUMENT_FILE_REGISTER_UNIT = QName.createQName(DOCUMENT_FILE_REGISTER_NAMESPACE, "organization-unit-assoc");

	public static final QName PROP_ORD_DIC_POINT_STATUS_CODE = QName.createQName(ORD_DIC_NAMESPACE, "ord-point-status-code");


    public static enum ORD_STATUS {
        CANCELED_FAKE("Отменен"), DELETED("Удален"), EXECUTION("На исполнении");
        private String historyValue;

        ORD_STATUS(String historyValue) {
            this.historyValue = historyValue;
        }

        public String getHistoryValue() {
            return historyValue;
        }
    }

    public static enum P_STATUSES {WAIT_PERFORMANCE_STATUS, PERFORMANCE_STATUS, EXECUTED_STATUS, NOT_EXECUTED_STATUS, EXPIRED_STATUS, CANCELED_STATUS, EXECUTED_BY_CONTROLLER_STATUS, CANCELED_BY_CONTROLLER_STATUS};
	public enum ATTACHMENT_CATEGORIES { DOCUMENT, APPLICATIONS, AGREEMENTS, ORIGINAL, OTHERS };

	public static final String ORD_POINT_DICTIONARY_NAME = "Статусы пунктов ОРД";
	public static final String ORD_POINT_PERFORMANCE_STATUS = "На исполнении";
	public static final String ORD_POINT_WAIT_PERFORMANCE_STATUS = "Ожидает исполнения";
	public static final String ORD_POINT_EXECUTED_STATUS = "Исполнен";
	public static final String ORD_POINT_NOT_EXECUTED_STATUS = "Не исполнен";
	public static final String ORD_POINT_EXPIRED_STATUS = "Просрочен";
	public static final String ORD_POINT_EXECUTED_BY_CONTROLLER_STATUS = "Исполнен Контролером";
	public static final String ORD_POINT_CANCELED_BY_CONTROLLER_STATUS = "Отменен Контролером";

	private ORDModel() throws IllegalAccessException {
		throw new IllegalAccessException("You cannot create any instance of ORDModel class.");
	}
}
