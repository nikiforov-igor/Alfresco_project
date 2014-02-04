package ru.it.lecm.arm.beans;

import org.alfresco.service.cmr.repository.NodeRef;
import ru.it.lecm.base.beans.BaseBean;

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
}
