package ru.it.lecm.businessjournal.remote;

import ru.it.lecm.businessjournal.beans.BusinessJournalRecord;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * User: pmelnikov
 * Date: 02.12.13
 * Time: 16:31
 */
public interface BusinessJournalStore {


    /**
     * Метод сохранения записи в хранилище
     * @param record
     */
    public void save(BusinessJournalRecord record);

    /**
     * Метод, возвращающий список ссылок на записи бизнес-журнала, сформированные за заданный период
     *
     * @param begin - начальная дата
     * @param end   - конечная дата
     * @return список ссылок
     */
    List<BusinessJournalRecord> getRecordsByInterval(Date begin, Date end);

    /**
     * Метод, возвращающий список ссылок на записи заданного типа(типов),
     * сформированные за заданный период с учетом инициатора
     *
     * @param objectTypeRefs  - тип объекта (или типы, разделенные запятой)
     * @param begin           - начальная дата
     * @param end             - конечная дата
     * @param whoseKey        - список инициаторов
     * @param skipCount       - пропустить первые skipCount записей
     * @param maxItems        - ограничить размер выдачи
     * @return список ссылок
     */
    List<BusinessJournalRecord> getRecordsByParams(String objectTypeRefs, Date begin, Date end, List<Long> whoseKey, Integer skipCount, Integer maxItems);

    /**
     * Метод, перемещающий заданную запись в архив
     *
     * @return boolean результат выполнения операции
     */
    boolean moveRecordToArchive(Long recordId);

    /**
     *
     * @param nodeId - Идентификатор документа
     * @param sortField - Имя поля для сортировки
     * @param sortAscending - прямая сортировка
     * @return
     */
    List<BusinessJournalRecord> getStatusHistory(Long nodeId, BusinessJournalRecord.Field sortField, boolean sortAscending);

    /**
     *
     * @param nodeId
     * @param sortField
     * @param sortAscending
     * @param includeSecondary
     * @param showInactive
     * @return
     */
    List<BusinessJournalRecord> getHistory(Long nodeId, BusinessJournalRecord.Field sortField, boolean sortAscending, boolean includeSecondary, boolean showInactive);

    List<BusinessJournalRecord> getLastRecords(int maxRecordsCount, boolean includeFromArchive);

    List<BusinessJournalRecord> getRecords(BusinessJournalRecord.Field sortField, boolean ascending, int startIndex, int maxResults, Map<BusinessJournalRecord.Field, String> filter, boolean andFilter, boolean includeArchived);

    Long getRecordsCount(Map<BusinessJournalRecord.Field, String> filter, boolean andFilter, boolean includeArchived);

}
