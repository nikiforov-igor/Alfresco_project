package ru.it.lecm.errands.shedule.exceptionProcessor;

import java.util.Map;

/**
 * User: IGanin
 * Date: 17.11.2017
 * Time: 17:25
 *
 * Процессор(обработчик) исключительных ситуаций (не путать с Exception)
 */
public interface ExceptionProcessor {
    /**
     * Проверка необходимости запуска процессора
     * @param params параметры для работы процессора
     */
    public boolean checkConditionsToProcess(final Map<ProcessorParamName, Object> params);

    /**
     * Запуск процессора для выполнения действий по обработке исключительной ситуации
     * @param params параметры для работы процессора
     */
    public void processException(final Map<ProcessorParamName, Object> params);
}
