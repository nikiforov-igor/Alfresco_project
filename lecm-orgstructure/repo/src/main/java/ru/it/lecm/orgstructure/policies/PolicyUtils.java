package ru.it.lecm.orgstructure.policies;

import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;

import ru.it.lecm.orgstructure.beans.OrgstructureBean;

public class PolicyUtils {

	// имя (логин) пользователя (cm:person)
	public static final QName PROP_USER_NAME = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, "userName");

	// lecm-orgstr:staffPosition
	// название должностной позиции
	public static final QName PROP_DP_NAME = QName.createQName(OrgstructureBean.ORGSTRUCTURE_NAMESPACE_URI, "lecm-orgstr:staffPosition-code");

}
