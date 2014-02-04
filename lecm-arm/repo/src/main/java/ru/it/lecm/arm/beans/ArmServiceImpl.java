package ru.it.lecm.arm.beans;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.NodeRef;
import ru.it.lecm.base.beans.BaseBean;

import java.util.ArrayList;
import java.util.List;

/**
 * User: AIvkin
 * Date: 04.02.14
 * Time: 10:10
 */
public class ArmServiceImpl extends BaseBean implements ArmService {

	@Override
	public NodeRef getServiceRootFolder() {
		return getFolder(ARM_ROOT_ID);
	}

	public NodeRef getDictionaryArmSettings() {
		return nodeService.getChildByName(getServiceRootFolder(), ContentModel.ASSOC_CONTAINS, ARM_SETTINGS_DICTIONARY_NAME);
	}

    public List<NodeRef> getRoots() {
        return new ArrayList<NodeRef>();
    }

    public List<NodeRef> getChilds(NodeRef node) {
        return new ArrayList<NodeRef>();
    }

    public Long getNodeCounterValue(NodeRef node) {
        return 0L;
    }

    public boolean isCounterEnable(NodeRef node) {
        return true;
    }

    public String getNodeFilter(NodeRef node) {
        return "";
    }

    public boolean hasChild(NodeRef node) {
        return !getChilds(node).isEmpty();
    }

    public String getChildTypes(NodeRef node) {
        return "lecm-document:base";
    }
}
