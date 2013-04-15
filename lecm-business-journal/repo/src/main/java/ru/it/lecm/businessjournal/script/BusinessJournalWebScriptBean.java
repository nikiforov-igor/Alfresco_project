package ru.it.lecm.businessjournal.script;

import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.scripts.ScriptException;
import org.alfresco.service.cmr.repository.NodeRef;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.springframework.extensions.surf.util.ParameterCheck;
import ru.it.lecm.base.beans.BaseWebScript;
import ru.it.lecm.businessjournal.beans.BusinessJournalServiceImpl;
import ru.it.lecm.businessjournal.schedule.BusinessJournalArchiverSettings;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author dbashmakov
 *         Date: 25.12.12
 *         Time: 13:57
 */
public class BusinessJournalWebScriptBean extends BaseWebScript {

	private BusinessJournalServiceImpl service;
	private BusinessJournalArchiverSettings archiverSettings;

	public void setService(BusinessJournalServiceImpl service) {
		this.service = service;
	}

	public ScriptNode log(String mainObject, String eventCategory, String description, Scriptable objects) {
		NodeRef record;
		Object[] objs = Context.getCurrentContext().getElements(objects);
		List<String> refs = new ArrayList<String>();
		for (Object obj : objs) {
			String ref = (String) obj;
			refs.add(ref);
		}
		record = service.log(new NodeRef(mainObject), eventCategory, description, refs);
		return new ScriptNode(record, serviceRegistry, getScope());
	}

	public Scriptable getRecordsByInterval(long start, long end) {
		List<NodeRef> refs = service.getRecordsByInterval(getDateFromLong(start), getDateFromLong(end));
		return createScriptable(refs);
	}

	public ScriptNode getRecord(String recordRef) {
		ParameterCheck.mandatory("recordRef", recordRef);
		NodeRef ref = new NodeRef(recordRef);
		if (!service.isBJRecord(ref)) {
			throw new ScriptException("Неправильный объект. Параметр должен содержать ссылку на запись бизнес-журнала");
		}
		return new ScriptNode(ref, serviceRegistry, getScope());
	}

    public Scriptable getRecordsByParams(String objectType, String daysCount, String whoseKey) {
        return getRecordsByParams(objectType, daysCount, whoseKey, null);
    }

    public Scriptable getRecordsByParams(String objectType, String daysCount, String whoseKey, String checkMainObject) {
        Date now = new Date();
        Date start = null;

        if (daysCount != null &&  !"".equals(daysCount)) {
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
        List<NodeRef> refs = service.getRecordsByParams(objectType, start, now, whoseKey, Boolean.parseBoolean(checkMainObject));
        return createScriptable(refs);
    }

	public ScriptNode getDirectory() {
		try {
			NodeRef ref = service.getBusinessJournalDirectory();
			return new ScriptNode(ref, serviceRegistry, getScope());
		} catch (Exception e) {
			throw new ScriptException("Не удалось получить директорию с бизнес-журналом", e);
		}
	}

    private Date getDateFromLong(long longDate) {
        if (longDate != -1) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(longDate);
            return calendar.getTime();
        }
        return null;
    }

	public String getObjectDescription(String objectRef) {
		NodeRef ref = new NodeRef(objectRef);
		return service.getObjectDescription(ref);
	}

	public boolean archiveRecord(String recordRef) {
		boolean result = false;
		NodeRef ref = new NodeRef(recordRef);
		if (service.isBJRecord(ref)) {
			result = service.moveRecordToArchive(ref);
		}
		return result;
	}

	public ScriptNode getArchiveDirectory() {
		try {
			NodeRef ref = service.getBusinessJournalArchiveDirectory();
			return new ScriptNode(ref, serviceRegistry, getScope());
		} catch (Exception e) {
			throw new ScriptException("Не удалось получить директорию с архивными записями", e);
		}
	}

	public Scriptable findOldRecords(String dateStr) {
        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd").parse(dateStr);

            List<NodeRef> refs = service.getRecordsByInterval(null, date);
            return createScriptable(refs);
        } catch (ParseException e) {
            throw new ScriptException("Неверный формат даты!", e);
        }
	}

    public Scriptable getHistory(String nodeRef, String sortColumnName, boolean ascending, boolean includeSecondary) {
        ParameterCheck.mandatory("parentRef", nodeRef);
        NodeRef ref = new NodeRef(nodeRef);
        List<NodeRef> records = service.getHistory(ref, sortColumnName, ascending, includeSecondary);

        return createScriptable(records);
    }

    public Scriptable getStatusHistory(String nodeRef, String sortColumnName, boolean ascending) {
        ParameterCheck.mandatory("parentRef", nodeRef);
        NodeRef ref = new NodeRef(nodeRef);
        List<NodeRef> records = service.getStatusHistory(ref, sortColumnName, ascending);

        return createScriptable(records);
    }

	public void setArchiverSettings(BusinessJournalArchiverSettings archiverSettings) {
		this.archiverSettings = archiverSettings;
	}

	public ScriptNode getArchSettings(){
		NodeRef settings = archiverSettings.getArchiveSettingsRef();
		return new ScriptNode(settings, serviceRegistry, getScope());
	}

	public boolean isBJEngeneer() {
		return service.isBJEngineer();
	}
}
