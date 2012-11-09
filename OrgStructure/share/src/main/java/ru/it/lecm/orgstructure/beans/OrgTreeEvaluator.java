package ru.it.lecm.orgstructure.beans;

import java.util.Map;

import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.surf.extensibility.impl.DefaultSubComponentEvaluator;

/**
 * @author dbashmakov
 *         Date: 09.10.12
 *         Time: 9:50
 */
public class OrgTreeEvaluator extends DefaultSubComponentEvaluator {
	// Не выводить дерево, когда задан параметр и параметр не равен нужному
	public boolean evaluate(RequestContext context, Map<String, String> params) {
		boolean result;
		String type = context.getParameter("type");
		result = (type != null && !type.equals("structure"));
		return result;
	}
}
