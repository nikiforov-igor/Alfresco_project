package ru.it.lecm.statemachine.expression;

/**
 * Created with IntelliJ IDEA.
 * User: pkotelnikova
 * Date: 21.06.13
 * Time: 16:56
 * To change this template use File | Settings | File Templates.
 */
public class TransitionExpression {
    private String expression = null;
    private String outputValue = null;
    private boolean stopSubWorkflows = false;
	private String script = null;

    public TransitionExpression(String expression, String outputValue, boolean stopSubWorkflows, String script) {
        this.expression = expression;
        this.outputValue = outputValue;
        this.stopSubWorkflows = stopSubWorkflows;
        this.script = script;
    }

    public String getExpression() {
        return expression;
    }

    public String getOutputValue() {
        return outputValue;
    }

    public boolean isStopSubWorkflows() {
        return stopSubWorkflows;
    }

    public String getScript() {
        return script == null ? "" : script;
    }

}
