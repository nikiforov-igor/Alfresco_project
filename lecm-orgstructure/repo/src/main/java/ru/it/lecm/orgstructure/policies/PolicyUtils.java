package ru.it.lecm.orgstructure.policies;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.slf4j.Logger;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.security.Types;

import java.util.*;

public class PolicyUtils {

	// имя (логин) пользователя (cm:person)
	public static final QName PROP_USER_NAME = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, "userName");

	// название Должностной Позиции
	// "lecm-orgstr:staffPosition"::"lecm-orgstr:staffPosition-code"
	public static final QName PROP_DP_INFO = ContentModel.PROP_NAME; // QName.createQName(OrgstructureBean.ORGSTRUCTURE_NAMESPACE_URI, "staffPosition-code");

	// "lecm-orgstr:organization-element"::"element-short-name"
	public static final QName PROP_ORGELEMENT_NAME = QName.createQName(OrgstructureBean.ORGSTRUCTURE_NAMESPACE_URI, "element-short-name");

	// "lecm-orgstr:organization-unit"::"unit-code"
	 public static final QName PROP_ORGUNIT_CODE = QName.createQName(OrgstructureBean.ORGSTRUCTURE_NAMESPACE_URI, "unit-code");

	/**
	 * Сравнить два объекта по значению
	 * @param o1
	 * @param o2
	 * @return true, если объекты равны (в том числе когда обоа null), false иначе
	 */
	public static boolean safeEquals(Object o1, Object o2) {
		return (o1 == o2) || (o1 != null && o1.equals(o2));
	}


	/**
	 * Получить логин Сотрудника.
	 * @param employee
	 * @param nodeService
	 * @param orgstructureService
	 * @param logger
	 * @return логин Сотрудника или null если Сотрудник не связан с системным пользователем.
	 */
	public static String getEmployeeLogin(NodeRef employee
			, NodeService nodeService
			, OrgstructureBean orgstructureService
			, Logger logger
			)
	{
		if (employee == null)
			return null;
		final NodeRef person = orgstructureService.getPersonForEmployee(employee);
		if (person == null) {
			logger.warn( String.format( "Employee '%s' is not linked to system user", employee.toString() ));
			return null;
		}
		return getPersonLogin(person, nodeService);
	}


	/**
	 * Получить логин Пользователя.
	 * @param person
	 * @param nodeService
	 * @return имя входа или null, если person = null или property "userName" пустое
	 */
	public static String getPersonLogin(final NodeRef person, NodeService nodeService) {
		if (person == null)
			return null;
		final Object loginName = nodeService.getProperty( person, PolicyUtils.PROP_USER_NAME);
		return (loginName != null && loginName.toString().length() > 0 ) ? loginName.toString() : null;
	}

	/**
	 * Получить название Должностной Позиции - здесь выдаётся название словарной 
	 * позиции с Должностью, т.к. родное name для staff-позиции будет содержать
	 * нечитабельный GUID.
	 * @param staffDP
	 * @param nodeService
	 * @param orgstructureService
	 * @return название DP
	 */
	public static String getDpInfoName(NodeRef staffDP, NodeService nodeService
			, OrgstructureBean orgstructureService)
	{
		if (staffDP == null)
			return null;
		// final String dpName = ""+ nodeService.getProperty( staffDP, PROP_DP_INFO); // тут будет GUID, который плохо читается
		final NodeRef posRef = orgstructureService.getPositionByStaff(staffDP); // получить словарное значение Должности по штатной позиции 
		final String dpName = ""+ nodeService.getProperty( posRef, PROP_DP_INFO);

		return dpName;
	}

	/**
	 * Получить id/название Бизнес Роли
	 * @param brole
	 * @param nodeService
	 * @return значение атрибута "business-role-identifier" или NULL, если он не задан или пуст
	 */
	public static String getBRoleIdCode(NodeRef brole, NodeService nodeService) {
		// return (brole == null) ? null : brole.getId();
		if (brole == null) return null;
		final Object roleCode = nodeService.getProperty( brole, OrgstructureBean.PROP_BUSINESS_ROLE_IDENTIFIER);
		if (roleCode == null) return null;
		final String result = roleCode.toString().trim();
		return (result.length() > 0) ? roleCode.toString() : null;
	}


	public static String getDPIdCode(NodeRef deputyPoint, NodeService nodeService) {
		return (deputyPoint == null) ? null : deputyPoint.getId();
	}

	/**
	 * Получить id/название Департамента (OU)
	 * @param orgUnit
	 * @param nodeService
	 * @return
	 */
	public static String getOrgUnitIdCode(NodeRef orgUnit, NodeService nodeService) {
		if (orgUnit == null)
			return null;
		// final String unitIdCode = ""+ nodeService.getProperty( orgUnit, PROP_ORGUNIT_CODE); // PROP_ORGELEMENT_NAME
		final String unitIdCode = orgUnit.getId();
		return unitIdCode;
	}
	
	/**
	 * Получить id/название Рабочей группы (WG)
	 * @param workGroup
	 * @param nodeService
	 * @return
	 */
	public static String getWorkGroupIdCode(NodeRef workGroup, NodeService nodeService) {
		return getOrgUnitIdCode(workGroup, nodeService);
	}

	/**
	 * Получить дексриптор Сотрудника, который используется в методах IOrgStructureNotifiers.
	 * В принципе, ничего особенно не делается, кроме как формируется "гуманоид-
	 * ориентированное" описание с входным именем Сотрудника.
	 * @param employee id узла Сотрудника
	 * @param nodeService служба работы с узлами
	 * @return
	 */
	public static Types.SGPrivateMeOfUser makeEmploeePos(NodeRef employee, NodeService nodeService
			, OrgstructureBean orgstructureService
			, Logger logger
	) {
		final String loginName = getEmployeeLogin(employee, nodeService, orgstructureService, logger);
		return (Types.SGPrivateMeOfUser) Types.SGKind.getSGMeOfUser( employee.getId(), loginName);
	}

	/**
	 * Получить дексриптор Должностной Позиции, который используется в методах IOrgStructureNotifiers.
	 * Здесь формируется "гуманоид-ориентированное" описание с названием DP и
	 * присоединяется Сотрудник, связанный с Должностной Позицией.
	 * @param staffDP id узла Должностной Позиции тип "lecm-orgstr:staffPosition"
	 * @param nodeService служба работы с узлами
	 * @return
	 */
	public static Types.SGDeputyPosition makeDeputyPos(
			NodeRef staffDP
			, NodeService nodeService
			, OrgstructureBean orgstructureService
			, Logger logger
	) {
		final NodeRef employee = orgstructureService.getEmployeeByPosition(staffDP);
		return makeDeputyPos(staffDP, employee, nodeService, orgstructureService, logger);
	}

	/**
	 * Получить дексриптор Должностной Позиции, который используется в методах IOrgStructureNotifiers.
	 * В принципе, ничего особенно не делается, кроме как формируется "гуманоид-
	 * ориентированное" описание с названием DP.
	 * @param staffDP id узла Должностной Позиции тип "lecm-orgstr:staffPosition"
	 * @param employee Сотрудник, соотвествующий Должности
	 * @param nodeService служба работы с узлами
	 * @param orgstructureService
	 * @param logger
	 * @return
	 */
	public static Types.SGDeputyPosition makeDeputyPos(
			NodeRef staffDP
			, NodeRef employee
			, NodeService nodeService
			, OrgstructureBean orgstructureService
			, Logger logger
	) {
		final String dpIdCode = getDPIdCode(staffDP, nodeService);
		final String dpName = getDpInfoName(staffDP, nodeService, orgstructureService);
		final String userLogin = getEmployeeLogin( employee, nodeService, orgstructureService, logger);
		return Types.SGKind.getSGDeputyPosition( dpIdCode, dpName, userLogin, (employee != null) ? employee.getId() : null);
	}


	/**
	 * Получить дексриптор Организации, который используется в методах IOrgStructureNotifiers.
	 * В принципе, ничего особенно не делается, кроме как формируется "гуманоид-
	 * ориентированное" описание.
	 * @param orgUnit id узла типа "lecm-orgstr:organization-element"
	 * @param nodeService служба работы с узлами
	 * @return
	 */
	public static Types.SGOrgUnit makeOrgUnitPos(NodeRef orgUnit, NodeService nodeService) {
		final String orgIdCode = getOrgUnitIdCode(orgUnit, nodeService);
		final String orgDetails= ""+ nodeService.getProperty( orgUnit, PROP_ORGELEMENT_NAME);
		final Types.SGOrgUnit sgOU = (Types.SGOrgUnit) Types.SGKind.SG_OU.getSGPos( orgIdCode, orgDetails);
		return sgOU;
	}

	/**
     * Получить дексриптор Организации, который используется в методах IOrgStructureNotifiers.
     * В принципе, ничего особенно не делается, кроме как формируется "гуманоид-
     * ориентированное" описание.
     * @param orgUnit id узла типа "lecm-orgstr:organization-element"
     * @param nodeService служба работы с узлами
     * @return
     */
    public static Types.SGOrgUnitPrivate makeOrgUnitPrivatePos(NodeRef orgUnit, NodeService nodeService) {
        final String orgIdCode = getOrgUnitIdCode(orgUnit, nodeService);
        final String orgDetails= ""+ nodeService.getProperty( orgUnit, PROP_ORGELEMENT_NAME) + " PRIVATE";
        final Types.SGOrgUnitPrivate sgOU = (Types.SGOrgUnitPrivate) Types.SGKind.SG_PRIVATE_OU.getSGPos( orgIdCode, orgDetails);
        return sgOU;
    }

    /**
	 * Получить дексриптор руководящей позиции для Организации. Используется в методах IOrgStructureNotifiers.
	 * В принципе, ничего особенно не делается, кроме как формируется "гуманоид-
	 * ориентированное" описание.
	 * @param orgUnit id узла типа "lecm-orgstr:organization-element"
	 * @param nodeService служба работы с узлами
	 * @return
	 */
	public static Types.SGSuperVisor makeOrgUnitSVPos(NodeRef orgUnit, NodeService nodeService) {
		final Types.SGOrgUnit sgOU = PolicyUtils.makeOrgUnitPos(orgUnit, nodeService);
		final Types.SGSuperVisor sgSV = (Types.SGSuperVisor) Types.SGKind.SG_SV.getSGPos( orgUnit.getId(), sgOU.getDisplayInfo());
		return sgSV;
	}

	/**
	 * Получить дексриптор Рабочей группы, который используется в методах IOrgStructureNotifiers.
	 * В принципе, ничего особенно не делается, кроме как формируется "гуманоид-
	 * ориентированное" описание.
	 * @param workGroup id узла типа "lecm-orgstr:organization-element"
	 * @param nodeService служба работы с узлами
	 * @return
	 */
	public static Types.SGWorkGroup makeWorkGroupPos(NodeRef workGroup, NodeService nodeService) {
		final String wgIdCode = getWorkGroupIdCode(workGroup, nodeService);
		final String wgDetails= ""+ nodeService.getProperty( workGroup, PROP_ORGELEMENT_NAME);
		final Types.SGWorkGroup sgWG = (Types.SGWorkGroup) Types.SGKind.SG_WG.getSGPos( wgIdCode, wgDetails);
		return sgWG;
	}
	
	/**
	 * Получить дексриптор Бизнес Роли, для использования в методах IOrgStructureNotifiers.
	 * В принципе, ничего особенно не делается, кроме как формируется "гуманоид-
	 * ориентированное" описание.
	 * @param brole id узла типа "lecm-orgstr:business-role"
	 * @param nodeService служба работы с узлами
	 * @return
	 */
	public static Types.SGBusinessRole makeBRPos(NodeRef brole, NodeService nodeService) {
		final String brolIdCode = getBRoleIdCode( brole, nodeService);
		if (brolIdCode == null) return null;
		final String orgDetails= ""+ nodeService.getProperty( brole, OrgstructureBean.PROP_BUSINESS_ROLE_IDENTIFIER);
		// brole.name / brole.id
		// final Object roleCode = String.format( "'%s'/'%s'", brolIdCode, nodeService.getProperty( brole, OrgstructureBean.PROP_BUSINESS_ROLE_IDENTIFIER));
		return (Types.SGBusinessRole) Types.SGKind.SG_BR.getSGPos( brolIdCode, orgDetails);
	}


	/**
	 * Получить дексриптор личной группы Бизнес Роли.
	 * В принципе, ничего особенно не делается, кроме как формируется "гуманоид-
	 * ориентированное" описание.
	 * @param brole id узла типа "lecm-orgstr:business-role"
	 * @param nodeService служба работы с узлами
	 * @return
	 */
	public static Types.SGPrivateBusinessRole makeBRPrivatePos(
			  NodeRef brole
			, NodeRef employee
			, NodeService nodeService
			, OrgstructureBean orgstructureService
			, Logger logger
			)
	{
		final String broleIdCode = getBRoleIdCode( brole, nodeService);
		if (broleIdCode == null || employee == null) return null;
		// final String broleDetails= ""+ nodeService.getProperty( brole, OrgstructureBean.PROP_BUSINESS_ROLE_IDENTIFIER);
		final String userLogin = getEmployeeLogin( employee, nodeService, orgstructureService, logger);

		// return Types.SGKind.getSGDeputyPosition( dpIdCode, dpName, userLogin, (employee != null) ? employee.getId() : null);
		return (Types.SGPrivateBusinessRole) Types.SGKind.getSGMyRolePos( employee.getId(), userLogin, broleIdCode);
	}

	/**
	 * Получить Родительское Подразделение для данного
	 * @param nodeOU
	 * @return
	 */
	static public NodeRef getParentOU(NodeRef nodeOU, NodeService nodeService) {
		final ChildAssociationRef parentRef = nodeService.getPrimaryParent(nodeOU);
		return (parentRef != null) ? parentRef.getParentRef() : null;
	}

	/**
	 * Получить список всех родительских Подразделений, (!) включая исходное
	 * @param nodeOU
	 * @param addSelf true, если в выходной список надо ввести nodeOU
	 * @return
	 */
	static public Set<NodeRef> getAllParentOU(NodeRef nodeOU, NodeService nodeService, boolean addSelf) {
		final Set<NodeRef> result = new HashSet<NodeRef>();
		for( NodeRef curOU = nodeOU; curOU != null; curOU = getParentOU(curOU, nodeService)) {
			result.add(curOU);
		}
		if (!addSelf) // убрать исходный узел, если не требуется ...
			result.remove(nodeOU);
		return result;
	}


	/**
	 * Построить карту Департаментов со списком Бизнес-Ролей, выданных
	 * непосредственно для них.
	 * @param roles список Бизнес Ролей, для которых надо строить Департаменты,
	 * для которых они выделены
	 * @return карту ключ=Департамент(OU), Значение=Список БР, непосредственно предоставленных для OU
	 */
	static public Map<NodeRef, Set<NodeRef>> scanBRolesForOrgUnits(Collection<NodeRef> roles
			, NodeService nodeService) {
		final Map<NodeRef, Set<NodeRef>> result = new HashMap<NodeRef, Set<NodeRef>>();
		if (roles != null) {
			for (NodeRef role: roles) {
				final List<AssociationRef> links2units = nodeService.getSourceAssocs(role, OrgstructureBean.ASSOC_BUSINESS_ROLE_ORGANIZATION_ELEMENT);
				for (AssociationRef link : links2units) {
					final NodeRef orgUnit = link.getTargetRef();
					final Set<NodeRef> units;
					if (result.containsKey(orgUnit))
						units = result.get(orgUnit);
					else
						result.put( orgUnit, units = new HashSet<NodeRef>());
					units.add(role);
				}
			}
		}
		return result;
	}

}
