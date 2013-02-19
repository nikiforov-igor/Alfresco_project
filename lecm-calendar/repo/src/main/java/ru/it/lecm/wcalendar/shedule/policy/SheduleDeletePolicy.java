package ru.it.lecm.wcalendar.shedule.policy;

import java.io.Serializable;
import java.util.Map;
import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.PropertyCheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.wcalendar.shedule.IShedule;

/**
 * Полиси, срабатывающее на изменение проперти у объекта типа shedule. Если
 * lecm-dic:active поменялось на false, график считается удаленным и у него
 * удаляется ассоциация с сотрудником, чтобы избежать проблем, связанных с типом
 * ассоциации.
 *
 * @author vlevin
 */
public class SheduleDeletePolicy implements NodeServicePolicies.OnUpdatePropertiesPolicy {

	private IShedule sheduleService;
	private PolicyComponent policyComponent;
	private final static Logger logger = LoggerFactory.getLogger(SheduleDeletePolicy.class);

	public final void init() {
		PropertyCheck.mandatory(this, "policyComponent", policyComponent);
		PropertyCheck.mandatory(this, "sheduleService", sheduleService);

		logger.info("Initializing SheduleDeletePolicy");
		policyComponent.bindClassBehaviour(NodeServicePolicies.OnUpdatePropertiesPolicy.QNAME, IShedule.TYPE_SHEDULE, new JavaBehaviour(this, "onUpdateProperties"));

	}

	public void setPolicyComponent(PolicyComponent policyComponent) {
		this.policyComponent = policyComponent;
	}

	public void setSheduleService(IShedule sheduleService) {
		this.sheduleService = sheduleService;
	}

	@Override
	public void onUpdateProperties(NodeRef nodeRef, Map<QName, Serializable> before, Map<QName, Serializable> after) {
		final Boolean prevActive = (Boolean) before.get(BaseBean.IS_ACTIVE);
		final Boolean curActive = (Boolean) after.get(BaseBean.IS_ACTIVE);

		if (curActive != null && curActive == false && curActive != prevActive) {
			sheduleService.unlinkShedule(nodeRef);
		}
		logger.debug(String.format("Policy SheduleDeletePolicy invoked on %s", nodeRef.toString()));
	}
}
