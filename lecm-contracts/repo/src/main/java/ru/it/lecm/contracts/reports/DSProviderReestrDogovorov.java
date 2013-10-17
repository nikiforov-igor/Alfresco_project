package ru.it.lecm.contracts.reports;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.search.ResultSetRow;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.it.lecm.contracts.beans.ContractsBeanImpl;
import ru.it.lecm.reports.api.AssocDataFilter.AssocDesc;
import ru.it.lecm.reports.api.AssocDataFilter.AssocKind;
import ru.it.lecm.reports.jasper.AlfrescoJRDataSource;
import ru.it.lecm.reports.jasper.DSProviderSearchQueryReportBase;
import ru.it.lecm.reports.jasper.containers.BasicEmployeeInfo;
import ru.it.lecm.reports.jasper.filter.AssocDataFilterImpl;
import ru.it.lecm.reports.utils.ArgsHelper;
import ru.it.lecm.reports.utils.Utils;

/**
 * Отчёт по реестру договоров
 * Параметры отчёта:
 * "contractSubject" - тематика договора
 * "contractType" - тип договора
 * "contractContractor" - контрагент
 * "contractActualOnly" - только актуальные
 * "contractSumLow"/"contractSumHi" - диапазон для суммы договора
 * "end" - стартовая дата, example "2013-04-03T00:00:00.000+06:00"
 * "start" - конечна дата
 *
 * @author rabdullin
 */
public class DSProviderReestrDogovorov extends DSProviderSearchQueryReportBase {

    private static final Logger logger = LoggerFactory.getLogger(DSProviderReestrDogovorov.class);

    /**
     * Фильтр поиска:
     * "contractSubject" - тематика договора
     * "contractType" - тип договора
     * "contractContractor" - контрагент
     * <p/>
     * "contractActualOnly" - только актуальные
     * "contractSum" - сумма
     * <p/>
     * "startAfter" и "startBefore"- интервл времени для стартовой даты
     * fmt like "2013-04-30T00:00:00.000+06:00"
     * "endAfter" .. "endBefore" - интервл времени для конечной даты, fmt the same
     */
    class SearchFilter {
        Date dateStartAfter, dateStartBefore, dateEndAfter, dateEndBefore;
        Double contractSumLow, contractSumHi;
        Boolean contractActualOnly;
        NodeRef contractSubject, contractType;
        List<NodeRef> contragents;

        public void clear() {
            dateStartAfter = dateStartBefore = dateEndAfter = dateEndBefore = null;
            contractSumLow = contractSumHi = null;
            contractSubject = contractType = null;
            contragents = null;
            contractActualOnly = null;
        }

        public void setContragents(List<NodeRef> list) {
            this.contragents = (list == null || list.isEmpty()) ? null : list;
        }

        public AssocDataFilterImpl makeAssocFilter() {

            final boolean hasSubject = (contractSubject != null);
            final boolean hasType = (contractType != null);
            final boolean hasCAgents = (contragents != null);
            final boolean hasAny = hasSubject || hasType || hasCAgents;
            if (!hasAny) {
                // в фильтре ничего не задачно -> любые данные подойдут
                return null;
            }

            final AssocDataFilterImpl result = new AssocDataFilterImpl(getServices().getServiceRegistry());

            final NamespaceService ns = getServices().getServiceRegistry().getNamespaceService();

            if (hasSubject) {
                final QName qnCSubject = QName.createQName("lecm-doc-dic:subjects", ns); // Тематика договора, "lecm-contract:subjectContract-assoc"
                final QName qnAssocCSubject = QName.createQName("lecm-contract:subjectContract-assoc", ns);
                result.addAssoc(AssocKind.target, qnCSubject, qnAssocCSubject, contractSubject);
            }

            if (hasType) {
                final QName qnCType = QName.createQName("lecm-contract-dic:contract-type", ns); // Вид договора
                final QName qnAssocCType = QName.createQName("lecm-contract:typeContract-assoc", ns);
                result.addAssoc(AssocKind.target, qnCType, qnAssocCType, contractType);
            }

            if (hasCAgents) {
                final QName qnCAgent = QName.createQName("lecm-contractor:contractor-type", ns); // Контрагенты, "lecm-contract:partner-assoc"
                final QName qnAssocCAgent = QName.createQName("lecm-contract:partner-assoc", ns);
                result.addAssoc(new AssocDesc(AssocKind.target, qnAssocCAgent, qnCAgent, contragents));
            }

            return result;
        }

    }

    private final SearchFilter filter = new SearchFilter();

    public void setStartAfter(final String value) {
        filter.dateStartAfter = ArgsHelper.tryMakeDate(value, "dateStartAfter");
    }

    public void setStartBefore(final String value) {
        filter.dateStartBefore = ArgsHelper.tryMakeDate(value, "dateStartBefore");
    }

    public void setEndAfter(final String value) {
        filter.dateEndAfter = ArgsHelper.tryMakeDate(value, "dateEndAfter");
    }

    public void setEndBefore(final String value) {
        filter.dateEndBefore = ArgsHelper.tryMakeDate(value, "dateEndBefore");
    }

    public void setContractSubject(String value) {
        filter.contractSubject = ArgsHelper.makeNodeRef(value, "contractSubject");
    }

    public void setContractType(String value) {
        filter.contractType = ArgsHelper.makeNodeRef(value, "contractType");
    }

    public void setContractContractor(String value) {
        filter.setContragents(ArgsHelper.makeNodeRefs(value, "contragents"));
    }

    //"contractActualOnly" - только актуальные
    public void setContractActualOnly(String value) {
        filter.contractActualOnly = Utils.isStringEmpty(value) ? null : Boolean.parseBoolean(value);
    }

    /**
     * Нижняяя граница для суммы договора.
     */
    public void setContractSumLow(String value) {
        try {
            filter.contractSumLow = Utils.isStringEmpty(value) ? null : Double.parseDouble(value);
            if (filter.contractSumLow != null && filter.contractSumLow == 0) {
                // значение ноль эквивалентно NULL
                filter.contractSumLow = null;
            }
        } catch (Throwable e) {
            logger.error(String.format("unexpected double value '%s' for contractSumLow -> ignored as NULL", value), e);
            filter.contractSumLow = null;
        }
    }

    /**
     * Верхняя граница для суммы договора.
     */
    public void setContractSumHi(String value) {
        try {
            filter.contractSumHi = Utils.isStringEmpty(value) ? null : Double.parseDouble(value);
            if (filter.contractSumHi != null && filter.contractSumHi == 0) {
                // значение ноль эквивалентно NULL
                filter.contractSumHi = null;
            }
        } catch (Throwable e) {
            logger.error(String.format("unexpected double value '%s' for contractSumHi -> ignored as NULL", value), e);
            filter.contractSumHi = null;
        }
    }

    final static String JRFLD_Executor_Family = "col_Executor_Family";
    final static String JRFLD_Executor_Name = "col_Executor_Name";
    final static String JRFLD_Executor_Otchestvo = "col_Executor_Otchestvo";
    final static String JRFLD_Executor_Staff = "col_Executor_Staff";

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
                        context.getCurNodeProps().put(getAlfAttrNameByJRKey(JRFLD_Executor_Name), docExecutor.firstName);
                        context.getCurNodeProps().put(getAlfAttrNameByJRKey(JRFLD_Executor_Otchestvo), docExecutor.middleName);
                        context.getCurNodeProps().put(getAlfAttrNameByJRKey(JRFLD_Executor_Family), docExecutor.lastName);

                        context.getCurNodeProps().put(getAlfAttrNameByJRKey(JRFLD_Executor_Staff), docExecutor.staffName);
                    }
                }
                return flag;
            }
        };

        dataSource.getContext().setFilter(filter.makeAssocFilter());
        return dataSource;
    }

    /**
     * Построить Lucene-запрос по данным фильтра.
     * Example:
     * TYPE:"{http://www.it.ru/logicECM/contract/1.0}document" AND  @lecm\-contract\:totalSum:(0 TO 23450000)   AND @lecm\-contract\:endDate:[ NOW/DAY TO *]
     */
    @Override
    protected String buildQueryText() {
        final StringBuilder bquery = new StringBuilder();
        bquery.append("TYPE:").append(Utils.quoted(ContractsBeanImpl.TYPE_CONTRACTS_DOCUMENT.toString()));

        // начало .. конец
        // начало == <!-- дата начала согласования -->
        String cond = Utils.emmitDateIntervalCheck("lecm\\-contract\\:startDate", filter.dateStartAfter, filter.dateStartBefore);
        if (cond != null)
            bquery.append(" AND ").append(cond);

        // окончание == <!-- дата согласования по документу -->

        cond = Utils.emmitDateIntervalCheck("lecm\\-contract\\:endDate", filter.dateEndAfter, filter.dateEndBefore);
        if (cond != null) {
            bquery.append(" AND( (@lecm\\-contract\\:unlimited:true) OR (").append(cond).append(") )");
        }

        // Сумма договора (указан диапазон)
        cond = Utils.emmitNumericIntervalCheck("lecm\\-contract\\:totalAmount", filter.contractSumLow, filter.contractSumHi);
        if (cond != null) {
            bquery.append(" AND ").append(cond);
        }

        // Контракт актуален: если ещё не истёк срок
        if (Boolean.TRUE.equals(filter.contractActualOnly)) {
            bquery.append(" AND (@lecm\\-contract\\:unlimited:true OR @lecm\\-contract\\:endDate:[NOW TO MAX])"); // неограниченый или "истекает позже чем сейчас"
        }

        return bquery.toString();
    }
}
