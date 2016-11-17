package ru.it.lecm.errands.beans;

import org.alfresco.service.cmr.repository.NodeRef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.documents.beans.DocumentFilter;
import ru.it.lecm.wcalendar.IWorkCalendar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * User: dbashmakov
 * Date: 25.07.13
 * Time: 16:31
 */
public class ErrandsFilter extends DocumentFilter {
    final private static Logger logger = LoggerFactory.getLogger(ErrandsFilter.class);

    final public static String ID = "errandsFilter";

    private IWorkCalendar workCalendar;

    public void setWorkCalendar(IWorkCalendar workCalendar) {
        this.workCalendar = workCalendar;
    }

    public static enum AssignEnum {
        MY,
        DEPARTMENT,
        ALL,
        INITIATOR
    }

    public static enum DateEnum {
        EXPIRED,
        ALL,
        DEADLINE
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public String getQuery(Object[] args) {
        String query = "";
        try {

            final String PROP_ITINITATOR =
                    ErrandsServiceImpl.PROP_ERRANDS_INITIATOR_REF.toPrefixString(namespaceService).replaceAll(":", "\\\\:").replaceAll("-", "\\\\-");
            final String PROP_EXECUTOR =
                    ErrandsServiceImpl.PROP_ERRANDS_EXECUTOR_REF.toPrefixString(namespaceService).replaceAll(":", "\\\\:").replaceAll("-", "\\\\-");

            final String PROP_IMPORTANT =
                    ErrandsServiceImpl.PROP_ERRANDS_IS_IMPORTANT.toPrefixString(namespaceService).replaceAll(":", "\\\\:").replaceAll("-", "\\\\-");

            final String PROP_CONTROLLER =
                    ErrandsServiceImpl.PROP_ERRANDS_CONTROLLER_REF.toPrefixString(namespaceService).replaceAll(":", "\\\\:").replaceAll("-", "\\\\-");

            final String PROP_EXPIRED =
                    ErrandsServiceImpl.PROP_ERRANDS_IS_EXPIRED.toPrefixString(namespaceService).replaceAll(":", "\\\\:").replaceAll("-", "\\\\-");

            final String PROP_EXEC_DATE =
                    ErrandsServiceImpl.PROP_ERRANDS_LIMITATION_DATE.toPrefixString(namespaceService).replaceAll(":", "\\\\:").replaceAll("-", "\\\\-");

            String assignFilter = (String) args[0];
            String dateFilter = (String) args[1];
            String importantFilter = (String) args[2];
            String controlFilter = (String) args[3];

            String username = authService.getCurrentUserName();
            if (username != null) {
                NodeRef currentEmployee = orgstructureService.getEmployeeByPerson(username);

                switch (AssignEnum.valueOf(assignFilter.toUpperCase())) {
                    case MY: {
                        query += "(@" + PROP_EXECUTOR + ":\"" + currentEmployee.toString().replace(":", "\\:") + "\")";
                        break;
                    }
                    case DEPARTMENT: {
                        List<NodeRef> employees = new ArrayList<NodeRef>();
                        List<NodeRef> departmentEmployees = orgstructureService.getBossSubordinate(currentEmployee);
                        employees.addAll(departmentEmployees);
                        StringBuilder employeesQuery = new StringBuilder();
                        if (employees.size() > 0) {
                            boolean addOR = false;
                            for (NodeRef employeeRef : employees) {
                                employeesQuery.append(addOR ? " OR " : "").append("(");
                                employeesQuery.append("@").append(PROP_EXECUTOR).append(":\"").append(employeeRef.toString().replace(":", "\\:")).append("\"");
                                employeesQuery.append(" OR ");
                                employeesQuery.append("@").append(PROP_ITINITATOR).append(":\"").append(employeeRef.toString().replace(":", "\\:")).append("\"");
                                employeesQuery.append(")");
                                addOR = true;
                            }
                        }
                        if (employeesQuery.length() > 0) {
                            query += "(" + employeesQuery.toString() + ")";
                        }
                        break;
                    }
                    case INITIATOR: {
                        query += "(@" + PROP_ITINITATOR + ":\"" + currentEmployee.toString().replace(":", "\\:") + "\")";
                        break;
                    }
                    case ALL: {
                        break;
                    }
                    default: {
                        break;
                    }
                }

                switch (DateEnum.valueOf(dateFilter.toUpperCase())) {
                    case EXPIRED: {
                        query += (query.length() > 0 ? " AND " : "") + " (@" + PROP_EXPIRED + ":true AND NOT (@lecm\\-statemachine\\:status:\"Удалено\" @lecm\\-statemachine\\:status:\"Отменено\" @lecm\\-statemachine\\:status:\"Исполнено\" @lecm\\-statemachine\\:status:\"Не исполнено\")) ";
                        break;
                    }
                    case DEADLINE: {
                        Date now = new Date();

                        Date deadlineDate = workCalendar.getNextWorkingDate(now, 5, Calendar.DAY_OF_MONTH);
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(deadlineDate);
                        calendar.set(Calendar.HOUR_OF_DAY, 0);
                        calendar.set(Calendar.MINUTE, 0);
                        calendar.set(Calendar.SECOND, 0);

                        Date end = calendar.getTime();

                        final String MIN = BaseBean.DateFormatISO8601.format(now);
                        final String MAX = end != null ? BaseBean.DateFormatISO8601.format(end) : "MAX";

                        query += (query.length() > 0 ? " AND " : "") + " (@" + PROP_EXEC_DATE + ":[\"" + MIN + "\" TO \"" + MAX + "\"])";
                        break;
                    }
                    case ALL: {
                        break;
                    }
                    default: {
                        break;
                    }
                }

                Boolean isImportantOnly = Boolean.valueOf(importantFilter);
                if (isImportantOnly) {
                    query += (query.length() > 0 ? " AND " : "") + "(@" + PROP_IMPORTANT + ":true)";
                }

                Boolean isControlOnly = Boolean.valueOf(controlFilter);
                if (isControlOnly){
                    query += (query.length() > 0 ? " AND " : "") + "(@" + PROP_CONTROLLER + ":\"*\")";
                }
            }
        } catch (Exception ignored) {
            logger.warn("Incorrect filter! Filter args:" + args);
        }
        return query;
    }

    @Override
    public String getParamStr() {
        return "all/all/false/false";
    }
}
