package ru.it.lecm.reports.beans;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.search.ResultSetRow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.base.beans.SubstitudeBean;
import ru.it.lecm.reports.api.DataFieldColumn;
import ru.it.lecm.reports.generators.SubreportBuilder;
import ru.it.lecm.reports.jasper.ReportDSContextImpl;
import ru.it.lecm.reports.model.impl.JavaDataType;

import java.util.*;


public class LinksResolver {

    private static final Logger logger = LoggerFactory.getLogger(LinksResolver.class);

    private WKServiceKeeper services;

    public WKServiceKeeper getServices() {
        return services;
    }

    public void setServices(WKServiceKeeper services) {
        this.services = services;
    }

    /**
     * Проверить является ли указанное поле вычисляемым (в понимании SubstitudeBean):
     * если первый символ "{", то является.
     *
     * @param expression выражение
     * @return true, если является.
     */
    public boolean isSubstCalcExpr(final String expression) {
        return (expression != null) &&
                expression.contains(SubstitudeBean.OPEN_SUBSTITUDE_SYMBOL) &&
                expression.contains(SubstitudeBean.CLOSE_SUBSTITUDE_SYMBOL) &&
                !expression.matches(SubreportBuilder.REGEXP_SUBREPORTLINK);
    }

    /**
     * Выполнить разименование ссылки в нативный тип.}
     *
     * @param docId NodeRef
     * @param column DataFieldColumn
     * @return Object
     */
    public Object evaluateLinkExpr(NodeRef docId, DataFieldColumn column, Map<String, Object> curProps) {
        if (column == null) {
            return null;
        }

        final String linkExpression = (column.getValueLink() != null) ? column.getValueLink() : column.getName();
        final String fldJavaClass = (column.getValueClass() != null) ? column.getValueClassName() : null;

		/*
         * если название имеется среди готовых свойств (прогруженных или вычисленных заранее) ...
		 */
        // (!) пробуем получить значения, указанные "путями" вида {acco1/acco2/.../field} ...
        // (!) если элемент начинается с "{{", то это спец. элемент, который будет обработан проксёй подстановок.
        Object value = null;
        try {
            if (curProps != null && curProps.containsKey(linkExpression)) { // простые свойства и  подотчеты
                value = curProps.get(linkExpression);
            } else if (isSubstCalcExpr(linkExpression)) { // ссылка или выражение ...
                value = services.getSubstitudeService().getNodeFieldByFormat(docId, linkExpression);
            } else { // считаем явно заданной константой ...
                value = linkExpression;
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }

        if (value == null) {
            return !linkExpression.matches(SubreportBuilder.REGEXP_SUBREPORTLINK) ? null : new ArrayList<>();
        }

        // типизация value согласно указанному классу ...
        if (!ru.it.lecm.reports.utils.Utils.isStringEmpty(fldJavaClass)) {
            final JavaDataType.SupportedTypes type = JavaDataType.SupportedTypes.findType(fldJavaClass);
            if (type == null) {
                return value;
            } else {
                // доп условия. Нам может прийти лист NodeRef - преобразуем его к строке.
                if (value instanceof List && type.equals(JavaDataType.SupportedTypes.STRING)) {
                    StringBuilder result = new StringBuilder();
                    for (Object item : ((List)value)) {
                        if (NodeRef.isNodeRef(item.toString())) {
                            result.append(getServices().getSubstitudeService().getObjectDescription((NodeRef) item));
                        } else {
                            result.append(item.toString());
                        }
                        result.append(SubstitudeBean.ASSOC_DELIMITER);
                    }
                    return result.length() > 2 ? result.toString().substring(0, result.length() - 2) : result.toString();
                } else {
                    return type.getValueByRealType(value);
                }
            }
        }
        return value;
    }

    public Iterator sortObjects(Iterator<ResultSetRow> iterator, String sorting, ReportDSContextImpl context) {
        Iterator result = iterator;
        if (sorting != null && !sorting.isEmpty() && iterator.hasNext()) {
            String[] sortSettings = sorting.split(",");

            TreeMap<MultiplySortObject, Set<ResultSetRow>> treeMap = new TreeMap<>();

            while (iterator.hasNext()) {
                ResultSetRow next = iterator.next();
                context.setCurNodeRef(next.getNodeRef());

                MultiplySortObject sortedObj = new MultiplySortObject();

                for (String sortSetting : sortSettings) {
                    String[] sortArray = sortSetting.split("\\|");
                    String columnCode = sortArray[0];
                    boolean asc = true; //ASC
                    if (sortArray.length == 2) {
                        asc = sortArray[1].equalsIgnoreCase("ASC");
                    }

                    Object property = context.getPropertyValueByJRField(columnCode);
                    if (property == null) {
                        property = "";
                    }

                    sortedObj.addSort((Comparable) property, asc);
                }

                if (treeMap.get(sortedObj) == null) {
                    treeMap.put(sortedObj, new HashSet<ResultSetRow>());
                }
                treeMap.get(sortedObj).add(next);
            }

            List<ResultSetRow> list = new ArrayList<>();
            for (MultiplySortObject multiplySortObject : treeMap.keySet()) {
                list.addAll(treeMap.get(multiplySortObject));
            }
            result = list.iterator();
        }

        return result;
    }
}

