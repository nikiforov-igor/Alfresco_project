package ru.it.lecm.reports.jasper;

import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.search.ResultSetRow;
import ru.it.lecm.base.beans.SubstitudeBean;
import ru.it.lecm.reports.api.DataFieldColumn;
import ru.it.lecm.reports.api.DataFilter;
import ru.it.lecm.reports.api.ReportDSContext;
import ru.it.lecm.reports.beans.LinksResolver;

import java.util.*;

public class ReportDSContextImpl implements ReportDSContext {
    private ServiceRegistry serviceRegistry;

    private DataFilter filter; // может быть NULL
    private Map<String, DataFieldColumn> metaFields; // ключ = имя колонки данных в НД

    // список отобранных для отчёта атрибутов Альфреско для активной строки набора данных
    // ключ = QName.toString() с короткими именами типов (т.е. вида "cm:folder" или "lecm-contract:document")
    private Map<String, Object> curProps; // ключ = нативное Альфреско-имя
    private NodeRef curNodeRef;
    private Iterator<ResultSetRow> rsIter;
    private ResultSetRow rsRow;

    private LinksResolver resolver = new LinksResolver();

    public void clear() {
        curProps = null;
        curNodeRef = null;
        rsRow = null;
        rsIter = null;
    }

    /**
     * список простых gname Альфреско-атрибутов, которые требуются для JR-отчёта,
     * здесь перечислены имена - с короткими префиксами, доступными для отчёта;
     * если список null - ограничений на имена не вносятся (и все поля объекта
     * Альфреско могут использоваться в самом шаблоне отчёта.
     */
    private Set<String> jrSimpleProps;

    @Override
    public ServiceRegistry getRegistryService() {
        return serviceRegistry;
    }

    public void setRegistryService(ServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
    }

    public Iterator<ResultSetRow> getRsIter() {
        return rsIter;
    }

    public void setRsIter(Iterator<ResultSetRow> rsIter) {
        this.rsIter = rsIter;
    }

    public ResultSetRow getRsRow() {
        return rsRow;
    }

    public void setRsRow(ResultSetRow rsRow) {
        this.rsRow = rsRow;
    }

    @Override
    public DataFilter getFilter() {
        return filter;
    }

    @Override
    public void setFilter(DataFilter value) {
        this.filter = value;
    }

    @Override
    /**
     * Вернуть список описаний полей, где ключ = имя колонки данных в НД ("короткое имя")
     */
    public Map<String, DataFieldColumn> getMetaFields() {
        return metaFields;
    }

    public void setMetaFields(Map<String, DataFieldColumn> metaFields) {
        this.metaFields = metaFields;
    }

    public void setMetaFields(List<DataFieldColumn> list) {
        final Map<String, DataFieldColumn> result = new HashMap<String, DataFieldColumn>();
        if (list != null) {
            for (DataFieldColumn fld : list) {
                result.put(fld.getName(), fld);
            }
        }
        this.metaFields = result;
    }

    @Override
    public Map<String, Object> getCurNodeProps() {
        return curProps;
    }

    public void setCurNodeProps(Map<String, Object> value) {
        this.curProps = value;
    }

    @Override
    public NodeRef getCurNodeRef() {
        return curNodeRef;
    }

    public void setCurNodeRef(NodeRef curNodeRef) {
        this.curNodeRef = curNodeRef;
    }

    public Set<String> getJrSimpleProps() {
        return jrSimpleProps;
    }

    public void setJrSimpleProps(Set<String> jrSimpleProps) {
        this.jrSimpleProps = jrSimpleProps;
    }

    // TODO: использовать LinksResolver
    @Override
    public Object getPropertyValueByJRField(String reportColumnName) {
        if (reportColumnName == null) {
            return null;
        }

        // получаем нативное название данных
        final DataFieldColumn fld = metaFields.get(reportColumnName);
        return getResolver().evaluateLinkExpr(curNodeRef, fld, getCurNodeProps());
    }

    public LinksResolver getResolver() {
        return resolver;
    }

    public void setResolver(LinksResolver resolver) {
        this.resolver = resolver;
    }
}
