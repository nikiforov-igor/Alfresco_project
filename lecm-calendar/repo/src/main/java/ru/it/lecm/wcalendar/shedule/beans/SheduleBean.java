package ru.it.lecm.wcalendar.shedule.beans;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.PropertyCheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.wcalendar.IWCalCommon;
import ru.it.lecm.wcalendar.beans.AbstractWCalCommonBean;

/**
 *
 * @author vlevin
 */
public class SheduleBean extends AbstractWCalCommonBean {

	public final static String CONTAINER_NAME = "SheduleContainer";
	public final static QName TYPE_SHEDULE = QName.createQName(SHEDULE_NAMESPACE, "shedule");
	public final static QName TYPE_SHEDULE_CONTAINER = QName.createQName(WCAL_NAMESPACE, "shedule-container");
	public final static QName ASSOC_SHEDULE_EMPLOYEE_LINK = QName.createQName(SHEDULE_NAMESPACE, "shed-employee-link-assoc");
	public final static QName PROP_SHEDULE_STD_BEGIN = QName.createQName(SHEDULE_NAMESPACE, "std-begin");
	public final static QName PROP_SHEDULE_STD_END = QName.createQName(SHEDULE_NAMESPACE, "std-end");
	public final static QName PROP_SHEDULE_TYPE = QName.createQName(SHEDULE_NAMESPACE, "type");
	// Получить логгер, чтобы писать, что с нами происходит.
	private final static Logger logger = LoggerFactory.getLogger(SheduleBean.class);

	@Override
	public IWCalCommon getWCalendarDescriptor() {
		return this;
	}

	@Override
	public QName getWCalendarItemType() {
		return TYPE_SHEDULE;
	}

	/**
	 * Метод, который запускает Spring при старте Tomcat-а. Создает корневой
	 * объект для графиков работы.
	 */
	public final void bootstrap() {
		PropertyCheck.mandatory(this, "repository", repository);
		PropertyCheck.mandatory(this, "nodeService", nodeService);
//		PropertyCheck.mandatory (this, "namespaceService", namespaceService);
		PropertyCheck.mandatory(this, "transactionService", transactionService);

		// Создание контейнера (если не существует).
		AuthenticationUtil.runAsSystem(this);
	}

	@Override
	protected Map<String, Object> containerParams() {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("CONTAINER_NAME", CONTAINER_NAME);
		params.put("CONTAINER_TYPE", TYPE_SHEDULE_CONTAINER);

		return params;
	}

	private NodeRef recursiveSheduleSearch(NodeRef node) {
		List<AssociationRef> sheduleAssocList = nodeService.getSourceAssocs(node, ASSOC_SHEDULE_EMPLOYEE_LINK);
		if (sheduleAssocList == null || sheduleAssocList.isEmpty()) {
			List<ChildAssociationRef> parentAssocList = nodeService.getParentAssocs(node);
			if (parentAssocList == null || parentAssocList.isEmpty()) {
				return null;
			}
			ChildAssociationRef parentAssoc = parentAssocList.get(0);
			NodeRef parentNode = parentAssoc.getParentRef();
			logger.debug(node.toString() + " has parent " + parentNode.toString());
			return recursiveSheduleSearch(parentNode);
		} else {
			AssociationRef sheduleAssoc = sheduleAssocList.get(0);
			NodeRef sheduleNode = sheduleAssoc.getSourceRef();
			return sheduleNode;
		}
	}

	public NodeRef getParentShedule(NodeRef node) {
		NodeRef primaryOU = null;
		QName nodeType = nodeService.getType(node);
		boolean searchFromCurrent = true;
		if (OrgstructureBean.TYPE_EMPLOYEE.isMatch(nodeType)) {
			List<AssociationRef> assocEmloyeeLinkList = nodeService.getSourceAssocs(node, OrgstructureBean.ASSOC_EMPLOYEE_LINK_EMPLOYEE);
			if (assocEmloyeeLinkList == null || assocEmloyeeLinkList.isEmpty()) {
				return null;
			}
			for (AssociationRef assocEmloyeeLink : assocEmloyeeLinkList) {
				NodeRef nodeEmployeeLink = assocEmloyeeLink.getSourceRef();
				Serializable isPrimaryLink = nodeService.getProperty(nodeEmployeeLink, OrgstructureBean.PROP_EMP_LINK_IS_PRIMARY);
				if (!(Boolean) isPrimaryLink) {
					continue;
				}
				List<AssociationRef> assocOrgElementMemberList = nodeService.getSourceAssocs(nodeEmployeeLink, OrgstructureBean.ASSOC_ELEMENT_MEMBER_EMPLOYEE);
				if (assocOrgElementMemberList == null || assocOrgElementMemberList.isEmpty()) {
					return null;
				}
				for (AssociationRef assocOrgElementMember : assocOrgElementMemberList) {
					NodeRef nodeOrgElementMember = assocOrgElementMember.getSourceRef();
					List<ChildAssociationRef> assocOrgUnitList = nodeService.getParentAssocs(nodeOrgElementMember);
					if (assocOrgUnitList == null || assocOrgUnitList.isEmpty()) {
						return null;
					}
					NodeRef nodeOrgUnit = (assocOrgUnitList.get(0)).getParentRef();
					primaryOU = nodeOrgUnit;
					searchFromCurrent = true;
				}
			}
		} else if (OrgstructureBean.TYPE_ORGANIZATION_UNIT.isMatch(nodeType)) {
			primaryOU = node;
			searchFromCurrent = false;
		}

		if (primaryOU == null) {
			return null;
		}

		NodeRef result;
		if (searchFromCurrent) {
			result = recursiveSheduleSearch(primaryOU);
		} else {
			List<ChildAssociationRef> parentAssocList = nodeService.getParentAssocs(primaryOU);
			if (parentAssocList == null || parentAssocList.isEmpty()) {
				return null;
			}
			ChildAssociationRef parentAssoc = parentAssocList.get(0);
			result = recursiveSheduleSearch(parentAssoc.getParentRef());
		}
		return result;
	}

	public Map<String, String> getParentSheduleStdTime(NodeRef node) {
		HashMap<String, String> result = new HashMap<String, String>();
		String sheduleStdBegin, sheduleStdEnd, sheduleType;
		NodeRef shedule = this.getParentShedule(node);
		if (shedule == null) {
			return null;
		}
		sheduleType = (String) nodeService.getProperty(shedule, PROP_SHEDULE_TYPE);
		if (sheduleType.equals("SPECIAL")) {
			sheduleStdBegin = "00:00";
			sheduleStdEnd = "00:00";

		} else {
			sheduleStdBegin = (String) nodeService.getProperty(shedule, PROP_SHEDULE_STD_BEGIN);
			sheduleStdEnd = (String) nodeService.getProperty(shedule, PROP_SHEDULE_STD_END);
		}
		result.put("type", sheduleType);
		result.put("begin", sheduleStdBegin);
		result.put("end", sheduleStdEnd);
		return result;
	}
}
