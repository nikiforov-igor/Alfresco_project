package ru.it.lecm.wcalendar.absence.extensions;

import java.util.List;
import org.alfresco.service.cmr.repository.NodeRef;
import org.json.JSONException;
import org.json.JSONObject;
import org.mozilla.javascript.Scriptable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.extensions.webscripts.WebScriptException;
import ru.it.lecm.wcalendar.absence.beans.AbsenceBean;
import ru.it.lecm.wcalendar.extensions.WCalendarJavascriptExtension;

/**
 *
 * @author vlevin
 */
public class AbsenceJavascriptExtension extends WCalendarJavascriptExtension {

	private AbsenceBean AbsenceService;
	private final static Logger logger = LoggerFactory.getLogger(AbsenceJavascriptExtension.class);

	public void setAbsenceService(AbsenceBean AbsenceService) {
		this.AbsenceService = AbsenceService;
	}

	public Scriptable getAbsenceByEmployee(final String nodeRefStr) {
		List<NodeRef> absenceList = AbsenceService.getAbsenceByEmployee(new NodeRef(nodeRefStr));
		if (absenceList != null) {
			return getAsScriptable(absenceList);
		} else {
			return null;
		}
	}

	public Scriptable getAbsenceByEmployee(final JSONObject node) {
		try {
			return getAbsenceByEmployee(node.getString("nodeRef"));
		} catch (JSONException ex) {
			throw new WebScriptException(ex.getMessage(), ex);
		}
	}

	public boolean isAbsenceAssociated(final String nodeRefStr) {
		return AbsenceService.isAbsenceAssociated(new NodeRef(nodeRefStr));
	}
}
