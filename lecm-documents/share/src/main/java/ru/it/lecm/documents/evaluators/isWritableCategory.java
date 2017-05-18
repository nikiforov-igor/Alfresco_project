package ru.it.lecm.documents.evaluators;

import org.alfresco.web.evaluator.BaseEvaluator;
import org.json.simple.JSONObject;

/**
 * User: dbashmakov
 * Date: 17.05.2017
 * Time: 10:09
 */
public class IsWritableCategory extends BaseEvaluator {
    private EvaluatorsUtil evaluatorsUtil;

    public void setEvaluatorsUtil(EvaluatorsUtil evaluatorsUtil) {
        this.evaluatorsUtil = evaluatorsUtil;
    }

    @Override
    public boolean evaluate(JSONObject jsonObject) {
        final String nodeRef = (String) jsonObject.get("nodeRef");
        return !evaluatorsUtil.checkReadOnlyCategory(nodeRef);
    }
}
