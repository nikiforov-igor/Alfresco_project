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

    public void register(ValueEvaluator newEvaluator) {
        if (newEvaluator != null) {
            final String id = newEvaluator.getId();
            if (id != null && !id.isEmpty()) {
                if (evaluators.get(id) == null) {
                    evaluators.put(id, newEvaluator);
                } else {
                    logger.warn("Evaluator with id= " + id + " already registered. New registration skipped...");
                }
            } else {
                logger.warn("Evaluator with class= " + newEvaluator.getClass().getName() + " has not ID. Registration skipped...");
            }
        }
    }

    public String evaluate(JSONObject value) throws JSONException {
        String TYPE_FIELD = "type";
        if (value.has(TYPE_FIELD)) {
            String evaluatorId = value.getString(TYPE_FIELD);
            ValueEvaluator evaluator = evaluators.get(evaluatorId);
            if (evaluator != null) {
                return evaluator.evaluate(value);
            }
        }
        return value.toString();
    }
}
