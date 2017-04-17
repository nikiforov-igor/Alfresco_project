package ru.it.lecm.notifications.template;

import org.alfresco.repo.admin.SysAdminParams;
import org.alfresco.service.ServiceRegistry;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import ru.it.lecm.base.utils.WrapUtils;

import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author vkuprin
 */
@Deprecated
public final class Utils {

	private static ApplicationContext applicationContext;
	private final static Logger logger = LoggerFactory.getLogger(Utils.class);

	/**
	 * Получить данные для регистрации функций в контексте SpEL.
	 * Регистрация осуществляется с помощью аннотаций RegnumTemplateFunction.
	 * Если в аннотации указан параметр functionName, то его значение
	 * используется в качестве имени для внутренней функции SpEL'а. Если
	 * параметр не указан, то имя функции совпадает с именем метода. Необходимо
	 * помнить, что в одном контексте контексте SpEL'а не могут быть
	 * зарегистрированы несколько функций с одним именем.
	 *
	 * @return карта вида "имяФункцииДляSpEL - метод"
	 */
	static Map<String, Method> getTemplateFunctionMethods() {
		Map<String, Method> result = new HashMap<String, Method>();
		Method[] declaredMethods = Utils.class.getDeclaredMethods();
		for (int i = 0; i < declaredMethods.length; i++) {
			Method method = declaredMethods[i];
			NotificationsTemplateFunction annotation = method.getAnnotation(NotificationsTemplateFunction.class);
			if (annotation != null) {
				String functionName = annotation.functionName();
				result.put(StringUtils.isEmpty(functionName) ? method.getName() : functionName, method);
			}
		}
		return result;
	}

	public static void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		Utils.applicationContext = applicationContext;
	}

	/**
	 * Форматировать дату по правилам DateFormat/
	 */
	@NotificationsTemplateFunction
	public static String formatDate(String format, Date date) {
		DateFormat dateFormatter = new SimpleDateFormat(format);
		return dateFormatter.format(date);
	}

	/**
	 * Форматировать текущую дату по правилам DateFormat/
	 */
	@NotificationsTemplateFunction(functionName = "formatCurrentDate")
	public static String formatDate(String format) {
		return formatDate(format, new Date());
	}

	/**
	 * Форматировать целочисленное значение по правилам DecimalFormat
	 */
	@NotificationsTemplateFunction
	public static String formatNumber(String format, Long number) {
		DecimalFormat decimalFormatter = new DecimalFormat(format);
		return decimalFormatter.format(number);
	}

	@NotificationsTemplateFunction
	public static String formatLink(String url, String description) {
		ServiceRegistry serviceRegistry = applicationContext.getBean("ServiceRegistry", ServiceRegistry.class);
		SysAdminParams params = serviceRegistry.getSysAdminParams();
        String serverUrl = params.getShareProtocol() + "://" + params.getShareHost() + ":" + params.getSharePort();
        return "<a href=\"" + serverUrl + url + "\">"
                + description + "</a>";
	}

	@NotificationsTemplateFunction
	public static String wrapTitle(String text, String title) {
		return  WrapUtils.wrapTitle(text, title);
	}

	private Utils() {
	}
}
