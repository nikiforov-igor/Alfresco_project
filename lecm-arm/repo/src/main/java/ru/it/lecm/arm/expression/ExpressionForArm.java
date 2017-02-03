package ru.it.lecm.arm.expression;

import org.alfresco.service.ServiceRegistry;
import org.springframework.context.ApplicationContext;
import ru.it.lecm.arm.beans.node.ArmNode;
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

    public ExpressionForArm(ArmNode node, ApplicationContext applicationContext) {
        super(applicationContext);
        this.armNode = new ExpressionArmNode(node);
        this.user = new ExpressionUser(node.getArmNodeRef(), serviceRegistry, orgstructureBean, documentService);
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
