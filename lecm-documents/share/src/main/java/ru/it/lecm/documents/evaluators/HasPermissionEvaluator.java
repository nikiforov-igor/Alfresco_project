package ru.it.lecm.documents.evaluators;

import org.alfresco.web.evaluator.BaseEvaluator;
import org.json.simple.JSONObject;

/**
 * User: dbashmakov
 * Date: 16.05.2017
 * Time: 16:08
 */
public class HasPermissionEvaluator extends BaseEvaluator {
    private EvaluatorsUtil evaluatorsUtil;
    private String permission;

    public void setEvaluatorsUtil(EvaluatorsUtil evaluatorsUtil) {
        this.evaluatorsUtil = evaluatorsUtil;
    }

    @Override
    public boolean evaluate(JSONObject jsonObject) {
        final String nodeRef = (String) jsonObject.get("nodeRef");
        final String login = getUserId();
        return evaluatorsUtil.hasPermissionOnAttachment(login, nodeRef, permission);
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }
}
