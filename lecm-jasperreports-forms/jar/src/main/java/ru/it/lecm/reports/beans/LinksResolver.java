package ru.it.lecm.reports.beans;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.search.ResultSetRow;
import org.alfresco.util.PropertyCheck;
import ru.it.lecm.base.beans.SubstitudeBean;
import ru.it.lecm.reports.jasper.ProxySubstitudeBean;
import ru.it.lecm.reports.jasper.ReportDSContextImpl;
import ru.it.lecm.reports.model.impl.JavaDataTypeImpl;
import ru.it.lecm.reports.utils.ArgsHelper;

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
        return (expression != null) && expression.contains(SubstitudeBean.OPEN_SUBSTITUDE_SYMBOL) && expression.contains(SubstitudeBean.CLOSE_SUBSTITUDE_SYMBOL);
    }

    /**
     * Выполнить разименование указанной ссылки и приведение типа.
     *
     * @param docId          id узла, относительно которого задано выражение
     * @param linkExpression выражение для разименования
     *                       ключь здесь - само выражение, если под таким ключом будет элемент, то
     *                       именно его значение станет результатом.
     * @return вычисленное выражение
     */
    public Object evaluateLinkExpr(NodeRef docId, String linkExpression) {
        return evaluateLinkExpr(docId, linkExpression, null, new HashMap<String, Object>());
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

        final ProxySubstitudeBean substService = new ProxySubstitudeBean();
        substService.setRealBean(services.getSubstitudeService());

		/*
         * если название имеется среди готовых свойств (прогруженных или вычисленных заранее) ...
		 */
        // (!) пробуем получить значения, указанные "путями" вида {acco1/acco2/.../field} ...
        // (!) если элемент начинается с "{{", то это спец. элемент, который будет обработан проксёй подстановок.
        Object value;
        if (curProps != null && curProps.containsKey(linkExpression)) {
            value = curProps.get(linkExpression);
        } else if (isSubstCalcExpr(linkExpression)) { // ссылка или выражение ...
            value = substService.getNodeFieldByFormat(docId, linkExpression);
        } else { // считаем явно заданной константой ...
            value = linkExpression;
        }

        if (value == null) {
            return null;
        }

        // типизация value согласно указанному классу ...
        if (!ru.it.lecm.reports.utils.Utils.isStringEmpty(destClassName)) {
            // TODO: метод для восстановления реального типа данных ...
            final JavaDataTypeImpl.SupportedTypes type = JavaDataTypeImpl.SupportedTypes.findType(destClassName);
            if (type == null) {
                return value;
            } else {
                String strValue = value.toString();
                switch (type) {
                    case DATE: {
                        value = (strValue.isEmpty()) ? null : ArgsHelper.tryMakeDate(strValue, null);
                        break;
                    }
                    case BOOL: {
                        value = Boolean.valueOf(strValue);
                        break;
                    }
                    case FLOAT: {
                        value = (strValue.isEmpty()) ? null : Float.valueOf(strValue);
                        break;
                    }
                    case INTEGER: {
                        value = (strValue.isEmpty()) ? null : Integer.valueOf(strValue);
                        break;
                    }
                    case LIST: {
                        break;
                    }
                    default: { // case STRING:
                        value = strValue;
                        break;
                    }
                } // switch
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
                    String propertyStr = "";
                    if (property != null) {
                        propertyStr = property.toString();
                    }
                    sortedObj.addSort(propertyStr, asc);
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

