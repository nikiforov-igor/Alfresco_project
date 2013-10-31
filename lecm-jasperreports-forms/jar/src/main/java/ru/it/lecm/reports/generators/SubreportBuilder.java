package ru.it.lecm.reports.generators;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.PropertyCheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.it.lecm.base.beans.SubstitudeBean;
import ru.it.lecm.reports.api.model.ReportDescriptor;
import ru.it.lecm.reports.api.model.SubReportDescriptor;
import ru.it.lecm.reports.api.model.SubReportDescriptor.ItemsFormatDescriptor;
import ru.it.lecm.reports.beans.LinksResolver;
import ru.it.lecm.reports.jasper.utils.MacrosHelper;
import ru.it.lecm.reports.utils.Utils;
import ru.it.lecm.utils.NodeUtils;

/**
 * Построитель для подотчётов на основе {@link SubReportDescriptor}.
 * <br/> Выполняется сбор свойств по всем узлам из вложенного в основной документ списка.
 * <br/> Результатом будет:
 * <li> либо <b> ОДНА отформатированная СТРОКА </b>
 * <li> либо <b> СПИСОК бинов. </b>
 * <br/> Описатели подотчётов {@link SubReportDescriptor} являются частью
 * {@link ReportDescriptor} и отнаследованы от него.
 * <br/> имеют:
 * <li> класс бина или формат в случае единой строки,</li>
 * <li> список атрибутов для присвоения и источники данных для них
 * <br/>(списком атрибутов или ассоциаций Альфреско).</li>
 *
 * @author rabdullin
 */
public class SubreportBuilder {

    final public static Class<?> DEFAULT_BEANCLASS = HashMap.class;

    private static final Logger logger = LoggerFactory.getLogger(SubreportBuilder.class);

    final private SubReportDescriptor subreport;
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
    protected Map<String, AssocListInfo> beanLists;

    public SubreportBuilder(SubReportDescriptor subReportDesc, LinksResolver resolver) {
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

    public SubReportDescriptor getSubreport() {
        return subreport;
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
        this.beanLists = new HashMap<String, AssocListInfo>();

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

                final AssocListInfo info = new AssocListInfo();
                info.parseSourceLink(svalue);
                this.beanLists.put(propNameOnly, info);
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

    /**
     * @return true, если текущий класс бина это интерфейс Map или класс HashMap
     *         или если класс не задан
     */
    final public boolean beanClassIsMap() {
        return Utils.isStringEmpty(subreport.getBeanClassName())
                || subreport.getBeanClassName().equals(Map.class.getName())
                || subreport.getBeanClassName().equals(HashMap.class.getName());
    }

    /**
     * Создать пустой бин для задачи (согласно beanClass)
     */
    protected Object createInfoBean() {
        Object result = null;
        try {
            if (!Utils.isStringEmpty(subreport.getBeanClassName())) {
                final Constructor<?> cons = Class.forName(subreport.getBeanClassName()).getConstructor();
                result = cons.newInstance();
            }
        } catch (Throwable ex) {
            // при ошбиках создаём по-умолчанию ...
            final String msg = String.format("Cannot create bean configured as class '%s' -> using default %s\n"
                    , subreport.getBeanClassName(), DEFAULT_BEANCLASS.getName());
            logger.error(msg, ex);
        }

        if (result == null) {
            try {
                result = DEFAULT_BEANCLASS.getConstructor().newInstance();
            } catch (Throwable t) {
                throw new RuntimeException(String.format("Cannot create bean by default type '%s'", DEFAULT_BEANCLASS.getName()));
            }
        }
        return result;
    }

    /**
     * Из указанного узла Альфреско получить бин или отформатированную строку по атрибутам.
     *
     * @param subItemId  узел Альфреско
     * @param subItemNum порядковый номер subItemId в родительском списке
     *                   <br/> используется для присвоения свойству {@link SubReportDescriptor.BEAN_PROPNAME_COL_ROWNUM} бина.
     * @return <li>если указан класс бина - создаётся бин и его свойствам
     *         присваиваются значения согласно this.beanFields и this.beanLists,</li>
     *         <li>иначе воз-ся строка, полученная форматированием атрибутов по {@link #subreport.itemsFormat.formatString}.</li>
     */
    protected Object makeSubItem(NodeRef subItemId, String subItemNum) {
        final Map<String, Object> args = gatherSubItemInfo(subItemId);
        if (args == null) {
            return null;
        }

        // автоматически вносим нумератор строки
        args.put(SubReportDescriptor.BEAN_PROPNAME_COL_ROWNUM, subItemNum);

        if (subreport.isUsingFormat()) { /* форматирование всех свойств объекта в строку ... */
            final String resultStr = formatArgs(args);
            return Utils.expandFromCharPairs(resultStr);
        }

        if (beanClassIsMap()) {
            // если требуется обычный Map/HashMap -> уже готово
            return args;
        }

		/* иначе -> создание бина ... */

        final Object resultBean = createInfoBean(); // Create bean
        assignProperties(resultBean, args);
        return resultBean;
    }

    /**
     * Заполнить бин свойствами. Если Бин это мапа, то заполнение короткое.
     *
     * @param destBean Object
     * @param args     Map<String, Object
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    protected void assignProperties(final Object destBean, final Map<String, Object> args) {
        if (destBean instanceof Map) {
            /* Если создан потомок Map - выполняем его заполнение напрямую */
            ((Map) destBean).putAll(args);
        } else {  /* заполнение "честного" бина */
            for (final Map.Entry<String, Object> e : args.entrySet()) {
                final String propName = e.getKey();
                MacrosHelper.safeSetBeanProperty(destBean, propName, e.getValue(), logger);
            }
        }
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
     *         <br/><b>значение</b> = соот-щий объект, полученный по ссылке для колонки согласно {@link #subreport.subItemsSourceMap}
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
                        final Object value = NodeUtils.getByLink(sourceLink, subItemId, props, resolver, logger, propName);
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
            for (Map.Entry<String, AssocListInfo> ent : beanLists.entrySet()) {
                final String propName = ent.getKey();
                final AssocListInfo src = ent.getValue();

                final QName assocRef = QName.createQName(src.assocQName, getNameService());
                final List<NodeRef> children = NodeUtils.findChildrenByAssoc(subItemId, assocRef, getNodeService());

                if (children != null && !children.isEmpty()) {
                    final List<String> list = new ArrayList<String>();
                    for (NodeRef childId : children) {
                        final Object val = NodeUtils.getByLink(src.dataPath, childId, resolver, logger, propName);
                        if (val != null) {
                            list.add(Utils.coalesce(val, ""));
                        }
                    } // for

                    values.put(propName, list); // (!) присвоение списочного свойства
                } // if
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
    public Object buildSubreport(NodeRef docId) {
        final boolean usingFormat = subreport.isUsingFormat();

        /* получение ассоциированного списка ... */
        List<NodeRef> children = getSubstService().getObjectsByTitle(docId, subreport.getSourceListExpression());

        if (children == null || children.isEmpty()) { // нет вложенных ...
            // если исопльзуется форматирование - вернуть его "пустую строку" ...
            return (usingFormat) ? subreport.getItemsFormat().getIfEmptyTag() : null;
        }

		/* 
         * формирование списка бинов или строк проходом по
		 * ассоциированному/вложенному списку...
		 */
        final List<Object> result = new ArrayList<Object>();

        final String childType = subreport.getSourceListType();

        int i = 0;
        for (NodeRef childId : children) {
            if (childType != null && !childType.isEmpty()) {
                //фильтруем по типу
                String currentType = getNodeService().getType(childId).toPrefixString(getNameService());
                if (!currentType.equals(childType)) {
                    continue;
                }
            }
            i++; // нумерация от единицы
            final Object item = makeSubItem(childId, String.valueOf(i));
            if (item != null) {
                result.add(item);
            }
        } // for

        if (usingFormat) { // форматирование всех вложенных в одну строку ...
            return Utils.getAsString(result, subreport.getItemsFormat().getItemsDelimiter());
        }

        // использование как списка бинов ...
        return result;
    }

    protected static class AssocListInfo {
        /**
         * ассоциация относительно Задачи (без символов {})
         */
        public String assocQName;

        /**
         * оставшаяся часть в виде "{...}" для получения данных,
         * задаётся относительно объектов из списка ассоциации assocQName.
         */
        public String dataPath;

        /**
         * Присвоить ссылку вида:
         * "{ассоциация1/...путь...}"
         * скобки в начале и в конце необязательны
         *
         * @param svalue
         */
        public void parseSourceLink(String svalue) {
            final int ibeg = (svalue.startsWith(SubstitudeBean.OPEN_SUBSTITUDE_SYMBOL))
                    ? SubstitudeBean.OPEN_SUBSTITUDE_SYMBOL.length() // после открывающей скобки, если она есть
                    : 0;
            final int iend = (svalue.endsWith(SubstitudeBean.CLOSE_SUBSTITUDE_SYMBOL))
                    ? svalue.length() - SubstitudeBean.CLOSE_SUBSTITUDE_SYMBOL.length() // до закрывающей скобки (без неё)
                    : svalue.length();
            int posDelim = svalue.indexOf(SubstitudeBean.SPLIT_TRANSITIONS_SYMBOL);
            if (posDelim < 0) {
                // нет "/" - считаем что указана только ассоциация и выводить надо будет "cm:name"
                this.assocQName = svalue.substring(ibeg, iend);
                this.dataPath = "cm:name";
            } else { // есть "/"
                this.assocQName = svalue.substring(ibeg, posDelim); // от начала до разделителя
                this.dataPath = svalue.substring(posDelim + SubstitudeBean.SPLIT_TRANSITIONS_SYMBOL.length(), iend); // после разделителя до конца
            }
            if (this.dataPath != null && this.dataPath.contains(SubstitudeBean.SPLIT_TRANSITIONS_SYMBOL)) {
                // оборачиваем скобками, если путь ещё остаётся сложнее просто свойства ...
                this.dataPath = SubstitudeBean.OPEN_SUBSTITUDE_SYMBOL + this.dataPath + SubstitudeBean.CLOSE_SUBSTITUDE_SYMBOL;
            }
        }
    }

    private NodeService getNodeService() {
        return (resolver == null) ? null : resolver.getServices().getServiceRegistry().getNodeService();
    }

    private NamespaceService getNameService() {
        return (resolver == null) ? null : resolver.getServices().getServiceRegistry().getNamespaceService();
    }

    private SubstitudeBean getSubstService() {
        return (resolver == null) ? null : resolver.getServices().getSubstitudeService();
    }

}

