/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.it.lecm.signed.docflow.evaluators;

import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.surf.extensibility.impl.DefaultSubComponentEvaluator;

/**
 *
 * @author ikhalikov
 */
public class SignPermissionComponentEvaluator extends DefaultSubComponentEvaluator {

	private final static Log logger = LogFactory.getLog(SignPermissionComponentEvaluator.class);
	private String roleId;
	private EvaluatorsUtil evaluatorsUtil;

	public void setEvaluatorsUtil(EvaluatorsUtil evaluatorsUtil) {
		this.evaluatorsUtil = evaluatorsUtil;
	}


	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}

	@Override
	public boolean evaluate(RequestContext context, Map<String, String> params) {
		String userName = context.getUserId();
		return evaluatorsUtil.hasBusinessRoleOrBoss(userName, roleId);
	}
}
