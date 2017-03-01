package ru.it.lecm.ord.api;

import java.util.EnumMap;
import org.alfresco.service.namespace.QName;

/**
 *
 * @author snovikov
 */
public final class ORDModel {

	public final static String ORD_PREFIX = "lecm-ord";
	public final static String ORD_TABLE_PREFIX = "lecm-ord-table-structure";
	public final static String ORD_NAMESPACE = "http://www.it.ru/lecm/ORD/1.0";
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

	public static final String ORD_POINT_DICTIONARY_NAME = "Статусы пунктов ОРД";
	public static final String ORD_POINT_PERFORMANCE_STATUS = "На исполнении";
	public static final String ORD_POINT_WAIT_PERFORMANCE_STATUS = "Ожидает исполнения";
	public static final String ORD_POINT_EXECUTED_STATUS = "Исполнен";
	public static final String ORD_POINT_NOT_EXECUTED_STATUS = "Не исполнен";
	public static final String ORD_POINT_EXPIRED_STATUS = "Просрочен";

	public static enum ORD_STATUSES { CANCELED_FAKE_STATUS, DELETED_STATUS };
	public static final EnumMap<ORD_STATUSES,String> STATUSES = new EnumMap<ORD_STATUSES,String>(ORD_STATUSES.class){{
		put(ORD_STATUSES.CANCELED_FAKE_STATUS, "Отменен");
		put(ORD_STATUSES.DELETED_STATUS, "Удален");
	}};

	public static enum P_STATUSES {WAIT_PERFORMANCE_STATUS, PERFORMANCE_STATUS, EXECUTED_STATUS, NOT_EXECUTED_STATUS, EXPIRED_STATUS };
	public static final EnumMap<P_STATUSES,String> POINT_STATUSES = new EnumMap<P_STATUSES,String>(P_STATUSES.class){{
		put(P_STATUSES.WAIT_PERFORMANCE_STATUS, "Ожидает исполнения");
		put(P_STATUSES.PERFORMANCE_STATUS, "На исполнении");
		put(P_STATUSES.EXECUTED_STATUS, "Исполнен");
		put(P_STATUSES.NOT_EXECUTED_STATUS, "Не исполнен");
		put(P_STATUSES.EXPIRED_STATUS, "Просрочен");
	}};

	public static enum ATTACHMENT_CATEGORIES { DOCUMENT, APPLICATIONS, AGREEMENTS, ORIGINAL, OTHERS };
	public static final EnumMap<ATTACHMENT_CATEGORIES,String> ATTACHMENT_CATEGORIES_MAP = new EnumMap<ATTACHMENT_CATEGORIES,String>(ATTACHMENT_CATEGORIES.class){{
		put(ATTACHMENT_CATEGORIES.DOCUMENT, "Документ");
		put(ATTACHMENT_CATEGORIES.APPLICATIONS, "Приложения");
		put(ATTACHMENT_CATEGORIES.AGREEMENTS, "Согласования");
		put(ATTACHMENT_CATEGORIES.ORIGINAL, "Подлинник");
		put(ATTACHMENT_CATEGORIES.OTHERS, "Прочее");
	}};

	private ORDModel() throws IllegalAccessException {
		throw new IllegalAccessException("You cannot create any instance of ORDModel class.");
	}
}
