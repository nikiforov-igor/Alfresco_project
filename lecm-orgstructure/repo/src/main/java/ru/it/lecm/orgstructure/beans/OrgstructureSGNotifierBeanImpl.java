package ru.it.lecm.orgstructure.beans;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.security.AuthenticationService;
import org.alfresco.util.PropertyCheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.orgstructure.policies.PolicyUtils;
import ru.it.lecm.security.Types;
import ru.it.lecm.security.Types.SGPosition;
import ru.it.lecm.security.events.IOrgStructureNotifiers;

public class OrgstructureSGNotifierBeanImpl
		extends BaseBean
		implements OrgstructureSGNotifierBean
{
	final static protected Logger logger = LoggerFactory.getLogger(OrgstructureSGNotifierBeanImpl.class);

	private AuthenticationService authService;
	private OrgstructureBean orgstructureService;
	private IOrgStructureNotifiers sgNotifier;


	public void init() {
		logger.debug("initializing");
		PropertyCheck.mandatory(this, "authService", authService);
		PropertyCheck.mandatory(this, "nodeService", this.nodeService);
		PropertyCheck.mandatory(this, "sgNotifier", this.sgNotifier);
		PropertyCheck.mandatory(this, "orgstructureService", this.orgstructureService);
		logger.info("initialized");
	}


	public AuthenticationService getAuthService() {
		return authService;
	}

	public void setAuthService(AuthenticationService authService) {
		this.authService = authService;
	}

	public OrgstructureBean getOrgstructureService() {
		return orgstructureService;
	}

	public void setOrgstructureService(OrgstructureBean orgstructureService) {
		this.orgstructureService = orgstructureService;
	}

	public IOrgStructureNotifiers getSgNotifier() {
		return sgNotifier;
	}

	public void setSgNotifier(IOrgStructureNotifiers sgNotifier) {
		this.sgNotifier = sgNotifier;
	}


	private String getEmployeeLogin(NodeRef employee) {
		return PolicyUtils.getEmployeeLogin(employee, nodeService, orgstructureService, logger);
	}


	/**
	 * Получить всех сотрудников указанного Подразделения
	 * @param nodeOU
	 * @return
	 */
	protected Set<NodeRef> getEmployeesByOU(NodeRef nodeOU) {
		final List<NodeRef> employees = this.orgstructureService.getOrganizationElementEmployees(nodeOU);
		return (employees == null) ? null : new HashSet<NodeRef>( employees);
	}


	/**
	 * СОбрать всех Сотрудников данного подразделения и вложенных в него
	 * @param nodeOU
	 * @return
	 */
	protected Set<NodeRef> getAllEmployeesByOUAndChild(NodeRef nodeOU) {
		// DONE: возможно надо пройтись вниз по орг-штатке и собрать всех вложенных Сотрудников
		final List<NodeRef> units = this.orgstructureService.getSubUnits(nodeOU, true, true); // список всех вложенных Активных Подразделений
		units.add(nodeOU); // текущее подразделение тоже надо

		final Set<NodeRef> result = new HashSet<NodeRef>();
		for(NodeRef ou: units) {
			final Set<NodeRef> employees = getEmployeesByOU(ou);
			if (employees == null)
				result.addAll(employees);
		}
		return result;
	}


	@Override
	public void notifyNodeCreated(SGPosition pos) {
		if (pos == null)
			return;
		if (pos.getId() == null) {
			logger.warn( String.format( "SG Group '"+ pos.getAlfrescoSuffix()+"' is skipping due to id is NULL"));
			return;
		}

		sgNotifier.orgNodeCreated( pos);
	}

	@Override
	public void notifyNodeDeactivated(SGPosition pos) {
		sgNotifier.orgNodeDeactivated( pos);
	}

	/**
	 * Оповещение о создании/изменении должностной позиции
	 * @param nodeDP  узел должностной позиции
	 */
	@Override
	public void notifyChangeDP(NodeRef nodeDP) {
		final NodeRef orgUnit = orgstructureService.getUnitByStaff(nodeDP);
		final List<NodeRef> staffList = orgstructureService.getUnitStaffLists(orgUnit);
		final boolean isBoss = staffList.size() >= 1 && staffList.get(0).equals(nodeDP);
		notifyChangeDP(nodeDP, isBoss, orgUnit);
	}

	/**
	 * Оповещение о создании/изменении должностной позиции
	 * @param nodeDP  узел должностной позиции
	 * @param isBoss true, если она является руководящей
	 * @param orgUnit
	 */
	@Override
	public void notifyChangeDP(NodeRef nodeDP, boolean isBoss, NodeRef orgUnit) {
		final NodeRef employee = orgstructureService.getEmployeeByPosition(nodeDP);
		final String loginName = getEmployeeLogin(employee);
		final String emplId = (employee != null) ? employee.getId() : null;

		// ensure SG_DP // Types.SGKind.getSGDeputyPosition(nodeDP.getId(), dpName, loginName, emplId);
		final Types.SGDeputyPosition sgDP = PolicyUtils.makeDeputyPos(nodeDP, nodeService, orgstructureService, logger);
		sgNotifier.orgNodeCreated( sgDP);

		// прописать Сотрудника в свою Должность ...
		// SG_Me -> SG_DP
		if (employee != null) {
			final Types.SGPosition sgMe = Types.SGKind.SG_ME.getSGPos( emplId, loginName);
			sgNotifier.sgInclude( sgMe, sgDP);
		}

		// Прописать SG_DP в своё Подразделение (SG_OU) ...
		// include SG_DP -> SG_OU
		final Types.SGPosition sgOU = PolicyUtils.makeOrgUnitPos(orgUnit, nodeService);
		sgNotifier.sgInclude( sgDP, sgOU);

		// Если позиция руководящая прописать её в SG_SV своего Подразделения ...
		final Types.SGPosition sgSV = Types.SGKind.SG_SV.getSGPos( orgUnit.getId(), sgOU.getDisplayInfo());
		if (isBoss) { // прописать руководящую SG_DP -> SG_SV(OU)
			sgNotifier.sgInclude( sgDP, sgSV);
		} else { // снять отметку руководящей ...
			sgNotifier.sgExclude( sgDP, sgSV);
		}

	}

	/**
	 * Назначение БР для Сотрудника.
	 * @param employee
	 * @param brole
	 */
	@Override
	public void notifyEmploeeSetBR(NodeRef employee, NodeRef brole) {
		final Types.SGPosition emplPos = PolicyUtils.makeEmploeePos(employee, nodeService, orgstructureService, logger);
		final String broleCode = PolicyUtils.getBRoleIdCode(brole, nodeService);
		if (broleCode != null)
			this.sgNotifier.orgBRAssigned( broleCode, emplPos);
		else
			logger.warn( String.format("Business role '%s' has no mnemonic -> not assigned to employee '%s'", brole, employee));
	}

	/**
	 * Убрать БР у Сотрудника
	 * @param employee
	 * @param brole
	 */
	@Override
	public void notifyEmploeeRemoveBR(NodeRef employee, NodeRef brole) {
		final Types.SGPosition emplPos = PolicyUtils.makeEmploeePos(employee, nodeService, orgstructureService, logger);
		// использование специального значения более "человечно" чем brole.getId(), и переносимо между разными базами Альфреско
		final String broleCode = PolicyUtils.getBRoleIdCode(brole, nodeService);
		if (broleCode != null)
			this.sgNotifier.orgBRRemoved( broleCode, emplPos);
		else
			logger.warn( String.format("Business role '%s' has no mnemonic -> not removed from employee '%s'", brole, employee));
	}

	/**
	 * Назначение DP для Сотрудника.
	 * @param employee узел типа "lecm-orgstr:employee-link"
	 * @param dpid узел типа "lecm-orgstr:position"
	 */
	@Override
	public void notifyEmploeeSetDP(NodeRef employee, NodeRef dpid) {
		final Types.SGPrivateMeOfUser emplPos = PolicyUtils.makeEmploeePos(employee, nodeService, orgstructureService, logger);
		final Types.SGDeputyPosition dpPos = PolicyUtils.makeDeputyPos(dpid, employee, nodeService, orgstructureService, logger);
		this.sgNotifier.sgInclude( emplPos, dpPos);
	}

	/**
	 * Убрать БР у Сотрудника
	 * @param employee
	 * @param brole
	 */
	@Override
	public void notifyEmploeeRemoveDP(NodeRef employee, NodeRef dpid) {
		final Types.SGPrivateMeOfUser emplPos = PolicyUtils.makeEmploeePos(employee, nodeService, orgstructureService, logger);
		final Types.SGDeputyPosition dpPos = PolicyUtils.makeDeputyPos(dpid, employee, nodeService, orgstructureService, logger);
		this.sgNotifier.sgExclude( emplPos, dpPos);
	}

	/**
	 * Нотификация о связывании Сотрудника и пользователя Альфреско.
	 * @param employee
	 * @param isActive true, если флажок "активен" включен
	 */
	@Override
	public void notifyEmploeeTie(NodeRef employee, Boolean isActive) {
		// ASSOC_EMPLOYEE_PERSON: "lecm-orgstr:employee-person-assoc"
		final String loginName = getEmployeeLogin(employee);
		sgNotifier.orgNodeCreated( Types.SGKind.SG_ME.getSGPos( employee.getId(), loginName));
		sgNotifier.orgEmployeeTie( employee.getId(), loginName, (isActive != null) && isActive.booleanValue());
	}

	/**
	 * Нотификация о связывании Сотрудника и пользователя Альфреско.
	 * @param employee
	 */
	@Override
	public void notifyEmploeeTie(NodeRef employee) {
		final Boolean isActive = (Boolean) nodeService.getProperty(employee, IS_ACTIVE);
		notifyEmploeeTie(employee, isActive);
	}

	/**
	 * Нотификация об отвязывании Сотрудника и пользователя Альфреско.
	 * @param employee
	 * @param userLogin
	 */
	@Override
	public void notifyEmploeeDown(NodeRef employee) {
		final String loginName = getEmployeeLogin(employee);
		sgNotifier.orgEmployeeTie( employee.getId(), loginName, false);
		sgNotifier.orgNodeDeactivated( Types.SGKind.SG_ME.getSGPos( employee.getId(), loginName));
	}

	@Override
	public void notifyChangedOU(NodeRef nodeDP) {
		final ChildAssociationRef ref = nodeService.getPrimaryParent(nodeDP);
		notifyChangedOU( nodeDP, (ref != null) ? ref.getParentRef() : null );
	}

	/**
	 * Оповещение об изменении/создании орг-единицы
	 * @param nodeOU изменённое Подразделение
	 * @param parentOU родительский узел (доп здесь будет проверка, что это
	 * Подразделение, а не просто папка)
	 */
	@Override
	public void notifyChangedOU(NodeRef nodeOU, NodeRef parent) {
		final Types.SGPosition posNodeOU = PolicyUtils.makeOrgUnitPos(nodeOU, nodeService);
		sgNotifier.orgNodeCreated( posNodeOU);

		/*
		 * есть родительский узел по орг-штатной структуре включаем:
		 *   1) включаем свой SG_OU -> parent(SG_OU)
		 *   2) родительский Руководящий узел в свой
		 */
		if (super.isProperType(parent, OrgstructureBean.TYPE_ORGANIZATION_UNIT)) {
			// (1) SG_OU -> SG_OU(parent)
			final Types.SGPosition posParentOU = PolicyUtils.makeOrgUnitPos(parent, nodeService);
			sgNotifier.sgInclude( posNodeOU, posParentOU);

			// (2) Группа Руководства из родительского подразделения в текущее: SG_SV(parent) -> SG_SV
			sgNotifier.sgInclude(
					Types.SGKind.SG_SV.getSGPos( parent.getId(), posParentOU.getDisplayInfo())
					, Types.SGKind.SG_SV.getSGPos( nodeOU.getId(), posNodeOU.getDisplayInfo())
			);
		}
	}

	@Override
	public void notifyDeleteOU(NodeRef nodeOU, NodeRef parent) {
		final Types.SGPosition posNodeOU = PolicyUtils.makeOrgUnitPos(nodeOU, nodeService);

		/*
		 * есть родительский узел по орг-штатной структуре включаем:
		 *   1) включаем свой SG_OU -> parent(SG_OU)
		 *   2) родительский Руководящий узел в свой
		 */
		if (orgstructureService.isUnit(parent)) { // super.isProperType(parent, OrgstructureBean.TYPE_ORGANIZATION_UNIT)
			notifyPrivateBRolesOfOrgUnits(nodeOU, false, true); // снятие БР с Сотрудников

			// (1) SG_OU -> SG_OU(parent)
			final Types.SGPosition posParentOU = PolicyUtils.makeOrgUnitPos(parent, nodeService);
			sgNotifier.sgExclude( posNodeOU, posParentOU);

			// (2) Группа Руководства из родительского подразделения в текущее: SG_SV(parent) -> SG_SV
			sgNotifier.sgExclude(
					Types.SGKind.SG_SV.getSGPos( parent.getId(), posParentOU.getDisplayInfo())
					, Types.SGKind.SG_SV.getSGPos( nodeOU.getId(), posNodeOU.getDisplayInfo())
			);
		}

		sgNotifier.orgNodeDeactivated( posNodeOU);
	}

	/**
	 * Выполнить подключение БР выданных для подразделения OU и всех его вложенных,
	 * для Сотрудников подразделения OU (и вложенных)
	 * @param nodeOU исходное подразделение
	 * @param include true, чтобы выдать БРоли, false чтобы отозвать
	 * @param recursivelyUseParentsBR true, чтобы дополнительно выполнить привязку
	 * БР назначенных для Родительских Подразделений
	 *
	 */
	@Override
	public void notifyPrivateBRolesOfOrgUnits( NodeRef nodeOU, boolean include
			, boolean recursivelyUseParentsBR)
	{
		// все Сотрудники текущего OU и всех его вложенных
		final Set<NodeRef> employees = getAllEmployeesByOUAndChild(nodeOU);

		if (employees == null || employees.isEmpty()) {
			logger.warn( String.format( "No employees found inside orgUnit '%s' -> linking of business roles skipped", nodeOU));
			return;
		}

		// DONE: получить список БР и в нём уже искать нужные огранизации
		final List<NodeRef> allRoles = orgstructureService.getBusinesRoles(true);
		if (allRoles == null || allRoles.isEmpty()) {
			logger.warn( "No any business roles found -> linking of business roles skipped");
			return;
		}

		// подразделения, с которых собирать БР
		final Set<NodeRef> curOUNodes = (recursivelyUseParentsBR)
				? PolicyUtils.getAllParentOU(nodeOU, nodeService, true) // со всех родительских, true = включая исходное Подразделение
				: new HashSet<NodeRef>( Arrays.asList(nodeOU)); // только с текущего узла

		// получить карту распределения БР по каждому OU: 
		// ключ=Департамент(OU), Значение=Список БР, непосредственно предоставленных для OU
		final Map<NodeRef, Set<NodeRef>> rolesByOU = PolicyUtils.scanBRolesForOrgUnits( allRoles, nodeService);

		// выдаём БР для этих Сотрудников, проходя по выбранным Подразделениям ...
		for (NodeRef ou: curOUNodes) {
			// Бизнес Роли выданные на конкретное подразделение ...
			final Set<NodeRef> ouRoles = rolesByOU.get(ou);
			if (ouRoles == null || ouRoles.isEmpty()) continue;

			// Активируем БР для Сотрудников
			for (NodeRef role: ouRoles) {
				final Types.SGPosition ouPos = PolicyUtils.makeOrgUnitPos(role, nodeService);
				for (NodeRef employee : employees) {
					final String userLogin = getEmployeeLogin( employee);
					// Активация/деактивация личной Личной группы бизнес роли SG_BRME относительно SG_OU ...
					final Types.SGPrivateBusinessRole brmePos = Types.SGKind.getSGMyRolePos(employee.getId(), userLogin, ouPos.getDisplayInfo());
					if (include) {
						this.sgNotifier.sgInclude( brmePos, ouPos); // BRME -> OU
						this.sgNotifier.sgInclude( Types.SGKind.SG_ME.getSGPos( employee.getId(), userLogin), brmePos); // ME -> BRME
					} else
						this.sgNotifier.sgExclude( brmePos, ouPos);
				}
			}
		}

	}

	/**
	 * Оповещение о предоставлении/отборе Бизнес Роли для  Сотрудника/Должности/Департамента
	 * @param nodeAssocRef связь объекта (target) и БР (source)
	 * @param created true, если БР предоставляется и false, если отбирается
	 */
	@Override
	public void notifyBRAssociationChanged(AssociationRef nodeAssocRef
			, boolean created)
	{
		// получаем основной объект - Бизнес Роль
		final NodeRef brole = nodeAssocRef.getSourceRef();
		final Types.SGPosition brolePos = PolicyUtils.makeBRPos(brole, nodeService);

		/*
		 * Теперь разделение по типу связи:
		 *    1) назначение БР для Сотрудника;
		 *    2) назначение БР для Должностной Позиции;
		 *    3) назначение БР для Подразделения;
		 QName ASSOC_BUSINESS_ROLE_EMPLOYEE = QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, "business-role-employee-assoc");
		 QName ASSOC_BUSINESS_ROLE_ORGANIZATION_ELEMENT = QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, "business-role-organization-element-assoc");
		 QName ASSOC_BUSINESS_ROLE_ORGANIZATION_ELEMENT_MEMBER = QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, "business-role-organization-element-member-assoc");
		 */
		final NodeRef destObj = nodeAssocRef.getTargetRef();
		Types.SGPosition assocPos;
		boolean isOrgUnitAssoc = false;
		if (orgstructureService.isEmployee(destObj)) { // присвоение БР для Сотрудника
			assocPos = PolicyUtils.makeEmploeePos(destObj, nodeService, orgstructureService, logger);
		} else if (orgstructureService.isStaffList(destObj)) { // присвоение БР для Должностной Позиции
			assocPos = PolicyUtils.makeDeputyPos(destObj, nodeService, orgstructureService, logger);

			// (!) включаем Сотрудника в личную группу бизнес роли
			
		} else if (orgstructureService.isUnit(destObj)) { // присвоение БР для Подразделения
			assocPos = PolicyUtils.makeOrgUnitPos(destObj, nodeService);
			isOrgUnitAssoc = true;
		} else {
			logger.warn( String.format( "Unsupported BusinessRole association\n\t id='%s' of type \"%s\"\n\t from node '%s' -> child node '%s'"
					, nodeAssocRef.getId()
					, nodeAssocRef.getTypeQName()
					, nodeAssocRef.getSourceRef()
					, nodeAssocRef.getTargetRef()
				));
			return;
		}

		if (created)
			sgNotifier.orgBRAssigned(brolePos.getId(), assocPos);
		else
			sgNotifier.orgBRRemoved(brolePos.getId(), assocPos);

		/*
		 * в случае связывания с Подразделением - надо активировать БР для всех
		 * Сотрудников, вложенных в Подразделение (и его вложенные)
		 * (!) не рекурсивно - от Родительских Подразделений БР не требуются
		*/
		if (isOrgUnitAssoc) {
			final boolean recurseBRFromParents = false;
			notifyPrivateBRolesOfOrgUnits(brole, created, recurseBRFromParents);
		}
	}


	@Override
	public void notifyBRDelegationChanged(NodeRef brole,
			NodeRef sourceEmployee, NodeRef destEmployee, boolean created) 
	{
		// получаем личные группы пользователей для бизнес-ролей
		final Types.SGPrivateBusinessRole sgSrcPrivBR = PolicyUtils.makeBRPrivatePos(brole, sourceEmployee, nodeService, orgstructureService, logger);
		// final Types.SGPosition destPrivBR = PolicyUtils.makeBRPrivatePos(brole, destEmployee, nodeService, orgstructureService, logger);
		final Types.SGPrivateMeOfUser sgDestMe = PolicyUtils.makeEmploeePos(destEmployee, nodeService, orgstructureService, logger);

		if (created) // включить личную группу того кому делегируем в группу бизнес роли того кто делегирует ...
			sgNotifier.sgInclude( sgDestMe, sgSrcPrivBR);
		else
			sgNotifier.sgExclude( sgDestMe, sgSrcPrivBR);
	}


	@Override
	public void notifyBossDelegationChanged(NodeRef sourceEmployee,
			NodeRef destEmployee, boolean created)
	{
		// личная группа делегата ...
		final Types.SGPrivateMeOfUser sgDestMe = PolicyUtils.makeEmploeePos(destEmployee, nodeService, orgstructureService, logger);

		// получить все подразделения, в которых делегирующий является руководителем ...
		final List<NodeRef> orgs = orgstructureService.getEmployeeUnits(sourceEmployee, true);

		if (orgs == null || orgs.isEmpty()) {
			logger.warn( String.format("Employee {%s} is not boss at any organization unit -> nothing to delegate for employee {%s}", sourceEmployee, destEmployee));
			return;
		}

		for(NodeRef orgUnit: orgs) {
			// руководящая позиция подразделения ...
			final Types.SGSuperVisor sgSV = PolicyUtils.makeOrgUnitSVPos(orgUnit, nodeService);

			if (created) // включить личную группу в руководящую подразделения ...
				sgNotifier.sgInclude( sgDestMe, sgSV);
			else
				sgNotifier.sgExclude( sgDestMe, sgSV);
		}
	}

}
