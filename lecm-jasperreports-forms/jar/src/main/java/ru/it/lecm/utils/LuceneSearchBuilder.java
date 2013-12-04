package ru.it.lecm.utils;

import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.PropertyCheck;
import ru.it.lecm.reports.utils.Utils;

import java.util.Arrays;
import java.util.Collection;


/**
 * Утилитарный класс для построения lucene-поисковых запросов.
 *
 * @author rabdullin
 */
public class LuceneSearchBuilder {

    private StringBuilder bquery;
    private NamespaceService nameService;

    public LuceneSearchBuilder() {
        super();
    }

    public LuceneSearchBuilder(NamespaceService nameService) {
        this(nameService, null);
    }

    public LuceneSearchBuilder(NamespaceService nameService, StringBuilder bquery) {
        super();
        this.nameService = nameService;
        this.bquery = bquery;
    }

    public void clear() {
        this.bquery = null;
    }

    public boolean isEmpty() {
        return (this.bquery == null) || (this.bquery.toString().trim().length() == 0);
    }

    @Override
    public String toString() {
        return getQuery().toString();
    }

    public NamespaceService getNameService() {
        return nameService;
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

    protected boolean emmitOneTypeCond(final String typeName, String prefix, boolean strictCond) {
        if (typeName == null || typeName.trim().isEmpty()) {
            return false;
        }
        PropertyCheck.mandatory(this, "nameService", getNameService());
        final QName qType = QName.createQName(typeName.trim().replace("{", "").replace("}", ""), this.getNameService());
        return emmitTypeCond(qType, prefix, strictCond);
    }

    /**
     * Добавить условие для проверки по типу
     *
     * @param typeNames требующийся тип или список типов, если NULL ничего не добавляется
     * @param prefix    префикс, добавляемый перед условием на тип  (например " AND")
     *                  , может быть NULL
     * @return true, если условие было добавлено
     */
    public boolean emmitTypeCond(final String typeNames, String prefix) {
        if (typeNames == null || typeNames.isEmpty()) {
            return false;
        }
        final String[] values = typeNames.split("\\s*[,;]\\s*");
        return values != null && emmitTypeCond(Arrays.asList(values), prefix);
    }

    public boolean emmitTypeCond(final Collection<String> typeNames, final String prefix) {
        if (typeNames == null || typeNames.isEmpty()) {
            return false;
        }

        if (typeNames.size() == 1) {
            // единичное значение присвоим без доп скобок ...
            return emmitOneTypeCond(typeNames.iterator().next(), prefix, true);
        }

        PropertyCheck.mandatory(this, "nameService", getNameService());
        if (prefix != null) {
            getQuery().append(prefix);
        }
        getQuery().append("+("); // (!) экранируем выражение с проверкой двух и более типов и "плюсом" задаём строгое условие ...
        boolean first = true;
        for (String typeName : typeNames) {
            getQuery().append("\n\t");
            // (!) условие здесь не строгое - плюс выставлен перед скобкой
            if (emmitOneTypeCond(typeName, (first ? "" : "OR "), false)) {
                first = false;
            }
        }
        if (first) {
            // фактически не было добавлено ни одного условия
            getQuery().append(" TYPE:*");
        }
        getQuery().append("\n)\n");

        return true;
    }

    /**
     * Добавить условие для проверки по типу
     *
     * @param qType      требующийся тип, если NULL ничего не добавляется
     * @param prefix     префикс, добавляемый перед условием на тип  (например " AND")
     *                   , может быть NULL
     * @param strictCond true, чтобы условие было строгим (добавляется "+" перед
     *                   типом), false иначе.
     * @return true, если условие было добавлено
     */
    public boolean emmitTypeCond(final QName qType, final String prefix, final boolean strictCond) {
        if (qType == null) {
            return false;
        }
        if (prefix != null) {
            getQuery().append(prefix);
        }
        final String typeTag = (strictCond) ? " +TYPE:" : " TYPE:";
        getQuery().append(typeTag).append(Utils.quoted(qType.toString()));
        return true;
    }

    /**
     * Выполнить вставку условия для проверки равенства поля указанной константе.
     * Экранированные кавычки для значения добавляются автоматически.
     *
     * @param prefix вставляется перед сгенерированным условием, если оно будет получено
     * @param fld    ссылка на поле (экранирование '-' и ':' не требуется)
     * @param value  значение или Null (генерации не будет в этом случае)
     * @return true, если условие было добавлено
     */
    public boolean emmitFieldCond(final String prefix, final String fld, Object value) {
        if (value == null) {
            return false;
        }
        if (prefix != null) {
            getQuery().append(prefix);
        }
        getQuery()
                .append(" +@")
                .append(Utils.luceneEncode(fld))
                .append(":\"")
                .append(value.toString())
                .append("\"");
        return true;
    }

    /**
     * Вставить в запрос указанную строку, если она не NULL.
     *
     * @param s String
     * @return LuceneSearchBuilder
     */
    public LuceneSearchBuilder emmit(String s) {
        if (s != null) {
            getQuery().append(s);
        }
        return this;
    }
}

