package ru.it.lecm.statemachine.bean;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.extensions.surf.util.I18NUtil;
import ru.it.lecm.statemachine.assign.AssignExecution;

import java.util.Collection;
import java.util.HashMap;

/**
 * User: PMelnikov
 * Date: 17.10.12
 * Time: 11:15
 */
public class DocumentStateMachineBean implements InitializingBean {

	private String documentType = null;
	private String stateMachineId = null;
	private static HashMap<String, String> stateMachines = new HashMap<String, String>();

	public void setDocumentType(String documentType) {
		this.documentType = documentType;
	}

	public void setStateMachineId(String stateMachineId) {
		this.stateMachineId = stateMachineId;
	}

	public HashMap<String, String> getStateMachines() {
		return stateMachines;
	}

	public Collection<String> getProcesses() {
		return stateMachines.values();
	}

	public AssignExecution testExecution() {
		return new AssignExecution();
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		if (documentType != null && stateMachineId != null) {
			stateMachines.put(documentType, stateMachineId);
		}
	}

	public String getTitle(String processId) {
		String key = "statemachine";
		key += "." + processId;
		key += ".title";

		String message = I18NUtil.getMessage(key, I18NUtil.getLocale());
		return message == null ? processId : message;
	}

	public String getDescription(String processId) {
		String key = "statemachine";
		key += "." + processId;
		key += ".description";

		String message = I18NUtil.getMessage(key, I18NUtil.getLocale());
		return message == null ? processId : message;
	}

}
