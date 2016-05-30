package ru.it.lecm.notifications.template;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.admin.SysAdminParams;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.*;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.namespace.NamespaceService;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;

import java.io.IOException;
import java.io.InputStream;
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
		return  "<span class=\"wrapper-title\" title=\"" + StringEscapeUtils.escapeHtml(title) + "\">" + text + "</span>";
	}

	@NotificationsTemplateFunction
	public static String getImageAsBase64(String imgName) {
		NodeRef nodeRef = new NodeRef("workspace://SpacesStore/notification-template-images");
		SearchService searchService = applicationContext.getBean("searchService", SearchService.class);
		NodeService nodeService = applicationContext.getBean("nodeService", NodeService.class);
		NamespaceService namespaceService = applicationContext.getBean("namespaceService", NamespaceService.class);

		String path = nodeService.getPath(nodeRef).toPrefixString(namespaceService);

		SearchParameters parameters = new SearchParameters();
		parameters.setLanguage(SearchService.LANGUAGE_FTS_ALFRESCO);
		parameters.addStore(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
		parameters.setQuery(" +PATH:\"" + path + "//*\" AND @cm\\:name:\"" + imgName + "\"");

		ResultSet resultSet = null;

		try {
			resultSet = searchService.query(parameters);
			if (resultSet != null && resultSet.length() > 0) {
				NodeRef imageNode = resultSet.getRow(0).getNodeRef();
				ContentService contentService = applicationContext.getBean("contentService", ContentService.class);
				ContentReader reader = contentService.getReader(imageNode, ContentModel.PROP_CONTENT);
				InputStream contentInputStream = reader.getContentInputStream();
				try {
					String base64String = Base64.encodeBase64String(IOUtils.toByteArray(contentInputStream));
					String mimeType = reader.getMimetype();
					return "data:" + mimeType + ";base64," + base64String;
				} catch (IOException e) {
					return "";
				}
			}
		} catch (Exception e) {
			logger.error("Error while getting image records", e);
		} finally {
			if (resultSet != null) {
				resultSet.close();
			}
		}

		return "";

	}
	
	private Utils() {
	}
}
