package ru.it.lecm.ord.api;

import java.util.EnumMap;
import java.util.HashMap;
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

	public final static QName ASSOC_ORD_TABLE_ITEMS = QName.createQName(ORD_TABLE_NAMESPACE, "items-assoc");
	public final static QName TYPE_ORD_TABLE_ITEM = QName.createQName(ORD_TABLE_NAMESPACE, "item");
	public final static QName PROP_ORD_TABLE_ITEM_CONTENT = QName.createQName(ORD_TABLE_NAMESPACE, "item-content");
	public final static QName PROP_ORD_TABLE_EXECUTION_DATE = QName.createQName(ORD_TABLE_NAMESPACE, "execution-date");
	public final static QName ASSOC_ORD_TABLE_EXECUTOR = QName.createQName(ORD_TABLE_NAMESPACE, "executor-assoc");
	public final static QName ASSOC_ORD_TABLE_ERRAND = QName.createQName(ORD_TABLE_NAMESPACE, "errand-assoc");
	public final static QName ASSOC_ORD_TABLE_ITEM_STATUS = QName.createQName(ORD_TABLE_NAMESPACE, "item-status-assoc");
	public final static QName ASSOC_ORD_SIGNERS = QName.createQName(ORD_NAMESPACE, "signers-assoc");

	public final static QName ASSOC_DOCUMENT_FILE_REGISTER_UNIT = QName.createQName(DOCUMENT_FILE_REGISTER_NAMESPACE, "organization-unit-assoc");

	public static final String ORD_POINT_DICTIONARY_NAME = "Статусы пунктов ОРД";
	public static final String ORD_POINT_PERFORMANCE_STATUS = "На исполнении";
	public static final String ORD_POINT_EXECUTED_STATUS = "Исполнен";
	public static final String ORD_POINT_NOT_EXECUTED_STATUS = "Не исполнен";
	public static final String ORD_POINT_EXPIRED_STATUS = "Просрочен";

	public static enum P_STATUSES { PERFORMANCE_STATUS, EXECUTED_STATUS, NOT_EXECUTED_STATUS, EXPIRED_STATUS };
	public static final EnumMap<P_STATUSES,String> POINT_STATUSES = new EnumMap<P_STATUSES,String>(P_STATUSES.class){{
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
