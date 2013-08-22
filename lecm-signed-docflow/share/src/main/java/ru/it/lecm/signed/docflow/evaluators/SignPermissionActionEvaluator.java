/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.it.lecm.signed.docflow.evaluators;

import org.alfresco.web.evaluator.BaseEvaluator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONObject;

/**
 *
 * @author ikhalikov
 */
public class SignPermissionActionEvaluator extends BaseEvaluator {
	private final static Log logger = LogFactory.getLog(SignPermissionActionEvaluator.class);
	private String businessRoleName;
	private EvaluatorsUtil evaluatorsUtil;

	public void setEvaluatorsUtil(EvaluatorsUtil evaluatorsUtil) {
		this.evaluatorsUtil = evaluatorsUtil;
	}

	public void setBusinessRoleName(String businessRoleName) {
		this.businessRoleName = businessRoleName;
	}
		
	@Override
	public boolean evaluate(JSONObject jsono) {
		return evaluatorsUtil.hasBusinessRoleOrBoss(getUserId(), businessRoleName);
	}

}
