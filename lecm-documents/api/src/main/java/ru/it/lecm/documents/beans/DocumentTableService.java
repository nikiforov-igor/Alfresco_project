package ru.it.lecm.documents.beans;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import ru.it.lecm.base.beans.WriteTransactionNeededException;
import ru.it.lecm.documents.TableTotalRowCalculator;

import java.util.List;
import java.util.Set;

/**
 * User: AIvkin
 * Date: 18.10.13
 * Time: 13:42
 */
public interface DocumentTableService {
	public static final QName TYPE_TABLE_DATA = QName.createQName(DocumentService.DOCUMENT_NAMESPACE_URI, "tableData");
	public static final QName TYPE_TABLE_DATA_ROW = QName.createQName(DocumentService.DOCUMENT_NAMESPACE_URI, "tableDataRow");
	public static final QName TYPE_TABLE_DATA_TOTAL_ROW = QName.createQName(DocumentService.DOCUMENT_NAMESPACE_URI, "tableDataTotalRow");

    public static final QName PROP_INDEX_TABLE_ROW = QName.createQName(DocumentService.DOCUMENT_NAMESPACE_URI, "indexTableRow");
    public static final QName PROP_TABLE_ROW_TYPE = QName.createQName(DocumentService.DOCUMENT_NAMESPACE_URI, "tableDataRowType");
    public static final QName PROP_TABLE_TOTAL_ROW_TYPE = QName.createQName(DocumentService.DOCUMENT_NAMESPACE_URI, "tableDataTotalRowType");

	public static final QName ASPECT_TABLE_DATA = QName.createQName(DocumentService.DOCUMENT_NAMESPACE_URI, "tableDataAspect");

	public static final String DOCUMENT_TABLES_ROOT_NAME = "Табличные данные";

	/**
	 * Получение папки с табличными данными для документра
	 * @param documentRef документ
	 * @return Ссылка на папку с вложениями
	 */
	public NodeRef getRootFolder(final NodeRef documentRef);

	/**
	 * Получение папки с табличными данными для документра
	 * или ее создание в случае отсутствия
	 * @param documentRef документ
	 * @return Ссылка на папку с вложениями
	 * @throws WriteTransactionNeededException
	 */
	//public NodeRef getOrCreateRootFolder(final NodeRef documentRef) throws WriteTransactionNeededException;

	/**
	 * Создание папки с табличными данными для документра
	 * или ее создание в случае отсутствия
	 * @param documentRef документ
	 * @return Ссылка на папку с вложениями
	 * @throws WriteTransactionNeededException
	 */
	public NodeRef createRootFolder(final NodeRef documentRef) throws WriteTransactionNeededException;

	/**
	 * Проверка, что объект является табличными данными документа
	 * @param nodeRef объект
	 * @return true если объект является табличными данными документа
	 */
	public boolean isDocumentTableData(NodeRef nodeRef);

	/**
	 * Проверка, что объект является строкой табличных данных документа
	 * @param nodeRef объект
	 * @return true если объект является строкой табличных данных документа
	 */
	public boolean isDocumentTableDataRow(NodeRef nodeRef);

	/**
	 * Получения документа для табличных данных
	 * @param tableDataRowRef строка табличных данных
	 * @return документ
	 */
	public NodeRef getDocumentByTableDataRow(NodeRef tableDataRowRef);

    /**
     * Получения документа для табличных данных
     * @param tableDataRef табличные данные
     * @return документ
     */
    public NodeRef getDocumentByTableData(NodeRef tableDataRef);

    /**
	 * Получения табличных данных для строки
	 * @param tableDataRowRef строка табличных данных
	 * @return табличные данные
	 */
	public NodeRef getTableDataByRow(NodeRef tableDataRowRef);

	/**
	 * Получение результирующих строк
	 * @param tableDataRef табличные данные
	 * @return результирующие строки
	 */
	public List<NodeRef> getTableDataTotalRows(NodeRef tableDataRef);

	/**
	 * Получение строк табличных данных
	 * @param tableDataRef табличные данные
	 * @return строки
	 */
	public List<NodeRef> getTableDataRows(NodeRef tableDataRef);

        
        public NodeRef copyTableData(NodeRef document, NodeRef tableDataRef); 
        
	/**
	 * Создание результирующей строки
	 * @param tableDataRef табличные данные
	 * @return результирующая строка
	 */
	public NodeRef createTotalRow(NodeRef tableDataRef);

	/**
	 * Пересчёт результирующей строки данных
	 * @param tableDataRef табличные данные
	 * @param row результирующая строка для обновления
	 * @param properties Список атрибутов для пересчёта, если null то будут пересчитаны все свойства
	 */
	public void recalculateTotalRow(NodeRef tableDataRef, NodeRef row, Set<QName> properties);

	/**
	 * Пересчёт результирующей строки данных
	 * @param tableDataRef табличные данные
	 * @param properties Список атрибутов для пересчёта, если null то будут пересчитаны все свойства
	 */
	public void recalculateTotalRows(NodeRef tableDataRef, Set<QName> properties);

	/**
	 * Пересчёт результирующей строки данных
	 * @param tableDataRef табличные данные
	 */
	public void recalculateTotalRows(NodeRef tableDataRef);

	/**
	 * Инициализация нового калькулятора
	 * @param postfix Постфик для поля
	 * @param calculator Класс калькулятора
	 */
	public void addCalculator(String postfix, TableTotalRowCalculator calculator);

    /**
     * Получение списка табличных записей начиная с определенного индекса
     * @param tableDataRef табличные данные
     * @param beginIndex номер индекса
     * @return список табличных записей
     */
    public List<NodeRef> getTableDataRows(NodeRef tableDataRef, int beginIndex);

    /**
     * Получение списка табличных записей начиная с определенного индекса
     * @param tableDataRef табличные данные
     * @param beginIndex номер начального индекса
     * @param endIndex номер конечного индекса
     * @return список табличных записей
     */
    public List<NodeRef> getTableDataRows(NodeRef tableDataRef, int beginIndex, int endIndex);

    /**
     * Переместить запись вверх, присвоить текущей табличной записи номер предыдущей табличной записи,
     * а предыдущей присвоить номер текущей
     * @param tableRow строка табличных данных
     * @return NodeRef Записи с которой произошёл обмен
     */
    public String moveTableRowUp(NodeRef tableRow);

    /**
     * Переместить запись вниз, присвоить текущей табличной записи номер следующей табличной записи,
     * а следующей присвоить номер текущей
     * @param tableRow строка табличных данных
     * @return NodeRef Записи с которой произошёл обмен
     */
    public String moveTableRowDown(NodeRef tableRow);

	public NodeRef getTable(NodeRef document, QName tableType);

	public void recalculateSearchDescription(NodeRef tableData);
}
