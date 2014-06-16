package ru.it.lecm.base.scripts;

import ru.it.lecm.base.beans.BaseWebScript;
import ru.it.lecm.base.beans.SearchQueryProcessorService;

/**
 * User: dbashmakov
 * Date: 16.06.2014
 * Time: 11:21
 */
public class SearchQueryProcessorScript extends BaseWebScript {

    private SearchQueryProcessorService processorService;

    public void setProcessorService(SearchQueryProcessorService processorService) {
        this.processorService = processorService;
    }

    /**
     * Получить запрос по коду процессора
     *
     * @param id     код процессора
     * @param params параметры
     */
    @SuppressWarnings("unused")
    public String getProcessorQuery(String id, String params) {
        return processorService.getProcessorQuery(id, params);
    }
    /**
     * Обработать запрос процессорами
     *
     * @param query запрос для обработки
     */
    @SuppressWarnings("unused")
    public String processQuery(String query) {
        return processorService.processQuery(query);
    }
}
