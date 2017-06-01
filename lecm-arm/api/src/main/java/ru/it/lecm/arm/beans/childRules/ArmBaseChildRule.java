package ru.it.lecm.arm.beans.childRules;

import org.alfresco.service.cmr.repository.NodeRef;
import ru.it.lecm.arm.beans.ArmWrapperService;
import ru.it.lecm.arm.beans.node.ArmNode;

import java.util.List;

/**
 * User: AIvkin
 * Date: 05.02.14
 * Time: 9:57
 */
public abstract class ArmBaseChildRule {
	private String substituteString;
    //заглушка для кэширования пустых значений
    public static final ArmBaseChildRule NULL_RULE = new ArmBaseChildRule() {
        @Override
        public List<ArmNode> build(ArmWrapperService service, ArmNode node) {
            return null;
        }

        @Override
        public List<NodeRef> getChildren(NodeRef node) {
            return null;
        }
    };

    abstract public List<ArmNode> build(ArmWrapperService service, ArmNode node);

    abstract public List<NodeRef> getChildren(NodeRef node);

	public String getSubstituteString() {
		return substituteString;
	}

	public void setSubstituteString(String substituteString) {
		this.substituteString = substituteString;
	}

}
