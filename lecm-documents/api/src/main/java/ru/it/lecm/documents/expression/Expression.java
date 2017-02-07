package ru.it.lecm.documents.expression;

import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.NodeRef;
import org.springframework.context.ApplicationContext;
import ru.it.lecm.statemachine.StateMachineServiceBean;

import java.util.HashMap;
import java.util.Map;

/**
 * User: PMelnikov
 * Date: 07.02.13
 * Time: 16:44
 */
public class Expression extends BaseSpellExpression{

    private Map<String, Object> state = new HashMap<>();
    private ExpressionDocument doc;
    private ExpressionUser user;
    private static StateMachineServiceBean stateMachineService;

    public Expression() {
    }

    public Expression(NodeRef document, ServiceRegistry serviceRegistry, ApplicationContext applicationContext) {
        super(applicationContext);
        this.doc = new ExpressionDocument(document);
        this.user = new ExpressionUser(document, serviceRegistry, orgstructureBean, documentService);
        String executionId = stateMachineService.getStatemachineId(document);
        if (!"Не запущен".equals(executionId)) {
            this.state = stateMachineService.getVariables(executionId);
        }
    }

    public Object state(String variableName) {
        return state.get(variableName);
    }

    public ExpressionDocument getDoc() {
        return doc;
    }

    public ExpressionUser getUser() {
        return user;
    }

    public void setStateMachineService(StateMachineServiceBean stateMachineService) {
        Expression.stateMachineService = stateMachineService;
    }
}
