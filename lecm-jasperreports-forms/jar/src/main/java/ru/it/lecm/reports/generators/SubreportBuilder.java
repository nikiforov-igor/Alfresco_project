package ru.it.lecm.reports.generators;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.PropertyCheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.base.beans.SubstitudeBean;
import ru.it.lecm.reports.model.impl.ItemsFormatDescriptor;
import ru.it.lecm.reports.api.model.ReportDescriptor;
import ru.it.lecm.reports.beans.LinksResolver;
import ru.it.lecm.reports.beans.MultiplySortObject;
import ru.it.lecm.reports.model.impl.SubReportDescriptorImpl;
import ru.it.lecm.reports.utils.Utils;
import ru.it.lecm.utils.NodeUtils;

import java.io.Serializable;
import java.util.*;

/**
 * Построитель для подотчётов на основе {@link SubReportDescriptorImpl}.
 * <br/> Выполняется сбор свойств по всем узлам из вложенного в основной документ списка.
 * <br/> Результатом будет:
 * <li> либо <b> ОДНА отформатированная СТРОКА </b>
 * <li> либо <b> СПИСОК бинов. </b>
 * <br/> Описатели подотчётов {@link SubReportDescriptorImpl} являются частью
 * {@link ReportDescriptor} и отнаследованы от него.
 * <br/> имеют:
 * <li> класс бина или формат в случае единой строки,</li>
 * <li> список атрибутов для присвоения и источники данных для них
 * <br/>(списком атрибутов или ассоциаций Альфреско).</li>
 *
 * @author rabdullin
 */
public class SubreportBuilder {
    final static public String REGEXP_SUBREPORTLINK = "[{]{0,2}subreport[:][:]([^}]+)([}]{0,2})";

    private static final Logger logger = LoggerFactory.getLogger(SubreportBuilder.class);

    final private SubReportDescriptorImpl subreport;
    final private LinksResolver resolver;

    /**
     * Простые одиночные поля-значения, которые заполняются непосредственным
     * получением свойств Альфреско или провайдером.
     * <b>
     * <br/>   ключи = названия свойств бина
     * <br/>   значения = ссылки на Альфреско атрибуты или ассоциации
     * </b>
     */
    protected Map<String, String[]> beanFields;

    /**
     * Списочные значения (помеченные "звёздочкой").
     * <br/>Фактически это ссылки на вложения (ассоциации).
     * <b>
     * <br/>   ключи = название свойства бина для присвоения списка (с типом List<String>).
     * <br/>   значения = описание для построения списка
     * </b>
     */
    protected Map<String, String> beanLists;

    public SubreportBuilder(SubReportDescriptorImpl subReportDesc, LinksResolver resolver) {
        super();
        this.subreport = subReportDesc;
        this.resolver = resolver;
        applySubItemsSourceMap();
    }

    private void clear() {
        this.beanFields = null;
        this.beanLists = null;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("%s [", this.getClass().getName()));
        builder.append("nodeService ").append((getNodeService() != null ? "ASSIGNED" : "(!?) NULL"));
        builder.append(", nameService ").append((getNameService() != null ? "ASSIGNED" : "(!?) NULL"));
        builder.append(", substService ").append((getSubstService() != null ? "ASSIGNED" : "(!?) NULL"));
        builder.append("\n\t beanFields ").append((beanFields == null ? "NULL" : beanFields));
        builder.append("\n\t beanLists ").append((beanLists == null ? "NULL" : beanLists));
        builder.append("\n\t subreport ").append((getSubreport() == null ? "NULL" : getSubreport()));
        builder.append("\n]");
        return builder.toString();
    }

    public SubReportDescriptorImpl getSubreport() {
        return subreport;
    }

    public NodeService getNodeService() {
        return (resolver == null) ? null : resolver.getServices().getServiceRegistry().getNodeService();
    }

    public NamespaceService getNameService() {
        return (resolver == null) ? null : resolver.getServices().getServiceRegistry().getNamespaceService();
    }

    public SubstitudeBean getSubstService() {
        return (resolver == null) ? null : resolver.getServices().getSubstitudeService();
    }

    /**
     * Присвоить конфигурационный список соот-вий для источников данных и
     * целевых полей бина или формата.
     * <br/> Выполняется разделение общего списка на два:
     * <li> для получения одиночных значений
     * <li> для получения списочных значений.
     * <br/> (простые атрибуты или составные могут быть в обоих списках)
     */
    private void applySubItemsSourceMap() {
        clear();

        final Map<String, String> cfg = subreport.getSubItemsSourceMap();

        if (cfg == null) {
            return;
        }

        this.beanFields = new HashMap<String, String[]>();
        this.beanLists = new HashMap<String, String>();

        for (Map.Entry<String, String> e : cfg.entrySet()) {
            final String propName = Utils.trimmed(e.getKey()); // ключ = название свойства

            if (e.getValue() == null) {
                continue;
            }

            final String svalue = e.getValue().trim();
            if (svalue.length() == 0) {
                // нет значения - ничего не присваиваем ...
                continue;
            }

			/* свойства для бина и соот-щие ссылки ... */
            if (propName.startsWith(ItemsFormatDescriptor.LIST_MARKER)) {
                // списочное значение ...
                final String propNameOnly = propName.substring(ItemsFormatDescriptor.LIST_MARKER.length()); // имя без "звёздочки"
                this.beanLists.put(propNameOnly, svalue);
                continue;
            }

             /* здесь имеем обычное поле или ссылка на ОДНО значение */
            // значения могут содержать списки ...
            final String[] sourceLinks = svalue.split("[;,]");
            if (sourceLinks == null || sourceLinks.length == 0) {
                logger.warn(String.format("Configuration data has blank sourceLinks for item '%s' -> skipped", propName));
                continue;
            }
            this.beanFields.put(propName, sourceLinks);

        } // for i
    }

    protected Object makeSubItem(Map<String, Object> args, int subItemNum) {
        if (args == null) {
            return null;
        }

        // автоматически вносим нумератор строки
        args.put(SubReportDescriptorImpl.BEAN_PROPNAME_COL_ROWNUM, String.valueOf(subItemNum));

        if (subreport.isUsingFormat()) { /* форматирование всех свойств объекта в строку ... */
            final String resultStr = formatArgs(args);
            return Utils.expandFromCharPairs(resultStr);
        }

            return args;
    }

    /**
     * Форматирование свойств текущей строки согласно формату fmt.
     *
     * @param args текущие свойства строки
     * @return форматированную строку
     */
    protected String formatArgs(final Map<String, Object> args) {
        String resultStr = subreport.getItemsFormat().getFormatString();
        if (resultStr != null && args != null) {
            for (final Map.Entry<String, Object> e : args.entrySet()) {
                final String propName = e.getKey();
                final String value = (e.getKey() == null) ? "" : String.format("%s", e.getValue());
                resultStr = resultStr.replaceAll("%" + propName, value);
            }
        }
        return resultStr;
    }

    /**
     * Получить сконфигурированные свойства вложенного объекта.
     * <br/> см также {@link #subreport}, {@link #beanFields} и {@link #beanLists}
     *
     * @param subItemId NodeRef
     * @return <b>ключ</b> = название колонки или свойства,
     *         <br/><b>значение</b> = соот-щий объект, полученный по ссылке для колонки согласно {subItemsSourceMap}
     */
    protected Map<String, Object> gatherSubItemInfo(NodeRef subItemId) {
        PropertyCheck.mandatory(this, "resolver", resolver);
        PropertyCheck.mandatory(this, "resolver.getServices()", resolver.getServices());

		/* разименованные значения */
        final Map<String, Object> values = new HashMap<String, Object>();

		/* присвоение свойств */
        if (beanFields != null) {
            final Map<QName, Serializable> props = getNodeService().getProperties(subItemId);

            for (Map.Entry<String, String[]> e : beanFields.entrySet()) {
                // имена могут содержать списки ...
                final String propName = e.getKey();
                final String[] sourceLinks = e.getValue();

                if (sourceLinks == null || sourceLinks.length == 0) {
                    logger.warn(String.format("Configuration data has blank sourceLinks for item '%s' -> skipped", propName));
                    continue;
                }

                // проходим по списку до первого непустого значения ...
                Object fldValue = null;
                for (String sourceLink : sourceLinks) {
                    sourceLink = sourceLink.trim();
                    if (sourceLink.length() > 0) {
                        final Object value = NodeUtils.getByLink(sourceLink, subItemId, props, resolver, this);
                        if (value != null && !((value instanceof String) && ((String) value).length() == 0)) {
                            // found non-null / not-empty
                            fldValue = value;
                            break; // for
                        }
                    }
                } // for sourceLink
                values.put(propName, fldValue);
            } // for i
        }

		/* Отдельные списки (например, сюда попадут Соисполнители) */
        if (beanLists != null) {
            for (Map.Entry<String, String> ent : beanLists.entrySet()) {
                final String propName = ent.getKey();
                final String src = ent.getValue();
                Object subList = buildSubreport(subItemId, src);
                values.put(propName, subList != null ? subList  : new ArrayList());
            } // for
        }

        return values;
    }

    /**
     * Выполнить построение бина подотчёта согласно текущего описателя и
     * указанного базового документа.
     *
     * @param docId id объекта, относительно которого надо получить вложенный список
     * @return получить вложенный список у основного объекта и построить для
     *         него результат согласно текущему описателю {@link #subreport} в виде:
     *         <li>   строки, когда используется форматирование,
     *         <li>   или List-а объектов типа #subreport.beanClassName
     */
    public Object buildSubreport(NodeRef docId, String sourceListExpression) {
        final boolean usingFormat = subreport.isUsingFormat();

        /* получение ассоциированного списка ... */
        List<NodeRef> children = getSubstService().getObjectsByTitle(docId, sourceListExpression);

        if (children == null || children.isEmpty()) { // нет вложенных ...
            // если исопльзуется форматирование - вернуть его "пустую строку" ...
            return (usingFormat) ? subreport.getItemsFormat().getIfEmptyTag() : null;
        }

		/* 
         * формирование списка бинов или строк проходом по
		 * ассоциированному/вложенному списку...
		 */
        final List<Object> result = new ArrayList<Object>();

        final Set<String> childTypes = subreport.getSourceListType();

        List<Map<String, Object>> unsortedSubs = new ArrayList<Map<String, Object>>();
        for (NodeRef childId : children) {
            if (childTypes != null && !childTypes.isEmpty()) {
                //фильтруем по типу
                String currentType = getNodeService().getType(childId).toPrefixString(getNameService());
                if (childTypes.contains(currentType)) {
                    unsortedSubs.add(gatherSubItemInfo(childId));
                }
            } else {
                unsortedSubs.add(gatherSubItemInfo(childId));
            }
        } // for

        List<Map<String, Object>> sortedSubs;

        String sortSettings = subreport.getFlags().getSort();
        if (sortSettings != null && !sortSettings.isEmpty()) {
            String[] sortSettingsArr = sortSettings.split(",");
            TreeMap<MultiplySortObject, Set<Map<String, Object>>> treeMap = new TreeMap<MultiplySortObject, Set<Map<String, Object>>>();

            for (Map<String, Object> current : unsortedSubs) {
                MultiplySortObject sortedObj = new MultiplySortObject();

                for (String sortSetting : sortSettingsArr) {
                    String[] sortArray = sortSetting.split("\\|");
                    String columnCode = sortArray[0];
                    boolean asc = true; //ASC
                    if (sortArray.length == 2) {
                        asc = sortArray[1].equalsIgnoreCase("ASC");
                    }

                    Object property = current.get(columnCode);
                    sortedObj.addSort(property != null ? (Comparable) property : null, asc);
                }

                if (treeMap.get(sortedObj) == null) {
                    treeMap.put(sortedObj, new HashSet<Map<String, Object>>());
                }
                treeMap.get(sortedObj).add(current);
            }

            List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
            for (MultiplySortObject multiplySortObject : treeMap.keySet()) {
                list.addAll(treeMap.get(multiplySortObject) != null ? treeMap.get(multiplySortObject) : new ArrayList<Map<String, Object>>());
            }
            sortedSubs = list;
        } else {
            sortedSubs = unsortedSubs;
        }

        int i = 0;
        for (Map<String, Object> sortedSub : sortedSubs) {
            i++; // нумерация от единицы
            final Object item = makeSubItem(sortedSub, i);
            if (item != null) {
                result.add(item);
            }
        }

        if (usingFormat) { // форматирование всех вложенных в одну строку ...
            return Utils.getAsString(result, subreport.getItemsFormat().getItemsDelimiter());
        }

        // использование как списка бинов ...
        return result;
    }
}

