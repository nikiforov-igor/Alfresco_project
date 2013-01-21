package ru.it.lecm.security.beans;

import java.util.Set;

import org.alfresco.service.cmr.security.AuthorityService;
import org.alfresco.service.cmr.security.AuthorityType;
import org.alfresco.util.PropertyCheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import ru.it.lecm.security.Types;
import ru.it.lecm.security.Types.SGKind;
import ru.it.lecm.security.events.IOrgStructureNotifiers;

public class LECMSecurityGroupsBean
		implements InitializingBean, IOrgStructureNotifiers
{

	final static protected Logger logger = LoggerFactory.getLogger (LECMSecurityGroupsBean.class);

	private AuthorityService authorityService;
	private boolean safeMode = false;

	public AuthorityService getAuthorityService() {
		return authorityService;
	}

	public void setAuthorityService(AuthorityService authorityService) {
		this.authorityService = authorityService;
	}

	/**
	 * В "безопасном режиме" при работе методов sgInclude, orgBRAssigned, orgEmployeeTie
	 * автоматически вызывается создание личных security-групп для участвующих объектов,
	 * если они ещё не были созданы к моменту вызова. Иначе, при отсутствии личных 
	 * групп будет подниматься исключение.
	 * По-умолчанию отключено.
	 * @return true = если "безопасный режим" включен
	 */
	public boolean isSafeMode() {
		return safeMode;
	}

	/**
	 * В "безопасном режиме" при работе методов sgInclude, orgBRAssigned, orgEmployeeTie
	 * автоматически вызывается создание личных security-групп для участвующих объектов,
	 * если они ещё не были созданы к моменту вызова. Иначе, при отсутствии личных 
	 * групп будет подниматься исключение.
	 * По-умолчанию отключено.
	 * @param safeModeFlag true = чтобы включить "безопасный режим"
	 */
	public void setSafeMode(boolean safeModeFlag) {
		this.safeMode = safeModeFlag;
		logger.debug("sageMode set to "+ this.safeMode);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		PropertyCheck.mandatory(this, "authorityService", authorityService);

		logger.info("initialized");
	}

	/**
	 * Проверить существование авторизации с названием shortName
	 * @param shortName
	 * @param parentName родитель, внутри которого искать, может быть Null
	 * @return
	 */
	private boolean hasAuth(String shortName, String parentName) {
		// есть user-авторизация? ...
		Set<String> found = null;
		try {
			found = this.authorityService.findAuthorities(AuthorityType.USER, parentName, true, shortName, null);
			if (found != null && found.size() > 0)
				return true;
		} catch (Throwable t) {
			logger.error( t.getMessage());
		}

		// есть групповая авторизация? ...
		try {
			found = this.authorityService.findAuthorities(AuthorityType.GROUP, parentName, true, shortName, null);
			if (found != null && found.size() > 0)
				return true;
		} catch (Throwable t) {
			logger.error( t.getMessage());
		}

		/*
		// есть не группа? ...
		found = this.authorityService.findAuthorities(AuthorityType.ROLE, parentName, true, shortName, null);
		if (found != null && found.size() > 0)
			return true;
		 */
		return false; // NOT FOUND
	}


	private boolean hasFullAuth(String fullChildName, String fullParentName) {
		final Set<String> found = this.authorityService.findAuthorities(null, fullParentName, true, fullChildName, null);
		return (found != null && found.size() > 0);
	}


	/**
	 * Проверить наличие и сохдать при отсутствии security-группу Альфреско
	 * с указанным коротким названием
	 * @param simpleName
	 * @return полное Альфресковское название для security-группы simpleName
	 */
	String ensureAlfrescoGroupName(String simpleName) {
		final String sgFullName;

		if (hasAuth(simpleName, null)) {
			sgFullName = this.authorityService.getName(AuthorityType.GROUP, simpleName);
			logger.info(String.format("Alfresco security-group '%s' already exists for object '%s'", sgFullName, simpleName));
		} else {
			sgFullName = this.authorityService.createAuthority(AuthorityType.GROUP, simpleName);
			logger.warn(String.format("Alfresco security-group '%s' created object '%s'", sgFullName, simpleName));
		}
		return sgFullName;
	}

	/**
	 * Удаление security-группы Альфреско 
	 * @param simpleName
	 */
	void removeAlfrescoGroupName(String simpleName) {
		final String sgFullName;

		if (!hasAuth(simpleName, null)) {
			sgFullName = this.authorityService.getName(AuthorityType.GROUP, simpleName);
			logger.warn(String.format("Alfresco security-group '%s' for object '%s' NOT exists or already removed", sgFullName, simpleName));
		} else {
			sgFullName = this.authorityService.getName( AuthorityType.GROUP, simpleName);
			this.authorityService.removeAuthority( null, sgFullName);
			logger.warn(String.format("Alfresco security-group '%s' for object '%s' removed", sgFullName, simpleName));
		}
	}

	void ensureParent(String sgItemFullName, String sgParentFullName) {
		if (hasFullAuth(sgItemFullName, sgParentFullName)) {
			logger.info(String.format("Security item '%s' is already inside security-group '%s'", sgItemFullName, sgParentFullName));
		} else {
			// добавление одного security-объекта в другой: sgItem -> sgParent
			this.authorityService.addAuthority(sgParentFullName, sgItemFullName);
			logger.warn(String.format("Security item '%s' put inside security-group '%s'", sgItemFullName, sgParentFullName));
		}
	}

	void removeParent(String sgItem, String sgParent) {
		if (!hasFullAuth(sgItem, sgParent)) {
			logger.info(String.format("Security item '%s' is already outside security-group '%s'", sgItem, sgParent));
		} else {
			this.authorityService.removeAuthority(sgParent, sgItem);
			logger.warn(String.format("Security item '%s' put out of security-group '%s'", sgItem, sgParent));
		}
	}

	@Override
	public String orgNodeCreated(Types.SGPosition obj) {
		// создание личной группы объекта
		final String sgName = ensureAlfrescoGroupName( obj.getAlfrescoSuffix());

		// дополнительные действия зависят от типа
		if (obj.getSgKind() ==  SGKind.SG_OU) 
			// создание SG_SV для Департамента (OU) ...
			ensureAlfrescoGroupName( SGKind.SG_SV.getAlfrescoSuffix(obj.getId()));

		return sgName;
	}

	@Override
	public void orgNodeDeactivated(Types.SGPosition obj) {
		// final String sgName = ensureAlfrescoGroupName(Types.getOrgUnitSuffix(nodeId, typeNode));
		final String sgName = ensureAlfrescoGroupName( obj.getAlfrescoSuffix());
		removeAlfrescoGroupName( sgName);
		// дополнительные действия зависят от типа
		if (obj.getSgKind() ==  SGKind.SG_OU) 
			removeAlfrescoGroupName( SGKind.SG_SV.getAlfrescoSuffix(obj.getId()));
	}

	@Override
	public void orgEmployeeTie(String employeeId, String alfrescoUserLogin) {
		final String emplSuffix = SGKind.SG_ME.getAlfrescoSuffix(employeeId);

		// safe-действия
		if (safeMode) {
			ensureAlfrescoGroupName( emplSuffix);
		}

		final String sg_user_name = this.authorityService.getName(AuthorityType.USER, alfrescoUserLogin);
		final String sg_me_group = this.authorityService.getName(AuthorityType.GROUP, emplSuffix);
		ensureParent( sg_user_name, sg_me_group);
	}

	@Override
	public void sgInclude( Types.SGPosition child, Types.SGPosition parent) 
	{
		// safe-действия
		if (safeMode) {
			orgNodeCreated( child);
			orgNodeCreated( parent);
		}

		// основные действия
		final String sgItem =  this.authorityService.getName(AuthorityType.GROUP, child.getAlfrescoSuffix());
		final String sgParent = this.authorityService.getName(AuthorityType.GROUP, parent.getAlfrescoSuffix());
		ensureParent(sgItem, sgParent);
	}

	@Override
	public void sgExclude( Types.SGPosition child, Types.SGPosition oldParent) {
		final String sgItem =  this.authorityService.getName(AuthorityType.GROUP, child.getAlfrescoSuffix());
		final String sgParent = this.authorityService.getName(AuthorityType.GROUP, oldParent.getAlfrescoSuffix());
		removeParent( sgItem, sgParent);
	}


	@Override
	public void orgBRAssigned(String broleId, Types.SGPosition obj) {
		final String broleSuffix = SGKind.SG_BR.getAlfrescoSuffix(broleId);
		// safe-действия
		if (safeMode) {
			ensureAlfrescoGroupName( broleSuffix);
			orgNodeCreated( obj);
		}

		final String sgBRole = this.authorityService.getName(AuthorityType.GROUP, broleSuffix);
		final String sgObj = this.authorityService.getName(AuthorityType.GROUP, obj.getAlfrescoSuffix());
		if (SGKind.SG_ME.equals(obj.getSgKind())) {
			/*
			 * Для сотрудника выполнятся «Активация относительно БР»:
			 *   - создается его личная SG-MyRole группа бизнес роли «A» (если ещё нет такой)
			 *   - в неё включается SG_Me 
			 *   - и эта группа включается в SG_BR бизнес роли.
			 */
			final String myBRole = SGKind.getSGBusinessRolePos(obj.getId(), broleId).getAlfrescoSuffix(); // личная группа для БР
			final String sgMyRole = ensureAlfrescoGroupName(myBRole);

			// SG_Me -> SG_MyRole
			ensureParent( sgObj, sgMyRole);

			// SG_MyRole -> SG_Role
			ensureParent(sgMyRole, sgBRole);
		}
		else if (SGKind.SG_DP.equals(obj.getSgKind())) {
			/*
			 * Полный список операций при назначении БР для Должности:
			 *    (а) прописать SG_DP в SG_BR, 
			 *    (б) выполнить «Активацию» для Сотрудника, занимающего DP, включив его личную SG_MyRole в SG_DP должностной позиции.
			 *
			 * (!) если здесь в obj не задан Сотрудник для должности, тогда второе 
			 * действие (б) надо вызвать явно в обработчике включения DP:
			 *     // obj это фактически SGKind.SG_DP.getSGPos( DP.id)
			 *     sgInclude( SGKind.getSGBusinessRolePos( DP.getUserId(), broleId), obj);
			 */
			final Types.SGDeputyPosition dp = (Types.SGDeputyPosition) obj;

			// SG_DP -> SG_Role
			ensureParent( sgObj, sgBRole);

			// SG_MyBRole -> SG_DP 
			if ( dp.getUserId() != null) {
				sgInclude( SGKind.getSGBusinessRolePos( dp.getUserId(), broleId), dp);
			}
		}
		else if (SGKind.SG_OU.equals(obj.getSgKind())) {
			/*
			 * Полный список операций при назначении БР для Должности:
			 *    (а) прописать SG_OU в SG_BR,
			 *    (б) выполнить «Активацию» относительно OU для всех Сотрудников – членов данного Подразделения (OU) и всех входящих в него Подразделений (рекурсивно вниз по иерархии)
			 * (!) второе действие (б) надо вызвать явно в обработчике включения OU
			 */
			// SG_OU -> SG_Role
			ensureParent( sgObj, sgBRole);
		}
	}

	@Override
	public void orgBRRemoved(String broleId, Types.SGPosition obj) {
		final String sgBRole = this.authorityService.getName(AuthorityType.GROUP, SGKind.SG_BR.getAlfrescoSuffix(broleId));
		final String sgObj = this.authorityService.getName(AuthorityType.GROUP, obj.getAlfrescoSuffix());
		if (SGKind.SG_ME.equals(obj.getSgKind())) {
			final String myBRole = SGKind.getSGBusinessRolePos(obj.getId(), broleId).getAlfrescoSuffix(); // личная группа для БР
			final String sgMyRole = this.authorityService.getName(AuthorityType.GROUP, myBRole);

			// (!) SG_Me <out of> SG_MyRole не выполнять, т.к. Бизнес роль может быть выдана сотруднику неявно через Подразделение или Должность ...
			// removeParent( sgObj, sgMyRole);

			// SG_MyRole <out of> SG_Role
			ensureParent(sgMyRole, sgBRole);
		} else {
			removeParent(sgObj, sgBRole);
		}
	}

}

