/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.it.lecm.signed.docflow.evaluators;

import java.util.Map;
import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.surf.extensibility.impl.DefaultSubComponentEvaluator;

/**
 *
 * @author ikhalikov
 */
public class hasAspectComponentEvaluator extends DefaultSubComponentEvaluator{

	private EvaluatorsUtil evaluatorsUtil;

	public void setEvaluatorsUtil(EvaluatorsUtil evaluatorsUtil) {
		this.evaluatorsUtil = evaluatorsUtil;
	}

	@Override
	public boolean evaluate(RequestContext context, Map<String, String> params) {
		String nodeRef = params.get("nodeRef");
		String aspect = params.get("aspect");
		return evaluatorsUtil.hasProperties(nodeRef);


	}

}
