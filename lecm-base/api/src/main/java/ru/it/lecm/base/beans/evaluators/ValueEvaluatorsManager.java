package ru.it.lecm.base.beans.evaluators;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * User: dbashmakov
 * Date: 04.05.2017
 * Time: 16:25
 */
public class ValueEvaluatorsManager {
    private final static Logger logger = LoggerFactory.getLogger(ValueEvaluatorsManager.class);
    private Map<String, ValueEvaluator> evaluators = new HashMap<>();

    public void resister(ValueEvaluator newEvaluator) {
        if (newEvaluator != null) {
            final String id = newEvaluator.getId();
            if (id != null && !id.isEmpty()) {
                if (getEvaluatorById(id) == null) {
                    getEvaluators().put(id, newEvaluator);
                } else {
                    logger.debug("Evaluator with id= " + id + " already registered. New registration skipped...");
                }
            } else {
                logger.debug("Evaluator with class= " + newEvaluator.getClass().getName() + " has not ID. Registration skipped...");
            }
        }
    }

    public ValueEvaluator getEvaluatorById(String id) {
        return getEvaluators().get(id);
    }

    public Map<String, ValueEvaluator> getEvaluators() {
        return evaluators;
    }

    public String evaluate(JSONObject value) throws JSONException {
        String TYPE_FIELD = "type";
        if (value.has(TYPE_FIELD)) {
            String evaluatorId = value.getString(TYPE_FIELD);
            ValueEvaluator evaluator = getEvaluatorById(evaluatorId);
            if (evaluator != null) {
                return evaluator.evaluate(value);
            }
        }
        return value.toString();
    }
}
