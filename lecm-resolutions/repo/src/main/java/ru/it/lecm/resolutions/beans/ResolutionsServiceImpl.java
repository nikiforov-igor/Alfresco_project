package ru.it.lecm.resolutions.beans;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.ISO8601DateFormat;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.errands.ErrandsService;
import ru.it.lecm.resolutions.api.ResolutionsService;
import ru.it.lecm.wcalendar.IWorkCalendar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * User: AIvkin
 * Date: 18.11.2016
 * Time: 14:41
 */
public class ResolutionsServiceImpl extends BaseBean implements ResolutionsService {
    private final static Logger logger = LoggerFactory.getLogger(ResolutionsServiceImpl.class);

    private IWorkCalendar calendarBean;
    private NamespaceService namespaceService;

    public void setCalendarBean(IWorkCalendar calendarBean) {
        this.calendarBean = calendarBean;
    }

    public void setNamespaceService(NamespaceService namespaceService) {
        this.namespaceService = namespaceService;
    }

    @Override
    public NodeRef getServiceRootFolder() {
        return null;
    }

    @Override
    public Date calculateResolutionExecutionDate(String radio, Integer days, String daysType, Date date) {
        if (EXECUTION_DATE_RADIO_DATE.equals(radio) && date != null) {
            return date;
        } else if (EXECUTION_DATE_RADIO_DAYS.equals(radio) && days != null && daysType != null) {
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR, 12);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);

            if (EXECUTION_DATE_DAYS_WORK.equals(daysType)) {
                return calendarBean.getNextWorkingDateByDays(cal.getTime(), days);
            } else if (EXECUTION_DATE_DAYS_CALENDAR.equals(daysType)) {
                cal.add(Calendar.DAY_OF_YEAR, days);
                return cal.getTime();
            }
        }
        return null;
    }

    @Override
    public boolean checkResolutionErrandsExecutionDate(NodeRef resolution) {
        if (nodeService.exists(resolution)) {
            String errandsJsonStr = (String) nodeService.getProperty(resolution, PROP_ERRANDS_JSON);
            if (errandsJsonStr != null) {
                try {
                    Date resolutionLimitationDate = calculateResolutionExecutionDate(
                            (String) nodeService.getProperty(resolution, PROP_LIMITATION_DATE_RADIO),
                            (Integer) nodeService.getProperty(resolution, PROP_LIMITATION_DATE_DAYS),
                            (String) nodeService.getProperty(resolution, PROP_LIMITATION_DATE_TYPE),
                            (Date) nodeService.getProperty(resolution, PROP_LIMITATION_DATE));

                    if (resolutionLimitationDate != null) {
                        JSONArray errandsJsonArray = new JSONArray(errandsJsonStr);
                        for (int i = 0; i < errandsJsonArray.length(); i++) {
                            JSONObject errandJson = errandsJsonArray.getJSONObject(i);

                            String errandLimitationDateRadio = getPropFromJson(errandJson, ErrandsService.PROP_ERRANDS_LIMITATION_DATE_RADIO);
                            String errandLimitationDateDays = getPropFromJson(errandJson, ErrandsService.PROP_ERRANDS_LIMITATION_DATE_DAYS);
                            String errandLimitationDateType = getPropFromJson(errandJson, ErrandsService.PROP_ERRANDS_LIMITATION_DATE_TYPE);
                            String errandLimitationDateDate = getPropFromJson(errandJson, ErrandsService.PROP_ERRANDS_LIMITATION_DATE);

                            Date errandLimitationDate = calculateResolutionExecutionDate(errandLimitationDateRadio,
                                    (errandLimitationDateDays != null && !errandLimitationDateDays.isEmpty()) ? Integer.parseInt(errandLimitationDateDays) : null,
                                    errandLimitationDateType,
                                    (errandLimitationDateDate != null && !errandLimitationDateDate.isEmpty()) ? ISO8601DateFormat.parse(errandLimitationDateDate) : null);

                            if (errandLimitationDate != null && errandLimitationDate.after(resolutionLimitationDate)) {
                                return false;
                            }
                        }
                    }
                } catch (JSONException e) {
                    logger.error("Error parse resolution errands json");
                }
            }
        }

        return true;
    }

    @Override
    public List<NodeRef> getResolutionClosers(NodeRef resolution) {
        List<NodeRef> result = new ArrayList<>();

        String closerType = (String) nodeService.getProperty(resolution, PROP_CLOSERS);

        if (CLOSERS_AUTHOR.equals(closerType) || CLOSERS_AUTHOR_AND_CONTROLLER.equals(closerType)) {
            result.add(findNodeByAssociationRef(resolution, ASSOC_AUTHOR, null, ASSOCIATION_TYPE.TARGET));
        }

        if (CLOSERS_CONTROLLER.equals(closerType) || CLOSERS_AUTHOR_AND_CONTROLLER.equals(closerType)) {
            result.add(findNodeByAssociationRef(resolution, ASSOC_CONTROLLER, null, ASSOCIATION_TYPE.TARGET));
        }

        return result;
    }

    private String getPropFromJson(JSONObject json, QName propQName) throws JSONException {
        String propName = "prop_" + propQName.toPrefixString(namespaceService).replace(":", "_");
        if (json.has(propName)) {
            return json.getString(propName);
        } else {
            return null;
        }
    }
}
