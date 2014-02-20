package ru.it.lecm.documents.evaluators;

import java.util.Map;
import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.surf.extensibility.impl.DefaultSubComponentEvaluator;

/**
 *
 * @author snovikov
 */
public class HasReadAttachmentComponentEvaluator extends DefaultSubComponentEvaluator {
	private EvaluatorsUtil evaluatorsUtil;

	public void setEvaluatorsUtil(EvaluatorsUtil evaluatorsUtil) {
		this.evaluatorsUtil = evaluatorsUtil;
	}

	@Override
	public boolean evaluate(RequestContext context, Map<String, String> params) {
		String nodeRef = params.get("nodeRef");
		String permission = params.get("permission");
		String aspect = params.get("aspect");
		String user = context.getUserId();
		boolean hasPermission = evaluatorsUtil.hasPermission(nodeRef, permission,aspect,user);
		return !hasPermission;
	}
}
