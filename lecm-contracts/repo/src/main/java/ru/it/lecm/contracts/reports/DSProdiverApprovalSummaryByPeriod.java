package ru.it.lecm.contracts.reports;

import net.sf.jasperreports.engine.JRException;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.search.ResultSetRow;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.approval.api.ApprovalListService;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.reports.calc.AvgValue;
import ru.it.lecm.reports.generators.GenericDSProviderBase;
import ru.it.lecm.reports.generators.LucenePreparedQuery;
import ru.it.lecm.reports.jasper.AlfrescoJRDataSource;
import ru.it.lecm.reports.jasper.containers.BasicEmployeeInfo;
import ru.it.lecm.reports.utils.ArgsHelper;
import ru.it.lecm.reports.utils.Utils;
import ru.it.lecm.utils.LuceneSearchBuilder;

import java.io.Serializable;
import java.util.*;

/**
 * Отчёт "10.5.2 Исполнительская дисциплина по согласованиям за период."
 *
 * @author rabdullin
 */
public class DSProdiverApprovalSummaryByPeriod extends GenericDSProviderBase {

    private static final Logger logger = LoggerFactory.getLogger(DSProdiverApprovalSummaryByPeriod.class);

    private Date periodStartDate, periodEndDate;

    final static String VALUE_STATUS_NOTREADY = "NO_DECISION";
    final static String FLD_DOC_PROJECTNUM = "lecm-contract:regNumProject"; // <!-- Регистрационный номер проекта договора-->

    // TODO: заменить потом на модельное значение
    final static float NORMAL_APPROVE_DURATION = 2; // за норму принимаем срок в два дня на задачу (Y)

    /* Названия полей для JR-jrxml */
    final static String JRName_EMPLOYEE = "col_Employee";
    final static String JRName_STAFFPOSNAME = "col_Employee.StaffPosition";
    final static String JRName_OU = "col_Employee.Unit";
    final static String JRName_MISSED_APPROVES_COUNT = "col_Employee.MissedApproves"; // Кол-во просроченных согласований
    final static String JRName_AVG_APPROVE_DAYS = "col_Employee.AvgApproved"; // Средний срок согласований, дней
    final static String JRName_AVG_MISSED_DAYS = "col_Employee.AvgMissed"; // Средний срок просрочки, дней
    final static String JRName_PERIOD_START = "col_Period.Start";
    final static String JRName_PERIOD_END = "col_Period.End";


    @SuppressWarnings("unused")
    public void setPeriodDate(final String value) {
        final String[] paramValue = value.split("\\|");
        periodStartDate = ArgsHelper.tryMakeDate(paramValue[0], "periodStartDate");
        if (paramValue.length >= 2) {
            periodEndDate = ArgsHelper.tryMakeDate(paramValue[1], "periodEndDate");
        }
    }

    @Override
    protected LucenePreparedQuery buildQuery() {
        final LucenePreparedQuery result = super.buildQuery();
        final LuceneSearchBuilder builder = new LuceneSearchBuilder(getServices().getServiceRegistry().getNamespaceService());

        builder.emmit(result.luceneQueryText());

        boolean hasData = !builder.isEmpty();

        // выполненные Согласования -> вне статуса 'NO_DECISION'
        builder.emmit(hasData ? " AND " : "").
                emmit(" NOT @lecm\\-al\\:approval\\-list\\-decision:" + VALUE_STATUS_NOTREADY + " ");

        result.setLuceneQueryText(builder.toString());
        return result;
    }

    @Override
    protected AlfrescoJRDataSource newJRDataSource(Iterator<ResultSetRow> iterator) {
        return new ApprovalDS(iterator);
    }

    /**
     * Структура для хранения данных о статистике по Сотруднику
     */
    protected class EmployeeInfo extends BasicEmployeeInfo {
        // просроченные Согласования
        final AvgValue missedApproves = new AvgValue();

        // Согласования, выполненные в срок
        final AvgValue normalApproves = new AvgValue();

        public EmployeeInfo(NodeRef employeeId) {
            super(employeeId);
        }

        /**
         * Зарегистрировать длительность работы
         *
         * @param plan длительность по плану
         * @param fact длительность по факту
         *             Если plan >= fact, задача выполнена во время (вычисляем avgApproves)
         *             иначе со срывами срока и вычисляется avdMissed (как fact - plan)
         */
        public void registerDuration(float plan, float fact) {
            final boolean isOk = (plan >= fact);
            if (isOk) { // средний срок выполнения
                this.normalApproves.adjust(fact);
            } else { // средний срок просрочки
                this.missedApproves.adjust(fact - plan);
            }
        }
    }

    /**
     * Очень вспомогательный класс для именования объектов, касающихся согласований
     */
    static class ApproveQNameHelper {
        final NamespaceService ns;

        // ссылки на отдельные Согласования из списка
        final QName QTYPE_APPROVE_ITEM;

        final Set<QName> childApproveSet;

        // <!-- ссылка на элемент справочника "Сотрудники" --> у Списка Согл
        final Set<QName> childEmployeeSet;

        // <!-- дата начала согласования --> у Списка Согласования
        final QName QFLD_STARTAPPROVE;

        // 	<!-- дата согласования по документу -->
        final QName QFLD_ENDAPPROVE;

        final QName QASSOC_APPROVAL_ITEM_TO_EMPLOYEE;

        // <!-- Результат согласования сотрудником -->
        final QName QFLD_USER_RESULT;

        // <!-- замечания сотрудника -->
        final QName QFLD_USER_COMMENT;

        // <!-- дата начала согласования -->
        // DONE: заменить на модельную дату получения задачи
        // TODO: см также ApprovalListService QName consts
        final QName QFLD_USER_APPROVE_START;

        // <!-- Дата Cогласования Сотрудником (фактического выполнения) -->
        final QName QFLD_USER_APPROVED;

        // <!-- допустимый срок согласования сотрудником -->
        final QName QFLD_USER_DUE_DATE;

        // <!-- результат согласования документа -->
        final QName QFLD_APPROVE_RESULT;

        // "lecm-al:approval-list-document-version"; // <!-- номер версии документа, по которой проводилось согласование -->
        final QName QFLD_APPROVE_DOCVER;

        // "lecm-contract:regNumProject"; // <!-- Регистрационный номер проекта договора-->
        final QName QFLD_DOC_PROJECTNUM;

        public ApproveQNameHelper(NamespaceService ns) {
            this.ns = ns;

            this.QTYPE_APPROVE_ITEM = ApprovalListService.TYPE_APPROVAL_ITEM;
            this.childApproveSet = new HashSet<QName>(Arrays.asList(QTYPE_APPROVE_ITEM));
            this.childEmployeeSet = new HashSet<QName>(Arrays.asList(OrgstructureBean.TYPE_EMPLOYEE));

            this.QFLD_STARTAPPROVE = ApprovalListService.PROP_APPROVAL_LIST_APPROVE_START; // <!-- дата согласования по документу -->
            this.QFLD_ENDAPPROVE = ApprovalListService.PROP_APPROVAL_LIST_APPROVE_DATE; // <!-- дата согласования по документу -->

            this.QFLD_APPROVE_RESULT = ApprovalListService.PROP_APPROVAL_LIST_DECISION; // <!-- результат согласования документа -->
            this.QFLD_APPROVE_DOCVER = ApprovalListService.PROP_APPROVAL_LIST_DOCUMENT_VERSION; // <!-- номер версии документа, по которой проводилось согласование -->
            this.QFLD_DOC_PROJECTNUM = QName.createQName(FLD_DOC_PROJECTNUM, ns);  // <!-- Регистрационный номер проекта договора-->

            this.QASSOC_APPROVAL_ITEM_TO_EMPLOYEE = ApprovalListService.ASSOC_APPROVAL_ITEM_EMPLOYEE;

            this.QFLD_USER_RESULT = ApprovalListService.PROP_APPROVAL_ITEM_DECISION;
            this.QFLD_USER_COMMENT = ApprovalListService.PROP_APPROVAL_ITEM_COMMENT;

            this.QFLD_USER_APPROVE_START = ApprovalListService.PROP_APPROVAL_ITEM_START_DATE; // "cm:created"
            this.QFLD_USER_APPROVED = ApprovalListService.PROP_APPROVAL_ITEM_APPROVE_DATE;

            this.QFLD_USER_DUE_DATE = ApprovalListService.PROP_APPROVAL_ITEM_DUE_DATE;
        }

        /**
         * Получить главный документ исходя из списка Согласования.
         * Главный документ типа "lecm-contract:document" находится на три (!)
         * уровня выше чем "lecm-al:approval-list"
         */
        static public NodeRef getMainDocByApproveListId(NodeRef approveListId, NodeService nodeSrv) {
            final NodeRef parent1 = nodeSrv.getPrimaryParent(approveListId).getParentRef(); // папка типа "cm:folder", cm:name="Параллельное согласование"
            final NodeRef parent2 = nodeSrv.getPrimaryParent(parent1).getParentRef(); // папка типа "cm:folder", cm:name="Согласование"
            return nodeSrv.getPrimaryParent(parent2).getParentRef(); // о как
        }
    }

    /**
     * 10.5.2	Исполнительская дисциплина по согласованиям за период.
     * Входные данные
     * •	Список Соласований за некоторый Период согласований.
     * <p/>
     * Выходные данные - перегруппированный список по ФИО на основных должностях:
     * •	ФИО сотрудника
     * •	Должность сотрудника
     * •	Отдел
     * •	Количество просроченных согласований
     * •	Средний срок согласования
     * •	Средний срок просрочки
     */
    protected class ApprovalDS extends AlfrescoJRDataSource {
        private List<EmployeeInfo> data;
        private Iterator<EmployeeInfo> iterData;

        public ApprovalDS(Iterator<ResultSetRow> iterator) {
            super(iterator);
            buildData();
        }

        @Override
        public boolean next() throws JRException {
            while (iterData != null && iterData.hasNext()) {
                final EmployeeInfo item = iterData.next();
                context.setCurNodeProps(makeCurProps(item));
                return true;
            } // while
            // NOT FOUND MORE - DONE
            context.setCurNodeProps(null);
            return false;
        }

        /**
         * Сформировать контйнер с данными для jr
         */
        private Map<String, Object> makeCurProps(EmployeeInfo item) {
            final Map<String, Object> result = new HashMap<String, Object>();

            result.put(getAlfAttrNameByJRKey(JRName_EMPLOYEE), item.ФамилияИО());
            result.put(getAlfAttrNameByJRKey(JRName_STAFFPOSNAME), item.staffName);
            result.put(getAlfAttrNameByJRKey(JRName_OU), item.unitName);

            result.put(getAlfAttrNameByJRKey(JRName_MISSED_APPROVES_COUNT), item.missedApproves.getCount());
            result.put(getAlfAttrNameByJRKey(JRName_AVG_MISSED_DAYS), roundToHumanRead(item.missedApproves.getAvg()));
            result.put(getAlfAttrNameByJRKey(JRName_AVG_APPROVE_DAYS), roundToHumanRead(item.normalApproves.getAvg()));

            // период согласования включаем как данные
            final Date now = new Date();
            result.put(getAlfAttrNameByJRKey(JRName_PERIOD_START), (periodStartDate != null) ? periodStartDate : null);
            result.put(getAlfAttrNameByJRKey(JRName_PERIOD_END), (periodEndDate != null) ? periodEndDate : now);

            return result;
        }

        /**
         * Округление до четвертей дней
         */
        float roundToHumanRead(float avg) {
            return (float) (Math.ceil(avg / 0.25) * 0.25);
        }

        /**
         * Собираем статистику по всем перечисленным в this.rsIter объектах-согласованиях
         */
        private void buildData() {
            this.data = new ArrayList<EmployeeInfo>();
            if (context.getRsIter() != null) {
                final Map<NodeRef, EmployeeInfo> statistic = new HashMap<NodeRef, EmployeeInfo>(); // накопленная статистика по Позователю

                final NodeService nodeSrv = getServices().getServiceRegistry().getNodeService();
                final NamespaceService ns = getServices().getServiceRegistry().getNamespaceService();

                final ApproveQNameHelper approveQNames = new ApproveQNameHelper(ns);

                while (context.getRsIter().hasNext()) {
                    final ResultSetRow rs = context.getRsIter().next();

                    final NodeRef approveListId = rs.getNodeRef(); // id Списка Согласований

                    // <!-- дата начала согласования --> у Списка Согласования

                    final List<ChildAssociationRef> childItems = nodeSrv.getChildAssocs(approveListId, approveQNames.childApproveSet);
                    if (childItems == null || childItems.isEmpty()) {
                        continue;
                    }

                    // поочерёдно грузим данные вложенных согласований
                    for (ChildAssociationRef child : childItems) {
                        final NodeRef childId = child.getChildRef();
                        final Map<QName, Serializable> childProps = nodeSrv.getProperties(childId); // свойства "Согласующего Сотрудника"

                        if (childProps == null || childProps.isEmpty()) {
                            continue;
                        }

                        final List<AssociationRef> employees = nodeSrv.getTargetAssocs(childId, approveQNames.QASSOC_APPROVAL_ITEM_TO_EMPLOYEE);
                        if (employees == null || employees.isEmpty()) {// (!?) с Согласованием не связан никакой сотрудник ...
                            logger.warn(String.format("No employee found for approve item %s", childId));
                            continue;
                        }

                        final NodeRef employeeId = employees.get(0).getTargetRef();

                        // <!-- Дата получения задачи на Согласование -->
                        // DONE: заменить на модельную дату получения задачи
                        final Date userApprovStartAt = (Date) childProps.get(approveQNames.QFLD_USER_APPROVE_START);

                        // <!-- дата фактического Согласования Сотрудником -->
                        final Date userApprovedAt = (Date) childProps.get(approveQNames.QFLD_USER_APPROVED);

                        // Плановая дата завершения задачи
                        final Date userDueAt = (Date) childProps.get(approveQNames.QFLD_USER_DUE_DATE);

						/*
                         * из доки "Договорная деятельность ТЗ.docx"
						 * 10.5.2	Исполнительская дисциплина по согласованиям за период.
						 * Пусть, X = количество рабочих дней между Датой фактического согласования и Датой получения задачи на Согласование. 
						 * Это фактический срок согласования.
						 * Y = значение атрибута «Плановое время согласования»,
						 * Если X > Y, то считаем такое согласование Просроченным, 
						 * и  величина, равная X – Y будет составлять Срок просрочки.
						 */
                        final float fact_duration = Utils.calcDurationInDays(userApprovStartAt, userApprovedAt, 0); // X
                        final float norm_duration = Utils.calcDurationInDays(userApprovStartAt, userDueAt, NORMAL_APPROVE_DURATION); // Y

                        final EmployeeInfo userInfo;
                        if (statistic.containsKey(employeeId)) {
                            userInfo = statistic.get(employeeId);
                        } else { // создание стр-ры о Пользователе и наполнение всякой инфой (как зовут, где и кем работает, ...)
                            userInfo = new EmployeeInfo(employeeId);
                            userInfo.loadProps(nodeSrv, getServices().getOrgstructureService());
                            statistic.put(employeeId, userInfo);
                        }
                        userInfo.registerDuration(norm_duration, fact_duration); // X,Y регистрируется
                    }
                } // while
                this.data.addAll(getSortedItemsList(statistic.values())); // перенос в основной блок
            }

            this.iterData = this.data.iterator();
        }

        private List<EmployeeInfo> getSortedItemsList(Collection<EmployeeInfo> values) {
            final List<EmployeeInfo> result = new ArrayList<EmployeeInfo>();
            if (values != null) {
                result.addAll(values);
                Collections.sort(result, new Comparator<EmployeeInfo>() {

                    @Override
                    public int compare(EmployeeInfo o1, EmployeeInfo o2) {
                        final String s1 = o1.ФамилияИО();
                        final String s2 = o2.ФамилияИО();
                        return (s1 == null)
                                ? (s2 == null ? 0 : 1)
                                : (s2 == null ? -1 : s1.compareToIgnoreCase(s2));
                    }
                });
            }

            return result;
        }
    }
}
