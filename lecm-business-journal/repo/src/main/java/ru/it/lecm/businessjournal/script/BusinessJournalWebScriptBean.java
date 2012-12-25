package ru.it.lecm.businessjournal.script;

import java.util.*;

import org.alfresco.repo.jscript.BaseScopableProcessorExtension;
import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.service.cmr.repository.NodeRef;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import ru.it.lecm.businessjournal.beans.BusinessJournalService;

/**
 * @author dbashmakov
 *         Date: 25.12.12
 *         Time: 13:57
 */
public class BusinessJournalWebScriptBean extends BaseScopableProcessorExtension {

	private BusinessJournalService service;

	public void setService(BusinessJournalService service) {
		this.service = service;
	}
	//TODO метод для тестирования. возможно будет переработан и оставлен
	public ScriptNode fire(long time, String initiator, String mainObject, String objType, String eventCategory, String description, Scriptable objects) {
		NodeRef record = null;
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(time);
		Object[] objs = Context.getCurrentContext().getElements(objects);
		List<NodeRef> refs = new ArrayList<NodeRef>();
		for (int i = 0; i < objs.length; i++) {
			String ref = (String) objs[i];
			refs.add(new NodeRef(ref));
		}
		record = service.fire(calendar.getTime(), new NodeRef(initiator), new NodeRef(mainObject), new NodeRef(objType), new NodeRef(eventCategory), description, refs);
		return new ScriptNode(record, service.getServiceRegistry(), getScope());
	}
}
