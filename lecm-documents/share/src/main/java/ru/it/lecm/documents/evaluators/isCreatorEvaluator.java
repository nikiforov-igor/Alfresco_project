package ru.it.lecm.documents.evaluators;

import org.alfresco.web.evaluator.BaseEvaluator;
import org.json.simple.JSONObject;

/**
 * User: dbashmakov
 * Date: 16.05.2017
 * Time: 16:48
 */
public class IsCreatorEvaluator extends BaseEvaluator {
    @Override
    public boolean evaluate(JSONObject jsonObject) {
        return getMatchesCurrentUser(jsonObject, "cm:creator");
    }
}
