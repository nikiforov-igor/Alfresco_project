package ru.it.lecm.statemachine.action.document;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.impl.util.xml.Element;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import ru.it.lecm.statemachine.StateMachineHelper;
import ru.it.lecm.statemachine.action.StateMachineAction;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * User: PMelnikov
 * Date: 23.10.12
 * Time: 8:47
 */
public class WaitForDocumentChangeAction extends StateMachineAction {

	private List<Expression> expressions = new ArrayList<Expression>();

	private static final String TAG_EXPRESSIONS = "expressions";
	private static final String TAG_EXPRESSION = "expression";

	private static final String PROP_EXPRESSION = "expression";
	private static final String PROP_OUTPUT_VARIABLE = "outputVariable";
	private static final String PROP_OUTPUT_VALUE = "outputValue";

	@Override
	public void init(Element element, String processId) {
		Element expressions = element.element(TAG_EXPRESSIONS);

		String outputVariable = expressions.attribute(PROP_OUTPUT_VARIABLE);

		for (Element expressionElement : expressions.elements(TAG_EXPRESSION)) {
			String expression = expressionElement.attribute(PROP_EXPRESSION);
			String outputValue = expressionElement.attribute(PROP_OUTPUT_VALUE);
			this.expressions.add(new Expression(expression, outputVariable, outputValue));
		}
	}

	public List<Expression> getExpressions() {
		return expressions;
	}

	@Override
	public void execute(DelegateExecution execution) {
		final String processId = execution.getProcessInstanceId();
		Timer timer = new Timer();
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				AuthenticationUtil.runAs(new AuthenticationUtil.RunAsWork<Object>() {

					@Override
					public Object doWork() throws Exception {
						StateMachineHelper helper = new StateMachineHelper();
						String taskId = helper.getCurrentTaskId(processId);
						helper.startDocumentProcessing(taskId.replace(StateMachineHelper.ACTIVITI_PREFIX, ""));
						return null;
					}
				}, AuthenticationUtil.SYSTEM_USER_NAME);
			}
		};
		timer.schedule(task, 1000);
        for (Expression expression : expressions) {
            execution.setVariable(expression.getOutputVariable(), "");
        }
	}


	public class Expression {

		private String expression = null;
		private String outputVariable = null;
		private String outputValue = null;

		public Expression(String expression, String outputVariable, String outputValue) {
			this.expression = expression;
			this.outputVariable = outputVariable;
			this.outputValue = outputValue;
		}

		public String getExpression() {
			return expression;
		}

		public String getOutputVariable() {
			return outputVariable;
		}

		public String getOutputValue() {
			return outputValue;
		}

	}
}
