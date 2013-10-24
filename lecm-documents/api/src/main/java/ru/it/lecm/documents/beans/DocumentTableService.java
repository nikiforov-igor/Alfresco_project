package ru.it.lecm.documents.beans;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;

import java.util.List;

/**
 * User: AIvkin
 * Date: 18.10.13
 * Time: 13:42
 */
public interface DocumentTableService {
	public static final QName TYPE_TABLE_DATA_ROW = QName.createQName(DocumentService.DOCUMENT_NAMESPACE_URI, "tableDataRow");
	public static final QName TYPE_TABLE_DATA_TOTAL_ROW = QName.createQName(DocumentService.DOCUMENT_NAMESPACE_URI, "tableDataTotalRow");

	public static final String DOCUMENT_TABLES_ROOT_NAME = "Табличные данные";
	public static final String DOCUMENT_TABLE_TOTAL_ASSOC_POSTFIX = "-total";

	/**
	 * Получение папки с табличными данными для документра
	 * @param documentRef документ
	 * @return Ссылка на папку с вложениями
	 */
	public NodeRef getRootFolder(final NodeRef documentRef);

	/**
	 * Проверка, что объект является табличными данными документа
	 * @param nodeRef объект
	 * @return true если объект является табличными данными документа
	 */
	public boolean isDocumentTableData(NodeRef nodeRef);

	/**
	 * Получения документа для табличных данных
	 * @param tableDataRef табличные данные
	 * @return документ
	 */
	public NodeRef getDocumentByTableData(NodeRef tableDataRef);

	/**
	 * Получение
	 * @param tableDataRef табличные данные
	 * @return документ
	 */
	public List<NodeRef> getTableDataTotalRows(NodeRef tableDataRef);

	/**
	 * Пересчёт результирующей строки данных
	 * @param document документ
	 * @param tableDataAssocType тип ассоциации для табличных данных
	 * @param createIfNotExist создать, если не существует
	 * @return результирующие строки
	 */
	public List<NodeRef> getTableDataTotalRows(NodeRef document, QName tableDataAssocType, boolean createIfNotExist);

	/**
	 * Пересчёт результирующей строки данных
	 * @param properties Список атрибутов для пересчёта, если null то будут пересчитаны все свойства
	 * @param row результирующая строка для обновления
	 */
	public void recalculateTotalRow(NodeRef row, List<QName> properties);

	/**
	 * Пересчёт результирующей строки данных
	 * @param properties Список атрибутов для пересчёта, если null то будут пересчитаны все свойства
	 * @param rows результирующие строки для обновления
	 */
	public void recalculateTotalRows(List<NodeRef> rows, List<QName> properties);
}
