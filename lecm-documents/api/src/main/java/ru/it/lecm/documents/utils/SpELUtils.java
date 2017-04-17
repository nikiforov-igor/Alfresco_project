package ru.it.lecm.documents.utils;

import org.alfresco.repo.admin.SysAdminParams;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * User: dbashmakov
 * Date: 16.06.2016
 * Time: 15:28
 */
public final class SpELUtils implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    /**
     * Получить данные для регистрации функций в контексте SpEL.
     * Регистрация осуществляется с помощью аннотаций SpELTemplateFunction.
     * Если в аннотации указан параметр functionName, то его значение
     * используется в качестве имени для внутренней функции SpEL'а. Если
     * параметр не указан, то имя функции совпадает с именем метода. Необходимо
     * помнить, что в одном контексте контексте SpEL'а не могут быть
     * зарегистрированы несколько функций с одним именем.
     *
     * @return карта вида "имяФункцииДляSpEL - метод"
     */
    static public Map<String, Method> getTemplateFunctionMethods() {
        Map<String, Method> result = new HashMap<>();
        Method[] declaredMethods = SpELUtils.class.getDeclaredMethods();
        for (Method method : declaredMethods) {
            SpELTemplateFunction annotation = method.getAnnotation(SpELTemplateFunction.class);
            if (annotation != null) {
                String functionName = annotation.functionName();
                result.put(StringUtils.isEmpty(functionName) ? method.getName() : functionName, method);
            }
        }
        return result;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpELUtils.applicationContext = applicationContext;
    }

    /**
     * Форматировать дату по правилам DateFormat/
     */
    @SpELTemplateFunction
    public static String formatDate(String format, Date date) {
        DateFormat dateFormatter = new SimpleDateFormat(format);
        return dateFormatter.format(date);
    }

    /**
     * Форматировать текущую дату по правилам DateFormat/
     */
    @SpELTemplateFunction(functionName = "formatCurrentDate")
    public static String formatDate(String format) {
        return formatDate(format, new Date());
    }

    /**
     * Форматировать целочисленное значение по правилам DecimalFormat
     */
    @SpELTemplateFunction
    public static String formatNumber(String format, Long number) {
        DecimalFormat decimalFormatter = new DecimalFormat(format);
        return decimalFormatter.format(number);
    }

    @SpELTemplateFunction
    public static String formatLink(String url, String description) {
        ServiceRegistry serviceRegistry = applicationContext.getBean("ServiceRegistry", ServiceRegistry.class);
        SysAdminParams params = serviceRegistry.getSysAdminParams();
        String serverUrl = params.getShareProtocol() + "://" + params.getShareHost() + ":" + params.getSharePort();
        return "<a href=\"" + serverUrl + url + "\">"
                + description + "</a>";
    }

    @SpELTemplateFunction
    public static String wrapTitle(String text, String title) {
        return "<span class=\"wrapper-title\" title=\"" + escapeQuotes(title) + "\">" + text + "</span>";
    }

    /**
     * Экранирование кавычек
     * @param title
     * @return
     */
    private static String escapeQuotes(String title) {
        return title.replaceAll("\"", "&quot;");
    }
    /**
     * Получить код подразделения, в котором занимает основную должность
     * указанный сотрудник. Если код не указан или сотрудник
     * не занимает должностей, то результатом будет "NA".
     */
    @SpELTemplateFunction
    public static String employeeOrgUnitCode(NodeRef employeeNode) {
        String result = "";
        OrgstructureBean orgstructureService = applicationContext.getBean("serviceOrgstructure", OrgstructureBean.class);
        NodeService nodeService = applicationContext.getBean("nodeService", NodeService.class);
        NodeRef employeeUnit = orgstructureService.getUnitByStaff(orgstructureService.getEmployeePrimaryStaff(employeeNode));
        if (employeeUnit != null) {
            result = (String) nodeService.getProperty(employeeUnit, OrgstructureBean.PROP_UNIT_CODE);
        }
        return result.isEmpty() ? "NA" : result;
    }

    /**
     * Получить инициалы указанного сотрудника: Иванов Андрей Петрович -> ИАП
     */
    @SpELTemplateFunction
    public static String employeeInitials(NodeRef employeeNode) {
        NodeService nodeService = applicationContext.getBean("nodeService", NodeService.class);
        String lastName = (String) nodeService.getProperty(employeeNode, OrgstructureBean.PROP_EMPLOYEE_LAST_NAME);
        String firstName = (String) nodeService.getProperty(employeeNode, OrgstructureBean.PROP_EMPLOYEE_FIRST_NAME);
        String middleName = (String) nodeService.getProperty(employeeNode, OrgstructureBean.PROP_EMPLOYEE_MIDDLE_NAME);

        return Character.toString(lastName.charAt(0)) + Character.toString(firstName.charAt(0)) + Character.toString(middleName.charAt(0));
    }

    /**
     * Получить табельный номер указанного сотрудника. Если номер не указан, то
     * строка "NA".
     */
    @SpELTemplateFunction
    public static String employeeNumber(NodeRef employeeNode) {
        NodeService nodeService = applicationContext.getBean("nodeService", NodeService.class);
        Long employeeCode = (Long) nodeService.getProperty(employeeNode, OrgstructureBean.PROP_EMPLOYEE_NUMBER);
        return employeeCode != null ? String.valueOf(employeeCode) : "NA";
    }

    private SpELUtils() {

    }
}
