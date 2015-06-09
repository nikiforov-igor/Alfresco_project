package ru.it.lecm.security.beans;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.security.AuthorityService;
import org.alfresco.service.cmr.security.AuthorityType;
import org.slf4j.Logger;
import ru.it.lecm.security.Types;
import ru.it.lecm.security.Types.SGPosition;

import java.util.Set;

public class SgNameResolver {

	private AuthorityService authorityService;
	private Logger logger;

	public SgNameResolver() {
	}

	public SgNameResolver(Logger logger) {
		this.logger = logger;
	}

	public SgNameResolver(AuthorityService authorityService, Logger logger) {
		super();
		this.authorityService = authorityService;
		this.logger = logger;
	}

	public Logger getLogger() {
		return logger;
	}

	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	public AuthorityService getAuthorityService() {
		return authorityService;
	}

	public void setAuthorityService(AuthorityService authorityService) {
		this.authorityService = authorityService;
	}

	/**
	 * Проверить существование группы авторизации с коротким названием shortName
	 * @param shortName
	 * @return
	 */
	public boolean hasAuth(String shortName) {
		String sgName = this.authorityService.getName(AuthorityType.GROUP, shortName);
		if (this.authorityService.authorityExists(sgName))
			return true; // has group
		// check as user name
		sgName = this.authorityService.getName(AuthorityType.USER, shortName);
		return this.authorityService.authorityExists(sgName);
	}

	/*
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
			this.authorityService.authorityExists(name);
			found = this.authorityService.findAuthorities(AuthorityType.GROUP, parentName, true, shortName, null);
			if (found != null && found.size() > 0)
				return true;
		} catch (Throwable t) {
			logger.error( t.getMessage());
		}

//		// есть не группа? ...
//		found = this.authorityService.findAuthorities(AuthorityType.ROLE, parentName, true, shortName, null);
//		if (found != null && found.size() > 0)
//			return true;

		return false; // NOT FOUND
	}
	 */

	/**
	 * Проверить наличие и сохдать при отсутствии security-группу Альфреско
	 * с указанным коротким названием
	 * @param simpleName
	 * @return полное Альфресковское название для security-группы simpleName
	 */
	public String ensureAlfrescoGroupName(String simpleName, String details) {
		final String sgFullName;

		if (hasAuth(simpleName)) {
			sgFullName = this.authorityService.getName(AuthorityType.GROUP, simpleName);
			// пропишем human-oriented данные, если они имеются
			/* if (details != null)
				this.authorityService.setAuthorityDisplayName( sgFullName, details); */
			logger.info(String.format("Alfresco security-group '%s' already exists for object '%s'", sgFullName, simpleName));
		} else {
			// sgFullName = this.authorityService.createAuthority(AuthorityType.GROUP, simpleName, details, null);
			sgFullName = this.authorityService.createAuthority(AuthorityType.GROUP, simpleName);
			this.authorityService.setAuthorityDisplayName( sgFullName, details);

			logger.warn(String.format("Alfresco security-group '%s' created for object '%s'\n\t details: %s \n\t zones: %s"
					, sgFullName, simpleName
					, this.authorityService.getAuthorityDisplayName(sgFullName)
					, this.authorityService.getAuthorityZones(sgFullName)
					));
		}
		return sgFullName;
	}

	//	private boolean hasFullAuth(String fullChildName, String fullParentName) {
	//	final Set<String> found = this.authorityService.findAuthorities(null, fullParentName, true, fullChildName, null);
	//	return (found != null && found.size() > 0);
	//}

	public boolean hasFullAuth(String fullName) {
		return (this.authorityService.authorityExists(fullName));
	}

	public boolean hasFullAuthGrp(String childFullName, String parentFullName) {
		return hasFullAuthEx( childFullName, parentFullName, AuthorityType.GROUP);
	}

	public boolean hasFullAuthEx(String childFullName, String parentFullName, AuthorityType childType) {
		// if (!hasFullAuth(childFullName)) return false;
		if (!this.authorityService.authorityExists(parentFullName)) {
			logger.warn( String.format( "Security group '%s' not exists", parentFullName));
			return false;
		}
		if (!this.authorityService.authorityExists(childFullName)) {
			logger.warn( String.format( "Security group/user '%s' not exists", childFullName));
			return false;
		}
		final Set<String> curChildren = this.authorityService.getContainedAuthorities(childType, parentFullName, true);
		return (curChildren != null) && curChildren.contains(childFullName);
	}

	public String makeSGName(SGPosition pos) {
		return this.authorityService.getName(AuthorityType.GROUP, pos.getAlfrescoSuffix());
	}

	public String makeSGName(String suffix) {
		return this.authorityService.getName(AuthorityType.GROUP, suffix);
	}


	public String makeFullBRMEAuthName(String userId, String roleCode) {
		// DONE: возможно стоит сделать обращение через authorityService.getName(xxx)
		// return "GROUP_" + Types.SGKind.getSGMyRolePos(userId, roleCode).getAlfrescoSuffix();
		// final String userLogin = getUserLogin( userId);
		return makeSGName( Types.SGKind.getSGMyRolePos(userId, roleCode));
	}

	/**
	 * Сформировать ПОЛНОЕ название security-группы Альфреско, которую надо
	 * сопоставлять указанному модельному объекту
	 * @param kind тип модельного объекта
	 * @param objId Id объекта
	 * @return
	 * @deprecated use makeSGName( SGPosition ... ); instead
	 */
	public String makeFullSGName(Types.SGKind kind, String objId) {
		// DONE: возможно стоит сделать обращение через authorityService.getName(xxx)
		// return "GROUP_" + kind.getSGPos(objId).getAlfrescoSuffix();
		return makeSGName( kind.getSGPos(objId));
	}

    public String parseDynamicRoleName(String authority, NodeRef employee) {
        String result = null;
        if (authority.contains(employee.getId()) && authority.contains(Types.SGKind.SG_BRME.getSuffix())) {
            result = authority.replace("GROUP_" + Types.PFX_LECM  + Types.SGKind.SG_BRME.getSuffix(), "");
            result = result.replace(Types.SFX_PRIV4USER + Types.SFX_DELIM + employee.getId(),"");
        }
        return result;
    }

}
