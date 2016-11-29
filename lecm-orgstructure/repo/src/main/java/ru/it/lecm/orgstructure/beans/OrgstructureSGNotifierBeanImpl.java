package ru.it.lecm.orgstructure.beans;

import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.security.AuthenticationService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.PropertyCheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEvent;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.orgstructure.policies.PolicyUtils;
import ru.it.lecm.security.LecmPermissionService;
import ru.it.lecm.security.Types;
import ru.it.lecm.security.Types.SGKind;
import ru.it.lecm.security.Types.SGPosition;
import ru.it.lecm.security.Types.SGPrivateMeOfUser;
import ru.it.lecm.security.Types.SGSuperVisor;
import ru.it.lecm.security.events.IOrgStructureNotifiers;

import java.util.*;

public class OrgstructureSGNotifierBeanImpl
		extends BaseBean
		implements OrgstructureSGNotifierBean
{
	final static protected Logger logger = LoggerFactory.getLogger(OrgstructureSGNotifierBeanImpl.class);

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

	// public static QName TYPE_EMPLOYEE = QName.createQName( OrgstructureBean.ORGSTRUCTURE_NAMESPACE_URI, "employee");

	/**
	 * Привязать для всех активных Сотрудников Login/userId к sgME группам ...
	 */
	@Override
	public void autoTieAllEmployeers() {
		final long start = System.currentTimeMillis();

		final Set<QName> typEmpl = new HashSet<QName>();
		typEmpl.add( OrgstructureBean.TYPE_EMPLOYEE);

		final List<ChildAssociationRef> employeeRefs = nodeService.getChildAssocs(orgstructureService.getEmployeesDirectory(), typEmpl);
		if (employeeRefs == null || employeeRefs.isEmpty()) {
			logger.warn("No Employeers found");
		} else {
			logger.warn("Employeers found : "+ employeeRefs.size());
			for(ChildAssociationRef item: employeeRefs) {
				final NodeRef employee = item.getChildRef();
				this.notifyEmploeeTie(employee);
			}
		}

		logger.info("scan time, ms: "+ (System.currentTimeMillis() - start) );
	}


	public AuthenticationService getAuthService() {
		return authService;
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
	 * Собрать всех Сотрудников данного подразделения и вложенных в него
	 * @param nodeOU
	 * @return
	 */
	protected Set<NodeRef> getAllEmployeesByOUAndChild(NodeRef nodeOU) {
		final Set<NodeRef> result = new HashSet<NodeRef>();
		// DONE: возможно надо пройтись вниз по орг-штатке и собрать всех вложенных Сотрудников
		List<NodeRef> units = this.orgstructureService.getSubUnits(nodeOU, true, true); // список всех вложенных Активных Подразделений
		if (units == null)
			units = new ArrayList<NodeRef>();
		units.add(nodeOU); // текущее подразделение тоже надо

		for(NodeRef ou: units) {
			final Set<NodeRef> employees = getEmployeesByOU(ou);
			if (employees != null)
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
		if (nodeDP == null) return;
		final NodeRef orgUnit = orgstructureService.getUnitByStaff(nodeDP);
		if (orgUnit == null) return;

		final boolean isBoss = Boolean.TRUE.equals(nodeService.getProperty(nodeDP, OrgstructureBean.PROP_STAFF_LIST_IS_BOSS));
		notifyChangeDP(nodeDP, isBoss, orgUnit);
	}

	/**
	 * Оповещение о создании/изменении должностной позиции
	 * @param staffDP  узел должностной позиции
	 * @param isBoss true, если она является руководящей
	 * @param nodeOrgUnit
	 */
	@Override
	public void notifyChangeDP(NodeRef staffDP, boolean isBoss, NodeRef nodeOrgUnit) {
		final NodeRef employee = orgstructureService.getEmployeeByPosition(staffDP);
		notifyChangeDPAndEmloyee(employee, staffDP, isBoss, nodeOrgUnit);
	}

	private void notifyChangeDPAndEmloyee(NodeRef employee, NodeRef staffDP, boolean isBoss, NodeRef nodeOrgUnit)
	{
		if (logger.isDebugEnabled()) {
			try {
				logger.debug( String.format( "notifyChangeDP/Employee: isBoss=%s \n\t employee {%s} of type {%s}\n\t DP {%s} of type {%s}\n\t OU {%s} of type {%s}",
					isBoss
					, employee, nodeService.getType(employee)
					, staffDP, nodeService.getType(staffDP)
					, nodeOrgUnit, nodeService.getType(nodeOrgUnit)));
			} catch(Throwable t) {
				logger.error( String.format( "notifyChangeDP: isBoss=%s \n\t DP {%s}\n\t OU {%s}",
						isBoss, staffDP, nodeOrgUnit), t);
				// eat the exception
			}
		}
		final String loginName = getEmployeeLogin(employee);
		final String emplId = (employee != null) ? employee.getId() : null;

		// ensure SG_DP // Types.SGKind.getSGDeputyPosition(nodeDP.getId(), dpName, loginName, emplId);
		final Types.SGDeputyPosition sgDP = PolicyUtils.makeDeputyPos(staffDP, nodeService, orgstructureService, logger);
		sgNotifier.orgNodeCreated( sgDP);

		// прописать Сотрудника в свою Должность ...
		// SG_Me -> SG_DP
		Types.SGPrivateMeOfUser sgMe = null;
		if (employee != null) {
			// safely-свяжем пользователя с его личной группой
			if (loginName != null)
				sgNotifier.orgEmployeeTie(emplId, loginName, true);

			// sgMe = PolicyUtils.makeEmploeePos(employee, nodeService, orgstructureService, logger);
			sgMe = Types.SGKind.getSGMeOfUser( emplId, loginName);
			sgNotifier.sgInclude( sgMe, sgDP);
		}

		// Прописать SG_DP в своё Подразделение (SG_OU) ...
		// include SG_DP -> SG_OU
		final Types.SGPosition sgOU = PolicyUtils.makeOrgUnitPos(nodeOrgUnit, nodeService);
		sgNotifier.sgInclude( sgDP, sgOU);

        final Types.SGPosition sgPrivateOU = PolicyUtils.makeOrgUnitPrivatePos(nodeOrgUnit, nodeService);
        sgNotifier.sgInclude( sgDP, sgPrivateOU);

		// Если позиция руководящая прописать её Сотрудника (!) в SG_SV своего Подразделения ...
		final Types.SGPosition sgSV = Types.SGKind.SG_SV.getSGPos( nodeOrgUnit.getId(), sgOU.getDisplayInfo());
		if (sgMe != null) {
			/*
			 *
			if (isBoss) {
				if (sgMe != null) // убрать SV подразедления из личной группы
					sgNotifier.sgExclude( sgSV, sgMe);
				// прописать руководящую SG_DP -> SG_SV(OU)
				sgNotifier.sgInclude( sgDP, sgSV);
			} else { // снять отметку руководящей ...
				sgNotifier.sgExclude( sgDP, sgSV);
				if (sgMe != null) // прописать SV подразедления в личную группу
					sgNotifier.sgInclude( sgSV, sgMe);
			}
			 */

			// включение группы SV к любому работнику подразделения в любом случае надо cделать
			sgNotifier.sgInclude( sgSV, sgMe);
			if (isBoss) { // прописать руководящую: USER -> SG_SV(OU)
				sgNotifier.sgInclude( sgMe, sgSV);
			} else {// снять руководящую: SG_SV(OU) rem USER
				sgNotifier.sgExclude( sgMe, sgSV);
			}
		}
	}

	/**
	 * Назначение БР для Сотрудника.
	 * @param employee
	 * @param brole
	 */
	@Override
	public void notifyEmploeeSetBR(NodeRef employee, NodeRef brole) {
		if (logger.isDebugEnabled()) {
			logger.debug( String.format( "notifyEmploeeSetBR:\n\t Employee {%s} of type {%s}\n\t Business Role {%s} of type {%s}",
					employee, nodeService.getType(employee), brole, nodeService.getType(brole)));
		}

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
		if (logger.isDebugEnabled()) {
			logger.debug( String.format( "notifyEmploeeRemoveBR:\n\t Employee {%s} of type {%s}\n\t Business Role {%s} of type {%s}",
					employee, nodeService.getType(employee), brole, nodeService.getType(brole)));
		}

		final Types.SGPosition emplPos = PolicyUtils.makeEmploeePos(employee, nodeService, orgstructureService, logger);
		// использование специального значения более "человечно" чем brole.getId(), и переносимо между разными базами Альфреско
		final String broleCode = PolicyUtils.getWorkGroupIdCode(brole, nodeService);
		if (broleCode != null)
			this.sgNotifier.orgBRRemoved( broleCode, emplPos);
		else
			logger.warn( String.format("Business role '%s' has no mnemonic -> not removed from employee '%s'", brole, employee));
	}

	/**
	 * Назначение DP для Сотрудника.
	 * @param employee узел типа "lecm-orgstr:employee-link"
	 * @param nodeDP узел типа "lecm-orgstr:staff-list
	 */
	@Override
	public void notifyEmploeeSetDP(NodeRef employee, NodeRef nodeDP) {
		if (logger.isDebugEnabled()) {
			logger.debug( String.format( "notifyEmploeeSetDP:\n\t Employee {%s} of type {%s}\n\t DP {%s} of type {%s}",
					employee, nodeService.getType(employee), nodeDP, nodeService.getType(nodeDP)));
		}

		final NodeRef orgUnit = orgstructureService.getUnitByStaff(nodeDP);
		if (orgUnit == null) {
			return;
		}
		final boolean isBoss = Boolean.TRUE.equals(nodeService.getProperty(nodeDP, OrgstructureBean.PROP_STAFF_LIST_IS_BOSS));

		notifyChangeDPAndEmloyee(employee, nodeDP, isBoss, orgUnit);
	}

	@Override
	public void notifyEmployeeSetWG(NodeRef employee, NodeRef nodeWR, NodeRef group) {

		if (logger.isDebugEnabled()) {
			logger.debug( String.format( "notifyEmploeeSetDP:\n\t Employee {%s} of type {%s}\n\t DP {%s} of type {%s}",
					employee, nodeService.getType(employee), nodeWR, nodeService.getType(nodeWR)));
		}

		final String loginName = getEmployeeLogin(employee);
		final String emplId = (employee != null) ? employee.getId() : null;

		final Types.SGPosition posNodeWG = PolicyUtils.makeWorkGroupPos(group, nodeService);

		Types.SGPrivateMeOfUser sgMe = null;
		if (employee != null) {
			// safely-свяжем пользователя с его личной группой
			if (loginName != null)
				sgNotifier.orgEmployeeTie(emplId, loginName, true);

			// sgMe = PolicyUtils.makeEmploeePos(employee, nodeService, orgstructureService, logger);
			sgMe = Types.SGKind.getSGMeOfUser(emplId, loginName);
			sgNotifier.sgInclude(sgMe, posNodeWG);
		}


	}

	public void notifyEmployeeRemoveWG(NodeRef employee, NodeRef nodeWR, NodeRef group) {

		if (logger.isDebugEnabled()) {
			logger.debug( String.format( "notifyEmploeeRemoveBR:\n\t Employee {%s} of type {%s}\n\t DP {%s} of type {%s}",
					employee, nodeService.getType(employee), nodeWR, nodeService.getType(nodeWR)));
		}

		final Types.SGPrivateMeOfUser sgMe = (employee != null)
				? PolicyUtils.makeEmploeePos(employee, nodeService, orgstructureService, logger)
				: null;
		final Types.SGWorkGroup sgWG = PolicyUtils.makeWorkGroupPos(group, nodeService);

		if (sgMe != null)
		{
			this.sgNotifier.sgExclude( sgMe, sgWG);
		}

	}

	@Override
	public void notifyEmploeeRemoveDP(NodeRef employee, NodeRef nodeDP) {
		if (logger.isDebugEnabled()) {
			logger.debug( String.format( "notifyEmploeeRemoveBR:\n\t Employee {%s} of type {%s}\n\t DP {%s} of type {%s}",
					employee, nodeService.getType(employee), nodeDP, nodeService.getType(nodeDP)));
		}

		// выход из DP-группы ...
		final Types.SGPrivateMeOfUser sgMe = (employee != null)
				? PolicyUtils.makeEmploeePos(employee, nodeService, orgstructureService, logger)
				: null;
		final Types.SGDeputyPosition sgDP = PolicyUtils.makeDeputyPos(nodeDP, employee, nodeService, orgstructureService, logger);
		if (sgMe != null)
		{
			this.sgNotifier.sgExclude( sgMe, sgDP);

			// выписывание из SV групп ...
			// 1) убрать SVOU из личной
			final NodeRef orgUnit = orgstructureService.getUnitByStaff(nodeDP);
			if (orgUnit == null) {
				logger.error( "Cannot find OU for staff position "+ nodeDP.toString());
				return;
			}
			final Types.SGPosition sgSV = Types.SGKind.SG_SV.getSGPos( orgUnit.getId());
			sgNotifier.sgExclude( sgSV, sgMe); // SVOU убрать из личной

			// 2) если босс - убрать USERid из SVOU (для других - фактичеки ничего не будет делать)
			sgNotifier.sgExclude( sgMe, sgSV); // для босса - убрать себя из SV, для остальных - фактически ничего не будет делать

		}
	}

	/**
	 * Нотификация о связывании Сотрудника и пользователя Альфреско.
	 * @param employee
	 * @param isActive true, если флажок "активен" включен
	 */
	@Override
	public void notifyEmploeeTie(NodeRef employee, Boolean isActive) {
		if (logger.isDebugEnabled()) {
			logger.debug( String.format( "notifyEmploeeTie: setActive=%s\n\t Employee {%s} of type {%s}",
					isActive, employee, nodeService.getType(employee)));
		}

		// ASSOC_EMPLOYEE_PERSON: "lecm-orgstr:employee-person-assoc"
		final String loginName = getEmployeeLogin(employee);

		sgNotifier.orgNodeCreated( Types.SGKind.getSGMeOfUser( employee.getId(), loginName));
		// safely-свяжем пользователя с его личной группой
		sgNotifier.orgEmployeeTie( employee.getId(), loginName, (isActive != null) && isActive.booleanValue());

		// (!) надо boss-должности перепривязать, т.к. они напрямую связаны в USER
		tie2OUSVgroups( employee, loginName, Boolean.TRUE.equals(isActive));
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
	public void notifyEmploeeDown(NodeRef employee, NodeRef person) {
		final String loginName = PolicyUtils.getPersonLogin(person, nodeService);// getEmployeeLogin(employee);

		if (logger.isDebugEnabled()) {
			logger.debug( String.format( "notifyEmploeeDown: login '%s'\n\t Employee {%s} of type {%s}",
					loginName, employee, nodeService.getType(employee)));
		}

		if (loginName == null)
			throw new RuntimeException( String.format( "Empty login: cannot untie employee {%s} from SG-ME", employee.getId() ));

		sgNotifier.orgEmployeeTie( employee.getId(), loginName, false);
		sgNotifier.orgNodeDeactivated( Types.SGKind.getSGMeOfUser( employee.getId(), loginName));

		tie2OUSVgroups(employee, loginName, false); // отвязаться от всех boss-SV
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
		if (logger.isDebugEnabled()) {
			logger.debug( String.format( "notifyChangedOU:\n\t OU {%s} of type {%s}\n\t Parent {%s} of type {%s}",
					nodeOU, nodeService.getType(nodeOU), parent, nodeService.getType(parent)));
		}

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

        final Types.SGPosition posNodePrivateOU = PolicyUtils.makeOrgUnitPrivatePos(nodeOU, nodeService);
        sgNotifier.orgNodeCreated( posNodePrivateOU);

		/*
		 * есть родительский узел по орг-штатной структуре включаем:
		 *   1) включаем свой SG_OU -> parent(SG_OU)
		 *   2) родительский Руководящий узел в свой
		 */
        if (super.isProperType(parent, OrgstructureBean.TYPE_ORGANIZATION_UNIT)) {
            final Types.SGPosition posParentOU = PolicyUtils.makeOrgUnitPos(parent, nodeService);
            // (2) Группа Руководства из родительского подразделения в текущее: SG_SV(parent) -> SG_SV
            sgNotifier.sgInclude(
                    Types.SGKind.SG_SV.getSGPos(parent.getId(), posParentOU.getDisplayInfo())
                    , SGKind.SG_PRIVATE_OU.getSGPos(nodeOU.getId(), posNodePrivateOU.getDisplayInfo())
            );
        }
	}

	@Override
	public void notifyDeleteOU(NodeRef nodeOU, NodeRef parent) {

		if (logger.isDebugEnabled()) {
			logger.debug( String.format( "notifyDeleteOU:\n\t OU {%s} of type {%s}\n\t Parent {%s} of type {%s}",
					nodeOU, nodeService.getType(nodeOU), parent, nodeService.getType(parent)));
		}

		final Types.SGPosition posNodeOU = PolicyUtils.makeOrgUnitPos(nodeOU, nodeService);
		final Types.SGPosition posNodePrivateOU = PolicyUtils.makeOrgUnitPrivatePos(nodeOU, nodeService);

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
			sgNotifier.sgExclude( posNodePrivateOU, posParentOU);

			// (2) Группа Руководства из родительского подразделения в текущее: SG_SV(parent) -> SG_SV
			sgNotifier.sgExclude(
					Types.SGKind.SG_SV.getSGPos( parent.getId(), posParentOU.getDisplayInfo())
					, Types.SGKind.SG_SV.getSGPos( nodeOU.getId(), posNodeOU.getDisplayInfo())
			);
		}

		sgNotifier.orgNodeDeactivated( posNodeOU);
	}

	/**
	 * Оповещение об изменении/создании рабочей группы
	 * @param nodeWG изменённая Рабочая группа
	 */
	@Override
	public void notifyChangedWG(NodeRef nodeWG) {
		if (logger.isDebugEnabled()) {
			logger.debug( String.format( "notifyChangedWG:\n\t WG {%s} of type {%s}",
					nodeWG, nodeService.getType(nodeWG)));
		}

		final Types.SGPosition posNodeWG = PolicyUtils.makeWorkGroupPos(nodeWG, nodeService);
		sgNotifier.orgNodeCreated( posNodeWG);
	}

	/**
	 * Оповещение об удалении рабочей группы
	 * @param nodeWG изменённая Рабочая группа
	 */
	@Override
	public void notifyDeleteWG(NodeRef nodeWG) {
		if (logger.isDebugEnabled()) {
			logger.debug( String.format( "notifyDeleteWG:\n\t OU {%s} of type {%s}",
					nodeWG, nodeService.getType(nodeWG)));
		}

		final Types.SGPosition posNodeWG = PolicyUtils.makeWorkGroupPos(nodeWG, nodeService);
		sgNotifier.orgNodeDeactivated(posNodeWG);
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
		if (logger.isDebugEnabled()) {
			logger.debug( String.format( "notifyGiveBR4OU: direction %s\n\t recurse %s\n\t OU {%s}\n\t of type {%s}"
					, (include ? "INCLUDE" : "EXCLUDE"), recursivelyUseParentsBR
					, nodeOU, nodeService.getType(nodeOU)) );
		}

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
				final Types.SGPosition ouPrivatePos = PolicyUtils.makeOrgUnitPrivatePos(role, nodeService);
				for (NodeRef employee : employees) {
					final String userLogin = getEmployeeLogin( employee);

					// safely-свяжем пользователя с его личной группой
					// (случай include=false, по-сути, не влияет на привязку User к SG_ME)
					if (userLogin != null)
						sgNotifier.orgEmployeeTie(employee.getId(), userLogin, true);

					// Активация/деактивация личной Личной группы бизнес роли SG_BRME относительно SG_OU ...
					final Types.SGPrivateBusinessRole brmePos = Types.SGKind.getSGMyRolePos(employee.getId(), userLogin, ouPos.getDisplayInfo());
					if (include) {
						this.sgNotifier.sgInclude( brmePos, ouPos); // BRME -> OU
						this.sgNotifier.sgInclude( brmePos, ouPrivatePos); // BRME -> OU
						this.sgNotifier.sgInclude( Types.SGKind.getSGMeOfUser( employee.getId(), userLogin), brmePos); // ME -> BRME
					} else {
						this.sgNotifier.sgExclude( brmePos, ouPos);
						this.sgNotifier.sgExclude( brmePos, ouPrivatePos);
                    }
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
	public void notifyBRAssociationChanged(AssociationRef nodeAssocRef, boolean created)
	{
		final NodeRef brole = nodeAssocRef.getSourceRef();
		final NodeRef destObj = nodeAssocRef.getTargetRef();

		if (logger.isDebugEnabled()) {
			logger.debug( String.format( "notifyBRAssociationChanged: created %s\n\t Business Role{%s} of type {%s}\n\t Dest {%s} of type {%s}"
					, created
					, brole, nodeService.getType(brole)
					, destObj, nodeService.getType(destObj)
			));
		}

		final Types.SGPosition brolePos = PolicyUtils.makeBRPos(brole, nodeService);
		if (brolePos == null) {
			logger.error( String.format( "\n(!) NO BUSINESS ROLE CODE detected, args are:\n\t created %s\n\t Business Role{%s} of type {%s}\n\t Dest {%s} of type {%s}"
					, created
					, brole, nodeService.getType(brole)
					, destObj, nodeService.getType(destObj)
			));
			return;
		}

		/*
		 * Теперь разделение по типу связи:
		 *    1) назначение БР для Сотрудника;
		 *    2) назначение БР для Должностной Позиции;
		 *    3) назначение БР для Подразделения;
		 QName ASSOC_BUSINESS_ROLE_EMPLOYEE = QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, "business-role-employee-assoc");
		 QName ASSOC_BUSINESS_ROLE_ORGANIZATION_ELEMENT = QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, "business-role-organization-element-assoc");
		 QName ASSOC_BUSINESS_ROLE_ORGANIZATION_ELEMENT_MEMBER = QName.createQName(ORGSTRUCTURE_NAMESPACE_URI, "business-role-organization-element-member-assoc");
		 */
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
			notifyPrivateBRolesOfOrgUnits(destObj, created, recurseBRFromParents);
		}
	}


	@Override
	public void notifyBRDelegationChanged(NodeRef brole,
			NodeRef sourceEmployee, NodeRef destEmployee, boolean created)
	{
		if (logger.isDebugEnabled()) {
			logger.debug( String.format( "notifyBRDelegationChanged: created %s\n\t Business Role{%s} of type {%s}\n\t Source Employee {%s} of type {%s}\n\t Dest Employee {%s} of type {%s}"
					, created
					, brole, nodeService.getType(brole)
					, sourceEmployee, nodeService.getType(sourceEmployee)
					, destEmployee, nodeService.getType(destEmployee)
			));
		}

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
	public void notifySpecDelegationChanged(LecmPermissionService.LecmPermissionGroup permissionGroup,
											NodeRef sourceEmployee, NodeRef destEmployee, boolean created) {
		final SGPosition posUserSpec = Types.SGKind.getSGSpecialUserRole(sourceEmployee.getId(), permissionGroup, null, null);

		// получаем личные группы пользователей для бизнес-ролей
		final Types.SGPrivateMeOfUser sgSourceMe = PolicyUtils.makeEmploeePos(destEmployee, nodeService, orgstructureService, logger);
		if (created) { // включить личную группу того кому делегируем в группу бизнес роли того кто делегирует ...
			sgNotifier.sgInclude(sgSourceMe, posUserSpec);
		} else {
			sgNotifier.sgExclude(sgSourceMe, posUserSpec);
		}
	}


	@Override
	public void notifyBossDelegationChanged(NodeRef sourceEmployee,
			NodeRef destEmployee, boolean created)
	{
		if (logger.isDebugEnabled()) {
			logger.debug( String.format( "notifyBossDelegationChanged: created %s\n\t Source Employee {%s} of type {%s}\n\t Dest Employee {%s} of type {%s}"
					, created
					, sourceEmployee, nodeService.getType(sourceEmployee)
					, destEmployee, nodeService.getType(destEmployee)
			));
		}

		// личная группа делегата ...
		final Types.SGPrivateMeOfUser sgDestMe = PolicyUtils.makeEmploeePos(destEmployee, nodeService, orgstructureService, logger);

		// получить все подразделения, в которых делегирующий является руководителем ...
		final List<NodeRef> orgsBoss = orgstructureService.getEmployeeUnits(sourceEmployee, true, false);

		if (orgsBoss == null || orgsBoss.isEmpty()) {
			logger.warn( String.format("Employee {%s} is not boss at any organization unit -> nothing to delegate for employee {%s}", sourceEmployee, destEmployee));
			return;
		}

		// DONE: посмотреть можно ли выделить в отдельный private-метод совместно с подобным кодом из notifyChangeDPAndEmloyee
		// Простой вариант, если не надо учитывать вложенность:
		for(NodeRef orgUnit: orgsBoss) {
			// руководящая позиция подразделения ...
			final Types.SGSuperVisor sgSV = PolicyUtils.makeOrgUnitSVPos(orgUnit, nodeService);

			if (created) {
				// sgNotifier.sgExclude( sgSV, sgDestMe); // отвязать SV-группу своего подраздедения (SVOU) от себя ("anti-recurse step")
				sgNotifier.sgInclude( sgDestMe, sgSV); // привязать себя к SVOU
			} else {
				sgNotifier.sgExclude( sgDestMe, sgSV); // ("anti-recurse step") отвязать себя от SVOU
				// sgNotifier.sgInclude( sgSV, sgDestMe); // привязать SVOU к себе
			}
		}


//		// получить все подразделения, в которых принимающий делегат является работником (но не боссом)...
//		// DONE: ты надо перечислить только подразделения, вложенные в те, которыми руководит босс (делегирующий)
//		final List<NodeRef> orgsDest = findOnlySimpleUnits(destEmployee);
//
//		if (created) {
//			// выполнить отсоединение принимающего делегата ото всех его обычных (работных) SVOU
//			for(NodeRef orgUnitDest: orgsDest) {
//				// руководящая позиция подразделения ...
//				final Types.SGSuperVisor sgSVDest = PolicyUtils.makeOrgUnitSVPos(orgUnitDest, nodeService);
//				sgNotifier.sgExclude( sgSVDest, sgDestMe); // отвязать SV-группу своего подраздедения (SVOU) от себя ("anti-recurse step")
//			}
//
//			// Присоединить принимающего к целевым ...
//			for(NodeRef orgUnit: orgsBoss) {
//				// руководящая позиция подразделения ...
//				final Types.SGSuperVisor sgSV = PolicyUtils.makeOrgUnitSVPos(orgUnit, nodeService);
//				sgNotifier.sgInclude( sgDestMe, sgSV); // привязать себя к SVOU в качестве Руководителя
//			}
//
//		} else {
//			// "снять со всех постов" )
//			// выполнить отсоединение делегата ото всх предоставленных Руководящих позиций ...
//			for(NodeRef orgUnit: orgsBoss) {
//				// руководящая позиция подразделения ...
//				final Types.SGSuperVisor sgSV = PolicyUtils.makeOrgUnitSVPos(orgUnit, nodeService);
//				sgNotifier.sgExclude( sgDestMe, sgSV);
//			}
//
//			// включить делегата во все его прежние работные SVOU (чтобы им могли руководить) ...
//			for(NodeRef orgUnitDest: orgsDest) {
//				// руководящая позиция подразделения ...
//				final Types.SGSuperVisor sgSVDest = PolicyUtils.makeOrgUnitSVPos(orgUnitDest, nodeService);
//				sgNotifier.sgInclude( sgSVDest, sgDestMe);
//			}
//		}

	}


	/**
	 * Получить все подразделения, в которых принимающий делегат является
	 * работником, но не боссом.
	 * @param employee
	 * @return
	 */
	List<NodeRef> findOnlySimpleUnits(NodeRef employee) {
		final List<NodeRef>
			total = orgstructureService.getEmployeeUnits(employee, false, false)		// полный список
			, asboss = orgstructureService.getEmployeeUnits(employee, true, false);	// там, где он босс
		total.removeAll(asboss);
		return total; // (total - boss)
	}


	/**
	 * Выполнить привязку Сотрудника к группам SG_SV (в тех OU где он босс)
	 * @param employee
	 * @param loginName
	 * @param tie true для привязки, false наоборот
	 */
	private void tie2OUSVgroups(NodeRef employee, String loginName, boolean tie) {
		if (loginName == null) {
			// нет возможности выполнить операцию ...
			return;
		}

		final SGPrivateMeOfUser posME = SGKind.getSGMeOfUser(employee.getId(), loginName);

		// получить подразделения, в которых Сотрудник является боссом ...
		final List<NodeRef> asbossOU = orgstructureService.getEmployeeUnits(employee, true, false);
		if (asbossOU == null || asbossOU.isEmpty()) { // нет "боссовых" должностей
			return;
		}

		// для них - включиться в SG_SV
		for (NodeRef unit: asbossOU) {
			final Types.SGPosition sgOU = PolicyUtils.makeOrgUnitPos(unit, nodeService);
			final SGSuperVisor sgSV = (SGSuperVisor) Types.SGKind.SG_SV.getSGPos( unit.getId(), sgOU.getDisplayInfo());
			this.sgNotifier.sgInclude(posME, sgSV); // ME >> SV
			logger.debug( String.format( "%s SG_SV group of unit {%s} for/from employee <%s>/ login <%s>"
					, (tie ? "Tied" : "Untied"), unit, employee, loginName));
		}

		logger.info( String.format( "%s %d SG_SV groups for/from employee <%s>/ login <%s>"
				, (tie ? "Tied" : "Untied"), asbossOU.size(), employee, loginName));
	}

	// в данном бине не используется каталог в /app:company_home/cm:Business platform/cm:LECM/
	@Override
	public NodeRef getServiceRootFolder() {
		return null;
	}
}
