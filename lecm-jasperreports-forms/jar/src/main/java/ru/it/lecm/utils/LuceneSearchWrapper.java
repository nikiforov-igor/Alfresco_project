package ru.it.lecm.utils;

import ru.it.lecm.reports.model.impl.ColumnDescriptor;

import java.util.ArrayList;
import java.util.List;


/**
 * Утилитарный класс для построения lucene-поисковых запросов.
 *
 * @author rabdullin
 */
public class LuceneSearchWrapper {

    private StringBuilder bquery;

    // колонки со сложными условиями (доступ к данных через ассоциации)
    final private List<ColumnDescriptor> argsByLinks = new ArrayList<ColumnDescriptor>();

    // колонки простые - с именем свойств
    final private List<ColumnDescriptor> argsByProps = new ArrayList<ColumnDescriptor>();

    public LuceneSearchWrapper() {
    }
    public LuceneSearchWrapper(StringBuilder bquery) {
        this.bquery = bquery;
    }

    public void clear() {
        this.bquery = null;
    }

    public boolean isEmpty() {
        return (this.bquery == null) || (this.bquery.toString().trim().length() == 0);
    }

    public StringBuilder getQuery() {
        if (bquery == null) {
            bquery = new StringBuilder();
        }
        return bquery;
    }

    public void setQuery(StringBuilder bquery) {
        this.bquery = bquery;
    }

    /**
     * Вставить в запрос указанную строку, если она не NULL.
     *
     * @param s String
     * @return LuceneSearchWrapper
     */
    public LuceneSearchWrapper emmit(String s) {
        if (s != null) {
            getQuery().append(s);
        }
        return this;
    }

    public List<ColumnDescriptor> getArgsByLinks() {
        return argsByLinks;
    }

    public List<ColumnDescriptor> getArgsByProps() {
        return argsByProps;
    }

    @Override
    public String toString() {
        return getQuery().toString();
    }
}

