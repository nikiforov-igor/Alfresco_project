/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.it.lecm.signed.docflow.evaluators;

import org.alfresco.web.evaluator.BaseEvaluator;
import org.json.simple.JSONObject;

/**
 *
 * @author ikhalikov
 */
public class isCreatorEvaluator extends BaseEvaluator{

	@Override
	public boolean evaluate(JSONObject jsono) {
		String userName = getUserId();		
		return getProperty(jsono, "cm:creator").equals(userName);
	}
	
}
