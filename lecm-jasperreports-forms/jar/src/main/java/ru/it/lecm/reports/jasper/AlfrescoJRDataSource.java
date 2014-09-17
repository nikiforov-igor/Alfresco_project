package ru.it.lecm.reports.jasper;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.search.ResultSetRow;
import org.alfresco.service.namespace.NamespaceException;
import org.alfresco.service.namespace.QName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.reports.api.DataFieldColumn;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
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

    public AlfrescoJRDataSource(Iterator<ResultSetRow> iterator) {
        this.context.setRsIter(iterator);
    }

    public void clear() {
        context.clear();
    }

    public ReportDSContextImpl getContext() {
        return context;
    }

    public void setContext(ReportDSContextImpl context) {
         this.context = context;
    }
    /**
     * @param propNameWithPrefix название свойства
     * @return true, если свойство простое (т.е. получается непосредственно у объекта)
     */
    protected boolean isPropVisibleInReport(final String propNameWithPrefix) {
        return (context.getJrSimpleProps() == null) // если нет фильтра -> видно всё
                || context.getJrSimpleProps().contains(propNameWithPrefix); // или название имеется в списке того, что отрисовывается в отчёте
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
    protected boolean loadAlfNodeProps(NodeRef id) {
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

		/*
		 * теперь вносим "нативные" Alfresco-свойства объекта как Map:
		 *    Map.key = краткие qname-атрибутов,
		 *    Map.value = соот-шее атрибуту значение.
		 * Если список context.getJrSimpleProps() null, то вносятся все 
		 * Альфреско-атриубуты, иначе только перечисленные в нём.
		 */
        final NodeService nodeSrv = context.getRegistryService().getNodeService();
        final Map<QName, Serializable> realProps = nodeSrv.getProperties(id);

        if (realProps != null) {
            for (Map.Entry<QName, Serializable> e : realProps.entrySet()) {
                // переводим название свойства в краткую форму
                final String key;
                String key1;
                try {
                    key1 = e.getKey().toPrefixString(context.getRegistryService().getNamespaceService());
                } catch (NamespaceException e1) {
                    key1 = e.getKey().toString();   //просто чтоб не падало на устаревших данных
                }
                // если есть мета-описания - добавим всё, что там упоминается
                key = key1;
                if (isPropVisibleInReport(key)) {
                    context.getCurNodeProps().put(key, e.getValue());
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
     * Сформировать список обычных (не вычисляемых и не косвенных) свойств,
     * перечисленных в visibleProps.
     * Удобно для случая, когда загружаемые данные по объекту содержат НЕ полный
     * набор всех свойств объекта (например, может не быть пустых значений),
     * добавлением мы гарантируем, чтобы curProps содержал всё, что надо для jr.
     */
    private HashMap<String, Object> ensureJRProps() {
        final HashMap<String, Object> result = new HashMap<String, Object>();
        if (this.context.getJrSimpleProps() != null) {
            // все свойства включаем в набор с пустыми значениями
            for (String fldName : this.context.getJrSimpleProps()) {
                if (!ReportDSContextImpl.isCalcField(fldName)) { // обычное поле
                    result.put(fldName, null);
                }
            }
        }
        return result;
    }
}
