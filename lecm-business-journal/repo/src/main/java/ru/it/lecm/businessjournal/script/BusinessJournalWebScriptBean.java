package ru.it.lecm.businessjournal.script;

import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.scripts.ScriptException;
import org.alfresco.service.cmr.repository.NodeRef;
import org.json.JSONArray;
import org.json.JSONException;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.springframework.extensions.surf.util.ParameterCheck;
import ru.it.lecm.base.beans.BaseWebScript;
import ru.it.lecm.businessjournal.beans.BusinessJournalRecord;
import ru.it.lecm.businessjournal.beans.BusinessJournalService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author dbashmakov Date: 25.12.12 Time: 13:57
 */
public class BusinessJournalWebScriptBean extends BaseWebScript {

    private BusinessJournalService service;

    public void setService(BusinessJournalService service) {
        this.service = service;
    }

    /**
     * произвести запись в Бизнес-журнал
     *
     * @param mainObject    текстовое представление ссылки на любой объект
     * @param eventCategory код категории события
     * @param description   описание по умолчанию
     * @param objects       список второстепенных объектов (текстовых представлений ссылок или просто строк)
     */
    @SuppressWarnings("unused")
    public void log(String mainObject, String eventCategory, String description, Scriptable objects) {
		List<String> refs = (objects != null) ? (List<String>) getValueConverter().convertValueForJava(objects) : new ArrayList<String>();
        service.log(new NodeRef(mainObject), eventCategory, description, refs);
    }

    /**
     * получить список записей бизнес-журнала, сформированных за заданный период
     *
     * @param start начальное время (в мс)
     * @param end   конечно время (в мс)
     */
    @SuppressWarnings("unused")
    public Scriptable getRecordsByInterval(long start, long end) {
        List<BusinessJournalRecord> refs = service.getRecordsByInterval(getDateFromLong(start), getDateFromLong(end));
        return Context.getCurrentContext().newArray(getScope(), createScriptRecord(refs).toArray());
    }

    /**
     * получить запись бизнес-журнала по nodeRef
     *
     * @param recordRef текстовое представление nodeRef записи
     */
    @SuppressWarnings("unused")
    public ScriptNode getRecord(String recordRef) {
        ParameterCheck.mandatory("recordRef", recordRef);
        NodeRef ref = new NodeRef(recordRef);
        if (!service.isBJRecord(ref)) {
            throw new ScriptException("Неправильный объект. Параметр должен содержать ссылку на запись бизнес-журнала");
        }
        return new ScriptNode(ref, serviceRegistry, getScope());
    }

    @SuppressWarnings("unused")
    public Scriptable getRecordsByParams(String objectTypes, String eventCategories, String daysCount, String whoseKey) {
        return getRecordsByParams(objectTypes, eventCategories, daysCount, whoseKey, null, null, null);
    }

    @SuppressWarnings("unused")
    public Scriptable getRecordsByParams(String objectTypes, String eventCategories, String daysCount, String whoseKey, String checkMainObject) {
        return getRecordsByParams(objectTypes, eventCategories, daysCount, whoseKey, checkMainObject, null, null);
    }

    /**
     * получить список записей Бизнес-журнала по определенным параметрам
     *
     * @param objectTypes     список типов объектов
     * @param daysCount       за сколько последних дней получить записи
     * @param whoseKey        дополнительная фильтрация по инициатору  (@link BusinessJournalServiceImpl.WhoseEnum)
     * @param checkMainObject проверять ли доступность основного объекта
     * @param skipCount       сколько записей следует пропустить
     * @param maxItems        максимальное число возвращаемых записей
     */
    public Scriptable getRecordsByParams(String objectTypes, String eventCategories, String daysCount, String whoseKey, String checkMainObject, String skipCount, String maxItems) {
        Date now = new Date();
        Date start = null;
        Integer skipCountInt = null;
        try {
            skipCountInt = Integer.parseInt(skipCount);
        } catch (NumberFormatException ignored) {
        }
        Integer maxItemsInt = null;
        try {
            maxItemsInt = Integer.parseInt(maxItems);
        } catch (NumberFormatException ignored) {
        }
        if (daysCount != null && !"".equals(daysCount)) {
            Integer days = Integer.parseInt(daysCount);

            if (days > 0) {
                Calendar calendar = Calendar.getInstance();

                calendar.setTime(now);
                calendar.add(Calendar.DAY_OF_MONTH, (-1) * (days - 1));
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                start = calendar.getTime();
            }
        }
        List<BusinessJournalRecord> refs = service.getRecordsByParams(objectTypes, eventCategories, start, now, whoseKey, Boolean.parseBoolean(checkMainObject), skipCountInt, maxItemsInt);
        return Context.getCurrentContext().newArray(getScope(), createScriptRecord(refs).toArray());
    }

    /**
     * получить список записей Бизнес-журнала
     *
     * @param sort            настройки сортировки в формате <поле|направление сортировки>
     * @param startIndex      начальный индекс для поиска
     * @param maxResults      максимальное число результатов в итоговой выборке
     * @param filter          фильтры для поиска
     * @param andFilter       использовать «AND» между разные фильтрами, иначе «OR»
     * @param includeArchived включать в выборку архивные записи
     */
    @SuppressWarnings("unused")
    public Scriptable getRecords(String sort, Integer startIndex, Integer maxResults, Scriptable filter, Boolean andFilter, Boolean includeArchived) {
        String[] sortSet = sort.split("\\|");
        BusinessJournalRecord.Field field = BusinessJournalRecord.Field.fromFieldName(sortSet[0]);
        boolean ascending = Boolean.valueOf(sortSet[1]);
        List<BusinessJournalRecord> result = service.getRecords(field, ascending, startIndex, maxResults, getFilter(filter), andFilter, includeArchived);
        return Context.getCurrentContext().newArray(getScope(), createScriptRecord(result).toArray());
    }

    /**
     * получить число записей Бизнес-журнала
     *
     * @param filter          фильтры для поиска
     * @param andFilter       использовать «AND» между разные фильтрами, иначе «OR»
     * @param includeArchived включать в выборку архивные записи
     */
    @SuppressWarnings("unused")
    public Integer getRecordsCount(Scriptable filter, Boolean andFilter, Boolean includeArchived) {
        Map<BusinessJournalRecord.Field, String> filterObject = getFilter(filter);
        return service.getRecordsCount(filterObject, andFilter, includeArchived);
    }

    private Map<BusinessJournalRecord.Field, String> getFilter(Scriptable filter) {
        HashMap<BusinessJournalRecord.Field, String> result = new HashMap<BusinessJournalRecord.Field, String>();
        if (filter != null) {
            Object[] ids = filter.getIds();
            for (Object id1 : ids) {
                String id = (String) id1;
                String value = ScriptableObject.getProperty(filter, id).toString();
                if (id.endsWith("-date-range")) {
                    id = id.replace("-date-range", "");
                    String[] dates = value.split("\\|");
                    Date start = new Date(0);
                    Date end = new Date();
                    if (!"".equals(dates[0])) {
                        start = parseDate(dates[0]);
                    }
                    if (dates.length > 1 && !"".equals(dates[1])) {
                        end = parseDate(dates[1]);
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(end);
                        calendar.add(Calendar.DAY_OF_MONTH, 1);
                        end = calendar.getTime();
                    }
                    value = DateFormatISO8601.format(start) + "|" + DateFormatISO8601.format(end);
                }
                result.put(BusinessJournalRecord.Field.fromFieldName(id), value);
            }
        }
        return result;
    }

    private Date parseDate(String date) {
        Pattern p = Pattern.compile("(\\+|\\-)(\\d+):(\\d+)");
        Matcher m = p.matcher(date);
        if (m.find()) {
            String old = m.group(1) + m.group(2) + ":" + m.group(3);
            String newValue = m.group(1) + m.group(2) + m.group(3);
            date = date.replace(old, newValue);
        }
        Date result = new Date();
        try {
            result = DateFormatISO8601.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;

    }

    private Date getDateFromLong(long longDate) {
        if (longDate != -1) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(longDate);
            return calendar.getTime();
        }
        return null;
    }

    /**
     * получить описание объекта
     *
     * @param objectRef строкое представление nodeRef объекта
     */
    @SuppressWarnings("unused")
    public String getObjectDescription(String objectRef) {
        NodeRef ref = new NodeRef(objectRef);
        return service.getObjectDescription(ref);
    }

    /**
     * выполнить архивацию записи Бизнес-журнала
     *
     * @param recordId идентификатор записи Бизнес-журнала
     */
    public boolean archiveRecord(Long recordId) {
        return service.moveRecordToArchive(recordId);
    }

    /**
     * получить список «устаревших» записей Бизнес-журнала (записей старше чем указанная дата)
     *
     * @param dateStr текстовое представление даты в формате yyyy-MM-dd
     */
    @SuppressWarnings("unused")
    public Scriptable findOldRecords(String dateStr) {
        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd").parse(dateStr);

            List<BusinessJournalRecord> refs = service.getRecordsByInterval(null, date);
            return Context.getCurrentContext().newArray(getScope(), createScriptRecord(refs).toArray());
        } catch (ParseException e) {
            throw new ScriptException("Неверный формат даты!", e);
        }
    }

    /**
     * получить историю (список записей Бизнес журнала) для заданного объекта
     *
     * @param nodeRef        текстовое представление nodeRef объекта
     * @param sortColumnName свойство для сортировки
     * @param ascending      направление сортировки (сортировать по возрастанию?)
     * @param showSecondary  включать ли записи, в которых заданный объект является второстепенным
     * @param showInactive   включать ли в итоговую выборку архивные записи
     */
    @SuppressWarnings("unused")
    public Scriptable getHistory(String nodeRef, String sortColumnName, boolean ascending, boolean showSecondary, boolean showInactive) {
        ParameterCheck.mandatory("parentRef", nodeRef);
        NodeRef ref = new NodeRef(nodeRef);
        List<BusinessJournalRecord> records = service.getHistory(ref, sortColumnName, ascending, showSecondary, showInactive);

        return Context.getCurrentContext().newArray(getScope(), createScriptRecord(records).toArray());
    }

    /**
     * получить историю (список записей Бизнес журнала) смены статусов для заданного объекта
     *
     * @param nodeRef        текстовое представление nodeRef объекта
     * @param sortColumnName свойство для сортировки
     * @param ascending      направление сортировки (сортировать по возрастанию?)
     */
    public Scriptable getStatusHistory(String nodeRef, String sortColumnName, boolean ascending) {
        ParameterCheck.mandatory("parentRef", nodeRef);
        NodeRef ref = new NodeRef(nodeRef);
        List<BusinessJournalRecord> records = service.getStatusHistory(ref, sortColumnName, ascending);

        return Context.getCurrentContext().newArray(getScope(), createScriptRecord(records).toArray());
    }

    /**
     * получить запись бизнес-журнала (BusinessJournalScriptRecord) по идентификатору
     *
     * @param nodeId идентификатор объекта
     */
    public BusinessJournalScriptRecord getNodeById(Long nodeId) {
        return createScriptRecord(service.getNodeById(nodeId));
    }

    /**
     * Является ли текущий пользователь технологом бизнес-журнала
     */
    @SuppressWarnings("unused")
    public boolean isBJEngeneer() {
        return service.isBJEngineer();
    }

    private List<BusinessJournalScriptRecord> createScriptRecord(List<BusinessJournalRecord> records) {
        List<BusinessJournalScriptRecord> result = new ArrayList<BusinessJournalScriptRecord>();
        for (BusinessJournalRecord record : records) {
            BusinessJournalScriptRecord resultRecord = createScriptRecord(record);
            if (resultRecord != null) {
                result.add(resultRecord);
            }
        }
        return result;
    }

    private BusinessJournalScriptRecord createScriptRecord(BusinessJournalRecord record) {
        if (record == null) {
            return null;
        } else {
            BusinessJournalScriptRecord scriptRecord = new BusinessJournalScriptRecord(
                    record.getNodeId(),
                    record.getDate(),
                    record.getInitiator() != null ? new ScriptNode(record.getInitiator(), serviceRegistry, getScope()) : null,
                    record.getMainObject() != null ? new ScriptNode(record.getMainObject(), serviceRegistry, getScope()) : null,
                    record.getObjectType() != null ? new ScriptNode(record.getObjectType(), serviceRegistry, getScope()) : null,
                    record.getMainObjectDescription(),
                    record.getRecordDescription(),
                    record.getEventCategory() != null ? new ScriptNode(record.getEventCategory(), serviceRegistry, getScope()) : null,
                    record.getObjects(),
                    record.isActive());
            scriptRecord.setObjectTypeText(record.getObjectTypeText());
            scriptRecord.setEventCategoryText(record.getEventCategoryText());
            scriptRecord.setInitiatorText(record.getInitiatorText());
            return scriptRecord;
        }
    }

    @SuppressWarnings("unused")
    /**
     * включить/выключить логирование определенной категории событий
	 *
     * @param nodeRefs  массив nodeRef категорий
     * @param turnOn включить/выключить логирование
     */
    public Boolean switchLogging(JSONArray nodeRefs, String turnOn) {
        for (int i = 0; i < nodeRefs.length(); i++) {
            try {
                String nodeRef = nodeRefs.getJSONObject(i).getString("nodeRef");
                Boolean turnOnBool = Boolean.parseBoolean(turnOn);
                serviceRegistry.getNodeService().setProperty(new NodeRef(nodeRef), BusinessJournalService.PROP_EVENT_CAT_ON, turnOnBool);
            } catch (JSONException ex) {
                Logger.getLogger(BusinessJournalWebScriptBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return false;
    }
}
