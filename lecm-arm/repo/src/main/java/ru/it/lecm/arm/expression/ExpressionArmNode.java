package ru.it.lecm.arm.expression;

import org.alfresco.service.ServiceRegistry;
import ru.it.lecm.arm.beans.ArmService;
import ru.it.lecm.arm.beans.ArmWrapperService;
import ru.it.lecm.arm.beans.node.ArmNode;

/**
 * User: dbashmakov
 * Date: 03.02.2017
 * Time: 11:10
 */
public class ExpressionArmNode {

    private static ServiceRegistry serviceRegistry;
    private static ArmService armService;
    private static ArmWrapperService armWrraper;

    private ArmNode node;

    public void setArmService(ArmService armService) {
        ExpressionArmNode.armService = armService;
    }

    public void setArmWrraper(ArmWrapperService armWrraper) {
        ExpressionArmNode.armWrraper = armWrraper;
    }

    public void setServiceRegistry(ServiceRegistry serviceRegistry) {
        ExpressionArmNode.serviceRegistry = serviceRegistry;
    }

    public ExpressionArmNode() {
    }

    public ExpressionArmNode(ArmNode node) {
        this.node = node;
    }
}
