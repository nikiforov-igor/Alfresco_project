package ru.it.lecm.reports.jasper;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.search.ResultSetRow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.reports.api.model.ReportDescriptor;
import ru.it.lecm.reports.beans.LinksResolver;
import ru.it.lecm.reports.generators.SubreportBuilder;
import ru.it.lecm.reports.model.impl.SubReportDescriptorImpl;
import ru.it.lecm.reports.utils.Utils;

import java.util.Iterator;

/**
 * User: dbashmakov
 * Date: 30.06.2014
 * Time: 16:25
 */
public class GenericJRDataSource extends AlfrescoJRDataSource {

    private static final Logger logger = LoggerFactory.getLogger(GenericJRDataSource.class);

    private ReportDescriptor reportDescriptor;
    private LinksResolver resolver;

    public GenericJRDataSource(ReportDescriptor descriptor, LinksResolver resolver, Iterator<ResultSetRow> iterator) {
        super(iterator);
        this.reportDescriptor = descriptor;
        this.resolver = resolver;
    }


    public ReportDescriptor getReportDescriptor() {
        return reportDescriptor;
    }

    public LinksResolver getResolver() {
        return resolver;
    }


    @Override
    protected boolean loadAlfNodeProps(NodeRef docId) {
        final boolean result = super.loadAlfNodeProps(docId); // (!) прогрузка бызовых свойств

        if (result) {
            if (getReportDescriptor().getSubreports() != null) {  // прогрузка вложенных subreports ...
                for (ReportDescriptor subreport : getReportDescriptor().getSubreports()) {
                    if (subreport.isSubReport()) {
                        final Object stringOrBean = prepareSubReport(docId, (SubReportDescriptorImpl) subreport, getResolver());
                        getContext().getCurNodeProps().put(getAlfAttrNameByJRKey(((SubReportDescriptorImpl) subreport).getDestColumnName()), stringOrBean);
                    }
                }
            }
        }
        return result;
    }

    /**
     * Подготовить данные подотчёта по ассоциированныму списку subreport:
     *
     * @param subreport SubReportDescriptorImpl
     * @return <li> ОДНУ строку, если subreport должен форматироваться (строка будет
     * состоять из форматированных всех элементов ассоциированного списка),
     * <li> или список бинов List[Object] - по одному на каждую строку
     */
    protected Object prepareSubReport(NodeRef docId, SubReportDescriptorImpl subreport, LinksResolver resolver) {
        if (Utils.isStringEmpty(subreport.getSourceListExpression())) {
            logger.warn(String.format("Subreport '%s' has empty association", subreport.getMnem()));
            return null;
        }

		/* получение ассоциированного списка и построение ... */
        final SubreportBuilder builder = new SubreportBuilder(subreport, resolver);
        return builder.buildSubreport(docId, builder.getSubreport().getSourceListExpression());
    }
}
