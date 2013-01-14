package ru.it.lecm.statemachine.action;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.impl.util.xml.Element;

import java.util.ArrayList;
import java.util.List;

/**
 * User: PMelnikov
 * Date: 11.01.13
 * Time: 17:27
 */
public class DocumentPermissionAction extends StateMachineAction {

	private ArrayList<String> roles = new ArrayList<String>();

	@Override
	public void execute(DelegateExecution execution) {
	}

	@Override
	public void init(Element actionElement, String processId) {
		List<Element> attributes = actionElement.elements("attribute");
		for (Element attribute : attributes) {
			String value = attribute.attribute("value");
			roles.add(value);
		}
	}

}
