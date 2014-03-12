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
	public final static String ORD_TABLE_NAMESPACE = "http://www.it.ru/lecm/ORD/table-structure/1.0";

	public final static QName TYPE_ORD = QName.createQName(ORD_NAMESPACE, "document");
	public final static QName ASSOC_ORD_REGISTRAR = QName.createQName(ORD_NAMESPACE, "registrar-assoc");
	public final static QName ASSOC_ORD_CONTROLLER = QName.createQName(ORD_NAMESPACE, "controller-assoc");

	public final static QName ASSOC_ORD_TABLE_ITEMS = QName.createQName(ORD_TABLE_NAMESPACE, "items-assoc");
	public final static QName TYPE_ORD_TABLE_ITEM = QName.createQName(ORD_TABLE_NAMESPACE, "item");
	public final static QName PROP_ORD_TABLE_ITEM_CONTENT = QName.createQName(ORD_TABLE_NAMESPACE, "item-content");
	public final static QName PROP_ORD_TABLE_EXECUTION_DATE = QName.createQName(ORD_TABLE_NAMESPACE, "execution-date");
	public final static QName ASSOC_ORD_TABLE_EXECUTOR = QName.createQName(ORD_TABLE_NAMESPACE, "executor-assoc");
	public final static QName ASSOC_ORD_TABLE_ERRAND = QName.createQName(ORD_TABLE_NAMESPACE, "errand-assoc");
	public final static QName ASSOC_ORD_TABLE_ITEM_STATUS = QName.createQName(ORD_TABLE_NAMESPACE, "item-status-assoc");

	public static final String ORD_POINT_DICTIONARY_NAME = "Статусы пунктов ОРД";
	public static final String ORD_POINT_PERFORMANCE_STATUS = "На исполнении";

	private ORDModel() throws IllegalAccessException {
		throw new IllegalAccessException("You cannot create any instance of ORDModel class.");
	}
}
