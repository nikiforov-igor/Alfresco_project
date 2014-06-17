package ru.it.lecm.base.beans;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.notifications.beans.NotificationsService;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.wcalendar.IWorkCalendar;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: dbashmakov
 * Date: 16.06.2014
 * Time: 12:33
 */
public class SearchQueryProcessorServiceImpl implements SearchQueryProcessorService {
    private final static Logger logger = LoggerFactory.getLogger(SearchQueryProcessorServiceImpl.class);
    public static final String CURRENT_USER = "#current-user";
    public static final String CURRENT_DATE = "#current-date";

    private final Pattern PROC_PATTERN = Pattern.compile("[{]{2}.+?[}]{2}");

    private final String OPEN_PARAM_SYMBOL = "(";
    private final String CLOSE_PARAM_SYMBOL = ")";

    private SearchQueryProcManager processorManager;
    private OrgstructureBean orgstructureService;
    private NotificationsService notificationsService;
    private IWorkCalendar workCalendarService;

    public void setOrgstructureService(OrgstructureBean orgstructureService) {
        this.orgstructureService = orgstructureService;
    }

    public void setNotificationsService(NotificationsService notificationsService) {
        this.notificationsService = notificationsService;
    }

    public void setWorkCalendarService(IWorkCalendar workCalendarService) {
        this.workCalendarService = workCalendarService;
    }

    public void setProcessorManager(SearchQueryProcManager processorManager) {
        this.processorManager = processorManager;
    }

    @Override
    public String processQuery(String query) {
        Matcher m = PROC_PATTERN.matcher(query);
        while (m.find()) {
            String groupText = m.group();
            String params = getParamsStr(groupText);
            int lastIndex = groupText.length() - (CLOSE_PARAM_SYMBOL.length() * 2); // - }} size
            if (params != null && !params.isEmpty()) {
                lastIndex = groupText.indexOf(params) - OPEN_PARAM_SYMBOL.length(); // - ( size
            }
            String processorId = groupText.substring(2, lastIndex);
            query = query.replace(groupText, "(" + getProcessorQuery(processorId, params) + ")");
        }

        // обработка спец-выражений
        if (query.contains(CURRENT_USER)) {
            query = query.replaceAll(CURRENT_USER, orgstructureService.getCurrentEmployee().toString());
        }
        if (query.contains(CURRENT_DATE)) {
            int limitDays = notificationsService.getSettingsNDays();
            Date nextWorkDate = workCalendarService.getNextWorkingDate(new Date(), limitDays, Calendar.DAY_OF_MONTH);
            query = query.replaceAll(CURRENT_DATE, BaseBean.DateFormatISO8601.format(nextWorkDate));
        }
        return query;
    }

    @Override
    public String getProcessorQuery(String id, String params) {
        Map<String, Object> paramsMap = getParametersMap(params);

        SearchQueryProcessor processor = processorManager.getProcessorById(id);
        if (processor != null) {
            return processor.getQuery(paramsMap);
        } else {
            logger.debug("Search Query Processor with id= " + id + " not found. Return empty string query!");
        }
        return "";
    }

    private Map<String, Object> getParametersMap(String params) {
        Map<String, Object> paramsMap = new HashMap<>();
        if (params != null) {
            try {
                JSONObject jsonParams = new JSONObject(params);
                Iterator keys = jsonParams.keys();
                while (keys.hasNext()) {
                    String next = (String) keys.next();
                    Object value = jsonParams.get(next);
                    paramsMap.put(next, value);
                }
            } catch (JSONException ex) {
                logger.error(ex.getMessage(), ex);
            }
        }
        return paramsMap;
    }

    private String getParamsStr(String processorText) {
        int openIndex = processorText.indexOf(OPEN_PARAM_SYMBOL);
        int closeIndex = processorText.indexOf(CLOSE_PARAM_SYMBOL);
        if (openIndex > -1 && closeIndex > -1) {
            return processorText.substring(openIndex + 1, closeIndex);
        }
        return null;
    }
}
