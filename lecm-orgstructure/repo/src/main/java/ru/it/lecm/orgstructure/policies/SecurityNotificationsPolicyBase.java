package ru.it.lecm.orgstructure.policies;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.util.PropertyCheck;
import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;
import ru.it.lecm.orgstructure.beans.OrgstructureSGNotifierBean;
import ru.it.lecm.secretary.SecretarySecurityService;
import ru.it.lecm.secretary.SecretaryService;
import ru.it.lecm.security.Types.SGPosition;

/**
 * Базовый класс для политик, исопльзующих помимо прочего SG-нотификацию.
 */
public abstract class SecurityNotificationsPolicyBase extends BaseBean {
	final protected Logger logger = LoggerFactory.getLogger (this.getClass());

	private final static String[] CHEKING_PROPERTY_NAMES = {"policyComponent", "orgstructureService", "orgSGNotifier", "secretaryService", "secretarySecurityService"};

	// final static protected String CHKNAME_SG_NOTIFIER = "sgNotifier";
	// final static protected String CHKNAME_AUTH_SERVICE = "authService";
	final static protected String CHKNAME_ORGSG_NOTIFIER = "orgSGNotifier";

	protected PolicyComponent policyComponent;
	protected OrgstructureBean orgstructureService;
	protected OrgstructureSGNotifierBean orgSGNotifier;
	private SecretaryService secretaryService;
	private SecretarySecurityService secretarySecurityService;

	public PolicyComponent getPolicyComponent() {
		return policyComponent;
	}

	public void setPolicyComponent(PolicyComponent policyComponent) {
		this.policyComponent = policyComponent;
	}

	public OrgstructureBean getOrgstructureService() {
		return orgstructureService;
	}

	public void setOrgstructureService(OrgstructureBean orgstructureService) {
		this.orgstructureService = orgstructureService;
	}

	public OrgstructureSGNotifierBean getOrgSGNotifier() {
		return this.orgSGNotifier;
	}

	public void setOrgSGNotifier(OrgstructureSGNotifierBean value) {
		this.orgSGNotifier = value;
	}

	public SecretaryService getSecretaryService() {
		return secretaryService;
	}

		public void setSecretaryService(SecretaryService secretaryService) {
		this.secretaryService = secretaryService;
	}

	public SecretarySecurityService getSecretarySecurityService() {
		return secretarySecurityService;
	}

		public void setSecretarySecurityService(SecretarySecurityService secretarySecurityService) {
		this.secretarySecurityService = secretarySecurityService;
	}

	/**
	 * Инициализация и проверка заполнения указанных свойств
	 * @param checkingPropertyNames
	 */
	protected void init( String ... checkingPropertyNames) {
		for (final String name: checkingPropertyNames) {
			try {
				PropertyCheck.mandatory(this, name, PropertyUtils.getProperty(this, name));
			} catch(AlfrescoRuntimeException ex) {
				throw ex;
			} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException ex) {
				final String info = String.format( "Problem accessing property '%s'", name);
				logger.error( info, ex);
				throw new AlfrescoRuntimeException(info, ex);
			}
		}
	}

	/**
	 * Инициализация и проверка заполнения свойств "policyComponent", "orgstructureService", "nodeService", "orgSGNotifier".
	 * (!) authService здесь не проверяется.
	 */
	public void init() {
		init(CHEKING_PROPERTY_NAMES);
	}

	/**
	 * Безопасное выполнение кода с журналированием ошибок.
	 * (!) Все местные исключения отсаются тут и наружу не просачиваются.
	 * @param todo выполняемый код
	 * @param runInfo пояснения о выполняемом коде для журналирования ошибок
	 */
	protected void safeExec(Runnable todo, String runInfo) {
		try {
			todo.run();
		} catch (Throwable t) {
			logger.error( String.format( "exception in %s %s", runInfo, t.getMessage()), t);
		}
	}

	protected void notifyNodeCreated(SGPosition pos) {
		this.orgSGNotifier.notifyNodeCreated(pos);
	}

	protected void notifyNodeDeactivated(SGPosition pos) {
		this.orgSGNotifier.notifyNodeDeactivated(pos);
	}

	/**
	 * Оповещение о создании/изменении должностной позиции
	 * @param nodeDP  узел должностной позиции
	 */
	protected void notifyChangeDP(NodeRef nodeDP) {
		this.orgSGNotifier.notifyChangeDP(nodeDP);
	}

	/**
	 * Оповещение о создании/изменении должностной позиции
	 * @param nodeDP  узел должностной позиции
	 * @param isBoss true, если она является руководящей
	 * @param orgUnit
	 */
	protected void notifyChangeDP(NodeRef nodeDP, boolean isBoss, NodeRef orgUnit) {
		this.orgSGNotifier.notifyChangeDP(nodeDP, isBoss, orgUnit);
	}

	/**
	 * Назначение БР для Сотрудника.
	 * @param employee
	 * @param brole
	 */
	protected void notifyEmploeeSetBR(NodeRef employee, NodeRef brole) {
		this.orgSGNotifier.notifyEmploeeSetBR(employee, brole);
	}

	/**
	 * Убрать БР у Сотрудника
	 * @param employee
	 * @param brole
	 */
	protected void notifyEmploeeRemoveBR(NodeRef employee, NodeRef brole) {
		this.orgSGNotifier.notifyEmploeeRemoveBR(employee, brole);
	}

	/**
	 * Назначение DP для Сотрудника.
	 * @param employee узел типа "lecm-orgstr:employee-link"
	 * @param dpid узел типа "lecm-orgstr:position"
	 */
	protected void notifyEmploeeSetDP(NodeRef employee, NodeRef dpid) {
		this.orgSGNotifier.notifyEmploeeSetDP(employee, dpid);
	}

	/**
	 * Убрать БР у Сотрудника
	 * @param employee
	 * @param brole
	 */
	protected void notifyEmploeeRemoveDP(NodeRef employee, NodeRef dpid) {
		this.orgSGNotifier.notifyEmploeeRemoveDP(employee, dpid);
	}

	/**
	 * Исключение сотрудника из роли рабочей группы
	 */
	protected void notifyEmployeeRemoveWG(NodeRef employee, NodeRef WR, NodeRef group) {
		this.orgSGNotifier.notifyEmployeeRemoveWG(employee, WR, group);
	}

	/**
	 * Нотификация о связывании Сотрудника и пользователя Альфреско.
	 * @param employee
	 * @param isActive true, если флажок "активен" включен
	 */
	protected void notifyEmploeeTie(NodeRef employee, Boolean isActive) {
		// ASSOC_EMPLOYEE_PERSON: "lecm-orgstr:employee-person-assoc"
		this.orgSGNotifier.notifyEmploeeTie(employee, isActive);
	}

	/**
	 * Нотификация о связывании Сотрудника и пользователя Альфреско.
	 * @param employee
	 */
	protected void notifyEmploeeTie(NodeRef employee) {
		this.orgSGNotifier.notifyEmploeeTie(employee);
	}

	/**
	 * Нотификация об отвязывании Сотрудника и пользователя Альфреско.
	 * @param employee Сотрудник
	 * @param person отвязываемая Персона
	 */
	protected void notifyEmploeeDown(NodeRef employee, NodeRef person) {
		this.orgSGNotifier.notifyEmploeeDown(employee, person);
	}

	protected void notifyChangedOU(NodeRef nodeDP) {
		this.orgSGNotifier.notifyChangedOU(nodeDP);
	}

	/**
	 * Оповещение об изменении/создании орг-единицы
	 * @param nodeOU изменённое Подразделение
	 * @param parentOU родительский узел (доп здесь будет проверка, что это
	 * Подразделение, а не просто папка)
	 */
	protected void notifyChangedOU(NodeRef nodeOU, NodeRef parent) {
		this.orgSGNotifier.notifyChangedOU(nodeOU, parent);
	}

	protected void notifyDeleteOU(NodeRef nodeOU, NodeRef parent) {
		this.orgSGNotifier.notifyDeleteOU(nodeOU, parent);
	}

	/**
	 * Оповещение об включении сотрудника в роль рабочей группы
	 */
	protected void notifyEmployeeSetWG(NodeRef employee, NodeRef nodeWR, NodeRef group) {
		this.orgSGNotifier.notifyEmployeeSetWG(employee, nodeWR, group);
	}

	/**
	 * Оповещение об изменении/создании рабочей группы
	 * @param nodeWG изменённая Рабочая группа
	 */
	protected void notifyChangedWG(NodeRef nodeWG) {
		this.orgSGNotifier.notifyChangedWG(nodeWG);
	}

	/**
	 * Оповещение об удалении рабочей группы
	 * @param nodeWG изменённая Рабочая группа
	 */
	protected void notifyDeleteWG(NodeRef nodeWG) {
		this.orgSGNotifier.notifyDeleteWG(nodeWG);
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
	protected void notifyPrivateBRolesOfOrgUnits( NodeRef nodeOU, boolean include
			, boolean recursivelyUseParentsBR) {
		this.orgSGNotifier.notifyPrivateBRolesOfOrgUnits(nodeOU, include, recursivelyUseParentsBR);
	}

	/**
	 * Оповещение о предоставлении/отборе Бизнес Роли для  Сотрудника/Должности/Департамента
	 * @param nodeAssocRef связь объекта (target) и БР (source)
	 * @param created true, если БР предоставляется и false, если отбирается
	 */
	protected void notifyBRAssociationChanged(AssociationRef nodeAssocRef, boolean created) {
		this.orgSGNotifier.notifyBRAssociationChanged(nodeAssocRef, created);
	}

	// в данном бине не используется каталог в /app:company_home/cm:Business platform/cm:LECM/
	@Override
	public NodeRef getServiceRootFolder() {
		return null;
	}

	/**
	 * оповещение об изменении позиции "руководителя"
	 * @param employee сотрудник являющийся руководителем
	 * @param unit подразделение в котором сотрудник занимает некую должность
	 * @param isBoss является ли она на текущий момент руководящей
	 */
	protected void notifyChiefChangeDP(final NodeRef employee, final NodeRef unit, final boolean isBoss) {
		if (secretaryService.isChief(employee)) {
			secretarySecurityService.makeChiefBossOrEmployee(employee, unit, isBoss);
			//проверить что employee больше не босс
			if (!isBoss && !orgstructureService.isBoss(employee)) {
				notifyChiefDown(employee);
			}
		}
	}

	/**
	 * оповещение об изменнии позиции секретаря
	 * @param employee сотрудник являющийся секретарем
	 * @param isBoss является ли она на текущий момент руководящей
	 */
	protected void notifySecretaryChangeDP(final NodeRef employee, final boolean isBoss) {
		if (secretaryService.isSecretary(employee)) {
			secretarySecurityService.makeSecretaryBossOrEmployee(employee, isBoss);
			if (isBoss) {
				notifySecretaryDown(employee);
			}
		}
	}

	protected void notifyChiefDown(final NodeRef employee) {
		boolean isChief = secretaryService.isChief(employee);
		if (isChief) {
			//удалить всех его секретарей
			secretaryService.removeSecretaries(employee);
			//удалить его группу SG_SECRETARY
			secretarySecurityService.purgeSGSecretary(employee);
		}
	}

	protected void notifySecretaryDown(final NodeRef employee) {
		boolean isSecretary = secretaryService.isChief(employee);
		if (isSecretary) {
			//получить всех "руководителей" секретаря
			List<NodeRef> allChiefs = secretaryService.getPrimaryChiefs(employee);
			//удалить этого секретаря от каждого руководителя
			for (NodeRef chief : allChiefs) {
				secretaryService.removeSecretary(chief, employee);
			}
		}
	}
}
