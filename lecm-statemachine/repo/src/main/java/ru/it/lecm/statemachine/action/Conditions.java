package ru.it.lecm.statemachine.action;

//import org.activiti.engine.impl.util.xml.Element;
import org.activiti.bpmn.model.ExtensionElement;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: pmelnikov
 * Date: 06.03.13
 * Time: 11:49
 * To change this template use File | Settings | File Templates.
 */
public class Conditions {

    private List<Condition> conditions = new ArrayList<Condition>();

    public Conditions(){
    	
    }
    
    public Conditions(ExtensionElement conditionsElement) {
//        if (conditionsElement != null) {
//            List<Element> conditions = conditionsElement.elements("condition");
//            for (Element condition : conditions) {
//                String expression = condition.element("expression").getText();
//                String errorMessage = condition.element("errorMessage").getText();
//                Element hideActionValue = condition.element("hideAction");
//                Boolean hideAction;
//                if (hideActionValue != null) {
//                    hideAction = Boolean.valueOf(hideActionValue.getText());
//                } else {
//                    hideAction = false;
//                }
//                this.conditions.add(new Condition(expression, errorMessage, hideAction));
//            }
//        }
    }

    public List<Condition> getConditions() {
        return conditions;
    }
    
    public void addCondition(String expression, String errorMessage, boolean hideAction) {
    	this.conditions.add(new Condition(expression, errorMessage, hideAction));
    }

    public class Condition {

        private String expression;
        private String errorMessage;
        private boolean hideAction;
        private Set<String> fields = new HashSet<String>();

        public Condition(String expression, String errorMessage, boolean hideAction) {
            this.expression = expression;
            this.errorMessage = errorMessage;
            Pattern pattern = Pattern.compile("doc\\.attr\\(\"(.*?)\"\\)");
            Matcher matcher = pattern.matcher(expression);
            while (matcher.find()) {
                fields.add(matcher.group(1));
            }
            this.hideAction = hideAction;
        }

        public String getExpression() {
            return expression;
        }

        public String getErrorMessage() {
            return errorMessage.replaceAll("\\p{Space}{2,}|\\s+", " ");
        }

        public Set<String> getFields() {
            return fields;
        }

        public boolean isHideAction() {
            return hideAction;
        }
    }
}
