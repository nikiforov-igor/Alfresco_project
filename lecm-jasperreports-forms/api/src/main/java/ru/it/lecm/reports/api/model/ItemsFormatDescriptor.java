package ru.it.lecm.reports.api.model;

/**
 * User: dbashmakov
 * Date: 02.12.13
 * Time: 10:58
 */
/**
 * Описатель для форматирования списка элементов в строку.
 */
public interface ItemsFormatDescriptor {

    /**
     * Маркер перед именем свойства бина для случая когда требуется
     * формирование одной строки для всего списка вложенных элементов.
     * <b><br/> Если маркета нет, то из списка будет браться только первый элемент.</b>
     */
    public static final String LIST_MARKER = "*";

    /**
     * @return форматная строка для объединения значений колонок НД dsSourceDescriptor
     */
    String getFormatString();
    void setFormatString(String formatString);

    /**
     * @return значение, которое надо выводить если список вложенных
     * объектов пуст, по-умолчанию пустая строка.
     */
    String getIfEmptyTag();
    void setIfEmptyTag(String tag);

    /**
     * Разделитель элементов в списке, если используется форматирование
     * всех элементов в одну строку
     */
    String getItemsDelimiter();
    void setItemsDelimiter(String delimiter);
}