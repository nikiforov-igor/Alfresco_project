package ru.it.lecm.security.beans;

import org.alfresco.service.cmr.security.AuthorityService;
import org.alfresco.service.cmr.security.AuthorityType;
import org.alfresco.util.PropertyCheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import ru.it.lecm.security.Types;
import ru.it.lecm.security.Types.SGKind;
import ru.it.lecm.security.Types.SGPosition;
import ru.it.lecm.security.Types.SGPrivateBusinessRole;
import ru.it.lecm.security.Types.SGPrivateMeOfUser;
import ru.it.lecm.security.events.IOrgStructureNotifiers;

import java.util.Set;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.namespace.QName;
import ru.it.lecm.orgstructure.beans.OrgstructureAspectsModel;

public class LECMSecurityGroupsBean
		implements InitializingBean, IOrgStructureNotifiers
{

	final static protected Logger logger = LoggerFactory.getLogger (LECMSecurityGroupsBean.class);

	private boolean allowSVOnlyWithAspect = false;

	private AuthorityService authorityService;
	private NodeService nodeService;
	private String SVAspectString;
	private QName SVAspectQName = OrgstructureAspectsModel.ASPECT_HAS_LINKED_ORGANIZATION;
	private boolean safeMode = false;
	private final SgNameResolver sgnm = new SgNameResolver(logger);

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	public void setSVAspect(String SVAspect) {
		this.SVAspectString = SVAspect;
		logger.info("Creating OU_SV is allowed only for units with aspect: " + SVAspect);
		this.SVAspectQName = QName.createQName(SVAspect);
	}

	public void setAllowOUSV(boolean allowOUSV) {
		this.allowSVOnlyWithAspect = allowOUSV;
		if(allowOUSV) {
			logger.info("Creating OU_SV is allowed only for nodes with aspect");
		} else {
			logger.info("Creating OU_SV is allowed for all nodes");
		}
	}

	public AuthorityService getAuthorityService() {
		return authorityService;
	}

	public void setAuthorityService(AuthorityService authorityService) {
		this.authorityService = authorityService;
		this.sgnm.setAuthorityService(authorityService);
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
		logger.info("sageMode set to "+ this.safeMode);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		PropertyCheck.mandatory(this, "authorityService", authorityService);

		logger.info("initialized");
	}


	/**
	 * Проверить наличие и сохдать при отсутствии security-группу Альфреско
	 * с указанным коротким названием
	 * @param simpleName
	 * @return полное Альфресковское название для security-группы simpleName,
	 * например, "GROUP_LECM$BR%BR_INITIATOR", "GROUP_LECM$ME%14e3cde7-da7c-4824-8991-d6270224744f"
	 * или "GROUP_LECM$OUSV%6f448346-1f8e-4aa6-8fc3-b169f89c8088"
	 */
	String ensureAlfrescoGroupName(String simpleName, String details) {
		final String sgFullName;

		if (sgnm.hasAuth(simpleName)) {
			sgFullName = this.authorityService.getName(AuthorityType.GROUP, simpleName);
			// пропишем human-oriented данные, если они имеются
			/*if (details != null)
				this.authorityService.setAuthorityDisplayName( sgFullName, details);*/
			logger.debug(String.format("Alfresco security-group '%s' already exists for object '%s'", sgFullName, simpleName));
		} else {
			// sgFullName = this.authorityService.createAuthority(AuthorityType.GROUP, simpleName, details, null);
			sgFullName = this.authorityService.createAuthority(AuthorityType.GROUP, simpleName);
			this.authorityService.setAuthorityDisplayName( sgFullName, details);

			logger.info(String.format("Alfresco security-group '%s' created for object '%s'\n\t details: %s \n\t zones: %s"
					, sgFullName, simpleName
					, this.authorityService.getAuthorityDisplayName(sgFullName)
					, this.authorityService.getAuthorityZones(sgFullName)
			));
		}
		return sgFullName;
	}

	/**
	 * Удаление security-группы Альфреско
	 * @param fullName
	 */
	void removeAlfrescoAuthority(String fullName) {
		removeAlfrescoAuthority( fullName, false);
	}

	/**
	 * Удалить группу Альфреско
	 * @param fullName полное имя (в системе координат security Альфреско)
	 * @param cascade удалить каскадом или нет
	 */
	void removeAlfrescoAuthority(String fullName, boolean cascade) {
		try {
			if (!authorityService.authorityExists(fullName)) {
				logger.warn(String.format("Alfresco security item '%s' NOT exists or already removed", fullName));
				return;
			}
			// DONE данная строчка ВСЕГДА вызывала падение по NullPointerExpection!!!
			this.authorityService.deleteAuthority(fullName, cascade);
		} catch (Throwable t) {
			logger.error( String.format( "(!?) Ignoring exception at removeAuthority '%s':\n"+ t.getMessage(), fullName), t);
		}
		logger.info(String.format("Alfresco security item '%s' removed", fullName));
	}

	private void ensureParentEx(String sgItemFullName, String sgParentFullName, AuthorityType childType) {
		if (sgnm.hasFullAuthEx(sgItemFullName, sgParentFullName, childType)) {
			logger.debug(String.format("Security item '%s' is already inside security-group '%s'", sgItemFullName, sgParentFullName));
		} else {
			// добавление одного security-объекта в другой: sgItem -> sgParent
			this.authorityService.addAuthority(sgParentFullName, sgItemFullName);
			logger.debug(String.format("Security item '%s' put inside security-group '%s'", sgItemFullName, sgParentFullName));
		}
	}

	void ensureParent(String sgItemFullName, String sgParentFullName) {
		ensureParentEx(sgItemFullName, sgParentFullName, AuthorityType.GROUP);
	}

	void ensureUserParent(String sgItemFullName, String sgParentFullName) {
		ensureParentEx(sgItemFullName, sgParentFullName, AuthorityType.USER);
	}

	private void removeParentEx(String sgFullItemName, String sgParentFullName, AuthorityType childType) {
		if (!sgnm.hasFullAuthEx(sgFullItemName, sgParentFullName, childType)) {
			logger.debug(String.format("Security item '%s' is not inside security-group '%s'", sgFullItemName, sgParentFullName));
		} else {
			this.authorityService.removeAuthority(sgParentFullName, sgFullItemName);
			logger.debug(String.format("Security item '%s' put out of security-group '%s'", sgFullItemName, sgParentFullName));
		}
	}

	void removeParent(String sgFullItemName, String sgParentFullName) {
		removeParentEx(sgFullItemName, sgParentFullName, AuthorityType.GROUP);
	}

	void removeUserParent(String sgFullItemName, String sgParentFullName) {
		removeParentEx(sgFullItemName, sgParentFullName, AuthorityType.USER);
	}

	@Override
	public String orgNodeCreated(Types.SGPosition obj) {
		String sgName = null;
		// создание личной группы объекта
		//Если пришла группа SG_SV, то произвести проверку
		if(obj.getSgKind().equals(SGKind.SG_SV)){
			if(allowSVOnlyWithAspect) {
				if(nodeService.hasAspect(new NodeRef(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE, obj.getId()), SVAspectQName)) {
					return ensureAlfrescoGroupName( obj.getAlfrescoSuffix(), obj.getDisplayInfo());
				}
			} else {
				return ensureAlfrescoGroupName( obj.getAlfrescoSuffix(), obj.getDisplayInfo());
			}
		} else {
			sgName = ensureAlfrescoGroupName( obj.getAlfrescoSuffix(), obj.getDisplayInfo());
		}

		// дополнительные действия зависят от типа
		if (obj.getSgKind() ==  SGKind.SG_OU) {
			if(allowSVOnlyWithAspect) {
				if(nodeService.hasAspect(new NodeRef(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE, obj.getId()), SVAspectQName)) {
					ensureAlfrescoGroupName( SGKind.SG_SV.getAlfrescoSuffix(obj.getId()), obj.getDisplayInfo());
				}
			} else {
				ensureAlfrescoGroupName( SGKind.SG_SV.getAlfrescoSuffix(obj.getId()), obj.getDisplayInfo());
			}
		}

		return sgName;
	}

	@Override
	public void orgEmployeeTie(String employeeId, String alfrescoUserLogin, boolean tie) {
		// final String emplSuffix = SGKind.SG_ME.getAlfrescoSuffix(employeeId);
		final SGPrivateMeOfUser mepos = SGKind.getSGMeOfUser(employeeId, alfrescoUserLogin);
		final String emplSuffix = mepos.getAlfrescoSuffix();

		// safe-действия if (safeMode) ...
		{ // (!) гарантировать создание группы Сотрудника (SG_ME)
			ensureAlfrescoGroupName( emplSuffix, alfrescoUserLogin);
		}

		if (alfrescoUserLogin == null) {
			logger.warn( String.format( "employee {%s}: user login is null -> %s operation with {USER <-> SG_ME} skipped",
					employeeId, (tie ? "tie" : "untie")));
		} else {
			final String sg_user_name = this.authorityService.getName(AuthorityType.USER, alfrescoUserLogin);
			final String sg_me_group = this.sgnm.makeSGName(emplSuffix);
			if (tie) // привязать ...
				ensureUserParent( sg_user_name, sg_me_group);
			else // отвязать ...
				removeUserParent( sg_user_name, sg_me_group);
		}
	}

	@Override
	public void orgNodeDeactivated(Types.SGPosition obj) {
		if (obj == null) return;

		final String sgFullName = this.sgnm.makeSGName( obj);
		if (Types.SGKind.SG_ME == obj.getSgKind()) {
			// исключаем пользователя Альфреско из личной ME-группы ...
			final Types.SGPrivateMeOfUser me = (Types.SGPrivateMeOfUser) obj;
			if (me.getUserLogin() == null) {
				logger.warn( String.format( "No login. Cannot deactivate <%s>.", me));
				return;
			}
			final String sgUserName = this.authorityService.getName(AuthorityType.USER, me.getUserLogin());
			removeUserParent( sgUserName, sgFullName);
		} else {
			// DP, OU, SV, BR, BRME
			final boolean cascade = false;
			removeAlfrescoAuthority( sgFullName, cascade);

			// дополнительные действия для OU - убрать SV-группу
			if (obj.getSgKind() ==  SGKind.SG_OU) {
				final SGPosition sgPos = SGKind.SG_SV.getSGPos( obj.getId());
				removeAlfrescoAuthority( sgnm.makeSGName(sgPos.getAlfrescoSuffix() ));
			}
		}
	}

	private void sgSetParent( Types.SGPosition child, Types.SGPosition parent, boolean include) {
		/*
		 * include/exclude всегда safe-операции - в основном по причине упрощения
		 * работы с личными группами Бизнес-Ролей - чтобы не выносить отдельно
		 * методы создания личных групп БР (SG_MyRole)
		 */
		// if (safeMode)
		{
			orgNodeCreated( child);
			orgNodeCreated( parent);
		}

		// основные действия
		// final String sgItem =  this.authorityService.getName(AuthorityType.GROUP, child.getAlfrescoSuffix());
		final String sgItem =  this.sgnm.makeSGName(child);
		final String sgParent = this.sgnm.makeSGName(parent);

		if(!sgnm.hasFullAuth(sgItem) || !sgnm.hasFullAuth(sgParent)) {
			return;
		}

		if (isOperWithBossAndSV(child, parent)) { // SG_ME >>> SG_SV
			// (!) вхождение личной группы в SV выполняется на уровне user->group
			// , а не group->group как для всего остального.
			final Types.SGPrivateMeOfUser user = (Types.SGPrivateMeOfUser) child;

			if (user.getUserLogin() == null) {
				logger.warn( String.format( "Cannot add <%s> as USER into <%s>\n\t (!) Login for employee is NULL", child, parent));
				return;
			}

			if (include)
				ensureUserParent( user.getUserLogin(), sgParent);
			else
				removeUserParent( user.getUserLogin(), sgParent);
		} else {
			if (include)
				ensureParent(sgItem, sgParent);
			else
				removeParent( sgItem, sgParent);
		}
	}

	@Override
	public void sgInclude( Types.SGPosition child, Types.SGPosition parent) {
		sgSetParent(child, parent, true);
	}

	/**
	 * Проверка операции над SG_ME и SG_SV
	 * @param child
	 * @param parent
	 * @return true если childPos является SG_ME, а parentPos SG_SV, т.е.
	 * если операнды для операции вклчения/искл личной группы босса в/из
	 * SG_SV подразделения.
	 */
	private boolean isOperWithBossAndSV(SGPosition child, SGPosition parent) {
		return (	child != null && parent != null
				&& Types.SGKind.SG_ME.equals(child.getSgKind())
				&& Types.SGKind.SG_SV.equals(parent.getSgKind())) ;
	}

	@Override
	public boolean isSgInside(SGPosition child, SGPosition parent) {
		if (child == null || parent == null)
			return false;

		final String sgChildFullName =  this.sgnm.makeSGName(child);
		final String sgParentFullName = this.sgnm.makeSGName(parent);

		if (isOperWithBossAndSV(child, parent)) { // SG_ME >>> SG_SV
			// вхождение личной группы в SV выполняется на уровне user->group
			// (а не group->group как для всего остального)
			return sgnm.hasFullAuthEx(
					this.authorityService.getName(AuthorityType.USER, child.getAlfrescoSuffix())
					, sgParentFullName, AuthorityType.USER
					);
		}

		return sgnm.hasFullAuthEx(sgChildFullName, sgParentFullName, AuthorityType.GROUP);
	}


	@Override
	public void sgExclude( Types.SGPosition child, Types.SGPosition oldParent) {
		sgSetParent(child, oldParent, false);
	}


	@Override
	public void orgBRAssigned(String broleId, Types.SGPosition obj) {
		final String broleSuffix = SGKind.SG_BR.getAlfrescoSuffix(broleId);


		// safe-действия по созданию security-groups под БР и сам объект
		if (safeMode) {
			final String details = "BRole-"+ broleId;
			ensureAlfrescoGroupName( broleSuffix, details); // sg для БР
			orgNodeCreated( obj); // sg для объекта (SGME, SGSV, SGOU, SGDP ...)
		}

		final String sgBRole = this.sgnm.makeSGName(broleSuffix);
		final String sgObj = this.sgnm.makeSGName(obj);

		if (SGKind.SG_ME.equals(obj.getSgKind())) {
			/*
			 * Для сотрудника выполнятся «Активация относительно БР»:
			 *   - создается его личная SG-MyRole группа бизнес роли «A» (если ещё нет такой)
			 *   - в неё включается SG_Me
			 *   - и эта группа включается в SG_BR бизнес роли.
			 */
			final String myBRole = SGKind.getSGMyRolePos(obj.getId(), broleId).getAlfrescoSuffix(); // личная группа для БР
			final String sgMyRole = ensureAlfrescoGroupName(myBRole, broleId + " for user '"+ obj.getDisplayInfo()+ "' {"+ obj.getId()+ "}");


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

			if ( dp.getUserId() != null) {
				// SG_MyBRole -> SG_DP
				final SGPrivateBusinessRole sgMyRole = SGKind.getSGMyRolePos( dp.getUserId(), broleId);
				sgInclude( sgMyRole, dp);

				// SG_Me -> SG_MyRole
				final SGPrivateMeOfUser sgMe = SGKind.getSGMeOfUser( dp.getUserId(), dp.getUserLogin());
				sgInclude( sgMe, sgMyRole);
			} else {
				logger.warn( String.format("DP '%s'/{%s} is not linked with Employee -> skipping links (BRME->DP) and (ME->BRME)", dp.getDPName(), dp.getDPId()));
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
		final String sgBRole = this.sgnm.makeSGName(SGKind.SG_BR.getAlfrescoSuffix(broleId));
		final String sgObj = this.sgnm.makeSGName(obj.getAlfrescoSuffix());
		if (SGKind.SG_ME.equals(obj.getSgKind())) {
			final SGPrivateBusinessRole myBRolePos = SGKind.getSGMyRolePos(obj.getId(), broleId); // личная группа для БР
			final String sgMyRole = this.sgnm.makeSGName(myBRolePos);

			// (!) SG_Me <out of> SG_MyRole не выполнять, т.к. Бизнес роль может быть выдана сотруднику неявно через Подразделение или Должность ...
			// removeParent( sgObj, sgMyRole);

			// SG_MyRole <out of> SG_Role
			removeParent(sgMyRole, sgBRole);
		} else {
			removeParent(sgObj, sgBRole);
//			PermissionModel model;
//			model.addPermissionModel("");
//			PermissionModelBootstrap bt;
//			bt.setPermissionModel("");
		}
	}

	/**
	 * Получить имена sec-объектов Альфреско типа USER, которые непосредственно
	 * включены в указанную группу.
	 * Например, для личной ME-группы это позволит получить её владельца, т.к.
	 * только он входит в неё как USER, а все остальные (Делегаты, Руководство
	 * и пр) входят как ГРУППЫ.
	 * @param fullAlfrescoSecGroupName полное имя sec-объекта Альфреско, соот-щее
	 * проверяемой sec-группе.
	 * @return список входящих в группу пользователей или Null, если нет ни одного.
	 */
	Set<String> getUsersOfTheGroup(String fullAlfrescoSecGroupName) {
		return authorityService.getContainedAuthorities(AuthorityType.USER, fullAlfrescoSecGroupName, true);
	}

	/**
	 * Получить login-имя пользователя-владельца личной группы.
	 * @param sgMe личная группа
	 * @return логин владельца или Null, если группа не имеет вложенных пользователей
	 * Если в группе содержится более одного пользователя поднимается исключение.
	 */
	String getUserLoginOfMeGroup(Types.SGPrivateMeOfUser sgMe) {
		final Set<String> found = getUsersOfTheGroup( this.sgnm.makeSGName(sgMe) );
		if (found == null || found.isEmpty())
			return null;
		if (found.size() > 1)
			throw new RuntimeException( String.format("Private LECM group <%s> contains more than 1 users directly (%d): [%s]",
					sgMe, found.size(), found)); // или log.WARN
		return found.iterator().next(); // get[0]
	}
}
