package ru.it.lecm.documents.beans;

import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;

import java.util.List;
import java.util.Set;

/**
 * User: AIvkin
 * Date: 18.10.13
 * Time: 13:42
 */
public interface DocumentTableService {
	public static final QName TYPE_TABLE_DATA_ROW = QName.createQName(DocumentService.DOCUMENT_NAMESPACE_URI, "tableDataRow");
	public static final QName TYPE_TABLE_DATA_TOTAL_ROW = QName.createQName(DocumentService.DOCUMENT_NAMESPACE_URI, "tableDataTotalRow");

    public static final QName PROP_INDEX_TABLE_ROW = QName.createQName(DocumentService.DOCUMENT_NAMESPACE_URI, "indexTableRow");

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
	 * @param tableDataType тип табличных данных
	 * @param tableDataAssocType тип ассоциации для табличных данных
	 * @param createIfNotExist создать, если не существует
	 * @return результирующие строки
	 */
	public List<NodeRef> getTableDataTotalRows(NodeRef document, QName tableDataType, QName tableDataAssocType, boolean createIfNotExist);

	/**
	 * Пересчёт результирующей строки данных
	 * @param properties Список атрибутов для пересчёта, если null то будут пересчитаны все свойства
	 * @param tableDataType тип табличных данных
	 * @param tableDataAssocType тип ассоциации для табличных данных
	 * @param row результирующая строка для обновления
	 * @param document документ
	 */
	public void recalculateTotalRow(NodeRef document, NodeRef row, QName tableDataType, QName tableDataAssocType, Set<QName> properties);

	/**
	 * Пересчёт результирующей строки данных
	 * @param properties Список атрибутов для пересчёта, если null то будут пересчитаны все свойства
	 * @param tableDataType тип табличных данных
	 * @param tableDataAssocType тип ассоциации для табличных данных
	 * @param rows результирующие строки для обновления
	 * @param document документ
	 */
	public void recalculateTotalRows(NodeRef document, List<NodeRef> rows, QName tableDataType, QName tableDataAssocType, Set<QName> properties);

	/**
	 * Получение ассоциации между документом и табличными данными
	 * @param tableDataRef табличные данные
	 * @return ассоциация
	 */
	public AssociationRef getDocumentAssocByTableData(NodeRef tableDataRef);

    /**
     * Присвоение индекса табличной записи, MAX+1
     * @param document документ
     * @param tableDataRef табличные данные
     * @param tableDataAssocType тип ассоциации для табличных данных
     */
    public void setIndexTableRow(NodeRef document, NodeRef tableDataRef, QName tableDataAssocType);
}
