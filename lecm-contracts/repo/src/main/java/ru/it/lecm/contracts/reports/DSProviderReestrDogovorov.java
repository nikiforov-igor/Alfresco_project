package ru.it.lecm.contracts.reports;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.search.ResultSetRow;
import ru.it.lecm.reports.generators.GenericDSProviderBase;
import ru.it.lecm.reports.generators.LucenePreparedQuery;
import ru.it.lecm.reports.jasper.AlfrescoJRDataSource;
import ru.it.lecm.reports.jasper.containers.BasicEmployeeInfo;
import ru.it.lecm.reports.utils.Utils;
import ru.it.lecm.utils.LuceneSearchBuilder;

import java.util.Iterator;

/**
 * Отчёт по реестру договоров
 * Параметры отчёта:
 * "contractActualOnly" - только актуальные
 * @author rabdullin
 */
public class DSProviderReestrDogovorov extends GenericDSProviderBase {

    private Boolean contractActualOnly;
    private String JRFLD_Executor_Name = "col_Executor";

    @SuppressWarnings("unused")
    public void setContractActualOnly(String value) {
        contractActualOnly = Utils.isStringEmpty(value) ? null : Boolean.parseBoolean(value);
    }

    @Override
    protected AlfrescoJRDataSource newJRDataSource(Iterator<ResultSetRow> iterator) {
        final AlfrescoJRDataSource dataSource = new AlfrescoJRDataSource(iterator) {
            @Override
            protected boolean loadAlfNodeProps(NodeRef id) {
                final boolean flag = super.loadAlfNodeProps(id);
                if (flag) {
                    // подгрузим автора - он же исполнгитель в договорах!
                    final NodeService nodeSrv = getServices().getServiceRegistry().getNodeService();
                    final NodeRef executorId = getServices().getDocumentService().getDocumentAuthor(id);
                    if (executorId != null) {
                        final BasicEmployeeInfo docExecutor = new BasicEmployeeInfo(executorId);
                        docExecutor.loadProps(nodeSrv, getServices().getOrgstructureService());
                        // сохраним ФИО ...
                        context.getCurNodeProps().put(getAlfAttrNameByJRKey(JRFLD_Executor_Name), docExecutor.ФамилияИО());
                    }
                }
                return flag;
            }
        };

        return dataSource;
    }

    @Override
    protected LucenePreparedQuery buildQuery() {
        final LucenePreparedQuery result = super.buildQuery();

        final LuceneSearchBuilder builder = new LuceneSearchBuilder(getServices().getServiceRegistry().getNamespaceService());
        builder.emmit(result.luceneQueryText());

        boolean hasData = !builder.isEmpty();

        // Контракт актуален: если ещё не истёк срок
        if (Boolean.TRUE.equals(contractActualOnly)) {
            builder.emmit(hasData ? " AND " : "").
                    emmit("(@lecm\\-contract\\:unlimited:true OR @lecm\\-contract\\:endDate:[NOW TO MAX])"); // неограниченый или "истекает позже чем сейчас"
        }

        result.setLuceneQueryText(builder.toString());
        return result;
    }
}
