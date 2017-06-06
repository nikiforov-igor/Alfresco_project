package ru.it.lecm.arm.expression;

import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.NodeRef;
import org.springframework.context.ApplicationContext;
import ru.it.lecm.documents.expression.BaseSpellExpression;
import ru.it.lecm.documents.expression.ExpressionUser;

/**
 * User: dbashmakov
 * Date: 03.02.2017
 * Time: 10:30
 */
public class ExpressionForArm extends BaseSpellExpression {
    private ExpressionArmNode armNode;
    private ExpressionUser user;

    private static ServiceRegistry serviceRegistry;

    public void setServiceRegistry(ServiceRegistry serviceRegistry) {
        ExpressionForArm.serviceRegistry = serviceRegistry;
    }

    public ExpressionForArm(NodeRef armNode, ApplicationContext applicationContext) {
        super(applicationContext);
        this.armNode = new ExpressionArmNode(armNode);
        this.user = new ExpressionUser(armNode, serviceRegistry, orgstructureBean, documentService);
    }

    public ExpressionForArm(NodeRef armNode, ApplicationContext applicationContext, NodeRef runAs) {
        super(applicationContext);
        this.armNode = new ExpressionArmNode(armNode, runAs);
        this.user = new ExpressionUser(armNode, serviceRegistry, orgstructureBean, documentService);
    }

    public ExpressionForArm() {
    }

    public ExpressionArmNode getArmNode() {
        return armNode;
    }

    public ExpressionUser getUser() {
        return user;
    }
}
