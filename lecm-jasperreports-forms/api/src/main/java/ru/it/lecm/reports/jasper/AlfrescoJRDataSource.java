package ru.it.lecm.reports.jasper;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;
import org.alfresco.repo.dictionary.constraint.ListOfValuesConstraint;
import org.alfresco.service.cmr.dictionary.Constraint;
import org.alfresco.service.cmr.dictionary.ConstraintDefinition;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.search.ResultSetRow;
import org.alfresco.service.namespace.NamespaceException;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.base.beans.SubstitudeBean;
import ru.it.lecm.reports.api.DataFieldColumn;
import ru.it.lecm.reports.api.ReportDSContext;
import ru.it.lecm.reports.api.model.DataSourceDescriptor;
import ru.it.lecm.reports.api.model.ReportDescriptor;
import ru.it.lecm.reports.generators.GenericDSProviderBase;
import ru.it.lecm.reports.generators.SubreportBuilder;
import ru.it.lecm.reports.model.impl.SubReportDescriptorImpl;
import ru.it.lecm.reports.utils.Utils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * AlfrescoJRDataSource: набор данных обеспечивает JR-интерфейс для полученных данных Alfresco.
 * Подразумеваемтся такая схема получения данных:
 * [ (!) имена JR-полей совпадают с названиями model-колонок в context.metaFields]
 * 1) основной поисковый запрос выдаёт только id отобранных объектов
 * 2) далее LocalJRDataSource "догружает" в методе next() значения
 * атрибутов Альфреско, которые прописаны в context.metaFields (или грузит все
 * properties-атрибуты)
 * 3) имеется возможность иметь вычисляемые поля (кодируется в имени - обрамление символами '{}').
 * <p/>
 * Значения метаописаний fields из context, могут использоваться в дальнейшем
 * для вычитывания части атрибутов данных вместо выборки целиком всех.
 *
 * @author rabdullin
 */
public class AlfrescoJRDataSource implements JRDataSource {
    private static final Logger logger = LoggerFactory.getLogger(AlfrescoJRDataSource.class);

    private ReportDSContextImpl context = new ReportDSContextImpl();

    private GenericDSProviderBase provider;

    private ReportDescriptor reportDescriptor;

    public AlfrescoJRDataSource(GenericDSProviderBase provider) {
        this.provider = provider;
        this.reportDescriptor = provider.getReportDescriptor();
    }

    public void clear() {
        context.clear();
    }

    public ReportDSContextImpl getContext() {
        return context;
    }

    public ReportDescriptor getReportDescriptor() {
        return reportDescriptor;
    }

    public void setContext(ReportDSContextImpl context) {
        this.context = context;
    }

    /**
     * @param propNameWithPrefix название свойства
     * @return true, если свойство простое (т.е. получается непосредственно у объекта)
     */
    protected boolean isPropVisibleInReport(final String propNameWithPrefix) {
        return (context.getJrSimpleProps() != null)
                && context.getJrSimpleProps().contains(propNameWithPrefix); // или название имеется в списке того, что отрисовывается в отчёте
    }

    @Override
    public boolean next() throws JRException {
        while (context.getRsIter() != null && context.getRsIter().hasNext()) {
            context.setRsRow(context.getRsIter().next());
            context.setCurNodeRef(context.getRsRow().getNodeRef());
            if (loadAlfNodeProps(context.getCurNodeRef())) {
                // загрузка данных по строке
                return true; // FOUND ONE MORE
            }
        } // while
        // NOT FOUND MORE - DONE
        context.setCurNodeProps(null);
        return false;
    }

    @Override
    public Object getFieldValue(JRField jrf) throws JRException {
        return context.getPropertyValueByJRField(jrf.getName());
    }

    /**
     * Загружает строку с указанным id и проверяет её на соот-вие фильтру.
     * Предполагается что отнаследованные "провайдерские" НД будут использовать
     * этот метод для вставки значений своих custom-колонок ("{{...}}").
     *
     * @param id NodeRef
     * @return true, если строка загружена и фильтра нет или строка удовлетворяет
     *         фильтру; и false, если не пропущена фильтром.
     */
    public boolean loadAlfNodeProps(NodeRef id) {
        // дополнительно фильтруем по критериям, если они есть ...
        if (this.context.getFilter() != null && !context.getFilter().isOk(id)) {
            this.context.setCurNodeProps(null);
            logger.debug(String.format("Filtered out node %s", id));
            return false;
        }

        // далее формируем список полей, т.к. фильтр пройден положительно

		/*
         * на случай, если в alfProps будет НЕ полной набор всех свойств
		 * объекта (например, может не быть пустых значений) гарантируем 
		 * чтобы curProps содержал всё, что задано в фильтре
		 */
        this.context.setCurNodeProps(ensureJRProps());

        this.context.getCurNodeProps().put(DataSourceDescriptor.COLNAME_ID, id.toString()); // добавляем для каждой строки её nodeRef
		/*
         * теперь вносим "нативные" Alfresco-свойства объекта как Map:
		 *    Map.key = краткие qname-атрибутов,
		 *    Map.value = соот-шее атрибуту значение.
		 * Если список context.getJrSimpleProps() null, то вносятся все 
		 * Альфреско-атриубуты, иначе только перечисленные в нём.
		 */
        final NodeService nodeSrv = context.getRegistryService().getNodeService();
        final DictionaryService dicSrv = context.getRegistryService().getDictionaryService();
        final Map<QName, Serializable> realProps = nodeSrv.getProperties(id);

        if (realProps != null) {
            NamespaceService namespaces = context.getRegistryService().getNamespaceService();
            for (Map.Entry<QName, Serializable> e : realProps.entrySet()) {
                // переводим название свойства в краткую форму
                //if (!e.getKey().getNamespaceURI().equals(NamespaceService.SYSTEM_MODEL_1_0_URI)) {
                    String key1;
                    try {
                        key1 = e.getKey().toPrefixString(namespaces);
                    } catch (NamespaceException e1) {
                        key1 = e.getKey().toString();   //просто чтоб не падало на устаревших данных
                    }
                    // если есть мета-описания - добавим всё, что там упоминается
                    final String key = key1;
                    if (isPropVisibleInReport(key)) {
                        Object value = e.getValue();
                        List<ConstraintDefinition> constraintDefinitionList = dicSrv.getProperty(e.getKey()).getConstraints();
                        //ищем привязанный LIST_CONSTRAINT
                        if (constraintDefinitionList != null && !constraintDefinitionList.isEmpty()) {
                            for (ConstraintDefinition constraintDefinition : constraintDefinitionList) {
                                Constraint constraint = constraintDefinition.getConstraint();
                                if (constraint instanceof ListOfValuesConstraint) {
                                    //получаем локализованное значение для LIST_CONSTRAINT
                                    String constraintProperty = ((ListOfValuesConstraint) constraint).getDisplayLabel(value.toString(), dicSrv);
                                    if (constraintProperty != null) {
                                        value = constraintProperty;
                                    }
                                }
                            }
                        }
                        context.getCurNodeProps().put(SubstitudeBean.OPEN_SUBSTITUDE_SYMBOL + key + SubstitudeBean.CLOSE_SUBSTITUDE_SYMBOL, value);
                    }
                //}
            }
        }

        if (getReportDescriptor().getSubreports() != null) {  // прогрузка вложенных subreports ...
            for (ReportDescriptor subreport : getReportDescriptor().getSubreports()) {
                if (subreport.isSubReport()) {
                    final Object subBean = prepareSubReport((SubReportDescriptorImpl) subreport, getContext());
                    context.getCurNodeProps().put(getAlfAttrNameByJRKey(((SubReportDescriptorImpl) subreport).getDestColumnName()), subBean);
                }
            }
        }
        return true;
    }

    /**
     * Получить qname-название поля соот-щее jasper-названию колонки.
     * Т.е. по "короткому имени" получить полное ссылочное.
     *
     * @param jrFldName название колонки для Отчёта (оно обычно упрощено относительно "полного" названия в curProps).
     * @return соот-ет ключам в списке атрибутов после вызова getReportContextProps.
     */
    protected String getAlfAttrNameByJRKey(String jrFldName) {
        final DataFieldColumn fld = context.getMetaFields().containsKey(jrFldName)
                ? context.getMetaFields().get(jrFldName)
                : null;
        return (fld != null) && (fld.getValueLink() != null)? fld.getValueLink() : jrFldName;
    }

    /**
     * Подготовить данные подотчёта по ассоциированныму списку subreport:
     *
     * @param subreport SubReportDescriptorImpl
     * @return <li> ОДНУ строку, если subreport должен форматироваться (строка будет
     * состоять из форматированных всех элементов ассоциированного списка),
     * <li> или список бинов List[Object] - по одному на каждую строку
     */
    protected Object prepareSubReport(SubReportDescriptorImpl subreport, ReportDSContext parentContext) {
        if (Utils.isStringEmpty(subreport.getSourceListExpression())) {
            logger.warn(String.format("SubReport '%s' has empty query", subreport.getMnem()));
            return null;
        }

		/* получение списка объектов... */
        return new SubreportBuilder(subreport, provider).buildSubReport(parentContext);
    }

    /**
     * Сформировать список обычных (не вычисляемых и не косвенных) свойств,
     * перечисленных в visibleProps.
     * Удобно для случая, когда загружаемые данные по объекту содержат НЕ полный
     * набор всех свойств объекта (например, может не быть пустых значений),
     * добавлением мы гарантируем, чтобы curProps содержал всё, что надо для jr.
     */
    private HashMap<String, Object> ensureJRProps() {
        final HashMap<String, Object> result = new HashMap<>();
        if (this.getContext().getJrSimpleProps() != null) {
            // все свойства включаем в набор с пустыми значениями
            for (String fldName : this.getContext().getJrSimpleProps()) {
                result.put(SubstitudeBean.OPEN_SUBSTITUDE_SYMBOL + fldName + SubstitudeBean.CLOSE_SUBSTITUDE_SYMBOL, null);
            }
        }
        return result;
    }
}
