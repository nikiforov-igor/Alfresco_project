package ru.it.lecm.businessjournal.script;

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
import org.json.JSONObject;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.Scriptable;
import ru.it.lecm.businessjournal.beans.BusinessJournalServiceImpl;

/**
 * @author dbashmakov
 *         Date: 25.12.12
 *         Time: 13:57
 */
public class BusinessJournalWebScriptBean extends BaseScopableProcessorExtension {

	private BusinessJournalServiceImpl service;

	public void setService(BusinessJournalServiceImpl service) {
		this.service = service;
	}

	public ScriptNode fire(String initiator, String mainObject, String eventCategory, String description, Scriptable objects) {
		NodeRef record = null;
		Object[] objs = Context.getCurrentContext().getElements(objects);
		List<NodeRef> refs = new ArrayList<NodeRef>();
		for (Object obj : objs) {
			NativeJavaObject ref = (NativeJavaObject) obj;
			refs.add((NodeRef) ref.unwrap());
		}
		if (initiator == null) {
			// получаем инициатора
			AuthenticationService authService = service.getServiceRegistry().getAuthenticationService();
			String init = authService.getCurrentUserName();
			PersonService personService = service.getServiceRegistry().getPersonService();
			if (personService.personExists(init)){
				initiator = personService.getPerson(init, false).toString();
			}
		}
		try {
			record = service.fire(new NodeRef(initiator), new NodeRef(mainObject), eventCategory, description, refs);
		} catch (Exception e) {
			throw new ScriptException("Не удалось создать запись бизнес-журнала", e);
		}
		return new ScriptNode(record, service.getServiceRegistry(), getScope());
	}

	public Scriptable getRecordsByInterval(long start, long end) {
		List<NodeRef> refs = service.getRecordsByInterval(getDateFromLong(start), getDateFromLong(end));
		return createScriptable(refs);
	}

	public JSONObject getRecord(String recordRef) {
		NodeRef ref = new NodeRef(recordRef);
		try {
			return service.getRecordJSON(ref);
		} catch (Exception e) {
			throw new ScriptException("Неправильный объект. Параметр должен содержать ссылку на запись бизнес-журнала", e);
		}
	}

    public Scriptable getRecordsByParams(String objectType, String daysCount, String whoseKey) {
        Date now = new Date();
        Date start = null;

        if (daysCount != null &&  !"".equals(daysCount)) {
            Integer days = Integer.parseInt(daysCount);

            if (days > 0) {
                Calendar calendar = Calendar.getInstance();

                calendar.setTime(now);
                calendar.add(Calendar.DAY_OF_MONTH, (-1) * days);
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

    public Date getDateFromLong(long longDate) {
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
}
