package ru.it.lecm.arm.beans.childRules;

import ru.it.lecm.arm.beans.ArmWrapperService;
import ru.it.lecm.arm.beans.node.ArmNode;

import java.util.List;

/**
 * User: AIvkin
 * Date: 05.02.14
 * Time: 9:57
 */
public abstract class ArmBaseChildRule {
    abstract public List<ArmNode> build(ArmWrapperService service, ArmNode node);

    abstract public ArmBaseChildRule getDuplicate();
}
