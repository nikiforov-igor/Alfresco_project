package ru.it.lecm.reports.beans;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.search.ResultSetRow;
import org.alfresco.util.PropertyCheck;
import ru.it.lecm.base.beans.SubstitudeBean;
import ru.it.lecm.reports.generators.SubreportBuilder;
import ru.it.lecm.reports.jasper.ReportDSContextImpl;
import ru.it.lecm.reports.model.impl.JavaDataType;

import java.util.*;


public class LinksResolver {

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
     * @param linkExpression String
     * @return Object
     */
    public Object evaluateLinkExpr(NodeRef docId, String linkExpression, String destClassName, Map<String, Object> curProps) {
        if (linkExpression == null) {
            return null;
        }

        PropertyCheck.mandatory(this, "services", services);
        PropertyCheck.mandatory(this, "services.getSubstitudeService", services.getSubstitudeService());

		/*
         * если название имеется среди готовых свойств (прогруженных или вычисленных заранее) ...
		 */
        // (!) пробуем получить значения, указанные "путями" вида {acco1/acco2/.../field} ...
        // (!) если элемент начинается с "{{", то это спец. элемент, который будет обработан проксёй подстановок.
        Object value;
        if (curProps != null && curProps.containsKey(linkExpression)) {
            value = curProps.get(linkExpression);
        } else if (isSubstCalcExpr(linkExpression)) { // ссылка или выражение ...
            value = services.getSubstitudeService().getNodeFieldByFormat(docId, linkExpression);
        } else { // считаем явно заданной константой ...
            value = linkExpression;
        }

        if (value == null) {
            return null;
        }

        // типизация value согласно указанному классу ...
        if (!ru.it.lecm.reports.utils.Utils.isStringEmpty(destClassName)) {
            final JavaDataType.SupportedTypes type = JavaDataType.SupportedTypes.findType(destClassName);
            if (type == null) {
                return value;
            } else {
                return type.getValueByRealType(value);
            }
        }
        return value;
    }

    public Iterator sortObjects(Iterator<ResultSetRow> iterator, String sorting, ReportDSContextImpl context) {
        Iterator result = iterator;
        if (sorting != null && !sorting.isEmpty() && iterator.hasNext()) {
            String[] sortSettings = sorting.split(",");

            TreeMap<MultiplySortObject, Set<org.alfresco.service.cmr.search.ResultSetRow>> treeMap = new TreeMap<MultiplySortObject, Set<ResultSetRow>>();

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

            List<ResultSetRow> list = new ArrayList<ResultSetRow>();
            for (MultiplySortObject multiplySortObject : treeMap.keySet()) {
                list.addAll(treeMap.get(multiplySortObject));
            }
            result = list.iterator();
        }

        return result;
    }
}

