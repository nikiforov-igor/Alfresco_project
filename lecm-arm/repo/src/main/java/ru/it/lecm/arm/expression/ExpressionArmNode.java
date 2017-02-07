package ru.it.lecm.arm.expression;

import org.alfresco.service.cmr.repository.NodeRef;
import ru.it.lecm.arm.beans.ArmService;
import ru.it.lecm.arm.beans.ArmWrapperService;
import ru.it.lecm.arm.beans.node.ArmNode;
import ru.it.lecm.documents.expression.ExpressionNode;

/**
 * User: dbashmakov
 * Date: 03.02.2017
 * Time: 11:10
 */
public class ExpressionArmNode extends ExpressionNode {

    private static ArmService armService;
    private static ArmWrapperService armWrapper;

    private ArmNode armNode;

    public void setArmService(ArmService armService) {
        ExpressionArmNode.armService = armService;
    }

    public void setArmWrapper(ArmWrapperService armWrapper) {
        ExpressionArmNode.armWrapper = armWrapper;
    }

    public ExpressionArmNode() {
    }

    public ExpressionArmNode(NodeRef nodeRef) {
        super(nodeRef);
        this.armNode = armWrapper.wrapArmNodeAsObject(nodeRef, armService.isArmAccordion(nodeRef));
    }

    public ArmNode getArmNode() {
        return armNode;
    }

    public long count() {
        return armWrapper.getObjectsCount(armNode);
    }
}
