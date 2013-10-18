package ru.it.lecm.documents.beans;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;

/**
 * User: AIvkin
 * Date: 18.10.13
 * Time: 13:42
 */
public interface DocumentTableService {
	public static final QName TYPE_TABLE_DATA_ROW = QName.createQName(DocumentService.DOCUMENT_NAMESPACE_URI, "tableDataRow");

	public static final String DOCUMENT_TABLES_ROOT_NAME = "Табличные данные";

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
}
