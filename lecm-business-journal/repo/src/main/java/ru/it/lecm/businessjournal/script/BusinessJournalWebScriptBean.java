package ru.it.lecm.businessjournal.script;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.alfresco.repo.jscript.BaseScopableProcessorExtension;
import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.scripts.ScriptException;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.security.AuthenticationService;
import org.alfresco.service.cmr.security.PersonService;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.springframework.extensions.surf.util.ParameterCheck;
import ru.it.lecm.businessjournal.beans.BusinessJournalServiceImpl;
import ru.it.lecm.businessjournal.schedule.BusinessJournalArchiverSettings;

/**
 * @author dbashmakov
 *         Date: 25.12.12
 *         Time: 13:57
 */
public class BusinessJournalWebScriptBean extends BaseScopableProcessorExtension {

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
		// получаем инициатора
		String initiator = null;
		AuthenticationService authService = service.getServiceRegistry().getAuthenticationService();
		String init = authService.getCurrentUserName();
		PersonService personService = service.getServiceRegistry().getPersonService();
		if (personService.personExists(init)) {
			initiator = personService.getPerson(init, false).toString();
		}
		try {
			record = service.log(new NodeRef(initiator), new NodeRef(mainObject), eventCategory, description, refs);
		} catch (Exception e) {
			throw new ScriptException("Не удалось создать запись бизнес-журнала", e);
		}
		return new ScriptNode(record, service.getServiceRegistry(), getScope());
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
		return new ScriptNode(ref, service.getServiceRegistry(), getScope());
	}

    public Scriptable getRecordsByParams(String objectType, String daysCount, String whoseKey) {
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
        List<NodeRef> refs = service.getRecordsByParams(objectType, start, now, whoseKey);
        return createScriptable(refs);
    }

	/**
	 * Возвращает массив, пригодный для использования в веб-скриптах
	 *
	 * @return Scriptable
	 */
	private Scriptable createScriptable(List<NodeRef> refs) {
		Object[] results = new Object[refs.size()];
		for (int i = 0; i < results.length; i++) {
			results[i] = new ScriptNode(refs.get(i), service.getServiceRegistry(), getScope());
		}
		return Context.getCurrentContext().newArray(getScope(), results);
	}

	public ScriptNode getDirectory() {
		try {
			NodeRef ref = service.getBusinessJournalDirectory();
			return new ScriptNode(ref, service.getServiceRegistry(), getScope());
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
			return new ScriptNode(ref, service.getServiceRegistry(), getScope());
		} catch (Exception e) {
			throw new ScriptException("Не удалось получить директорию с архивными записями", e);
		}
	}

	public Scriptable findOldRecords(String dateISO8601) {
        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS").parse(dateISO8601);

            List<NodeRef> refs = service.getRecordsByInterval(null, date);
            return createScriptable(refs);
        } catch (ParseException e) {
            throw new ScriptException("Неверный формат даты!", e);
        }
	}

    public Scriptable getHistory(String nodeRef, String sortColumnName, boolean ascending) {
        ParameterCheck.mandatory("parentRef", nodeRef);
        NodeRef ref = new NodeRef(nodeRef);
        List<NodeRef> records = service.getHistory(ref, sortColumnName, ascending);

        return createScriptable(records);
    }

	public void setArchiverSettings(BusinessJournalArchiverSettings archiverSettings) {
		this.archiverSettings = archiverSettings;
	}

	public ScriptNode getArchSettings(){
		NodeRef settings = archiverSettings.getArchiveSettingsRef();
		return new ScriptNode(settings, service.getServiceRegistry(), getScope());
	}

	public boolean isBJEngeneer() {
		return service.isBJEngineer();
	}
}
